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

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;

import vash.Vash;


public class TestRegressions {
	@Test
	public void testNonSquareLinGradVerticals() {
		String algorithm = "1";
		String data = "foo";
		int width = 64;
		int height = 32;

		BufferedImage img1 = null;
		try { img1 = Vash.createImage(algorithm, data, width, height);
		} catch(NoSuchAlgorithmException e) { Assert.assertFalse(true); }
		
		Assert.assertTrue("this should not crash", img1 != null);
	}
}
