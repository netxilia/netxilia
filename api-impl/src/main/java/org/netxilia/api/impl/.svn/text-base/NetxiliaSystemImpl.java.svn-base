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
package org.netxilia.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.IPreloadedFormulaContextFactory;
import org.netxilia.api.impl.model.SheetNames;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.user.IAclService;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.user.Permission;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.spi.formula.IFormulaCalculatorFactory;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.storage.IWorkbookStorageService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is the core of the Netxilia sheet system. It allows client code access to the sheets and manages events
 * occured after a sheet was modified.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class NetxiliaSystemImpl implements INetxiliaSystem {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NetxiliaSystemImpl.class);

	@Autowired
	private IWorkbookStorageService storageService;

	@Autowired
	private IFormulaParser formulaParser;

	private final Map<WorkbookId, Workbook> workbooks = new HashMap<WorkbookId, Workbook>();

	@Autowired
	private IStyleService styleService;

	@Autowired
	private IAclService aclService;

	@Autowired
	private IUserService userService;

	@Autowired
	private IGenericValueParseService parseService;

	@Autowired
	private ISpringUserService springUserService;

	@Autowired
	private IFormulaCalculatorFactory formulaCalculatorFactory;

	@Autowired
	private IExecutorServiceFactory executorServiceFactory;

	@Autowired
	private IMoreCellCommands moreCellCommands;

	@Autowired
	private IPreloadedFormulaContextFactory preloadContextFactory;

	private boolean sheetInitializationEnabled = true;

	public NetxiliaSystemImpl() {

	}

	public IWorkbookStorageService getStorageService() {
		return storageService;
	}

	public void setStorageService(IWorkbookStorageService storageService) {
		this.storageService = storageService;
	}

	public IMoreCellCommands getMoreCellCommands() {
		return moreCellCommands;
	}

	public void setMoreCellCommands(IMoreCellCommands moreCellCommands) {
		this.moreCellCommands = moreCellCommands;
	}

	public IPreloadedFormulaContextFactory getPreloadContextFactory() {
		return preloadContextFactory;
	}

	public void setPreloadContextFactory(IPreloadedFormulaContextFactory preloadContextFactory) {
		this.preloadContextFactory = preloadContextFactory;
	}

	public IExecutorServiceFactory getExecutorServiceFactory() {
		return executorServiceFactory;
	}

	public void setExecutorServiceFactory(IExecutorServiceFactory executorServiceFactory) {
		this.executorServiceFactory = executorServiceFactory;
	}

	public IFormulaParser getFormulaParser() {
		return formulaParser;
	}

	public void setFormulaParser(IFormulaParser formulaParser) {
		this.formulaParser = formulaParser;
	}

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	public IAclService getAclService() {
		return aclService;
	}

	public void setAclService(IAclService aclService) {
		this.aclService = aclService;
	}

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public ISpringUserService getSpringUserService() {
		return springUserService;
	}

	public void setSpringUserService(ISpringUserService springUserService) {
		this.springUserService = springUserService;
	}

	public IGenericValueParseService getParseService() {
		return parseService;
	}

	public void setParseService(IGenericValueParseService parseService) {
		this.parseService = parseService;
	}

	public IFormulaCalculatorFactory getFormulaCalculatorFactory() {
		return formulaCalculatorFactory;
	}

	public void setFormulaCalculatorFactory(IFormulaCalculatorFactory formulaCalculatorFactory) {
		this.formulaCalculatorFactory = formulaCalculatorFactory;
	}

	@Override
	public Workbook getWorkbook(WorkbookId workbookId) throws StorageException, NotFoundException {
		aclService.checkPermission(workbookId, Permission.read);

		synchronized (workbooks) {
			// TODO increase concurrency here
			Workbook workbook = workbooks.get(workbookId);
			if (workbook == null) {
				List<SheetData> sheets = storageService.loadSheets(workbookId);
				workbook = Workbook.newInstance(this, workbookId, sheets);

				if (workbook == null) {
					throw new NotFoundException("Could not load the workbook");
				}

				workbooks.put(workbookId, workbook);
			}
			return workbook;
		}
	}

	public void deleteWorkbook(WorkbookId workbookId) throws StorageException, NotFoundException {
		boolean wasSet = AclPrivilegedMode.set();

		synchronized (workbooks) {
			try {
				// delete all the sheets one after the other in privileged mode
				Workbook workbook = getWorkbook(workbookId);
				// make a copy of the list as the collection is altered when deleting a sheet
				Collection<ISheet> copySheets = new ArrayList<ISheet>(workbook.getSheets());
				for (ISheet sheet : copySheets) {
					workbook.deleteSheet(sheet);
				}

				storageService.deleteWorkbook(workbookId);
				workbooks.remove(workbookId.getKey());
			} finally {
				if (!wasSet) {
					AclPrivilegedMode.clear();
				}
			}
		}
	}

	@Override
	public IWorkbook addNewWorkbook(DataSourceConfigurationId dataSourceConfigId, WorkbookId workbookId)
			throws StorageException, NotFoundException, AlreadyExistsException {
		boolean wasSet = AclPrivilegedMode.set();
		IWorkbook workbook = null;
		synchronized (workbooks) {
			try {
				if (workbooks.get(workbookId) != null) {
					throw new AlreadyExistsException();
				}
				workbook = storageService.add(dataSourceConfigId, workbookId);
				workbooks.put(workbookId, (Workbook) workbook);
				aclService.setPermissions(workbookId, null, Permission.read, Permission.write);
				aclService.setPermissions(new SheetFullName(workbookId, SheetNames.PERMISSIONS), null, Permission.read,
						Permission.write);
			} finally {
				if (!wasSet) {
					AclPrivilegedMode.clear();
				}
			}
		}
		return workbook;
	}

	public void close() {
		log.info("Closing workbook processor");
		workbooks.clear();

	}

	public boolean isSheetInitializationEnabled() {
		return sheetInitializationEnabled;
	}

	public void setSheetInitializationEnabled(boolean sheetInitializationEnabled) {
		this.sheetInitializationEnabled = sheetInitializationEnabled;
	}

}
