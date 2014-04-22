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

import org.netxilia.api.model.CellData;
import org.netxilia.api.model.SheetFullName;

public class CellEvent extends WorkbookEvent<CellEventType, CellData.Property> {
	private static final long serialVersionUID = 1L;
	private final CellData cell;

	public CellEvent(CellEventType type, SheetFullName sheetName, CellData cell,
			Collection<CellData.Property> properties) {
		super(type, sheetName, properties);
		this.cell = cell;
	}

	public CellData getCell() {
		return cell;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((cell.getReference() == null) ? 0 : cell.getReference().hashCode());
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
		CellEvent other = (CellEvent) obj;
		if (cell.getReference() == null) {
			if (other.cell.getReference() != null) {
				return false;
			}
		} else if (!cell.getReference().equals(other.cell.getReference())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CellEvent [cell=" + cell + ", getProperty()=" + getProperties() + ", getType()=" + getType()
				+ ", sheet=" + getSheetName() + "]";
	}

}
