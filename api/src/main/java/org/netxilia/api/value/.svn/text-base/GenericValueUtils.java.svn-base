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
package org.netxilia.api.value;

import org.joda.time.ReadablePartial;
import org.netxilia.api.reference.AreaReference;

/**
 * Different conversion functions between IGenericValue and java types.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class GenericValueUtils {
	public static final StringValue EMTPY_STRING = new StringValue("");

	public static IGenericValue convert(IGenericValue value, GenericValueType returnType) {
		if (value == null) {
			return null;
		}
		if (value.getValueType() == returnType) {
			return value;
		}
		switch (returnType) {
		case BOOLEAN:
			return new BooleanValue(value.getBooleanValue());
		case NUMBER:
			return new NumberValue(value.getNumberValue());
		case STRING:
			return new StringValue(value.getStringValue());
		case DATE:
			return new DateValue(value.getDateValue());
		case ERROR:
			return new ErrorValue(ErrorValueType.NA);
		default:
			throw new IllegalArgumentException("Don't know how to convert to returnType: " + returnType);
		}
	}

	private static Double safeDouble(IGenericValue value) {
		Double n = value.getNumberValue();
		return n != null ? n : Double.valueOf(0);
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(IGenericValue value, Class<T> returnType) {
		if (value == null) {
			return null;
		}
		if (returnType.isAssignableFrom(value.getClass())) {
			return (T) value;
		}
		if (returnType == Boolean.class || returnType == boolean.class) {
			return (T) safeBoolean(value.getBooleanValue());
		}
		if (returnType == String.class) {
			return (T) value.getStringValue();
		}
		if (returnType == ReadablePartial.class) {
			return (T) value.getDateValue();
		}
		if (returnType == Integer.class || returnType == int.class) {
			return (T) Integer.valueOf(safeDouble(value).intValue());
		}
		if (returnType == Long.class || returnType == long.class) {
			return (T) Long.valueOf(safeDouble(value).longValue());
		}
		if (returnType == Float.class || returnType == float.class) {
			return (T) Float.valueOf(safeDouble(value).floatValue());
		}
		if (returnType == Double.class || returnType == double.class) {
			return (T) Double.valueOf(safeDouble(value).doubleValue());
		}
		if (returnType == AreaReference.class) {
			if (value instanceof ReferenceValue) {
				return (T) ((ReferenceValue) value).getReference();
			}
			return (T) new AreaReference(value.getStringValue());
		}
		throw new IllegalArgumentException("Don't know how to convert to returnType: " + returnType);
	}

	private static Boolean safeBoolean(Boolean booleanValue) {
		if (booleanValue == null) {
			return Boolean.FALSE;
		}
		return booleanValue;
	}

	public static Object valueAsObject(IGenericValue value) {
		if (value == null) {
			return null;
		}
		switch (value.getValueType()) {
		case BOOLEAN:
			return value.getBooleanValue();
		case NUMBER:
			return value.getNumberValue();
		case STRING:
			return value.getStringValue();
		case DATE:
			return value.getDateValue();
		case ERROR:
			return "#" + ((ErrorValue) value).getStringValue();
		default:
			throw new IllegalArgumentException("Don't know how to convert to object the type: " + value.getValueType());
		}
	}

	public static IGenericValue objectAsValue(Object object) {
		if (object == null) {
			return null;
		}
		if (object instanceof String) {
			return new StringValue((String) object);
		}
		if (object instanceof Double) {
			return new NumberValue((Double) object);
		}
		if (object instanceof Float) {
			return new NumberValue(((Float) object).doubleValue());
		}
		if (object instanceof Long) {
			return new NumberValue(((Long) object).doubleValue());
		}
		if (object instanceof Integer) {
			return new NumberValue(((Integer) object).doubleValue());
		}
		if (object instanceof Boolean) {
			return new BooleanValue((Boolean) object);
		}
		if (object instanceof ReadablePartial) {
			return new DateValue((ReadablePartial) object);
		}
		if (object instanceof ErrorValueType) {
			return new ErrorValue((ErrorValueType) object);
		}
		throw new IllegalArgumentException("Don't know how to convert to genericValue the type: "
				+ object.getClass().getCanonicalName());
	}

	public static IGenericValue rawStringToGeneric(String rawString, GenericValueType rawType) {
		if (rawString == null) {
			return null;
		}
		if (rawType == null) {
			throw new IllegalArgumentException("Cannot convert to generic with null rawType for: " + rawString);
		}
		switch (rawType) {
		case BOOLEAN:
			return new BooleanValue(rawString);
		case NUMBER:
			return new NumberValue(rawString);
		case STRING:
			return new StringValue(rawString);
		case DATE:
			return new DateValue(rawString);
		case ERROR:
			return new ErrorValue(rawString);
		default:
			throw new IllegalArgumentException("Don't know how to convert to object the type: " + rawType);

		}

	}

	/**
	 * if the given value is a {@link IDelegateValue} return the innermost IGenericValue. RichValue makes an exception
	 * because it will return the attributes, but the inner IGenericValue is dereferenced.
	 * 
	 * @param value
	 * @return
	 */
	public static IGenericValue deferenceValue(IGenericValue value) {
		IGenericValue derefValue = value;
		while (derefValue instanceof IDelegateValue) {
			if (derefValue instanceof RichValue) {
				RichValue richValue = (RichValue) derefValue;
				IGenericValue derefInnerValue = deferenceValue(richValue.getGenericValue());

				if (richValue.getGenericValue() != derefInnerValue) {
					derefValue = new RichValue(derefInnerValue, richValue.getDisplay(), richValue.getStyles());
				}
				break;
			} else {
				derefValue = ((IDelegateValue) derefValue).getGenericValue();
			}
		}
		return derefValue;
	}
}
