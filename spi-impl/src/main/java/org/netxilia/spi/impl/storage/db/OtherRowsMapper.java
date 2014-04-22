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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.reference.Range;

/**
 * This class is used to map the rows of the other type of sheet: summary, user. Because these sheet have potentially
 * few data their content is entirely stored in props table. Thus for rows only their count is stored.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class OtherRowsMapper extends AbstractRowsMapper {
	private final static Logger log = Logger.getLogger(OtherRowsMapper.class);

	public static final String COUNT_PROPERTY = "count";

	@Override
	public DbRowStorageInfo createStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws NotFoundException {
		int rowCount = data.getStorageService().getRowCount(data);
		// add also the missing rows
		DbRowStorageInfo rowStorage = null;
		for (int createRow = rowCount; createRow <= row; createRow++) {
			rowStorage = generateKey(data, sheetStorage, rowStorage, createRow, rowCount);
			addProperty(data.getWorkbookData(), sheetStorage.getId(), ROW_CATEGORY, rowStorage.getId(),
					DbRowStorageInfo.Property.orderBy.name(), String.valueOf(rowStorage.getOrderBy()));
		}
		return rowStorage;
	}

	@Override
	public DbRowStorageInfo insertStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row)
			throws NotFoundException {
		int rowCount = data.getStorageService().getRowCount(data);

		// add also the missing rows
		DbRowStorageInfo rowStorage = null;
		for (int createRow = rowCount; createRow <= row; createRow++) {
			rowStorage = generateKey(data, sheetStorage, rowStorage, createRow, rowCount);
			addProperty(data.getWorkbookData(), sheetStorage.getId(), ROW_CATEGORY, rowStorage.getId(),
					DbRowStorageInfo.Property.orderBy.name(), String.valueOf(rowStorage.getOrderBy()));
		}
		if (row < rowCount) {
			rowStorage = generateKey(data, sheetStorage, rowStorage, row, rowCount);
			addProperty(data.getWorkbookData(), sheetStorage.getId(), ROW_CATEGORY, rowStorage.getId(),
					DbRowStorageInfo.Property.orderBy.name(), String.valueOf(rowStorage.getOrderBy()));
		}
		return rowStorage;
	}

	@Override
	public void deleteStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, int row) throws NotFoundException {
		// deleted already when deleting a row
	}

	@Override
	public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession data, DbSheetStorageInfo sheetStorage, Range range)
			throws NotFoundException {
		return getProperties(data.getWorkbookData(), sheetStorage.getId(), new RowStorageFromMap(), ROW_CATEGORY, null,
				null, range);
	}

	@Override
	public int getRowCount(SheetDbSession data, DbSheetStorageInfo sheetStorage) {
		return count(data.getWorkbookData(), sheetStorage.getId(), ROW_CATEGORY, null,
				DbRowStorageInfo.Property.orderBy.name());
	}

	@Override
	protected int getMaxRowId(SheetDbSession data, DbSheetStorageInfo sheetStorage) {
		return getMaxObjectId(data.getWorkbookData(), sheetStorage.getId(), ROW_CATEGORY);
	}

	/** Internal helper that is used to restore a complete sheet from DB */
	private class RowStorageFromMap implements IInstanceProviderFromMap<DbRowStorageInfo> {
		public RowStorageFromMap() {
		}

		@Override
		public DbRowStorageInfo newInstance(String category, String objectId, Map<String, String> properties) {
			try {
				int rowId = Integer.valueOf(objectId);
				float orderBy = Float.valueOf(properties.get(DbRowStorageInfo.Property.orderBy.name()));

				return new DbRowStorageInfo(rowId, orderBy);
			} catch (Exception ex) {
				log.error("Could not load row from:" + properties + ": " + ex);
				return null;
			}
		}
	}

}
