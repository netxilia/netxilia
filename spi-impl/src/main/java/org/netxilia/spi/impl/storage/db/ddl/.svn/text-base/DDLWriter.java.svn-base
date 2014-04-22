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
package org.netxilia.spi.impl.storage.db.ddl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.netxilia.spi.impl.storage.db.ddl.dialect.ISqlDialect;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbSchema;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.springframework.util.Assert;

/**
 * Writer for DB schema.
 * 
 * @author catac
 */
public class DDLWriter {
	private final static Logger logger = Logger.getLogger(DDLWriter.class);

	private final DataSource dataSource;
	private final ISqlDialect sqlDialect;

	public DDLWriter(DataSource dataSource, ISqlDialect sqlDialect) {
		this.dataSource = dataSource;
		this.sqlDialect = sqlDialect;
	}

	/** Modify the current schema to */
	public void modifySchema(DbSchema crtSchema, DbSchema newSchema, boolean allowDropTables, boolean allowDropColumns)
			throws SQLException {
		executeDDLQueries(diffSchemas(crtSchema, newSchema, allowDropTables, allowDropColumns));
	}

	/** Compare the two tables and generate the commands corresponding to the modifications */
	protected List<String> diffSchemas(DbSchema crtSchema, DbSchema newSchema, boolean allowDropTables,
			boolean allowDropColumns) {
		List<String> commands = new ArrayList<String>();

		DbSchema tmpSchema = new DbSchema(crtSchema);
		Map<String, DbTable> tmpTables = new LinkedHashMap<String, DbTable>();
		for (DbTable ct : crtSchema.getTables()) {
			tmpTables.put(ct.getNameUnique(), ct);
		}

		for (DbTable nt : newSchema.getTables()) {
			DbTable ct = tmpTables.remove(nt.getNameUnique());
			if (ct == null) {
				commands.add(sqlDialect.sqlCreateTable(nt));
				tmpSchema.addTable(nt);
			} else if (!ct.equals(nt)) {
				commands.addAll(diffTables(ct, nt, allowDropColumns));
				tmpSchema.setTable(nt);
			}
		}

		for (DbTable ct : tmpTables.values()) {
			if (allowDropTables) {
				commands.add(sqlDialect.sqlDropTable(ct.getName()));
			}
			tmpSchema.removeTable(ct.getName());
		}
		return commands;
	}

	/** Create the provided table structure and add it to the given schema. */
	public void createTable(DbSchema schema, DbTable newTable) throws SQLException {
		executeDDLQuery(sqlDialect.sqlCreateTable(newTable));
		// add to schema if query succeeds
		schema.addTable(newTable);
	}

	/** Drop the table with the given name and remove it from the provided schema. */
	public void dropTable(DbSchema schema, String tableName) throws SQLException {
		executeDDLQuery(sqlDialect.sqlDropTable(tableName));
		// remove from schema if query succeeds
		schema.removeTable(tableName);
	}

	/**
	 * Modify the existing table in the schema to have the same structure as the given table. If allowDropColumns is
	 * false, even if some columns should be dropped, they are not actually dropped in DB. Note that the modified table
	 * in the given schema is updated to the given newTable, even if allowDropColumns is false.
	 */
	public void modifyTable(DbSchema schema, DbTable newTable, boolean allowDropColumns) throws SQLException {
		DbTable crtTable = schema.getTable(newTable.getName());
		if (crtTable != null) {
			executeDDLQueries(diffTables(crtTable, newTable, allowDropColumns));
			// replace the table if queries succeed
			schema.setTable(newTable);
		} else {
			createTable(schema, newTable);
		}
	}

