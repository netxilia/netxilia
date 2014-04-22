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

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;
import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.BlockCellCommandBuilder;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ColumnCommands;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impexp.IImportService;
import org.netxilia.api.impexp.IProcessingConsole;
import org.netxilia.api.impexp.ImportException;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.value.BooleanValue;
import org.netxilia.api.value.DateValue;
import org.netxilia.api.value.GenericValueUtils;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.NumberValue;
import org.netxilia.api.value.StringValue;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.beans.factory.annotation.Autowired;

public class ExcelImportService extends AbstractImportService implements IImportService {
	private final static Logger log = Logger.getLogger(ExcelImportService.class);
	private final static DateTime EXCEL_START = new DateTime(1904, 1, 1, 0, 0, 0, 0);
	@Autowired
	private IFormulaParser formulaParser;

	@Autowired
	private IStyleService styleService;

	@Autowired
	private IMoreCellCommands moreCellCommands;

	public IFormulaParser getFormulaParser() {
		return formulaParser;
	}

	public void setFormulaParser(IFormulaParser formulaParser) {
		this.formulaParser = formulaParser;
	}

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	public IMoreCellCommands getMoreCellCommands() {
		return moreCellCommands;
	}

	public void setMoreCellCommands(IMoreCellCommands moreCellCommands) {
		this.moreCellCommands = moreCellCommands;
	}

