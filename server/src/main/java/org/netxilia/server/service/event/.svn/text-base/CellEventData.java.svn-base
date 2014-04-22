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

public class CellEventData {
	private final int row;
	private final int column;
	private final Object value;// this can also be the formula
	private final String formattedValue;
	private final String style;
	private final int rowSpan;
	private final int colSpan;

	public CellEventData(int row, int column, Object value, String formattedValue, String style, int rowSpan,
			int colSpan) {
		this.row = row;
		this.column = column;
		this.value = value;
		this.formattedValue = formattedValue;
		this.style = style;
		this.colSpan = colSpan;
		this.rowSpan = rowSpan;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public Object getValue() {
		return value;
	}

	public String getFormattedValue() {
		return formattedValue;
	}

	public String getStyle() {
		return style;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public int getColSpan() {
		return colSpan;
	}

	@Override
	public String toString() {
		return "CellEventData [colSpan=" + colSpan + ", column=" + column + ", formattedValue=" + formattedValue
				+ ", row=" + row + ", rowSpan=" + rowSpan + ", style=" + style + ", value=" + value + "]";
	}

}
