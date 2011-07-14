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

public class Flower extends OperationNode {
	private static final double MAX_SIZE = 2.5;

	private final Position center;
	private final Wrapping angle;
	private final Bounded size;
	private final Bounded ratio;
	private final int n_points;

	private Flower(Position center, Wrapping angle, Bounded size, Bounded ratio, int n_points) {
		super(4, 0);
		assert(center.hasBounds(-1, -1, 1, 1));
		assert(angle.hasBounds(0, 360));
		assert(size.hasBounds(0, MAX_SIZE));
		assert(ratio.hasBounds(0, 1));
		_values[0] = this.center = center;
		_values[1] = this.angle = angle;
		_values[2] = this.size = size;
		_values[3] = this.ratio = ratio;
		this.n_points = n_points;
	}
	
	public Flower(double x, double y, double angle, double size, double ratio, int n_points) {
		this(new Position(x, y), new Wrapping(angle, 0, 360), new Bounded(size, 0, MAX_SIZE), new Bounded(ratio, 0, 1), n_points);
	}
	
	public Flower(Seed s) {
		super(4, 0);
		_values[0] = this.center = new Position(s);
		_values[1] = this.angle = new Wrapping(s, 0, 360);
		_values[2] = this.size = new Bounded(s, 0.0, MAX_SIZE);
		_values[3] = this.ratio = new Bounded(s, 0.0, 1.0);
		this.n_points = (int)(s.nextDouble() * 11.0) + 1;
	}

	@Override
	public OperationNode clone() {
		return new Flower(center.clone(), angle.clone(), size.clone(), ratio.clone(), n_points);
	}

	@Override
	public Plane compute(ImageParameters ip) {
		Plane out = ip.getPlane();
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x = (float)this.center.getX();
		float y = (float)this.center.getY();
		float angle = (float)this.angle.getV();
		float sz = (float)this.size.getV();
		float ratio = (float)this.ratio.getV();
		float inner = sz * ratio;
		
		// provide manual anti-aliasing
		float fringe = X[2] - X[0];

        // note: adjust the angle by -PI/2 so 0 is up
        float ca = (float)Math.cos((angle * Math.PI / 180.0) - (Math.PI / 2.0));
        float sa = (float)Math.sin((angle * Math.PI / 180.0) - (Math.PI / 2.0));

		for(int j = 0; j < ip.getH(); j++) {
			float y0 = Y[j] - y;
			for(int i = 0; i < ip.getW(); i++) {
				float x0 = X[i] - x;
				
				// distance from center
				float d = (float)Math.sqrt(x0 * x0 + y0 * y0);
				
				// rotate into angle
				float x1 = (x0 * ca) - (y0 * sa);
				float y1 = (x0 * sa) + (y0 * ca);
				
				if(d < sz * ratio) { // inside
					out.data[i][j] = 1.0f;
				} else if(d > sz) { // outsize
					out.data[i][j] = -1.0f;
				} else {
					// is the point in an arm?
					// the spiky bits are 0 + (n / n_points)
					float theta = (float)((Math.atan2(y1, x1) / Math.PI + 1.0) / 2.0); // [0,1] on full circle
					float expanded = theta * n_points; // [0,n] on full circle
					float offset = expanded - (int)expanded; // [0,1] on each segment
					offset = offset * 2.0f - 1.0f; // [-1,1] centered on segment
					// the ratio from inner to outer, inverted
					float r = ((d - inner) * (1.0f / (sz - inner)));

					// inside/outside test is now simply a compare
					float dist = r - Math.abs(offset);
					if(dist < 0) {
						out.data[i][j] = 1.0f;
					} else {
						if(dist < fringe) {
							out.data[i][j] = 1.0f - (2.0f * dist / fringe);
						} else {
							out.data[i][j] = -1.0f;
						}
					}
				}
			}
		}

		return out;
	}
}
