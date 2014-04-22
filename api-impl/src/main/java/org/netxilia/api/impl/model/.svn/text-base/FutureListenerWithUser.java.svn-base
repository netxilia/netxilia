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

import java.util.concurrent.Future;

import org.netxilia.api.concurrent.IFutureListener;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.impl.user.NetxiliaSecurityContext;

/**
 * This listener sets the authentication present when the object was created, back when the listener is called.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class FutureListenerWithUser<V> implements IFutureListener<V> {
	private final NetxiliaSecurityContext securityContext;
	private final IFutureListener<V> delegate;

	public FutureListenerWithUser(ISpringUserService userService, IFutureListener<V> delegate) {
		this.securityContext = new NetxiliaSecurityContext(userService);
		this.delegate = delegate;
	}

	@Override
	public void ready(Future<V> future) {
		try {
			securityContext.set();
			delegate.ready(future);
		} finally {
			securityContext.restore();
		}
	}

}
