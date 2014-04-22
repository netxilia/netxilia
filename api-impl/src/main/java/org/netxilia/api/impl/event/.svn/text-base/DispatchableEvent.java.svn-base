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
 * Abstract class used by applications to create and fire their own ExecutableEvents. Typically this class is subclassed
 * anonymously and used in conjunction with {@link DispatchableEventDispatcher DispatchableEventDispatcher} or
 * {@link DispatchableEventSupport DispatchableEventSupport}. eg, Assuming you were dispatching AWTEvents to
 * AWTEventListeners:
 * 
 * <pre>
 *  <code>
 *      DispatchableEventSupport<AWTEventListener> support = ...
 *      
 *      public void fireEvent(AWTEvent e) {
 *          support.fireEvent(new DispatchableEvent&lt;AWTEventListener, AWTEvent&gt;(e) {
 *              public void dispatch(AWTEventListener target, AWTEvent event) {
 *                  target.eventDispatched(event);
 *              }     
 *          });
 *      }
 *  </code>
 * </pre>
 * 
 * @param <T>
 *            the target the Event will be delivered to
 * @param <E>
 *            the event type delivered by this DispatchableEvent
 * @see org.recoil.pixel.dispatchable.DispatchableEventDispatcher
 * @see org.recoil.pixel.dispatchable.DispatchableEventSupport
 */
public abstract class DispatchableEvent<T, E extends EventObject> {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DispatchableEvent.class);

	/** The event to dispatch */
	private final E event;

	/**
	 * Construct a new <code>DispatchableEvent</code> for the given <code>EventObject</code>
	 * 
	 * @param event
	 *            the event that will be dispatched
	 */
	public DispatchableEvent(E event) {
		this.event = event;
	}

	/**
	 * Called by the DispatchableEventDispatcher to fire the event using the correct <code>Executor</code> and target
	 * 
	 * @param executor
	 *            the <code>Executor</code> that will dispatch the event on the correct Thread
	 * @param target
	 *            the target (eg, a Listener) to which the event is to be delivered
	 */
	void dispatch(Executor executor, final T target) {
		executor.execute(new Runnable() {
			public void run() {
				try {
					dispatch(target, event);
				} catch (Exception ex) {
					log.error("Exception dispaching event:" + event + ":" + ex, ex);
				}
			}
		});
	}

	/**
	 * Method implemented by concrete subclass to dispatch the event to a target. When a <code>DispatchableEvent</code>
	 * is used with {@link DispatchableEventDispatcher DispatchableEventDispatcher} this method is called back once for
	 * each target, on the correct Thread determined by the Executor in use in the DispatchableEventDispatcher.
	 * 
	 * @param target
	 *            the target the event should be delivered to
	 * @param event
	 *            the event to be delivered
	 */
	public abstract void dispatch(T target, E event);
}
