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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ColumnData.Property;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.Range;
import org.netxilia.api.storage.IJsonSerializer;
import org.netxilia.api.utils.CollectionUtils;
import org.netxilia.api.utils.IListElementCreator;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbDataType;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.reflect.TypeToken;

/**
 * Handles the DB-Mapping for Column. Columns are stored in a separate table containing the columns for all the sheets
 * in a workbook.
 * 
 * 
 * @author acraciun
 */
public class ColumnsMapper extends AbstractMapper {
	private static final String COLUMN_NAME_PREFIX = "COL";

	private final static Logger log = Logger.getLogger(ColumnsMapper.class);

	private static final String COLUMNS_STORAGE_PROPERTY = "columnsStorage";

	private static final String COLUMNS_PROPERTY = "columns";

	private static final IListElementCreator<ColumnData> COLUMN_DATA_CREATOR = new IListElementCreator<ColumnData>() {
		@Override
		public ColumnData newElement(int index) {
			return new ColumnData(index, 0, null);
		}
	};

	@Autowired
	private SparseMatrixMapper matrixMapper;

	@Autowired
	private IJsonSerializer jsonSerializer;

	public SparseMatrixMapper getMatrixMapper() {
		return matrixMapper;
	}

	public void setMatrixMapper(SparseMatrixMapper matrixMapper) {
		this.matrixMapper = matrixMapper;
	}

	/**
	 * creates the storage for the given column. this will add a new column in the table if needed.
	 * 
	 * @param data
	 * @param sheetFullName
	 * @param columnIndex
	 * @return
	 * @throws NotFoundException
	 */
	public List<DbColumnStorageInfo> createStorageInfo(SheetDbSession data, SheetFullName sheetFullName,
			int maxColumnIndex) throws NotFoundException {
		DbSheetStorageInfo sheetStorageInfo = data.getStorageService().getSheetStorage(data);

		List<DbColumnStorageInfo> columns = loadColumnsStorageInfo(data, sheetStorageInfo.getId(), Range.ALL);
		CollectionUtils.atLeastSize(columns, maxColumnIndex, columnStorageCreator(data, sheetStorageInfo, columns));
		if (maxColumnIndex == columns.size()) {
			DbColumnStorageInfo columnStorage = internalCreateStorageInfo(data, columns, sheetStorageInfo,
					maxColumnIndex);
			columns.add(columnStorage);
			saveColumnsStorageInfo(data, sheetStorageInfo.getId(), columns);
		}
		return columns;
	}

