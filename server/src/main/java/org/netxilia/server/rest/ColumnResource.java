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
package org.netxilia.server.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.netxilia.api.command.ColumnCommands;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.Range;

@Path("/columns")
public class ColumnResource extends AbstractResource {

	@PUT
	@Path("/{workbook}/{sheet}/{column}")
	public void insert(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@PathParam("column") int pos) throws NotFoundException, StorageException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		sheet.sendCommand(ColumnCommands.insert(Range.range(pos)));
	}

	@DELETE
	@Path("/{workbook}/{sheet}/{column}")
	public void delete(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@PathParam("column") int pos) throws NotFoundException, StorageException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		sheet.sendCommand(ColumnCommands.delete(Range.range(pos)));

	}

	@POST
	@Path("/{workbook}/{sheet}/{column}")
	public void modify(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@PathParam("column") int pos, @FormParam("width") int width) throws NotFoundException, StorageException {

		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		sheet.sendCommand(ColumnCommands.width(Range.range(pos), width));
	}
}
