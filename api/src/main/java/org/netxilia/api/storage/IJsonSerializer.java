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
package org.netxilia.api.storage;

import java.lang.reflect.Type;

import org.netxilia.api.exception.StorageException;

/**
 * Creates an abstraction around the json library. It is used also on the server side for long-time storage (should use
 * annotations to protect against refactoring!)
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IJsonSerializer {
	/**
	 * 
	 * @param object
	 * @return
	 * @throws StorageException
	 *             if the object is not serializable by JSON
	 */
	public String serialize(Object object) throws StorageException;

	/**
	 * Creates an object of the given type and fills it in with the information from the JSON string.
	 * <p>
	 * TODO for classes without no-parameter constructor should use either annotation or builders.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param jsonString
	 * @return
	 * @throws StorageException
	 */
	public <T> T deserialize(Class<T> clazz, String jsonString) throws StorageException;

	public Object deserialize(Type type, String jsonString) throws StorageException;

}
