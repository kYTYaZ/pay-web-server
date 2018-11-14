package com.trade.c2b.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.util.HttpUtil;

public class C2BNotifyTest {


	/**
	 * C2B银联二维码模拟通知接口
	 * @throws Exception 
	 * @throws IOException 
	 */
	@Test
	public void C2BNotify() throws Exception {
		
		String url = "http://158.222.72.110:8080/PayWebServer/pay/cups/recvCupsNotifyReq.do";
		Map<String,String> map = new HashMap<String, String>();
		map.put(Dict.currencyCode, "156");
		map.put(Dict.reqType, "0230000903");
		map.put(Dict.transAddnInfo, "e21lcklkPTc3NzI5MDA1ODEzNTg4MCZtZXJOYW1lPTM2MeW6piZhY3FDb2RlPTE0MjkzMzEwfQ==");
		map.put(Dict.signature, "ZR43BMqgO1L+5IsLTM6hB6cx2/qnNm0GLO9qF7RLt1IunPcMVQCiFfTWt0ZHS0k98Qv01USc267kOKye2tB50+I2vPblXKy8HfYJXA7CpK6EkMO64KRVPES9/xMC8V4h6b9QLU64nZkkM5Lc5ZUXWim30H2kU+Wrh66cVObIr4yF8OjUi98LBYUE+uSzCaXkxaAYYS7i8WrrJTsMtrxsrvgS3pFzHaRbZdL9O5nqc7MnJOa67lvYG4v7qP49HSQmQ5QscUkNjtyCipoIB6We25q8ZpN0alWi/GK46dRodiACRsRKTYNPg5oh/6YhmOZ1m3d9lMIoA7MajzTg4qxy8g==");
		map.put(Dict.voucherNum, "6223615353782493344");
		map.put(Dict.payerInfo, "tJcq16tO49WZNlIv7rif1qan9UCohHXPfeyOhBpX1CmQWU7x1EsJSYsF0MErhkkLBu6BpgzL7IGhdPeayj4xGQbLtqvWtEgN1ykeoVHJE/bC1nfoPelilqZxiHh0qfDC0aiIDoWFTZuwogjeeuxUah3TzFBoBpxe/uh5qFDCwdUv4G4guBRyKyp3ITr5qOS86cuMWYDMvjX6YYUHM46lChs9/xz1pnBZsRV5cBGD2hBodBamnMX2HJ/+5RufEmLBrR1eY2bq4yYsDu2y1EaTGX6Z9bQODAbN7LU2y4iR0qXxmwPl/9xS2N2yqfsVFSIOUO1nlIsmaeLPPic9hThVlg==");
		map.put(Dict.qrNo, "6220176094562736658");
		map.put(Dict.certId, "69026276696");
		map.put(Dict.version, "1.0.0");
		map.put(Dict.txnAmt, "60");
		map.put(Dict.encryptCertId, "68759529225");
		
		
		String respMsg = HttpUtil.httpRequest(url, map);
		System.out.println(respMsg);
		
		
	}

}
