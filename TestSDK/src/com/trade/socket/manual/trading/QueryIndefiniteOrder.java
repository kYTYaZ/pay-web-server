package com.trade.socket.manual.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.DateUtil;

/**
 * 定时查询状态为(01,06)的订单状态流程
 */
public class QueryIndefiniteOrder {

	public static Map<String, String> getData() {
		System.out.println("定时查询状态为(01,06)的订单状态流程");
		
		String orderDate = DateUtil.format(DateUtil.addHour(-1),"yyyyMMdd");
		String txnTmStart = DateUtil.format(DateUtil.addHour(-2),"HH")+"3000";
		String txnTmEnd = DateUtil.format(DateUtil.addHour(-1),"HH")+"2959";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		map.put(Dict.orderDate, orderDate);
		map.put(Dict.txnTmStart, txnTmStart);
		map.put(Dict.txnTmEnd, txnTmEnd);
		
		return map;
	}
}
