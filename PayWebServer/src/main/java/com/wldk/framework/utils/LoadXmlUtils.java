package com.wldk.framework.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class LoadXmlUtils {
	
	private static Logger logger = LoggerFactory.getLogger(LoadXmlUtils.class);
	
	public static void LOAD(String value) {
		String fielName=DateUtils.getCurTimestampStr()+".xml";
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(fielName)));
//			out.write(bean.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			if(out != null){
				out.close();
			}
		}
	}
}
