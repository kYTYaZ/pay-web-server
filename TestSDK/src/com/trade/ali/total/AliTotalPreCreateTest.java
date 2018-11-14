package com.trade.ali.total;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.ali.trading.AliClose;
import com.trade.ali.trading.AliPreCreate;
import com.trade.ali.trading.AliQuery;
import com.util.CommonUtil;
import com.util.MatrixToImageWriter;
import com.util.PropertyUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.common.Validation;

public class AliTotalPreCreateTest {
	
	@Test
	public void SocketReq() throws Exception {
		
		String alipayMerchantId = "2088000203474835";
		String merId = "20181019102836102836";
		int sleepTime = 15 * 1000;
		
		//主扫
		Map<String,String> mapAliPreCreate = AliPreCreate.getData();
		mapAliPreCreate.put(Dict.alipayMerchantId, alipayMerchantId);
		mapAliPreCreate.put(Dict.merId, merId);
		String dataAliPreCreate = TransUtil.mapToXml(mapAliPreCreate);
		String reqDataAliPreCreate = CommonUtil.fillString(dataAliPreCreate)+dataAliPreCreate;
		String respDataAliPreCreate = SocketUtil.socketConnect(reqDataAliPreCreate);
		Map<String,String> mapRespAliPreCreate = TransUtil.xmlToMap(respDataAliPreCreate.substring(6));
		Validation.validate(mapRespAliPreCreate, AliValidation.vali_AliPreCreate ,"支付扫码交易");
		
		Assert.assertEquals("支付宝主扫生成二维码失败",mapRespAliPreCreate.get(Dict.respCode), "02");
		
		//生成二维码
		String codeUrl = mapRespAliPreCreate.get(Dict.codeUrl);
    	//删除路径下所有文件
		MatrixToImageWriter.deleteAll(new File(PropertyUtil.getProperty(Dict.ewmpath)));
		MatrixToImageWriter.createEWM(codeUrl.replace("https://qr.alipay.com/", ""), codeUrl, PropertyUtil.getProperty(Dict.ewmpath));
		
		CommonUtil.sleepTime(sleepTime, false);		
		
		String orderNumber = mapRespAliPreCreate.get(Dict.orderNumber);
		String orderTime = mapRespAliPreCreate.get(Dict.orderTime);
		String txnSeqId = mapRespAliPreCreate.get(Dict.txnSeqId);
		String txnTime = mapRespAliPreCreate.get(Dict.txnTime);
		
		//交易查询
		Map<String,String> mapAliQuery = AliQuery.getData();
		mapAliQuery.put(Dict.orderNumber, orderNumber);
		mapAliQuery.put(Dict.orderTime, orderTime);
		mapAliQuery.put(Dict.merId, merId);
		mapAliQuery.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		String dataAliQuery = TransUtil.mapToXml(mapAliQuery);
		String reqDataAliQuery = CommonUtil.fillString(dataAliQuery)+dataAliQuery;
		String respDataAliQuery = SocketUtil.socketConnect(reqDataAliQuery);
		Map<String,String> mapRespAliQuery = TransUtil.xmlToMap(respDataAliQuery.substring(6));
		Validation.validate(mapRespAliQuery, AliValidation.vali_AliQuery ,"支付宝查询交易");
		
		//关闭订单
		Map<String,String> mapAliClose = AliClose.getData();
		mapAliClose.put(Dict.initTxnSeqId, txnSeqId);
		mapAliClose.put(Dict.initTxnTime, txnTime);
		mapAliClose.put(Dict.merId, merId);
		String dataAliClose = TransUtil.mapToXml(mapAliClose);
		String reqDataAliClose = CommonUtil.fillString(dataAliClose)+dataAliClose;
		String respDataAliClose = SocketUtil.socketConnect(reqDataAliClose);
		Map<String,String> mapRespAliClose = TransUtil.xmlToMap(respDataAliClose.substring(6));
		Validation.validate(mapRespAliClose, AliValidation.vali_AliClose ,"支付宝关闭订单");
		
		//交易查询
		Map<String,String> mapAliQuery1 = AliQuery.getData();
		mapAliQuery1.put(Dict.orderNumber, orderNumber);
		mapAliQuery1.put(Dict.orderTime, orderTime);
		mapAliQuery1.put(Dict.merId, merId);
		mapAliQuery1.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		String dataAliQuery1 = TransUtil.mapToXml(mapAliQuery1);
		String reqDataAliQuery1 = CommonUtil.fillString(dataAliQuery1)+dataAliQuery1;
		String respDataAliQuery1 = SocketUtil.socketConnect(reqDataAliQuery1);
		Map<String,String> mapRespAliQuery1 = TransUtil.xmlToMap(respDataAliQuery1.substring(6));
		Validation.validate(mapRespAliQuery1, AliValidation.vali_AliQuery ,"支付宝查询交易");
		
	
		
		
	}

}
