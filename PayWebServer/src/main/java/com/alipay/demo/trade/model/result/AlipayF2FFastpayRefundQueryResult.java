package com.alipay.demo.trade.model.result;

import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.demo.trade.model.TradeStatus;

/**
 * Created by liuyangkly on 15/8/27.
 */
public class AlipayF2FFastpayRefundQueryResult implements Result {
    private TradeStatus tradeStatus;
    private AlipayTradeFastpayRefundQueryResponse response;

    public AlipayF2FFastpayRefundQueryResult(AlipayTradeFastpayRefundQueryResponse response) {
        this.response = response;
    }

    public void setTradeStatus(TradeStatus tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public void setResponse(AlipayTradeFastpayRefundQueryResponse response) {
        this.response = response;
    }

    public TradeStatus getTradeStatus() {
        return tradeStatus;
    }

    public AlipayTradeFastpayRefundQueryResponse getResponse() {
        return response;
    }

    @Override
    public boolean isTradeSuccess() {
        return response != null && TradeStatus.SUCCESS.equals(tradeStatus);
    }
}
