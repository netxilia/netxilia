package org.netxilia.spi.impl.storage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.storage.DataSourceConfigurationServiceImpl;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.utils.Pair;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDatasourceConfiguration {
	private DataSourceConfigurationServiceImpl dsService;

	// test errors : wrong conf, wrong wk

	@Before
	public void setup() throws IOException {
		AclPrivilegedMode.set();
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		dsService = context.getBean(DataSourceConfigurationServiceImpl.class);
		String path = new File(System.getProperty("java.io.tmpdir"), "nx-test").getAbsolutePath();
		dsService.setPath(path);
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
	public void testFindConfigAndInit() {
		// the datasource-0.properties should be copied and one config should be present
		List<DataSourceConfiguration> configs = dsService.findAll();
		Assert.assertNotNull(configs);
		Assert.assertEquals(1, configs.size());
		Assert.assertEquals("nx test", configs.get(0).getName());
	}

	@Test
	public void testWorkbook() throws StorageException, NotFoundException {
		dsService.setConfigurationForWorkbook(new WorkbookId("wktest"), new DataSourceConfigurationId(0));
		DataSourceConfiguration cfg = dsService.loadByWorkbook(new WorkbookId("wktest"));
		Assert.assertNotNull(cfg);
		Assert.assertEquals("nx test", cfg.getName());

		List<WorkbookId> wks = dsService.findAllWorkbooksConfigurationsForDatasource(new DataSourceConfigurationId(0));
		Assert.assertNotNull(wks);
		Assert.assertEquals(1, wks.size());
		Assert.assertEquals(new WorkbookId("wktest"), wks.get(0));

		List<Pair<WorkbookId, DataSourceConfigurationId>> pairs = dsService.findAllWorkbooksConfigurations();
		Assert.assertNotNull(pairs);
		Assert.assertEquals(1, pairs.size());
		Assert.assertEquals(new WorkbookId("wktest"), pairs.get(0).getFirst());
		Assert.assertEquals(new DataSourceConfigurationId(0), pairs.get(0).getSecond());

		dsService.deleteConfigurationForWorkbook(new WorkbookId("wktest"));
		wks = dsService.findAllWorkbooksConfigurationsForDatasource(new DataSourceConfigurationId(0));
		Assert.assertNotNull(wks);
		Assert.assertEquals(0, wks.size());

	}

	@Test
	public void testDeleteConfiguration() throws StorageException, NotFoundException {
		DataSourceConfiguration newCfg = dsService.save(new DataSourceConfiguration(null, "new name", "new", "new",
				"new", "new", "new"));
		Assert.assertNotNull(newCfg);
		Assert.assertNotNull(newCfg.getId());
		Assert.assertEquals("new name", newCfg.getName());

		dsService.setConfigurationForWorkbook(new WorkbookId("wktest"), newCfg.getId());
		dsService.delete(newCfg.getId());

		try {
			dsService.load(newCfg.getId());
			Assert.fail("NotFoundException not raised");
		} catch (NotFoundException e) {
			// expected
		}
		try {
			dsService.loadByWorkbook(new WorkbookId("wktest"));
			Assert.fail("NotFoundException not raised");
		} catch (NotFoundException e) {
			// expected
		}
	}

	@Test
	public void testModifyConfiguration() throws StorageException, NotFoundException {
		DataSourceConfiguration newCfg = dsService.save(new DataSourceConfiguration(new DataSourceConfigurationId(0),
				"new name", "new", "org.h2.Driver", "jdbc:h2:mem:nxtest", "new", "new"));
		Assert.assertNotNull(newCfg);
		Assert.assertNotNull(newCfg.getId());
		Assert.assertEquals("new name", newCfg.getName());

		newCfg = dsService.load(new DataSourceConfigurationId(0));
		Assert.assertNotNull(newCfg);
		Assert.assertNotNull(newCfg.getId());
		Assert.assertEquals("new name", newCfg.getName());
	}

	@Test
	public void testDataSource() throws StorageException, NotFoundException, SQLException {
		dsService.test(new DataSourceConfigurationId(0));
	}

}
