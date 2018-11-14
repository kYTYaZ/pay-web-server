package com.trade.ali.test;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.ali.trading.AliPreCreate;
import com.util.CommonUtil;
import com.util.MatrixToImageWriter;
import com.util.PropertyUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.common.Validation;

public class AliPreCreateTest {
	
	@Test
	public void SocketReq() throws Exception {
		String alipayMerchantId = "2088131944076999";
		String merId = "20180713111015111015";
		
		//��ɨ
		Map<String,String> mapAliPreCreate = AliPreCreate.getData();
		mapAliPreCreate.put(Dict.authCode, alipayMerchantId);
		mapAliPreCreate.put(Dict.alipayMerchantId, alipayMerchantId);
		mapAliPreCreate.put(Dict.merId, merId);
		String dataAliPreCreate = TransUtil.mapToXml(mapAliPreCreate);
		String reqDataAliPreCreate = CommonUtil.fillString(dataAliPreCreate)+dataAliPreCreate;
		String respDataAliPreCreate = SocketUtil.socketConnect(reqDataAliPreCreate);
		Map<String,String> mapRespAliPreCreate = TransUtil.xmlToMap(respDataAliPreCreate.substring(6));
		Validation.validate(mapRespAliPreCreate, AliValidation.vali_AliPreCreate ,"֧��ɨ�뽻��");
		
		Assert.assertEquals("֧������ɨ���ɶ�ά��ʧ��",mapRespAliPreCreate.get(Dict.respCode), "02");
		
		//���ɶ�ά��
		String codeUrl = mapRespAliPreCreate.get(Dict.codeUrl);
    	//ɾ��·���������ļ�
		MatrixToImageWriter.deleteAll(new File(PropertyUtil.getProperty(Dict.ewmpath)));
		MatrixToImageWriter.createEWM(codeUrl.replace("https://qr.alipay.com/", ""), codeUrl, PropertyUtil.getProperty(Dict.ewmpath));
		
		
	}
	
	

}
