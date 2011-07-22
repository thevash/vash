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
	
	private OperationNode _selectAndCreateOp(int level) {
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
				return OperationFactory.createNode(item, this.params.getSeed());
			}
		}
		
		throw new RuntimeException("Overflowed our OperationytecodeTable somehow at level: " + Integer.toString(level));
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
