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
import vash.value.Wrapping;

public class RadialGradient extends OperationNode {
	private static final double MIN_SIZE = 0.1;
	private static final double MAX_SIZE = 0.8; 
	private static final double MIN_ANGLE = 0.0;
	private static final double MAX_ANGLE = 360.0; 
	
	private final Position center;
	private final Bounded width;
	private final Bounded height;
	private final Wrapping angle;
	

	private RadialGradient(Position center, Bounded w, Bounded h, Wrapping angle) {
		super(4, 0);
		assert(w.hasBounds(MIN_SIZE, MAX_SIZE));
		assert(h.hasBounds(MIN_SIZE, MAX_SIZE));
		assert(angle.hasBounds(MIN_ANGLE, MAX_ANGLE));
		_values[0] = this.center = center;
		_values[1] = this.width = w;
		_values[2] = this.height = h;
		_values[3] = this.angle = angle;
	}
	
	public RadialGradient(double x, double y, double w, double h, double angle) {
		this(new Position(x, y), 
				new Bounded(w, MIN_SIZE, MAX_SIZE), 
				new Bounded(h, MIN_SIZE, MAX_SIZE),
				new Wrapping(angle, MIN_ANGLE, MAX_ANGLE));
	}
	
	public RadialGradient(Seed s) {
		this(new Position(s), 
				new Bounded(s, MIN_SIZE, MAX_SIZE), 
				new Bounded(s, MIN_SIZE, MAX_SIZE), 
				new Wrapping(s, MIN_ANGLE, MAX_ANGLE));
	}

	@Override
	public OperationNode clone() {
		return new RadialGradient(center.clone(), width.clone(), height.clone(), angle.clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float x0, y0, x1, y1, x2, y2, tmp;
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x = (float)this.center.getX();
		float y = (float)this.center.getY();
		float w = (float)this.width.getV();
		float h = (float)this.height.getV();
		float angle = (float)this.angle.getV();
		float twoOverSqrtTwo = (float)(2.0 / Math.sqrt(2.0));
		
		Plane out = ip.getPlane();

        // note: adjust the angle by -PI/2 so 0 is up
        float ca = (float)Math.cos((angle * Math.PI / 180.0) - (Math.PI / 2.0));
        float sa = (float)Math.sin((angle * Math.PI / 180.0) - (Math.PI / 2.0));

		for(int j = 0; j < ip.getH(); j++) {
			y0 = Y[j] - y;
			for(int i = 0; i < ip.getW(); i++) {
				x0 = X[i] - x;
				
				// rotate
				x1 = (x0 * ca) - (y0 * sa);
				y1 = (x0 * sa) + (y0 * ca);
				
				// squeeze by proportion
				x2 = x1 / w;
				y2 = y1 / h;
				
				// intensity in proportion to distance
				tmp = -(float)Math.sqrt(x2 * x2 + y2 * y2) * twoOverSqrtTwo + 1.0f;
				out.data[i][j] = OperationNode.clampf(tmp, -1.0f, 1.0f);
			}
		}

		return out;
	}
}
