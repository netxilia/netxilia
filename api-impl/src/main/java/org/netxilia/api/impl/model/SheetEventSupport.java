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

import org.netxilia.api.event.CellEvent;
import org.netxilia.api.event.ColumnEvent;
import org.netxilia.api.event.ISheetEventListener;
import org.netxilia.api.event.RowEvent;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.impl.IExecutorServiceFactory;
import org.netxilia.api.impl.event.DispatchableEvent;
import org.netxilia.api.impl.event.DispatchableEventSupport;
import org.netxilia.api.model.SheetFullName;

/**
 * Event Adapter for Sheet
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetEventSupport extends DispatchableEventSupport<ISheetEventListener> {
	public SheetEventSupport(SheetFullName name, IExecutorServiceFactory executorServiceFactory) {
		super(executorServiceFactory.newExecutorService("events-" + name));
	}

	public void fireEvent(SheetEvent ev) {
		fireEvent(new DispatchableEvent<ISheetEventListener, SheetEvent>(ev) {
			@Override
			public void dispatch(ISheetEventListener target, SheetEvent event) {
				target.onSheetEvent(event);
			}
		});
	}

	public void fireEvent(CellEvent cellEvent) {
		fireEvent(new DispatchableEvent<ISheetEventListener, CellEvent>(cellEvent) {
			@Override
			public void dispatch(ISheetEventListener target, CellEvent event) {
				target.onCellEvent(event);
			}
		});
	}

	public void fireEvent(RowEvent ev) {
		fireEvent(new DispatchableEvent<ISheetEventListener, RowEvent>(ev) {
			@Override
			public void dispatch(ISheetEventListener target, RowEvent event) {
				target.onRowEvent(event);
			}
		});
	}

	public void fireEvent(ColumnEvent ev) {
		fireEvent(new DispatchableEvent<ISheetEventListener, ColumnEvent>(ev) {
			@Override
			public void dispatch(ISheetEventListener target, ColumnEvent event) {
				target.onColumnEvent(event);
			}
		});
	}

}
