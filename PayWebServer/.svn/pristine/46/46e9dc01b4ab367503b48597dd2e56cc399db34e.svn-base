package com.alipay.demo.trade.service.impl;

import org.apache.commons.lang.StringUtils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.demo.trade.client.factory.ClientBuildFactory;
import com.alipay.demo.trade.config.Constants;
import com.alipay.demo.trade.model.ClazzName;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradeCancelRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCloseOrderRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FCancelResult;
import com.alipay.demo.trade.model.result.AlipayF2FCloseOrderResult;
import com.alipay.demo.trade.model.result.AlipayF2FFastpayRefundQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;

/**
 * Created by liuyangkly on 15/7/29.
 *
 *  一定要在创建AlipayTradeService之前调用Configs.init("xxxxxx");设置参数
 *
 */
public class AlipayTradeServiceImpl extends AbsAlipayTradeService {
	
	
	public AlipayTradeServiceImpl(){
		
	}
	
    public static class ClientBuilder {
        private String gatewayUrl;
        private String appid;
        private String privateKey;
        private String format;
        private String charset;
        private String alipayPublicKey;

        public AlipayTradeServiceImpl build() {

            return new AlipayTradeServiceImpl(this);
        }

        public ClientBuilder setAlipayPublicKey(String alipayPublicKey) {
            this.alipayPublicKey = alipayPublicKey;
            return this;
        }

        public ClientBuilder setAppid(String appid) {
            this.appid = appid;
            return this;
        }

        public ClientBuilder setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public ClientBuilder setFormat(String format) {
            this.format = format;
            return this;
        }

        public ClientBuilder setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
            return this;
        }

        public ClientBuilder setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public String getAlipayPublicKey() {
            return alipayPublicKey;
        }

        public String getAppid() {
            return appid;
        }

        public String getCharset() {
            return charset;
        }

        public String getFormat() {
            return format;
        }

