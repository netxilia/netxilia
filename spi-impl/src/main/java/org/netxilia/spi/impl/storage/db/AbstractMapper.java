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
package org.netxilia.spi.impl.storage.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.Range;
import org.netxilia.api.storage.IJsonSerializer;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.netxilia.spi.impl.storage.db.sql.RowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

public abstract class AbstractMapper {
	private final static Logger log = Logger.getLogger(AbstractMapper.class);

	public final static String COLUMN_CATEGORY = "column";
	public final static String ROW_CATEGORY = "row";
	public final static String SHEET_CATEGORY = "sheet";
	public final static String CELLS_CATEGORY = "cells";

	/**
	 * to store different all other properties not stored as values
	 */
	private DbTable propertiesTableModel;

	@Autowired
	private IJsonSerializer jsonSerializer;

	private DbWorkbookStorageServiceImpl storageService;

	/**
	 * this value should match the size of the "value" column in the properties table schema
	 */
	private int smallValueSize = 150;

	public int getSmallValueSize() {
		return smallValueSize;
	}

	public void setSmallValueSize(int smallValueSize) {
		this.smallValueSize = smallValueSize;
	}

	public DbTable getPropertiesTableModel() {
		return propertiesTableModel;
	}

	public void setPropertiesTableModel(DbTable propertiesTableModel) {
		this.propertiesTableModel = propertiesTableModel;
	}

	public IJsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	public void setJsonSerializer(IJsonSerializer jsonSerializer) {
		this.jsonSerializer = jsonSerializer;
	}

	public DbWorkbookStorageServiceImpl getStorageService() {
		return storageService;
	}

	public void setStorageService(DbWorkbookStorageServiceImpl storageService) {
		this.storageService = storageService;
	}

	protected String ensureTableExists(String tableName, DbTable tableModel, WorkbookDbSession data)
			throws StorageException {
		if (data.getSchema().getTable(tableName) == null) {
			// the table does not even exist
			DbTable sheetTable = new DbTable(tableModel);
			sheetTable.setName(tableName);
			try {
				data.getDdl().writer().createTable(data.getSchema(), sheetTable);
			} catch (SQLException e) {
				throw new StorageException(e);
			}
		}
		// check also columns are the same!
		return tableName;
	}

	protected String getPropertiesTableName(WorkbookDbSession data) {
		String tableName = data.getWorkbookId() + propertiesTableModel.getName();
		return tableName;
	}

	protected <E extends Enum<E>> List<String> toStringList(Collection<E> enumProperties) {
		List<String> strings = new ArrayList<String>(enumProperties.size());
		for (Enum<?> e : enumProperties) {
			strings.add(e.name());
		}
		return strings;
	}

	protected <T> void addObject(WorkbookDbSession data, SheetId sheetId, String category, T object, Object objectId,
			List<String> properties) throws StorageException {
		addObject(data, sheetId, category, object, objectId, properties, new DefaultPropertySerializer<T>());
	}

	protected <T> void addObject(WorkbookDbSession data, SheetId sheetId, String category, T object, Object objectId,
			List<String> properties, IPropertySerializer<T> propertySerializer) throws StorageException {
		if (properties == null) {
			return;
		}
		for (String property : properties) {
			String propertyValue = propertySerializer.serializeProperty(object, property);

			if (propertyValue != null) {
				addProperty(data, sheetId, category, objectId, property, propertyValue.toString());
			}
		}
	}

	protected <T> void setObject(WorkbookDbSession data, SheetId sheetId, String category, T object, Object objectId,
			List<String> properties) throws StorageException {
		setObject(data, sheetId, category, object, objectId, properties, new DefaultPropertySerializer<T>());
	}

