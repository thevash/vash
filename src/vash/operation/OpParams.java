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
package vash.operation;

/**
 * A simple class that holds the parameters for an operation class, for an
 * algorithm specifier.
 */
public class OpParams {
	public final double ratio;
	public final double channels;

	/**
	 * Instantiate a new operation parameters with a default channels count of 3.
	 * @param ratio
	 */
	public OpParams(double ratio) {
		this.ratio = ratio;
		this.channels = 3.0;
	}

	/**
	 * Instantiate a new operation parameters.
	 * @param ratio
	 * @param channels
	 */
	public OpParams(double ratio, double channels) {
		this.ratio = ratio;
		this.channels = channels;
	}
}
