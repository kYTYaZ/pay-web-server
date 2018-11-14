package com.huateng.pay.services.weixin.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.services.weixin.YLWXPayService;
import com.huateng.pay.services.weixin.wxutil.WXPayConstants;
import com.huateng.pay.services.weixin.wxutil.WXPayRequest;
import com.huateng.pay.services.weixin.wxutil.WXPayUtil;
import com.wldk.framework.utils.MappingUtils;

/**
 * 微信支付核心步骤，如订单查询等
 */
public class YLWXPayServiceImpl implements YLWXPayService{
	
	
	/**
	 * 作用：发送 银联微信交易
	 * 
	 * @param cacheKey 配置的key
	 * 
	 * @return API返回数据
	 * @throws Exception
	 */
	public Map<String, String> wxSend(Map<String, String> reqData,String cacheKey,String url) throws Exception {
		this.fillRequestData(reqData,cacheKey);
		String respXml = this.post(reqData, url, WXPayConstants.CHARSET_UTF_8);
		return this.processResponseXml(respXml,cacheKey);
	}
	/**
	 * 向 Map 中添加 appid、mch_id、sub_mch_id、sign <br>
	 * @param reqData
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> fillRequestData(Map<String, String> reqData,String cacheKey) throws Exception {
		Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
		reqData.put("appid", configMap.get(Dict.WX_APPID));
		reqData.put("mch_id", configMap.get(Dict.WX_MERID));
		reqData.put("channel_id", configMap.get(Dict.WX_CHANNEL_ID));
		reqData.put("nonce_str", WXPayUtil.generateUUID());
//		reqData.put("sub_mch_id", SDKConfig.getConfig().getSubMchId());
		reqData.put("sign", WXPayUtil.generateSignature(reqData, configMap.get(Dict.ALI_PRIVATE_KEY)));
		return reqData;
	}

	/**
	 * 判断xml数据的sign是否有效，必须包含sign字段，否则返回false。
	 *
	 * @param reqData
	 *            向wxpay post的请求数据
	 * @return 签名是否有效
	 * @throws Exception
	 */
	public boolean isResponseSignatureValid(Map<String, String> reqData,String cacheKey) throws Exception {
		String signStr = reqData.get(WXPayConstants.FIELD_SIGN);
		if (StringUtils.isBlank(signStr)) {
			return false;
		}
		String content = WXPayUtil.getSignContent(reqData, "sign");
		return WXPayUtil.rsa256CheckContent(content, signStr,MappingUtils.getChannelConfig(cacheKey).get(Dict.ALI_PUBLIC_KEY));
	}

	private String post(Map<String, String> reqData, String reqUrl, String encoding) throws Exception {
		Map<String, String> rspData = new HashMap<String, String>();
		WXPayUtil.getLogger().info("请求银联地址:" + reqUrl);
		// 发送后台请求数据
		WXPayRequest hc = new WXPayRequest(reqUrl, 30000, 30000);// 连接超时时间，读超时时间（可自行判断，修改）
		String resultString = "";
		try {
			int status = hc.send(reqData, encoding);
			if (200 == status) {
				resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					// 将返回结果转换为map
					Map<String, String> tmpRspData = WXPayUtil.convertString2Map(resultString);
					rspData.putAll(tmpRspData);
				}
			} else {
				WXPayUtil.getLogger().info("返回http状态码[" + status + "]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			WXPayUtil.getLogger().error("银联微信请求异常 "+e.getMessage(), e);
			throw new Exception("银联微信请求异常 "+e.getMessage());
		}
		return resultString;
	}

	/**
	 * 功能：http Get方法<br>
	 */
	@SuppressWarnings("unused")
	private static String get(String reqUrl, String encoding) {
		WXPayUtil.getLogger().info("请求银联地址:" + reqUrl);
		// 发送后台请求数据
		WXPayRequest hc = new WXPayRequest(reqUrl, 30000, 30000);
		try {
			int status = hc.sendGet(encoding);
			if (200 == status) {
				String resultString = hc.getResult();
				if (null != resultString && !"".equals(resultString)) {
					return resultString;
				}
			} else {
				WXPayUtil.getLogger().info("返回http状态码[" + status + "]，请检查请求报文或者请求地址是否正确");
			}
		} catch (Exception e) {
			WXPayUtil.getLogger().error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
	 * 
	 * @param xmlStr
	 *            API返回的XML格式数据
	 * @return Map类型数据
	 * @throws Exception
	 */
	public Map<String, String> processResponseXml(String xmlStr,String cacheKey) throws Exception {
		String RETURN_CODE = "return_code";
		String return_code;
		Map<String, String> respData = WXPayUtil.xmlToMap(xmlStr);
		if (respData.containsKey(RETURN_CODE)) {
			return_code = respData.get(RETURN_CODE);
		} else {
			throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
		}

		if (return_code.equals(WXPayConstants.FAIL)) {
			return respData;
		} else if (return_code.equals(WXPayConstants.SUCCESS)) {
			if (this.isResponseSignatureValid(respData,cacheKey)) {
				return respData;
			} else {
				throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
			}
		} else {
			throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
		}
	}


}
