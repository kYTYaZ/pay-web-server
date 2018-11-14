package com.alipay.demo.trade.model.builder;

import com.alipay.api.domain.AntMerchantExpandIndirectQueryModel;
import com.alipay.api.internal.util.StringUtils;

public class AntMerchantExpandIndirectQueryRequetBuilder extends RequestBuilder{

private BizContent bizContent = new BizContent();
	
	@Override
	public BizContent getBizContent() {
		return bizContent;
	}

	@Override
	public boolean validate() {
		
		if(StringUtils.isEmpty(bizContent.getModel().getExternalId()) && StringUtils.isEmpty(bizContent.getModel().getSubMerchantId())){
			throw new NullPointerException("externalId or subMerchantId should not be NULL!");
		}
		
		return true;
		
	}


	public String getExternalId() {
		return bizContent.getModel().getExternalId();
	}


	public AntMerchantExpandIndirectQueryRequetBuilder setExternalId(String externalId) {
		bizContent.getModel().setExternalId(externalId);
		return this;
	}

	public String getSubMerchantId() {
		return bizContent.getModel().getSubMerchantId();
	}

	public AntMerchantExpandIndirectQueryRequetBuilder setSubMerchantId(String subMerchantId) {
		bizContent.getModel().setSubMerchantId(subMerchantId);
		return this;
	}
	
	public static class BizContent{
		
		private AntMerchantExpandIndirectQueryModel model = new AntMerchantExpandIndirectQueryModel();

		public AntMerchantExpandIndirectQueryModel getModel() {
			return model;
		}

		public void setModel(AntMerchantExpandIndirectQueryModel model) {
			this.model = model;
		}
	}
}
