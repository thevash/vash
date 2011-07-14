package example;

import java.awt.image.BufferedImage;

import vash.Vash;


/**
 * This example shows off the super-high-level interface to Vash by creating
 * a BufferedImage from some data.
 */
public class Example1 {
	public static void main(String[] args) {
		// The algorithm String is an identifier that we use internally to 
		// select tree creation parameters.  The algorithm string allows us to 
		// guarantee that the same images will come out for a given seed, no 
		// matter what version of Vash is currently in use.  This allows 
		// users of our library to upgrade Vash (e.g. for performance and API 
		// improvements) without changing the images they produce, potentially
		// confusing their users or breaking security invariants.
		// Currently the only known algorithms are "1" and "1-fast".
		//
		// "1-fast" is modestly faster on large data sets, but is less secure:
		// it uses md5 instead of sha-512 internally.  In general you should use
		// "1", unless you have specific performance concerns with huge data sets.
		String algorithm = "1";
		
		// The seed string is the value that we are hashing.
		String data = "Foo";
		
		// The width and height are the dimensions of the output image
		int width = 128;
		int height = 128;
		
		// Use the high level interface
		BufferedImage img1 = Vash.createImage(algorithm, data, width, height);
		
		// The same interface is available as bytes.
		BufferedImage img2 = Vash.createImage(algorithm, data.getBytes(), width, height);

		// these two images will be identical
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				assert(img1.getRGB(x, y) == img2.getRGB(x, y));
			}
		}
	}
}
