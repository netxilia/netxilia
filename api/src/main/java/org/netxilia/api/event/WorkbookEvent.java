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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;

import org.netxilia.api.model.SheetFullName;

/**
 * This is the base class for all the other event types that can occur within a Workbook.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 * @param <EventType>
 */
abstract public class WorkbookEvent<EventType, PropertyType> extends EventObject {

	private static final long serialVersionUID = 1L;
	private final EventType type;
	private final SheetFullName sheetName;
	/**
	 * this is the name of the modified property. is null for all the other events
	 */
	private final Collection<PropertyType> properties;

	public WorkbookEvent(EventType type, SheetFullName sheetName, Collection<PropertyType> properties) {
		// TODO add source
		super("nosource");
		this.type = type;
		this.sheetName = sheetName;
		this.properties = properties != null ? new ArrayList<PropertyType>(properties) : null;
	}

	public EventType getType() {
		return type;
	}

	public SheetFullName getSheetName() {
		return sheetName;
	}

	public Collection<PropertyType> getProperties() {
		if (properties == null) {
			return null;
		}
		return Collections.unmodifiableCollection(properties);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((sheetName == null) ? 0 : sheetName.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WorkbookEvent other = (WorkbookEvent) obj;
		if (properties == null) {
			if (other.properties != null) {
				return false;
			}
		} else if (!properties.equals(other.properties)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (sheetName == null) {
			if (other.sheetName != null) {
				return false;
			}
		} else if (!sheetName.equals(other.sheetName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "WorkbookEvent [properties=" + properties + ", type=" + type + ", sheetName=" + sheetName + "]";
	}

}
