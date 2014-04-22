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
package org.netxilia.functions;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.netxilia.api.exception.EvaluationException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.RCCellReference;
import org.netxilia.api.value.ErrorValueType;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.ReferenceValue;
import org.netxilia.spi.formula.Functions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

/*
 *
 */
@Functions
public class LookupFunctions {
	public static final int MATCH_EXACT = 0;
	/**
	 * the smallest value that is greater than or equal to value
	 */
	public static final int MATCH_SMALLEST_GREATER = -1;
	/**
	 * or largest value that is less than value
	 */
	public static final int MATCH_LARGEST_LESS = 1;

	/**
	 * Returns a cell address (reference) as text, according to the specified row and column numbers. Optionally,
	 * whether the address is interpreted as an absolute address (for example, $A$1) or as a relative address (as A1) or
	 * in a mixed form (A$1 or $A1) can be determined. The name of the sheet can also be specified. Row is the row
	 * number for the cell reference. Column is the column number for the cell reference (the number, not the letter).
	 * Abs determines the type of reference: 1 for column/row absolute; 2 for column relative, row absolute; 3 for
	 * column absolute, row relative; 4 for column/row relative. Ref is a Boolean value: true for A1 notation; FALSE for
	 * R1C1 notation. Sheet is the name of the sheet.
	 * 
	 * @param row
	 * @param column
	 * @param abs
	 * @param ref
	 * @param sheet
	 * @return
	 */
	public String ADDRESS(int row, int column, int abs, boolean ref, String sheet) {
		int r = row - 1;
		int c = column - 1;
		if (ref) {
			switch (abs) {
			case 1:
				return new CellReference(sheet, r, c, true, true).formatAsString();
			case 2:
				return new CellReference(sheet, r, c, true, false).formatAsString();
			case 3:
				return new CellReference(sheet, r, c, false, true).formatAsString();
			case 4:
				return new CellReference(sheet, r, c, false, false).formatAsString();
			default:
				throw new IllegalArgumentException("Third parameter can only be between 1,2,3,4");
			}
		}
		switch (abs) {
		case 1:
			return new RCCellReference(sheet, r, c, true, true).toString();
		case 2:
			return new RCCellReference(sheet, r, c, true, false).toString();
		case 3:
			return new RCCellReference(sheet, r, c, false, true).toString();
		case 4:
			return new RCCellReference(sheet, r, c, false, false).toString();
		default:
			throw new IllegalArgumentException("Third parameter can only be between 1,2,3,4");
		}

	}

	public IGenericValue CHOOSE(int index, Iterator<IGenericValue> values) {
		int zeroBasedIndex = index - 1;
		int i = 0;
		while (values.hasNext() && i < zeroBasedIndex) {
			values.next();
			++i;
		}
		if (values.hasNext()) {
			return values.next();
		}
		return null;
	}

	public int COLUMN(AreaReference ref) {
		return ref.getFirstColumnIndex() + 1;
	}

	/**
	 * Returns the number of columns in the given reference. Array is the reference to a cell range whose total number
	 * of columns is to be found. The argument can also be a single cell.
	 */
	public int COLUMNS(AreaReference ref) {
		return ref.getLastColumnIndex() - ref.getFirstColumnIndex() + 1;
	}

	public String HYPERLINK(String url, String cellText) {
		return "<a href='" + url + "' target='_blank'>" + cellText + "</a>";
	}

	/**
	 * Returns the content of a cell, specified by row and column number or an optional range name. Reference is a cell
	 * reference, entered either directly or by specifying a range name. If the reference consists of multiple ranges,
	 * the reference or range name must be enclosed in parentheses. Row (optional) is the row number of the reference
	 * range, for which to return a value. Column (optional) is the column number of the reference range, for which to
	 * return a value. Range (optional) is the index of the subrange if referring to a multiple range. Example:
	 * =Index(A1:D5, 2, 3) <br>
	 * 2nd usage: =Index((A1:B5, C1:D5), 3, 2, 1)
	 */
	public AreaReference INDEX(AreaReference ref, int row, int column) {
		// TODO implement range id
		return new AreaReference(new CellReference(ref.getSheetName(), ref.getFirstRowIndex() + row - 1, ref
				.getFirstColumnIndex()
				+ column - 1));
	}

