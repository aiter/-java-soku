package com.youku.soku.newext.util;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.json.JSONArray;
import org.json.JSONObject;


public class JSONUtil {

	static Log logger = LogFactory.getLog(JSONUtil.class);

	public static class ParseResult {
		public String input;
		public boolean valid;
		public JSONObject object;
	}

	public static ParseResult tryParse(String jsonString) {

		ParseResult result = new ParseResult();
		result.input = jsonString;

		if (jsonString == null || jsonString.trim().length() == 0) {
			return result;
		}

		try {
			JSONObject object = new JSONObject(jsonString);

			result.valid = true;
			result.object = object;

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return result;
	}

	public static void encodeAscii(JSONObject object) {
		Iterator<String> keys = object.keys();
		while (keys.hasNext()) {
			try {
				String key = keys.next();
				Object value = object.get(key);
				if (value instanceof JSONObject) {
					encodeAscii((JSONObject) value);

				} else if (value instanceof JSONArray) {
					encodeAscii((JSONArray) value);

				} else {
					object.put(key, StringUtil.toAscii(value.toString()));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static void encodeAscii(JSONArray array) {

		for (int i = 0; i < array.length(); i++) {
			try {
				Object value = array.get(i);
				if (value instanceof JSONObject) {
					encodeAscii((JSONObject) value);

				} else if (value instanceof JSONArray) {
					encodeAscii((JSONArray) value);

				} else {
					array.put(i, StringUtil.toAscii(value.toString()));
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	public static JSONObject newIfNull(JSONObject object) {
		if (object == null) {
			return new JSONObject();
		}

		return object;
	}

	public static Object jsonNullIfNull(Object value) {
		if (value == null) {
			return JSONObject.NULL;
		}

		return value;
	}

	/**
	 * 如果参数为null、空对象，返回""；
	 * 
	 * 否则，把参数中的空对象属性设置为""，然后返回修改后的对象；
	 * 
	 * 否则，返回原对象
	 * 
	 */
	public static Object filterEmptyObjectAndProps(JSONObject object) {
		if (object == null || object.length() == 0) {
			return "";
		}

		filter(object);

		return object;
	}

	/**
	 * 把给定的JSONObject中的空对象属性用空字符串替换
	 * 
	 * @return true 如果给定的JSONObject参数被修改
	 */
	public static boolean filter(JSONObject old) {

		if (old == null) {
			return false;
		}

		boolean changed = false;

		try {
			for (Iterator<String> i = old.keys(); i.hasNext();) {
				String key = i.next();
				Object value = old.opt(key);
				if (value instanceof JSONObject) {
					JSONObject object = (JSONObject) value;
					if (object.length() == 0) {
						old.put(key, "");
						changed = true;
					} else {
						changed = filter(object) || changed;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return changed;
	}

	public static boolean isEmpty(JSONObject jsonObject) {
		return jsonObject == null || jsonObject.length() == 0;
	}

	public static boolean isEmpty(JSONArray jsonArray) {
		return jsonArray == null || jsonArray.length() == 0;
	}

	public static boolean isEmpty(JSONObject parent, String property) {
		if (property == null || isEmpty(parent) || !parent.has(property)) {
			return true;
		}

		Object value;
		if ((value = parent.optJSONObject(property)) != null) {
			return isEmpty((JSONObject) value);
		}

		if ((value = parent.optJSONArray(property)) != null) {
			return isEmpty((JSONArray) value);
		}

		return false;
	}

	public static <T> T getProperty(JSONObject parent, Class<T> target,
			String property) {

		if (parent == null || property == null) {
			return null;
		}

		Object value = parent.opt(property);
		if (value == null) {
			return null;
		}

		if (target.isAssignableFrom(value.getClass())) {
			return target.cast(value);
		}

		if (String.class.equals(target)) {
			return target.cast(String.valueOf(value));
		}

		try {
			if (Byte.class.equals(target)) {
				return target.cast(Byte.valueOf(String.valueOf(value)));
			}
			if (Short.class.equals(target)) {
				return target.cast(Short.valueOf(String.valueOf(value)));
			}
			if (Integer.class.equals(target)) {
				return target.cast(Integer.valueOf(String.valueOf(value)));
			}
			if (Long.class.equals(target)) {
				return target.cast(Long.valueOf(String.valueOf(value)));
			}
			if (Float.class.equals(target)) {
				return target.cast(Float.valueOf(String.valueOf(value)));
			}
			if (Double.class.equals(target)) {
				return target.cast(Double.valueOf(String.valueOf(value)));
			}
		} catch (Exception e) {
		}

		return null;
	}

	public static <T> T getProperty(JSONArray parent, Class<T> target, int index) {

		if (parent == null || index < 0 || index > parent.length() - 1) {
			return null;
		}

		Object value = parent.opt(index);
		if (value == null) {
			return null;
		}

		if (target.isAssignableFrom(value.getClass())) {
			return target.cast(value);
		}

		if (String.class.equals(target)) {
			return target.cast(String.valueOf(value));
		}

		try {
			if (Byte.class.equals(target)) {
				return target.cast(Byte.valueOf(String.valueOf(value)));
			}
			if (Short.class.equals(target)) {
				return target.cast(Short.valueOf(String.valueOf(value)));
			}
			if (Integer.class.equals(target)) {
				return target.cast(Integer.valueOf(String.valueOf(value)));
			}
			if (Long.class.equals(target)) {
				return target.cast(Long.valueOf(String.valueOf(value)));
			}
			if (Float.class.equals(target)) {
				return target.cast(Float.valueOf(String.valueOf(value)));
			}
			if (Double.class.equals(target)) {
				return target.cast(Double.valueOf(String.valueOf(value)));
			}
		} catch (Exception e) {
		}

		return null;
	}

	public static <T> T getProperty(JSONObject parent, Class<T> target,
			String... property) {

		if (parent == null || property == null || property.length == 0) {
			return null;
		}

		for (int i = 0; i < property.length - 1; i++) {
			parent = parent.optJSONObject(property[i]);
			if (parent == null) {
				return null;
			}
		}

		return getProperty(parent, target, property[property.length - 1]);
	}

	public static boolean contain(JSONArray jsonArray, String value) {
		if (isEmpty(jsonArray) || value == null) {
			return false;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			Object item = jsonArray.opt(i);
			if (item instanceof String) {
				if (value.equals(item)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String join(JSONArray array, String token) {
		return join(array, token, -1);
	}

	/**
	 * @param count
	 *            如果count小于0，表示使用全部的JSONArray元素；如果count等于0，返回""；如果大于0，返回count个JSONArray元素
	 */
	public static String join(JSONArray array, String token, int count) {

		if (count == 0 || isEmpty(array)) {
			return "";
		}

		if (token == null) {
			token = "";
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length() && (count < 0 || i < count); i++) {
			String s = array.optString(i);

			if (i > 0) {
				builder.append(token);
			}
			builder.append(s);
		}

		return builder.toString();
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		ParseResult result = JSONUtil.tryParse("");
		System.out.println(result.valid);
		System.out.println(result.object);

		JSONObject object = new JSONObject();
		object.put("001", "");
		object.put("002", "002东方时空");
		object.put("004", new JSONObject());
		object.put("005", new JSONObject().put("_001", false).put("_002",
				new JSONObject()));
		object.put("006", new JSONObject());

		System.out.println(object.toString(4));
		filter(object);
		System.out.println(object.toString(4));

		System.out.println("---------------------");
		object = new JSONObject();
		System.out.println(object.toString(4));

	}

	/**
	 * @param optJSONArray
	 * @return
	 */
	public static JSONArray parsePerson(JSONArray personJSONArray) {
		if(isEmpty(personJSONArray)){
			return null;
		}
		JSONArray tmpArray = new JSONArray();
		for (int i = 0; i < personJSONArray.length(); i++) {
			Object tmp = personJSONArray.opt(i);
			if(tmp instanceof JSONObject){
				tmpArray.put(((JSONObject)tmp).opt("name"));
			}else {
				tmpArray.put(tmp.toString());
			}
		}
		return tmpArray;
	}
}
