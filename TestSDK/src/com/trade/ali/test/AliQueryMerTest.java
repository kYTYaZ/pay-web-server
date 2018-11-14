package com.trade.ali.test;

import java.util.Map;

import org.junit.Test;

import com.trade.ali.trading.AliQueryMer;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.common.Validation;

public class AliQueryMerTest {

	@Test
	public void SocketReq() throws Exception {
		
		Map<String,String> map = AliQueryMer.getData();
		map.put("merId", "20181024195724195724");
		map.put("alipayMerchantId", "2088000263035056");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		String respDataAliQueryMer = SocketUtil.socketConnect(reqData);
		Map<String,String> mapRespAliQueryMer = TransUtil.xmlToMap(respDataAliQueryMer.substring(6));
		Validation.validate(mapRespAliQueryMer, AliValidation.vali_AliQueryMer ,"支付宝商户查询");
		
	}
	
}
