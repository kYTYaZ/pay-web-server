package com.alipay.demo.trade.service.impl;

import org.apache.commons.lang.StringUtils;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.json.JSONWriter;
import com.alipay.demo.trade.client.factory.ClientBuildFactory;
import com.alipay.demo.trade.config.Constants;
import com.alipay.demo.trade.model.ClazzName;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectCreateRequetBuilder;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectModifyRequetBuilder;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectQueryRequetBuilder;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectCreateResult;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectModifyResult;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectQueryResult;
import com.alipay.demo.trade.request.AntMerchantExpandIndirectCreateRequest;
import com.alipay.demo.trade.request.AntMerchantExpandIndirectModifyRequest;
import com.alipay.demo.trade.request.AntMerchantExpandIndirectQueryRequest;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectCreateResponse;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectModifyResponse;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectQueryResponse;
import com.alipay.demo.trade.service.AntMerchantExpandIndirectSummerchantService;

public class AntMerchantExpandIndirectSummerchantServiceImp extends AbsAlipayService implements AntMerchantExpandIndirectSummerchantService{
	
	public AlipayClient client;
	
	public AntMerchantExpandIndirectSummerchantServiceImp(){
//		this.client = new ClientBuilder().build().client;
	}
	
	public AntMerchantExpandIndirectSummerchantServiceImp(ClientBuilder builder){
		 
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

        public AntMerchantExpandIndirectSummerchantServiceImp build() {
//            if (StringUtils.isEmpty(gatewayUrl)) {
//                gatewayUrl = Configs.getOpenApiDomain(); // 与mcloudmonitor网关地址不同
//            }
//            if (StringUtils.isEmpty(appid)) {
//                appid = Configs.getAppid();
//            }
//            if (StringUtils.isEmpty(privateKey)) {
//                privateKey = Configs.getPrivateKey();
//            }
//            if (StringUtils.isEmpty(format)) {
//                format = "json";
//            }
//            if (StringUtils.isEmpty(charset)) {
//                charset = "utf-8";
//            }
//            if (StringUtils.isEmpty(alipayPublicKey)) {
//                alipayPublicKey = Configs.getAlipayPublicKey();
//            }

            return new AntMerchantExpandIndirectSummerchantServiceImp(this);
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
    
	@Override
	public AntMerchantExpandIndirectCreateResult createSubmerchant(AntMerchantExpandIndirectCreateRequetBuilder builder) {

		validateBuilder(builder);
		
		AntMerchantExpandIndirectCreateRequest request = new AntMerchantExpandIndirectCreateRequest();
		 // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(new JSONWriter().write(builder.getBizContent().getModel(), true));
        log.info("trade.pay bizContent:" + request.getBizContent());
		
        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AntMerchantExpandIndirectSummerchantServiceImp, builder);
        
        AntMerchantExpandIndirectCreateResponse response = (AntMerchantExpandIndirectCreateResponse)getResponse(client, request);
        
        AntMerchantExpandIndirectCreateResult result = new AntMerchantExpandIndirectCreateResult(response);
        
        if(response != null && Constants.SUCCESS.equals(response.getCode())){
        	result.setTradeStatus(TradeStatus.SUCCESS);
        }else{
        	result.setTradeStatus(TradeStatus.FAILED);
        }
        
        return result;
	}
	
	

	@Override
	public AntMerchantExpandIndirectModifyResult modifySubmerchant(AntMerchantExpandIndirectModifyRequetBuilder builder) {
		
		validateBuilder(builder);
		
		AntMerchantExpandIndirectModifyRequest request = new AntMerchantExpandIndirectModifyRequest();
		 // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(new JSONWriter().write(builder.getBizContent().getModel(), true));
        log.info("trade.pay bizContent:" + request.getBizContent());
		
        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AntMerchantExpandIndirectSummerchantServiceImp, builder);

        AntMerchantExpandIndirectModifyResponse response = (AntMerchantExpandIndirectModifyResponse)getResponse(client, request);
        
        AntMerchantExpandIndirectModifyResult result = new AntMerchantExpandIndirectModifyResult(response);
        
        if(response != null && Constants.SUCCESS.equals(response.getCode())){
        	result.setTradeStatus(TradeStatus.SUCCESS);
        }else{
        	result.setTradeStatus(TradeStatus.FAILED);
        }
        
        return result;
	}

	@Override
	public AntMerchantExpandIndirectQueryResult querySubmerchant(AntMerchantExpandIndirectQueryRequetBuilder builder) {
		
		validateBuilder(builder);
		
		AntMerchantExpandIndirectQueryRequest request = new AntMerchantExpandIndirectQueryRequest();
		 // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(new JSONWriter().write(builder.getBizContent().getModel(), true));
        log.info("trade.pay bizContent:" + request.getBizContent());
        
        AlipayClient client = ClientBuildFactory.getAlipayClient(ClazzName.AntMerchantExpandIndirectSummerchantServiceImp, builder);
        
        AntMerchantExpandIndirectQueryResponse response = (AntMerchantExpandIndirectQueryResponse)getResponse(client, request);
        
        AntMerchantExpandIndirectQueryResult result = new AntMerchantExpandIndirectQueryResult(response);
        
        if(response != null && Constants.SUCCESS.equals(response.getCode())){
        	result.setTradeStatus(TradeStatus.SUCCESS);
        }else{
        	result.setTradeStatus(TradeStatus.FAILED);
        }
        
        return result;
	}

}
