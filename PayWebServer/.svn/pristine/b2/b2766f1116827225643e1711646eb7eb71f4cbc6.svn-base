package com.huateng.pay.po.local;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.alipay.demo.trade.utils.GsonFactory;
import com.google.gson.annotations.SerializedName;

/**
 * 查询静态二维码应答 二维码信息集
 * 
 * @author zhaoyuanxiang
 */
@XmlRootElement(name="qrCodeInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class QRCodeInfo {
	
	// 二维码信息
	@SerializedName("codeUrl")
	private String codeUrl;

	// 卡号
	@SerializedName("acctNo")
	private String acctNo;

	// 二维码类型
	@SerializedName("localQrType")
	private String localQrType;
	
	//客户名称
	@SerializedName("customName")
	private String customName;
	
	public String getCodeUrl() {
		return codeUrl;
	}

	public void setCodeUrl(String codeUrl) {
		this.codeUrl = codeUrl;
	}
	

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}
	
	public String getLocalQrType() {
		return localQrType;
	}

	public void setLocalQrType(String localQrType) {
		this.localQrType = localQrType;
	}

	 public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public static String toJson(Object obj){
	    	
	    return  GsonFactory.getGson().toJson(obj);
	    	
	}
}
