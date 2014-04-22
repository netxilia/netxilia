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
package org.netxilia.impexp.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.display.Style;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.utils.Pair;

/**
 * This class tries to approximate foreground and background colors with style names found in the given list of style
 * definitions.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class NetxiliaStyleResolver {
	private static final String COLOR_ATTRIBUTE = "color";
	private static final String BACKGROUNB_COLOR_ATTRIBUTE = "background-color";

	private final List<Pair<Style, Color>> foregroundColors = new ArrayList<Pair<Style, Color>>();
	private final List<Pair<Style, Color>> backgroundColors = new ArrayList<Pair<Style, Color>>();

	private final Map<Color, Style> foregrounds = new HashMap<Color, Style>();
	private final Map<Color, Style> backgrounds = new HashMap<Color, Style>();

	public NetxiliaStyleResolver(Collection<StyleDefinition> definitions) {
		for (StyleDefinition def : definitions) {
			String color = def.getAttribute(COLOR_ATTRIBUTE);
			if (color != null) {
				foregroundColors.add(new Pair<Style, Color>(def.getId(), buildColor(color)));
			}

			// XXX: if could be that the definition contains a full background entry
			String background = def.getAttribute(BACKGROUNB_COLOR_ATTRIBUTE);
			if (background != null) {
				backgroundColors.add(new Pair<Style, Color>(def.getId(), buildColor(background)));
			}
		}
	}

	private Color buildColor(String color) {
		return Color.decode(color);
	}

	public Style approximateForeground(int r, int g, int b) {
		if (r == 0 && g == 0 && b == 0) {
			// default foreground
			return null;
		}
		Color color = new Color(r, g, b);
		Style style = foregrounds.get(color);
		if (style != null) {
			return style;
		}
		Pair<Style, Color> closest = findSimilarColor(foregroundColors, color);
		if (closest == null) {
			return null;
		}
		foregrounds.put(closest.getSecond(), closest.getFirst());
		return closest.getFirst();
	}

	public Style approximateBackground(int r, int g, int b) {
		if (r == 255 && g == 255 && b == 255) {
			// default background
			return null;
		}
		Color color = new Color(r, g, b);
		Style style = backgrounds.get(color);
		if (style != null) {
			return style;
		}
		Pair<Style, Color> closest = findSimilarColor(backgroundColors, color);
		if (closest == null) {
			return null;
		}
		backgrounds.put(closest.getSecond(), closest.getFirst());
		return closest.getFirst();
	}

	private Pair<Style, Color> findSimilarColor(List<Pair<Style, Color>> colors, Color color) {
		Pair<Style, Color> result = null;
		double minColorDistance = Double.MAX_VALUE;
		for (Pair<Style, Color> styleColor : colors) {
			double colorDistance = colorDistance(styleColor.getSecond(), color);
			if (colorDistance < minColorDistance) {
				minColorDistance = colorDistance;
				result = styleColor;
			}
		}
		return result;
	}

	public double colorDistance(Color c1, Color c2) {
		double rmean = (c1.getRed() + c2.getRed()) / 2;
		int r = c1.getRed() - c2.getRed();
		int g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256;
		return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}

}
