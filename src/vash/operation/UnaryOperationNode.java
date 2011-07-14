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

import vash.Seed;

/**
 * An operation node with exactly one child.
 */
abstract public class UnaryOperationNode extends OperationNode {
	private UnaryOperationNode() {
		super(0, 1);
	}

	/**
	 * The constructor needs 2 children.
	 * @param a
	 */
	public UnaryOperationNode(OperationNode a) {
		this();
		_children[0] = a;
	}

	/**
	 * There is also a constructor form that takes a seed value.
	 * @param s
	 */
	public UnaryOperationNode(Seed s) {
		this();
	}
}

