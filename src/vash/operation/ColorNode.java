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

/**
 * A top-level nodes in the computation tree.  Where most nodes deal 
 * exclusively with Planes of data, the actual result of a computation is an 
 * image.  Thus, the toplevel node in a tree must be a ColorNode.  This class 
 * extends OperationNode with an extra "compute" method that returns a byte[], 
 * instead of a Plane.
 */
abstract public class ColorNode extends OperationNode {
	protected ColorNode() {super(0, 3);}
	abstract public byte[] compute(ImageParameters ip, boolean this_method_is_different);
}
