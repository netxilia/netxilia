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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.netxilia.api.chart.Axis;
import org.netxilia.api.chart.Chart;
import org.netxilia.api.chart.ChartWithData;
import org.netxilia.api.chart.Element;
import org.netxilia.api.chart.Title;
import org.netxilia.api.chart.Type;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;

@Path("/charts")
public class ChartResource extends AbstractResource {
	/**
	 * return the chart along with its data.
	 * 
	 * @param workbookName
	 * @param sheetName
	 * @param chartIndex
	 * @return
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	@GET
	@Path("/{workbook}/{sheetName}/{chart}")
	@Produces(MediaType.APPLICATION_JSON)
	public ChartWithData data(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheetName") String sheetName,
			@PathParam("chart") int chartIndex) throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		SheetData sheetData = sheet.receiveSheet().getNonBlocking();
		if (sheetData.getCharts() == null || chartIndex < 0 || chartIndex >= sheetData.getCharts().size()) {
			throw new NotFoundException("Chart with index " + chartIndex + " of sheet: " + sheetName + " not found");
		}
		Chart chart = sheetData.getCharts().get(chartIndex);
		ChartWithData chartData = new ChartWithData(chart);
		chartData.populate(sheet);
		return chartData;

	}

	@PUT
	@Path("/{workbook}/{sheetName}")
	@Produces({ MediaType.APPLICATION_JSON })
	public int add(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheetName") String sheetName,
			@FormParam("areaRef") AreaReference areaRef, @FormParam("title") String title,
			@FormParam("type") Type chartType) throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		Chart chart = new Chart();
		chart.setY_axis(new Axis());
		chart.getY_axis().setMax(50);
		chart.getY_axis().setTick_height(10);
		chart.getY_axis().setStroke(4);
		chart.getY_axis().setTick_length(3);
		chart.setAreaReference(areaRef);
		chart.setTitle(new Title(title));
		chart.setType(chartType);
		List<Element> series = new ArrayList<Element>();
		// each column is a series (optionally can be done by rows)
		for (int i = areaRef.getFirstColumnIndex(); i <= areaRef.getLastColumnIndex(); ++i) {
			Element s = new Element();
			series.add(s);
		}
		chart.setElements(series);
		sheet.sendCommand(SheetCommands.addChart(chart));
		SheetData sheetData = sheet.receiveSheet().getNonBlocking();
		// TODO maybe should just look for the corresponding chart or generate externally an ID
		return sheetData.getCharts().size() - 1;

	}

	@POST
	@Path("/{workbook}/{sheetName}/{chart}")
	@Produces(MediaType.APPLICATION_JSON)
	public void set(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheetName") String sheetName,
			@PathParam("chart") int chartIndex, @FormParam("areaRef") AreaReference areaRef,
			@FormParam("title") String title, @FormParam("type") Type chartType) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		SheetData sheetData = sheet.receiveSheet().getNonBlocking();
		if (sheetData.getCharts() == null || chartIndex < 0 || chartIndex >= sheetData.getCharts().size()) {
			throw new NotFoundException("Chart with index " + chartIndex + " of sheet: " + sheetName + " not found");
		}
		Chart chart = sheetData.getCharts().get(chartIndex).clone();
		chart.setAreaReference(areaRef);
		chart.setTitle(new Title(title));
		chart.setType(chartType);
		sheet.sendCommand(SheetCommands.setChart(chartIndex, chart)).getNonBlocking();
	}

	@POST
	@Path("/{workbook}/{sheetName}/{chart}/move")
	@Produces(MediaType.APPLICATION_JSON)
	public void move(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheetName") String sheetName,
			@PathParam("chart") int chartIndex, @FormParam("left") int left, @FormParam("top") int top,
			@FormParam("width") int width, @FormParam("height") int height) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		SheetData sheetData = sheet.receiveSheet().getNonBlocking();
		if (sheetData.getCharts() == null || chartIndex < 0 || chartIndex >= sheetData.getCharts().size()) {
			throw new NotFoundException("Chart with index " + chartIndex + " of sheet: " + sheetName + " not found");
		}
		Chart chart = sheetData.getCharts().get(chartIndex);
		chart.setLeft(left);
		chart.setTop(top);
		chart.setWidth(width);
		chart.setHeight(height);
		sheet.sendCommand(SheetCommands.setChart(chartIndex, chart)).getNonBlocking();

	}

	@DELETE
	@Path("/{workbook}/{sheetName}/{chart}")
	@Produces(MediaType.APPLICATION_JSON)
	public void delete(@PathParam("workbook") WorkbookId workbookName, @PathParam("sheetName") String sheetName,
			@PathParam("chart") int chartIndex) throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = getWorkbookProcessor().getWorkbook(workbookName).getSheet(sheetName);
		SheetData sheetData = sheet.receiveSheet().getNonBlocking();
		if (sheetData.getCharts() == null || chartIndex < 0 || chartIndex >= sheetData.getCharts().size()) {
			throw new NotFoundException("Chart with index " + chartIndex + " of sheet: " + sheetName + " not found");
		}
		sheet.sendCommand(SheetCommands.deleteChart(chartIndex)).getNonBlocking();
	}

}
