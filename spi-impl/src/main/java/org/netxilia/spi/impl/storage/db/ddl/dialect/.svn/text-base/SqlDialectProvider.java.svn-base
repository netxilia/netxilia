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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Provides the appropriate SqlDialect for a DB Dialect name.
 * 
 * @author catac
 */
public class SqlDialectProvider {
	private final static Logger logger = Logger.getLogger(SqlDialectProvider.class);

	private final static Map<String, ISqlDialect> dialects = new HashMap<String, ISqlDialect>();
	static {
		registerSqlDialect(new DerbyDialect());
		registerSqlDialect(new MySQLDialect());
		registerSqlDialect(new PostgreSQLDialect());
		registerSqlDialect(new H2Dialect());
	}

	private static void registerSqlDialect(ISqlDialect generator) {
		dialects.put(generator.getDbDialect(), generator);
	}

	/** Get the SqlGenerator for the given DB Dialect */
	public static ISqlDialect getSqlDialect(String dbDialect) {
		ISqlDialect dia = dialects.get(dbDialect);
		if (dia == null) {
			throw new UnsupportedOperationException("DB Dialect " + dbDialect + " is unsupported");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Using the " + dbDialect + " SqlDialect");
		}
		return dia;
	}
}
