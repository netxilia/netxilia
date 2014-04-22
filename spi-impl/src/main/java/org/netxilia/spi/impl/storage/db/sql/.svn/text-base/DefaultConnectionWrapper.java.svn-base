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
package org.netxilia.spi.impl.storage.db.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.netxilia.api.exception.StorageException;

/**
 * This is a default implementation of the wrapper around a connection.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DefaultConnectionWrapper implements IConnectionWrapper {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DefaultConnectionWrapper.class);

	private final Connection connection;

	DefaultConnectionWrapper(Connection connection) {
		this.connection = connection;
	}

	public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
		PreparedStatement st = null;
		ResultSet rs = null;

		if (log.isDebugEnabled()) {
			log.debug("QUERY:" + sql + " with " + ArrayUtils.toString(params));
		}
		try {
			st = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; ++i) {
				st.setObject(i + 1, params[i]);
			}
			rs = st.executeQuery();
			return new RowMapperResultSetExtractor<T>(rowMapper).extractData(rs);
		} catch (SQLException e) {
			throw new StorageException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// silent
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					// silent
				}
			}
		}

	}

	public int queryForInt(String sql, Object... params) {
		PreparedStatement st = null;
		ResultSet rs = null;
		if (log.isDebugEnabled()) {
			log.debug("QUERY INT:" + sql + " with " + ArrayUtils.toString(params));
		}
		try {
			st = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; ++i) {
				st.setObject(i + 1, params[i]);
			}
			rs = st.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} catch (SQLException e) {
			throw new StorageException(e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					// silent
				}
			}
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					// silent
				}
			}
		}
	}

	public int update(String sql, Object... params) {
		PreparedStatement st = null;

		long t1 = System.currentTimeMillis();

		try {
			st = connection.prepareStatement(sql);
			for (int i = 0; i < params.length; ++i) {
				st.setObject(i + 1, params[i]);
			}
			return st.executeUpdate();
		} catch (SQLException e) {
			throw new StorageException(e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					// silent
				}
			}
			long t2 = System.currentTimeMillis();
			if (log.isDebugEnabled()) {
				log.debug("UPDATE:" + sql + " with " + ArrayUtils.toString(params) + " update:" + (t2 - t1));
			}
		}
	}

	@Override
	public void close() {
		try {
			connection.commit();
			connection.close();
		} catch (SQLException e) {
			// silent
		}
	}

}
