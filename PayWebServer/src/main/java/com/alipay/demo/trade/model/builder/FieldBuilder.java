package com.alipay.demo.trade.model.builder;

import com.alipay.demo.trade.service.impl.hb.TradeListener;

public abstract class FieldBuilder {

	private String appid;
	private String privateKey;
	private String alipayPublicKey;
	private String gatewayUrl;
	private String format;
	private String charset;
	private TradeListener listener;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getAlipayPublicKey() {
		return alipayPublicKey;
	}

	public void setAlipayPublicKey(String alipayPublicKey) {
		this.alipayPublicKey = alipayPublicKey;
	}

	public String getGatewayUrl() {
		return gatewayUrl;
	}

	public void setGatewayUrl(String gatewayUrl) {
		this.gatewayUrl = gatewayUrl;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public TradeListener getListener() {
		return listener;
	}

	public void setListener(TradeListener listener) {
		this.listener = listener;
	}

}
