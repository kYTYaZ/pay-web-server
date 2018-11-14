package com.trade.posp.test;

import java.util.Map;

import org.junit.Test;

import com.common.dicts.Dict;
import com.trade.posp.trading.PospRefundQuery;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.PospValidation;
import com.validate.common.Validation;

public class PospRefundQueryTest {

	@Test
	public void SocketReq() throws Exception {

		Map<String,String> map = PospRefundQuery.getData();
		map.put(Dict.merId, "20181019102836102836");
		map.put(Dict.txnSeqId, "1006707037");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		String respDataRefund = SocketUtil.socketConnect(reqData);
		Map<String, String> mapRespRefund = TransUtil.xmlToMap(respDataRefund.substring(6));
		Validation.validate(mapRespRefund, PospValidation.vali_PospRefundQuery, "退款查询交易");
	}

}
