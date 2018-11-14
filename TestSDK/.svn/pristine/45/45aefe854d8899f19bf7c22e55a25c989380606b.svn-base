package com.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

	public static String getOrderNo() {
		return new SimpleDateFormat("MMDDHHmmss").format(new Date());
	}

	/**
	 * ×Ö·û²¹È«
	 * @param content
	 * @return
	 */
	public static String fillString(int content){
		return String.format("%06d", content);
	}
	
	public static String fillString(String content){
		try {
			return String.format("%06d", content.getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String fillString(String str, int i, String char1) {
		if (str.length() > i)
			return str.substring(0, i);
		int len = str.length();
		for (int j = 0; j < i - len; j++) {
			str = char1 + str;
		}
		return str;
	}
	
	public static void sleepTime(int time){
		try {
			Thread.sleep(time);
			System.out.println();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleepTime(int time,boolean flag){
		try {
			Thread.sleep(time);
			if(flag){
				System.out.println();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleepTimeSysout(int time){
		try {
			System.out.println("¿ªÊ¼ÑÓ³Ù"+time+"Ãë");
			for (int i = time; i > 0; i--) {
				Thread.sleep(1000);
				System.out.print(fillString(i+"",3," ")+"  ");
				if(i % 10 == 1){
					System.out.println();
				}
			}
			System.out.println();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