	/**
	 * Returns the reference specified by a text string. This function can also be used to return the area of a
	 * corresponding string. Reference is a reference to a cell or an area (in text form) for which to return the
	 * contents.
	 */
	public AreaReference INDIRECT(String ref) {
		return new AreaReference(ref);
	}

	/**
	 * Returns the value of a cell offset by a certain number of rows and columns from a given reference point.
	 * Reference is the cell from which the function searches for the new reference. Rows is the number of cells by
	 * which the reference was corrected up (negative value) or down. Columns is the number of columns by which the
	 * reference was corrected to the left (negative value) or to the right. Height is the optional vertical height for
	 * an area that starts at the new reference position. Width is the optional horizontal width for an area that starts
	 * at the new reference position.
	 */
	public AreaReference OFFSET(AreaReference reference, int rows, int columns, Integer height, Integer width) {
		CellReference topLeft = new CellReference(reference.getSheetName(), reference.getFirstRowIndex() + rows,
				reference.getFirstColumnIndex() + columns);
		CellReference bottomRight = new CellReference(reference.getSheetName(), topLeft.getRowIndex()
				+ (width != null ? width - 1 : 0), topLeft.getColumnIndex() + (height != null ? height - 1 : 0));

		return new AreaReference(topLeft, bottomRight);
	}

	public int ROW(AreaReference ref) {
		return ref.getFirstRowIndex() + 1;
	}

	public int ROWS(AreaReference ref) {
		return ref.getLastRowIndex() - ref.getFirstRowIndex() + 1;
	}

	/**
	 * Returns the relative position of an item in an array that matches a specified value. The function returns the
	 * position of the value found in the lookup_array as a number. Search_criterion is the value which is to be
	 * searched for in the single-row or single-column array. Lookup_array is the reference searched. A lookup array can
	 * be a single row or column, or part of a single row or column. Type may take the values 1, 0, or -1. This
	 * corresponds to the same function in Microsoft Excel. <br>
	 * matchType Explanation
	 * <ul>
	 * <li>1 (default) The Match function will find the largest value that is less than or equal to value. You should be
	 * sure to sort your array in ascending order. If the match_type parameter is omitted, the Match function assumes a
	 * match_type of 1.
	 * <li>0 The Match function will find the first value that is equal to value. The array can be sorted in any order.
	 * <li>-1 The Match function will find the smallest value that is greater than or equal to value. You should be sure
	 * to sort your array in descending order.
	 * </ul>
	 * 
	 * The Match function does not distinguish between upper and lowercase when searching for a match.
	 * 
	 * If the Match function does not find a match, it will return a #N/A error.
	 * 
	 * If the match_type parameter is 0 and a text value, then you can use wildcards in the value parameter.
	 * 
	 * Wild card Explanation: '*' matches any sequence of characters, '?' matches any single character
	 */
	public int MATCH(IGenericValue searchCriterion, ReferenceValue refValue, Integer matchType) {
		boolean oneRow = refValue.getReference().getFirstRowIndex() == refValue.getReference().getLastRowIndex();
		boolean oneColumn = refValue.getReference().getFirstColumnIndex() == refValue.getReference()
				.getLastColumnIndex();

		if (!oneRow && !oneColumn) {
			throw new IllegalArgumentException("One-row or one-column area should be given");
		}

		int type = matchType != null ? matchType : MATCH_LARGEST_LESS;
		ISheet sheet = refValue.getContext().getSheet();
		CellData searchedCell = find(refValue.getReference(), sheet, searchCriterion, type);
		if (searchedCell == null) {
			// TODO - find out what to return here
			return 0;
		}
		if (oneRow) {
			return searchedCell.getReference().getColumnIndex() - refValue.getReference().getFirstColumnIndex() + 1;
		}
		// one col
		return searchedCell.getReference().getRowIndex() - refValue.getReference().getFirstRowIndex() + 1;
	}

