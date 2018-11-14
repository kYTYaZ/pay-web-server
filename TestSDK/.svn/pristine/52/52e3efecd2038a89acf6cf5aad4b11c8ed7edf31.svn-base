package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.CommonUtil;
import com.util.DateUtil;

public class C2BConsume {

	public static Map<String, String> getC2BData() {
		System.out.println("开始组装C2B消费接口报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_9001); 
		map.put(Dict.orderNo, CommonUtil.getOrderNo());
		map.put(Dict.merId, "777920058135880");
		map.put(Dict.termId, "88888888");
		map.put(Dict.merName, "adidas");
		map.put(Dict.txnAmt, "60");
		map.put(Dict.orderTime, DateUtil.getCurrentDateTime());
		map.put(Dict.channel, "6001");
		map.put(Dict.payAccessType, "05");
		map.put(Dict.currencyCode, SDKConstant.currencyCode);
		map.put(Dict.qrNo, "6220413551015941672");
		map.put(Dict.areaInfo, "1561111");
		map.put(Dict.merCatCode, "1111");
		map.put(Dict.transType, "01");
		map.put(Dict.merAcctNo, "123123123123");

//		System.out.println("C2B消费请求报文:"+map.toString());
		return map;
	}
}
