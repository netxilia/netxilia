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
package org.netxilia.api.model;

import java.util.Collection;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.event.IWorkbookEventListener;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;

/**
 * This interface represent a workbook that is seen only as a collection of sheet names. Also a workbook is associated
 * with a datasource because all the sheets from this workbook are stored togheter in the same datasource.
 * 
 * To access a specific sheet use the {@link INetxiliaSystem} to open it in the proper open mode.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IWorkbook {

	WorkbookId getId();

	String getName();

	Collection<ISheet> getSheets();

	// ******** sheets *******
	public ISheet getSheet(String sheetName) throws StorageException, NotFoundException;

	public boolean hasSheet(String sheetName) throws StorageException;

	public ISheet addNewSheet(String name, SheetType type) throws StorageException, NotFoundException,
			AlreadyExistsException;

	public void deleteSheet(String sheetFullName) throws NotFoundException, StorageException;

	public void addListener(IWorkbookEventListener listener);

	public void removeListener(IWorkbookEventListener listener);

}
