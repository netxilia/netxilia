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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.Range;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.netxilia.spi.impl.storage.db.sql.RowMapper;

/**
 * Rows properties are stored along with other cell properties in the properties table. For the row insertion an ID has
 * to be generated. Because the ID is also in charge of giving the order
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class NormalRowsMapper extends AbstractRowsMapper {
	private String orderByColumn = "order_by";

	public List<List<String>> loadValues(SheetDbSession data, DbSheetStorageInfo sheetStorage, AreaReference area)
			throws NotFoundException {
		String valueTableName = sheetStorage.getDbTableName();
		DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
		if (valuesTable == null) {
			return Collections.emptyList();
		}
		List<DbColumnStorageInfo> columns = data.getStorageService().loadColumnsStorageInfo(data, area.getColumns());
		ParameterizedQuery query = new ParameterizedQuery("SELECT * FROM " + valueTableName + " ORDER BY "
				+ orderByColumn);
		if (!area.equals(AreaReference.ALL)) {
			query = DatabaseUtils.limitQuery(query, area.getRows());
		}
		return data.getWorkbookData().query(query.getQuery(), new CellsLoader(area, columns),
				query.getParams().toArray());
	}

	@Override
	public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession data, DbSheetStorageInfo storageInfo, Range rowsRange) {
		String valueTableName = storageInfo.getDbTableName();
		DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
		if (valuesTable == null) {
			return Collections.emptyList();
		}
		ParameterizedQuery query = new ParameterizedQuery("SELECT id, order_by FROM " + valueTableName + " ORDER BY "
				+ orderByColumn);
		if (!rowsRange.equals(Range.ALL)) {
			query = DatabaseUtils.limitQuery(query, rowsRange);
		}
		return data.getWorkbookData().query(query.getQuery(), new RowLoader(), query.getParams().toArray());
	}

	@Override
	public DbRowStorageInfo createStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws StorageException, NotFoundException {
		return createStorageInfo(data, sheetStorage, row, false);
	}

	@Override
	public DbRowStorageInfo insertStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws StorageException, NotFoundException {
		return createStorageInfo(data, sheetStorage, row, true);
	}

	protected DbRowStorageInfo createStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row,
			boolean insert) throws StorageException, NotFoundException {
		String valueTableName = sheetStorage.getDbTableName();
		DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
		ensureTableExists(valueTableName, valuesTable, data.getWorkbookData());

		int rowCount = data.getStorageService().getRowCount(data);
		// add also the missing rows
		DbRowStorageInfo rowStorageInfo = null;
		for (int createRow = rowCount; createRow <= row; createRow++) {
			rowStorageInfo = generateKey(data, sheetStorage, rowStorageInfo, createRow, createRow);
			data.getWorkbookData().update("INSERT INTO " + valueTableName //
					+ " (id, order_by) VALUES (?, ?)", //
					rowStorageInfo.getId(), rowStorageInfo.getOrderBy());
		}
		if (insert && row < rowCount) {
			rowStorageInfo = generateKey(data, sheetStorage, rowStorageInfo, row, rowCount);
			data.getWorkbookData().update("INSERT INTO " + valueTableName //
					+ " (id, order_by) VALUES (?, ?)", //
					rowStorageInfo.getId(), rowStorageInfo.getOrderBy());
		}

		return rowStorageInfo;
	}

	@Override
	public void deleteStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row) throws NotFoundException {
		DbRowStorageInfo rowStorageInfo = data.getStorageService().getRowStorage(data, row);
		if (rowStorageInfo == null) {
			throw new StorageException("The row storage is not yet set");
		}
		String valueTableName = sheetStorage.getDbTableName();
		DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
		if (valuesTable == null) {
			// the table should be created by the sheet
			throw new StorageException("The table " + valueTableName + " does not exist");
		}
		data.getWorkbookData().update("DELETE FROM " + valueTableName + " WHERE id = ?", rowStorageInfo.getId());

	}

	@Override
	public int getRowCount(SheetDbSession data, DbSheetStorageInfo sheetStorage) {
		String valueTableName = sheetStorage.getDbTableName();
		return data.getWorkbookData().queryForInt("SELECT COUNT(*) FROM " + valueTableName);
	}

	/*
	 * public void deleteAll(SheetDbData data, DbSheetStorageInfo sheetStorageInfo) { String valueTableName =
	 * sheetStorageInfo.getDbTableName(); DbTable valuesTable = data.getSchema().getTable(valueTableName); if
	 * (valuesTable == null) { // the table should be created by the sheet throw new StorageException("The table " +
	 * valueTableName + " does not exist"); } data.update("DELETE FROM " + valueTableName); deleteProperty(data,
	 * sheetStorageInfo.getId(), ROW_CATEGORY, null, null);
	 * 
	 * // this is called when the sheet is deleted // no need to clean matrix mapper and rowStorage }
	 */

	@Override
	protected int getMaxRowId(SheetDbSession data, DbSheetStorageInfo sheetStorage) {
		String valueTableName = sheetStorage.getDbTableName();
		DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
		ensureTableExists(valueTableName, valuesTable, data.getWorkbookData());

		String query = "SELECT MAX(id) FROM " + valueTableName;

		return data.getWorkbookData().queryForInt(query.toString());
	}

	/** Internal helper that is used to restore a column from DB */
	private class RowLoader implements RowMapper<DbRowStorageInfo> {
		@Override
		public DbRowStorageInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new DbRowStorageInfo(rs.getInt("id"), rs.getFloat("order_by"));
		}
	}

	private class CellsLoader implements RowMapper<List<String>> {
		private final AreaReference area;
		private final List<DbColumnStorageInfo> columns;

		public CellsLoader(AreaReference area, List<DbColumnStorageInfo> columns) {
			this.area = area;
			this.columns = columns;
		}

		@Override
		public List<String> mapRow(ResultSet rs, int rowNum) throws SQLException {
			List<String> row = new ArrayList<String>(area.getLastColumnIndex() - area.getFirstColumnIndex() + 1);
			for (int c = area.getFirstColumnIndex(); c <= area.getLastColumnIndex(); ++c) {
				DbColumnStorageInfo columnStorage = columns.get(c - area.getFirstColumnIndex());
				if (columnStorage == null) {
					throw new StorageException("Cannot find storage info for column " + c);
				}
				row.add(rs.getString(columnStorage.getDbColumnName()));
			}
			return row;

		}
	}

}
