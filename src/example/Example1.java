package example;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

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
		String algorithm = "1.1";
		
		// The seed string is the value that we are hashing.
		String data = "Foo";
		
		// The width and height are the dimensions of the output image
		int width = 128;
		int height = 128;
		
		// Use the high level interface
		BufferedImage img1 = null, img2 = null, img3 = null;
		try {
			img1 = Vash.createImage(algorithm, data, width, height);

			// The same interface is available for bytes data.
			img2 = Vash.createImage(algorithm, data.getBytes(), width, height);

			// And and for InputStreams.
			try {
				InputStream dataStream = new ByteArrayInputStream(data.getBytes());
				img3 = Vash.createImage(algorithm, dataStream, width, height);
			} catch(IOException e) {
				// An IOException will not be raised for a ByteArrayInputStream so we
				//	simply ignore it.  In the general case though, the InputStream method 
				//	may fail for disk/network errors and should be handled accordingly.
			}
		} catch(NoSuchAlgorithmException e) {
			// Vash makes heavy use of the system's crytographic primitives, including
			//	SHA-512 and SHA-256, which were under export regulations some years ago.
			//	Everything we need should be present on any modern machine, so this should
			//	never get thrown in practice.
			System.err.println("Missing cryptographic primitives: " + e.toString());
			System.exit(1);
		}
		
		// these images will all be identical
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				assert(img1.getRGB(x, y) == img2.getRGB(x, y));
				assert(img1.getRGB(x, y) == img3.getRGB(x, y));
			}
		}
	}
}
