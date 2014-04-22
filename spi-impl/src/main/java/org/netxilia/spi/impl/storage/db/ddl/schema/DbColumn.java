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

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

/**
 * Hold all properties for a DB column.
 * 
 * @author catac
 */
public class DbColumn {
	private String name;
	private DbDataType dataType;
	private Integer size;
	private Integer scale;
	private boolean primaryKey = false;
	private boolean nullable = true;
	private String defaultValue;

	public DbColumn() {
		// empty
	}

	public DbColumn(DbColumn other) {
		name = other.name;
		dataType = other.dataType;
		size = other.size;
		scale = other.scale;
		primaryKey = other.primaryKey;
		nullable = other.nullable;
		defaultValue = other.defaultValue;
	}

	public String getName() {
		return name;
	}

	public String getNameUnique() {
		return name != null ? name.toLowerCase() : null;
	}

	@Required
	public void setName(String name) {
		Assert.notNull(name);
		this.name = name;
	}

	public DbDataType getDataType() {
		return dataType;
	}

	@Required
	public void setDataType(DbDataType dataType) {
		Assert.notNull(dataType);
		this.dataType = dataType;
	}

	/** Returns null if column's type has fixed size */
	public Integer getSize() {
		return ((dataType == null) || dataType.isSizeFixed()) ? null : size;
	}

	/** Has no effect if column's type has fixed size. However, the value is recorded. */
	public void setSize(Integer size) {
		Assert.isTrue((size == null) || (size.intValue() > 0), "Column size must be > 0");
		this.size = size;
	}

	/** Precision, for decimal numbers. Null if column's type has fixed size */
	public Integer getScale() {
		return ((dataType == null) || dataType.isSizeFixed()) ? null : scale;
	}

	/** Has no effect if column's type has fixed size. However, the value is recorded. */
	public void setScale(Integer scale) {
		Assert.isTrue((scale == null) || (scale.intValue() > 0), "Column scale must be > 0");
		this.scale = scale;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/** Note that a primary key column cannot be null-able */
	public boolean isNullable() {
		return primaryKey ? false : nullable;
	}

	/** Has no effect if it's a primary key. However, the value is recorded. */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * The default value for the column is none is specified. Some DBs (like Derby) require this to be present for
	 * non-nullable columns.
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isPrimaryKey() ? 1231 : 1237);
		result = prime * result + (isNullable() ? 1231 : 1237);
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((getSize() == null) ? 0 : size.hashCode());
		result = prime * result + ((getScale() == null) ? 0 : scale.hashCode());
		result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
		result = prime * result + ((name == null) ? 0 : getNameUnique().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		DbColumn other = (DbColumn) obj;
		return (isPrimaryKey() == other.isPrimaryKey()) //
				&& (isNullable() == other.isNullable()) //
				&& (dataType == other.dataType) //
				&& ObjectUtils.equals(getSize(), other.getSize()) //
				&& ObjectUtils.equals(getScale(), other.getScale()) //
				&& ObjectUtils.equals(defaultValue, other.defaultValue) //
				&& ObjectUtils.equals(getNameUnique(), other.getNameUnique());
	}

	@Override
	public String toString() {
		return "DbColumn[name=" + name + ", primaryKey=" + isPrimaryKey() + ", dataType=" + dataType //
				+ ", size=" + getSize() + ", scale=" + getScale() + ", nullable=" + isNullable() //
				+ ", default=" + defaultValue + "]";
	}
}
