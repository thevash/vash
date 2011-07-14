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

import vash.Seed;


/**
 * The abstract base class of all Values in a tree.  Values are attached to a 
 * node and provide input that does not vary as a function of X/Y position in 
 * the computation plane.  This is opposed to children of a node which provide
 * a full plane of values that are, presumably, different at each X/Y 
 * coordinate.
 */
public interface Value {
	abstract public Value clone();
	abstract public void createKeyFrame(double t, Seed s);
	abstract public void setTime(double t, double dt);
}
