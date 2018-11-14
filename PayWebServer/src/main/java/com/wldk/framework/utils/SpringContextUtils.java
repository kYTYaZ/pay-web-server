package com.wldk.framework.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * title:获取Spring上下文容器的工具类，单例模式<br>
 */
public class SpringContextUtils {
	/** 单例对象 */
	private static SpringContextUtils instance = new SpringContextUtils();
	/** Spring上下文对象 */
	private ApplicationContext context;

	/**
	 * 构造方法
	 */
	private SpringContextUtils() {

	}

	/**
	 * 获取单例对象的静态方法
	 * 
	 * @return
	 */
	public static SpringContextUtils getInstance() {
		return instance;
	}

	/**
	 * 初始化Spring上下文对象
	 * 
	 * @param context
	 */
	public void initContext(ApplicationContext context) {
		this.context = context;
	}

	/**
	 * 获取Spring上下文对象
	 * 
	 * @return
	 */
	public ApplicationContext getContext() {
		return context;
	}

	/**
	 * 销毁Spring上下文对象
	 */
	public void destoryedContext() {
		if (context != null) {
			try {
				((FileSystemXmlApplicationContext) context).destroy();
			} catch (Exception e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * 获取一个指定名称的Bean对象 <Br>
	 * 
	 * @param beanId
	 *            Bean标识 <br>
	 * @return Object
	 */
	public Object getBean(String beanId) {
		Object obj = null;
		if (context != null) {
			obj = context.getBean(beanId);
		}
		return obj;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
