package com.wldk.framework.utils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonFunction {

	private static Logger logger = LoggerFactory.getLogger(CommonFunction.class);
	
	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	private static final String Decimal_Format = "###,##0.00#";
	private static final String Decimal_Format2 = "###,##0";

	/**
	 * 填补字符串
	 *
	 * @param str
	 * @param fill
	 * @param len
	 * @param isEnd
	 * @return
	 */
	public static String fillString(String str, char fill, int len, boolean isEnd) {
		// int fillLen = 0;
		// if (StringUtil.isNull(str)) {
		// fillLen = len;
		// str = "";
		// } else {
		// fillLen = len - str.getBytes().length;
		// }
		int fillLen = len - str.getBytes().length;
		if (len <= 0) {
			return str;
		}
		for (int i = 0; i < fillLen; i++) {
			if (isEnd) {
				str += fill;
			} else {
				str = fill + str;
			}
		}
		return str;
	}

	/**
	 * 填补字符串(中文字符扩充)
	 *
	 * @param str
	 * @param fill
	 * @param len
	 * @param isEnd
	 * @return
	 */
	public static String fillStringForChinese(String str, char fill, int len, boolean isEnd) {
		int num = 0;
		Pattern p = Pattern.compile("^[\u4e00-\u9fa5]");
		for (int i = 0; i < str.length(); i++) {
			Matcher m = p.matcher(str.substring(i, i + 1));
			if (m.find()) {
				num++;
			}
		}
		int fillLen = len - (str.length() + num);
		if (len <= 0) {
			return str;
		}
		for (int i = 0; i < fillLen; i++) {
			if (isEnd) {
				str += fill;
			} else {
				str = fill + str;
			}
		}
		return str;
	}

	/**
	 * 获得指定日期的偏移日期
	 *
	 * @param refDate 参照日期
	 * @param offSize 偏移日期
	 * @return
	 */
	public static String getOffSizeDate(String refDate, String offSize) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Integer.parseInt(refDate.substring(0, 4)), Integer.parseInt(refDate.substring(4, 6)) - 1,
				Integer.parseInt(refDate.substring(6, 8)));
		calendar.add(Calendar.DATE, Integer.parseInt(offSize));
		String year = String.valueOf(calendar.get(Calendar.YEAR));
		String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
		String retDate = String.valueOf(calendar.get(Calendar.DATE));
		if (Integer.parseInt(month) < 10) {
			month = "0" + month;
		}
		if (Integer.parseInt(retDate) < 10) {
			retDate = "0" + retDate;
		}
		return year + month + retDate;
	}

	/**
	 * 将金额元转分
	 *
	 * @param str
	 * @return
	 */
	public static String transYuanToFen(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return bigDecimal.movePointRight(2).toString();
	}

	/**
	 * 左去零
	 *
	 * @param str
	 * @return
	 */
	public static String Lefttransn(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return bigDecimal.toString();
	}

	/**
	 * 将金额分转元
	 *
	 * @param str
	 * @return
	 */
	public static String transFenToYuan(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return bigDecimal.movePointLeft(2).toString();
	}

	/**
	 * 将分转元并已千分位返回（小数点后二位，四舍五入）
	 * 
	 * @param str
	 * @return ###,###.##
	 */
	public static String transFenToYuanFormat(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return new DecimalFormat(Decimal_Format)
				.format(bigDecimal.movePointLeft(2).setScale(2, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * 将金额转已千分位返回(整数)
	 * 
	 * @param str
	 * @return ###,###
	 */
	public static String decimalFormat(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return new DecimalFormat(Decimal_Format2).format(bigDecimal);
	}

	/**
	 * 将金额转已千分位返回（小数点后二位，四舍五入）
	 * 
	 * @param str
	 * @return ###,###.##
	 */
	public static String decimalFormatUp(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return new DecimalFormat(Decimal_Format).format(bigDecimal);
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1 被加数
	 * @param v2 加数
	 * @return 两个参数的和
	 */

	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1 被减数
	 * @param v2 减数
	 * @return 两个参数的差
	 */

	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1 被乘数
	 * @param v2 乘数
	 * @return 两个参数的积
	 */

	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 将金额转元
	 *
	 * @param str
	 * @return
	 */
	public static String transToYuan(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());

		return bigDecimal.toString();
	}

	/**
	 * 将金额转千分位返回（小数点后二位，四舍五入）
	 * 
	 * @param str
	 * @return ###,###.##
	 */
	public static String transToYuanFormat(String str) {
		if (str == null || "".equals(str.trim()))
			return "";
		BigDecimal bigDecimal = new BigDecimal(str.trim());
		return new DecimalFormat(Decimal_Format).format(bigDecimal);
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1 被除数
	 * @param v2 除数
	 * @return 两个参数的商
	 */

	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1    被除数
	 * @param v2    除数
	 * @param scale 表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 获得指定个数的随机数组合
	 *
	 * @param len
	 * @return 2010-8-19上午10:51:15
	 */
	public static String getRandomNum(int len) {
		// String ran = "";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < len; i++) {
			sb.append(String.valueOf(random.nextInt(10)));
			// ran += String.valueOf(random.nextInt(10));
		}
		return sb.toString();
	}

	/**
	 * 判断字符串是否全部由数字组成
	 *
	 * @param str
	 * @return 2010-8-26下午02:20:28
	 */
	public static boolean isMoney(String str) {

		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}
		return true;
	}

	/**
	 * 判断字符串是否全部由数字组成
	 *
	 * @param str
	 * @return 2010-8-26下午02:20:28
	 */
	public static boolean isAllDigit(String str) {
		str = str.replace(".", "");
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isDigit(str.charAt(i)))
				return false;
		}
		return true;
	}

	public static Date getCurrentTs() {
		Date now = new Date();
		return new Timestamp(now.getTime());
	}

	/**
	 * trim给定对象的field 仅对private field String有效(不含static)
	 *
	 * @param obj
	 * @return 2011-6-22下午03:46:12
	 */
	public static Object trimObject(Object obj) {

		try {
			Method[] methods = obj.getClass().getMethods();
			for (Method m : methods) {
				if (m.getName().startsWith("get")) {
					// 允许个别字段转换失败
					try {
						if (String.class == m.getReturnType()) {
							String value = (String) m.invoke(obj, new Object[] {});
							if (!StringUtil.isNullLength(value)) {
								obj.getClass().getMethod("s" + m.getName().substring(1), String.class).invoke(obj,
										value.trim());
							}
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return obj;
	}

	/**
	 * 获得指定个数转换为Double
	 *
	 * @param len
	 * @return 2010-8-19上午10:51:15
	 */

	public static Double getDValue(String value, Double _default) {
		if (StringUtil.isNull(value)) {
			return Double.valueOf(value);
		}
		return _default;
	}

	/**
	 * 获得指定个数转换为BigDecimal
	 *
	 * @param len
	 * @return 2010-8-19上午10:51:15
	 */
	public static BigDecimal getBValue(String value, BigDecimal _default) {
		if (StringUtil.isNull(value)) {
			try {
				return new BigDecimal(value.trim());
			} catch (Exception ex) {
				return _default;
			}
		} else
			return _default;
	}

	/**
	 * 获得指定个数转换为int
	 *
	 * @param len
	 * @return 2010-8-19上午10:51:15
	 */
	public static Integer getInt(String value, int _default) {
		if (StringUtil.isNull(value)) {
			try {
				return Integer.parseInt(value.trim());
			} catch (Exception ex) {
				return _default;
			}
		} else
			return _default;
	}

	/**
	 *
	 * @param str
	 * @return
	 */
	public static String formate8Date(String str) {
		if (str.length() == 8) {
			return str.substring(0, 4) + "-" + str.substring(4, 6) + "-" + str.substring(6, 8);
		}
		return str;
	}

	public static String getCurrDate(String format) {
		SimpleDateFormat formater = new SimpleDateFormat(format);
		return formater.format(new Date());
	}

	/**
	 * 16进制
	 *
	 * @param c
	 * @return 2011-7-27上午11:50:28
	 */
	private static byte toByte(char c) {
		byte b = (byte) "0123456789abcdef".indexOf(c);
		return b;
	}

	/**
	 * 16进制转BCD
	 *
	 * @param hex
	 * @return 2011-7-27上午11:49:20
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	/**
	 * BCD转成16进制
	 *
	 * @param bArray
	 * @return 2011-7-27上午11:47:56
	 */
	public static String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	public static String urlToRoleId(String url) {
		try {
			String[] array = url.split("/");
			String[] result = array[array.length - 1].split("\\.");
			String res = result[0];
			return res;
		} catch (Exception e) {
			return url;
		}
	}

	public static String transMoney(double n) {
		try {
			String[] fraction = { "角", "分" };
			String[] digit = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
			String[][] unit = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

			String head = n < 0 ? "负" : "";
			n = Math.abs(n);

			String s = "";

			for (int i = 0; i < fraction.length; i++) {
				s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
			}
			if (s.length() < 1) {
				s = "整";
			}
			int integerPart = (int) Math.floor(n);

			for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
				String p = "";
				for (int j = 0; j < unit[1].length && n > 0; j++) {
					p = digit[integerPart % 10] + unit[1][j] + p;
					integerPart = integerPart / 10;
				}
				s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
			}
			return head + s.replaceAll("(零.)*零元", "元").replaceAll("(零.)+", "").replaceAll("(零.)+", "零")
					.replaceAll("^整$", "零元整");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return "";
	}

	public static String insertString(String src, String fill) {
		// String tmp = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {
			sb.append(fill);
			sb.append(src.substring(i, i + 1));
			// tmp += fill;
			// tmp += src.substring(i, i + 1);
		}
		return sb.toString();
	}

	public static void main(String[] args) {
//		System.out.println(decimalFormat("00000"));
//		System.out.println(new DecimalFormat("###,###.##").format(3234.7867));
	}
}
