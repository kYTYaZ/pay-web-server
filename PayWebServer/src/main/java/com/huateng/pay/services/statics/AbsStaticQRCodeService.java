package com.huateng.pay.services.statics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.po.local.QRCodeInfo;
import com.huateng.pay.services.db.IStaticQRCodeDataService;
import com.huateng.utils.QRCodeTypeEnum;
import com.huateng.utils.QRCodeUtil;
import com.huateng.utils.QRUtil;
import com.huateng.utils.Util;

/**
 * @author Administrator
 * 静态二维码抽象类处理共用方法
 */
public abstract class AbsStaticQRCodeService implements IStaticQRCodeService{
	private Logger logger = LoggerFactory.getLogger(AbsStaticQRCodeService.class);
	protected IStaticQRCodeDataService  staticQRCodeDataService;
	
	@Override
	public abstract OutputParam createStaticQRCode(InputParam inputParam);
		

	@Override
	public OutputParam deleteStaticQRCode(InputParam inputParam) throws FrameException{
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 查询静态二维码
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam queryStaticQRCode(InputParam inputParam) throws FrameException{
		
		logger.info("【查询静态二维码流程 】  queryStaticQRCode 方法开始 请求信息" + inputParam.toString());
		OutputParam outputParam = new OutputParam();
		
		
		
		try {
			
			//账号
			String acctNo = String.format("%s", inputParam.getValue("acctNo"));
			
			//静态二位码类型
			String localQrType = String.format("%s", inputParam.getValue("localQrType"));
			
			//渠道
			String channel = String.format("%s", inputParam.getValue("channel"));
			
			//原样返回
			if(!StringUtil.isEmpty(acctNo)){
				outputParam.putValue("acctNo",acctNo);
			}
			
			if(!StringUtil.isEmpty(localQrType)){
				outputParam.putValue("localQrType", localQrType);
			}
			
			//需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("acctNo");
			validateParamList.add("localQrType");
			
			//校验字段
			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[查询静态二维码] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
			
			//判断静态二维码类型
			logger.debug("[查询静态二维码]  二维码类型校验   localQrType=" + localQrType);
			if (!QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(localQrType) && !QRCodeTypeEnum.STATIC_QR_CODE_OTHER.getType().equals(localQrType)){
				logger.debug("[查询静态二维码] 二维码类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码类型不正确");
				return outputParam;
			}
			
			//判断渠道类型
			logger.debug("[查询静态二维码] 渠道类型校验   channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_BANK.equals(channel) && !StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[查询静态二维码] 渠道类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道类型不正确");
				return outputParam;
			}
			
			InputParam  queryParam = new InputParam();
			queryParam.putparamString("acctNo", acctNo);
			queryParam.putparamString("channel", channel);
			queryParam.putparamString("ewmType", localQrType);
			queryParam.putparamString("ewmStatue",StringConstans.QRCodeStatus.ENABLE);
			
			logger.debug("[查询静态二维码]  查询静态二维码   请求信息" + queryParam.toString());
			
			OutputParam queryOutput = staticQRCodeDataService.queryStaticQRCodeInfo(queryParam);
			
			//logger.info("[查询静态二维码]  查询静态二维码    完成");
			
			if (!StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())) {
				logger.debug("[查询静态二维码]  查询静态二维码失败,没有找到合适的二维码");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("查询静态二维码失败,没有找到合适的二维码");
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "查询静态二维码失败,没有找到合适的二维码");
				return outputParam;
			}
			
			Map<String, Object> map = queryOutput.getReturnObj();
			
			//二维码密文信息
			String ewmData = String.format("%s", map.get("ewmCiphertext"));
		