        public String getGatewayUrl() {
            return gatewayUrl;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public AlipayTradeServiceImpl(ClientBuilder builder) {
        if (StringUtils.isEmpty(builder.getGatewayUrl())) {
            throw new NullPointerException("gatewayUrl should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getAppid())) {
            throw new NullPointerException("appid should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getPrivateKey())) {
            throw new NullPointerException("privateKey should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getFormat())) {
            throw new NullPointerException("format should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getCharset())) {
            throw new NullPointerException("charset should not be NULL!");
        }
        if (StringUtils.isEmpty(builder.getAlipayPublicKey())) {
            throw new NullPointerException("alipayPublicKey should not be NULL!");
        }

        client = new DefaultAlipayClient(builder.getGatewayUrl(), builder.getAppid(), builder.getPrivateKey(),
                builder.getFormat(), builder.getCharset(), builder.getAlipayPublicKey());
    }
    
    /**
     * 支付宝主扫
     */
    @Override
    public AlipayF2FPrecreateResult tradePrecreate(AlipayTradePrecreateRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
      
        request.setNotifyUrl(builder.getNotifyUrl());
        log.info("[支付宝预下单] 后台通知地址:" + builder.getNotifyUrl());
        
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
       
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝预下单] 请求报文:" + request.getBizContent());

        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
        
        AlipayTradePrecreateResponse response = (AlipayTradePrecreateResponse) getResponse(client, request);
        log.info("[支付宝预下单] 响应报文:" + response.getBody());
        
        AlipayF2FPrecreateResult result = new AlipayF2FPrecreateResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 预下单交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);
            log.info("[支付宝预下单] 预下单交易成功");
            
        } else if (tradeError(response)) {
            // 预下单发生异常，状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);
            log.info("[支付宝预下单] 预下单发生异常，状态未知");
            
        } else {
            // 其他情况表明该预下单明确失败
            result.setTradeStatus(TradeStatus.FAILED);
            log.info("[支付宝预下单] 预下单失败");
        }
        
        return result;
    }
    

    /**
     * 支付宝被扫
     */
    public AlipayF2FPayResult tradePay(AlipayTradePayRequestBuilder builder) {
     
    	validateBuilder(builder);

    	final String outTradeNo = builder.getOutTradeNo();

    	AlipayTradePayRequest request = new AlipayTradePayRequest();
    	// 设置平台参数
    	request.setNotifyUrl(builder.getNotifyUrl());
    	String appAuthToken = builder.getAppAuthToken();
    	// todo 等支付宝sdk升级公共参数后使用如下方法
    	// request.setAppAuthToken(appAuthToken);
    	request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝被扫支付宝] 请求报文" + request.getBizContent());

        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
        
    	// 首先调用支付api
    	AlipayTradePayResponse response = (AlipayTradePayResponse) getResponse(client, request);
    	log.info("[支付宝被扫支付宝] 响应报文:" + response.getBody());
    	
    	AlipayF2FPayResult result = new AlipayF2FPayResult(response);
    	
    	if (response != null && Constants.SUCCESS.equals(response.getCode())) {
    		// 支付交易明确成功
    		result.setTradeStatus(TradeStatus.SUCCESS);
    		log.info("[支付宝被扫支付宝] 交易成功");
    		
    	} else if (response != null && Constants.PAYING.equals(response.getCode())) {
    		// 返回用户处理中，则轮询查询交易是否成功，如果查询超时，则调用撤销
    		AlipayTradeQueryRequestBuilder queryBuiler = new AlipayTradeQueryRequestBuilder();
    		queryBuiler.setAppAuthToken(appAuthToken)
                       .setOutTradeNo(outTradeNo);
    		queryBuiler.setAppid(builder.getAppid());
    		queryBuiler.setPrivateKey(builder.getPrivateKey());
    		queryBuiler.setAlipayPublicKey(builder.getAlipayPublicKey());
    		queryBuiler.setGatewayUrl(builder.getGatewayUrl());
    		
    		log.info("[支付宝被扫支付宝] 返回正在支付中，开启轮询查询模式 start");
    		
    		AlipayTradeQueryResponse loopQueryResponse = loopQueryResult(queryBuiler);
    		
    		TradeStatus tadeStatus = this.handQueryResponse(loopQueryResponse);
    		
    		log.info("[支付宝被扫支付宝] 返回正在支付中，开启轮询查询模式 end tadeStatus=" + tadeStatus);
    		
    		if(TradeStatus.SUCCESS == tadeStatus){
    			result.setResponse(toPayResponse(loopQueryResponse));
    			log.info("[支付宝被扫支付宝] 交易成功");
    		}
    		
    		result.setTradeStatus(tadeStatus);
    		
    	} else if (tradeError(response)) {
    		log.info("[支付宝被扫支付宝] 返回交易状态未明确");
    		// 系统错误，则查询一次交易，如果交易没有支付成功，则调用撤销
    		AlipayTradeQueryRequestBuilder queryBuiler = new AlipayTradeQueryRequestBuilder();
    		queryBuiler.setAppAuthToken(appAuthToken)
                       .setOutTradeNo(outTradeNo);
    		queryBuiler.setAppid(builder.getAppid());
    		queryBuiler.setPrivateKey(builder.getPrivateKey());
    		queryBuiler.setAlipayPublicKey(builder.getAlipayPublicKey());
    		queryBuiler.setGatewayUrl(builder.getGatewayUrl());
          
    		log.info("[支付宝被扫支付宝] 返回交易状态未明确，查询订单交易状态 start");
    		AlipayTradeQueryResponse queryResponse = tradeQuery(queryBuiler);
          
    		TradeStatus tadeStatus = this.handQueryResponse(queryResponse);
    		
    		log.info("[支付宝被扫支付宝] 返回交易状态未明确，查询订单交易状态 end tadeStatus=" + tadeStatus);
    		
    		if(TradeStatus.SUCCESS == tadeStatus){
    			result.setResponse(toPayResponse(queryResponse));
    			log.info("[支付宝被扫支付宝] 交易成功");
    		}
    		
    		result.setTradeStatus(tadeStatus);

    	} else {
    		// 其他情况表明该订单支付明确失败
    		result.setTradeStatus(TradeStatus.FAILED);
    		log.info("[支付宝被扫支付宝] 交易失败");
    	}

    	return result;
    }
    
    /**
     * 支付宝查询
     */
    @Override
    public AlipayF2FQueryResult queryTradeResult(AlipayTradeQueryRequestBuilder builder) {
        AlipayTradeQueryResponse response = tradeQuery(builder);

        AlipayF2FQueryResult result = new AlipayF2FQueryResult(response);
        if (querySuccess(response)) {
            // 查询返回该订单交易支付成功
            result.setTradeStatus(TradeStatus.SUCCESS);
            log.info("[支付宝查询] 查询返回该订单交易支付成功");
            
        } else if (tradeError(response)) {
            // 查询发生异常，交易状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);
            log.info("[支付宝查询] 查询发生异常，交易状态未知");
            
        } else {
            // 其他情况均表明该订单号交易失败
            result.setTradeStatus(TradeStatus.FAILED);
            log.info("[支付宝查询] 查询交易失败");
        }
        return result;
    }
    
    protected AlipayTradeQueryResponse tradeQuery(AlipayTradeQueryRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝查询] 请求报文:" + request.getBizContent());

        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
        
        AlipayTradeQueryResponse response =  (AlipayTradeQueryResponse)getResponse(client, request);
        log.info("[支付宝查询] 响应报文:" + response.getBody());
       
        return response;
    }
    
