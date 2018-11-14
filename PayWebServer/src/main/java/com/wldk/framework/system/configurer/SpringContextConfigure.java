/**
 * 
 */
package com.wldk.framework.system.configurer;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.wldk.framework.utils.SpringContextUtils;

/**
 * @author Administrator
 * 
 */
public class SpringContextConfigure {
	private static final String CONFIG_LOCATION_PARAM = "/config/testcase-spring.xml";

	/**
	 * 初始化方法实现
	 * 
	 * @throws Exception
	 */
	public static void initialized(String[] files) {
		System.out.append("Initialize Spring context...");
		String[] configs = null;
		try {
			if (files != null) {
				configs = (String[]) ArrayUtils.add(files, CONFIG_LOCATION_PARAM);
			} else {
				configs = new String[] { CONFIG_LOCATION_PARAM };
			}
			// 实例化Spring
			FileSystemXmlApplicationContext springContext = new FileSystemXmlApplicationContext(configs);
			// 初始化工具类
			SpringContextUtils.getInstance().initContext(springContext);
		} catch (Exception e) {
			System.err.println(e);
		}
//		System.out.println("Initialize Spring context is completed.");
	}

	/**
	 * 关闭Spring上下文
	 */
	public static void destroyed() {
		// TODO Auto-generated method stub
		System.out.append("Shutdown Spring context...");
		SpringContextUtils.getInstance().destoryedContext();
//		System.out.println("Shutdown Spring context is completed.");
	}

	public static void main(String[] args) throws Exception {
		SpringContextConfigure.initialized(null);
		SpringContextConfigure.destroyed();
	}
}
