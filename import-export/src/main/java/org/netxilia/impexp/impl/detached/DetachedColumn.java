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
package org.netxilia.impexp.impl.detached;

import org.netxilia.api.display.Styles;

public class DetachedColumn {
	public static final int DEFAULT_WIDTH = 0;

	private Integer width;
	private Styles styles;

	public Integer getWidth() {
		return width;
	}

	public Styles getStyles() {
		return styles;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setStyles(Styles styles) {
		this.styles = styles;
	}

}
