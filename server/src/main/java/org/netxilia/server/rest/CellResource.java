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
package org.netxilia.server.rest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IColumnCommand;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.command.IMoreColumnCommands;
import org.netxilia.api.command.IMoreRowCommands;
import org.netxilia.api.command.IRowCommand;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.StyleApplyMode;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SpanTable;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.operation.ISheetOperations;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.server.service.user.WindowIndex;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/cells")
public class CellResource extends AbstractResource {
	// private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CellResource.class);
	@Autowired
	private IStyleService styleService;

	@Context
	private HttpServletRequest request;

	@Autowired
	private IMoreCellCommands moreCellCommands;

	@Autowired
	private IMoreRowCommands moreRowCommands;

	@Autowired
	private IMoreColumnCommands moreColumnCommands;

	@Autowired
	private IGenericValueParseService parserService;

	@Autowired
	private ISheetOperations sheetOperations;

	private long longOperationTimeout = 60;

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	public IGenericValueParseService getParserService() {
		return parserService;
	}

	public void setParserService(IGenericValueParseService parserService) {
		this.parserService = parserService;
	}

	public ICellCommand paste(AreaReference ref, String[][] pasteData, CellReference from) {
		return moreCellCommands.paste(ref, pasteData, from);
	}

	public long getLongOperationTimeout() {
		return longOperationTimeout;
	}

	public void setLongOperationTimeout(long longOperationTimeout) {
		this.longOperationTimeout = longOperationTimeout;
	}

