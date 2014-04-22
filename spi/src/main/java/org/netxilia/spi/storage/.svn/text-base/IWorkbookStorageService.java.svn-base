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
package org.netxilia.spi.storage;

import java.util.List;

import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfigurationId;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 *         The implementation handle the persistent storage of a workbook.
 */
public interface IWorkbookStorageService {
	// workbooks
	IWorkbook add(DataSourceConfigurationId dataSourceConfigId, WorkbookId workbookId) throws StorageException,
			NotFoundException, AlreadyExistsException;

	void deleteWorkbook(WorkbookId workbookId) throws StorageException, NotFoundException;

	List<SheetData> loadSheets(WorkbookId workbookName) throws StorageException, NotFoundException;

	/**
	 * returns the storage for the given sheet. Creates the sheet if necessary.
	 * 
	 * @param sheetName
	 * @return
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	ISheetStorageService getSheetStorage(SheetFullName sheetName, SheetType sheetType) throws StorageException,
			NotFoundException;

	void deleteSheet(SheetFullName sheetName, SheetType sheetType) throws StorageException, NotFoundException;

}
