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

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import org.netxilia.api.display.Styles;
import org.netxilia.api.utils.ObjectUtils;

/**
 * Column data.
 * 
 * @author alexandru craciun
 * 
 */
public final class ColumnData {
	public enum Property {
		storageInfo, index, width, styles
	}

	// XXX - this to exclude it from GSON serialization - should find a better way (do not store data and id)
	private final transient int index;
	private final int width;
	private final Styles styles;

	public ColumnData(int index, int width, Styles styles) {
		this.index = index;
		this.width = width;
		this.styles = styles;
	}

	public int getIndex() {
		return index;
	}

	public int getWidth() {
		return width;
	}

	public Styles getStyles() {
		return styles;
	}

	@Override
	public String toString() {
		return "ColumnData [index=" + index + ", styles=" + styles + ", width=" + width + "]";
	}

	public ColumnData withWidth(int newWidth) {
		return new ColumnData(index, newWidth, styles);
	}

	public ColumnData withStyles(Styles newStyles) {
		return new ColumnData(index, width, newStyles);
	}

	public static Collection<Property> diff(ColumnData col1, ColumnData col2) {
		if (col1 == null || col2 == null) {
			return Arrays.asList(Property.values());
		}
		if (col1.index != col2.index) {
			throw new IllegalArgumentException("The reference should be identical:" + col2.index);
		}
		Collection<Property> properties = EnumSet.noneOf(Property.class);
		if (!ObjectUtils.equals(col1.styles, col2.styles)) {
			properties.add(Property.styles);
		}
		if (!ObjectUtils.equals(col1.width, col2.width)) {
			properties.add(Property.width);
		}
		return properties;
	}
}
