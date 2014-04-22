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
package org.netxilia.api.impl.user;

import org.netxilia.api.user.AclPrivilegedMode;
import org.springframework.security.core.Authentication;

/**
 * This class read the privileges mode and the user in the current thread. It can be used to set it temporarily and than
 * restore the previous data.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class NetxiliaSecurityContext {
	private final ISpringUserService userService;
	private final Authentication authentication;
	private final boolean privilegedMode;

	private Authentication previousAuthentication;
	private boolean previousPrivilegedMode;

	public NetxiliaSecurityContext(ISpringUserService userService) {
		this.userService = userService;
		this.authentication = userService.getSpringAuthentication();
		this.privilegedMode = AclPrivilegedMode.isSet();
	}

	public void set() {
		previousAuthentication = userService.getSpringAuthentication();
		previousPrivilegedMode = AclPrivilegedMode.isSet();
		userService.setSpringAuthentication(authentication);
		if (privilegedMode) {
			AclPrivilegedMode.set();
		} else {
			AclPrivilegedMode.clear();
		}
	}

	public void restore() {
		userService.setSpringAuthentication(previousAuthentication);
		if (previousPrivilegedMode) {
			AclPrivilegedMode.set();
		} else {
			AclPrivilegedMode.clear();
		}
	}
}
