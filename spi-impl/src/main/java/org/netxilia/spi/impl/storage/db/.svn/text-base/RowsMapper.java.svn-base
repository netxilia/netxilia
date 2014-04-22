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
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.RowData.Property;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.Range;
import org.springframework.beans.factory.annotation.Autowired;

public class RowsMapper extends AbstractMapper {
	@Autowired
	private NormalRowsMapper normalRowsMapper;
	@Autowired
	private OtherRowsMapper otherRowsMapper;

	@Autowired
	private SparseMatrixMapper matrixMapper;

	public NormalRowsMapper getNormalRowsMapper() {
		return normalRowsMapper;
	}

	public void setNormalRowsMapper(NormalRowsMapper normalRowsMapper) {
		this.normalRowsMapper = normalRowsMapper;
	}

	public OtherRowsMapper getOtherRowsMapper() {
		return otherRowsMapper;
	}

	public void setOtherRowsMapper(OtherRowsMapper otherRowsMapper) {
		this.otherRowsMapper = otherRowsMapper;
	}

	public SparseMatrixMapper getMatrixMapper() {
		return matrixMapper;
	}

	public void setMatrixMapper(SparseMatrixMapper matrixMapper) {
		this.matrixMapper = matrixMapper;
	}

	private AbstractRowsMapper getRowsMapper(SheetType sheetType) {
		return (sheetType == SheetType.normal) ? normalRowsMapper : otherRowsMapper;
	}

	public List<RowData> loadRows(SheetDbSession data, SheetFullName sheetFullName, Range rows) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return getRowsMapper(sheetStorage.getType()).loadRows(data, sheetStorage, rows);
	}

	public void deleteRow(SheetDbSession data, SheetFullName sheetFullName, int row) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		getRowsMapper(sheetStorage.getType()).deleteRow(data, sheetStorage, row);
		matrixMapper.deleteRow(data, sheetStorage, row);
	}

	public void saveRow(SheetDbSession data, SheetFullName sheetFullName, RowData row, Collection<Property> properties)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		getRowsMapper(sheetStorage.getType()).saveRow(data, sheetStorage, row, properties);
	}

	public void insertRow(SheetDbSession data, SheetFullName sheetFullName, RowData row, Collection<Property> properties)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		getRowsMapper(sheetStorage.getType()).insertRow(data, sheetStorage, row, properties);
		matrixMapper.insertRow(data, sheetStorage, row.getIndex());
	}

	public List<DbRowStorageInfo> loadRowsStorageInfo(SheetDbSession data, SheetFullName sheetFullName, Range range)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return getRowsMapper(sheetStorage.getType()).loadRowsStorageInfo(data, sheetStorage, range);
	}

	public DbRowStorageInfo createStorageInfo(SheetDbSession data, SheetFullName sheetFullName, int row)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return getRowsMapper(sheetStorage.getType()).createStorageInfo(data, sheetStorage, row);
	}

	public DbRowStorageInfo insertStorageInfo(SheetDbSession data, SheetFullName sheetFullName, int row)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return getRowsMapper(sheetStorage.getType()).insertStorageInfo(data, sheetStorage, row);
	}

	public void deleteStorageInfo(SheetDbSession data, SheetFullName sheetFullName, int row) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		getRowsMapper(sheetStorage.getType()).deleteStorageInfo(data, sheetStorage, row);
	}

	public int getRowCount(SheetDbSession data, SheetFullName sheetFullName) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return getRowsMapper(sheetStorage.getType()).getRowCount(data, sheetStorage);
	}

	public Integer getMaxRowId(SheetDbSession data, SheetFullName sheetFullName) throws NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		return getRowsMapper(sheetStorage.getType()).getMaxRowId(data, sheetStorage);
	}

}
