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
package org.netxilia.api.impl.value;
import java.util.ArrayList;
import java.util.List;

import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.api.value.IGenericValueParser;
import org.netxilia.api.value.StringValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


public class GenericValueParseServiceImpl implements IGenericValueParseService, BeanPostProcessor {
	private final List<IGenericValueParser> parsers = new ArrayList<IGenericValueParser>();

	@Override
	public IGenericValue parse(String text) {
		if (text == null) {
			return new StringValue("");
		}
		for (IGenericValueParser parser : parsers) {
			IGenericValue parsedValue = parser.parse(text);
			if (parsedValue != null) {
				return parsedValue;
			}
		}

		return new StringValue(text);
	}

	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof IGenericValueParser) {
			parsers.add((IGenericValueParser) bean);
		}
		return bean;
	}

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
