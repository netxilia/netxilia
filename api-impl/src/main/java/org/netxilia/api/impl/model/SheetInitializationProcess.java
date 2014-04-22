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
package org.netxilia.api.impl.model;

import java.util.ArrayList;
import java.util.List;

import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.impl.dependencies.SheetAliasDependencyManager;
import org.netxilia.api.impl.dependencies.SheetDependencyManager;
import org.netxilia.api.impl.dependencies.WorkbookAliasDependencyManager;
import org.netxilia.api.impl.dependencies.WorkbookDependencyManager;
import org.netxilia.api.model.CellData;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.utils.Matrix;
import org.netxilia.spi.formula.IFormulaParser;

/**
 * Go through all the cells of a sheet at load time to retrieve the dependencies and to recalculate non-cacheable
 * formulas.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetInitializationProcess {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SheetInitializationProcess.class);

	private final SheetActor sheet;
	private final IFormulaParser formulaParser;
	private final WorkbookDependencyManager dependencyManager;
	private final WorkbookAliasDependencyManager aliasDependencyManager;
	private final IMoreCellCommands moreCellCommands;

	public SheetInitializationProcess(SheetActor sheet, IFormulaParser parser,
			WorkbookDependencyManager dependencyManager, WorkbookAliasDependencyManager aliasDependencyManager,
			IMoreCellCommands moreCellCommands) {
		this.sheet = sheet;
		this.formulaParser = parser;
		this.dependencyManager = dependencyManager;
		this.aliasDependencyManager = aliasDependencyManager;
		this.moreCellCommands = moreCellCommands;
		start();
	}

	private void start() {
		// load the cells in the same thread
		Matrix<CellData> cells = sheet.getCells(AreaReference.ALL);

		boolean privilegeWasSet = AclPrivilegedMode.set();
		try {
			SheetDependencyManager sheetMgr = null;
			SheetAliasDependencyManager sheetAliasMgr = null;
			try {
				sheetMgr = dependencyManager.getManagerForSheet(sheet.getName());
				sheetAliasMgr = aliasDependencyManager.getManagerForSheet(sheet.getName());
			} catch (StorageException e) {
				log.error("Could not register depedency manager for sheet: " + sheet.getName() + ":" + e, e);
				return;
			} catch (NotFoundException e) {
				log.error("Could not register depedency manager for sheet: " + sheet.getName() + ":" + e, e);
				return;
			}

			List<CellReference> refreshCells = new ArrayList<CellReference>();
			for (CellData cell : cells) {
				if (cell.getFormula() != null) {
					try {
						IFormulaContext context = new SheetActorFormulaContext(sheet, cell.getReference());
						sheetMgr.setDependencies(cell.getFormula(), context);
						sheetAliasMgr.setAliasDependencies(cell.getFormula(), context);
						if (!formulaParser.isCacheable(cell.getFormula())) {
							refreshCells.add(cell.getReference());
						}
					} catch (FormulaParsingException e) {
						log.error("Cannot refresh cell " + cell + ":" + e, e);
					} catch (CyclicDependenciesException e) {
						log.error("Cannot refresh cell " + cell + ":" + e, e);
					} catch (StorageException e) {
						log.error("Cannot refresh cell " + cell + ":" + e, e);
					}
				}
			}
			try {
				if (!refreshCells.isEmpty()) {
					sheet.sendCommandNoUndo(moreCellCommands.refresh(refreshCells, false));
				}
			} catch (NotFoundException e) {
				log.error("Cannot refresh cells " + refreshCells + ":" + e, e);
			} catch (CyclicDependenciesException e) {
				log.error("Cannot refresh cells " + refreshCells + ":" + e, e);
			} catch (NetxiliaBusinessException e) {
				log.error("Cannot refresh cells " + refreshCells + ":" + e, e);
			}
		} finally {
			if (!privilegeWasSet) {
				AclPrivilegedMode.clear();
			}
		}

	}
}
