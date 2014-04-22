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

import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.user.Email;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.user.Role;
import org.netxilia.api.user.User;
import org.netxilia.jaxrs.html.ModelAndView;
import org.netxilia.server.rest.exception.RedirectionException;
import org.netxilia.server.rest.html.admin.AdminInitModel;
import org.netxilia.server.rest.html.admin.IAdminModelService;
import org.netxilia.server.service.startup.IStartupService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Path("/admin")
public class AdminResource extends AbstractResource implements ApplicationContextAware {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AdminResource.class);

	@Autowired
	private IUserService userService;

	private AuthenticationManager authenticationManager;

	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	@Autowired
	private IAdminModelService adminModelService;

	private ApplicationContext applicationContext;

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public IAdminModelService getAdminModelService() {
		return adminModelService;
	}

	public void setAdminModelService(IAdminModelService adminModelService) {
		this.adminModelService = adminModelService;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	@GET
	@Path("/init")
	@Produces(MediaType.TEXT_HTML)
	public ModelAndView<AdminInitModel> init() throws IllegalAccessException, NetxiliaResourceException,
			NetxiliaBusinessException {
		if (userService.isAdminAccountCreated()) {
			throw new RedirectionException(HomeResource.class);
		}
		AdminInitModel model = adminModelService.buildInitModel();
		return new ModelAndView<AdminInitModel>(model, "/WEB-INF/jsp/admin/init.jsp");
	}

	@POST
	@Path("/create")
	@Produces(MediaType.TEXT_HTML)
	public void create(@FormParam("login") String login, @FormParam("password") String password,
			@FormParam("email") Email email, @FormParam("ds.id") DataSourceConfigurationId dataSourceConfigurationId,
			@FormParam("ds.driver") String driver, @FormParam("ds.url") String url,
			@FormParam("ds.username") String dsUsername, @FormParam("ds.password") String dsPassword,//
			@FormParam("createDemo") boolean createDemo) throws IllegalAccessException, NetxiliaResourceException,
			NetxiliaBusinessException {
		if (userService.isAdminAccountCreated()) {
			throw new IllegalAccessException("");
		}

		// TODO use validation
		if (StringUtils.isEmpty(login)) {
			throw new IllegalArgumentException("Login cannot be empty!");
		}
		if (StringUtils.isEmpty(password)) {
			throw new IllegalArgumentException("Password cannot be empty!");
		}

		DataSourceConfiguration ds = null;
		if (dataSourceConfigurationId.getId() < 0) {
			// new datasource
			ds = new DataSourceConfiguration(null, "SYSTEM", "The SYSTEM Datasource", driver, url, dsUsername,
					dsPassword);
		} else {
			DataSourceConfiguration prevDs = dataSourceConfigurationService.load(dataSourceConfigurationId);
			ds = new DataSourceConfiguration(prevDs.getId(), prevDs.getName(), prevDs.getDescription(), driver, url,
					dsUsername, dsPassword);
		}
		ds = dataSourceConfigurationService.save(ds);
		dataSourceConfigurationService.setConfigurationForWorkbook(userService.getWorkbookId(), ds.getId());

		User user = new User();
		user.setLogin(login);
		user.setPassword(password);
		user.setEmail(email);
		user.setRoles(new Role[] { Role.ROLE_ADMIN });

		userService.addUser(user);

		Map<String, IStartupService> startupServices = applicationContext.getBeansOfType(IStartupService.class);
		for (IStartupService service : startupServices.values()) {
			try {
				service.startup(ds, user, createDemo);
			} catch (Exception ex) {
				log.error("Could not execute startup service:" + ex, ex);
			}
		}
		authenticateWithSpring(user);
		throw new RedirectionException(HomeResource.class);
	}

	private void authenticateWithSpring(User user) {
		try {
			// TODO - this is not working - fix it
			Authentication request = new UsernamePasswordAuthenticationToken(user.getLogin(), user.getPassword());
			Authentication result = authenticationManager.authenticate(request);
			SecurityContextHolder.getContext().setAuthentication(result);
		} catch (AuthenticationException e) {
			log.error("Could not authenticate with spring: " + e, e);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
