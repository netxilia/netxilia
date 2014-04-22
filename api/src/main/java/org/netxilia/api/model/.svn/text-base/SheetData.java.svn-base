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
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.chart.Chart;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.utils.ObjectUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * This class offers minimum information about a Sheet to be able to display sheet lists without having to open the
 * given sheets for read/write. It contains immutable data for a sheet like id, type and name.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetData {
	public enum Property {
		storageInfo, name, charts, aliases, type, spans
	}

	private final SheetFullName name;
	private final SheetType type;

	private final ImmutableMap<Alias, AreaReference> aliases;
	private final ImmutableList<Chart> charts;
	private final ImmutableList<AreaReference> spans;

	public SheetData(SheetFullName name, SheetType type) {
		this(name, type, ImmutableMap.<Alias, AreaReference> of(), ImmutableList.<Chart> of(), ImmutableList
				.<AreaReference> of());
	}

	public SheetData(SheetFullName name, SheetType type, Map<Alias, AreaReference> aliases, List<Chart> charts,
			List<AreaReference> spans) {
		assert name != null;
		assert type != null;
		assert aliases != null;
		assert charts != null;
		assert spans != null;
		this.name = name;
		this.type = type;
		this.aliases = ImmutableMap.copyOf(aliases);
		this.charts = ImmutableList.copyOf(charts);
		this.spans = ImmutableList.copyOf(spans);
	}

	public SheetFullName getFullName() {
		return name;
	}

	public String getName() {
		return name.getSheetName();
	}

	public SheetType getType() {
		return type;
	}

	public List<AreaReference> getSpans() {
		return spans;
	}

	public Map<Alias, AreaReference> getAliases() {
		return aliases;
	}

	/**
	 * Resolve the given alias within the sheet. It can be a one-cell area also. If the alias is not recongnized null is
	 * returned.
	 * 
	 * @param alias
	 * @return
	 */
	public AreaReference resolveAlias(Alias alias) {
		return aliases.get(alias);
	}

	public List<Chart> getCharts() {
		return charts;
	}

	public SheetData setAlias(Alias aliasName, AreaReference ref) {
		Map<Alias, AreaReference> newAliases = new HashMap<Alias, AreaReference>(aliases);
		if (ref == null) {
			newAliases.remove(aliasName);
		} else {
			newAliases.put(aliasName, ref);
		}
		return withAliases(newAliases);
	}

	public SheetData withAliases(Map<Alias, AreaReference> newAliases) {
		return new SheetData(name, type, newAliases, charts, spans);
	}

	public SheetData addChart(Chart chart) {
		List<Chart> newCharts = new ArrayList<Chart>(charts);
		newCharts.add(chart);
		return withCharts(newCharts);
	}

	public SheetData setChart(int chartIndex, Chart chart) {
		List<Chart> newCharts = new ArrayList<Chart>(charts);
		newCharts.set(chartIndex, chart);
		return withCharts(newCharts);
	}

	public SheetData deleteChart(int chartIndex) {
		List<Chart> newCharts = new ArrayList<Chart>(charts);
		newCharts.remove(chartIndex);
		return withCharts(newCharts);
	}

	public SheetData withCharts(List<Chart> newCharts) {
		return new SheetData(name, type, aliases, newCharts, spans);
	}

	public SheetData withSpans(List<AreaReference> newSpans) {
		if (newSpans != null) {
			// remove the sheet name as it's useless
			List<AreaReference> cleanSpans = new ArrayList<AreaReference>(newSpans.size());
			for (AreaReference span : newSpans) {
				cleanSpans.add(span.withSheetName(null));
			}
			return new SheetData(name, type, aliases, charts, cleanSpans);
		}
		return new SheetData(name, type, aliases, charts, newSpans);
	}

	public static Collection<Property> diff(SheetData sheet1, SheetData sheet2) {
		if (sheet1 == null || sheet2 == null) {
			return Arrays.asList(Property.values());
		}
		if (!sheet1.getFullName().equals(sheet2.getFullName())) {
			throw new IllegalArgumentException("The reference should be identical:" + sheet2.getFullName());
		}
		Collection<Property> properties = EnumSet.noneOf(Property.class);
		if (!ObjectUtils.equals(sheet1.aliases, sheet2.aliases)) {
			properties.add(Property.aliases);
		}
		if (!ObjectUtils.equals(sheet1.charts, sheet2.charts)) {
			properties.add(Property.charts);
		}
		if (!ObjectUtils.equals(sheet1.spans, sheet2.spans)) {
			properties.add(Property.spans);
		}
		return properties;
	}

}
