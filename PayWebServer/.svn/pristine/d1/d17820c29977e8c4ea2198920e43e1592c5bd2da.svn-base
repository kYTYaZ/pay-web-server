package com.alipay.demo.trade.model.result;

import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.demo.trade.model.TradeStatus;

public class AlipaySystemOauthTokenResult implements Result{
	
	private TradeStatus tradeStatus;
	private AlipaySystemOauthTokenResponse response;
	
	public AlipaySystemOauthTokenResult(AlipaySystemOauthTokenResponse response){
		this.response = response;
	}
	
	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public AlipaySystemOauthTokenResponse getResponse() {
		return response;
	}

	public void setResponse(AlipaySystemOauthTokenResponse response) {
		this.response = response;
	}

	@Override
	public boolean isTradeSuccess() {
		 return response != null && TradeStatus.SUCCESS.equals(tradeStatus);
	}

}
