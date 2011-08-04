/*
 * Copyright 2011, Zettabyte Storage LLC
 * 
 * This file is part of Vash.
 * 
 * Vash is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Vash is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Vash.  If not, see <http://www.gnu.org/licenses/>.
 */
package vash;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import ec.util.MersenneTwisterFast;


/*
 * We have extracted algorithm 2 into its own class, since it needs to do some buffering
 * of values externally and has several extra constants over the other algorithms.
 */
class SeedProviderHVAC {
	private static final String XTR_ALGORITHM = "HmacSHA512";
	@SuppressWarnings("unused") private static final int XTR_SIZE = 512 / 8;

	private static final String PRF_ALGORITHM = "HmacSHA256";
	private static final int PRF_SIZE = 256 / 8;

	// our salt is the first 512 bits of PI, ignoring the decimal place
	public static final int SALT_SIZE = 512 / 8;
	private static final byte[] DEFAULT_SALT = {
		(byte)201, (byte) 15, (byte)218, (byte)162, (byte) 33, (byte)104, (byte)194, (byte) 52,
		(byte)196, (byte)198, (byte) 98, (byte)139, (byte)128, (byte)220, (byte) 28, (byte)209,
		(byte) 41, (byte)  2, (byte) 78, (byte)  8, (byte)138, (byte)103, (byte)204, (byte)116,
		(byte)  2, (byte) 11, (byte)190, (byte)166, (byte) 59, (byte) 19, (byte)155, (byte) 34,
		(byte) 81, (byte) 74, (byte)  8, (byte)121, (byte)142, (byte) 52, (byte)  4, (byte)221,
		(byte)239, (byte)149, (byte) 25, (byte)179, (byte)205, (byte) 58, (byte) 67, (byte) 27,
		(byte) 48, (byte) 43, (byte) 10, (byte)109, (byte)242, (byte) 95, (byte) 20, (byte) 55,
		(byte) 79, (byte)225, (byte) 53, (byte)109, (byte)109, (byte) 81, (byte)194, (byte) 69
	};
	private static final byte[] INFO = "20110719 terrence@thevash.com VASH/hmacExpandInfoBytes".getBytes();
	
	// the expansion function, buffer, and counter
	private final Mac hmacExpand;
	private byte[] Tcurrent;
	private int Toffset;
	// represents the buffer as bits, and holds a position within it
	private final BitSet Tbits; 
	private int Tbitpos;
	

