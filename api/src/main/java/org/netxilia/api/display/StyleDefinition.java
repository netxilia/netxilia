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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StyleDefinition {
	private final Style id;
	private final StyleGroup group;
	private final String name;
	private final String description;
	private final Map<String, StyleAttribute> attributes;
	private IStyleFormatter formatter;
	private final String css;

	public StyleDefinition(Style id, StyleGroup group, String name, String description,
			Collection<StyleAttribute> attributes) {
		assert id != null;

		this.id = id;
		this.group = group != null ? group : StyleGroup.DEFAULT_GROUP;
		this.name = name;
		this.description = description;
		this.attributes = new HashMap<String, StyleAttribute>();
		StringBuilder cssBuilder = new StringBuilder();
		cssBuilder.append(id).append(" {");
		if (attributes != null) {
			for (StyleAttribute att : attributes) {
				this.attributes.put(att.getName(), att);
				cssBuilder.append(att).append(";\n");
			}
		}
		cssBuilder.append("}");
		css = cssBuilder.toString();
	}

	public Style getId() {
		return id;
	}

	public StyleGroup getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Map<String, StyleAttribute> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public IStyleFormatter getFormatter() {
		return formatter;
	}

	public String getAttribute(String name) {
		StyleAttribute att = attributes.get(name);
		return att != null ? att.getValue() : null;
	}

	public String getCss() {
		return css;
	}

	/**
	 * the formatter can only be set once in the construction of the formatter
	 * 
	 * @param formatter
	 */
	public void setFormatter(IStyleFormatter formatter) {
		assert (formatter != null);
		assert (this.formatter == null);
		this.formatter = formatter;
	}

	@Override
	public String toString() {
		return css;
	}
}
