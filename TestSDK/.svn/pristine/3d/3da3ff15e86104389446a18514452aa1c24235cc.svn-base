package com.trade.wx.total;

import java.io.File;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.wx.trading.WxClose;
import com.trade.wx.trading.WxPreCreate;
import com.trade.wx.trading.WxQuery;
import com.util.CommonUtil;
import com.util.MatrixToImageWriter;
import com.util.PropertyUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.WxValidation;
import com.validate.common.Validation;

public class WxTotalPreCreateTest {
	
	@Test
	public void SocketReq() throws Exception {
		
		String subWxMerId = "242097266";
		String merId = "20180913164630164630";
		int sleepTime = 15 * 1000;
		
		//主扫
		Map<String,String> mapWxPreCreate = WxPreCreate.getData();
		mapWxPreCreate.put(Dict.subWxMerId, subWxMerId);
		mapWxPreCreate.put(Dict.merId, merId);
		String dataWxPreCreate = TransUtil.mapToXml(mapWxPreCreate);
		String reqDataWxPreCreate = CommonUtil.fillString(dataWxPreCreate)+dataWxPreCreate;
		String respDataWxPreCreate = SocketUtil.socketConnect(reqDataWxPreCreate);
		Map<String,String> mapRespWxPreCreate = TransUtil.xmlToMap(respDataWxPreCreate.substring(6));
		Validation.validate(mapRespWxPreCreate, WxValidation.vali_WxPreCreate ,"微信扫码交易");
		
		Assert.assertEquals("微信主扫生成二维码失败",mapRespWxPreCreate.get(Dict.respCode), "02");
		
		//生成二维码
		String codeUrl = mapRespWxPreCreate.get(Dict.codeUrl);
    	//删除路径下所有文件
		MatrixToImageWriter.deleteAll(new File(PropertyUtil.getProperty(Dict.ewmpath)));
		MatrixToImageWriter.createEWM("wxpic", codeUrl, PropertyUtil.getProperty(Dict.ewmpath));
		
		CommonUtil.sleepTime(sleepTime, false);		
		
		String orderNumber = mapRespWxPreCreate.get(Dict.orderNumber);
		String orderTime = mapRespWxPreCreate.get(Dict.orderTime);
		String txnSeqId = mapRespWxPreCreate.get(Dict.txnSeqId);
		String txnTime = mapRespWxPreCreate.get(Dict.txnTime);
		
		//交易查询
//		Map<String,String> mapWxQuery = WxQuery.getData();
//		mapWxQuery.put(Dict.orderNumber, orderNumber);
//		mapWxQuery.put(Dict.orderTime, orderTime);
//		mapWxQuery.put(Dict.merId, merId);
//		mapWxQuery.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_WX);
//		String dataWxQuery = TransUtil.mapToXml(mapWxQuery);
//		String reqDataWxQuery = CommonUtil.fillString(dataWxQuery)+dataWxQuery;
//		String respDataWxQuery = SocketUtil.socketConnect(reqDataWxQuery);
//		Map<String,String> mapRespWxQuery = TransUtil.xmlToMap(respDataWxQuery.substring(6));
//		Validation.validate(mapRespWxQuery, WxValidation.vali_WxQuery ,"微信查询交易");
//		
//		//关闭订单
//		Map<String,String> mapWxClose = WxClose.getData();
//		mapWxClose.put(Dict.initOrderNumber, txnSeqId);
//		mapWxClose.put(Dict.initOrderTime, txnTime);
//		mapWxClose.put(Dict.merId, merId);
//		String dataWxClose = TransUtil.mapToXml(mapWxClose);
//		String reqDataWxClose = CommonUtil.fillString(dataWxClose)+dataWxClose;
//		String respDataWxClose = SocketUtil.socketConnect(reqDataWxClose);
//		Map<String,String> mapRespWxClose = TransUtil.xmlToMap(respDataWxClose.substring(6));
//		Validation.validate(mapRespWxClose, WxValidation.vali_WxClose ,"微信关闭订单");
//		
//		//交易查询
//		Map<String,String> mapWxQuery1 = WxQuery.getData();
//		mapWxQuery1.put(Dict.orderNumber, orderNumber);
//		mapWxQuery1.put(Dict.orderTime, orderTime);
//		mapWxQuery1.put(Dict.merId, merId);
//		mapWxQuery1.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_WX);
//		String dataWxQuery1 = TransUtil.mapToXml(mapWxQuery1);
//		String reqDataWxQuery1 = CommonUtil.fillString(dataWxQuery1)+dataWxQuery1;
//		String respDataWxQuery1 = SocketUtil.socketConnect(reqDataWxQuery1);
//		Map<String,String> mapRespWxQuery1 = TransUtil.xmlToMap(respDataWxQuery1.substring(6));
//		Validation.validate(mapRespWxQuery1, WxValidation.vali_WxQuery ,"微信查询交易");
		
		
	}

}
