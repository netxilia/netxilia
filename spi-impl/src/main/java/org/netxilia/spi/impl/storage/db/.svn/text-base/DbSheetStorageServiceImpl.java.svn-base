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
package org.netxilia.spi.impl.storage.db;

import java.util.Collection;
import java.util.List;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.utils.Matrix;
import org.netxilia.spi.storage.ISheetStorageService;
import org.springframework.util.Assert;

public class DbSheetStorageServiceImpl implements ISheetStorageService, IDbSheetStorageService {
	private final SheetFullName sheetName;

	private final SheetsMapper sheetsMapper;

	private final RowsMapper rowsMapper;

	private final ColumnsMapper columnsMapper;

	private final CellsMapper cellsMapper;

	private final SparseMatrixMapper matrixMapper;

	private DbWorkbookStorageServiceImpl workbookService;

	public DbSheetStorageServiceImpl(DbWorkbookStorageServiceImpl workbookService, SheetFullName sheeetName,
			SheetsMapper sheetsMapper, RowsMapper rowsMapper, ColumnsMapper columnsMapper, CellsMapper cellsMapper,
			SparseMatrixMapper matrixMapper) {
		this.sheetName = sheeetName;
		this.sheetsMapper = sheetsMapper;
		this.rowsMapper = rowsMapper;
		this.columnsMapper = columnsMapper;
		this.cellsMapper = cellsMapper;
		this.matrixMapper = matrixMapper;
		this.workbookService = workbookService;
	}

