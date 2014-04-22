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

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.utils.Pair;
import org.netxilia.jaxrs.html.ModelAndView;
import org.netxilia.server.rest.html.datasource.DataSourceConfigurationModel;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/ds")
@Produces( { MediaType.APPLICATION_JSON })
public class DataSourceResource extends AbstractResource {
	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	@GET
	public List<DataSourceConfiguration> list() throws StorageException {
		return dataSourceConfigurationService.findAll();
	}

	@GET
	@Path("/workbooks")
	public List<Pair<WorkbookId, DataSourceConfigurationId>> listWorkbooks() throws StorageException {
		return dataSourceConfigurationService.findAllWorkbooksConfigurations();
	}

	@GET
	@Path("/{id}")
	public DataSourceConfiguration getById(@PathParam("id") DataSourceConfigurationId id) throws StorageException,
			NotFoundException {
		return dataSourceConfigurationService.load(id);
	}

	@GET
	@Path("/{id}/test")
	public String test(@PathParam("id") DataSourceConfigurationId id) throws StorageException, NotFoundException {
		try {
			dataSourceConfigurationService.test(id);
			return "OK";
		} catch (SQLException e) {
			return e.getMessage();
		}
	}

	@POST
	@Path("/{id}")
	public void save(@PathParam("id") DataSourceConfigurationId id, @FormParam("name") String name,
			@FormParam("description") String description, @FormParam("driver") String driver,
			@FormParam("url") String url, @FormParam("username") String username, @FormParam("password") String password)
			throws StorageException {
		DataSourceConfiguration cfg = new DataSourceConfiguration(id, name, description, driver, url, username,
				password);
		dataSourceConfigurationService.save(cfg);
	}

	@PUT
	public DataSourceConfigurationId add(@FormParam("name") String name, @FormParam("description") String description,
			@FormParam("driver") String driver, @FormParam("url") String url, @FormParam("username") String username,
			@FormParam("password") String password, @FormParam("workbooks") String workbooksString)
			throws StorageException {
		DataSourceConfiguration cfg = new DataSourceConfiguration(null, name, description, driver, url, username,
				password);
		cfg = dataSourceConfigurationService.save(cfg);
		return cfg.getId();
	}

	@DELETE
	@Path("/{id}")
	public void remove(@PathParam("id") DataSourceConfigurationId id) throws StorageException, NotFoundException {
		dataSourceConfigurationService.delete(id);
	}

	@GET
	@Path("/{id}/edit")
	@Produces(MediaType.TEXT_HTML)
	public ModelAndView<DataSourceConfigurationModel> edit(@PathParam("id") DataSourceConfigurationId id)
			throws StorageException, IllegalAccessException, NotFoundException {

		DataSourceConfiguration cfg = dataSourceConfigurationService.load(id);
		List<WorkbookId> workbookIds = dataSourceConfigurationService.findAllWorkbooksConfigurationsForDatasource(id);
		return new ModelAndView<DataSourceConfigurationModel>(new DataSourceConfigurationModel(cfg, workbookIds),
				"/WEB-INF/jsp/ds/edit.jsp");
	}

	@GET
	@Path("/editNew")
	@Produces(MediaType.TEXT_HTML)
	public ModelAndView<DataSourceConfigurationModel> editNew() throws StorageException, IllegalAccessException,
			NotFoundException {

		DataSourceConfiguration cfg = new DataSourceConfiguration(null, "New Datasource", "", "", "", "", "");
		return new ModelAndView<DataSourceConfigurationModel>(new DataSourceConfigurationModel(cfg, null),
				"/WEB-INF/jsp/ds/edit.jsp");
	}

	@POST
	@Path("/workbooks/{key}")
	public void setConfigurationForWorkbook(@PathParam("key") WorkbookId workbookKey,
			@FormParam("config") DataSourceConfigurationId id) throws StorageException, NotFoundException {
		dataSourceConfigurationService.setConfigurationForWorkbook(workbookKey, id);
	}

	@DELETE
	@Path("/workbooks/{key}")
	public void deleteConfigurationForWorkbook(@PathParam("key") WorkbookId workbookKey) throws StorageException,
			NotFoundException {
		dataSourceConfigurationService.deleteConfigurationForWorkbook(workbookKey);
	}

}
