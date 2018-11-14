package com.trade.wx.trading;

import java.util.HashMap;
import java.util.Map;

import com.util.DateUtil;

public class WxMicroPay {

	public static Map<String, String> getData() {
		System.out.println("开始组装微信被扫报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("orderAmount", "000000000001");
		map.put("channel", "6001");
		map.put("orderNumber", "1000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime",DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("merId", "963539910306119");
		map.put("payType", "15");
		map.put("transType", "01");
		map.put("merName", "正在输入");
		map.put("isCredit", "1");
		map.put("subWxMerId", "51844084");
		map.put("deviceInfo", "POS");
		map.put("currencyType", "156");
		map.put("payAccessType", "02");
		map.put("authCode", "135569776518385974");
		map.put("txnCode", "1002");
		map.put("remark", "aasdfsdf");

		return map;
	}

}
