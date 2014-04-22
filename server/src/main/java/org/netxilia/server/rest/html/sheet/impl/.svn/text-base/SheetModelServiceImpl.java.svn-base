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
package org.netxilia.server.rest.html.sheet.impl;

import java.security.AccessControlException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.SpanTable;
import org.netxilia.api.operation.ISheetOperations;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.storage.IJsonSerializer;
import org.netxilia.api.user.IAclService;
import org.netxilia.api.user.Permission;
import org.netxilia.api.utils.CollectionUtils;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.RichValue;
import org.netxilia.server.rest.html.sheet.CellModel;
import org.netxilia.server.rest.html.sheet.ColumnModel;
import org.netxilia.server.rest.html.sheet.ISheetModelService;
import org.netxilia.server.rest.html.sheet.RowModel;
import org.netxilia.server.rest.html.sheet.SheetModel;
import org.netxilia.server.util.ILazyElementResolver;
import org.netxilia.server.util.LazyArrayList;
import org.springframework.beans.factory.annotation.Autowired;

public class SheetModelServiceImpl implements ISheetModelService {
	// private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SheetModelServiceImpl.class);

	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private IStyleService styleService;

	@Autowired
	private IJsonSerializer jsonSerializer;

	@Autowired
	private ISheetOperations sheetOperations;

	@Autowired
	private IAclService aclService;
	// @Autowired
	// private IStyleService styleService;

	private int mainExtraRows = 50;
	private int mainExtraCols = 20;

	private int privateExtraRows = 15;
	private int privateExtraCols = 6;

	private int summaryExtraRows = 15;
	private int summaryExtraCols = 20;

	private int defaultColumnWidth = 80;

	private int defaultRowHeight = 20;

	private int pageSize = 120;

	private String emptyCellContent = "<td></td>";
	private String skipCellContent = "";

	private int getExtraRows(SheetType type) {
		switch (type) {
		case normal:
			return mainExtraRows;
		case summary:
			return summaryExtraRows;
		case user:
			return privateExtraRows;
		}
		return 0;
	}

