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
package org.netxilia.server.service.event;

import java.util.List;

import org.netxilia.api.event.CellEvent;
import org.netxilia.api.event.ColumnEvent;
import org.netxilia.api.event.RowEvent;
import org.netxilia.api.event.SheetEvent;

/**
 * It converts the event structure on the server side on simpler coarser grained event for the client. Some event may
 * not be converted at all (the corresponding method returns null). In this case the communication infrastructure with
 * the client should ignore the event.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IClientEventConversionService {
	public List<WorkbookClientEvent> convert(CellEvent ev) throws EventConversionException;

	public List<WorkbookClientEvent> convert(ColumnEvent columnEvent) throws EventConversionException;

	public List<WorkbookClientEvent> convert(RowEvent rowEvent) throws EventConversionException;

	public List<WorkbookClientEvent> convert(SheetEvent sheetEvent) throws EventConversionException;
}
