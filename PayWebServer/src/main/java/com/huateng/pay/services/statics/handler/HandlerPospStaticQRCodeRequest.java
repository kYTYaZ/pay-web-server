package com.huateng.pay.services.statics.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.utils.QRCodeTypeEnum;
import com.huateng.utils.QRCodeUtil;

/**
 * @author Administrator
 * 处理POSP静态二维码请求
 */
public class HandlerPospStaticQRCodeRequest extends AbsHandlerStaticQRCodeRequest{
	
	private Logger logger = LoggerFactory.getLogger(HandlerPospStaticQRCodeRequest.class); 
	
	/**
	 * posp创建丽水溯源的静态二维码
	 * @param inputParam
	 * @return
	 */
	public   OutputParam createStaicQRCode(InputParam inputParam){
		
		OutputParam  outputParam = new OutputParam();
		
		//二维码类型
		String qrType = String.format("%s", inputParam.getValue("localQrType"));
		
		if (QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(qrType)) {
			
			outputParam = this.toCreateLSSYStaticQRCode(inputParam);
			
		}else if(QRCodeTypeEnum.STATIC_QR_CODE_OTHER.getType().equals(qrType)){
					
			outputParam = this.toCreateOtherStaticQRCode(inputParam);
		}
			
		return outputParam;
	}
	
	
	/**
	 * 创建丽水溯源静态二维码
	 * @param inputParam
	 * @return
	 */
	public OutputParam toCreateLSSYStaticQRCode(InputParam inputParam){
		
		logger.info("[POSP创建丽水溯源静态二维码]  创建丽水溯源静态二维码  方法开始" + inputParam.toString());
		OutputParam  outputParam = new OutputParam();
		
		try {
			
			
			
			OutputParam output = this.createLSSYStaicQRCode(inputParam);
			
			//logger.info("-------[POSP创建丽水溯源静态二维码]  创建丽水溯源静态二维码   完成---------");
		
			if (!StringConstans.returnCode.SUCCESS.equals(output.getReturnCode())) {
				logger.debug("[POSP创建丽水溯源静态二维码]  创建丽水溯源静态二维码失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("创建丽水溯源静态二维码失败");
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前置异常");
				return outputParam;
			}
			
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnObj(output.getReturnObj());
			
		} catch (Exception e) {
			logger.error("[POSP创建丽水溯源静态二维码] 创建丽水溯源静态二维码失败" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("创建丽水溯源静态二维码失败");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常");
		} finally {
			logger.info("[POSP创建丽水溯源静态二维码] 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	} 
	
	
	/**
	 * 创建其他静态二维码
	 * @param inputParam
	 * @return
	 */
	public  OutputParam toCreateOtherStaticQRCode(InputParam inputParam){
		logger.info("[创建其他静态二维码] 方法开始 请求信息" + inputParam.toString());
		
		OutputParam outputParam = new OutputParam();
		
		try {
			
			//账号
			String acctNo = String.format("%s", inputParam.getValue("acctNo"));
			
			//静态二位码类型
			String localQrType = String.format("%s", inputParam.getValue("localQrType"));
			
			//渠道
			String channel = String.format("%s", inputParam.getValue("channel"));
			
			//金额
			String orderAmount = String.format("%s", inputParam.getValue("orderAmount"));
			
			//币种
			String currencyType = String.format("%s", inputParam.getValue("currencyType"));
			
			//商户号
			String merId = String.format("%s", inputParam.getValue("merId"));
			
			//商户名称
			String merName = String.format("%s", inputParam.getValue("merName"));
			
			//姓名
			String customName = String.format("%s", inputParam.getValue("customName"));
			
			//客户号
			String customerNo = String.format("%s", inputParam.getValue("customerNo"));
			
			//logger.info("[创建静态二维码] 开始获取静态二维码的序列号");
			
			//获取二维码序列号
			String staticQRSeqNo = staticQRCodeDataService.getStaticQRSeqNo();
			
			//logger.info("[创建静态二维码] 获取静态二维码的序列号完成,staticQRSeqNo:[" + staticQRSeqNo +"]");
			
			InputParam  qrInput  = new InputParam();
			qrInput.putParams("txnServiceCode", inputParam.getValue("serviceCode"));
			qrInput.putParams("qrSeqNo",staticQRSeqNo);
			qrInput.putParams("localQrType",localQrType);
			
			//logger.info("[创建其他静态二维码] 生成静态二维码串码  开始---------");
			
			OutputParam qrOutputParam = QRCodeUtil.encryptQRCodeCode(qrInput);
			
			//logger.info("-------[创建静态二维码] 生成静态二维码串码  完成---------");
			
			if(!StringConstans.returnCode.SUCCESS.equals(qrOutputParam.getReturnCode())){
				logger.debug("[创建静态二维码] 生成静态二维码失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("创建静态二维码失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "创建静态二维码失败");
				return outputParam;
			}
			
			//logger.info("-------[创建静态二维码] 生成二维码串码  成功---------");

			//二维码明文
			String qrCodeProClaimed = String.format("%s", qrOutputParam.getValue("qrCodeProClaimed"));
			//二维码密文
			String qrCodecipherText = String.format("%s", qrOutputParam.getValue("qrCodecipherText"));
			//二维码状态
			String qrCodeStatus = String.format("%s", qrOutputParam.getValue("qrCodeStatus"));
			//二维码有效期
			String qrCodeExpire = String.format("%s", qrOutputParam.getValue("qrCodeExpire"));	
			//生成时间
			String generatDt = String.format("%s", qrOutputParam.getValue("generatDt"));	
			
			InputParam  queryParam = new InputParam();
			queryParam.putparamString("acctNo", acctNo);
			queryParam.putparamString("channel",channel);
			queryParam.putparamString("ewmType", localQrType);
			queryParam.putparamString("ewmStatue",	StringConstans.QRCodeStatus.ENABLE);
			
			logger.info("[创建其他静态二维码]  查询静态二维码 是否存在   请求信息" + queryParam.toString());
			
			OutputParam queryOutput = staticQRCodeDataService.queryStaticQRCodeInfo(queryParam);
			
			//logger.info("[创建静态二维码]  查询静态二维码 是否存在    完成");
			
			if(StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())){
				
				logger.debug("[创建静态二维码]  查询静态二维码已经存在 ");

				//静态二维码流水号
				String ewmSeq = String.format("%s", queryOutput.getReturnObj().get("ewmSeq"));
				
 				InputParam  updateInput =  new InputParam(); 
				updateInput.putparamString("ewmSeq", ewmSeq);
				updateInput.putparamString("updateDt", DateUtil.getCurrentDateTime());
				updateInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.DISABLE);
				
				logger.info("[创建其他静态二维码]  静态二维码存在，更新已存在静态二维码失效      请求信息" + updateInput.toString());
				
				OutputParam updateOutput = staticQRCodeDataService.updateStaticQRCodeStatus(updateInput);
				
				//logger.info("[创建静态二维码]  静态二维码存在，更新已存在静态二维码失效             完成");
				
				if(!StringConstans.returnCode.SUCCESS.equals(updateOutput.getReturnCode())){	
					logger.debug("[创建静态二维码]  更新静态二维码失效失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg("更新静态二维码失效失败");
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "二维码前置异常,更新静态二维码失效失败");
					return outputParam;
				}			
			}
					
			/*************************增加静态二维码流程************************************/
			
			//logger.info("[创建静态二维码] 开始获取二维码的流水号");
			
			//获取二维码流水
			String ewmSeq = staticQRCodeDataService.getStaticQRCodeTxnNo();
			
			logger.debug("[创建静态二维码] 获取二维码的流水号完成,qrSeqNo:[" + ewmSeq +"]");
			
			InputParam  saveInput =  new InputParam(); 
			saveInput.putparamString("ewmSeq", ewmSeq);
			saveInput.putparamString("ewmData", qrCodeProClaimed);
			saveInput.putparamString("ewmCipherText", qrCodecipherText);
			saveInput.putparamString("acctNo", acctNo);
			saveInput.putparamString("ewmType", localQrType);
			saveInput.putparamString("ewmStatue", qrCodeStatus);
			saveInput.putparamString("validateDay", qrCodeExpire);
			saveInput.putparamString("createDt", generatDt);
			saveInput.putparamString("channel", channel);
			
			
			//判断金额不为空
			if(!StringUtil.isEmpty(orderAmount)){		
				if(StringUtil.isEmpty(currencyType)){	
					currencyType = StringConstans.SETTLE_CURRENTY_TYPE;
				}
				saveInput.putparamString("goodsAmount", orderAmount);
				saveInput.putparamString("currencyType", currencyType);
			}
			
			//判断客户姓名
			if(!StringUtil.isEmpty(customName)){
				saveInput.putparamString("contactName", customName);
			}
			
			//判断商户号
			if(!StringUtil.isEmpty(merId)){
				saveInput.putparamString("merId", merId);
			}
			
			//判断商户名称
			if(!StringUtil.isEmpty(merName)){
				saveInput.putparamString("merName", merName);
			}
		
			//客户号
			if(!StringUtil.isEmpty(customerNo)){
				saveInput.putparamString("customerNo", customerNo);
			}
			
			logger.info("[创建其他静态二维码] 增加静态二维码 请求信息" + saveInput.toString());
			
			OutputParam  saveOutput = staticQRCodeDataService.saveStaticQRCodeInfo(saveInput);
			
			//logger.info("[创建静态二维码]  完成增        加静态二维码");
			
			if(!StringConstans.returnCode.SUCCESS.equals(saveOutput.getReturnCode())){
				logger.debug("[创建静态二维码]  增加静态二维码失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("增加静态二维码失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前置异常,增加静态二维码失败");
				return outputParam;
			}
			
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("增加静态二维码成功");	
			outputParam.putValue("codeUrl", qrCodecipherText);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "增加静态二维码成功");
			
		} catch (FrameException e) {
			logger.error("[创建静态二维码] 创建静态二维码异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("创建静态二维码异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常,创建静态二维码异常");
		} finally {
			logger.info("创建其他静态二维码 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}
}