	private IListElementCreator<DbColumnStorageInfo> columnStorageCreator(final SheetDbSession data,
			final DbSheetStorageInfo sheetStorageInfo, final List<DbColumnStorageInfo> existentColumns) {
		return new IListElementCreator<DbColumnStorageInfo>() {
			@Override
			public DbColumnStorageInfo newElement(int index) {
				try {
					return internalCreateStorageInfo(data, existentColumns, sheetStorageInfo, index);
				} catch (NotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private DbColumnStorageInfo internalCreateStorageInfo(SheetDbSession data,
			List<DbColumnStorageInfo> existentColumns, DbSheetStorageInfo sheetStorageInfo, int columnIndex)
			throws NotFoundException {

		DbColumnStorageInfo columnStorage = new DbColumnStorageInfo(generateColumnName(existentColumns));

		// save storage
		if (sheetStorageInfo.getType() == SheetType.normal) {
			String valueTableName = sheetStorageInfo.getDbTableName();
			DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
			if (valuesTable == null) {
				// the table should be created by the sheet
				throw new StorageException("The table " + valueTableName + " does not exist");
			}

			DbColumn newCol = buildDbColumn(columnStorage);
			try {
				data.getWorkbookData().getDdl().writer().addColumn(valuesTable, newCol);
			} catch (SQLException e) {
				throw new StorageException(e);
			}
		}
		return columnStorage;
	}

	/**
	 * 
	 * @param data
	 * @param sheetFullName
	 * @param columnIndex
	 * @throws NotFoundException
	 */
	public void deleteStorageInfo(SheetDbSession data, SheetFullName sheetFullName, int columnIndex)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorageInfo = data.getStorageService().getSheetStorage(data);
		DbColumnStorageInfo columnStorage = data.getStorageService().getColumnStorage(data, columnIndex);
		if (columnStorage == null) {
			throw new StorageException("The column storage is not yet set");
		}

		if (sheetStorageInfo.getType() == SheetType.normal) {
			String valueTableName = sheetStorageInfo.getDbTableName();
			DbTable valuesTable = data.getWorkbookData().getSchema().getTable(valueTableName);
			if (valuesTable == null) {
				// the table should be created by the sheet
				throw new StorageException("The table " + valueTableName + " does not exist");
			}
			try {
				data.getWorkbookData().getDdl().writer().dropColumn(valuesTable, columnStorage.getDbColumnName());
			} catch (SQLException e) {
				throw new StorageException(e);
			}
		}

		List<DbColumnStorageInfo> columns = loadColumnsStorageInfo(data, sheetStorageInfo.getId(), Range.ALL);
		if (columnIndex < columns.size()) {
			columns.remove(columnIndex);
		}
		saveColumnsStorageInfo(data, sheetStorageInfo.getId(), columns);
	}

	/**
	 * insert a new column storage at the given position. create the new column also
	 * 
	 * @param data
	 * @param sheetFullName
	 * @param columnIndex
	 * @throws NotFoundException
	 */
	public DbColumnStorageInfo insertStorageInfo(SheetDbSession data, SheetFullName sheetFullName, int columnIndex)
			throws NotFoundException {

		DbSheetStorageInfo sheetStorageInfo = data.getStorageService().getSheetStorage(data);

		List<DbColumnStorageInfo> columns = loadColumnsStorageInfo(data, sheetStorageInfo.getId(), Range.ALL);
		DbColumnStorageInfo columnStorage = internalCreateStorageInfo(data, columns, sheetStorageInfo, columnIndex);
		// make sure to add missing elements
		CollectionUtils.atLeastSize(columns, columnIndex - 1, columnStorageCreator(data, sheetStorageInfo, columns));
		if (columnIndex < columns.size()) {
			columns.add(columnIndex, columnStorage);
		} else {
			columns.add(columnStorage);
		}
		saveColumnsStorageInfo(data, sheetStorageInfo.getId(), columns);
		return columnStorage;
	}

	/**
	 * save the given column. adds missing columns
	 * 
	 * @param data
	 * @param sheetFullName
	 * @param column
	 * @param properties
	 * @throws NotFoundException
	 */
	public void saveColumn(SheetDbSession data, SheetFullName sheetFullName, ColumnData column,
			Collection<ColumnData.Property> properties) throws NotFoundException {
		DbSheetStorageInfo sheetStorageInfo = data.getStorageService().getSheetStorage(data);
		data.getStorageService().createColumnStorage(data, column.getIndex());

		List<ColumnData> columns = loadColumns(data, sheetFullName, Range.ALL);
		CollectionUtils.atLeastSize(columns, column.getIndex() + 1, COLUMN_DATA_CREATOR);
		columns.set(column.getIndex(), column);
		saveColumns(data, sheetStorageInfo.getId(), columns);
	}

	/**
	 * inserts the given column at the specified position. adds also the missing columns
	 * 
	 * @param data
	 * @param sheetFullName
	 * @param column
	 * @param properties
	 * @throws NotFoundException
	 */
	public void insertColumn(SheetDbSession data, SheetFullName sheetFullName, ColumnData column,
			Collection<Property> properties) throws NotFoundException {
		DbSheetStorageInfo sheetStorageInfo = data.getStorageService().getSheetStorage(data);
		List<ColumnData> columns = loadColumns(data, sheetFullName, Range.ALL);

		data.getStorageService().insertColumnStorage(data, column.getIndex());
		// modify matrix collections
		matrixMapper.insertColumn(data, sheetStorageInfo, column.getIndex());

		// make sure to add missing elements
		CollectionUtils.atLeastSize(columns, column.getIndex() - 1, COLUMN_DATA_CREATOR);
		if (column.getIndex() < columns.size()) {
			columns.add(column.getIndex(), column);
		} else {
			columns.add(column);
		}
		saveColumns(data, sheetStorageInfo.getId(), columns);
	}

	/**
	 * deletes the given column
	 * 
	 * @param data
	 * @param sheetStorageInfo
	 * @param column
	 * @throws NotFoundException
	 * @throws StorageException
	 */
	public void deleteColumn(SheetDbSession data, SheetFullName sheetFullName, int column) throws StorageException,
			NotFoundException {
		// make sure it exists
		data.getStorageService().getColumnStorage(data, column);
		data.getStorageService().deleteColumnStorage(data, column);

		DbSheetStorageInfo sheetStorageInfo = data.getStorageService().getSheetStorage(data);
		List<ColumnData> columns = loadColumns(data, sheetFullName, Range.ALL);
		if (column < columns.size()) {
			columns.remove(column);
		}
		saveColumns(data, sheetStorageInfo.getId(), columns);

		matrixMapper.deleteColumn(data, sheetStorageInfo, column);
	}

	protected void saveColumns(SheetDbSession data, SheetId sheetId, List<ColumnData> columns) throws StorageException,
			NotFoundException {

		String serializedColumns = null;

		if (columns.size() != 0) {
			serializedColumns = jsonSerializer.serialize(columns);
		}

		if (!setProperty(data.getWorkbookData(), sheetId, SHEET_CATEGORY, sheetId, COLUMNS_PROPERTY, serializedColumns)) {
			addProperty(data.getWorkbookData(), sheetId, SHEET_CATEGORY, sheetId, COLUMNS_PROPERTY, serializedColumns);
		}
	}

	private String generateColumnName(List<DbColumnStorageInfo> existentColumns) {
		for (int i = 0;; i++) {
			String newColumnName = COLUMN_NAME_PREFIX + i;
			boolean found = false;
			for (DbColumnStorageInfo col : existentColumns) {
				if (newColumnName.equals(col.getDbColumnName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				return newColumnName;
			}
		}

	}

	private DbColumn buildDbColumn(DbColumnStorageInfo column) {
		DbColumn col = new DbColumn();
		col.setName(column.getDbColumnName());
		col.setPrimaryKey(false);
		col.setDataType(DbDataType.VARCHAR);
		col.setSize(255);
		// col.setDefaultValue(colTempl.getDefaultValue());
		return col;
	}

	/**
	 * Loads all the column data from the properties table
	 * 
	 * @param data
	 * @param sheetStorage
	 * @param columnsRange
	 * @return
	 * @throws NotFoundException
	 */
	public List<ColumnData> loadColumns(SheetDbSession data, SheetFullName sheetFullName, Range columnsRange)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		int totalColumns = data.getStorageService().getColumnCount(data);
		return loadColumns(data, sheetStorage.getId(), columnsRange, totalColumns);
	}

	public int getColumnCount(SheetDbSession data, SheetFullName sheetFullName) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return loadColumnsStorageInfo(data, sheetStorage.getId(), Range.ALL).size();
	}

	@SuppressWarnings("unchecked")
	protected List<ColumnData> loadColumns(SheetDbSession data, SheetId sheetId, Range columnsRange, int totalColumns)
			throws NotFoundException {
		String serColumns = getProperty(data.getWorkbookData(), sheetId, SHEET_CATEGORY, sheetId.getId(),
				COLUMNS_PROPERTY);
		List<ColumnData> columns = null;
		if (serColumns != null) {
			try {
				columns = (List<ColumnData>) jsonSerializer.deserialize(new TypeToken<List<ColumnData>>() {
				}.getType(), serColumns);
			} catch (Exception e) {
				log.error("Cannot load charts from " + serColumns + ":" + e, e);
				columns = new ArrayList<ColumnData>();
			}
		} else {
			columns = new ArrayList<ColumnData>();
		}
		columns = CollectionUtils.atLeastSize(columns, totalColumns, COLUMN_DATA_CREATOR);
		Range boundRange = columnsRange.bind(0, columns.size());
		return columns.subList(boundRange.getMin(), boundRange.getMax());
	}

	public List<DbColumnStorageInfo> loadColumnsStorageInfo(SheetDbSession data, SheetFullName sheetFullName,
			Range columnsRange) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return loadColumnsStorageInfo(data, sheetStorage.getId(), columnsRange);
	}

	/**
	 * Loads all the storage information from the properties table
	 * 
	 * @param data
	 * @param sheetStorage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DbColumnStorageInfo> loadColumnsStorageInfo(SheetDbSession data, SheetId sheetId, Range columnsRange) {
		String serColumns = getProperty(data.getWorkbookData(), sheetId, SHEET_CATEGORY, sheetId.getId(),
				COLUMNS_STORAGE_PROPERTY);
		List<DbColumnStorageInfo> columns = null;
		if (serColumns != null) {
			try {
				columns = (List<DbColumnStorageInfo>) jsonSerializer.deserialize(
						new TypeToken<List<DbColumnStorageInfo>>() {
						}.getType(), serColumns);
			} catch (Exception e) {
				log.error("Cannot load charts from " + serColumns + ":" + e, e);
			}
		} else {
			columns = new ArrayList<DbColumnStorageInfo>();
		}
		Range boundRange = columnsRange.bind(0, columns.size());
		return columns.subList(boundRange.getMin(), boundRange.getMax());

	}

	protected void saveColumnsStorageInfo(SheetDbSession data, SheetId sheetId, Collection<DbColumnStorageInfo> columns)
			throws StorageException {
		String serializedColumns = null;

		if (columns.size() != 0) {
			serializedColumns = jsonSerializer.serialize(columns);
		}

		if (!setProperty(data.getWorkbookData(), sheetId, SHEET_CATEGORY, sheetId, COLUMNS_STORAGE_PROPERTY,
				serializedColumns)) {
			addProperty(data.getWorkbookData(), sheetId, SHEET_CATEGORY, sheetId, COLUMNS_STORAGE_PROPERTY,
					serializedColumns);
		}
	}

}
