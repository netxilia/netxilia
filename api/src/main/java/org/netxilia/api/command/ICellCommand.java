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
package org.netxilia.api.command;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;

/**
 * This commands applies for and {@link CellData} existing in the returned target.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface ICellCommand {
	/**
	 * 
	 * @return the sheet are to which this command is applied
	 */
	public AreaReference getTarget();

	/**
	 * 
	 * @return true to stop the propagation of the calculation of depending cells
	 */
	public boolean isStopPropagation();

	/**
	 * applies the command to the data of a cell (that is part of the target area) and return the new cell data along
	 * with the properties that were modified.
	 * 
	 * @param data
	 * @return
	 * @throws NetxiliaBusinessException
	 */
	public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException;

	/**
	 * This method is called once the data of a modified cell data was calculated and saved
	 * 
	 * @param sheet
	 * @param newCellWithProps
	 */
	public void done(ISheet sheet, CellDataWithProperties newCellWithProps);
}
