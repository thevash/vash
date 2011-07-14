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
package vash;

/**
 * Specifies how keyframes will be added during tree generation.  Streaming 
 * and wrapping animations have very different requirements during tree 
 * generation.
 * 
 * NOTE: animation is still a work in progress and is not yet available as an 
 * output option.
 */
public enum AnimationMode {
	WRAP,
	STREAM;
	
	public static AnimationMode parseAnimationMode(String s) {
		if(s.equalsIgnoreCase("WRAP")) {
			return WRAP;
		} else if(s.equalsIgnoreCase("STREAM")) {
			return STREAM;
		}
		throw new IllegalArgumentException(s + "is not a valid animation mode.");
	}
}
