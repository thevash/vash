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
package vash;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import vash.operation.ColorNode;
import vash.operation.Operation;
import vash.operation.OperationFactory;
import vash.operation.OperationNode;
import vash.value.Value;


/**
 * A tree of operations which represent the computation of an image.
 */
public class Tree {
	// we use these at class construction to precompute values
	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	private double _totalFreq(Operation[] data) {
		double sum = 0.0;
		for(Operation item : data) {
			sum += this.params.getOperationRatio(item);
		}
		return sum;
	}
	
	// operation sets for our guided random walks
	private static final Operation[] TOPS = {
			Operation.RGB,
			};
	private static final Operation[] NODES = {
			// Unary
			Operation.ABSOLUTE,
			Operation.INVERT,
			// Binary
			Operation.ADD,
			Operation.DIVIDE,
			Operation.EXPONENTIATE,
			Operation.MODULUS,
			Operation.MULTIPLY,
			// Trig
			Operation.SINC,
			Operation.SINE,
			Operation.SPIRAL,
			Operation.SQUIRCLE,
			};
	private static final Operation[] LEAFS = {
			Operation.CONST,
			Operation.ELLIPSE,
			Operation.FLOWER,
			Operation.GRADIENT_LINEAR,
			Operation.GRADIENT_RADIAL,
			Operation.POLAR_THETA,
			};
	private static final Operation [] NODES_AND_LEAFS = concat(NODES, LEAFS);

	// pre-compute our total frequencies when we learn our generation parameters
	private final double TOPS_total;
	private final double NODES_total;
	private final double LEAFS_total;
	private final double NODES_AND_LEAFS_total;

	// tree parameters
	private final TreeParameters params;
	private final ColorNode tree;
	private final ArrayList<Value> values = new ArrayList<Value>();
	private ImageParameters ip;
	
	
	/**
	 * Construct a tree with the given tree parameters.
	 */
	public Tree(TreeParameters params) {
		this.params = params;
		TOPS_total = _totalFreq(TOPS);
		NODES_total = _totalFreq(NODES);
		LEAFS_total = _totalFreq(LEAFS);
		NODES_AND_LEAFS_total = NODES_total + LEAFS_total;

		this.tree = this._buildToplevel();
		this.tree.accumulateValues(this.values);
	}

	
	/**
	 * Not for public use.
	 * @param s
	 * @param count
	 * @return
	 */
	public static boolean[] __buildChannelMask(Seed s, int count) {
		switch(count) {
		case 3: return new boolean[] {true, true, true};
		case 2:
			switch(s.nextInt(3)) {
			case 0: return new boolean[] {false, true, true};
			case 1: return new boolean[] {true, false, true};
			case 2: return new boolean[] {true, true, false};
			}
		case 1:
			switch(s.nextInt(3)) {
			case 0: return new boolean[] {true, false, false};
			case 1: return new boolean[] {false, true, false};
			case 2: return new boolean[] {false, false, true};
			}
		case 0: return new boolean[] {false, false, false};
		default: throw new IllegalArgumentException("BuildChannelMask needs count in [0..3]");
		}
	}

	/**
	 * Not for public use.
	 * @param s
	 * @param channels
	 * @return
	 */
	public static int __getChannelExclusionCount(Seed s, double channels) {
		// NOTE: we have not touched these doubles at all, so it is safe to compare directly here
		if(channels <= 0.0) { // 0 channels
			return 3;
		} else if(channels > 0.0 && channels < 1.0) { // maybe 1 channel
			if(s.nextDouble() > channels) // larger number in channels = higher probability 2 (not 3)
				return 3;
			else
				return 2;
		} else if(channels == 1.0) { // 1 channel
			return 2;
		} else if(channels > 1.0 && channels < 2.0) { // 1 and maybe 2 channels
			if(s.nextDouble() > (channels - 1.0)) // larger number in channels = higher probability 1 (not 2)
				return 2;
			else
				return 1;
		} else if(channels == 2.0) { // 2 channel
			return 1;
		} else if(channels > 2.0 && channels < 3.0) { // 2 and maybe 3 channels
			if(s.nextDouble() > (channels - 2.0)) // larger number in channels = higher probability 0 (not 1)
				return 1;
			else
				return 0;
		}
		// 3 channels: no exclusions
		return 0;
	}
	
	private ColorNode _buildToplevel() {
		return (ColorNode)_buildNode(0);
	}
	

	private OperationNode _buildNode(int level) {
		OperationNode op = _selectAndCreateOp(level);
		for(int i = 0; i < op.getChildCount(); i++) {
			OperationNode child = _buildNode(level + 1);
			op.setChild(i, child);
		}
		return op;
	}
	
	
	private Operation _selectOp(int level) {
		// select list of ops to select from
		Operation[] ops;
		double total;
		double rand, pos;

		if(level == 0) {
			ops = TOPS;
			total = TOPS_total;
		} else if(level <= this.params.getMinDepth()) {
			ops = NODES;
			total = NODES_total;
		} else if(level >= this.params.getMaxDepth()) {
			ops = LEAFS;
			total = LEAFS_total;
		} else {
			ops = NODES_AND_LEAFS;
			total = NODES_AND_LEAFS_total;
		}
		
		// get a random number in [0, total]
		rand = this.params.getSeed().nextDouble() * total;
		
		// walk the table until we find our op
		pos = 0.0;
		for(Operation item : ops) {
			pos += this.params.getOperationRatio(item);
			if(pos > rand) {
				return item;
			}
		}
		
		throw new RuntimeException("Overflowed our OperationytecodeTable somehow at level: " + Integer.toString(level));
	}
	
	
	private OperationNode _selectAndCreateOp(int level) {
		return OperationFactory.createNode(_selectOp(level), this.params.getSeed());
	}
	
	
	/**
	 * Write a string representation of this tree to a given filename. 
	 * @throws IOException
	 */
	public void show(String filename) throws IOException {
		BufferedWriter fp = new BufferedWriter(new FileWriter(filename));
		this.tree.show(fp, 0);
		fp.close();
	}
	
	
	/**
	 * Provide parameters for the construction of images from this tree.  These
	 * parameters are independent of the tree itself and it is frequently useful
	 * to give several to a single tree over its lifetime, e.g. if we are generating
	 * multiple image resolutions from one tree.  This method must be called with
	 * an ImageParameters before calling generateCurrentFrame.
	 */
	public void setGenerationParameters(ImageParameters ip) {
		this.ip = ip;
	}
	
	void setTime(double t, double dt) {
		for(Value v : this.values) {
			v.setTime(t, dt);
		}
	}
	
	
	/**
	 * Compute an image from this tree.  Before calling this function,
	 * setGenerationParameters must be called with an ImageParameters object.
	 */
	public byte[] generateCurrentFrame() {
		if(this.ip == null)
			throw new IllegalArgumentException("setGenerationParameters must be called to set ImageParameters");
		return this.tree.compute(this.ip, true);
	}
}
