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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.user.IUserService;
import org.netxilia.server.service.user.Window;
import org.netxilia.server.service.user.WindowIndex;
import org.netxilia.server.service.user.WindowInfo;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
@Path("/windows")
@Produces({ MediaType.APPLICATION_JSON })
public class WindowResource extends AbstractResource {

	@Context
	private HttpServletRequest request;

	@Autowired
	private IUserService userService;

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	@PUT
	@Path("/{workbook}/{sheet}")
	public WindowIndex register(@PathParam("workbook") String workbookName, @PathParam("sheet") String sheetName)
			throws StorageException, NotFoundException {
		return getWindowProcessor().registerSheet(request.getSession().getId(), userService.getCurrentUser(),
				new SheetFullName(workbookName, sheetName));
	}

	@DELETE
	@Path("/{window}")
	public void terminate(@PathParam("window") WindowIndex windowId) throws NotFoundException {
		getWindowProcessor().terminate(checkWindowId(request, windowId));
	}

	@GET
	@Path("/{workbook}/{sheet}")
	public List<WindowInfo> getWindowsForSheet(@PathParam("workbook") String workbookName,
			@PathParam("sheet") String sheetName) {
		List<Window> windows = getWindowProcessor().getWindows(new SheetFullName(workbookName, sheetName));
		if (windows == null) {
			return null;
		}
		List<WindowInfo> windowInfos = new ArrayList<WindowInfo>();
		for (Window window : windows) {
			windowInfos.add(new WindowInfo(window));
		}
		return windowInfos;
	}

	@POST
	@Path("/{window}/notifySelection/{ref}")
	public void notifySelection(@PathParam("window") WindowIndex windowId, @PathParam("ref") AreaReference areaRef)
			throws NotFoundException {
		getWindowProcessor().notifySelection(checkWindowId(request, windowId), areaRef);
	}

	@PUT
	@Path("/{window}/undo")
	public void undo(@PathParam("window") WindowIndex windowId) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		getWindow(checkWindowId(request, windowId)).undo();
	}

	@PUT
	@Path("/{window}/redo")
	public void redo(@PathParam("window") WindowIndex windowId) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		getWindow(checkWindowId(request, windowId)).redo();
	}

}
