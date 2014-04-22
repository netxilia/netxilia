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
 * Different operations with area and cell references.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ReferenceUtils {
	public static AreaReference insertRow(AreaReference ref, int newRowIndex) {
		CellReference topLeft = insertRow(ref.getTopLeft(), newRowIndex);
		CellReference bottomRight = insertRow(ref.getBottomRight(), newRowIndex);
		if (topLeft != ref.getTopLeft() || bottomRight != ref.getBottomRight()) {
			return new AreaReference(topLeft, bottomRight);
		}
		return ref;
	}

	public static AreaReference insertColumn(AreaReference ref, int newColumnIndex) {
		CellReference topLeft = insertColumn(ref.getTopLeft(), newColumnIndex);
		CellReference bottomRight = insertColumn(ref.getBottomRight(), newColumnIndex);
		if (topLeft != ref.getTopLeft() || bottomRight != ref.getBottomRight()) {
			return new AreaReference(topLeft, bottomRight);
		}
		return ref;
	}

	public static AreaReference deleteRow(AreaReference ref, int deletedRowIndex) {
		CellReference topLeft = ref.getTopLeft();
		if (deletedRowIndex != topLeft.getRowIndex()) {
			topLeft = deleteRow(topLeft, deletedRowIndex);
		}
		CellReference bottomRight = ref.getBottomRight();
		if (deletedRowIndex == bottomRight.getRowIndex()) {
			bottomRight = bottomRight.withRow(deletedRowIndex - 1);
		} else {
			bottomRight = deleteRow(bottomRight, deletedRowIndex);
		}
		if (topLeft.getRowIndex() > bottomRight.getRowIndex()) {
			// don't normalize
			return null;
		}
		if (topLeft != ref.getTopLeft() || bottomRight != ref.getBottomRight()) {
			return new AreaReference(topLeft, bottomRight);
		}
		return ref;
	}

	public static AreaReference deleteColumn(AreaReference ref, int deletedColumnIndex) {
		CellReference topLeft = ref.getTopLeft();
		if (deletedColumnIndex != topLeft.getColumnIndex()) {
			topLeft = deleteColumn(topLeft, deletedColumnIndex);
		}
		CellReference bottomRight = ref.getBottomRight();
		if (deletedColumnIndex == bottomRight.getColumnIndex()) {
			bottomRight = bottomRight.withColumn(deletedColumnIndex - 1);
		} else {
			bottomRight = deleteColumn(bottomRight, deletedColumnIndex);
		}
		if (topLeft.getRowIndex() > bottomRight.getColumnIndex()) {
			// don't normalize
			return null;
		}
		if (topLeft != ref.getTopLeft() || bottomRight != ref.getBottomRight()) {
			return new AreaReference(topLeft, bottomRight);
		}
		return ref;
	}

	public static CellReference insertRow(CellReference ref, int newRowIndex) {
		if (newRowIndex <= ref.getRowIndex() && !ref.isInfiniteRow()) {
			return ref.withRow(ref.getRowIndex() + 1);
		}
		return ref;
	}

	public static CellReference insertColumn(CellReference ref, int newColumnIndex) {
		if (newColumnIndex <= ref.getColumnIndex() && !ref.isInfiniteColumn()) {
			return ref.withColumn(ref.getColumnIndex() + 1);
		}
		return ref;
	}

	public static CellReference deleteRow(CellReference ref, int deletedRowIndex) {
		if (deletedRowIndex == ref.getRowIndex()) {
			return null;
		} else if (deletedRowIndex < ref.getRowIndex() && !ref.isInfiniteRow()) {
			return ref.withRow(ref.getRowIndex() - 1);
		}
		return ref;
	}

	public static CellReference deleteColumn(CellReference ref, int deletedColumnIndex) {
		if (deletedColumnIndex == ref.getColumnIndex()) {
			return null;
		} else if (deletedColumnIndex < ref.getColumnIndex() && !ref.isInfiniteColumn()) {
			return ref.withColumn(ref.getColumnIndex() - 1);
		}
		return ref;
	}

	/**
	 * given an area return the cell that would correspond to the given contextCell. If the area is one cell than return
	 * it direcly. Otherwise take the column from the area and the row from the context.
	 * 
	 * @param area
	 * @param contextCell
	 * @return
	 */
	public static CellReference toOneCell(AreaReference area, CellReference contextCell) {
		if (area.isOneCell()) {
			return area.getTopLeft();
		}
		if (contextCell.getRowIndex() < area.getFirstRowIndex() || contextCell.getRowIndex() > area.getLastRowIndex()) {
			// TODO what's the correct behavior here !? no clear way between google and MS
			return area.getTopLeft();
		}
		// take the column from reference and row from context -> like aliases at google
		return new CellReference(area.getSheetName(), contextCell.getRowIndex(), area.getFirstColumnIndex());

	}
}
