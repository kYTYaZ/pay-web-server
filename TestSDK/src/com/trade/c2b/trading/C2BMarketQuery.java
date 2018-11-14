package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;

public class C2BMarketQuery {

	public static Map<String, String> getC2BMarketQuery() {
		System.out.println("开始组装C2B生成二维码接口报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.C2BEWM_MARKET_QUERY); 
		map.put(Dict.channel, "6005");
		return map;
	}
}
