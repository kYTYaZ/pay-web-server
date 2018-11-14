package com.huateng.pay.action;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.services.weixin.WxPayService;
/**
 * 微信支付Action
 * @author guohuan
 *
 */
@Controller
@RequestMapping(value="/pay/wx/")
public class WxPayAction2 /*extends BaseAction*/ {
//	private static final long serialVersionUID = -3200289333344802927L;
	private Logger logger = LoggerFactory.getLogger(WxPayAction2.class);
	@Autowired
	private WxPayService wxPayService;
	
	/**
	 * 微信支付后台接收通知
	 * @return
	 */
	@RequestMapping("recvWxNotifyReq.mvc")
	public String recvWxNotifyReq(HttpServletRequest request,HttpServletResponse response) {
		logger.info("微信支付接收后台通知报文[START]...");
		InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
		try {
			// 将返回的输入流转换成字符串
			StringBuffer buffer = new StringBuffer();
	        inputStream = request.getInputStream();
	        inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	        bufferedReader = new BufferedReader(inputStreamReader);
	
	        String str = null;
	        while ((str = bufferedReader.readLine()) != null) {
	            buffer.append(str);
	        }
	        bufferedReader.close();
	        inputStreamReader.close();
	        logger.info("接收微信后台通知的报文参数："+buffer.toString());
	        
			//进行微信后台通知处理
			InputParam input = new InputParam();
			input.putParams("respStr", buffer.toString());
			
			OutputParam out = wxPayService.recvWxNotifyReq(input);
			String respPostData = "";
			if(!StringConstans.returnCode.SUCCESS.equals(out.getReturnCode())){
				logger.error("微信后台处理失败");
			}else{
				respPostData = String.format("%s", out.getValue("respData"));
			}
			
			//TODO 返回结果给微信
			logger.info("微信支付接收后台通知报文[END]...");
			OutputStream outputStream = response.getOutputStream();
			// 注意编码格式，防止中文乱码
			outputStream.write(respPostData.getBytes("UTF-8"));
			outputStream.flush();
			outputStream.close();
//			send(respPostData);
		} catch (Exception e) {
			logger.error("接收微信支付后台通知异常："+e.getMessage(),e);
		} finally {
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
				logger.error("接收微信支付后台通知 ：关闭流出现异常："+e.getMessage(),e);
			}
		}
		return null;
	}
	
	/**
	 * 微信支付后台接收通知
	 * @return
	 */
	@RequestMapping("recvWxNotifyReqYL.mvc")
	public String recvWxNotifyReqYL(HttpServletRequest request,HttpServletResponse response) {
		logger.info("微信支付接收后台通知报文[START]...");
		InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
		try {
			// 将返回的输入流转换成字符串
			StringBuffer buffer = new StringBuffer();
	        inputStream = request.getInputStream();
	        inputStreamReader = new InputStreamReader(inputStream, "utf-8");
	        bufferedReader = new BufferedReader(inputStreamReader);
	
	        String str = null;
	        while ((str = bufferedReader.readLine()) != null) {
	            buffer.append(str);
	        }
	        bufferedReader.close();
	        inputStreamReader.close();
	        logger.info("接收微信后台通知的报文参数："+buffer.toString());
	        
			//进行微信后台通知处理
			InputParam input = new InputParam();
			input.putParams("respStr", buffer.toString());
			
			OutputParam out = wxPayService.recvWxNotifyReqYL(input);
			String respPostData = "";
			if(!StringConstans.returnCode.SUCCESS.equals(out.getReturnCode())){
				logger.error("微信后台处理失败");
			}else{
				respPostData = String.format("%s", out.getValue("respData"));
			}
			
			//TODO 返回结果给微信
			logger.info("微信支付接收后台通知报文[END]...");
			OutputStream outputStream = response.getOutputStream();
			// 注意编码格式，防止中文乱码
			outputStream.write(respPostData.getBytes("UTF-8"));
			outputStream.flush();
			outputStream.close();
//			send(respPostData);
		} catch (Exception e) {
			logger.error("接收微信支付后台通知异常："+e.getMessage(),e);
		} finally {
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
				logger.error("接收微信支付后台通知 ：关闭流出现异常："+e.getMessage(),e);
			}
		}
		return null;
	}

	public WxPayService getWxPayService() {
		return wxPayService;
	}

	public void setWxPayService(WxPayService wxPayService) {
		this.wxPayService = wxPayService;
	}

}
