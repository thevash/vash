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
import vash.value.Position;


public class LinearGradient1 extends OperationNode {
	private final Position p0;
	private final Position p1;

	private LinearGradient1(Position p0, Position p1) {
		super(2, 0);
		_values[0] = this.p0 = p0;
		_values[1] = this.p1 = p1;
	}

	public LinearGradient1(double x0, double y0, double x1, double y1) {
		this(new Position(x0, y0), new Position(x1, y1));
	}

	public LinearGradient1(Seed s) {
		this(new Position(s), new Position(s));
	}

	@Override
	public OperationNode clone() {
		return new LinearGradient1(p0.clone(), p1.clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float w = ip.getW();
		float h = ip.getH();
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x0 = (float)p0.getX();
		float y0 = (float)p0.getY();
		float x1 = (float)p1.getX();
		float y1 = (float)p1.getY();
		Plane out = ip.getPlane();
		float pX, pY, ppY;
		float color;
		
		// get angle p0->p1 vector (Note: subtract p0 so we are from the origin)
		double ang = Math.atan2(y1 - y0, x1 - x0);
		// Note: actual angle is negative and offset by 1/4 Tau, because of how atan2 is defined
		// Note2: we twist by an extra 1/2 Tau here, so that we can offset by +1 instead of -1 in our inner loop
		ang = Math.PI * 3.0 / 2.0 - ang; 
		
		// get length of p0->p1 for use later to scale our points 
		float len = distance(x0, y0, x1, y1);
		
		// precompute the sin and cos we will need for every pixel
		float sa = (float)Math.sin(ang);
		float ca = (float)Math.cos(ang);
		
	    for(int j = 0; j < h; j++ ) {
	    	pY = Y[j] - y0;
			for(int i = 0; i < w; i++) {
				pX = X[i] - x0;

				// rotate by ang
				//ppX = pX * ca - pY * sa; // Note: we don't need the x component
				ppY = pX * sa + pY * ca;

				// lightness is distance along p1->p0, but centered at p0
				color =  ppY / len + 1.0f;
				
				// scale into [-1,1] and clamp
				color -= 0.5f;
				color *= 2.0f;
				out.data[i][j] = clampf(color, -1.0f, 1.0f);
			}
		}

		return out;
	}
}
