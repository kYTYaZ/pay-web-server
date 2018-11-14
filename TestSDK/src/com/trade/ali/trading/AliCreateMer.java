package com.trade.ali.trading;

import java.util.HashMap;
import java.util.Map;

import com.common.constants.SDKConstant;
import com.util.DateUtil;

public class AliCreateMer {

	public static Map<String, String> getData() {
		System.out.println("开始组装支付宝商户新增报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", SDKConstant.TXN_CODE.CODE_8003); 
		map.put("channel", "6001");
		map.put("merId", DateUtil.getDateStr("yyyyMMddHHmmssHHmmss"));
		map.put("name", "客户81009041035");
		map.put("aliasName", "客户81009041035");
		map.put("servicePhone", "15661219999");
		map.put("contactName", "测试人员");
		map.put("contactType", "LEGAL_PERSON");
		map.put("idCardNo", "6228580107000000861");
		map.put("categoryId", "2016042200000148");
		map.put("provinceCode", "370000");
		map.put("cityCode", "371000");
		map.put("districtCode", "371002");
		map.put("address", "浙江省杭州市江干区秋涛路322号");
		map.put("rateChannel", "20");
		map.put("memo", "aaaaaaaaaaa");
		map.put("mcc", "5331");
		map.put("orgPid", "2088721382101609");

		return map;
	}

}
