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
package org.netxilia.api.impl.storage.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.storage.IJsonSerializer;
import org.springframework.beans.factory.InitializingBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

/**
 * Json serialization using GSON.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class GsonSerializerImpl implements IJsonSerializer, InitializingBean {
	private Gson gson;

	@Override
	public <T> T deserialize(Class<T> clazz, String jsonString) throws StorageException {
		return gson.fromJson(jsonString, clazz);
	}

	@Override
	public Object deserialize(Type type, String jsonString) throws StorageException {
		return gson.fromJson(jsonString, type);
	}

	@Override
	public String serialize(Object object) throws StorageException {
		return gson.toJson(object);
	}

	private Gson buildGson() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		// gsonBuilder.setExclusionStrategies(new AnnExclusionStrategy());
		// TODO use a more dynamic way or String config
		gsonBuilder.registerTypeAdapter(AreaReference.class, new StringAdapter<AreaReference>(AreaReference.class));
		gsonBuilder.registerTypeAdapter(CellReference.class, new StringAdapter<CellReference>(CellReference.class));
		gsonBuilder.registerTypeAdapter(Styles.class, new ValueOfStringAdapter<Styles>(Styles.class));
		gsonBuilder.registerTypeAdapter(Alias.class, new StringAdapter<Alias>(Alias.class));
		gsonBuilder.registerTypeAdapter(Formula.class, new StringAdapter<Formula>(Formula.class));

		Type columnDataListType = new TypeToken<List<ColumnData>>() {
		}.getType();
		gsonBuilder.registerTypeAdapter(columnDataListType, new ColumnDataListDeserializer());

		gsonBuilder.registerTypeAdapter(List.class, new ListCreator());

		Gson gson = gsonBuilder.create();
		return gson;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		gson = buildGson();
	}

	/**
	 * Serialize and deserialize a class directly from a String.
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 * @param <BoundType>
	 */
	public static class StringAdapter<BoundType> implements JsonSerializer<BoundType>, JsonDeserializer<BoundType> {
		private final Constructor<BoundType> constructor;

		public StringAdapter(Class<BoundType> clazz) {
			try {
				constructor = clazz.getConstructor(String.class);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public JsonElement serialize(BoundType src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}

		@Override
		public BoundType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			try {
				return constructor.newInstance(json.getAsString());
			} catch (Exception e) {
				throw new JsonParseException(e);
			}
		}

	}

	/**
	 * Serialize and deserialize a class directly from a String.
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 * @param <BoundType>
	 */
	public static class ValueOfStringAdapter<BoundType> implements JsonSerializer<BoundType>,
			JsonDeserializer<BoundType> {
		private final Method valueOfMethod;

		public ValueOfStringAdapter(Class<BoundType> clazz) {
			try {
				valueOfMethod = clazz.getMethod("valueOf", String.class);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public JsonElement serialize(BoundType src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}

		@SuppressWarnings("unchecked")
		@Override
		public BoundType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {

			try {
				return (BoundType) valueOfMethod.invoke(null, json.getAsString());
			} catch (Exception e) {
				throw new JsonParseException(e);
			}
		}

	}

	public static class ListCreator implements InstanceCreator<List<?>> {

		@SuppressWarnings("rawtypes")
		@Override
		public List<?> createInstance(Type type) {
			return new ArrayList();
		}

	}

	public static class ColumnDataListDeserializer implements JsonDeserializer<List<ColumnData>> {

		@Override
		public List<ColumnData> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonArray array = json.getAsJsonArray();
			List<ColumnData> columns = new ArrayList<ColumnData>(array.size());
			for (int i = 0; i < array.size(); ++i) {
				JsonObject jsonColumn = array.get(i).getAsJsonObject();
				JsonElement jsonWidth = jsonColumn.get(ColumnData.Property.width.name());
				JsonElement jsonStyles = jsonColumn.get(ColumnData.Property.styles.name());
				columns.add(new ColumnData(i, jsonWidth != null ? jsonWidth.getAsInt() : 0, jsonStyles != null ? Styles
						.valueOf(jsonStyles.getAsString()) : null));
			}
			return columns;
		}
	}

}
