package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliEditMer;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class AliEditMerTest {
	@Test
	public void SocketReq() {
		Map<String,String> map = AliEditMer.getData();
		map.put("merId", "20181018192224192224");
		map.put("alipayMerchantId", "2088000201793862");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}
}