			outputParam.putValue("codeUrl", ewmData);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("查询静态二维码成功");
			outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "查询静态二维码成功");
			
			//logger.info("------------  查询静态二维码流程    END---------------");
		} catch (Exception e) {
			logger.error("[查询静态二维码] 查询静态二维码异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("查询静态二维码异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "[查询静态二维码] 查询静态二维码异常" + e.getMessage());
		} finally {
			logger.info("[查询静态二维码] queryStaticQRCode  方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}

	/**
	 * 停用静态二维码
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam stopStaticQRCode(InputParam inputParam) throws FrameException{
		
		OutputParam outputParam = new OutputParam();
		
		logger.info("【停用静态二维码流程】stopStaticQRCode    方法开始 请求信息" + inputParam.toString());
		
		try {
			
			//账号
			String acctNo = String.format("%s", inputParam.getValue("acctNo"));
			
			//静态二位码类型
			String localQrType = String.format("%s", inputParam.getValue("localQrType"));
			
			//渠道
			String channel = String.format("%s", inputParam.getValue("channel"));
			
			//原样返回
			if(!StringUtil.isEmpty(acctNo)){
				outputParam.putValue("acctNo",acctNo);
			}
			
			if(!StringUtil.isEmpty(localQrType)){
				outputParam.putValue("localQrType", localQrType);
			}
				
			//需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("acctNo");
			validateParamList.add("localQrType");
			
			//校验字段
			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[停用静态二维码] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
			
			
			//判断静态二维码类型
			logger.debug("[停用静态二维码]  二维码类型校验   localQrType=" + localQrType);
			if (!QRCodeTypeEnum.STATIC_QR_CODE_LSSY.getType().equals(localQrType) && !QRCodeTypeEnum.STATIC_QR_CODE_OTHER.getType().equals(localQrType)) {
				logger.debug("[停用静态二维码] 二维码类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码类型不正确"+localQrType);
				return outputParam;
			}
			
			//判断渠道类型
			logger.debug("[停用静态二维码] 渠道类型校验   channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_BANK.equals(channel) && !StringConstans.CHANNEL.CHANNEL_POSP.equals(channel) 
					&& !StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logger.debug("[停用静态二维码] 渠道类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道类型不正确"+channel);
				return outputParam;
			}
			
			InputParam  queryParam = new InputParam();
			queryParam.putparamString("acctNo", acctNo);
			queryParam.putparamString("channel", channel);
			queryParam.putparamString("ewmType", localQrType);
			queryParam.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);
			
			logger.debug("[停用静态二维码]  查询静态二维码是否存在    开始");
			
			OutputParam queryOutput = staticQRCodeDataService.queryStaticQRCodeInfo(queryParam);
			
			//logger.info("[停用静态二维码]  查询静态二维码是否存在    结束");
			
			if (!StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())) {
				logger.debug("[停用静态二维码]  停用静态二维码失败,没有找到静态二维码");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("停用静态二维码失败");
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "停用静态二维码失败,没有找到静态二维码" );
				return outputParam;
			}
			
			//logger.info("[停用静态二维码]  查询静态二维码存在  ");
			
			//二维码序列号
			String  ewmSeq = String.format("%s", queryOutput.getReturnObj().get("ewmSeq"));
				
			InputParam updateInput = new InputParam();
			updateInput.putparamString("ewmSeq", ewmSeq);
			updateInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.DISABLE);
			updateInput.putparamString("updateDt", DateUtil.getCurrentDateTime());
			
			logger.debug("[停用静态二维码]  停用静态二维码    请求信息" + updateInput.toString());
			
			OutputParam updateOutput = staticQRCodeDataService.updateStaticQRCodeStatus(updateInput);
			
			//logger.info("[停用静态二维码]  停用静态二维码    完成");
			
			if (!StringConstans.returnCode.SUCCESS.equals(updateOutput.getReturnCode())) {
				logger.debug("[停用静态二维码]  停用静态二维码失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("停用静态二维码失败");
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前置异常,停用静态二维码失败");
				return outputParam;
			}
			
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("停用静态二维码成功");
			outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "停用静态二维码成功");
			
			//logger.info("------------  停用静态二维码流程    END-----------------");
			
		} catch (Exception e) {
			logger.error("[停用静态二维码] 停用静态二维码异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("停用静态二维码异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常,停用静态二维码异常");
		} finally {
			logger.info("[停用静态二维码] stopStaticQRCode 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}

	/**
	 * 解析静态二维码
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam parseStaticQRCode(InputParam inputParam) throws FrameException {
		
		OutputParam outputParam = new OutputParam();
		
		logger.info("【解析静态二维码流程】 parseStaticQRCode 方法开始 请求信息" + inputParam.toString());
		
		try {
			
			//二维码密文
			String qrCodecipherText = String.format("%s", inputParam.getValue("codeUrl"));
		
			//外部订单号
			String orderNumber = String.format("%s", inputParam.getValue("orderNumber"));
			
			//外部订单时间
			String orderTime = String.format("%s", inputParam.getValue("orderTime"));
			
			//原样返回
			if(!StringUtil.isEmpty(orderNumber)){
				outputParam.putValue("orderNumber",orderNumber);
			}
			
			if(!StringUtil.isEmpty(orderTime)){
				outputParam.putValue("orderTime", orderTime);
			}
			
			//需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("codeUrl");
			validateParamList.add("orderNumber");
			validateParamList.add("orderTime");
			
			//校验字段
			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[解析静态二维码] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
		
			//判断渠道类型
			String channel = String.format("%s", inputParam.getValue("channel"));
			
			//校验支付接入类型
			String payAccessType = String.format("%s", inputParam.getValue("payAccessType"));
			
			logger.debug("[解析静态二维码] 支付接入类型校验   payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType)) {
				logger.debug("[解析静态二维码] 支付接入类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "接入类型不正确"+payAccessType);
				return outputParam;
			}
			
			logger.debug("[解析静态二维码] 渠道类型校验   channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_BANK.equals(channel) 
					&& !StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)
					&& !StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logger.debug("[解析静态二维码] 渠道类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道类型不正确"+channel);
				return outputParam;
			}
			
			
			//logger.info("--------[解析静态二维码] 开始解析静态二维码-----------");
			
			OutputParam  qrOutput = QRCodeUtil.decryptQRCodeCode(qrCodecipherText);
			
			//logger.info("--------[解析静态二维码] 完成解析静态二维码-----------");
			
			if(!StringConstans.returnCode.SUCCESS.equals(qrOutput.getReturnCode())){
				logger.debug("[解析静态二维码] 解析二维码密文失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc",qrOutput.getReturnMsg());
				return outputParam;
			}
			
			//二维码明文
			String qrCodeProClaimed = String.format("%s", qrOutput.getValue("qrCodeProClaimed"));
			
			//logger.info("--------[解析静态二维码] 解析静态二维码成功-----------");
			
			InputParam  queryInput = new InputParam();
			queryInput.putparamString("ewmData", qrCodeProClaimed);

			logger.info("[解析静态二维码]  查询静态二维码 是否存在   请求信息" + queryInput.toString());
			
			OutputParam queryOutput = staticQRCodeDataService.queryStaticQRCodeInfo(queryInput);
			
			//logger.info("[解析静态二维码]  查询静态二维码 是否存在    完成");
			
			if(!StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())){
				logger.debug("[解析静态二维码] 没有查询到二维码相关信息");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc","没有查询到二维码相关信息");
				return outputParam;
			}
			
			//logger.info("[解析静态二维码]  查询静态二维码成功");
			
			//账号
			String acctNo = String.format("%s", queryOutput.getReturnObj().get("acctno"));
			
			//二维码类型
			String localQrType = String.format("%s", queryOutput.getReturnObj().get("ewmType"));
			
			//二维码类型
			String ewmStatue = String.format("%s", queryOutput.getReturnObj().get("ewmStatue"));		
			if(StringConstans.QRCodeStatus.DISABLE.equals(ewmStatue)){
				logger.debug("[解析静态二维码] 该二维码已经失效");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc","该二维码已经失效"+ewmStatue);
				return outputParam;
			}
			
			//姓名
			String customName = String.format("%s", queryOutput.getValue("contactName"));
			if(!StringUtil.isEmpty(customName)){
				outputParam.putValue("customName", customName);
			}
			
			//商户号
			String merId = String.format("%s", queryOutput.getValue("merId"));
			if(!StringUtil.isEmpty(merId)){
				outputParam.putValue("merId", merId);
			}
			
			//商户名称
			String merName = String.format("%s", queryOutput.getValue("merName"));
			if(!StringUtil.isEmpty(merName)){
				outputParam.putValue("merName", merName);
			}
			
			//金额
			String orderAmount = String.format("%s", queryOutput.getValue("goodsAmount"));
			if(!StringUtil.isEmpty(orderAmount)){
				outputParam.putValue("orderAmount", orderAmount);
			}
			
			//二维码币种
			String currencyType = String.format("%s", queryOutput.getValue("currencyType"));
			if(!StringUtil.isEmpty(currencyType)){
				outputParam.putValue("currencyType", currencyType);
			}
		
			//设备号
			String deviceNumber = String.format("%s", queryOutput.getValue("deviceNumber"));
			if(!StringUtil.isEmpty(deviceNumber)){
				outputParam.putValue("deviceNumber", deviceNumber);
			}
			
			outputParam.putValue("acctNo",acctNo);
			outputParam.putValue("localQrType",localQrType);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "解析静态二维码成功");
			
			//logger.info("------------  解析静态二维码流程    END---------------");
			
		} catch (Exception e) {
			logger.error("[解析静态二维码] 解析静态二维码异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("解析静态二维码异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常,解析静态二维码异常");
		} finally {
			logger.info("[解析静态二维码]  parseStaticQRCode 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}

	/**
	 * 生成静态二维码图片
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam createStaticQRCodeImage(InputParam inputParam) throws FrameException {
		
		OutputParam outputParam = new OutputParam();
		
		logger.info(" 生成静态二维码图片流程   方法开始  请求信息"+ inputParam.toString());
		
		try {
			
			//判断渠道类型
			String channel = String.format("%s", inputParam.getValue("channel"));

			//账号
			String acctNo = String.format("%s", inputParam.getValue("acctNo"));
			
			//二维码串码
			String codeUrl = String.format("%s", inputParam.getValue("codeUrl"));
			
			//二维码类型
			String localQrType = String.format("%s", inputParam.getValue("localQrType"));
			
			//原样返回
			if(!StringUtil.isEmpty(acctNo)){
				outputParam.putValue("acctNo",acctNo);
			}
			
			if(!StringUtil.isEmpty(codeUrl)){
				outputParam.putValue("codeUrl", codeUrl);
			}
			
			if(!StringUtil.isEmpty(localQrType)){
				outputParam.putValue("localQrType", localQrType);
			}

			//需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("acctNo");
			validateParamList.add("codeUrl");
			validateParamList.add("localQrType");
			
			//校验字段
			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[生成静态二维码图片] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
					
			//渠道校验
			logger.debug("[生成静态二维码图片] 渠道类型校验 channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[生成静态二维码图片] 渠道类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道类型不正确"+channel);
				return outputParam;
			}
			
			//logger.debug("[生成静态二维码图片] 查询静态二维码是否存在        开始");
			
			OutputParam queryOut = this.queryStaticQRCode(inputParam);
			
			//logger.debug("[生成静态二维码图片] 查询静态二维码是否存在        完成");
			
			if(!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())){
				logger.debug("[生成静态二维码图片] 生成静态二维码图片,查询静态二维码失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "生成静态二维码图片,查询静态二维码失败");
				return outputParam;
			}
			
			//数据库存储的静态二维码
			String codeUrl2 = String.format("%s", queryOut.getValue("codeUrl"));
			if(!codeUrl.equals(codeUrl2)){
				logger.debug("[生成静态二维码图片] 请求的静态二维码串码与数据库不匹配");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求的静态二维码串码与数据库不匹配");
				return outputParam;
			}
			
			//logger.info("[生成静态二维码图片]  开始  生成二维码图片");
			
			String imageBase64 = QRUtil.createQRImage(codeUrl);
			
			//logger.info("[生成静态二维码图片]  完成  生成二维码图片");
			
			if (StringUtil.isEmpty(imageBase64)) {
				logger.debug(" [生成静态二维码图片] 生成静态二维码图片失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "生成静态二维码图片失败");
				return outputParam;
			}
			
			//logger.info(" [生成静态二维码图片] 生成静态二维码图片成功 ");
			
			outputParam.putValue("imageContent", imageBase64);
			outputParam.putValue("imageType", QRUtil.IMAGE_TYPE);
			outputParam.putValue("imageName", acctNo);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "生成静态二维码图片成功");
			
			//logger.info("------------  生成静态二维码图片流程    END---------------");
			
		} catch (Exception e) {
			logger.error("[生成静态二位图片] 生成静态二维码图片异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg(" 生成静态二维码图片异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常,生成静态二维码图片异常");
		} finally {
			logger.info("[生成静态二位图片] 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}
	
	/**
	 * 批量查询静态二维码
	 * @param inputParam
	 */
	@Override
	public OutputParam queryBatchStaticQRCode(InputParam inputParam) throws FrameException {
		
		OutputParam outputParam = new OutputParam();
		
		logger.info("批量查询静态二维码流程  方法开始 请求信息" + inputParam.toString());
		
		try {
			
			
			//判断渠道类型
			String channel = String.format("%s", inputParam.getValue("channel"));
			
			//校验支付接入类型
			String payAccessType = String.format("%s", inputParam.getValue("payAccessType"));
			
			//客户号
			String customerNo = String.format("%s", inputParam.getValue("customerNo"));
			
			//原样返回
			if(!StringUtil.isEmpty(channel)){
				outputParam.putValue("channel",channel);
			}
			
			if(!StringUtil.isEmpty(payAccessType)){
				outputParam.putValue("payAccessType", payAccessType);
			}
			
			if(!StringUtil.isEmpty(customerNo)){
				outputParam.putValue("customerNo",customerNo);
			}
		
			//校验字段
			if (StringUtil.isEmpty(customerNo)) {
				logger.debug("[批量查询静态二维码] 请求报文字段customerNo不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段customerNo不能为空");
				return outputParam;
			}
			
			//接入类型校验
			logger.debug("[批量查询静态二维码] 接入类型校验 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType)) {
				logger.debug("[批量查询静态二维码] 接入类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "接入类型不正确"+payAccessType);
				return outputParam;
			}
			
			//渠道校验
			logger.info("[批量查询静态二维码] 渠道类型校验 channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel) 
				&& !StringConstans.CHANNEL.CHANNEL_BANK.equals(channel)
				&& !StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				
				logger.debug("[批量查询静态二维码] 渠道类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道类型不正确"+channel);
				return outputParam;
			}
			
			InputParam  queryInput = new InputParam();
			queryInput.putparamString("customerNo", customerNo);
			queryInput.putparamString("channel", channel);
			queryInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);
			
			//logger.debug("[批量查询静态二维码]  查询静态二维码 是否存在   开始");
			
			List<Map<String, Object>> staticCodeList = staticQRCodeDataService.queryStaticQRCodeList(queryInput);
			
			//logger.info("[批量查询静态二维码]  查询静态二维码 是否存在    完成");
			
			List<QRCodeInfo> qrCodeList = new ArrayList<QRCodeInfo>();
			
			//logger.info("[批量查询静态二维码]  查询静态二维码成功");
	
			for (Map<String, Object> map : staticCodeList) {
				
				QRCodeInfo qrCodeInfo = new QRCodeInfo();
				
				//账号
				qrCodeInfo.setAcctNo(String.format("%s", map.get("acctno")));
				
				//二维码类型
				qrCodeInfo.setLocalQrType(String.format("%s", map.get("ewmType")));
				
				//二维码密文
				qrCodeInfo.setCodeUrl(String.format("%s", map.get("ewmCiphertext")));
				
				//客户名称
				qrCodeInfo.setCustomName(String.format("%s", map.get("contactName")));
				
				qrCodeList.add(qrCodeInfo);
			}
			
			logger.debug("[批量查询静态二维码] 静态二维码数量 count=" + qrCodeList.size());
			
			//渠道为POSP 转成xml
			if(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)){
				StringBuilder builder = new StringBuilder();
				for (QRCodeInfo qrCodeInfo : qrCodeList) {
					builder.append(Util.objToXml(qrCodeInfo));
				}
				outputParam.putValue("qrCodeList",builder.toString());
			}
			
			//渠道为手机银行 转成JSON
			if(StringConstans.CHANNEL.CHANNEL_BANK.equals(channel)
					||StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)){				

				outputParam.putValue("qrCodeList",JsonUtil.objToJson(qrCodeList));
			}
			
			logger.debug("[批量查询静态二维码] 批量二维码内容 qrCodeList=" + outputParam.getValue("qrCodeList"));
			
			outputParam.putValue("qrCodeNum",qrCodeList.size());
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode",StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc","批量查询静态二维码成功");
			
			//logger.info("------------  批量查询静态二维码流程    END---------------");
			
		} catch (Exception e) {
			logger.error("[批量查询静态二维码] 批量查询静态二维码异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("批量查询静态二维码异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常,批量查询静态二维码异常");
		} finally {
			logger.info("[批量查询静态二维码] 方法结束 返回信息" + outputParam.toString());
		}
		
		return outputParam;
	}
	
	public IStaticQRCodeDataService getStaticQRCodeDataService() {
		return staticQRCodeDataService;
	} 


	public void setStaticQRCodeDataService(
			IStaticQRCodeDataService staticQRCodeDataService) {
		this.staticQRCodeDataService = staticQRCodeDataService;
	}
}
