package com.huateng.pay.services.statics.imple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.services.statics.AbsStaticQRCodeService;
import com.huateng.pay.services.statics.IStaticQRCodeService;
import com.huateng.pay.services.statics.handler.HandlerMobileStaticQRCodeRequest;
import com.huateng.pay.services.statics.handler.HandlerPospStaticQRCodeRequest;
import com.huateng.utils.QRCodeTypeEnum;
import com.huateng.utils.Util;

/**
 * @author Administrator
 * 静态二维码接口实现类
 */
public class StaticQRCodeServiceImpl extends AbsStaticQRCodeService implements IStaticQRCodeService{
	private Logger logger = LoggerFactory.getLogger(StaticQRCodeServiceImpl.class); 
	private HandlerMobileStaticQRCodeRequest   handlerMobile;
	private HandlerPospStaticQRCodeRequest   handlerPosp;

	/**
	 * 生成静态二维码
	 * @param inputParam
	 */
	@Override
	public OutputParam createStaticQRCode(InputParam inputParam)throws FrameException {
		logger.info("[生成静态二维码] 方法开始  请求信息" + inputParam.toString());
		
		OutputParam  outputParam = new OutputParam();
		
		try {
			
			//支付接入类型
			String payAccessType = String.format("%s", inputParam.getValue("payAccessType"));
			
			//渠道编号
			String channel = String.format("%s", inputParam.getValue("channel"));
			
			//二维码类型
			String qrType = String.format("%s", inputParam.getValue("localQrType"));
			
			//金额
			String orderAmount = String.format("%s", inputParam.getValue("orderAmount"));
			
			//币种
			String currencyType = String.format("%s", inputParam.getValue("currencyType"));
			
			//账号
			String acctNo = String.format("%s", inputParam.getValue("acctNo"));
			
			//设备号
			String deviceNumber = String.format("%s", inputParam.getValue("deviceNumber"));
		
			//按原样返回
			if(!StringUtil.isEmpty(acctNo)){
				outputParam.putValue("acctNo",acctNo);
			}
			
			if(!StringUtil.isEmpty(qrType)){
				outputParam.putValue("localQrType",qrType);
			}
			
			if(!StringUtil.isEmpty(payAccessType)){
				outputParam.putValue("payAccessType",payAccessType);
			}
			
			if(!StringUtil.isEmpty(orderAmount)){
				outputParam.putValue("orderAmount",orderAmount);
			}
			
			if(!StringUtil.isEmpty(currencyType)){
				outputParam.putValue("currencyType",currencyType);
			}
			
			if(!StringUtil.isEmpty(deviceNumber)){
				outputParam.putValue("deviceNumber",deviceNumber);
			}
			
			//需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("acctNo");
			validateParamList.add("localQrType");
			
			//校验字段
			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[获取静态二维码] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
			
			logger.debug("[获取静态二维码] 金额校验 orderAmount=" + orderAmount);
			if (!StringUtil.isEmpty(orderAmount)) {
				//校验金额格式
				if(!orderAmount.matches("\\d{12}") ){
					logger.debug("[获取静态二维码] 金额格式不正确");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "金额格式不正确"+orderAmount);
					return outputParam;
				}
				//校验金额大小
				BigDecimal  orderAmt = new BigDecimal(orderAmount);
				if(orderAmt.compareTo(new BigDecimal("0")) <= 0){
					logger.debug("[获取静态二维码] 金额不正确");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", " [获取静态二维码] 金额不正确"+orderAmt);
					return outputParam;
				}
			}
			
			//校验支付接入类型
			logger.debug("[获取静态二维码] 接入类型校验 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType)) {
				logger.debug("[获取静态二维码] 接入类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "接入类型不正确"+payAccessType);
				return outputParam;
			}
			
			//判断静态二维码类型
			logger.debug("[获取静态二维码] 二维码前缀类型校验   qrType=" + qrType);
			if (!QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(qrType) && !QRCodeTypeEnum.STATIC_QR_CODE_OTHER.getType().equals(qrType)) {
				logger.debug("[获取静态二维码] 二维码前缀类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前缀类型不正确"+qrType);
				return outputParam;
			}
			
			//判断渠道类型
			logger.debug("[获取静态二维码] 渠道类型校验   channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_BANK.equals(channel) && !StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)
					&& !StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logger.debug("[获取静态二维码] 渠道类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道类型不正确"+channel);
				return outputParam;
			}
			 
			//判断  手机银行 / 丰收互联  获取静态二维码是否为丽水溯源 
			if (StringConstans.CHANNEL.CHANNEL_BANK.equals(channel) || StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				if (!QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(qrType)) {
					logger.debug("[获取静态二维码] 获取静态二维码类型不正确 {channel:" + channel + ",qrType:" + qrType + "}");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "获取静态二维码类型不正确 {channel:" + channel + ",qrType:" + qrType + "}");
					return outputParam;	
				}
			} 
			
			//手机银行和丰收互联 生成静态二维码
			if (StringConstans.CHANNEL.CHANNEL_BANK.equals(channel) || StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {

				OutputParam staticOutput = handlerMobile.createStaicQRCode(inputParam);
				Util.mapCopy(outputParam.getReturnObj(), staticOutput.getReturnObj());

			//POSP 生成静态二维码
			} else if (StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {

				OutputParam staticOutput = handlerPosp.createStaicQRCode(inputParam);
				Util.mapCopy(outputParam.getReturnObj(), staticOutput.getReturnObj());
			}
			
		} catch (Exception e) {
			logger.error("[创建丽水溯源静态二维码] 创建丽水溯源静态二维码异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("创建丽水溯源静态二维码异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常,创建丽水溯源静态二维码异常");
		} finally {
			logger.info("[生成静态二维码] 方法结束 返回信息" + outputParam.toString());
		}
		return outputParam;
	}

	public HandlerMobileStaticQRCodeRequest getHandlerMobile() {
		return handlerMobile;
	}

	public void setHandlerMobile(HandlerMobileStaticQRCodeRequest handlerMobile) {
		this.handlerMobile = handlerMobile;
	}

	public HandlerPospStaticQRCodeRequest getHandlerPosp() {
		return handlerPosp;
	}

	public void setHandlerPosp(HandlerPospStaticQRCodeRequest handlerPosp) {
		this.handlerPosp = handlerPosp;
	}
}
