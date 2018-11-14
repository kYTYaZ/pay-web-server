package com.alipay.demo.trade.model.builder;

import com.alipay.api.internal.util.StringUtils;
import com.google.gson.annotations.SerializedName;

public class AlipayBossProdSubmerchantQueryRequestBuilder extends RequestBuilder{
	
	private BizContent bizContent = new BizContent();
	
	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		if(StringUtils.isEmpty(bizContent.externalId) && StringUtils.isEmpty(bizContent.subMerchantId)){
			throw new NullPointerException("externalId or subMerchantId should not be NULL!");
		}
		
		return true;
	}


	public String getExternalId() {
		return bizContent.externalId;
	}


	public AlipayBossProdSubmerchantQueryRequestBuilder setExternalId(String externalId) {
		bizContent.externalId = externalId;
		return this;
	}


	public String getSubMerchantId() {
		return bizContent.subMerchantId;
	}


	public AlipayBossProdSubmerchantQueryRequestBuilder setSubMerchantId(String subMerchantId) {
		bizContent.subMerchantId = subMerchantId;
		return this;
	}

	public static class BizContent{
		//商户外部编号，一个受理机构下唯一的
		@SerializedName("external_id")
		private String  externalId;
		
		//商户识别号
		@SerializedName("sub_merchant_id")
		private String  subMerchantId;
		
		
		@Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("externalId='").append(externalId).append('\'');
            sb.append(", subMerchantId='").append(subMerchantId).append('\'');    
            sb.append('}');
            
            return sb.toString();
        }
	}
}
