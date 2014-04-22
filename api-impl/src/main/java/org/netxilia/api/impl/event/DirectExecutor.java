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

import java.util.concurrent.Executor;

/**
 * DirectExecutor simply executes a <code>Runnable</code> immediately. This executor is synchronous, the code in the
 * <code>Runnable</code> is executed on the <code>Thread</code> that calls <code>execute</code>
 */
public class DirectExecutor implements Executor {

	/**
	 * Execute the given <code>Runnable</code> immediately.
	 * 
	 * @param r
	 *            the runnable to run
	 */
	public void execute(Runnable r) {
		r.run();
	}
}
