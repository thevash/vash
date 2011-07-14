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

import vash.ImageParameters;
import vash.Plane;
import vash.Seed;
import vash.value.Bounded;

public class Const extends OperationNode {
	private static final double MIN_VALUE = -1.0;
	private static final double MAX_VALUE = 1.0;

	private final Bounded v;
	
	/**
	 * Constructor that takes an existing value, for cloning.
	 */
	private Const(Bounded init) {
		super(1, 0);
		assert(init.hasBounds(MIN_VALUE, MAX_VALUE));
		_values[0] = v = init;
	}

	/**
	 * Constructor that takes a specified value (e.g. for testing).
	 */
	public Const(double init) {
		this(new Bounded(init, MIN_VALUE, MAX_VALUE));
	}

	/**
	 * Construct with a random initial value.
	 */
	public Const(Seed s) {
		this(new Bounded(s, MIN_VALUE, MAX_VALUE));
	}
	
	@Override
	public Const clone() {
		return new Const(v.clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		int w = ip.getW();
		int h = ip.getH();
		float v = (float)this.v.getV();
		Plane p = ip.getPlane();
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				p.data[x][y] = v;
			}
		}
		return p;
	}
}
