package com.alipay.demo.trade.model.result;

import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantModifyResponse;

public class AlipayBossProdSubmerchantModifyResult implements Result{
	
	private AlipayBossProdSubmerchantModifyResponse response;
	private TradeStatus  tradeStatus;
	
	public AlipayBossProdSubmerchantModifyResult(AlipayBossProdSubmerchantModifyResponse response){
		this.response = response;
	}
	
	
	public AlipayBossProdSubmerchantModifyResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayBossProdSubmerchantModifyResponse response) {
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
