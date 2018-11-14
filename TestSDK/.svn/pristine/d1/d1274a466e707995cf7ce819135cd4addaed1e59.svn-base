package com.trade.posp.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.dicts.Dict;
import com.util.DateUtil;

public class PospRefund {
	
	public static Map<String, String> getData() {
		System.out.println("开始组装退款报文");
		
		Map<String,String >map= new HashMap<String, String>();
		map.put(Dict.txnCode,"3008");
		map.put(Dict.merId, "20181024195724195724");
//		map.put(Dict.initTxnSeqId, "111111111");
//		map.put(Dict.initOrderNumber, "222222222222222");
		map.put(Dict.refundAmount, "000000000001");
		map.put(Dict.refundReason, "七天无理由退款");
		map.put(Dict.outRefundNo, "2000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put(Dict.outRefundTime, DateUtil.getDateStr("yyyyMMddHHmmss"));
//		map.put(Dict.outRefundNo, "200020181106143403");
//		map.put(Dict.outRefundTime, "20181106143403");
		
		return map;
		
	}

}
