package com.huateng.utils;

public enum QRCodeTypeEnum {
	
	DYNAMIC_QR_CODE_POSP("00"), 
	DYNAMIC_QR_CODE_MOBILE("01"), 
	DYNAMIC_QR_CODE_MOBILE_FRONT("02"),
	DYNAMIC_QR_CODE_PAY_PLATFORM("20"),
	STATIC_QR_CODE_LSSY("10"), 
	STATIC_QR_CODE_OTHER("11"), 
	STATIC_QR_CODE_THREE_CODE("12"),
	STATIC_QR_CODE_CUPS("13"),
	STATIC_QR_CODE_LOCAL_CUPS("62");
	
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private QRCodeTypeEnum(String type){
		this.type = type;		
	}

	
	public static boolean isContains(String qrType){
		for (QRCodeTypeEnum type : QRCodeTypeEnum.values()) {
			if(type.getType().equals(qrType)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return this.type;
	}
}
