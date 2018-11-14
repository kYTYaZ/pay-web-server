package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliMicroPay;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class AliMicroPayTest {
	
	@Test
	public void SocketReq() {
		
		Map<String,String> map = AliMicroPay.getData();
		map.put("authCode", "285357203040305982");
		map.put("alipayMerchantId", "2088000201793862");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}

}
