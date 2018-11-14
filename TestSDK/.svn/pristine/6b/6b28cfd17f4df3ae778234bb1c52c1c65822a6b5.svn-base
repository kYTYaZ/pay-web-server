package com.trade.socket._interface.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.DateUtil;

public class FSHLQueryThreeCode {

	public static Map<String, String> getData() {
		System.out.println("开始组装丰收家查询三码合一流水报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_3006);
		map.put(Dict.acctNo, "6228580399062449074");
		map.put(Dict.startDate, DateUtil.getDateYYYYMMDD());
		map.put(Dict.endDate, DateUtil.getDateYYYYMMDD());
		map.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
		map.put(Dict.txnSta, SDKConstant.TXN_STA.STA_02);
		map.put(Dict.merId, "998350165130003");
		map.put(Dict.page, "1");
		map.put(Dict.txnChannel, "8001");
		map.put(Dict.txnDetailFlag, "1");
		
		return map;
	}
}
