package com.huateng.pay.po.weixin;

/**
 * 新增商户请求数据
 */

import java.util.Map;

import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.Signature;

/**
 * 新增商户API需要提交的数据
 */
public class AddWxMerData extends WxBaseData{
	// 每个字段具体的意思请查看API文档
	private String merchant_name = "";
	private String merchant_shortname = "";
	private String service_phone = "";
	private String contact = "";
	private String contact_phone = "";
	private String contact_email = "";
	private String business = "";
	private String merchant_remark = "";
	private String channel_id = "";
	private String sign = "";

	/**
	 * @param appid 受理公众账号ID，微信分配的公众号ID（开通公众号之后可以获取到）
	 * @param mch_id 受理商户号，微信支付分配的商户号
	 * @param sign 签名
	 * @param merchant_name 商户名称，须与营业执照的名称保持一致
	 * @param merchant_shortname 商户简称，该名称将于支付成功页向消费者进行展示
	 * @param service_phone 客服电话，必填，以方便微信在必要时能联系上商家
	 * @param contact 联系人，选填，以方便微信在必要时能联系上商家，此字段也用于商户平台安装操作证书使用
	 * @param contact_phone 联系电话，选填，以方便微信在必要时能联系上商家，此字段也用于发送开户邮箱、重置密码使用
	 * @param contact_email 联系邮箱，选填，以方便微信在必要时能联系上商家，此字段也用于发送开户邮箱、重置密码使用
	 * @param business 经营类目，必填，须与实际售卖商品保持一致，如开通公众号将会在公众号的Profile页面展示
	 * @param merchant_remark 商户备注，同一个受理结构，子商户“商户备注”唯一，不同受理机构间，“商户备注”允许重复
	 * 
	 */
	public AddWxMerData(Map<String, Object> dataMap, String key) {
		super(dataMap);
		
		// 须与营业执照的名称保持一致
		setMerchant_name(dataMap.get("merchant_name").toString());

		// 该名称将于支付成功页向消费者进行展示
		setMerchant_shortname(dataMap.get("merchant_shortname").toString());

		// 必填，以方便微信在必要时能联系上商家
		setService_phone(dataMap.get("service_phone").toString());

		// 选填，以方便微信在必要时能联系上商家，此字段也用于商户平台安装操作证书使用
		if (!StringUtil.isEmpty(dataMap.get("contact"))) {
			setContact(dataMap.get("contact").toString());
		}

		// 选填，以方便微信在必要时能联系上商家，此字段也用于发送开户邮箱、重置密码使用
		if (!StringUtil.isEmpty(dataMap.get("contact_phone"))) {
			setContact_phone(dataMap.get("contact_phone").toString());
		}

		// 选填，以方便微信在必要时能联系上商家，此字段也用于发送开户邮箱、重置密码使用
		if (!StringUtil.isEmpty(dataMap.get("contact_email"))) {
			setContact_email(dataMap.get("contact_email").toString());
		}

		// 必填，须与实际售卖商品保持一致，如开通公众号将会在公众号的Profile页面展示
		setBusiness(dataMap.get("business").toString());

		// 同一个受理结构，子商户“商户备注”唯一，不同受理机构间，“商户备注”允许重复
		setMerchant_remark(dataMap.get("merchant_remark").toString());
		
		//渠道号
		setChannel_id(dataMap.get("wxChannelId").toString());
		
		//如果有上送则使用上送的
		if (!StringUtil.isEmpty(dataMap.get("channel_id"))) {
			setChannel_id(dataMap.get("channel_id").toString());
		}
		
		// 根据API给的签名规则进行签名
		Map<String, Object> map =  toMap();
		String sign = Signature.getSign(map, key);
		setSign(sign);// 把签名数据设置到Sign这个属性中

	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchantName) {
		merchant_name = merchantName;
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

	public String getContact_phone() {
		return contact_phone;
	}

	public void setContact_phone(String contactPhone) {
		contact_phone = contactPhone;
	}

	public String getContact_email() {
		return contact_email;
	}

	public void setContact_email(String contactEmail) {
		contact_email = contactEmail;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getMerchant_remark() {
		return merchant_remark;
	}

	public void setMerchant_remark(String merchantRemark) {
		merchant_remark = merchantRemark;
	}

	public String getChannel_id() {
		return channel_id;
	}

	public void setChannel_id(String channelId) {
		channel_id = channelId;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
