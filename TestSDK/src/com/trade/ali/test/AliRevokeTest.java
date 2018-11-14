package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliRevoke;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class AliRevokeTest {
	@Test
	public void SocketReq() {
		Map<String,String> map = AliRevoke.getData();
		map.put("initOrderNumber", "100020181019085606");
		map.put("initOrderTime", "20181019085606");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}
}
