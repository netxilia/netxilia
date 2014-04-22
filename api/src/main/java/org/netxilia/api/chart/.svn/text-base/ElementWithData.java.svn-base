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

/**
 * This contains the definition of a chart Element (series) along with the data taken from the associated Sheet.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ElementWithData {

	private final Type type;

	private final float alpha;
	private final String colour;
	private final String text;
	private final int fontSize;

	// the data values
	private Object[] values;

	public ElementWithData(Chart chart, Element element) {
		this.type = element.getType() != null ? element.getType() : chart.getType();
		this.alpha = element.getAlpha();
		this.colour = element.getColour();
		this.text = element.getColour();
		this.fontSize = element.getFontSize();
	}

	public Object[] getValues() {
		return values;
	}

	public void setValues(Object[] values) {
		this.values = values;
	}

	public Type getType() {
		return type;
	}

	public float getAlpha() {
		return alpha;
	}

	public String getColour() {
		return colour;
	}

	public String getText() {
		return text;
	}

	public int getFontSize() {
		return fontSize;
	}

}
