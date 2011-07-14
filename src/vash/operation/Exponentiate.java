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

public class Exponentiate extends BinaryOperationNode {
	public Exponentiate(OperationNode a, OperationNode b) {super(a,b);}
	public Exponentiate(Seed s) {super(s);}
	
	@Override
	public OperationNode clone() {
		return new Exponentiate(_children[0].clone(), _children[1].clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float twoOverPi = (float)(2.0 / Math.PI);
		Plane A = _children[0].compute(ip);
		Plane B = _children[1].compute(ip);
		Plane out = ip.getPlane();
	    for(int j = 0; j < ip.getH(); j++ ) {
			for(int i = 0; i < ip.getW(); i++) {
				float v0 = A.data[i][j];
				float val;
				if(v0 == 0.0f)
					val = 0.0f;
				else if(v0 < 0.0f)
					val = (float)(-Math.pow(-v0, B.data[i][j]));
				else
					val = (float)Math.pow(v0, B.data[i][j]);
				val *= twoOverPi;
				out.data[i][j] = OperationNode.clampf(val, -1.0f, 1.0f);
			}
	    }
	    ip.putPlane(A);
	    ip.putPlane(B);
	    return out;
	}
}
