package com.alipay.demo.trade.model.result;

import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectModifyResponse;

public class AntMerchantExpandIndirectModifyResult  implements Result{
	private AntMerchantExpandIndirectModifyResponse response;
	private TradeStatus  tradeStatus;
	
	public AntMerchantExpandIndirectModifyResult(AntMerchantExpandIndirectModifyResponse response){
		this.response = response;
	}
	
	public AntMerchantExpandIndirectModifyResponse getResponse() {
		return response;
	}

	public void setResponse(AntMerchantExpandIndirectModifyResponse response) {
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