	/*
	 * This algorithm is taken from RFC5869[1] and "Cryptographic Extraction and 
	 * Key Derivation: The HKDF Scheme", Hugo Krawczyk, Crypto/2010[2].  As 
	 * suggested in [2], we use HMAC-SHA512 extract with HMAC-SHA256 expand.
	 * We also make the additional suggested change of using a 0 buffer as
	 * input to the first cycle T(1).  In addition, we have expanded L to be
	 * a 4 byte number, in order to provide our algorithm with enough bits
	 * to work, even in rare cases.
	 */
	SeedProviderHVAC(byte[] saltBytes, InputStream seedStream) 
			throws NoSuchAlgorithmException, IOException 
	{
		// check that the salt is either null, or the correct length
		if(saltBytes != null && saltBytes.length != SALT_SIZE) {
			throw new InvalidSaltException("The salt for algorithm 2 must be 64 bytes long.");
		}

		// get a mac instance for extract
		Mac macXTR = Mac.getInstance(XTR_ALGORITHM);

		// init with our salt
		if(saltBytes == null) { saltBytes = DEFAULT_SALT; }
		SecretKeySpec salt = new SecretKeySpec(saltBytes, XTR_ALGORITHM);
		try {
			macXTR.init(salt);
		} catch(InvalidKeyException e) {
			throw new InvalidSaltException(e.toString());
		}

		// hash the input key material to get PRK
		int cnt = 0;
		byte[] buffer = new byte[4096];
		while(cnt != -1) {
			cnt = seedStream.read(buffer);
			if(cnt == -1) { break; }
			macXTR.update(buffer, 0, cnt);
		}
		byte[] prkBase = macXTR.doFinal();
		
		// truncate prk into key string for our expand phase
		SecretKeySpec prk = new SecretKeySpec(prkBase, 0, PRF_SIZE, PRF_ALGORITHM);

		// get a mac instance for expand
		hmacExpand = Mac.getInstance(PRF_ALGORITHM);
		try {
			hmacExpand.init(prk);
		} catch(InvalidKeyException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
		
		// prepare our bit set
		Tbits = new BitSet(PRF_SIZE * 8);
		Tbitpos = 0;
		
		// initialize T(0) with zeros
		Tcurrent = new byte[PRF_SIZE];
		Toffset = 0;
		
		// initialize T(1) from T(0) and fill the bitset
		nextT();
	}
	
	private void nextT() {
		int i, j;
		byte[] inp = new byte[PRF_SIZE + INFO.length + 4];
		// copy in current buffer
		System.arraycopy(Tcurrent, 0, inp, 0, PRF_SIZE);
		// copy over info string
		System.arraycopy(INFO, 0, inp, PRF_SIZE, INFO.length);
		// copy in current offset (little endian)
		i = PRF_SIZE + INFO.length;
		for(j = 3; j >= 0; j--) {
			inp[i + (3 - j)] = (byte)((Toffset >> (j * 8)) & 0xFF);
		}

		// get next T
		Tcurrent = hmacExpand.doFinal(inp);
		Toffset += 1;
		
		// fill our bitset with the bits in current
		Tbitpos = 0;
		for(i = 0; i < PRF_SIZE; i++) {
			byte b = Tcurrent[i];
			for(j = 7; j >= 0; j--) {
				boolean b1 = ((b >> j) & 1) == 1;
				int off = i * 8 + (7 - j);
				Tbits.set(off, b1);
			}
		}
	}
	
	double nextDouble() {
		long l0 = nextBits(26);
		long l1 = nextBits(27);
        return ((l0 << 27) + l1) / (double)(1L << 53);
	}
	
	int nextInt(int n) {
		if (n <= 0)
			throw new IllegalArgumentException("n must be positive");

		if ((n & -n) == n)  // i.e., n is a power of 2
			return (int)((n * nextBits(31)) >> 31);
		
		int bits, val;
		do {
			bits = (int)nextBits(31);
			val = bits % n;
		} while (bits - val + (n-1) < 0);
		return val;
    }
	
	private long nextBits(int n) {
		assert(n <= 64);
		long out = 0;
		for(int i = n - 1; i >= 0; i--) {
			long b1 = Tbits.get(Tbitpos) ? 1L : 0L;
			Tbitpos++;
			out |= (b1 << i);
			if(Tbitpos == Tbits.size()) {
				nextT();
			}
		}
		return out;
	}
}

/**
 * Encapsulates the generation of an unbounded sequence of random values, parameterized by the given
 * seed key material and a given algorithm.
 */
public class Seed {
	private final String algorithm;
	// algorithm 1-fast
	private Random linear_congruent;
	// algorithm 1
	private MersenneTwisterFast twister;
	// algorithm 2
	private SeedProviderHVAC hvac;
	// for 1 and 1-fast
	public static final int SALT_SIZE = 512 / 8;

	// count out how many bits we used
	private int usedEntropy = 0;

	/**
	 * Return the number of bytes of data that should be passed for salt, if a salt is used.
	 * @param algo
	 * @return
	 */
	public static int getSaltSizeForAlgorithm(String algo) {
		if(algo.equals("1-fast") || algo.equals("1")) {
			return SALT_SIZE;
		} else if(algo.equals("1.1")) {
			return SeedProviderHVAC.SALT_SIZE;
		}
		throw new InvalidAlgorithmException("Unrecognized seed algorithm: " + algo);
	}

