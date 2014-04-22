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
package org.netxilia.api.impl.command;

import org.netxilia.api.command.AbstractColumnCommand;
import org.netxilia.api.command.IColumnCommand;
import org.netxilia.api.command.IMoreColumnCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.StyleApplyMode;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.Range;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class MoreColumnCommandsImpl implements IMoreColumnCommands {
	@Autowired
	private IStyleService styleService;

	@Override
	public IColumnCommand applyStyles(final WorkbookId workbookId, final Range range, final Styles applyStyle,
			final StyleApplyMode applyMode) {
		return new AbstractColumnCommand(range) {
			@Override
			public ColumnData apply(ColumnData data) throws NetxiliaBusinessException {
				Styles newStyles = styleService.applyStyle(workbookId, data.getStyles(), applyStyle, applyMode);
				return data.withStyles(newStyles);
			}
		};

	}
}
