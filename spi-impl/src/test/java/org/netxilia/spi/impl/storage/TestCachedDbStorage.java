package org.netxilia.spi.impl.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.chart.Chart;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.storage.DataSourceConfigurationServiceImpl;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.GenericValueUtils;
import org.netxilia.spi.impl.storage.cache.CachedDbWorkbookStorageServiceImpl;
import org.netxilia.spi.storage.ISheetStorageService;
import org.netxilia.spi.storage.IWorkbookStorageService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCachedDbStorage {
	private DataSourceConfigurationServiceImpl dsService;
	private IWorkbookStorageService storage;

	// test add/remove rows
	// test add/remove columns

	@Before
	public void setup() throws IOException {
		AclPrivilegedMode.set();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		dsService = context.getBean(DataSourceConfigurationServiceImpl.class);
		String path = new File(System.getProperty("java.io.tmpdir"), "nx-test").getAbsolutePath();
		dsService.setPath(path);

		storage = context.getBean(CachedDbWorkbookStorageServiceImpl.class);
	}

	@After
	public void tearDown() {
		try {
			dsService.close();
			FileUtils.deleteDirectory(new File(dsService.getPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		AclPrivilegedMode.clear();
	}

	@Test
	public void testWorkbook() throws StorageException, NotFoundException, AlreadyExistsException {
		IWorkbook wk = storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		Assert.assertNotNull(wk);

		List<SheetData> sheets = storage.loadSheets(new WorkbookId("wktest"));
		Assert.assertNotNull(sheets);

		storage.deleteWorkbook(new WorkbookId("wktest"));
		try {
			sheets = storage.loadSheets(new WorkbookId("wktest"));
			Assert.fail("NotFoundException expected");
		} catch (NotFoundException ex) {
			//
		}
	}

	@Test
	public void testSheetNormal() throws StorageException, NotFoundException, AlreadyExistsException {
		// try first without existent workbook
		ISheetStorageService sheetStorage = null;

		try {
			sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"), SheetType.normal);
			Assert.fail("NotFoundException expected");
		} catch (NotFoundException ex) {
			//
		}

		// create workbook
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"), SheetType.normal);
		Assert.assertNotNull(sheetStorage);

		List<SheetData> sheets = storage.loadSheets(new WorkbookId("wktest"));
		Assert.assertNotNull(sheets);
		Assert.assertEquals(1, sheets.size());
		Assert.assertEquals("sheet1", sheets.get(0).getName());

		// modify
		Map<Alias, AreaReference> aliases = new HashMap<Alias, AreaReference>();
		aliases.put(new Alias("alias"), new AreaReference("B1:C1"));
		sheetStorage.saveSheet(new SheetData(new SheetFullName("wktest", "sheet1"), SheetType.normal, aliases,
				Collections.<Chart> emptyList(), Collections.<AreaReference> emptyList()), Collections
				.singleton(SheetData.Property.aliases));
		// check
		SheetData sheet = sheetStorage.loadSheet();
		Assert.assertNotNull(sheet);
		Assert.assertNotNull(sheet.getAliases());
		Assert.assertEquals(1, sheet.getAliases().size());

		storage.deleteSheet(new SheetFullName("wktest", "sheet1"), SheetType.normal);
		sheets = storage.loadSheets(new WorkbookId("wktest"));
		Assert.assertNotNull(sheets);
		Assert.assertEquals(0, sheets.size());

	}

	@Test
	public void testSheetOther() throws StorageException, NotFoundException, AlreadyExistsException {
		// try first without existent workbook
		ISheetStorageService sheetStorage = null;

		try {
			sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"), SheetType.summary);
			Assert.fail("NotFoundException expected");
		} catch (NotFoundException ex) {
			//
		}

		// create workbook
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"), SheetType.summary);
		Assert.assertNotNull(sheetStorage);

		List<SheetData> sheets = storage.loadSheets(new WorkbookId("wktest"));
		Assert.assertNotNull(sheets);
		Assert.assertEquals(1, sheets.size());
		Assert.assertEquals("sheet1", sheets.get(0).getName());

		storage.deleteSheet(new SheetFullName("wktest", "sheet1"), SheetType.normal);
		sheets = storage.loadSheets(new WorkbookId("wktest"));
		Assert.assertNotNull(sheets);
		Assert.assertEquals(0, sheets.size());

	}

	@Test
	public void testCellsSetGet() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));

		SheetDimensions dims = sheetStorage.getSheetDimensions();
		Assert.assertNotNull(dims);
		Assert.assertEquals(2, dims.getRowCount());
		Assert.assertEquals(2, dims.getColumnCount());

		Matrix<CellData> cells = sheetStorage.loadCells(new AreaReference("B2:B2"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(Double.valueOf(100), cells.get(0, 0).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(0, 0).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(0, 0).getReference());
	}

	@Test
	public void testCellsSetGetBiggerArea() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));
		Matrix<CellData> cells = sheetStorage.loadCells(new AreaReference("A1:C3"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(2, cells.getColumnCount());
		Assert.assertEquals(2, cells.getRowCount());

		Assert.assertEquals(Double.valueOf(100), cells.get(1, 1).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(1, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(1, 1).getReference());
	}

	@Test
	public void testCellsModify() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));
		sheetStorage.saveCells(cellsValueWithStyle("B2", 200, "u"));
		Matrix<CellData> cells = sheetStorage.loadCells(new AreaReference("B2:B2"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(Double.valueOf(200), cells.get(0, 0).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("u"), cells.get(0, 0).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(0, 0).getReference());
	}

	@Test
	public void testCellsCheckRowsCols() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));
		List<RowData> rows = sheetStorage.loadRows(Range.range(0, 3));
		Assert.assertNotNull(rows);
		Assert.assertEquals(2, rows.size());

		List<ColumnData> cols = sheetStorage.loadColumns(Range.range(0, 3));
		Assert.assertNotNull(cols);
		Assert.assertEquals(2, cols.size());
	}

	@Test
	public void testCellsAddMore() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));
		sheetStorage.loadCells(new AreaReference("A1:A1"));// just to force the cache
		sheetStorage.saveCells(cellsValueWithStyle("D4", 200, "u"));
		Matrix<CellData> cells = sheetStorage.loadCells(new AreaReference("A1:D4"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(Double.valueOf(100), cells.get(1, 1).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(1, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(1, 1).getReference());

		List<RowData> rows = sheetStorage.loadRows(Range.range(0, 3));
		Assert.assertNotNull(rows);
		Assert.assertEquals(3, rows.size());

		List<ColumnData> cols = sheetStorage.loadColumns(Range.range(0, 3));
		Assert.assertNotNull(cols);
		Assert.assertEquals(3, cols.size());
	}

	@Test
	public void testRows() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		// save
		sheetStorage.saveRow(new RowData(1, 100, Styles.valueOf("b")), Collections.singleton(RowData.Property.styles));

		// check
		SheetDimensions dims = sheetStorage.getSheetDimensions();
		Assert.assertNotNull(dims);
		Assert.assertEquals(2, dims.getRowCount());
		List<RowData> rows = sheetStorage.loadRows(Range.range(0, 3));
		Assert.assertNotNull(rows);
		Assert.assertEquals(2, rows.size());
		Assert.assertNotNull(rows.get(0));
		Assert.assertNull(rows.get(0).getStyles());

		Assert.assertNotNull(rows.get(1));
		Assert.assertEquals(Styles.valueOf("b"), rows.get(1).getStyles());

		// insert
		sheetStorage
				.insertRow(new RowData(1, 200, Styles.valueOf("u")), Collections.singleton(RowData.Property.styles));

		// check
		dims = sheetStorage.getSheetDimensions();
		Assert.assertEquals(3, dims.getRowCount());
		rows = sheetStorage.loadRows(Range.range(0, 3));
		Assert.assertEquals(3, rows.size());
		Assert.assertNotNull(rows.get(1));
		Assert.assertEquals(Styles.valueOf("u"), rows.get(1).getStyles());
		Assert.assertNotNull(rows.get(2));
		Assert.assertEquals(Styles.valueOf("b"), rows.get(2).getStyles());

		// modify
		sheetStorage.saveRow(new RowData(1, 300, Styles.valueOf("i")), Collections.singleton(RowData.Property.styles));

		// check
		dims = sheetStorage.getSheetDimensions();
		Assert.assertEquals(3, dims.getRowCount());
		rows = sheetStorage.loadRows(Range.range(0, 3));
		Assert.assertEquals(3, rows.size());
		Assert.assertNotNull(rows.get(1));
		Assert.assertEquals(Styles.valueOf("i"), rows.get(1).getStyles());

		// delete
		sheetStorage.deleteRow(1);

		// check
		dims = sheetStorage.getSheetDimensions();
		Assert.assertEquals(2, dims.getRowCount());
		rows = sheetStorage.loadRows(Range.range(0, 3));
		Assert.assertEquals(2, rows.size());
		Assert.assertNotNull(rows.get(1));
		Assert.assertEquals(Styles.valueOf("b"), rows.get(1).getStyles());

	}

	@Test
	public void testColumns() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		// save
		sheetStorage.saveColumn(new ColumnData(1, 100, Styles.valueOf("b")),
				Collections.singleton(ColumnData.Property.styles));

		// check
		SheetDimensions dims = sheetStorage.getSheetDimensions();
		Assert.assertNotNull(dims);
		Assert.assertEquals(2, dims.getColumnCount());
		List<ColumnData> columns = sheetStorage.loadColumns(Range.range(0, 3));
		Assert.assertNotNull(columns);
		Assert.assertEquals(2, columns.size());
		Assert.assertNotNull(columns.get(0));
		Assert.assertNull(columns.get(0).getStyles());

		Assert.assertNotNull(columns.get(1));
		Assert.assertEquals(Styles.valueOf("b"), columns.get(1).getStyles());

		// insert
		sheetStorage.insertColumn(new ColumnData(1, 200, Styles.valueOf("u")),
				Collections.singleton(ColumnData.Property.styles));

		// check
		dims = sheetStorage.getSheetDimensions();
		Assert.assertEquals(3, dims.getColumnCount());
		columns = sheetStorage.loadColumns(Range.range(0, 3));
		Assert.assertEquals(3, columns.size());
		Assert.assertNotNull(columns.get(1));
		Assert.assertEquals(Styles.valueOf("u"), columns.get(1).getStyles());
		Assert.assertNotNull(columns.get(2));
		Assert.assertEquals(Styles.valueOf("b"), columns.get(2).getStyles());

		// modify
		sheetStorage.saveColumn(new ColumnData(1, 300, Styles.valueOf("i")),
				Collections.singleton(ColumnData.Property.styles));

		// check
		dims = sheetStorage.getSheetDimensions();
		Assert.assertEquals(3, dims.getColumnCount());
		columns = sheetStorage.loadColumns(Range.range(0, 3));
		Assert.assertEquals(3, columns.size());
		Assert.assertNotNull(columns.get(1));
		Assert.assertEquals(Styles.valueOf("i"), columns.get(1).getStyles());

		// delete
		sheetStorage.deleteColumn(1);

		// check
		dims = sheetStorage.getSheetDimensions();
		Assert.assertEquals(2, dims.getColumnCount());
		columns = sheetStorage.loadColumns(Range.range(0, 3));
		Assert.assertEquals(2, columns.size());
		Assert.assertNotNull(columns.get(1));
		Assert.assertEquals(Styles.valueOf("b"), columns.get(1).getStyles());

	}

	@Test
	public void testCellsModifyRows() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));
		sheetStorage.loadCells(new AreaReference("B2:B2"));// force cache

		// insert row
		sheetStorage
				.insertRow(new RowData(1, 200, Styles.valueOf("u")), Collections.singleton(RowData.Property.styles));

		Matrix<CellData> cells = sheetStorage.loadCells(new AreaReference("A1:C3"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(3, cells.getRowCount());
		Assert.assertNull(cells.get(1, 1).getValue());
		Assert.assertNull(cells.get(1, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(1, 1).getReference());

		Assert.assertEquals(Double.valueOf(100), cells.get(2, 1).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(2, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B3"), cells.get(2, 1).getReference());

		// remove row
		sheetStorage.deleteRow(1);
		cells = sheetStorage.loadCells(new AreaReference("A1:C3"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(2, cells.getRowCount());
		Assert.assertEquals(Double.valueOf(100), cells.get(1, 1).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(1, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(1, 1).getReference());
	}

	@Test
	public void testCellsModifyColumns() throws StorageException, NotFoundException, AlreadyExistsException {
		storage.add(new DataSourceConfigurationId(0), new WorkbookId("wktest"));
		ISheetStorageService sheetStorage = storage.getSheetStorage(new SheetFullName("wktest", "sheet1"),
				SheetType.normal);

		sheetStorage.saveCells(cellsValueWithStyle("B2", 100, "b"));
		sheetStorage.loadCells(new AreaReference("B2:B2"));// force cache

		// insert row
		sheetStorage.insertColumn(new ColumnData(1, 200, Styles.valueOf("u")),
				Collections.singleton(ColumnData.Property.styles));

		Matrix<CellData> cells = sheetStorage.loadCells(new AreaReference("A1:C3"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(3, cells.getColumnCount());
		Assert.assertNull(cells.get(1, 1).getValue());
		Assert.assertNull(cells.get(1, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(1, 1).getReference());

		Assert.assertEquals(Double.valueOf(100), cells.get(1, 2).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(1, 2).getStyles());
		Assert.assertEquals(new CellReference("sheet1!C2"), cells.get(1, 2).getReference());

		// remove row
		sheetStorage.deleteColumn(1);
		cells = sheetStorage.loadCells(new AreaReference("A1:C3"));
		Assert.assertNotNull(cells);
		Assert.assertEquals(2, cells.getColumnCount());
		Assert.assertEquals(Double.valueOf(100), cells.get(1, 1).getValue().getNumberValue());
		Assert.assertEquals(Styles.valueOf("b"), cells.get(1, 1).getStyles());
		Assert.assertEquals(new CellReference("sheet1!B2"), cells.get(1, 1).getReference());
	}

	private Collection<CellDataWithProperties> cellsValueWithStyle(Object... valueStyle) {
		List<CellDataWithProperties> props = new ArrayList<CellDataWithProperties>();
		for (int i = 0; i < valueStyle.length; i += 3) {
			props.add(new CellDataWithProperties(new CellData(new CellReference((String) valueStyle[i]),
					GenericValueUtils.objectAsValue(valueStyle[i + 1]), null, Styles
							.valueOf((String) valueStyle[i + 2])), Arrays.asList(CellData.Property.value,
					CellData.Property.styles)));
		}
		return props;
	}
}
