package com.alipay.demo.trade.model.builder;

import com.alipay.api.internal.util.StringUtils;
import com.google.gson.annotations.SerializedName;

public class AlipayBossProdSubmerchantCreateRequestBuilder extends RequestBuilder{
	
	private BizContent bizContent = new BizContent();
	
	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		if(StringUtils.isEmpty(bizContent.externalId)){
			throw new NullPointerException("externalId  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.name)){
			throw new NullPointerException("name  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.aliasName)){
			throw new NullPointerException("aliasName  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.servicePhone)){
			throw new NullPointerException("servicePhone  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.categoryId)){
			throw new NullPointerException("categoryId  should not be NULL!");
		}
		
		return true;
	}


	public String getExternalId() {
		return bizContent.externalId;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setExternalId(String externalId) {
		bizContent.externalId = externalId;
		return this;
	}


	public String getName() {
		return bizContent.name;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setName(String name) {
		bizContent.name = name;
		return this;
	}


	public String getAliasName() {
		return bizContent.aliasName;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setAliasName(String aliasName) {
		bizContent.aliasName = aliasName;
		return this;
	}


	public String getServicePhone() {
		return bizContent.servicePhone;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setServicePhone(String servicePhone) {
		bizContent.servicePhone = servicePhone;
		return this;
	}


	public String getContactName() {
		return bizContent.contactName;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setContactName(String contactName) {
		bizContent.contactName = contactName;
		return this;
	}


	public String getContactPhone() {
		return bizContent.contactPhone;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setContactPhone(String contactPhone) {
		bizContent.contactPhone = contactPhone;
		return this;
	}


	public String getContactMobile() {
		return bizContent.contactMobile;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setContactMobile(String contactMobile) {
		bizContent.contactMobile = contactMobile;
		return this;
	}


	public String getContactEmail() {
		return bizContent.contactEmail;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setContactEmail(String contactEmail) {
		bizContent.contactEmail = contactEmail;
		return this;
	}


	public String getCategoryId() {
		return bizContent.categoryId;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setCategoryId(String categoryId) {
		bizContent.categoryId = categoryId;
		return this;
	}
	
	public String getSource() {
		return bizContent.source;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setSource(String source) {
		bizContent.source = source;
		return this;
	}

	public String getMemo() {
		return bizContent.memo;
	}


	public AlipayBossProdSubmerchantCreateRequestBuilder setMemo(String memo) {
		bizContent.memo = memo;
		return this;
	}

	
	
	public static class BizContent{
		//商户外部编号，一个受理机构下唯一的
		@SerializedName("external_id")
		private String  externalId;
		
		//商户名称
		@SerializedName("name")
		private String  name;
		
		//商户简称
		@SerializedName("alias_name")
		private String  aliasName;
		
		//客服电话
		@SerializedName("service_phone")
		private String  servicePhone;
		
		//联系人名称
		@SerializedName("contact_name")
		private String  contactName;
		
		//联系人电话
		@SerializedName("contact_phone")
		private String  contactPhone;
		
		//联系人手机号
		@SerializedName("contact_mobile")
		private String  contactMobile;
		
		//联系人邮箱
		@SerializedName("contact_email")
		private String  contactEmail;
		
		//经营类目
		@SerializedName("category_id")
		private String  categoryId;
		
		//机构商户号
		@SerializedName("source")
		private String source;
		
		//商户备注
		@SerializedName("memo")
		private String  memo;
		
		@Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("externalId='").append(externalId).append('\'');
            sb.append(", name='").append(name).append('\'');    
            sb.append(", aliasName='").append(aliasName).append('\'');
            sb.append(", servicePhone='").append(servicePhone).append('\'');
            sb.append(", contactName='").append(contactName).append('\'');
            sb.append(", contactPhone='").append(contactPhone).append('\'');
            sb.append(", contactMobile='").append(contactMobile).append('\'');
            sb.append(", contactEmail='").append(contactEmail).append('\'');
            sb.append(", categoryId='").append(categoryId).append('\'');
            sb.append(", source='").append(source).append('\'');
            sb.append(", memo='").append(memo).append('\'');
            sb.append('}');
            
            return sb.toString();
        }
		
	}
}
