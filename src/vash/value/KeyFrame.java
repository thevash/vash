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
package vash.value;

/**
 * A Pair mapping between a time in seconds as a double, to some other 
 * specified type.
 *
 * @param <T>
 * 
 * TODO: Unused in version1.  Will be the center of value animation, in v2.
 */
class KeyFrame <T> {
	private double t;
	private T v;
	
	public KeyFrame(double t, T v) {
		super();
		this.t = t;
		this.v = v;
	}

	public double getT() {
		return t;
	}
	public void setT(double t) {
		this.t = t;
	}
	public T getV() {
		return v;
	}
	public void setV(T v) {
		this.v = v;
	}
	
}
