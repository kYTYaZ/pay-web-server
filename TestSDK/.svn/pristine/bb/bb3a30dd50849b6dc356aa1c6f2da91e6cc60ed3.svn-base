package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliTransfer;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class AliTransferTest {
	
	@Test
	public void SocketReq() {
		Map<String,String> map = AliTransfer.getData();
		map.put("merId", "20181030090023090023");
		map.put("alipayMerchantId", "2088000294772492");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}

}
