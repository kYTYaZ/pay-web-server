package com.trade.socket.manual.test;

import java.util.Map;

import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.google.gson.Gson;
import com.trade.socket.manual.trading.QuerySettleUnknowStauts;
import com.util.CommonUtil;
import com.util.SocketUtil;

public class QuerySettleUnknowStautsTest {

	/**
	 * ≤È—ØT+0Œ¥»Î’À
	 */
	@Test
	public void SocketReq() {

		Map<String, String> map = QuerySettleUnknowStauts.getData();
		map.put(Dict.orderDate, "20180719");
		map.put(Dict.txnTmStart, "090000");
		map.put(Dict.txnTmEnd, "175959");
		Gson gson = new Gson();
		String gsonStr = gson.toJson(map);

		String data = SDKConstant.PREFIX.T0_ACCOUNTED + gsonStr;
		String reqData = CommonUtil.fillString(data.length()) + data;
		SocketUtil.socketConnect(reqData);

	}

}
