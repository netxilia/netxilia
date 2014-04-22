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

import java.util.ArrayList;
import java.util.List;

import org.netxilia.api.model.CellCreator;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.MatrixBuilder;

public class CompleteSheet {
	private SheetData sheetData;
	private List<ColumnData> columns = new ArrayList<ColumnData>();
	private List<RowData> rows = new ArrayList<RowData>();

	private Matrix<CellData> cells = new Matrix<CellData>();

	public SheetData getSheetData() {
		return sheetData;
	}

	public void setSheetData(SheetData sheetData) {
		this.sheetData = sheetData;
	}

	public List<ColumnData> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnData> columns) {
		this.columns = columns;
	}

	public List<RowData> getRows() {
		return rows;
	}

	public void setRows(List<RowData> rows) {
		this.rows = rows;
	}

	public Matrix<CellData> getCells() {
		return cells;
	}

	public MatrixBuilder<CellData> getCellsBuilder() {
		return new MatrixBuilder<CellData>(cells, new CellCreator(sheetData.getFullName().getSheetName(), 0, 0));
	}

	public void setCells(Matrix<CellData> cells) {
		this.cells = cells;
	}

}
