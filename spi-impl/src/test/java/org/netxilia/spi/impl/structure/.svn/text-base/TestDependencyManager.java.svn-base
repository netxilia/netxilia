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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impl.dependencies.SheetDependencyManager;
import org.netxilia.api.impl.dependencies.WorkbookDependencyManager;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.impl.formula.FormulaContextImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDependencyManager {
	private ApplicationContext context;
	private Map<String, ISheet> sheets = new HashMap<String, ISheet>();
	private WorkbookDependencyManager mgr;
	private IFormulaParser parser;

	@Before
	public void startup() throws AlreadyExistsException, StorageException, NotFoundException {
		context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		parser = context.getBean(IFormulaParser.class);
		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell();
		sheets.put(sheet.getName(), sheet);
		ISheet sheet2 = sheet.getWorkbook().addNewSheet("test2", SheetType.normal);
		sheets.put(sheet2.getName(), sheet2);
		mgr = WorkbookDependencyManager.newInstance(sheet.getWorkbook(), context.getBean(IMoreCellCommands.class),
				context.getBean(IFormulaParser.class));

	}

	private WorkbookDependencyManager dependencyManager() {
		return mgr;
	}

	private ISheet getSheet(String sheetName) {
		return sheets.get(sheetName);
	}

	@Test
	public void testSimpleGetSet() throws StorageException, NotFoundException {
		// A2 = B2 + 1

		CellReference ref = new CellReference("test", 1, 0);
		try {
			setDependencies(mgr, getSheet("test"), ref, new Formula("=B2 + 1"));
		} catch (CyclicDependenciesException e) {
			e.printStackTrace();
			Assert.fail();
		}
		List<AreaReference> refs = mgr.getDependencies(ref);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getBottomRight());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());
	}

	@Test
	public void testCompactSet() throws StorageException, NotFoundException {
		// A2 = B2 + 1
		// A3 = B3 + 1
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test", 1, 0); // A2
		CellReference ref2 = new CellReference("test", 2, 0); // A3
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B2 + 1"));
			setDependencies(mgr, getSheet("test"), ref2, new Formula("=B3 + 1"));
		} catch (CyclicDependenciesException e) {
			e.printStackTrace();
			Assert.fail();
		}
		// ref1
		List<AreaReference> refs = mgr.getDependencies(ref1);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getBottomRight());

		// ref2
		refs = mgr.getDependencies(ref1);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getBottomRight());

		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());
	}

	@Test
	public void testMultDepsSet() throws StorageException, NotFoundException {
		// b11 = sum(b1:b10)

		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref = new CellReference("test", 10, 1);// b11
		try {
			setDependencies(mgr, getSheet("test"), ref, new Formula("=sum(b1:b10)"));
		} catch (CyclicDependenciesException e) {
			e.printStackTrace();
			Assert.fail();
		}
		List<AreaReference> refs = mgr.getDependencies(ref);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 0, 1), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 9, 1), refs.get(0).getBottomRight());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());
	}

	@Test
	public void testReplace() throws StorageException, NotFoundException {
		// A2 = B2 + 1
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref = new CellReference("test", 1, 0);
		try {
			setDependencies(mgr, getSheet("test"), ref, new Formula("=B2 + 1"));
		} catch (CyclicDependenciesException e) {
			e.printStackTrace();
			Assert.fail();
		}
		List<AreaReference> refs = mgr.getDependencies(ref);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getBottomRight());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());

		// replace it
		try {
			setDependencies(mgr, getSheet("test"), ref, new Formula("=C3 + 1"));
		} catch (CyclicDependenciesException e) {
			e.printStackTrace();
			Assert.fail();
		}
		refs = mgr.getDependencies(ref);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 2, 2), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 2, 2), refs.get(0).getBottomRight());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());

	}

	@Test
	public void testCycleDetection() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test", 1, 0);// A2
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B2 + 1"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}
		CellReference ref2 = new CellReference("test", 1, 1);// B2
		try {
			setDependencies(mgr, getSheet("test"), ref2, new Formula("=C2 + 1"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}
		CellReference ref3 = new CellReference("test", 1, 2);// C2
		try {
			setDependencies(mgr, getSheet("test"), ref3, new Formula("=A2 + 1"));
			Assert.fail("Cycle not detected");
		} catch (CyclicDependenciesException e) {
			// OK
		}
	}

	@Test
	public void testInvDependencies() throws StorageException, NotFoundException {
		// A2 = B2 + 1
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!A2");// A2
		CellReference ref2 = new CellReference("test!B11");// B11
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B11 + 1"));
			setDependencies(mgr, getSheet("test"), ref2, new Formula("=sum(b1:b10)"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		List<CellReference> refs = mgr.getAllInverseDependencies(new CellReference("test!B5"));
		Assert.assertNotNull(refs);
		Assert.assertEquals(2, refs.size());
		Assert.assertEquals(ref2, refs.get(0));
		Assert.assertEquals(ref1, refs.get(1));
	}

	@Test
	public void testMultiSheet() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();
		CellReference ref = new CellReference("test!A2");// A2
		try {
			setDependencies(mgr, getSheet("test"), ref, new Formula("=B2 + test2!B2 + test2!C3"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		List<AreaReference> refs = mgr.getDependencies(ref);

		Assert.assertNotNull(refs);
		Assert.assertEquals(3, refs.size());
		Assert.assertEquals(new CellReference("test!B2"), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test!B2"), refs.get(0).getBottomRight());
		Assert.assertEquals(new CellReference("test2!B2"), refs.get(1).getTopLeft());
		Assert.assertEquals(new CellReference("test2!B2"), refs.get(1).getBottomRight());
		Assert.assertEquals(new CellReference("test2!C3"), refs.get(2).getTopLeft());
		Assert.assertEquals(new CellReference("test2!C3"), refs.get(2).getBottomRight());

		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());
		Assert.assertEquals(0, mgr.getManagerForSheet("test2").getDirectDependenciesBlockCount());
		Assert.assertEquals(2, mgr.getManagerForSheet("test2").getInverseDependenciesBlockCount());

		List<CellReference> invRefs = mgr.getAllInverseDependencies(new CellReference("test!B2"));
		Assert.assertNotNull(invRefs);
		Assert.assertEquals(1, invRefs.size());
		Assert.assertEquals(new CellReference("test!A2"), invRefs.get(0));

		invRefs = mgr.getAllInverseDependencies(new CellReference("test2!B2"));
		Assert.assertNotNull(invRefs);
		Assert.assertEquals(1, invRefs.size());
		Assert.assertEquals(new CellReference("test!A2"), invRefs.get(0));

		invRefs = mgr.getAllInverseDependencies(new CellReference("test2!C3"));
		Assert.assertNotNull(invRefs);
		Assert.assertEquals(1, invRefs.size());
		Assert.assertEquals(new CellReference("test!A2"), invRefs.get(0));
	}

	@Test
	public void testMultiSheetCycle() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!A2");// A2
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=test2!B2 + 1"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}
		CellReference ref2 = new CellReference("test2!B2");// B2
		try {
			setDependencies(mgr, getSheet("test"), ref2, new Formula("=test!C2 + 1"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}
		CellReference ref3 = new CellReference("test!C2");// C2
		try {
			setDependencies(mgr, getSheet("test"), ref3, new Formula("=A2 + 1"));
			Assert.fail("Cycle not detected");
		} catch (CyclicDependenciesException e) {
			// OK
		}
	}

	@Test
	public void testDeleteRow() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B4");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=sum(A1:A3)"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		Set<AreaReference> affectedCells = mgr.getManagerForSheet("test").deleteRow(1);
		Assert.assertEquals(1, affectedCells.size());
		Assert.assertEquals(new AreaReference("test!$B$3:$B$3"), affectedCells.iterator().next());
	}

	@Test
	public void testInsertRowBefore() throws StorageException, NotFoundException {
		final WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B3");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B1 + 10"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		DirectFormulaTransformer transformer = new DirectFormulaTransformer(mgr.getManagerForSheet("test"));
		transformer.addFormula(new AreaReference("test!B4:B4"), new Formula("=B1 + 10"));
		mgr.getManagerForSheet("test").setTransformFormulaCallback(transformer);

		Set<AreaReference> affectedCells = mgr.getManagerForSheet("test").insertRow(0);
		Assert.assertEquals(1, affectedCells.size());

		List<AreaReference> oldDeps = mgr.getManagerForSheet("test").getDependencies(ref1);
		Assert.assertNotNull(oldDeps);
		Assert.assertEquals(0, oldDeps.size());

		// the cells ref changed, but it still depends on the original cell
		CellReference ref1Moved = new CellReference("test!B4");
		List<AreaReference> newDeps = mgr.getManagerForSheet("test").getDependencies(ref1Moved);

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new AreaReference("test!$B$2:$B$2"), newDeps.iterator().next());

		// check inverse dependencies
		List<CellReference> invDeps = mgr.getManagerForSheet("test").getAllInverseDependencies(
				new CellReference("test!B2"));

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new CellReference("test!B4"), invDeps.iterator().next());
	}

	@Test
	public void testInsertRowBetween1() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B3");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B1 + 10"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		Set<AreaReference> affectedCells = mgr.getManagerForSheet("test").insertRow(1);
		Assert.assertEquals(0, affectedCells.size());

		List<AreaReference> oldDeps = mgr.getManagerForSheet("test").getDependencies(ref1);
		Assert.assertNotNull(oldDeps);
		Assert.assertEquals(0, oldDeps.size());

		// the cells ref changed, but it still depends on the original cell
		CellReference ref1Moved = new CellReference("test!B4");
		List<AreaReference> newDeps = mgr.getManagerForSheet("test").getDependencies(ref1Moved);

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new AreaReference("test!$B$1:$B$1"), newDeps.iterator().next());

		// check inverse dependencies
		List<CellReference> invDeps = mgr.getManagerForSheet("test").getAllInverseDependencies(
				new CellReference("test!B1"));

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new CellReference("test!B4"), invDeps.iterator().next());
	}

	@Test
	public void testInsertRowBetween2() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B3");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B5 + 10"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		DirectFormulaTransformer transformer = new DirectFormulaTransformer(mgr.getManagerForSheet("test"));
		transformer.addFormula(new AreaReference("test!B3:B3"), new Formula("=B5 + 10"));
		mgr.getManagerForSheet("test").setTransformFormulaCallback(transformer);

		Set<AreaReference> affectedCells = mgr.getManagerForSheet("test").insertRow(3);
		Assert.assertEquals(1, affectedCells.size());

		// the cells ref changed, but it still depends on the original cell
		List<AreaReference> newDeps = mgr.getManagerForSheet("test").getDependencies(ref1);

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new AreaReference("test!$B$6:$B$6"), newDeps.iterator().next());

		// check inverse dependencies
		List<CellReference> invDeps = mgr.getManagerForSheet("test").getAllInverseDependencies(
				new CellReference("test!B6"));

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new CellReference("test!B3"), invDeps.iterator().next());
	}

	@Test
	public void testInsertRowAfter() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B3");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=B1 + 10"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		Set<AreaReference> affectedCells = mgr.getManagerForSheet("test").insertRow(4);
		Assert.assertEquals(0, affectedCells.size());

		List<AreaReference> newDeps = mgr.getManagerForSheet("test").getDependencies(ref1);

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new AreaReference("test!$B$1:$B$1"), newDeps.iterator().next());

		// check inverse dependencies
		List<CellReference> invDeps = mgr.getManagerForSheet("test").getAllInverseDependencies(
				new CellReference("test!B1"));

		Assert.assertEquals(1, newDeps.size());
		Assert.assertEquals(new CellReference("test!B3"), invDeps.iterator().next());
	}

	@Test
	public void testSetNull() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!A1");
		CellReference ref2 = new CellReference("test!A2");
		CellReference ref3 = new CellReference("test!A3");
		try {
			setDependencies(mgr, getSheet("test"), ref2, new Formula("=A1 + 1"));
			setDependencies(mgr, getSheet("test"), ref3, new Formula("=A2 + 1"));
			setDependencies(mgr, getSheet("test"), ref2, null);
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		List<CellReference> deps = mgr.getAllInverseDependencies(ref1);
		Assert.assertEquals(0, deps.size());

		deps = mgr.getAllInverseDependencies(ref2);
		Assert.assertEquals(1, deps.size());
		Assert.assertEquals(new CellReference("test!A3"), deps.iterator().next());
	}

	@Test
	public void testFullColumn() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B1");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=sum(A:A)"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		List<CellReference> deps = mgr.getAllInverseDependencies(new CellReference("test!A2"));
		Assert.assertEquals(1, deps.size());
		Assert.assertEquals(new CellReference("test!B1"), deps.iterator().next());
	}

	@Test
	public void testOrder() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		try {
			setDependencies(mgr, getSheet("test"), new CellReference("test!C1"), new Formula("=sum(b1:b3)"));
			setDependencies(mgr, getSheet("test"), new CellReference("test!B2"), new Formula("=A1+B1"));
			setDependencies(mgr, getSheet("test"), new CellReference("test!B3"), new Formula("=A1+B2"));
			setDependencies(mgr, getSheet("test"), new CellReference("test!B1"), new Formula("=A1"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		List<CellReference> deps = mgr.getAllInverseDependencies(new CellReference("test!A1"));
		Assert.assertEquals(4, deps.size());
		Assert.assertEquals(new CellReference("test!B1"), deps.get(0));
		Assert.assertEquals(new CellReference("test!B2"), deps.get(1));
		Assert.assertEquals(new CellReference("test!B3"), deps.get(2));
		Assert.assertEquals(new CellReference("test!C1"), deps.get(3));
	}

	@Test
	public void testDeleteColumnMultisheet() throws StorageException, NotFoundException {
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref1 = new CellReference("test!B4");
		try {
			setDependencies(mgr, getSheet("test"), ref1, new Formula("=sum(A1:A3)"));
		} catch (CyclicDependenciesException e) {
			Assert.fail(e.getMessage());
		}

		Set<AreaReference> affectedCells = mgr.getManagerForSheet("test").deleteRow(1);
		Assert.assertEquals(1, affectedCells.size());
		Assert.assertEquals(new AreaReference("test!$B$3:$B$3"), affectedCells.iterator().next());
	}

	@Test
	public void testFormulaWithAlias() throws StorageException, NotFoundException {
		// A2 = B2 + 1
		WorkbookDependencyManager mgr = dependencyManager();

		CellReference ref = new CellReference("test", 1, 0);
		try {
			ISheet sheet = getSheet("test");
			sheet.sendCommand(SheetCommands.setAlias(new Alias("alias"), new AreaReference("B2:B2")));
			setDependencies(mgr, sheet, ref, new Formula("=alias + 1"));
		} catch (CyclicDependenciesException e) {
			e.printStackTrace();
			Assert.fail();
		}
		List<AreaReference> refs = mgr.getDependencies(ref);
		Assert.assertNotNull(refs);
		Assert.assertEquals(1, refs.size());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getTopLeft());
		Assert.assertEquals(new CellReference("test", 1, 1), refs.get(0).getBottomRight());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getDirectDependenciesBlockCount());
		Assert.assertEquals(1, mgr.getManagerForSheet("test").getInverseDependenciesBlockCount());
	}

	private class DirectFormulaTransformer implements SheetDependencyManager.ITransformFormulaCallback {
		private final SheetDependencyManager sheetManager;
		private final Map<AreaReference, Formula> formulas = new HashMap<AreaReference, Formula>();

		public DirectFormulaTransformer(SheetDependencyManager sheetManager) {
			this.sheetManager = sheetManager;
		}

		public void addFormula(AreaReference areaReference, Formula formula) {
			formulas.put(areaReference, formula);
		}

		@Override
		public void transformFormulas(Set<AreaReference> affectedAreas, IReferenceTransformer referenceTransformer) {
			try {

				for (AreaReference area : affectedAreas) {
					Formula formula = formulas.get(area);
					if (formula == null) {
						throw new IllegalStateException("Formula was not set for ref:" + area);
					}
					sheetManager.setDependencies(parser.transformFormula(formula, referenceTransformer),
							new FormulaContextImpl(sheetManager.getSheet(), area.getTopLeft()));
				}
			} catch (StorageException e) {
				e.printStackTrace();
			} catch (CyclicDependenciesException e) {
				e.printStackTrace();
			} catch (FormulaParsingException e) {
				e.printStackTrace();
			}
		}
	}

	private void setDependencies(WorkbookDependencyManager mgr, ISheet sheet, CellReference ref, Formula formula)
			throws StorageException, CyclicDependenciesException, NotFoundException {
		mgr.setDependencies(formula, new FormulaContextImpl(sheet, ref));
	}
}
