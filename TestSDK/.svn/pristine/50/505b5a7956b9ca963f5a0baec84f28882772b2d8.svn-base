package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BQuery;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BQueryTest {


	/**
	 * C2B银联二维码被扫消费冲正
	 * @throws IOException 
	 */
	@Test
	public void C2BReverse() {
		
		Map<String,String> reqMapQuery = C2BQuery.getQueryData();
		reqMapQuery.put(Dict.qrNo, "6227520738923344502");
		reqMapQuery.put(Dict.orderTime, "20181030155643");
		reqMapQuery.put(Dict.orderNo, "10303155643");
		String data = TransUtil.mapToXml(reqMapQuery);
		String reqData = CommonUtil.fillString(data.length())+data;
		SocketUtil.socketConnect(reqData);
		
	}

}
