package com.alipay.demo.trade.model.builder;

import com.alipay.api.internal.util.StringUtils;
import com.google.gson.annotations.SerializedName;

public class AlipayTradeQueryBillRequestBuilder extends RequestBuilder {
	
	private BizContent bizContent = new BizContent();
	
	@Override
	public boolean validate() {
		if(StringUtils.isEmpty(bizContent.billType)){
			throw new NullPointerException("billType  should not be NULL!");
		}
		
		if (StringUtils.isEmpty(bizContent.billDate)){
			throw new NullPointerException("billDate  should not be NULL!");
		}
	
		return true;
	}

	@Override
	public BizContent getBizContent() {
		return bizContent;
	}
	
	
	public String getBillType(){
		return bizContent.billType;
	}
	
	public AlipayTradeQueryBillRequestBuilder setBillType(String billType){
		bizContent.billType = billType;
		return this;
	}
	
	public String getBillDate(){
		return bizContent.billDate;
	}
	
	public AlipayTradeQueryBillRequestBuilder setBillDate(String billDate){
		bizContent.billDate = billDate;
		return this;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AlipayTradeBillQueryRequestBuilder{");
	    sb.append("bizContent=").append(bizContent);
	    sb.append(", super=").append(super.toString());
	    sb.append('}');
	    return sb.toString();
	}
	
	
	public static class BizContent{
		
		//账单类型
		@SerializedName("bill_type")
		public String billType;
		
		//账单日期
		@SerializedName("bill_date")
		public String billDate;
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder("BizContent{");
	        sb.append("billType='").append(billType).append('\'');
	        sb.append(", billDate='").append(billDate).append('\'');
	        sb.append('}');
	        
	        return sb.toString();
		}
		
	}
}
