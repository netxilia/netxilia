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
package org.netxilia.api.impl.display;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netxilia.api.display.Style;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.display.StyleGroup;
import org.netxilia.api.model.WorkbookId;

public class WorkbookStyleDefinitions {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WorkbookStyleDefinitions.class);
	private final IStyleRepository repository;
	private final WorkbookId parentId;

	private final Map<Style, StyleDefinition> styleDefinitions = new LinkedHashMap<Style, StyleDefinition>();
	private final Map<StyleGroup, List<StyleDefinition>> styleDefinitionsByGroup = new LinkedHashMap<StyleGroup, List<StyleDefinition>>();

	public WorkbookStyleDefinitions(IStyleRepository reporitory, WorkbookId parentId, List<StyleDefinition> styles) {
		this.repository = reporitory;
		this.parentId = parentId;
		for (StyleDefinition def : styles) {
			styleDefinitions.put(def.getId(), def);
			addToGroup(def);
		}
	}

	private WorkbookStyleDefinitions getParent() {
		if (parentId == null) {
			return null;
		}
		try {
			return repository.getDefinitions(parentId);
		} catch (Exception e) {
			log.error("Cannot load parent definitions: " + e, e);
			return null;
		}
	}

	public StyleDefinition getDefinition(Style style) {
		StyleDefinition def = styleDefinitions.get(style);
		if (def != null) {
			return def;
		}
		WorkbookStyleDefinitions parent = getParent();
		if (parent != null) {
			return parent.getDefinition(style);
		}
		return null;
	}

	public Collection<StyleDefinition> getDefinitionsByGroup(StyleGroup group) {
		WorkbookStyleDefinitions parent = getParent();
		if (parent == null) {
			List<StyleDefinition> defs = styleDefinitionsByGroup.get(group);
			if (defs == null) {
				return Collections.emptyList();
			}
			return Collections.unmodifiableCollection(defs);
		}
		Collection<StyleDefinition> ownDefs = styleDefinitionsByGroup.get(group);
		if (ownDefs == null) {
			return parent.getDefinitionsByGroup(group);
		}
		Set<StyleDefinition> allDefs = new LinkedHashSet<StyleDefinition>(parent.getDefinitionsByGroup(group));
		allDefs.addAll(ownDefs);
		return allDefs;
	}

	public Collection<StyleDefinition> getDefinitions() {
		WorkbookStyleDefinitions parent = getParent();
		if (parent == null) {
			return Collections.unmodifiableCollection(styleDefinitions.values());
		}
		if (styleDefinitions.size() == 0) {
			return parent.getDefinitions();
		}

		Set<StyleDefinition> allDefs = new LinkedHashSet<StyleDefinition>(parent.getDefinitions());
		allDefs.addAll(styleDefinitions.values());
		return allDefs;
	}

	private void addToGroup(StyleDefinition def) {
		List<StyleDefinition> defs = styleDefinitionsByGroup.get(def.getGroup());
		if (defs == null) {
			defs = new ArrayList<StyleDefinition>();
			styleDefinitionsByGroup.put(def.getGroup(), defs);
		}
		defs.add(def);
	}
}
