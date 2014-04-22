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

import org.netxilia.spi.impl.storage.db.ddl.schema.DbSchema;

public class ExtendedDataSource {
	private final DDLUtils ddl;
	private final DbSchema schema;

	public ExtendedDataSource(DDLUtils ddl) throws SQLException {
		this.ddl = ddl;
		this.schema = ddl.reader().loadDbSchema();
	}

	public DDLUtils getDdl() {
		return ddl;
	}

	public DbSchema getSchema() {
		return schema;
	}

	public DataSource getDataSource() {
		return ddl.getDataSource();
	}
}
