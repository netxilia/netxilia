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

import java.util.List;

import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.spi.impl.storage.db.ddl.DDLUtils;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbSchema;
import org.netxilia.spi.impl.storage.db.sql.IConnectionWrapper;
import org.netxilia.spi.impl.storage.db.sql.RowMapper;

/**
 * This is the information that accompanies a workbook
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class WorkbookDbSession implements IConnectionWrapper {
	private final DDLUtils ddl;
	private final IConnectionWrapper jdbc;

	private final DbSchema schema;
	private final WorkbookId workbookId;

	public WorkbookDbSession(WorkbookId workbookId, DDLUtils ddl, DbSchema schema, IConnectionWrapper jdbc) {
		this.ddl = ddl;
		this.jdbc = jdbc;
		this.schema = schema;
		this.workbookId = workbookId;
	}

	public DDLUtils getDdl() {
		return ddl;
	}

	public DbSchema getSchema() {
		return schema;
	}

	public WorkbookId getWorkbookId() {
		return workbookId;
	}

	public void close() {
		jdbc.close();
	}

	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) throws StorageException {
		return jdbc.query(sql, rowMapper, params);
	}

	public int queryForInt(String sql, Object... params) throws StorageException {
		return jdbc.queryForInt(sql, params);
	}

	public int update(String sql, Object... params) throws StorageException {
		return jdbc.update(sql, params);
	}

}
