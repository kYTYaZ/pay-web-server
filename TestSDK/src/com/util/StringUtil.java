package com.util;

public class StringUtil {

	public static boolean isEmpty(String str) {
		if (null == str || "".equals(str.trim()) || "null".equals(str.trim())
				|| str.trim().length() == 0) {
			return true;
		}
		return false;
	}


}
