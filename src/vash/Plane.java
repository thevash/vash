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
 * A 2D plane composed of floats.  Contains extra information that we may need
 * to iterate over elements in the plane, absent any external information.
 */
public class Plane {
	private final int w;
	private final int h;
	public final float[][] data;
	
	/**
	 * Allocate a new plane of values with the given width and height.
	 */
	Plane(int w, int h) {
		this.w = w;
		this.h = h;
		this.data = new float[w][h];
	}
	
	public int getW() {return this.w;}
	public int getH() {return this.h;}
}
