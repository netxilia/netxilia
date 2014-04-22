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
package org.netxilia.api.display;

/**
 * This is a list of predefined styles
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public enum DefaultStyle {
	// font
	bold("b"), italic("i"), underline("u"), strikeout("s"), nowrap("wp"),

	// borders
	borderLeft("bl"), borderRight("br"), borderTop("bt"), borderBottom("bb"),

	// align
	alignLeft("a-l"), alignRight("a-r"), alignCenter("a-c"), alignJustify("a-j"),

	// these are the default style formatters
	formatBoolean("boolean"), formatNumber("number"), formatDate("date"), formatTime("time"), formatDateTime("dateTime");

	private final Style style;

	private DefaultStyle(String style) {
		this.style = new Style(style);
	}

	public Style getStyle() {
		return style;
	}

}
