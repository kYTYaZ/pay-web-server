package com.trade.wx.trading;

import java.util.HashMap;
import java.util.Map;

import com.util.DateUtil;

public class WxPreCreate {

	public static Map<String, String> getData() {

		System.out.println("开始组装微信主扫报文");

		Map<String,String> map = new HashMap<String, String>();
		map.put("txnCode", "1001");
		map.put("orderAmount", "000000000001");
		map.put("orderNumber", "1000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime",DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("transType", "01");
		map.put("merId", "20181016181518181518"); 
		map.put("merName", "正在输入");
		map.put("channel", "6001");
		map.put("payAccessType", "02");
		map.put("currencyType", "156"); 
		map.put("payType", "12");
		map.put("isCredit", "0");
	    map.put("subWxMerId", "247583402");
		map.put("deviceInfo", "POS");

		return map;
	}

}
