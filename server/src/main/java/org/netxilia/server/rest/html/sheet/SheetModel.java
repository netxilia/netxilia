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
package org.netxilia.server.rest.html.sheet;

import java.util.List;
import java.util.Map;

import org.netxilia.api.chart.Chart;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.storage.IJsonSerializer;

public class SheetModel {
	private final boolean readOnly;
	private final SheetFullName sheetFullName;
	private final Map<Alias, AreaReference> aliases;
	private final List<Chart> charts;
	private final List<RowModel> rows;
	private final List<ColumnModel> columns;
	private final List<AreaReference> spans;

	/**
	 * should match the padding for cells in the CSS file
	 */
	private static final int HORIZ_PADDING = 4;
	/**
	 * should match the border for cells in the CSS file
	 */
	private static final int HORIZ_BORDER = 1;

	private final int totalWidth;

	private final int totalRows;
	private final int pageSize;

	private final IJsonSerializer jsonSerializer;
	private final boolean overviewMode;

	public SheetModel(IJsonSerializer jsonSerializer, boolean overviewMode, SheetFullName sheetFullName,
			List<RowModel> rows, List<ColumnModel> columns,//
			Map<Alias, AreaReference> aliases, List<Chart> charts, //
			List<AreaReference> spans, int totalRows, int pageSize, boolean readOnly) {
		this.rows = rows;
		this.columns = columns;
		this.sheetFullName = sheetFullName;
		int w = 0;
		for (ColumnModel c : columns) {
			w += c.getWidth() + HORIZ_PADDING + HORIZ_BORDER;
		}
		totalWidth = w;
		this.totalRows = totalRows;
		this.pageSize = pageSize;
		this.aliases = aliases;
		this.charts = charts;
		this.jsonSerializer = jsonSerializer;
		this.overviewMode = overviewMode;
		this.spans = spans;
		this.readOnly = readOnly;
	}

	public String getName() {
		return sheetFullName.getSheetName();
	}

	public String getWorkbookName() {
		return sheetFullName.getWorkbookName();
	}

	public int getTotalWidth() {
		return totalWidth;
	}

	public List<RowModel> getRows() {
		return rows;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageCount() {
		return totalRows > 0 ? (totalRows - 1) / pageSize + 1 : 0;
	}

	public Map<Alias, AreaReference> getAliases() {
		return aliases;
	}

	public String getAliasesJson() {
		try {
			return jsonSerializer.serialize(aliases);
		} catch (StorageException e) {
			return null;
		}
	}

	public String getChartsJson() {
		try {
			return jsonSerializer.serialize(charts);
		} catch (StorageException e) {
			return null;
		}
	}

	public List<Chart> getCharts() {
		return charts;
	}

	public boolean isOverviewMode() {
		return overviewMode;
	}

	public List<AreaReference> getSpans() {
		return spans;
	}

	public String getSpansJson() {
		try {
			return jsonSerializer.serialize(spans);
		} catch (StorageException e) {
			return null;
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

}
