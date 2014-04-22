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

import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;

/**
 * The information needed to store the sheet in a database.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DbSheetStorageInfo {
	public enum Property {
		dbTableName, type
	}

	private final SheetId id;
	/**
	 * This is the name of the table containing the data for this sheet. It can be generated or the name of an already
	 * existing table
	 */
	private final String dbTableName;

	private final SheetType type;

	private final SheetFullName fullName;

	public DbSheetStorageInfo(SheetId id, SheetFullName fullName, SheetType type, String dbTableName) {
		this.id = id;
		this.dbTableName = dbTableName;
		this.type = type;
		this.fullName = fullName;
	}

	public String getDbTableName() {
		return dbTableName;
	}

	public SheetId getId() {
		return id;
	}

	public SheetType getType() {
		return type;
	}

	public SheetFullName getFullName() {
		return fullName;
	}

}
