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
package org.netxilia.api.impl.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.io.filefilter.SuffixFileFilter;

/**
 * This class add to the classpath the application's main folder.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DynamicClassLoader {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DynamicClassLoader.class);

	private Class<?>[] parameters = new Class[] { URL.class };

	public DynamicClassLoader() {
		try {
			File home = new File(System.getProperty("user.home"), "netxilia");
			if (!home.exists()) {
				if (!home.mkdir()) {
					log.error("Could not create Netxilia storage directory:" + home);
					return;
				}
			}
			addFile(home);
			for (File jar : home.listFiles((FilenameFilter) new SuffixFileFilter(".jar"))) {
				addFile(jar);
			}
		} catch (IOException e) {
			log.error("ERROR:" + e);
		}

	}

	public void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}

	public void addFile(File f) throws IOException {
		log.info("ADDED:" + f.getAbsolutePath());
		addURL(new URL("file", "", f.getAbsolutePath()));
	}

	public void addURL(URL u) throws IOException {

		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}// end try catch

	}// end method

}
