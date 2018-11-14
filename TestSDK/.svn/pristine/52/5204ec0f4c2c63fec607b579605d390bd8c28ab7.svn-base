package com.validate.common;


import java.util.Map;

import com.util.CommonUtil;
import com.util.StringUtil;

public class Validation {
	
	public static void validate(Map<String, String> map, String[] ary,
			String validateName) {
		System.out.println("开始校验:" + validateName);
		CommonUtil.sleepTime(1 * 1000, false);
		for (int i = 0; i < ary.length; i++) {
			String key = ary[i];
			String value = map.get(key);
			if (!StringUtil.isEmpty(value)) {
				System.out.println(key + "   " + value);
			} else {
				System.err.println(key + "   " + value + "   字段缺失");
			}
			CommonUtil.sleepTime(1 * 1, false);
		}
		CommonUtil.sleepTime(1 * 1000, false);
		System.out.println("结束校验:" + validateName);
		System.out.println();
	}

}
