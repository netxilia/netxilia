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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import org.netxilia.api.display.Styles;
import org.netxilia.api.utils.ObjectUtils;

/**
 * Row data. It does not contain the cells. To access the cell data, access directly the sheet.
 * 
 * @author alexandru craciun
 * 
 */
public final class RowData implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Property {
		storageInfo, index, height, styles
	}

	private final int index;
	private final int height;
	private final Styles styles;

	public RowData(int index, int height, Styles styles) {
		this.index = index;
		this.height = height;
		this.styles = styles;
	}

	public int getIndex() {
		return index;
	}

	public int getHeight() {
		return height;
	}

	public Styles getStyles() {
		return styles;
	}

	@Override
	public String toString() {
		return "RowData [height=" + height + ", index=" + index + ", styles=" + styles + "]";
	}

	public RowData withHeight(int newHeight) {
		return new RowData(index, newHeight, styles);
	}

	public RowData withStyles(Styles newStyles) {
		return new RowData(index, height, newStyles);
	}

	public static Collection<Property> diff(RowData row1, RowData row2) {
		if (row1 == null || row2 == null) {
			return Arrays.asList(Property.values());
		}
		if (row1.index != row2.index) {
			throw new IllegalArgumentException("The reference should be identical:" + row2.index);
		}
		Collection<Property> properties = EnumSet.noneOf(Property.class);
		if (!ObjectUtils.equals(row1.styles, row2.styles)) {
			properties.add(Property.styles);
		}
		if (!ObjectUtils.equals(row1.height, row2.height)) {
			properties.add(Property.height);
		}
		return properties;
	}
}
