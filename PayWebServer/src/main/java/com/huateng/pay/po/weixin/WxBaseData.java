package com.huateng.pay.po.weixin;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.util.StringUtil;

public class WxBaseData {

	private static Logger logger = LoggerFactory.getLogger(WxBaseData.class);
	
	/**
	 * 微信分配的公众号ID（开通公众号之后可以获取到）
	 */
	private String appid = "";
	/**
	 * 微信支付分配的商户号
	 */
	private String mch_id = "";

	public WxBaseData(Map<String, Object> dataMap) {
		this.appid = StringUtil.toString(dataMap.get("appid"));
		this.mch_id = StringUtil.toString(dataMap.get("mch_id"));
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mchId) {
		mch_id = mchId;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		Method[] methods = this.getClass().getMethods();
		for (Method m : methods) {
			String methName = m.getName();
			if (methName.startsWith("get")) {
				String fieldName = methName.substring(3, 4).toLowerCase() + methName.substring(4);
				if ("class".equals(fieldName)) {
					continue;
				}
				try {
					Object value = m.invoke(this, null);
					map.put(fieldName, value);
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		return map;
	}

	/**
	 * map集合转xml字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToXml(Map<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<xml>");

			Set<String> set = map.keySet();
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String key = it.next();
				Object value = map.get(key);
				if (null == value) {
					value = "";
				}
				sb.append("<" + key + ">" + value + "</" + key + ">");
			}
			sb.append("</xml>");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return sb.toString();
	}

	/**
	 * 将Map转换为XML格式的字符串
	 *
	 * @param data Map类型数据
	 * @return XML格式的字符串
	 * @throws Exception
	 */
//    public static String mapToXml(Map<String,Object> data) throws Exception {
//        org.w3c.dom.Document document = WXPayXmlUtil.newDocument();
//        org.w3c.dom.Element root = document.createElement("xml");
//        document.appendChild(root);
//        for (String key: data.keySet()) {
//        	Object obj = data.get(key);
//            String value = null;
//            if (obj == null) {
//                value = "";
//            }else {
//            	value = obj.toString();
//            }
//            value = value.trim();
//            org.w3c.dom.Element filed = document.createElement(key);
//            filed.appendChild(document.createTextNode(value));
//            root.appendChild(filed);
//        }
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        DOMSource source = new DOMSource(document);
//        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        StringWriter writer = new StringWriter();
//        StreamResult result = new StreamResult(writer);
//        transformer.transform(source, result);
//        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
//        try {
//            writer.close();
//        }
//        catch (Exception ex) {
//        }
//        return output;
//    }

	/**
	 * 生成签名. 注意，若含有sign_type字段，必须和signType参数保持一致。
	 *
	 * @param data     待签名数据
	 * @param key      API密钥
	 * @param signType 签名方式
	 * @return 签名
	 */
	public static String generateSignature(final Map<String, Object> data, String key) throws Exception {
		Set<String> keySet = data.keySet();
		String[] keyArray = keySet.toArray(new String[keySet.size()]);
		Arrays.sort(keyArray);
		StringBuilder sb = new StringBuilder();
		for (String k : keyArray) {
			if (k.equals("sign")) {
				continue;
			}
			if (data.get(k) != null && data.get(k).toString().trim().length() > 0) // 参数值为空，则不参与签名
				sb.append(k).append("=").append(data.get(k).toString().trim()).append("&");
		}
		sb.append("key=").append(key);
		return MD5(sb.toString()).toUpperCase();
	}

	/**
	 * 生成 MD5
	 *
	 * @param data 待处理数据
	 * @return MD5结果
	 */
	public static String MD5(String data) throws Exception {
		java.security.MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] array = md.digest(data.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte item : array) {
			sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
		}
		return sb.toString().toUpperCase();
	}

	/**
	 * 生成带有 sign 的 XML 格式字符串
	 *
	 * @param data     Map类型数据
	 * @param key      API密钥
	 * @param signType 签名类型
	 * @return 含有sign字段的XML
	 */
	public static String generateSignedXml(final Map<String, Object> data, String key) throws Exception {
		String sign = generateSignature(data, key);
		data.put("sign", sign);
		return mapToXml(data);
	}

}
