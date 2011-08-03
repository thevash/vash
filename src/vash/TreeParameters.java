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

import vash.operation.OpParams;
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
	private final HashMap<Operation, OpParams> ops;
	
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
		this(opts.getAlgorithm(), opts.getSalt(), opts.getData());
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

		ops = new HashMap<Operation, OpParams>();
		if(algo.equals("1") || algo.equals("1-fast")) {
			minDepth = 2;
			maxDepth = 8;
			// color
			ops.put(Operation.RGB, 				new OpParams(1.0));
			// arith
			ops.put(Operation.ABSOLUTE, 		new OpParams(0.5));
			ops.put(Operation.ADD, 				new OpParams(0.5));
			ops.put(Operation.DIVIDE, 			new OpParams(0.5));
			ops.put(Operation.EXPONENTIATE,		new OpParams(0.5));
			ops.put(Operation.INVERT,			new OpParams(0.5));
			ops.put(Operation.MODULUS,			new OpParams(0.5));
			ops.put(Operation.MULTIPLY,			new OpParams(0.5));
			// trig
			ops.put(Operation.SINC,				new OpParams(0.0));
			ops.put(Operation.SINE,				new OpParams(0.0));
			ops.put(Operation.SPIRAL,			new OpParams(0.1));
			ops.put(Operation.SQUIRCLE,			new OpParams(2.0));
			// leaf
			ops.put(Operation.CONST,			new OpParams(0.0));
			ops.put(Operation.FLOWER,			new OpParams(3.5));
			ops.put(Operation.GRADIENT_RADIAL,	new OpParams(1.0));
			ops.put(Operation.ELLIPSE,			new OpParams(0.0));
			ops.put(Operation.GRADIENT_LINEAR,	new OpParams(1.0));
			ops.put(Operation.POLAR_THETA,		new OpParams(2.0));
		} else if(algo.equals("1.1")) {
			minDepth = 2;
			maxDepth = 8;
			// color
			ops.put(Operation.RGB, 				new OpParams(1.0, 3.0));
			// arith
			ops.put(Operation.ABSOLUTE, 		new OpParams(0.2, 0.9));
			ops.put(Operation.ADD, 				new OpParams(0.3, 3.0));
			ops.put(Operation.DIVIDE, 			new OpParams(0.3, 3.0));
			ops.put(Operation.EXPONENTIATE,		new OpParams(0.5, 3.0));
			ops.put(Operation.INVERT,			new OpParams(0.1, 3.0));
			ops.put(Operation.MODULUS,			new OpParams(0.5, 3.0));
			ops.put(Operation.MULTIPLY,			new OpParams(0.3, 3.0));
			// trig
			ops.put(Operation.SINC,				new OpParams(0.0, 0.0));
			ops.put(Operation.SINE,				new OpParams(0.0, 0.0));
			ops.put(Operation.SPIRAL,			new OpParams(0.2, 2.0));
			ops.put(Operation.SQUIRCLE,			new OpParams(2.0, 1.8));
			// leaf
			ops.put(Operation.CONST,			new OpParams(0.0, 0.0));
			ops.put(Operation.FLOWER,			new OpParams(3.5, 3.0));
			ops.put(Operation.GRADIENT_RADIAL,	new OpParams(1.0, 3.0));
			ops.put(Operation.ELLIPSE,			new OpParams(2.0, 3.0));
			ops.put(Operation.GRADIENT_LINEAR,	new OpParams(1.0, 3.0));
			ops.put(Operation.POLAR_THETA,		new OpParams(2.0, 3.0));
		} else {
			throw new InvalidAlgorithmException("Unrecognized algorithm string: \"" + algo + "\"");
		}

		this.animationMode = null;
		this.duration = 0;
		this.minPeriod = 0;
		this.fps = 30.0;
	}
	
	/**
	 * Get the seed.
	 * @return
	 */
	public Seed getSeed() {
		return seed;
	}

	/**
	 * Get the minimum tree depth.
	 * @return
	 */
	public short getMinDepth() {
		return minDepth;
	}

	/**
	 * Get the maximum tree depth.
	 * @return
	 */
	public short getMaxDepth() {
		return maxDepth;
	}

	/**
	 * Get the relative frequency of appearance of the given node type.
	 * @param op
	 * @return
	 */
	public double getOperationRatio(Operation op) {
		OpParams p = this.ops.get(op);
		return p.ratio;
	}

	/**
	 * Get the maximum channel count the given operation can appear in. 
	 * @param op
	 * @return
	 */
	public double getOperationChannels(Operation op) {
		OpParams p = this.ops.get(op);
		return p.channels;
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
