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

import java.util.Iterator;

import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.utils.CollectionUtils;

public final class AreaReference implements Iterable<CellReference> {
	public static final AreaReference ALL = new AreaReference(new CellReference(null, 0, 0), new CellReference(null,
			CellReference.MAX_ROW_INDEX - 1, CellReference.MAX_COLUMN_INDEX - 1));

	private final CellReference topLeft;
	private final CellReference bottomRight;

	public AreaReference(CellReference oneCell) {
		this(oneCell, oneCell);
	}

	public AreaReference(CellReference topLeft, CellReference bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		checkInfinite();
		normalizeCoords();
	}

	public AreaReference(String reference) {
		String[] refs = reference.split(":");
		if (refs.length == 1) {// in fact a cell reference (XXX: POI does not allow it. Should we?)
			topLeft = new CellReference(refs[0]);
			bottomRight = topLeft;
		} else if (refs.length == 2) {
			topLeft = new CellReference(refs[0]);
			// XXX - for 3D reference this is not OK
			bottomRight = new CellReference(topLeft.getSheetName(), refs[1]);
		} else {
			throw new IllegalArgumentException("Cannot parse reference " + reference);
		}
		checkInfinite();
	}

	public AreaReference(String sheetName, int firstRow, int firstColumn, int lastRow, int lastColumn) {
		this(new CellReference(sheetName, firstRow, firstColumn), new CellReference(sheetName, lastRow, lastColumn));
	}

	private void checkInfinite() {
		if (topLeft.isInfiniteColumn() != bottomRight.isInfiniteColumn()) {
			throw new IllegalArgumentException("Cannot mix infinite and not infinite cell references");
		}
		if (topLeft.isInfiniteRow() != bottomRight.isInfiniteRow()) {
			throw new IllegalArgumentException("Cannot mix infinite and not infinite cell references");
		}
	}

	public String getSheetName() {
		return topLeft.getSheetName();
	}

	public String formatAsString() {
		StringBuilder sb = new StringBuilder();

		sb.append(topLeft.formatAsString());
		sb.append(":");
		sb.append(bottomRight.formatAsString(false));
		return sb.toString();
	}

	// normalize reference, if necessary (make topLeft be topLeft, and
	// bottomRight be bottomRight)
	private void normalizeCoords() {
		// TODO - fix this using immutable references
		// TODO - check the sheetName is the same
		// if (topLeft.getColumnIndex() > bottomRight.getColumnIndex()) {
		// int tmp = topLeft.getColumnIndex();
		// topLeft.setColumnIndex(bottomRight.getColumnIndex());
		// bottomRight.setColumnIndex(tmp);
		// }
		// if (topLeft.getRowIndex() > bottomRight.getRowIndex()) {
		// int tmp = topLeft.getRowIndex();
		// topLeft.setRowIndex(bottomRight.getRowIndex());
		// bottomRight.setRowIndex(tmp);
		// }
		// deletedTarget = false;
	}

	// position getters & setters

	public CellReference getTopLeft() {
		return topLeft;
	}

	public CellReference getBottomRight() {
		return bottomRight;
	}

	public int getFirstColumnIndex() {
		return topLeft.isInfiniteColumn() ? 0 : topLeft.getColumnIndex();
	}

	public int getLastColumnIndex() {
		return bottomRight.getColumnIndex();
	}

	public int getFirstRowIndex() {
		return topLeft.isInfiniteRow() ? 0 : topLeft.getRowIndex();
	}

	public int getLastRowIndex() {
		return bottomRight.getRowIndex();
	}

	public boolean isOneCell() {
		return !isFullRow() && !isFullColumn() && topLeft.getRowIndex() == bottomRight.getRowIndex()
				&& topLeft.getColumnIndex() == bottomRight.getColumnIndex();
	}

	/**
	 * ex: "3:4"
	 * 
	 * @return
	 */
	public boolean isFullRow() {
		return topLeft.isInfiniteColumn();
	}

	/**
	 * ex: C:D
	 * 
	 * @return
	 */
	public boolean isFullColumn() {
		return topLeft.isInfiniteRow();
	}

