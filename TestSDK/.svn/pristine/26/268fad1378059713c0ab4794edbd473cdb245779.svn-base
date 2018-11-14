package com.trade.wx.total;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.wx.trading.WxMicroPay;
import com.trade.wx.trading.WxQuery;
import com.trade.wx.trading.WxRevoke;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.WxValidation;
import com.validate.common.Validation;

public class WxTotalPayMicroTest {
	
	@Test
	public void SocketReq() throws Exception {
		
		String authCode = "135626566097280791";
		String subWxMerId = "242097266";
		String merId = "20180913164630164630";
		String orderAmount = "000000000001";
		
		//被扫
		Map<String,String> mapWxMicroPay = WxMicroPay.getData();
		mapWxMicroPay.put(Dict.authCode, authCode);
		mapWxMicroPay.put(Dict.subWxMerId, subWxMerId);
		mapWxMicroPay.put(Dict.merId, merId);
		mapWxMicroPay.put(Dict.orderAmount, orderAmount);
		String dataWxMicroPay = TransUtil.mapToXml(mapWxMicroPay);
		String reqDataWxMicroPay = CommonUtil.fillString(dataWxMicroPay)+dataWxMicroPay;
		String respDataWxMicroPay = SocketUtil.socketConnect(reqDataWxMicroPay);
		Map<String,String> mapRespWxMicroPay = TransUtil.xmlToMap(respDataWxMicroPay.substring(6));
		Validation.validate(mapRespWxMicroPay, WxValidation.vali_WxMicroPay ,"微信被扫交易");
		
//		Assert.assertEquals("微信被扫交易失败",mapRespWxMicroPay.get(Dict.respCode), "02");
		
		String orderNumber = mapRespWxMicroPay.get(Dict.orderNumber);
		String orderTime = mapRespWxMicroPay.get(Dict.orderTime);
		
		//交易查询
		Map<String,String> mapWxQuery = WxQuery.getData();
		mapWxQuery.put(Dict.orderNumber, orderNumber);
		mapWxQuery.put(Dict.orderTime, orderTime);
		mapWxQuery.put(Dict.merId, merId);
		mapWxQuery.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_WX);
		String dataWxQuery = TransUtil.mapToXml(mapWxQuery);
		String reqDataWxQuery = CommonUtil.fillString(dataWxQuery)+dataWxQuery;
		String respDataWxQuery = SocketUtil.socketConnect(reqDataWxQuery);
		Map<String,String> mapRespWxQuery = TransUtil.xmlToMap(respDataWxQuery.substring(6));
		Validation.validate(mapRespWxQuery, WxValidation.vali_WxQuery ,"微信查询交易");
		
		//交易撤销
		Map<String,String> mapWxRevoke = WxRevoke.getData();
		mapWxRevoke.put(Dict.initOrderNumber, orderNumber);
		mapWxRevoke.put(Dict.initOrderTime, orderTime);
		mapWxRevoke.put(Dict.merId, merId);
		mapWxRevoke.put(Dict.orderAmount, orderAmount);
		String dataWxRevoke = TransUtil.mapToXml(mapWxRevoke);
		String reqDataWxRevoke = CommonUtil.fillString(dataWxRevoke)+dataWxRevoke;
		String respDataWxRevoke = SocketUtil.socketConnect(reqDataWxRevoke);
		Map<String,String> mapRespWxRevoke = TransUtil.xmlToMap(respDataWxRevoke.substring(6));
		Validation.validate(mapRespWxRevoke, WxValidation.vali_WxRevoke ,"微信撤销交易");
		
		Assert.assertEquals("微信撤销交易失败",mapRespWxRevoke.get(Dict.respCode), "02");
		
		//交易查询
		Map<String,String> mapWxQuery1 = WxQuery.getData();
		mapWxQuery1.put(Dict.orderNumber, orderNumber);
		mapWxQuery1.put(Dict.orderTime, orderTime);
		mapWxQuery1.put(Dict.merId, merId);
		mapWxQuery1.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_WX);
		String dataWxQuery1 = TransUtil.mapToXml(mapWxQuery1);
		String reqDataWxQuery1 = CommonUtil.fillString(dataWxQuery1)+dataWxQuery1;
		String respDataWxQuery1 = SocketUtil.socketConnect(reqDataWxQuery1);
		Map<String,String> mapRespWxQuery1 = TransUtil.xmlToMap(respDataWxQuery1.substring(6));
		Validation.validate(mapRespWxQuery1, WxValidation.vali_WxQuery ,"微信查询交易");
		
	}

}
