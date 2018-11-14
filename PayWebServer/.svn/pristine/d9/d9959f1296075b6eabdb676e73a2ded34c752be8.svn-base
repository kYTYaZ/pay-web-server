/**
 * 
 */
package com.wldk.framework.web.taglib;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class Functions {
	protected static Logger log = LoggerFactory.getLogger(Functions.class);

	public Functions() {
	}

	/** 判断集合中是否存在指定对象的函数 */
	public static boolean exists(Collection<Object> c, Object o) {
		// log.debug("Functions:" + c + ":" + o);
		return c.contains(o);
	}

	public static Object convert(String obj, String type) {
		if (type.equalsIgnoreCase("integer")) {
			return Integer.valueOf(obj);
		} else if (type.equalsIgnoreCase("long")) {
			return Long.valueOf(obj);
		} else {
			return obj;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
