package com.huateng.pay.po.weixin;

import java.util.Map;

import com.huateng.utils.Signature;

/**
 * 查询商户API需要提交的数据
 */
public class QueryWxMerData  extends WxBaseData{

	// 每个字段具体的意思请查看API文档
	private String sub_mch_id = "";
	private String merchant_name = "";
	private String page_index = "";
	private String page_size = "";
	private String sign;

    /**
     * @param appid 受理公众账号ID，微信分配的公众账号ID
     * @param mch_id 受理商户号，微信支付分配的商户号
     * @param sub_mch_id 商户识别码，微信支付分配的商户识别码
     * @param merchant_name 商户名称，公司名称需与营业执照登记公司名称一致
     * @param sign 签名
     * @param page_index 页码，当前查询的具体分页页面
     * @param page_size 展示资料个数，每一个分页，可展示多少条资料信息
     * 
     */
	public QueryWxMerData(Map<String, Object> dataMap, String key) {
		super(dataMap);
		// 微信分配的公众号ID（开通公众号之后可以获取到）
		setAppid(dataMap.get("appid").toString());

		// 微信支付分配的商户号
		setMch_id(dataMap.get("mch_id").toString());

		// 微信支付分配的商户识别码
		setSub_mch_id(dataMap.get("sub_mch_id").toString());

		// 公司名称需与营业执照登记公司名称一致
		setMerchant_name(dataMap.get("merchant_name").toString());

		// 当前查询的具体分页页面
		setPage_index(dataMap.get("page_index").toString());

		// 每一个分页，可展示多少条资料信息
		setPage_size(dataMap.get("page_size").toString());

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

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchantName) {
		merchant_name = merchantName;
	}

	public String getPage_index() {
		return page_index;
	}

	public void setPage_index(String pageIndex) {
		page_index = pageIndex;
	}

	public String getPage_size() {
		return page_size;
	}

	public void setPage_size(String pageSize) {
		page_size = pageSize;
	}
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
