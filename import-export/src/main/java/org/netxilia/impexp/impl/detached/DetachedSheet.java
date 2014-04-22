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
package org.netxilia.impexp.impl.detached;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.chart.Chart;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;

public class DetachedSheet {
	private SheetType type;

	private String name;
	private Map<Alias, AreaReference> aliases = new HashMap<Alias, AreaReference>();
	private List<Chart> charts = new ArrayList<Chart>();
	private List<AreaReference> spans = new ArrayList<AreaReference>();

	private List<DetachedColumn> columns = new ArrayList<DetachedColumn>();
	private List<DetachedRow> rows = new ArrayList<DetachedRow>();

	public SheetType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Map<Alias, AreaReference> getAliases() {
		return aliases;
	}

	public List<Chart> getCharts() {
		return charts;
	}

	public List<DetachedColumn> getColumns() {
		return columns;
	}

	public List<DetachedRow> getRows() {
		return rows;
	}

	public void setType(SheetType type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAliases(Map<Alias, AreaReference> aliases) {
		this.aliases = aliases;
	}

	public void setCharts(List<Chart> charts) {
		this.charts = charts;
	}

	public void setColumns(List<DetachedColumn> columns) {
		this.columns = columns;
	}

	public void setRows(List<DetachedRow> rows) {
		this.rows = rows;
	}

	public List<AreaReference> getSpans() {
		return spans;
	}

	public void setSpans(List<AreaReference> spans) {
		this.spans = spans;
	}

}
