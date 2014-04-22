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
package org.netxilia.api.impl.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.value.NamedValue;

/**
 * The pattern's format is: [enum class];[name attribute];[value attribute];[multiple true/false]
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class EnumerationFormatter extends AbstractValueListFormatter {
	private static final long serialVersionUID = 1L;

	private static final String ATT_ENUM_CLASS = "enum";
	private static final String ATT_ENUM_VALUE = "enum-value";

	private static final String ENUM_VALUE_NAME = "name";
	private static final String ENUM_VALUE_ORDINAL = "ordinal";

	private final List<NamedValue> values;

	private final Class<Enum<?>> enumClass;

	@SuppressWarnings("unchecked")
	public EnumerationFormatter(StyleDefinition definition) {
		super(definition);
		values = new ArrayList<NamedValue>();
		// TODO allow different attributes for names and values
		String enumClassName = getDefinition().getAttribute(ATT_ENUM_CLASS);
		if (enumClassName == null) {
			throw new IllegalArgumentException("Enum Class was not defined for the enum formatter: "
					+ definition.getName());
		}
		try {
			enumClass = (Class<Enum<?>>) Class.forName(enumClassName);
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Unknown class:" + enumClassName);
		}
		String enumValueProperty = getDefinition().getAttribute(ATT_ENUM_VALUE);
		for (Enum<?> enumItem : enumClass.getEnumConstants()) {
			values.add(new NamedValue(enumItem.name(), enumValue(enumValueProperty, enumItem)));
		}

	}

	private String enumValue(String enumValueProperty, Enum<?> enumItem) {
		if (enumValueProperty == null || ENUM_VALUE_NAME.equals(enumValueProperty)) {
			return enumItem.name();
		}

		if (ENUM_VALUE_ORDINAL.equals(enumValueProperty)) {
			return String.valueOf(enumItem.ordinal());
		}

		try {
			return BeanUtils.getSimpleProperty(enumItem, enumValueProperty);
		} catch (Exception e) {
			throw new IllegalArgumentException("Unknown property:" + enumItem.getClass() + "." + enumValueProperty);
		}
	}

	@Override
	public List<NamedValue> getValues() {
		return values;
	}

}
