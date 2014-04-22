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
package org.netxilia.api.user;

import java.security.AccessController;

/**
 * This class allows the calling code to execute privileged actions on the protected objects. When the calling code sets
 * the priveleged mode the permissions on the accessed sheets are no longer checked.
 * 
 * TODO Should have a look to {@link AccessController#doPrivileged(java.security.PrivilegedAction)} for a possible
 * replacement.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class AclPrivilegedMode {
	private static ThreadLocal<Boolean> privilegedMode = new ThreadLocal<Boolean>();

	/**
	 * allows the code in the current thread to access the objects. For example access to users sheet only to get the
	 * user's credentials.
	 * 
	 * @return true if the privileged mode was set before. If was set the clear method should not be called.
	 */
	public static boolean set() {
		boolean isSet = isSet();
		privilegedMode.set(Boolean.TRUE);
		return isSet;
	}

	/**
	 * clears the privileged mode
	 */
	public static void clear() {
		privilegedMode.set(Boolean.FALSE);
	}

	public static boolean isSet() {
		return (Boolean.TRUE.equals(privilegedMode.get()));
	}

}
