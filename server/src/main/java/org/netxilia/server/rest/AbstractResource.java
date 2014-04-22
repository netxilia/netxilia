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

import javax.servlet.http.HttpServletRequest;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.model.ISheet;
import org.netxilia.server.service.user.IWindowProcessor;
import org.netxilia.server.service.user.Window;
import org.netxilia.server.service.user.WindowIndex;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractResource {
	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private IWindowProcessor windowProcessor;

	public IWindowProcessor getWindowProcessor() {
		return windowProcessor;
	}

	public void setWindowProcessor(IWindowProcessor windowProcessor) {
		this.windowProcessor = windowProcessor;
	}

	public INetxiliaSystem getWorkbookProcessor() {
		return workbookProcessor;
	}

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	protected void executeCommand(ISheet sheet, ICellCommand cmd, WindowIndex windowId)
			throws NetxiliaResourceException, NotFoundException, NetxiliaBusinessException {
		if (windowId == null) {
			sheet.sendCommand(cmd);
			return;
		}
		getWindow(windowId).execute(cmd);

	}

	protected WindowIndex checkWindowId(HttpServletRequest request, WindowIndex windowId)
			throws IllegalArgumentException {
		if (windowId != null) {
			Window window = windowProcessor.getWindow(windowId);
			if (window == null) {
				throw new IllegalArgumentException("Illegal window id:" + windowId);
			}
			if (!window.getHttpSessionId().equals(request.getSession().getId())) {
				throw new IllegalArgumentException("Tindow id:" + windowId + " does not belong to the user's session");
			}

		}
		return windowId;
	}

	protected Window getWindow(WindowIndex windowId) throws NotFoundException {
		Window window = getWindowProcessor().getWindow(windowId);
		if (window == null) {
			throw new NotFoundException("Window " + windowId + " not found");
		}
		return window;
	}
}
