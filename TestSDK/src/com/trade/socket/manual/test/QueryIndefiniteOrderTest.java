package com.trade.socket.manual.test;

import java.util.Map;

import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.google.gson.Gson;
import com.trade.socket.manual.trading.QueryIndefiniteOrder;
import com.util.CommonUtil;
import com.util.SocketUtil;

public class QueryIndefiniteOrderTest {

	/**
	 * 定时查询状态为(01,06)的订单状态流程
	 */
	@Test
	public void SocketReq() {

		Map<String, String> map = QueryIndefiniteOrder.getData();
		map.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_WX);
		map.put(Dict.orderDate, "20181015");
		map.put(Dict.txnTmStart, "000000");
		map.put(Dict.txnTmEnd, "230000");
		Gson gson = new Gson();
		String gsonStr = gson.toJson(map);

		String data = SDKConstant.PREFIX.TC_QUERY_ORDER + gsonStr;
//		String data = "wxbill20180920";
		
		String reqData = CommonUtil.fillString(data.length()) + data;
		SocketUtil.socketConnect(reqData);

	}

}
