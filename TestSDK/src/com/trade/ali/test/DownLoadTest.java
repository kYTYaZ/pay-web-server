package com.trade.ali.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.common.constants.SDKConstant;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class DownLoadTest {
	
	@Test
	public void SocketReq() {
//		Map<String,String> map = AliClose.getData();
		
		Map<String,String >map= new HashMap<String, String>();
		map.put("txnCode",SDKConstant.TXN_CODE.ALIPAY_BILL_DOWN);
		map.put("transType", "1001");
		map.put("billType", "02");
		map.put("alipayBillDate", "20181007");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		SocketUtil.socketConnect(reqData);
		
	}

}
