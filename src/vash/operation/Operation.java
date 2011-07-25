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
 * A complete list of all available operations.
 */
public enum Operation {
	// colors
	RGB,
	// arithmetic
	ABSOLUTE,
	ADD,
	DIVIDE,
	EXPONENTIATE,
	INVERT,
	MODULUS,
	MULTIPLY,
	// trig
	SINC,
	SINE,
	SPIRAL,
	SQUIRCLE,
	// LEAF
	CONST,
	ELLIPSE,
	FLOWER,
	GRADIENT_LINEAR,
	GRADIENT_RADIAL,
	POLAR_THETA,
	;
}
