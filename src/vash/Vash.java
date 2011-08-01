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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;


/**
 * Vash stand-alone program entry point and driver.
 */
public class Vash {
	public static final int MAJOR_VERSION = 1;
	public static final int MINOR_VERSION = 1;
	public static final int REVISION = 0;
	public static final String VERSION = String.format("%d.%d.%d", MAJOR_VERSION, MINOR_VERSION, REVISION);
	
	/**
	 * Entry point of command line program.
	 * @param args
	 */
	public static void main(String[] args) {
		// parse arguments and load parameters
		Options opts = null;
		TreeParameters tp = null;
		OutputParameters op = null;
		try {
			opts = new Options(args);
			tp = TreeParameters.createInstanceOrDie(opts);
			op = new OutputParameters(opts);
		} catch(IllegalArgumentException e) {
			System.err.println("Error: " + e.getLocalizedMessage());
			System.exit(1);
		}
		
		// load a tree
		Tree tree = new Tree(tp);
		
		// do debug tasks, if specified
		if(opts.hasADebugFlagSet("TREE", "ENTROPY")) {
			if(opts.hasDebugFlag("TREE")) {
				try {
					tree.show("-");
				} catch(IOException e) {
					System.err.println("IO Error: " + e.getLocalizedMessage());
				}
			}
			if(opts.hasDebugFlag("ENTROPY")) {
				System.out.format("Entropy Used: %d bits%n", tp.getSeed().getBitsOfEntropyUsed());
			}
			return;
		}

		// generate and write out the tree
		Output out = new Output(op, tree);
		try {
			out.generate();
		} catch(IOException e) {
			System.err.println("IO Error: " + e.getLocalizedMessage());
			System.exit(1);
		}
	}

	
	/**
	 * The super-high-level interface to Vash.  This takes an algorithm  specifier, 
	 * data as a String, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, String data, int width, int height)
			throws NoSuchAlgorithmException
	{
		return createImage(algorithm, data.getBytes(), width, height);
	}

	
	/**
	 * The super-high-level interface to Vash.  This takes an algorithm  specifier, 
	 * data as a byte array, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, byte[] data, int width, int height)
			throws NoSuchAlgorithmException
	{
		try {
			InputStream dataStream = new ByteArrayInputStream(data);
			return createImage(algorithm, dataStream, width, height);
		} catch(IOException e) { // not going to happen on a ByteArrayInputStream
			return null;
		}
	}

	
	/**
	 * The super-high-level interface to Vash.  This takes an algorithm  specifier, 
	 * data as an input stream, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, InputStream dataStream, int width, int height)
			throws IOException, NoSuchAlgorithmException
	{
		TreeParameters tp = new TreeParameters(algorithm, null, dataStream);
		Tree tree = new Tree(tp);

		ImageParameters ip = new ImageParameters(width, height);
		tree.setGenerationParameters(ip);
		
		byte[] pix = tree.generateCurrentFrame();
		return Output.dataToImage(pix, width, height);
	}

	

	/**
	 * The super-high-level interface to Vash.  This takes an algorithm  specifier, 
	 * a salt, data as a String, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param salt the salt value, appropriately sized for algorithm
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, byte[] salt, String data, int width, int height)
			throws NoSuchAlgorithmException
	{
		return createImage(algorithm, salt, data.getBytes(), width, height);
	}

	
	/**
	 * The super-high-level interface to Vash.  This takes an algorithm  specifier, 
	 * a salt, data as a byte array, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param salt the salt value, appropriately sized for algorithm
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, byte[] salt, byte[] data, int width, int height)
			throws NoSuchAlgorithmException
	{
		try {
			return createImage(algorithm, salt, new ByteArrayInputStream(data), width, height);
		} catch(IOException e) { // not going to happen on a ByteArrayInputStream
			return null;
		}
	}


	/**
	 * The super-high-level interface to Vash.  This takes an algorithm  specifier, 
	 * a salt, data as a Stream, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param salt the salt value, appropriately sized for algorithm
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, byte[] salt, InputStream data, int width, int height)
			throws IOException, NoSuchAlgorithmException
	{
		TreeParameters tp = new TreeParameters(algorithm, salt, data);
		Tree tree = new Tree(tp);

		ImageParameters ip = new ImageParameters(width, height);
		tree.setGenerationParameters(ip);
		
		byte[] pix = tree.generateCurrentFrame();
		return Output.dataToImage(pix, width, height);
	}

}