	protected <T> void setObject(WorkbookDbSession data, SheetId sheetId, String category, T object, Object objectId,
			List<String> properties, IPropertySerializer<T> propertySerializer) throws StorageException {
		if (properties == null) {
			return;
		}
		for (String property : properties) {
			String propertyValue = propertySerializer.serializeProperty(object, property);
			if (propertyValue == null) {
				deleteProperty(data, sheetId, category, objectId, property);
			} else {
				if (!setProperty(data, sheetId, category, objectId, property, propertyValue.toString())) {
					addProperty(data, sheetId, category, objectId, property, propertyValue.toString());
				}
			}
		}

	}

	protected boolean addProperty(WorkbookDbSession data, SheetId sheetId, String category, Object objectId,
			String property, Object value) throws StorageException {
		String tableName = getPropertiesTableName(data);
		ensureTableExists(tableName, propertiesTableModel, data);
		// TODO may use JSON here for the value
		try {
			String rawValue = value.toString();
			String smallValue = rawValue.length() <= smallValueSize ? rawValue : null;
			String bigValue = rawValue.length() <= smallValueSize ? null : rawValue;
			int rows = data.update("INSERT INTO " + tableName
					+ " (sheet_id, category, object, property, value, big_value) VALUES (?,?,?,?,?,?)", //
					sheetId.getId(), category, objectId.toString(), property, smallValue, bigValue);
			return rows != 0;
		} catch (Exception ex) {
			throw new StorageException("Caused when inserting:" + sheetId.getId() + "," + category + ","
					+ objectId.toString() + "," + property + "," + value.toString(), ex);
		}

	}

	protected boolean setProperty(WorkbookDbSession data, SheetId sheetId, String category, Object objectId,
			String property, Object value) throws StorageException {
		String tableName = getPropertiesTableName(data);
		ensureTableExists(tableName, propertiesTableModel, data);
		String rawValue = value.toString();
		String smallValue = rawValue.length() <= smallValueSize ? rawValue : null;
		String bigValue = rawValue.length() <= smallValueSize ? null : rawValue;
		// TODO may use JSON here for the value
		int rows = data.update("UPDATE " + tableName
				+ " SET value = ?, big_value = ? WHERE sheet_id = ? AND category = ? AND object = ? AND property = ?", //
				smallValue, bigValue, sheetId.getId(), category, objectId.toString(), property);
		return rows != 0;
	}

	protected boolean deleteProperty(WorkbookDbSession data, SheetId sheetId, String category, Object objectId,
			String property) throws StorageException {
		String tableName = getPropertiesTableName(data);
		ensureTableExists(tableName, propertiesTableModel, data);

		List<Object> whereParams = new ArrayList<Object>(3);
		StringBuilder query = new StringBuilder("DELETE FROM " + tableName + " ");
		addParam(query, whereParams, "sheet_id", sheetId.getId());
		addParam(query, whereParams, "category", category);
		if (objectId != null) {
			addParam(query, whereParams, "object", objectId.toString());
		}
		addParam(query, whereParams, "property", property);
		try {
			int rows = data.update(query.toString(), whereParams.toArray());
			return rows != 0;
		} catch (Exception ex) {
			throw new StorageException("Cannot execute query: " + query + " params:" + whereParams + ":" + ex, ex);
		}
	}

	@SuppressWarnings("rawtypes")
	private void addParam(StringBuilder query, List<Object> whereParams, String param, Object paramValue) {
		if (paramValue != null) {
			if (whereParams.size() == 0) {
				query.append("WHERE");
			} else {
				query.append("AND");
			}
			if (paramValue instanceof Collection) {
				query.append(" ").append(param).append(" IN (");
				boolean first = true;
				for (Object itemValue : (Collection) paramValue) {
					if (!first) {
						query.append(",");
					}
					query.append("?");
					whereParams.add(itemValue);
					first = false;
				}
				query.append(") ");
			} else {
				query.append(" ").append(param).append(" = ? ");
				whereParams.add(paramValue);
			}
		}
	}

	/**
	 * return the list of the properties from the database corresponding the the given sheet. If the category, objectId
	 * or property are null they will not be used in the where clause.
	 * 
	 * @
	 */
	protected String getProperty(WorkbookDbSession data, SheetId sheetId, String category, Object objectId,
			String property) {
		String tableName = getPropertiesTableName(data);
		if (data.getSchema().getTable(tableName) == null) {
			return null;
		}
		List<Object> whereParams = new ArrayList<Object>(3);
		StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " ");

