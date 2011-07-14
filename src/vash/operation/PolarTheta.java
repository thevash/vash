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
import vash.value.Wrapping;

public class PolarTheta extends OperationNode {
	private final Position center;
	private final Wrapping angle;
	
	private PolarTheta(Position center, Wrapping angle) {
		super(2, 0);
		assert(this.center.hasBounds(-1, -1, 1, 1));
		assert(this.angle.hasBounds(-1, 1));
		_values[0] = this.center = center;
		_values[1] = this.angle = angle;
	}
	
	public PolarTheta(double x, double y, double angle) {
		this(new Position(x, y), new Wrapping(angle, -1, 1));
	}
	
	public PolarTheta(Seed s) {
		super(2, 0);
		_values[0] = this.center = new Position(s);
		_values[1] = this.angle = new Wrapping(s, -1, 1);
	}

	@Override
	public OperationNode clone() {
		return new PolarTheta(center.clone(), angle.clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float x0, y0, x1, y1;
		float angle = (float)(this.angle.getV());
        float ca = (float)Math.cos(angle * Math.PI);
        float sa = (float)Math.sin(angle * Math.PI);
		float x = (float)this.center.getX();
		float y = (float)this.center.getY();
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float w = ip.getW();
		float h = ip.getH();
		Plane out = ip.getPlane();
		
		for(int j = 0; j < h; j++) {
			y0 = Y[j] - y;
			for(int i = 0; i < w; i++) {
				x0 = X[i] - x;
				x1 = (x0 * ca) - (y0 * sa);
				y1 = (x0 * sa) + (y0 * ca);
				out.data[i][j] = (float)(Math.atan2(y1, x1) / Math.PI);
			}
		}
		
		return out;
	}
}
