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


public class Spiral extends OperationNode {
	private final static double MIN_N = 0.0f;
	private final static double MAX_N = 10.0f;
	private final static double MIN_B = -1.0f;
	private final static double MAX_B = 1.0f;

	private final Position center;
	private final Bounded n;
	private final Bounded b;

	private Spiral(Position center, Bounded n, Bounded b, OperationNode v) {
		super(3, 1);
		assert(n.hasBounds(MIN_N, MAX_N));
		assert(b.hasBounds(MIN_B, MAX_B));
		_values[0] = this.center = center;
		_values[1] = this.n = n;
		_values[2] = this.b = b;
		_children[0] = v;
	}
	
	public Spiral(double x, double y, double n, double b, OperationNode v) {
		this(new Position(x, y),
				new Bounded(n, MIN_N, MAX_N),
				new Bounded(b, MIN_B, MAX_B), 
				v);
	}

	public Spiral(Seed s) {
		this(new Position(s),
				new Bounded(s, MIN_N, MAX_N),
				new Bounded(s, MIN_B, MAX_B), 
				null);
	}

	@Override
	public OperationNode clone() {
		return new Spiral(center.clone(), n.clone(), b.clone(), _children[0].clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x0, y0, r, theta, tmp;
		float twoOverSqrtTwo = (float)(2.0 / Math.sqrt(2.0));
		float x = (float)this.center.getX();
		float y = (float)this.center.getY();
		float n = (float)Math.floor(this.n.getV());
		float b = (float)this.b.getV();

		Plane V = _children[0].compute(ip);
		Plane out = ip.getPlane();
	    for(int j = 0; j < ip.getH(); j++ ) {
	    	y0 = Y[j] - y;
			for(int i = 0; i < ip.getW(); i++) {
				x0 = X[i] - x;
				
				r = (((x0 * x0) + (y0 * y0)) * twoOverSqrtTwo) - 1.0f;
				theta = (float)(Math.atan2(y0, x0) / Math.PI);
				tmp = V.data[i][j] - r + (b * (float)Math.pow(theta, n));
				
				while(tmp > 1.0f) tmp -= 1.0f;
				while(tmp < -1.0f) tmp += 1.0f;
				tmp = (float)Math.abs(Math.abs(tmp) - 0.5);
				
				out.data[i][j] = 4.0f * tmp - 1.0f;
			}
	    }
		ip.putPlane(V);

		return out;
	}
/*

    V = self->children[0]->compute_generic(self->children[0], width, height, X, Y);

    data = malloc(sizeof(float) * width * height);
    for(j = 0; j < height; j++) {
            y0 = Y[j] - x;
            for(i = 0; i < width; i++) {
                    x0 = X[i] - y;
                    off = j * width + i;
                    r = (((x0 * x0) + (y0 * y0)) * twoOverSqrtTwo) - 1.0f;
                    theta = atan2f(y0, x0) / M_PI;
                    tmp = V[off] - r + (b * powf(theta, n));

                    while(tmp > 1.0f) tmp -= 1.0f;
                    while(tmp < -1.0f) tmp += 1.0f;
                    tmp = fabsf(fabsf(tmp) - 0.5f);

                    data[off] = 4.0f * tmp - 1.0f;
            }
    }
    */
}
