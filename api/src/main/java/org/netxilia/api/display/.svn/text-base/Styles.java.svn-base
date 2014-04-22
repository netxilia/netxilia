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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.netxilia.api.concurrent.LRUCache;

import com.google.common.collect.ImmutableList;

/**
 * The style is a series of @ Style . E.g: <b>a-l b fg-ffffff</b> or <b>b i</b>. Only one entry for each group is
 * allowed. More items without group are allowed.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class Styles {
	private final static int CACHE_SIZE = 50;
	private final static LRUCache<String, Styles> cache = new LRUCache<String, Styles>(CACHE_SIZE,
			new LRUCache.IValueProvider<String, Styles>() {
				public Styles getValue(String key) {
					return new Styles(key);
				}
			});

	private final String asString;
	private final List<Style> items;

	/** Cache the hash code for the list of entries */
	private int hash; // default to 0

	private Styles(String styles) {
		this.asString = styles;
		String[] entriesString = styles.split(" ");
		items = new ArrayList<Style>(entriesString.length);
		for (String s : entriesString) {
			items.add(new Style(s));
		}
	}

	private Styles(List<Style> items) {
		this.items = ImmutableList.copyOf(items);
		// this can be cached
		StringBuilder sb = new StringBuilder();
		for (Style entry : items) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(entry.toString());
		}
		asString = sb.toString();
	}

	public static Styles styles(String styles) {
		return valueOf(styles);
	}

	public static Styles styles(List<Style> items) {
		return new Styles(items);
	}

	public List<Style> getItems() {
		return Collections.unmodifiableList(items);
	}

	@Override
	public String toString() {
		return asString;
	}

	public boolean contains(Styles style) {
		for (Style entry1 : style.items) {
			if (!items.contains(entry1)) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(Style entry) {
		return items.contains(entry);
	}

	@Override
	public int hashCode() {
		int h = hash; // see String.hashCode()
		if (h == 0) {
			h = 1 + (items == null ? 0 : items.hashCode());
			hash = h;
		}
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		Styles other = (Styles) obj;
		return items == null ? other.items == null : items.equals(other.items);
	}

	public static Styles valueOf(String s) {
		if (s == null) {
			return null;
		}
		return cache.getCached(s);
	}

}
