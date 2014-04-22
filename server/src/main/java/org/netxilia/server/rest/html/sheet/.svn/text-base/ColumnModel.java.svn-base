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

import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.reference.CellReference;

public class ColumnModel {
	private final int width;
	private final int index;
	private final int defaultWidth;
	private final String alias;

	public ColumnModel(ColumnData column, Alias alias, int defaultWidth) {
		this.width = column != null ? column.getWidth() : defaultWidth;
		this.index = column != null ? column.getIndex() : 0;
		this.defaultWidth = defaultWidth;
		this.alias = alias != null ? alias.getAlias() : null;
	}

	public String getLabel() {
		return CellReference.columnLabel(index);
	}

	public int getWidth() {
		return width > 0 ? width : defaultWidth;
	}

	public int getIndex() {
		return index;
	}

	public String getAlias() {
		return alias;
	}

}
