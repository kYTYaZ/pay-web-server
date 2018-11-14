package com.alipay.demo.trade.response;

import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.mapping.ApiField;

public class AlipayTradeCloseOrderResponse extends AlipayResponse{
	
	private static final long serialVersionUID = 1L;
	
	@ApiField("trade_no")
	private String  tradeNo;
	
	@ApiField("out_trade_no ")
	private String outTradeNo;
	
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	
	
	
}
