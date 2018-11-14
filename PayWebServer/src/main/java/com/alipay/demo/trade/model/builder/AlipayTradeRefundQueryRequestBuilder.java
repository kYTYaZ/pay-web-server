package com.alipay.demo.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;

public class AlipayTradeRefundQueryRequestBuilder extends RequestBuilder{
	private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.outTradeNo) && StringUtils.isEmpty(bizContent.tradeNo)) {
            throw new NullPointerException("out_trade_no or trade_no should not both be NULL!");
        }
        
        if (StringUtils.isEmpty(bizContent.outRequestNo)) {
            throw new NullPointerException("out_request_no  should not both be NULL!");
        }
        
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeRefundRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeRefundQueryRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeRefundQueryRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeRefundQueryRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeRefundQueryRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }
    
    public String getTradeNo() {
        return bizContent.tradeNo;
    }

    
    public String getOutRequestNo() {
        return bizContent.outRequestNo;
    }
    
    public AlipayTradeRefundQueryRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public AlipayTradeRefundQueryRequestBuilder setTradeNo(String tradeNo) {
        bizContent.tradeNo = tradeNo;
        return this;
    }

    public AlipayTradeRefundQueryRequestBuilder setOutRequestNo(String outRequestNo) {
        bizContent.outRequestNo = outRequestNo;
        return this;
    }

    public static class BizContent {
        // 支付宝交易号，当面付支付成功后支付宝会返回给商户系统。通过此支付宝交易号进行交易退款
        @SerializedName("trade_no")
        private String tradeNo;

        // (推荐) 外部订单号，可通过外部订单号申请退款，推荐使用
        @SerializedName("out_trade_no")
        private String outTradeNo;

        // (可选，需要支持重复退货时必填) 商户退款请求号，相同支付宝交易号下的不同退款请求号对应同一笔交易的不同退款申请，
        // 对于相同支付宝交易号下多笔相同商户退款请求号的退款交易，支付宝只会进行一次退款
        @SerializedName("out_request_no")
        private String outRequestNo;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("tradeNo='").append(tradeNo).append('\'');
            sb.append(", outTradeNo='").append(outTradeNo).append('\'');
            sb.append(", outRequestNo='").append(outRequestNo).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
