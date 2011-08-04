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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import vash.operation.ColorNode;
import vash.operation.Operation;
import vash.operation.OperationFactory;
import vash.operation.OperationNode;
import vash.value.Value;



/**
 * A tree of operations which represent the computation of an image.
 */
public class Tree {
	/*
	 * ChannelParameters overlays TreeParameters, masking or augmenting values as needed on a
	 * per channel basis.
	 */
	class ChannelParameters {
		// operations which are excluded from inclusion in this channel
		HashSet<Operation> exclude;

		ChannelParameters() {
			exclude = new HashSet<Operation>();
		}
		
		void addExclude(Operation op) {
			exclude.add(op);
		}

		boolean isExcluded(Operation op) {
			return exclude.contains(op);
		}
	}
	
	// we use these at class construction to precompute values
	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
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

	// tree parameters
	private final TreeParameters params;
	private final ColorNode tree;
	private final ArrayList<Value> values = new ArrayList<Value>();
	private ImageParameters ip;
	
	
	/**
	 * Construct a tree with the given tree parameters.
	 * @param params TreeParameters that will define this tree
	 */
	public Tree(TreeParameters params) {
		this.params = params;

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
	
	// drive channel exclusion selection for each operation on each channel
	private void _setupChannelExclusions(ChannelParameters[] chans) {
		Seed s = params.getSeed();
		for(Operation op : NODES_AND_LEAFS) {
			double n_channels = params.getOperationChannels(op);
			int n_exclude = __getChannelExclusionCount(s, n_channels);
			boolean[] exclude = __buildChannelMask(s, n_exclude);
			for(int i = 0; i < 3; i++) {
				if(exclude[i]) {
					chans[i].addExclude(op);
				}
			}
		}
	}
	
	private ColorNode _buildToplevel() {
		// only allow one plane to have the singleton class
		if(this.params.getSeed().getAlgorithm().equals("1.1")) {
			// toplevel (color) node
			ColorNode rgb = (ColorNode)_selectAndCreateOp(0, new ChannelParameters());
			
			// channel parameters
			ChannelParameters[] chan = {
					new ChannelParameters(),
					new ChannelParameters(),
					new ChannelParameters()
			};
			_setupChannelExclusions(chan);
			
			// build each channel and attach to color node
			OperationNode r = _buildNode(1, chan[0]);
			OperationNode g = _buildNode(1, chan[1]);
			OperationNode b = _buildNode(1, chan[2]);
			rgb.setChild(0, r);
			rgb.setChild(1, g);
			rgb.setChild(2, b);

			return rgb;
		} else {
			return (ColorNode)_buildNode(0, new ChannelParameters());
		}
	}

	
	private OperationNode _buildNode(int level, ChannelParameters chan) {
		OperationNode op = _selectAndCreateOp(level, chan);
		for(int i = 0; i < op.getChildCount(); i++) {
			OperationNode child = _buildNode(level + 1, chan);
			op.setChild(i, child);
		}
		return op;
	}
	
	
	private Operation _selectOp(int level, ChannelParameters chan) {
		// select list of ops to select from
		Operation[] ops;
		double rand, pos;

		if(level == 0) {
			ops = TOPS;
		} else if(level <= this.params.getMinDepth()) {
			ops = NODES;
		} else if(level >= this.params.getMaxDepth()) {
			ops = LEAFS;
		} else {
			ops = NODES_AND_LEAFS;
		}

		// compute the total for our ops
		double total = 0.0;
		for(Operation op : ops) {
			if(chan.isExcluded(op)) { continue; }
			total += this.params.getOperationRatio(op);
		}
		
		// get a random number in [0, total]
		rand = this.params.getSeed().nextDouble() * total;
		
		// walk the table until we find our op
		pos = 0.0;
		for(Operation op : ops) {
			if(chan.isExcluded(op)) { continue; }
			pos += this.params.getOperationRatio(op);
			if(pos > rand) {
				return op;
			}
		}
		
		throw new RuntimeException("Overflowed our OperationytecodeTable somehow at level: " + Integer.toString(level));
	}
	
	
	private OperationNode _selectAndCreateOp(int level, ChannelParameters chan) {
		return OperationFactory.createNode(_selectOp(level, chan), this.params.getSeed());
	}
	
	
	/**
	 * Write a string representation of this tree to a given filename. 
	 * @throws IOException
	 */
	public void show(String filename) throws IOException {
		OutputStream fp;
		if(filename.equals("-")) {
			fp = System.out;
		} else {
			fp = new FileOutputStream(filename);
		}
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
