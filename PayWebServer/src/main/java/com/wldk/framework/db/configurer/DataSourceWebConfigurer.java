/**
 * 
 */
package com.wldk.framework.db.configurer;

import java.io.FileNotFoundException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.util.WebUtils;

/**
 * @author Administrator
 * 
 */
public class DataSourceWebConfigurer extends
		SimpleDataSourceConfigurer<ServletContext> {
	/** Parameter specifying the location of the datasource config file */
	public static final String CONFIG_LOCATION_PARAM = "dsConfigLocation";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.configurer.SimpleDataSourceConfigurer#getApplicationContext(java.lang.Object)
	 */
	
	public ApplicationContext getApplicationContext(ServletContext arg) {
		// TODO Auto-generated method stub
		return (ApplicationContext) WebApplicationContextUtils
				.getWebApplicationContext(arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.configurer.SimpleDataSourceConfigurer#getConfigFileURL(java.lang.Object)
	 */
	
	public URL getConfigFileURL(ServletContext arg)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		String location = arg.getInitParameter(CONFIG_LOCATION_PARAM);
		if (!ResourceUtils.isUrl(location)) {
			// Resolve system property placeholders before resolving
			// real path.
			location = SystemPropertyUtils.resolvePlaceholders(location);
			location = WebUtils.getRealPath(arg, location);
		}
		location = SystemPropertyUtils.resolvePlaceholders(location);
		URL url = ResourceUtils.getURL(location);
		// Write log message to log.
		log.info("DataSource Pool Configuration from [" + url + "]");
		return url;
	}
}
