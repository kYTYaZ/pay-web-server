package com.wldk.framework.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;

/**
 * JSON转换工具类
 * 
 * @author Administrator
 * 
 */
public class JsonUtil {
	private static JsonConfig jsonConfig = new JsonConfig();
	static {
		jsonConfig.registerJsonValueProcessor(Date.class,
				new JsonValueProcessor() {
					private String process(Date value) {
						if(value!=null){
							return "";
						}else{
							return DateUtils.formatime
									.format(value);
						}
					}

					public Object processObjectValue(String key, Object value,
							JsonConfig arg2) {
						return process((Date) value);
					}

					public Object processArrayValue(Object value,
							JsonConfig arg1) {
						return process((Date) value);
					}
				});

		jsonConfig.registerJsonValueProcessor(float.class,
				new JsonValueProcessor() {
					private String process(Float value) {
						return String.valueOf(value.floatValue());
					}

					public Object processArrayValue(Object value,
							JsonConfig arg1) {
						return process((Float) value);
					}

					public Object processObjectValue(String key, Object value,
							JsonConfig arg2) {
						return process((Float) value);
					}
				});

		JSONUtils.getMorpherRegistry().registerMorpher(
				new DateMorpher(new String[] { "yyyy-MM-dd HH:mm:ss" }));
	}

	/**
	 * 对象转换Json
	 * 
	 * @param bean
	 * @return
	 */
	public static String beanToJson(Object bean) {
		JSONObject jsobj = JSONObject.fromObject(bean);
		return jsobj.toString();
	}

	/**
	 * JSON转换对象
	 * 
	 * @param bean
	 * @return
	 */
	
	public static Object jsonToBean(String json, Class cls) {
		JSONObject jsonObject = JSONObject.fromObject(json);
		return JSONObject.toBean(jsonObject, cls);
	}

	/**
	 * list转换Json
	 * 
	 * @param list
	 * @return
	 */
	public static String listTojson(List<?> list) {
		JSONArray jsonArray = JSONArray.fromObject(list, jsonConfig);
		return jsonArray.toString();
	}

	/**
	 * JSON转换list
	 * 
	 * @param json
	 * @param cls
	 * @return
	 */
	
	public static List jsonTolist(String json, Class cls) {
		JSONArray jsonArr = JSONArray.fromObject(json);
		List list = new ArrayList();
		for (int i = 0; i < jsonArr.size(); i++) {
			JSONObject jsonObject = JSONObject.fromObject(jsonArr
					.getJSONObject(i));
			Object obj = JSONObject.toBean(jsonObject, cls);
			list.add(obj);
		}
		return list;
	}

}
