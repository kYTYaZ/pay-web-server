package com.alipay.demo.trade.model.result;

import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.demo.trade.model.TradeStatus;

public class AlipayF2FQueryBillResult implements  Result {
	
	private TradeStatus  tradeStatus;
	private AlipayDataDataserviceBillDownloadurlQueryResponse response;
	
	public AlipayF2FQueryBillResult(AlipayDataDataserviceBillDownloadurlQueryResponse response) {
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

	public AlipayDataDataserviceBillDownloadurlQueryResponse getResponse() {
		return response;
	}

	public void setResponse(AlipayDataDataserviceBillDownloadurlQueryResponse response) {
		this.response = response;
	}
}
