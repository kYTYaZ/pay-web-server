package com.validate.common;


import java.util.Map;

import com.util.CommonUtil;
import com.util.StringUtil;

public class Validation {
	
	public static void validate(Map<String, String> map, String[] ary,
			String validateName) {
		System.out.println("��ʼУ��:" + validateName);
		CommonUtil.sleepTime(1 * 1000, false);
		for (int i = 0; i < ary.length; i++) {
			String key = ary[i];
			String value = map.get(key);
			if (!StringUtil.isEmpty(value)) {
				System.out.println(key + "   " + value);
			} else {
				System.err.println(key + "   " + value + "   �ֶ�ȱʧ");
			}
			CommonUtil.sleepTime(1 * 1, false);
		}
		CommonUtil.sleepTime(1 * 1000, false);
		System.out.println("����У��:" + validateName);
		System.out.println();
	}

}
