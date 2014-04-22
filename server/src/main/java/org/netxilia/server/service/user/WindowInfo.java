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

/**
 * This information is used by the javascript client
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class WindowInfo {
	private final WindowIndex windowId;
	private final String username;
	private final boolean undoEnabled;
	private final boolean redoEnabled;

	public WindowInfo(Window window) {
		this.windowId = window.getId();
		this.username = window.getUser().getLogin();
		this.undoEnabled = window.hasUndo();
		this.redoEnabled = window.hasRedo();
	}

	public WindowIndex getWindowId() {
		return windowId;
	}

	public String getUsername() {
		return username;
	}

	public boolean isUndoEnabled() {
		return undoEnabled;
	}

	public boolean isRedoEnabled() {
		return redoEnabled;
	}

}
