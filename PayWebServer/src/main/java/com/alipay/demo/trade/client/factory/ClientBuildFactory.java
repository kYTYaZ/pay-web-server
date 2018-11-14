package com.alipay.demo.trade.client.factory;

import com.alipay.api.AlipayClient;
import com.alipay.demo.trade.model.ClazzName;
import com.alipay.demo.trade.model.builder.FieldBuilder;

public class ClientBuildFactory {

	public static AlipayClient getAlipayClient(ClazzName clazzName,
			FieldBuilder fb) {
		AlipayClient client = null;

		switch (clazzName) {
			case AlipayTradeServiceImpl:
				com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl.ClientBuilder cbTrade = new com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl.ClientBuilder();
				cbTrade.setGatewayUrl(fb.getGatewayUrl());
				cbTrade.setAlipayPublicKey(fb.getAlipayPublicKey());
				cbTrade.setAppid(fb.getAppid());
				cbTrade.setPrivateKey(fb.getPrivateKey());
				cbTrade.setCharset("utf-8");
				cbTrade.setFormat("json");
				client = cbTrade.build().client;
				break;
			case AntMerchantExpandIndirectSummerchantServiceImp:
				com.alipay.demo.trade.service.impl.AntMerchantExpandIndirectSummerchantServiceImp.ClientBuilder cbMer = new com.alipay.demo.trade.service.impl.AntMerchantExpandIndirectSummerchantServiceImp.ClientBuilder();
				cbMer.setGatewayUrl(fb.getGatewayUrl());
				cbMer.setAlipayPublicKey(fb.getAlipayPublicKey());
				cbMer.setAppid(fb.getAppid());
				cbMer.setPrivateKey(fb.getPrivateKey());
				cbMer.setCharset("utf-8");
				cbMer.setFormat("json");
				client = cbMer.build().client;
				break;
			default:
				client = null;
				break;
		}

		return client;

	}

}