    /**
     * 支付宝关闭订单
     */
	@Override
	public AlipayF2FCloseOrderResult closeOrder(AlipayTradeCloseOrderRequestBuilder builder) {
		 
		validateBuilder(builder);

	     AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
	     request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
	     request.setBizContent(builder.toJsonString());
	     log.info("[支付宝订单关闭] 请求报文:" + request.getBizContent());
	     
	     AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
	     
	     AlipayTradeCloseResponse response =  (AlipayTradeCloseResponse) getResponse(client, request);
	     AlipayF2FCloseOrderResult result = new AlipayF2FCloseOrderResult(response);
	     log.info("[支付宝订单关闭] 响应报文:" + response.getBody());
	     
	     if (response != null && Constants.SUCCESS.equals(response.getCode())) {
	         //关闭订单成功
	    	 result.setTradeStatus(TradeStatus.SUCCESS);
	    	 log.info("[支付宝订单关闭] 订单关闭成功");
	     } else if (tradeError(response)) {
	    	 //关闭订单成功异常，状态未知
	    	 result.setTradeStatus(TradeStatus.UNKNOWN);
	    	 log.info("[支付宝订单关闭] 订单关闭状态未知");
	     } else {
	    	 //关闭订单失败
	         result.setTradeStatus(TradeStatus.FAILED);
	         log.info("[支付宝订单关闭] 订单关闭失败");
	     }
	     
	     return result;
	}
	
	/**
	 * 支付宝撤销订单
	 */
	@Override
    public AlipayF2FCancelResult tradeCancel(AlipayTradeCancelRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝撤销] 请求报文:" + request.getBizContent());

        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
        
        AlipayTradeCancelResponse response =  (AlipayTradeCancelResponse) getResponse(client, request);
        log.info("[支付宝撤销] 响应报文:" + response.getBody());
        
        AlipayF2FCancelResult result = new AlipayF2FCancelResult(response);
	     if (response != null && Constants.SUCCESS.equals(response.getCode())) {
	         //撤销订单成功
	    	 result.setTradeStatus(TradeStatus.SUCCESS);
	    	 log.info("[支付宝撤销] 撤销订单成功");
	    	 
	     } else if (tradeError(response)) {
	    	 //撤销订单成功异常，状态未知
	    	 result.setTradeStatus(TradeStatus.UNKNOWN);
	    	 log.info("[支付宝撤销] 撤销订单成功异常，状态未知");

	     } else {
	    	 //撤销订单失败
	         result.setTradeStatus(TradeStatus.FAILED);
	         log.info("[支付宝撤销] 撤销订单失败");
	     }
	     
