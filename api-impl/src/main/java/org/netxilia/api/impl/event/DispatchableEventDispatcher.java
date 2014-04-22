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

import java.util.EventObject;
import java.util.concurrent.Executor;

/**
 * Dispatches your events using a particular {@link java.util.concurrent.Executor Executor}. This class does not manage
 * event listeners, making it ideal when you want to write your own listener management code or you have an existing
 * dispatch mechanism like {@link java.beans.PropertyChangeSupport PropertyChangeSupport} to integrate with. Most
 * applications will probably want to use the more complete subclass {@link DispatchableEventSupport
 * DispatchableSupport} which adds listener management facilities.
 * 
 * @param <T>
 *            the type of the target this dispatcher delivers events to, usually an <code>EventListener</code>
 * @see org.recoil.pixel.dispatchable.DispatchableEventSupport
 */
public class DispatchableEventDispatcher<T> {
	/** The {@link java.util.concurrent.Executor Executor} to use to fire events if no more specific one is specified */
	private final Executor defaultExecutor;

	/**
	 * Construct a new dispatcher with the given default {@link java.util.concurrent.Executor Executor}
	 * 
	 * @param executor
	 *            the default executor to use
	 */
	public DispatchableEventDispatcher(Executor executor) {
		defaultExecutor = executor;
	}

	/**
	 * Get the default {@link java.util.concurrent.Executor Executor}
	 * 
	 * @return the {@link java.util.concurrent.Executor Executor} to use to dispatch events if no more specific one is
	 *         provided
	 */
	public Executor getDefaultExecutor() {
		return defaultExecutor;
	}

	/**
	 * Fire an event with a specific {@link java.util.concurrent.Executor Executor}
	 * 
	 * @param a_executor
	 *            the executor to use to fire this event, overriding the default one
	 * @param a_target
	 *            the target of the event, usually an <code>EventListener</code>
	 * @param a_e
	 *            the executable event to fire
	 */
	protected void fireEvent(Executor a_executor, T a_target, DispatchableEvent<T, ? extends EventObject> a_e) {
		a_e.dispatch(a_executor, a_target);
	}

	/**
	 * Fire an event using the default {@link java.util.concurrent.Executor Executor} for this dispatcher.
	 * 
	 * @param a_target
	 *            the target of the event, usually an <code>EventListener</code>
	 * @param a_e
	 *            the executable event to fire
	 */
	public void fireEvent(T a_target, DispatchableEvent<T, ? extends EventObject> a_e) {
		fireEvent(getDefaultExecutor(), a_target, a_e);
	}
}