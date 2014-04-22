/*******************************************************************************
 * 
 * Copyright 2010 Alexandru Craciun, and individual contributors as indicated
 * by the @authors tag. 
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 ******************************************************************************/
package org.netxilia.api.reference;

/**
 * 
 * A range with the mimimum limit inclusive and maximum limit exclusive.
 * 
 * @author alexandru craciun
 * 
 */
public final class Range {
	public static final Range ALL = new Range(0, Integer.MAX_VALUE);
	/**
	 * this is a special value to indicate the last element in a unknown size collection
	 */
	public static final Range LAST = new Range(Integer.MAX_VALUE - 1, Integer.MAX_VALUE);
	private final int min;
	private final int max;

	private Range(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException("min param should be less than or equal to the max param");
		}
		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	private int bind(int n, int limit1, int limit2) {
		int min = Math.min(limit1, limit2);
		int max = Math.max(limit1, limit2);
		return Math.min(max, Math.max(min, n));
	}

	public Range bind(int limitMin, int limitMax) {
		return new Range(bind(min, limitMin, limitMax), bind(max, limitMin, limitMax));
	}

	public int count() {
		return max - min;
	}

	@Override
	public String toString() {
		return "Range [min=" + min + ", max=" + max + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + max;
		result = prime * result + min;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Range other = (Range) obj;
		if (max != other.max) {
			return false;
		}
		if (min != other.min) {
			return false;
		}
		return true;
	}

	public static Range range(int min, int max) {
		return new Range(min, max);
	}

	public static Range range(int index) {
		return range(index, index + 1);
	}

}
