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
package org.netxilia.server.rest.command;

import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.ISheet;

/**
 * this implements the undoable contract for cell commands. It executes synchronously the command and it keeps the
 * commands needed to undo the original command.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class UndoableCellCommand implements IUndoableCommand {
	private final ISheet sheet;
	private final ICellCommand command;

	private ICellCommand rollbackCommand;

	public UndoableCellCommand(ISheet sheet, ICellCommand command) {
		this.sheet = sheet;
		this.command = command;
	}

	@Override
	public void execute() throws NetxiliaResourceException, NetxiliaBusinessException {
		// execute the command and wait it to finish
		rollbackCommand = sheet.sendCommand(command).getNonBlocking();

	}

	@Override
	public void undo() throws NetxiliaResourceException, NetxiliaBusinessException {
		if (rollbackCommand == null) {
			return;
		}
		// execute the command and wait it to finish -> should not wait here !?
		sheet.sendCommand(rollbackCommand).getNonBlocking();
	}

}
