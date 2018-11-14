package com.alipay.demo.trade.model.result;

import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.demo.trade.model.TradeStatus;

public class AlipayF2FCancelResult implements  Result {
	
	private TradeStatus  tradeStatus;
	private AlipayTradeCancelResponse response;
	
	public AlipayF2FCancelResult(AlipayTradeCancelResponse response) {
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

	public AlipayTradeCancelResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayTradeCancelResponse response) {
		this.response = response;
	}
	
}
