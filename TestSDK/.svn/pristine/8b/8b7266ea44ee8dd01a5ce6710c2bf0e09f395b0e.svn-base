package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliClose;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class AliCloseTest {
	
	@Test
	public void SocketReq() {
		Map<String,String> map = AliClose.getData();
		map.put("initTxnSeqId", "1006706405");
		map.put("initTxnTime", "20181018193444");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}

}
