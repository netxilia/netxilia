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
package org.netxilia.spi.impl.storage.ddl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.netxilia.spi.impl.storage.db.ddl.DDLWriter;
import org.netxilia.spi.impl.storage.db.ddl.dialect.ISqlDialect;

public class MockDDLWriter extends DDLWriter {
	private List<String> executedQueries = new ArrayList<String>();

	public MockDDLWriter(DataSource dataSource, ISqlDialect sqlDialect) {
		super(dataSource, sqlDialect);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void executeDDLQuery(String query) throws SQLException {
		executedQueries.add(query);
	}

	/** Execute the given DDL queries */
	@Override
	protected void executeDDLQueries(List<String> queryList) throws SQLException {
		executedQueries.addAll(queryList);
	}

	public List<String> getExecutedQueries() {
		return executedQueries;
	}

	public void setExecutedQueries(List<String> executedQueries) {
		this.executedQueries = executedQueries;
	}

}
