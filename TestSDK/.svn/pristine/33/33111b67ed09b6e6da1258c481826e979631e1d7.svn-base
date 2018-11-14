package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliQuery;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.common.Validation;

public class AliQueryTest {
	@Test
	public void SocketReq() throws Exception {
		Map<String,String> map = AliQuery.getData();
		map.put("merId", "20181019102836102836");
		map.put("payAccessType", "03");
		map.put("orderNumber", "100020181019140801");
		map.put("orderTime", "20181019140801");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		String respDataAliQuery = SocketUtil.socketConnect(reqData);
		Map<String,String> mapRespAliQuery = TransUtil.xmlToMap(respDataAliQuery.substring(6));
		Validation.validate(mapRespAliQuery, AliValidation.vali_AliQuery ,"支付宝查询交易");
		
	}
}
