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
package org.netxilia.server.service.user.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.event.CellEvent;
import org.netxilia.api.event.ColumnEvent;
import org.netxilia.api.event.ISheetEventListener;
import org.netxilia.api.event.RowEvent;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.user.User;
import org.netxilia.server.service.event.ClientEventType;
import org.netxilia.server.service.event.IClientEventConversionService;
import org.netxilia.server.service.event.WindowClientEvent;
import org.netxilia.server.service.event.WorkbookClientEvent;
import org.netxilia.server.service.user.IWindowProcessor;
import org.netxilia.server.service.user.Window;
import org.netxilia.server.service.user.WindowIndex;
import org.netxilia.server.service.user.WindowInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class WindowProcessorImpl implements IWindowProcessor, ISheetEventListener {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WindowProcessorImpl.class);

	private final ConcurrentMap<WindowIndex, Window> windows = new ConcurrentHashMap<WindowIndex, Window>();
	private final ConcurrentMap<SheetFullName, Collection<Window>> windowsBySheet = new ConcurrentHashMap<SheetFullName, Collection<Window>>();

	private AtomicLong nextWindowId = new AtomicLong(0);

	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private IClientEventConversionService clientEventConversionService;

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	public IClientEventConversionService getClientEventConversionService() {
		return clientEventConversionService;
	}

	public void setClientEventConversionService(IClientEventConversionService clientEventConversionService) {
		this.clientEventConversionService = clientEventConversionService;
	}

	private void addWindow(Window window, ISheet sheet) throws StorageException {
		Collection<Window> windowList = windowsBySheet.get(sheet.getFullName());
		if (windowList == null) {
			Collection<Window> newWindowList = new ConcurrentLinkedQueue<Window>();
			windowList = windowsBySheet.putIfAbsent(sheet.getFullName(), newWindowList);
			if (windowList == null) {
				windowList = newWindowList;
			}
		}
		windowList.add(window);

		// add listeners for sheet
		sheet.addListener(this);
	}

	public synchronized WindowIndex registerSheet(String httpSessionId, User user, SheetFullName sheetFullName)
			throws StorageException, NotFoundException {
		SheetFullName summarySheetFullName = SheetFullName.summarySheetName(sheetFullName, user);
		SheetFullName privateSheetFullName = SheetFullName.privateSheetName(sheetFullName, user);

		ISheet sheet = workbookProcessor.getWorkbook(sheetFullName.getWorkbookId()).getSheet(
				sheetFullName.getSheetName());
		ISheet summarySheet = workbookProcessor.getWorkbook(summarySheetFullName.getWorkbookId()).getSheet(
				summarySheetFullName.getSheetName());
		ISheet privateSheet = workbookProcessor.getWorkbook(privateSheetFullName.getWorkbookId()).getSheet(
				privateSheetFullName.getSheetName());

		Window window = new Window(new WindowIndex(nextWindowId.getAndIncrement()), httpSessionId, user, sheet,
				summarySheet, privateSheet);
		windows.put(window.getId(), window);
		addWindow(window, sheet);
		addWindow(window, summarySheet);
		addWindow(window, privateSheet);

		// push an event to all the windows looking on the same sheet
		informSiblingWindows(window, ClientEventType.windowCreated, null);

		return window.getId();
	}

	/**
	 * inform the windows registered for the same sheet when a window is created or terminated
	 * 
	 * @param window
	 * @param eventType
	 * @param areaRef
	 */
	private void informSiblingWindows(Window window, ClientEventType eventType, AreaReference areaRef) {
		String sheetName = areaRef != null ? areaRef.getSheetName() : window.getSheetFullName().getSheetName();
		Collection<Window> windowList = windowsBySheet.get(window.getSheetFullName());

		if (windowList != null) {
			WindowClientEvent windowEvent = new WindowClientEvent(new WindowInfo(window), sheetName, eventType, areaRef);
			for (Window otherWindow : windowList) {
				if (otherWindow != window) {
					otherWindow.pushEvent(windowEvent);
				}
			}
		}
	}

	private void distributeEvents(SheetFullName sheetFullName, List<WorkbookClientEvent> clientEvents) {
		if (clientEvents == null || clientEvents.size() == 0) {
			return;
		}
		Collection<Window> affectedWindows = windowsBySheet.get(sheetFullName);
		if (affectedWindows == null) {
			return;
		}

		for (Window window : affectedWindows) {
			for (WorkbookClientEvent clientEvent : clientEvents) {
				window.pushEvent(clientEvent);
			}
		}
	}

	public synchronized boolean isWindowIdValid(WindowIndex windowId) {
		return windows.get(windowId) != null;
	}

	@Override
	public synchronized void onCellEvent(CellEvent cellEvent) {
		try {
			List<WorkbookClientEvent> clientEvents = clientEventConversionService.convert(cellEvent);
			distributeEvents(cellEvent.getSheetName(), clientEvents);
		} catch (Exception e) {
			log.error("CellEvent: " + cellEvent + " could not be converted: " + e, e);
			return;
		}

	}

	@Override
	public synchronized void onColumnEvent(ColumnEvent columnEvent) {
		try {
			List<WorkbookClientEvent> clientEvents = clientEventConversionService.convert(columnEvent);
			distributeEvents(columnEvent.getSheetName(), clientEvents);
		} catch (Exception e) {
			log.error("ColumnEvent: " + columnEvent + " could not be converted: " + e, e);
			return;
		}
	}

	@Override
	public synchronized void onRowEvent(RowEvent rowEvent) {
		try {
			List<WorkbookClientEvent> clientEvents = clientEventConversionService.convert(rowEvent);
			distributeEvents(rowEvent.getSheetName(), clientEvents);
		} catch (Exception e) {
			log.error("RowEvent: " + rowEvent + " could not be converted: " + e, e);
			return;
		}
	}

	@Override
	public synchronized void onSheetEvent(SheetEvent sheetEvent) {
		try {
			List<WorkbookClientEvent> clientEvents = clientEventConversionService.convert(sheetEvent);
			distributeEvents(sheetEvent.getSheetName(), clientEvents);
		} catch (Exception e) {
			log.error("SheetEvent: " + sheetEvent + " could not be converted: " + e, e);
			return;
		}
	}

	@Override
	public synchronized Window getWindow(WindowIndex windowId) {
		return windows.get(windowId);
	}

	private void removeWindow(Window window, SheetFullName sheetName) throws NotFoundException {
		Collection<Window> windowsForSheet = windowsBySheet.get(sheetName);
		if (windowsForSheet != null) {
			windowsForSheet.remove(window);
		}
		// if the list is empty - remove it from the map
		if (windowsForSheet == null || windowsForSheet.size() == 0) {
			windowsBySheet.remove(window.getSheetFullName());
			workbookProcessor.getWorkbook(sheetName.getWorkbookId()).getSheet(sheetName.getSheetName())
					.removeListener(this);
		}

	}

	@Override
	public synchronized void terminate(WindowIndex windowId) throws NotFoundException {

		try {
			// or should use the user's credentials !?
			AclPrivilegedMode.set();
			Window window = windows.remove(windowId);
			if (window == null) {
				throw new NotFoundException("Window [" + windowId + "] not found");
			}
			log.info("Terminated window: " + windowId);
			removeWindow(window, window.getSheetFullName());
			removeWindow(window, window.getSummarySheetFullName());
			removeWindow(window, window.getPrivateSheetFullName());

			// push an event to all the windows looking on the same sheet
			informSiblingWindows(window, ClientEventType.windowTerminated, null);
		} finally {
			AclPrivilegedMode.clear();
		}
	}

	@Override
	public synchronized List<Window> getWindows(SheetFullName sheetFullName) {
		Collection<Window> windowsForSheet = windowsBySheet.get(sheetFullName);
		return windowsForSheet != null ? new ArrayList<Window>(windowsForSheet) : null;
	}

	@Override
	public synchronized void notifySelection(WindowIndex windowId, AreaReference areaRef) throws NotFoundException {
		Window window = windows.get(windowId);
		if (window == null) {
			throw new NotFoundException("Window [" + windowId + "] not found");
		}
		informSiblingWindows(window, ClientEventType.cellSelected, areaRef);
	}

	private List<WindowIndex> getWindowsForSession(String httpSessionId) {
		List<WindowIndex> ids = new ArrayList<WindowIndex>();
		for (Window window : windows.values()) {
			if (httpSessionId.equals(window.getHttpSessionId())) {
				ids.add(window.getId());
			}
		}
		return ids;
	}

	@Override
	public synchronized void terminateSession(String httpSessionId) {
		List<WindowIndex> windowIds = getWindowsForSession(httpSessionId);
		for (WindowIndex id : windowIds) {
			try {
				terminate(id);
			} catch (NotFoundException e) {
				// should not happen
				log.error("Unexpected exception terminating window:" + id + ":" + e, e);
			}
		}
	}
}
