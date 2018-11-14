package com.wldk.framework.utils;

/**
 * 用于10进 和 n进制的转换，默认的最大进制为62，可以自定义任意进制。
 * 
 * @author zhuwei
 * @since 2007-6-27 15::43
 */
public class NstrUtil {
	/**
	 * 默认的64进制字符序列
	 */
	public static char[] DEFAULT_CHARS = new char[62];
	static {
		int j = 0;
		for (int i = '0', n = '9'; i <= n; i++, j++)
			DEFAULT_CHARS[j] = (char) i;
		for (int i = 'a', n = 'z'; i <= n; i++, j++)
			DEFAULT_CHARS[j] = (char) i;
		for (int i = 'A', n = 'Z'; i <= n; i++, j++)
			DEFAULT_CHARS[j] = (char) i;
	}

	/**
	 * 将指定进制的String转化为10进制的long,此时将采用默认的64进制字符序列
	 * 
	 * @param data
	 *            N进制值字符串
	 * @param max
	 *            N进制
	 * @return
	 */
	public static long nstrs2Long(String data, int max) {
		nLengthCheck(max, DEFAULT_CHARS);
		return toLong(data, max, DEFAULT_CHARS);
	}

	/**
	 * 将指定进制的String转化为10进制的long
	 * 
	 * @param data
	 *            N进制值字符串
	 * @param keys
	 *            N进制的字符序列
	 * @return
	 */
	public static long nstrs2Long(String data, char[] keys) {
		return toLong(data, keys.length, keys);
	}

	/**
	 * 将10进制的long转化为指定进制的String,此时将采用默认的64进制字符序列
	 * 
	 * @param data
	 *            原10进制值
	 * @param max
	 *            N进制
	 * @return
	 */
//	public static String long2Nstrs(long data, int max) {
//		nLengthCheck(max, DEFAULT_CHARS);
//		String s = toNstr(data, max, DEFAULT_CHARS).replaceAll("^" + DEFAULT_CHARS[0] + "*", "");
//		return s.length() == 0 ? DEFAULT_CHARS[0] + "" : s;
//	}

	/**
	 * 将10进制的long转化为指定进制的String
	 * 
	 * @param data
	 *            原10进制值
	 * @param keys
	 *            N进制的字符序列
	 * @return
	 */
//	public static String long2Nstrs(long data, char[] keys) {
//		String s = toNstr(data, keys.length, keys).replaceAll("^" + keys[0] + "*", "");
//		return s.length() == 0 ? keys[0] + "" : s;
//	}

	private static void nLengthCheck(int max, char[] KEYS) {
		if (max > KEYS.length)
			throw new IllegalArgumentException("非法参数：" + max + "，只支持最大" + KEYS.length + "进制的转换。");
	}

//	private static String toNstr(long data, int max, char[] KEYS) {
//		long z1 = data / max;
//		long z2 = data % max;
//		return "" + (z1 >= max ? toNstr(z1, max, KEYS) : KEYS[(int) z1]) + (z2 >= max ? toNstr(z2, max, KEYS) : KEYS[(int) z2]);
//	}

	private static int getCharIndex(char c, int max, char[] KEYS) {
		for (int i = 0; i < KEYS.length; i++) {
			if (KEYS[i] == c) {
				if (i >= max)
					throw new IllegalArgumentException("字符'" + c + "'在字符序列中的索引值超出了指定进制值" + max + "的范围。");
				return i;
			}
		}
//		String s = "";
//		for (int i = 0; i < KEYS.length; i++)
//			s = s + KEYS[i];
		throw new IllegalArgumentException("字符'" + c + "'不在N进制的字符序列中，字符序列为[" + String.valueOf(KEYS) + "]");
	}

	private static long toLong(String data, int max, char[] KEYS) {
		long res = 0;
		for (int i = 0, n = data.length(); i < n; i++) {
			long x = getCharIndex(data.charAt(n - 1 - i), max, KEYS);
			x = i > 0 ? x * max : x;
			for (int j = 0; j < i - 1; j++)
				x = x * max;
			res = res + x;
		}
		return res;
	}

	public static void main(String[] as) {
//		 System.out.println("默认62进制字符序列测试：");
//		 String s=NstrUtil.long2Nstrs(100,8);
//		 System.out.println(s);
//		 long n=NstrUtil.nstrs2Long(s,8);
//		 System.out.println(n);
//				
//		 System.out.println("\n自定义N进制字符序列测试：");
//		 char[]TKEYS={'中','华','人','民','共','和','国','政','府'};
//		 String s1=NstrUtil.long2Nstrs(100,TKEYS);
//		 System.out.println(s1);
//		 long n1=NstrUtil.nstrs2Long(s1,TKEYS);
//		 System.out.println(n1);
//		 System.out.println(NstrUtil.getCharIndex('呵', 5, TKEYS));
	}

}
