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
package org.netxilia.api.exception;

import java.util.concurrent.TimeoutException;

public class IncompleteTaskException extends NetxiliaResourceException {
	public enum Reason {
		timeout, interrupted, other
	};

	private static final long serialVersionUID = 1L;

	private final Reason reason;

	public IncompleteTaskException(Throwable cause) {
		super(cause);
		if (cause instanceof TimeoutException) {
			this.reason = Reason.timeout;
		} else if (cause instanceof InterruptedException) {
			this.reason = Reason.interrupted;
		} else {
			this.reason = Reason.other;
		}
	}

	public Reason getReason() {
		return reason;
	}

	@Override
	public String getMessage() {
		return "InterruptedException reason:" + reason;
	}

	@Override
	public String toString() {
		return "IncompleteTaskException [reason=" + reason + "]";
	}

}