	private int getExtraCols(SheetType type) {
		switch (type) {
		case normal:
			return mainExtraCols;
		case summary:
			return summaryExtraCols;
		case user:
			return privateExtraCols;
		}
		return 0;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getMainExtraRows() {
		return mainExtraRows;
	}

	public void setMainExtraRows(int mainExtraRows) {
		this.mainExtraRows = mainExtraRows;
	}

	public int getMainExtraCols() {
		return mainExtraCols;
	}

	public void setMainExtraCols(int mainExtraCols) {
		this.mainExtraCols = mainExtraCols;
	}

	public int getPrivateExtraRows() {
		return privateExtraRows;
	}

	public void setPrivateExtraRows(int privateExtraRows) {
		this.privateExtraRows = privateExtraRows;
	}

	public int getPrivateExtraCols() {
		return privateExtraCols;
	}

	public void setPrivateExtraCols(int privateExtraCols) {
		this.privateExtraCols = privateExtraCols;
	}

	public int getSummaryExtraRows() {
		return summaryExtraRows;
	}

	public void setSummaryExtraRows(int summaryExtraRows) {
		this.summaryExtraRows = summaryExtraRows;
	}

	public int getSummaryExtraCols() {
		return summaryExtraCols;
	}

	public void setSummaryExtraCols(int summaryExtraCols) {
		this.summaryExtraCols = summaryExtraCols;
	}

	public int getDefaultColumnWidth() {
		return defaultColumnWidth;
	}

	public void setDefaultColumnWidth(int defaultColumnWidth) {
		this.defaultColumnWidth = defaultColumnWidth;
	}

	public int getDefaultRowHeight() {
		return defaultRowHeight;
	}

	public void setDefaultRowHeight(int defaultRowHeight) {
		this.defaultRowHeight = defaultRowHeight;
	}

	public IJsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	public void setJsonSerializer(IJsonSerializer jsonSerializer) {
		this.jsonSerializer = jsonSerializer;
	}

	private Alias findAliasByReference(SheetData sheet, AreaReference ref) {
		for (Map.Entry<Alias, AreaReference> entry : sheet.getAliases().entrySet()) {
			if (ref.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override
	public SheetModel buildModel(SheetFullName sheet, int start, Formula filter, boolean createSheetIfMissing)
			throws StorageException, NetxiliaBusinessException {
		return buildModel(sheet, false, 0, start, filter, createSheetIfMissing);
	}

	@Override
	public SheetModel buildOverviewModel(SheetFullName sheet) throws StorageException, NetxiliaBusinessException {
		return buildModel(sheet, true, 0, 0, null, false);
	}

	@Override
	public SheetModel buildSummaryModel(SheetFullName summarySheetName, int mainSheetColumnCount,
			boolean createSheetIfMissing) throws StorageException, NetxiliaBusinessException {
		return buildModel(summarySheetName, true, mainSheetColumnCount, 0, null, createSheetIfMissing);
	}

	private SheetModel buildModel(SheetFullName sheetName, boolean overviewMode, int mainSheetColumnCount,
			int startRow, Formula filter, boolean createSheetIfMissing) throws StorageException,
			NetxiliaBusinessException {
		ISheet sheet = null;

		try {
			SheetType type = sheetName.getType();

			try {
				sheet = workbookProcessor.getWorkbook(sheetName.getWorkbookId()).getSheet(sheetName.getSheetName());
			} catch (NotFoundException e) {
				if (createSheetIfMissing) {
					try {
						sheet = workbookProcessor.getWorkbook(sheetName.getWorkbookId()).addNewSheet(
								sheetName.getSheetName(), type);
					} catch (AlreadyExistsException e1) {
						// if the sheet was created in the mean time - nothing to do
					}
				} else {
					throw new NotFoundException("Cannot find sheet:" + sheetName);
				}
			}

			boolean readOnly = true;
			try {
				aclService.checkPermission(sheet.getFullName(), Permission.write);
				readOnly = false;
			} catch (AccessControlException e) {
				// readonly
			}

			SheetDimensions dim = sheet.getDimensions().getNonBlocking();
			int totalColumns = dim.getColumnCount() + getExtraCols(type);

			// synchronize the number of columns from summary and main sheet
			if (type == SheetType.summary) {
				totalColumns = mainSheetColumnCount;
			}
			SheetData sheetData = sheet.receiveSheet().getNonBlocking();
			List<ColumnModel> columns = new LazyArrayList<ColumnModel>(totalColumns, new LazyColumnResolver());
			List<ColumnData> columnData = sheet.receiveColumns(Range.ALL).getNonBlocking();

			SpanTable spans = new SpanTable(sheetData.getSpans());

			for (int c = 0; c < columnData.size(); ++c) {
				AreaReference fullColumnRef = new AreaReference(new CellReference(null, CellReference.MAX_ROW_INDEX, c));

				Alias alias = findAliasByReference(sheetData, fullColumnRef);
				columns.add(new ColumnModel(columnData.get(c), alias, defaultColumnWidth));
			}

			List<RowModel> rows = Collections.emptyList();
			// int totalRows = 0;

			int firstRow = startRow;
			int lastRow = Math.min(startRow + dim.getRowCount() + getExtraRows(type), pageSize + startRow);
			int rowCount = lastRow - firstRow;

			List<Integer> rowIds = null;
			if (filter != null) {
				rowIds = sheetOperations.filter(sheet, filter).getNonBlocking();
				if (rowIds.size() > 0) {
					firstRow = rowIds.get(0);
					lastRow = rowIds.get(rowIds.size() - 1) + 1;
				} else {
					lastRow = firstRow;
				}
				rowCount = rowIds.size();
			}
			rows = new LazyArrayList<RowModel>(rowCount, new LazyRowResolver(firstRow, totalColumns));
			List<RowData> rowData = sheet.receiveRows(Range.range(firstRow, lastRow)).getNonBlocking();

			Matrix<CellData> cellData = sheet.receiveCells(
					new AreaReference(new CellReference(sheetName.getSheetName(), firstRow, 0), new CellReference(
							sheetName.getSheetName(), lastRow, totalColumns))).getNonBlocking();

			for (Integer rowIndex : rowIds != null ? getRowIdIterable(rowIds) : getRowIdIterable(firstRow, lastRow)) {
				int relativeRowIndex = rowIndex - firstRow;
				if (relativeRowIndex >= rowData.size()) {
					continue;
				}
				RowData row = rowData.get(relativeRowIndex);
				if (row == null) {
					continue;
				}

				List<CellModel> cells = new LazyArrayList<CellModel>(totalColumns, new LazyCellResolver());

				for (CellData cell : cellData.getRow(relativeRowIndex)) {
					CellModel cellModel;

					int colSpan = cell != null ? spans.getColSpan(cell.getReference()) : 1;
					int rowSpan = cell != null ? spans.getRowSpan(cell.getReference()) : 1;

					if (cell == null) {
						cellModel = new CellModel(emptyCellContent);
					} else if (colSpan < 0 || rowSpan < 0) {
						// the cell is part of a merged area
						cellModel = new CellModel(skipCellContent);
					} else {
						ColumnData columnDataForCell = cell.getReference().getColumnIndex() < columnData.size() ? columnData
								.get(cell.getReference().getColumnIndex()) : null;
						RichValue formattedValue = styleService.formatCell(sheetName.getWorkbookId(), cell, row,
								columnDataForCell);

						StringBuilder cellContent = new StringBuilder();
						cellContent.append("<td");
						if (cell.getFormula() != null) {
							cellContent.append(" title='").append(cell.getFormula()).append("'");
						} else if (cell.getValue() != null) {
							// XXX assumes Strings are not formatted which may be wrong. other types may also be
							// skipped (i.e. BINARY)
							if (cell.getValue().getValueType() != GenericValueType.STRING) {
								cellContent.append(" title='").append(cell.getValue().getStringValue()).append("'");
							}
						}

						if (formattedValue.getStyles() != null && formattedValue.getStyles().toString().length() > 0) {
							cellContent.append(" class='").append(formattedValue.getStyles()).append("'");
						}

						if (colSpan > 1) {
							cellContent.append(" colspan='").append(colSpan).append("'");
						}
						if (rowSpan > 1) {
							cellContent.append(" rowspan='").append(rowSpan).append("'");
						}

						cellContent.append(">");
						if (colSpan > 1) {
							cellContent.append("<div class='merge'>");
							cellContent.append(formattedValue.getDisplay());
							cellContent.append("</div>");
						} else {
							cellContent.append(formattedValue.getDisplay());
						}
						cellContent.append("</td>");
						cellModel = new CellModel(cellContent.toString());
					}
					cells.add(cellModel);
				}
				rows.add(new RowModel(row, defaultRowHeight, cells));
			}

			SheetModel model = new SheetModel(jsonSerializer, overviewMode, sheetName, rows, columns,
					sheetData.getAliases(), sheetData.getCharts(), sheetData.getSpans(), rowCount, pageSize, readOnly);
			return model;
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	/**
	 * Columns resolvers
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 */
	private class LazyColumnResolver implements ILazyElementResolver<ColumnModel> {
		private LazyColumnModel columnModel = new LazyColumnModel();

		public ColumnModel get(int index) {
			columnModel.setIndex(index);
			return columnModel;
		}
	}

	private class LazyColumnModel extends ColumnModel {
		private int index;

		public LazyColumnModel() {
			super(null, null, defaultColumnWidth);
		}

		@Override
		public int getWidth() {
			return defaultColumnWidth;
		}

		@Override
		public String getLabel() {
			return CellReference.columnLabel(index);
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	/**
	 * Row resolver. For inexistent rows will return its index shifted with the start row and empty cells
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 */
	private class LazyRowResolver implements ILazyElementResolver<RowModel> {
		private LazyRowModel rowModel;
		private int startRow;

		public LazyRowResolver(int startRow, int columnCount) {
			rowModel = new LazyRowModel(columnCount);
			this.startRow = startRow;
		}

		public RowModel get(int index) {
			rowModel.setIndex(startRow + index);
			return rowModel;
		}
	}

	private class LazyRowModel extends RowModel {
		private int index;

		public LazyRowModel(int columnCount) {
			super(null, defaultRowHeight, new LazyArrayList<CellModel>(columnCount, new LazyCellResolver()));
		}

		@Override
		public int getHeight() {
			return defaultRowHeight;
		}

		@Override
		public int getIndex() {

			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	/**
	 * CellResolver
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 */
	private class LazyCellResolver implements ILazyElementResolver<CellModel> {
		private CellModel cellModel = new CellModel(emptyCellContent);

		public CellModel get(int index) {
			return cellModel;
		}
	}

	private Iterable<Integer> getRowIdIterable(List<Integer> rowIds) {
		return rowIds;
	}

	private Iterable<Integer> getRowIdIterable(final int firstRow, final int lastRow) {

		return CollectionUtils.iterable(new Iterator<Integer>() {
			private int i = firstRow;

			@Override
			public boolean hasNext() {
				return i < lastRow;
			}

			@Override
			public Integer next() {
				if (i >= lastRow) {
					throw new IllegalStateException();
				}
				return i++;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		});
	}

}
