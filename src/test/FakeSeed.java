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
import java.util.Vector;

import vash.Seed;

/**
 * Allow the user to feed specific values to a function taking a seed.
 */
public class FakeSeed extends Seed {
	Vector<Double> data;
	Vector<Integer> idata;
	
	public FakeSeed() throws NoSuchAlgorithmException, IOException {
		super("1.1", null, new ByteArrayInputStream("Foo".getBytes()));
		data = new Vector<Double>();
		idata = new Vector<Integer>();
	}

	public void addValue(double d) {
		data.add(d);
	}
	public void addValue(int d) {
		idata.add(d);
	}
	
	@Override
	public double nextDouble() {
		return data.remove(0);
	}
	@Override
	public int nextInt(int n) {
		return idata.remove(0);
	}
}
