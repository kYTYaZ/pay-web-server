package com.wldk.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 读取配置文件.properties
 * 
 * @author zhaodengke
 * 
 */
public class Configuration {
	
	private static Logger logger = LoggerFactory.getLogger(Configuration.class);
	
	private static Configuration instance = new Configuration();

	private String webName;
	private String fileName;
	private Properties load = new Properties();

	public Configuration() {
		Properties load = new Properties();
		InputStream is = null;
		try {
			is = Configuration.class.getResourceAsStream("/system.properties");
			load.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					is = null;
					logger.error(e.getMessage(), e);
				}
			}
		}
		this.webName = load.getProperty("WEB_NAME");
	}

	/**
	 * 读取配置文件
	 * 
	 * @param fileName
	 */
	public Configuration(String fileName) {
		this.fileName = fileName;
		InputStream is = null;
		try {
			is = Configuration.class.getResourceAsStream("/" + fileName);
			load.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					is = null;
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	public String getWebName() {
		return webName;
	}

	public void setWebName(String webName) {
		this.webName = webName;
	}

	public static Configuration getInstance() {
		return instance;
	}

	/**
	 * 组装字段类型
	 * 
	 * @return
	 */
	public List<Object[]> FIELD_TYPE() {
		List<Object[]> list = new ArrayList<Object[]>();
		String fieldType = load.getProperty("FIELD_TYPE");
		String[] obj = fieldType.split("\\|");
		if (obj != null && obj.length > 0) {
			for (String st : obj) {
				list.add(new Object[] { st, st });
			}
		}
		return list;
	}

	public static void main(String[] args) {
//		System.out.println(Configuration.getInstance().getWebName());
		List<Object[]> list = new Configuration("field_type.properties").FIELD_TYPE();
		for (Object[] obj : list) {
//			System.out.println(obj[0] + "=" + obj[1]);
		}
	}

}