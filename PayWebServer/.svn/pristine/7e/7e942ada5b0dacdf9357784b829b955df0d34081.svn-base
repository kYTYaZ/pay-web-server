package com.wldk.framework.utils;

import java.io.FileOutputStream;
import java.io.StringReader;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * xml生成工具
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-11-14 <br>
 * Author: Administrator <br>
 * 
 */
public class XmlUtils {

	private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);
	
	public static void perttyFormat(String xmlValue, String file) throws Exception {
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		StringReader read = new StringReader(xmlValue);
		/** 创建新德输入源SAX解析器将使用InputSource对象来确定如何读取xml输入 */
		InputSource source = new InputSource(read);
		SAXBuilder sb = new SAXBuilder();
		Document doc = sb.build(source);
		out.output(doc, new FileOutputStream(file));
		out.clone();
	}

	public static void main(String[] args) {

		try {
			XmlUtils.perttyFormat("<?xml version='1.0' encoding='utf-8'?><tomcat-users> <role rolename=\"manager\"/><user username=\"tomcat\" password=\"tomcat\" roles=\"manager\"/></tomcat-users>",
					"d:\\f.xml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
		}
	}
}
