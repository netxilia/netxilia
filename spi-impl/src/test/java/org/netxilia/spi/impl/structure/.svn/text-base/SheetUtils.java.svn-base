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
package org.netxilia.spi.impl.structure;

import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.GenericValueUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SheetUtils {
	public static ISheet sheetWithCell(Object... cellValues) throws AlreadyExistsException, StorageException,
			NotFoundException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		NetxiliaSystemImpl nx = context.getBean(NetxiliaSystemImpl.class);
		nx.setAclService(new NoCheckAclServiceImpl());
		// nx.setStorageService(new InMemoryStorageServiceImpl());
		// nx.setExecutorServiceFactory(new DirectExecutorServiceFactory());
		// nx.setUserService(new UserServiceImpl());
		// nx.setFormulaCalculatorFactory(new FormulaCalculatorFactoryImpl());
		return sheetWithCell(nx, cellValues);
	}

	public static ISheet sheetWithCell(NetxiliaSystemImpl wp, Object... cellValues) throws AlreadyExistsException,
			StorageException, NotFoundException {
		Workbook wb = Workbook.newInstance(wp, new WorkbookId("wktest"));
		ISheet sheet = wb.addNewSheet("test", SheetType.normal);

		for (int i = 0; i < cellValues.length; i += 2) {
			CellReference ref = new CellReference(cellValues[i].toString());
			sheet.sendValue(ref, GenericValueUtils.objectAsValue(cellValues[i + 1]));
		}
		return sheet;
	}
}
