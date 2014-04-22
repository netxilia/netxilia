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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Holds DB schema.
 * 
 * @author catac
 */
public class DbSchema implements Iterable<DbTable> {
	private String name;
	private final Map<String, DbTable> tables = new LinkedHashMap<String, DbTable>();

	public DbSchema() {
		// empty
	}

	public DbSchema(DbSchema other) {
		name = other.name;
		setTables(other.getTables());
	}

	public String getName() {
		return name;
	}

	@Required
	public void setName(String name) {
		Assert.notNull(name);
		this.name = name;
	}

	/** Get a collection containing the tables in this schema. The collection is a view in the underlying tables map. */
	public Collection<DbTable> getTables() {
		return tables.values();
	}

	/** Get the name-to-table mapping of the tables in this schema. */
	public Map<String, DbTable> getTablesMap() {
		return tables;
	}

	/** Get the DbTable with the givn name */
	public DbTable getTable(String tableName) {
		return tableName != null ? tables.get(tableName.toLowerCase()) : null;
	}

	/** Replace all tables in the schema with the given list of tables */
	public void setTables(Collection<DbTable> replacements) {
		Assert.notNull(replacements);
		this.tables.clear();
		for (DbTable table : replacements) {
			addTable(table);
		}
	}

	/** Add a new table to the schema */
	public void addTable(DbTable table) {
		String tblName = table.getNameUnique();
		if (tables.containsKey(tblName)) {
			throw new IllegalArgumentException("Table " + tblName + " already exists.");
		}
		tables.put(tblName, new DbTable(table));
	}

	/** Replace the table with the same name, with the given one */
	public void setTable(DbTable replacement) {
		tables.put(replacement.getNameUnique(), replacement);
	}

	/** Remove the table with the given name */
	public void removeTable(String tableName) {
		if (tables.remove(tableName.toLowerCase()) == null) {
			throw new IllegalArgumentException("No table " + tableName.toLowerCase() + " in schema " + name);
		}
	}

	@Override
	public Iterator<DbTable> iterator() {
		return tables.values().iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DbSchema[name=").append(name);
		for (DbTable tbl : this) {
			sb.append("\n  => ").append(tbl);
		}
		sb.append("]");
		return sb.toString();
	}
}
