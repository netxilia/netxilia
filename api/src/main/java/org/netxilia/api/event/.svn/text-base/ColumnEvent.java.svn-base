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
package org.netxilia.api.event;

import java.util.Collection;

import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.SheetFullName;

public class ColumnEvent extends WorkbookEvent<ColumnEventType, ColumnData.Property> {

	private static final long serialVersionUID = 1L;
	private final ColumnData column;

	public ColumnEvent(ColumnEventType type, SheetFullName sheetName, ColumnData column,
			Collection<ColumnData.Property> properties) {
		super(type, sheetName, properties);
		this.column = column;
	}

	public ColumnData getColumn() {
		return column;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + column.getIndex();
		result = prime * result + ((getSheetName() == null) ? 0 : getSheetName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ColumnEvent other = (ColumnEvent) obj;
		if (column.getIndex() != other.column.getIndex()) {
			return false;
		}
		if (getSheetName() == null) {
			if (other.getSheetName() != null) {
				return false;
			}
		} else if (!getSheetName().equals(other.getSheetName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ColumnEvent [column=" + column + ", sheetName=" + getSheetName() + ", getProperty()=" + getProperties()
				+ ", getType()=" + getType() + "]";
	}

}
