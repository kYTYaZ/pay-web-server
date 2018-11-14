package com.alipay.demo.trade.model.builder;


import com.alipay.api.internal.util.StringUtils;
import com.google.gson.annotations.SerializedName;

public class AlipaySystemOauthTokenBuilder extends  RequestBuilder{
	
	private BizContent bizContent = new BizContent();
	
	@Override
	public boolean validate() {
		
		if(StringUtils.isEmpty(bizContent.grantType)){
			return false;
		}
		
		if(StringUtils.isEmpty(bizContent.code)){
			return false;
		}
		
		return true;
	}

	@Override
	public Object getBizContent() {
		return bizContent;
	}

	public AlipaySystemOauthTokenBuilder setGrantType(String grantType){
		bizContent.grantType = grantType;
		return this;
	}
	
	public String getGrantType(){
		return bizContent.grantType;
	}
	
	public AlipaySystemOauthTokenBuilder setCode(String code){
		bizContent.code = code;
		return this;
	}
	
	public String getCode(){
		return bizContent.code;
	}
	
	
	public AlipaySystemOauthTokenBuilder setRefreshToken(String refreshToken){
		bizContent.refreshToken = refreshToken;
		return this;
	}
	
	public String GetRefreshToken(){
		return bizContent.refreshToken;
	}
	
	public static class BizContent{
		
		@SerializedName("grantType")
		private String grantType;
		
		@SerializedName("code")
		private String code;
		
		@SerializedName("refreshToken")
		private String refreshToken;
		
		
		@Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("grantType='").append(grantType).append('\'');
            sb.append(", code='").append(code).append('\'');    
            sb.append(", refreshToken='").append(refreshToken).append('\'');
            sb.append('}');
            
            return sb.toString();
        }
	}
}
