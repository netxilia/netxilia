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

import java.util.List;
import java.util.Map;

import org.netxilia.api.chart.Chart;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.reference.AreaReference;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SheetCommands {

	public static ISheetCommand setAlias(final Alias aliasName, final AreaReference ref) {
		return new ISheetCommand() {
			@Override
			public SheetData apply(SheetData data) {
				return data.setAlias(aliasName, ref);
			}
		};
	}

	public static ISheetCommand addChart(final Chart chart) {
		return new ISheetCommand() {
			@Override
			public SheetData apply(SheetData data) {
				return data.addChart(chart);
			}
		};
	}

	public static ISheetCommand setChart(final int chartIndex, final Chart chart) {
		return new ISheetCommand() {
			@Override
			public SheetData apply(SheetData data) {
				return data.setChart(chartIndex, chart);
			}
		};
	}

	public static ISheetCommand deleteChart(final int chartIndex) {
		return new ISheetCommand() {
			@Override
			public SheetData apply(SheetData data) {
				return data.deleteChart(chartIndex);
			}
		};
	}

	public static ISheetCommand sheet(Map<Alias, AreaReference> aliases, List<Chart> charts, List<AreaReference> spans) {
		final Map<Alias, AreaReference> immAliases = ImmutableMap.copyOf(aliases);
		final List<Chart> immCharts = ImmutableList.copyOf(charts);
		final List<AreaReference> immSpans = ImmutableList.copyOf(spans);

		return new ISheetCommand() {
			@Override
			public SheetData apply(SheetData data) {
				return new SheetData(data.getFullName(), data.getType(), immAliases, immCharts, immSpans);
			}
		};
	}

	public static ISheetCommand spans(List<AreaReference> spans) {
		final List<AreaReference> immSpans = ImmutableList.copyOf(spans);
		return new ISheetCommand() {
			@Override
			public SheetData apply(SheetData data) {
				return data.withSpans(immSpans);
			}
		};
	}
}
