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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huateng.pay.common.util.StringUtil;

/**
 * 
 * 接口入参 公用类 所有的webservice接口都要给该对象传值 工程中不要出现冗余的bo
 * 
 * @author sunguohua
 */
public class InputParam implements Serializable{
    private static final long serialVersionUID = 6536014145998714011L;
    /*
     * 后台的service
     */
    private String serviceName;
    /*
     * 后台service的方法
     */
    private String serviceMethod;
    /*
     * 后台方法需要的入参的值
     */
    private Map<String, Object> params = new HashMap<String, Object>();
    
    private Map<String, String> paramString = new HashMap<String, String>();
    
    private List<Map<String, String>> paramStrings = new ArrayList<Map<String, String>>();
    
    
    public List<Map<String, String>> getParamStrings() {
		return paramStrings;
	}
	public void setParamStrings(List<Map<String, String>> paramStrings) {
		this.paramStrings = paramStrings;
	}
    /**
     * 
     * 参数传map对象
     *
     * @param mapParam
     */
    public void putMap(Map<String,Object> mapParam){
        this.params = mapParam;
    }
    /**
     * 
     * 参数传map对象
     *
     * @param mapParam
     */
    public void putMapString(Map<String,String> paramString){
    	this.paramString = paramString;
    }
    
    /**
     * @param key 参数名
     * @param value 参数值
     */
    public void putParams(String key, Object value) {
        params.put(key, value);
    }
    /**
     * @param key 参数名
     * @param value 参数值
     */
    public void putparamString(String key,String value) {
    	paramString.put(key, value);
    }
    
    public void putparamStringRemoveNull(String key,String value) {
    	if(!StringUtil.isEmpty(value)) {
    		paramString.put(key, value);
    	}
    }
    
    public Map<String, String> getParamString() {
		return paramString;
	}
	public void setParamString(Map<String, String> paramString) {
		this.paramString = paramString;
	}
	public Map<String,Object> getParams(){
        return params;
    }
    
    public void setParams(Map<String,Object> params){
        this.params = params;
    }

    /**
     * 取参的方法
     * 
     * @param key 参数名
     * @return 返回Object对象
     */
    public Object getValue(String key) {
        return params.get(key);
    }
    /**
     * 取参的方法
     * 
     * @param key 参数名
     * @return 返回Object对象
     */
    public String getValueString(String key) {
        return paramString.get(key);
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getServiceMethod() {
        return this.serviceMethod;
    }
	@Override
	public String toString() {
		return "InputParam [serviceName=" + serviceName + ", serviceMethod=" + serviceMethod + ", params=" + params
				+ ", paramString=" + paramString + ", paramStrings=" + paramStrings + "]";
	}
    
    
    
}
