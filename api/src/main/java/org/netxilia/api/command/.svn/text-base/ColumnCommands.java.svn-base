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

import org.netxilia.api.display.Styles;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.reference.Range;

public class ColumnCommands {

	public static IColumnCommand width(final Range range, final int width) {
		return new AbstractColumnCommand(range) {
			@Override
			public ColumnData apply(ColumnData data) {
				return data.withWidth(width);
			}

		};
	}

	public static IColumnCommand styles(final Range range, final Styles styles) {
		return new AbstractColumnCommand(range) {
			@Override
			public ColumnData apply(ColumnData data) {
				return data.withStyles(styles);
			}

		};
	}

	public static IColumnCommand column(final Range range, final int width, final Styles styles) {
		return new AbstractColumnCommand(range) {
			@Override
			public ColumnData apply(ColumnData data) {
				return new ColumnData(data.getIndex(), width, styles);
			}

		};
	}

	public static IColumnCommand delete(final Range range) {
		return new AbstractColumnCommand(range) {
			@Override
			public ColumnData apply(ColumnData data) {
				return null;
			}

		};
	}

	public static IColumnCommand insert(final Range range) {
		return new AbstractColumnCommand(range, true) {
			@Override
			public ColumnData apply(ColumnData data) {
				// the incoming data is a new empty object
				return data;
			}

		};

	}
}
