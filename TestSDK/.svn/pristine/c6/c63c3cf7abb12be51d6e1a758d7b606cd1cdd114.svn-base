package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliCreateMer;
import com.util.CommonUtil;
import com.util.DateUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class AliCreateMerTest {

	@Test
	public void SocketReq() {
		
		String merId = DateUtil.getDateStr("yyyyMMddHHmmssHHmmss");
		String rateChannel = "20";
		
		Map<String,String> map = AliCreateMer.getData();
		map.put("merId", merId);
		map.put("rateChannel", rateChannel);
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}
	
}
