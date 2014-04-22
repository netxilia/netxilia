package org.netxilia.spi.impl.storage.db;

import java.util.List;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.Range;

public interface IDbSheetStorageService {
	/************* storage methods - to cache ******************/
	public int getRowCount(SheetDbSession data) throws NotFoundException;

	public int getColumnCount(SheetDbSession data) throws NotFoundException;

	public DbSheetStorageInfo getSheetStorage(SheetDbSession data) throws NotFoundException;

	public DbSheetStorageInfo getOrCreateSheetStorage(SheetDbSession data, SheetType type);

	/******** column storage *************/
	public DbColumnStorageInfo getColumnStorage(SheetDbSession data, int column) throws NotFoundException;

	public List<DbColumnStorageInfo> createColumnStorage(SheetDbSession data, int maxColumn) throws NotFoundException;

	public DbColumnStorageInfo insertColumnStorage(SheetDbSession data, int column) throws NotFoundException;

	public void deleteColumnStorage(SheetDbSession data, int column) throws NotFoundException;

	public List<DbColumnStorageInfo> loadColumnsStorageInfo(SheetDbSession data, Range columnsRange)
			throws NotFoundException;

	/******** row storage *************/
	public DbRowStorageInfo getRowStorage(SheetDbSession data, int row) throws NotFoundException;

	public DbRowStorageInfo getOrCreateRowStorage(SheetDbSession data, int row) throws NotFoundException;

	public DbRowStorageInfo insertRowStorage(SheetDbSession data, int row) throws NotFoundException;

	public void deleteRowStorage(SheetDbSession data, int row) throws NotFoundException;

	public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession data, Range rowsRange) throws NotFoundException;

	public Integer getMaxRowId(SheetDbSession data) throws NotFoundException;

	/********
	 * cell storage
	 * 
	 * @throws NotFoundException
	 *             *
	 ********/
	// other props
	public SparseMatrixCollection getCellsStorage(SheetDbSession data) throws NotFoundException;

	public SheetDimensions getSheetDimensions(SheetDbSession data) throws NotFoundException;
}