	/**
	 * Create a new Seed from an algorithm, salt, and data.
	 * @param algorithm one of our known algorithms
	 * @param saltBytes 64 bytes (512 bits) of salt data or null to use the default salt
	 * @param dataStream input to the vash
	 * @throws IllegalArgumentException
	 * @throws InvalidSaltException
	 * @throws NoSuchAlgorithmException
	 */
	public Seed(String algorithm, byte[] saltBytes, InputStream dataStream) 
			throws NoSuchAlgorithmException, IOException 
	{
		this.algorithm = algorithm;
		if(algorithm.equals("1-fast")) {
			init1Fast(saltBytes, dataStream);
		} else if(algorithm.equals("1")) {
			init1(saltBytes, dataStream);
		} else if(algorithm.equals("1.1")) {
			initHVAC(saltBytes, dataStream);
		} else {
			throw new InvalidAlgorithmException("Unknown seed algorithm: " + algorithm);
		}
	}
	

	private void init1Fast(byte[] saltBytes, InputStream seedStream) 
			throws	NoSuchAlgorithmException, IOException 
	{
		MessageDigest md = MessageDigest.getInstance("MD5");
		if(saltBytes != null) {
			md.update(saltBytes);
		}

		int cnt = 0;
		byte[] buffer = new byte[4096];
		while(cnt != -1) {
			cnt = seedStream.read(buffer);
			if(cnt == -1) {
				break;
			}
			md.update(buffer, 0, cnt);
		}
		byte[] base = md.digest();
		long seed = 
			(base[0] & 0xFFL) << 40 | 
			(base[1] & 0xFFL) << 32 |
			(base[2] & 0xFFL) << 24 |
			(base[3] & 0xFFL) << 16 |
			(base[4] & 0xFFL) << 8 |
			base[5] & 0xFFL;
		this.linear_congruent = new Random(seed);
	}
	
	
	private void init1(byte[] saltBytes, InputStream seedStream) throws 
				NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA-512");
		if(saltBytes != null) {
			md.update(saltBytes, 0, SALT_SIZE);
		}
		
		int cnt = 0;
		byte[] buffer = new byte[4096];
		while(cnt != -1) {
			cnt = seedStream.read(buffer);
			if(cnt == -1) {
				break;
			}
			md.update(buffer, 0, cnt);
		}
		byte[] base = md.digest();

		// transform seed_base(64 bytes) into an int[16]
		int[] seed = new int[16];
		for(int i = 0; i < 16; i++) {
			seed[i] = (base[i * 4 + 0] & 0xFF) << 24 |
					  (base[i * 4 + 1] & 0xFF) << 16 |
					  (base[i * 4 + 2] & 0xFF) << 8 |
					   base[i * 4 + 3] & 0xFF;
		}
		this.twister = new MersenneTwisterFast(seed);
	}

	
	private void initHVAC(byte[] saltBytes, InputStream seedStream) 
			throws IOException, NoSuchAlgorithmException   
	{
		hvac = new SeedProviderHVAC(saltBytes, seedStream);
	}


	/**
	 * Get and return the next double from the seed.
	 * @return
	 */
	public double nextDouble() {
		double d = 0.0;
		if(this.hvac != null)
			d = this.hvac.nextDouble();
		else if(this.twister != null)
			d = this.twister.nextDouble();
		else
			d = this.linear_congruent.nextDouble();
		usedEntropy += 53;
		return d;
	}
	
	
	/**
	 * Get and return the next int value in range [0,n) from the seed.
	 * @param n
	 * @return
	 */
	public int nextInt(int n) {
		int i;
		if(this.hvac != null)
			i = this.hvac.nextInt(n);
		else if(this.twister != null)
			i = this.twister.nextInt(n);
		else
			i = this.linear_congruent.nextInt(n);
		usedEntropy += 31;
		return i;
	}
	
	/**
	 * Return the number of bits our generator has returned.
	 * @return
	 */
	public int getBitsOfEntropyUsed() {
		return usedEntropy;
	}
	
	/**
	 * Return the algorithm used to initialize this seed.
	 * @return
	 */
	public String getAlgorithm() {
		return this.algorithm;
	}
}
