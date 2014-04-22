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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.RowData.Property;
import org.netxilia.api.reference.Range;

public abstract class AbstractRowsMapper extends AbstractMapper {
	private final static Logger log = Logger.getLogger(AbstractRowsMapper.class);

	abstract public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage,
			Range range) throws NotFoundException;

	abstract public DbRowStorageInfo createStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws NotFoundException;

	abstract public DbRowStorageInfo insertStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws NotFoundException;

	abstract public void deleteStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws NotFoundException;

	abstract protected int getMaxRowId(SheetDbSession data, DbSheetStorageInfo sheetStorage);

	abstract public int getRowCount(SheetDbSession data, DbSheetStorageInfo sheetStorage);

	protected DbRowStorageInfo generateKey(SheetDbSession data, DbSheetStorageInfo sheetStorage,
			DbRowStorageInfo givenPrevRow, int row, int totalRows) throws StorageException, NotFoundException {
		DbRowStorageInfo prevRow = givenPrevRow;
		if (prevRow == null && row > 0) {
			prevRow = data.getStorageService().getRowStorage(data, row - 1);
		}
		DbRowStorageInfo nextRow = row < totalRows ? data.getStorageService().getRowStorage(data, row) : null;
		Integer maxRowId = null;
		if (givenPrevRow != null) {
			// this is part of a chained insert - the given previous row contains the last id
			maxRowId = givenPrevRow.getId() + 1;
		} else {
			maxRowId = data.getStorageService().getMaxRowId(data) + 1;
		}
		float order = 1;
		if (prevRow != null) {
			if (nextRow != null) {
				order = (prevRow.getOrderBy() + nextRow.getOrderBy()) / 2;
			} else {
				order = prevRow.getOrderBy() + 1;
			}
		} else {
			if (nextRow != null) {
				order = (nextRow.getOrderBy()) / 2;
			} else {
				order = 1;
			}
		}
		return new DbRowStorageInfo(maxRowId, order);
	}

	public List<RowData> loadRows(SheetDbSession data, DbSheetStorageInfo storageInfo, Range rowsRange)
			throws NotFoundException {
		// TODO - improve here
		List<DbRowStorageInfo> storages = data.getStorageService().loadRowsStorageInfo(data, rowsRange);

		// map rowId to index in the returned array
		Map<Integer, Integer> rowIndexes = new HashMap<Integer, Integer>();
		for (int i = 0; i < storages.size(); ++i) {
			rowIndexes.put(storages.get(i).getId(), i + rowsRange.getMin());
		}
		List<Integer> rowIds = null;
		if (!rowsRange.equals(Range.ALL) && storages.size() > 0) {
			// if only certain rows are needed, then build the list of desired rowids
			// otherwise the list stays null - load all the rows
			rowIds = new ArrayList<Integer>(storages.size());
			for (int i = 0; i < storages.size(); ++i) {
				rowIds.add(storages.get(i).getId());
			}
		}
		// load only the needed rows
		List<RowData> foundRows = getProperties(data.getWorkbookData(), storageInfo.getId(), new RowDataFromMap(
				rowIndexes), ROW_CATEGORY, rowIds, null);

		// add the found rows
		RowData[] rowsArray = new RowData[storages.size()];
		for (RowData foundRow : foundRows) {
			if (foundRow != null) {
				rowsArray[foundRow.getIndex() - rowsRange.getMin()] = foundRow;
			}
		}
		// create the missing rows
		for (int i = 0; i < rowsArray.length; ++i) {
			if (rowsArray[i] == null) {
				rowsArray[i] = new RowData(i + rowsRange.getMin(), 0, null);
			}
		}
		// Collections.sort(rows, new BeanComparator(RowData.Property.index.name()));
		return Arrays.asList(rowsArray);
	}

	public void deleteAll(SheetDbSession data, DbSheetStorageInfo storageInfo) throws StorageException {
		deleteProperty(data.getWorkbookData(), storageInfo.getId(), ROW_CATEGORY, null, null);
	}

	public void saveRow(SheetDbSession data, DbSheetStorageInfo sheetStorage, RowData row,
			Collection<RowData.Property> properties) throws StorageException, NotFoundException {
		DbRowStorageInfo rowStorageInfo = data.getStorageService().getOrCreateRowStorage(data, row.getIndex());
		// update only modifies separate properties
		super.setObject(data.getWorkbookData(), sheetStorage.getId(), ROW_CATEGORY, row, rowStorageInfo.getId(),
				toStringList(properties));
	}

	public void insertRow(SheetDbSession data, DbSheetStorageInfo sheetStorageInfo, RowData row,
			Collection<Property> properties) throws NotFoundException {

		DbRowStorageInfo rowStorageInfo = data.getStorageService().insertRowStorage(data, row.getIndex());
		// insert
		super.addObject(data.getWorkbookData(), sheetStorageInfo.getId(), ROW_CATEGORY, row, rowStorageInfo.getId(),
				toStringList(properties));
	}

	public void deleteRow(SheetDbSession data, DbSheetStorageInfo sheetStorageInfo, int rowIndex)
			throws NotFoundException {
		DbRowStorageInfo rowStorageInfo = data.getStorageService().getRowStorage(data, rowIndex);
		if (rowStorageInfo == null) {
			throw new StorageException("The row storage is not yet set");
		}
		data.getStorageService().deleteRowStorage(data, rowIndex);

		deleteProperty(data.getWorkbookData(), sheetStorageInfo.getId(), ROW_CATEGORY, rowStorageInfo.getId(), null);
	}

	/** Internal helper that is used to restore a complete sheet from DB */
	private class RowDataFromMap implements IInstanceProviderFromMap<RowData> {
		private final Map<Integer, Integer> rowIndexes;

		public RowDataFromMap(Map<Integer, Integer> rowIndexes) {
			this.rowIndexes = rowIndexes;
		}

		@Override
		public RowData newInstance(String category, String objectId, Map<String, String> properties) {
			try {
				Integer rowId = Integer.valueOf(objectId);
				Integer rowIndex = rowIndexes.get(rowId);
				if (rowIndex == null) {
					throw new RuntimeException("Unknown row index for row id:" + rowId);
				}
				Styles styles = Styles.valueOf(properties.get(RowData.Property.styles.name()));
				int height = NumberUtils.toInt(properties.get(RowData.Property.height.name()), 0);
				return new RowData(rowIndex, height, styles);
			} catch (Exception ex) {
				log.error("Could not load row description from:" + properties + ":" + ex);
				return null;
			}
		}
	}

}