	/**************
	 * SHEEETS
	 * 
	 * @throws NotFoundException
	 * @throws StorageException
	 *             *
	 ****************/
	@Override
	public SheetData loadSheet() throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			return sheetsMapper.load(session, sheetName);
		} finally {
			session.close();
		}
	}

	@Override
	public SheetDimensions getSheetDimensions() throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			return getSheetDimensions(session);
		} finally {
			session.close();
		}
	}

	@Override
	public SheetDimensions getSheetDimensions(SheetDbSession session) throws NotFoundException {
		return new SheetDimensions(getRowCount(session), getColumnCount(session));
	}

	@Override
	public void deleteSheet() throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			sheetsMapper.deleteSheet(session, sheetName);
		} finally {
			session.close();
		}
	}

	@Override
	public void saveSheet(SheetData sheet, Collection<SheetData.Property> properties) throws StorageException,
			NotFoundException {
		Assert.notNull(properties);
		SheetDbSession session = newDbSession();
		try {
			sheetsMapper.save(session, sheet, properties);
		} finally {
			session.close();
		}
	}

	/************** ROWS *****************/

	@Override
	public List<RowData> loadRows(Range rows) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			return rowsMapper.loadRows(session, sheetName, rows);
		} finally {
			session.close();
		}
	}

	@Override
	public void deleteRow(int row) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			rowsMapper.deleteRow(session, sheetName, row);
		} finally {
			session.close();
		}
	}

	@Override
	public void saveRow(RowData row, Collection<RowData.Property> properties) throws StorageException,
			NotFoundException {

		Assert.notNull(properties);
		SheetDbSession session = newDbSession();
		try {
			rowsMapper.saveRow(session, sheetName, row, properties);
		} finally {
			session.close();
		}
	}

	@Override
	public void insertRow(RowData row, Collection<RowData.Property> properties) throws StorageException,
			NotFoundException {

		Assert.notNull(properties);
		SheetDbSession session = newDbSession();
		try {
			rowsMapper.insertRow(session, sheetName, row, properties);
		} finally {
			session.close();
		}
	}

	/************** COLUMNS *****************/

	@Override
	public void saveColumn(ColumnData column, Collection<ColumnData.Property> properties) throws StorageException,
			NotFoundException {

		Assert.notNull(properties);
		SheetDbSession session = newDbSession();
		try {
			columnsMapper.saveColumn(session, sheetName, column, properties);
		} finally {
			session.close();
		}
	}

	@Override
	public void insertColumn(ColumnData column, Collection<ColumnData.Property> properties) throws NotFoundException {

		Assert.notNull(properties);
		SheetDbSession session = newDbSession();
		try {
			columnsMapper.insertColumn(session, sheetName, column, properties);
		} finally {
			session.close();
		}

	}

	@Override
	public void deleteColumn(int column) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			columnsMapper.deleteColumn(session, sheetName, column);
		} finally {
			session.close();
		}
	}

	@Override
	public List<ColumnData> loadColumns(Range columns) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			return columnsMapper.loadColumns(session, sheetName, columns);
		} finally {
			session.close();
		}
	}

	/************** CELLS *****************/
	@Override
	public void saveCells(Collection<CellDataWithProperties> cells) throws StorageException, NotFoundException {
		Assert.notNull(cells);
		SheetDbSession session = newDbSession();
		try {
			cellsMapper.saveCells(session, sheetName, cells);
		} finally {
			session.close();
		}

	}

	@Override
	public Matrix<CellData> loadCells(AreaReference area) throws StorageException, NotFoundException {
		SheetDbSession session = newDbSession();
		try {
			return cellsMapper.loadCells(session, sheetName, area);
		} finally {
			session.close();
		}
	}

	/************* storage methods - to cache ******************/
	public int getRowCount(SheetDbSession session) throws NotFoundException {
		return rowsMapper.getRowCount(session, sheetName);
	}

	public int getColumnCount(SheetDbSession session) throws NotFoundException {
		return columnsMapper.getColumnCount(session, sheetName);
	}

	public DbSheetStorageInfo getSheetStorage(SheetDbSession session) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = sheetsMapper.loadStorageInfo(session, sheetName);
		if (sheetStorage == null) {
			throw new NotFoundException("Sheet " + sheetName + " was not found");
		}
		return sheetStorage;
	}

	public DbSheetStorageInfo getOrCreateSheetStorage(SheetDbSession session, SheetType type) {
		try {
			return getSheetStorage(session);
		} catch (NotFoundException ex) {
			DbSheetStorageInfo storage = sheetsMapper.createStorageInfo(session, sheetName, type);
			return storage;
		}
	}

	/******** column storage *************/
	public DbColumnStorageInfo getColumnStorage(SheetDbSession session, int column) throws NotFoundException {
		List<DbColumnStorageInfo> columnStorages = columnsMapper.loadColumnsStorageInfo(session, sheetName,
				Range.range(column));
		if (columnStorages.size() > 0) {
			return columnStorages.get(0);
		}
		throw new NotFoundException("Column " + sheetName + "." + column + " was not found");
	}

	/**
	 * creates all the storage columns until the given column
	 * 
	 * @param session
	 * @param maxColumn
	 * @throws NotFoundException
	 */
	public List<DbColumnStorageInfo> createColumnStorage(SheetDbSession session, int maxColumn)
			throws NotFoundException {
		return columnsMapper.createStorageInfo(session, sheetName, maxColumn);
	}

	public DbColumnStorageInfo insertColumnStorage(SheetDbSession session, int column) throws NotFoundException {
		return columnsMapper.insertStorageInfo(session, sheetName, column);
	}

	public void deleteColumnStorage(SheetDbSession session, int column) throws NotFoundException {
		columnsMapper.deleteStorageInfo(session, sheetName, column);
	}

	public List<DbColumnStorageInfo> loadColumnsStorageInfo(SheetDbSession session, Range columnsRange)
			throws NotFoundException {
		return columnsMapper.loadColumnsStorageInfo(session, sheetName, columnsRange);
	}

	/******** row storage *************/
	public DbRowStorageInfo getRowStorage(SheetDbSession session, int row) throws NotFoundException {
		List<DbRowStorageInfo> rowsStorages = rowsMapper.loadRowsStorageInfo(session, sheetName, Range.range(row));
		if (rowsStorages.size() > 0) {
			return rowsStorages.get(0);
		}
		throw new NotFoundException("Row " + sheetName + "." + row + " was not found");
	}

	protected DbRowStorageInfo createRowStorage(SheetDbSession session, int row) throws NotFoundException {
		return rowsMapper.createStorageInfo(session, sheetName, row);
	}

	public DbRowStorageInfo getOrCreateRowStorage(SheetDbSession session, int row) throws NotFoundException {
		List<DbRowStorageInfo> rowsStorages = rowsMapper.loadRowsStorageInfo(session, sheetName, Range.range(row));
		if (rowsStorages.size() > 0) {
			return rowsStorages.get(0);
		}
		return createRowStorage(session, row);
	}

	public DbRowStorageInfo insertRowStorage(SheetDbSession session, int row) throws NotFoundException {
		return rowsMapper.insertStorageInfo(session, sheetName, row);
	}

	public void deleteRowStorage(SheetDbSession session, int row) throws NotFoundException {
		rowsMapper.deleteStorageInfo(session, sheetName, row);
	}

	public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession session, Range rowsRange) throws NotFoundException {
		return rowsMapper.loadRowsStorageInfo(session, sheetName, rowsRange);
	}

	public Integer getMaxRowId(SheetDbSession session) throws NotFoundException {
		return rowsMapper.getMaxRowId(session, sheetName);
	}

	/********
	 * cell storage
	 * 
	 * @throws NotFoundException
	 *             *
	 ********/
	// other props
	public SparseMatrixCollection getCellsStorage(SheetDbSession session) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = getSheetStorage(session);

		SparseMatrixCollection collection = matrixMapper.loadAll(session, sheetStorage);
		return collection;
	}

	public SheetFullName getSheetName() {
		return sheetName;
	}

	protected SheetDbSession newDbSession() throws StorageException, NotFoundException {
		return new SheetDbSession(workbookService.newDbSession(sheetName.getWorkbookId()), this);
	}

}
