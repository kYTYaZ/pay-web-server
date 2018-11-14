package com.huateng.pay.services.statics.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.QRCodeTypeEnum;

/**
 * @author Administrator
 * 处理手机银行 / 丰收互联的静态二维码请求
 */
public class HandlerMobileStaticQRCodeRequest extends AbsHandlerStaticQRCodeRequest{
	
	private Logger logger = LoggerFactory.getLogger(HandlerMobileStaticQRCodeRequest.class); 
	/**
	 * 手机银行  / 丰收互联 创建丽水溯源的静态二维码
	 * @return
	 */
	public  OutputParam createStaicQRCode(InputParam inputParam){
		
		logger.info("[手机银行  / 丰收互联 创建丽水溯源的静态二维码] 方法开始 请求报文" + inputParam.toString());
		OutputParam  outputParam = new OutputParam();
		
		String channelMessage = "手机银行";
		try {
			
			String channel = String.format("%s", inputParam.getValue("channel"));
			if(StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)){
				channelMessage = "丰收互联";
			}
			
			//静态二维码类型
			String qrType = String.format("%s", inputParam.getValue("localQrType"));
			
			//设备号
			String deviceNumber  = String.format("%s", inputParam.getValue("deviceNumber"));
			
			if(QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(qrType)){
				
				if (StringUtil.isEmpty(deviceNumber)) {
					logger.debug(channelMessage+"[创建丽水溯源静态二维码] 请求报文字段 deviceNumber不能为空");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "请求报文字段 deviceNumber不能为空");
					return outputParam;
				}
				
				//logger.info("-------"+channelMessage+"[创建丽水溯源静态二维码]  创建丽水溯源静态二维码   开始---------");
				
				OutputParam output = this.createLSSYStaicQRCode(inputParam);
				
				//logger.info("-------"+channelMessage+"[创建丽水溯源静态二维码]  创建丽水溯源静态二维码   结束---------");
				
				if(!StringConstans.returnCode.SUCCESS.equals(output.getReturnCode())){	
					logger.debug(channelMessage+"[创建丽水溯源静态二维码]  创建丽水溯源静态二维码失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg("创建丽水溯源静态二维码失败");
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "创建丽水溯源静态二维码失败");
					return outputParam;
				}		
				
				outputParam.setReturnObj(output.getReturnObj());
			}
		} catch (Exception e) {
			logger.error(channelMessage+"[创建丽水溯源静态二维码] 创建丽水溯源静态二维码失败" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("创建丽水溯源静态二维码失败");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常");
		} finally {
			logger.info("[创建丽水溯源静态二维码] 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}
}
