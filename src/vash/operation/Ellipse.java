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

public class Ellipse extends OperationNode {
	private final Position f0;
	private final Position f1;
	
	// NOTE: size is a fraction between 0.1 and 1 between the minimum and maximum size of an ellipse.
	// The minimum size is the distance between f0 and f1 and the max size is twice the distance between
	// f0 and f1.
	private final Bounded size;

	private static final double MIN_SIZE = 0.1;
	private static final double MAX_SIZE = 1.0;
	private static final double MIN_POSITION = -1.0;
	private static final double MAX_POSITION = 1.0;
	

	private Ellipse(Position f0, Position f1, Bounded size) {
		super(3, 0);
		assert(f0.hasBounds(MIN_POSITION, MIN_POSITION, MAX_POSITION, MAX_POSITION));
		assert(f1.hasBounds(MIN_POSITION, MIN_POSITION, MAX_POSITION, MAX_POSITION));
		assert(size.hasBounds(MIN_SIZE, MAX_SIZE));
		_values[0] = this.f0 = f0;
		_values[1] = this.f1 = f1;
		_values[2] = this.size = size;
	}
	
	public Ellipse(double x0, double y0, double x1, double y1, double size) {
		this(new Position(x0, y0), new Position(x1, y1), new Bounded(size, MIN_SIZE, MAX_SIZE));
	}
	
	public Ellipse(Seed s) {
		this(new Position(s), new Position(s), new Bounded(s, MIN_SIZE, MAX_SIZE));
	}

	@Override
	public OperationNode clone() {
		return new Ellipse(f0.clone(), f1.clone(), size.clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float pX, pY, dist;
		float[] X = ip.getXValues();
		float[] Y = ip.getYValues();
		float x0 = (float)this.f0.getX();
		float y0 = (float)this.f0.getY();
		float x1 = (float)this.f1.getX();
		float y1 = (float)this.f1.getY();
		float minDist = distance(x0, y0, x1, y1);
		float szFraction = (float)this.size.getV();
		float sz = minDist + szFraction * minDist;
		
		float fringe = X[2] - X[0];
		
		Plane out = ip.getPlane();
		for(int j = 0; j < ip.getH(); j++) {
			pY = Y[j];
			for(int i = 0; i < ip.getW(); i++) {
				pX = X[i];
				
				dist = distance(pX, pY, x0, y0) + distance(pX, pY, x1, y1);
				
				if(dist < sz) {
					out.data[i][j] = 1.0f;
				} else {
					if(dist < sz + fringe) {
						out.data[i][j] = 1.0f - (dist - sz) / fringe * 2.0f; 
					} else {
						out.data[i][j] = -1.0f;
					}
				}
			}
		}

		return out;
	}
}
