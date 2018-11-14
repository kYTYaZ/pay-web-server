package com.wldk.framework.utils;


import java.text.SimpleDateFormat;

/**
 * 根据时间产生唯一字符串
 * 
 * @author 
 * @since 2007-6-27 12:09:06
 */
public class UniqueUtil {
	private static UniqueUtil uu = new UniqueUtil();

	private long currTime = 0;

	private long beginTime = 0;

	private long point = 0;

	private UniqueUtil() {
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
			beginTime = sf.parse("20070101000000").getTime();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static UniqueUtil getUtil() {
		return uu;
	}

//	public synchronized String getUnique() {
//		long ztime = System.currentTimeMillis();
//		if (ztime > currTime) {
//			point = 0;
//			currTime = ztime;
//		}
//		return NstrUtil.long2Nstrs((currTime-beginTime),35) + "z" + NstrUtil.long2Nstrs(point++,35);
//	}


//	public static void main(String[] as) throws ParseException {
////		long a=System.currentTimeMillis();
//		for (int i = 0; i < 1000; i++) {
//			System.out.println(UniqueUtil.getUtil().getUnique());
//		}
//	}

}