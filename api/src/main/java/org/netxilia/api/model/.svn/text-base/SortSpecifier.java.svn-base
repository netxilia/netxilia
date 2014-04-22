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
package org.netxilia.api.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to specify how a sheet has to be sort. It has a string representation like the example +B-A+C.
 * 
 * The sheet is sorted in the order of the given columns. A "+" before the column's label means a ascending sort, a "-"
 * means a descending sort. The "+" or "-" before the first column is optional. If not present ascending is assumed.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SortSpecifier {
	private static Pattern columnSpecRegex = Pattern.compile("([+-]?)([^+-]+)");
	private final List<SortColumn> columns;

	public SortSpecifier(String text) {
		Matcher m = columnSpecRegex.matcher(text);
		List<SortColumn> cols = new ArrayList<SortColumn>();
		while (m.find()) {
			cols.add(new SortColumn(m.group(2), "".equals(m.group(1)) || "+".equals(m.group(1)) ? SortOrder.ascending
					: SortOrder.descending));
		}
		this.columns = Collections.unmodifiableList(cols);
	}

	public SortSpecifier(List<SortColumn> columns) {
		this.columns = Collections.unmodifiableList(columns);
	}

	public List<SortColumn> getColumns() {
		return columns;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (SortColumn column : columns)
			builder.append(column.getOrder() == SortOrder.ascending ? "+" : "-").append(column.getName());
		return builder.toString();
	}

	public enum SortOrder {
		ascending, descending;
	}

	public class SortColumn {
		private final String name;
		private final SortOrder order;

		public SortColumn(String name, SortOrder order) {
			this.name = name;
			this.order = order;
		}

		public String getName() {
			return name;
		}

		public SortOrder getOrder() {
			return order;
		}

	}

}
