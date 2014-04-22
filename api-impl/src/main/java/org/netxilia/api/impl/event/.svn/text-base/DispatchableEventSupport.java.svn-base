/*
 Copyright 2005 Andrew Thompson
 
 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.netxilia.api.impl.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * Dispatches your events using a particular {@link java.util.concurrent.Executor Executor} and provides listener
 * management. This class supports a defauly {@link java.util.concurrent.Executor Executor} for the listeners, which you
 * can override by providing a custom {@link java.util.concurrent.Executor Executor} when you add a listener.
 * 
 * @param <L>
 *            the type of {@link java.util.EventListener EventListener} being dispatched to
 */
public class DispatchableEventSupport<L extends EventListener> extends DispatchableEventDispatcher<L> {
	/** The listeners being managed */
	Collection<ExecutableListener<L>> listeners = new ArrayList<ExecutableListener<L>>();

	/**
	 * Create a new DispatchableEventSupport with a simple {@link org.recoil.pixel.executor.DirectExecutor} as the
	 * default {@link java.util.concurrent.Executor Executor}.
	 */
	public DispatchableEventSupport() {
		this(new DirectExecutor());
	}

	/**
	 * Creates a new DispatchableEventSupport
	 * 
	 * @param executor
	 *            the default {@link java.util.concurrent.Executor Executor} to use when no specific
	 *            {@link java.util.concurrent.Executor Executor} is provided with a listener.
	 */
	public DispatchableEventSupport(Executor executor) {
		super(executor);
	}

	/**
	 * Add a listener that will use the default {@link java.util.concurrent.Executor Executor}
	 * 
	 * @param listener
	 *            the listener that will receive events
	 */
	public void addListener(L listener) {
		addListener(listener, getDefaultExecutor());
	}

	/**
	 * Add a listener with a custom {@link java.util.concurrent.Executor Executor}
	 * 
	 * @param listener
	 *            the listener that will receive events
	 * @param executor
	 *            the {@link java.util.concurrent.Executor Executor} that will dispatch events to this listener
	 */
	public synchronized void addListener(L listener, Executor executor) {
		checkNullArgument(listener, "Listener may not be null");
		checkNullArgument(executor, "Executor may not be null");
		listeners.add(new ExecutableListener<L>(listener, executor));
	}

	/**
	 * Remove the first occurance of the indicated listener
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeListener(L listener) {
		removeListener(listener, getDefaultExecutor());
	}

	/**
	 * Remove the first occurance of the indicated listener with the given custom {@link java.util.concurrent.Executor
	 * Executor}
	 * 
	 * @param listener
	 *            the listener to remove
	 * @param executor
	 *            the custom executor that was associated with the listener when it was added
	 */
	public synchronized void removeListener(L listener, Executor executor) {
		checkNullArgument(listener, "Listener may not be null");
		checkNullArgument(executor, "Executor may not be null");
		listeners.remove(new ExecutableListener<L>(listener, executor));
	}

	/**
	 * Remove the first occurance of the indicated listener regardless of which {@link java.util.concurrent.Executor
	 * Executor} it is paired with
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public synchronized void removeFirstInstanceOfListener(L listener) {
		for (Iterator<ExecutableListener<L>> i = snapshotListeners().iterator(); i.hasNext();) {
			ExecutableListener<L> l = i.next();
			if (l.getListener().equals(listener)) {
				i.remove();
				break;
			}
		}
	}

	/**
	 * Get all currently registered {@link java.util.EventListener}s
	 * 
	 * @return an {@link java.util.Iterator} copy of the registered listeners
	 */
	public Iterator<L> getListeners() {
		Collection<L> result = new ArrayList<L>();
		for (ExecutableListener<L> l : snapshotListeners()) {
			result.add(l.getListener());
		}
		return result.iterator();
	}

	/**
	 * Test whether an argument is null otherwise throw an IllegalArgumentException
	 * 
	 * @param argument
	 *            the argument to check
	 * @param msg
	 *            the message for the IllegalArgumentException thrown if the argument is null
	 */
	private static void checkNullArgument(Object argument, String msg) {
		if (argument == null) {
			throw new IllegalArgumentException(msg);
		}
	}

	/**
	 * Fire an event to each registered listener
	 * 
	 * @param e
	 *            the {@link DispatchableEvent DispatchableEvent} to dispatch, contains the
	 *            {@link java.util.EventObject EventObject} that will be delivered to the listeners
	 */
	public void fireEvent(DispatchableEvent<L, ? extends EventObject> e) {
		for (ExecutableListener<L> l : snapshotListeners()) {
			fireEvent(l.getExecutor(), l.getListener(), e);
		}
	}

	/**
	 * Get a copy of the currently registered listeners
	 * 
	 * @return a copy of the listeners currently registered
	 */
	private synchronized Collection<ExecutableListener<L>> snapshotListeners() {
		Collection<ExecutableListener<L>> listenerSnapshot = new ArrayList<ExecutableListener<L>>(listeners.size());
		listenerSnapshot.addAll(listeners);
		return listenerSnapshot;
	}

	/**
	 * 
	 * @return true if there is at least one listener registered
	 */
	public boolean hasListeners() {
		return !listeners.isEmpty();
	}

	/**
	 * Helper class used to store listeners with an associated {@link java.util.concurrent.Executor Executor}
	 * 
	 * @param <L>
	 *            the type of {@link java.util.EventListener EventListener} being dispatched to
	 */
	private static class ExecutableListener<L extends EventListener> {
		/** The listener in this record */
		private final L listener;
		/** The executor associated with the listener */
		private final Executor executor;

		/**
		 * Construct a new listener-{@link java.util.concurrent.Executor Executor} pair
		 * 
		 * @param listener
		 *            the listenerfor this record
		 * @param executor
		 *            the executor to associate with the listener
		 */
		public ExecutableListener(L listener, Executor executor) {
			this.listener = listener;
			this.executor = executor;
		}

		/**
		 * Get the listener for this record
		 * 
		 * @return the listener
		 */
		public L getListener() {
			return listener;
		}

		/**
		 * The {@link java.util.concurrent.Executor Executor} associated with the listener
		 * 
		 * @return the executor
		 */
		public Executor getExecutor() {
			return executor;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object a_other) {
			if (!(a_other instanceof ExecutableListener)) {
				return false;
			}
			ExecutableListener<?> other = (ExecutableListener<?>) a_other;
			return getExecutor().equals(other.getExecutor()) && getListener().equals(other.getListener());
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			int result = 17;
			result = 37 * result + getExecutor().hashCode();
			result = 37 * result + getListener().hashCode();
			return result;
		}
	}
}