	public AreaReference withSheetName(String sheetName) {
		return new AreaReference(topLeft.withSheetName(sheetName), bottomRight.withSheetName(sheetName));
	}

	public AreaReference withRelativeSheetName(String baseSheetName) {
		if (SheetFullName.ALIAS_MAIN_SHEET.equals(topLeft.getSheetName())) {
			String relativeName = SheetFullName.relativeName(baseSheetName, topLeft.getSheetName(), null);
			return new AreaReference(topLeft.withSheetName(relativeName), bottomRight.withSheetName(relativeName));
		}
		return this;
	}

	public Iterator<CellReference> iterator() {
		return iterator(CellReference.MAX_ROW_INDEX, CellReference.MAX_COLUMN_INDEX);
	}

	public int getRowCount() {
		return getLastRowIndex() - getFirstRowIndex() + 1;
	}

	public int getColumnCount() {
		return getLastColumnIndex() - getFirstColumnIndex() + 1;
	}

	public Range getColumns() {
		return Range.range(getFirstColumnIndex(), getLastColumnIndex() + 1);
	}

	public Range getRows() {
		return Range.range(getFirstRowIndex(), getLastRowIndex() + 1);
	}

	/**
	 * @param maxRow
	 *            - max row - non inclusive
	 * @param maxColumn
	 *            - max column - non inclusive
	 */
	public AreaReference bind(int maxRow, int maxColumn) {
		if (getLastColumnIndex() < maxColumn && getLastRowIndex() < maxRow) {
			return this;
		}
		return new AreaReference(getSheetName(), Math.min(getFirstRowIndex(), maxRow - 1), Math.min(
				getFirstColumnIndex(), maxColumn - 1), Math.min(getLastRowIndex(), maxRow - 1), Math.min(
				getLastColumnIndex(), maxColumn - 1));
	}

	/**
	 * Return an iterator on all the cells represented by this area reference that are present in this Sheet. That is,
	 * if there are references outside the boundaries of the sheet (e.g. areaReference.lastColumnIndex >=
	 * this.columnCount) they will not appear in the Iterator. The iterator cannot be used to change the sheet. The
	 * {@link Iterator#remove()} will throw a {@link UnsupportedOperationException}.
	 * 
	 * The cells are iterated by rows, so first will be returned the cells from the first row of the area, the those of
	 * second row, and so on.
	 * 
	 * @param areaReference
	 * @param maxRow
	 *            - max row - non inclusive
	 * @param maxColumn
	 *            - max column - non inclusive
	 * @return the area's cells iterator
	 */
	public Iterator<CellReference> iterator(int maxRow, int maxColumn) {
		return new ReferenceIterator(this, maxRow, maxColumn);
	}

	public Iterable<CellReference> iterable(int maxRow, int maxColumn) {
		return CollectionUtils.iterable(iterator(maxRow, maxColumn));
	}

	public boolean contains(AreaReference area) {
		assert area != null;
		return area.getFirstRowIndex() >= getFirstRowIndex() && //
				area.getLastRowIndex() <= getLastRowIndex() && //
				area.getFirstColumnIndex() >= getFirstColumnIndex() && //
				area.getLastColumnIndex() <= getLastColumnIndex();
	}

	@Override
	public String toString() {
		return formatAsString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bottomRight == null) ? 0 : bottomRight.hashCode());
		result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
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
		AreaReference other = (AreaReference) obj;
		if (bottomRight == null) {
			if (other.bottomRight != null) {
				return false;
			}
		} else if (!bottomRight.equals(other.bottomRight)) {
			return false;
		}
		if (topLeft == null) {
			if (other.topLeft != null) {
				return false;
			}
		} else if (!topLeft.equals(other.topLeft)) {
			return false;
		}
		return true;
	}

	public static AreaReference lastRow(int firstColumn, int lastColumn) {
		return new AreaReference(new CellReference(CellReference.LAST_ROW_INDEX, firstColumn), new CellReference(
				CellReference.LAST_ROW_INDEX, lastColumn));
	}

}
