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
package org.netxilia.spi.impl.storage.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.CellCreator;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetData.Property;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.MatrixBuilder;
import org.netxilia.spi.impl.storage.db.CellsMapper;
import org.netxilia.spi.impl.storage.db.ColumnsMapper;
import org.netxilia.spi.impl.storage.db.DbColumnStorageInfo;
import org.netxilia.spi.impl.storage.db.DbRowStorageInfo;
import org.netxilia.spi.impl.storage.db.DbSheetStorageInfo;
import org.netxilia.spi.impl.storage.db.DbSheetStorageServiceImpl;
import org.netxilia.spi.impl.storage.db.DbWorkbookStorageServiceImpl;
import org.netxilia.spi.impl.storage.db.RowsMapper;
import org.netxilia.spi.impl.storage.db.SheetDbSession;
import org.netxilia.spi.impl.storage.db.SheetsMapper;
import org.netxilia.spi.impl.storage.db.SparseMatrixCollection;
import org.netxilia.spi.impl.storage.db.SparseMatrixMapper;

/**
 * This is the entry for a sheet in the storage service cache.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class CachedDbSheetStorageServiceImpl extends DbSheetStorageServiceImpl {
	private final static Logger log = Logger.getLogger(CachedDbSheetStorageServiceImpl.class);

	/**
	 * this a collection of sparse matrices one for each property of a cell (other than value)
	 */
	private SparseMatrixCollection cellsStorage = null;

	private volatile Integer maxRowId = null;

	private volatile SheetData sheetData;

	private volatile DbSheetStorageInfo sheetStorage;

	private List<ColumnData> columns;
	private List<DbColumnStorageInfo> storageColumns;

	private List<RowData> rows;
	private List<DbRowStorageInfo> storageRows;

	private List<List<CellData>> cells;

	public CachedDbSheetStorageServiceImpl(DbWorkbookStorageServiceImpl workbookService, SheetFullName sheeetName,
			SheetsMapper sheetsMapper, RowsMapper rowsMapper, ColumnsMapper columnsMapper, CellsMapper cellsMapper,
			SparseMatrixMapper matrixMapper) {
		super(workbookService, sheeetName, sheetsMapper, rowsMapper, columnsMapper, cellsMapper, matrixMapper);
	}

	public void remove() {

	}

	@Override
	public void deleteSheet() throws StorageException, NotFoundException {
		super.deleteSheet();
	}

	@Override
	public SheetData loadSheet() throws StorageException, NotFoundException {
		if (sheetData == null) {
			sheetData = super.loadSheet();
		}
		return sheetData;
	}

	@Override
	public void saveSheet(SheetData sheet, Collection<Property> properties) throws StorageException, NotFoundException {
		super.saveSheet(sheet, properties);
		sheetData = sheet;
	}

	private void loadAllColumns(SheetDbSession data) throws StorageException, NotFoundException {
		if (columns == null || columns.size() != getColumnCount(data)) {
			columns = new ArrayList<ColumnData>(super.loadColumns(Range.ALL));
		}
	}

	private void loadAllStorageColumns(SheetDbSession data) throws StorageException, NotFoundException {
		if (storageColumns == null) {
			storageColumns = new ArrayList<DbColumnStorageInfo>(super.loadColumnsStorageInfo(data, Range.ALL));
		}
	}

	@Override
	public void saveColumn(ColumnData column, Collection<ColumnData.Property> properties) throws StorageException,
			NotFoundException {
		super.saveColumn(column, properties);
		if (columns != null) {
			if (column.getIndex() < columns.size()) {
				columns.set(column.getIndex(), column);
			}
		}
	}

	private void resetColumnIndexes(int startColumn) {
		// decrement next columns
		for (int i = startColumn; i < columns.size(); ++i) {
			ColumnData data = columns.get(i);
			columns.set(i, new ColumnData(i, data.getWidth(), data.getStyles()));
		}
	}

	@Override
	public void insertColumn(ColumnData column, Collection<ColumnData.Property> properties) throws NotFoundException {
		super.insertColumn(column, properties);
		if (columns != null) {
			if (column.getIndex() < columns.size()) {
				columns.add(column.getIndex(), column);
				resetColumnIndexes(column.getIndex() + 1);
			}
		}
		// increment next columns
		shiftCellsColumns(column.getIndex(), 1);

	}

	@Override
	public void deleteColumn(int column) throws StorageException, NotFoundException {
		super.deleteColumn(column);
		if (columns != null) {
			if (column < columns.size()) {
				columns.remove(column);
				resetColumnIndexes(column);
			}
		}
		// decrement next columns
		shiftCellsColumns(column, -1);

	}

	@Override
	public List<ColumnData> loadColumns(Range columnsRange) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			loadAllColumns(session);
		} finally {
			session.close();
		}
		if (Range.ALL.equals(columnsRange)) {
			return columns;
		}
		Range realRange = columnsRange.bind(0, columns.size());
		return columns.subList(realRange.getMin(), realRange.getMax());

	}

	@Override
	public int getColumnCount(SheetDbSession data) throws NotFoundException {
		loadAllStorageColumns(data);

		return storageColumns.size();
	}

	@Override
	public DbSheetStorageInfo getSheetStorage(SheetDbSession data) throws NotFoundException {
		if (sheetStorage == null) {
			sheetStorage = super.getSheetStorage(data);
		}
		return sheetStorage;

	}

	@Override
	public DbSheetStorageInfo getOrCreateSheetStorage(SheetDbSession data, SheetType type) {

		if (sheetStorage == null) {
			sheetStorage = super.getOrCreateSheetStorage(data, type);
		}
		return sheetStorage;
	}

	@Override
	public DbColumnStorageInfo insertColumnStorage(SheetDbSession data, int column) throws NotFoundException {
		DbColumnStorageInfo s = super.insertColumnStorage(data, column);
		if (storageColumns != null) {
			storageColumns.add(column, s);
		}
		return s;
	}

	@Override
	public void deleteColumnStorage(SheetDbSession data, int column) throws NotFoundException {
		super.deleteColumnStorage(data, column);
		if (storageColumns != null) {
			storageColumns.remove(column);
		}
	}

	@Override
	public List<DbColumnStorageInfo> loadColumnsStorageInfo(SheetDbSession data, Range columnsRange)
			throws NotFoundException {

		loadAllStorageColumns(data);

		if (Range.ALL.equals(columnsRange)) {
			return storageColumns;
		}
		Range realRange = columnsRange.bind(0, storageColumns.size());
		return storageColumns.subList(realRange.getMin(), realRange.getMax());
	}

	@Override
	public DbColumnStorageInfo getColumnStorage(SheetDbSession data, int column) throws NotFoundException {
		if (storageColumns != null) {
			return storageColumns.get(column);
		}

		return super.getColumnStorage(data, column);
	}

	@Override
	public List<DbColumnStorageInfo> createColumnStorage(SheetDbSession data, int maxColumn) throws NotFoundException {
		loadAllStorageColumns(data);
		if (maxColumn >= storageColumns.size()) {
			super.createColumnStorage(data, maxColumn);
			storageColumns = null;
			loadAllStorageColumns(data);
		}
		return storageColumns;
	}

	/***************************
	 * ROWS
	 * 
	 * @param session
	 * 
	 * @param b
	 * 
	 * @throws NotFoundException
	 * @throws StorageException
	 *********************/
	private List<RowData> loadCachedRows(SheetDbSession session, Range rowsRange) throws StorageException,
			NotFoundException {
		// for the moment load all
		if (rows == null) {
			rows = new ArrayList<RowData>(super.loadRows(Range.ALL));
		} else if (rows.size() != getRowCount(session)) {
			// rows added when saving cells
			rows.addAll(super.loadRows(Range.range(rows.size(), getRowCount(session))));
		}
		if (Range.ALL.equals(rowsRange)) {
			return rows;
		}
		if (rowsRange.getMin() > rows.size()) {
			throw new NotFoundException("No row index " + rowsRange + " for sheet " + getSheetName());
		}
		Range realRowsRange = rowsRange.bind(0, rows.size());
		return rows.subList(realRowsRange.getMin(), realRowsRange.getMax());
	}

	private List<DbRowStorageInfo> loadCachedStorageRows(SheetDbSession session, Range rowsRange)
			throws StorageException, NotFoundException {
		// for the moment load all
		if (storageRows == null) {
			storageRows = new ArrayList<DbRowStorageInfo>(super.loadRowsStorageInfo(session, Range.ALL));
		}
		if (Range.ALL.equals(rowsRange)) {
			return storageRows;
		}
		if (rowsRange.getMin() > storageRows.size()) {
			throw new NotFoundException("No row index " + rowsRange + " for sheet " + getSheetName());
		}
		Range realRowsRange = rowsRange.bind(0, storageRows.size());
		return storageRows.subList(realRowsRange.getMin(), realRowsRange.getMax());
	}

	@Override
	public List<RowData> loadRows(Range rowsRange) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			return loadCachedRows(session, rowsRange);
		} finally {
			session.close();
		}
	}

	@Override
	public void saveRow(RowData row, Collection<RowData.Property> properties) throws StorageException,
			NotFoundException {
		super.saveRow(row, properties);
		if (rows != null) {
			if (row.getIndex() < rows.size()) {
				rows.set(row.getIndex(), row);
			} // the rows will be completely loaded if the array size changed
		}
	}

	private void resetRowIndexes(int startRow) {
		for (int i = startRow; i < rows.size(); ++i) {
			RowData data = rows.get(i);
			rows.set(i, new RowData(i, data.getHeight(), data.getStyles()));
		}
	}

	@Override
	public void deleteRow(int row) throws StorageException, NotFoundException {
		if (rows != null) {
			rows.remove(row);
			resetRowIndexes(row);
		}
		shiftCellsRows(row, -1);
		super.deleteRow(row);
	}

	@Override
	public void insertRow(RowData row, Collection<RowData.Property> properties) throws StorageException,
			NotFoundException {
		if (rows != null) {
			rows.add(row.getIndex(), row);
			resetRowIndexes(row.getIndex() + 1);
		}
		shiftCellsRows(row.getIndex(), 1);
		super.insertRow(row, properties);
	}

	@Override
	public DbRowStorageInfo getRowStorage(SheetDbSession data, int row) throws NotFoundException {
		List<DbRowStorageInfo> cachedRows = loadCachedStorageRows(data, Range.range(row));
		return cachedRows.get(0);
	}

	@Override
	protected DbRowStorageInfo createRowStorage(SheetDbSession session, int row) throws NotFoundException {
		return super.createRowStorage(session, row);
	}

	@Override
	public DbRowStorageInfo getOrCreateRowStorage(SheetDbSession data, int row) throws NotFoundException {
		try {
			List<DbRowStorageInfo> cachedRows = loadCachedStorageRows(data, Range.range(row));
			if (cachedRows.size() > 0) {
				return cachedRows.get(0);
			}
		} catch (NotFoundException ex) {
			// rows need to be created
		}

		DbRowStorageInfo storage = createRowStorage(data, row);
		if (storageRows != null) {
			if (row > storageRows.size()) {
				storageRows.addAll(super.loadRowsStorageInfo(data, Range.range(storageRows.size(), row)));
			}
			if (row == storageRows.size()) {
				storageRows.add(storage);
			} else {
				storageRows.set(row, storage);
			}
		}
		maxRowId = storage.getId();
		return storage;

	}

	@Override
	public DbRowStorageInfo insertRowStorage(SheetDbSession data, int row) throws NotFoundException {
		DbRowStorageInfo storage = super.insertRowStorage(data, row);
		if (storageRows != null) {
			storageRows.add(row, storage);
		}
		maxRowId = storage.getId();
		return storage;

	}

	@Override
	public void deleteRowStorage(SheetDbSession data, int row) throws NotFoundException {
		super.deleteRowStorage(data, row);
		if (storageRows != null) {
			storageRows.remove(row);
		}
	}

	@Override
	public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession data, Range rowsRange) throws NotFoundException {
		return loadCachedStorageRows(data, rowsRange);
	}

	@Override
	public Integer getMaxRowId(SheetDbSession data) throws NotFoundException {
		if (maxRowId == null) {
			maxRowId = super.getMaxRowId(data);
			return maxRowId;
		}
		return maxRowId;

	}

	@Override
	public int getRowCount(SheetDbSession data) throws NotFoundException {
		// TODO: temporary
		loadCachedStorageRows(data, Range.ALL);
		return storageRows.size();
	}

	/************** CELLS ***************/

	@Override
	public SparseMatrixCollection getCellsStorage(SheetDbSession data) throws NotFoundException {
		if (cellsStorage == null) {
			cellsStorage = super.getCellsStorage(data);
		}
		return cellsStorage;
	}

	private List<List<CellData>> loadCachedCells(Range rowsRange) throws StorageException, NotFoundException {
		// TODO for the moment load all, but load only needed cells
		if (cells == null) {
			Matrix<CellData> allCells = super.loadCells(AreaReference.ALL);
			cells = new ArrayList<List<CellData>>(allCells.getRowCount());
			for (List<CellData> row : allCells.getRows()) {
				cells.add(new ArrayList<CellData>(row));
			}
		}
		if (Range.ALL.equals(rowsRange)) {
			return cells;
		}
		if (rowsRange.getMin() >= cells.size()) {
			return Collections.emptyList();
		}
		// range should already by bound to the size
		return cells.subList(rowsRange.getMin(), rowsRange.getMax());
	}

	private void resetReferences(int startRow, int startColumn) {
		for (int r = startRow; r < cells.size(); ++r) {
			List<CellData> rowCells = cells.get(r);
			for (int c = startColumn; c < rowCells.size(); ++c) {
				CellData crt = rowCells.get(c);
				rowCells.set(c, new CellData(new CellReference(getSheetName().getSheetName(), r, c), crt.getValue(),
						crt.getFormula(), crt.getStyles()));
			}
		}
	}

	private void shiftCellsColumns(int startColumn, int diff) {
		if (cells != null) {
			if (diff > 0 && startColumn >= cells.size()) {
				setCellsMinSize(cells.size(), startColumn + 1);
			}
			for (int r = 0; r < cells.size(); ++r) {
				List<CellData> rowCells = cells.get(r);
				// add / remove column
				if (diff < 0) {
					rowCells.remove(startColumn);
				} else if (startColumn < rowCells.size()) {
					rowCells.add(startColumn, emptyCell(r, startColumn));
				}
			}

			resetReferences(0, startColumn);
		}
	}

	private void shiftCellsRows(int startRow, int diff) {
		if (cells != null) {
			int crtRowCount = cells.size();
			int crtColumnCount = crtRowCount > 0 ? cells.get(0).size() : 0;

			// add / remove row
			if (diff < 0) {
				cells.remove(startRow);
			} else if (startRow < cells.size()) {
				cells.add(startRow, emptyRow(startRow, crtColumnCount));
			} else {
				setCellsMinSize(startRow + 1, crtColumnCount);
			}

			resetReferences(startRow, 0);
		}

	}

	private CellData emptyCell(int row, int column) {
		return new CellData(new CellReference(getSheetName().getSheetName(), row, column));
	}

	private List<CellData> emptyRow(int rowIndex, int columnCount) {
		List<CellData> row = new ArrayList<CellData>(columnCount);
		for (int c = 0; c < columnCount; ++c) {
			row.add(emptyCell(rowIndex, c));
		}
		return row;
	}

	private void setCellsMinSize(int minRows, int minCols) {
		int crtRowCount = cells.size();
		int crtColumnCount = crtRowCount > 0 ? cells.get(0).size() : 0;

		if (minCols > crtColumnCount) {
			// add the columns
			for (int r = 0; r < cells.size(); ++r) {
				for (int c = crtColumnCount; c < minCols; ++c) {
					cells.get(r).add(emptyCell(r, c));
				}
			}
		}
		// add the rows
		for (int r = cells.size(); r < minRows; ++r) {
			cells.add(emptyRow(r, minCols));
		}
	}

	@Override
	public void saveCells(Collection<CellDataWithProperties> saveCells) throws StorageException, NotFoundException {
		super.saveCells(saveCells);
		if (cells != null) {
			SheetDbSession session = newDbSession();
			try {
				int newRows = cells.size();
				int newCols = newRows > 0 ? cells.get(0).size() : 0;
				for (CellDataWithProperties saveCell : saveCells) {
					CellData cell = saveCell.getCellData();
					newRows = Math.max(cell.getReference().getRowIndex() + 1, newRows);
					newCols = Math.max(cell.getReference().getColumnIndex() + 1, newCols);
				}
				setCellsMinSize(newRows, newCols);
				for (CellDataWithProperties saveCell : saveCells) {
					CellData cell = saveCell.getCellData();

					List<CellData> row = cells.get(cell.getReference().getRowIndex());
					row.set(cell.getReference().getColumnIndex(), cell);
				}
			} finally {
				session.close();
			}
		}

	}

	@Override
	public Matrix<CellData> loadCells(AreaReference fullArea) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			int rowCount = getRowCount(session);
			int colCount = getColumnCount(session);

			if (fullArea.getFirstRowIndex() >= rowCount || fullArea.getFirstColumnIndex() >= colCount) {
				return new Matrix<CellData>();
			}
			AreaReference area = fullArea.bind(rowCount, colCount);
			List<List<CellData>> cachedRows = loadCachedCells(area.getRows());
			if (cachedRows.size() == 0) {
				return new Matrix<CellData>();
			}
			MatrixBuilder<CellData> builder = new MatrixBuilder<CellData>(new CellCreator(
					getSheetName().getSheetName(), area.getFirstRowIndex(), area.getFirstColumnIndex()));
			for (int r = 0; r < cachedRows.size(); ++r) {
				for (int c = area.getFirstColumnIndex(); c <= area.getLastColumnIndex() && c < cachedRows.get(r).size(); ++c) {
					builder.set(r, c - area.getFirstColumnIndex(), cachedRows.get(r).get(c));
				}
			}
			return builder.build();
		} catch (Exception ex) {
			log.error("Exception on " + super.getSheetName() + " ! " + fullArea + ": " + ex + " dims:"
					+ getSheetDimensions(), ex);
			throw new StorageException(ex);
		} finally {
			session.close();
		}

	}

}
