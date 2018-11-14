package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BCreateQrcode;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BCreateQrcodeTest {

	/**
	 * C2B银联二维码申请二维码服务
	 * 
	 * @throws IOException
	 */
	@Test
	public void C2BConsumn() {
		//C2B银联二维码申请二维码服务
		Map<String,String> reqMapCreateQrcode = C2BCreateQrcode.getC2BCreateQrcode();
		reqMapCreateQrcode.put(Dict.maxAmont, "200");
		String data = TransUtil.mapToXml(reqMapCreateQrcode);
		String reqData = CommonUtil.fillString(data.getBytes().length) + data;
		SocketUtil.socketConnect(reqData);

	}

}
