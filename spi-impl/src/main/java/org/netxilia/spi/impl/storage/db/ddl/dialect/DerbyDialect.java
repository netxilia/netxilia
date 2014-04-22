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

import org.apache.commons.lang.ObjectUtils;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;

/**
 * Generate Apache Derby specific SQL
 * 
 * @author catac
 */
public class DerbyDialect extends AbstractSqlDialect {
	@Override
	public String getDbDialect() {
		return "Apache Derby";
	}

	@Override
	protected void appendModifyColumnCommands(List<String> commands, DbTable table, DbColumn oldCol, DbColumn newCol) {
		if (!oldCol.getNameUnique().equals(newCol.getNameUnique())) {
			throw new UnsupportedOperationException("Derby doesn't support renaming columns. "
					+ "Got when trying to update table " + table.getName() + " column " + oldCol + " to " + newCol);
		}
		String alter = "ALTER TABLE " + table.getName() + " ALTER COLUMN " + newCol.getName() + " ";
		if ((oldCol.getDataType() != newCol.getDataType()) //
				|| !ObjectUtils.equals(oldCol.getSize(), newCol.getSize())//
				|| !ObjectUtils.equals(oldCol.getScale(), newCol.getScale())) {
			StringBuilder sb = new StringBuilder(alter);
			sb.append(" SET DATA TYPE ");
			appendSqlColumnType(newCol, sb);
			commands.add(sb.toString());
		}
		if (!ObjectUtils.equals(oldCol.getDefaultValue(), newCol.getDefaultValue())) {
			String defaultValue = newCol.getDefaultValue();
			if (defaultValue != null) {
				commands.add(alter + "SET DEFAULT " + getSqlValue(newCol, defaultValue));
			} else {
				commands.add(alter + "DROP DEFAULT");
			}
		}
		if (oldCol.isNullable() != newCol.isNullable()) {
			commands.add(alter + (newCol.isNullable() ? "" : "NOT ") + "NULL");
		}
	}
}
