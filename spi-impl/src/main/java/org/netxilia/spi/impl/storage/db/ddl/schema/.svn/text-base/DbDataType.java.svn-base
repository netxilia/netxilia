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

/**
 * Supported DB data types, a subset of java.sql.Types constants.
 * 
 * @author catac
 */
public enum DbDataType {

	BOOLEAN {
		@Override
		public boolean isSizeFixed() {
			return true;
		}

		@Override
		public boolean isQuotedValue() {
			return false;
		}

		@Override
		public boolean isSynonymFor(String name) {
			return "BIT".equals(name);
		}
	},
	INTEGER {
		@Override
		public boolean isSizeFixed() {
			return true;
		}

		@Override
		public boolean isQuotedValue() {
			return false;
		}

		@Override
		public boolean isSynonymFor(String name) {
			return "INT".equals(name);
		}
	},
	DOUBLE {
		@Override
		public boolean isSizeFixed() {
			return true;
		}

		@Override
		public boolean isQuotedValue() {
			return false;
		}
	},
	DECIMAL {
		@Override
		public boolean isSizeFixed() {
			return false;
		}

		@Override
		public boolean isQuotedValue() {
			return false;
		}

		@Override
		public boolean isSynonymFor(String name) {
			return "NUMERIC".equals(name);
		}
	},
	TIMESTAMP {
		@Override
		public boolean isSizeFixed() {
			return true;
		}

		@Override
		public boolean isQuotedValue() {
			return true;
		}
	},
	VARCHAR {
		@Override
		public boolean isSizeFixed() {
			return false;
		}

		@Override
		public boolean isQuotedValue() {
			return true;
		}

		@Override
		public boolean isSynonymFor(String name) {
			return "VARCHAR2".equals(name);
		}
	},
	CLOB {
		@Override
		public boolean isSizeFixed() {
			return true;
		}

		@Override
		public boolean isQuotedValue() {
			return true;
		}

		@Override
		public boolean isSynonymFor(String name) {
			return false;
		}
	};

	/** True if the column's size is fixed for this data type */
	public abstract boolean isSizeFixed();

	/** True if when giving a value for this column we have to quote it */
	public abstract boolean isQuotedValue();

	/** Returns true if this type is also known with the given name */
	public boolean isSynonymFor(String name) {
		return false;
	}

	/** Equivalent to valueOf() but also takes into account the type name's synonyms */
	public static DbDataType from(String name) {
		String uniqueName = name.toUpperCase();
		for (DbDataType type : DbDataType.values()) {
			if (type.name().equals(uniqueName) || type.isSynonymFor(uniqueName)) {
				return type;
			}
		}
		throw new IllegalArgumentException("Type '" + name + "' is not supported.");
	}
}
