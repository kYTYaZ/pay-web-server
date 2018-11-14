package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BConsume;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BConsumeTest {

	/**
	 * C2B银联二维码被扫消费
	 * @throws Exception 
	 * 
	 * @throws IOException
	 */
	@Test
	public void C2BConsumn() throws Exception {
		Map<String,String> reqMapConsume = C2BConsume.getC2BData();
//		reqMapConsume.put(Dict.qrNo, mapCreateQrcode.get(Dict.codeUrl));
		reqMapConsume.put(Dict.qrNo, "6221583387322969594");
		reqMapConsume.put(Dict.txnAmt, "2");
		String dataConsume = TransUtil.mapToXml(reqMapConsume);
		String reqDataConsume = CommonUtil.fillString(dataConsume.length()) + dataConsume;
		SocketUtil.socketConnect(reqDataConsume);

	}

}
