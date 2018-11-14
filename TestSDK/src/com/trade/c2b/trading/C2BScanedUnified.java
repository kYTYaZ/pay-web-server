package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.CommonUtil;
import com.util.DateUtil;

public class C2BScanedUnified {

	public static Map<String, String> getC2BScanedUnified() {
		System.out.println("开始组装冲正接口报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_1009);
		map.put(Dict.channel, "6005");
		map.put(Dict.orderTime, DateUtil.getCurrentDateTime());
		map.put(Dict.orderNumber, CommonUtil.getOrderNo());
		map.put(Dict.accNo, "6216261000000002485");
		map.put(Dict.cardAttr, "01");
		map.put(Dict.mobile, "15757871158");
		map.put(Dict.deviceID, "PSN123456");
		map.put(Dict.deviceType, "1");
		map.put(Dict.accountIdHash, "15757871158");
		map.put(Dict.sourceIP, "127.0.0.1");
		map.put(Dict.deviceLocation, "135168");
		map.put(Dict.fullDeviceNumber, "1898484");
		return map;
	}
}
