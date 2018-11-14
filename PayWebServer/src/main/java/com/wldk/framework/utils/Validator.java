/*@(#)
 * 
 * Project: java_frame
 *
 * Modify Information:
 * =============================================================================
 *   Author         Date           Description
 *   ------------ ---------- ---------------------------------------------------
 *   zhaodk        2012-5-24        first release
 *
 * 
 * Copyright Notice:
 * =============================================================================
 *       Copyright 2012 Huateng Software, Inc. All rights reserved.
 *
 *       This software is the confidential and proprietary information of
 *       Shanghai HUATENG Software Co., Ltd. ("Confidential Information").
 *       You shall not disclose such Confidential Information and shall use it
 *       only in accordance with the terms of the license agreement you entered
 *       into with Huateng.
 *
 * Warning:
 * =============================================================================
 * 
 */
package com.wldk.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 特殊字符装换
 * 
 */
public class Validator {

	public static void main(String[] args) {
		System.out.println(matchPattern("112223eddddW", "^[a-zA-Z0-9]*$"));
		System.out.println(filter("dddfsdsd《》<>"));
	}

	/**
	 * 过滤用户输入要保护应用程序免遭跨站点脚本编制的攻击,<br>
	 * 请通过将敏感字符转换为其对 应的字符实体来清理HTML。<br>
	 * 这些是HTML 敏感字符：< > " ' % ; ) ( & + 以下示例通过<br>
	 * 将敏感字符转换为其对应的字符实体，来过滤指定字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String filter(String value) {
		if (value == null) {
			return null;
		}
		StringBuffer result = new StringBuffer(value.length());
		for (int i = 0; i < value.length(); ++i) {
			switch (value.charAt(i)) {
			case '<':
				result.append("&lt;");
				break;
			case '>':
				result.append("&gt;");
				break;
			case '"':
				result.append("&quot;");
				break;
			case '\'':
				result.append("&#39;");
				break;
			case '%':
				result.append("&#37;");
				break;
			case ';':
				result.append("&#59;");
				break;
			case '(':
				result.append("&#40;");
				break;
			case ')':
				result.append("&#41;");
				break;
			case '&':
				result.append("&amp;");
				break;
			case '+':
				result.append("&#43;");
				break;
			default:
				result.append(value.charAt(i));
				break;
			}
		}
		return result.toString();
	}

	/**
	 * 验证是否为int型
	 * 
	 * @param value
	 * @return
	 */
	public static boolean validateInt(String value) {
		boolean isFieldValid = false;
		try {
			Integer.parseInt(value);
			isFieldValid = true;
		} catch (Exception e) {
			isFieldValid = false;
		}
		return isFieldValid;
	}

	/**
	 * 正则表达式验证
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static boolean matchPattern(String str, String pattern) {
		try {
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(str);
			return m.matches();
		} catch (Exception e) {
			return false;
		}
	}

}
