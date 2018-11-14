package com.trade.ali.trading;

import java.util.HashMap;
import java.util.Map;

public class AliQuery {

	public static Map<String, String> getData() {

		System.out.println("开始组装支付宝订单查询报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", "3001");
		map.put("transType", "35");
		map.put("channel", "6002");
		map.put("merId", "20180913152307152307");
		map.put("payAccessType", "03");
		map.put("orderNumber", "100020180921142611");
		map.put("orderTime", "20180921142611");

		return map;

	}

}
