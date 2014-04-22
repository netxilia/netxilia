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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.DateValue;

public class ExcelExportService implements IExportService {

	@Override
	public void exportSheetTo(INetxiliaSystem workbookProcessor, SheetFullName sheetName, OutputStream out,
			IProcessingConsole console) throws ExportException, NetxiliaResourceException, NetxiliaBusinessException {
		Workbook poiWorkbook = new HSSFWorkbook();
		Sheet poiSheet = poiWorkbook.createSheet(sheetName.getSheetName());
		ISheet nxSheet = null;
		try {
			nxSheet = workbookProcessor.getWorkbook(sheetName.getWorkbookId()).getSheet(sheetName.getSheetName());

			SheetData nxSheetData = nxSheet.receiveSheet().getNonBlocking();
			for (AreaReference area : nxSheetData.getSpans()) {
				poiSheet.addMergedRegion(new CellRangeAddress(area.getFirstRowIndex(), area.getLastRowIndex(), area
						.getFirstColumnIndex(), area.getLastColumnIndex()));
			}
			// cells
			Matrix<CellData> nxCells = nxSheet.receiveCells(AreaReference.ALL).getNonBlocking();

			int rowIndex = 0;
			for (List<CellData> nxRow : nxCells.getRows()) {
				Row poiRow = poiSheet.createRow(rowIndex);
				for (CellData nxCell : nxRow) {
					if (nxCell != null) {
						Cell poiCell = poiRow.createCell(nxCell.getReference().getColumnIndex());
						try {
							copyCellValue(nxCell, poiCell);
						} catch (Exception ex) {
							if (console != null) {
								console.println("Error " + nxCell.getReference() + ":" + ex);
							}
						}
					}
				}
				rowIndex++;
			}

			// columns
			List<ColumnData> nxColumns = nxSheet.receiveColumns(Range.ALL).getNonBlocking();
			for (int c = 0; c < nxColumns.size(); ++c) {
				ColumnData col = nxColumns.get(c);
				if (col.getWidth() > 0) {
					poiSheet.setColumnWidth(c, PoiUtils.pixel2WidthUnits(col.getWidth()));
				}

				PoiUtils.netxiliaStyle2Poi(col.getStyles(), poiSheet.getWorkbook(), poiSheet.getColumnStyle(c));
			}
		} catch (StorageException e) {
			throw new ExportException(e);
		}

		// close the workbook
		try {
			poiWorkbook.write(out);
		} catch (IOException e) {
			throw new ExportException(e);
		}
	}

	private void copyCellValue(CellData nxCell, Cell poiCell) {
		if (nxCell.getValue() == null) {
			return;
		}
		if (nxCell.getFormula() != null) {
			// remove leading =
			poiCell.setCellFormula(nxCell.getFormula().getFormula().substring(1));
			return;
		}
		switch (nxCell.getValue().getValueType()) {
		case BOOLEAN:
			poiCell.setCellValue(nxCell.getValue().getBooleanValue());
			break;
		case NUMBER:
			poiCell.setCellValue(nxCell.getValue().getNumberValue());
			break;
		case DATE:
			poiCell.setCellValue(nxCell.getValue().getDateValue().toDateTime(DateValue.ORIGIN).toDate());
			break;
		case ERROR:
			// TODO translate errors
			// poiCell.setCellErrorValue(((ErrorValue)nxCell.getValue()).getErrorType());
			break;
		case STRING:
			poiCell.setCellValue(nxCell.getValue().getStringValue());
			break;
		}

		poiCell.setCellStyle(PoiUtils.netxiliaStyle2Poi(nxCell.getStyles(), poiCell.getSheet().getWorkbook()));
	}

}
