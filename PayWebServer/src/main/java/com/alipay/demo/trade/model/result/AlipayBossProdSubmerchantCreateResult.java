package com.alipay.demo.trade.model.result;

import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantCreateResponse;

public class AlipayBossProdSubmerchantCreateResult implements Result{
	
	private AlipayBossProdSubmerchantCreateResponse response;
	private TradeStatus  tradeStatus;
	
	public AlipayBossProdSubmerchantCreateResult(AlipayBossProdSubmerchantCreateResponse response){
		this.response = response;
	}
	
	
	public AlipayBossProdSubmerchantCreateResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayBossProdSubmerchantCreateResponse response) {
		this.response = response;
	}


	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}


	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}


	@Override
	public boolean isTradeSuccess() {
		return response != null && TradeStatus.SUCCESS.equals(tradeStatus);
	}

}
