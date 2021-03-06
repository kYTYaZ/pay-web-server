package com.alipay.demo.trade.model.result;

import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.demo.trade.model.TradeStatus;

/**
 * Created by liuyangkly on 15/8/27.
 */
public class AlipayF2FCreateResult implements Result {
    private TradeStatus tradeStatus;
    private AlipayTradeCreateResponse response;

    public AlipayF2FCreateResult(AlipayTradeCreateResponse response) {
        this.response = response;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public void setResponse(AlipayTradeCreateResponse response) {
        this.response = response;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public AlipayTradeCreateResponse getResponse() {
        return response;
    }

    @Override
    public boolean isTradeSuccess() {
        return response != null &&
                TradeStatus.SUCCESS.equals(tradeStatus);
    }
}
