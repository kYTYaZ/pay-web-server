package com.trade.ali.trading;

import java.util.HashMap;
import java.util.Map;

import com.util.DateUtil;

public class AliRevoke {

	public static Map<String, String> getData() {

		System.out.println("开始组装支付宝撤销报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", "8008");
		map.put("orderNumber", "1000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime", DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderAmount", "000000300003");
		map.put("merId", "20181018192224192224");
		map.put("transType", "31");
		map.put("channel", "6001");
		map.put("payAccessType", "03");
		map.put("initOrderNumber", "100020170427160941");
		map.put("initOrderTime", "20170427160941");

		return map;
	}

}
