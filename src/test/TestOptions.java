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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestOptions {
	vash.Options opt;
	
	@Before
	public void setUp() throws Exception {
		opt = new vash.Options();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetData() {
		opt.setData("FooBar".getBytes());
		//Assert.assertEquals("FooBar", opt.getData());
		//FIXME: testSetData
	}

	@Test
	public void testSetAlgorithm() {
		opt.setAlgorithm("FooBar");
		Assert.assertEquals("FooBar", opt.getAlgorithm());
	}

	@Test
	public void testSetOutput() {
		opt.setOutput("FooBar");
		Assert.assertEquals("FooBar", opt.getOutput());
	}

	@Test
	public void testSetWidth() {
		opt.setWidth(42);
		Assert.assertEquals(42, opt.getWidth());
	}

	@Test
	public void testSetHeight() {
		opt.setHeight(42);
		Assert.assertEquals(42, opt.getHeight());
	}

	@Test
	public void testSetAnimationMode() {
		//TODO: not yet implemented
	}

	@Test
	public void testSetDuration() {
		//TODO: not yet implemented
	}

	@Test
	public void testSetPeriod() {
		//TODO: not yet implemented
	}

	@Test
	public void testSetFrameRate() {
		//TODO: not yet implemented
	}

	@Test
	public void testShowKnownAlgorithms() {
		vash.Options.showKnownAlgorithms();
	}

	@Test
	public void testOptionsStringArray() {
		String[] toTest = {
				"--algorithm", "1",
				"--data", "data",
				"--output", "output",
				"--width", "42",
				"--height", "24",
		};
		opt = new vash.Options(toTest);
		Assert.assertEquals("1", opt.getAlgorithm());
		// FIXME: test file input
		Assert.assertEquals("output", opt.getOutput());
		Assert.assertEquals(42, opt.getWidth());
		Assert.assertEquals(24, opt.getHeight());

		String[] toTestShort = {
				"-a", "1",
				"-d", "data",
				"-o", "output",
				"-w", "42",
				"-h", "24",
		};
		opt = new vash.Options(toTestShort);
		Assert.assertEquals("1", opt.getAlgorithm());
		// FIXME: test file input
		Assert.assertEquals("output", opt.getOutput());
		Assert.assertEquals(42, opt.getWidth());
		Assert.assertEquals(24, opt.getHeight());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNoSaltDataSharing() {
		String[] toTest = {
				"--algorithm", "1",
				"--output", "output",
				"--file", "-",
				"--salt-file", "-",
		};
		opt = new vash.Options(toTest);
	}
}
