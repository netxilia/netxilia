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
import java.util.regex.Pattern;

/**
 * The key of a workbook references a workbook in the system. The user gives this key (that should be unique in the
 * system) and than, for a given workbook he cannot change it. It's not the name of the workbook but can be derived
 * from.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class WorkbookId implements Serializable, Comparable<WorkbookId> {
	private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z_][a-zA-Z_1-9]*");
	private static final long serialVersionUID = 1L;

	private final String key;

	public WorkbookId(String key) {
		if (!isValid(key)) {
			throw new IllegalArgumentException("A workbook name can only contain letters, numbers and underscore");
		}
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public static boolean isValid(String key) {
		return key != null && NAME_PATTERN.matcher(key).matches();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		WorkbookId other = (WorkbookId) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return key;
	}

	@Override
	public int compareTo(WorkbookId o) {
		return key.compareTo(o.key);
	}
}
