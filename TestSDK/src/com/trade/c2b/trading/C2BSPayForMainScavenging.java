package com.trade.c2b.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;

public class C2BSPayForMainScavenging {

	public static Map<String, String> getC2BSPayForMainScavenging() {
		System.out.println("��ʼ��װ�����ӿڱ���");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put(Dict.txnCode, SDKConstant.TXN_CODE.CODE_4002);
		map.put(Dict.channel, "6005");
		return map;
	}
}
