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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import ec.util.MersenneTwisterFast;



/**
 * Encapsulates the generation of an unbounded sequence of random values, parameterized by the given
 * seed key material and a given algorithm.
 */
public class Seed {
	private final byte[] seed_base; 
	private final Random linear_congruent;
	private final MersenneTwisterFast twister;
	private final String algorithm;
	

	/**
	 * Convenience method to create a seed.   Creating a seed normally can 
	 * throw two exceptions.  Generally if we don't have key material, we
	 * don't have much point in running, so we provide a convenient method
	 * to simply die if we don't have a workable seed/environment.
	 */
	public static Seed fromBytesOrDie(byte[] seedBytes, String algorithm) {
		Seed seed = null;
		try {
			seed = new Seed(seedBytes, algorithm);
		} catch(NoSuchAlgorithmException e) {
			System.out.println(e.toString());
			System.exit(1);
		} catch(IllegalArgumentException e) {
			System.out.println(e.toString());
			System.exit(2);
		}
		assert(seed != null);
		return seed;
	}

	
	/**
	 * Create a new Seed from some seed material as a string and given algorithm.
	 * The seed string is converted to bytes using getBytes().
	 * @param str_seed
	 * @param algorithm
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalArgumentException
	 */
	public Seed(String str_seed, String algorithm) throws NoSuchAlgorithmException, IllegalArgumentException {
		this(str_seed.getBytes(), algorithm);
	}

	
	/**
	 * Create a new Seed from some seed material as a byte array and given algorithm.
	 * @param byte_seed
	 * @param algorithm
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalArgumentException
	 */
	public Seed(byte[] byte_seed, String algorithm) throws NoSuchAlgorithmException, IllegalArgumentException {
		if(byte_seed == null)
			throw new IllegalArgumentException("A seed value is required.");
		
		if(algorithm.equals("1-fast")) {
			MessageDigest md = MessageDigest.getInstance("MD5");
			seed_base = md.digest(byte_seed);
	
			long seed = 
				(seed_base[0] & 0xFFL) << 40 | 
				(seed_base[1] & 0xFFL) << 32 |
				(seed_base[2] & 0xFFL) << 24 |
				(seed_base[3] & 0xFFL) << 16 |
				(seed_base[4] & 0xFFL) << 8 |
				seed_base[5] & 0xFFL;
			
			this.linear_congruent = new Random(seed);
			this.twister = null;

			this.algorithm = algorithm;
		} else {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			seed_base = md.digest(byte_seed);
			
			// transform seed_base(64 bytes) into an int[16]
			int[] seed = new int[16];
			for(int i = 0; i < 16; i++) {
				seed[i] = (seed_base[i * 4 + 0] & 0xFF) << 24 |
						  (seed_base[i * 4 + 1] & 0xFF) << 16 |
						  (seed_base[i * 4 + 2] & 0xFF) << 8 |
						   seed_base[i * 4 + 3] & 0xFF;
			}
			
			this.twister = new MersenneTwisterFast(seed);
			this.linear_congruent = null;
			
			this.algorithm = algorithm;
		}
	}

	public byte[] getSeedBase() {
		return this.seed_base;
	}
	
	public double nextDouble() {
		if(this.twister != null)
			return this.twister.nextDouble();
		return this.linear_congruent.nextDouble();
	}
	
	public String getAlgorithm() {
		return this.algorithm;
	}
}
