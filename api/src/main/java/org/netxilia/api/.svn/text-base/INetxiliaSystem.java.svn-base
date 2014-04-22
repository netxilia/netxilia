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
package org.netxilia.api;

import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfigurationId;

/**
 * This class is the main entry to the Netxilia's spreadsheet system. It offers services to open a spreadsheet, delete,
 * add a new one, add or remove listeners.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface INetxiliaSystem {

	public IWorkbook addNewWorkbook(DataSourceConfigurationId dataSourceConfigId, WorkbookId workbookId)
			throws StorageException, NotFoundException, AlreadyExistsException;

	public IWorkbook getWorkbook(WorkbookId workbookName) throws StorageException, NotFoundException;

	public void deleteWorkbook(WorkbookId workbook) throws StorageException, NotFoundException;

}
