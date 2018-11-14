package com.trade.posp.test;

import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.posp.trading.PospRefund;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.PospValidation;
import com.validate.common.Validation;

public class PospRefundTest {

	@Test
	public void SocketReq() throws Exception {
		
		Map<String,String> map = PospRefund.getData();
		map.put(Dict.merId, "20181019102836102836");
		map.put(Dict.initTxnSeqId, "1006707044");
//		map.put(Dict.initOrderNumber, "222222222222222");
		map.put(Dict.refundAmount, "000000000001");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		String respDataRefund = SocketUtil.socketConnect(reqData);
		Map<String,String> mapRespRefund = TransUtil.xmlToMap(respDataRefund.substring(6));
		Validation.validate(mapRespRefund, PospValidation.vali_PospRefund ,"ÍË¿î½»Ò×");
		
	}
	
}
