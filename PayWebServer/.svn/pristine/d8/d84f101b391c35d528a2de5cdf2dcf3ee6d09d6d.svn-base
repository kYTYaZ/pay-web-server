package com.wldk.framework.system.servlet;

import java.io.File;

import javax.servlet.http.HttpServlet;

import com.wldk.framework.utils.AppVars;
/**
 * 加载配置文件
 * @author Administrator
 *
 */
public class BaseActionServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 69172711043459909L;

	public void init() {
		String webappPath = getServletContext().getRealPath(File.separator);
		AppVars.getAppVars().setVar(AppVars.WEB_APP_REAL_PATH, webappPath);
		AppVars.getAppVars().setVars(webappPath + "/WEB-INF/classes/system.properties");
	}
}
