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
package vash.value;

import vash.Seed;


/**
 * A value that represents a 2-Tuple X, Y coordinate.
 */
public class Position implements Value {
	/**
	 * This internal class is here so that we can place our keyframes in a single list.
	 */
	class Coord {
		public double x, y;
		public Coord(double x, double y) {
			super();
			this.x = x;
			this.y = y;
		}
	}

	/**
	 * The current position.
	 */
	private final Coord v = new Coord(0, 0);
	
	//TODO: move animation framework to version 2; leaving this here so we 
	//	don't drop it later.
	//private final Vector<KeyFrame<Coord>> keys = new Vector<KeyFrame<Coord>>();
	
	/**
	 * Default constructor creates at origin.
	 */
	public Position() {
		this(0, 0);
	}
	
	/**
	 * Construct from the seed with random values.
	 * @param s
	 */
	public Position(Seed s) {
		this(	s.nextDouble() * 2.0 - 1.0,
				s.nextDouble() * 2.0 - 1.0);
	}
	
	/**
	 * Copy constructor.
	 * @param p
	 */
	public Position(Position p) {
		this(p.v.x, p.v.y);
	}
	
	/**
	 * Construct from given position. 
	 * @param x
	 * @param y
	 */
	public Position(double x, double y) {
		super();
		v.x = x;
		v.y = y;
	}

	@Override
	public Position clone() {
		return new Position(v.x, v.y);
	}
	

	public double getX() {
		return v.x;
	}
	public void setX(double x) {
		v.x = x;
	}

	public double getY() {
		return v.y;
	}
	public void setY(double y) {
		v.y = y;
	}
	
	public boolean hasBounds(double x0, double y0, double x1, double y1) {
		// FIXME: give the position bounds
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("[%.2f,%.2f]", v.x, v.y); 
	}
	
	@Override
	public void createKeyFrame(double t, Seed s) {
		// TODO: create frames for movement
	}
	
	@Override
	public void setTime(double t, double dt) {
		// TODO: interpolate between frames
	}
}

