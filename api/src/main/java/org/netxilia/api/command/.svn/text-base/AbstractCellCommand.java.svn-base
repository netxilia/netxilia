/*******************************************************************************
 * 
 * Copyright 2010 Alexandru Craciun, and individual contributors as indicated by the @authors tag.
 * 
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this software; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 ******************************************************************************/
package org.netxilia.api.command;

import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;

/**
 * This is a basic implementation of a ICellCommand
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public abstract class AbstractCellCommand implements ICellCommand {
	private final AreaReference target;
	private final boolean stopPropagation;

	public AbstractCellCommand(AreaReference target) {
		this(target, false);
	}

	public AbstractCellCommand(AreaReference target, boolean stopPropagation) {
		assert target != null;
		this.target = target;
		this.stopPropagation = stopPropagation;
	}

	public boolean isStopPropagation() {
		return stopPropagation;
	}

	@Override
	public AreaReference getTarget() {
		return target;
	}

	@Override
	public void done(ISheet sheet, CellDataWithProperties newCellWithProps) {
		// do nothing
	}

}
