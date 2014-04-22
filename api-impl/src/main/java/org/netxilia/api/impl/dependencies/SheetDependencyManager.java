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
package org.netxilia.api.impl.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.impl.utils.BlockMetadata;
import org.netxilia.api.impl.utils.ISparseMatrix;
import org.netxilia.api.impl.utils.ISparseMatrixEntry;
import org.netxilia.api.impl.utils.InsertMode;
import org.netxilia.api.impl.utils.OrderedBlock;
import org.netxilia.api.impl.utils.OrderedBlockMatrix;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.reference.RCAreaReference;
import org.netxilia.api.reference.RCCellReference;
import org.netxilia.api.reference.ReferenceTransformers;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.util.Assert;

/**
 * This class manages the dependencies between cells used in formulas. The direct dependencies are the one from the cell
 * containing the formula to the ones within the formula. This matrix is used only to update the entries of inverse
 * dependencies each time the list of dependencies changed.
 * <p>
 * Ex: A2=B3 + C4 + 1 => A2 has two dependencies to B3 and C4.
 * 
 * <p>
 * The inverse dependencies are the one from the cells used in a formula to the cell containing the formula. In the
 * previous example:
 * <p>
 * B3 to A2 and C4 to A2. This matrix is used to know what cells to update when one cell is updated.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetDependencyManager implements IDependencyManager {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SheetDependencyManager.class);

	private final ISparseMatrix<List<RCAreaReference>> directDependencies = new OrderedBlockMatrix<List<RCAreaReference>>();

	private final ISparseMatrix<List<RCCellReference>> inverseDependencies = new OrderedBlockMatrix<List<RCCellReference>>();
	private final List<List<RCCellReference>> inverseRowDependencies = new ArrayList<List<RCCellReference>>();
	private final List<List<RCCellReference>> inverseColumnDependencies = new ArrayList<List<RCCellReference>>();

	private final ISheet sheet;
	private final WorkbookDependencyManager workbookDependencyManager;

	private final IFormulaParser formulaParser;
	private final IMoreCellCommands moreCellCommands;

	private ITransformFormulaCallback transformFormulaCallback;

	public static SheetDependencyManager newInstance(WorkbookDependencyManager workbookDependencyManager, ISheet sheet,
			IMoreCellCommands moreCellCommands, IFormulaParser formulaParser) {
		Assert.notNull(workbookDependencyManager);
		Assert.notNull(sheet);
		Assert.notNull(moreCellCommands);
		Assert.notNull(formulaParser);

		SheetDependencyManager mgr = new SheetDependencyManager(workbookDependencyManager, sheet, moreCellCommands,
				formulaParser);
		return mgr;
	}

	private SheetDependencyManager(WorkbookDependencyManager workbookDependencyManager, ISheet sheet,
			IMoreCellCommands moreCellCommands, IFormulaParser formulaParser) {
		this.sheet = sheet;
		this.workbookDependencyManager = workbookDependencyManager;
		this.moreCellCommands = moreCellCommands;
		this.formulaParser = formulaParser;
		this.transformFormulaCallback = new DefaultTranformFormulaCallback();
	}

	public ISheet getSheet() {
		return sheet;
	}

	/**
	 * remove reverse dependencies pointing to the given cell
	 * 
	 * @param ref
	 */
	private void removeDependencies(CellReference ref) {
		List<RCAreaReference> directDeps = directDependencies.get(ref.getRowIndex(), ref.getColumnIndex());
		if (directDeps == null) {
			return;
		}
		for (RCAreaReference dirDep : directDeps) {
			SheetDependencyManager mgr;
			try {
				mgr = getManagerForSheet(dirDep.getTopLeft().getSheetName());
			} catch (StorageException e) {
				continue;
			} catch (NotFoundException e) {
				// the formula may have a reference to a wrong sheet - continue anyway
				continue;
			}

			AreaReference absDirDep = dirDep.getAbsoluteReference(ref);

			if (dirDep.isFullColumn()) {
				mgr.removeInverseColumnDependency(absDirDep, ref);
			} else if (dirDep.isFullRow()) {
				mgr.removeInverseRowDependency(absDirDep, ref);
			} else {
				for (int r = absDirDep.getFirstRowIndex(); r <= absDirDep.getLastRowIndex(); ++r) {
					for (int c = absDirDep.getFirstColumnIndex(); c <= absDirDep.getLastColumnIndex(); ++c) {
						mgr.removeInverseDependency(dirDep, r, c, ref);
					}
				}
			}
		}

		directDependencies.set(ref.getRowIndex(), ref.getColumnIndex(), null);
	}

	protected synchronized void removeInverseRowDependency(AreaReference dirDep, CellReference ref) {
		RCCellReference rcRef = new RCCellReference(ref.getSheetName(), ref.getRowIndex(), ref.getColumnIndex(), true,
				true);

		for (int r = dirDep.getTopLeft().getRowIndex(); r <= dirDep.getBottomRight().getRowIndex(); ++r) {
			minumumSize(inverseRowDependencies, r + 1);
			List<RCCellReference> invDeps = inverseRowDependencies.get(r);
			if (invDeps == null) {
				log.error("Inverse dependency from " + dirDep + " to " + ref + " are not found");
				continue;
			}
			inverseRowDependencies.set(r, removeFromList(invDeps, rcRef));
		}

	}

	protected synchronized void removeInverseColumnDependency(AreaReference dirDep, CellReference ref) {
		RCCellReference rcRef = new RCCellReference(ref.getSheetName(), ref.getRowIndex(), ref.getColumnIndex(), true,
				true);
		for (int c = dirDep.getTopLeft().getColumnIndex(); c <= dirDep.getBottomRight().getColumnIndex(); ++c) {
			minumumSize(inverseColumnDependencies, c + 1);
			List<RCCellReference> invDeps = inverseColumnDependencies.get(c);
			if (invDeps == null) {
				log.error("Inverse dependency from " + dirDep + " to " + ref + " are not found");
				continue;
			}
			inverseColumnDependencies.set(c, removeFromList(invDeps, rcRef));
		}
	}

	/**
	 * 
	 * @param dirDep
	 * @param r
	 * @param c
	 * @param ref
	 */
	protected synchronized void removeInverseDependency(RCAreaReference dirDep, int r, int c, CellReference ref) {
		RCCellReference rcRef = getRCReference(dirDep.isOneCell(), ref, r, c);
		List<RCCellReference> invDeps = inverseDependencies.get(r, c);
		if (invDeps == null) {
			log.error("Inverse dependency from " + dirDep + " to " + ref + " are not found");
			return;
		}
		inverseDependencies.set(r, c, removeFromList(invDeps, rcRef));
	}

	private <T> List<T> removeFromList(List<T> list, T elem) {
		if (list == null) {
			return null;
		}
		List<T> result = new ArrayList<T>(list);// the list is not modifiable
		if (!result.remove(elem)) {
			log.error(elem + "is not found in list " + list);
			return list;
		}
		if (result.size() == 0) {
			return null;
		} else {
			return Collections.unmodifiableList(result);
		}

	}

	private <T> List<T> addToList(List<T> list, T elem) {
		List<T> result = null;
		if (list == null) {
			result = new ArrayList<T>(1);
		} else {
			result = new ArrayList<T>(list);
		}
		result.add(elem);
		return Collections.unmodifiableList(result);
	}

	private <T> void minumumSize(List<T> list, int size) {
		while (list.size() < size) {
			list.add(null);
		}
	}

	protected SheetDependencyManager getManagerForSheet(String sheetName) throws StorageException, NotFoundException {
		if (sheetName == null || sheetName.equals(sheet.getName())) {
			return this;
		}
		return workbookDependencyManager.getManagerForSheet(sheetName);

	}

	/**
	 * 
	 * @param dirDep
	 * @param r
	 * @param c
	 * @param ref
	 */
	protected synchronized void addInverseDependency(RCAreaReference dirDep, int r, int c, CellReference ref) {
		RCCellReference rcRef = getRCReference(dirDep.isOneCell(), ref, r, c);
		inverseDependencies.set(r, c, addToList(inverseDependencies.get(r, c), rcRef));
	}

	protected synchronized void addInverseRowDependency(RCAreaReference dirDep, CellReference ref) {
		RCCellReference rcRef = new RCCellReference(ref.getSheetName(), ref.getRowIndex(), ref.getColumnIndex(), true,
				true);
		CellReference absDirDepTopLeft = dirDep.getTopLeft().getAbsoluteReference(ref);
		CellReference absDirDepBottomRight = dirDep.getBottomRight().getAbsoluteReference(ref);
		for (int r = absDirDepTopLeft.getRowIndex(); r <= absDirDepBottomRight.getRowIndex(); ++r) {
			minumumSize(inverseRowDependencies, r + 1);
			inverseRowDependencies.set(r, addToList(inverseRowDependencies.get(r), rcRef));
		}

	}

	protected synchronized void addInverseColumnDependency(RCAreaReference dirDep, CellReference ref) {
		RCCellReference rcRef = new RCCellReference(ref.getSheetName(), ref.getRowIndex(), ref.getColumnIndex(), true,
				true);
		CellReference absDirDepTopLeft = dirDep.getTopLeft().getAbsoluteReference(ref);
		CellReference absDirDepBottomRight = dirDep.getBottomRight().getAbsoluteReference(ref);
		for (int c = absDirDepTopLeft.getColumnIndex(); c <= absDirDepBottomRight.getColumnIndex(); ++c) {
			minumumSize(inverseColumnDependencies, c + 1);
			inverseColumnDependencies.set(c, addToList(inverseColumnDependencies.get(c), rcRef));
		}
	}

	/**
	 * This method tries to minimize the number of block in the inverse dependencies matrix.
	 * <p>
	 * For example for a formula like: A2 = B2 + 1, the inverse dependency will use the relative RC value R[0]C[-1].
	 * <p>
	 * For a formula like B11 = sum(B1:B10) is better to use the absolute RC value R10C1
	 * 
	 * @param one2one
	 * @param ref
	 * @param contextRow
	 * @param contextColumn
	 * @return
	 */
	private RCCellReference getRCReference(boolean one2one, CellReference ref, int contextRow, int contextColumn) {
		if (one2one) {
			return new RCCellReference(ref.getSheetName(), ref.getRowIndex() - contextRow, ref.getColumnIndex()
					- contextColumn, false, false);
		}
		return new RCCellReference(ref.getSheetName(), ref.getRowIndex(), ref.getColumnIndex(), true, true);
	}

	/**
	 * 
	 * @param ref
	 * @param deps
	 */
	private void addDependencies(CellReference ref, List<RCAreaReference> deps) {
		if (deps == null || deps.size() == 0) {
			return;
		}
		directDependencies.set(ref.getRowIndex(), ref.getColumnIndex(), Collections.unmodifiableList(deps));

		// add it to the inverse dependencies
		for (RCAreaReference dirDep : deps) {
			SheetDependencyManager mgr;
			try {
				mgr = getManagerForSheet(dirDep.getTopLeft().getSheetName());
			} catch (StorageException e) {
				continue;
			} catch (NotFoundException e) {
				// the formula may have a reference to a wrong sheet - continue anyway
				continue;
			}

			if (dirDep.isFullColumn()) {
				mgr.addInverseColumnDependency(dirDep, ref);
			} else if (dirDep.isFullRow()) {
				mgr.addInverseRowDependency(dirDep, ref);
			} else {
				CellReference absDirDepTopLeft = dirDep.getTopLeft().getAbsoluteReference(ref);
				CellReference absDirDepBottomRight = dirDep.getBottomRight().getAbsoluteReference(ref);
				for (int r = absDirDepTopLeft.getRowIndex(); r <= absDirDepBottomRight.getRowIndex(); ++r) {
					for (int c = absDirDepTopLeft.getColumnIndex(); c <= absDirDepBottomRight.getColumnIndex(); ++c) {
						mgr.addInverseDependency(dirDep, r, c, ref);
					}
				}
			}
		}
	}

	@Override
	public synchronized List<AreaReference> getDependencies(CellReference ref) {
		List<RCAreaReference> deps = directDependencies.get(ref.getRowIndex(), ref.getColumnIndex());
		if (deps == null) {
			return Collections.emptyList();
		}
		List<AreaReference> absDeps = new ArrayList<AreaReference>(deps.size());
		for (RCAreaReference dep : deps) {
			absDeps.add(dep.getAbsoluteReference(ref));
		}
		return absDeps;
	}

	/**
	 * sets the new dependencies between the cell with the given formula and the other cells managed by this class. If
	 * the formula is null, it will remove any dependency.
	 * 
	 * @param ref
	 * @param formula
	 * @throws CyclicDependenciesException
	 */
	public synchronized void setDependencies(Formula formula, IFormulaContext context)
			throws CyclicDependenciesException {
		Assert.notNull(context);

		CellReference ref = context.getCell();
		List<RCAreaReference> deps = getDependencies(formula, context);
		List<RCAreaReference> prevDeps = directDependencies.get(ref.getRowIndex(), ref.getColumnIndex());
		// change dependencies table if the dependency list changed
		if (!ObjectUtils.equals(deps, prevDeps)) {
			removeDependencies(ref);
			addDependencies(ref, deps);

			// test cycles
			try {
				visit(new HashSet<CellReference>(), new HashSet<CellReference>(), null, ref);
			} catch (CyclicDependenciesException ex) {
				// put references back
				removeDependencies(ref);
				addDependencies(ref, prevDeps);
				throw ex;
			}
		}

	}

	@Override
	public synchronized List<CellReference> getAllInverseDependencies(CellReference ref) {
		List<RCCellReference> invDeps = getOwnInverseDependencies(ref.getRowIndex(), ref.getColumnIndex());

		if (invDeps == null || invDeps.size() == 0) {
			return Collections.emptyList();
		}

		List<CellReference> refreshCells = new ArrayList<CellReference>();
		Set<CellReference> visited = new HashSet<CellReference>();

		try {
			visit(visited, new HashSet<CellReference>(), refreshCells, ref);
		} catch (Exception ex) {
			log.error("Exception " + ex, ex);
		}
		// refreshCells is reversed. skip the last one which is the cell that triggered the calculations
		List<CellReference> allInvDeps = new ArrayList<CellReference>(refreshCells.size() - 1);
		for (int i = refreshCells.size() - 2; i >= 0; --i) {
			allInvDeps.add(refreshCells.get(i));
		}
		return allInvDeps;
	}

	protected synchronized List<RCCellReference> getOwnInverseDependencies(int row, int column) {
		List<RCCellReference> invDeps = Collections.emptyList();
		invDeps = addToList(invDeps, inverseDependencies.get(row, column));
		invDeps = addToList(invDeps, row < inverseRowDependencies.size() ? inverseRowDependencies.get(row) : null);
		invDeps = addToList(invDeps, column < inverseColumnDependencies.size() ? inverseColumnDependencies.get(column)
				: null);
		return invDeps;
	}

	private <T> List<T> addToList(List<T> list, List<T> add) {
		List<T> result = list;
		if (add == null || add.size() == 0) {
			return result;
		}
		if (result == null || result == Collections.emptyList()) {
			result = new ArrayList<T>();
		}
		result.addAll(add);
		return result;
	}

	private List<RCCellReference> getInverseDependencies(CellReference ref) throws StorageException, NotFoundException {
		if (sheet.getName().equals(ref.getSheetName())) {
			return getOwnInverseDependencies(ref.getRowIndex(), ref.getColumnIndex());
		}
		return getManagerForSheet(ref.getSheetName())
				.getOwnInverseDependencies(ref.getRowIndex(), ref.getColumnIndex());
	}

	/**
	 * use topological sort to find the order of processing cells
	 */
	private void visit(Set<CellReference> visited, Set<CellReference> path, List<CellReference> refreshCells,
			CellReference ref) throws CyclicDependenciesException {
		if (path.contains(ref)) {
			throw new CyclicDependenciesException("Cycles detected!");
		}

		if (!visited.contains(ref)) {
			visited.add(ref);
			path.add(ref);

			List<RCCellReference> invDeps = null;
			try {
				invDeps = getInverseDependencies(ref);
			} catch (StorageException e) {
				log.error("Cannot find sheet for cell:" + ref + ":" + e, e);
			} catch (NotFoundException e) {
				log.error("Cannot find sheet for cell:" + ref + ":" + e, e);
			}

			if (invDeps != null) {
				for (RCCellReference invDep : invDeps) {
					CellReference absInvDep = invDep.getAbsoluteReference(ref);
					visit(visited, path, refreshCells, absInvDep);
					path.remove(absInvDep);
				}
			}
			if (refreshCells != null) {
				refreshCells.add(ref);

			}

		}

	}

	private List<RCAreaReference> getDependencies(Formula formula, IFormulaContext context) {
		if (formula == null || formula.isEmpty()) {
			return Collections.emptyList();
		}
		try {
			List<AreaReference> areaRefs = formulaParser.getDependencies(formula, context);
			List<RCAreaReference> refs = new ArrayList<RCAreaReference>();
			for (AreaReference areaRef : areaRefs) {
				refs.add(new RCAreaReference(context.getCell(), areaRef));
			}
			return refs;
		} catch (FormulaParsingException e) {
			// should not be the case as the formula as already checked previously
			return Collections.emptyList();
		}
	}

	private Set<AreaReference> matrixEntryToAreaReference(
			Collection<? extends ISparseMatrixEntry<List<RCCellReference>>> entries,
			IReferenceTransformer directTransformer, IReferenceTransformer inverseTransformer) {
		Set<AreaReference> areas = new HashSet<AreaReference>();
		for (ISparseMatrixEntry<List<RCCellReference>> entry : entries) {
			for (int r = entry.getFirstRow(); r <= entry.getLastRow(); ++r) {
				for (int c = entry.getFirstColumn(); c <= entry.getLastColumn(); ++c) {
					List<RCCellReference> newDeps = new ArrayList<RCCellReference>(entry.getValue().size());

					CellReference ref = new CellReference(sheet.getName(), r, c);
					CellReference prevRef = inverseTransformer.transform(ref);
					if (prevRef == null) {
						continue;
					}
					// all the inverse dependencies are relative to the reference
					// but for area references (ie sum(A1:A3) ) see {@link #getRCReference} the reference can be RC
					// absolute
					// to get the current cell should apply the transformer both to ref and corresponding area (if from
					// the same sheet)
					for (RCCellReference rcInvDev : entry.getValue()) {
						AreaReference area = new AreaReference(rcInvDev.getAbsoluteReference(prevRef));
						newDeps.add(getRCReference(true, rcInvDev.getAbsoluteReference(prevRef), r, c));

						if (sheet.getName().equals(area.getSheetName())) {
							area = directTransformer.transform(area);
						}
						if (area != null) {
							areas.add(area);
						} else {
							// TODO remove orphan dependencies
							// removeInverseDependency(new RCAreaReference(rcInvDev, rcInvDev), r, c, ref);
						}
					}
					inverseDependencies.set(r, c, newDeps);
				}
			}
		}
		return areas;
	}

	/**
	 * for test only
	 * 
	 * @return
	 */
	public synchronized int getDirectDependenciesBlockCount() {
		return directDependencies.getBlockCount();
	}

	/**
	 * for test only
	 * 
	 * @return
	 */
	public synchronized int getInverseDependenciesBlockCount() {
		return inverseDependencies.getBlockCount();
	}

	public void propagateCellValue(CellData cell, Collection<CellData.Property> properties) {

		if (properties == null) {
			return;
		}

		if (properties.contains(CellData.Property.value)) {
			List<CellReference> refreshCells = getAllInverseDependencies(cell.getReference());
			if (!refreshCells.isEmpty()) {
				sheet.sendCommandNoUndo(moreCellCommands.refresh(refreshCells, true));
			}
		}

	}

	/**
	 * the list of area references were relative to the cells before the change. Re-write them to be relative to the new
	 * position
	 * 
	 * @param dirEntries
	 * @param referenceTransformer
	 */
	private void applyTransformer(List<? extends ISparseMatrixEntry<List<RCAreaReference>>> dirEntries,
			IReferenceTransformer referenceTransformer) {
		List<CellReference> removeDeps = new ArrayList<CellReference>();
		Map<CellReference, List<RCAreaReference>> addDeps = new HashMap<CellReference, List<RCAreaReference>>();

		for (ISparseMatrixEntry<List<RCAreaReference>> entry : dirEntries) {
			for (int r = entry.getFirstRow(); r <= entry.getLastRow(); ++r) {
				for (int c = entry.getFirstColumn(); c <= entry.getLastColumn(); ++c) {
					List<RCAreaReference> newDeps = new ArrayList<RCAreaReference>(entry.getValue().size());
					CellReference ref = new CellReference(r, c);
					CellReference prevRef = referenceTransformer.transform(ref);
					for (RCAreaReference dep : entry.getValue()) {
						AreaReference absRef = dep.getAbsoluteReference(prevRef);
						newDeps.add(new RCAreaReference(ref, absRef));
					}
					removeDeps.add(prevRef);
					addDeps.put(ref, newDeps);
				}
			}
		}

		// remove dependencies from their previous position
		for (CellReference ref : removeDeps) {
			removeDependencies(ref);
		}

		// put the in the new position
		for (Map.Entry<CellReference, List<RCAreaReference>> entry : addDeps.entrySet()) {
			addDependencies(entry.getKey(), entry.getValue());
		}
	}

	public synchronized Set<AreaReference> insertColumn(int column) {
		IReferenceTransformer referenceTransformer = ReferenceTransformers.insertColumn(column);
		IReferenceTransformer inverseReferenceTransformer = ReferenceTransformers.deleteColumn(column);

		List<? extends ISparseMatrixEntry<List<RCAreaReference>>> dirEntries = directDependencies.insertColumn(column,
				InsertMode.split);

		// TODO - check inversColumnDependencies
		Collection<? extends ISparseMatrixEntry<List<RCCellReference>>> affectedEntries = inverseDependencies
				.insertColumn(column, InsertMode.grow);

		applyTransformer(dirEntries, inverseReferenceTransformer);

		Set<AreaReference> areas = matrixEntryToAreaReference(affectedEntries, referenceTransformer,
				inverseReferenceTransformer);
		transformFormulas(areas, referenceTransformer);
		return areas;
	}

	public synchronized Set<AreaReference> deleteColumn(int column) {
		IReferenceTransformer referenceTransformer = ReferenceTransformers.deleteColumn(column);
		IReferenceTransformer inverseReferenceTransformer = ReferenceTransformers.insertColumn(column);

		List<? extends ISparseMatrixEntry<List<RCAreaReference>>> dirEntries = directDependencies.deleteColumn(column);

		// TODO - check inversColumnDependencies
		// TODO when delete a column the formula referencing the column should be changed to loose the reference
		// WARN the affected entries does not contain the information in the completely deleted cells
		Collection<? extends ISparseMatrixEntry<List<RCCellReference>>> affectedEntries = inverseDependencies
				.deleteColumn(column);

		applyTransformer(dirEntries, inverseReferenceTransformer);
		Set<AreaReference> areas = matrixEntryToAreaReference(affectedEntries, referenceTransformer,
				inverseReferenceTransformer);
		transformFormulas(areas, referenceTransformer);
		return areas;
	}

	public synchronized Set<AreaReference> deleteRow(int rowIndex) {
		IReferenceTransformer referenceTransformer = ReferenceTransformers.deleteRow(rowIndex);
		IReferenceTransformer inverseReferenceTransformer = ReferenceTransformers.insertRow(rowIndex);

		// TODO - check inversRowDependencies
		// TODO when delete a row the formula referencing the row should be changed to loose the reference
		List<? extends ISparseMatrixEntry<List<RCAreaReference>>> dirEntries = directDependencies.deleteRow(rowIndex);
		List<? extends ISparseMatrixEntry<List<RCCellReference>>> entries = inverseDependencies.deleteRow(rowIndex);

		applyTransformer(dirEntries, inverseReferenceTransformer);
		Set<AreaReference> areas = matrixEntryToAreaReference(entries, referenceTransformer,
				inverseReferenceTransformer);
		transformFormulas(areas, referenceTransformer);
		return areas;
	}

	public synchronized Set<AreaReference> insertRow(int rowIndex) {
		// the dependencies should not be affected when rows are added - not inserted
		IReferenceTransformer referenceTransformer = ReferenceTransformers.insertRow(rowIndex);
		IReferenceTransformer inverseReferenceTransformer = ReferenceTransformers.deleteRow(rowIndex);

		// TODO - check inversRowDependencies
		List<? extends ISparseMatrixEntry<List<RCAreaReference>>> dirEntries = directDependencies.insertRow(rowIndex,
				InsertMode.split);
		List<? extends ISparseMatrixEntry<List<RCCellReference>>> invEntries = inverseDependencies.insertRow(rowIndex,
				InsertMode.grow);

		applyTransformer(dirEntries, inverseReferenceTransformer);
		Set<AreaReference> areas = matrixEntryToAreaReference(invEntries, referenceTransformer,
				inverseReferenceTransformer);
		transformFormulas(areas, referenceTransformer);
		return areas;
	}

	protected void transformFormulas(Set<AreaReference> affectedAreas, IReferenceTransformer referenceTransformer) {
		transformFormulaCallback.transformFormulas(affectedAreas, referenceTransformer);
	}

	/**
	 * 
	 * @return the list of area references that are affected when the sheet corresponding to this manager is deleted. It
	 *         will be returned only references from other sheets from the same workbook.
	 */
	public synchronized Set<AreaReference> deleteSheet() {
		// TODO - check inversRowDependencies
		// TODO - check inversColumnDependencies
		return matrixEntryToAreaReference(inverseDependencies.entries(),
				ReferenceTransformers.excludeSheet(sheet.getName()), ReferenceTransformers.unchanged());
	}

	public void print() {
		System.out.println("------------ DIRECT ------------- ");
		System.out.println(directDependencies);
		System.out.println("------------ INVERSE -------------: " + inverseDependencies.getBlockCount());
		for (ISparseMatrixEntry<List<RCCellReference>> entry : inverseDependencies.entries()) {
			System.out.println(((BlockMetadata<?>) entry).getBlock() + "=" + entry.toString());
		}
		System.out.println("--");
		int i = 0;
		for (ISparseMatrixEntry<List<RCCellReference>> entry : inverseDependencies.entries()) {
			OrderedBlock block = ((BlockMetadata<?>) entry).getBlock();
			System.out.println("add(" + block.getFirstRow() + "," + block.getFirstCol() + "," + block.getLastRow()
					+ "," + block.getLastCol() + "," + (i++) + ");");
		}

	}

	public ITransformFormulaCallback getTransformFormulaCallback() {
		return transformFormulaCallback;
	}

	public void setTransformFormulaCallback(ITransformFormulaCallback transformFormulaCallback) {
		this.transformFormulaCallback = transformFormulaCallback;
	}

	public static interface ITransformFormulaCallback {
		public void transformFormulas(Set<AreaReference> affectedAreas, IReferenceTransformer referenceTransformer);
	}

	public class DefaultTranformFormulaCallback implements ITransformFormulaCallback {
		public void transformFormulas(Set<AreaReference> affectedAreas, IReferenceTransformer referenceTransformer) {
			// transform the formula in the affected cells
			for (AreaReference area : affectedAreas) {
				try {
					ISheet affectedSheet = area.getSheetName() == null ? sheet : sheet.getWorkbook().getSheet(
							area.getSheetName());
					affectedSheet.sendCommand(moreCellCommands.formulaTransformer(area, referenceTransformer));
				} catch (Exception e) {
					log.error("Could not apply modification on the row :" + e, e);
				}
			}
		}
	}

}
