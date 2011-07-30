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
import vash.value.Position;

public class Squircle extends OperationNode {
	private final static double MIN_R = 0.0f;
	private final static double MAX_R = 2.0f;
	private final static double MIN_N = 0.0f;
	private final static double MAX_N = 4.0f;
	
	private final Position center;
	private final Bounded r;
	private final Bounded n;

	private Squircle(Position center, Bounded r, Bounded n, OperationNode a, OperationNode b) {
		super(3, 2);
		assert(r.hasBounds(MIN_R, MAX_R));
		assert(n.hasBounds(MIN_N, MAX_N));
		_values[0] = this.center = center;
		_values[1] = this.r = r;
		_values[2] = this.n = n;
		_children[0] = a;
		_children[1] = b;
	}

	public Squircle(double x, double y, double r, double n, OperationNode a, OperationNode b) {
		this(new Position(x, y),
				new Bounded(r, MIN_R, MAX_R), 
				new Bounded(n, MIN_N, MAX_N),
				a, b);
	}

	public Squircle(Seed s) {
		this(new Position(s),
				new Bounded(s, MIN_R, MAX_R), 
				new Bounded(s, MIN_N, MAX_N),
				null, null);
	}

	@Override
	public OperationNode clone() {
		return new Squircle(center.clone(), r.clone(), n.clone(), _children[0].clone(), _children[1].clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x0, y0, a, b, numer, denom;
		float x = (float)this.center.getX();
		float y = (float)this.center.getY();
		float r = (float)this.r.getV();
		float n = (float)this.n.getV();

		Plane A = _children[0].compute(ip);
		Plane B = _children[1].compute(ip);
		Plane out = ip.getPlane();
	    for(int j = 0; j < ip.getH(); j++ ) {
	    	y0 = Y[j] - y;
			for(int i = 0; i < ip.getW(); i++) {
				x0 = X[i] - x;
				a = Math.abs(x0 - A.data[i][j]);
				b = Math.abs(y0 - B.data[i][j]);
				numer = (float)-(Math.pow(a, n) + Math.pow(b, n));
				denom = (float)Math.pow(r, n);
				if(denom == 0.0f)
					out.data[i][j] = 1.0f;
				else
					out.data[i][j] = clampf(numer / denom, -1.0f, 1.0f);
			}
	    }
	    ip.putPlane(A);
	    ip.putPlane(B);
	    return out;
	}
}
