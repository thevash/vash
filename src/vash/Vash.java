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
		// parse arguments
		Options opts = null;
		try {
			opts = new Options(args);
		} catch(IllegalArgumentException e) {
			System.out.println("Error: " + e.getMessage());
			System.exit(1);
		}
		
		// load a tree
		TreeParameters tp = new TreeParameters(opts);
		Tree tree = new Tree(tp);

		// generate and write out the tree
		OutputParameters op = new OutputParameters(opts);
		Output out = new Output(op, tree);
		out.generate();
	}

	/**
	 * The super-high-level interface to Vash.  This takes an algorithm 
	 * specifier, data, and the requested width and height and will return
	 * the image for the given algorithm and data.
	 * @param algorithm The algorithm selector (see documentation)
	 * @param data the data to hash
	 * @param width the output image width
	 * @param height the output image height
	 */
	public static BufferedImage createImage(String algorithm, byte[] data, int width, int height) {
		TreeParameters tp = new TreeParameters(data, algorithm);
		Tree tree = new Tree(tp);

		ImageParameters ip = new ImageParameters(width, height);
		tree.setGenerationParameters(ip);
		
		byte[] pix = tree.generateCurrentFrame();
		return Output.dataToImage(pix, width, height);
	}
	
	/**
	 * Like the other createImage, but takes a String as data.  This is only
	 * a convenience method and simply calls getBytes on the data.
	 */
	public static BufferedImage createImage(String algorithm, String data, int width, int height) {
		return createImage(algorithm, data.getBytes(), width, height);
	}
}

