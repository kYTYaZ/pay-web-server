package com.huateng.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.huateng.cert.CertUtils;
import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.po.notify.NotifyBeanAnnotation;
import com.huateng.pay.po.notify.NotifyMessage;
import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 工具类
 * @author guohuan
 *
 */
public class Util {
	
	private static Logger logger = LoggerFactory.getLogger(Util.class);
	
    private final static String[] hexDigits = {"0", "1", "2", "3", "4", "5", "6", "7",
        "8", "9", "a", "b", "c", "d", "e", "f"};
    
    private final static String regExNum = "(-?\\d+)(\\.\\d{0,2})?";   //正整数或者带有两位小数数字

    public static byte[] readInput(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = 0;
        byte[] buffer = new byte[1024];
        while ((len = in.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }

    public static String inputStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }


    public static InputStream getStringStream(String sInputString) throws UnsupportedEncodingException {
        ByteArrayInputStream tInputStringStream = null;
        if (sInputString != null && !sInputString.trim().equals("")) {
            tInputStringStream = new ByteArrayInputStream(sInputString.getBytes("UTF-8"));
        }
        return tInputStringStream;
    }

    public static Object getObjectFromXML(String xml, Class tClass) {
        //将从API返回的XML数据映射到Java对象
        XStream xStreamForResponseData = new XStream();
        xStreamForResponseData.alias("xml", tClass);
        xStreamForResponseData.ignoreUnknownElements();//暂时忽略掉一些新增的字段
        return xStreamForResponseData.fromXML(xml);
    }

    public static String getStringFromMap(Map<String, Object> map, String key, String defaultValue) {
        if (key == "" || key == null) {
            return defaultValue;
        }
        String result = (String) map.get(key);
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    public static int getIntFromMap(Map<String, Object> map, String key) {
        if (key == "" || key == null) {
            return 0;
        }
        if (map.get(key) == null) {
            return 0;
        }
        return Integer.parseInt((String) map.get(key));
    }


    /**
     * 读取本地的xml数据，一般用来自测用
     * @param localPath 本地xml文件路径
     * @return 读到的xml字符串
     */
    public static String getLocalXMLString(String localPath) throws IOException {
        return Util.inputStreamToString(Util.class.getResourceAsStream(localPath));
    }

    /**
     * 获取一定长度的随机字符串
     * @param length 指定字符串长度
     * @return 一定长度的字符串
     */
    public static String getRandomStringByLength(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    /**
     * 解析xml转map
     * @param xmlString
     * @return
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Map<String,Object> getMapFromXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        //这里用Dom的方式解析回包的最主要目的是防止API新增回包字段
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputStream is =  Util.getStringStream(xmlString);
        Document document = builder.parse(is);

        //获取到document里面的全部结点
        NodeList allNodes = document.getFirstChild().getChildNodes();
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < allNodes.getLength(); i++) {
        	Node node = allNodes.item(i);
        	nodeIterator(node,map,2000);	
		}
        
        return map;
    }

    public static  void nodeIterator(Node node,Map<String, Object> map,int deep){
    	if (deep > 2000) {
			// 具体数值由虚拟机内存大小和每次递归堆栈大小决定，一般在2000次左右
			deep = 2000;
		}
		
		if (--deep < 0) {
			return;
		}
		
    	if(node instanceof Element && node.hasChildNodes()){
    		for (int i = 0; i < node.getChildNodes().getLength(); i++) {
    			Node subNode = node.getChildNodes().item(i);
    			if(subNode.getNodeType() == Node.ELEMENT_NODE){
    				nodeIterator(subNode,map,2000);
    			}else{
    				map.put(node.getNodeName(), node.getTextContent());
    			}
    		}	
		}
    }
    
	/**
	 * 转换字节数组为16进制字串
	 * @param b 字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
	    StringBuilder resultSb = new StringBuilder();
	    for (byte aB : b) {
	        resultSb.append(byteToHexString(aB));
	    }
	    return resultSb.toString();
	}
	
	/**
	 * 转换byte到16进制
	 * @param b 要转换的byte
	 * @return 16进制格式
	 */
	private static String byteToHexString(byte b) {
	    int n = b;
	    if (n < 0) {
	        n = 256 + n;
	    }
	    int d1 = n / 16;
	    int d2 = n % 16;
	    return hexDigits[d1] + hexDigits[d2];
	}
	
	/**
	 * MD5编码
	 * @param origin 原始字符串
	 * @return 经过MD5加密之后的结果
	 */
	public static String MD5Encode(String origin) {
	    String resultString = null;
	    try {
	        resultString = origin;
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	    }
	    return resultString;
	}
	
	// 返回形式为数字跟字符串
	private static String byteToArrayString(byte bByte) {
	    int iRet = bByte;
	    // System.out.println("iRet="+iRet);
	    if (iRet < 0) {
	        iRet += 256;
	    }
	    int iD1 = iRet / 16;
	    int iD2 = iRet % 16;
	    return hexDigits[iD1] + hexDigits[iD2];
	}
	
	// 转换字节数组为16进制字串
	private static String byteToString(byte[] bByte) {
	    StringBuffer sBuffer = new StringBuffer();
	    for (int i = 0; i < bByte.length; i++) {
	        sBuffer.append(byteToArrayString(bByte[i]));
	    }
	    return sBuffer.toString();
	}
	
	public static String GetMD5Code(String strObj) throws UnsupportedEncodingException {
	    String resultString = null;
	    try {
	        resultString = new String(strObj);
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        // md.digest() 该函数返回值为存放哈希值结果的byte数组
	        resultString = byteToString(md.digest(strObj.getBytes("UTF-8")));
	    } catch (NoSuchAlgorithmException ex) {
	        ex.printStackTrace();
	    }
	    return resultString;
	}
	
	public static String getMD5(String str) throws Exception {
	    /** 创建MD5加密对象 */
	    MessageDigest md5 = MessageDigest.getInstance("MD5"); 
	    /** 进行加密 */
	    md5.update(str.getBytes("UTF-8"));
	    /** 获取加密后的字节数组 */
	    byte[] md5Bytes = md5.digest();
	    String res = "";
	    for (int i = 0; i < md5Bytes.length; i++){
	        int temp = md5Bytes[i] & 0xFF;
	        if (temp <= 0XF){ // 转化成十六进制不够两位，前面加零
	            res += "0";
	        }
	        res += Integer.toHexString(temp);
	    }
	    return res;
	}
	
	/**
	 * 获取表中的微信商户号
	 * @param args
	 * @throws Exception
	 */
	public static String getWxParam(String keyPath,String keyPwd,String encodeStr) throws Exception{
		PrivateKey priKey = CertUtils.getPrivateKey(keyPath, keyPwd);
		String de = SecureUtil.DecryptedData(encodeStr, "UTF-8", priKey);
		return de;
	}
	
	/**
	 * 
	 * 功能描述: <br>
	 *  验证 是否为  null
	 *
	 * @param arrayValue
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static boolean validateString(Object[] arrayValue){
	    boolean flag = false;
	    if(arrayValue.length > 0){
	        for(int i = 0; i < arrayValue.length; i++){
	            arrayValue[i] = (arrayValue[i] == null ? "" : arrayValue[i].toString());
	            if(StringUtils.isBlank(arrayValue[i].toString())
	                    ||StringUtils.isEmpty(arrayValue[i].toString())
	                    ||"null".equalsIgnoreCase(arrayValue[i].toString())
	                    ||"".equals(arrayValue[i])){
	                flag = true;            //为空
	                return flag;
	            }else{
	                flag = false;
	            }
	        }
	    }else{
	        flag = false;
	    }
	    
	    return flag;
	}
	
	/**
	 * 验证输入字段是否为空
	 * @param input
	 * @return
	 */
	public static String validateIsNull(Map<String, Object> map){
		String nullStr = "";
		try {
			Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
			while (iter.hasNext()){
				Entry<String, Object> e = iter.next();
				String value = e.getValue().toString();
				if(StringUtil.isEmpty(value)){
					nullStr = e.getKey();
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return nullStr;
	}
	
	/**
	 * 验证输入字段是否为空
	 * @param input
	 * @return
	 */
	public static String validateIsNull(List<String> list,InputParam input){
		String nullStr = "";
		try {
			for(String str : list){
				Object obj = input.getValue(str);
				if(StringUtil.isEmpty(obj)){
					nullStr = str;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return nullStr;
	}
	
	/**
	 * 验证输入字段是否为空
	 * @param input
	 * @return
	 */
	public static String validateIsNull(String[] list,InputParam input){
		String nullStr = "";
		try {
			for(String str : list){
				Object obj = input.getValue(str);
				if(StringUtil.isEmpty(obj)){
					nullStr += str+".";
//					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return nullStr;
	}
	
	
	/**
	 * 验证输入字段是否为空
	 * @param input
	 * @return
	 */
	public static String validateIsNull(InputParam input){
		
		String nullStr = "";
		
		try {
			for (Entry<String, Object> entry : input.getParams().entrySet()) {
				String value = String.format("%s", entry.getValue());
				if(StringUtils.isBlank(value) || "null".equals(value)){
					nullStr = entry.getKey();
					break;
				}
			}
	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return nullStr;
	}
	
	
	/**
	 * 验证输入字段是否为空
	 * @param input
	 * @return
	 */
	public static String validateIsNull(List<String> list,Map<String, Object> paramMap){
		
		String nullStr = "";
		
		try {
			for (String str : list) {
				Object obj = paramMap.get(str);
				if(StringUtil.isEmpty(obj)){
					nullStr = str;
					break;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return nullStr;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 正则表达式验证  验证数字或者含有两位小数
	 *
	 * @param rules
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static boolean patternMatchNum(String rules){
	    Pattern pattern = Pattern.compile(regExNum);
        Matcher matcher = pattern.matcher(rules);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
	}
	
	/**
	 * map集合转xml字符串
	 * @param map
	 * @return
	 */
	public static String mapToXml(Map<String, Object> map){
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("<xml>");
			
			Set<String> set = map.keySet();
			for(Iterator<String> it = set.iterator();it.hasNext();){
				String key = it.next();
				Object value = map.get(key);
				if(null == value){
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
	 * 转换错误码
	 * @param protocol
	 * @return
	 */
	public static  String getErrorMessage(String protocol,Map<String, Object> errorMap){
			
		if(StringConstans.Portocol.PROTOCOL_XML.equals(protocol)){
			
			return  Util.mapToXml(errorMap);
			
		}else if(StringConstans.Portocol.PROTOCOL_JSON.equals(protocol)){
			
			return JsonUtil.bean2Json(errorMap);
			
		}else{
			
			return String.format("%s%s%s", "Error:",errorMap.get("errorMessage"),"\r\n");
		}
		
	}
	
	/**
	 * 对象转xml
	 * @param obj
	 * @return
	 */
	public static String objToXml(Object obj){
		
		try {
			
			JAXBContext context = JAXBContext.newInstance(obj.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
			//marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			StringWriter writer = new StringWriter();
			marshaller.marshal(obj, writer);

			return writer.toString();
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
		
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T xmlToObj(String xml,Class<T> cls) throws Exception{
		
		T t = null;
	
		try {
			
			JAXBContext  context = JAXBContext.newInstance(cls);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			t = (T)unmarshaller.unmarshal(new StringReader(xml));
			
		} catch (JAXBException e) {
			logger.error(e.getMessage(),e);
			throw new UnmarshalException(e.getMessage());
		}
		
		return t;
	}
	
	
	public static void  mapCopy(Map<String, Object> destinationMap,Map<String, ? extends Object> sourceMap){
		for (Entry<String, ? extends Object> map : sourceMap.entrySet()) {
			destinationMap.put(map.getKey(), map.getValue());
		}
	}
	
	public static String xmlFormatter(String xml){
		
		String formatXml = "";
		
		try {
			
			org.dom4j.Document document = DocumentHelper.parseText(xml);
		    
			//创建字符串缓冲区 
	        StringWriter stringWriter = new StringWriter();  
	       
	        OutputFormat xmlFormat = new OutputFormat();  
	        //设置文件编码  
	        xmlFormat.setEncoding("UTF-8"); 
	        // 设置换行 
	        xmlFormat.setNewlines(true); 
	        // 生成缩进 
	        xmlFormat.setIndent(true); 
	        // 使用4个空格进行缩进, 可以兼容文本编辑器 
	        xmlFormat.setIndent("    "); 
	        
	        //创建写文件方法  
	        XMLWriter xmlWriter = new XMLWriter(stringWriter,xmlFormat);  
	        //写入文件  
	        xmlWriter.write(document);  
	        //关闭  
	        xmlWriter.close(); 
	        
	        formatXml = stringWriter.toString();
	        
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return formatXml;
	}
	


	public static String getLocalIP(){
		String IP = "127.0.0.1";
		try {
			InetAddress address = InetAddress.getLocalHost();
			IP=address.getHostAddress();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return IP;
	}
	
	public static Object getNotifyBeanNotNull(NotifyMessage notifyMessage){
		try {
			
			Method[] methods = notifyMessage.getClass().getMethods();  
			for(Method method : methods){  
			    if(method.isAnnotationPresent(NotifyBeanAnnotation.class)){  
			    	Object obj = method.invoke(notifyMessage);
			    	if(obj != null) return obj;
			    }  
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
		
		return null;  
	}
	
	
	/**
	 * 计算手续费
	 * @param feeRate
	 * @param orderAmount
	 * @return
	 * @throws Exception
	 */
	public static String getThirdPartyFee(String feeRate, String orderAmount) throws Exception {
		if (StringUtil.isEmpty(feeRate)) {
			throw new FrameException("手续费率为空");
		}
		if (StringUtil.isEmpty(orderAmount)) {
			throw new FrameException("订单金额为空");
		}
		// 手续费=费率乘以订单金额然后四舍五入
		BigDecimal fee = new BigDecimal(feeRate).multiply(new BigDecimal(orderAmount)).divide(new BigDecimal(100))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		// 12位数字
		String feeStr = StringUtil.amountTo12Str(ObjectUtils.toString(fee));
		return feeStr;
	}
	
	
	/**
	 * 获取入账金额——交易金额-交易手续费
	 * @param feeRate
	 * @param orderAmount
	 * @return
	 * @throws Exception
	 */
	public static String getActualTradeMoney(String feeRate, String orderAmount) throws FrameException {
		if (StringUtil.isEmpty(feeRate)) {
			throw new FrameException("手续费率为空");
		}
		if (StringUtil.isEmpty(orderAmount)) {
			throw new FrameException("订单金额为空");
		}
		// 手续费=费率乘以订单金额然后四舍五入
		BigDecimal fee = new BigDecimal(feeRate).multiply(new BigDecimal(orderAmount)).divide(new BigDecimal(100))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		//入账金额=交易金额-交易手续费
		BigDecimal actualMoney = (new BigDecimal(orderAmount).divide(new BigDecimal(100))).subtract(fee);
		
		return actualMoney.toString();
	}
	
	/**
	 * 过滤请求报文中的空字符串或者空字符串
	 * @param contentData
	 * @return
	 */
	public static Map<String, String> filterBlank(Map<String, String> contentData){
		LogUtil.writeLog("打印请求报文域 :");
		Map<String, String> submitFromData = new HashMap<String, String>();
		Set<String> keyset = contentData.keySet();
		
		for(String key:keyset){
			String value = contentData.get(key);
			if (StringUtils.isNotBlank(value)) {
				// 对value值进行去除前后空处理
				submitFromData.put(key, value.trim());
				LogUtil.writeLog(key + "-->" + String.valueOf(value));
			}
		}
		return submitFromData;
	}
	
	/**
	 * 将Map存储的对象，转换为key=value&key=value的字符(通知返回报文不做url编码)
	 *
	 * @param requestParam
	 * @param coder
	 * @return
	 */
	public static String getRequestParamString(Map<String, String> respParam) {

		StringBuffer sf = new StringBuffer("");
		String rspstr = "";
		if (null != respParam && 0 != respParam.size()) {
			for (Entry<String, String> en : respParam.entrySet()) {
				sf.append(en.getKey()
						+ "="
						+ (null == en.getValue() || "".equals(en.getValue()) ? "" : en.getValue()) + "&");
			}
			rspstr = sf.substring(0, sf.length() - 1);
		}
		return rspstr;
	}
	/**
	 * 线程睡眠
	 * @param time
	 */
	public static void sleep(long time) {
		
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
	}
	public static void main(String[] args) throws Exception {
		String data = "0123456789abcdef0123456789abcdef";
		String encoding = "UTF-8";
//		PublicKey pubKey = PublicCert.getPublicKey("E:\\home\\paymentCert\\payweb.cer");
//		String en = SecureUtil.EncryptData(data, encoding, pubKey);
//		System.out.println(en);
//		
//		String keyPath = "E:\\home\\paymentCert\\payweb.pfx";
//		String keyPassword = "111111";
//		PrivateKey priKey = CertUtils.getPrivateKey(keyPath, keyPassword);
//
//		
//		String de = SecureUtil.DecryptedData(en, encoding, priKey);
//		System.out.println(de);
//		byte[] bytes = SecureUtil.sha1(data, encoding);
//		System.out.println(new String(bytes));
		/*Map<String, Object> map = new HashMap<String, Object>();
		map.put("A", "a");
		map.put("B", "b");
		map.put("C", "c");
		System.out.println(mapToXml(map));*/
		String money = "000000000800";
		String rate = "0.002";
		
		String chargeFee = getActualTradeMoney(rate, money);
		
		System.out.println(chargeFee);
		
		/*System.out.println(chargeFee);
		BigDecimal dec = (new BigDecimal(money).divide(new BigDecimal(100))).subtract((new BigDecimal(chargeFee)).divide(new BigDecimal(100)));
		
		String fee = ObjectUtils.toString(dec);
		
		System.out.println("" + dec);*/
	
	}
	
	public static String mapToXML(Map map, StringBuffer sb) {
		Set set = map.keySet();
		for (Iterator it = set.iterator(); it.hasNext();) {
			String key = (String) it.next();
			Object value = map.get(key);
			if (null == value)
				value = "";
			if (value.getClass().getName().equals("java.util.ArrayList")) {
				ArrayList list = (ArrayList) map.get(key);
				sb.append("<" + key + ">");
				for (int i = 0; i < list.size(); i++) {
					HashMap hm = (HashMap) list.get(i);
					mapToXML(hm, sb);
				}
				sb.append("</" + key + ">");

			} else {
				if (value instanceof HashMap) {
					sb.append("<" + key + ">");
					mapToXML((HashMap) value, sb);
					sb.append("</" + key + ">");
				} else {
					sb.append("<" + key + ">" + value + "</" + key + ">");
				}

			}

		}
		return sb.toString();
	}
	
	/**
	 * 判断字符串是否是jsonarray
	 * @param content
	 * @return
	 */
	public static boolean isJsonArray(String content) {
	    if(StringUtils.isBlank(content))
	        return false;
	    try {
	        JSONArray jsonStr = JSONArray.fromObject(content);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
	
	/**
	* 判断字符串是否可以转化为json对象
	* @param content
	* @return
	*/
	public static boolean isJsonObject(String content) {
	    // 此处应该注意，不要使用StringUtils.isEmpty(),因为当content为"  "空格字符串时，JSONObject.parseObject可以解析成功，
	    // 实际上，这是没有什么意义的。所以content应该是非空白字符串且不为空，判断是否是JSON数组也是相同的情况。
	    if(StringUtils.isBlank(content))
	        return false;
	    try {
	        JSONObject jsonStr = JSONObject.fromObject(content);
	        return true;
	    } catch (Exception e) {
	        return false;
	    }
	}
}

