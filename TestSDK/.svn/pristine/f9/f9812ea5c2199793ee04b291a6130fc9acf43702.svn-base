package com.trade.wx.test;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import com.common.constants.SDKConstant;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class Test1 {

	@Test
	public void refreshMoreConfig() {
		System.out.println("刷新多费率配置");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", SDKConstant.TXN_CODE.MORE_FEE_CONFIG);

		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);
	}
}
