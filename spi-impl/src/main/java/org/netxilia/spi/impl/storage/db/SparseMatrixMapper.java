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

import org.apache.log4j.Logger;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.impl.utils.BlockMetadata;
import org.netxilia.api.impl.utils.OrderedBlock;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;

/**
 * This class manages the storage of the sparse matrices containing the properties of the cells from a sheet.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SparseMatrixMapper extends AbstractMapper {
	private final static Logger log = Logger.getLogger(SparseMatrixMapper.class);

	public SparseMatrixCollection loadAll(SheetDbSession data, DbSheetStorageInfo storageInfo) throws StorageException {
		final SparseMatrixCollection matrices = new SparseMatrixCollection();

		super.getProperties(data.getWorkbookData(), storageInfo.getId(), new IInstanceProvider<AreaReference>() {
			@Override
			public AreaReference newInstance(String category, String objectId) {
				return new AreaReference(objectId);
			}

			@Override
			public boolean setProperty(AreaReference ref, String property, String value) {
				matrices.put(ref, property, value);
				return true;
			}
		}, CELLS_CATEGORY, null, null);

		return matrices;
	}

	public void deleteAll(WorkbookDbSession data, Workbook workbook) {
		String tableName = getPropertiesTableName(data);
		if (data.getSchema().getTable(tableName) == null) {
			return;
		}

		try {
			data.getDdl().writer().dropTable(data.getSchema(), tableName);
		} catch (SQLException e) {
			throw new StorageException(e);
		}

	}

	public void deleteAll(SheetDbSession data, DbSheetStorageInfo storageInfo) {
		String tableName = getPropertiesTableName(data.getWorkbookData());
		if (data.getWorkbookData().getSchema().getTable(tableName) == null) {
			return;
		}
		data.getWorkbookData().update("DELETE FROM " + tableName + " WHERE sheet_id = ? AND category = ?",
				storageInfo.getId().getId(), CELLS_CATEGORY);

	}

	private AreaReference blockToReference(OrderedBlock block) {
		return new AreaReference(new CellReference(null, block.getFirstRow(), block.getFirstCol()), new CellReference(
				null, block.getLastRow(), block.getLastCol()));
	}

	public void insert(SheetDbSession data, DbSheetStorageInfo storage, String property, BlockMetadata<String> block) {
		addProperty(data.getWorkbookData(), storage.getId(), CELLS_CATEGORY, blockToReference(block.getBlock())
				.formatAsString(), property, block.getValue());

	}

	public void modify(SheetDbSession data, DbSheetStorageInfo storage, String property, BlockMetadata<String> block) {
		setProperty(data.getWorkbookData(), storage.getId(), CELLS_CATEGORY, blockToReference(block.getBlock())
				.formatAsString(), property, block.getValue());

	}

	public void delete(SheetDbSession data, DbSheetStorageInfo storage, String property, OrderedBlock block) {
		boolean ok = deleteProperty(data.getWorkbookData(), storage.getId(), CELLS_CATEGORY, blockToReference(block)
				.formatAsString(), property);
		if (!ok) {
			log.warn("Deleting inexistant matrix entry:" + blockToReference(block).formatAsString() + " prop:"
					+ property);
		}
	}

	public void insertColumn(SheetDbSession data, DbSheetStorageInfo sheetStorage, int columnIndex)
			throws StorageException, NotFoundException {
		SparseMatrixCollection matrices = data.getStorageService().getCellsStorage(data);
		matrices.setListener(getMatrixCollectionListener(data, sheetStorage));
		try {
			for (String property : matrices.getProperties()) {
				matrices.insertColumn(property, columnIndex);
			}
		} finally {
			matrices.getListener().save();
			matrices.setListener(null);
		}
	}

	public void deleteColumn(SheetDbSession data, DbSheetStorageInfo sheetStorage, int columnIndex)
			throws StorageException, NotFoundException {
		SparseMatrixCollection matrices = data.getStorageService().getCellsStorage(data);
		matrices.setListener(getMatrixCollectionListener(data, sheetStorage));
		try {
			for (String property : matrices.getProperties()) {
				matrices.deleteColumn(property, columnIndex);
			}
		} finally {
			matrices.getListener().save();
			matrices.setListener(null);
		}
	}

	public void deleteRow(SheetDbSession data, DbSheetStorageInfo sheetStorage, int rowIndex) throws StorageException,
			NotFoundException {
		SparseMatrixCollection matrices = data.getStorageService().getCellsStorage(data);
		matrices.setListener(getMatrixCollectionListener(data, sheetStorage));
		try {
			for (String property : matrices.getProperties()) {
				matrices.deleteRow(property, rowIndex);
			}
		} finally {
			matrices.getListener().save();
			matrices.setListener(null);
		}
	}

	public void insertRow(SheetDbSession data, DbSheetStorageInfo sheetStorage, int rowIndex) throws StorageException,
			NotFoundException {
		SparseMatrixCollection matrices = data.getStorageService().getCellsStorage(data);
		matrices.setListener(getMatrixCollectionListener(data, sheetStorage));
		try {
			for (String property : matrices.getProperties()) {
				matrices.insertRow(property, rowIndex);
			}
		} finally {
			matrices.getListener().save();
			matrices.setListener(null);
		}
	}

	public ISparseMatrixCollectionListener getMatrixCollectionListener(final SheetDbSession data,
			final DbSheetStorageInfo storage) {
		return new SparseMatrixCollectionListener(data, storage, this);
	}
}
