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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impexp.IImportService;
import org.netxilia.api.impexp.ImportException;
import org.netxilia.api.impexp.StringBuilderProcessingConsole;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.jaxrs.html.ModelAndView;
import org.netxilia.server.rest.html.workbook.IWorkbookModelService;
import org.netxilia.server.rest.html.workbook.ImportSheetModel;
import org.netxilia.server.rest.html.workbook.WorkbookModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
@Path("/workbooks")
public class WorkbookResource extends AbstractResource {
	@Autowired
	private IWorkbookModelService workbookModelService;

	private IImportService excelImportService;

	private IImportService jsonImportService;

	public IWorkbookModelService getWorkbookModelService() {
		return workbookModelService;
	}

	public void setWorkbookModelService(IWorkbookModelService workbookModelService) {
		this.workbookModelService = workbookModelService;
	}

	public IImportService getExcelImportService() {
		return excelImportService;
	}

	public void setExcelImportService(IImportService excelImportService) {
		this.excelImportService = excelImportService;
	}

	public IImportService getJsonImportService() {
		return jsonImportService;
	}

	public void setJsonImportService(IImportService jsonImportService) {
		this.jsonImportService = jsonImportService;
	}

	@GET
	@Path("/{workbook}")
	@Produces({ MediaType.TEXT_HTML })
	public ModelAndView<WorkbookModel> getWorkbook(@PathParam("workbook") WorkbookId workbookId)
			throws NotFoundException, StorageException {
		WorkbookModel workbookModel = workbookModelService.buildModel(workbookId);
		return new ModelAndView<WorkbookModel>(workbookModel, "/WEB-INF/jsp/workbook/view.jsp");
	}

	@DELETE
	@Path("/{workbook}")
	public void deleteWorkbook(@PathParam("workbook") WorkbookId workbookId) throws NotFoundException, StorageException {

		getWorkbookProcessor().deleteWorkbook(workbookId);

	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/{workbook}/import")
	@Produces({ MediaType.TEXT_HTML })
	public ModelAndView<ImportSheetModel> importSheetsFromExcel(@PathParam("workbook") WorkbookId workbookId,
			@Context HttpServletRequest request) throws ImportException, IOException {

		long t1 = System.currentTimeMillis();
		List<SheetFullName> sheetNames = new ArrayList<SheetFullName>();
		StringBuilderProcessingConsole console = new StringBuilderProcessingConsole();

		if (ServletFileUpload.isMultipartContent(request)) {
			DiskFileItemFactory factory = new DiskFileItemFactory();

			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = null;
			try {
				items = upload.parseRequest(request);
			} catch (FileUploadException e) {
				e.printStackTrace();
			}

			String format = "json";

			if (items != null) {
				// check first form fields
				for (FileItem item : items) {
					if (!item.isFormField()) {
						continue;
					}
					String name = item.getFieldName();
					String value = item.getString();
					if ("format".equals(name)) {
						format = value;
					}
				}
				IImportService importService = "json".equals(format) ? jsonImportService : excelImportService;
				for (FileItem item : items) {
					if (!item.isFormField() && item.getSize() > 0) {
						sheetNames.addAll(importService.importSheets(getWorkbookProcessor(), workbookId,
								item.getInputStream(), console));
					}
				}
			}
		}
		long t2 = System.currentTimeMillis();
		return new ModelAndView<ImportSheetModel>(new ImportSheetModel(sheetNames, console.getContent().toString(),
				(t2 - t1)), "/WEB-INF/jsp/workbook/importSheet.jsp");
	}

	@PUT
	@Path("/{workbook}")
	public void newWorkbook(@PathParam("workbook") WorkbookId workbookId,
			@FormParam("config") DataSourceConfigurationId id) throws NotFoundException, StorageException {
		try {
			getWorkbookProcessor().addNewWorkbook(id, workbookId);
		} catch (AlreadyExistsException ex) {
			throw new WebApplicationException(ex, Response.Status.CONFLICT);
		}

	}
}
