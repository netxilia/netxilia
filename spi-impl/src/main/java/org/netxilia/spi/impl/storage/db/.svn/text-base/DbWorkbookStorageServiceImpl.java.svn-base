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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.storage.IDataSourceConfigurationListener;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.spi.impl.storage.db.ddl.ExtendedDataSource;
import org.netxilia.spi.impl.storage.db.ddl.IDDLUtilsFactory;
import org.netxilia.spi.impl.storage.db.sql.IConnectionWrapperFactory;
import org.netxilia.spi.storage.ISheetStorageService;
import org.netxilia.spi.storage.IWorkbookStorageService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This service stores workbooks and sheets in a database. See storage schema on the website. The storage is meant to
 * support multiple access on different workbooks.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DbWorkbookStorageServiceImpl implements IWorkbookStorageService, ApplicationContextAware,
		InitializingBean, IDataSourceConfigurationListener {
	// private final static Logger log = Logger.getLogger(DbWorkbookStorageServiceImpl.class);

	// configurable params
	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	@Autowired
	private IDDLUtilsFactory ddlUtilsFactory;

	@Autowired
	private IConnectionWrapperFactory connectionWrapperFactory;

	@Autowired
	private WorkbooksMapper workbooksMapper;
	@Autowired
	private SheetsMapper sheetsMapper;

	@Autowired
	private RowsMapper rowsMapper;

	@Autowired
	private ColumnsMapper columnsMapper;
	@Autowired
	private CellsMapper cellsMapper;
	@Autowired
	private SparseMatrixMapper matrixMapper;

	private ApplicationContext context;

	private ConcurrentMap<SheetFullName, DbSheetStorageServiceImpl> sheetCache = new ConcurrentHashMap<SheetFullName, DbSheetStorageServiceImpl>();

	private ConcurrentMap<WorkbookId, ExtendedDataSource> dataSources = new ConcurrentHashMap<WorkbookId, ExtendedDataSource>();

	private ConcurrentMap<DataSourceConfigurationId, ExtendedDataSource> configurations = new ConcurrentHashMap<DataSourceConfigurationId, ExtendedDataSource>();

	// ADD row/data cache with EHCACHE

	public IDDLUtilsFactory getDdlUtilsFactory() {
		return ddlUtilsFactory;
	}

	public void setDdlUtilsFactory(IDDLUtilsFactory ddlUtilsFactory) {
		this.ddlUtilsFactory = ddlUtilsFactory;
	}

	public IConnectionWrapperFactory getConnectionWrapperFactory() {
		return connectionWrapperFactory;
	}

	public void setConnectionWrapperFactory(IConnectionWrapperFactory connectionWrapperFactory) {
		this.connectionWrapperFactory = connectionWrapperFactory;
	}

	public WorkbooksMapper getWorkbooksMapper() {
		return workbooksMapper;
	}

	public void setWorkbooksMapper(WorkbooksMapper workbooksMapper) {
		this.workbooksMapper = workbooksMapper;
	}

	public SheetsMapper getSheetsMapper() {
		return sheetsMapper;
	}

	public void setSheetsMapper(SheetsMapper sheetsMapper) {
		this.sheetsMapper = sheetsMapper;
	}

	public CellsMapper getCellsMapper() {
		return cellsMapper;
	}

	public void setCellsMapper(CellsMapper cellsMapper) {
		this.cellsMapper = cellsMapper;
	}

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	public RowsMapper getRowsMapper() {
		return rowsMapper;
	}

	public void setRowsMapper(RowsMapper rowsMapper) {
		this.rowsMapper = rowsMapper;
	}

	public ColumnsMapper getColumnsMapper() {
		return columnsMapper;
	}

	public void setColumnsMapper(ColumnsMapper columnsMapper) {
		this.columnsMapper = columnsMapper;
	}

	public SparseMatrixMapper getMatrixMapper() {
		return matrixMapper;
	}

	public void setMatrixMapper(SparseMatrixMapper matrixMapper) {
		this.matrixMapper = matrixMapper;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;

	}

	private ExtendedDataSource getDataSource(DataSourceConfiguration datasourceConfiguration) throws StorageException,
			NotFoundException, SQLException {
		ExtendedDataSource extDataSource = configurations.get(datasourceConfiguration.getId());
		if (extDataSource == null) {
			DataSource dataSource = dataSourceConfigurationService.buildDataSource(datasourceConfiguration);
			ExtendedDataSource newExtDataSource = new ExtendedDataSource(ddlUtilsFactory.newInstance(dataSource));
			extDataSource = configurations.putIfAbsent(datasourceConfiguration.getId(), newExtDataSource);
			if (extDataSource == null) {
				extDataSource = newExtDataSource;
			}
		}
		return extDataSource;
	}

	private ExtendedDataSource getDataSource(WorkbookId workbookKey) throws StorageException, NotFoundException,
			SQLException {
		ExtendedDataSource extDataSource = dataSources.get(workbookKey);
		if (extDataSource == null) {
			DataSourceConfiguration datasourceConfiguration = dataSourceConfigurationService
					.loadByWorkbook(workbookKey);
			if (datasourceConfiguration == null) {
				throw new StorageException("No datasource was defined for workbook [" + workbookKey + "]");
			}
			ExtendedDataSource newExtDataSource = getDataSource(datasourceConfiguration);
			extDataSource = dataSources.putIfAbsent(workbookKey, newExtDataSource);
			if (extDataSource == null) {
				extDataSource = newExtDataSource;
			}
		}
		return extDataSource;
	}

	/**
	 * create a new session to the corresponding database. The session must be closed when done with!
	 * 
	 * @param workbookKey
	 * @return
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	public WorkbookDbSession newDbSession(WorkbookId workbookKey) throws StorageException, NotFoundException {
		WorkbookDbSession wkdata;
		try {
			ExtendedDataSource extDataSource = getDataSource(workbookKey);
			wkdata = new WorkbookDbSession(workbookKey, extDataSource.getDdl(), extDataSource.getSchema(),
					connectionWrapperFactory.newInstance(extDataSource.getDataSource()));
		} catch (SQLException e) {
			throw new StorageException(e);
		}
		return wkdata;
	}

	@Override
	public void deleteWorkbook(WorkbookId workbookId) throws StorageException, NotFoundException {
		WorkbookDbSession data = newDbSession(workbookId);
		try {
			// TODO find a better way to give access to services to Workbooks and Sheets
			NetxiliaSystemImpl workbookProcessor = context.getBean(NetxiliaSystemImpl.class);

			workbooksMapper.delete(workbookProcessor, data, workbookId);

			dataSourceConfigurationService.deleteConfigurationForWorkbook(workbookId);

			// remove all storage info for workbook
		} catch (SQLException e) {
			throw new StorageException(e);
		} finally {
			data.close();
		}
	}

	@Override
	public IWorkbook add(DataSourceConfigurationId dataSourceConfigId, WorkbookId workbookId) throws StorageException,
			NotFoundException {
		// TODO Auto-generated method stub
		dataSourceConfigurationService.setConfigurationForWorkbook(workbookId, dataSourceConfigId);
		NetxiliaSystemImpl workbookProcessor = context.getBean(NetxiliaSystemImpl.class);
		return Workbook.newInstance(workbookProcessor, workbookId);
	}

	@Override
	public List<SheetData> loadSheets(WorkbookId workbookId) throws StorageException, NotFoundException {
		WorkbookDbSession data = newDbSession(workbookId);
		try {
			return sheetsMapper.loadSheets(data);
		} finally {
			data.close();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// workbooksMapper.setStorageService(this);
		sheetsMapper.setStorageService(this);
		rowsMapper.setStorageService(this);
		columnsMapper.setStorageService(this);
		cellsMapper.setStorageService(this);
		matrixMapper.setStorageService(this);

		dataSourceConfigurationService.addDataSourceConfigurationListener(this);
	}

	protected DbSheetStorageServiceImpl newSheetStorage(SheetFullName sheetName, SheetsMapper sheetsMapper,
			RowsMapper rowsMapper, ColumnsMapper columnsMapper, CellsMapper cellsMapper, SparseMatrixMapper matrixMapper) {
		return new DbSheetStorageServiceImpl(this, sheetName, sheetsMapper, rowsMapper, columnsMapper, cellsMapper,
				matrixMapper);
	}

	@Override
	public ISheetStorageService getSheetStorage(SheetFullName sheetName, SheetType sheetType) throws StorageException,
			NotFoundException {
		DbSheetStorageServiceImpl entry = sheetCache.get(sheetName);
		if (entry == null) {
			DbSheetStorageServiceImpl newEntry = newSheetStorage(sheetName, sheetsMapper, rowsMapper, columnsMapper,
					cellsMapper, matrixMapper);
			SheetDbSession session = newEntry.newDbSession();
			try {
				newEntry.getOrCreateSheetStorage(session, sheetType);
			} finally {
				session.close();
			}
			entry = sheetCache.putIfAbsent(sheetName, newEntry);
			if (entry == null) {
				entry = newEntry;
			}
		}
		return entry;
	}

	@Override
	public void deleteSheet(SheetFullName sheetName, SheetType sheetType) throws StorageException, NotFoundException {
		ISheetStorageService sheetStorage = getSheetStorage(sheetName, sheetType);
		sheetStorage.deleteSheet();
		sheetCache.remove(sheetName);
	}

	@Override
	public void onModifyConfiguration(DataSourceConfigurationId id) {
		onDeleteConfiguration(id);
	}

	@Override
	public void onDeleteConfiguration(DataSourceConfigurationId id) {
		ExtendedDataSource ds = configurations.remove(id);
		if (ds != null) {
			// TODO do we need here synchronization
			for (Map.Entry<WorkbookId, ExtendedDataSource> entry : dataSources.entrySet()) {
				if (entry.getValue() == ds) {
					dataSources.remove(entry.getKey());
				}
			}
		}
	}

	@Override
	public void onDeleteConfigurationForWorkbook(WorkbookId workbookKey) {
		dataSources.remove(workbookKey);
	}

}
