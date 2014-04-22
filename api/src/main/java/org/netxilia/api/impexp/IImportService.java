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
package org.netxilia.api.impexp;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.WorkbookId;

/**
 * This is a generic service to import Sheets from different sources.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IImportService {
	public List<SheetFullName> importSheets(INetxiliaSystem workbookProcessor, WorkbookId workbookName, URL url,
			IProcessingConsole console) throws ImportException;

	public List<SheetFullName> importSheets(INetxiliaSystem workbookProcessor, WorkbookId workbookName,
			InputStream inputStream, IProcessingConsole console) throws ImportException;
}
