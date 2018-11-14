package com.wldk.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;

/**
 * 常用变量环境容器类
 * 
 * @author Administrator
 * 
 */
public class AppVars {
	private static AppVars env = new AppVars();

	private CheckHelper checkHelper = null;
	/** WEB应用真实目录 */
	public final static String WEB_APP_REAL_PATH = "web_app_real_path";

	public final static String CONTEXT_PATH = "context_path";

	public static final String ENCRYPTOR_BYTE_KEY = "cmsByteEncryptor";

	private boolean contextPathSeted = false;

	
	private Map vars = null;

	private HashMap<String, JSONObject> jsonMap = new HashMap<String, JSONObject>();

	public CheckHelper getCheckHelper() {
		return checkHelper;
	}

	public void setCheckHelper(CheckHelper checkHelper) {
		this.checkHelper = checkHelper;
	}

	
	private AppVars() {
		vars = new HashMap();
	}

	public AppVars(ServletContext servletContext) {
	}
	

	public static AppVars getAppVars() {
		return env;
	}

	public String getContextPath() {
		return vars.get(CONTEXT_PATH).toString();
	}

	
	public void setContextPath(HttpServletRequest req) {
		if (!contextPathSeted) {
			vars.put(CONTEXT_PATH, req.getContextPath());
			contextPathSeted = true;
		}
	}

	
	public Map getVars() {
		return vars;
	}

	
	public void setVar(Object key, Object value) {
		vars.put(key, value);
	}

	
	public <T> T getVar(Object key) {
		return (T) vars.get(key);
	}

	public String getVarStr(Object key) {
		return (String) (vars.get(key));
	}

	/**
	 * 添加多个配置
	 * 
	 * @param cfgFile
	 */
	
	public void setVars(String cfgFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(cfgFile), "GBK"));
			String l = null;
			while ((l = br.readLine()) != null) {
				if (l.length() == 0 || l.charAt(0) == '#')
					continue;
				int nx = l.indexOf("=");
				if (nx != -1)
					vars.put(l.substring(0, nx), l.substring(nx + 1));
			}
			br.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 添加json配置,此处将用json的文件名作为key
	 * 
	 * @param jsonFile
	 */
	public void setJson(String jsonFile) {
		try {
			File jf = new File(jsonFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(jf), "GBK"));
			StringBuffer sb = new StringBuffer();
			String l = null;
			while ((l = br.readLine()) != null)
				sb.append(l).append(" ");
			String name = jf.getName();
			String str = sb.toString();
			br.close();
			JSONObject jobj = JSONObject.fromObject(str);
			jsonMap.put(name, jobj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据json文件名，获取JSONObject
	 * 
	 * @param fileName
	 * @return
	 */
	public JSONObject getJson(String fileName) {
		return jsonMap.get(fileName);
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer(StringUtil.LINE + "[AppVars]" + StringUtil.LINE);
		sb.append("<-----------------------------------------------------------------");
		sb.append(StringUtil.LINE);
		for (Iterator it = vars.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			sb.append(key).append("=").append(vars.get(key)).append(StringUtil.LINE);
		}
		sb.append("-----------------------------------------------------------------");
		sb.append(StringUtil.LINE).append("[JSON-DATA]").append(StringUtil.LINE);
		for (Iterator it = jsonMap.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			sb.append(key).append("=").append(jsonMap.get(key)).append(StringUtil.LINE);
		}
		sb.append("----------------------------------------------------------------->");
		return sb.toString();
	}
}