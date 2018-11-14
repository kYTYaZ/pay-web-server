package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.util.CommonUtil;
import com.util.DateUtil;

/**
 * �����������ױ�����װ
 * 
 * @author Administrator
 * 
 */
public class C2BReverse {

	public static Map<String, String> getReverseData() {
		System.out.println("��ʼ��װ�����ӿڱ���");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_9002);
		map.put(Dict.orderTime, DateUtil.getCurrentDateTime());
		map.put(Dict.orderNo, CommonUtil.getOrderNo());
		map.put(Dict.initOrderTime, "20180408095824");
		map.put(Dict.initOrderNo, "0498095824");
		map.put(Dict.merId, "777920058135880");

//		System.out.println("����������:"+map.toString());
		return map;
	}
	

}
