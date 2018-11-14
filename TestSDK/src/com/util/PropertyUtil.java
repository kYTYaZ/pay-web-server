package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {
	
	public static String getProperty(String key){
		
		InputStream input = ClassLoader.getSystemResourceAsStream("constants.properties");
		Properties prop = new Properties();
		try {
			prop.load(input);
			String value = prop.getProperty(key);
			return value;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
}