	@GET
	@Path("/{workbook}/{ref}")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML })
	public Matrix<CellData> getCell(@PathParam("workbook") WorkbookId workbookName, @PathParam("ref") AreaReference ref)
			throws NetxiliaBusinessException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(ref.getSheetName());
		return sheet.receiveCells(ref).getNonBlocking();
	}

	@GET
	@Path("/{workbook}/{ref}/json")
	@Produces({ MediaType.APPLICATION_JSON })
	public CellData getCellJson(@PathParam("workbook") WorkbookId workbookName, @PathParam("ref") AreaReference ref)
			throws NetxiliaBusinessException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(ref.getSheetName());
		return sheet.receiveCells(ref).getNonBlocking().get(0, 0);
	}

	@POST
	@Path("/{workbook}/{ref}/value")
	public void setValue(@PathParam("workbook") WorkbookId workbookName, @PathParam("ref") AreaReference ref,
			@FormParam("value") String value, @HeaderParam("nxwindow") WindowIndex nxWindowId)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		String theValue = value;
		if ("".equals(theValue)) {
			theValue = null;
		}
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(ref.getSheetName());
		ICellCommand cmd;
		if (Formula.isFormula(theValue)) {
			cmd = CellCommands.formula(ref, new Formula(theValue));
		} else {
			IGenericValue genericValue = parserService.parse(theValue);
			cmd = CellCommands.value(ref, genericValue);
		}
		executeCommand(sheet, cmd, checkWindowId(request, nxWindowId));
	}

	@POST
	@Path("/{workbook}/{ref}/style")
	public void setStyle(@PathParam("workbook") WorkbookId workbookName, @PathParam("ref") AreaReference ref,
			@FormParam("style") Styles styles, @HeaderParam("nxwindow") WindowIndex nxWindowId)
			throws NetxiliaResourceException, NetxiliaBusinessException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(ref.getSheetName());
		ICellCommand cmd = CellCommands.styles(ref, styles);
		executeCommand(sheet, cmd, checkWindowId(request, nxWindowId));
	}

	@POST
	@Path("/{workbook}/{ref}/style/apply")
	public void applyStyle(@PathParam("workbook") WorkbookId workbookName, @PathParam("ref") AreaReference ref,
			@FormParam("style") Styles styles, @FormParam("mode") @DefaultValue("add") StyleApplyMode mode,
			@HeaderParam("nxwindow") WindowIndex nxWindowId) throws NetxiliaResourceException,
			NetxiliaBusinessException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(ref.getSheetName());

		if (ref.isFullColumn()) {
			IColumnCommand cmd = moreColumnCommands.applyStyles(workbookName, ref.getColumns(), styles, mode);
			sheet.sendCommand(cmd);
		} else if (ref.isFullRow()) {
			IRowCommand cmd = moreRowCommands.applyStyles(workbookName, ref.getRows(), styles, mode);
			sheet.sendCommand(cmd);
		} else {
			ICellCommand cmd = moreCellCommands.applyStyles(workbookName, ref, styles, mode);
			executeCommand(sheet, cmd, checkWindowId(request, nxWindowId));
		}
	}

	@PUT
	@Path("/{workbook}/{ref}/merge")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML })
	public void merge(@PathParam("workbook") WorkbookId workbookName, @PathParam("ref") AreaReference ref,
			@HeaderParam("nxwindow") WindowIndex nxWindowId) throws NetxiliaResourceException,
			NetxiliaBusinessException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(ref.getSheetName());
		SheetData sheetData = sheet.receiveSheet().getNonBlocking();
		SpanTable spans = new SpanTable(sheetData.getSpans());
		spans.toggleSpan(ref);

		sheet.sendCommand(SheetCommands.spans(spans.getSpans()));
	}

	/**
	 * Replicate the content of the "from" cell in each cell of the "to" area. Formulas containing relative references
	 * are modified accordingly.
	 * 
	 * @param from
	 * @param to
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	@POST
	@Path("/{workbook}/replicate/{from}/{to}")
	public void replicate(@PathParam("workbook") WorkbookId workbookName, @PathParam("from") CellReference from,
			@PathParam("to") AreaReference to, @HeaderParam("nxwindow") WindowIndex nxWindowId)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(from.getSheetName());
		CellData source = sheet.receiveCell(from).getNonBlocking();
		if (to.getSheetName() == null) {
			to = to.withSheetName(from.getSheetName());
		}
		ICellCommand cmd = moreCellCommands.copyContent(to, source);
		executeCommand(sheet, cmd, checkWindowId(request, nxWindowId));

	}

	/**
	 * Copy the content of the "from" area into the "to" area. If "to" area is smaller on one dimension only the
	 * selected cell will be filled.
	 * 
	 * It the "to" are is larger on one dimension, the remaining positions are not touched.
	 * 
	 * @param from
	 * @param to
	 * @throws StorageException
	 */
	@POST
	@Path("/{workbook}/copy/{from}/{to}")
	public void copy(@PathParam("workbook") WorkbookId workbookName, @PathParam("from") AreaReference from,
			@PathParam("to") AreaReference to, @HeaderParam("nxwindow") WindowIndex nxWindowId)
			throws NotFoundException, StorageException {
		// not implemented yet

	}

	@PUT
	@Path("/{workbook}/move/{from}/{to}")
	public void move(@PathParam("workbook") WorkbookId workbookName, @PathParam("from") AreaReference from,
			@PathParam("to") CellReference to, @HeaderParam("nxwindow") WindowIndex nxWindowId)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(from.getSheetName());
		ICellCommand cmd = CellCommands.moveContent(from, to);
		executeCommand(sheet, cmd, checkWindowId(request, nxWindowId));
	}

	// private void moveCells(ISheet sheet, AreaReference from, CellReference to) {
	// Sheet fromSheet = (Sheet) getSheet(from.getTopLeft().getSheetName());
	//
	// Sheet toSheet = (Sheet) getSheet(to.getSheetName());
	// aclService.checkPermission(toSheet, Permission.write);

	// for (CellReference ref : from) {
	// IWriteableCell fromCell = sheet.getOrCreateCell(ref);
	// CellReference toCellRef = new CellReference(to.getSheetName(), to.getRowIndex() + fromCell.getRowIndex()
	// - from.getFirstRowIndex(), to.getColumnIndex() + fromCell.getColumnIndex()
	// - from.getFirstColumnIndex());
	// IWriteableCell toCell = sheet.getOrCreateCell(toCellRef.getRowIndex(), toCellRef.getColumnIndex());
	// toCell.copyContent(fromCell);
	// fromCell.setContent(null, null);
	// }
	// from area is now empty

	// AreaReference modifiedArea = new AreaReference(to, new CellReference(shhet.getName(), to.getRowIndex()
	// + from.getLastRowIndex() - from.getFirstRowIndex(), to.getColumnIndex() + from.getLastColumnIndex()
	// - from.getFirstColumnIndex()));
	// return modifiedArea;
	// }

	/**
	 * Pastes the given string into cells starting on the given cell. The string is supposed to have the following
	 * format:
	 * 
	 * a1[tab]b1[tab]c1[enter]
	 * 
	 * a2[tab]b2[tab]c2[enter]
	 * 
	 * ...
	 * 
	 * where [tab] is a tab (\t) character and [enter] (\n) character. This is the usual format of a Excel cell area
	 * copied to clipboard.
	 * 
	 * @param to
	 * @param pastedString
	 * @param nxWindowId
	 * @return the affected area (having its top left corner the "to" cell
	 * @throws NetxiliaBusinessException
	 * @throws IllegalArgumentException
	 * @throws NetxiliaResourceException
	 */
	private void paste(ISheet sheet, CellReference to, String pastedString, CellReference from, WindowIndex nxWindowId)
			throws NetxiliaResourceException, IllegalArgumentException, NetxiliaBusinessException {

		String[] rows = pastedString.split("\n");
		int rowCount = rows.length;
		if (rows[rows.length - 1].length() == 0) {
			rowCount--;
		}

		String[][] pasteData = new String[rowCount][];
		int colCount = 0;
		for (int r = 0; r < rowCount; ++r) {
			pasteData[r] = rows[r].split("\t");
			colCount = Math.max(colCount, pasteData[r].length);
		}

		AreaReference modifiedArea = new AreaReference(to, new CellReference(sheet.getName(), to.getRowIndex()
				+ rowCount - 1, to.getColumnIndex() + colCount - 1));
		executeCommand(sheet, moreCellCommands.paste(modifiedArea, pasteData, from), checkWindowId(request, nxWindowId));
	}

	@POST
	@Path("/{workbook}/paste/{from}/{to}")
	public void paste(@PathParam("workbook") WorkbookId workbookName, @PathParam("from") CellReference from,
			@PathParam("to") CellReference to, @FormParam("value") String value,
			@HeaderParam("nxwindow") WindowIndex nxWindowId) throws NetxiliaResourceException,
			IllegalArgumentException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(from.getSheetName());
		// to have good URLs if the paste comes with no reference cell (e.g from external source) than the form
		// parameter is the same as the source
		paste(sheet, to, value, from.equals(to) ? null : from, nxWindowId);

	}

	/**
	 * append the given value to the last row of the given sheet. The value is in the format of a pasted content: ie tab
	 * cell in the same row are tab-delimited and rows are \n delimited.
	 * 
	 * @param workbookName
	 * @param sheetName
	 * @param value
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	@PUT
	@Path("/{workbook}/{sheet}/append")
	public void append(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@FormParam("value") String value, @HeaderParam("nxwindow") WindowIndex nxWindowId)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		CellReference to = new CellReference(sheet.getName(), sheet.getDimensions().getNonBlocking().getRowCount(), 0);
		paste(sheet, to, value, null, nxWindowId);
	}

	@GET
	@Path("/{workbook}/{sheet}/filter")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML })
	public List<Integer> filter(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@QueryParam("filter") Formula filter) throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		return sheetOperations.filter(sheet, filter).getNonBlocking(longOperationTimeout, TimeUnit.SECONDS);

	}

	@GET
	@Path("/{workbook}/{sheet}/find")
	@Produces(MediaType.APPLICATION_JSON)
	public CellReference find(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@QueryParam("startRef") CellReference startRef, @QueryParam("searchText") Formula searchText)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		return sheetOperations.find(sheet, startRef, searchText).getNonBlocking(longOperationTimeout, TimeUnit.SECONDS);

	}
}
