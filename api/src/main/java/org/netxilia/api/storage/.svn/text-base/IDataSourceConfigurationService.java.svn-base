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
package org.netxilia.api.storage;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.utils.Pair;

public interface IDataSourceConfigurationService {
	DataSourceConfiguration load(DataSourceConfigurationId id) throws StorageException, NotFoundException;

	List<DataSourceConfiguration> findAll() throws StorageException;

	DataSourceConfiguration save(DataSourceConfiguration cfg) throws StorageException;

	void delete(DataSourceConfigurationId id) throws StorageException, NotFoundException;

	void test(DataSourceConfigurationId id) throws StorageException, NotFoundException, SQLException;

	DataSourceConfiguration loadByWorkbook(WorkbookId workbookKey) throws StorageException, NotFoundException;

	void setConfigurationForWorkbook(WorkbookId workbookKey, DataSourceConfigurationId id) throws StorageException,
			NotFoundException;

	void deleteConfigurationForWorkbook(WorkbookId workbookKey) throws StorageException, NotFoundException;

	List<Pair<WorkbookId, DataSourceConfigurationId>> findAllWorkbooksConfigurations() throws StorageException;

	/**
	 * Should use pooled datasources
	 * 
	 * @param cfg
	 * @return
	 */
	DataSource buildDataSource(DataSourceConfiguration cfg);

	List<WorkbookId> findAllWorkbooksConfigurationsForDatasource(DataSourceConfigurationId id) throws StorageException;

	public void addDataSourceConfigurationListener(IDataSourceConfigurationListener listener);

	public void removeDataSourceConfigurationListener(IDataSourceConfigurationListener listener);

}
