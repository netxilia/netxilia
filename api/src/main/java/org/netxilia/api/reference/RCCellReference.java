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
 * This is R1C1 style reference. By default the reference is absolute. If the reference is relative (or mixed), a
 * context cell is needed to transform it in an absolute reference.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public final class RCCellReference {
	private final String sheetName; // if null, it's a reference to a local cell
	private final int rowIndex;
	private final int columnIndex;
	private final boolean absoluteRow;
	private final boolean absoluteColumn;

	public RCCellReference(int rowIndex, int columnIndex) {
		this(rowIndex, columnIndex, true, true);
	}

	public RCCellReference(int rowIndex, int columnIndex, boolean absoluteRow, boolean absoluteColumn) {
		this(null, rowIndex, columnIndex, absoluteRow, absoluteColumn);
	}

	public RCCellReference(String sheetName, int rowIndex, int columnIndex, boolean absoluteRow, boolean absoluteColumn) {
		this.sheetName = sheetName;
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
		this.absoluteRow = absoluteRow;
		this.absoluteColumn = absoluteColumn;
	}

	public RCCellReference(CellReference contextCell, CellReference ref) {
		this.absoluteColumn = ref.isAbsoluteColumn();
		this.absoluteRow = ref.isAbsoluteRow();
		this.sheetName = ref.getSheetName() != null ? ref.getSheetName() : contextCell.getSheetName();
		this.rowIndex = this.absoluteRow || ref.isInfiniteRow() ? ref.getRowIndex() : ref.getRowIndex()
				- contextCell.getRowIndex();
		this.columnIndex = this.absoluteColumn || ref.isInfiniteColumn() ? ref.getColumnIndex() : ref.getColumnIndex()
				- contextCell.getColumnIndex();
	}

	public String getSheetName() {
		return sheetName;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public boolean isAbsoluteRow() {
		return absoluteRow;
	}

	public boolean isAbsoluteColumn() {
		return absoluteColumn;
	}

	/**
	 * Infinite columns are for full row ranges
	 * 
	 * @return true if the columnIndex value has exactly the maximum value
	 */
	public boolean isInfiniteColumn() {
		return columnIndex == CellReference.MAX_COLUMN_INDEX;
	}

	/**
	 * Infinite rows are for full column ranges
	 * 
	 * @return true if the rowIndex value has exactly the maximum value
	 */
	public boolean isInfiniteRow() {
		return rowIndex == CellReference.MAX_ROW_INDEX;
	}

	public CellReference getAbsoluteReference(CellReference contextCell) {
		int row = absoluteRow || isInfiniteRow() ? rowIndex : contextCell.getRowIndex() + rowIndex;
		int column = absoluteColumn || isInfiniteColumn() ? columnIndex : contextCell.getColumnIndex() + columnIndex;
		String sheetName = this.sheetName != null ? this.sheetName : contextCell.getSheetName();
		return new CellReference(sheetName, row, column, absoluteRow, absoluteColumn);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (absoluteColumn ? 1231 : 1237);
		result = prime * result + (absoluteRow ? 1231 : 1237);
		result = prime * result + columnIndex;
		result = prime * result + rowIndex;
		result = prime * result + ((sheetName == null) ? 0 : sheetName.hashCode());
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
		RCCellReference other = (RCCellReference) obj;
		if (absoluteColumn != other.absoluteColumn) {
			return false;
		}
		if (absoluteRow != other.absoluteRow) {
			return false;
		}
		if (columnIndex != other.columnIndex) {
			return false;
		}
		if (rowIndex != other.rowIndex) {
			return false;
		}
		if (sheetName == null) {
			if (other.sheetName != null) {
				return false;
			}
		} else if (!sheetName.equals(other.sheetName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return toString(true);
	}

	public String toString(boolean withSheet) {
		StringBuilder sb = new StringBuilder();
		if (withSheet && sheetName != null) {
			sb.append(sheetName).append("!");
		}
		if (!isInfiniteRow()) {
			if (absoluteRow) {
				sb.append("R").append(rowIndex);
			} else {
				sb.append("R[").append(rowIndex).append("]");
			}
		}
		if (!isInfiniteColumn()) {
			if (absoluteColumn) {
				sb.append("C").append(columnIndex);
			} else {
				sb.append("C[").append(columnIndex).append("]");
			}
		}
		return sb.toString();

	}

}
