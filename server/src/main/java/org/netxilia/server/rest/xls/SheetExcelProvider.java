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
package org.netxilia.server.rest.xls;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.impexp.ExportException;
import org.netxilia.api.impexp.IExportService;
import org.netxilia.api.impexp.StringBuilderProcessingConsole;
import org.netxilia.api.model.SheetFullName;
import org.springframework.beans.factory.annotation.Autowired;

@Produces("application/vnd.ms-excel")
@Provider
public class SheetExcelProvider implements MessageBodyWriter<SheetFullName> {
	@Autowired
	private INetxiliaSystem workbookProcessor;

	private IExportService exportService;

	public IExportService getExportService() {
		return exportService;
	}

	public void setExportService(IExportService exportService) {
		this.exportService = exportService;
	}

	public INetxiliaSystem getWorkbookProcessor() {
		return workbookProcessor;
	}

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	@Override
	public long getSize(SheetFullName t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return SheetFullName.class.isAssignableFrom(type);
	}

	@Override
	public void writeTo(SheetFullName sheetName, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {

		StringBuilderProcessingConsole console = new StringBuilderProcessingConsole();
		try {
			exportService.exportSheetTo(workbookProcessor, sheetName, entityStream, console);
		} catch (ExportException e) {
			throw new WebApplicationException(e);
		} catch (NotFoundException e) {
			throw new WebApplicationException(e);
		} catch (NetxiliaResourceException e) {
			throw new WebApplicationException(e);
		} catch (NetxiliaBusinessException e) {
			throw new WebApplicationException(e);
		}
	}
}
