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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.user.IUserService;
import org.netxilia.jaxrs.html.ModelAndView;
import org.netxilia.server.rest.exception.SystemNotInitializedException;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/users")
public class UserResource extends AbstractResource {

	@Autowired
	private IUserService userService;

	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	@GET
	@Path("/login")
	@Produces(MediaType.TEXT_HTML)
	public ModelAndView<String> showLogin() throws SystemNotInitializedException, NetxiliaResourceException,
			NetxiliaBusinessException {
		if (userService.isAdminAccountCreated()) {
			return new ModelAndView<String>(null, "/WEB-INF/jsp/user/login.jsp");
		}
		throw new SystemNotInitializedException();

	}

	// @GET
	// @Path("/home")
	// @Produces(MediaType.TEXT_HTML)
	// public ModelAndView<String> home() throws SystemNotInitializedException, StorageException {
	// return new ModelAndView<String>(null, "/WEB-INF/jsp/admin/init.jsp");
	// }
}
