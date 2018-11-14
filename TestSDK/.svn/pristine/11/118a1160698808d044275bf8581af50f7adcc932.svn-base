package com.trade.c2b.trading;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;

public class C2BCreateQrcode {

	public static Map<String, String> getC2BCreateQrcode() {
		System.out.println("开始组装C2B生成二维码接口报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_1005); 
		map.put(Dict.cardNo, "6230910199025590783");
		map.put(Dict.customName, "wangwang");
		map.put(Dict.deviceNumber, "12345678");
		map.put(Dict.channel, "6005");
		map.put(Dict.orderNumber, "9999"+new SimpleDateFormat("yyyyMMDDHHmmss").format(new Date()));
		map.put(Dict.orderTime, new SimpleDateFormat("yyyyMMDDHHmmss").format(new Date()));
		map.put(Dict.localQrType, "13");
		map.put(Dict.cardAttr, "01");
		map.put(Dict.mobile, "18667018888");
		map.put(Dict.deviceType, "1");
		map.put(Dict.accountIdHash, "qsadfe");
		map.put(Dict.sourceIP, "158.222.2.1");
		map.put(Dict.deviceID, "qw123");
		map.put(Dict.pinFree, "50");
		map.put(Dict.maxAmont, "200");

//		System.out.println("C2B生成二维码报文:"+map.toString());
		return map;
	}
}