	private AreaReference row(AreaReference ref, int row) {
		return new AreaReference(new CellReference(ref.getSheetName(), row, ref.getFirstColumnIndex()),
				new CellReference(ref.getSheetName(), row, ref.getLastColumnIndex()));
	}

	private AreaReference column(AreaReference ref, int column) {
		return new AreaReference(new CellReference(ref.getSheetName(), ref.getFirstRowIndex(), column),
				new CellReference(ref.getSheetName(), ref.getLastRowIndex(), column));
	}

	/**
	 * In Excel, the HLookup function searches for value in the top row of table_array and returns the value in the same
	 * column based on the index_number. value is the value to search for in the first row of the table_array.
	 * 
	 * table_array is two or more rows of data that is sorted in ascending order.
	 * 
	 * index_number is the row number in table_array from which the matching value must be returned. The first row is 1.
	 * 
	 * not_exact_match determines if you are looking for an exact match based on value. Enter FALSE to find an exact
	 * match. Enter TRUE to find an approximate match, which means that if an exact match if not found, then the HLookup
	 * function will look for the next largest value that is less than value.
	 */
	public IGenericValue HLOOKUP(IGenericValue searchCriterion, ReferenceValue refValue, int index,
			boolean notExactMatch) {
		ISheet sheet = refValue.getContext().getSheet();
		CellData searchedCell = find(row(refValue.getReference(), refValue.getReference().getFirstRowIndex()), sheet,
				searchCriterion, notExactMatch ? MATCH_LARGEST_LESS : MATCH_EXACT);
		if (searchedCell == null) {
			return null;
		}

		CellData valueCell;
		try {
			valueCell = sheet.receiveCell(
					new CellReference(refValue.getReference().getFirstRowIndex() + index - 1, searchedCell
							.getReference().getColumnIndex())).getNonBlocking();
		} catch (NetxiliaResourceException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		} catch (NetxiliaBusinessException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		}
		return valueCell != null ? valueCell.getValue() : null;
	}

	/**
	 * In Excel, the VLookup function searches for value in the left-most column of table_array and returns the value in
	 * the same row based on the index_number.
	 * 
	 * The syntax for the VLookup function is:
	 * 
	 * VLookup( value, table_array, index_number, not_exact_match )
	 * 
	 * value is the value to search for in the first column of the table_array.
	 * 
	 * table_array is two or more columns of data that is sorted in ascending order.
	 * 
	 * index_number is the column number in table_array from which the matching value must be returned. The first column
	 * is 1.
	 * 
	 * not_exact_match determines if you are looking for an exact match based on value. Enter FALSE to find an exact
	 * match. Enter TRUE to find an approximate match, which means that if an exact match if not found, then the VLookup
	 * function will look for the next largest value that is less than value.
	 */
	public IGenericValue VLOOKUP(IGenericValue searchCriterion, ReferenceValue refValue, int index,
			boolean notExactMatch) {
		ISheet sheet = refValue.getContext().getSheet();
		CellData searchedCell = find(column(refValue.getReference(), refValue.getReference().getFirstColumnIndex()),
				sheet, searchCriterion, notExactMatch ? MATCH_LARGEST_LESS : MATCH_EXACT);
		if (searchedCell == null) {
			return null;
		}
		CellData valueCell;
		try {
			valueCell = sheet.receiveCell(
					new CellReference(searchedCell.getReference().getRowIndex(), refValue.getReference()
							.getFirstColumnIndex()
							+ index - 1)).getNonBlocking();
		} catch (NetxiliaResourceException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		} catch (NetxiliaBusinessException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		}
		return valueCell != null ? valueCell.getValue() : null;
	}

