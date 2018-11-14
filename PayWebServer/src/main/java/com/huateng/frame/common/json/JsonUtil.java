/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: JsonUtil.java
 * Author:   justin
 * Date:     2014-7-23 下午7:45:26
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.frame.common.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonParser;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.param.OutputParam;
import com.huateng.utils.GsonFactory;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.JSONUtils;

/**
 * json转换类
 * 该类提供常用的json转对象，对象转json工具  
 * @author sunguohua
 */
public class JsonUtil {
    private static JsonConfig jsonConfig = new JsonConfig();
    static{
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonValueProcessor() {
            private String process(Date value){
                return value == null ? "" : DateUtil.format(value,"yyyy-MM-dd HH:mm:ss");
            }
            
            public Object processObjectValue(String key, Object value, JsonConfig arg2) {
                return process((Date)value);
            }
            
            public Object processArrayValue(Object value, JsonConfig arg1) {
                return process((Date)value);
            }
        });
        
        jsonConfig.registerJsonValueProcessor(float.class, new JsonValueProcessor(){
            private String process(Float value){
                return String.valueOf(value.floatValue());
            }
            
            public Object processArrayValue(Object value, JsonConfig arg1) {
                return process((Float)value);
            }
            public Object processObjectValue(String key, Object value,
                    JsonConfig arg2) {
                return process((Float)value);
            }
        });
        
        JSONUtils.getMorpherRegistry().registerMorpher(new DateMorpher(new String[]{
                "yyyy-MM-dd HH:mm:ss"
        }));
    }
    
    public static String bean2Json(Object bean) {
        JSONObject jsobj = JSONObject.fromObject(bean);
        return jsobj.toString();
    }

    @SuppressWarnings("rawtypes")
    public static Object json2Bean(String json, Class cls) {
        JSONObject jsonObject = JSONObject.fromObject(json);
        return JSONObject.toBean(jsonObject, cls);
    }

    public static String list2json(List<?> list) {
        JSONArray jsonArray = JSONArray.fromObject(list,jsonConfig);
        return jsonArray.toString();
    }
    public static List json2list(String json, Class cls){
        JSONArray jsonArr = JSONArray.fromObject(json);
        List list = new ArrayList();
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject jsonObject = JSONObject.fromObject(jsonArr.getJSONObject(i));
            Object obj = JSONObject.toBean(jsonObject, cls);
            list.add(obj);
        }
        return list;
    }
    
    /**
     * 
     * json转outputParam对象
     * @param json
     * @return
     */
    public static OutputParam json2Output(String json){
        return (OutputParam) json2Bean(json,OutputParam.class);
    }
    
    /**
     * json转List<Map>
     * @param jsonStr
     * @return
     */
    public static List<Map<String, Object>> parseJSON2List(String jsonStr){  
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);  
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
        Iterator<JSONObject> it = jsonArr.iterator();  
        while(it.hasNext()){  
            JSONObject json2 = it.next();  
            list.add(parseJSON2Map(json2.toString(),2000));  
        }  
        return list;  
    }  
      
    /**
     *  json转map
     * @param jsonStr
     * @return
     */
    public static Map<String, Object> parseJSON2Map(String jsonStr,int deep){  
        Map<String, Object> map = new HashMap<String, Object>();  
        if (deep > 2000) {
			// 具体数值由虚拟机内存大小和每次递归堆栈大小决定，一般在2000次左右
			deep = 2000;
		}
		if (--deep < 0) {
			return map;
		}
        //最外层解析  
        JSONObject json = JSONObject.fromObject(jsonStr);  
        for(Object k : json.keySet()){  
            Object v = json.get(k);   
            //如果内层还是数组的话，继续解析  
            if(v instanceof JSONArray){  
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();  
                Iterator<JSONObject> it = ((JSONArray)v).iterator();  
                while(it.hasNext()){  
                    JSONObject json2 = it.next();  
                    list.add(parseJSON2Map(json2.toString(),2000));  
                }  
                map.put(k.toString(), list);  
            } else {  
                map.put(k.toString(), String.valueOf(v).trim()); 
            }  
        }  
        return map;  
    }  
    
    public static boolean isJson(String jsonStr){
    	if(StringUtils.isEmpty(jsonStr)){
			return false;	
		}
    	try {
    		new JsonParser().parse(jsonStr);
    		return true;
		} catch (Exception e) {
			return false;
		}
    }
    
  public static String objToJson(Object obj){
    	
    	return GsonFactory.getGson().toJson(obj);
    	
   }
}
