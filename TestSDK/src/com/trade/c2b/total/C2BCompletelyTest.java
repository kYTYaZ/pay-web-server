package com.trade.c2b.total;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.c2b.trading.C2BConsume;
import com.trade.c2b.trading.C2BQuery;
import com.trade.c2b.trading.C2BReverse;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.C2BValidation;
import com.validate.common.Validation;

public class C2BCompletelyTest {


	@Test
	public void C2BComplete() throws Exception {
		
//		//c2b申请二维码服务
//		Map<String,String> reqMapCreateQrcode = C2BCreateQrcode.getC2BCreateQrcode();
//		reqMapCreateQrcode.put(Dict.maxAmont, "60");
//		String data = TransUtil.mapToXml(reqMapCreateQrcode);
//		String reqData = CommonUtil.fillString(data.getBytes().length) + data;
//		String respDataCreateQrcode = SocketUtil.socketConnect(reqData);
//		Map<String,String> mapCreateQrcode = TransUtil.xmlToMap(respDataCreateQrcode.substring(6));
//		String qrNo = mapCreateQrcode.get(Dict.codeUrl);
//		if(StringUtil.isEmpty(qrNo)){
//			System.out.println("申请二维码返回异常");
//			return;
//		}
//		CommonUtil.sleepTime(2*1000);
		
		//c2b消费
		Map<String,String> reqMapConsume = C2BConsume.getC2BData();
//		reqMapConsume.put(Dict.qrNo, mapCreateQrcode.get(Dict.codeUrl));
		reqMapConsume.put(Dict.qrNo, "6225796905755972718");
		reqMapConsume.put(Dict.txnAmt, "1");
		String dataConsume = TransUtil.mapToXml(reqMapConsume);
		String reqDataConsume = CommonUtil.fillString(dataConsume.length()) + dataConsume;
		String respDataConsume = SocketUtil.socketConnect(reqDataConsume);
		Map<String,String> mapConsume = TransUtil.xmlToMap(respDataConsume.substring(6));
		Validation.validate(mapConsume, C2BValidation.vali_consume ,"C2B消费交易");
		
		Assert.assertEquals("消费交易失败", SDKConstant.TXN_STA.STA_01 ,mapConsume.get(Dict.respCode));
		
		
		//查询消费订单
		int num = 1;
		while(true){
			if(num == 3){
				break;
			}
			CommonUtil.sleepTime(5*1000);
			Map<String,String> reqMapQuery = C2BQuery.getQueryData();
			reqMapQuery.put(Dict.orderNo, reqMapConsume.get(Dict.orderNo));
			reqMapQuery.put(Dict.orderTime, reqMapConsume.get(Dict.orderTime));
			String dataQuery = TransUtil.mapToXml(reqMapQuery);
			String reqDataQuery = CommonUtil.fillString(dataQuery.length()) + dataQuery;
			String respDataQuery = SocketUtil.socketConnect(reqDataQuery);
			Map<String,String> mapQuery = TransUtil.xmlToMap(respDataQuery.substring(6));
			Validation.validate(mapQuery, C2BValidation.vali_query, "查询交易");
			if(mapQuery.get(Dict.respCode).equals(SDKConstant.TXN_STA.STA_02)){
				System.out.println("第"+num+"次查出结果");
				break;
//				System.exit(0);
			}
			num++;
		}
		
		CommonUtil.sleepTime(5*1000);

		
		//若结果不正确，冲正
		Map<String,String> reqMapReverse = C2BReverse.getReverseData();
		reqMapReverse.put(Dict.initOrderNo, reqMapConsume.get(Dict.orderNo));
		reqMapReverse.put(Dict.initOrderTime, reqMapConsume.get(Dict.orderTime));
		String dataReverse = TransUtil.mapToXml(reqMapReverse);
		String reqDataReverse = CommonUtil.fillString(dataReverse.length()) + dataReverse;
		String respDataReverse = SocketUtil.socketConnect(reqDataReverse);
		Map<String,String> mapReverse = TransUtil.xmlToMap(respDataReverse.substring(6));
		Validation.validate(mapReverse, C2BValidation.vali_reverse, "冲正交易");
		
		CommonUtil.sleepTime(2*1000);
		
		
//		//查询冲正订单
//		Map<String,String> reqMapQuery = C2BQuery.getQueryData();
//		reqMapQuery.put(Dict.orderNo, reqMapReverse.get(Dict.orderNo));
//		reqMapQuery.put(Dict.orderTime, reqMapReverse.get(Dict.orderTime));
//		String dataQuery = TransUtil.mapToXml(reqMapQuery);
//		String reqDataQuery = CommonUtil.fillString(dataQuery.length()) + dataQuery;
//		String respDataQuery = SocketUtil.socketConnect(reqDataQuery);
//		Map<String,String> mapQuery = TransUtil.xmlToMap(respDataQuery.substring(6));
//		Validation.validate(mapQuery, ValidationUtil.vali_query, "查询交易");

	}

}
