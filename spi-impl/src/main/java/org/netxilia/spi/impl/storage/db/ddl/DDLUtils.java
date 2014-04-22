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

import java.sql.SQLException;

import javax.sql.DataSource;

import org.netxilia.spi.impl.storage.db.ddl.dialect.ISqlDialect;
import org.netxilia.spi.impl.storage.db.ddl.dialect.SqlDialectProvider;

/**
 * The entry point for DDL related operations.
 * 
 * @author catac
 */
public class DDLUtils {
	private final DataSource dataSource;
	private final DDLReader reader;
	private final DDLWriter writer;

	/** Create the DDLUtils reader and writer from the given dataSource. The dialect is detected from DB driver. */
	public DDLUtils(DataSource dataSource) throws SQLException {
		this(dataSource, null);
	}

	/** Create the DDLUtils reader and writer form the given dataSource, forcing the sql dialect to use. */
	public DDLUtils(DataSource dataSource, String forceDbDialect) throws SQLException {
		this.dataSource = dataSource;
		this.reader = new DDLReader(dataSource, forceDbDialect);
		ISqlDialect dialect = SqlDialectProvider.getSqlDialect(reader.getDbDialect());
		this.writer = new DDLWriter(dataSource, dialect);
	}

	/** Get the current dataSource */
	public DataSource getDataSource() {
		return dataSource;
	}

	/** Get the handle for DDL read operations */
	public DDLReader reader() {
		return reader;
	}

	/** Get the handle for DDL write operations */
	public DDLWriter writer() {
		return writer;
	}
}