	private ICellCommand copyCell(Cell poiCell, CellReference cellReference, HSSFPalette palette,
			NetxiliaStyleResolver styleResolver) throws FormulaParsingException {

		CellStyle poiStyle = poiCell.getCellStyle();
		Styles styles = PoiUtils.poiStyle2Netxilia(poiStyle,
				poiCell.getSheet().getWorkbook().getFontAt(poiStyle.getFontIndex()), palette, styleResolver);

		IGenericValue value = null;
		Formula formula = null;

		// log.info("CELL TYPE:" + cellReference + " type:" + poiCell.getCellType() + " "
		// + (poiCell.getCellType() == Cell.CELL_TYPE_FORMULA ? poiCell.getCellFormula() : "no"));
		switch (poiCell.getCellType()) {
		// TODO translate errors

		case Cell.CELL_TYPE_STRING:
			value = new StringValue(poiCell.getStringCellValue());
			break;
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(poiCell)) {
				DateTime dt = new DateTime(poiCell.getDateCellValue());
				// TODO decide whether is date or time
				if (dt.isBefore(EXCEL_START)) {
					value = new DateValue(dt.toLocalTime());
				} else if (dt.getMillisOfDay() == 0) {
					value = new DateValue(dt.toLocalDate());
				} else {
					value = new DateValue(dt.toLocalDateTime());
				}
			} else {
				value = new NumberValue(poiCell.getNumericCellValue());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = new BooleanValue(poiCell.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_FORMULA:
			if (poiCell.getCellFormula() != null) {
				formula = formulaParser.parseFormula(new Formula("=" + poiCell.getCellFormula()));
			}
			break;
		}

		if ((styles == null || styles.getItems().isEmpty()) && formula == null
				&& (value == null || value.equals(GenericValueUtils.EMTPY_STRING))) {
			return null;
		}
		return CellCommands.cell(new AreaReference(cellReference), value, formula, styles);
	}

	@Override
	public List<SheetFullName> importSheets(INetxiliaSystem workbookProcessor, WorkbookId workbookName, URL url,
			IProcessingConsole console) throws ImportException {

		try {
			return importSheets(workbookProcessor, workbookName, url.openStream(), console);
		} catch (Exception e) {
			throw new ImportException(url, "Cannot open workbook:" + e, e);
		}
	}

	@Override
	public List<SheetFullName> importSheets(INetxiliaSystem workbookProcessor, WorkbookId workbookName, InputStream is,
			IProcessingConsole console) throws ImportException {
		List<SheetFullName> sheetNames = new ArrayList<SheetFullName>();
		try {
			log.info("Starting import:" + workbookName);
			Workbook poiWorkbook = new HSSFWorkbook(is);
			IWorkbook nxWorkbook = workbookProcessor.getWorkbook(workbookName);
			log.info("Read POI");

			NetxiliaStyleResolver styleResolver = new NetxiliaStyleResolver(
					styleService.getStyleDefinitions(workbookName));

			HSSFPalette palette = ((HSSFWorkbook) poiWorkbook).getCustomPalette();

			for (int s = 0; s < poiWorkbook.getNumberOfSheets(); ++s) {
				Sheet poiSheet = poiWorkbook.getSheetAt(s);

				SheetFullName sheetName = new SheetFullName(workbookName, getNextFreeSheetName(nxWorkbook,
						poiSheet.getSheetName()));
				ISheet nxSheet = null;
				BlockCellCommandBuilder cellCommandBuilder = new BlockCellCommandBuilder();
				try {
					List<CellReference> refreshCells = new ArrayList<CellReference>();

					for (Row poiRow : poiSheet) {
						if (poiRow.getRowNum() % 100 == 0) {
							log.info("importing row #" + poiRow.getRowNum());
						}
						for (Cell poiCell : poiRow) {
							if (nxSheet == null) {
								// lazy creation
								while (true) {
									try {
										nxSheet = nxWorkbook.addNewSheet(sheetName.getSheetName(), SheetType.normal);
										nxSheet.setRefreshEnabled(false);
										break;
									} catch (AlreadyExistsException e) {
										// may happen is simultaneous imports take place
										sheetName = new SheetFullName(workbookName, getNextFreeSheetName(nxWorkbook,
												poiSheet.getSheetName()));
									}
								}
							}

							CellReference ref = new CellReference(sheetName.getSheetName(), poiRow.getRowNum(),
									poiCell.getColumnIndex());
							try {
								ICellCommand cmd = copyCell(poiCell, ref, palette, styleResolver);
								if (cmd != null) {
									cellCommandBuilder.command(cmd);
								}
								if (poiCell.getCellType() == Cell.CELL_TYPE_FORMULA) {
									refreshCells.add(ref);
								}

							} catch (Exception e) {
								if (console != null) {
									console.println("Could import cell " + ref + ":" + poiCell + ":" + e);
								}
								log.error("Could import cell " + ref + ":" + poiCell + ":" + e, e);
							}
						}

						if (poiRow.getRowNum() % 100 == 0 && !cellCommandBuilder.isEmpty()) {
							nxSheet.sendCommandNoUndo(cellCommandBuilder.build());
							cellCommandBuilder = new BlockCellCommandBuilder();
						}
					}

					if (nxSheet == null) {
						// empty sheet
						continue;
					}
					if (!cellCommandBuilder.isEmpty()) {
						nxSheet.sendCommandNoUndo(cellCommandBuilder.build());
					}
					// add the columns after as is not very clear how to get the number of cols in poi
					for (int c = 0; c < nxSheet.getDimensions().getNonBlocking().getColumnCount(); ++c) {
						int width = 50;
						try {
							width = PoiUtils.widthUnits2Pixel(poiSheet.getColumnWidth(c));
							nxSheet.sendCommand(ColumnCommands.width(Range.range(c), width));
						} catch (NullPointerException ex) {
							// ignore it
							// NPE in at org.apache.poi.hssf.model.Sheet.getColumnWidth(Sheet.java:998)
							// defaultColumnWidth can be null !?
						}

						CellStyle poiStyle = poiSheet.getColumnStyle(c);
						if (poiStyle == null) {
							continue;
						}
						Styles styles = PoiUtils.poiStyle2Netxilia(poiStyle,
								poiSheet.getWorkbook().getFontAt(poiStyle.getFontIndex()), palette, styleResolver);
						if (styles != null) {
							nxSheet.sendCommand(ColumnCommands.styles(Range.range(c), styles));
						}
					}

					// merge
					List<AreaReference> spans = new ArrayList<AreaReference>(poiSheet.getNumMergedRegions());
					for (int i = 0; i < poiSheet.getNumMergedRegions(); ++i) {
						CellRangeAddress poiSpan = poiSheet.getMergedRegion(i);
						spans.add(new AreaReference(sheetName.getSheetName(), poiSpan.getFirstRow(), poiSpan
								.getFirstColumn(), poiSpan.getLastRow(), poiSpan.getLastColumn()));
					}
					nxSheet.sendCommand(SheetCommands.spans(spans));

					// refresh all the cells now
					nxSheet.setRefreshEnabled(true);
					nxSheet.sendCommandNoUndo(moreCellCommands.refresh(refreshCells, false));

				} finally {
					if (nxSheet != null) {
						sheetNames.add(sheetName);
					}
				}
			}
		} catch (IOException e) {
			throw new ImportException(null, "Cannot open workbook:" + e, e);
		} catch (StorageException e) {
			throw new ImportException(null, "Error storing sheet:" + e, e);
		} catch (NotFoundException e) {
			throw new ImportException(null, "Cannot find workbook:" + e, e);
		} catch (NetxiliaResourceException e) {
			throw new ImportException(null, e.getMessage(), e);
		} catch (NetxiliaBusinessException e) {
			throw new ImportException(null, e.getMessage(), e);
		}

		return sheetNames;
	}
}
