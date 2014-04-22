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
package org.netxilia.api.operation;

import java.util.List;

import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.CellReference;

/**
 * The implementor of this class will create a separate actor for each of the operation. Thus long operation on a sheet
 * will not block the sheet.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface ISheetOperations {
	/**
	 * 
	 * @param sheetName
	 * @param filter
	 *            is a boolean formula. TODO how to express column C = xxx
	 * @return the list of row ids matching the expression
	 */
	public IListenableFuture<List<Integer>> filter(ISheet sheet, Formula filter);

	/**
	 * returns the first cell after the given one, matching the formula or null if none found
	 * 
	 * @param sheet
	 * @param startRef
	 * @param searchFormula
	 * @return
	 */
	public IListenableFuture<CellReference> find(ISheet sheet, CellReference startRef, Formula searchFormula);

}
