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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.IRational;
*/

/**
 * Generates image(s) using a Tree and OutputParameters.
 */
public class Output {
	private final OutputParameters params;
	private final Tree tree;
	
	/**
	 * Construct a new Output driver from parameters and tree. 
	 * @param params
	 * @param tree
	 */
	public Output(OutputParameters params, Tree tree) {
		this.params = params;
		this.tree = tree;

		ImageParameters ip = new ImageParameters(params.getWidth(), params.getHeight());
		tree.setGenerationParameters(ip);
	}
	
	
	/**
	 * Generate image(s) from the Tree and write them with the instance's OutputParameters.
	 */
	public void generate() throws IOException {
		// do computation of a frame
		//if(!this.params.isVideo()) {
		BufferedImage bimg = this.generateImage();
		Output.writeImageFile(this.params.getFilename(), this.params.getImageType(), bimg);
		//} else {
		//	this.writeVideo();
		//}
	}
	
	/*
	 * TODO: video output isn't going to make the cut for version 1.  We will revisit
	 * 	this for version 2, when we have more time to concentrate on distributing
	 *  binaries, dependent jars, plugins, etc.
	private void writeVideo() {
		IMediaWriter writer = this.prepareVideo();
		double t = 0;
		double dt = 1.0 / this.params.getFrameRate();
		for(int i = 0; i < 30 * 10; i++) {
			t += dt;
			tree.setTime(t, dt);
			byte[] pix = tree.generateCurrentFrame();
			this.writeFrame(writer, (long)(t * 1000000.0), pix);
		}
		this.finishVideo(writer);
	}

	private IMediaWriter prepareVideo() {
		IMediaWriter writer = ToolFactory.makeWriter(this.params.getFilename());
		writer.addVideoStream(0, 0, IRational.make(this.params.getFrameRate()), this.params.getWidth(), this.params.getHeight());
		return writer;
	}

	private void writeFrame(IMediaWriter writer, long nsec, byte[] pix) {
		BufferedImage bimage = _data2Image(pix, this.params.getWidth(), this.params.getHeight());

		//BufferedImage bgrScreen = convertToType(bimage, BufferedImage.TYPE_3BYTE_BGR);
		writer.encodeVideo(0, bimage, nsec, TimeUnit.NANOSECONDS);

	}

	private void finishVideo(IMediaWriter writer) {
		writer.close();
	}
	*/
	
	/**
	 * Build and return the current frame as an image.
	 */
	public BufferedImage generateImage() {
		byte[] pix = tree.generateCurrentFrame();
		BufferedImage bimage = dataToImage(pix, params.getWidth(), params.getHeight());
		return bimage;
	}
	
	
	/**
	 * Write the given image to the output file specified in our output parameters.
	 */
	public static void writeImageFile(String filename, String filetype, BufferedImage bimage) 
			throws IOException 
	{
		if(filename.equals("-")) {
			ImageIO.write(bimage, filetype, System.out);
		} else {
			File fp = new File(filename);
			fp = new File(fp.getCanonicalPath());
			ImageIO.write(bimage, filetype, fp);
		}
	}
	
	
	/**
	 * Convert an array of raw pixel values, as produced by a Tree, into a 
	 * BufferedImage suitable for use with the rest of Java.
	 */
	public static BufferedImage dataToImage(byte[] pix, int w, int h) {
		DataBuffer data = new DataBufferByte(pix, pix.length);
		int[] bandOffset = {2, 1, 0};
		SampleModel fmt = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, w, h, 3, w * 3, bandOffset);
		Raster raster = Raster.createRaster(fmt, data, new Point(0, 0));
		BufferedImage bimage = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
		bimage.setData(raster);
		return bimage;
	}
}

