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
package test;

import junit.framework.Assert;

import org.junit.Test;

import vash.OutputParameters;

public class TestOutputParameters {
	private OutputParameters op;
	
    
	@Test
	public void testGetFilename() {
		op = new vash.OutputParameters("foo.png", 64, 64);
		Assert.assertEquals("foo.png", op.getFilename());
		op = new vash.OutputParameters("-", 64, 64);
		Assert.assertEquals("-", op.getFilename());
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetFilenameFail() {
		op = new vash.OutputParameters("foo.bar", 64, 64);
	}
	
	@Test
	public void testGetImageType() {
		op = new vash.OutputParameters("foo.png", 64, 64);
		Assert.assertEquals("png", op.getImageType());
		op = new vash.OutputParameters("foo.bmp", 64, 64);
		Assert.assertEquals("bmp", op.getImageType());
		op = new vash.OutputParameters("foo.jpg", 64, 64);
		Assert.assertEquals("jpeg", op.getImageType());
		op = new vash.OutputParameters("-", 64, 64);
		Assert.assertEquals("png", op.getImageType());
	}

	@Test
	public void testGetWidth() {
		op = new vash.OutputParameters("foo.png", 42, 64);
		Assert.assertEquals(42, op.getWidth());
	}

	@Test
	public void testGetHeight() {
		op = new vash.OutputParameters("foo.png", 64, 42);
		Assert.assertEquals(42, op.getHeight());
	}
}
