/**
 * 
 */
package com.wldk.framework.db.web.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.wldk.framework.db.configurer.DataSourceConfigurer;
import com.wldk.framework.db.configurer.DataSourceWebConfigurer;

/**
 * @author Administrator
 * 
 */
public class DataSourceConfigListener implements ServletContextListener {
	private DataSourceConfigurer<ServletContext> configurer;

	/**
	 * 构造方法
	 */
	public DataSourceConfigListener() {
		configurer = new DataSourceWebConfigurer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub
		if (configurer != null) {
			try {
				configurer.shutdownDataSource(event.getServletContext());
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent event) {
		// TODO Auto-generated method stub
		if (configurer != null) {
			try {
				configurer.initDataSource(event.getServletContext());
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

}
