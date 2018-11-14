package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BReverse;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BReverseTest {


	/**
	 * C2B银联二维码被扫消费冲正
	 * @throws IOException 
	 */
	@Test
	public void C2BReverse() {
		
		Map<String,String> reqMapReverse = C2BReverse.getReverseData();
		reqMapReverse.put(Dict.initOrderTime, "201804104725");
		reqMapReverse.put(Dict.initOrderNo, "04100104725");
		String data = TransUtil.mapToXml(reqMapReverse);
		String reqData = CommonUtil.fillString(data.length())+data;
		SocketUtil.socketConnect(reqData);
		
	}

}
