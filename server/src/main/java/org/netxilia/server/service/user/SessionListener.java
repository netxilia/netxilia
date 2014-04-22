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
package org.netxilia.server.service.user;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This class listens for the session creation and destruction and cleans-up memory one a session terminated.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SessionListener implements HttpSessionListener {

	private IWindowProcessor windowProcessor;

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		//
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		if (windowProcessor == null) {
			// not thread-safe but the same object is returned each time
			ServletContext servletContext = sessionEvent.getSession().getServletContext();
			ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
			windowProcessor = springContext.getBean(IWindowProcessor.class);
		}
		windowProcessor.terminateSession(sessionEvent.getSession().getId());
	}

}
