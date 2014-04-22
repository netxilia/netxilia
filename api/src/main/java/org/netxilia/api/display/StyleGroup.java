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

import java.io.Serializable;

/**
 * The id of the style group may contain a "-" meaning this group is radio (i.e. only on style belonging to this group
 * should be active at any time). This applies to styles like align for example.
 * 
 * @author sa
 * 
 */
public class StyleGroup implements Serializable {
	public static final StyleGroup DEFAULT_GROUP = new StyleGroup("default");

	private static final long serialVersionUID = 1L;
	private static final String RADIO_PREFIX = "-";
	private final String id;

	public StyleGroup(String id) {
		assert id != null;
		assert id.length() > 1;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public boolean isRadio() {
		return id.startsWith(RADIO_PREFIX);
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StyleGroup other = (StyleGroup) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
