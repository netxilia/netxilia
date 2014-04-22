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
package org.netxilia.api.impl.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DataSourceConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.storage.IDataSourceConfigurationListener;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.utils.Pair;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

public class DataSourceConfigurationServiceImpl implements IDataSourceConfigurationService, ApplicationContextAware {
	private final static Logger log = Logger.getLogger(DataSourceConfigurationServiceImpl.class);

	private static final Pattern REGEX_FILE = Pattern.compile("datasource-(\\d+)\\.properties");
	private static final String PROP_NAME = "jdbc.name";
	private static final String PROP_DESCRIPTION = "jdbc.description";

	private static final String PROP_DRIVER = "jdbc.driver";
	private static final String PROP_URL = "jdbc.url";
	private static final String PROP_USERNAME = "jdbc.username";
	private static final String PROP_PASSWORD = "jdbc.password";

	private static final String NETXILIA_HOME_VAR = "${netxilia.home}";

	private String workbooksFile = "workbooks.properties";
	private String path;

	private ApplicationContext applicationContext;

	private Map<DataSourceConfigurationId, Pair<GenericObjectPool, DataSource>> pools = new HashMap<DataSourceConfigurationId, Pair<GenericObjectPool, DataSource>>();

	private List<IDataSourceConfigurationListener> listeners = new ArrayList<IDataSourceConfigurationListener>();

	private boolean initialized = false;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getWorkbooksFile() {
		return workbooksFile;
	}

	public void setWorkbooksFile(String workbooksFile) {
		this.workbooksFile = workbooksFile;
	}

	private boolean createAppDir() {
		File file = new File(path);
		if (!file.exists()) {
			boolean ok = file.mkdirs();
			if (!ok) {
				log.warn("The datasource storage path[" + path + "] could not be created");
			}
			return ok;
		}
		return true;
	}

	private File fileForId(DataSourceConfigurationId id) {
		return new File(getPath(), "datasource-" + id.getId() + ".properties");
	}

	@Override
	public synchronized void delete(DataSourceConfigurationId id) throws StorageException, NotFoundException {
		init();
		File dsFile = fileForId(id);
		if (dsFile.exists()) {
			notifyDeleteConfiguration(id);
			dsFile.delete();
		} else {
			throw new NotFoundException(dsFile + " does not exists");
		}

	}

