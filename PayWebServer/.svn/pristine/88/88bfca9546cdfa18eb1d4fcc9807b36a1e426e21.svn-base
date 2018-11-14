package com.alipay.demo.trade.service;

import com.alipay.demo.trade.model.builder.AlipaySystemOauthTokenBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCancelRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCloseOrderRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryBillRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FCancelResult;
import com.alipay.demo.trade.model.result.AlipayF2FCloseOrderResult;
import com.alipay.demo.trade.model.result.AlipayF2FCreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FFastpayRefundQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryBillResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.model.result.AlipaySystemOauthTokenResult;

/**
 * Created by liuyangkly on 15/7/29.
 */
public interface AlipayTradeService {

	// 当面付2.0流程支付
    public AlipayF2FPayResult tradePay(AlipayTradePayRequestBuilder builder);

    // 当面付2.0消费查询
    public AlipayF2FQueryResult queryTradeResult(AlipayTradeQueryRequestBuilder builder);

    // 当面付2.0消费退款
    public AlipayF2FRefundResult tradeRefund(AlipayTradeRefundRequestBuilder builder);

    // 当面付2.0预下单(生成二维码)
    public AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateRequestBuilder builder);
   
    //当面付下单
    public AlipayF2FCreateResult tradeCreate(AlipayTradeCreateRequestBuilder builder); 
    
    // 当面付2.0关闭订单
    public AlipayF2FCloseOrderResult closeOrder(AlipayTradeCloseOrderRequestBuilder builder);
    
    // 当面付2.0查询对账单
    public AlipayF2FQueryBillResult queryBill(AlipayTradeQueryBillRequestBuilder builder);
 
    //当面付撤销
    public AlipayF2FCancelResult tradeCancel(AlipayTradeCancelRequestBuilder builder);

    //当面付退款查询
    public AlipayF2FFastpayRefundQueryResult queryTradeRefund(AlipayTradeRefundQueryRequestBuilder builder);
    
    //获取用户授权
    public AlipaySystemOauthTokenResult  getAlipaySystemOauthToken(AlipaySystemOauthTokenBuilder builder);
}
