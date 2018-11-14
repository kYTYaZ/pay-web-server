package com.trade.socket.manual.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.dicts.Dict;
import com.util.DateUtil;

/**
 * ��ѯT+0δ����
 * @author Administrator
 *
 */
public class QuerySettleUnknowStauts {
	
	public static Map<String, String> getData() {
		System.out.println("��ʱ��ѯT+0δ����");
		
		String orderDate = DateUtil.format(DateUtil.addHour(-2),"yyyyMMdd");
		String txnTmStart = DateUtil.format(DateUtil.addHour(-2),"HH")+"0000";
		String txnTmEnd = DateUtil.format(DateUtil.addHour(-2),"HH")+"5959";
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.orderDate, orderDate);
		map.put(Dict.txnTmStart, txnTmStart);
		map.put(Dict.txnTmEnd, txnTmEnd);
		
		return map;
	}

}
