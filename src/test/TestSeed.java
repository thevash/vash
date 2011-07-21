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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSeed {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSeed2() {
		vash.Seed s = null;
		try {
			s = new vash.Seed("2", null, new ByteArrayInputStream("Foo".getBytes()));
		} catch(NoSuchAlgorithmException e) {
			System.err.println(e.toString());
			System.exit(1);
		} catch(vash.InvalidSaltException e) {
			System.err.println(e.toString());
			System.exit(3);
		} catch(IOException e) {
			System.err.println(e.toString());
			System.exit(3);
		}
		s.nextDouble();
	}
}
