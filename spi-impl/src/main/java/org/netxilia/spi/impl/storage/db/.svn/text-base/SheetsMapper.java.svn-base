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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.netxilia.api.chart.Chart;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.storage.IJsonSerializer;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.google.gson.reflect.TypeToken;

/**
 * Handles the DB-Mapping for Sheet.
 * 
 * @author acraciun
 */
public class SheetsMapper extends AbstractMapper {

	private final static Logger log = Logger.getLogger(SheetsMapper.class);

	@Autowired
	private IJsonSerializer jsonSerializer;

	/**
	 * the same table as the one for the cells' value. it is used only to modify its structure
	 */
	private DbTable valuesTableModel;

	@Autowired
	private SparseMatrixMapper matrixMapper;

	@Autowired
	private NormalRowsMapper normalRowsMapper;
	@Autowired
	private OtherRowsMapper otherRowsMapper;
	@Autowired
	private ColumnsMapper columnsMapper;

	public SheetsMapper() {
	}

	public DbTable getValuesTableModel() {
		return valuesTableModel;
	}

	public void setValuesTableModel(DbTable valuesTableModel) {
		this.valuesTableModel = valuesTableModel;
	}

	public SparseMatrixMapper getMatrixMapper() {
		return matrixMapper;
	}

	public void setMatrixMapper(SparseMatrixMapper matrixMapper) {
		this.matrixMapper = matrixMapper;
	}

	public NormalRowsMapper getNormalRowsMapper() {
		return normalRowsMapper;
	}

	public void setNormalRowsMapper(NormalRowsMapper normalRowsMapper) {
		this.normalRowsMapper = normalRowsMapper;
	}

	public OtherRowsMapper getOtherRowsMapper() {
		return otherRowsMapper;
	}

	public void setOtherRowsMapper(OtherRowsMapper otherRowsMapper) {
		this.otherRowsMapper = otherRowsMapper;
	}

	public ColumnsMapper getColumnsMapper() {
		return columnsMapper;
	}

	public void setColumnsMapper(ColumnsMapper columnsMapper) {
		this.columnsMapper = columnsMapper;
	}

	@Override
	public IJsonSerializer getJsonSerializer() {
		return jsonSerializer;
	}

	@Override
	public void setJsonSerializer(IJsonSerializer jsonSerializer) {
		this.jsonSerializer = jsonSerializer;
	}

	public List<DbSheetStorageInfo> loadAllStorageInfo(WorkbookDbSession data, final WorkbookId workbookId) {
		return getProperties(data, null, new SheetStorageFromMap(workbookId), SHEET_CATEGORY, null, null);
	}

	public DbSheetStorageInfo loadStorageInfo(SheetDbSession data, SheetFullName fullName) {
		SheetId sheetId = super.findSheetIdByName(data.getWorkbookData(), fullName);
		if (sheetId != null) {
			List<DbSheetStorageInfo> sheets = getProperties(data.getWorkbookData(), sheetId, new SheetStorageFromMap(
					data.getWorkbookData().getWorkbookId()), SHEET_CATEGORY, null, null);

			if (sheets.size() == 0) {
				return null;
			}

			return sheets.get(0);
		}
		return null;
	}

	public SheetData load(SheetDbSession data, SheetFullName sheetName) throws StorageException, NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		List<SheetData> sheets = getProperties(data.getWorkbookData(), sheetStorage.getId(), new SheetFromMap(data
				.getWorkbookData().getWorkbookId()), SHEET_CATEGORY, null, null);

		if (sheets.size() == 0) {
			return null;
		}

