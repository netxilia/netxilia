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

import java.util.ArrayList;
import java.util.List;

import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbDataType;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;

/**
 * Base class for SQL generators.
 * 
 * @author catac
 */
public abstract class AbstractSqlDialect implements ISqlDialect {

	@Override
	public String sqlCreateTable(DbTable table) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(table.getName()).append('(');
		boolean first = true;
		for (DbColumn col : table.getColumns()) {
			if (!first) {
				sb.append(", ");
			}
			appendSqlColumnDefinition(col, sb);
			first = false;
		}
		if (!first) {
			sb.append(", ");
		}
		appendSqlPrimaryKeyConstraint(table, sb);
		sb.append(")");
		return sb.toString();
	}

	/** Append the column's name, type and if the case, its null-ability constraint */
	protected void appendSqlColumnDefinition(DbColumn column, StringBuilder sb) {
		sb.append(column.getName()).append(' ');
		appendSqlColumnType(column, sb);

		if (!column.isNullable()) {
			sb.append(" NOT NULL");
		} else {
			sb.append(" DEFAULT ").append(getSqlValue(column, column.getDefaultValue()));
		}
	}

	/** Append the given value, based on column's data type isQuotedValue() */
	protected String getSqlValue(DbColumn column, String value) {
		if (value == null) {
			return "NULL";
		}
		if (column.getDataType().isQuotedValue()) {
			return "'" + value + "'";
		}
		return value;
	}

	/** Append the column type and precision and scale, if they are defined */
	protected void appendSqlColumnType(DbColumn column, StringBuilder sb) {
		sb.append(getTypeName(column.getDataType()));
		if (column.getSize() != null) {
			sb.append('(').append(column.getSize());
			if (column.getScale() != null) {
				sb.append(',').append(column.getScale());
			}
			sb.append(')');
		}
	}

	/** Allows dialects to provide their own name for a DbDataType */
	protected String getTypeName(DbDataType type) {
		return type.name();
	}

	/** Append the PK clause, if the table has a primary key */
	protected void appendSqlPrimaryKeyConstraint(DbTable table, StringBuilder sb) {
		int origLen = sb.length();
		boolean hasPk = false;
		sb.append("CONSTRAINT pk_").append(table.getName()).append(" PRIMARY KEY(");
		for (DbColumn col : table.getColumns()) {
			if (col.isPrimaryKey()) {
				if (hasPk) {
					sb.append(", ");
				}
				sb.append(col.getName());
				hasPk = true;
			}
		}
		if (hasPk) {
			sb.append(')');
		} else {
			sb.setLength(origLen);
		}
	}

	@Override
	public String sqlDropTable(String tableName) {
		return "DROP TABLE " + tableName;
	}

	@Override
	public String sqlCreatePrimaryKey(DbTable table) {
		StringBuilder sb = new StringBuilder("ALTER TABLE ");
		sb.append(table.getName());
		sb.append(" ADD ");
		appendSqlPrimaryKeyConstraint(table, sb);
		return sb.toString();
	}

	@Override
	public String sqlDropPrimaryKey(String tableName) {
		return "ALTER TABLE " + tableName + " DROP PRIMARY KEY";
	}

	@Override
	public List<String> sqlAddColumn(DbTable table, DbColumn newCol) {
		if (table.getColumn(newCol.getName()) != null) {
			throw new IllegalArgumentException("Column " + newCol.getName() //
					+ " already exists in table " + table.getName());
		}
		List<String> commands = new ArrayList<String>(3); // 1 alter + drop PK + create PK
		StringBuilder sb = new StringBuilder();
		sb.append("ALTER TABLE ").append(table.getName()).append(" ADD COLUMN ");
		appendSqlColumnDefinition(newCol, sb);
		commands.add(sb.toString());
		return commands;
	}

	@Override
	public List<String> sqlDropColumn(DbTable table, String columnName) {
		List<String> commands = new ArrayList<String>(3); // 1 alter + drop PK + create PK
		if (table.getColumn(columnName) == null) {
			throw new IllegalArgumentException("No column " + columnName + " in table " + table.getName());
		}
		commands.add("ALTER TABLE " + table.getName() + " DROP COLUMN " + columnName);
		return commands;
	}

	@Override
	public List<String> sqlModifyColumn(DbTable table, DbColumn newCol) {
		List<String> commands = new ArrayList<String>(5); // 3 alter (see pgsql) + drop PK + create PK
		DbColumn oldCol = table.getColumn(newCol.getName());
		if (oldCol == null) {
			throw new IllegalArgumentException("No column " + newCol.getName() + " in table " + table.getName());
		}

		if (oldCol.equals(newCol)) {
			// skip further checks if there's no work to do
			return commands;
		}

		// update the column definition
		appendModifyColumnCommands(commands, table, oldCol, newCol);
		return commands;
	}

	/** To be extended by specific dialects to update the column name and description */
	abstract protected void appendModifyColumnCommands(List<String> commands, //
			DbTable table, DbColumn oldCol, DbColumn newCol);

}
