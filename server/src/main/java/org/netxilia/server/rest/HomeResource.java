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
package org.netxilia.server.rest;

import java.security.AccessControlException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.user.IAclService;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.user.Permission;
import org.netxilia.api.user.User;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.Pair;
import org.netxilia.jaxrs.html.ModelAndView;
import org.netxilia.server.rest.html.HomeModel;
import org.netxilia.server.util.StringHolder;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/home")
public class HomeResource extends AbstractResource {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HomeResource.class);

	private static final String FOLDERS_SHEET = "folders";

	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	@Autowired
	private IAclService aclService;

	@Autowired
	private IUserService userService;

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	public IAclService getAclService() {
		return aclService;
	}

	public void setAclService(IAclService aclService) {
		this.aclService = aclService;
	}

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	private DataSourceConfigurationId getMostUsedDataSource() throws StorageException {
		Map<DataSourceConfigurationId, Integer> dataSourceWorkbookCount = new HashMap<DataSourceConfigurationId, Integer>();

		List<Pair<WorkbookId, DataSourceConfigurationId>> workbooksAndConfigs = dataSourceConfigurationService
				.findAllWorkbooksConfigurations();
		for (Pair<WorkbookId, DataSourceConfigurationId> wkCfg : workbooksAndConfigs) {
			Integer count = dataSourceWorkbookCount.get(wkCfg.getSecond());
			dataSourceWorkbookCount.put(wkCfg.getSecond(), count != null ? count + 1 : 0);
		}

		DataSourceConfigurationId maxEntry = null;
		int max = Integer.MIN_VALUE;
		for (Map.Entry<DataSourceConfigurationId, Integer> entry : dataSourceWorkbookCount.entrySet()) {
			if (entry.getValue() > max) {
				maxEntry = entry.getKey();
				max = entry.getValue();
			}
		}
		return maxEntry;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public ModelAndView<HomeModel> get() throws NetxiliaResourceException, NetxiliaBusinessException {
		HomeModel homeModel = new HomeModel(treeview().getValue(), getMostUsedDataSource());
		return new ModelAndView<HomeModel>(homeModel, "/WEB-INF/jsp/home.jsp");
	}

	@GET
	@Path("/treeview")
	@Produces(MediaType.APPLICATION_JSON)
	public StringHolder treeview() throws NetxiliaResourceException, NetxiliaBusinessException {
		// TODO - here a light version if IWorkbook should be return to not be able to get around security exceptions

		// add workbooks
		List<Pair<WorkbookId, DataSourceConfigurationId>> workbooksAndConfigs = dataSourceConfigurationService
				.findAllWorkbooksConfigurations();

		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(null);

		for (Pair<WorkbookId, DataSourceConfigurationId> wkCfg : workbooksAndConfigs) {
			WorkbookId wkId = wkCfg.getFirst();
			IWorkbook workbook = null;
			Set<SheetFullName> sheetNames = new TreeSet<SheetFullName>();
			try {
				workbook = getWorkbookProcessor().getWorkbook(wkId);

				for (ISheet sheet : workbook.getSheets()) {
					if (sheet.getType() == SheetType.normal) {
						try {
							aclService.checkPermission(sheet.getFullName(), Permission.read);
						} catch (AccessControlException e) {
							continue;
						}
						sheetNames.add(sheet.getFullName());
					}
				}
			} catch (AccessControlException ex) {
				// user has not write to see the workbook
				continue;
			} catch (Exception ex) {
				log.error("Could not load workbook " + wkId + ":" + ex, ex);
				// add the workbook with error
				DefaultMutableTreeNode workbookNode = new DefaultMutableTreeNode(new TreeViewData(wkId.getKey(),
						wkId.getKey(), "workbook error"));
				rootNode.add(workbookNode);
				continue;
			}

			ISheet foldersSheet = null;
			SheetFullName folderSheetName = new SheetFullName(workbook.getName(), FOLDERS_SHEET);
			try {
				foldersSheet = getWorkbookProcessor().getWorkbook(wkId).getSheet(FOLDERS_SHEET);
			} catch (NotFoundException ex) {
				// no folder sheet
			} catch (Exception ex) {
				log.error("Could not load folder sheet" + folderSheetName + ":" + ex, ex);
			}
			DefaultMutableTreeNode workbookNode = buildWorkbookTree(workbook, foldersSheet, sheetNames);
			rootNode.add(workbookNode);

		}

		// only for admins
		User currentUser = userService.getCurrentUser();
		if (currentUser != null && currentUser.isAdmin()) {
			// add admin
			DefaultMutableTreeNode adminNode = new DefaultMutableTreeNode(new TreeViewData("admin", "Administration",
					"admin", true));
			rootNode.add(adminNode);

			// add datasources nodes
			DefaultMutableTreeNode dsNode = new DefaultMutableTreeNode(new TreeViewData("ds", "Datasources",
					"datasources", true));
			adminNode.add(dsNode);
			for (DataSourceConfiguration dsConfig : dataSourceConfigurationService.findAll()) {
				dsNode.add(new DefaultMutableTreeNode(new TreeViewData(dsConfig.getId().toString(), dsConfig.getName(),
						"datasource")));
			}

			// add modules nodes
			DefaultMutableTreeNode moduleNode = new DefaultMutableTreeNode(new TreeViewData("modules", "Modules",
					"modules", true));
			adminNode.add(moduleNode);

			// add build nodes
			DefaultMutableTreeNode buildNode = new DefaultMutableTreeNode(new TreeViewData("build", "Custom Modules",
					"build", true));
			adminNode.add(buildNode);
		}

		StringBuilder treeview = new StringBuilder();
		buildTreeView(rootNode, treeview);
		return new StringHolder(treeview.toString());
	}

	/**
	 * walk the tree and dump the node to the tree
	 * 
	 * @param node
	 * @param treeview
	 */
	@SuppressWarnings("unchecked")
	private void buildTreeView(DefaultMutableTreeNode node, StringBuilder treeview) {
		// <ul id="example" class="filetree">
		// <li><span class="folder">Folder 1</span>
		// <ul>
		// <li><span class="file">Item 1.1</span></li>
		// </ul>
		// </li>
		// <li><span class="folder">Folder 2</span>

		TreeViewData viewData = (TreeViewData) node.getUserObject();
		if (node.isLeaf()) {
			if (viewData != null) {
				treeview.append("<li><span id='").append(viewData.getId()).append("' class='")
						.append(viewData.getCssClass()).append("'>").append(viewData.getName()).append("</span></li>");
			}
		} else {
			if (viewData != null) {
				if (viewData.isCollapsed()) {
					treeview.append("<li class='closed'>");
				} else {
					treeview.append("<li>");
				}
				treeview.append("<span class='folder ").append(viewData.getCssClass()).append("'>")
						.append(viewData.getName()).append("</span><ul>");
			}
			Enumeration<DefaultMutableTreeNode> childrenEnum = node.children();
			while (childrenEnum.hasMoreElements()) {
				buildTreeView(childrenEnum.nextElement(), treeview);
			}
			if (viewData != null) {
				treeview.append("</ul></li>");
			}
		}
	}

	/**
	 * build the HTML tags for a tree like view
	 * 
	 * @param foldersSheet
	 * @param treeview
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	private DefaultMutableTreeNode buildWorkbookTree(IWorkbook workbook, ISheet foldersSheet,
			Set<SheetFullName> sheetNames) throws NetxiliaResourceException, NetxiliaBusinessException {
		DefaultMutableTreeNode workbookNode = new DefaultMutableTreeNode(new TreeViewData(workbook.getId().getKey(),
				workbook.getName(), "workbook"));

		Stack<DefaultMutableTreeNode> stockNodes = new Stack<DefaultMutableTreeNode>();
		stockNodes.push(workbookNode);

		Set<SheetFullName> alreadyInsertedSheets = new HashSet<SheetFullName>();
		if (foldersSheet != null) {
			Matrix<CellData> folderCells = foldersSheet.receiveCells(AreaReference.ALL).getNonBlocking();
			for (List<CellData> row : folderCells.getRows()) {
				int level = 0;
				String nodeName = null;
				for (CellData cell : row) {
					if (cell.getValue() != null) {
						nodeName = cell.getValue().getStringValue();
						if (nodeName != null && nodeName.length() > 0) {
							level = cell.getReference().getColumnIndex();
							break;
						}
					}
				}

				if (nodeName == null) {
					// empty line - ignored
					continue;
				}

				// first level for folders is 1 (under the root node)
				level = level + 1;
				SheetFullName sheetName = new SheetFullName(workbook.getName(), nodeName);
				boolean isSheet = sheetNames.contains(sheetName);
				if (isSheet) {
					alreadyInsertedSheets.add(sheetName);
				}
				DefaultMutableTreeNode crt = new DefaultMutableTreeNode(new TreeViewData(sheetName.toString(),
						sheetName.getSheetName(), isSheet ? "sheet" : "folder"));
				while (!stockNodes.empty()) {
					DefaultMutableTreeNode node = stockNodes.peek();
					if (level > node.getLevel()) {
						// make sure is the direct child
						node.add(crt);
						break;
					}
					stockNodes.pop();
				}
				stockNodes.push(crt);
			}
		}
		// add the sheets not already added
		for (SheetFullName sheetName : sheetNames) {
			if (alreadyInsertedSheets.contains(sheetName)) {
				continue;
			}
			DefaultMutableTreeNode sheetNode = new DefaultMutableTreeNode(new TreeViewData(sheetName.toString(),
					sheetName.getSheetName(), "sheet"));
			workbookNode.add(sheetNode);
		}

		return workbookNode;
	}

	private static class TreeViewData {
		private final String id;
		private final String name;
		private final String cssClass;
		private final boolean collapsed;

		public TreeViewData(String id, String name, String cssClass) {
			this(id, name, cssClass, false);
		}

		public TreeViewData(String id, String name, String cssClass, boolean collapsed) {
			this.id = id;
			this.name = name;
			this.cssClass = cssClass;
			this.collapsed = collapsed;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getCssClass() {
			return cssClass;
		}

		public boolean isCollapsed() {
			return collapsed;
		}

	}
}