		return sheets.get(0);
	}

	public List<SheetData> loadSheets(WorkbookDbSession data) throws StorageException, NotFoundException {
		return getProperties(data, null, new SheetFromMap(data.getWorkbookId()), SHEET_CATEGORY, null, null);
	}

	private String getValuesTableName(SheetId sheetId, WorkbookDbSession data) {
		String tableName = data.getWorkbookId() + "_" + sheetId.getId() + valuesTableModel.getName();
		return tableName;
	}

	public DbSheetStorageInfo createStorageInfo(SheetDbSession data, SheetFullName sheetName, SheetType sheetType) {
		DbSheetStorageInfo storageInfo;
		SheetId id = generateNewSheetId(data.getWorkbookData());
		if (sheetType == SheetType.normal) {
			// value table name can also be added by user
			String valueTableName = getValuesTableName(id, data.getWorkbookData());
			ensureTableExists(valueTableName, valuesTableModel, data.getWorkbookData());
			storageInfo = new DbSheetStorageInfo(id, sheetName, sheetType, valueTableName);
		} else {
			storageInfo = new DbSheetStorageInfo(id, sheetName, sheetType, "none");
		}
		addProperty(data.getWorkbookData(), storageInfo.getId(), SHEET_CATEGORY, storageInfo.getId().getId(),
				SheetData.Property.name.name(), sheetName.getSheetName());
		addProperty(data.getWorkbookData(), storageInfo.getId(), SHEET_CATEGORY, storageInfo.getId().getId(),
				SheetData.Property.type.name(), sheetType);
		addProperty(data.getWorkbookData(), storageInfo.getId(), SHEET_CATEGORY, storageInfo.getId().getId(),
				DbSheetStorageInfo.Property.dbTableName.name(), storageInfo.getDbTableName());

		return storageInfo;
	}

	/**
	 * Save sheet's modifications according to the specified type
	 * 
	 * @param property
	 * 
	 * @throws SQLException
	 * @throws StorageException
	 * @throws DataAccessException
	 */
	public void save(SheetDbSession data, SheetData sheetData, Collection<SheetData.Property> properties)
			throws StorageException {
		DbSheetStorageInfo storageInfo = data.getStorageService().getOrCreateSheetStorage(data, sheetData.getType());
		// update only modifies separate properties
		super.setObject(data.getWorkbookData(), storageInfo.getId(), SHEET_CATEGORY, sheetData, storageInfo.getId(),
				toStringList(properties), new SheetPropertySerializer());

	}

	public void deleteSheet(SheetDbSession data, SheetFullName sheetName) throws NotFoundException {
		DbSheetStorageInfo storageInfo = data.getStorageService().getSheetStorage(data);
		if (storageInfo.getType() == SheetType.normal) {
			try {
				data.getWorkbookData().getDdl().writer()
						.dropTable(data.getWorkbookData().getSchema(), storageInfo.getDbTableName());
			} catch (SQLException e) {
				throw new StorageException(e);
			}
		}
		// delete sheet properties
		deleteProperty(data.getWorkbookData(), storageInfo.getId(), SHEET_CATEGORY, storageInfo.getId().getId(), null);

		// delete rows
		AbstractRowsMapper rowsMapper = storageInfo.getType() == SheetType.normal ? normalRowsMapper : otherRowsMapper;
		rowsMapper.deleteAll(data, storageInfo);
		// delete cells properties
		matrixMapper.deleteAll(data, storageInfo);
	}

	private class SheetPropertySerializer extends DefaultPropertySerializer<SheetData> {
		@Override
		public String serializeProperty(SheetData sheet, String property) {
			if (SheetData.Property.charts.name().equals(property)) {
				return serializeCharts(sheet);
			}
			if (SheetData.Property.aliases.name().equals(property)) {
				return serializeAliases(sheet);
			}
			if (SheetData.Property.spans.name().equals(property)) {
				return serializeSpans(sheet);
			}
			return super.serializeProperty(sheet, property);
		}

		private String serializeCharts(SheetData sheet) throws StorageException {
			List<Chart> charts = sheet.getCharts();
			if (charts.size() == 0) {
				return null;
			}
			return jsonSerializer.serialize(charts);
		}

		private String serializeAliases(SheetData sheet) throws StorageException {
			Map<Alias, AreaReference> aliases = sheet.getAliases();
			if (aliases.size() == 0) {
				return null;
			}
			return jsonSerializer.serialize(aliases);
		}

		private String serializeSpans(SheetData sheet) throws StorageException {
			List<AreaReference> spans = sheet.getSpans();
			if (spans.size() == 0) {
				return null;
			}
			return jsonSerializer.serialize(spans);
		}
	}

	/**
	 * build a DbSheetStorageInfo from a map
	 */
	private class SheetStorageFromMap implements IInstanceProviderFromMap<DbSheetStorageInfo> {
		private final WorkbookId workbookId;

		public SheetStorageFromMap(WorkbookId workbookId) {
			this.workbookId = workbookId;
		}

		@Override
		public DbSheetStorageInfo newInstance(String category, String objectId, Map<String, String> properties) {
			try {
				SheetId sheetId = new SheetId(Integer.valueOf(objectId));
				SheetType type = SheetType.valueOf(properties.get(DbSheetStorageInfo.Property.type.name()));
				SheetFullName sheetFullName = new SheetFullName(workbookId, properties.get(SheetData.Property.name
						.name()));
				return new DbSheetStorageInfo(sheetId, sheetFullName, type,
						properties.get(DbSheetStorageInfo.Property.dbTableName.name()));
			} catch (Exception ex) {
				log.error("Could not load sheet description from:" + properties);
				return null;
			}
		}
	}

	/** Internal helper that is used to restore a complete sheet from DB */
	private class SheetFromMap implements IInstanceProviderFromMap<SheetData> {
		private final WorkbookId workbookId;

		public SheetFromMap(WorkbookId workbookId) {
			this.workbookId = workbookId;
		}

		@SuppressWarnings("unchecked")
		private Map<Alias, AreaReference> loadAliases(String serAliases) {
			if (serAliases != null) {
				try {
					return (Map<Alias, AreaReference>) jsonSerializer.deserialize(
							new TypeToken<Map<Alias, AreaReference>>() {
							}.getType(), serAliases);
				} catch (Exception e) {
					log.error("Cannot load aliases from " + serAliases + ":" + e, e);
				}
			}
			return new HashMap<Alias, AreaReference>();
		}

		@SuppressWarnings("unchecked")
		private List<Chart> loadCharts(String serCharts) {
			if (serCharts != null) {
				try {
					return (List<Chart>) jsonSerializer.deserialize(new TypeToken<List<Chart>>() {
					}.getType(), serCharts);
				} catch (Exception e) {
					log.error("Cannot load charts from " + serCharts + ":" + e, e);
				}
			}
			return new ArrayList<Chart>();
		}

		@SuppressWarnings("unchecked")
		private List<AreaReference> loadSpans(String serSpans) {
			if (serSpans != null) {
				try {
					return (List<AreaReference>) jsonSerializer.deserialize(new TypeToken<List<AreaReference>>() {
					}.getType(), serSpans);
				} catch (Exception e) {
					log.error("Cannot load spans from " + serSpans + ":" + e, e);
				}
			}
			return new ArrayList<AreaReference>();
		}

		@Override
		public SheetData newInstance(String category, String objectId, Map<String, String> properties) {
			try {
				SheetType type = SheetType.valueOf(properties.get(SheetData.Property.type.name()));
				String name = properties.get(SheetData.Property.name.name());

				Map<Alias, AreaReference> aliases = loadAliases(properties.get(SheetData.Property.aliases.name()));
				List<Chart> charts = loadCharts(properties.get(SheetData.Property.charts.name()));
				List<AreaReference> spans = loadSpans(properties.get(SheetData.Property.spans.name()));

				return new SheetData(new SheetFullName(workbookId, name), type, aliases, charts, spans);
			} catch (Exception ex) {
				log.error("Could not load sheet description from:" + properties);
				return null;
			}
		}
	}

}
