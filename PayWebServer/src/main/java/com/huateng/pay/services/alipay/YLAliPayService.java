package com.huateng.pay.services.alipay;

import java.util.Map;

public interface YLAliPayService {
	public String aliSdk(Map<String, Object> reqData,Map<String, String> needData) throws Exception;

	public String aliSdkTransfer(Map<String, Object> reqData, Map<String, String> needData) throws Exception;
	
}
