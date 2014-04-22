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
package org.netxilia.api.value;

import java.util.ArrayList;
import java.util.List;

import org.netxilia.api.display.Style;
import org.netxilia.api.display.Styles;

public class RichValueBuilder {
	private final List<Style> styles;
	private String display;
	private IGenericValue value;

	public RichValueBuilder() {
		styles = new ArrayList<Style>();
	}

	public RichValueBuilder(RichValue value) {
		if (value == null) {
			throw new NullPointerException();
		}
		if (value.getStyles() != null) {
			this.styles = new ArrayList<Style>(value.getStyles().getItems());
		} else {
			this.styles = new ArrayList<Style>();
		}
		this.value = value.getValue();
		this.display = value.getDisplay();
	}

	public RichValueBuilder addStyle(Style style) {
		this.styles.add(style);
		return this;
	}

	public RichValueBuilder value(IGenericValue value) {
		this.value = value;
		return this;
	}

	public IGenericValue getValue() {
		return value;
	}

	public RichValueBuilder display(String display) {
		this.display = display;
		return this;
	}

	public RichValue build() {
		return new RichValue(value, display, Styles.styles(styles));
	}

	public Object getDisplay() {
		return display;
	}

}
