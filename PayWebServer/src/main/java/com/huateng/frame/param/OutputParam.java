/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: OutputParam.java
 * Author:   sunguohua
 * Date:     2014-7-23 下午7:32:13
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.frame.param;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huateng.frame.common.json.JsonUtil;
import com.huateng.pay.common.util.StringUtil;

/**
 * 出参对象， 所有的接口返回的对象都是该对象
 * 
 * @author sunguohua
 */
public class OutputParam implements Serializable {
    private static final long serialVersionUID = -1740385367410423439L;
    /*
     * 返回的成功或失败码
     */
    private String returnCode;
    /*
     * 返回的成功或失败的提示信息
     */
    private String returnMsg;
    /*
     * 用于查询用返回的json格式
     */
    private String returnObjJson;
    
    private String requestUrl;
    
    
    public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	/*
     * 返回的list集合
     */
    private String returnListJson;
    
    public void setReturnListJson(String returnListJson){
        this.returnListJson = returnListJson;
    }
    
    public String getReturnListJson(){
        return this.returnListJson;
    }
    
    public void putReturnList(List<Map<String,Object>> list){
        this.returnListJson = JsonUtil.list2json(list);
    } 
    
    public String takeReturnListJson(){
        return this.returnListJson;
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> takeReturnList(){
        return JsonUtil.json2list(this.returnListJson, Map.class);
    }
    
    /*
     * 返回json转后的Map对象，通过key进行取值
     */
    public Map<String, Object> returnObj = new HashMap<String, Object>();

    /*
     * 返回json转后的Map对象，通过key进行取值
     */
    public Map<String, String> returnStr = new HashMap<String, String>();
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getReturnObj() {
        if(returnObj != null && returnObj.size()>0){
            return returnObj;
        }
        if (returnObjJson != null && !"".equals(returnObjJson)) {
            return (Map<String, Object>) JsonUtil.json2Bean(returnObjJson, Map.class);
        }
        return this.returnObj;
    }
    

    public void putValue(String key, Object value) {
        returnObj.put(key, value);
    }
    public void putValueRemoveNull(String key, Object value) {
    	if(!StringUtil.isEmpty(value)) {
    		returnObj.put(key, value);
    	}
    }
    
    public void putValueStr(String key, String value) {
    	returnStr.put(key, value);
    }
    
    
	public Map<String, String> getReturnStr() {
        if(returnStr != null && returnStr.size()>0){
            return returnStr;
        }
        return this.returnStr;
    }

    /**
     * 
     * 获取map对象数据
     * 
     * @param key
     * @return
     */
    public Object getValue(String key) {
        return returnObj.get(key);
    }
    
    /**
     * 
     * 获取map对象数据
     * 
     * @param key
     * @return
     */
    public String getValueString(String key) {
        return returnStr.get(key);
    }

    public void setReturnObj(Map<String, Object> returnObj) {
        this.returnObj = returnObj;
    }
    
    public void setReturnStr(Map<String, String> returnStr) {
        this.returnStr = returnStr;
    }
    
    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getReturnObjJson() {
        return returnObjJson;
    }

    public void setReturnObjJson(String returnObjJson) {
        this.returnObjJson = returnObjJson;
    }

	@Override
	public String toString() {
		return "OutputParam [requestUrl=" + requestUrl + ", returnCode="
				+ returnCode + ", returnListJson=" + returnListJson
				+ ", returnMsg=" + returnMsg + ", returnObj=" + returnObj
				+ ", returnObjJson=" + returnObjJson + ", returnStr="
				+ returnStr + "]";
	}
    
    
}
