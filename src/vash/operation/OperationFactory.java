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

import vash.Seed;


/**
 * Factory to create operation instances given an Operation enum value. 
 */
public final class OperationFactory {
	public static OperationNode createNode(Operation type, Seed s) {
		switch(type) {
		case RGB: 				return new RGB_Space(s);
		// arithmetic
		case ABSOLUTE: 			return new Absolute(s);
		case ADD: 				return new Add(s);
		case DIVIDE: 			return new Divide(s);
		case EXPONENTIATE: 		return new Exponentiate(s);
		case INVERT: 			return new Invert(s);
		case MODULUS: 			return new Modulus(s);
		case MULTIPLY: 			return new Multiply(s);
		// trig
		case SINC: 				return new Sinc(s);
		case SINE: 				return new Sine(s);
		case SPIRAL: 			return new Spiral(s);
		case SQUIRCLE: 			return new Squircle(s);
		// LEAF
		case CONST: 			return new Const(s);
		case ELLIPSE:			return new Ellipse(s);
		case FLOWER: 			return new Flower(s);
		case GRADIENT_LINEAR: 	return createLinearGradient(s);
		case GRADIENT_RADIAL: 	return new RadialGradient(s);
		case POLAR_THETA: 		return new PolarTheta(s);
		}
		throw new IllegalArgumentException("Unknown operation type: " + type.toString());
	}
	
	private static OperationNode createLinearGradient(Seed s) {
		String algo = s.getAlgorithm();
		if(algo.equals("1") || algo.equals("1-fast"))
			return new LinearGradient(s);
		return new LinearGradient1(s);
	}
}
