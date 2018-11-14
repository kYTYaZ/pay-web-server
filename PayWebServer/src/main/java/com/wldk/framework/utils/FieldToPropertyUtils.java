package com.wldk.framework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 对象属性和数据库字段转换
 * 
 * @author Administrator
 * 
 */
public class FieldToPropertyUtils {

	/**
	 * 对象属性转换为表字段 例如：userName to user_name
	 * 
	 * @param property
	 *            对象属性
	 * @return 数据库字段
	 */
	public static String propertyToField(String property) {
		if (null == property) {
			return "";
		}
		char[] chars = property.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (char c : chars) {
			if (CharUtils.isAsciiAlphaUpper(c)) {
				sb.append("_" + StringUtils.lowerCase(CharUtils.toString(c)));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 表字段转换成对象属性 例如：user_name to userName
	 * 
	 * @param field
	 *            数据库字段
	 * @return 对象属性
	 */
	public static String fieldToProperty(String field) {
		if (null == field) {
			return "";
		}
		field = field.toLowerCase();
		char[] chars = field.toCharArray();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == '_') {
				int j = i + 1;
				if (j < chars.length) {
					sb.append(StringUtils.upperCase(CharUtils
							.toString(chars[j])));
					i++;
				}
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	

	/**
	 * 集合数据转换 例如：user_name to userName
	 * 
	 * @param rs
	 *            集合
	 * @return
	 */
	public static List<Map<String, Object>> tofupperCaseList(List<Map<String, Object>> rs) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (rs != null && rs.size() > 0) {
			for (Map<String, Object> row : rs) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> m = row;
				Set s = m.keySet();
				Iterator i = s.iterator();
				while (i.hasNext()) {
					Object key = i.next();
					Object name = m.get(key);
					map.put(fieldToProperty(key.toString()), name);
				}
				list.add(map);
			}
		}

		return list;
	}
	
	
	
	/**
	 * 商户信息转换IN SQL
	 * 
	 * @param rs
	 *            集合
	 * @return
	 */
	public static String tofupperCaseINSQL(List<Map<String, Object>> rs) {
		StringBuffer stringBuffer = new StringBuffer();
		if (rs != null && rs.size() > 0) {
			stringBuffer.append("(");
			for (int k=0;k<rs.size();k++) {
				Map<String, Object> row =rs.get(k);
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> m = row;
				Set s = m.keySet();
				Iterator i = s.iterator();
				while (i.hasNext()) {
					Object key = i.next();
					 if("mchtNo".equals(key.toString())){
						 stringBuffer.append("'"+ m.get(key)+"'");
						 continue;
					 }
				}
				if (k != (rs.size() - 1)) {
					stringBuffer.append(",");
				}
			}
			stringBuffer.append(")");
		}
//		System.out.println(stringBuffer.toString());
		return stringBuffer.toString();
	}
	

	/**
	 * 集合数据转换 例如：user_name to userName
	 * 
	 * @param rs
	 *            集合
	 * @return
	 */
	public static List<Object[]> tofupperCaseListObject(List<Map<String, Object>> rs) {
		List<Object[]> list = new ArrayList<Object[]>();
		if (rs != null && rs.size() > 0) {
			for (Map<String, Object> row : rs) {
				Object[] values = new Object[row.size()];
				Set<String> rowKey = row.keySet();
				Iterator rowIt = rowKey.iterator();
				int count = 0;
				while(rowIt.hasNext()){
					String key = rowIt.next().toString();
					values[count] = new Object();
					values[count] = row.get(key);
					count ++;
				}
				list.add(values);
			}
		}

		return list;
	}

	public static void main(String[] args) {
		String str = fieldToProperty("BANK_FEE_RATE"); 
		System.out.println(str);
	}
}
