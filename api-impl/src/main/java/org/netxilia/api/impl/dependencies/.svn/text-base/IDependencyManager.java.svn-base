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

import java.util.List;

import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;

/**
 * This interface is implemented by different dependency managers between cells.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IDependencyManager {

	/**
	 * 
	 * @param ref
	 * @return the list of AreaReference (it can be one-cell reference) of this cell.
	 */
	public List<AreaReference> getDependencies(CellReference ref);

	/**
	 * 
	 * @param ref
	 * @return the list of all the cells depending on the given cell directly or indirectly, i.e. all the cells that
	 *         would be affected by an update of the given cell.
	 */
	public List<CellReference> getAllInverseDependencies(CellReference ref);

}