	/** Compare the two tables and generate the commands corresponding to the modifications */
	protected List<String> diffTables(DbTable crtTable, DbTable newTable, boolean allowDropColumns) {
		List<String> commands = new ArrayList<String>();

		DbTable tmpTable = new DbTable(crtTable);
		Map<String, DbColumn> tmpCols = new LinkedHashMap<String, DbColumn>();
		for (DbColumn cc : crtTable.getColumns()) {
			tmpCols.put(cc.getNameUnique(), cc);
		}

		boolean updatePK = false;
		for (DbColumn nc : newTable.getColumns()) {
			DbColumn cc = tmpCols.remove(nc.getNameUnique());
			if (cc == null) {
				commands.addAll(sqlDialect.sqlAddColumn(crtTable, nc));
				tmpTable.addColumn(nc);
				updatePK |= nc.isPrimaryKey();
			} else if (!cc.equals(nc)) {
				commands.addAll(sqlDialect.sqlModifyColumn(crtTable, nc));
				tmpTable.setColumn(nc);
				updatePK |= cc.isPrimaryKey() || nc.isPrimaryKey();
			}
		}
		for (DbColumn cc : tmpCols.values()) {
			if (allowDropColumns) {
				commands.addAll(sqlDialect.sqlDropColumn(crtTable, cc.getName()));
				updatePK |= cc.isPrimaryKey();
			}
			tmpTable.removeColumn(cc.getName());
		}

		Assert.isTrue(newTable.equals(tmpTable), "Bug in handling tables diff");

		if (updatePK && crtTable.hasPrimaryKey()) {
			commands.add(0, sqlDialect.sqlDropPrimaryKey(crtTable.getName()));
		}
		if (updatePK && newTable.hasPrimaryKey()) {
			commands.add(sqlDialect.sqlCreatePrimaryKey(newTable));
		}
		return commands;
	}

	/** Add the specified column to the table */
	public void addColumn(DbTable table, DbColumn newCol) throws SQLException {
		List<String> commands = sqlDialect.sqlAddColumn(table, newCol);
		if (newCol.isPrimaryKey()) {
			if (table.hasPrimaryKey()) {
				commands.add(0, sqlDialect.sqlDropPrimaryKey(table.getName()));
			}
			DbTable newTable = new DbTable(table);
			newTable.addColumn(newCol);
			commands.add(sqlDialect.sqlCreatePrimaryKey(newTable));
		}
		executeDDLQueries(commands);
		// add to the original table only if queries succeed
		table.addColumn(newCol);
	}

	/** Drop the specified column from the given table */
	public void dropColumn(DbTable table, String columnName) throws SQLException {
		List<String> commands = sqlDialect.sqlDropColumn(table, columnName);
		DbColumn col = table.getColumn(columnName);
		if (col.isPrimaryKey()) {
			commands.add(0, sqlDialect.sqlDropPrimaryKey(table.getName()));
			DbTable newTable = new DbTable(table);
			newTable.removeColumn(columnName);
			if (newTable.hasPrimaryKey()) {
				commands.add(sqlDialect.sqlCreatePrimaryKey(newTable));
			}
		}
		executeDDLQueries(commands);
		// remove from the original table only if queries succeed
		table.removeColumn(columnName);
	}

	/** Modify the specified DB column to correspond with the one in the given table. */
	public void modifyColumn(DbTable table, DbColumn newCol) throws SQLException {
		List<String> commands = sqlDialect.sqlModifyColumn(table, newCol);
		if (commands.size() == 0) {
			return; // column hasn't changed
		}
		DbColumn oldCol = table.getColumn(newCol.getName());
		if (oldCol.isPrimaryKey() || newCol.isPrimaryKey()) {
			if (table.hasPrimaryKey()) {
				commands.add(0, sqlDialect.sqlDropPrimaryKey(table.getName()));
			}
			DbTable newTable = new DbTable(table);
			newTable.setColumn(newCol);
			if (newTable.hasPrimaryKey()) {
				commands.add(sqlDialect.sqlCreatePrimaryKey(newTable));
			}
		}
		executeDDLQueries(commands);
		// update the original table only if queries succeed
		table.setColumn(newCol);
	}

	/** Execute the given DDL query */
	protected void executeDDLQuery(String query) throws SQLException {
		logger.info("DDLQuery: " + query);
		Connection conn = dataSource.getConnection();
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
		} finally {
			conn.close();
		}
	}

	/** Execute the given DDL queries */
	protected void executeDDLQueries(List<String> queryList) throws SQLException {
		if (queryList.size() == 0) {
			return;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("DDLQueries [");
			sb.append(queryList.size()).append("]");
			for (String query : queryList) {
				sb.append("\n  => ").append(query);
			}
			logger.info(sb.toString());
		}
		Connection conn = dataSource.getConnection();
		try {
			for (String query : queryList) {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(query);
			}
		} finally {
			conn.close();
		}
	}
}
