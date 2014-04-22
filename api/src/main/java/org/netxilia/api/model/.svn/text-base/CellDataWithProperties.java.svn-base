package org.netxilia.api.model;

import java.util.Collection;
import java.util.Collections;

import org.netxilia.api.model.CellData.Property;

import com.google.common.collect.ImmutableSet;

/**
 * This class is a tuple of {@link CellData} and the modified properties
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class CellDataWithProperties {
	private final CellData cellData;
	private final Collection<CellData.Property> properties;

	public CellDataWithProperties(CellData cellData) {
		this.cellData = cellData;
		this.properties = Collections.<CellData.Property> emptySet();
	}

	public CellDataWithProperties(CellData cellData, Collection<CellData.Property> properties) {
		this.cellData = cellData;
		this.properties = ImmutableSet.copyOf(properties);
	}

	public CellDataWithProperties(CellData cellData, Property property) {
		this.cellData = cellData;
		this.properties = ImmutableSet.of(property);
	}

	public CellData getCellData() {
		return cellData;
	}

	public Collection<CellData.Property> getProperties() {
		return properties;
	}

}
