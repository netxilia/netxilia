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
package org.netxilia.spi.impl.storage.memory;

import java.util.Collection;
import java.util.List;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ColumnData.Property;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.reference.Range;
import org.netxilia.api.reference.ReferenceTransformers;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.MatrixBuilder;
import org.netxilia.spi.storage.ISheetStorageService;

public class InMemorySheetStorageServiceImpl implements ISheetStorageService {
	private final CompleteSheet sheet;

	public InMemorySheetStorageServiceImpl(CompleteSheet sheet) {
		this.sheet = sheet;
	}

	public CompleteSheet getSheet() {
		return sheet;
	}

	@Override
	public synchronized void deleteRow(int row) throws StorageException, NotFoundException {
		if (row < sheet.getRows().size()) {
			sheet.getRows().remove(row);
			// REMOVE also cells
			sheet.setCells(sheet.getCellsBuilder().removeRow(row).build());
			applyTransformer(sheet, ReferenceTransformers.deleteRow(row));
		}
	}

	@Override
	public synchronized List<RowData> loadRows(Range rows) throws StorageException, NotFoundException {
		int count = sheet.getRows().size();
		return sheet.getRows().subList(Math.min(count, rows.getMin()), Math.min(rows.getMax(), count));
	}

	@Override
	public synchronized void saveRow(RowData row, Collection<RowData.Property> properties) throws StorageException,
			NotFoundException {
		List<RowData> rows = sheet.getRows();
		minRows(rows, row.getIndex() + 1);
		rows.set(row.getIndex(), row);
	}

	@Override
	public synchronized void insertRow(RowData row, Collection<RowData.Property> properties) throws StorageException,
			NotFoundException {
		if (row.getIndex() < sheet.getRows().size()) {
			sheet.getRows().add(row.getIndex(), row);
			sheet.setCells(sheet.getCellsBuilder().insertRow(row.getIndex()).build());
			applyTransformer(sheet, ReferenceTransformers.insertRow(row.getIndex()));
		} else {
			minRows(sheet.getRows(), row.getIndex() + 1);
			sheet.getRows().set(row.getIndex(), row);
		}

	}

	@Override
	public synchronized Matrix<CellData> loadCells(AreaReference area) throws StorageException, NotFoundException {
		Matrix<CellData> cells = sheet.getCells();
		return cells.subMatrix(
				Math.min(area.getFirstRowIndex(), cells.getRowCount()),//
				Math.min(area.getFirstColumnIndex(), cells.getColumnCount()),
				Math.min(area.getLastRowIndex() + 1, cells.getRowCount()),
				Math.min(area.getLastColumnIndex() + 1, cells.getColumnCount()));
	}

	@Override
	public synchronized List<ColumnData> loadColumns(Range columns) throws StorageException, NotFoundException {
		int count = sheet.getColumns().size();
		return sheet.getColumns().subList(Math.min(count, columns.getMin()), Math.min(columns.getMax(), count));
	}

	@Override
	public synchronized void saveCells(Collection<CellDataWithProperties> cells) throws StorageException,
			NotFoundException {
		for (CellDataWithProperties saveCell : cells) {
			saveCell(saveCell.getCellData(), saveCell.getProperties());
		}
	}

	public synchronized void saveCell(CellData cell, Collection<CellData.Property> properties) throws StorageException,
			NotFoundException {
		sheet.setCells(sheet.getCellsBuilder()
				.set(cell.getReference().getRowIndex(), cell.getReference().getColumnIndex(), cell).build());
		minColumns(sheet.getColumns(), cell.getReference().getColumnIndex() + 1);
		minRows(sheet.getRows(), cell.getReference().getRowIndex() + 1);

	}

	public static void minColumns(List<ColumnData> columns, int minSize) {
		while (columns.size() < minSize) {
			columns.add(new ColumnData(columns.size(), 0, null));
		}
	}

	public static void minRows(List<RowData> rows, int minSize) {
		while (rows.size() < minSize) {
			rows.add(new RowData(rows.size(), 0, null));
		}
	}

	@Override
	public synchronized void saveColumn(ColumnData column, Collection<ColumnData.Property> properties)
			throws StorageException, NotFoundException {
		List<ColumnData> columns = sheet.getColumns();
		minColumns(columns, column.getIndex() + 1);
		columns.set(column.getIndex(), column);
	}

	@Override
	public synchronized void deleteColumn(int column) throws StorageException, NotFoundException {

		if (column < sheet.getColumns().size()) {
			sheet.getColumns().remove(column);
			sheet.setCells(sheet.getCellsBuilder().removeColumn(column).build());
		}
	}

	@Override
	public synchronized void saveSheet(SheetData sheetData, Collection<SheetData.Property> properties)
			throws StorageException, NotFoundException {

		sheet.setSheetData(sheetData);

	}

	@Override
	public SheetData loadSheet() throws StorageException, NotFoundException {
		return sheet.getSheetData();
	}

	@Override
	public void insertColumn(ColumnData column, Collection<Property> properties) {
		// TODO Auto-generated method stub

	}

	@Override
	public SheetDimensions getSheetDimensions() throws StorageException, NotFoundException {
		return new SheetDimensions(sheet.getRows().size(), sheet.getColumns().size());
	}

	private void applyTransformer(CompleteSheet sheet, IReferenceTransformer referenceTransformer) {
		MatrixBuilder<CellData> newCells = sheet.getCellsBuilder();
		for (List<CellData> row : sheet.getCells().getRows()) {
			for (CellData cell : row) {
				if (cell == null) {
					continue;
				}
				CellReference newRef = referenceTransformer.transform(cell.getReference());
				if (newRef != null) {
					CellData newCell = new CellData(newRef, cell.getValue(), cell.getFormula(), cell.getStyles());
					newCells = newCells.set(newCell.getReference().getRowIndex(), newCell.getReference()
							.getColumnIndex(), newCell);
				}
			}
		}
		sheet.setCells(newCells.build());
	}

	@Override
	public void deleteSheet() throws StorageException, NotFoundException {
		// nothing to do
	}

}
