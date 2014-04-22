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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.event.IWorkbookEventListener;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.util.Assert;

/**
 * This class coordinates the dependency between cells in different sheets. It contains a map of
 * {@link SheetDependencyManager}
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class WorkbookDependencyManager implements IDependencyManager, IWorkbookEventListener {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WorkbookDependencyManager.class);

	private final ConcurrentMap<String, SheetDependencyManager> cellDependencyManagers = new ConcurrentHashMap<String, SheetDependencyManager>();
	private final IWorkbook workbook;

	private final IMoreCellCommands moreCellCommands;

	private final IFormulaParser formulaParser;

	private WorkbookDependencyManager(IWorkbook workbook, IMoreCellCommands moreCellCommands,
			IFormulaParser formulaParser) {
		this.workbook = workbook;
		this.moreCellCommands = moreCellCommands;
		this.formulaParser = formulaParser;
	}

	public IWorkbook getWorkbook() {
		return workbook;
	}

	@Override
	public List<AreaReference> getDependencies(CellReference ref) {
		if (ref.getSheetName() == null) {
			throw new IllegalArgumentException("Sheet name is not specified for this reference:" + ref);
		}
		SheetDependencyManager mgr = cellDependencyManagers.get(ref.getSheetName());
		if (mgr == null) {
			return Collections.emptyList();
		}
		return mgr.getDependencies(ref);
	}

	@Override
	public List<CellReference> getAllInverseDependencies(CellReference ref) {
		if (ref.getSheetName() == null) {
			throw new IllegalArgumentException("Sheet name is not specified for this reference:" + ref);
		}
		SheetDependencyManager mgr = cellDependencyManagers.get(ref.getSheetName());
		if (mgr == null) {
			return Collections.emptyList();
		}
		return mgr.getAllInverseDependencies(ref);
	}

	public SheetDependencyManager getManagerForSheet(String sheetName) throws StorageException, NotFoundException {
		SheetDependencyManager mgr = cellDependencyManagers.get(sheetName);
		if (mgr == null) {
			ISheet sheet = workbook.getSheet(sheetName);
			SheetDependencyManager newMgr = SheetDependencyManager.newInstance(this, sheet, moreCellCommands,
					formulaParser);
			mgr = cellDependencyManagers.putIfAbsent(sheetName, newMgr);
			if (mgr == null) {
				mgr = newMgr;
			}
		}
		return mgr;
	}

	public void setDependencies(Formula formula, IFormulaContext context) throws CyclicDependenciesException,
			StorageException, NotFoundException {
		CellReference ref = context.getCell();
		if (ref.getSheetName() == null) {
			throw new IllegalArgumentException("Sheet name is not specified for this reference:" + ref);
		}
		getManagerForSheet(ref.getSheetName()).setDependencies(formula, context);
	}

	public Set<AreaReference> deleteSheet(String sheetName) {
		SheetDependencyManager mgr = cellDependencyManagers.get(sheetName);
		if (mgr != null) {
			Set<AreaReference> affectedAreas = mgr.deleteSheet();
			cellDependencyManagers.remove(sheetName);
			return affectedAreas;
		}
		return Collections.emptySet();
	}

	@Override
	public void onDeletedSheet(SheetEvent sheetEvent) {
		deleteSheet(sheetEvent.getSheetName().getSheetName());
	}

	@Override
	public void onNewSheet(SheetEvent sheetEvent) {
		try {
			getManagerForSheet(sheetEvent.getSheetName().getSheetName());
		} catch (StorageException e) {
			log.error("Could not register depedency manager for sheet: " + sheetEvent.getSheetName() + ":" + e, e);
		} catch (NotFoundException e) {
			log.error("Could not register depedency manager for sheet: " + sheetEvent.getSheetName() + ":" + e, e);
		}

	}

	public static WorkbookDependencyManager newInstance(IWorkbook workbook, IMoreCellCommands moreCellCommands,
			IFormulaParser formulaParser) {
		Assert.notNull(workbook);
		Assert.notNull(moreCellCommands);
		Assert.notNull(formulaParser);

		WorkbookDependencyManager manager = new WorkbookDependencyManager(workbook, moreCellCommands, formulaParser);
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
