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
package org.netxilia.api.impl.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Identifies an area of cells by their indexes. Both first and last indexes are INCLUSIVE and zero based.
 * 
 * @author catac
 */
public class OrderedBlock implements Comparable<OrderedBlock> {

	private final int firstRow;
	private final int firstCol;
	private final int lastRow;
	private final int lastCol;

	public OrderedBlock(int firstRow, int firstCol, int lastRow, int lastCol) {
		this.firstRow = firstRow;
		this.firstCol = firstCol;
		this.lastRow = lastRow;
		this.lastCol = lastCol;
	}

	/** Build an OrderedBlock covering a single cell */
	public OrderedBlock(int row, int col) {
		this.firstRow = this.lastRow = row;
		this.firstCol = this.lastCol = col;
	}

	public int getFirstRow() {
		return firstRow;
	}

	public int getFirstCol() {
		return firstCol;
	}

	public int getLastRow() {
		return lastRow;
	}

	public int getLastCol() {
		return lastCol;
	}

	/**
	 * Get the neighbor cells for this OrderedBlock. These can be used afterwards to check for merging with the actual
	 * neighbor OrderedBlocks, if they exist.
	 */
	public List<OrderedBlock> neighborCells() {
		List<OrderedBlock> result = new ArrayList<OrderedBlock>(4);
		if (firstRow > 0) {
			// above, if possible
			result.add(new OrderedBlock(firstRow - 1, firstCol));
		}
		// below
		result.add(new OrderedBlock(lastRow + 1, firstCol));
		if (firstCol > 0) {
			// to the left, if possible
			result.add(new OrderedBlock(firstRow, firstCol - 1));
		}
		// to the right
		result.add(new OrderedBlock(firstRow, lastCol + 1));
		return result;
	}

	/** Check if this OrderedBlock can be merged (is next to) the given block */
	public boolean canMerge(OrderedBlock ob) {
		// try merge vertically
		if ((firstCol == ob.firstCol) && (lastCol == ob.lastCol)) {
			return (firstRow == ob.lastRow + 1) || (lastRow == ob.firstRow - 1);
		}
		// try merge horizontally
		if ((firstRow == ob.firstRow) && (lastRow == ob.lastRow)) {
			return (firstCol == ob.lastCol + 1) || (lastCol == ob.firstCol - 1);
		}
		return false;
	}

	/**
	 * Create a new OrderedBlock for the merged area. Use canMerge() before to check.
	 * 
	 * @throws IllegalArgumentException
	 *             if merge cannot be done
	 */
	public OrderedBlock merge(OrderedBlock ob) {
		// try merge vertically
		if ((firstCol == ob.firstCol) && (lastCol == ob.lastCol)) {
			if (firstRow == ob.lastRow + 1) {
				// ob is above this
				return new OrderedBlock(ob.firstRow, firstCol, lastRow, lastCol);
			} else if (lastRow == ob.firstRow - 1) {
				// ob is below this
				return new OrderedBlock(firstRow, firstCol, ob.lastRow, lastCol);
			}
		}
		// try merge horizontally
		if ((firstRow == ob.firstRow) && (lastRow == ob.lastRow)) {
			if (firstCol == ob.lastCol + 1) {
				// ob is to the left of this
				return new OrderedBlock(firstRow, ob.firstCol, lastRow, lastCol);
			} else if (lastCol == ob.firstCol - 1) {
				// ob is to the right of this
				return new OrderedBlock(firstRow, firstCol, lastRow, ob.lastCol);
			}
		}
		throw new IllegalArgumentException("Cannot merge: " + this + " and " + ob);
	}

	/**
	 * Split the current OrderedBlock around the given ob. We favor vertical blocks, i.e split first all to the left and
	 * all to the right, and then the remaining above and below the given block.
	 * 
	 * @return the list of generated OrderedBlocks.
	 * @throws IllegalArgumentException
	 *             if ob doesn't overlap the current OrderedBlock.
	 */
	public List<OrderedBlock> split(OrderedBlock ob) {
		List<OrderedBlock> result = new ArrayList<OrderedBlock>(4);
		// select all to the left
		int cut = ob.firstCol - 1;
		if ((cut >= firstCol) && (cut < lastCol)) {
			result.add(new OrderedBlock(firstRow, firstCol, lastRow, cut));
		}
		// select all to the right
		cut = ob.lastCol + 1;
		if ((cut > firstCol) && (cut <= lastCol)) {
			result.add(new OrderedBlock(firstRow, cut, lastRow, lastCol));
		}
		// select what is above ob
		cut = ob.firstRow - 1;
		if ((cut >= firstRow) && (cut < lastRow)) {
			result.add(new OrderedBlock(firstRow, ob.firstCol, cut, ob.lastCol));
		}
		// select what is below ob
		cut = ob.lastRow + 1;
		if ((cut > firstRow) && (cut <= lastRow)) {
			result.add(new OrderedBlock(cut, ob.firstCol, lastRow, ob.lastCol));
		}
		if (result.size() == 0) {
			throw new IllegalArgumentException("Cannot split " + this + " around " + ob + ". Do they overlap?");
		}
		return result;
	}

