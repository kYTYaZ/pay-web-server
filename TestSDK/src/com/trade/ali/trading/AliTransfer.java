package com.trade.ali.trading;

import java.util.HashMap;
import java.util.Map;

public class AliTransfer {
	
	public static Map<String, String> getData() {
		System.out.println("��ʼ��װ֧���������̻�Ǩ�Ʊ���");
		
		Map<String,String >map= new HashMap<String, String>();
		map.put("txnCode","8009");
		map.put("merId", "20181030090023090023");
		map.put("alipayMerchantId", "2088000294772492");
		return map;
		
	}

}
