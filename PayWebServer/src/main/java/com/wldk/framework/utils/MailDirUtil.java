package com.wldk.framework.utils;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件存放路径的工具类
 * 
 * @author Administrator
 *
 */
public class MailDirUtil {

	private static Logger logger = LoggerFactory.getLogger(MailDirUtil.class);
	
	private static final String BUNDLE_NAME = "mailDir";

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static String getProperty(String key) {
		try {
			String value = BUNDLE.getString(key);
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return key;
		}
	}

	public static void main(String[] args) {
//		System.out.println(getProperty("hehe"));
//		System.out.println(getProperty("mail.windows.dir"));
//		System.out.println(getProperty("mail.linux.dir"));
//		System.out.println(getProperty("mail.aix.dir"));
	}
}