	/**
	 * 
	 * @param area
	 * @param sheet
	 * @param search
	 * @param type
	 * @return
	 */
	private CellData find(AreaReference area, ISheet sheet, IGenericValue search, int type) {
		// TODO use a binary search for sorted area
		PreviousValuePredicate<CellReference> predicate = getPredicate(sheet, search, type);
		try {
			SheetDimensions dim = sheet.getDimensions().getNonBlocking();
			CellReference cellRef = Iterators.find(area.iterator(dim.getRowCount(), dim.getColumnCount()), predicate);
			if (predicate.getPreviousValue() != null) {
				cellRef = predicate.getPreviousValue();
			}
			return sheet.receiveCell(cellRef).getNonBlocking();
		} catch (NoSuchElementException ex) {
			return null;
		} catch (NetxiliaResourceException e) {
			return null;
		} catch (NetxiliaBusinessException e) {
			return null;
		}

		// for (ICell cell : CollectionUtils.iterable(sheet.iterator(area))) {
		// if (cell != null && predicate.apply(cell.getValue()))
		// return cell;
		// }
		// return null;
	}

	private PreviousValuePredicate<CellReference> getPredicate(ISheet sheet, IGenericValue search, int type) {
		switch (type) {
		case MATCH_EXACT:
			return new ExactMatch(sheet, search);
		case MATCH_SMALLEST_GREATER:
			return new EqualOrLess(sheet, search, -1);
		case MATCH_LARGEST_LESS:
			return new EqualOrLess(sheet, search, 1);
		}
		return null;
	}

	private static interface PreviousValuePredicate<T> extends Predicate<T> {
		public T getPreviousValue();
	}

	private static class ExactMatch implements PreviousValuePredicate<CellReference> {
		private final ISheet sheet;
		private final IGenericValue search;

		public ExactMatch(ISheet sheet, IGenericValue search) {
			this.search = search;
			this.sheet = sheet;
		}

		@Override
		public boolean apply(CellReference inputRef) {
			CellData input;
			try {
				input = sheet.receiveCell(inputRef).getNonBlocking();
			} catch (NetxiliaResourceException e) {
				throw e;
			} catch (NetxiliaBusinessException e) {
				throw new NetxiliaResourceException(e);
			}
			if (input == null || input.getValue() == null) {
				return false;
			}
			if (search.getValueType() == GenericValueType.STRING) {
				String s = input.getValue().getStringValue();
				return s.equalsIgnoreCase(search.getStringValue());
			}

			return search.equals(input.getValue());
		}

		@Override
		public CellReference getPreviousValue() {
			return null;
		}
	}

	private static class EqualOrLess implements PreviousValuePredicate<CellReference> {
		private final ISheet sheet;
		private final IGenericValue search;
		private CellReference previousCell = null;
		private final int direction;

		public EqualOrLess(ISheet sheet, IGenericValue search, int direction) {
			this.search = search;
			this.direction = direction;
			this.sheet = sheet;
		}

		@Override
		public boolean apply(CellReference inputRef) {
			CellData input;
			try {
				input = sheet.receiveCell(inputRef).getNonBlocking();
			} catch (NetxiliaResourceException e) {
				throw e;
			} catch (NetxiliaBusinessException e) {
				throw new NetxiliaResourceException(e);
			}
			if (input == null || input.getValue() == null) {
				return false;
			}
			int cmp = direction * search.compareTo(input.getValue());
			if (cmp == 0) {
				previousCell = null;
				return true;
			}
			if (cmp < 0) {
				// stop search here, but use in fact the previous cell
				return true;
			}
			previousCell = inputRef;
			return false;
		}

		@Override
		public CellReference getPreviousValue() {
			return previousCell;
		}
	}

}