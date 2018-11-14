package com.trade.wx.trading;

import java.util.HashMap;
import java.util.Map;

import com.util.DateUtil;

public class WxRevoke {

	public static Map<String, String> getData() {

		System.out.println("开始组装微信撤销报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("orderAmount", "000000000899");
		map.put("channel", "6001");
		map.put("orderNumber", "1000"+DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime",DateUtil.getDateStr("yyyyMMddHHmmss") + "");
		map.put("merId", "998350165130003");
		map.put("transType", "31");
		map.put("currencyType", "156");
		map.put("payAccessType", "02");
		map.put("txnCode", "2001");
		map.put("initOrderNumber", "PSN018040814467551");
		map.put("initOrderTime", "20180408140140");

		return map;
	}

}
