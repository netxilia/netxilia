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

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.storage.IDataSourceConfigurationListener;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.utils.Pair;

public class MockDataSourceConfigurationService implements IDataSourceConfigurationService {

	@Override
	public DataSource buildDataSource(DataSourceConfiguration cfg) {

		return null;
	}

	@Override
	public void delete(DataSourceConfigurationId id) throws StorageException, NotFoundException {

	}

	@Override
	public void deleteConfigurationForWorkbook(WorkbookId workbookKey) throws StorageException, NotFoundException {

	}

	@Override
	public List<DataSourceConfiguration> findAll() throws StorageException {

		return null;
	}

	@Override
	public List<Pair<WorkbookId, DataSourceConfigurationId>> findAllWorkbooksConfigurations() throws StorageException {

		return null;
	}

	@Override
	public DataSourceConfiguration load(DataSourceConfigurationId id) throws StorageException, NotFoundException {

		return null;
	}

	@Override
	public DataSourceConfiguration loadByWorkbook(WorkbookId workbookKey) throws StorageException, NotFoundException {
		return new DataSourceConfiguration(new DataSourceConfigurationId(0), "test", "test", "test", "test", "test",
				"test");
	}

	@Override
	public DataSourceConfiguration save(DataSourceConfiguration cfg) throws StorageException {

		return null;
	}

	@Override
	public void setConfigurationForWorkbook(WorkbookId workbookKey, DataSourceConfigurationId id)
			throws StorageException, NotFoundException {

	}

	@Override
	public void test(DataSourceConfigurationId id) throws StorageException, NotFoundException, SQLException {

	}

	@Override
	public List<WorkbookId> findAllWorkbooksConfigurationsForDatasource(DataSourceConfigurationId id)
			throws StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDataSourceConfigurationListener(IDataSourceConfigurationListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeDataSourceConfigurationListener(IDataSourceConfigurationListener listener) {
		// TODO Auto-generated method stub

	}

}
