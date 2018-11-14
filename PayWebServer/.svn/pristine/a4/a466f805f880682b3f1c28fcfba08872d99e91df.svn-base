package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.pay.common.util.Constants;
import com.huateng.utils.Signature;

/**
 * 删除商户API需要提交的数据
 */
public class DeleteWxMerData  extends WxBaseData{

	// 每个字段具体的意思请查看API文档
	private String sub_mch_id = "";
	private String sign;

    /**
     * @param appid 受理公众账号ID，微信分配的公众账号ID
     * @param mch_id 受理商户号，微信支付分配的商户号
     * @param sub_mch_id 商户识别码，微信支付分配的商户识别码
     * @param sign 签名
     * 
     */
	public DeleteWxMerData(Map<String, Object> dataMap, String key) {
		super(dataMap);
		// 微信分配的公众号ID（开通公众号之后可以获取到）
		setAppid(Constants.getParam("jg_appId"));

		// 微信支付分配的商户号
		setMch_id(Constants.getParam("jg_merId"));

		// 微信支付分配的商户识别码
		setSub_mch_id(dataMap.get("sub_mch_id").toString());

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
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
