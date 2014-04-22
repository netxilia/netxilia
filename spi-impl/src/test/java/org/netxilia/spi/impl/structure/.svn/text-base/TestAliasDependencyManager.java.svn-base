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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.command.ColumnCommands;
import org.netxilia.api.command.RowCommands;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.dependencies.WorkbookAliasDependencyManager;
import org.netxilia.api.impl.dependencies.WorkbookDependencyManager;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.spi.impl.formula.FormulaContextImpl;

public class TestAliasDependencyManager {
	private Map<String, ISheet> sheets = new HashMap<String, ISheet>();
	private WorkbookAliasDependencyManager amgr;
	private WorkbookDependencyManager dmgr;

	@Before
	public void startup() throws AlreadyExistsException, StorageException, NotFoundException {
		// ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell();
		sheets.put(sheet.getName(), sheet);
		ISheet sheet2 = sheet.getWorkbook().addNewSheet("test2", SheetType.normal);
		sheets.put(sheet2.getName(), sheet2);
		amgr = ((Workbook) sheet.getWorkbook()).getAliasDependencyManager();
		dmgr = ((Workbook) sheet.getWorkbook()).getDependencyManager();
	}

	private ISheet getSheet(String sheetName) {
		return sheets.get(sheetName);
	}

	@Test
	public void testSimpleGetSet() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B2:C3")));

		CellReference ref = new CellReference("test", 1, 0);
		amgr.setAliasDependencies(new Formula("=test2!Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test2!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(ref, refs.iterator().next().getTopLeft());
		Assert.assertEquals(ref, refs.iterator().next().getBottomRight());

	}

	@Test
	public void testChangeAlias() throws StorageException, NotFoundException, CyclicDependenciesException {
		// A2 = B2 + 1

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B2:C3")));

		CellReference ref = new CellReference("test", 1, 0);
		getSheet("test").sendFormula(ref, new Formula("=test2!Alias + 1"));

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("D2:E3")));

		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test2!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(ref, refs.iterator().next().getTopLeft());
		Assert.assertEquals(ref, refs.iterator().next().getBottomRight());

		// verify deps were changed
		List<AreaReference> deps = dmgr.getDependencies(ref);
		Assert.assertNotNull(deps);
		Assert.assertEquals(1, deps.size());
		Assert.assertEquals(new AreaReference("test2!D2:D2"), deps.get(0));
	}

	@Test
	public void testRemoveAlias() throws StorageException, NotFoundException, CyclicDependenciesException {
		// A2 = B2 + 1

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B2:C3")));

		CellReference ref = new CellReference("test", 1, 0);
		getSheet("test").sendFormula(ref, new Formula("=test2!Alias + 1"));

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), null));

		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test2!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(ref, refs.iterator().next().getTopLeft());
		Assert.assertEquals(ref, refs.iterator().next().getBottomRight());

		// verify deps were changed
		List<AreaReference> deps = dmgr.getDependencies(ref);
		Assert.assertNotNull(deps);
		Assert.assertEquals(0, deps.size());
	}

	@Test
	public void testChangeFormula() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B2:C3")));

		CellReference ref = new CellReference("test", 1, 0);
		getSheet("test").sendFormula(ref, new Formula("=test2!Alias + 1"));

		getSheet("test").sendFormula(ref, null);

		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test2!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(0, refs.size());

		// verify deps were changed
		List<AreaReference> deps = dmgr.getDependencies(ref);
		Assert.assertNotNull(deps);
		Assert.assertEquals(0, deps.size());

	}

	@Test
	public void testInvalidAlias() throws StorageException, NotFoundException {

		CellReference ref = new CellReference("test", 1, 0);
		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test2!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(0, refs.size());

		// verify deps were changed
		List<AreaReference> deps = dmgr.getDependencies(ref);
		Assert.assertNotNull(deps);
		Assert.assertEquals(0, deps.size());

	}

	@Test
	public void testDeleteSheet() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test2").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B2:C3")));

		CellReference ref = new CellReference("test", 1, 0);
		getSheet("test").sendFormula(ref, new Formula("=test2!Alias + 1"));

		getSheet("test2").getWorkbook().deleteSheet("test2");

		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test2!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());

		// verify deps were changed
		List<AreaReference> deps = dmgr.getDependencies(ref);
		Assert.assertNotNull(deps);
		Assert.assertEquals(0, deps.size());

	}

	@Test
	public void testFullColumn() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B:B")));

		CellReference ref = new CellReference("test!A2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(ref, refs.iterator().next().getTopLeft());
		Assert.assertEquals(ref, refs.iterator().next().getBottomRight());

		List<AreaReference> deps = dmgr.getDependencies(ref);
		Assert.assertNotNull(deps);
		Assert.assertEquals(1, deps.size());
		Assert.assertEquals(new AreaReference("test!B2:B2"), deps.get(0));

		List<CellReference> ideps = dmgr.getAllInverseDependencies(new CellReference("test!B2"));
		Assert.assertNotNull(ideps);
		Assert.assertEquals(1, ideps.size());
		Assert.assertEquals(ref, ideps.get(0));
	}

	@Test
	public void testDeleteRowBefore() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B1:B10")));

		CellReference ref = new CellReference("test!A2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(RowCommands.delete(Range.range(0)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new AreaReference("test!A1:A1"), refs.iterator().next());

	}

	@Test
	public void testDeleteRowExact() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B1:B10")));

		CellReference ref = new CellReference("test!A2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(RowCommands.delete(Range.range(1)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(0, refs.size());

	}

	@Test
	public void testDeleteRowAfter() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B1:B10")));

		CellReference ref = new CellReference("test!A2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(RowCommands.delete(Range.range(2)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new AreaReference("test!A2:A2"), refs.iterator().next());

	}

	@Test
	public void testDeleteColumnBefore() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B1:B10")));

		CellReference ref = new CellReference("test!D2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(ColumnCommands.delete(Range.range(0)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new AreaReference("test!C2:C2"), refs.iterator().next());

	}

	@Test
	public void testDeleteColumnExact() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B1:B10")));

		CellReference ref = new CellReference("test!D2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(ColumnCommands.delete(Range.range(3)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(0, refs.size());

	}

	@Test
	public void testDeleteColumnAfter() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B1:B10")));

		CellReference ref = new CellReference("test!D2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(ColumnCommands.delete(Range.range(4)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new AreaReference("test!D2:D2"), refs.iterator().next());

	}

	@Test
	public void testDeleteRowOneCellAlias() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		getSheet("test").sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("B2:B2")));

		CellReference ref = new CellReference("test!A2");
		// amgr.setAliasDependencies(new Formula("=Alias + 1"), new FormulaContextImpl(getSheet("test"), ref));

		getSheet("test").sendFormula(ref, new Formula("=Alias + 1"));

		getSheet("test").sendCommand(RowCommands.delete(Range.range(0)));
		Collection<AreaReference> refs = amgr.getAliasDependants("test", new AbsoluteAlias("test!Alias"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new AreaReference("test!A1:A1"), refs.iterator().next());

		// the formula should keeps its dependencies to alias's cell -> B2 (should not shift)
		List<AreaReference> deps = dmgr.getDependencies(new CellReference("test!A1"));
		Assert.assertNotNull(deps);
		Assert.assertEquals(1, deps.size());
		Assert.assertEquals(new AreaReference("test!B2:B2"), deps.get(0));
	}

}
