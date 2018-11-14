package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.CommonUtil;
import com.util.DateUtil;

/**
 * 银联查询订单
 * 
 * @author Administrator
 * 
 */
public class C2BQuery {

	public static Map<String, String> getQueryData() {
		System.out.println("开始组装查询接口报文");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_9003);
		map.put(Dict.orderTime, DateUtil.getCurrentDateTime());
		map.put(Dict.orderNo, CommonUtil.getOrderNo());
		
//		System.out.println("查询请求报文:"+map.toString());
		return map;
	}
	

}
