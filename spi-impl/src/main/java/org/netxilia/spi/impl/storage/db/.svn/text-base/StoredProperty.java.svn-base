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
package org.netxilia.spi.impl.storage.db;

/**
 * This class represents a property how is stored in the database in the properties table.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class StoredProperty {
	private final String category;
	private final String object;
	private final String property;
	private final String value;

	public StoredProperty(String category, String object, String property, String value) {
		this.category = category;
		this.object = object;
		this.property = property;
		this.value = value;
	}

	public String getCategory() {
		return category;
	}

	public String getObject() {
		return object;
	}

	public String getProperty() {
		return property;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "StoredProperty [category=" + category + ", object=" + object + ", property=" + property + ", value="
				+ value + "]";
	}

}
