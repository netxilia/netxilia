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
package org.netxilia.api.impl.utils.intervals;

/**
 * The Interval class maintains an interval with some associated data
 * 
 * 
 * 
 * @param <Type>
 *            The type of data being stored
 */
public class Interval implements Comparable<Interval> {
	private final int start;
	private final int end;
	private int min;
	private int max;

	public Interval(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	/**
	 * this is used by the algorithm
	 * 
	 * @return the maximum between the interval's end, the maximum of left node and the maximum of the right node
	 */
	int getMax() {
		return max;
	}

	void setMax(int max) {
		this.max = max;
	}

	int getMin() {
		return min;
	}

	void setMin(int min) {
		this.min = min;
	}

	/**
	 * @param time
	 * @return true if this interval contains time (invlusive)
	 */
	public boolean contains(int time) {
		return time <= end && time >= start;
	}

	/**
	 * @param other
	 * @return return true if this interval intersects other
	 */
	public boolean intersects(Interval other) {
		return other.getEnd() >= start && other.getStart() <= end;
	}

	/**
	 * Return -1 if this interval's start time is less than the other, 1 if greater In the event of a tie, -1 if this
	 * interval's end time is less than the other, 1 if greater, 0 if same
	 * 
	 * @param other
	 * @return 1 or -1
	 */
	public int compareTo(Interval other) {
		if (start < other.getStart()) {
			return -1;
		} else if (start > other.getStart()) {
			return 1;
		} else if (end < other.getEnd()) {
			return -1;
		} else if (end > other.getEnd()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "[" + this.start + " - " + this.end + "]";
	}
}
