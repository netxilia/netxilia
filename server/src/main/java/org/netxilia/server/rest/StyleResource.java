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

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.WorkbookId;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/styles")
@Produces(MediaType.TEXT_PLAIN)
public class StyleResource extends AbstractResource {
	@Autowired
	private IStyleService styleService;

	private String styleDefinitionPrefix = ".cells";

	private String stylesSheetName = "styles";

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	public String getStyleDefinitionPrefix() {
		return styleDefinitionPrefix;
	}

	public String getStylesSheetName() {
		return stylesSheetName;
	}

	public void setStylesSheetName(String stylesSheetName) {
		this.stylesSheetName = stylesSheetName;
	}

	/**
	 * This string is appended for each style's definitions when generated. It is usually the CSS class of the sheet's
	 * container.
	 * 
	 * @param styleDefinitionPrefix
	 */
	public void setStyleDefinitionPrefix(String styleDefinitionPrefix) {
		this.styleDefinitionPrefix = styleDefinitionPrefix;
	}

	@GET
	@Path("/{workbook}")
	@Produces("text/css")
	public String getStyleDefinitions(@PathParam("workbook") WorkbookId workbookName) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		StringBuilder response = new StringBuilder();

		// these are default style definitions
		Collection<StyleDefinition> styleDefinitions = styleService.getStyleDefinitions(workbookName);
		for (StyleDefinition def : styleDefinitions) {
			response.append(styleDefinitionPrefix).append(" .").append(def.getCss()).append("\n");
		}

		return response.toString();
	}

}
