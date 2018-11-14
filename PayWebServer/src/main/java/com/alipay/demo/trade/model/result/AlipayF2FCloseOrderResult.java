package com.alipay.demo.trade.model.result;

import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.demo.trade.model.TradeStatus;

public class AlipayF2FCloseOrderResult implements  Result {

	private TradeStatus  tradeStatus;
	private AlipayTradeCloseResponse response;
	
	public AlipayF2FCloseOrderResult(AlipayTradeCloseResponse response){
		this.response = response;
	}
	@Override
	public boolean isTradeSuccess() {
	        return response != null && TradeStatus.SUCCESS.equals(tradeStatus);
	}

	public TradeStatus getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(TradeStatus tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public AlipayTradeCloseResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayTradeCloseResponse response) {
		this.response = response;
	}

}
