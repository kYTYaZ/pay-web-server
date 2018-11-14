package com.alipay.demo.trade.model.result;

import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantQueryResponse;

public class AlipayBossProdSubmerchantQueryResult implements Result{
	private AlipayBossProdSubmerchantQueryResponse response;
	private TradeStatus  tradeStatus;
	
	public AlipayBossProdSubmerchantQueryResult(AlipayBossProdSubmerchantQueryResponse response){
		this.response = response;
	}
	
	
	public AlipayBossProdSubmerchantQueryResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayBossProdSubmerchantQueryResponse response) {
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
