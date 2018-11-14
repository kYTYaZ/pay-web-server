package com.alipay.demo.trade.model.result;

import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectCreateResponse;

public class AntMerchantExpandIndirectCreateResult  implements Result{
	private AntMerchantExpandIndirectCreateResponse response;
	private TradeStatus  tradeStatus;
	
	public AntMerchantExpandIndirectCreateResult(AntMerchantExpandIndirectCreateResponse response){
		this.response = response;
	}
	
	public AntMerchantExpandIndirectCreateResponse getResponse() {
		return response;
	}

	public void setResponse(AntMerchantExpandIndirectCreateResponse response) {
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
