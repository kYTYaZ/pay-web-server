package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.CommonUtil;
import com.util.DateUtil;

public class C2BSPayQuery {

	public static Map<String, String> getC2BSPayQuery() {
		System.out.println("开始组装冲正接口报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, "3003");
		map.put(Dict.orderNumber, "20180101010101");
		map.put(Dict.orderTime, "20180101010101");
		return map;
	}
}
