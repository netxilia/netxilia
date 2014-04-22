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
package org.netxilia.server.rest.html.workbook.impl;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.server.rest.html.workbook.IWorkbookModelService;
import org.netxilia.server.rest.html.workbook.WorkbookModel;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkbookModelServiceImpl implements IWorkbookModelService {
	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	@Override
	public WorkbookModel buildModel(WorkbookId workbookId) throws StorageException, NotFoundException {
		DataSourceConfiguration cfg = dataSourceConfigurationService.loadByWorkbook(workbookId);
		return new WorkbookModel(workbookId, cfg);
	}

}
