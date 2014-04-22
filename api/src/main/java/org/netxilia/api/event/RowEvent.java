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

import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetFullName;

public class RowEvent extends WorkbookEvent<RowEventType, RowData.Property> {
	private static final long serialVersionUID = 1L;
	private final RowData row;

	public RowEvent(RowEventType type, SheetFullName sheetName, RowData row, Collection<RowData.Property> properties) {
		super(type, sheetName, properties);
		if (row == null) {
			throw new NullPointerException();
		}
		this.row = row;
	}

	public RowData getRow() {
		return row;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + row.getIndex();
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
		RowEvent other = (RowEvent) obj;
		if (row.getIndex() != other.row.getIndex()) {
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
		return "RowEvent [row=" + row + ", sheetName=" + getSheetName() + ", getProperty()=" + getProperties()
				+ ", getType()=" + getType() + "]";
	}

}
