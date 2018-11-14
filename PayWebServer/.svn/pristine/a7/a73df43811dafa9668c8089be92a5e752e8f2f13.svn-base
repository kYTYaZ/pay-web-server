package com.alipay.demo.trade.model.builder;

import java.util.List;

import com.alipay.api.domain.AddressInfo;
import com.alipay.api.domain.AntMerchantExpandIndirectCreateModel;
import com.alipay.api.domain.BankCardInfo;
import com.alipay.api.domain.ContactInfo;
import com.alipay.api.internal.util.StringUtils;

public class AntMerchantExpandIndirectCreateRequetBuilder extends RequestBuilder{

private BizContent bizContent = new BizContent();
	
	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		
		if(StringUtils.isEmpty(bizContent.getModel().getExternalId())){
			throw new NullPointerException("externalId  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.getModel().getName())){
			throw new NullPointerException("name  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.getModel().getAliasName())){
			throw new NullPointerException("aliasName  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.getModel().getServicePhone())){
			throw new NullPointerException("servicePhone  should not be NULL!");
		}
		
		if(StringUtils.isEmpty(bizContent.getModel().getCategoryId())){
			throw new NullPointerException("categoryId  should not be NULL!");
		}
		
		if(null == bizContent.getModel().getContactInfo()){
			throw new NullPointerException("contactInfo  should not be NULL!");
		}
		
		return true;
	}


	public String getExternalId() {
		return bizContent.getModel().getExternalId();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setExternalId(String externalId) {
		bizContent.getModel().setExternalId(externalId);
		return this;
	}


	public String getName() {
		return bizContent.getModel().getName();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setName(String name) {
		bizContent.getModel().setName(name);
		return this;
	}


	public String getAliasName() {
		return bizContent.getModel().getAliasName();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setAliasName(String aliasName) {
		bizContent.getModel().setAliasName(aliasName);
		return this;
	}


	public String getServicePhone() {
		return bizContent.getModel().getServicePhone();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setServicePhone(String servicePhone) {
		bizContent.getModel().setServicePhone(servicePhone);
		return this;
	}

	public String getCategoryId() {
		return bizContent.getModel().getCategoryId();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setCategoryId(String categoryId) {
		bizContent.getModel().setCategoryId(categoryId);
		return this;
	}
	
	public String getSource() {
		return bizContent.getModel().getSource();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setSource(String source) {
		bizContent.getModel().setSource(source);
		return this;
	}

	public String getMemo() {
		return bizContent.getModel().getMemo();
	}


	public AntMerchantExpandIndirectCreateRequetBuilder setMemo(String memo) {
		bizContent.getModel().setMemo(memo);
		return this;
	}
	
	public String getMcc() {
		return bizContent.getModel().getMcc();
	}
	
	
	public AntMerchantExpandIndirectCreateRequetBuilder setMcc(String mcc) {
		bizContent.getModel().setMcc(mcc);
		return this;
	}
	
	public String getOrgPid() {
		return bizContent.getModel().getOrgPid();
	}
	
	
	public AntMerchantExpandIndirectCreateRequetBuilder setOrgPid(String orgPid) {
		bizContent.getModel().setOrgPid(orgPid);
		return this;
	}
	
	public String getBusinessLicense() {
		return bizContent.getModel().getBusinessLicense();
	}

	public AntMerchantExpandIndirectCreateRequetBuilder setBusinessLicense(String businessLicense) {
		bizContent.getModel().setBusinessLicense(businessLicense);
		return this;
	}

	public String getBusinessLicenseType() {
		return bizContent.getModel().getBusinessLicenseType();
	}

	public AntMerchantExpandIndirectCreateRequetBuilder setBusinessLicenseType(String businessLicense) {
		bizContent.getModel().setBusinessLicenseType(businessLicense);
		return this;
	}
	
	public List<String> getPayCodeInfo() {
		return bizContent.getModel().getPayCodeInfo();
	}

	public AntMerchantExpandIndirectCreateRequetBuilder setPayCodeInfo(List<String> payCodeInfo) {
		bizContent.getModel().setPayCodeInfo(payCodeInfo);;
		return this;
	}
	
	public List<String> getLogonId() {
		return bizContent.getModel().getLogonId();
	}
	
	public AntMerchantExpandIndirectCreateRequetBuilder setLogonId(List<String> logonId) {
		bizContent.getModel().setLogonId(logonId);
		return this;
	}

	public List<BankCardInfo> getBankcardInfo() {
		return bizContent.getModel().getBankcardInfo();
	}
	
	public AntMerchantExpandIndirectCreateRequetBuilder setBankcardInfo(List<BankCardInfo> bankcardInfo) {
		bizContent.getModel().setBankcardInfo(bankcardInfo);
		return this;
	}
	
	public List<AddressInfo> getAddressInfo() {
		return bizContent.getModel().getAddressInfo();
	}
	
	public AntMerchantExpandIndirectCreateRequetBuilder setAddressInfo(List<AddressInfo> addressInfo) {
		bizContent.getModel().setAddressInfo(addressInfo);
		return this;
	}
	
	public List<ContactInfo> getContactInfo() {
		return bizContent.getModel().getContactInfo();
	}
	
	public AntMerchantExpandIndirectCreateRequetBuilder setContactInfo(List<ContactInfo> contactInfo) {
		bizContent.getModel().setContactInfo(contactInfo);
		return this;
	}
	
	public static class BizContent{
		
		private AntMerchantExpandIndirectCreateModel model = new AntMerchantExpandIndirectCreateModel();

		public AntMerchantExpandIndirectCreateModel getModel() {
			return model;
		}

		public void setModel(AntMerchantExpandIndirectCreateModel model) {
			this.model = model;
		}
		
	}
}
