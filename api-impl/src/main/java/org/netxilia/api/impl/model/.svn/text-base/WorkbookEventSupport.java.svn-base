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

import java.util.List;

import org.netxilia.api.event.IWorkbookEventListener;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.event.SheetEventType;
import org.netxilia.api.impl.event.DispatchableEvent;
import org.netxilia.api.impl.event.DispatchableEventSupport;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * Event Adapter for Sheet
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class WorkbookEventSupport extends DispatchableEventSupport<IWorkbookEventListener> {
	public WorkbookEventSupport() {
		// TODO is multithreading ok here !?
		super(MoreExecutors.sameThreadExecutor());
		// super(Executors.newSingleThreadExecutor());
	}

	public void fireEvent(SheetEvent ev) {
		fireEvent(new DispatchableEvent<IWorkbookEventListener, SheetEvent>(ev) {
			@Override
			public void dispatch(IWorkbookEventListener target, SheetEvent event) {
				if (event.getType() == SheetEventType.deleted) {
					target.onDeletedSheet(event);
				} else if (event.getType() == SheetEventType.inserted) {
					target.onNewSheet(event);
				}
			}
		});
	}

	public void fireEvents(List<SheetEvent> events) {
		if (events == null) {
			return;
		}
		for (SheetEvent event : events) {
			fireEvent(event);
		}
	}
}
