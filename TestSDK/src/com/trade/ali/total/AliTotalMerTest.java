package com.trade.ali.total;

import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.ali.trading.AliCreateMer;
import com.trade.ali.trading.AliEditMer;
import com.trade.ali.trading.AliQueryMer;
import com.util.CommonUtil;
import com.util.DateUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.common.Validation;

public class AliTotalMerTest {
	
	@Test
	public void SocketReq() throws Exception {
		
//		String merId = "20181024195724195724";
		String merId = DateUtil.getDateStr("yyyyMMddHHmmssHHmmss");
		String rateChannel = "20";
		
		//商户新增
		Map<String,String> mapAliCreateMer = AliCreateMer.getData();
		mapAliCreateMer.put("merId", merId);
		mapAliCreateMer.put("rateChannel", rateChannel);
//		mapAliCreateMer.remove("rateChannel");
//		mapAliCreateMer.remove("merId");
		String dataAliCreateMer = TransUtil.mapToXml(mapAliCreateMer);
		String reqDataAliCreateMer = CommonUtil.fillString(dataAliCreateMer)+dataAliCreateMer;
		String respDataAliCreateMer = SocketUtil.socketConnect(reqDataAliCreateMer);
		Map<String,String> mapRespAliCreateMer = TransUtil.xmlToMap(respDataAliCreateMer.substring(6));
		Validation.validate(mapRespAliCreateMer, AliValidation.vali_AliCreateMer ,"支付宝商户新增");
		
		String alipayMerchantId = mapRespAliCreateMer.get(Dict.alipayMerchantId);
		
		//商户查询
		Map<String,String> mapAliQueryMer = AliQueryMer.getData();
		mapAliQueryMer.put(Dict.merId, merId);
		mapAliQueryMer.put(Dict.alipayMerchantId, alipayMerchantId);
		String dataAliQueryMer = TransUtil.mapToXml(mapAliQueryMer);
		String reqDataAliQueryMer = CommonUtil.fillString(dataAliQueryMer)+dataAliQueryMer;
		String respDataAliQueryMer = SocketUtil.socketConnect(reqDataAliQueryMer);
		Map<String,String> mapRespAliQueryMer = TransUtil.xmlToMap(respDataAliQueryMer.substring(6));
		Validation.validate(mapRespAliQueryMer, AliValidation.vali_AliQueryMer ,"支付宝商户查询");
		
		//商户修改
		Map<String,String> mapAliEditMer = AliEditMer.getData();
		mapAliEditMer.put(Dict.merId, merId);
		mapAliEditMer.put(Dict.alipayMerchantId, alipayMerchantId);
		String dataAliEditMer = TransUtil.mapToXml(mapAliEditMer);
		String reqDataAliEditMer = CommonUtil.fillString(dataAliEditMer)+dataAliEditMer;
		String respDataAliEditMer = SocketUtil.socketConnect(reqDataAliEditMer);
		Map<String,String> mapRespAliEditMer = TransUtil.xmlToMap(respDataAliEditMer.substring(6));
		Validation.validate(mapRespAliEditMer, AliValidation.vali_AliEditMer ,"支付宝商户修改");
		
		//商户查询
		Map<String,String> mapAliQueryMer1 = AliQueryMer.getData();
		mapAliQueryMer1.put(Dict.merId, merId);
		mapAliQueryMer1.put(Dict.alipayMerchantId, alipayMerchantId);
		String dataAliQueryMer1 = TransUtil.mapToXml(mapAliQueryMer1);
		String reqDataAliQueryMer1 = CommonUtil.fillString(dataAliQueryMer1)+dataAliQueryMer1;
		String respDataAliQueryMer1 = SocketUtil.socketConnect(reqDataAliQueryMer1);
		Map<String,String> mapRespAliQueryMer1 = TransUtil.xmlToMap(respDataAliQueryMer1.substring(6));
		Validation.validate(mapRespAliQueryMer1, AliValidation.vali_AliQueryMer ,"支付宝商户查询");
		
		
	}
	

}
