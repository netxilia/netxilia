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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.IValueListStyleFormatter;
import org.netxilia.api.display.Style;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.value.NamedValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class handles the manipulation of the diffent formatters.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
@Path("/formatters")
public class FormatterResource extends AbstractResource {
	@Autowired
	private IStyleService styleService;

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

	@POST
	@Path("/{workbook}/{name}/{sourceWorkbook}/{names}/{values}")
	public void setFormatter(@PathParam("workbook") WorkbookId workbookName, @PathParam("name") String formatterName,
			@PathParam("sourceWorkbook") WorkbookId sourceWorkbookId, @PathParam("names") AreaReference nameRef,
			@PathParam("values") AreaReference valueRef) throws NetxiliaResourceException, NetxiliaBusinessException {

		styleService.addValueFormatter(workbookName, formatterName, sourceWorkbookId, nameRef, valueRef);
	}

	@GET
	@Path("/{workbook}/{formatterStyles}/formatValues")
	@Produces(MediaType.APPLICATION_JSON)
	public List<NamedValue> getFormatValues(@PathParam("workbook") WorkbookId workbookName,
			@PathParam("formatterStyles") Styles formatterStyles) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		if (formatterStyles != null) {
			for (Style style : formatterStyles.getItems()) {
				StyleDefinition def = styleService.getStyleDefinition(workbookName, style);

				if (def == null || !(def.getFormatter() instanceof IValueListStyleFormatter)) {
					return null;
				}

				return ((IValueListStyleFormatter) def.getFormatter()).getValues();
			}
		}
		return null;

	}

	@GET
	@Path("/{workbook}/{name}/values")
	@Produces(MediaType.APPLICATION_JSON)
	public List<NamedValue> getValues(@PathParam("workbook") WorkbookId workbookName,
			@PathParam("name") String formatterName) throws NetxiliaResourceException, NetxiliaBusinessException {

		Style styleName = new Style(formatterName);

		StyleDefinition def = styleService.getStyleDefinition(workbookName, styleName);

		if (def == null || !(def.getFormatter() instanceof IValueListStyleFormatter)) {
			return null;
		}

		return ((IValueListStyleFormatter) def.getFormatter()).getValues();

	}
}
