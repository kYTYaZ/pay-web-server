package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.Signature;

/**
 * 修改商户API需要提交的数据
 */
public class ModifyWxMerData  extends WxBaseData{

	// 每个字段具体的意思请查看API文档
	private String sub_mch_id = "";
	private String merchant_shortname = "";
	private String service_phone = "";
	private String contact = ""; 
	private String sign;

    /**
     * @param appid 受理公众账号ID，微信分配的公众账号ID
     * @param mch_id 受理商户号，微信支付分配的商户号
     * @param sub_mch_id 商户识别码，微信支付分配的商户识别码
     * @param sign 签名
     * @param merchant_shortname 商户简称，该名称将于支付成功页向消费者进行展示
     * @param service_phone 客服电话，该名称将于支付成功页向消费者进行展示
     * @param contact 联系人，如开公众号支付，则该字段必填，以方便微信在必要时能联系上
     * 
     */
	public ModifyWxMerData(Map<String, Object> dataMap, String key) {
		super(dataMap);
		// 微信分配的公众号ID（开通公众号之后可以获取到）
		setAppid(Constants.getParam("jg_appId"));

		// 微信支付分配的商户号
		setMch_id(Constants.getParam("jg_merId"));

		// 微信支付分配的商户识别码
		setSub_mch_id(dataMap.get("sub_mch_id").toString());

		// 该名称将于支付成功页向消费者进行展示
		setMerchant_shortname(dataMap.get("merchant_shortname").toString());

		// 该名称将于支付成功页向消费者进行展示
		setService_phone(dataMap.get("service_phone").toString());

		// 非必输，如开公众号支付，则该字段必填，以方便微信在必要时能联系上
		if (!StringUtil.isEmpty(dataMap.get("contact"))) {
			setContact(dataMap.get("contact").toString());
		}

		// 根据API给的签名规则进行签名
		String sign = Signature.getSign(toMap(), key);
		setSign(sign);// 把签名数据设置到Sign这个属性中

	}


	public String getSub_mch_id() {
		return sub_mch_id;
	}

	public void setSub_mch_id(String subMchId) {
		sub_mch_id = subMchId;
	}

	public String getMerchant_shortname() {
		return merchant_shortname;
	}

	public void setMerchant_shortname(String merchantShortname) {
		merchant_shortname = merchantShortname;
	}

	public String getService_phone() {
		return service_phone;
	}

	public void setService_phone(String servicePhone) {
		service_phone = servicePhone;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
