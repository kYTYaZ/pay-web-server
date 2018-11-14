package com.alipay.demo.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

/**
 * @author Administrator
 * 关闭订单
 */
public class AlipayTradeCloseOrderRequestBuilder extends RequestBuilder{
	private BizContent bizContent = new BizContent();

	@Override
	public boolean validate() {
		  if (StringUtils.isEmpty(bizContent.outTradeNo) && StringUtils.isEmpty(bizContent.tradeNo)) {
	            throw new NullPointerException("out_trade_no and trade_no should not both be NULL!");
	        }
		return true;
	}

	@Override
	public BizContent getBizContent() {
		return bizContent;
	}
	
	@Override
    public AlipayTradeRefundRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeRefundRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeRefundRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeRefundRequestBuilder) super.setNotifyUrl(notifyUrl);
    }
    
    public AlipayTradeCloseOrderRequestBuilder setOutTradeNo(String outTradeNo){
    	bizContent.outTradeNo = outTradeNo;
    	return this;
    }
	
    public String getOutTradeNo(){
    	return bizContent.outTradeNo;
    }
    
    public String getTradeNo(){
    	return bizContent.tradeNo;
    }
    
    public AlipayTradeCloseOrderRequestBuilder setTradeNo(String tradeNo){
    	 bizContent.tradeNo = tradeNo;
    	 return this;
    }
    
    public String getOperatorId(String operatorId){
    	return bizContent.operatorId;
    }
    
    public AlipayTradeCloseOrderRequestBuilder setOperatorId(String operatorId){
    	bizContent.operatorId = operatorId;
    	return this;
    }
    
    @Override
    public String toString() {
    	final StringBuilder sb = new StringBuilder("AlipayTradeCloseOrderRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();    	   
    }
    
	public static class BizContent {
        // 支付宝交易号，当面付支付成功后支付宝会返回给商户系统。通过此支付宝交易号进行交易退款
        @SerializedName("trade_no")
        private String tradeNo;

        // (推荐) 外部订单号，可通过外部订单号申请退款，推荐使用
        @SerializedName("out_trade_no")
        private String outTradeNo;


        // 卖家端自定义的的操作员 ID
        @SerializedName("operator_id")
        private String operatorId;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("tradeNo='").append(tradeNo).append('\'');
            sb.append(", outTradeNo='").append(outTradeNo).append('\'');    
            sb.append(", terminalId='").append(operatorId).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

	 
}
