package com.trade.ali.trading;

import java.util.HashMap;
import java.util.Map;

import com.util.DateUtil;

public class AliPreCreate {

	public static Map<String, String> getData() {

		System.out.println("��ʼ��װ֧������ɨ����");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", "8001");
		map.put("orderAmount", "000000000001");
		map.put("channel", "6001");
		map.put("transType", "01");
		map.put("payAccessType", "03");
		map.put("currencyType", "156");
		map.put("merId", "100000000000002");
		map.put("merName", "����");
		map.put("orderNumber", "1000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime", DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("payType", "12");
		map.put("subject", "ɨ�������Ʒ");
		map.put("body", "ɨ�������Ʒbody");
		map.put("termId", "1000001");
		map.put("operatorId", "10001");
		map.put("storeId", "�����н���������·");
		map.put("alipayStoreId", "֧���������н���������·");
		map.put("alipayMerchantId", "47383");

		return map;
	}

}