package example;

import java.awt.image.BufferedImage;
import java.util.Vector;

import vash.ImageParameters;
import vash.Output;
import vash.Tree;
import vash.TreeParameters;


/**
 * This example shows off the low-level interface to Vash by creating a 
 * series of BufferedImage's with the same content at different resolutions.
 * 
 * Using the low-level interface, we are able to generate several images 
 * from a single tree, saving the time it would take to re-create it on
 * each invocation of the high-level interface.
 */
public class Example2 {
	public static void main(String[] args) {
		Vector<BufferedImage> mipmaps = new Vector<BufferedImage>();
		
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

		// The tree parameters encapsulate everything the Tree class needs to know
		//	to build a unique tree, based on the passed string of data.
		TreeParameters tp = new TreeParameters(data, algorithm);
		Tree tree = new Tree(tp);

		for(int size = 16; size <= 2048; size *= 2) {
			// The image parameters encapsulate the data we need map the tree
			//	into a physical image and provides several size-dependent 
			//	services the tree uses when generating images.
			ImageParameters ip = new ImageParameters(size, size);
			tree.setGenerationParameters(ip);

			// The tree provides images as a packed, unstrided array of 24-bit
			//	BGR pixels.
			byte[] pix = tree.generateCurrentFrame();
			
			// The static dataToImage method of Output builds a BufferedImage 
			//	stack to interpret our raw pixels.  The BufferedImage does not
			//	rewrite the pixels, it just gives us an easy-to-use API on
			//	top of our existing data.
			BufferedImage img = Output.dataToImage(pix, size, size);
			
			// We can now do whatever we want with our image.
			mipmaps.add(img);
		}
	}
}
