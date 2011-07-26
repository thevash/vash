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

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.operation.TestOperationIntegration;
import vash.ImageParameters;
import vash.Output;
import vash.OutputParameters;
import vash.Tree;
import vash.TreeParameters;


public class TestTreeBuilder {
	private final int GALLERY_SIZE = 100;
	private final int WIDTH = 128;
	private final int HEIGHT = 128;

	public void runStaticTest(String algo, String name, int width, int height) {
		TreeParameters tp;
		Tree tree;

		// ensure we have an output directory
		File treeTgt = new File(String.format("./gallery-%s/trees/", name));
		if(!treeTgt.exists()) {
			Assert.assertTrue(treeTgt.mkdirs());
		}
		
		for(int i = 0; i < GALLERY_SIZE; i++) {
			System.out.format("At image: %03d\n", i);
			InputStream data = new ByteArrayInputStream(String.format("%03d", i).getBytes());
			tp = TreeParameters.createInstanceOrDie(algo, null, data);
			tree = new Tree(tp);

			// write out the tree we built
			try {
				tree.show(String.format("./gallery-%s/trees/%03d.txt", name, i));
			} catch(IOException e) {
				fail(e.toString());
			}

			ImageParameters ip = new ImageParameters(width, height);
			tree.setGenerationParameters(ip);
			byte[] actual = tree.generateCurrentFrame();
			TestOperationIntegration.compare(actual, width, height, String.format("./test/reference-%s/%03d.png", name, i), null);
			
			OutputParameters op = new OutputParameters(String.format("./gallery-%s/%03d.png", name, i), width, height);
			Output out = new Output(op, tree);
			try {
				out.generate();
			} catch(IOException e) {
				System.err.println(e.toString());
				Assert.assertTrue(false);
			}
		}
	}

	/////// GOLDEN OUTPUT TESTS
	@Test
	public void testBuildTree1FastNonSquare() {
		runStaticTest("1-fast", "1-fast-nonsquare", WIDTH * 2, HEIGHT);
	}

	@Test
	public void testBuildTree1Fast() {
		runStaticTest("1-fast", "1-fast", WIDTH, HEIGHT);
	}

	@Test
	public void testBuildTree1NonSquare() {
		runStaticTest("1", "1-nonsquare", WIDTH * 2, HEIGHT);
	}

	@Test
	public void testBuildTree1() {
		runStaticTest("1", "1", WIDTH, HEIGHT);
	}
	/////// END GOLDEN OUTPUT TESTS

	protected FakeSeed seed;
	
	@Before
	public void setUp() throws Exception {
		try {
			seed = new FakeSeed();
		} catch(NoSuchAlgorithmException e) {
		} catch(IOException e) {}
	}
	
	@Test
	public void testExcludeCount() {
		Assert.assertEquals(3, Tree.__getChannelExclusionCount(seed, -1.0));
		Assert.assertEquals(3, Tree.__getChannelExclusionCount(seed, 0.0));
		Assert.assertEquals(2, Tree.__getChannelExclusionCount(seed, 1.0));
		Assert.assertEquals(1, Tree.__getChannelExclusionCount(seed, 2.0));
		Assert.assertEquals(0, Tree.__getChannelExclusionCount(seed, 3.0));
		Assert.assertEquals(0, Tree.__getChannelExclusionCount(seed, 4.0));

		seed.addValue(0.75);
		Assert.assertEquals(3, Tree.__getChannelExclusionCount(seed, 0.6));
		seed.addValue(0.25);
		Assert.assertEquals(2, Tree.__getChannelExclusionCount(seed, 0.6));
		
		seed.addValue(0.75);
		Assert.assertEquals(2, Tree.__getChannelExclusionCount(seed, 1.6));
		seed.addValue(0.25);
		Assert.assertEquals(1, Tree.__getChannelExclusionCount(seed, 1.6));

		seed.addValue(0.75);
		Assert.assertEquals(1, Tree.__getChannelExclusionCount(seed, 2.6));
		seed.addValue(0.25);
		Assert.assertEquals(0, Tree.__getChannelExclusionCount(seed, 2.6));
	}
	
	void assertSameMask(boolean[] expected, boolean[] actual) {
		for(int i = 0; i < 3; i++) {
			Assert.assertEquals(expected[i], actual[i]);
		}
	}
	
	@Test
	public void testBuildChannelMask() {
		assertSameMask(new boolean[] {true, true, true}, Tree.__buildChannelMask(seed, 3));

		seed.addValue(0);
		assertSameMask(new boolean[] {false, true, true}, Tree.__buildChannelMask(seed, 2));
		seed.addValue(1);
		assertSameMask(new boolean[] {true, false, true}, Tree.__buildChannelMask(seed, 2));
		seed.addValue(2);
		assertSameMask(new boolean[] {true, true, false}, Tree.__buildChannelMask(seed, 2));

		seed.addValue(0);
		assertSameMask(new boolean[] {true, false, false}, Tree.__buildChannelMask(seed, 1));
		seed.addValue(1);
		assertSameMask(new boolean[] {false, true, false}, Tree.__buildChannelMask(seed, 1));
		seed.addValue(2);
		assertSameMask(new boolean[] {false, false, true}, Tree.__buildChannelMask(seed, 1));

		assertSameMask(new boolean[] {false, false, false}, Tree.__buildChannelMask(seed, 0));
	}

}
