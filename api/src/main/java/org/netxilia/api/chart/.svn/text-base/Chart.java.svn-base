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

import java.util.List;

import org.netxilia.api.reference.AreaReference;

public class Chart implements Cloneable {
	private Type type;
	private Title title;
	private Title y_legend;
	private List<Element> elements;
	private Axis x_axis;
	private Axis y_axis;
	private AreaReference areaReference;
	private int height = 200;
	private int width = 500;
	private int top;
	private int left;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public Title getY_legend() {
		return y_legend;
	}

	public void setY_legend(Title yLegend) {
		y_legend = yLegend;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public Axis getX_axis() {
		return x_axis;
	}

	public void setX_axis(Axis xAxis) {
		x_axis = xAxis;
	}

	public Axis getY_axis() {
		return y_axis;
	}

	public void setY_axis(Axis yAxis) {
		y_axis = yAxis;
	}

	public AreaReference getAreaReference() {
		return areaReference;
	}

	public void setAreaReference(AreaReference areaReference) {
		this.areaReference = areaReference;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	@Override
	public Chart clone() {
		try {
			Chart chart = (Chart) super.clone();
			// TODO - do a deep copy
			return chart;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}

	}
}
