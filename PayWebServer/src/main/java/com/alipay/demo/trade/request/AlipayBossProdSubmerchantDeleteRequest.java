package com.alipay.demo.trade.request;

import java.util.Map;

import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.internal.util.AlipayHashMap;
import com.alipay.demo.trade.response.AlipayBossProdSubmerchantQueryResponse;

public class AlipayBossProdSubmerchantDeleteRequest implements AlipayRequest<AlipayBossProdSubmerchantQueryResponse>{
	private AlipayHashMap udfParams; // add user-defined text parameters
	private String apiVersion="1.0";
	private String terminalType;
	private String terminalInfo;	
	private String prodCode;
	private String notifyUrl;
	private String returnUrl;
	private boolean needEncrypt=false;
	
	private String bizContent;

	public void setBizContent(String bizContent) {
		this.bizContent = bizContent;
	}
	public String getBizContent() {
		return this.bizContent;
	}
	
	
	@Override
	public String getApiMethodName() {
		return "alipay.boss.prod.submerchant.delete";
	}

	@Override
	public String getApiVersion() {
		return this.apiVersion;
	}

	@Override
	public String getNotifyUrl() {
		return this.notifyUrl;
	}

	@Override
	public String getProdCode() {
		return this.prodCode;
	}
	
	@Override
	public String getTerminalInfo() {
		return this.terminalInfo;
	}

	@Override
	public String getTerminalType() {
		return this.terminalType;
	}

	@Override
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	@Override
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	@Override
	public void setProdCode(String prodCode) {
		this.prodCode = prodCode;
	}

	@Override
	public void setTerminalInfo(String terminalInfo) {
		this.terminalInfo = terminalInfo;
	}

	@Override
	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}
	
	
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public boolean isNeedEncrypt() {
		return needEncrypt;
	}
	public void setNeedEncrypt(boolean needEncrypt) {
		this.needEncrypt = needEncrypt;
	}
	public Map<String, String> getTextParams() {		
		AlipayHashMap txtParams = new AlipayHashMap();
		txtParams.put("biz_content", this.bizContent);
		if(udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public void putOtherTextParam(String key, String value) {
		if(this.udfParams == null) {
			this.udfParams = new AlipayHashMap();
		}
		this.udfParams.put(key, value);
	}

	@Override
	public Class<AlipayBossProdSubmerchantQueryResponse> getResponseClass() {
		return AlipayBossProdSubmerchantQueryResponse.class;
	}
	
	@Override
	public AlipayObject getBizModel() {
		return null;
	}
	
	@Override
	public void setBizModel(AlipayObject bizModel) {
		
	}
	
}
