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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.util.concurrent.MoreExecutors;

/**
 * This is a copy the Google concurrent SameThreadExecutor. See {@link MoreExecutors#sameThreadExecutor()}. The only
 * difference is that the submitted tasks are {@link ListenableFutureTask}
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SameThreadExecutor extends AbstractExecutorService {

	/**
	 * Lock used whenever accessing the state variables (runningTasks, shutdown, terminationCondition) of the executor
	 */
	private final Lock lock = new ReentrantLock();

	/** Signaled after the executor is shutdown and running tasks are done */
	private final Condition termination = lock.newCondition();

	/*
	 * Conceptually, these two variables describe the executor being in one of three states: - Active: shutdown == false
	 * - Shutdown: runningTasks > 0 and shutdown == true - Terminated: runningTasks == 0 and shutdown == true
	 */
	private int runningTasks = 0;
	private boolean shutdown = false;

	/* @Override */
	public void execute(Runnable command) {
		startTask();
		try {
			command.run();
		} finally {
			endTask();
		}
	}

	/* @Override */
	public boolean isShutdown() {
		lock.lock();
		try {
			return shutdown;
		} finally {
			lock.unlock();
		}
	}

	/* @Override */
	public void shutdown() {
		lock.lock();
		try {
			shutdown = true;
		} finally {
			lock.unlock();
		}
	}

	// See sameThreadExecutor javadoc for unusual behavior of this method.
	/* @Override */
	public List<Runnable> shutdownNow() {
		shutdown();
		return Collections.emptyList();
	}

	/* @Override */
	public boolean isTerminated() {
		lock.lock();
		try {
			return shutdown && runningTasks == 0;
		} finally {
			lock.unlock();
		}
	}

	/* @Override */
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		long nanos = unit.toNanos(timeout);
		lock.lock();
		try {
			for (;;) {
				if (isTerminated()) {
					return true;
				} else if (nanos <= 0) {
					return false;
				} else {
					nanos = termination.awaitNanos(nanos);
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Checks if the executor has been shut down and increments the running task count.
	 * 
	 * @throws RejectedExecutionException
	 *             if the executor has been previously shutdown
	 */
	private void startTask() {
		lock.lock();
		try {
			if (isShutdown()) {
				throw new RejectedExecutionException("Executor already shutdown");
			}
			runningTasks++;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Decrements the running task count.
	 */
	private void endTask() {
		lock.lock();
		try {
			runningTasks--;
			if (isTerminated()) {
				termination.signalAll();
			}
		} finally {
			lock.unlock();
		}
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
		return new ListenableFutureTask<T>(callable);
	}

	@Override
	protected <T extends Object> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new ListenableFutureTask<T>(runnable, value);
	};

}
