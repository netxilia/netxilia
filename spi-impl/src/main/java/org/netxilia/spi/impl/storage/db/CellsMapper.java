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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.CellCreator;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellData.Property;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.utils.CollectionUtils;
import org.netxilia.api.utils.IListElementCreator;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.MatrixBuilder;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.GenericValueUtils;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.api.value.StringValue;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the most complicated mapper because is stores value in the data table and all the order properties in a
 * compressed (SparseMatrix) format in the properties table.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class CellsMapper extends AbstractMapper {
	private final static Logger log = Logger.getLogger(CellsMapper.class);

	private final static IGenericValue IGNORE_VALUE = new StringValue("__IGNORE");

	private final IListElementCreator<IGenericValue> GENERIC_VALUE_CREATOR = CollectionUtils
			.sameElementCreator(IGNORE_VALUE);

	@Autowired
	private SparseMatrixMapper matrixMapper;

	@Autowired
	private NormalRowsMapper rowsMapper;

	@Autowired
	private IGenericValueParseService parseService;

	public SparseMatrixMapper getMatrixMapper() {
		return matrixMapper;
	}

	public void setMatrixMapper(SparseMatrixMapper matrixMapper) {
		this.matrixMapper = matrixMapper;
	}

	public IGenericValueParseService getParseService() {
		return parseService;
	}

	public void setParseService(IGenericValueParseService parseService) {
		this.parseService = parseService;
	}

	public NormalRowsMapper getRowsMapper() {
		return rowsMapper;
	}

	public void setRowsMapper(NormalRowsMapper rowsMapper) {
		this.rowsMapper = rowsMapper;
	}

	private void saveRow(SheetDbSession session, DbTable valuesTable, int row, List<IGenericValue> cells,
			List<DbColumnStorageInfo> columnStorages) throws NotFoundException {
		DbRowStorageInfo rowStorage = session.getStorageService().getOrCreateRowStorage(session, row);
		StringBuilder query = new StringBuilder("UPDATE " + valuesTable.getName() + " SET ");
		List<Object> params = new ArrayList<Object>(cells.size());

		for (int col = 0; col < cells.size(); ++col) {
			IGenericValue value = cells.get(col);
			if (value == IGNORE_VALUE) {
				continue;
			}
			DbColumnStorageInfo columnStorage = columnStorages.get(col);
			String rawValue = value != null ? value.getStringValue() : null;
			int maxCellSize = valuesTable.getColumn(columnStorage.getDbColumnName()).getSize();
			if (rawValue != null && rawValue.length() > maxCellSize) {
				log.warn(new CellReference(row, col) + " value was truncated from " + rawValue.length() + " to "
						+ maxCellSize);
				rawValue = rawValue.substring(0, maxCellSize);
			}
			if (params.size() > 0) {
				query.append(",");
			}
			query.append(columnStorage.getDbColumnName()).append(" = ?");
			params.add(rawValue);
		}

		if (params.size() == 0) {
			return;
		}
		query.append(" WHERE id = ?");
		params.add(rowStorage.getId());
		session.getWorkbookData().update(query.toString(), params.toArray());
	}

	private int getMaxColumn(Collection<CellDataWithProperties> cells) {
		int maxColumn = 0;
		for (CellDataWithProperties saveCell : cells) {
			CellReference ref = saveCell.getCellData().getReference();
			maxColumn = Math.max(maxColumn, ref.getColumnIndex());
		}
		return maxColumn;
	}

	private void saveNormalData(DbSheetStorageInfo sheetStorage, SheetDbSession session, SheetFullName sheetName,
			Collection<CellDataWithProperties> cells, List<DbColumnStorageInfo> columnStorage) throws NotFoundException {
		String valueTableName = sheetStorage.getDbTableName();
		DbTable valuesTable = session.getWorkbookData().getSchema().getTable(valueTableName);
		if (valuesTable == null) {
			// the table should be created by the sheet
			throw new StorageException("The table " + valueTableName + " does not exist");
		}
		// TODO - quick save for one cell

		// cells grouped by row
		// for each row, the map contains the value in the corresponding column position
		// the special generic value is used to mark a cell to be ignored when saving
		Map<Integer, List<IGenericValue>> rows = new LinkedHashMap<Integer, List<IGenericValue>>();
		for (CellDataWithProperties saveCell : cells) {
			if (!saveCell.getProperties().contains(CellData.Property.value)) {
				continue;
			}
			CellReference ref = saveCell.getCellData().getReference();
			List<IGenericValue> row = rows.get(ref.getRowIndex());
			if (row == null) {
				row = new ArrayList<IGenericValue>(ref.getColumnIndex() + 1);
				rows.put(ref.getRowIndex(), row);
			}
			CollectionUtils.atLeastSize(row, ref.getColumnIndex() + 1, GENERIC_VALUE_CREATOR);
			row.set(ref.getColumnIndex(), saveCell.getCellData().getValue());
		}

		// save each row
		for (Map.Entry<Integer, List<IGenericValue>> entry : rows.entrySet()) {
			saveRow(session, valuesTable, entry.getKey(), entry.getValue(), columnStorage);
		}
	}

	public void saveCells(SheetDbSession session, SheetFullName sheetName, Collection<CellDataWithProperties> cells)
			throws NotFoundException {
		DbSheetStorageInfo sheetStorage = session.getStorageService().getSheetStorage(session);

		long t1 = System.currentTimeMillis();
		List<DbColumnStorageInfo> columnStorage = session.getStorageService().createColumnStorage(session,
				getMaxColumn(cells));

		if (sheetStorage.getType() == SheetType.normal) {
			saveNormalData(sheetStorage, session, sheetName, cells, columnStorage);
		}
		long t2 = System.currentTimeMillis();
		SparseMatrixCollection matrices = session.getStorageService().getCellsStorage(session);
		matrices.setListener(matrixMapper.getMatrixCollectionListener(session, sheetStorage));
		try {
			for (CellDataWithProperties saveCell : cells) {

				saveCellProperties(matrices, session, sheetName, sheetStorage.getType(), saveCell.getCellData(),
						saveCell.getProperties());
			}
		} finally {
			matrices.getListener().save();
			matrices.setListener(null);
		}
		long t3 = System.currentTimeMillis();
		if (log.isDebugEnabled()) {
			log.debug("Mapper save:" + " data=" + (t2 - t1) + " props=" + (t3 - t2));
		}
	}

	/**
	 * save a cell's property (including value for sheets of type other than "normal")
	 */
	private CellData saveCellProperties(SparseMatrixCollection matrices, SheetDbSession session,
			SheetFullName sheetFullName, SheetType sheetType, CellData cell, Collection<Property> properties)
			throws NotFoundException {
		// make sure the corresponding row exists
		session.getStorageService().getOrCreateRowStorage(session, cell.getReference().getRowIndex());

		for (Property property : properties) {
			if (Property.value == property) {
				// TODO - store rich values

				if (sheetType != SheetType.normal) {
					// create column
					// create row
					String rawValue = cell.getValue() != null ? cell.getValue().getStringValue() : null;
					matrices.put(cell.getReference().getRowIndex(), cell.getReference().getColumnIndex(),
							property.name(), rawValue);
				}
				// add also type
				matrices.put(cell.getReference().getRowIndex(), cell.getReference().getColumnIndex(),
						CellData.Property.type.name(), cell.getValue() != null ? cell.getValue().getValueType().name()
								: null);

			} else {
				// update the other properties
				Object propertyValue = null;
				try {
					propertyValue = PropertyUtils.getSimpleProperty(cell, property.name());
				} catch (Exception e) {
					log.error("Cannot get cell property " + property + ":" + e, e);
				}
				matrices.put(cell.getReference().getRowIndex(), cell.getReference().getColumnIndex(), property.name(),
						propertyValue != null ? propertyValue.toString() : null);
				// save modification right away
			}
		}

		return cell;
	}

	public Matrix<CellData> loadCells(SheetDbSession data, SheetFullName sheetFullName, AreaReference area)
			throws StorageException, NotFoundException {
		DbSheetStorageInfo sheetStorage = data.getStorageService().getSheetStorage(data);
		SheetDimensions dimensions = data.getStorageService().getSheetDimensions(data);
		if (dimensions.getRowCount() == 0 || dimensions.getColumnCount() == 0) {
			// empty sheet
			return new Matrix<CellData>();
		}
		if (area.getFirstColumnIndex() >= dimensions.getColumnCount()
				|| area.getFirstRowIndex() >= dimensions.getRowCount()) {
			// the desired area is completely outside the sheet
			return new Matrix<CellData>();
		}
		AreaReference boundArea = area.bind(dimensions.getRowCount(), dimensions.getColumnCount());
		if (boundArea.getSheetName() == null) {
			boundArea = boundArea.withSheetName(sheetFullName.getSheetName());
		}
		List<List<String>> values = null;
		if (sheetStorage.getType() == SheetType.normal) {
			values = rowsMapper.loadValues(data, sheetStorage, boundArea);
		}
		SparseMatrixCollection matrices = data.getStorageService().getCellsStorage(data);

		MatrixBuilder<CellData> cells = new MatrixBuilder<CellData>(new CellCreator(boundArea.getSheetName(),
				boundArea.getFirstRowIndex(), boundArea.getFirstColumnIndex()));
		for (CellReference cell : boundArea.iterable(dimensions.getRowCount(), dimensions.getColumnCount())) {
			int relativeRowIndex = cell.getRowIndex() - boundArea.getFirstRowIndex();
			int relativeColIndex = cell.getColumnIndex() - boundArea.getFirstColumnIndex();

			// load the other properties
			String type = matrices.get(cell.getRowIndex(), cell.getColumnIndex(), CellData.Property.type.name());
			String style = matrices.get(cell.getRowIndex(), cell.getColumnIndex(), CellData.Property.styles.name());
			String formula = matrices.get(cell.getRowIndex(), cell.getColumnIndex(), CellData.Property.formula.name());

			String rawValue = null;
			if (sheetStorage.getType() == SheetType.normal) {
				if (relativeRowIndex < values.size()) {
					List<String> row = values.get(relativeRowIndex);
					if (row != null && relativeColIndex < row.size()) {
						rawValue = row.get(relativeColIndex);
					}
				}
			} else {
				rawValue = matrices.get(cell.getRowIndex(), cell.getColumnIndex(), CellData.Property.value.name());
			}
			IGenericValue value = null;
			try {
				// TODO - store rich values
				value = GenericValueUtils.rawStringToGeneric(rawValue, type != null ? GenericValueType.valueOf(type)
						: GenericValueType.STRING);
			} catch (Exception e) {
				log.error("Could not convert [" + rawValue + "] to type " + type + ". Re-parse it.");
				value = parseService.parse(rawValue);
			}

			CellData cellData = new CellData(cell, value, Formula.valueOf(formula), Styles.valueOf(style));
			cells.set(relativeRowIndex, relativeColIndex, cellData);

		}

		return cells.build();
	}

}
