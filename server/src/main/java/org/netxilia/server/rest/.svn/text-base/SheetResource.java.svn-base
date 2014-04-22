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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.display.DefaultStyleGroup;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.StyleAttribute;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impexp.ExportException;
import org.netxilia.api.impexp.IExportService;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.SortSpecifier;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.user.User;
import org.netxilia.jaxrs.html.ModelAndView;
import org.netxilia.server.rest.html.sheet.EditSheetModel;
import org.netxilia.server.rest.html.sheet.ISheetModelService;
import org.netxilia.server.rest.html.sheet.SheetModel;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/sheets")
public class SheetResource extends AbstractResource {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SheetResource.class);

	@Autowired
	private ISheetModelService sheetModelService;

	@Autowired
	private IStyleService styleService;

	@Autowired
	private IUserService userService;

	private IExportService jsonExportService;

	public ISheetModelService getSheetModelService() {
		return sheetModelService;
	}

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	public void setSheetModelService(ISheetModelService sheetModelService) {
		this.sheetModelService = sheetModelService;
	}

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public IExportService getJsonExportService() {
		return jsonExportService;
	}

	public void setJsonExportService(IExportService jsonExportService) {
		this.jsonExportService = jsonExportService;
	}

	/**
	 * This is used when clicking in the navigation tree
	 * 
	 * @throws NetxiliaBusinessException
	 */
	@GET
	@Path("/{workbook}/{sheet}/overview")
	@Produces({ MediaType.TEXT_HTML })
	public ModelAndView<SheetModel> overviewSheet(@PathParam("workbook") String workbookName,
			@PathParam("sheet") String sheetName) throws StorageException, NetxiliaBusinessException {
		SheetModel sheetModel = sheetModelService.buildOverviewModel(new SheetFullName(workbookName, sheetName));
		return new ModelAndView<SheetModel>(sheetModel, "/WEB-INF/jsp/sheet/view.jsp");
	}

	@GET
	@Path("/{workbook}/{sheet}")
	@Produces({ MediaType.TEXT_HTML })
	public ModelAndView<SheetModel> getSheet(@PathParam("workbook") String workbookName,
			@PathParam("sheet") String sheetName, @QueryParam("start") int start, @QueryParam("filter") String filter)
			throws StorageException, NetxiliaBusinessException {
		SheetModel sheetModel = sheetModelService.buildModel(new SheetFullName(workbookName, sheetName), start,
				filter != null ? new Formula(filter) : null, false);
		return new ModelAndView<SheetModel>(sheetModel, "/WEB-INF/jsp/sheet/view.jsp");
	}

	@GET
	@Path("/{workbook}/{sheet}/edit")
	@Produces({ MediaType.TEXT_HTML })
	public ModelAndView<EditSheetModel> editSheet(@PathParam("workbook") WorkbookId workbookName,
			@PathParam("sheet") String sheetName, @QueryParam("start") int start, @QueryParam("filter") String filter)
			throws StorageException, NetxiliaBusinessException {
		long t1 = System.currentTimeMillis();
		Collection<StyleDefinition> fontSizes = styleService.getStyleDefinitionsByGroup(workbookName,
				DefaultStyleGroup.FontSize.getGroupId());

		Collection<StyleDefinition> backgrounds = styleService.getStyleDefinitionsByGroup(workbookName,
				DefaultStyleGroup.Background.getGroupId());

		Collection<StyleDefinition> foregrounds = styleService.getStyleDefinitionsByGroup(workbookName,
				DefaultStyleGroup.Foreground.getGroupId());

		Collection<StyleDefinition> formatters = styleService.getStyleDefinitionsByGroup(workbookName,
				DefaultStyleGroup.Formatters.getGroupId());

		Collection<StyleDefinition> all = styleService.getStyleDefinitions(workbookName);
		Collection<StyleDefinition> editors = new ArrayList<StyleDefinition>();

		for (StyleDefinition def : all) {
			String editor = def.getAttribute(StyleAttribute.EDITOR);
			if (editor != null) {
				editors.add(def);
			}
		}

		long t2 = System.currentTimeMillis();
		User user = userService.getCurrentUser();

		SheetFullName sheetFullName = new SheetFullName(workbookName, sheetName);
		SheetModel sheetModel = sheetModelService.buildModel(sheetFullName, 0, null, true);
		// make sure the summary sheet exists
		SheetModel summarySheetModel = sheetModelService.buildSummaryModel(
				SheetFullName.summarySheetName(sheetFullName, user), sheetModel.getColumns().size(), true);

		SheetModel privateSheetModel = sheetModelService.buildModel(
				SheetFullName.privateSheetName(sheetFullName, user), 0, null, true);

		long t3 = System.currentTimeMillis();
		log.info("edit sheet:" + " styles:" + (t2 - t1) + " model:" + (t3 - t2));
		return new ModelAndView<EditSheetModel>(new EditSheetModel(sheetModel, summarySheetModel, privateSheetModel,
				fontSizes, backgrounds, foregrounds, formatters, editors), "/WEB-INF/jsp/sheet/edit.jsp");
	}

	@GET
	@Path("/{workbook}/{sheet}/pdf")
	@Produces("application/pdf")
	public SheetFullName getSheetPdf(@PathParam("workbook") String workbookName, @PathParam("sheet") String sheetName)
			throws NotFoundException, StorageException {
		return new SheetFullName(workbookName, sheetName);
	}

	@GET
	@Path("/{workbook}/{sheet}/excel")
	@Produces("application/vnd.ms-excel")
	public SheetFullName getSheetExcel(@PathParam("workbook") String workbookName, @PathParam("sheet") String sheetName)
			throws NotFoundException, StorageException {
		return new SheetFullName(workbookName, sheetName);
	}

	@GET
	@Path("/{workbook}/{sheet}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public SheetFullName getSheetJson(@Context HttpServletResponse response,
			@PathParam("workbook") String workbookName, @PathParam("sheet") String sheetName) throws ExportException,
			IOException, NetxiliaResourceException, NetxiliaBusinessException {
		OutputStream out = response.getOutputStream();
		jsonExportService.exportSheetTo(getWorkbookProcessor(), new SheetFullName(workbookName, sheetName), out, null);
		out.flush();
		out.close();
		return new SheetFullName(workbookName, sheetName);
	}

	@PUT
	@Path("/{workbook}/{sheet}")
	@Produces({ MediaType.APPLICATION_JSON })
	public SheetFullName newSheet(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName)
			throws NotFoundException, StorageException {
		try {
			SheetFullName sheetFullName = new SheetFullName(workbookName.getKey(), sheetName);
			getWorkbookProcessor().getWorkbook(workbookName)
					.addNewSheet(sheetFullName.getSheetName(), SheetType.normal);
			return sheetFullName;
		} catch (AlreadyExistsException ex) {
			throw new WebApplicationException(ex, Response.Status.CONFLICT);
		}

	}

	@DELETE
	@Path("/{workbook}/{sheet}")
	public void deleteSheet(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName)
			throws NotFoundException, StorageException {

		getWorkbookProcessor().getWorkbook(workbookName).deleteSheet(sheetName);

	}

	@POST
	@Path("/{workbook}/{sheet}/sort/{sortSpec}")
	public void sort(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@PathParam("sortSpec") SortSpecifier sortSpecifier) throws NotFoundException, StorageException {
		ISheet sheet = null;
		sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		sheet.sort(sortSpecifier);
	}

	@POST
	@Path("/{workbook}/{sheet}/alias/{aliasName}/{ref}")
	public void setAlias(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@PathParam("aliasName") String aliasName, @PathParam("ref") AreaReference ref)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = null;
		sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		sheet.sendCommand(SheetCommands.setAlias(new Alias(aliasName.trim()), ref)).getNonBlocking();

	}

	@DELETE
	@Path("/{workbook}/{sheet}/alias/{aliasName}")
	public void deleteAlias(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheet") String sheetName,
			@PathParam("aliasName") String aliasName) throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = null;
		sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		sheet.sendCommand(SheetCommands.setAlias(new Alias(aliasName.trim()), null)).getNonBlocking();

	}

}
