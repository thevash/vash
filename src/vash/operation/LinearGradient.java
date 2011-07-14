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

public class LinearGradient extends OperationNode {
	private final Position p0;
	private final Position p1;


	private LinearGradient(Position p0, Position p1) {
		super(2, 0);
		_values[0] = this.p0 = p0;
		_values[1] = this.p1 = p1;
	}

	public LinearGradient(double x0, double y0, double x1, double y1) {
		this(new Position(x0, y0), new Position(x1, y1));
	}
	
	public LinearGradient(Seed s) {
		this(new Position(s), new Position(s));
	}

	private float _dist(float x0, float y0, float x1, float y1) {
		float xp = x1 - x0;
		float yp = y1 - y0;
		return (float)Math.sqrt(xp * xp + yp * yp);
	}

	@Override
	public LinearGradient clone() {
		return new LinearGradient(p0.clone(), p1.clone());
	}
	
	@Override
	public Plane compute(ImageParameters ip) {
		float denom, b, m, d0to1, intX, intY, d0toInt, d1toInt, d;
		float w = ip.getW();
		float h = ip.getH();
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x0 = (float)p0.getX();
		float y0 = (float)p0.getY();
		float x1 = (float)p1.getX();
		float y1 = (float)p1.getY();
		boolean needFlip = false;
		Plane out = ip.getPlane();

		denom = x1 - x0;
		if(denom < 0.1f) {
			needFlip = true;
			float tmp;
			tmp = y0;
			y0 = x0;
			x0 = tmp;
			tmp = y1;
			y1 = x1;
			x1 = tmp;
			denom = x1 - x0;
		}

        m = (y1 - y0) / denom;
        b = m * x0 - y0;
        d0to1 = _dist(x0, y0, x1, y1);

	    for(int j = 0; j < h; j++ ) {
			for(int i = 0; i < w; i++) {
				intX = (m * Y[j] + X[i] - m * b) / (m * m + 1);
				intY = (m * m * Y[j] + m * X[i] + b) / (m * m + 1);
				
				d0toInt = _dist(x0, y0, intX, intY);
				d1toInt = _dist(x1, y1, intX, intY);
				d = d0toInt / d0to1;
				
				if((d0toInt + d1toInt) > (d0to1 + 0.0001)) {
					if(d1toInt > d0toInt) {
						out.data[i][j] = -1.0f;
					} else {
						out.data[i][j] = 1.0f;
					}
				} else {
					out.data[i][j] = d * 2.0f - 1.0f;
				}
			}
		}
	    
	    if(needFlip) {
	    	Plane out2 = ip.getPlane();
		    for(int j = 0; j < h; j++ ) {
				for(int i = 0; i < w; i++) {
					out2.data[i][j] = out.data[j][i];
				}
		    }
		    ip.putPlane(out);
		    return out2;
	    }
		
		return out;
	}
}
