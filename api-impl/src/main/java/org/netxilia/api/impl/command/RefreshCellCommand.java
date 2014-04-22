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
package org.netxilia.api.impl.command;

import java.util.List;

import org.netxilia.api.command.AbstractCellCommand;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellData.Property;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.springframework.util.Assert;

import com.google.common.collect.ImmutableList;

/**
 * This commands executes the refresh of the cells one after this other - i.e. it waits until the refresh of a cell
 * finishes before launching the new one.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class RefreshCellCommand extends AbstractCellCommand {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(RefreshCellCommand.class);

	private ISheet mainSheet;
	private final AreaReference currentCell;
	private final List<CellReference> refreshCells;

	public RefreshCellCommand(List<CellReference> refreshCells, boolean stopPropagation) {
		this(null, refreshCells, stopPropagation);
	}

	public RefreshCellCommand(ISheet mainSheet, List<CellReference> refreshCells, boolean stopPropagation) {
		super(AreaReference.ALL, stopPropagation);
		Assert.notNull(refreshCells);
		Assert.notEmpty(refreshCells);
		this.refreshCells = ImmutableList.copyOf(refreshCells);
		currentCell = new AreaReference(refreshCells.get(0));
		this.mainSheet = mainSheet;
	}

	@Override
	public AreaReference getTarget() {
		return currentCell;
	}

	private ISheet getSheetByName(String sheetName) {
		ISheet foundSheet = null;
		if (sheetName == null || sheetName.equals(mainSheet.getName())) {
			foundSheet = mainSheet;
		} else {
			try {
				foundSheet = mainSheet.getWorkbook().getSheet(sheetName);
			} catch (StorageException e) {
				log.error("Cannot find sheet for name:" + sheetName + ":" + e, e);
				return null;
			} catch (NotFoundException e) {
				log.error("Cannot find sheet for cell:" + sheetName + ":" + e, e);
				return null;
			}
		}
		return foundSheet;
	}

	@Override
	public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException {
		// make as the formulas was changed - to trigger the recalculation
		return new CellDataWithProperties(data, Property.formula);
	}

	@Override
	public void done(ISheet sheet, CellDataWithProperties newCellWithProps) {
		Assert.notNull(sheet);
		// move next
		if (mainSheet == null) {
			// this is the sheet to which the command was sent first time
			// basically the sheet that contains the cells with a null reference
			mainSheet = sheet;
		}
		if (refreshCells.size() > 1) {
			List<CellReference> nextCells = refreshCells.subList(1, refreshCells.size());
			ISheet targetSheet = getSheetByName(nextCells.get(0).getSheetName());
			RefreshCellCommand nextCommand = new RefreshCellCommand(mainSheet, nextCells, isStopPropagation());
			targetSheet.sendCommandNoUndo(nextCommand);
		}
	}
}
