package com.alipay.demo.trade.service.impl;

import org.apache.commons.lang.StringUtils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.config.Constants;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayBossProdSubmerchantCreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayBossProdSubmerchantDeleteRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayBossProdSubmerchantModifyRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayBossProdSubmerchantQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayBossProdSubmerchantCreateResult;
import com.alipay.demo.trade.model.result.AlipayBossProdSubmerchantDeleteResult;
import com.alipay.demo.trade.model.result.AlipayBossProdSubmerchantModifyResult;
import com.alipay.demo.trade.model.result.AlipayBossProdSubmerchantQueryResult;
import com.alipay.demo.trade.request.AlipayBossProdSubmerchantCreateRequest;
import com.alipay.demo.trade.request.AlipayBossProdSubmerchantDeleteRequest;
import com.alipay.demo.trade.request.AlipayBossProdSubmerchantModifyRequest;
import com.alipay.demo.trade.request.AlipayBossProdSubmerchantQueryRequest;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantCreateResponse;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantDeleteResponse;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantModifyResponse;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantQueryResponse;
import com.alipay.demo.trade.service.AlipayBossProdSubmerchantService;

public class AliapyBossProdSubmerchantServiceImpl extends AbsAlipayService implements AlipayBossProdSubmerchantService {
	
	protected AlipayClient client;
	
	public AliapyBossProdSubmerchantServiceImpl(){
		this.client = new ClientBuilder().build().client;
	}
	
	public AliapyBossProdSubmerchantServiceImpl(ClientBuilder builder){
		 
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

    public static class ClientBuilder {
        
    	private String gatewayUrl;
        private String appid;
        private String privateKey;
        private String format;
        private String charset;
        private String alipayPublicKey;

        public AliapyBossProdSubmerchantServiceImpl build() {
            if (StringUtils.isEmpty(gatewayUrl)) {
                gatewayUrl = Configs.getOpenApiDomain(); // 与mcloudmonitor网关地址不同
            }
            if (StringUtils.isEmpty(appid)) {
                appid = Configs.getAppid();
            }
            if (StringUtils.isEmpty(privateKey)) {
                privateKey = Configs.getPrivateKey();
            }
            if (StringUtils.isEmpty(format)) {
                format = "json";
            }
            if (StringUtils.isEmpty(charset)) {
                charset = "utf-8";
            }
            if (StringUtils.isEmpty(alipayPublicKey)) {
                alipayPublicKey = Configs.getAlipayPublicKey();
            }

            return new AliapyBossProdSubmerchantServiceImpl(this);
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

	/**
	 * 增加商户
	 * @return
	 */
	@Override
	public AlipayBossProdSubmerchantCreateResult createSubmerchant(AlipayBossProdSubmerchantCreateRequestBuilder builder) {
		
		validateBuilder(builder);
		
		AlipayBossProdSubmerchantCreateRequest request = new AlipayBossProdSubmerchantCreateRequest();
		 // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        log.info("trade.pay bizContent:" + request.getBizContent());
		
        AlipayBossProdSubmerchantCreateResponse response = (AlipayBossProdSubmerchantCreateResponse)getResponse(client, request);
        
        AlipayBossProdSubmerchantCreateResult result = new AlipayBossProdSubmerchantCreateResult(response);
        
        if(response != null && Constants.SUCCESS.equals(response.getCode())){
        	result.setTradeStatus(TradeStatus.SUCCESS);
        }else{
        	result.setTradeStatus(TradeStatus.FAILED);
        }
        
        return result;
	}
	
	/**
	 * 查询商户
	 * @return
	 */
	@Override
	public AlipayBossProdSubmerchantQueryResult querySubmerchant(AlipayBossProdSubmerchantQueryRequestBuilder builder) {
		
		validateBuilder(builder);
		
        AlipayBossProdSubmerchantQueryRequest request = new AlipayBossProdSubmerchantQueryRequest();
        // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        log.info("trade.pay bizContent:" + request.getBizContent());
		
		AlipayBossProdSubmerchantQueryResponse response = (AlipayBossProdSubmerchantQueryResponse)getResponse(client, request);
		
		AlipayBossProdSubmerchantQueryResult result = new AlipayBossProdSubmerchantQueryResult(response);
		
		if (response != null && Constants.SUCCESS.equals(response.getCode())) {
			// 支付交易明确成功
	        result.setTradeStatus(TradeStatus.SUCCESS);
	    }else {
	        // 其他情况表明该订单支付明确失败
	        result.setTradeStatus(TradeStatus.FAILED);
	    } 

		return result;
	}
	
	/**
	 * 删除商户
	 * @return
	 */
	@Override
	public AlipayBossProdSubmerchantDeleteResult deleteSubmerchant(AlipayBossProdSubmerchantDeleteRequestBuilder builder) {
		
		validateBuilder(builder);
		
        AlipayBossProdSubmerchantDeleteRequest request = new AlipayBossProdSubmerchantDeleteRequest();
        // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        log.info("trade.pay bizContent:" + request.getBizContent());
		
		AlipayBossProdSubmerchantDeleteResponse response = (AlipayBossProdSubmerchantDeleteResponse)getResponse(client, request);
		
		AlipayBossProdSubmerchantDeleteResult result = new AlipayBossProdSubmerchantDeleteResult(response);
		
		if (response != null && Constants.SUCCESS.equals(response.getCode())) {
			// 支付交易明确成功
	        result.setTradeStatus(TradeStatus.SUCCESS);
	    }else {
	        // 其他情况表明该订单支付明确失败
	        result.setTradeStatus(TradeStatus.FAILED);
	    } 

		return result;
	}
	
	/**
	 * 修改商户
	 * @return
	 */
	@Override
	public AlipayBossProdSubmerchantModifyResult modifySubmerchant(AlipayBossProdSubmerchantModifyRequestBuilder builder) {
		
		validateBuilder(builder);
		
        AlipayBossProdSubmerchantModifyRequest request = new AlipayBossProdSubmerchantModifyRequest();
        // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(builder.toJsonString());
        log.info("trade.pay bizContent:" + request.getBizContent());
		
		AlipayBossProdSubmerchantModifyResponse response = (AlipayBossProdSubmerchantModifyResponse)getResponse(client, request);
		
		AlipayBossProdSubmerchantModifyResult result = new AlipayBossProdSubmerchantModifyResult(response);
		
		if (response != null && Constants.SUCCESS.equals(response.getCode())) {
			// 支付交易明确成功
	        result.setTradeStatus(TradeStatus.SUCCESS);
	    }else {
	        // 其他情况表明该订单支付明确失败
	        result.setTradeStatus(TradeStatus.FAILED);
	    } 

		return result;
	}
	
	

}
