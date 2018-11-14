/**
 * 
 */
package com.wldk.framework.db.configurer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.context.ApplicationContext;

import com.wldk.framework.utils.SpringContextUtils;

/**
 * 解析加载config/ds-config.xml配置文件
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-11-14 <br>
 * Author: Administrator <br>
 * 
 */
public class DataSourceLocalConfigurer extends SimpleDataSourceConfigurer<Void> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.configurer.SimpleDataSourceConfigurer#getApplicationContext(java.lang.Object)
	 */

	public ApplicationContext getApplicationContext(Void arg) {
		// TODO Auto-generated method stub
		return SpringContextUtils.getInstance().getContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.configurer.SimpleDataSourceConfigurer#getConfigFileURL(java.lang.Object)
	 */

	public URL getConfigFileURL(Void arg) throws FileNotFoundException {
		// TODO Auto-generated method stub
		File file = new File("config/ds-config.xml");
		if (file.exists()) {
			try {
				return file.toURI().toURL();
			} catch (MalformedURLException e) {
				return null;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
	}
}
