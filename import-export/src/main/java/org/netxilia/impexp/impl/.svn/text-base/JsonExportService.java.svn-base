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
package org.netxilia.impexp.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impexp.ExportException;
import org.netxilia.api.impexp.IExportService;
import org.netxilia.api.impexp.IProcessingConsole;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.storage.IJsonSerializer;
import org.netxilia.api.utils.Matrix;
import org.netxilia.impexp.impl.detached.DetachedCell;
import org.netxilia.impexp.impl.detached.DetachedColumn;
import org.netxilia.impexp.impl.detached.DetachedRow;
import org.netxilia.impexp.impl.detached.DetachedSheet;
import org.springframework.beans.factory.annotation.Autowired;

public class JsonExportService implements IExportService {
	@Autowired
	private IJsonSerializer serializer;

	public IJsonSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(IJsonSerializer serializer) {
		this.serializer = serializer;
	}

	@Override
	public void exportSheetTo(INetxiliaSystem workbookProcessor, SheetFullName sheetName, OutputStream out,
			IProcessingConsole console) throws ExportException, NetxiliaResourceException, NetxiliaBusinessException {
		ISheet nxSheet = null;
		try {
			nxSheet = workbookProcessor.getWorkbook(sheetName.getWorkbookId()).getSheet(sheetName.getSheetName());

			SheetData sheetData = nxSheet.receiveSheet().getNonBlocking();

			DetachedSheet detachedSheet = new DetachedSheet();
			detachedSheet.setAliases(sheetData.getAliases());
			detachedSheet.setName(nxSheet.getName());
			detachedSheet.setType(nxSheet.getType());
			detachedSheet.setCharts(sheetData.getCharts());
			detachedSheet.setSpans(sheetData.getSpans());

			Matrix<CellData> nxCells = nxSheet.receiveCells(AreaReference.ALL).getNonBlocking();

			List<ColumnData> columns = nxSheet.receiveColumns(Range.ALL).getNonBlocking();
			for (ColumnData nxColumn : columns) {
				detachedSheet.getColumns().add(detachedColumn(nxColumn));
			}

			List<RowData> rows = nxSheet.receiveRows(Range.ALL).getNonBlocking();
			for (RowData nxRow : rows) {
				detachedSheet.getRows().add(detachedRow(nxRow, nxCells));
			}

			String json = serializer.serialize(detachedSheet);
			out.write(json.getBytes());
		} catch (StorageException e) {
			throw new ExportException(e);
		} catch (IOException e) {
			throw new ExportException(e);
		}

	}

	private DetachedRow detachedRow(RowData nxRow, Matrix<CellData> nxCells) {
		DetachedRow detachedRow = new DetachedRow();
		detachedRow.setStyles(nxRow.getStyles());
		if (nxRow.getHeight() != DetachedRow.DEFAULT_HEIGHT) {
			detachedRow.setHeight(nxRow.getHeight());
		}
		for (CellData nxCell : nxCells.getRow(nxRow.getIndex())) {
			detachedRow.getCells().add(detachedCell(nxCell));
		}
		return detachedRow;
	}

	private DetachedCell detachedCell(CellData nxCell) {
		if (nxCell == null) {
			return null;
		}
		DetachedCell detachedCell = new DetachedCell();
		detachedCell.setStyles(nxCell.getStyles());

		if (nxCell.getFormula() != null) {
			detachedCell.setContent(nxCell.getFormula().getFormula());
		} else if (nxCell.getValue() != null && !"".equals(nxCell.getValue().getStringValue())) {
			detachedCell.setContent(nxCell.getValue().getStringValue());
		}
		return detachedCell;
	}

	private DetachedColumn detachedColumn(ColumnData nxColumn) {
		DetachedColumn detachedColumn = new DetachedColumn();
		detachedColumn.setStyles(nxColumn.getStyles());
		if (nxColumn.getWidth() != DetachedColumn.DEFAULT_WIDTH) {
			detachedColumn.setWidth(nxColumn.getWidth());
		}
		return detachedColumn;
	}
}
