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
package org.netxilia.spi.impl.storage.db.ddl.schema;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Hold all columns in a table.
 * 
 * @author catac
 */
public class DbTable implements Iterable<DbColumn> {
	private String name;
	private final Map<String, DbColumn> columns = new LinkedHashMap<String, DbColumn>();

	public DbTable() {
		// empty
	}

	public DbTable(DbTable other) {
		name = other.name;
		setColumns(other.getColumns());
	}

	public String getName() {
		return name;
	}

	public String getNameUnique() {
		return name != null ? name.toLowerCase() : null;
	}

	@Required
	public void setName(String name) {
		Assert.notNull(name);
		this.name = name;
	}

	/** Get a collection containing the columns of this table. This is a view in the underlying columns map */
	public Collection<DbColumn> getColumns() {
		return columns.values();
	}

	/** Get the name-to-column mapping for all columns in this table */
	public Map<String, DbColumn> getColumnsMap() {
		return columns;
	}

	/**
	 * Get the DbColumn with the given column name
	 * 
	 * @return null if there is no column with the given name
	 */
	public DbColumn getColumn(String columnName) {
		return columnName != null ? columns.get(columnName.toLowerCase()) : null;
	}

	/** Replace all columns in this table with copies for all columns in the given collection. */
	public void setColumns(Collection<DbColumn> replacements) {
		Assert.notNull(replacements);
		this.columns.clear();
		for (DbColumn col : replacements) {
			addColumn(col);
		}
	}

	/** Add the given column as the last in the table */
	public void addColumn(DbColumn newCol) {
		String colName = newCol.getNameUnique();
		if (columns.containsKey(colName)) {
			throw new IllegalArgumentException("Column " + colName + " already exists in table " + name);
		}
		columns.put(colName, new DbColumn(newCol));
	}

	/** Replace (if exists already) the column with the same name, with the given one */
	public void setColumn(DbColumn replacement) {
		columns.put(replacement.getNameUnique(), replacement);
	}

	/** Remove the column with the given name */
	public void removeColumn(String columnName) {
		String nameUnique = columnName.toLowerCase();
		if (columns.remove(nameUnique) == null) {
			throw new IllegalArgumentException("No column " + nameUnique + " in table " + name);
		}
	}

	/** Return true if the table has at least a column which is primary key */
	public boolean hasPrimaryKey() {
		for (DbColumn col : this) {
			if (col.isPrimaryKey()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<DbColumn> iterator() {
		return columns.values().iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : getNameUnique().hashCode());
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		DbTable other = (DbTable) obj;
		return StringUtils.equalsIgnoreCase(getNameUnique(), other.getNameUnique()) //
				&& ObjectUtils.equals(columns, other.columns);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DbTable[name=").append(name);
		for (DbColumn col : this) {
			sb.append("\n    => ").append(col);
		}
		sb.append("]");
		return sb.toString();
	}
}
