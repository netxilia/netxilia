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
package org.netxilia.api.impl.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.event.IWorkbookEventListener;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.util.Assert;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class WorkbookAliasDependencyManager implements IWorkbookEventListener {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger
			.getLogger(WorkbookAliasDependencyManager.class);

	private final ConcurrentMap<String, SheetAliasDependencyManager> sheetDependencyManagers = new ConcurrentHashMap<String, SheetAliasDependencyManager>();

	private final IWorkbook workbook;
	private final IFormulaParser formulaParser;
	private final IMoreCellCommands moreCellCommands;

	private WorkbookAliasDependencyManager(IWorkbook workbook, IFormulaParser formulaParser,
			IMoreCellCommands moreCellCommands) {
		this.workbook = workbook;
		this.formulaParser = formulaParser;
		this.moreCellCommands = moreCellCommands;
	}

	/**
	 * sets a new formula in the given cell. If the formula contains a reference to an alias, it will be added to the
	 * internal structure.
	 * 
	 * @param ref
	 * @param formula
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	public synchronized void setAliasDependencies(Formula formula, IFormulaContext context) throws StorageException,
			NotFoundException {
		getManagerForSheet(context.getCell().getSheetName()).setAliasDependencies(formula, context);
	}

	/**
	 * 
	 * @param sheetName
	 * @param alias
	 * @return all the cells that reference the given alias in the given sheet.
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	public synchronized Collection<AreaReference> getAliasDependants(String sheetName, AbsoluteAlias alias)
			throws StorageException, NotFoundException {
		return getManagerForSheet(sheetName).getAliasDependants(alias);
	}

	public synchronized SheetAliasDependencyManager getManagerForSheet(String sheetName) throws StorageException,
			NotFoundException {
		SheetAliasDependencyManager mgr = sheetDependencyManagers.get(sheetName);
		if (mgr == null) {
			ISheet sheet = workbook.getSheet(sheetName);
			SheetAliasDependencyManager newMgr = SheetAliasDependencyManager.newInstance(this, sheet, formulaParser);
			mgr = sheetDependencyManagers.putIfAbsent(sheetName, newMgr);
			if (mgr == null) {
				mgr = newMgr;
			}
		}
		return mgr;
	}

	public void deleteSheet(String sheetName) throws NotFoundException {

		// make a diff between the stored version of aliases and the new list
		SheetAliasDependencyManager sheetMgr = sheetDependencyManagers.remove(sheetName);
		if (sheetMgr == null) {
			return;
		}
		boolean wasPrivileged = false;
		try {
			wasPrivileged = AclPrivilegedMode.set();
			for (SheetAliasDependencyManager mgr : sheetDependencyManagers.values()) {
				List<AreaReference> affectedAreas = new ArrayList<AreaReference>();
				// new aliases are not taken into account - they affect no formula
				// deleted aliases are taken into account
				for (Alias alias : sheetMgr.getAliases().keySet()) {
					affectedAreas.addAll(mgr
							.getAliasDependants(new AbsoluteAlias(sheetMgr.getSheet().getName(), alias)));
				}

				List<CellReference> refs = new ArrayList<CellReference>();
				for (AreaReference area : affectedAreas) {
					for (CellReference ref : area) {
						refs.add(ref);
					}
				}
				mgr.getSheet().sendCommandNoUndo(moreCellCommands.refresh(refs, false));
			}
		} finally {
			if (!wasPrivileged) {
				AclPrivilegedMode.clear();
			}

		}

	}

	@Override
	public void onDeletedSheet(SheetEvent sheetEvent) {
		try {
			deleteSheet(sheetEvent.getSheetName().getSheetName());
		} catch (NotFoundException e) {
			log.error("Cannot find sheet for while processing event :" + sheetEvent + ":" + e, e);
		}

	}

	@Override
	public void onNewSheet(SheetEvent sheetEvent) {
		try {
			getManagerForSheet(sheetEvent.getSheetName().getSheetName());
		} catch (StorageException e) {
			log.error("Cannot load sheet for while processing event :" + sheetEvent + ":" + e, e);
		} catch (NotFoundException e) {
			log.error("Cannot find sheet for while processing event :" + sheetEvent + ":" + e, e);
		}

	}

	synchronized void refreshAliases(String sheetName, Collection<Alias> modified, Collection<Alias> deleted) {
		for (SheetAliasDependencyManager mgr : sheetDependencyManagers.values()) {
			List<AreaReference> affectedAreas = new ArrayList<AreaReference>();
			// new aliases are not taken into account - they affect no formula
			// deleted aliases are taken into account
			for (Alias alias : deleted) {
				affectedAreas.addAll(mgr.getAliasDependants(new AbsoluteAlias(sheetName, alias)));
			}
			// modified aliases are also taken into account
			for (Alias alias : modified) {
				affectedAreas.addAll(mgr.getAliasDependants(new AbsoluteAlias(sheetName, alias)));
			}

			List<CellReference> refs = new ArrayList<CellReference>();
			for (AreaReference area : affectedAreas) {
				for (CellReference ref : area) {
					refs.add(ref);
				}
			}
			if (refs.size() > 0) {
				mgr.getSheet().sendCommandNoUndo(moreCellCommands.refresh(refs, false));
			}
		}

	}

	public static WorkbookAliasDependencyManager newInstance(IWorkbook workbook, IFormulaParser formulaParser,
			IMoreCellCommands moreCellCommands) {
		Assert.notNull(workbook);
		Assert.notNull(formulaParser);
		Assert.notNull(moreCellCommands);

		WorkbookAliasDependencyManager manager = new WorkbookAliasDependencyManager(workbook, formulaParser,
				moreCellCommands);
		manager.workbook.addListener(manager);

		// add a listener for all existing sheets
		for (ISheet sheet : workbook.getSheets()) {
			try {
				manager.getManagerForSheet(sheet.getName());
			} catch (StorageException e) {
				log.error("Could not register depedency manager for sheet: " + sheet.getName() + ":" + e, e);
			} catch (NotFoundException e) {
				log.error("Could not register depedency manager for sheet: " + sheet.getName() + ":" + e, e);
			}
		}
		return manager;
	}
}
