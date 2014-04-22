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
package org.netxilia.api.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.netxilia.api.exception.IncompleteTaskException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;

import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ListenableFuture;

public class NetxiliaListenableFutureAdapter<V> extends ForwardingListenableFuture<V> implements IListenableFuture<V> {
	// private final static Logger log = Logger.getLogger(NetxiliaListenableFutureAdapter.class);

	private volatile boolean hasListeners = false;

	private final ListenableFuture<V> googleDelegate;

	public NetxiliaListenableFutureAdapter(ListenableFuture<V> googleDelegate) {
		this.googleDelegate = googleDelegate;
	}

	@Override
	public void addListener(final IFutureListener<V> listener, Executor exec) {
		hasListeners = true;
		super.addListener(new Runnable() {
			@Override
			public void run() {
				listener.ready(NetxiliaListenableFutureAdapter.this);
			}
		}, exec);

	}

	// @Override
	// protected void done() {
	// super.done();
	// if (!hasListeners) {
	// // if no listener - log the exception at the console otherwise the exception goes unnoticed
	// try {
	// super.get();
	// } catch (ExecutionException ex) {
	// log.error("Exception executing FutureTask: " + ex.getCause(), ex.getCause());
	// } catch (Exception ex) {
	// log.error("Exception executing FutureTask: " + ex, ex);
	// }
	// }
	// }

	@Override
	public V getNonBlocking() throws NetxiliaResourceException, NetxiliaBusinessException {
		return getNonBlocking(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
	}

	@SuppressWarnings("unchecked")
	private <T extends Exception> T getNetxiliaCause(Exception ex, Class<T> clazz) {
		Throwable testEx = ex;
		while (testEx != null) {
			if (clazz.isAssignableFrom(testEx.getClass())) {
				return (T) testEx;
			}
			testEx = testEx.getCause();
		}
		return null;

	}

	@Override
	public V getNonBlocking(long timeout, TimeUnit timeoutUnit) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		try {
			return get(timeout, timeoutUnit);
		} catch (InterruptedException e) {
			throw new IncompleteTaskException(e);
		} catch (ExecutionException e) {
			NetxiliaBusinessException nbe = getNetxiliaCause(e, NetxiliaBusinessException.class);
			if (nbe != null) {
				throw nbe;
			}

			NetxiliaResourceException nre = getNetxiliaCause(e, NetxiliaResourceException.class);
			if (nre != null) {
				throw nre;
			}

			throw new NetxiliaResourceException(e);
		} catch (TimeoutException e) {
			throw new IncompleteTaskException(e);
		}
	}

	@Override
	protected ListenableFuture<V> delegate() {
		return googleDelegate;
	}

}
