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
package org.netxilia.api.chart;

import java.util.ArrayList;
import java.util.List;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.utils.Matrix;

/**
 * This class populates chart's data from the given sheet and chart definition
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ChartWithData {
	private final transient Chart chart;
	private final Type type;
	private final Title title;
	private final Title y_legend;
	private final Axis x_axis;
	private final Axis y_axis;
	private List<ElementWithData> elements;

	public ChartWithData(Chart chart) {
		// need to copy field for gson
		this.chart = chart;
		this.type = chart.getType();
		this.title = chart.getTitle();
		this.y_legend = chart.getY_legend();
		this.y_axis = chart.getY_axis();
		this.x_axis = chart.getX_axis();
	}

	public List<ElementWithData> getElements() {
		return elements;
	}

	public Type getType() {
		return type;
	}

	public Title getTitle() {
		return title;
	}

	public Title getY_legend() {
		return y_legend;
	}

	public Axis getX_axis() {
		return x_axis;
	}

	public Axis getY_axis() {
		return y_axis;
	}

	public void populate(ISheet sheet) throws NetxiliaResourceException, NetxiliaBusinessException {
		double ymax = Double.MIN_VALUE;
		elements = new ArrayList<ElementWithData>();
		Matrix<CellData> cellData = sheet.receiveCells(chart.getAreaReference()).getNonBlocking();
		// each column is a series (optionally can be done by rows)
		for (int c = 0; c < cellData.getColumnCount(); ++c) {

			Object[] values = new Object[cellData.getRowCount()];
			for (int r = 0; r < cellData.getRowCount(); ++r) {
				if (cellData.get(r, c) != null && cellData.get(r, c).getValue() != null) {
					Double value = cellData.get(r, c).getValue().getNumberValue();
					values[r] = value;
					if (value != null) {
						ymax = Math.max(value, ymax);
					}
				}
			}
			// if the corresponding element exists, take it otherwise use an empty one
			Element element = c < chart.getElements().size() ? chart.getElements().get(c) : new Element();
			ElementWithData s = new ElementWithData(chart, element);
			s.setValues(values);
			elements.add(s);

			if (ymax > Double.MIN_VALUE) {
				y_axis.setMax((int) ymax);
			}
		}
	}
}
