package com.alipay.demo.trade.model.result;

import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantDeleteResponse;

public class AlipayBossProdSubmerchantDeleteResult implements Result{
	private AlipayBossProdSubmerchantDeleteResponse response;
	private TradeStatus  tradeStatus;
	
	public AlipayBossProdSubmerchantDeleteResult(AlipayBossProdSubmerchantDeleteResponse response){
		this.response = response;
	}
	
	
	public AlipayBossProdSubmerchantDeleteResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayBossProdSubmerchantDeleteResponse response) {
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
