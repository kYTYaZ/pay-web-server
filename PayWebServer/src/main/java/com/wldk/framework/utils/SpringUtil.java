package com.wldk.framework.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 用于无法得到ServletContext时，获取ApplicationContext对象
 * 
 * @author zhaodk
 */
public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		if(applicationContext == null){
			applicationContext = ac;
		}
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}
}
