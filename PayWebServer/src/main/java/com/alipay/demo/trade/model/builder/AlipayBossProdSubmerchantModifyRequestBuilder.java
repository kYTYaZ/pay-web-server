package com.alipay.demo.trade.model.builder;

import com.alipay.api.internal.util.StringUtils;
import com.google.gson.annotations.SerializedName;

public class AlipayBossProdSubmerchantModifyRequestBuilder extends RequestBuilder{
	
	private BizContent bizContent = new BizContent();
	
	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		
		if(StringUtils.isEmpty(bizContent.externalId) && StringUtils.isEmpty(bizContent.subMerchantId)){
			throw new NullPointerException("externalId  and subMerchantId should not be NULL!");
		}
	
		return true;
	}


	public String getExternalId() {
		return bizContent.externalId;
	}


	public AlipayBossProdSubmerchantModifyRequestBuilder setExternalId(String externalId) {
		bizContent.externalId = externalId;
		return this;
	}

	public String getSubMerchantId() {
		return bizContent.subMerchantId;
	}


	public AlipayBossProdSubmerchantModifyRequestBuilder setSubMerchantId(String subMerchantId) {
		bizContent.subMerchantId = subMerchantId;
		return this;
	}
	
	
	public String getAliasName() {
		return bizContent.aliasName;
	}


	public AlipayBossProdSubmerchantModifyRequestBuilder setAliasName(String aliasName) {
		bizContent.aliasName = aliasName;
		return this;
	}


	public String getServicePhone() {
		return bizContent.servicePhone;
	}


	public AlipayBossProdSubmerchantModifyRequestBuilder setServicePhone(String servicePhone) {
		bizContent.servicePhone = servicePhone;
		return this;
	}


	public String getContactName() {
		return bizContent.contactName;
	}


	public AlipayBossProdSubmerchantModifyRequestBuilder setContactName(String contactName) {
		bizContent.contactName = contactName;
		return this;
	}

	
	public String getSource() {
		return bizContent.source;
	}


	public AlipayBossProdSubmerchantModifyRequestBuilder setSource(String source) {
		bizContent.source = source;
		return this;
	}
	
	public static class BizContent{
		//商户外部编号，一个受理机构下唯一的
		@SerializedName("external_id")
		private String  externalId;
		
		//商户识别号
		@SerializedName("sub_merchant_id")
		private String  subMerchantId;
		
		//商户简称
		@SerializedName("alias_name")
		private String  aliasName;
		
		//客服电话
		@SerializedName("service_phone")
		private String  servicePhone;
		
		//联系人姓名
		@SerializedName("contact_name")
		private String  contactName;
		
		//机构商户号
		@SerializedName("source")
		private String source;
		
		@Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("externalId='").append(externalId).append('\'');
            sb.append(", subMerchantId='").append(subMerchantId).append('\'');    
            sb.append(", aliasName='").append(aliasName).append('\'');    
            sb.append(", servicePhone='").append(servicePhone).append('\'');    
            sb.append(", contactName='").append(contactName).append('\''); 
            sb.append(", source='").append(source).append('\'');    
            sb.append('}');
            
            return sb.toString();
        }
		
	}
}
