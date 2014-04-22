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
package org.netxilia.spi.impl.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.exception.StorageException;
import org.netxilia.spi.impl.storage.db.sql.IConnectionWrapper;
import org.netxilia.spi.impl.storage.db.sql.RowMapper;
import org.netxilia.spi.impl.storage.db.sql.RowMapperResultSetExtractor;

public class MockConnectionWrapper implements IConnectionWrapper {
	// private final static Logger log = Logger.getLogger(MockJdbcOperations.class);

	private final List<ParamQuery> updateQueries = new ArrayList<ParamQuery>();
	private final Map<ParamQuery, ResultSet> responses = new HashMap<ParamQuery, ResultSet>();

	public List<ParamQuery> getUpdateQueries() {
		return updateQueries;
	}

	public void addResponse(ParamQuery query, ResultSet resultSet) {
		responses.put(query, resultSet);
	}

	@Override
	public <T> List<T> query(String sql, RowMapper<T> rm, Object... args) {
		ParamQuery q = new ParamQuery(sql, args);
		ResultSet rs = responses.get(q);
		if (rs != null) {
			try {
				return new RowMapperResultSetExtractor<T>(rm).extractData(rs);
			} catch (SQLException e) {
				throw new StorageException(e);
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		throw new RuntimeException("Result set not found for: " + sql + " Params: " + Arrays.toString(args));
	}

	@Override
	public int queryForInt(String sql, Object... args) {
		ParamQuery q = new ParamQuery(sql, args);
		ResultSet rs = responses.get(q);
		if (rs != null) {
			try {
				if (rs.next()) {
					return rs.getInt(1);
				}
			} catch (SQLException e) {
				throw new StorageException(e);
			} finally {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		throw new RuntimeException("Result set not found for: " + sql + " Params: " + Arrays.toString(args));
	}

	@Override
	public int update(String sql, Object... args) {
		updateQueries.add(new ParamQuery(sql, args));
		return 0;
	}

	public static class ParamQuery {
		private final String sql;
		private final Object[] args;

		public ParamQuery(String sql) {
			this(sql, new Object[] {});
		}

		public ParamQuery(String sql, Object[] args) {
			this.sql = sql.trim();
			this.args = args;
		}

		public String getSql() {
			return sql;
		}

		public Object[] getArgs() {
			return args;
		}

		@Override
		public String toString() {
			return "UpdateQuery [sql=" + sql + ", args=" + Arrays.toString(args) + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(args);
			result = prime * result + ((sql == null) ? 0 : sql.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ParamQuery other = (ParamQuery) obj;
			if (!Arrays.equals(args, other.args)) {
				return false;
			}
			if (sql == null) {
				if (other.sql != null) {
					return false;
				}
			} else if (!sql.equals(other.sql)) {
				return false;
			}
			return true;
		}

	}

	@Override
	public void close() {

	}
}
