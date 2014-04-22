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
package org.netxilia.server.service.user;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.user.User;
import org.netxilia.server.rest.command.IUndoableCommand;
import org.netxilia.server.rest.command.UndoableCellCommand;
import org.netxilia.server.service.event.ClientEventType;
import org.netxilia.server.service.event.WindowClientEvent;
import org.netxilia.server.service.event.WorkbookClientEvent;
import org.springframework.util.Assert;

/**
 * Represents a session open by a web user on given sheet (together with accompaning sheets). The class is not
 * thread-safe.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class Window {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Window.class);

	private final static int UNDO_STACK_SIZE = 10;

	private final WindowIndex id;
	private final String httpSessionId;
	private final BlockingQueue<WorkbookClientEvent> events = new LinkedBlockingQueue<WorkbookClientEvent>();
	private final long creationTime;
	private final User user;
	private final ISheet sheet;
	private final ISheet summarySheet;
	private final ISheet privateSheet;

	private final Stack<IUndoableCommand> undoStack = new Stack<IUndoableCommand>();
	private final Stack<IUndoableCommand> redoStack = new Stack<IUndoableCommand>();

	public Window(WindowIndex id, String httpSessionId, User user, ISheet sheet, ISheet summarySheet,
			ISheet privateSheet) {
		Assert.notNull(id);
		Assert.notNull(user);
		Assert.notNull(sheet);
		Assert.notNull(summarySheet);
		Assert.notNull(privateSheet);

		this.id = id;
		this.httpSessionId = httpSessionId;
		this.creationTime = System.currentTimeMillis();
		this.user = user;
		this.sheet = sheet;
		this.summarySheet = summarySheet;
		this.privateSheet = privateSheet;
	}

	public WindowIndex getId() {
		return id;
	}

	public String getHttpSessionId() {
		return httpSessionId;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public User getUser() {
		return user;
	}

	public SheetFullName getSheetFullName() {
		return sheet.getFullName();
	}

	public SheetFullName getSummarySheetFullName() {
		return summarySheet.getFullName();
	}

	public SheetFullName getPrivateSheetFullName() {
		return privateSheet.getFullName();
	}

	public List<WorkbookClientEvent> pollEvents(long msWaitingTime) {
		WorkbookClientEvent firstEvent = null;
		try {
			firstEvent = events.poll(msWaitingTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			return null;
		}
		if (firstEvent == null) {
			return null;
		}
		List<WorkbookClientEvent> returnedEvents = new LinkedList<WorkbookClientEvent>();
		returnedEvents.add(firstEvent);
		// check for some more
		events.drainTo(returnedEvents);
		return returnedEvents;
	}

	/**
	 * TODO check for a maximum size
	 * 
	 * @param event
	 */
	public void pushEvent(WorkbookClientEvent event) {
		log.info("Send event:" + event + " to window:" + id);
		events.offer(event);
	}

	private ISheet getSheet(String name) throws NotFoundException {
		if (sheet.getName().equals(name)) {
			return sheet;
		}
		if (summarySheet.getName().equals(name)) {
			return summarySheet;
		}
		if (privateSheet.getName().equals(name)) {
			return privateSheet;
		}
		throw new NotFoundException("Sheet " + name + " was not found");
	}

	/**
	 * Executes the given command and add it to the undo stack
	 * 
	 * @param command
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public void execute(ICellCommand command) throws NetxiliaResourceException, NetxiliaBusinessException {
		Assert.notNull(command);
		ISheet affectedSheet = getSheet(command.getTarget().getSheetName());

		IUndoableCommand undoableCommand = new UndoableCellCommand(affectedSheet, command);
		undoableCommand.execute();

		undoStack.push(undoableCommand);
		if (undoStack.size() > UNDO_STACK_SIZE) {
			undoStack.remove(0);
		}
		redoStack.clear();

		pushEvent(new WindowClientEvent(new WindowInfo(this), sheet.getName(), ClientEventType.windowUndoChanged));
	}

	/**
	 * undo the execution of the last executed command
	 * 
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 * 
	 * @exception EmptyStackException
	 *                if this stack is empty.
	 */
	public void undo() throws NetxiliaResourceException, NetxiliaBusinessException {
		IUndoableCommand command = undoStack.pop();
		redoStack.push(command);
		command.undo();

		pushEvent(new WindowClientEvent(new WindowInfo(this), sheet.getName(), ClientEventType.windowUndoChanged));
	}

	/**
	 * redo the last undone command
	 * 
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 * 
	 * @exception EmptyStackException
	 *                if this stack is empty.
	 */
	public void redo() throws NetxiliaResourceException, NetxiliaBusinessException {
		IUndoableCommand command = redoStack.pop();
		undoStack.push(command);
		command.execute();

		pushEvent(new WindowClientEvent(new WindowInfo(this), sheet.getName(), ClientEventType.windowUndoChanged));
	}

	public boolean hasUndo() {
		return !undoStack.isEmpty();
	}

	public boolean hasRedo() {
		return !redoStack.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Window other = (Window) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Window [SheetFullName=" + getSheetFullName() + ", id=" + id + ", user=" + user + "]";
	}
}
