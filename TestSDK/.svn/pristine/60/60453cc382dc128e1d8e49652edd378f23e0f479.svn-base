package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.trade.c2b.trading.C2BScanedUnified;

public class C2BScanedUnifiedCreate {

	/**
	 * C2B银联二维码查询订单
	 * @throws IOException 
	 */
	@Test
	public void C2BScanedUnified() {
		
		Map<String,String> reqMapReverse = C2BScanedUnified.getC2BScanedUnified();
		reqMapReverse.put(Dict.codeUrl, "https://qr.95516.com/00010000/621657666234388826");
		String data = TransUtil.mapToXml(reqMapReverse);
		String reqData = CommonUtil.fillString(data.length())+data;
		SocketUtil.socketConnect(reqData);
		
	}
}
