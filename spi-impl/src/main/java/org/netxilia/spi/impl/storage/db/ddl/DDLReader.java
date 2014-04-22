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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbDataType;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbSchema;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;

/**
 * Reader for DB schema.
 * 
 * @author catac
 */
public class DDLReader {
	private final static Logger log = Logger.getLogger(DDLReader.class);

	private final DataSource dataSource;
	private String dbDialect;

	public DDLReader(DataSource dataSource) {
		this(dataSource, null);
	}

	public DDLReader(DataSource dataSource, String forceDbDialect) {
		this.dataSource = dataSource;
		this.dbDialect = forceDbDialect;
	}

	public void setForceDbDialect(String forceDbDialect) {
		this.dbDialect = forceDbDialect;
	}

	/** Retrieve the forced of the actual DB's dialect */
	public String getDbDialect() throws SQLException {
		String d = dbDialect;
		if (d == null) {
			d = dbDialect = loadDbDialect();
		}
		return d;
	}

	/** Load DB dialect from the DB itself */
	protected String loadDbDialect() throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			return dmd.getDatabaseProductName();
		} finally {
			conn.close();
		}
	}

	/** Load existing DB Schema. We take all tables in the default schema. */
	public DbSchema loadDbSchema() throws SQLException {
		DbSchema dbSchema = new DbSchema();
		dbSchema.setName("default");
		loadTables(dbSchema);
		return dbSchema;
	}

	/** Load existing DB tables f */
	protected void loadTables(DbSchema dbSchema) throws SQLException {
		Connection conn = dataSource.getConnection();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			if (log.isDebugEnabled()) {
				log.debug("reading metadata");
			}
			ResultSet rs = dmd.getTables(null, null, "%", new String[] { "TABLE" });
			while (rs.next()) {
				DbTable table = new DbTable();
				table.setName(rs.getString("TABLE_NAME"));
				loadColumns(table);
				loadPrimaryKeys(table);
				dbSchema.addTable(table);
				if (log.isDebugEnabled()) {
					log.debug("loaded:" + table.getName());
				}
			}
		} finally {
			conn.close();
		}
	}

	/** Load existing DB columns for the given table. */
	protected void loadColumns(DbTable dbTable) throws SQLException {
		String tableName = dbTable.getName();
		Connection conn = dataSource.getConnection();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			ResultSet rs = dmd.getColumns(null, null, tableName, "%");
			while (rs.next()) {
				DbColumn col = new DbColumn();
				col.setName(rs.getString("COLUMN_NAME"));
				col.setDataType(DbDataType.from(rs.getString("TYPE_NAME")));
				col.setSize((Integer) rs.getObject("COLUMN_SIZE"));
				String defaultValue = (String) rs.getObject("COLUMN_DEF");
				if ("NULL".equals(defaultValue)) {
					defaultValue = null;
				}
				if ((defaultValue != null) && col.getDataType().isQuotedValue()) {
					col.setDefaultValue(defaultValue.substring(1, defaultValue.length() - 1));
				} else {
					col.setDefaultValue(defaultValue);
				}
				col.setNullable("YES".equals(rs.getString("IS_NULLABLE")));
				dbTable.addColumn(col);
			}
		} finally {
			conn.close();
		}
	}

	/** Set the primaryKey flags in the DbColumns of the given DbTable, based on what's in DB. */
	protected void loadPrimaryKeys(DbTable dbTable) throws SQLException {
		String tableName = dbTable.getName();
		Connection conn = dataSource.getConnection();
		try {
			DatabaseMetaData dmd = conn.getMetaData();
			ResultSet rs = dmd.getPrimaryKeys(null, null, tableName);
			// FIXME we should reorder the columns based on the primary keys's KEY_SEQ index
			while (rs.next()) {
				DbColumn col = dbTable.getColumn(rs.getString("COLUMN_NAME"));
				col.setPrimaryKey(true);
			}
		} finally {
			conn.close();
		}
	}
}
