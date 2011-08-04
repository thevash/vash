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


/**
 * Controls how we write image(s) data to storage.  On construction, the value of the
 * given output filename is analyzed in order to select an output mode.  The filename
 * extension currently must be one of "jpg", "png", or "bmp".
 */
public class OutputParameters {
	private static final int MINIMUM_SIZE = 4;
	
	/**
	 * The output filename, regardless of other output modes.
	 */
	private final String filename;
	
	/*
	 * Set true if the filetype is a video output mode.  Controls which output
	 * method we use.
	 */
	//private final boolean isVideo;
	
	/**
	 * In isVideo=false mode, this is set to the type flag we will pass to the output engine,
	 * based on the extension of our filename.
	 */
	private final String imageType;
	
	/**
	 * The height and width of the output image(s).
	 */
	private final int width;
	private final int height;


	/**
	 * Initialize a parameters from values passed on the command line.
	 * @param opts
	 */
	public OutputParameters(Options opts) {
		this(opts.getOutput(), opts.getOutputFormat(), opts.getWidth(), opts.getHeight());
	}
	

	/**
	 * Initialize parameters from given values.  Guess output format from filename.
	 * @param filename The name of the target file to write to.
	 * @param width The width of the image to write.
	 * @param height The height of the image to write.
	 */
	public OutputParameters(String filename, int width, int height) {
		this(filename, guessFormat(filename), width, height);
	}

	
	/**
	 * Initialize parameters from given values.
	 * @param filename The name of the target file to write to.
	 * @param format The image output format (bmp, png, or jpeg).
	 * @param width The width of the image to write.
	 * @param height The height of the image to write.
	 */
	public OutputParameters(String filename, String format, int width, int height) {
		if(format == null) {
			format = guessFormat(filename);
		}
		if(!format.equals("png") && !format.equals("jpeg") && !format.equals("bmp")) {
			throw new IllegalArgumentException("Unknown output format: '" + format + "'");
		}
		if(width < MINIMUM_SIZE || height < MINIMUM_SIZE) {
			throw new IllegalArgumentException("Width and Height must both be at least 4.");
		}
		this.filename = filename;
		this.imageType = format;
		this.width = width;
		this.height = height;
	}

	/**
	 * Guess the filetype from the extension.
	 * @param filename
	 * @return
	 */
	public static String guessFormat(String filename) {
		String tmp = filename.toLowerCase();
		if(tmp.endsWith(".png")) return "png";
		else if(tmp.endsWith(".jpg")) return "jpeg";
		else if(tmp.endsWith(".bmp")) return "bmp";
		else if(tmp.equals("-")) return "png";
		throw new IllegalArgumentException("Unknown media type for given extension: \"" + 
					tmp.subSequence(filename.length() - 4, filename.length()) + "\"");
	}

	
	public String getFilename() {
		return filename;
	}


	public String getImageType() {
		return imageType;
	}

	
	public int getWidth() {
		return width;
	}

	
	public int getHeight() {
		return height;
	}
}
