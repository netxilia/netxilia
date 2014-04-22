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
package org.netxilia.spi.impl.format;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.StyleApplyMode;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.user.AclPrivilegedMode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestStyleService {
	private ApplicationContext context;
	private IStyleService styleService;

	@Before
	public void setup() {
		context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		styleService = context.getBean(IStyleService.class);
		AclPrivilegedMode.set();
	}

	@Test
	public void testSet() throws NetxiliaResourceException, NetxiliaBusinessException {
		Styles style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b i"), Styles.valueOf("u s"),
				StyleApplyMode.set);

		Assert.assertEquals(Styles.valueOf("u s"), style);

		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b i"), null, StyleApplyMode.set);

		Assert.assertNull(style);
	}

	@Test
	public void testAdd() throws NetxiliaResourceException, NetxiliaBusinessException {
		Styles style = styleService.applyStyle(new WorkbookId("SYSTEM"), null, Styles.valueOf("u s"),
				StyleApplyMode.add);

		Assert.assertEquals(Styles.valueOf("u s"), style);

		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b i"), Styles.valueOf("u s"),
				StyleApplyMode.add);

		Assert.assertEquals(Styles.valueOf("u s b i"), style);

		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b i"), null, StyleApplyMode.add);

		Assert.assertEquals(Styles.valueOf("b i"), style);

		// toggle
		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b a-l"), Styles.valueOf("u a-c"),
				StyleApplyMode.add);

		Assert.assertEquals(Styles.valueOf("u a-c b"), style);
	}

	@Test
	public void testClear() throws NetxiliaResourceException, NetxiliaBusinessException {
		Styles style = styleService.applyStyle(new WorkbookId("SYSTEM"), null, Styles.valueOf("u s"),
				StyleApplyMode.clear);

		Assert.assertNull(style);

		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b i"), Styles.valueOf("i s"),
				StyleApplyMode.clear);

		Assert.assertEquals(Styles.valueOf("b"), style);

		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b i"), null, StyleApplyMode.clear);

		Assert.assertEquals(Styles.valueOf("b i"), style);

		// toggle
		style = styleService.applyStyle(new WorkbookId("SYSTEM"), Styles.valueOf("b a-l"), Styles.valueOf("u a-c"),
				StyleApplyMode.clear);

		Assert.assertEquals(Styles.valueOf("b"), style);
	}
}
