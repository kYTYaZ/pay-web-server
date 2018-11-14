package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BMarketQuery;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BMarketQueryTest {

	/**
	 * C2B银联二维码申请二维码服务
	 * 
	 * @throws IOException
	 */
	@Test
	public void C2BMarketQuery() {
		//C2B银联二维码申请二维码服务
		Map<String,String> reqMapCreateQrcode = C2BMarketQuery.getC2BMarketQuery();
		reqMapCreateQrcode.put(Dict.txnSeqId, "1006707265");
		reqMapCreateQrcode.put(Dict.txnTime, "20181108164102");
		reqMapCreateQrcode.put(Dict.orderAmount, "000000000191");
		String data = TransUtil.mapToXml(reqMapCreateQrcode);
		String reqData = CommonUtil.fillString(data.getBytes().length) + data;
		SocketUtil.socketConnect(reqData);

	}
}
