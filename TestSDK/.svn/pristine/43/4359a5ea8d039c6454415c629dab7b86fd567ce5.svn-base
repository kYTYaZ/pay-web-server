package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BSPayForMainScavenging;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BSPayForMainScavengingTest {

	/**
	 * C2B银联二维码被扫消费冲正
	 * @throws IOException 
	 */
	@Test
	public void C2BSPayForMainScavengingTest() {
		
		Map<String,String> reqMapReverse = C2BSPayForMainScavenging.getC2BSPayForMainScavenging();
		reqMapReverse.put(Dict.codeUrl, "https://qr.95516.com/00010000/62242445447443906678917955424424");
		reqMapReverse.put(Dict.txnSeqId,"1006706941");
		reqMapReverse.put(Dict.txnTime,"20181105160048");
		reqMapReverse.put(Dict.txnSta,"01");
		reqMapReverse.put(Dict.orderNumber,"20181029185843");
		reqMapReverse.put(Dict.orderTime,"20181030083707");
		reqMapReverse.put(Dict.orderAmount,"000000000015");
		reqMapReverse.put("orderDesc","aa");
		reqMapReverse.put("orderCode","02");
		reqMapReverse.put("orderType","10");
		String data = TransUtil.mapToXml(reqMapReverse);
		String reqData = CommonUtil.fillString(data.length())+data;
		SocketUtil.socketConnect(reqData);
		
	}
}
