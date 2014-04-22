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
package org.netxilia.api;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.netxilia.api.model.SpanTable;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;

public class TestSpanTable {

	@Test
	public void testCreation() {
		SpanTable spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		Assert.assertEquals(1, spans.getColSpan(new CellReference("B1")));
		Assert.assertEquals(2, spans.getColSpan(new CellReference("B2")));
		Assert.assertEquals(1, spans.getRowSpan(new CellReference("B2")));
		Assert.assertEquals(-1, spans.getColSpan(new CellReference("C2")));
		Assert.assertEquals(1, spans.getColSpan(new CellReference("D2")));
	}

	@Test
	public void testMergeInside() {
		SpanTable spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		spans.toggleSpan(new AreaReference("C2:C2"));
		List<AreaReference> newSpans = spans.getSpans();
		Assert.assertEquals(0, newSpans.size());

		spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		spans.toggleSpan(new AreaReference("B2:B2"));
		newSpans = spans.getSpans();
		Assert.assertEquals(0, newSpans.size());

		spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		spans.toggleSpan(new AreaReference("B2:C2"));
		newSpans = spans.getSpans();
		Assert.assertEquals(0, newSpans.size());
	}

	@Test
	public void testMergeIntersect() {
		SpanTable spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		spans.toggleSpan(new AreaReference("C2:C3"));
		List<AreaReference> newSpans = spans.getSpans();
		Assert.assertEquals(1, newSpans.size());
		Assert.assertEquals(new AreaReference("B2:C3"), newSpans.get(0));

		spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		spans.toggleSpan(new AreaReference("A2:D2"));
		newSpans = spans.getSpans();
		Assert.assertEquals(1, newSpans.size());
		Assert.assertEquals(new AreaReference("A2:D2"), newSpans.get(0));

		spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2"), new AreaReference("D2:E2")));
		spans.toggleSpan(new AreaReference("A2:E2"));
		newSpans = spans.getSpans();
		Assert.assertEquals(1, newSpans.size());
		Assert.assertEquals(new AreaReference("A2:E2"), newSpans.get(0));

		spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2"), new AreaReference("D2:E2")));
		spans.toggleSpan(new AreaReference("B2:E2"));
		newSpans = spans.getSpans();
		Assert.assertEquals(1, newSpans.size());
		Assert.assertEquals(new AreaReference("B2:E2"), newSpans.get(0));
	}

	@Test
	public void testMergeDirect() {
		SpanTable spans = new SpanTable(Arrays.asList(new AreaReference("B2:C2")));
		spans.toggleSpan(new AreaReference("D2:E2"));
		List<AreaReference> newSpans = spans.getSpans();

		Assert.assertEquals(2, newSpans.size());
		Assert.assertTrue(newSpans.contains(new AreaReference("B2:C2")));
		Assert.assertTrue(newSpans.contains(new AreaReference("D2:E2")));
	}
}
