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
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ColumnCommands;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IColumnCommand;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.command.IRowCommand;
import org.netxilia.api.command.RowCommands;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impexp.IImportService;
import org.netxilia.api.impexp.IProcessingConsole;
import org.netxilia.api.impexp.ImportException;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.storage.IJsonSerializer;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.impexp.impl.detached.DetachedCell;
import org.netxilia.impexp.impl.detached.DetachedColumn;
import org.netxilia.impexp.impl.detached.DetachedRow;
import org.netxilia.impexp.impl.detached.DetachedSheet;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Strings;

public class JsonImportService extends AbstractImportService implements IImportService {
	@Autowired
	private IJsonSerializer serializer;

	@Autowired
	private IGenericValueParseService parseService;

	@Autowired
	private IMoreCellCommands moreCellCommands;

	public IJsonSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(IJsonSerializer serializer) {
		this.serializer = serializer;
	}

	public IGenericValueParseService getParseService() {
		return parseService;
	}

	public void setParseService(IGenericValueParseService parseService) {
		this.parseService = parseService;
	}

	public IMoreCellCommands getMoreCellCommands() {
		return moreCellCommands;
	}

	public void setMoreCellCommands(IMoreCellCommands moreCellCommands) {
		this.moreCellCommands = moreCellCommands;
	}

	@Override
	public List<SheetFullName> importSheets(INetxiliaSystem workbookProcessor, WorkbookId workbookName, URL url,
			IProcessingConsole console) throws ImportException {
		try {
			return importSheets(workbookProcessor, workbookName, url.openStream(), console);
		} catch (IOException e) {
			throw new ImportException(url, "Cannot open workbook:" + e, e);
		}
	}

	@Override
	public List<SheetFullName> importSheets(INetxiliaSystem workbookProcessor, WorkbookId workbookName,
			InputStream inputStream, IProcessingConsole console) throws ImportException {
		List<SheetFullName> sheetNames = new ArrayList<SheetFullName>();
		try {
			IWorkbook nxWorkbook = workbookProcessor.getWorkbook(workbookName);
			DetachedSheet detachedSheet = serializer.deserialize(DetachedSheet.class, IOUtils.toString(inputStream));
			if (detachedSheet == null || detachedSheet.getName() == null) {
				// nothing to do here
				return sheetNames;
			}
			SheetFullName sheetName = new SheetFullName(workbookName, getNextFreeSheetName(nxWorkbook,
					detachedSheet.getName()));

			ISheet nxSheet = null;
			while (true) {
				try {
					nxSheet = nxWorkbook.addNewSheet(sheetName.getSheetName(), detachedSheet.getType());
					nxSheet.setRefreshEnabled(false);
					break;
				} catch (AlreadyExistsException e) {
					// may happen is simultaneous imports take place
					sheetName = new SheetFullName(workbookName, getNextFreeSheetName(nxWorkbook,
							detachedSheet.getName()));
				}
			}

			nxSheet.sendCommand(SheetCommands.sheet(detachedSheet.getAliases(), detachedSheet.getCharts(),
					detachedSheet.getSpans()));

			try {
				List<CellReference> refreshCells = new ArrayList<CellReference>();
				for (int c = 0; c < detachedSheet.getColumns().size(); ++c) {
					DetachedColumn detachedColumn = detachedSheet.getColumns().get(c);
					IColumnCommand cmd = copyColumn(c, detachedColumn);
					nxSheet.sendCommand(cmd);
				}
				for (int r = 0; r < detachedSheet.getRows().size(); ++r) {
					DetachedRow detachedRow = detachedSheet.getRows().get(r);
					IRowCommand cmd = copyRow(r, detachedRow);
					nxSheet.sendCommand(cmd);

					for (int c = 0; c < detachedRow.getCells().size(); ++c) {
						DetachedCell detachedCell = detachedRow.getCells().get(c);
						ICellCommand cellCmd = copyCell(r, c, detachedCell);
						if (cellCmd != null) {
							nxSheet.sendCommand(cellCmd);
						}
						if (Formula.isFormula(detachedCell.getContent())) {
							refreshCells.add(new CellReference(sheetName.getSheetName(), r, c));
						}
					}
				}

				// refresh all the cells now
				nxSheet.setRefreshEnabled(true);
				nxSheet.sendCommandNoUndo(moreCellCommands.refresh(refreshCells, false));
			} finally {
				if (nxSheet != null) {
					sheetNames.add(sheetName);
				}
			}

		} catch (IOException e) {
			throw new ImportException(null, "Cannot open workbook:" + e, e);
		} catch (StorageException e) {
			throw new ImportException(null, "Error storing sheet:" + e, e);
		} catch (NotFoundException e) {
			throw new ImportException(null, "Cannot find workbook:" + e, e);
		}

		return sheetNames;
	}

	private IColumnCommand copyColumn(int index, DetachedColumn detachedColumn) {
		Styles styles = detachedColumn.getStyles();
		int width = 60;
		if (detachedColumn.getWidth() != null) {
			width = detachedColumn.getWidth();
		}
		return ColumnCommands.column(Range.range(index), width, styles);
	}

	private IRowCommand copyRow(int index, DetachedRow detachedRow) {
		Styles styles = detachedRow.getStyles();
		int height = 20;
		if (detachedRow.getHeight() != null) {
			height = detachedRow.getHeight();
		}
		return RowCommands.row(Range.range(index), height, styles);
	}

	private ICellCommand copyCell(int r, int c, DetachedCell detachedCell) {
		if (detachedCell.getStyles() == null && Strings.isNullOrEmpty(detachedCell.getContent())) {
			return null;
		}
		CellReference cellReference = new CellReference(null, r, c);

		Styles styles = null;
		Formula formula = null;
		IGenericValue value = null;

		styles = detachedCell.getStyles();
		if (Formula.isFormula(detachedCell.getContent())) {
			formula = new Formula(detachedCell.getContent());
		} else {
			value = parseService.parse(detachedCell.getContent());
		}
		if (formula != null) {
			return CellCommands.cell(new AreaReference(cellReference), formula, styles);
		}
		return CellCommands.cell(new AreaReference(cellReference), value, styles);
	}

}
