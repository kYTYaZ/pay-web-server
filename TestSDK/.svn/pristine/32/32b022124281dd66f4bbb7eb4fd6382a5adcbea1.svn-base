package com.trade.c2b.test;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BSPayForMainScavenging;
import com.trade.c2b.trading.C2BSPayQuery;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

public class C2BSPayQueryTest {

	/**
	 * C2B银联二维码被扫消费冲正
	 * @throws IOException 
	 */
	@Test
	public void C2BSPayForMainScavengingTest() {
		
		Map<String,String> reqMapReverse = C2BSPayQuery.getC2BSPayQuery();
		reqMapReverse.put(Dict.txnSeqId, "1006706811");
		reqMapReverse.put(Dict.txnTime, "20181031201306");
		reqMapReverse.put(Dict.qrNo, "https://qr.95516.com/00010000/62012294257471518547363411227021");
		String data = TransUtil.mapToXml(reqMapReverse);
		String reqData = CommonUtil.fillString(data.length())+data;
		SocketUtil.socketConnect(reqData);
		
	}
}
