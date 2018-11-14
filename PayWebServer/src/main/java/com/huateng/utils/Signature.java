package com.huateng.utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.alipay.api.internal.util.AlipaySignature;
import com.huateng.pay.common.util.StringUtil;

/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 15:23
 */
public class Signature {
	
	private static Logger logger = LoggerFactory.getLogger(Signature.class);
	
    /**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Object o,String key) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
                list.add(f.getName() + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        logger.info("Sign Before MD5:" + result);
        result = Util.MD5Encode(result).toUpperCase();
        logger.info("Sign Result:" + result);
        return result;
    }

    public static String getSign(Map<String,Object> map,String key){
        ArrayList<String> list = new ArrayList<String>();
        for(Map.Entry<String,Object> entry:map.entrySet()){
            if(entry.getValue() != null && !"".equals(entry.getValue().toString()) ){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
      
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        //Arrays.sort(arrayToSort);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        if(!StringUtil.isEmpty(key)){
        	result += "key=" + key;
        }
        logger.debug("Sign Before MD5:" + result);
        result = Util.MD5Encode(result).toUpperCase();
        logger.debug("Sign Result:" + result);
        return result;
    }
    
	private static String replaceURLDecodeIllegalCharacter(String toBeDecode) {

		return toBeDecode.replaceAll("%(?![0-9a-fA-F]{2})", "%25").replaceAll(
				"\\+", "%2B");
	}
    public static boolean getSign(Map<String,Object> map,String sign,String signType,String charset,String publicKey)throws Exception{
    	
    	List<String> list = new ArrayList<String>();
		
    	for (Entry<String, Object> m : map.entrySet()) {
    		String key = String.format("%s", m.getKey());
    		String val = String.format("%s", m.getValue());
    		if(!StringUtil.isEmpty(val)){
    			String value =  URLDecoder.decode(replaceURLDecodeIllegalCharacter(val),charset);
    			String keyValue = String.format("%s%s%s%s", key,"=",value,"&");
    			list.add(keyValue);
    		}
		}
		
		int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
      
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        
        String result = sb.delete(sb.length()-1, sb.length()).toString();
       
        return AlipaySignature.rsaCheck(result, sign, publicKey, charset, signType);

    }
    
    /**
     * 从API返回的XML数据里面重新计算一次签名
     * @param responseString API返回的XML数据
     * @return 新鲜出炉的签名
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static String getSignFromResponseString(String responseString,String key) throws IOException, SAXException, ParserConfigurationException {
        Map<String,Object> map = Util.getMapFromXML(responseString);
        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.put("sign","");
        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        return Signature.getSign(map,key);
    }
    
    /**
	 * 请求报文签名(使用配置文件中配置的私钥证书或者对称密钥签名)<br>
	 * 功能：对请求报文进行签名,并计算赋值certid,signature字段并返回<br>
	 * @param reqData 请求报文map<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return　签名后的map对象<br>
	 */
	public static Map<String, String> sign(Map<String, String> reqData,String encoding) {
		reqData = Util.filterBlank(reqData);
		CertUtil.sign(reqData, encoding);
		return reqData;
	}

	/**
	 * 验证签名(SHA-1摘要算法)<br>
	 * @param resData 返回报文数据<br>
	 * @param encoding 上送请求报文域encoding字段的值<br>
	 * @return true 通过 false 未通过<br>
	 */
	public static boolean validate(Map<String, String> rspData, String encoding) {
		return CupsUtil.validate(rspData, encoding);
	}

}
