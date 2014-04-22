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
package org.netxilia.api.formula;

import java.util.Iterator;

import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.IGenericValue;

/**
 * This context is used to evaluate formulas. it contains references to the current cell and sheet.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IFormulaContext {
	/**
	 * used by ReferenceValue for lazy resolution
	 * 
	 * @param ref
	 * @return
	 */
	public IGenericValue getCellValue(CellReference ref);

	/**
	 * used by ReferenceValue for lazy resolution
	 * 
	 * @param ref
	 * @return
	 */
	public CellReference getCell();

	public AreaReference resolveAlias(AbsoluteAlias alias);

	public ISheet getSheet();

	public Iterator<CellData> getCellIterator(AreaReference ref);
}
