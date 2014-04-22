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
package org.netxilia.api.impl.model;

import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.impl.user.NetxiliaSecurityContext;

import com.google.common.base.Function;

/**
 * This class takes the current user in the local thread when the command was sent and resets it when the command is
 * executed.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class FunctionWithUser<F, T> implements Function<F, T> {
	private final NetxiliaSecurityContext securityContext;
	private final Function<F, T> delegate;

	public FunctionWithUser(ISpringUserService userService, Function<F, T> delegate) {
		this.securityContext = new NetxiliaSecurityContext(userService);
		this.delegate = delegate;
	}

	@Override
	public T apply(F input) {
		try {
			securityContext.set();
			return delegate.apply(input);
		} finally {
			securityContext.restore();
		}
	}
}
