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
package org.netxilia.spi.impl.formula.function;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.spi.formula.Functions;
import org.netxilia.spi.formula.SkipFunction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * Provides registry facilities for existing functions.
 * 
 * @author catac
 * @since Nov 19, 2009
 */
public class FunctionRegistry implements BeanPostProcessor {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FunctionRegistry.class);

	private Map<String, IFunction> funMap = new HashMap<String, IFunction>();

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (isFunctionSet(bean)) {
			registerMethods(bean);
			log.info("NEW Function set:" + beanName);
		}

		return bean;
	}

	private boolean isFunctionSet(Object bean) {
		return AnnotationUtils.findAnnotation(bean.getClass(), Functions.class) != null;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/** Register a new function in the system */
	public void registerFunction(IFunction fun) {
		String name = fun.getName();
		if (funMap.containsKey(name)) {
			throw new IllegalArgumentException("Function '" + name + "' is already registered");
		}
		funMap.put(name, fun);
	}

	/** Register a bulk of functions in the system */
	public void registerFunctionList(List<IFunction> funList) {
		for (IFunction fun : funList) {
			registerFunction(fun);
		}
	}

	/**
	 * Get the function with the given name
	 * 
	 * @return the function, or null if there is no function with that name.
	 */
	public IFunction getFunction(String name) {
		IFunction fun = funMap.get(name);
		if (fun == null) {
			throw new IllegalArgumentException("Unknown function: " + name);
		}
		return fun;
	}

	/**
	 * register all the public methods of the given instance
	 * 
	 * @param instance
	 */
	public void registerMethods(Object instance) {
		for (Method m : instance.getClass().getDeclaredMethods())
			if ((m.getModifiers() & Modifier.PUBLIC) != 0
					&& AnnotationUtils.findAnnotation(m, SkipFunction.class) == null)
				registerFunction(new MethodWrapper(instance, m));
	}
}
