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
package org.netxilia.server.service.event;

import org.netxilia.api.reference.AreaReference;
import org.netxilia.server.service.user.WindowInfo;

public class WindowClientEvent extends WorkbookClientEvent {
	private final WindowInfo windowInfo;
	private final String selectedArea;

	public WindowClientEvent(WindowInfo windowInfo, String sheetName, ClientEventType type) {
		this(windowInfo, sheetName, type, null);
	}

	public WindowClientEvent(WindowInfo windowInfo, String sheetName, ClientEventType eventType, AreaReference areaRef) {
		super(sheetName, eventType);
		this.windowInfo = windowInfo;
		this.selectedArea = areaRef != null ? areaRef.formatAsString() : null;
	}

	public WindowInfo getWindowInfo() {
		return windowInfo;
	}

	public String getSelectedArea() {
		return selectedArea;
	}

	@Override
	public String toString() {
		return "WindowClientEvent [selectedArea=" + selectedArea + ", windowInfo=" + windowInfo + "]";
	}

}