	/**
	 * Get the OrderedBlock obtained after inserting a row on the given position.
	 * 
	 * @return the shifted, enlarged or this as the given row affects the current block
	 */
	public List<OrderedBlock> withInsertedRow(int onRow, InsertMode insertMode) {
		if (onRow <= lastRow) {
			if (onRow < firstRow) {
				// block is shifted down
				return Collections.singletonList(new OrderedBlock(firstRow + 1, firstCol, lastRow + 1, lastCol));
			}
			// block is enlarged vertically
			if (insertMode == InsertMode.grow) {
				return Collections.singletonList(new OrderedBlock(firstRow, firstCol, lastRow + 1, lastCol));
			}
			// split
			List<OrderedBlock> blocks = new ArrayList<OrderedBlock>(2);
			if (onRow > firstRow) {
				blocks.add(new OrderedBlock(firstRow, firstCol, onRow - 1, lastCol));
			}
			blocks.add(new OrderedBlock(onRow + 1, firstCol, lastRow + 1, lastCol));
			return blocks;
		}
		// row inserted below, this doesn't change anything
		return Collections.singletonList(this);
	}

	/**
	 * Get the OrderedBlock obtained after inserting a column on the given position.
	 * 
	 * @param insertMode
	 * 
	 * @return the shifted, enlarged or this as the given column affects the current block
	 */
	public List<OrderedBlock> withInsertedCol(int onCol, InsertMode insertMode) {
		if (onCol <= lastCol) {
			if (onCol < firstCol) {
				// block is shifted right
				return Collections.singletonList(new OrderedBlock(firstRow, firstCol + 1, lastRow, lastCol + 1));
			}
			// block is enlarged horizontally
			if (insertMode == InsertMode.grow) {
				return Collections.singletonList(new OrderedBlock(firstRow, firstCol, lastRow, lastCol + 1));
			}
			// split
			List<OrderedBlock> blocks = new ArrayList<OrderedBlock>(2);
			if (onCol > firstCol) {
				blocks.add(new OrderedBlock(firstRow, firstCol, lastRow, onCol - 1));
			}
			blocks.add(new OrderedBlock(firstRow, onCol + 1, lastRow, lastCol + 1));
			return blocks;
		}
		// column inserted to the right of the block, this doesn't change anything
		return Collections.singletonList(this);
	}

	/**
	 * Get the OrderedBlock obtained after deleting a row on the given position. NOTE that if the block doesn't exist
	 * anymore after removing the row, the result is null.
	 * 
	 * @return the shifted, shrinked, this or null as the given row affects the current block
	 */
	public List<OrderedBlock> withDeletedRow(int onRow) {
		if (onRow <= lastRow) {
			if (onRow < firstRow) {
				// block is shifted up
				return Collections.singletonList(new OrderedBlock(firstRow - 1, firstCol, lastRow - 1, lastCol));
			}
			if (lastRow - 1 < firstRow) {
				// block doesn't exist after row's removal
				return null;
			}
			// block is shrinked vertically
			return Collections.singletonList(new OrderedBlock(firstRow, firstCol, lastRow - 1, lastCol));
		}
		// block not affected
		return Collections.singletonList(this);
	}

	/**
	 * Get the OrderedBlock obtained after deleting a column on the given position. NOTE that if the block doesn't exist
	 * anymore after removing the column, the result is null.
	 * 
	 * @return the shifted, shrinked, this or null as the given column affects the current block
	 */
	public List<OrderedBlock> withDeletedCol(int onCol) {
		if (onCol <= lastCol) {
			if (onCol < firstCol) {
				// block is shifted left
				return Collections.singletonList(new OrderedBlock(firstRow, firstCol - 1, lastRow, lastCol - 1));
			}
			if (lastCol - 1 < firstCol) {
				// block doesn't exist after column's removal
				return null;
			}
			// block is shrinked horizontally
			return Collections.singletonList(new OrderedBlock(firstRow, firstCol, lastRow, lastCol - 1));
		}
		// block not affected
		return Collections.singletonList(this);
	}

	/**
	 * @param o
	 * @return true if this block completely contains the given o block
	 */
	public boolean contains(OrderedBlock o) {
		return o.firstRow >= firstRow && o.lastRow <= lastRow && o.firstCol >= firstCol && o.lastCol <= lastCol;
	}

	/**
	 * Check if the this OrderedBlock overlaps the given OrderedBlock. If they don't overlap, this provides an ordering
	 * in terms of position. Since the OrderedBlocks are not overlapping in the SheetMedatada structure, this is useful
	 * for quickly identifying the OrderedBlock covering a given position. NOTE this is NOT consistent with equals(). 0
	 * if this includes o, or the other way around
	 */
	@Override
	public int compareTo(OrderedBlock o) {
		if (contains(o) || o.contains(this)) {
			return 0;
		}
		if (o.firstRow > firstRow) {
			return -1; // o is below
		}
		if ((o.firstRow == firstRow) && (o.firstCol > firstCol)) {
			return -1; // o is to the right
		}
		return 1; // o is above or same row to the left
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + firstCol;
		result = prime * result + firstRow;
		result = prime * result + lastCol;
		result = prime * result + lastRow;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		OrderedBlock ob = (OrderedBlock) obj;
		return (firstRow == ob.firstRow) && (firstCol == ob.firstCol) && //
				(lastRow == ob.lastRow) && (lastCol == ob.lastCol);
	}

	@Override
	public String toString() {
		return "OB[" + firstRow + "," + firstCol //
				+ " : " + lastRow + "," + lastCol + "]";
	}
}
