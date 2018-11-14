/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: BaseAction.java
 * Author:   sunguohua
 * Date:     2014-7-25 下午12:52:12
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.huateng.pay.common.http.HttpRequestClient;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * action父类，提供常用的web对象获取
 * 
 * @author sunguohua
 */
public class BaseAction extends ActionSupport {
    private static final long serialVersionUID = -3212009413350211461L;
    private Logger logger = LoggerFactory.getLogger(BaseAction.class);

    protected HttpServletRequest request = getRequest();
    
    protected HttpServletResponse response = getResponse();
    
    protected  Map<String,Object> parameter =new HashMap<String, Object>();
    
    protected Map<String,String> requestParam=new HashMap<String, String>();
    

    public Map<String, Object> getParameter() {
		return parameter;
	}

	public void setParameter(Map<String, Object> parameter) {
		this.parameter = parameter;
	}

	protected HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    protected HttpServletResponse getResponse() {
        return ServletActionContext.getResponse();
    }

    protected Map<String, String> getParamaters() {
        Map<?, ?> map = getRequest().getParameterMap();
        Set<?> keSet = map.entrySet();
        Map<String, String> paramMap = new HashMap<String, String>();
        for (Iterator<?> itr = keSet.iterator(); itr.hasNext();) {
            @SuppressWarnings("rawtypes")
            Map.Entry me = ((Map.Entry) itr.next());
            Object ok = me.getKey();
            Object ov = me.getValue();
            String[] value = new String[1];
            if (ov instanceof String[]) {
                value = (String[]) ov;
            } else {
                value[0] = ov.toString();
            }
            for (int k = 0; k < value.length; k++) {
                paramMap.put((String) ok, value[k]);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("getParamaters : " + paramMap.toString());
        }

        return paramMap;
    }

    protected HttpSession getSession() {
        return getRequest().getSession();
    }

    protected HttpSession getSession(boolean arg0) {
        return getRequest().getSession(arg0);
    }

    protected ServletContext getServletContext() {
        return ServletActionContext.getServletContext();
    }

    protected ValueStack getValueStack() {
        return ServletActionContext.getValueStack(getRequest());
    }
    /**
     * 请求重定向
     * @param str
     */
    protected void send(String str) {
        try {
            HttpServletResponse response = getResponse();
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(str);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            logger.error("method[sendJson] send json to client error", e);
        }
    }
    
    /**
     * 请求重定向
     * @param str
     */
    protected void send(PrintWriter writer, String str) {
		try {
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
    }
    
    /**
     * 后台通知商户
     * @param backEndUrl
     * @param map
     */
    public void backNotifyMer(String backEndUrl,String respStr){
    	Map<String,String> map  = new HashMap<String, String>();
    	map.put("resp",respStr);  	
    	if(backEndUrl.contains("https")){
            HttpRequestClient.httpsRequest(backEndUrl, map);
    	}else{
    		HttpRequestClient.httpRequest(backEndUrl, map);
    	}
    }
    protected Object getBean(String name) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils
                .getRequiredWebApplicationContext(ServletActionContext.getServletContext());
        return webApplicationContext.getBean(name);
    }

    /**
     * 获取request参数并转化Map
     * @param request
     * @return
     */
    public Map<String, String> getAllRequestParam(){
    	logger.debug("开始 [ request.getParameterNames 转换 Map<String, String>]");
    	requestParam = new HashMap<String,String>();
        Enumeration temp = request.getParameterNames();
      if (null != temp) {
        while (temp.hasMoreElements()) {
          String en = (String)temp.nextElement();
          String value = request.getParameter(en);
          logger.info("[ ky"+en+",value:"+value+" ]");
          requestParam.put(en,value );
          if ((requestParam.get(en) == null) || (requestParam.get(en) == ""))
          {
        	  requestParam.remove(en);
          }
        }
      }
      logger.debug("完成 [ request.getParameterNames 转换 Map<String, String>]");
      return requestParam;
    }  
}
