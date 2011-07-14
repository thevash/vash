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
import vash.value.Wrapping;

public class Sine extends OperationNode {
	private final static double MIN_FREQ = (float)-Math.PI;
	private final static double MAX_FREQ = (float)Math.PI;
	private final static double MIN_PHASE = (float)-Math.PI;
	private final static double MAX_PHASE = (float)Math.PI;
	
	private final Bounded frequency;
	private final Wrapping phase;
	
	private Sine(Bounded frequency, Wrapping phase, OperationNode child) {
		super(2, 1);
		assert(frequency.hasBounds(MIN_FREQ, MAX_FREQ));
		assert(phase.hasBounds(MIN_PHASE, MAX_PHASE));
		_values[0] = this.frequency = frequency;
		_values[1] = this.phase = phase;
		_children[0] = child;
	}

	public Sine(double freq, double phase, OperationNode child) {
		this(new Bounded(freq, MIN_FREQ, MAX_FREQ), new Wrapping(phase, MIN_PHASE, MAX_PHASE), child);
	}
	
	public Sine(Seed s) {
		this(new Bounded(s, MIN_FREQ, MAX_FREQ), new Wrapping(s, MIN_PHASE, MAX_PHASE), null);
	}
	
	@Override
	public OperationNode clone() {
		return new Sine(this.frequency.clone(), this.phase.clone(), _children[0].clone());
	}

	@Override
	public Plane compute(ImageParameters ip) {
		float freq = (float)this.frequency.getV();
		float phase = (float)this.phase.getV();
		Plane A = _children[0].compute(ip);
		Plane out = ip.getPlane();
	    for(int j = 0; j < ip.getH(); j++ ) {
			for(int i = 0; i < ip.getW(); i++) {
				out.data[i][j] = (float)Math.sin(A.data[i][j] * freq + phase);
			}
	    }
	    ip.putPlane(A);
	    return out;
	}
}
