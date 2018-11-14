package com.alipay.demo.trade.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.client.factory.ClientBuildFactory;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.config.Constants;
import com.alipay.demo.trade.model.ClazzName;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipaySystemOauthTokenBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCancelRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCloseOrderRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCreateRequestBuilder;
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
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.Utils;
/**
 * Created by liuyangkly on 15/10/28.
 */
abstract class AbsAlipayTradeService extends AbsAlipayService implements AlipayTradeService {
    protected static ExecutorService executorService = Executors.newCachedThreadPool();
    public AlipayClient client;

    @Override
    public abstract AlipayF2FQueryResult queryTradeResult(AlipayTradeQueryRequestBuilder builder);

    protected AlipayTradeQueryResponse tradeQuery(AlipayTradeQueryRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝查询] 请求报文:" + request.getBizContent());

        AlipayTradeQueryResponse response =  (AlipayTradeQueryResponse)getResponse(client, request);
        log.info("[支付宝查询] 响应报文:" + response.getBody());
       
        return response;
    }

    @Override
    public abstract AlipayF2FRefundResult tradeRefund(AlipayTradeRefundRequestBuilder builder) ;

    @Override
    public abstract AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateRequestBuilder builder);

    @Override
    public AlipayF2FCreateResult tradeCreate(AlipayTradeCreateRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
      
        request.setNotifyUrl(builder.getNotifyUrl());
        log.info("[支付宝交易创建] 后台通知地址:" + builder.getNotifyUrl());
        
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
       
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝交易创建] 请求报文:" + request.getBizContent());

        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
        log.info("client:"+client.toString());
        
        AlipayTradeCreateResponse response = (AlipayTradeCreateResponse) getResponse(client, request);
        log.info("[支付宝交易创建] 响应报文:" + response.getBody());
        
        AlipayF2FCreateResult result = new AlipayF2FCreateResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 预下单交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);
            log.info("[支付宝交易创建] 支付宝交易创建成功");
            
        } else if (tradeError(response)) {
            // 预下单发生异常，状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);
            log.info("[支付宝交易创建] 支付宝交易创建，状态未知");
            
        } else {
            // 其他情况表明该预下单明确失败
            result.setTradeStatus(TradeStatus.FAILED);
            log.info("[支付宝交易创建] 支付宝交易创建失败");
        }
        
        return result;
    }
    
    // 根据查询结果queryResponse判断交易是否支付成功，如果支付成功则更新result并返回，如果不成功则调用撤销
    protected AlipayF2FPayResult checkQueryAndCancel(String outTradeNo, String appAuthToken, AlipayF2FPayResult result,
                                                   AlipayTradeQueryResponse queryResponse,AlipayTradeQueryRequestBuilder queryBuiler) {
        if (querySuccess(queryResponse)) {
            // 如果查询返回支付成功，则返回相应结果
            result.setTradeStatus(TradeStatus.SUCCESS);
            result.setResponse(toPayResponse(queryResponse));
            return result;
        }

        // 如果查询结果不为成功，则调用撤销
        AlipayTradeCancelRequestBuilder builder = new AlipayTradeCancelRequestBuilder().setOutTradeNo(outTradeNo);
        builder.setAppAuthToken(appAuthToken);
        builder.setAppid(queryBuiler.getAppid());
        builder.setPrivateKey(queryBuiler.getPrivateKey());
        builder.setAlipayPublicKey(queryBuiler.getAlipayPublicKey());
        builder.setGatewayUrl(queryBuiler.getGatewayUrl());
        
        AlipayTradeCancelResponse cancelResponse = cancelPayResult(builder);
        if (tradeError(cancelResponse)) {
            // 如果第一次同步撤销返回异常，则标记支付交易为未知状态
            result.setTradeStatus(TradeStatus.UNKNOWN);
        } else {
            // 标记支付为失败，如果撤销未能成功，产生的单边帐由人工处理
            result.setTradeStatus(TradeStatus.FAILED);
        }
        return result;
    }

    // 根据外部订单号outTradeNo撤销订单
    public abstract AlipayF2FCancelResult tradeCancel(AlipayTradeCancelRequestBuilder builder);

    // 轮询查询订单支付结果
    public AlipayTradeQueryResponse loopQueryResult(AlipayTradeQueryRequestBuilder builder) {
       
    	log.info("-----------  支付宝轮询查询交易状态  开始   ----------------");
    	
    	AlipayTradeQueryResponse queryResponse = null;
    	
    	int MaxQuery = Configs.getMaxQueryRetry();
        
        for (int i = 0; i < MaxQuery; i++) {
        	
        	log.info("[支付宝轮询查询交易状态]  轮询最大次数MaxQuery=" + MaxQuery + "当前轮询次数 currentQuery=" + (i + 1));
           
        	Utils.sleep(Configs.getQueryDuration());
        	
        	log.info("[支付宝轮询查询交易状态] 睡眠："+ Configs.getQueryDuration() + "ms完成，开始查询");
        	
            AlipayTradeQueryResponse response = tradeQuery(builder);
            if (response != null) {
                if (stopQuery(response)) {
                	log.info("[支付宝轮询查询交易状态] 交易状态查询完成 停止查询 响应结果：" + response.getBody());
                	log.info("-----------  支付宝轮询查询交易状态  完成   ----------------");
                	return response;
                }
                
                queryResponse = response;
            }
        }
        
    	log.info("-----------  支付宝轮询查询交易状态  完成   ----------------");
        
        return queryResponse;
    }

    // 判断是否停止查询
    protected boolean stopQuery(AlipayTradeQueryResponse response) {
        if (Constants.SUCCESS.equals(response.getCode())) {
            if ("TRADE_FINISHED".equals(response.getTradeStatus()) ||
                    "TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                    "TRADE_CLOSED".equals(response.getTradeStatus())) {
                // 如果查询到交易成功、交易结束、交易关闭，则返回对应结果
                return true;
            }
        }
        return false;
    }

    // 根据外部订单号outTradeNo撤销订单
    protected AlipayTradeCancelResponse cancelPayResult(AlipayTradeCancelRequestBuilder builder) {
        AlipayTradeCancelResponse response = tradeCancel(builder).getResponse();
        if (cancelSuccess(response)) {
            // 如果撤销成功，则返回撤销结果
            return response;
        }

        // 撤销失败
        if (needRetry(response)) {
            // 如果需要重试，首先记录日志，然后调用异步撤销
            log.warn("begin async cancel request:" + builder);
            asyncCancel(builder);
        }
        return response;
    }

    // 异步撤销
    protected void asyncCancel(final AlipayTradeCancelRequestBuilder builder) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Configs.getMaxCancelRetry(); i++) {
                    Utils.sleep(Configs.getCancelDuration());

                    AlipayTradeCancelResponse response = tradeCancel(builder).getResponse();
                    if (cancelSuccess(response) ||
                            !needRetry(response)) {
                        // 如果撤销成功或者应答告知不需要重试撤销，则返回撤销结果（无论撤销是成功失败，失败人工处理）
                        return ;
                    }
                }
            }
        });
    }

    // 将查询应答转换为支付应答
    protected AlipayTradePayResponse toPayResponse(AlipayTradeQueryResponse response) {
        AlipayTradePayResponse payResponse = new AlipayTradePayResponse();
        // 只有查询明确返回成功才能将返回码设置为10000，否则均为失败
        payResponse.setCode(querySuccess(response) ? Constants.SUCCESS : Constants.FAILED);
      
        // 补充交易状态信息
        StringBuilder msg = new StringBuilder(response.getMsg());
        msg.append(" tradeStatus:").append(response.getTradeStatus());
        
        payResponse.setMsg(msg.toString());
        payResponse.setSubCode(response.getSubCode());
        payResponse.setSubMsg(response.getSubMsg());
        payResponse.setBody(response.getBody());
        payResponse.setParams(response.getParams());
        // payResponse应该是交易支付时间，但是response里是本次交易打款给卖家的时间,是否有问题
        // payResponse.setGmtPayment(response.getSendPayDate());
        payResponse.setTradeNo(response.getTradeNo());
        payResponse.setOutTradeNo(response.getOutTradeNo());
        payResponse.setOpenId(response.getOpenId());
        payResponse.setBuyerLogonId(response.getBuyerLogonId());
        payResponse.setTotalAmount(response.getTotalAmount());
        payResponse.setReceiptAmount(response.getReceiptAmount());
        payResponse.setBuyerPayAmount(response.getBuyerPayAmount());
        payResponse.setPointAmount(response.getPointAmount());
        payResponse.setInvoiceAmount(response.getInvoiceAmount());
        payResponse.setGmtPayment(response.getSendPayDate());
        payResponse.setFundBillList(response.getFundBillList());
        payResponse.setStoreName(response.getStoreName());
        payResponse.setBuyerUserId(response.getBuyerUserId());
        payResponse.setDiscountGoodsDetail(response.getDiscountGoodsDetail());
        
        return payResponse;
    }
    
	@Override
	public abstract AlipayF2FCloseOrderResult closeOrder(AlipayTradeCloseOrderRequestBuilder builder);

	@Override
	public AlipayF2FQueryBillResult queryBill(AlipayTradeQueryBillRequestBuilder builder) {
		 validateBuilder(builder);

		 AlipayDataDataserviceBillDownloadurlQueryRequest  request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
	     request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
	     request.setBizContent(builder.toJsonString());
	     log.info("[支付宝对账单下载] 请求报文:" + request.getBizContent());
	     
	     AlipayDataDataserviceBillDownloadurlQueryResponse response =  (AlipayDataDataserviceBillDownloadurlQueryResponse) getResponse(client, request);
	     AlipayF2FQueryBillResult result = new AlipayF2FQueryBillResult(response);
	     log.info("[支付宝对账单下载] 响应报文:" + response.getBody());
	     
	     if (response != null && Constants.SUCCESS.equals(response.getCode())) {
	         //查询对账单
	    	 result.setTradeStatus(TradeStatus.SUCCESS);
	    	 log.info("[支付宝对账单下载] 获取对账单下载地址成功");
	     } else if (tradeError(response)) {
	    	 //查询对账单异常，状态未知
	    	 result.setTradeStatus(TradeStatus.UNKNOWN);
	    	 log.info("[支付宝对账单下载] 获取对账单下载地址状态未知");

	     } else {
	    	 //查询对账单失败
	         result.setTradeStatus(TradeStatus.FAILED);
	         log.info("[支付宝对账单下载] 获取对账单下载地址失败");
	     }
	     
	     return result;
	}

	@Override
	public abstract AlipayF2FFastpayRefundQueryResult  queryTradeRefund(AlipayTradeRefundQueryRequestBuilder builder);

	
	@Override
	public AlipaySystemOauthTokenResult getAlipaySystemOauthToken(AlipaySystemOauthTokenBuilder builder) {
		
		validateBuilder(builder);
		
		log.info("[支付宝获取用户授权] 请求参数" + builder.getBizContent());
		
		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
		request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
		request.setGrantType(builder.getGrantType());
		request.setCode(builder.getCode());
		if(!StringUtils.isEmpty(builder.GetRefreshToken())){
			request.setRefreshToken(builder.GetRefreshToken());
		}
		
		AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
		
		AlipaySystemOauthTokenResponse response = (AlipaySystemOauthTokenResponse)getResponse(client, request);
		AlipaySystemOauthTokenResult result = new AlipaySystemOauthTokenResult(response);
		log.info("[支付宝获取用户授权] 响应报文:" + response.getBody());
		
		if(response.isSuccess()){
			//获取授权信息成功
			result.setTradeStatus(TradeStatus.SUCCESS);
			log.info("[支付宝获取用户授权] 成功");
		}else if (tradeError(response)) {
			//查询退货异常，状态未知
	    	result.setTradeStatus(TradeStatus.UNKNOWN);
	    	log.info("[支付宝获取用户授权] 失败");
	     } else {
	    	//查询退货失败
	        result.setTradeStatus(TradeStatus.FAILED);
	        log.info("[支付宝获取用户授权] 失败");
	     }
		
		return result;
	}
	
    // 撤销需要重试
    protected boolean needRetry(AlipayTradeCancelResponse response) {
        return response == null ||
                "Y".equals(response.getRetryFlag());
    }

    // 查询返回“支付成功”
    protected boolean querySuccess(AlipayTradeQueryResponse response) {
        return response != null &&
                Constants.SUCCESS.equals(response.getCode()) &&
                ("TRADE_SUCCESS".equals(response.getTradeStatus()) ||
                        "TRADE_FINISHED".equals(response.getTradeStatus())
                );
    }

    // 撤销返回“撤销成功”
    protected boolean cancelSuccess(AlipayTradeCancelResponse response) {
        return response != null &&
                Constants.SUCCESS.equals(response.getCode());
    }

    // 交易异常，或发生系统错误
    protected boolean tradeError(AlipayResponse response) {
        return response == null ||
                Constants.ERROR.equals(response.getCode());
    }
}
