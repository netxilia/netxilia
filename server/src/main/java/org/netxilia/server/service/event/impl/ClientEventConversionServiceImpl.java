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
package org.netxilia.server.service.event.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.event.CellEvent;
import org.netxilia.api.event.ColumnEvent;
import org.netxilia.api.event.RowEvent;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SpanTable;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.RichValue;
import org.netxilia.api.value.StringValue;
import org.netxilia.server.service.event.CellClientEvent;
import org.netxilia.server.service.event.CellEventData;
import org.netxilia.server.service.event.ClientEventType;
import org.netxilia.server.service.event.ColumnClientEvent;
import org.netxilia.server.service.event.EventConversionException;
import org.netxilia.server.service.event.IClientEventConversionService;
import org.netxilia.server.service.event.RowClientEvent;
import org.netxilia.server.service.event.SheetClientEvent;
import org.netxilia.server.service.event.WorkbookClientEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This services converts the events from the system to a simpler format usable on the Javascript client site.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ClientEventConversionServiceImpl implements IClientEventConversionService {
	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private IStyleService styleService;

	public INetxiliaSystem getWorkbookProcessor() {
		return workbookProcessor;
	}

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<WorkbookClientEvent> getCellEvents(WorkbookId workbookName, AreaReference areaRef)
			throws EventConversionException {
		ISheet sheet = null;

		try {
			AclPrivilegedMode.set();
			sheet = workbookProcessor.getWorkbook(workbookName).getSheet(areaRef.getSheetName());

			SheetData sheetData = sheet.receiveSheet().getNonBlocking();
			Matrix<CellData> cells = sheet.receiveCells(areaRef).getNonBlocking();
			List<ColumnData> columns = sheet.receiveColumns(areaRef.getColumns()).getNonBlocking();
			List<RowData> rows = sheet.receiveRows(areaRef.getRows()).getNonBlocking();

			SpanTable spans = new SpanTable(sheetData.getSpans());

			List<CellEventData> data = new ArrayList<CellEventData>();
			for (CellData cell : cells) {

				IGenericValue value = cell.getValue();
				if (cell.getFormula() != null) {
					value = new StringValue(cell.getFormula().getFormula());
				}
				// rows and columns are relative to the area start
				int rowPos = cell.getReference().getRowIndex() - areaRef.getFirstRowIndex();
				int colPos = cell.getReference().getColumnIndex() - areaRef.getFirstColumnIndex();

				RichValue formattedValue = styleService.formatCell(workbookName, cell, rows.get(rowPos),
						columns.get(colPos));

				data.add(new CellEventData(cell.getReference().getRowIndex(), cell.getReference().getColumnIndex(),
						value != null ? value.getStringValue() : null, formattedValue.getDisplay(), formattedValue
								.getStyles() != null ? formattedValue.getStyles().toString() : null, spans
								.getRowSpan(cell.getReference()), spans.getColSpan(cell.getReference())));
			}
			return (List) Collections.singletonList(new CellClientEvent(areaRef.getSheetName(), data));
		} catch (Exception e) {
			throw new EventConversionException(e);
		} finally {
			AclPrivilegedMode.clear();
		}
	}

	@Override
	public List<WorkbookClientEvent> convert(CellEvent ev) throws EventConversionException {
		return getCellEvents(ev.getSheetName().getWorkbookId(), new AreaReference(ev.getCell().getReference()));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<WorkbookClientEvent> convert(ColumnEvent columnEvent) throws EventConversionException {
		switch (columnEvent.getType()) {
		case deleted:
			return (List) Collections.singletonList(new ColumnClientEvent(columnEvent.getSheetName().getSheetName(),
					ClientEventType.columnDeleted, columnEvent.getColumn().getIndex(), 0));
		case inserted:
			// if (columnEvent.getColumn().getIndex() == columnEvent.getTotalColumns() - 1) {
			// return null;
			// }
			return (List) Collections.singletonList(new ColumnClientEvent(columnEvent.getSheetName().getSheetName(),
					ClientEventType.columnInserted, columnEvent.getColumn().getIndex(), 0));
		case modified:
			try {
				AclPrivilegedMode.set();

				// for style, formatter and editor info all cells from the column are affected
				List<WorkbookClientEvent> events = new ArrayList<WorkbookClientEvent>();
				events.add(new ColumnClientEvent(columnEvent.getSheetName().getSheetName(),
						ClientEventType.columnModified, columnEvent.getColumn().getIndex(), columnEvent.getColumn()
								.getWidth()));
				if (columnEvent.getProperties() != null
						&& (columnEvent.getProperties().contains("style")
								|| columnEvent.getProperties().contains("format") || columnEvent.getProperties()
								.contains("editorInfo"))) {
					events.addAll(getCellEvents(columnEvent.getSheetName().getWorkbookId(), new AreaReference(
							new CellReference(columnEvent.getSheetName().getSheetName(), CellReference.MAX_ROW_INDEX,
									columnEvent.getColumn().getIndex()))));
				}
				return events;
			} catch (Exception e) {
				throw new EventConversionException(e);
			} finally {
				AclPrivilegedMode.clear();
			}
		default:
			return null;
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<WorkbookClientEvent> convert(RowEvent rowEvent) throws EventConversionException {
		switch (rowEvent.getType()) {
		case deleted:
			return (List) Collections.singletonList(new RowClientEvent(rowEvent.getSheetName().getSheetName(),
					ClientEventType.rowDeleted, rowEvent.getRow().getIndex()));
		case inserted:
			// if (rowEvent.getRow().getIndex() == rowEvent.getTotalRows() - 1) {
			// return null;
			// }
			return (List) Collections.singletonList(new RowClientEvent(rowEvent.getSheetName().getSheetName(),
					ClientEventType.rowInserted, rowEvent.getRow().getIndex()));
		case modified:
			// for style, formatter and editor info all cells from the row are affected
			List<WorkbookClientEvent> events = new ArrayList<WorkbookClientEvent>();
			events.add(new RowClientEvent(rowEvent.getSheetName().getSheetName(), ClientEventType.rowModified, rowEvent
					.getRow().getIndex()));
			if (rowEvent.getProperties() != null
					&& (rowEvent.getProperties().contains("style") || rowEvent.getProperties().contains("format") || rowEvent
							.getProperties().contains("editorInfo"))) {
				events.addAll(getCellEvents(rowEvent.getSheetName().getWorkbookId(), new AreaReference(
						new CellReference(rowEvent.getSheetName().getSheetName(), rowEvent.getRow().getIndex(),
								CellReference.MAX_COLUMN_INDEX))));
			}
			return events;
		default:
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<WorkbookClientEvent> convert(SheetEvent sheetEvent) throws EventConversionException {
		switch (sheetEvent.getType()) {
		case deleted:
			return (List) Collections.singletonList(new SheetClientEvent(sheetEvent.getSheetName().getSheetName(),
					null, null, ClientEventType.sheetDeleted));
		case inserted:
			return (List) Collections.singletonList(new SheetClientEvent(sheetEvent.getSheetName().getSheetName(),
					null, null, ClientEventType.sheetInserted));
		case modified:
			try {
				AclPrivilegedMode.set();
				return (List) Collections.singletonList(new SheetClientEvent(sheetEvent.getSheetName().getSheetName(),
						sheetEvent.getSheet().getAliases(), sheetEvent.getSheet().getSpans(),
						ClientEventType.sheetModified));
			} catch (Exception e) {
				throw new EventConversionException(e);
			} finally {
				AclPrivilegedMode.clear();
			}
		default:
			return null;
		}
	}
}
