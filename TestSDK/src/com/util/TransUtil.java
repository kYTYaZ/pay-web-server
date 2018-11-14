package com.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.common.constants.SDKConstant;

public class TransUtil {

	/**
	 * map集合转xml字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToXml(Map<String, String> map) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<xml>");

			Set<String> set = map.keySet();
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String key = it.next();
				Object value = map.get(key);
				if (null == value) {
					value = "";
				}
				sb.append("<" + key + ">" + value + "</" + key + ">");
			}
			sb.append("</xml>");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 * 解析xml转map
	 * 
	 * @param xmlString
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	public static Map<String, String> xmlToMap(String xmlString)
			throws Exception {

		// 这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream is = getStringStream(xmlString);
		Document document = builder.parse(is);

		// 获取到document里面的全部结点
		NodeList allNodes = document.getFirstChild().getChildNodes();
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < allNodes.getLength(); i++) {
			Node node = allNodes.item(i);
			nodeIterator(node, map, 2000);
		}

		return map;
	}

	public static InputStream getStringStream(String sInputString)
			throws UnsupportedEncodingException {
		ByteArrayInputStream tInputStringStream = null;
		if (sInputString != null && !sInputString.trim().equals("")) {
			tInputStringStream = new ByteArrayInputStream(sInputString
					.getBytes(SDKConstant.UTF8));
		}
		return tInputStringStream;
	}

	public static void nodeIterator(Node node, Map<String, String> map, int deep) {
		if (deep > 2000) {
			// 具体数值由虚拟机内存大小和每次递归堆栈大小决定，一般在2000次左右
			deep = 2000;
		}

		if (--deep < 0) {
			return;
		}

		if (node instanceof Element && node.hasChildNodes()) {
			for (int i = 0; i < node.getChildNodes().getLength(); i++) {
				Node subNode = node.getChildNodes().item(i);
				if (subNode.getNodeType() == Node.ELEMENT_NODE) {
					nodeIterator(subNode, map, 2000);
				} else {
					map.put(node.getNodeName(), node.getTextContent());
				}
			}
		}
	}
	
	public static void main(String[] args) {
		String ss = "<xml><remark>&amp;＃55357;&amp;＃56351;</remark></xml>";
//		String ss = "<xml><remark>&＃55357;&＃56351;</remark></xml>";
//		String ss = "<xml><remark>sdfsdfs</remark></xml>";
		try {
			System.out.println(TransUtil.xmlToMap(ss));;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
