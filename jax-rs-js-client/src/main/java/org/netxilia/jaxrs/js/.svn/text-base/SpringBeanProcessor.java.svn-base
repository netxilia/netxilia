/*******************************************************************************
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
package org.netxilia.jaxrs.js;

import javax.ws.rs.Path;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * The processor will register any bean annotated with @Path once they are
 * initialize by Spring.
 * 
 * @author <a href="mailto:ax.craciun@gmail.com">Alexandru Craciun</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessor implements BeanPostProcessor {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SpringBeanProcessor.class);

	@Autowired
	private ResourceRegistry resourceRegistry;

	public ResourceRegistry getResourceRegistry() {
		return resourceRegistry;
	}

	public void setResourceRegistry(ResourceRegistry resourceRegistry) {
		this.resourceRegistry = resourceRegistry;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (isRestRootResource(bean))
			log.info("NEW RESOURCE:" + beanName);

		return bean;
	}

	private boolean isRestRootResource(Object bean) {
		return AnnotationUtils.findAnnotation(bean.getClass(), Path.class) != null;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
