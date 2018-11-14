package com.huateng.pay.services.weixin;

import java.util.Map;

public interface YLWXPayService {

	public Map<String, String> wxSend(Map<String, String> reqData,String cacheKey,String url) throws Exception;
	
	public boolean isResponseSignatureValid(Map<String, String> reqData,String cacheKey) throws Exception;
	
}
