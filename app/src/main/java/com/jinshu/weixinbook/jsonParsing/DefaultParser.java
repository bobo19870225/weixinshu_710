package com.jinshu.weixinbook.jsonParsing;

import android.text.TextUtils;


import com.jinshu.weixinbook.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class DefaultParser implements IParser {

	private final String mTag = this.getClass().getSimpleName();

	private boolean isEqualString(String one, String two) {
 		if (one == null || two == null || one.length() != two.length()) {

			// return false;
		}

		for (int i = 0; i < one.length(); i++) {
 			// two.charAt(i));
			if (i < two.length() && one.charAt(i) != two.charAt(i)) {
				return false;
			}

		}

		return true;
	}

	@Override
	public void parseJSONObject(JSONObject jsonObj, Object instance) {

		if (jsonObj == null) {
			return;
		}

		Field[] fields = instance.getClass().getFields();
		if (fields == null) {
			return;
		}
		for (int i = 0; i < fields.length; i++) {
			boolean hasAnnotated = fields[i].isAnnotationPresent(JSONKey.class);
			if (hasAnnotated) {
				JSONKey annotation = fields[i].getAnnotation(JSONKey.class);
				String[] keys = annotation.keys();
				Class<?> targetType = annotation.type();
				if (keys == null || targetType == null) {
					continue;
				}

				
				for (String key : keys) {
					
					Object value = null;
					if (key.contains(".")) {
						String notes[] = key.split("\\.");
						JSONObject jObj = (JSONObject) ParseHelper.getLastObj(jsonObj, key);
						
						value = jObj.opt(notes[notes.length - 1]);
						if (value instanceof JSONObject) {
							value = parseJSONObjectForResult((JSONObject) value,
									fields[i].getType());
						}
					} else {
						value = jsonObj.opt(key);

						if (value == null) {
							continue;
						}

						if (value instanceof JSONObject) {
							value = parseJSONObjectForResult((JSONObject) value,
									fields[i].getType());
						} else if (value instanceof JSONArray) {
//							Type type = fields[i].getGenericType();
//							if (type instanceof ParameterizedType) {
//								value = parseListObjectForResult(
//										(ParameterizedType) type, (JSONArray) value);
//							} else if (fields[i].getType().isArray()) {
//								Class<?> componentType = fields[i].getType()
//										.getComponentType();
//								value = parseArrayObjectForResult(
//										(JSONArray) value, componentType);	
//							}
							
							value = parseListObjectForResult(
									(JSONArray) value, targetType);
							setFieldValueByType(instance, fields[i], value,
									fields[i].getType());
							continue;
						}
					}
					

					try {
						if (value == null) {
							continue;
						}
						setFieldValueByType(instance, fields[i], value,
								targetType);
					} catch (IllegalArgumentException e) {
						if (null != e) {
//							LogUtil.e(mTag, e.getMessage(), e);
						}
					} catch (Exception e) {
						if (null != e) {
//							LogUtil.e(mTag, e.getMessage(), e);
						}
					}
				}
			}
		}
	}
	
	
	private Object parseListObjectForResult(JSONArray array,
											Class<?> componentType) {
		int size = array.length();
		//Object objects = Array.newInstance(componentType, size);
		List list = new ArrayList();
		for (int j = 0; j < size; j++) {
			Object value = null;
			try {
				value = array.get(j);
			} catch (JSONException e) {
				if (null != e) {
//					LogUtil.e(mTag, e.getMessage(), e);
				}
			}
			
			if (value instanceof JSONObject) {
				list.add(parseJSONObjectForResult((JSONObject) value,
						componentType));
			} else {
				//Array.set(objects, j, value);
				list.add(value);
			}
		}
		
		return list;

	}

	@Override
	public void parseJSONString(String json, Object instance) {
		if (TextUtils.isEmpty(json)) {
			return;
		}
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(json);
		} catch (JSONException e) {
			if (null != e) {
//				LogUtil.e(mTag, e.getMessage(), e);
			}
		}
		parseJSONObject(jsonObj, instance);
	}

	private Object parseJSONObjectForResult(JSONObject jsonObj, Class<?> cls) {

		if (jsonObj == null || cls == null) {
			return null;
		}
		Object obj = null;
		try {
			//LogUtil.d("name = " + cls.getSimpleName());
			if (cls.getSimpleName().equalsIgnoreCase("Integer")) {
				obj = new Integer(0);
			} else if (cls.getSimpleName().equalsIgnoreCase("Float")) {
				obj = new Float(0);
			} else {
				obj = cls.newInstance(); 
			}
			
			
		} catch (InstantiationException e1) {
			if (null != e1) {
//				LogUtil.e(mTag, e1.getMessage(), e1);
			}
		} catch (IllegalAccessException e1) {
			if (null != e1) {
//				LogUtil.e(mTag, e1.getMessage(), e1);
			}
		}
		Field[] fields = cls.getFields();
		if (fields == null) {
			return null;
		}
		for (int i = 0; i < fields.length; i++) {
			boolean hasAnnotated = fields[i].isAnnotationPresent(JSONKey.class);
			if (hasAnnotated) {
				Class<?> targetType = fields[i].getAnnotation(JSONKey.class)
						.type();
				String[] keys = fields[i].getAnnotation(JSONKey.class)
						.keys();
				if (keys == null) {
					continue;
				}
				for (String key : keys) {
					if (jsonObj.isNull(key)) {
						continue;
					}
					Object value = jsonObj.opt(key);
					if (value == null) {
						continue;
					}
					if (value instanceof JSONObject) {
						value = parseJSONObjectForResult(
								(JSONObject) value, fields[i].getType());
					} else if (value instanceof JSONArray) {
//						Type type = fields[i].getGenericType();
//						if (type instanceof ParameterizedType) {
//							value = parseListObjectForResult(
//									(ParameterizedType) type,
//									(JSONArray) value);
//						} else if (fields[i].getType().isArray()) {
//							Class<?> componentType = fields[i].getType()
//									.getComponentType();
//							value = parseArrayObjectForResult(
//									(JSONArray) value, componentType);
//						}
						
						value = parseListObjectForResult(
								(JSONArray) value, targetType);
						//setFieldValueByType(instance, fields[i], value,
							//	fields[i].getType());
					}
					try {
						if (value == null) {
							continue;
						}
						setFieldValueByType(obj, fields[i], value,
								targetType);

					} catch (IllegalArgumentException e) {
						if (null != e) {
//							LogUtil.e(mTag, e.getMessage(), e);
						}
					} catch (Exception e) {
						if (null != e) {
//							LogUtil.e(mTag, e.getMessage(), e);
						}
					}
				}
			}
		}
		return obj;
	}

	private List<Object> parseListObjectForResult(ParameterizedType type,
												  JSONArray array) {
		Type[] types = ((ParameterizedType) type).getActualTypeArguments();
		Class<?> subCls = (Class<?>) types[0];

		int length = array.length();
		List<Object> list = new ArrayList<Object>();
		for (int j = 0; j < length; j++) {
			Object item = null;
			try {
				item = array.get(j);
			} catch (JSONException e) {
				if (null != e) {
//					LogUtil.e(mTag, e.getMessage(), e);
				}
			}
			if (item instanceof JSONObject) {
				list.add(parseJSONObjectForResult((JSONObject) item, subCls));
			} else {
				list.add(item);
			}
		}
		return list;

	}

	private void setFieldValueByType(Object instance, Field field,
									 Object value, Class<?> targetClazz) {
		if (null == field || null == value || null == targetClazz) {
			return;
		}
		try {
			if (targetClazz.equals(value.getClass())) {
				field.set(instance, value);
			} else {
				setFieldValueByConversionType(instance, field, value,
						targetClazz);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void setFieldValueByConversionType(Object instance, Field field,
											   Object value, Class<?> targetClazz) {
		// 枚举转换
		try {
			if (targetClazz.isEnum()) {
				// TODO　枚举类型的处理

				// if (targetClazz.equals(BOOKSOURCE.class)) {
				// // default value 0
				// int valueInt = 0;
				// try {
				// valueInt = Integer.valueOf(value.toString());
				// } catch (Exception e) {
				// }
				// BOOKSOURCE bs = BOOKSOURCE.getIntanceByValue(valueInt);
				// field.set(instance, bs);
				// return;
				// }

			}

			// 基本类型转换
			// TODO : 可以用基本数据类型再次区分一下，少判断几次。
			String valuestr = String.valueOf(value);
			if (targetClazz.equals(int.class)) {
				field.set(instance, StringUtils.toInt(valuestr, 0));
				return;
			}

			if (targetClazz.equals(Integer.class)) {
				field.set(instance, StringUtils.toInt(valuestr, 0));
				return;
			}

			if (targetClazz.equals(Long.class)) {
				field.set(instance, StringUtils.toLong(valuestr));

				return;
			}

			if (targetClazz.equals(Double.class)) {
				field.set(instance, StringUtils.toDouble(valuestr));
				return;
			}

			if (targetClazz.equals(Character.class)) {
				char def = 0;
				field.set(instance, StringUtils.str2Character(valuestr));
				return;
			}

			if (targetClazz.equals(Byte.class)) {
				Byte def = 0;
				field.set(instance, StringUtils.str2Byte(valuestr, def));
				return;
			}

			if (targetClazz.equals(Boolean.class)) {
				if (valuestr.equals("1")) {
					valuestr = "true";
				} else if (valuestr.equals("2")) {
					valuestr = "false";
				}
				field.set(instance, StringUtils.toBool(valuestr));
				return;
			}
			if (targetClazz.equals(Short.class)) {
				short def = 0;
				field.set(instance, StringUtils.str2Short(valuestr, def));
				return;
			}
			if (targetClazz.equals(Float.class)) {
				field.set(instance, StringUtils.str2Float(valuestr, 0f));
				return;
			}

			// 字符串类型转换
			if (targetClazz.equals(String.class)) {
				field.set(instance, String.valueOf(valuestr));
				return;
			}
			
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
			return;
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
			return;
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
			return;
		}
		// 其他对象 ,主要用于对象嵌套。
		try {
			field.set(instance, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private Object parseArrayObjectForResult(JSONArray array,
											 Class<?> componentType) {
		int size = array.length();
		Object objects = Array.newInstance(componentType, size);
		for (int j = 0; j < size; j++) {
			Object value = null;
			try {
				value = array.get(j);
			} catch (JSONException e) {
				if (null != e) {
//					LogUtil.e(mTag, e.getMessage(), e);
				}
			}
			if (value instanceof JSONObject) {
				Array.set(
						objects,
						j,
						parseJSONObjectForResult((JSONObject) value,
								componentType));
			} else {
				Array.set(objects, j, value);
			}
		}
		return objects;

	}
}
