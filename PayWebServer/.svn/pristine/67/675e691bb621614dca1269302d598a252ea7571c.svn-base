/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: RandomUtil.java
 * Author:   sunguohua
 * Date:     2014-7-29 下午3:35:56
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.common.util;

import java.util.Random;

/**
 * 随机数生成工具
 * 
 * @author sunguohua
 * 
 */
public class RandomUtil {
	
	private static char[] DIGIT_LETTER = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	private static Random RANDOM = new Random();

	/**
	 * 取得随机字符串
	 * 
	 * @param count 随机字符长度
	 * @param type 
	 *          0 is a number string. 
	 *          1 is alphabet string. 
	 *          2 is alphabet with numbers. 
	 *          3 is numbers, alphabet with "_" which is default.
	 *          4 is Ascii. 
	 * 
	 */
	public static String getRandomString(int count, int type) {
		switch (type) {
		case 0:
			return random(count, 0, 0, false, true, null, RANDOM);
		case 1:
			return random(count, 0, 0, true, false, null, RANDOM);
		case 2:
			return random(count, 0, 0, true, true, null, RANDOM);
		case 3:
			return random(count, 0, DIGIT_LETTER.length, false, false, DIGIT_LETTER, RANDOM);
		case 4:
			return random(count, 32, 127, false, false, null, RANDOM);
			// 默认获取数字
		default:
			return random(count, 0, 0, false, true, null, RANDOM);
		}
	}
	
	/**
	 * 取得随机字符串
	 * @param count 随机字符长度
	 * @param start 随机字符数组的起始位置
	 * @param end   随机字符数组的末尾位置
	 * @param letters 是否只包含字母
	 * @param numbers 是否只包含数字
	 * @param chars 随机选取的字符数组，如果为空，则为所有字符集
	 * @param random 随机数生成器
	 * @return
	 */
	public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
		if (count == 0) {
			return "";
		} else if (count < 0) {
			throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
		}
		if ((start == 0) && (end == 0)) {
			end = 'z' + 1;
			start = ' ';
			if (!letters && !numbers) {
				start = 0;
				end = Integer.MAX_VALUE;
			}
		}

		char[] buffer = new char[count];
		int gap = end - start;

		while (count-- != 0) {
			char ch;
			if (chars == null) {
				ch = (char) (random.nextInt(gap) + start);
			} else {
				ch = chars[random.nextInt(gap) + start];
			}
			if ((letters && Character.isLetter(ch)) || (numbers && Character.isDigit(ch)) || (!letters && !numbers)) {
				if (ch >= 56320 && ch <= 57343) {
					if (count == 0) {
						count++;
					} else {
						// low surrogate, insert high surrogate after putting it
						// in
						buffer[count] = ch;
						count--;
						buffer[count] = (char) (55296 + random.nextInt(128));
					}
				} else if (ch >= 55296 && ch <= 56191) {
					if (count == 0) {
						count++;
					} else {
						// high surrogate, insert low surrogate before putting
						// it in
						buffer[count] = (char) (56320 + random.nextInt(128));
						count--;
						buffer[count] = ch;
					}
				} else if (ch >= 56192 && ch <= 56319) {
					// private high surrogate, no effing clue, so skip it
					count++;
				} else {
					buffer[count] = ch;
				}
			} else {
				count++;
			}
		}
		return new String(buffer);
	}
	public static void main(String[] args){
		System.out.println(RandomUtil.getRandomString(6, 3));
	}
}