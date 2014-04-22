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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.netxilia.api.impl.IExecutorServiceFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class OneActorPerThreadExecutorServiceFactory implements IExecutorServiceFactory {
	private int threadCount = 1;

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	@Override
	public ExecutorService newExecutorService(String name) {
		ThreadFactory threadFactory = new ThreadFactoryBuilder().setThreadFactory(Executors.defaultThreadFactory())
				.setDaemon(true).setNameFormat(name + "-%d").build();
		return new OneThreadPerActorExecutor(0, threadCount, 0L, TimeUnit.MILLISECONDS, threadFactory);
	}

}
