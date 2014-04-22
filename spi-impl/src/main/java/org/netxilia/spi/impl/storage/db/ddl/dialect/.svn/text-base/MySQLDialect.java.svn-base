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
 * Generate MySQL specific SQL
 * 
 * @author catac
 */
public class MySQLDialect extends AbstractSqlDialect {
	@Override
	public String getDbDialect() {
		return "MySQL";
	}

	@Override
	protected void appendModifyColumnCommands(List<String> commands, DbTable table, DbColumn oldCol, DbColumn newCol) {
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(table.getName());
		sb.append(" CHANGE COLUMN ").append(oldCol.getName()).append(' ').append(newCol.getName());
		appendSqlColumnDefinition(newCol, sb);
		commands.add(sb.toString());
	}

	// @Override
	// protected String getTypeName(DbDataType type) {
	// switch (type) {
	// case INTEGER:
	// return "INT";
	// }
	//
	// return super.getTypeName(type);
	// }
}
