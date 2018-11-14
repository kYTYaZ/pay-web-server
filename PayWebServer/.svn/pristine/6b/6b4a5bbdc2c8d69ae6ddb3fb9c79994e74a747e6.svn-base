package com.huateng.pay.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.services.weixin.WxPayService;
/**
 * 微信支付Action
 * @author guohuan
 *
 */
public class WxPayAction extends BaseAction {
	private static final long serialVersionUID = -3200289333344802927L;
	private Logger logger = LoggerFactory.getLogger(WxPayAction.class);
	private WxPayService wxPayService;
	
	/**
	 * 微信支付后台接收通知
	 * @return
	 */
	public String recvWxNotifyReq() {
		logger.debug("微信支付接收后台通知报文[START]");
		InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
		try {
			// 将返回的输入流转换成字符串
			StringBuffer buffer = new StringBuffer();
	        
			try {
				inputStream = request.getInputStream();
		        inputStreamReader = new InputStreamReader(inputStream, "utf-8");
		        bufferedReader = new BufferedReader(inputStreamReader);
		
		        String str = null;
		        while ((str = bufferedReader.readLine()) != null) {
		            buffer.append(str);
		        }
		        bufferedReader.close();
		        inputStreamReader.close();
		        
		        
		        String returnCode = "SUCCESS";
			    String returnMsg = "OK";
			    Map<String,String> retMap = new HashMap<String,String>();
			    retMap.put("return_code", returnCode);
			    retMap.put("return_msg", returnMsg);
			    String postDataXML = map2XmlString(retMap);
		        
				logger.info("返回微信报文：" + postDataXML);
				response.setHeader("Connection", "close");
				send(postDataXML);
			} catch (Exception e) {
				logger.error("[微信后台通知] 返回响应报文异常："+e.getMessage(),e);
			}finally {
				//关闭流
				try {
					if (bufferedReader != null) {
						bufferedReader.close();
					}
					if (inputStreamReader != null) {
						inputStreamReader.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (IOException e) {
					logger.error("[微信后台通知] ：关闭流出现异常："+e.getMessage(),e);
				}
			}
			
	        logger.info("接收微信后台通知的报文参数："+buffer.toString());
	        
			//进行微信后台通知处理
			InputParam input = new InputParam();
			input.putParams("respStr", buffer.toString());
			logger.debug("进行微信后台通知处理,请求报文:"+input.toString());
			OutputParam out = wxPayService.recvWxNotifyReq(input);
			logger.debug("进行微信后台通知处理,返回报文:"+out.toString());
			
		} catch (Exception e) {
			logger.error("接收微信支付后台通知异常："+e.getMessage(),e);
		}  finally {
			logger.debug("微信支付接收后台通知报文[END]");
		}
		return null;
	}

	
	 public static String map2XmlString(Map<String, String> map) {
	        String xmlResult = "";

	        StringBuffer sb = new StringBuffer();
	        sb.append("<xml>");
	        for (String key : map.keySet()) {
	            System.out.println(key + "========" + map.get(key));

	            String value = "<![CDATA[" + map.get(key) + "]]>";
	            sb.append("<" + key + ">" + value + "</" + key + ">");
	            System.out.println();
	        }
	        sb.append("</xml>");
	        xmlResult = sb.toString();

	        return xmlResult;
	    }

	public WxPayService getWxPayService() {
		return wxPayService;
	}

	public void setWxPayService(WxPayService wxPayService) {
		this.wxPayService = wxPayService;
	}

}
