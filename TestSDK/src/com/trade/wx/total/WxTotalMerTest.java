package com.trade.wx.total;

import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.wx.trading.WxQueryMer;
import com.util.CommonUtil;
import com.util.DateUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.WxValidation;
import com.validate.common.Validation;

public class WxTotalMerTest {
	
	@Test
	public void SocketReq() throws Exception {
		
		String merId = DateUtil.getDateStr("yyyyMMddHHmmssHHmmss");
//		String merId = "20180913160905160905";
		String rateChannel = "20";
		String mchtRemark = DateUtil.getDateStr("yyyyMMddHHmmssHHmmss");
		
		//商户新增e
		Map<String,String> mapWxCreateMer = null;//WxCreateMer.getData();
		mapWxCreateMer.put("merId", merId);
		mapWxCreateMer.put("mchtRemark", mchtRemark);
		mapWxCreateMer.put("rateChannel", rateChannel);
//		mapWxCreateMer.remove("rateChannel");
		String dataWxCreateMer = TransUtil.mapToXml(mapWxCreateMer);
		String reqDataWxCreateMer = CommonUtil.fillString(dataWxCreateMer)+dataWxCreateMer;
		String respDataWxCreateMer = SocketUtil.socketConnect(reqDataWxCreateMer);
		Map<String,String> mapRespWxCreateMer = TransUtil.xmlToMap(respDataWxCreateMer.substring(6));
		Validation.validate(mapRespWxCreateMer, WxValidation.vali_WxCreateMer ,"微信商户新增");
		
		String subMchId = mapRespWxCreateMer.get(Dict.subMchId);
		
		//商户查询
//		Map<String,String> mapWxQueryMer = WxQueryMer.getData();
//		mapWxQueryMer.put(Dict.merId, merId);
//		mapWxQueryMer.put(Dict.subMchId, subMchId);
//		String dataWxQueryMer = TransUtil.mapToXml(mapWxQueryMer);
//		String reqDataWxQueryMer = CommonUtil.fillString(dataWxQueryMer)+dataWxQueryMer;
//		String respDataWxQueryMer = SocketUtil.socketConnect(reqDataWxQueryMer);
//		Map<String,String> mapRespWxQueryMer = TransUtil.xmlToMap(respDataWxQueryMer.substring(6));
//		Validation.validate(mapRespWxQueryMer, WxValidation.vali_WxQueryMer ,"微信商户查询");
		
		
	}
	

}
