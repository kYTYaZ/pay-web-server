package com.trade.wx.trading;

import java.util.HashMap;
import java.util.Map;

public class WxQuery {

	public static Map<String, String> getData() {

		System.out.println("开始组装微信订单查询报文");

		Map<String, String> map = new HashMap<String, String>();
//		map.put("txnCode", "3001");
//		map.put("transType", "35");
//		map.put("channel", "6002");
//		map.put("merId", "006331070110001");
//		map.put("payAccessType", "03");
//		map.put("orderNumber", "100020170426140606");
//		map.put("orderTime", "20170426140606");
		
		map.put("txnCode", "3001");
		map.put("orderNumber", "100020180921142611");
		map.put("orderTime", "20180921142611");
		map.put("transType", "35");
		map.put("channel", "6001"); 
		map.put("payAccessType", "02");


		return map;

	}

}
