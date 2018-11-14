package com.alipay.demo.trade.response;

import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.mapping.ApiField;

public class AlipayBossProdSubmerchantQueryResponse extends AlipayResponse{

	private static final long serialVersionUID = 1L;
	
	@ApiField("sub_merchant_id")
	private String subMerchantId;
	
	//商户外部编号，一个受理机构下唯一的
	@ApiField("external_id")
	private String  externalId;
	
	//商户名称
	@ApiField("name")
	private String  name;
	
	//商户简称
	@ApiField("alias_name")
	private String  aliasName;
	
	//客服电话
	@ApiField("service_phone")
	private String  servicePhone;
	
	//联系人名称
	@ApiField("contact_name")
	private String  contactName;
	
	//联系人电话
	@ApiField("contact_phone")
	private String  contactPhone;
	
	//联系人手机号
	@ApiField("contact_mobile")
	private String  contactMobile;
	
	//联系人邮箱
	@ApiField("contact_email")
	private String  contactEmail;
	
	//经营类目
	@ApiField("category_id")
	private String  categoryId;
	
	//机构商户号
	@ApiField("source")
	private String source;
	
	//商户备注
	@ApiField("memo")
	private String  memo;
	

	public String getSubMerchantId() {
		return subMerchantId;
	}

	public void setSubMerchantId(String subMerchantId) {
		this.subMerchantId = subMerchantId;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public void setServicePhone(String servicePhone) {
		this.servicePhone = servicePhone;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getContactMobile() {
		return contactMobile;
	}

	public void setContactMobile(String contactMobile) {
		this.contactMobile = contactMobile;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
}
