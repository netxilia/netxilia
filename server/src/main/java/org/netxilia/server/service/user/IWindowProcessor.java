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
package org.netxilia.server.service.user;

import java.util.List;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.user.User;

/**
 * This service handles all the connections from the clients. A connected client will have a separate window for each
 * sheet he's editing.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IWindowProcessor {

	public WindowIndex registerSheet(String httpSessionId, User user, SheetFullName sheetFullName)
			throws StorageException, NotFoundException;

	// public void setCurrentCell(windowId windowId, int column, int row);

	/**
	 * 
	 * @param windowId
	 * @return true if the given windowId is registered
	 */
	public boolean isWindowIdValid(WindowIndex windowId);

	public Window getWindow(WindowIndex windowId);

	/**
	 * terminates (closes) the given windowId and remove all state associated with that window
	 * 
	 * @param windowId
	 * @throws NotFoundException
	 */
	public void terminate(WindowIndex windowId) throws NotFoundException;

	/**
	 * terminates the session with the given id, to terminates all the windows associated with that session, if any
	 * 
	 * @param httpSessionId
	 */
	public void terminateSession(String httpSessionId);

	/**
	 * 
	 * @param sheetFullName
	 * @return the list of all the windows that are registered for the given sheet
	 */
	public List<Window> getWindows(SheetFullName sheetFullName);

	/**
	 * Called to inform other windows looking on the same cells about the selection the used made in the window with the
	 * given ID.
	 * 
	 * @param windowId
	 * @param areaRef
	 */
	public void notifySelection(WindowIndex windowId, AreaReference areaRef) throws NotFoundException;

}
