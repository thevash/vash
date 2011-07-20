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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import vash.operation.Operation;

/**
 * Encapsulate all inputs to tree construction, parameterized by the seed and algorithm.
 */
public class TreeParameters {
	// tree layout seed
	private final Seed seed;

	// tree layout algorithm parameters
	private final short minDepth;
	private final short maxDepth;
	private final HashMap<Operation, Double> opRatios;
	
	// tree value animation parameters
	private final AnimationMode animationMode;
	private final double duration;
	private final double minPeriod;
	private final double fps;

	
	/**
	 * This method instantiates a new TreeParameters for data Bytes.  The advantage of this method
	 * over one of the constructors is that it ignores the needlessly general IOException, as 
	 * there is no File IO in this version.  This version of the function takes the default salt.
	 * @param algo
	 * @param dataBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static TreeParameters createInstance(String algo, byte[] dataBytes) 
			throws NoSuchAlgorithmException
	{
		try {
			return new TreeParameters(algo, null, new ByteArrayInputStream(dataBytes));
		} catch(IOException e) { // not going to happen on a ByteArrayInputStream :-/
			return null;
		}
	}

	
	/**
	 * This method instantiates a new TreeParameters for data Bytes.  The advantage of this method
	 * over one of the constructors is that it ignores the needlessly general IOException, as 
	 * there is no File IO in this version.  This version of the function takes a salt.
	 * @param algo
	 * @param salt
	 * @param dataBytes
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static TreeParameters createInstance(String algo, byte[] salt, byte[] dataBytes) 
			throws NoSuchAlgorithmException
	{
		try {
			return new TreeParameters(algo, salt, new ByteArrayInputStream(dataBytes));
		} catch(IOException e) { // not going to happen on a ByteArrayInputStream :-/
			return null;
		}
	}

	
	/**
	 * A helper method for applications that wraps potential errors during TreeParameter creation
	 * and simply makes them fatal.
	 * @param algo
	 * @param salt
	 * @param data
	 * @return
	 */
	public static TreeParameters createInstanceOrDie(String algo, byte[] salt, InputStream data)
	{
		try {
			return new TreeParameters(algo, salt, data);
		} catch(IOException e) {
			System.err.format("Failed to create TreeParameters: %s%n", e.toString());
			e.printStackTrace(System.err);
			System.exit(1);
		} catch(NoSuchAlgorithmException e) {
			System.err.format("Failed to create TreeParameters: %s%n", e.toString());
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return null;
	}

	
	/**
	 * A helper method for applications that wraps potential errors during TreeParameter creation
	 * and simply makes them fatal.
	 * @param opt
	 * @return
	 */
	public static TreeParameters createInstanceOrDie(Options opt)
	{
		try {
			return new TreeParameters(opt);
		} catch(IOException e) {
			System.err.format("Failed to create TreeParameters: %s%n", e.toString());
			e.printStackTrace(System.err);
			System.exit(1);
		} catch(NoSuchAlgorithmException e) {
			System.err.format("Failed to create TreeParameters: %s%n", e.toString());
			e.printStackTrace(System.err);
			System.exit(1);
		}
		return null;
	}

	
	/**
	 * Initialize new tree generation parameters from command line options.
	 * @param opts
	 */
	public TreeParameters(Options opts) 
			throws IOException, NoSuchAlgorithmException
	{
		this(opts.getAlgorithm(), null, opts.getData());
	}

	
	/**
	 * Initialize new tree generation parameters from input data and algorithm.
	 * @param dataStr the input data as a string
	 * @param algo the algorithm identification string
	 */
	public TreeParameters(String dataStr, String algo) 
			throws IOException, NoSuchAlgorithmException
	{
		this(algo, null, new ByteArrayInputStream(dataStr.getBytes()));
	}


	/**
	 * Initialize new tree generation parameters from a salt, input data, and algorithm.
	 * @param saltBytes
	 * @param seedStream
	 * @param algo
	 */
	public TreeParameters(String algo, byte[] saltBytes, InputStream dataStream) 
			throws IOException, NoSuchAlgorithmException
	{
		seed = new Seed(algo, saltBytes, dataStream);

		opRatios = new HashMap<Operation, Double>();
		if(algo.equals("1") || algo.equals("1-fast") || algo.equals("1.1")) {
			minDepth = 2;
			maxDepth = 8;
			// color
			opRatios.put(Operation.RGB, 			1.0);
			// arith
			opRatios.put(Operation.ABSOLUTE, 		0.5);
			opRatios.put(Operation.ADD, 			0.5);
			opRatios.put(Operation.DIVIDE, 			0.5);
			opRatios.put(Operation.EXPONENTIATE,	0.5);
			opRatios.put(Operation.INVERT,			0.5);
			opRatios.put(Operation.MODULUS,			0.5);
			opRatios.put(Operation.MULTIPLY,		0.5);
			// trig
			opRatios.put(Operation.SINC,			0.0);
			opRatios.put(Operation.SINE,			0.0);
			opRatios.put(Operation.SPIRAL,			0.1);
			opRatios.put(Operation.SQUIRCLE,		2.0);
			// leaf
			opRatios.put(Operation.CONST,			0.0);
			opRatios.put(Operation.FLOWER,			3.5);
			opRatios.put(Operation.GRADIENT_RADIAL,	1.0);
			opRatios.put(Operation.GRADIENT_LINEAR,	1.0);
			opRatios.put(Operation.POLAR_THETA,		2.0);
		} else {
			throw new InvalidAlgorithmException("Unrecognized algorithm string: \"" + algo + "\"");
		}

		this.animationMode = null;
		this.duration = 0;
		this.minPeriod = 0;
		this.fps = 30.0;
	}
	
	public Seed getSeed() {
		return seed;
	}

	public short getMinDepth() {
		return minDepth;
	}

	public short getMaxDepth() {
		return maxDepth;
	}

	public double getOperationRatio(Operation op) {
		return this.opRatios.get(op);
	}
	
	AnimationMode getAnimationMode() {
		return animationMode;
	}

	double getDuration() {
		return duration;
	}

	double getMinPeriod() {
		return minPeriod;
	}

	double getFps() {
		return fps;
	}
}
