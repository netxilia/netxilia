/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netxilia.spi.impl.storage.sql;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.util.Iterator;

import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;

/**
 * MockResultSetMetaData dynamically implements the ResultSetMetaData interface.
 */
public class MockResultSetMetaData implements InvocationHandler {

	private String[] columnNames = null;
	private String[] columnLabels = null;

	/**
	 * Create a <code>MockResultSetMetaData</code> proxy object. This is equivalent to:
	 * 
	 * <pre>
	 * ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames));
	 * </pre>
	 * 
	 * @param columnNames
	 * @return the proxy object
	 */
	public static ResultSetMetaData create(String[] columnNames) {
		return ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(columnNames));
	}

	public static ResultSetMetaData create(DbTable table) {
		return ProxyFactory.instance().createResultSetMetaData(new MockResultSetMetaData(table));
	}

	public MockResultSetMetaData(DbTable table) {
		this.columnNames = new String[table.getColumns().size()];
		int i = 0;
		Iterator<DbColumn> colIterator = table.getColumns().iterator();
		while (i < columnNames.length)
			columnNames[i++] = colIterator.next().getName();
		this.columnLabels = new String[columnNames.length];
	}

	public MockResultSetMetaData(String[] columnNames) {
		super();
		this.columnNames = columnNames;
		this.columnLabels = new String[columnNames.length];

	}

	public MockResultSetMetaData(String[] columnNames, String[] columnLabels) {
		super();
		this.columnNames = columnNames;
		this.columnLabels = columnLabels;

	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		String methodName = method.getName();

		if (methodName.equals("getColumnCount")) {
			return new Integer(this.columnNames.length);

		} else if (methodName.equals("getColumnName")) {

			int col = ((Integer) args[0]).intValue() - 1;
			return this.columnNames[col];

		} else if (methodName.equals("getColumnLabel")) {

			int col = ((Integer) args[0]).intValue() - 1;
			return this.columnLabels[col];

		} else if (methodName.equals("hashCode")) {
			return new Integer(System.identityHashCode(proxy));

		} else if (methodName.equals("toString")) {
			return "MockResultSetMetaData " + System.identityHashCode(proxy);

		} else if (methodName.equals("equals")) {
			return Boolean.valueOf(proxy == args[0]);

		} else {
			throw new UnsupportedOperationException("Unsupported method: " + methodName);
		}
	}
}