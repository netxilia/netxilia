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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netxilia.api.model.SheetFullName;

/**
 * Represents a reference to a cell as specified by Excel and Excel-like softwares.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public final class CellReference {
	// first group (for the sheet name is non-capturing)
	public static final Pattern REFERENCE_PATTERN = Pattern.compile("(?:'?(" + SheetFullName.NAME_PATTERN.pattern()
			+ ")'?!)?([$]?)([A-Za-z]*)([$]?)([0-9]*)");

	public static final int MAX_COLUMN_INDEX = 1000;
	public static final int MIN_COLUMN_INDEX = 0;
	public static final int MAX_ROW_INDEX = Integer.MAX_VALUE;
	/**
	 * this is a special reference to the row just after the last one in a spreadsheet. it is used to add a new row.
	 */
	public static final int LAST_ROW_INDEX = MAX_ROW_INDEX - 1;

	public static final int MIN_ROW_INDEX = 0;
	private final String sheetName; // if null, it's a reference to a local cell
	// (in
	// the same sheet)
	private final int rowIndex;
	private final int columnIndex;
	private final boolean absoluteRow;
	private final boolean absoluteColumn;

	public CellReference(int row, int column) {
		this(null, row, column);
	}

	/**
	 * @param sheet
	 *            the target sheet. Null, means the same sheet as the source
	 * @param row
	 *            , zero-based
	 * @param column
	 *            , zero-based
	 */
	public CellReference(String sheetName, int row, int column) {
		this(sheetName, row, column, false, false);
	}

	/**
	 * @param sheet
	 *            the target sheet. Null, means the same sheet as the source
	 * @param reference
	 *            a string like A12, $BA2, CX$33, $D$4
	 */
	public CellReference(String reference) {
		this(null, reference);
	}

	public CellReference(String forceSheetName, String reference) {
		// has to put everything in the constructor to be able to have final
		// fields
		Matcher m = REFERENCE_PATTERN.matcher(reference);
		if (!m.matches()) {
			throw new IllegalArgumentException("Cannot parse reference: " + reference);
		}
		String newSheetName = m.group(1);

		boolean newAbsoluteColumn = false;
		if (m.group(2).length() > 0) {
			newAbsoluteColumn = true;
		}

		boolean newAbsoluteRow = false;
		if (m.group(4).length() > 0) {
			newAbsoluteRow = true;
		}
		int newRow = 0;
		try {
			if (m.group(5).isEmpty()) {
				newRow = MAX_ROW_INDEX;
			} else {
				newRow = Integer.parseInt(m.group(5)) - 1;
			}
		} catch (NumberFormatException nfe) {
			// we should not get here, as the regexp is matched
			throw new IllegalArgumentException("Cannot parse numeric part: " + m.group(5));
		}
		checkRange("Row", newRow, MIN_ROW_INDEX, MAX_ROW_INDEX);

		// if everything went fine, commit values
		absoluteColumn = newAbsoluteColumn;
		columnIndex = columnIndex(m.group(3));
		absoluteRow = newAbsoluteRow;
		rowIndex = newRow;
		sheetName = forceSheetName != null && !forceSheetName.isEmpty() ? forceSheetName : newSheetName;
	}

	public CellReference(String sheetName, int row, int column, boolean absoluteRow, boolean absoluteColumn) {
		this.sheetName = sheetName;
		checkRange("Column", column, MIN_COLUMN_INDEX, MAX_COLUMN_INDEX);
		checkRange("Row", row, MIN_ROW_INDEX, MAX_ROW_INDEX);
		this.rowIndex = row;
		this.columnIndex = column;
		this.absoluteRow = absoluteRow;
		this.absoluteColumn = absoluteColumn;
	}

	// getters
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
		return columnIndex == MAX_COLUMN_INDEX;
	}

	/**
	 * Infinite rows are for full column ranges
	 * 
	 * @return true if the rowIndex value has exactly the maximum value
	 */
	public boolean isInfiniteRow() {
		return rowIndex == MAX_ROW_INDEX;
	}

	public CellReference withRow(int row) {
		return new CellReference(sheetName, row, columnIndex, absoluteRow, absoluteColumn);
	}

	public CellReference withColumn(int col) {
		return new CellReference(sheetName, rowIndex, col, absoluteRow, absoluteColumn);
	}

	public CellReference withSheetName(String newSheetName) {
		return new CellReference(newSheetName, rowIndex, columnIndex, absoluteRow, absoluteColumn);
	}

	public String formatAsString() {
		return formatAsString(true);
	}

	public String formatAsString(boolean withSheetName) {
		StringBuilder sb = new StringBuilder();
		if (withSheetName && sheetName != null) {
			sb.append(SheetFullName.toStringForFormula(sheetName)).append("!");
		}

		if (!isInfiniteColumn()) {
			if (absoluteColumn) {
				sb.append("$");
			}

			columnLabel(sb, columnIndex);
		}

		if (!isInfiniteRow()) {
			if (absoluteRow) {
				sb.append("$");
			}
			sb.append(rowIndex + 1);
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return formatAsString();
	}

	public static String columnLabel(int c) {
		StringBuilder sb = new StringBuilder();
		columnLabel(sb, c);
		return sb.toString();
	}

	public static void columnLabel(StringBuilder sb, int c) {
		int p = sb.length();
		while (c >= 26) {
			sb.insert(p, (char) ('A' + ((c % 26))));
			c /= 26;
			c--;
		}
		sb.insert(p, (char) ('A' + c));
	}

	private static void checkRange(String dimension, int value, int min, int max) {
		if (value < min) {
			throw new IllegalArgumentException(dimension + " too small: " + value + " min allowed: " + min);
		}
		if (value > max) {
			throw new IllegalArgumentException(dimension + " too big: " + value + " max allowed: " + max);
		}
	}

	public static int columnIndex(String columnSpec) {
		if (columnSpec.isEmpty()) {
			return MAX_COLUMN_INDEX;
		}
		int columnIndex = 0;
		String colStr = columnSpec.toUpperCase();
		for (int i = 0; i < colStr.length(); i++) {
			columnIndex = columnIndex * 26 + (1 + colStr.charAt(i) - 'A');
		}
		columnIndex--;
		checkRange("Column", columnIndex, MIN_COLUMN_INDEX, MAX_COLUMN_INDEX);
		return columnIndex;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		CellReference other = (CellReference) obj;
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

}
