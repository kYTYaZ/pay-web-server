package com.trade.ali.trading;

import java.util.HashMap;
import java.util.Map;

import com.util.DateUtil;

public class AliMicroPay {

	public static Map<String, String> getData() {
		System.out.println("��ʼ��װ֧������ɨ����");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", "8002");
		map.put("orderAmount", "000000000001");
		map.put("channel", "6001");
		map.put("transType", "01");
		map.put("payAccessType", "03");
		map.put("currencyType", "156");
		map.put("merId", "20181018192224192224");
		map.put("merName", "����");
		map.put("orderNumber", "1000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime", DateUtil.getDateStr("yyyyMMddHHmmss"));
//		map.put("discountableAmount", "000000000001");
//		map.put("undiscountableAmount", "000000000002");
		map.put("payType", "15");
		map.put("scene", "bar_code");
		map.put("authCode", "1111111111111111111");
		map.put("subject", "������Ʒ");
		map.put("body", "������Ʒbody");
		map.put("termId", "1000001");
		map.put("operatorId", "10001");
		map.put("storeId", "NJ_001");
		map.put("timeoutExpress", "5m");
//		map.put("alipayStoreId", "֧���������н���������·");
//		map.put("alipayMerchantId", "1000002");
		map.put("authCode", "287852846597602620");
		map.put("alipayMerchantId", "2088000201793862");
		return map;
	}

}
