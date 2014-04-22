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
package org.netxilia.functions;

import java.util.Iterator;

import org.netxilia.api.display.Styles;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.RichValue;
import org.netxilia.spi.formula.Function;
import org.netxilia.spi.formula.Functions;

/**
 * These are different functions working with rich values
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
@Functions
public class RichValueFunctions {
	public String STYLE(RichValue rv) {
		return rv.getStyles() != null ? rv.getStyles().toString() : "";
	}

	/**
	 * don't know how to store RichValues so recalculate them each time the method is like STYLEDECODE(value, v1,
	 * style1, v2, style2, ... defaultStyle);
	 * 
	 * @param value
	 * @param valueAndStyle
	 * @return
	 */
	@Function(cacheable = false)
	public IGenericValue STYLEDECODE(IGenericValue value, Iterator<IGenericValue> valueAndStyle) {
		String style = decode(value, valueAndStyle, true);
		if (style == null) {
			return value;
		}
		return new RichValue(value, Styles.styles(style));
	}

	@Function(cacheable = false)
	public IGenericValue STYLEINTERVALS(IGenericValue value, Iterator<IGenericValue> valueAndStyle) {
		String style = decode(value, valueAndStyle, false);
		if (style == null) {
			return value;
		}
		return new RichValue(value, Styles.styles(style));
	}

	public String DISPLAY(RichValue rv) {
		return rv.getDisplay() != null ? rv.getDisplay() : "";
	}

	@Function(cacheable = false)
	public IGenericValue DISPLAYDECODE(IGenericValue value, Iterator<IGenericValue> valueAndDisplay) {
		String display = decode(value, valueAndDisplay, true);
		if (display == null) {
			return value;
		}
		return new RichValue(value, display);
	}

	@Function(cacheable = false)
	public IGenericValue DISPLAYINTERVALS(IGenericValue value, Iterator<IGenericValue> valueAndDisplay) {
		String display = decode(value, valueAndDisplay, false);
		if (display == null) {
			return value;
		}
		return new RichValue(value, display);
	}

	private String decode(IGenericValue value, Iterator<IGenericValue> decodeInfo, boolean strictEqual) {
		String code = null;

		IGenericValue testValue = null;
		while (decodeInfo.hasNext()) {
			IGenericValue elem = decodeInfo.next();

			if (decodeInfo.hasNext()) {
				testValue = elem;
				code = decodeInfo.next().getStringValue();
			} else {
				// this is the default style
				code = elem.getStringValue();
				break;
			}
			int cmp = testValue.compareTo(value);
			if (cmp == 0 && strictEqual || cmp >= 0 && !strictEqual) {
				break;
			}
		}
		return code;
	}

}
