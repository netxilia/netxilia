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
package org.netxilia.spi.storage;

import java.util.Collection;
import java.util.List;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.RowData.Property;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.utils.Matrix;

public interface ISheetStorageService {
	// sheets
	void saveSheet(SheetData sheet, Collection<SheetData.Property> properties) throws StorageException,
			NotFoundException;

	void deleteSheet() throws StorageException, NotFoundException;

	SheetData loadSheet() throws StorageException, NotFoundException;

	SheetDimensions getSheetDimensions() throws StorageException, NotFoundException;

	// rows
	List<RowData> loadRows(Range rows) throws StorageException, NotFoundException;

	void insertRow(RowData row, Collection<Property> properties) throws StorageException, NotFoundException;

	void saveRow(RowData row, Collection<RowData.Property> properties) throws StorageException, NotFoundException;

	void deleteRow(int row) throws StorageException, NotFoundException;

	// columns
	List<ColumnData> loadColumns(Range columns) throws StorageException, NotFoundException;

	void saveColumn(ColumnData column, Collection<ColumnData.Property> properties) throws StorageException,
			NotFoundException;

	void insertColumn(ColumnData column, Collection<ColumnData.Property> properties) throws NotFoundException;

	void deleteColumn(int column) throws StorageException, NotFoundException;

	// cells
	Matrix<CellData> loadCells(AreaReference area) throws StorageException, NotFoundException;

	void saveCells(Collection<CellDataWithProperties> cells) throws StorageException, NotFoundException;
}