	private boolean copyToFile(String folder, String fileName) throws StorageException {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(folder + "/" + fileName);
		if (in == null) {
			return false;
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(path, fileName));
			IOUtils.copy(in, out);
			in.close();
			out.flush();
		} catch (Exception e) {
			throw new StorageException(e);
		} finally {
			if (in != null) {
				IOUtils.closeQuietly(in);
			}
			if (out != null) {
				IOUtils.closeQuietly(out);
			}
		}
		return true;
	}

	private void init() throws StorageException {
		if (initialized) {
			return;
		}
		if (!createAppDir()) {
			// a read-only mode
			initialized = true;
			return;
		}
		File dir = new File(path);
		File[] cfgFiles = dir.listFiles((FilenameFilter) new RegexFileFilter(REGEX_FILE));
		if (cfgFiles == null || cfgFiles.length == 0) {

			// TODO use a scanner to find both in jar and in extracted version
			int i = 0;
			while (true) {
				String fileName = "datasource-" + i + ".properties";

				if (!copyToFile("datasources", fileName)) {
					break;
				}
				++i;

			}
			if (!copyToFile("datasources", "workbooks.properties")) {

			}
		}

		initialized = true;

	}

	@Override
	public synchronized List<DataSourceConfiguration> findAll() throws StorageException {
		init();
		File dir = new File(path);
		File[] cfgFiles = dir.listFiles((FilenameFilter) new RegexFileFilter(REGEX_FILE));
		if (cfgFiles == null || cfgFiles.length == 0) {
			return Collections.emptyList();
		}
		List<DataSourceConfiguration> configs = new ArrayList<DataSourceConfiguration>(cfgFiles.length);
		for (File cfgFile : cfgFiles) {
			configs.add(readConfig(cfgFile));
		}
		return configs;
	}

	private DataSourceConfiguration readConfig(File cfgFile) throws StorageException {
		Properties props = new Properties();
		FileReader reader = null;

		try {
			reader = new FileReader(cfgFile);
			props.load(reader);
			Matcher m = REGEX_FILE.matcher(cfgFile.getName());
			if (!m.find()) {
				throw new StorageException("Wrong file name:" + cfgFile);
			}
			DataSourceConfigurationId id = new DataSourceConfigurationId(Integer.valueOf(m.group(1)));

			DataSourceConfiguration cfg = new DataSourceConfiguration(id, props.getProperty(PROP_NAME),
					props.getProperty(PROP_DESCRIPTION), props.getProperty(PROP_DRIVER), props.getProperty(PROP_URL),
					props.getProperty(PROP_USERNAME), props.getProperty(PROP_PASSWORD));
			return cfg;
		} catch (IOException e) {
			throw new StorageException(e);
		} finally {
			if (reader != null) {
				IOUtils.closeQuietly(reader);
			}
		}

	}

	@Override
	public synchronized DataSourceConfiguration load(DataSourceConfigurationId id) throws StorageException,
			NotFoundException {
		init();
		File cfgFile = fileForId(id);
		if (cfgFile == null || !cfgFile.exists()) {
			throw new NotFoundException(id + " not found");
		}
		return readConfig(cfgFile);
	}

	@Override
	public synchronized DataSourceConfiguration save(DataSourceConfiguration cfg) throws StorageException {
		init();
		DataSourceConfiguration returnCfg = cfg;

		if (cfg.getId() == null) {
			DataSourceConfigurationId newId = newId();
			returnCfg = new DataSourceConfiguration(newId, cfg.getName(), cfg.getDescription(),
					cfg.getDriverClassName(), cfg.getUrl(), cfg.getUsername(), cfg.getPassword());
		} else {
			notifyModifyConfiguration(cfg.getId());
			Pair<GenericObjectPool, DataSource> poolInfo = pools.get(cfg.getId());
			if (poolInfo != null) {
				try {
					poolInfo.getFirst().close();
				} catch (Exception e) {
					log.error("Cannot close datasource pool:" + e, e);
				}
			}
			pools.remove(cfg.getId());
			buildDataSource(cfg);
		}
		Properties props = new Properties();
		FileWriter writer = null;

		try {
			props.setProperty(PROP_NAME, returnCfg.getName());
			props.setProperty(PROP_DESCRIPTION, returnCfg.getDescription());
			props.setProperty(PROP_DRIVER, returnCfg.getDriverClassName());
			props.setProperty(PROP_URL, returnCfg.getUrl());
			props.setProperty(PROP_USERNAME, returnCfg.getUsername());
			props.setProperty(PROP_PASSWORD, returnCfg.getPassword());

			writer = new FileWriter(fileForId(returnCfg.getId()));
			props.store(writer, "Saved by Netxilia");

		} catch (IOException e) {
			throw new StorageException(e);
		} finally {
			if (writer != null) {
				try {
					writer.flush();
				} catch (Exception e2) {
					throw new StorageException(e2);
				}
				IOUtils.closeQuietly(writer);
			}
		}

		return returnCfg;

	}

	private DataSourceConfigurationId newId() throws StorageException {
		List<DataSourceConfiguration> cfgs = findAll();
		if (cfgs == null) {
			return new DataSourceConfigurationId(0);
		}
		int max = -1;
		for (DataSourceConfiguration cfg : cfgs) {
			max = Math.max(max, cfg.getId().getId());
		}

		return new DataSourceConfigurationId(max + 1);
	}

	private Properties loadWorkbookToDataSourceFile() throws StorageException {
		Properties props = new Properties();
		FileReader reader = null;

		try {
			File propFile = new File(getPath(), workbooksFile);
			if (!propFile.exists()) {
				return props;
			}
			reader = new FileReader(propFile);
			props.load(reader);

			return props;
		} catch (IOException e) {
			throw new StorageException(e);
		} finally {
			if (reader != null) {
				IOUtils.closeQuietly(reader);
			}
		}
	}

	private void saveWorkbookToDataSourceFile(Properties props) throws StorageException {
		FileWriter writer = null;

		try {
			File propFile = new File(getPath(), workbooksFile);
			writer = new FileWriter(propFile);
			props.store(writer, "Saved by Netxilia");

		} catch (IOException e) {
			throw new StorageException(e);
		} finally {
			if (writer != null) {
				try {
					writer.flush();
				} catch (Exception e2) {
					throw new StorageException(e2);
				}
				IOUtils.closeQuietly(writer);
			}
		}
	}

	@Override
	public synchronized void deleteConfigurationForWorkbook(WorkbookId workbookKey) throws StorageException,
			NotFoundException {
		init();
		Properties props = loadWorkbookToDataSourceFile();
		if (props.containsKey(workbookKey.getKey())) {
			notifyDeleteConfigurationForWorkbook(workbookKey);
			props.remove(workbookKey.getKey());
			saveWorkbookToDataSourceFile(props);
		}

	}

	@Override
	public synchronized DataSourceConfiguration loadByWorkbook(WorkbookId workbookKey) throws StorageException,
			NotFoundException {
		init();
		Properties props = loadWorkbookToDataSourceFile();
		String configId = props.getProperty(workbookKey.getKey());
		if (configId == null) {
			throw new NotFoundException("Workbook:" + workbookKey + " is not configured");
		}
		return load(new DataSourceConfigurationId(configId));
	}

	@Override
	public synchronized void setConfigurationForWorkbook(WorkbookId workbookKey, DataSourceConfigurationId id)
			throws StorageException, NotFoundException {
		init();
		Properties props = loadWorkbookToDataSourceFile();
		props.setProperty(workbookKey.getKey(), id.toString());
		saveWorkbookToDataSourceFile(props);
	}

	@Override
	public synchronized void test(DataSourceConfigurationId id) throws StorageException, NotFoundException,
			SQLException {
		init();
		DataSourceConfiguration cfg = load(id);
		DataSource dataSource = buildSimpleDataSource(cfg);
		Connection connection = null;
		try {
			connection = dataSource.getConnection();

		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// quiet
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public DataSource buildSimpleDataSource(DataSourceConfiguration cfg) {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

		Class<? extends Driver> driverClass;
		try {
			driverClass = (Class<? extends Driver>) Class.forName(cfg.getDriverClassName());
		} catch (ClassNotFoundException e) {
			throw new NetxiliaResourceException("Cannot find class driver:" + cfg.getDriverClassName());
		}
		dataSource.setDriverClass(driverClass);
		dataSource.setUrl(cfg.getUrl().replace(NETXILIA_HOME_VAR, path));
		dataSource.setUsername(cfg.getUsername());
		dataSource.setPassword(cfg.getPassword());
		return dataSource;
	}

	public synchronized DataSource buildDataSource(DataSourceConfiguration cfg) {
		init();
		Pair<GenericObjectPool, DataSource> poolInfo = pools.get(cfg.getId());
		if (poolInfo != null) {
			return poolInfo.getSecond();
		}

		GenericObjectPool connectionPool = applicationContext.getBean(GenericObjectPool.class);
		DataSource simpleDataSource = buildSimpleDataSource(cfg);

		ConnectionFactory connectionFactory = new DataSourceConnectionFactory(simpleDataSource);

		// ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(cfg.getUrl().replace(
		// NETXILIA_HOME_VAR, path), cfg.getUsername(), cfg.getPassword());

		new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
		PoolingDataSource poolingDataSource = new PoolingDataSource(connectionPool);

		log.info("Creating a new datasource " + poolingDataSource + " for config:" + cfg.getId());
		pools.put(cfg.getId(), new Pair<GenericObjectPool, DataSource>(connectionPool, poolingDataSource));

		return poolingDataSource;
	}

	@Override
	public List<Pair<WorkbookId, DataSourceConfigurationId>> findAllWorkbooksConfigurations() throws StorageException {
		init();
		Properties props = loadWorkbookToDataSourceFile();
		List<Pair<WorkbookId, DataSourceConfigurationId>> list = new ArrayList<Pair<WorkbookId, DataSourceConfigurationId>>();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			list.add(new Pair<WorkbookId, DataSourceConfigurationId>(new WorkbookId(entry.getKey().toString()),
					new DataSourceConfigurationId(entry.getValue().toString())));
		}
		return list;
	}

	@Override
	public List<WorkbookId> findAllWorkbooksConfigurationsForDatasource(DataSourceConfigurationId id)
			throws StorageException {
		init();
		Properties props = loadWorkbookToDataSourceFile();
		List<WorkbookId> list = new ArrayList<WorkbookId>();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			WorkbookId workbookId = new WorkbookId(entry.getKey().toString());
			DataSourceConfigurationId cfgId = new DataSourceConfigurationId(entry.getValue().toString());
			if (id.equals(cfgId)) {
				list.add(workbookId);
			}
		}
		return list;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	public void close() {
		log.info("Closing Datasource pools");
		for (Map.Entry<DataSourceConfigurationId, Pair<GenericObjectPool, DataSource>> entry : pools.entrySet()) {
			try {
				log.info("Closed datasource " + entry.getValue().getSecond() + " for config:" + entry.getKey());
				entry.getValue().getFirst().close();

			} catch (Exception e) {
				log.error("Cannot close datasource pool:" + e, e);
			}
		}
		pools.clear();
	}

	@Override
	public void addDataSourceConfigurationListener(IDataSourceConfigurationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeDataSourceConfigurationListener(IDataSourceConfigurationListener listener) {
		listeners.remove(listener);
	}

	private void notifyDeleteConfiguration(DataSourceConfigurationId id) {
		for (IDataSourceConfigurationListener listener : listeners) {
			listener.onDeleteConfiguration(id);
		}
	}

	private void notifyDeleteConfigurationForWorkbook(WorkbookId workbookKey) {
		for (IDataSourceConfigurationListener listener : listeners) {
			listener.onDeleteConfigurationForWorkbook(workbookKey);
		}
	}

	private void notifyModifyConfiguration(DataSourceConfigurationId id) {
		for (IDataSourceConfigurationListener listener : listeners) {
			listener.onModifyConfiguration(id);
		}
	}
}
