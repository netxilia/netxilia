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
package org.netxilia.api.impl.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.netxilia.api.concurrent.IFutureListener;
import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.exception.IncompleteTaskException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;

import com.google.common.util.concurrent.AbstractListenableFuture;

public class MutableFuture<V> extends AbstractListenableFuture<V> implements IListenableFuture<V> {

	public MutableFuture(NetxiliaBusinessException e) {
		super();
		setException(e);
	}

	public MutableFuture(V value) {
		super();
		set(value);
	}

	public MutableFuture() {
		super();
	}

	/**
	 * Sets the value of this future. This method will return {@code true} if the value was successfully set, or
	 * {@code false} if the future has already been set or cancelled.
	 * 
	 * @param newValue
	 *            the value the future should hold.
	 * @return true if the value was successfully set.
	 */
	@Override
	public boolean set(V newValue) {
		return super.set(newValue);
	}

	/**
	 * Sets the future to having failed with the given exception. This exception will be wrapped in an
	 * ExecutionException and thrown from the get methods. This method will return {@code true} if the exception was
	 * successfully set, or {@code false} if the future has already been set or cancelled.
	 * 
	 * @param t
	 *            the exception the future should hold.
	 * @return true if the exception was successfully set.
	 */
	@Override
	public boolean setException(Throwable t) {
		return super.setException(t);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * A ValueFuture is never considered in the running state, so the {@code mayInterruptIfRunning} argument is ignored.
	 */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return super.cancel();
	}

	@Override
	public void addListener(final IFutureListener<V> listener, Executor exec) {
		super.addListener(new Runnable() {
			@Override
			public void run() {
				listener.ready(MutableFuture.this);
			}
		}, exec);

	}

	@Override
	public V getNonBlocking() throws NetxiliaResourceException, NetxiliaBusinessException {
		return getNonBlocking(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
	}

	@Override
	public V getNonBlocking(long timeout, TimeUnit timeoutUnit) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		try {
			return get(timeout, timeoutUnit);
		} catch (InterruptedException e) {
			throw new IncompleteTaskException(e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof NetxiliaBusinessException) {
				throw (NetxiliaBusinessException) e.getCause();
			}
			if (e.getCause() instanceof NetxiliaResourceException) {
				throw (NetxiliaResourceException) e.getCause();
			}
			throw new NetxiliaResourceException(e);
		} catch (TimeoutException e) {
			throw new IncompleteTaskException(e);
		}
	}
}
