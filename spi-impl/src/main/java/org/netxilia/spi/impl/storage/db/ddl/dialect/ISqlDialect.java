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
package org.netxilia.spi.impl.storage.db.ddl.dialect;

import java.util.List;

import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;

/**
 * Defines the possible SQL queries for all DB-related operations required in the application.
 * 
 * @author catac
 */
public interface ISqlDialect {

	/** Get the dialect implemented by this generator */
	String getDbDialect();

	/** Generate the SQL for creating the table for the given table */
	String sqlCreateTable(DbTable table);

	/** Generate the SQL for dropping the table with the given table */
	String sqlDropTable(String tableName);

	/** Generate the SQL for creating the primary key for the given table */
	String sqlCreatePrimaryKey(DbTable table);

	/** Generate the SQL for dropping the primary key of the given table */
	String sqlDropPrimaryKey(String tableName);

	/** Generate the SQL for adding the specified column to the table */
	List<String> sqlAddColumn(DbTable table, DbColumn newCol);

	/** Generate the SQL for dropping the specified column from the table */
	List<String> sqlDropColumn(DbTable table, String columnName);

	/** Generate the SQL for modifying the specified DB column to what's in newCol */
	List<String> sqlModifyColumn(DbTable table, DbColumn newCol);
}
