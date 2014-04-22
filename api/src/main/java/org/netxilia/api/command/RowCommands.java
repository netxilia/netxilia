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
import org.netxilia.api.model.RowData;
import org.netxilia.api.reference.Range;

public class RowCommands {

	public static IRowCommand height(final Range range, final int height) {
		return new AbstractRowCommand(range) {
			@Override
			public RowData apply(RowData data) {
				return data.withHeight(height);
			}
		};
	}

	public static IRowCommand styles(final Range range, final Styles styles) {
		return new AbstractRowCommand(range) {
			@Override
			public RowData apply(RowData data) {
				return data.withStyles(styles);
			}
		};
	}

	public static IRowCommand row(final Range range, final int height, final Styles styles) {
		return new AbstractRowCommand(range) {
			@Override
			public RowData apply(RowData data) {
				return new RowData(data.getIndex(), height, styles);
			}

		};
	}

	public static IRowCommand delete(final Range range) {
		return new AbstractRowCommand(range) {
			@Override
			public RowData apply(RowData data) {
				return null;
			}
		};
	}

	public static IRowCommand insert(final Range range) {
		return new AbstractRowCommand(range, true) {
			@Override
			public RowData apply(RowData data) {
				// the incoming data is a new empty object
				return data;
			}
		};

	}

}
