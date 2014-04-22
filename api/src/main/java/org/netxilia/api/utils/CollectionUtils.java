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
package org.netxilia.api.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Different utility methods for collections
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class CollectionUtils {
	@SuppressWarnings("rawtypes")
	public final static IListElementCreator NULL_ELEMENT_CREATOR = new IListElementCreator<Object>() {
		@Override
		public Object newElement(int index) {
			return null;
		}
	};

	public static <T> Iterable<T> iterable(final Iterator<T> iterator) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return iterator;
			}
		};
	}

	public static String join(Collection<?> collection, String sep) {
		StringBuilder s = new StringBuilder();
		for (Object obj : collection) {
			if (s.length() > 0) {
				s.append(sep);
			}
			if (obj == null) {
				s.append("");
			} else {
				s.append(obj.toString());
			}
		}
		return s.toString();
	}

	public static <E> IListElementCreator<E> sameElementCreator(final E e) {
		return new IListElementCreator<E>() {
			@Override
			public E newElement(int index) {
				return e;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> atLeastSize(List<T> list, int size, IListElementCreator<T> elementCreator) {
		if (size <= list.size()) {
			return list;
		}
		IListElementCreator<T> creator = elementCreator;
		if (creator == null) {
			creator = NULL_ELEMENT_CREATOR;
		}
		while (list.size() < size) {
			list.add(creator.newElement(list.size()));
		}
		return list;
	}
}
