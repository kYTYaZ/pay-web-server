/**
 * 
 */
package com.wldk.framework.system.web.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

import com.wldk.framework.utils.SpringContextUtils;

/**
 * 全局变量定义类，使用Servlet的Listener机制，在Web容器启动的时候加载变量。<br>
 * 
 * @author Administrator
 * 
 */
public class GlobalParameterListener implements ServletContextListener {
	/** 应用程序根路径 */
	public final static String WEB_ROOT = "web.root";

	/** 应用程序的Web-info路径 */
	public final static String WEB_INFO = "web.info";

	/** 应用程序的类路径 */
	public final static String CLASSES_PATH = "classes.path";


	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 *      .ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		ServletContext context = event.getServletContext();
		// set Webapp Root
		WebUtils.setWebAppRootSystemProperty(context);
		try {
			String root = WebUtils.getRealPath(context, "");
			// System.out.println(root);
			System.setProperty(WEB_ROOT, root);
			System.setProperty(WEB_INFO, root
					+ System.getProperty("file.separator") + "WEB-INF");
			System.setProperty(CLASSES_PATH, System.getProperty(WEB_INFO)
					+ System.getProperty("file.separator") + "classes");
			ApplicationContext ac = WebApplicationContextUtils
					.getWebApplicationContext(context);
			SpringContextUtils.getInstance().initContext(ac);
			
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
