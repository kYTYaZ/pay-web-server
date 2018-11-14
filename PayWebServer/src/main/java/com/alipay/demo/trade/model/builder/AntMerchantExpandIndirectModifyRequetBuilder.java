package com.alipay.demo.trade.model.builder;

import java.util.List;

import com.alipay.api.domain.AddressInfo;
import com.alipay.api.domain.AntMerchantExpandIndirectModifyModel;
import com.alipay.api.domain.BankCardInfo;
import com.alipay.api.domain.ContactInfo;
import com.alipay.api.internal.util.StringUtils;

public class AntMerchantExpandIndirectModifyRequetBuilder extends RequestBuilder{

private BizContent bizContent = new BizContent();
	
	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		
		if(StringUtils.isEmpty(bizContent.getModel().getExternalId()) &&
				StringUtils.isEmpty(bizContent.getModel().getSubMerchantId())){
			
			throw new NullPointerException("externalId  and subMerchantId should not be NULL!");
		}
		
		return true;
	}


	public String getExternalId() {
		return bizContent.getModel().getExternalId();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setExternalId(String externalId) {
		bizContent.getModel().setExternalId(externalId);
		return this;
	}


	public String getName() {
		return bizContent.getModel().getName();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setName(String name) {
		bizContent.getModel().setName(name);
		return this;
	}


	public String getAliasName() {
		return bizContent.getModel().getAliasName();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setAliasName(String aliasName) {
		bizContent.getModel().setAliasName(aliasName);
		return this;
	}


	public String getServicePhone() {
		return bizContent.getModel().getServicePhone();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setServicePhone(String servicePhone) {
		bizContent.getModel().setServicePhone(servicePhone);
		return this;
	}

	public String getCategoryId() {
		return bizContent.getModel().getCategoryId();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setCategoryId(String categoryId) {
		bizContent.getModel().setCategoryId(categoryId);
		return this;
	}
	
	public String getSource() {
		return bizContent.getModel().getSource();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setSource(String source) {
		bizContent.getModel().setSource(source);
		return this;
	}

	public String getMemo() {
		return bizContent.getModel().getMemo();
	}


	public AntMerchantExpandIndirectModifyRequetBuilder setMemo(String memo) {
		bizContent.getModel().setMemo(memo);
		return this;
	}
	
	public AntMerchantExpandIndirectModifyRequetBuilder setMcc(String mcc) {
		bizContent.getModel().setMcc(mcc);
		return this;
	}
	
	public AntMerchantExpandIndirectModifyRequetBuilder setOrgPid(String orgPid) {
		bizContent.getModel().setOrgPid(orgPid);
		return this;
	}
	
	public String getBusinessLicense() {
		return bizContent.getModel().getBusinessLicense();
	}

	public AntMerchantExpandIndirectModifyRequetBuilder setBusinessLicense(String businessLicense) {
		bizContent.getModel().setBusinessLicense(businessLicense);
		return this;
	}

	public String getBusinessLicenseType() {
		return bizContent.getModel().getBusinessLicenseType();
	}

	public AntMerchantExpandIndirectModifyRequetBuilder setBusinessLicenseType(String businessLicense) {
		bizContent.getModel().setBusinessLicenseType(businessLicense);
		return this;
	}
	
	public List<String> getPayCodeInfo() {
		return bizContent.getModel().getPayCodeInfo();
	}

	public AntMerchantExpandIndirectModifyRequetBuilder setPayCodeInfo(List<String> payCodeInfo) {
		bizContent.getModel().setPayCodeInfo(payCodeInfo);;
		return this;
	}
	
	public List<String> getLogonId() {
		return bizContent.getModel().getLogonId();
	}
	
	public AntMerchantExpandIndirectModifyRequetBuilder setLogonId(List<String> logonId) {
		bizContent.getModel().setLogonId(logonId);
		return this;
	}

	public List<BankCardInfo> getBankcardInfo() {
		return bizContent.getModel().getBankcardInfo();
	}
	
	public AntMerchantExpandIndirectModifyRequetBuilder setBankcardInfo(List<BankCardInfo> bankcardInfo) {
		bizContent.getModel().setBankcardInfo(bankcardInfo);
		return this;
	}
	
	public List<AddressInfo> getAddressInfo() {
		return bizContent.getModel().getAddressInfo();
	}
	
	public AntMerchantExpandIndirectModifyRequetBuilder setAddressInfo(List<AddressInfo> addressInfo) {
		bizContent.getModel().setAddressInfo(addressInfo);
		return this;
	}
	
	public List<ContactInfo> getContactInfo() {
		return bizContent.getModel().getContactInfo();
	}
	
	public AntMerchantExpandIndirectModifyRequetBuilder setContactInfo(List<ContactInfo> contactInfo) {
		bizContent.getModel().setContactInfo(contactInfo);
		return this;
	}
	
	public String getSubMerchantId() {
		return bizContent.getModel().getSubMerchantId();
	}

	public AntMerchantExpandIndirectModifyRequetBuilder setSubMerchantId(String subMerchantId) {
		bizContent.getModel().setSubMerchantId(subMerchantId);
		return this;
	}
	
	public static class BizContent{
		
		private AntMerchantExpandIndirectModifyModel model = new AntMerchantExpandIndirectModifyModel();

		public AntMerchantExpandIndirectModifyModel getModel() {
			return model;
		}

		public void setModel(AntMerchantExpandIndirectModifyModel model) {
			this.model = model;
		}
		
	}
}
