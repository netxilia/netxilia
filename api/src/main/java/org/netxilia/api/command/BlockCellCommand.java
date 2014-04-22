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

import java.util.Map;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;

import com.google.common.collect.ImmutableMap;

/**
 * This command is used to put together multiple cell commands concerning the same cell block. If a command exists for
 * the given cell from the block it is applied, otherwise the cell is kept untouched.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class BlockCellCommand extends AbstractCellCommand {
	private final ImmutableMap<CellReference, ICellCommand> commands;

	public BlockCellCommand(AreaReference target, Map<CellReference, ICellCommand> commands) {
		super(target);
		this.commands = ImmutableMap.copyOf(commands);
	}

	@Override
	public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException {
		ICellCommand cmd = commands.get(data.getReference());
		if (cmd == null) {
			return new CellDataWithProperties(data);
		}
		return cmd.apply(data);

	}
}
