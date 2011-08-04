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

import java.util.LinkedList;


/**
 * Encapsulate all of the image generation data that is needed by a
 * node to compute values for itself.
 */
public class ImageParameters {
	final private int w;
	final private int h;
	final private float[] X;
	final private float[] Y;
	private final LinkedList<Plane> cache;
	private long _puts = 0;
	private long _gets = 0;
	private final static long MAX_CACHE_SIZE = 64 * 1024 * 1024;
	private final static boolean DEBUG = false;
	
	/**
	 * Initialize a new set of image parameters for the given width and height.
	 */
	public ImageParameters(int w, int h) {
		this.w = w;
		this.h = h;
		this.cache = new LinkedList<Plane>();
		this.X = new float[w];
		this.Y = new float[h];
		
		int i, j;
		float gX, gY;
		float delta_x = 2.0f / w;
		float delta_y = 2.0f / h;
		for(j = 0, gY = 1.0f - (delta_y / 2.0f); j < h; j++, gY -= delta_y)
			Y[j] = gY;
		for(i = 0, gX = -1.0f + (delta_x / 2.0f); i < w; i++, gX += delta_x)
			X[i] = gX;
	}

	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}

	
	/**
	 * Returns an array of width length filled such that each index offset is 
	 * the logical X value for that index.  We use this as a faster way to map 
	 * [0,width) offsets into the [-1.0, 1.0] logical range used by our 
	 * computations.
	 */
	public float[] getXValues() {
		return X;
	}
	
	
	/**
	 * Returns an array of height length filled such that each index offset is 
	 * the logical Y value for that index.  We use this as a faster way to map 
	 * [0,height) offsets into the [1.0, -1.0] logical range used by our 
	 * computations.
	 */
	public float[] getYValues() {
		return Y;
	}
	
	
	/**
	 * Returns a new, or cached, plane of values.  The plane values will not be
	 * initialized to any specific value.
	 */
	public Plane getPlane() {
		_gets += 1;
		if(DEBUG) {
			long tmp = ((_gets - _puts) + cache.size()) * (w * h * 4);
			System.out.format("GetPlane: %d gets, %d puts, %d out, %d cached: %d%n", 
					_gets, _puts, _gets - _puts, cache.size(), tmp);
		}
		if(cache.size() > 0) {
			return cache.removeFirst();
		}
		return new Plane(this.w, this.h);
	}

	/**
	 * Give back a plane taken with getPlane.  This allows us to re-use planes,
	 * rather than re-allocating new, massive arrays constantly.
	 */
	public void putPlane(Plane p) {
		_puts += 1;
		long outstanding = ((_gets - _puts) + cache.size()) * (w * h * 4);
		if(outstanding < MAX_CACHE_SIZE) {
			cache.addFirst(p);
		}
	}
	
	/**
	 * Returns a new plane of values who's coordinates are mirrored around 
	 * y=x.  This is useful in some nodes that use slope, in order to avoid 
	 * singularities around vertical lines.
	 * @return a one-off plane; must be returned with putYXPlane
	 */
	public Plane getYXPlane() {
		return new Plane(this.h, this.w);
	}

	/**
	 * Give back a plane taken with getYXPlane.
	 * @param p the plane allocated with getYXPlane
	 */
	public void putYXPlane(Plane p) {
	}
}