	     return result;
    }
    
	/**
	 * 支付宝退货
	 */
    @Override
    public AlipayF2FRefundResult tradeRefund(AlipayTradeRefundRequestBuilder builder) {
        validateBuilder(builder);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
        request.setBizContent(builder.toJsonString());
        log.info("[支付宝退货] 请求报文" + request.getBizContent());

        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
        
        AlipayTradeRefundResponse response = (AlipayTradeRefundResponse) getResponse(client, request);
        log.info("[支付宝退货] 响应报文" + request.getBizContent());
        
        
        AlipayF2FRefundResult result = new AlipayF2FRefundResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 退货交易成功
            result.setTradeStatus(TradeStatus.SUCCESS);
            log.info("[支付宝退货] 退货交易成功");
            
        } else if (tradeError(response)) {
            // 退货发生异常，退货状态未知
            result.setTradeStatus(TradeStatus.UNKNOWN);
            log.info("[支付宝退货] 退货发生异常，退货状态未知");

        } else {
            // 其他情况表明该订单退货明确失败
            result.setTradeStatus(TradeStatus.FAILED);
            log.info("[支付宝退货] 退货失败");
        }
        
        return result;
    }
    
    /**
     * 支付宝退货查询
     */
	@Override
	public AlipayF2FFastpayRefundQueryResult  queryTradeRefund(AlipayTradeRefundQueryRequestBuilder builder) {
		
		 validateBuilder(builder);
		 
	     AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
	     request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());
	     request.setBizContent(builder.toJsonString());
	     log.info("[支付宝退货查询] 请求报文:" + request.getBizContent());
	     
	     AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AlipayTradeServiceImpl, builder);
	     
	     AlipayTradeFastpayRefundQueryResponse response =  (AlipayTradeFastpayRefundQueryResponse) getResponse(client, request);
	     AlipayF2FFastpayRefundQueryResult result = new AlipayF2FFastpayRefundQueryResult(response);
	     log.info("[支付宝退货查询] 响应报文:" + response.getBody());
	     
	     if (response != null && Constants.SUCCESS.equals(response.getCode()) 
	    		 && !(response.getOutTradeNo() == null && response.getTradeNo() == null && response.getOutRequestNo() == null)) {
	         //查询退货成功
	    	 result.setTradeStatus(TradeStatus.SUCCESS);
	    	 log.info("[支付宝对账单下载] 退货查询成功");
	     } else if (tradeError(response)) {
	    	 //查询退货异常，状态未知
	    	 result.setTradeStatus(TradeStatus.UNKNOWN);
	    	 log.info("[支付宝对账单下载] 退货查询状态未知");
	     } else {
	    	 //查询退货失败
	         result.setTradeStatus(TradeStatus.FAILED);
	         log.info("[支付宝对账单下载] 退货查询失败");
	     }
	     
	     return result;
	}	

    protected TradeStatus handQueryResponse(AlipayTradeQueryResponse response){
	  
	   if(response != null){
		 
		   if("TRADE_SUCCESS".equals(response.getTradeStatus()) || "TRADE_FINISHED".equals(response.getTradeStatus())){   
			   
			   return TradeStatus.SUCCESS;
			   
		   }else if("TRADE_CLOSED".equals(response.getTradeStatus())){
			   
			   return TradeStatus.CLOSED;
			   
		   }else if("WAIT_BUYER_PAY".equals(response.getTradeStatus())){
			  
			   return TradeStatus.PAYING;
			   
		   }else{
			   
			  return TradeStatus.UNKNOWN;
		   }
	   }
	   
	   return TradeStatus.UNKNOWN;
   }
}
