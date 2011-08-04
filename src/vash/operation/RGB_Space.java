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

public class RGB_Space extends ColorNode {
	public RGB_Space(OperationNode r, OperationNode g, OperationNode b) {
		super();
		_children[0] = r;
		_children[1] = g;
		_children[2] = b;
	}

	public RGB_Space(Seed s) {
		super();
	}
	
	@Override
	public OperationNode clone() {
		return new RGB_Space(_children[0].clone(), _children[1].clone(), _children[2].clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		throw new RuntimeException("Wrong compute method on colorspace called.");
	}

	@Override
	public byte[] compute(ImageParameters ip, boolean this_method_is_different) {
		int w = ip.getW();
		int h = ip.getH();
		Plane R = _children[0].compute(ip);
		Plane G = _children[1].compute(ip);
		Plane B = _children[2].compute(ip);
		byte pix[] = new byte[w * h * 3];

		int index = 0;
		for (int y = h - 1; y >= 0; y--) {
		    for (int x = 0; x < w; x++) {
		    	byte r = (byte)Math.floor((R.data[x][y] + 1.0f) / 2.0f * 255.0f);
		    	byte g = (byte)Math.floor((G.data[x][y] + 1.0f) / 2.0f * 255.0f);
		    	byte b = (byte)Math.floor((B.data[x][y] + 1.0f) / 2.0f * 255.0f);
		    	pix[index++] = b;
		    	pix[index++] = g;
		    	pix[index++] = r;
		    }
		}
		
		ip.putPlane(R);
		ip.putPlane(G);
		ip.putPlane(B);
		
		return pix;
	}
}
