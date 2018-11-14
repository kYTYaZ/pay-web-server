package com.trade.ali.total;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.ali.trading.AliMicroPay;
import com.trade.ali.trading.AliQuery;
import com.trade.ali.trading.AliRevoke;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.common.Validation;

public class AliTotalPayMicroTest {
	
	@Test
	public void SocketReq() throws Exception {
		
		String authCode ="2868447970622341211";
		String alipayMerchantId = "2088000203474835";
		String merId = "20181019102836102836";
		String orderAmount = "000000000001";
		boolean delayFlag = false;//�Ƿ���Ҫ��ʱ��������̨ ture:��Ҫ  false:����Ҫ
		int delayTime =120;
		
		if(delayFlag){
			CommonUtil.sleepTimeSysout(delayTime);
		}
		
		//��ɨ
		Map<String,String> mapAliMicroPay = AliMicroPay.getData();
		mapAliMicroPay.put(Dict.authCode, authCode);
		mapAliMicroPay.put(Dict.alipayMerchantId, alipayMerchantId);
		mapAliMicroPay.put(Dict.merId, merId);
		mapAliMicroPay.put(Dict.orderAmount, orderAmount);
		String dataAliMicroPay = TransUtil.mapToXml(mapAliMicroPay);
		String reqDataAliMicroPay = CommonUtil.fillString(dataAliMicroPay)+dataAliMicroPay;
		String respDataAliMicroPay = SocketUtil.socketConnect(reqDataAliMicroPay);
		Map<String,String> mapRespAliMicroPay = TransUtil.xmlToMap(respDataAliMicroPay.substring(6));
		Validation.validate(mapRespAliMicroPay, AliValidation.vali_AliMicroPay ,"֧������ɨ����");
		
		Assert.assertEquals("֧������ɨ����ʧ��",mapRespAliMicroPay.get(Dict.respCode), "02");
		
		String orderNumber = mapRespAliMicroPay.get(Dict.orderNumber);
		String orderTime = mapRespAliMicroPay.get(Dict.orderTime);
		
		//���ײ�ѯ
		Map<String,String> mapAliQuery = AliQuery.getData();
		mapAliQuery.put(Dict.orderNumber, orderNumber);
		mapAliQuery.put(Dict.orderTime, orderTime);
		mapAliQuery.put(Dict.merId, merId);
		mapAliQuery.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		String dataAliQuery = TransUtil.mapToXml(mapAliQuery);
		String reqDataAliQuery = CommonUtil.fillString(dataAliQuery)+dataAliQuery;
		String respDataAliQuery = SocketUtil.socketConnect(reqDataAliQuery);
		Map<String,String> mapRespAliQuery = TransUtil.xmlToMap(respDataAliQuery.substring(6));
		Validation.validate(mapRespAliQuery, AliValidation.vali_AliQuery ,"֧������ѯ����");
		
		//���׳���
		Map<String,String> mapAliRevoke = AliRevoke.getData();
		mapAliRevoke.put(Dict.initOrderNumber, orderNumber);
		mapAliRevoke.put(Dict.initOrderTime, orderTime);
		mapAliRevoke.put(Dict.merId, merId);
		mapAliRevoke.put(Dict.orderAmount, orderAmount);
		String dataAliRevoke = TransUtil.mapToXml(mapAliRevoke);
		String reqDataAliRevoke = CommonUtil.fillString(dataAliRevoke)+dataAliRevoke;
		String respDataAliRevoke = SocketUtil.socketConnect(reqDataAliRevoke);
		Map<String,String> mapRespAliRevoke = TransUtil.xmlToMap(respDataAliRevoke.substring(6));
		Validation.validate(mapRespAliRevoke, AliValidation.vali_AliRevoke ,"֧������������");
		
		Assert.assertEquals("֧������������ʧ��",mapRespAliRevoke.get(Dict.respCode), "02");
		
		//���ײ�ѯ
		Map<String,String> mapAliQuery1 = AliQuery.getData();
		mapAliQuery1.put(Dict.orderNumber, orderNumber);
		mapAliQuery1.put(Dict.orderTime, orderTime);
		mapAliQuery1.put(Dict.merId, merId);
		mapAliQuery1.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		String dataAliQuery1 = TransUtil.mapToXml(mapAliQuery1);
		String reqDataAliQuery1 = CommonUtil.fillString(dataAliQuery1)+dataAliQuery1;
		String respDataAliQuery1 = SocketUtil.socketConnect(reqDataAliQuery1);
		Map<String,String> mapRespAliQuery1 = TransUtil.xmlToMap(respDataAliQuery1.substring(6));
		Validation.validate(mapRespAliQuery1, AliValidation.vali_AliQuery ,"֧������ѯ����");
		
	}

}
