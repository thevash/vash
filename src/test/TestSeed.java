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


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import vash.Seed;

public class TestSeed {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private Seed getSeedOrFail(String algo, byte[] salt, byte[] data) {
		vash.Seed s = null;
		try {
			s = new Seed(algo, null, new ByteArrayInputStream(data));
		} catch(NoSuchAlgorithmException e) {
			System.err.println(e.toString());
			Assert.assertTrue(false);
		} catch(vash.InvalidSaltException e) {
			System.err.println(e.toString());
			Assert.assertTrue(false);
		} catch(IOException e) {
			System.err.println(e.toString());
			Assert.assertTrue(false);
		}
		return s;
	}
	
	@Test
	public void testSeed1_1() {
		double[] expected = {
			0.11692921568044146,
			0.11104142497722236,
			0.4730442377064087,
			0.3989263610263144,
			0.18972689944312215,
			0.2929340032378568,
			0.1333668999326335,
			0.40169289506505934,
			0.1829785663961886,
			0.778782471904214,
			0.9927633067415756,
			0.9388042904794711,
			0.7195341252586679,
			0.9581764259719031,
			0.4729503362008035,
			0.21240207599109995
		};
		Seed s = getSeedOrFail("1.1", null, "Vash".getBytes());
		for(double e : expected) {
			double a = s.nextDouble();
			Assert.assertEquals(e, a, 0.0001);
		}
	}
	
	@Test
	public void testSeed1() {
		double[] expected = {
			0.11298426133038797,
			0.45935411918961133,
			0.44077867647073776,
			0.817059684471341,
			0.47487933691839357,
			0.03377769874954928,
			0.8317895710276257,
			0.2422862382846691,
			0.3033586161007882,
			0.7249877048176159,
			0.10388144503825236,
			0.7627241353980637,
			0.9916866420769115,
			0.4520950836578299,
			0.5418716741986549,
			0.9171026475628842,
		};
		Seed s = getSeedOrFail("1", null, "Vash".getBytes());
		for(double e : expected) {
			double a = s.nextDouble();
			Assert.assertEquals(e, a, 0.0001);
		}
	}

	@Test
	public void testSeed1Fast() {
		double[] expected = {
			0.4349793537867147,
			0.5998083775244623,
			0.028456023274416986,
			0.7201140387755028,
			0.08832659381631036,
			0.8702047739698752,
			0.2880562189263811,
			0.40259849783435653,
			0.850779080855865,
			0.776656887563028,
			0.7336161092199189,
			0.48953165467021065,
			0.6835855007740271,
			0.8962415620254659,
			0.6706493194388035,
			0.690958033861017,
		};
		Seed s = getSeedOrFail("1-fast", null, "Vash".getBytes());
		for(double e : expected) {
			double a = s.nextDouble();
			Assert.assertEquals(e, a, 0.0001);
		}
	}
}
