package com.trade.socket._interface.test;

import java.util.Map;

import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.socket._interface.trading.FSHLQueryThreeCode;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

/**
 * 单线程：丰收家查询三码合一
 * @author Administrator
 *
 */
public class FSHLQueryThreeCodeTest {

	@Test
	public void SocketReq() {
		
		Map<String,String> map = FSHLQueryThreeCode.getData();
		map.put(Dict.acctNo, "6228580199023786188");
		map.put(Dict.startDate, "20180505");
		map.put(Dict.endDate, "20180509");
		map.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		map.put(Dict.txnSta, SDKConstant.TXN_STA.STA_02);
		map.put(Dict.merId, "99900000000000000036");
		map.put(Dict.page, "1");
		map.put(Dict.txnDetailFlag, "1");
		
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data.length())+data;
		SocketUtil.socketConnect(reqData);
		
	}
	
	

}