		addParam(query, whereParams, "sheet_id", sheetId.getId());
		addParam(query, whereParams, "category", category);
		addParam(query, whereParams, "object", objectId);
		addParam(query, whereParams, "property", property);

		List<String> values = data.query(query.toString(), //
				new PropertyLoader<Object>(null, null), whereParams.toArray());
		return values.size() > 0 ? values.get(0) : null;
	}

	/**
	 * return the count of the properties from the database corresponding the the given sheet. If the category, objectId
	 * or property are null they will not be used in the where clause.
	 * 
	 * @
	 */
	protected int count(WorkbookDbSession data, SheetId sheetId, String category, Object objectId, String property) {
		String tableName = getPropertiesTableName(data);
		if (data.getSchema().getTable(tableName) == null) {
			return 0;
		}
		List<Object> whereParams = new ArrayList<Object>(3);
		StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM " + tableName + " ");

		addParam(query, whereParams, "sheet_id", sheetId.getId());
		addParam(query, whereParams, "category", category);
		addParam(query, whereParams, "object", objectId);
		addParam(query, whereParams, "property", property);

		return data.queryForInt(query.toString(), whereParams.toArray());
	}

	private boolean queryForProperties(WorkbookDbSession data, SheetId sheetId, RowMapper<String> rowMapper,
			String category, Object objectId, String property, Range records) {
		String tableName = getPropertiesTableName(data);
		if (data.getSchema().getTable(tableName) == null) {
			return false;
		}
		List<Object> whereParams = new ArrayList<Object>(3);
		StringBuilder query = new StringBuilder("SELECT * FROM " + tableName + " ");
		if (sheetId != null) {
			addParam(query, whereParams, "sheet_id", sheetId.getId());
		}
		addParam(query, whereParams, "category", category);
		addParam(query, whereParams, "object", objectId);
		addParam(query, whereParams, "property", property);
		query.append("order by category, object, property");

		if (records != null && !records.equals(Range.ALL)) {
			ParameterizedQuery pq = new ParameterizedQuery(query.toString(), whereParams);
			pq = DatabaseUtils.limitQuery(pq, records);
			data.query(pq.getQuery(), //
					rowMapper, pq.getParams().toArray());
		} else {
			data.query(query.toString(), //
					rowMapper, whereParams.toArray());
		}
		return true;
	}

	/**
	 * return the list of the properties from the database corresponding the the given sheet. If the category, objectId
	 * or property are null they will not be used in the where clause.
	 * 
	 * @
	 */
	protected <T> List<T> getProperties(WorkbookDbSession data, SheetId sheetId, IInstanceProvider<T> instanceProvider,
			String category, Object objectId, String property) {

		Map<String, T> instances = new LinkedHashMap<String, T>();
		queryForProperties(data, sheetId, new PropertyLoader<T>(instances, instanceProvider), category, objectId,
				property, null);
		return new ArrayList<T>(instances.values());
	}

	/**
	 * return the list of the properties from the database corresponding the the given sheet. If the category, objectId
	 * or property are null they will not be used in the where clause.
	 * 
	 * @
	 */
	protected <T> List<T> getProperties(WorkbookDbSession data, SheetId sheetId,
			IInstanceProviderFromMap<T> instanceProvider, String category, Object objectId, String property) {
		return getProperties(data, sheetId, instanceProvider, category, objectId, property, null);
	}

	/**
	 * return the list of the properties from the database corresponding the the given sheet. If the category, objectId
	 * or property are null they will not be used in the where clause.
	 * 
	 * @
	 */
	protected <T> List<T> getProperties(WorkbookDbSession data, SheetId sheetId,
			IInstanceProviderFromMap<T> instanceProvider, String category, Object objectId, String property,
			Range records) {

		PropertyInMapLoader loader = new PropertyInMapLoader();
		queryForProperties(data, sheetId, loader, category, objectId, property, records);

		// transfer the map of properties to an object
		List<T> results = new ArrayList<T>();
		for (Map.Entry<String, Map<String, String>> entry : loader.getInstances().entrySet()) {
			T result = instanceProvider.newInstance(category, entry.getKey(), entry.getValue());
			if (result != null) {
				results.add(result);
			}
		}
		return results;
	}

	protected SheetId generateNewSheetId(WorkbookDbSession data) {
		String tableName = getPropertiesTableName(data);
		ensureTableExists(tableName, propertiesTableModel, data);

		String query = "SELECT MAX(sheet_id) FROM " + tableName;

		int max = data.queryForInt(query);
		// will return 0 if none sheet exists yet
		return new SheetId(max + 1);
	}

	protected int getMaxObjectId(WorkbookDbSession data, SheetId sheetId, String category) {
		String tableName = getPropertiesTableName(data);
		ensureTableExists(tableName, propertiesTableModel, data);
		String query = "SELECT MAX(object) FROM " + tableName + " WHERE sheet_id = ? AND category = ?";

		return data.queryForInt(query.toString(), //
				sheetId.getId(), category);
	}

	public SheetId findSheetIdByName(WorkbookDbSession data, SheetFullName fullName) {
		String tableName = getPropertiesTableName(data);
		if (data.getSchema().getTable(tableName) == null) {
			return null;
		}
		String query = "SELECT sheet_id FROM " + tableName + " WHERE category = ? AND value = ?";

		try {
			int sheetId = data.queryForInt(query.toString(), //
					SHEET_CATEGORY, fullName.getSheetName());
			return new SheetId(sheetId);
		} catch (EmptyResultDataAccessException ex) {
			// not found
			return null;
		}

	}

	/** Internal helper that is used to restore a workbook from DB */
	private class PropertyLoader<T> implements RowMapper<String> {
		private final Map<String, T> instances;
		private final IInstanceProvider<T> instanceProvider;

		public PropertyLoader(Map<String, T> instances, IInstanceProvider<T> instanceProvider) {
			this.instances = instances;
			this.instanceProvider = instanceProvider;
		}

		@SuppressWarnings("unchecked")
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String objectId = rs.getString("object");
			String property = rs.getString("property");
			String smallValue = rs.getString("value");
			String bigValue = rs.getString("big_value");
			String value = smallValue != null ? smallValue : bigValue;

			if (instanceProvider != null) {
				T object = instances.get(objectId);
				if (object == null) {
					object = instanceProvider.newInstance(rs.getString("category"), objectId);
					instances.put(objectId, object);
				}
				// give first a chance to instance provider to deal with the property
				if (!instanceProvider.setProperty(object, property, value)) {
					try {
						// TODO should use here Type Converters or JSON !?
						BeanUtils.setProperty(
								object,
								property,
								jsonSerializer.deserialize(PropertyUtils.getPropertyType(object, property), "\""
										+ value + "\""));
					} catch (Exception e) {
						log.error("Cannot set property " + property + " of object:" + object);
					}
				}
			}
			return value;
		}
	}

	// /** Internal helper that is used to restore a workbook from DB */
	private class PropertyInMapLoader implements RowMapper<String> {
		private final Map<String, Map<String, String>> instances;

		public PropertyInMapLoader() {
			this.instances = new LinkedHashMap<String, Map<String, String>>();
		}

		public Map<String, Map<String, String>> getInstances() {
			return instances;
		}

		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			String objectId = rs.getString("object");
			String property = rs.getString("property");
			String smallValue = rs.getString("value");
			String bigValue = rs.getString("big_value");
			String value = smallValue != null ? smallValue : bigValue;

			Map<String, String> object = instances.get(objectId);
			if (object == null) {
				object = new HashMap<String, String>();
				instances.put(objectId, object);
			}
			object.put(property, value);

			return value;
		}
	}

}
