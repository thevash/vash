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

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import vash.ImageParameters;
import vash.Plane;
import vash.value.Value;


/**
 * The abstract base class of all nodes in a Tree.  This class also does much of the
 * low-level work of allocating, managing, presenting, and otherwise dealing with 
 * children and values, leaving the actual implementers free to concentrate on their
 * algorithm instead of bookkeeping.
 */
abstract public class OperationNode {
	/**
	 * A node constructor needs to place all values in the value array so that they can be
	 * iterated by the runtime.
	 */
	protected final Value[] _values;
	
	/**
	 * A node constructor needs to create an array for its children so that the runtime
	 * knows how many it needs to create for this node.
	 */
	protected final OperationNode[] _children;

	/**
	 * We provide a default constructor that will build the value and children lists from 
	 * a given count of each.
	 * @param n_values
	 * @param n_children
	 */
	protected OperationNode(int n_values, int n_children) {
		if(n_values > 0) {
			_values = new vash.value.Value[n_values];
		} else {
			_values = null;
		}
		if(n_children > 0) {
			_children = new OperationNode[n_children];
		} else {
			_children = null;
		}
	}
	
	/**
	 * Returns the count of children allocated for this node.
	 */
	public int getChildCount() {
		if(_children == null) { return 0; }
		return _children.length;
	}

	/**
	 * Copy child references from the provided list of children into our own children.
	 * Our own list of children must be empty.
	 * @param children A list of children; must be same length as n_children passed to constructor.
	 */
	public void setChildren(OperationNode... children) {
		assert(children.length == _children.length);
		for(int i = 0; i < children.length; i++) {
			assert(_children[i] == null);
			_children[i] = children[i];
		}
	}
	
	/**
	 * Update a single child node.
	 * @param offset must be less than n_children passed to constructor
	 * @param child
	 */
	public void setChild(int offset, OperationNode child) {
		assert(offset < _children.length);
		assert(_children[offset] == null);
		_children[offset] = child;
	}

	/**
	 * Return a reference to this nodes array of values.
	 * @return an internal reference, not a copy; be careful if modifying.
	 */
	public Value[] getValues() {
		return _values;
	}

	/**
	 * Add all values we contain to the list, then call on our children.
	 * @param values mutable list, not created by us, only populated
	 */
	public void accumulateValues(ArrayList<Value> values) {
		if(_values != null) {
			for(Value v : _values) {
				values.add(v);
			}
		}
		if(_children != null) {
			for(OperationNode child : _children) {
				child.accumulateValues(values);
			}
		}
	}
	
	/**
	 * Write info about the node (and its children) to a file.
	 * @param fp
	 * @param level
	 */
	public void show(BufferedWriter fp, int level) {
		try {
			for(int i = 0; i < level; i++) fp.write("  ");
			fp.write(String.format("%s(", this.getClass().getName()));
			if(_values != null) {
				for(int i = 0; i < _values.length; i++) {
					if(i != 0) fp.write(", ");
					fp.write(_values[i].toString());
				}
			}
			fp.write(String.format(")%n"));
		} catch(IOException e) {
			return;
		}
		if(_children != null) {
			for(int i = 0; i < _children.length; i++) {
				_children[i].show(fp, level + 1);
			}
		}
	}
	
	/**
	 * A utility function used by many nodes when computing values.
	 */
	protected static float clampf(float in, float lower, float upper) {
		return Math.min(Math.max(in, lower), upper);
	}
	
	/**
	 * Create and return a completely independent copy of this node.
	 */
	@Override
	abstract public OperationNode clone();

	/**
	 * The main computation routine.
	 * @param ip
	 */
	abstract public Plane compute(ImageParameters ip);
}
