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

	@Override
	public LinearGradient clone() {
		return new LinearGradient(p0.clone(), p1.clone());
	}

	private void _computeInternal(
			Plane out, float w, float h, 
			float[] X, float[] Y, 
			float x0, float y0, float x1, float y1) {
		float denom, b, m, d0to1, intX, intY, d0toInt, d1toInt, d;

		denom = x1 - x0;
        m = (y1 - y0) / denom;
        b = m * x0 - y0;
        d0to1 = distance(x0, y0, x1, y1);

	    for(int j = 0; j < h; j++ ) {
			for(int i = 0; i < w; i++) {
				intX = (m * Y[j] + X[i] - m * b) / (m * m + 1);
				intY = (m * m * Y[j] + m * X[i] + b) / (m * m + 1);
				
				d0toInt = distance(x0, y0, intX, intY);
				d1toInt = distance(x1, y1, intX, intY);
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
		float denom;

		denom = x1 - x0;
		if(denom < 0.1f) {
			/*
			 * Since we use the slope to compute the gradient, we have a nasty
			 * singularity when the slope is infinite.  If the slope is near
			 * infinite, we compute the gradient on the plane mirrored about
			 * y=x and then flip the result back when we are done.
			 */
			if(w == h) {
				_computeInternal(out, w, h, X, Y, y0, x0, y1, x1);
				Plane tmp = ip.getPlane();
				for(int j = 0; j < h; j++) {
					for(int i = 0; i < w; i++) {
						tmp.data[i][j] = out.data[j][i];
					}
				}
				ip.putPlane(out);
				out = tmp;
			} else {
				Plane yxOut = ip.getYXPlane();
				_computeInternal(yxOut, h, w, Y, X, y0, x0, y1, x1);
				for(int j = 0; j < h; j++) {
					for(int i = 0; i < w; i++) {
						out.data[i][j] = yxOut.data[(int)h - j - 1][(int)w - i - 1];
					}
				}
				ip.putYXPlane(yxOut);
			}
		} else {
			_computeInternal(out, w, h, X, Y, x0, y0, x1, y1);
		}
		
		return out;
	}
}
