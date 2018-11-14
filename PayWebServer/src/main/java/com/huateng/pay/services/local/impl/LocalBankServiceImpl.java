package com.huateng.pay.services.local.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.demo.trade.config.Configs;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.crypto.LRUCache;
import com.huateng.pay.common.socket.SocketClient;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.common.validate.POSPValidation;
import com.huateng.pay.common.validate.vali.Validation;
import com.huateng.pay.handler.thread.ThreadNotifyHelper;
import com.huateng.pay.po.notify.NotifyMessage;
import com.huateng.pay.services.alipay.AliPayPayService;
import com.huateng.pay.services.cups.ICupsPayService;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IQRCodeService;
import com.huateng.pay.services.db.IStaticQRCodeDataService;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;
import com.huateng.pay.services.local.ILocalBankService;
import com.huateng.pay.services.tradelimit.ITblMerTradeLimitService;
import com.huateng.pay.services.weixin.WxMerchantSynchService;
import com.huateng.pay.services.weixin.WxPayService;
import com.huateng.utils.QRCodeTypeEnum;
import com.huateng.utils.QRCodeUtil;
import com.huateng.utils.Util;

/**
 * 本行二维码业务处理类
 *
 */
public class LocalBankServiceImpl implements ILocalBankService {
	private Logger logger = LoggerFactory.getLogger(LocalBankServiceImpl.class);
	private IOrderService orderService;
	private IQRCodeService qrCodeService;
	private WxPayService wxPayService;
	private AliPayPayService aliPayPayService;
	protected IStaticQRCodeDataService staticQRCodeDataService;
	private IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService;
	private ICupsPayService cupsPayService;
	private ITblMerTradeLimitService tblMerTradeLimitService;
	private WxMerchantSynchService wxMerchantSynchService;
	private IMerchantChannelService merchantChannelService;
	private LRUCache<String,String> lruCache = new LRUCache<String,String>(256-1);

	/**
	 * 手机或者移动前端获取二维码
	 * 
	 * @param paramMap
	 * @return
	 */
	@Override
	public OutputParam getMobileQRcode(InputParam intPutParam) {

		logger.info("[手机或者移动前端获取二维码]  getMobileQRcode 方法开始 请求信息" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();

		String logPrefix = "[手机银行获取二维码]";

		try {

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));

			if (StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logPrefix = "[移动前端获取二维码]";
			}

			logger.info(logPrefix + " channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_BANK.equals(channel))
					&& !(StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel))) {
				logger.debug(logPrefix + " 渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确"+channel);
				return outputParam;
			}
			/* add by ghl 20180320 */
			String cardAttr = String.format("%s", intPutParam.getValue("cardAttr"));
			logger.debug(logPrefix + " cardAttr:[" + cardAttr + "]");

			// 二维码前缀类型
			String localQrType = String.format("%s", intPutParam.getValue("localQrType"));

			// 因为信用卡暂时不上线，所以只生成借记卡银联二维码
			/*
			 * if (StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr)) {
			 * //一期银联二维码暂不支持本行信用卡，所以信用卡暂时只生成本行二维码 localQrType =
			 * QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType(); }
			 */

			logger.info(logPrefix + " localQrType:[" + localQrType + "]");

			// 卡属性是01或02，并且二维码前缀类型为13，则生成银联二维码
			if ((StringConstans.CupsEwmInfo.CATD_ATTR_DEBIT.equals(cardAttr)
					|| StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr))
					&& QRCodeTypeEnum.STATIC_QR_CODE_CUPS.getType().equals(localQrType)) {
				outputParam = cupsPayService.unionPayApplyQrNo(intPutParam);
				return outputParam;
			}

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("cardNo");
			validateParamList.add("customName");
			validateParamList.add("deviceNumber");
			validateParamList.add("orderNumber");
			validateParamList.add("orderTime");

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug(logPrefix + " 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			/*
			 * //二维码前缀类型 String localQrType = String.format("%s",
			 * intPutParam.getValue("localQrType"));
			 * 
			 * //因为信用卡暂时不上线，所以只生成借记卡银联二维码 if
			 * (StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr)) {
			 * //一期银联二维码暂不支持本行信用卡，所以信用卡暂时只生成本行二维码 localQrType =
			 * QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType(); }
			 * 
			 * logger.info(logPrefix + " localQrType:[" + localQrType + "]");
			 *  这两个判断可以合并
			 */
			if (!QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE.getType().equals(localQrType)
					&& StringConstans.CHANNEL.CHANNEL_BANK.equals(channel)) {
				logger.debug(logPrefix + " 二维码前缀类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前缀类型不正确");
				return outputParam;
			}

			if (!QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType().equals(localQrType)
					&& StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logger.debug(logPrefix + " 二维码前缀类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前缀类型不正确"+localQrType);
				return outputParam;
			}

			// logger.info(logPrefix + " 开始获取二维码的序列号");

			// 获取二维码序列号
			String qrSeqNo = qrCodeService.getQRSeqNo();

			logger.debug(logPrefix + " 获取二维码的序列号完成,qrSeqNo:[" + qrSeqNo + "]");

			InputParam qrInput = new InputParam();
			qrInput.putParams("txnServiceCode", intPutParam.getValue("txnCode"));
			qrInput.putParams("qrSeqNo", qrSeqNo);
			qrInput.putParams("localQrType", localQrType);

			// logger.info(logPrefix + " 开始获取二维码的加密信息");

			// 获取二维码信息
			OutputParam output = QRCodeUtil.encryptQRCodeCode(qrInput);

			// logger.info(logPrefix + " 获取二维码的加密信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(output.getReturnCode()))) {
				logger.debug(logPrefix + " 获取二维码密文失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 获取二维码密文失败");
				return outputParam;
			}

			// logger.info("---------------" + logPrefix + " 获取二维码的加密信息成功--------------");

			// 二维码明文
			String qrCodeProClaimed = String.format("%s", output.getValue("qrCodeProClaimed"));
			// 二维码密文
			String qrCodecipherText = String.format("%s", output.getValue("qrCodecipherText"));
			// 二维码状态
			String qrCodeStatus = String.format("%s", output.getValue("qrCodeStatus"));
			// 二维码类型
			String qrCodeType = String.format("%s", output.getValue("qrCodeType"));
			// 二维码有效期
			String qrCodeExpire = String.format("%s", output.getValue("qrCodeExpire"));
			// 生成时间
			String generatDt = String.format("%s", output.getValue("generatDt"));
			// 卡号
			String cardNo = String.format("%s", intPutParam.getValue("cardNo"));
			// 姓名
			String customName = String.format("%s", intPutParam.getValue("customName"));
			// 设备号
			String deviceNumber = String.format("%s", intPutParam.getValue("deviceNumber"));
			// 订单号
			String orderNumber = String.format("%s", intPutParam.getValue("orderNumber"));

			InputParam saveQRParam = new InputParam();
			saveQRParam.putparamString("ewmData", qrCodeProClaimed);
			saveQRParam.putparamString("cardNo", cardNo);
			saveQRParam.putparamString("customName", customName);
			saveQRParam.putparamString("deviceNumber", deviceNumber);
			saveQRParam.putparamString("channel", channel);
			saveQRParam.putparamString("orderNumber", orderNumber);
			saveQRParam.putparamString("ewmType", qrCodeType);
			saveQRParam.putparamString("status", qrCodeStatus);
			saveQRParam.putparamString("validMinute", qrCodeExpire);
			saveQRParam.putparamString("createTime", generatDt);

			logger.debug("[手机或者移动前端获取二维码]  开始保存二维码的信息 请求信息" + saveQRParam.toString());

			OutputParam resultOutput = qrCodeService.saveQRCodeInfo(saveQRParam);

			// logger.info(logPrefix + " 保存二维码的信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(resultOutput.getReturnCode()))) {
				logger.debug(logPrefix + " 保存二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 保存二维码信息失败");
				return outputParam;
			}

			// logger.info("------------ " + logPrefix + " 保存二维码的信息成功-------------");

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("codeUrl", qrCodecipherText);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "获取二维码成功");

			// logger.info("------------" + logPrefix + " 获取二维码的信息成功--------------");

		} catch (Exception e) {
			logger.error(logPrefix + " 获取二维码信息异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码 前置异常" + e.getMessage());
		} 
		logger.info("[手机或者移动前端获取二维码]  getMobileQRcode 方法结束 返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 本行主扫下单[POSP获取二维码]
	 * 
	 * @param intPutParam
	 * @return
	 */
	@Override
	public OutputParam pospUnifiedConsume(InputParam intPutParam) {
		logger.info("本行主扫下单[POSP获取二维码]  pospUnifiedConsume 方法开始  请求信息" + intPutParam.toString());

		OutputParam outputParam = new OutputParam();

		String logPrefix = "[POSP获取二维码]";

		try {

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));

			if (StringConstans.CHANNEL.CHANNEL_ONLINE_PAY.equals(channel)) {
				logPrefix = "[支付平台获取二维码]";
			}

			if (StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				intPutParam.putParams("localQrType", QRCodeTypeEnum.DYNAMIC_QR_CODE_POSP.getType());
			}

			if (!(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel))
					&& !(StringConstans.CHANNEL.CHANNEL_ONLINE_PAY.equals(channel))) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确" + channel);
				return outputParam;
			}

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("merId");
			validateParamList.add("termId");
			validateParamList.add("merName");
			validateParamList.add("merAcctNo");
			validateParamList.add("orderTime");
			validateParamList.add("orderNumber");

			// 校验字段
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 校验金额
			String orderAmount = String.format("%s", intPutParam.getValue("orderAmount"));
			logger.debug(logPrefix + "orderAmount:[" + orderAmount + "]");

			// 验证金额长度
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug(logPrefix + "交易金额长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易金额长度不正确" + orderAmount);
				return outputParam;
			}

			// 校验金额大小
			BigDecimal orderAmt = new BigDecimal(orderAmount);
			if (orderAmt.compareTo(new BigDecimal("0")) <= 0) {
				logger.debug(logPrefix + "交易金额不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", " 交易金额不正确" + orderAmt);
				return outputParam;

			}

			// 校验交易类型
			String transType = String.format("%s", intPutParam.getValue("transType"));
			if (!(StringConstans.TransType.TRANS_CONSUME.equals(transType))) {
				logger.debug(logPrefix + "交易类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易类型不正确" + transType);
				return outputParam;
			}

			// 校验接入类型
			String payAccessType = String.format("%s", intPutParam.getValue("payAccessType"));
			if (!(StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType))) {
				logger.debug(logPrefix + "支付接入不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付接入不正确" + payAccessType);
				return outputParam;
			}

			// 校验支付类型
			String payType = String.format("%s", intPutParam.getValue("payType"));
			if (!(StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType))) {
				logger.debug(logPrefix + " 支付类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付类型不正确" + payType);
				return outputParam;
			}

			// 商户订单时间
			String orderTime = String.format("%s", intPutParam.getValue("orderTime"));
			if (!orderTime.matches("\\d{14}")) {
				logger.debug(logPrefix + "商户订单时间长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "商户订单时间长度不正确" + orderTime);
				return outputParam;
			}

			// 二维码前缀类型
			String localQrType = String.format("%s", intPutParam.getValue("localQrType"));
			if (!QRCodeTypeEnum.DYNAMIC_QR_CODE_POSP.getType().equals(localQrType)
					&& StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug(logPrefix + "二维码前缀类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前缀类型不正确" + localQrType);
				return outputParam;
			}

			if (!QRCodeTypeEnum.DYNAMIC_QR_CODE_PAY_PLATFORM.getType().equals(localQrType)
					&& StringConstans.CHANNEL.CHANNEL_ONLINE_PAY.equals(channel)) {
				logger.debug(logPrefix + "二维码前缀类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前缀类型不正确" + localQrType);
				return outputParam;
			}

			// 商户号
			String merId = String.format("%s", intPutParam.getValue("merId"));
			// 商户订单号
			String orderNumber = String.format("%s", intPutParam.getValue("orderNumber"));
			// 订单日期
			String merOrDt = orderTime.substring(0, 8);
			// 订单时间
			String merOrTm = orderTime.substring(8);

			InputParam input = new InputParam();
			input.putparamString("merId", merId);
			input.putparamString("merOrderId", orderNumber);
			input.putparamString("merOrDt", merOrDt);
			input.putparamString("merOrTm", merOrTm);

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryOrder(input);

			// 订单存在
			if (StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// 订单状态为成功或失败
				logger.debug(logPrefix + "该订单已存在，请勿重复提交");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该订单已存在，请勿重复提交");
				return outputParam;
			}

			// 获取二维码序列号
			String qrSeqNo = qrCodeService.getQRSeqNo();

			logger.debug(logPrefix + "获取二维码的序列号完成,qrSeqNo:[" + qrSeqNo + "]");

			InputParam qrInput = new InputParam();
			qrInput.putParams("txnServiceCode", intPutParam.getValue("txnCode"));
			qrInput.putParams("qrSeqNo", qrSeqNo);
			qrInput.putParams("localQrType", localQrType);

			// logger.info(logPrefix + " 开始加密二维码信息");

			// 获取二维码信息
			OutputParam generatQROutput = QRCodeUtil.encryptQRCodeCode(qrInput);

			// logger.info(logPrefix + " 加密二维码信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(generatQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 获取二维码密文失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 获取二维码密文失败");
				return outputParam;
			}

			// logger.info(logPrefix + " ---------------加密二维码信息成功--------------");

			// 二维码明文
			String qrCodeProClaimed = String.format("%s", generatQROutput.getValue("qrCodeProClaimed"));
			// 二维码密文
			String qrCodecipherText = String.format("%s", generatQROutput.getValue("qrCodecipherText"));
			// 二维码状态
			String qrCodeStatus = String.format("%s", generatQROutput.getValue("qrCodeStatus"));
			// 二维码类型
			String qrCodeType = String.format("%s", generatQROutput.getValue("qrCodeType"));
			// 二维码有效期
			String qrCodeExpire = String.format("%s", generatQROutput.getValue("qrCodeExpire"));
			// 生成时间
			String generatDt = String.format("%s", generatQROutput.getValue("generatDt"));
			// 商户清算账户
			String merAcctNo = String.format("%s", intPutParam.getValue("merAcctNo"));
			// 商户名称
			String merName = String.format("%s", intPutParam.getValue("merName"));
			// 预留字段
			String reserve = String.format("%s", intPutParam.getValue("reserve"));

			// 保存二维码参数
			InputParam saveQRParam = new InputParam();
			saveQRParam.putparamString("ewmData", qrCodeProClaimed);
			saveQRParam.putparamString("merId", merId);
			saveQRParam.putparamString("merName", merName);
			saveQRParam.putparamString("merAcctNo", merAcctNo);
			saveQRParam.putparamString("tradeMoney", orderAmount);
			saveQRParam.putparamString("channel", channel);
			saveQRParam.putparamString("orderNumber", orderNumber);
			saveQRParam.putparamString("createTime", generatDt);
			saveQRParam.putparamString("ewmType", qrCodeType);
			saveQRParam.putparamString("status", qrCodeStatus);
			saveQRParam.putparamString("validMinute", qrCodeExpire);

			// 预留字段
			if (!StringUtil.isEmpty(reserve)) {
				saveQRParam.putparamString("reserve", reserve);
			}

			logger.debug(logPrefix + " 开始保存二维码的信息");

			// 保存二维码信息
			OutputParam saveQROutput = qrCodeService.saveQRCodeInfo(saveQRParam);

			logger.debug(logPrefix + " 保存二维码的信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveQROutput.getReturnCode()))) {
				logger.debug(logPrefix + "保存二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + "保存二维码信息失败");
				return outputParam;
			}

			logger.debug(logPrefix + " ---------------保存二维码的信息成功--------------");

			logger.debug(logPrefix + " 开始获取订单序列号");

			// 获取订单序列号
			String txnSeqId = orderService.getOrderNo();

			logger.debug(logPrefix + " 获取订单序列号完成,txnSeqId:[" + txnSeqId + "]");

			// 新增订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("ewmData", qrCodeProClaimed);
			orderInput.putparamString("txnDt", DateUtil.getDateYYYYMMDD());
			orderInput.putparamString("txnTm", DateUtil.getDateHHMMSS());
			orderInput.putparamString("merId", merId);
			orderInput.putparamString("merOrderId", orderNumber);
			orderInput.putparamString("merOrDt", merOrDt);
			orderInput.putparamString("merOrTm", merOrTm);
			orderInput.putparamString("txnType", transType);
			orderInput.putparamString("txnChannel", channel);
			orderInput.putparamString("payType", payType);
			orderInput.putparamString("payAccessType", payAccessType);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("currencyCode", StringConstans.SETTLE_CURRENTY_TYPE);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "订单初始化");

			logger.debug("本行主扫下单[POSP获取二维码] 新增订单 请求信息" + orderInput.toString());

			// 保存订单
			OutputParam saveOrderOutput = orderService.insertOrder(orderInput);

			// logger.info(logPrefix + " 保存订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				logger.debug(logPrefix + " 保存订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 保存订单信息失败");
				return outputParam;
			}

			// logger.info("--------------" + logPrefix + " 保存订单信息成功-------------");

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("codeUrl", qrCodecipherText);
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "生成二维码信息成功");

		} catch (FrameException e) {
			logger.error(logPrefix + "获取二维码信息异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常");
		}
		logger.info("本行主扫下单[POSP获取二维码]  pospUnifiedConsume 方法结束  返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 本行被扫下单[POSP扫手机银行]
	 * 
	 * @param intPutParam
	 * @return
	 */
	@Override
	public OutputParam pospMicroConsume(InputParam intPutParam) {
		logger.info("本行被扫下单[POSP扫手机银行]  pospMicroConsume 方法开始  请求信息" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("codeUrl");
			validateParamList.add("merId");
			validateParamList.add("termId");
			validateParamList.add("merName");
			validateParamList.add("merAcctNo");
			validateParamList.add("orderTime");
			validateParamList.add("orderNumber");

			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[POSP被扫] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 校验串码
			String codeUrl = String.format("%s", intPutParam.getValue("codeUrl"));
			logger.debug("[POSP被扫] codeUrl:[" + codeUrl + "]");
			if (!codeUrl.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE.getType())
					&& !codeUrl.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType())) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码类型不正确"+codeUrl);
				return outputParam;
			}

			// 校验金额
			String orderAmount = String.format("%s", intPutParam.getValue("orderAmount"));
			logger.debug("[POSP被扫] orderAmount:[" + orderAmount + "]");

			// 验证金额长度
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug("[POSP被扫] 交易金额长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易金额长度不正确"+orderAmount);
				return outputParam;
			}

			// 验证金额大小
			BigDecimal orderAmt = new BigDecimal(orderAmount);
			if (orderAmt.compareTo(new BigDecimal("0")) <= 0) {
				logger.debug("[POSP被扫] 交易金额不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易金额不正确"+orderAmt);
				return outputParam;

			}

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));
			logger.debug("[POSP被扫] channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel))) {
				logger.debug("[POSP被扫] 渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确"+channel);
				return outputParam;
			}

			// 校验交易类型
			String transType = String.format("%s", intPutParam.getValue("transType"));
			logger.debug("[POSP被扫] transType:[" + transType + "]");
			if (!(StringConstans.TransType.TRANS_CONSUME.equals(transType))) {
				logger.debug("[POSP被扫] 交易类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易类型不正确"+transType);
				return outputParam;
			}

			// 校验接入类型
			String payAccessType = String.format("%s", intPutParam.getValue("payAccessType"));
			logger.debug("[POSP被扫] payAccessType:[" + payAccessType + "]");
			if (!(StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType))) {
				logger.debug("[POSP被扫] 支付接入不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付接入不正确"+payAccessType);
				return outputParam;
			}

			// 校验支付类型
			String payType = String.format("%s", intPutParam.getValue("payType"));
			logger.debug("[POSP被扫] payType:[" + payType + "]");
			if (!(StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType))) {
				logger.debug("[POSP被扫] 支付类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付类型不正确"+payType);
				return outputParam;
			}

			// 校验币种类型
			String currencyType = String.format("%s", intPutParam.getValue("currencyType"));
			logger.debug("[POSP被扫] currencyType:[" + currencyType + "]");
			if (!(StringConstans.SETTLE_CURRENTY_TYPE.equals(currencyType))) {
				logger.debug("[POSP被扫] 币种类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "币种型不正确"+currencyType);
				return outputParam;
			}

			// 商户订单时间
			String orderTime = String.format("%s", intPutParam.getValue("orderTime"));
			logger.debug("[POSP被扫] orderTime:[" + orderTime + "]");
			if (!orderTime.matches("\\d{14}")) {
				logger.debug("[POSP被扫] 商户订单时间长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "商户订单时间长度不正确"+orderTime);
				return outputParam;
			}

			// 商户号
			String merId = String.format("%s", intPutParam.getValue("merId"));
			// 商户名称
			String merName = String.format("%s", intPutParam.getValue("merName"));
			// 商户订单号
			String orderNumber = String.format("%s", intPutParam.getValue("orderNumber"));
			// 商户清算账号
			String merAcctNo = String.format("%s", intPutParam.getValue("merAcctNo"));
			// 订单日期
			String merOrDt = orderTime.substring(0, 8);
			// 订单时间
			String merOrTm = orderTime.substring(8);
			// 二维码串
			String qrCodecipherText = String.format("%s", intPutParam.getValue("codeUrl"));

			InputParam input = new InputParam();
			input.putparamString("merId", merId);
			input.putparamString("merOrderId", orderNumber);
			input.putparamString("merOrDt", merOrDt);
			input.putparamString("merOrTm", merOrTm);

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryOrder(input);

			// 订单存在
			if (StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				logger.debug("[POSP被扫] 该订单已存在，请勿重复提交");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该订单已存在，请勿重复提交");
				return outputParam;
			}

			// 解密二维码信息
			OutputParam decryptQROutput = QRCodeUtil.decryptQRCodeCode(qrCodecipherText);

			if (!(StringConstans.returnCode.SUCCESS.equals(decryptQROutput.getReturnCode()))) {
				logger.debug("[POSP被扫] 二维码信息解密失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[POSP被扫] 二维码信息解密失败");
				return outputParam;
			}

			// 二维码明文信息
			String qrCodeProClaimed = String.format("%s", decryptQROutput.getValue("qrCodeProClaimed"));
			if (StringUtils.isBlank(qrCodeProClaimed) || "null".equals(qrCodeProClaimed)
					|| qrCodeProClaimed.length() != 18) {
				logger.debug("[POSP被扫] 二维码信息解密失败,解密后qrCodeProClaimed[" + qrCodeProClaimed + "]");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[POSP被扫] 二维码信息解密失败,解密后qrCodeProClaimed[\" + qrCodeProClaimed + \"]");
				return outputParam;
			}

			InputParam queryQRInput = new InputParam();
			queryQRInput.putparamString("ewmData", qrCodeProClaimed);

			// 查询二维码信息
			OutputParam queryQROutput = qrCodeService.queryQRCodeInfo(queryQRInput);

			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				logger.debug("[POSP被扫] 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// 获取查询记录
			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();
			// 二维码生成时间
			String generatQRDate = String.format("%s", queryQRMap.get("createTime"));
			// 二维码状态
			String status = String.format("%s", queryQRMap.get("status"));
			// 卡号
			String cardNo = String.format("%s", queryQRMap.get("cardNo"));

			// 校验二维码是否已失效
			if (StringConstans.QRCodeStatus.DISABLE.equals(status)) {
				logger.debug("[手机解密POSP二维码信息] 二维码信息已失效");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码信息已失效");
				return outputParam;
			}

			// 二维码失效标志
			boolean isValidate = false;
			// 二维码生成时间
			long generatQRDateVal = DateUtil.parse(generatQRDate, DateUtil.YYYYMMDDHHMMSS).getTime();
			// 当前时间
			long nowDateVal = (new Date()).getTime();
			// 二维码的有效分钟
			long qrExpire = Long.valueOf(Constants.getParam("qrcode_expire"));
			// 判断二维码是否超过有效期
			if (nowDateVal - generatQRDateVal > qrExpire * 60 * 1000) {
				isValidate = true;
			}

			// 二维码码失效
			if (isValidate) {

				logger.debug("[POSP被扫] 二维码已失效");

				InputParam upateQRInput = new InputParam();
				upateQRInput.putparamString("ewmData", qrCodeProClaimed);
				upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

				// 更新二维码信息
				OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

				if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
					logger.debug("[POSP被扫] 更新二维码失效信息失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "[POSP被扫] 更新二维码失效信息失败");
					return outputParam;
				}

				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该二维码已失效");
				return outputParam;
			}

			// 二维码被扫以后置为失效状态
			InputParam upateQRInput = new InputParam();
			upateQRInput.putparamString("ewmData", qrCodeProClaimed);
			upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

			logger.info("本行被扫下单[POSP扫手机银行] 更新二维码已失效信息 请求信息" + upateQRInput.toString());

			// 更新二维码信息
			OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

			// logger.info("[POSP被扫] 更新二维码已失效信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
				logger.debug("[POSP被扫] 更新二维码失效信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[POSP被扫] 更新二维码已失效信息成功------------");

			// logger.info("[POSP被扫] 开始获取订单流水号");

			// 获取内部流水号
			String txnSeqId = orderService.getOrderNo(channel);

			logger.debug("[POSP被扫] 获取订单流水号完成,txnSeqId:[" + txnSeqId + "]");

			// 内部交易日期
			String txnDt = DateUtil.getDateYYYYMMDD();
			// 内部交易时间
			String txnTm = DateUtil.getDateHHMMSS();
			// 14位的内部交易时间
			String txnTime = String.format("%s%s", txnDt, txnTm);

			// 新增订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("ewmData", qrCodeProClaimed);
			orderInput.putparamString("merId", merId);
			orderInput.putparamString("merOrderId", orderNumber);
			orderInput.putparamString("merOrDt", merOrDt);
			orderInput.putparamString("merOrTm", merOrTm);
			orderInput.putparamString("txnType", transType);
			orderInput.putparamString("txnChannel", channel);
			orderInput.putparamString("payType", payType);
			orderInput.putparamString("payAccessType", payAccessType);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("currencyCode", StringConstans.SETTLE_CURRENTY_TYPE);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "订单初始化");

			logger.info("本行被扫下单[POSP扫手机银行] 开始保存订单信息 请求信息" + orderInput.toString());

			// 保存订单
			OutputParam saveOrderOutput = orderService.insertOrder(orderInput);

			// logger.info("[POSP被扫] 保存订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				logger.debug("[POSP被扫] 保存订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[POSP被扫] 保存订单信息成功------------");

			InputParam mobileInputParam = new InputParam();

			// 手机需要银行的的字段,需要请求手机银行
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("orderAmount", orderAmount);
			paramMap.put("merName", merName);
			paramMap.put("merAcctNo", merAcctNo);
			paramMap.put("txnSeqId", txnSeqId);
			paramMap.put("txnTime", txnTime);
			paramMap.put("orderNumber", orderNumber);
			paramMap.put("orderTime", orderTime);
			paramMap.put("merId", merId);
			paramMap.put("cardNo", cardNo);
			paramMap.put("codeUrl", qrCodecipherText);
			paramMap.put("respCode", StringConstans.OrderState.STATE_01);
			paramMap.put("respDesc", "交易正在处理");

			mobileInputParam.setParamString(paramMap);

			// logger.info("[POSP被扫] 调用通知支付方法 开始");

			OutputParam mobileOutput = this.notifyPay(mobileInputParam);

			logger.info("[POSP被扫] 调用通知支付方法  结束:返回参数" + mobileOutput.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(mobileOutput.getReturnCode())) {
				logger.error("[POSP被扫] 通知支付返回失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", mobileOutput.getValue("respCode"));
				outputParam.putValue("respDesc", mobileOutput.getValue("respDesc"));
				return outputParam;
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("respCode", mobileOutput.getValue("respCode"));
			outputParam.putValue("respDesc", mobileOutput.getValue("respDesc"));

		} catch (Exception e) {
			logger.error("[POSP被扫] 出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码系统异常");
		}
		logger.info("[POSP被扫]  pospMicroConsume 支付方法  结束:返回参数" + outputParam.getReturnObj().toString());
		return outputParam;
	}

	/**
	 * 手机解密POSP二维码信息
	 * 
	 * @param paramMap
	 * @return
	 */
	@Override
	public OutputParam mobileDecryptPospQRcode(InputParam intPutParam) {

		logger.info("[手机解密POSP二维码信息]  mobileDecryptPospQRcode 方法开始 请求信息" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();

		String logPrefix = "[手机银行解密POSP二维码信息]";

		try {

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));

			// 二维码密文
			String qrCodeCipherText = String.format("%s", intPutParam.getValue("codeUrl"));

			if (qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_POSP.getType())
					&& StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logPrefix = "[移动前端解密POSP二维码信息]";
			}

			if (qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_PAY_PLATFORM.getType())
					&& StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logPrefix = "[移动前端解密支付平台二维码信息]";
			}

			if (qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_PAY_PLATFORM.getType())
					&& StringConstans.CHANNEL.CHANNEL_BANK.equals(channel)) {
				logPrefix = "[手机银行解密支付平台二维码信息]";
			}

			if (!qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_POSP.getType())
					&& !qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_PAY_PLATFORM.getType())) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码类型不正确"+qrCodeCipherText);
				return outputParam;
			}

			logger.info(logPrefix + " channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_BANK.equals(channel)
					|| StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel))) {
				logger.debug(logPrefix + " 请渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确"+channel);
				return outputParam;
			}

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("codeUrl");
			validateParamList.add("orderTime");
			validateParamList.add("orderNumber");

			// 校验字段不能为空
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug(logPrefix + " 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// logger.info(logPrefix + " 开始解密二维码信息");

			OutputParam decryptQROutput = QRCodeUtil.decryptQRCodeCode(qrCodeCipherText);

			// logger.info(logPrefix + " 解密二维码信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(decryptQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 解密二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 解密二维码信息失败");
				return outputParam;
			}

			// 二维码明文信息
			String qrCodeProClaimed = String.format("%s", decryptQROutput.getValue("qrCodeProClaimed"));
			if (StringUtils.isBlank(qrCodeProClaimed) || "null".equals(qrCodeProClaimed)
					|| qrCodeProClaimed.length() != 18) {
				logger.debug(logPrefix + " 二维码信息解密失败,解密后qrCodeProClaimed[" + qrCodeProClaimed + "]");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 二维码信息解密失败,解密后qrCodeProClaimed[" + qrCodeProClaimed + "]");
				return outputParam;
			}

			// logger.info("----------------" + logPrefix + " 解密二维码信息成功------------");

			InputParam queryQRInput = new InputParam();
			queryQRInput.putparamString("ewmData", qrCodeProClaimed);

			// logger.info(logPrefix + " 开始查询二维码信息");

			// 查询二维码信息
			OutputParam queryQROutput = qrCodeService.queryQRCodeInfo(queryQRInput);

			// logger.info(logPrefix + " 查询二维码信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("----------" + logPrefix + " 查询二维码信息成功-----------");

			// 获取查询记录
			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();
			// 二维码生成时间
			String generatQRDate = String.format("%s", queryQRMap.get("createTime"));
			// 金额
			String orderAmount = String.format("%s", queryQRMap.get("tradeMoney"));
			// 商户清算账号
			String merAcctNo = String.format("%s", queryQRMap.get("merAcctno"));
			// 商户名称
			String merName = String.format("%s", queryQRMap.get("merName"));
			// 商户号
			String merId = String.format("%s", queryQRMap.get("merId"));
			// 订单号
			String orderNumber = String.format("%s", queryQRMap.get("orderNumber"));
			// 二维码状态
			String status = String.format("%s", queryQRMap.get("status"));
			// 预留字段
			String reserve = String.format("%s", queryQRMap.get("reserve"));

			// 二维码已失效
			if (StringConstans.QRCodeStatus.DISABLE.equals(status)) {
				logger.error(logPrefix + " 二维码信息已失效");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码信息已失效");
				return outputParam;
			}

			// 二维码失效标志
			boolean isValidate = false;
			// 二维码生成时间
			long generatQRDateVal = DateUtil.parse(generatQRDate, DateUtil.YYYYMMDDHHMMSS).getTime();
			// 当前时间
			long nowDateVal = (new Date()).getTime();
			// 二维码的有效分钟
			long qrExpire = Long.valueOf(Constants.getParam("qrcode_expire"));
			// 判断二维码是否超过有效期
			if (nowDateVal - generatQRDateVal > qrExpire * 60 * 1000) {
				isValidate = true;
			}

			// 二维码码失效
			if (isValidate) {

				logger.debug(logPrefix + " 二维码信息已失效");

				// 更新二维码参数
				InputParam upateQRInput = new InputParam();
				upateQRInput.putparamString("ewmData", qrCodeProClaimed);
				upateQRInput.putParams("status", StringConstans.QRCodeStatus.DISABLE);

				logger.debug("手机解密POSP二维码信息 开始更新二维码失效信息 请求信息" + upateQRInput.toString()); // 更新二维码信息

				OutputParam updateQROutput = qrCodeService.queryQRCodeInfo(upateQRInput);

				// logger.info(logPrefix + " 更新二维码失效信息完成");

				if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
					logger.debug(logPrefix + " 更新二维码信息失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", logPrefix + " 更新二维码信息失败");
					return outputParam;
				}

				// logger.info("---------" + logPrefix + " 更新二维码失效信息成功--------");

				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该二维码已失效");

				return outputParam;
			}

			// 二维码被扫以后置为失效状态
			InputParam upateQRInput = new InputParam();
			upateQRInput.putparamString("ewmData", qrCodeProClaimed);
			upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

			logger.debug("手机解密POSP二维码信息 开始更新二维码已失效信息 请求信息" + upateQRInput.toString());

			// 更新二维码信息
			OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

			// logger.info(logPrefix + " 更新二维码已失效信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 更新二维码失效信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 更新二维码失效信息失败");
				return outputParam;
			}

			// logger.info("--------------" + logPrefix + " 更新二维码已失效信息成功------------");

			InputParam queryOrderParam = new InputParam();
			queryOrderParam.putparamString("merId", merId);
			queryOrderParam.putparamString("merOrderId", orderNumber);

			logger.debug("手机解密POSP二维码信息  开始订单信息 请求信息" + queryOrderParam.toString());

			// 查询订单
			OutputParam queryOrderOutput = orderService.queryOrder(queryOrderParam);

			// logger.info(logPrefix + " 查询订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode()))) {
				logger.debug(logPrefix + " 查询订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "查询订单信息失败");
				return outputParam;
			}

			// logger.info("----------" + logPrefix + " 查询订单信息成功-----------");

			Map<String, Object> queryOrderMap = queryOrderOutput.getReturnObj();

			// 二维码前置生成的订单号
			String txnSeqId = String.format("%s", queryOrderMap.get("txnSeqId"));
			// 二维码前置生成的订单号
			String txnDt = String.format("%s", queryOrderMap.get("txnDt"));
			// 二维码前置生成的订单号
			String txnTm = String.format("%s", queryOrderMap.get("txnTm"));
			// 14位的订单时间
			String txnTime = String.format("%s%s", txnDt, txnTm);
			// 交易日期
			String merOrDt = String.format("%s", queryOrderMap.get("merOrDt"));
			// 交易时间
			String merOrTm = String.format("%s", queryOrderMap.get("merOrTm"));
			// 14位的订单时间
			String initOrderTime = String.format("%s%s", merOrDt, merOrTm);
			// 订单号
			String initOrderNumber = String.format("%s", queryOrderMap.get("merOrderId"));

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("orderAmount", orderAmount);
			outputParam.putValue("merName", merName);
			outputParam.putValue("merAcctNo", merAcctNo);
			outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.putValue("txnTime", txnTime);
			outputParam.putValue("respDesc", "解密二维码信息成功");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);

			// 只有移动前端返回，手机银行不返回
			if (StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				outputParam.putValue("merId", merId);
				outputParam.putValue("initOrderNumber", initOrderNumber);
				outputParam.putValue("initOrderTime", initOrderTime);
				if (!StringUtil.isEmpty(reserve))
					outputParam.putValue("reserve", reserve);
			}

			// logger.info("--------------" + logPrefix + " 手机解密POSP二维码信息成功------------");

		} catch (Exception e) {
			logger.error(logPrefix + " 手机解密二维码信息异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置系统异常" + e.getMessage());
		} 
		logger.info("[手机解密POSP二维码信息]   mobileDecryptPospQRcode 方法结束 返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 手机或移动前端解密手机二维码信息[面对面转账]
	 * 
	 * @param paramMap
	 * @return
	 */
	@Override
	public OutputParam mobileDecrytMobileQRcode(InputParam intPutParam) {

		logger.info("[手机或移动前端解密手机二维码信息[面对面转账]]  mobileDecrytMobileQRcode 方法开始  请求信息" + intPutParam.toString());

		OutputParam outputParam = new OutputParam();

		String logPrefix = "[手机解密手机二维码信息[面对面转账]";

		try {

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));

			// 二维码密文
			String qrCodeCipherText = String.format("%s", intPutParam.getValue("codeUrl"));

			if (StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logPrefix = "[移动前端解密手机二维码信息[面对面转账]]";
			}

			logger.info(logPrefix + " channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_BANK.equals(channel))
					&& !(StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel))) {
				logger.debug(logPrefix + " 渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确");
				return outputParam;
			}

			if (!qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE.getType())
					&& !qrCodeCipherText.startsWith(QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType())) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码类型不正确");
				return outputParam;
			}

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("codeUrl");

			// 校验字段是否为空
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug(logPrefix + " 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// logger.info(logPrefix + " 开始解密二维码信息");

			OutputParam decryptQROutput = QRCodeUtil.decryptQRCodeCode(qrCodeCipherText);

			// logger.info(logPrefix + " 解密二维码信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(decryptQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 解密二维码密文失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "解密二维码密文失败");
				return outputParam;
			}

			// logger.info("--------" + logPrefix + " 解密二维码信息成功-----------");

			// 二维码明文
			String qrCodeProClaimed = String.format("%s", decryptQROutput.getValue("qrCodeProClaimed"));

			InputParam queryQRInput = new InputParam();
			queryQRInput.putparamString("ewmData", qrCodeProClaimed);

			logger.debug("手机或移动前端解密手机二维码信息[面对面转账] 开始查询二维码信息 请求信息" + queryQRInput.toString());

			// 查询二维码信息
			OutputParam queryQROutput = qrCodeService.queryQRCodeInfo(queryQRInput);

			// logger.info(logPrefix+ " 查询二维码信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "查询二维码信息失败");
				return outputParam;
			}

			// logger.info("------" + logPrefix + " 查询二维码信息成功------");

			// 获取查询记录
			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();

			// 二维码生成时间
			String generatQRDate = String.format("%s", queryQRMap.get("createTime"));
			// 卡号
			String cardNo = String.format("%s", queryQRMap.get("cardNo"));
			// 姓名
			String customName = String.format("%s", queryQRMap.get("customName"));
			// 设备号
			String deviceNumber = String.format("%s", queryQRMap.get("deviceNumber"));
			// 设备号
			String status = String.format("%s", queryQRMap.get("status"));

			// 二维码已失效
			if (StringConstans.QRCodeStatus.DISABLE.equals(status)) {
				logger.error(logPrefix + " 二维码信息已失效");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码信息已失效");
				return outputParam;
			}

			// 二维码失效标志
			boolean isValidate = false;
			// 二维码生成时间
			long generatQRDateVal = DateUtil.parse(generatQRDate, DateUtil.YYYYMMDDHHMMSS).getTime();
			// 当前时间
			long nowDateVal = (new Date()).getTime();
			// 二维码的有效分钟
			long qrExpire = Long.valueOf(Constants.getParam("qrcode_expire"));
			// 判断二维码是否超过有效期
			if (nowDateVal - generatQRDateVal > qrExpire * 60 * 1000) {
				isValidate = true;
			}

			// 二维码码失效
			if (isValidate) {

				logger.debug(logPrefix + " 二维码信息失效");

				// 更新二维码参数
				InputParam upateQRInput = new InputParam();
				upateQRInput.putparamString("ewmData", qrCodeProClaimed);
				upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

				logger.debug("手机或移动前端解密手机二维码信息[面对面转账]  开始更新二维码信息 请求信息" + upateQRInput.toString());

				// 更新二维码信息
				OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

				// logger.info(logPrefix + " 更新二维码信息完成");

				if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
					logger.debug(logPrefix + " 更新二维码信息失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", logPrefix + " 更新二维码信息失败");
					return outputParam;
				}

				// logger.info("-------" + logPrefix + " 更新二维码信息成功-------");

				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该二维码已失效");

				return outputParam;

			}

			// 二维码被扫以后置为失效状态
			InputParam upateQRInput = new InputParam();
			upateQRInput.putparamString("ewmData", qrCodeProClaimed);
			upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

			logger.debug("手机或移动前端解密手机二维码信息[面对面转账]  开始更新二维码已失效信息 请求信息" + upateQRInput.toString());

			// 更新二维码信息
			OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

			// logger.info(logPrefix + " 更新二维码已失效信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
				logger.debug(logPrefix + " 更新二维码失效信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 更新二维码失效信息失败");
				return outputParam;
			}

			// logger.info("-------" + logPrefix + " 更新二维码失效信息成功-------");

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("cardNo", cardNo);
			outputParam.putValue("customName", customName);
			outputParam.putValue("deviceNumber", deviceNumber);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "解密二维码信息成功");

		} catch (Exception e) {
			logger.error(logPrefix + " 解密二维码信息异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置系统异常"+e.getMessage());

		}
		logger.info("[手机或移动前端解密手机二维码信息[面对面转账] ]   mobileDecrytMobileQRcode 方法结束  返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 查询订单状态[POSP查询订单状态]
	 * 
	 * @param intPutParam
	 * @return
	 */
	@Override
	public OutputParam pospQueryState(InputParam inputParam) {
		logger.info("查询订单状态[POSP查询订单状态] pospQueryState START "+inputParam.toString());
		OutputParam outputParam = new OutputParam();
		try {

			OutputParam valiOut = Validation.validate(inputParam, POSPValidation.vali_POSPQuery ,"POSP订单查询");
			if(!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())){
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}
			
			String channel = String.format("%s", inputParam.getValue(Dict.channel));
			String payAccessType = String.format("%s", inputParam.getValue(Dict.payAccessType));
			String transType = String.format("%s", inputParam.getValue("transType"));
			String orderNumber = String.format("%s", inputParam.getValue("orderNumber"));
			String orderTime = String.format("%s", inputParam.getValue("orderTime"));
			String merOrDt = orderTime.substring(0, 8);
			String merOrTm = orderTime.substring(8);
			
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)
					&& !StringConstans.CHANNEL.CHANNEL_ONLINE_PAY.equals(channel)) {
				logger.debug("[查询订单状态]：渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确"+channel);
				return outputParam;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType)
					&& !StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)
					&& !StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.debug("[查询订单状态]：支付接入类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付接入类型不正确"+payAccessType);
				return outputParam;
			}

			if (!StringConstans.TransType.TRANS_QUERY_ORDER.equals(transType)) {
				logger.debug("[查询订单状态]：交易类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易类型不正确"+transType);
				return outputParam;
			}

			logger.debug("[查询订单状态] ,查询订单信息 orderNumber:[" + orderNumber + "],orderTime:[" + orderTime + "]");

			InputParam queryOrderParam = new InputParam();
			queryOrderParam.putparamString("merOrderId", orderNumber);
			queryOrderParam.putparamString("merOrDt", merOrDt);
			queryOrderParam.putparamString("merOrTm", merOrTm);

			logger.debug(" [查询订单状态] 开始查询订单信息");
			OutputParam orderOutput = orderService.queryOrder(queryOrderParam);
			logger.debug(" [查询订单状态] 查询订单信息完成,返回报文:" + orderOutput.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(orderOutput.getReturnCode())) {
				logger.debug("[查询订单状态]：该订单不存在");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该订单不存在");
				return outputParam;
			}

			Map<String, Object> orderMap = orderOutput.getReturnObj();

			String merId = String.format("%s", orderMap.get("merId"));
			String tradeMoney = String.format("%s", orderMap.get("tradeMoney"));
			String currencyCode = String.format("%s", orderMap.get("currencyCode"));
			String txnSeqId = String.format("%s", orderMap.get("txnSeqId"));
			String txnDt = String.format("%s", orderMap.get("txnDt"));
			String txnTm = String.format("%s", orderMap.get("txnTm"));
			String txnTime = String.format("%s%s", txnDt, txnTm);
			String payType = String.format("%s", orderMap.get("payType"));
			String orderState = String.format("%s", orderMap.get("txnSta"));
			String resDesc = String.format("%s", orderMap.get("resDesc"));
			String initPayAccessType = String.format("%s", orderMap.get("payAccessType"));

			outputParam.putValue("wxPayTime", "");
			outputParam.putValue("wxOrderNo", "");

			if (!payAccessType.equals(initPayAccessType)) {
				logger.error("[查询订单状态]：原始接入类型和查询的接入类型不匹配");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "原始接入类型和查询的接入类型不匹配");
				return outputParam;
			}

			if (StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType)) {
				logger.debug("判断为丰收互联查询交易");
				// 如果是pos扫丰收互联 交易状态为01 就去丰收互联查询
				if (StringConstans.OrderState.STATE_01.equals(orderState)
						&& StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {

					Map<String, String> querymap = new HashMap<String, String>();
					querymap.put("ip", Constants.getParam("internet_lead_ip"));
					querymap.put("port", Constants.getParam("internet_lead_port"));

					Map<String, Object> xmlMap = new HashMap<String, Object>();
					xmlMap.put("txnSeqId", ObjectUtils.toString(txnSeqId));
					xmlMap.put("reqCode", StringConstans.OutSystemServiceCode.QUERY_FSHL_ORDER);
					String sendXmlStr = String.format("%06d%s", Util.mapToXml(xmlMap).getBytes("UTF-8").length,
							Util.mapToXml(xmlMap));

					querymap.put("sendXmlStr", sendXmlStr);

					logger.info("*****************POS扫丰收互联二维码订单状态查询  开始*******************" + querymap.toString());
					OutputParam bhOutput = queryFshlOrder(querymap);
					logger.info("*****************POS扫丰收互联二维码订单状态查询  结束*******************" + bhOutput.toString());

					orderState = StringUtil.toString(bhOutput.getValue("respCode"));
					resDesc = StringUtil.toString(bhOutput.getValue("respDesc"));

					if (!StringConstans.OrderState.STATE_01.equals(orderState)) {
						InputParam updateInput = new InputParam();
						updateInput.putParams("txnSta", orderState);
						updateInput.putParams("resDesc", resDesc);
						updateInput.putParams("txnSeqId", txnSeqId);
						updateInput.putParams("txnDt", txnDt);
						updateInput.putParams("txnTm", txnTm);
						orderService.updateWxOrderInfo(updateInput);
					}
				}
				outputParam.putValue("respCode", orderState);
				outputParam.putValue("respDesc", resDesc);

			} else if (StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				logger.debug("判断为微信查询交易");
				this.handWxQueryBack(orderMap, outputParam);

			} else if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.debug("判断为支付宝查询交易");
				this.handAlipayQueryBack(orderMap, outputParam);
			}

			outputParam.putValue("merId", merId);
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderAmount", tradeMoney);
			outputParam.putValue("transType", transType);
			outputParam.putValue("currencyCode", currencyCode);
			outputParam.putValue("payType", payType);
			outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.putValue("txnTime", txnTime);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (Exception e) {
			logger.error("[查询订单状态]：查询订单出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置系统异常"+ e.getMessage());
		}
		logger.info("查询订单状态[POSP查询订单状态] pospQueryState END "+outputParam.toString());
		return outputParam;

	}
	
	
	public OutputParam refund(InputParam inputParam) {
		logger.info("退款请求START,请求报文:"+inputParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(inputParam, POSPValidation.vali_POSPRefund ,"退款");
			if(!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())){
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, valiOut.getReturnMsg());
				return outputParam;
			}
			
			String merId = StringUtil.toString(inputParam.getValue(Dict.merId));
			String initTxnSeqId = StringUtil.toString(inputParam.getValue(Dict.initTxnSeqId));
			String initOrderNumber = StringUtil.toString(inputParam.getValue(Dict.initOrderNumber));
			String outRefundNo = StringUtil.toString(inputParam.getValue(Dict.outRefundNo));
			String outRefundTime = StringUtil.toString(inputParam.getValue(Dict.outRefundTime));
			String refundAmount = StringUtil.toString(inputParam.getValue(Dict.refundAmount));
			String refundReason = StringUtil.toString(inputParam.getValue(Dict.refundReason));
			
			/**参数校验 start*/
			if(StringUtil.isEmpty(initTxnSeqId) && StringUtil.isEmpty(initOrderNumber)){
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "initTxnSeqId和initOrderNumber不能同时为空");
				return outputParam;
			}
			
			if (!refundAmount.matches("\\d{12}")) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "refundAmount格式错误");
				return outputParam;
			}

			if (new BigDecimal(refundAmount).compareTo(new BigDecimal(0)) <= 0) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "refundAmount不能小于0");
				return outputParam;
			}

			if (!outRefundTime.matches("\\d{14}")) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "outRefundTime格式错误");
				return outputParam;
			}
			/**参数校验 end*/
			
			/**判断是否已存在该笔退款订单 start*/
			InputParam queryRefundOrder = new InputParam();
			queryRefundOrder.putparamString(Dict.merId, merId);
			queryRefundOrder.putparamString(Dict.merOrderId, outRefundNo);
			OutputParam orderOutputRefund = orderService.queryOrder(queryRefundOrder);
			if (StringConstans.returnCode.SUCCESS.equals(orderOutputRefund.getReturnCode())) {
				String refundTxnSta = StringUtil.toString(orderOutputRefund.getValue(Dict.txnSta));
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "已存在该笔退款订单，并且该笔退款订单状态为:"+refundTxnSta);
				return outputParam;
			}
			/**查询不存在 去月表查询*/
			OutputParam orderOutputRefundMonth = orderService.queryOrderMonth(queryRefundOrder);
			if (StringConstans.returnCode.SUCCESS.equals(orderOutputRefundMonth.getReturnCode())) {
				String refundTxnSta = StringUtil.toString(orderOutputRefundMonth.getValue(Dict.txnSta));
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "已存在该笔退款订单，并且该笔退款订单状态为:"+refundTxnSta);
				return outputParam;
			}
			
			/**判断是否已存在该笔退款订单 end*/
			
			/**原订单查询 start*/
			InputParam queryOrderParam = new InputParam();
			queryOrderParam.putparamString(Dict.merId, merId);
			if(!StringUtil.isEmpty(initTxnSeqId)){
				queryOrderParam.putparamString(Dict.txnSeqId, initTxnSeqId);
			} else {
				queryOrderParam.putparamString(Dict.merOrderId, initOrderNumber);
			}
			OutputParam orderOutput = orderService.queryOrder(queryOrderParam);
			if (!StringConstans.returnCode.SUCCESS.equals(orderOutput.getReturnCode())) {
				/**查询不存在 去月表查询*/
				orderOutput = orderService.queryOrderMonth(queryOrderParam);
				if (!StringConstans.returnCode.SUCCESS.equals(orderOutput.getReturnCode())) {
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "原订单信息不存在");
					return outputParam;
				}
			}
			/**原订单查询 end*/
			
			String totalRefundFee = StringUtil.toString(orderOutput.getValue(Dict.totalRefundFee));
			String tradeMoney = StringUtil.toString(orderOutput.getValue(Dict.tradeMoney));
			String payAccessType = StringUtil.toString(orderOutput.getValue(Dict.payAccessType));
			String txnType = StringUtil.toString(orderOutput.getValue(Dict.txnType));
			String txnChannel = StringUtil.toString(orderOutput.getValue(Dict.txnChannel));
			String initTxnSta = StringUtil.toString(orderOutput.getValue(Dict.txnSta));
			String initTradeMoney = StringUtil.toString(orderOutput.getValue(Dict.tradeMoney));
			String initTxnTime = StringUtil.toString(orderOutput.getValue(Dict.txnDt))+StringUtil.toString(orderOutput.getValue(Dict.txnTm));
			//防止传入为空时，所以进行重新赋值
			initTxnSeqId = StringUtil.toString(orderOutput.getValue(Dict.txnSeqId));
			initOrderNumber = StringUtil.toString(orderOutput.getValue(Dict.merOrderId));
			
			/**原订单校验 start*/
			if (!StringConstans.TransType.TRANS_CONSUME.equals(txnType)) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "原订单不是消费订单，不允许退款");
				return outputParam;
			}
			
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(txnChannel)) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "原订单渠道错误，仅提供POSP渠道退款");
				return outputParam;
			}
			
			if (StringConstans.OrderState.STATE_03.equals(initTxnSta)) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "原订单交易失败，不允许退款");
				return outputParam;
			}
			//原交易订单日期+30天后
			Date initTxnTimeAdd30 =DateUtil.addDay(DateUtil.parse(initTxnTime, DateUtil.YYYYMMDDHHMMSS), 30);
			Date now = DateUtil.now();
			if(now.compareTo(initTxnTimeAdd30)>0) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "原订单交易超过30天，不允许退款");
				return outputParam;
			}
			
			BigDecimal totalRefundFeeBig = new BigDecimal(StringUtil.toString(totalRefundFee,0));
			BigDecimal refundAmountBig = new BigDecimal(StringUtil.toString(refundAmount,0));
			BigDecimal tradeMoneyBig = new BigDecimal(StringUtil.toString(tradeMoney,0));
			if(totalRefundFeeBig.add(refundAmountBig).compareTo(tradeMoneyBig) >0){
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "本次退款金额加已退金额已超过原订单金额");
				return outputParam;
			}
			/**原订单校验 end*/
			
			/*** 查询商户信息 start*/
			InputParam subMerQuery = new InputParam();
			subMerQuery.putparamString(Dict.merId, merId);
			String subMerchant = "";
			if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
				subMerQuery.putparamString(Dict.subMerchant, StringUtil.toString(orderOutput.getValue(Dict.subAlipayMerId)));
				subMerQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
				subMerchant = StringUtil.toString(orderOutput.getValue(Dict.subAlipayMerId));
			} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
				subMerQuery.putparamString(Dict.subMerchant, StringUtil.toString(orderOutput.getValue(Dict.subWxMerId)));
				subMerQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
				subMerchant = StringUtil.toString(orderOutput.getValue(Dict.subWxMerId));
			} else {
				//其它渠道
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "非微信支付宝交易不能退款");
				return outputParam;
			}
			
			OutputParam subMerOutput =merchantChannelService.querySubmerChannelRateInfo(subMerQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(subMerOutput.getReturnCode())) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "商户信息查询失败，请检查商户号");
				return outputParam;
			}
			String connectMethod = StringUtil.toString(subMerOutput.getValue(Dict.connectMethod));
			String rateChannel = StringUtil.toString(subMerOutput.getValue(Dict.rate));
			if(!StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "直连商户不允许退款");
				return outputParam;
			}
			/*** 查询商户信息 end*/
			String syncMerId = getSyncMerId(merId);
		
			String refundTxnSeqId = orderService.getOrderNo(txnChannel);
			String txnDt = DateUtil.format(now, DateUtil.YYYYMMDD);
			String txnTm = DateUtil.format(now, DateUtil.HHMMSS);
			synchronized (syncMerId) {
				/**查询校验商户可退款金额start*/
				subMerQuery.putparamString(Dict.txnDt,  DateUtil.format(new Date(), DateUtil.YYYYMMDD));
				OutputParam merEnableMoneyParam = orderService.queryMerEnableMoneyByDay(subMerQuery);
				if (!StringConstans.returnCode.SUCCESS.equals(merEnableMoneyParam.getReturnCode())) {
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, merEnableMoneyParam.getReturnMsg());
					return outputParam;
				}
				BigDecimal enableMoney = BigDecimal.ZERO;
				if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
					String aliEnableMoney = StringUtil.toString(merEnableMoneyParam.getValue("aliEnableMoney"),"0");
					enableMoney = new BigDecimal(aliEnableMoney);
				} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
					String wxEnableMoney = StringUtil.toString(merEnableMoneyParam.getValue("wxEnableMoney"),"0");
					enableMoney = new BigDecimal(wxEnableMoney);
				} 
				if(refundAmountBig.compareTo(enableMoney) >0){
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "本次退款金额已超过商户可退款金额");
					return outputParam;
				}
				/**查询校验商户可退款金额end*/
				
				/**新增退款订单 start*/
				
				String oglOrdDate = StringUtil.toString(orderOutput.getValue(Dict.txnDt))+StringUtil.toString(orderOutput.getValue(Dict.txnTm));
				String merOrDt = outRefundTime.substring(0, 8);
				String merOrTm =  outRefundTime.substring(8, 14);
				String payType = StringUtil.toString(orderOutput.getValue(Dict.payType));
				String currencyCode = StringUtil.toString(orderOutput.getValue(Dict.currencyCode));
				String resDesc = "退款订单初始化";
				InputParam orderInput = new InputParam();
				orderInput.putparamString(Dict.txnSeqId, refundTxnSeqId);
				orderInput.putparamString(Dict.txnDt, txnDt);
				orderInput.putparamString(Dict.txnTm, txnTm);
				orderInput.putparamString(Dict.oglOrdId, initTxnSeqId);
				orderInput.putparamString(Dict.oglOrdDate, oglOrdDate);
				orderInput.putparamString(Dict.merOrderId, outRefundNo);
				orderInput.putparamString(Dict.merOrDt, merOrDt);
				orderInput.putparamString(Dict.merOrTm, merOrTm);
				orderInput.putparamString(Dict.txnType, StringConstans.TransType.TRANS_RETURN_GOODS);
				orderInput.putparamString(Dict.txnChannel, txnChannel);
				orderInput.putparamString(Dict.payAccessType, payAccessType);
				orderInput.putparamString(Dict.tradeMoney, refundAmount);
				orderInput.putparamString(Dict.merId, merId);
				if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
					orderInput.putparamString(Dict.alipayMerId,StringUtil.toString(orderOutput.getValue(Dict.alipayMerId)));
					orderInput.putparamString(Dict.subAlipayMerId, subMerchant);
				} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
					orderInput.putparamString(Dict.wxMerId,StringUtil.toString(orderOutput.getValue(Dict.wxMerId)));
					orderInput.putparamString(Dict.subWxMerId, subMerchant);
				} 
				
				orderInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_01);
				orderInput.putparamString(Dict.payType, payType);
				orderInput.putparamString(Dict.currencyCode, currencyCode);
				orderInput.putparamString(Dict.resDesc,resDesc);
				orderInput.putparamString(Dict.refundAmount,refundAmount);
				orderInput.putparamString(Dict.outRequestNo,refundTxnSeqId);
				orderInput.putparamString(Dict.initTradeMoney,initTradeMoney);
				orderInput.putparamString(Dict.remark,refundReason);
				
				OutputParam orderOutPut = orderService.insertOrder(orderInput);
				if (!StringConstans.returnCode.SUCCESS.equals(orderOutPut.getReturnCode())) {
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "退款订单新增失败");
					return outputParam;
				}
				/**新增退款订单 end*/
			}
			inputParam.putParams(Dict.rateChannel, rateChannel);
			inputParam.putParams(Dict.txnSeqId, refundTxnSeqId);
			inputParam.putParams(Dict.initTxnSta, initTxnSta);
			inputParam.putParams(Dict.initTxnSeqId, initTxnSeqId);
			inputParam.putParams(Dict.initTxnTime, initTxnTime);
			inputParam.putParams(Dict.subMerchant, subMerchant);
			inputParam.putParams(Dict.initTotalRefundFee, totalRefundFee);
			inputParam.putParams(Dict.initTradeMoney, initTradeMoney);
			
			/**分渠道进行退款 start*/
			OutputParam outRefund = new OutputParam();
			if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
				outRefund = aliPayPayService.refundOrder(inputParam);
			} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
				outRefund = wxPayService.refundOrderYL(inputParam);
			}
			/**分渠道进行退款 end*/
			
			/**组装返回报文 start*/
			outputParam.putValue(Dict.refundStatus, outRefund.getValue(Dict.refundStatus));
			outputParam.putValue(Dict.msg, outRefund.getValue(Dict.msg));
			outputParam.putValueRemoveNull(Dict.txnSeqId, refundTxnSeqId);
			outputParam.putValueRemoveNull(Dict.txnTime, txnDt+txnTm);
			outputParam.putValueRemoveNull(Dict.outRefundNo, outRefundNo);
			outputParam.putValueRemoveNull(Dict.refundAmount, refundAmount);
			outputParam.putValueRemoveNull(Dict.initTxnSeqId, initTxnSeqId);
			outputParam.putValueRemoveNull(Dict.initOrderNumber, initOrderNumber);
			/**组装返回报文 end*/
		} catch (Exception e) {
			logger.error("退款请求出现异常:" + e.getMessage(), e);
			outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
			outputParam.putValue(Dict.msg, "退款请求出现异常");
		} finally {
			logger.info("退款请求START,返回报文:"+outputParam.toString());
		}
		return outputParam;

	}
	
	private synchronized String getSyncMerId(String merId) {
		String syncMerId = lruCache.get(merId);
		if(syncMerId==null) {
			lruCache.put(merId, merId);
			return merId;
		}
		return syncMerId;
	}
	
	public OutputParam refundQuery(InputParam inputParam) {
		logger.info("退款查询请求START,请求报文:"+inputParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(inputParam, POSPValidation.vali_POSPRefundQuery ,"退款查询");
			if(!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())){
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, valiOut.getReturnMsg());
				return outputParam;
			}
			
			String merId = StringUtil.toString(inputParam.getValue(Dict.merId));
			String txnSeqId = StringUtil.toString(inputParam.getValue(Dict.txnSeqId));
			String outRefundNo = StringUtil.toString(inputParam.getValue(Dict.outRefundNo));
			
			/**参数校验 start*/
			if(StringUtil.isEmpty(txnSeqId) && StringUtil.isEmpty(txnSeqId)){
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "txnSeqId和outRefundNo不能同时为空");
				return outputParam;
			}
			
			/**原退款订单查询 start*/
			InputParam queryOrderParam = new InputParam();
			queryOrderParam.putparamString(Dict.merId, merId);
			if(!StringUtil.isEmpty(txnSeqId)){
				queryOrderParam.putparamString(Dict.txnSeqId, txnSeqId);
			} else {
				queryOrderParam.putparamString(Dict.merOrderId, outRefundNo);
			}
			OutputParam orderOutput = orderService.queryOrder(queryOrderParam);
			if (!StringConstans.returnCode.SUCCESS.equals(orderOutput.getReturnCode())) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "原订单信息不存在");
				return outputParam;
			}
			/**原退款订单查询 end*/
			
			String txnDt = StringUtil.toString(orderOutput.getValue(Dict.txnDt));
			String txnTm = StringUtil.toString(orderOutput.getValue(Dict.txnTm));
			String refundAmount = StringUtil.toString(orderOutput.getValue(Dict.refundAmount));
			String oglOrdId = StringUtil.toString(orderOutput.getValue(Dict.oglOrdId));
			String oglOrdDate = StringUtil.toString(orderOutput.getValue(Dict.oglOrdDate));
			String remark = StringUtil.toString(orderOutput.getValue(Dict.remark));
			String txnSta = StringUtil.toString(orderOutput.getValue(Dict.txnSta));
			String resDesc = StringUtil.toString(orderOutput.getValue(Dict.resDesc));
			String payAccessType = StringUtil.toString(orderOutput.getValue(Dict.payAccessType));
			String txnType = StringUtil.toString(orderOutput.getValue(Dict.txnType));
			txnSeqId = StringUtil.toString(orderOutput.getValue(Dict.txnSeqId));
			outRefundNo = StringUtil.toString(orderOutput.getValue(Dict.merOrderId));
			
			/**原退款订单校验 start*/
			if(!StringConstans.TransType.TRANS_RETURN_GOODS.equals(txnType)){
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "该笔不是退款订单");
				return outputParam;
			}
			
			/**明确成功与失败  直接返回结果*/
			if(StringConstans.RefundStatus.STATUS_02.equals(txnSta)||StringConstans.RefundStatus.STATUS_03.equals(txnSta)) {
				outputParam.putValue(Dict.refundStatus, txnSta);
				outputParam.putValue(Dict.msg, resDesc);
				outputParam.putValueRemoveNull(Dict.txnSeqId, txnSeqId);
				outputParam.putValueRemoveNull(Dict.txnTime, txnDt+txnTm);
				outputParam.putValueRemoveNull(Dict.outRefundNo, outRefundNo);
				outputParam.putValueRemoveNull(Dict.refundAmount, refundAmount);
				outputParam.putValueRemoveNull(Dict.initTxnSeqId, oglOrdId);
				outputParam.putValueRemoveNull(Dict.refundReason, remark);
				return outputParam;
			}
			
			/**原退款退款订单校验 end*/
			
			/**原交易订单查询 start*/
			InputParam inintqueryOrderParam = new InputParam();
			inintqueryOrderParam.putparamString(Dict.merId, merId);
			inintqueryOrderParam.putparamString(Dict.txnSeqId, oglOrdId);
			OutputParam initorderOutput = orderService.queryOrder(inintqueryOrderParam);
			if (!StringConstans.returnCode.SUCCESS.equals(initorderOutput.getReturnCode())) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "原交易订单信息不存在");
				return outputParam;
			}
			/**原交易订单查询 end*/
			String totalRefundFee = StringUtil.toString(initorderOutput.getValue(Dict.totalRefundFee),"0");
			String tradeMoney = StringUtil.toString(initorderOutput.getValue(Dict.tradeMoney));
			
			/*** 查询商户信息 start*/
			InputParam subMerQuery = new InputParam();
			subMerQuery.putparamString(Dict.merId, merId);
			String subMerchant = "";
			if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
				subMerQuery.putparamString(Dict.subMerchant, StringUtil.toString(orderOutput.getValue(Dict.subAlipayMerId)));
				subMerQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
				subMerchant = StringUtil.toString(orderOutput.getValue(Dict.subAlipayMerId));
			} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
				subMerQuery.putparamString(Dict.subMerchant, StringUtil.toString(orderOutput.getValue(Dict.subWxMerId)));
				subMerQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
				subMerchant = StringUtil.toString(orderOutput.getValue(Dict.subWxMerId));
			} else {
				//其它渠道
			}
			
			OutputParam subMerOutput =merchantChannelService.querySubmerChannelRateInfo(subMerQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(subMerOutput.getReturnCode())) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "商户信息查询失败，请检查商户号");
				return outputParam;
			}
			String connectMethod = StringUtil.toString(subMerOutput.getValue(Dict.connectMethod));
			String rateChannel = StringUtil.toString(subMerOutput.getValue(Dict.rate));
			if(!StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
				outputParam.putValue(Dict.msg, "直连商户不允许退款");
				return outputParam;
			}
			/*** 查询商户信息 end*/

			
			/**分渠道进行退款查询 start*/
			InputParam queryRefundInput = new InputParam();
			queryRefundInput.putParams(Dict.txnSeqId, txnSeqId);
			queryRefundInput.putParams(Dict.rateChannel, rateChannel);
			queryRefundInput.putParams(Dict.initTxnSeqId, oglOrdId);
			queryRefundInput.putParams(Dict.initTxnTime, oglOrdDate);
			queryRefundInput.putParams(Dict.tradeMoney, tradeMoney);
			queryRefundInput.putParams(Dict.refundAmount, refundAmount);
			queryRefundInput.putParams(Dict.initTotalRefundFee, totalRefundFee);
			OutputParam outRefund = new OutputParam();
			if(!StringConstans.RefundStatus.STATUS_02.equals(txnSta) && !StringConstans.RefundStatus.STATUS_03.equals(txnSta)){
				if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
					outRefund = aliPayPayService.queryRefundOrder(queryRefundInput);
				} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
					queryRefundInput.putParams(Dict.subMerchant, subMerchant);
					outRefund = wxPayService.queryRefundOrderYL(queryRefundInput);
				}
				txnSta = StringUtil.toString(outRefund.getValue(Dict.refundStatus));
				resDesc = StringUtil.toString(outRefund.getValue(Dict.msg));
			}
			/**分渠道进行退款查询 end*/
			
			/**组装返回报文 start*/
			outputParam.putValue(Dict.refundStatus, txnSta);
			outputParam.putValue(Dict.msg, resDesc);
			outputParam.putValueRemoveNull(Dict.txnSeqId, txnSeqId);
			outputParam.putValueRemoveNull(Dict.txnTime, txnDt+txnTm);
			outputParam.putValueRemoveNull(Dict.outRefundNo, outRefundNo);
			outputParam.putValueRemoveNull(Dict.refundAmount, tradeMoney);
			outputParam.putValueRemoveNull(Dict.initTxnSeqId, oglOrdId);
			outputParam.putValueRemoveNull(Dict.refundReason, remark);
			/**组装返回报文 end*/
		} catch (Exception e) {
			logger.info("退款查询请求出现异常" + e.getMessage(), e);
			outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
			outputParam.putValue(Dict.msg, "退款查询请求出现异常");
		} finally {
			logger.info("退款查询请求END,返回报文:"+outputParam.toString());
		}
		return outputParam;
		
	}
	
	

	/**
	 * @param orderMap
	 * @param outputParam 处理微信查询返回
	 */
	public void handWxQueryBack(Map<String, Object> orderMap, OutputParam outputParam) {
		logger.info("开始处理微信查询交易,请求报文:" + orderMap.toString());
		// 商户号
		String merId = String.format("%s", orderMap.get("merId"));
		// 二维码前置生成订单号
		String txnSeqId = String.format("%s", orderMap.get("txnSeqId"));
		// 二维码前置生成订单日期
		String txnDt = String.format("%s", orderMap.get("txnDt"));
		// 二维码前置生成订单时间
		String txnTm = String.format("%s", orderMap.get("txnTm"));
		// 交易类型
		String txnType = String.format("%s", orderMap.get("txnType"));
		// 订单状态
		String orderState = String.format("%s", orderMap.get("txnSta"));
		// 交易状态描述
		String resDesc = String.format("%s", orderMap.get("resDesc"));
		// 交易状态描述
		String channel = String.format("%s", orderMap.get("txnChannel"));
		// 原交易订单号
		String oglOrdId = String.format("%s", orderMap.get("oglOrdId"));
		// 原交易订单时间
		String oglOrdDate = String.format("%s", orderMap.get("oglOrdDate"));
		// 微信订单号
		String wxOrderNo = StringUtil.isEmpty(orderMap.get("wxOrderNo")) ? "" : orderMap.get("wxOrderNo").toString();
		// 微信子商户号
		String subMchId = StringUtil.isEmpty(orderMap.get("subWxMerId")) ? "" : orderMap.get("subWxMerId").toString();
		// 微信支付时间
		String wxPayTime = StringUtil.isEmpty(orderMap.get("settleDate")) ? "" : orderMap.get("settleDate").toString();

		if (StringConstans.OrderState.STATE_06.equals(orderState)
				|| StringConstans.OrderState.STATE_01.equals(orderState)
				|| StringConstans.OrderState.STATE_04.equals(orderState)) {
			logger.debug("交易状态为:" + orderState + ",需要去微信查询订单状态");
			// 微信类交易需要发起到微信的订单查询
			InputParam querParam = new InputParam();
			querParam.putParams("txnSeqId", txnSeqId);
			querParam.putParams("subMchId", subMchId);
			querParam.putParams("txnDt", txnDt);
			querParam.putParams("txnTm", txnTm);
			querParam.putParams("channel", channel);
			querParam.putParams(Dict.merId, merId);

			// 如果是撤销交易，则使用原交易信息去查
			if (StringConstans.TransType.TRANS_REVOKE.equals(txnType)) {
				querParam.putParams("txnSeqId", oglOrdId);
				querParam.putParams("txnDt", oglOrdDate);
			}

			logger.debug("[交易查询] 调用微信订单状态查询接口  开始");
			
			OutputParam routing = wxMerchantSynchService.routing(querParam);
			String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
			OutputParam queryOut = null;
			querParam.putParams(Dict.rate, routing.getValue(Dict.rate));
			if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				//间连
				queryOut = wxPayService.queryWxOrderYL(querParam);
			} else {
				//直连
				queryOut = wxPayService.queryWxOrder(querParam);
			}
			
			logger.debug("[交易查询] 调用微信订单状态查询接口  结束,返回报文:" + queryOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				orderState = StringConstans.OrderState.STATE_01;
			} else {
				String tradeSta = queryOut.getValue("txnSta").toString();
				orderState = tradeSta.equals(StringConstans.OrderState.STATE_06) ? StringConstans.OrderState.STATE_01
						: tradeSta;
				wxPayTime = StringUtil.toString(queryOut.getValue("settleDate"));// 清算时间
				wxOrderNo = StringUtil.toString(queryOut.getValue("wxOrderNo"));// 微信订单号
				resDesc = queryOut.getValue(Dict.respDesc).toString();

				if (StringConstans.TransType.TRANS_REVOKE.equals(txnType)
						&& StringConstans.OrderState.STATE_15.equals(tradeSta)) {
					orderState = StringConstans.OrderState.STATE_02;
					tradeSta = StringConstans.OrderState.STATE_02;
					resDesc = "撤销成功";
				}

				// 更新订单信息
				logger.debug("[交易查询] 查询完成更新订单状态");
				InputParam updateInput = new InputParam();
				updateInput.putParams("txnSta", tradeSta);
				updateInput.putParams(Dict.respDesc, resDesc);
				updateInput.putParams("txnSeqId", txnSeqId);
				updateInput.putParams("txnDt", txnDt);
				updateInput.putParams("txnTm", txnTm);
				updateInput.putParams("settleDate", wxPayTime);
				if (!StringUtil.isEmpty(wxOrderNo)) {
					updateInput.putParams("wxOrderNo", wxOrderNo);
				}

				OutputParam orderOut = orderService.updateWxOrderInfo(updateInput);
				logger.debug("更新订单返回报文:" + orderOut.toString());
			}
		} else {
			logger.debug("交易状态为:" + orderState + ",不需要去微信查询订单状态");
		}

		outputParam.putValue("respCode", orderState);
		outputParam.putValue("respDesc", resDesc);
		outputParam.putValue("wxPayTime", wxPayTime);
		outputParam.putValue("wxOrderNo", wxOrderNo);

	}

	/**
	 * 处理支付宝查询返回
	 * 
	 * @param orderMap
	 * @param outputParam
	 */
	public void handAlipayQueryBack(Map<String, Object> orderMap, OutputParam outputParam) {

		String txnSeqId = String.format("%s", orderMap.get(Dict.txnSeqId));
		String txnDt = String.format("%s", orderMap.get(Dict.txnDt));
		String txnTm = String.format("%s", orderMap.get(Dict.txnTm));
		String txnType = String.format("%s", orderMap.get(Dict.txnType));
		String orderState = String.format("%s", orderMap.get(Dict.txnSta));
		String oglOrdId = String.format("%s", orderMap.get(Dict.oglOrdId));
		String oglOrdDate = String.format("%s", orderMap.get(Dict.oglOrdDate));
		String payType = String.format("%s", orderMap.get(Dict.payType));
		String alipayTradeNo = String.format("%s", orderMap.get(Dict.alipayTradeNo));
		String alipayPayTime = String.format("%s", orderMap.get(Dict.settleDate));
		String resDesc = String.format("%s", orderMap.get(Dict.resDesc));
		String subAlipayMerId = String.format("%s", orderMap.get(Dict.subAlipayMerId));
		String merId = String.format("%s", orderMap.get(Dict.merId));

		outputParam.putValue("respCode", orderState);
		outputParam.putValue("respDesc", resDesc);

		if (StringConstans.OrderState.STATE_01.equals(orderState)
				|| StringConstans.OrderState.STATE_04.equals(orderState)
				|| StringConstans.OrderState.STATE_06.equals(orderState)
				|| StringConstans.OrderState.STATE_08.equals(orderState)
				|| StringConstans.OrderState.STATE_10.equals(orderState)) {

			InputParam queryInput = new InputParam();
			queryInput.putParams("outTradeNo", txnSeqId + txnDt + txnTm);
			queryInput.putParams("alipayTradeNo", alipayTradeNo);
			queryInput.putParams("subAlipayMerId", subAlipayMerId);
			queryInput.putParams("merId", merId);

			// 如果是撤销交易，则使用原交易信息去查
			if (StringConstans.TransType.TRANS_REVOKE.equals(txnType)) {
				queryInput.putParams("outTradeNo", oglOrdId + oglOrdDate);
			}

			logger.info("[交易查询] 开始支付宝订单查询");
			OutputParam queryOutput = aliPayPayService.queryALipayOrder(queryInput);
			logger.debug("[交易查询] 完成支付宝订单查询,返回报文:" + queryOutput.toString());

			// 出现异常或者去支付宝查询失败默认交易正在处理，让posp继续查询
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_01);
			outputParam.putValue("respDesc", StringConstans.RespDesc.RESP_DESC_01);

			if (StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())) {
				orderState = String.format("%s", queryOutput.getReturnObj().get("orderSta"));
				resDesc = String.format("%s", queryOutput.getReturnObj().get("orderDesc"));
				alipayTradeNo = String.format("%s", queryOutput.getReturnObj().get("alipayTradeNo"));
				alipayPayTime = String.format("%s", queryOutput.getReturnObj().get("settleDate"));

				// 主扫下单,等待用户付款
				if (StringConstans.TransType.TRANS_CONSUME.equals(txnType)
						&& StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
						&& (StringConstans.OrderState.STATE_01.equals(orderState)
								|| StringConstans.OrderState.STATE_06.equals(orderState)
								|| StringConstans.OrderState.STATE_13.equals(orderState))) {
					logger.info("[交易查询] txnType=" + txnType + ",payType=" + payType + ",orderState=" + orderState);
					logger.info("[交易查询] 预下单成功,等待用户支付,返回响应码 respCode=" + outputParam.getValue("respCode"));
					return;
				}

				// 交易结果为失败或者成功 更新订单 ，未知的交易结果不更新订单
				if (!StringConstans.OrderState.STATE_10.equals(orderState) && !StringUtil.isEmpty(orderState)) {

					InputParam updateInput = new InputParam();
					updateInput.putparamString("txnSeqId", txnSeqId);
					updateInput.putparamString("txnDt", txnDt);
					updateInput.putparamString("txnTm", txnTm);
					updateInput.putparamString("txnSta", orderState);
					updateInput.putparamString("resDesc", resDesc);

					if (!StringConstans.TransType.TRANS_REVOKE.equals(txnType)
							&& !StringConstans.TransType.TRANS_RETURN_GOODS.equals(txnType)) {

						if (!StringUtil.isEmpty(alipayTradeNo)) {
							updateInput.putparamString("alipayTradeNo", alipayTradeNo);
						}

						if (!StringUtil.isEmpty(alipayPayTime)) {
							updateInput.putparamString("settleDate", alipayPayTime);
						}
					}

					// 交易类型为撤销
					if (StringConstans.TransType.TRANS_REVOKE.equals(txnType)) {

						// 原交易02订单成功，表示当前订单撤销失败
						if (StringConstans.OrderState.STATE_02.equals(orderState)) {
							resDesc = "撤销失败";
							orderState = StringConstans.OrderState.STATE_03;
							updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);
							updateInput.putparamString("resDesc", resDesc);
						}

						// 原交易09订单已关闭，表示当前订单已经撤销成功
						if (StringConstans.OrderState.STATE_09.equals(orderState)) {
							resDesc = "撤销成功";
							orderState = StringConstans.OrderState.STATE_02;
							updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_02);
							updateInput.putparamString("resDesc", resDesc);
						}
					}

					logger.info("[交易查询 - 支付宝交易查询] 更新订单结束");

					OutputParam updateOutput = orderService.updateOrder(updateInput);

					logger.info("[交易查询 - 支付宝交易查询] 更新订单结束");

					if (!StringConstans.returnCode.SUCCESS.equals(updateOutput.getReturnCode())) {
						logger.error("[交易查询 - 支付宝交易查询] 更新订单失败");
					}
				}

				// 等待输密码或者买家付款
				if (StringConstans.OrderState.STATE_01.equals(orderState)) {
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_01);
					outputParam.putValue("respDesc", StringConstans.RespDesc.RESP_DESC_01);
				}

				if (StringConstans.OrderState.STATE_02.equals(orderState)) {
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
					outputParam.putValue("respDesc", resDesc);
				}

				if (StringConstans.OrderState.STATE_03.equals(orderState)) {
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", resDesc);
				}

				if (StringConstans.OrderState.STATE_09.equals(orderState)) {
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_09);
					outputParam.putValue("respDesc", StringConstans.RespDesc.RESP_DESC_09);
				}
				// 未知状态，返回正在处理让POSP 继续查询
				if (StringConstans.OrderState.STATE_10.equals(orderState)) {
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_01);
					outputParam.putValue("respDesc", StringConstans.RespDesc.RESP_DESC_01);
				}

				if (StringConstans.OrderState.STATE_11.equals(orderState)) {
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_11);
					outputParam.putValue("respDesc", StringConstans.RespDesc.RESP_DESC_11);
				}
			}
		}

		// 如果交易是成功，按原来的返回，如果交易状态未明查询后赋值返回
		if (!StringConstans.TransType.TRANS_REVOKE.equals(txnType)) {

			if (!StringUtil.isEmpty(alipayTradeNo)) {
				outputParam.putValue("alipayTradeNo", alipayTradeNo);
			}

			if (!StringUtil.isEmpty(alipayPayTime)) {
				outputParam.putValue("alipayPayTime", alipayPayTime);
			}
		}
	}

	/**
	 * 处理手机银行支付通知
	 * 
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam handleMobilePayNotify(InputParam inputParam) {

		logger.info("[处理本行支付通知]  handleMobilePayNotify 传入参数：" + inputParam.toString());
		OutputParam outputParam = new OutputParam();

		try {

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");
			validateParamList.add("txnSta");
			validateParamList.add("orderNumber");
			validateParamList.add("orderTime");
			validateParamList.add("orderDesc");

			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[处理本行支付通知]:请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 手机银行订单号
			String orderNumber = String.format("%s", inputParam.getValue("orderNumber"));
			// 手机银行订单时间
			String orderTime = String.format("%s", inputParam.getValue("orderTime"));
			// 手机银行订单日期
			String mobileOrDt = orderTime.substring(0, 8);
			// 手机银行订单时间
			String mobileOrTm = orderTime.substring(8);

			logger.debug("[处理本行支付通知]:订单信息orderNumber:[" + orderNumber + "],orderTime:[" + orderTime + "]");

			// 二维码前置生成订单号
			String txnSeqId = String.format("%s", inputParam.getValue("txnSeqId"));
			// 二维码前置生成订单时间
			String txnTime = String.format("%s", inputParam.getValue("txnTime"));
			// 订单日期
			String txnDt = txnTime.substring(0, 8);
			// 订单时间
			String txnTm = txnTime.substring(8);

			logger.debug("[处理本行支付通知 ]:二维码前置订单信息txnSeqId:[" + txnSeqId + "],txnTime:[" + txnTime + "]");

			// 订单状态
			String txnSta = String.format("%s", inputParam.getValue("txnSta"));
			// 原订单状态描述
			String orderDesc = String.format("%s", inputParam.getValue("orderDesc"));

			logger.debug("[处理本行支付通知 ]:支付结果 txnSta:[" + txnSta + "],orderDesc:[" + orderDesc + "]");

			InputParam queryOrderParam = new InputParam();
			queryOrderParam.putparamString("txnSeqId", txnSeqId);
			queryOrderParam.putparamString("txnDt", txnDt);
			queryOrderParam.putparamString("txnTm", txnTm);

			logger.debug("[处理本行支付通知]:开始进行订单查询 请求信息" + queryOrderParam.toString());

			OutputParam queryOrderOutput = orderService.queryOrder(queryOrderParam);

			// logger.info("[处理本行支付通知]:查询订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode()))) {
				logger.debug("[处理本行支付通知 ：查询订单信息失败] ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", queryOrderOutput.getReturnMsg());
				return outputParam;
			}

			// logger.info("---------------[处理本行支付通知 ]:查询订单信息成功-----------");
			Map<String, Object> queryOrderMap = queryOrderOutput.getReturnObj();
			if (queryOrderMap == null || queryOrderMap.isEmpty()) {
				logger.debug("[处理本行支付通知] ：查询订单信息失败 ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_05);
				outputParam.putValue("respDesc", "原订单不存在");
				return outputParam;
			}

			if (StringConstans.OrderState.STATE_02.equals(queryOrderMap.get("txnSta"))) {
				logger.debug("[处理丰收互联支付通知] ：订单已经处理过了");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
				outputParam.putValue("respCode", queryOrderMap.get("txnSta"));
				outputParam.putValue("respDesc", "接收成功");
				return outputParam;
			}
			// 更新订单参数
			InputParam updateOrderParam = new InputParam();
			updateOrderParam.putparamString("txnSeqId", txnSeqId);
			updateOrderParam.putparamString("txnDt", txnDt);
			updateOrderParam.putparamString("txnTm", txnTm);
			updateOrderParam.putparamString("mobileOrderId", orderNumber);
			updateOrderParam.putparamString("mobileOrDt", mobileOrDt);
			updateOrderParam.putparamString("mobileOrTm", mobileOrTm);
			updateOrderParam.putparamString("txnSta", txnSta);
			updateOrderParam.putparamString("resDesc", orderDesc);
			updateOrderParam.putparamString("settleDate", DateUtil.getCurrentDateTime());

			logger.debug("[处理本行支付通知],开始更新订单信息  请求信息" + updateOrderParam.toString());

			// 更新订单信息
			OutputParam updateOrderOutput = this.updateOrderState(updateOrderParam);

			// logger.info("[处理手机银行支付通知] ,更新订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateOrderOutput.getReturnCode()))) {
				logger.debug("[处理本行支付通知] ：更新订单信息失败 ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[处理本行支付通知] ：更新订单信息失败");
				return outputParam;
			}

			// logger.info("-----------处理本行支付通知 ,更新订单信息成功-----------");

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "接收成功");

		} catch (FrameException e) {
			logger.error("[处理本行支付通知]:更新订单信息失败] " + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("二维码前置异常"+ e.getMessage());
		} finally {
			if (Configs.getConfigs().getBoolean("is_push"))
				this.notfiyThreadHandler(inputParam);
		}
		logger.info("[处理本行支付通知]  handleMobilePayNotify 方法结束  返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 通知手机银行去支付
	 * 
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam notifyPay(InputParam inputParam) {

		logger.info("[通知手机银行（丰收互联）去支付]  notifyPay 方法开始 请求报文" + inputParam.toString());

		OutputParam outPut = new OutputParam();

		try {

			// 二维码的订单号
			String txnSeqId = inputParam.getParamString().get("txnSeqId");
			// 二维码订单时间
			String txnTime = inputParam.getParamString().get("txnTime");
			// 卡号
			String cardNo = inputParam.getParamString().get("cardNo");
			// 商户名称
			String merName = inputParam.getParamString().get("merName");
			// 商户清算帐号
			String merAcctNo = inputParam.getParamString().get("merAcctNo");
			// 二维码串
			String codeUrl = inputParam.getParamString().get("codeUrl");
			// 交易金额
			String orderAmount = inputParam.getParamString().get("orderAmount");
			// 商户号
			String merId = inputParam.getParamString().get("merId");
			// 商户订单号
			String orderNumber = inputParam.getParamString().get("orderNumber");
			// 商户订单时间
			String orderTime = inputParam.getParamString().get("orderTime");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("codeUrl", codeUrl);
			map.put("merName", merName);
			map.put("merAcctNo", merAcctNo);
			map.put("orderAmount", orderAmount);
			map.put("cardNo", cardNo);
			map.put("txnSeqId", txnSeqId);
			map.put("txnTime", txnTime);
			map.put("isLocalBank", StringConstans.BankFlag.IS_BANK);

			InputParam input = new InputParam();
			input.putparamString("orderNumber", orderNumber);
			input.putparamString("orderTime", orderTime);
			input.putparamString("merId", merId);
			input.putMap(map);

			// 二维码类型
			String qrType = codeUrl.substring(0, 2);

			Map<String, Object> returnMap = new HashMap<String, Object>();
			if (QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE.getType().equals(qrType)) {
				returnMap = this.notifyMobileBankPay(input);
			}

			// if(QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType().equals(qrType)){
			if (QRCodeTypeEnum.DYNAMIC_QR_CODE_MOBILE_FRONT.getType().equals(qrType)
					|| QRCodeTypeEnum.STATIC_QR_CODE_LOCAL_CUPS.getType().equals(qrType)) {
				returnMap = this.notifyMobileFontPay(input);
			}

			logger.info("[开始通知支付] 接收手返回的json数据:" + returnMap.toString());

			if (StringConstans.OrderState.STATE_02.equals(returnMap.get("isSuccess"))) {
				logger.debug("[开始通知支付] 通知已经生效");
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				outPut.putValue("respDesc", "交易成功");
				outPut.setReturnCode(StringConstans.returnCode.SUCCESS);
				return outPut;
			}

			// 将JSON格式转换为Map
			// 订单状态
			String respCode = String.format("%s", returnMap.get("respCode"));
			// 订单状态描述
			String respDesc = (String.format("%s", returnMap.get("respMsg"))
					+ String.format("%s", returnMap.get("respDesc"))).replace("null", "");

			returnMap.put("txnSeqId", txnSeqId);
			returnMap.put("txnTime", txnTime);

			// 校验字段列表
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, returnMap);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[开始通知支付] 返回报文字段[" + nullStr + "]不能为空");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "[开始通知支付] 返回报文字段[\" + nullStr + \"]不能为空");
				return outPut;
			}

			// 获取手机银行返回信息
			InputParam upateOrderParam = new InputParam();
			String txnTimereturn = ObjectUtils.toString(returnMap.get("txnTime"));
			String mobileOrTime = ObjectUtils.toString(returnMap.get("respTime"));

			upateOrderParam.putparamString("txnSta", ObjectUtils.toString(returnMap.get("respCode")));
			upateOrderParam.putparamString("resDesc", ObjectUtils.toString(returnMap.get("respDesc")));
			upateOrderParam.putparamString("txnSeqId", ObjectUtils.toString(returnMap.get("txnSeqId")));
			upateOrderParam.putparamString("mobileOrderId", ObjectUtils.toString(returnMap.get("respSeqId")));
			upateOrderParam.putparamString("txnDt", txnTimereturn.substring(0, 8));
			upateOrderParam.putparamString("txnTm", txnTimereturn.substring(8, 14));

			if (!StringUtil.isEmpty(mobileOrTime)) {
				upateOrderParam.putparamString("mobileOrDt", mobileOrTime.substring(0, 8));
				upateOrderParam.putparamString("mobileOrTm", mobileOrTime.substring(8, 14));
			}
			logger.info("[通知手机银行（丰收互联）去支付] 通知同步响应后更新订单信息 请求信息" + upateOrderParam.toString());

			OutputParam updateOrderStateOut = this.updateOrderState(upateOrderParam);

			// logger.info("[开始通知支付] 通知同步响应后更新订单信息 结束");

			// 更新订单失败
			if (!StringConstans.returnCode.SUCCESS.equals(updateOrderStateOut.getReturnCode())) {
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "二维码前置异常");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				return outPut;
			}
			// 更新订单成功
			outPut.putValue("txnSeqId", txnSeqId);
			outPut.putValue("txnTime", txnTime);
			outPut.putValue("respCode", respCode);
			outPut.putValue("respDesc", respDesc);
			outPut.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (Exception e) {
			logger.error("请求和处理手机银行信息异常:" + e.getMessage(), e);
			if (e instanceof FrameException) {
				outPut.putValue("respDesc", e.getMessage());
			} else {
				outPut.putValue("respDesc", "二维码前置异常");
			}
			outPut.setReturnMsg(e.getMessage());
			outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outPut.setReturnCode(StringConstans.returnCode.FAIL);
		}
		
		logger.info("[通知手机银行（丰收互联）去支付]  notifyPay 方法结束 返回信息" + outPut.toString());
		return outPut;
	}

	/**
	 * 通知手机银行去支付
	 * 
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> notifyMobileBankPay(InputParam inputParam) {

		logger.info(" [请求手机银行进行支付] 方法开始 请求报文" + inputParam.toString());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			Map<String, Object> map = inputParam.getParams();

			// 请求服务码
			map.put("reqCode", StringConstans.OutSystemServiceCode.REQUEST_MOBILE_BANK);
			// 渠道
			map.put("channel", StringConstans.OutSystemChannel.CHANNEL_MOBILE_BANK);
			// 用以区分是三码合一还是原先的二维码：1原先的二维码，2三码合一的二维码 modify by tpf 2016-12-26
			map.put("txnFlag", "1");

			// 请求手机银行的地址
			String ip = Constants.getParam("notify_mobile_ip");
			int port = Integer.valueOf(Constants.getParam("notify_mobile_port"));
			logger.debug("[请求手机银行进行支付] 请求手机银行的IP:" + ip + ",port：" + port);

			// 参数转换为JSON格式
			String jsonStr = JsonUtil.bean2Json(map);
			String sendJsonStr = String.format("%06d%s", jsonStr.getBytes("UTF-8").length, jsonStr);
			logger.info("[请求手机银行进行支付] 发送手机银行的json数据:" + sendJsonStr);

			SocketClient socket = new SocketClient(ip, port);
			String reviceStr = socket.sendBytes(sendJsonStr, "UTF-8");
			// logger.info("[请求手机银行进行支付] 接收手机银行返回的json数据:" + reviceStr);

			// logger.info(" ----------------请求手机银行进行支付流程 END ----------------");

			paramMap = (Map<String, Object>) JsonUtil.json2Bean(reviceStr, Map.class);

		} catch (Exception e) {
			logger.error("[请求手机银行进行支付]出现异常:" + e.getMessage(), e);
			paramMap.put("respCode", StringConstans.OrderState.STATE_01);
			paramMap.put("respDesc", "请求手机银行进行支付出现异常");
		} finally {
			logger.info("[请求手机银行进行支付] 方法结束  返回信息" + paramMap.toString());
		}

		return paramMap;
	}

	/**
	 * 通知移动前端去支付
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, Object> notifyMobileFontPay(InputParam inputParam) {

		logger.info("[通知移动前端去支付]  notifyMobileFontPay 方法开始 请求报文" + inputParam.toString());
		Map<String, Object> paramMap = new HashMap<String, Object>();
		try {

			Map<String, Object> map = inputParam.getParams();

			// 商户号
			map.put("merId", inputParam.getValueString("merId"));
			// 商户订单号
			map.put("orderNumber", inputParam.getValueString("orderNumber"));
			// 商户订单时间
			map.put("orderTime", inputParam.getValueString("orderTime"));
			// 请求服务码
			map.put("reqCode", StringConstans.OutSystemServiceCode.REQUEST_MOBILE_FRONT);
			// 渠道
			map.put("channel", StringConstans.OutSystemChannel.CHANNEL_MOBILE_FRONT);

			// 请求移动前端的地址
			String ip = Constants.getParam("internet_lead_ip");
			int port = Integer.valueOf(Constants.getParam("internet_lead_port"));
			// logger.info("[请求移动前端进行支付]请求移动前端的IP:" + ip + ",port：" + port);

			String xmlStr = Util.mapToXml(map);
			String sendXmlStr = String.format("%06d%s", xmlStr.getBytes("UTF-8").length, xmlStr);
			
			logger.info("[请求移动前端进行支付] 发送给移动前端的xml数据:" + sendXmlStr);
			SocketClient socket = new SocketClient(ip, port);
			String reviceXmlStr = socket.sendBytes(sendXmlStr, "UTF-8");
			logger.info("[请求移动前端进行支付] 接收移动前端的返回的xml数据:" + reviceXmlStr);

			// 将JSON格式转换为Map
			paramMap = Util.getMapFromXML(reviceXmlStr);

			// 判断是否开启轮询
			if (Configs.getConfigs().getBoolean("is_query_fshl")
					&& StringConstans.OrderState.STATE_01.equals(String.format("%s", paramMap.get("respCode")))) {
				// 轮询，返回结果
				OutputParam out = loopQueryResult(inputParam);
				if (StringConstans.OrderState.STATE_02.equals(out.getValue("isSuccess"))) {
					paramMap.put("isSuccess", StringConstans.OrderState.STATE_02);
					return paramMap;
				} else {
					paramMap = out.getReturnObj();
				}
			}
			// logger.info(" ----------------请求移动前端进行支付流程 END ----------------");

		} catch (Exception e) {
			logger.error("[请求移动前端进行支付]出现异常:" + e.getMessage(), e);
			paramMap.put("respCode", StringConstans.OrderState.STATE_03);
			paramMap.put("respDesc", "二维码前置异常");
		} 
		logger.info("[通知移动前端去支付]  notifyMobileFontPay  返回信息" + paramMap.toString());
		return paramMap;
	}

	/**
	 * 更新订单状态[更新订单状态]
	 * 
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam updateOrderState(InputParam inputParam) {

		OutputParam outputParam = new OutputParam();

		try {

			// 空字段校验
			String nullStr = Util.validateIsNull(inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("更新订单请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");

				return outputParam;
			}

			logger.info("开始更新订单信息");
			// 更新订单信息
			OutputParam updateOrderOutput = orderService.updateOrderState(inputParam);

			logger.info("更新订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateOrderOutput.getReturnCode()))) {
				logger.error("[更新订单信息失败] ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "更新订单信息失败");
				return outputParam;
			}

			logger.info("-----------更新订单信息成功-----------");

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("更新订单成功");

		} catch (FrameException e) {
			logger.error("更新订单状态失败" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("更新订单信息失败");
		}

		return outputParam;
	}

	/**
	 * 获取手机银行返回信息
	 * 
	 * @param updateOrderMap
	 * @return
	 * @throws Exception
	 */
	public InputParam getMobileBankBackInputParam(Map<String, Object> paramMap) throws Exception {
		// 二维码前置生成订单号
		String txnSeqId = String.format("%s", paramMap.get("txnSeqId"));
		// 二维码前置生成订单时间
		String txnTime = String.format("%s", paramMap.get("txnTime"));
		// 订单日期
		String txnDt = txnTime.substring(0, 8);
		// 订单时间
		String txnTm = txnTime.substring(8);
		// 订单状态
		String txnSta = String.format("%s", paramMap.get("respCode"));
		// 原订单状态描述
		String orderDesc = String.format("%s", paramMap.get("respDesc"));
		// 手机银行的订单号
		String mobileOrderId = String.format("%s", paramMap.get("respSeqId"));
		// 手机银行生成的订单时间
		String respTime = String.format("%s", paramMap.get("respTime"));
		// 手机银行订单日期
		String mobileOrDt = respTime.substring(0, 8);
		// 手机银行订单时间
		String mobileOrTm = respTime.substring(8);

		logger.info("处理手机银行支付通知 ,支付结果 txnSta:[" + txnSta + "],orderDesc:[" + orderDesc + "]");

		// 更新订单参数
		InputParam updateOrderParam = new InputParam();
		updateOrderParam.putparamString("txnSeqId", txnSeqId);
		updateOrderParam.putparamString("txnDt", txnDt);
		updateOrderParam.putparamString("txnTm", txnTm);
		updateOrderParam.putparamString("mobileOrderId", mobileOrderId);
		updateOrderParam.putparamString("mobileOrDt", mobileOrDt);
		updateOrderParam.putparamString("mobileOrTm", mobileOrTm);
		updateOrderParam.putparamString("txnSta", txnSta);
		updateOrderParam.putparamString("resDesc", orderDesc);

		return updateOrderParam;

	}

	/**
	 * 处理本行的三码合一
	 * 
	 * @param inputParam
	 * @return
	 */
	@Override
	public OutputParam handlerLocalThreedQRCode(InputParam input) {

		logger.info("---------------- 处理三码合一 本行扫码  创建订单并支付的流程    START---------");

		OutputParam outputParam = new OutputParam();

		try {

			logger.info("[多码合一  本行二维码  创建订单并支付] 获取交易流水号  开始");

			String txnSeqId = orderService.getOrderNo(StringConstans.CHANNEL.CHANNEL_SELF);

			logger.info("[多码合一 本行二维码  创建订单并支付] 获取交易流水号   结束");

			logger.info("[多码合一本行二维码  创建订单并支付] 获取订单序列号完成,txnSeqId:[" + txnSeqId + "]");

			// 订单金额
			String orderAmount = String.format("%s", input.getValue("orderAmount"));
			orderAmount = StringUtil.amountTo12Str(orderAmount);
			// 订单号
			String orderInfo = String.format("%s", input.getValue("orderInfo"));
			// 商户名称
			String merName = String.format("%s", input.getValue("merName"));
			// 账号
			String acctNo = String.format("%s", input.getValue("acctNo"));
			// 商户号
			String merId = String.format("%s", input.getValue("merId"));
			// 交易日期
			String txnDt = DateUtil.getDateYYYYMMDD();
			// 交易时间
			String txnTm = DateUtil.getDateHHMMSS();

			// 新增订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("merId", merId);
			orderInput.putparamString("merOrderId", txnSeqId);
			orderInput.putparamString("merOrDt", txnDt);
			orderInput.putparamString("merOrTm", txnTm);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("txnType", StringConstans.TransType.TRANS_CONSUME);
			orderInput.putparamString("txnChannel", StringConstans.CHANNEL.CHANNEL_SELF);
			orderInput.putparamString("payType", StringConstans.Pay_Type.PAY_TYPE_NATIVE);
			orderInput.putparamString("payAccessType", StringConstans.PAYACCESSTYPE.ACCESS_NATIVE);
			orderInput.putparamString("currencyCode", StringConstans.SETTLE_CURRENTY_TYPE);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "订单初始化");

			// 三码合一时需要该字段 与 三码合一静态二维码关联
			if (!StringUtil.isEmpty(input.getValue("ewmData"))) {
				orderInput.putparamString("ewmData", input.getValue("ewmData").toString());
			}

			logger.info("[多码合一本行二维码  创建订单并支付] 开始保存订单信息");

			OutputParam saveOrderOutput = orderService.insertOrder(orderInput);

			logger.info("[多码合一本行二维码  创建订单并支付] 保存订单信息 完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				logger.error("[多码合一本行二维码  创建订单并支付] 保存订单信息失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "保存订单信息失败");
				return outputParam;
			}

			logger.info("-------------- [多码合一本行二维码  创建订单并支付] 保存订单信息成功-------------");

			InputParam mobileInput = new InputParam();

			mobileInput.putparamString("txnSeqId", txnSeqId);
			mobileInput.putparamString("txnTime", txnDt + txnTm);
			mobileInput.putparamString("merName", merName);
			mobileInput.putparamString("merAcctNo", acctNo);
			mobileInput.putparamString("codeUrl", orderInfo);
			mobileInput.putparamString("txnFlag", "2");// 用以区分是三码合一还是原先的二维码：1原先的二维码，2三码合一的二维码
			mobileInput.putparamString("orderAmount", orderAmount);
			mobileInput.putparamString("reqCode", StringConstans.OutSystemServiceCode.REQUEST_MOBILE_BANK);
			mobileInput.putparamString("channel", StringConstans.OutSystemChannel.CHANNEL_MOBILE_BANK);

			logger.info("[多码合一本行二维码  创建订单并支付] 调用手机银行支付   开始");

			OutputParam mobileOut = this.notifyMobileBankPayOfThreeCode(mobileInput);

			logger.info("[多码合一本行二维码  创建订单并支付] 调用手机银行支付   结束");

			if (!StringConstans.returnCode.SUCCESS.equals(mobileOut.getReturnCode())) {
				logger.error("[多码合一本行二维码  创建订单并支付] 手机银行返回失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", mobileOut.getReturnMsg());
				return outputParam;
			}
			outputParam.setReturnObj(mobileOut.getReturnObj());
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (FrameException e) {
			logger.error("[多码合一本行二维码  创建订单并支付] 本行创建订单并支付异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常");
		}

		return outputParam;
	}

	/**
	 * 通知手机银行去支付
	 * 
	 * @param inputParam
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private OutputParam notifyMobileBankPayOfThreeCode(InputParam inputParam) {

		logger.info("------------开始通知手机银行进行支付----------");

		OutputParam outPut = new OutputParam();

		try {

			// 请求手机银行的地址
			String ip = Constants.getParam("notify_mobile_ip");
			int port = Integer.valueOf(Constants.getParam("notify_mobile_port"));
			logger.info("请求手机银行的IP:" + ip + ",port：" + port);

			// 二维码的订单号
			String txnSeqId = inputParam.getParamString().get("txnSeqId");
			// 二维码订单时间
			String txnTime = inputParam.getParamString().get("txnTime");
			// 卡号
			String cardNo = inputParam.getParamString().get("cardNo");
			// 商户名称
			String merName = inputParam.getParamString().get("merName");
			// 商户清算帐号
			String merAcctNo = inputParam.getParamString().get("merAcctNo");
			// 渠道号
			String channel = inputParam.getParamString().get("channel");
			// 二维码串
			String codeUrl = inputParam.getParamString().get("codeUrl");
			// 手机银行服务码
			String reqCode = inputParam.getParamString().get("reqCode");
			// 交易金额
			String orderAmount = inputParam.getParamString().get("orderAmount");
			// 平台交易标识
			String txnFlag = inputParam.getParamString().get("txnFlag");

			Map<String, Object> jsonMap = new HashMap<String, Object>();
			jsonMap.put("reqCode", reqCode);
			jsonMap.put("codeUrl", codeUrl);
			jsonMap.put("merName", merName);
			jsonMap.put("merAcctNo", merAcctNo);
			jsonMap.put("orderAmount", orderAmount);
			jsonMap.put("channel", channel);
			jsonMap.put("txnSeqId", txnSeqId);
			jsonMap.put("txnTime", txnTime);
			jsonMap.put("txnFlag", txnFlag);
			if (!StringUtil.isEmpty(cardNo)) {
				jsonMap.put("cardNo", cardNo);
			}

			// 参数转换为JSON格式
			String jsonStr = JsonUtil.bean2Json(jsonMap);
			String sendJsonStr = String.format("%06d%s", jsonStr.getBytes("UTF-8").length, jsonStr);

			logger.info("发送手机银行的json数据:" + sendJsonStr);

			SocketClient socket = new SocketClient(ip, port);
			String reviceStr = socket.sendBytes(sendJsonStr, "UTF-8");

			logger.info("接收手机银行返回的json数据:" + reviceStr);

			// 判断手机银行传过来的参数值
			if (StringUtils.isBlank(reviceStr)) {
				logger.error("-----------------手机银行返回报文为空-----------------");
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "手机银行返回支付报文信息错误");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				return outPut;
			}

			// 将JSON格式转换为Map
			Map<String, Object> paramMap = (Map<String, Object>) JsonUtil.json2Bean(reviceStr, Map.class);
			// 订单状态
			String respCode = String.format("%s", paramMap.get("respCode"));
			// 订单状态描述
			String respDesc = String.format("%s", paramMap.get("respDesc"));

			if (StringConstans.RespCode.RESP_CODE_03.equals(respCode)) {
				logger.error("[手机银行返回支付结果] 手机银行返回结果失败,原因:" + respDesc);
				paramMap.put("txnSeqId", txnSeqId);
				paramMap.put("txnTime", txnTime);
			}

			// 校验字段列表
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");
			validateParamList.add("respDesc");
			validateParamList.add("respCode");
			validateParamList.add("respSeqId");
			validateParamList.add("respTime");

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, paramMap);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("[手机银行返回支付结果] 返回报文字段[" + nullStr + "]不能为空");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "手机银行返回支付报文信息有误");
				return outPut;
			}

			// 获取手机银行返回信息
			InputParam upateOrderParam = this.getMobileBankBackInputParam(paramMap);

			// 更新订单信息
			OutputParam updateOrderStateOut = this.updateOrderState(upateOrderParam);
			// 更新订单失败
			if (!StringConstans.returnCode.SUCCESS.equals(updateOrderStateOut.getReturnCode())) {
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "二维码前置异常");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				return outPut;
			}

			// 更新订单成功
			outPut.putValue("orderNumber", inputParam.getValueString("orderNumber"));
			outPut.putValue("orderTime", inputParam.getValueString("orderTime"));
			outPut.putValue("respCode", respCode);
			outPut.putValue("respDesc", respDesc);
			outPut.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (Exception e) {
			logger.error("请求和处理手机银行信息异常:" + e.getMessage(), e);
			if (e instanceof FrameException) {
				outPut.putValue("respDesc", e.getMessage());
			} else {
				outPut.putValue("respDesc", "二维码前置异常");
			}
			outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outPut.setReturnCode(StringConstans.returnCode.FAIL);
		}

		return outPut;
	}

	/**
	 * 通知线程处理
	 * 
	 * @param input
	 */
	private void notfiyThreadHandler(InputParam inputParam) {

		logger.info("------------  本行后台通知 --通知外围系统  start ---------");

		try {

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");
			validateParamList.add("txnSta");
			validateParamList.add("orderNumber");
			validateParamList.add("orderTime");
			validateParamList.add("orderDesc");

			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("[本行后台通知 --通知外围系统]:返回报文字段[" + nullStr + "]不能为空");
				return;
			}

			// 二维码前置生成订单号
			String txnSeqId = String.format("%s", inputParam.getValue("txnSeqId"));
			// 丰收互联交易订单交易金额
			String tradeMoney = String.format("%s", inputParam.getValue("orderAmount"));
			// 订单交易状态
			String txnSta = String.format("%s", inputParam.getValue("txnSta"));
			// 用户信息，丰收互联和手机银行暂未提供次字段
			String userInfo = String.format("%s", inputParam.getValue("userInfo"));

			if (!StringConstans.OrderState.STATE_02.equals(txnSta)) {
				logger.info("[本行后台通知 -- 外围系统通知] 交易未成功不进行通知");
				return;
			}

			if (!txnSeqId.matches("TC\\d*")) {
				logger.info("[本行后台通知 -- 外围系统通知] 非三码合一扫码交易不进行通知");
				return;
			}

			Pattern p = Pattern.compile("[a-zA-Z]*");
			Matcher m = p.matcher(txnSeqId);
			String prefix = m.find() ? m.group() : "";
			logger.info("[本行后台通知 -- 外围系统通知] prefix=" + prefix);

			// 二维码前置生成订单时间
			String txnTime = String.format("%s", inputParam.getValue("txnTime"));
			// 订单日期
			String txnDt = txnTime.substring(0, 8);
			// 订单时间
			String txnTm = txnTime.substring(8);

			logger.info("[本行后台通知 --通知外围系统 ]:二维码前置订单信息txnSeqId:[" + txnSeqId + "],txnTime:[" + txnTime + "]");

			InputParam queryInput = new InputParam();
			queryInput.putparamString("txnSeqId", txnSeqId);
			queryInput.putparamString("txnDt", txnDt);
			queryInput.putparamString("txnTm", txnTm);

			logger.info("[本行后台通知 --通知外围系统]  根据外部订单号查询订单是否存在            开始");

			OutputParam queryOut = orderService.queryOrder(queryInput);

			logger.info("[本行后台通知 --通知外围系统]  根据外部订单号查询订单是否存在            结束");

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.error("[本行后台通知 --通知外围系统理]  根据外部订单号查询订单没有找到匹配的记录");
				return;
			}

			// 串码明文
			String ewmData = String.format("%s", queryOut.getValue("ewmData"));

			InputParam queryThreeCodeInput = new InputParam();
			queryThreeCodeInput.putparamString("ewmData", ewmData);
			queryThreeCodeInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);

			logger.info("[接受本行后台通知 -- 外围系统通知] 根据ewmData三码合一相关信息  开始");

			OutputParam queryTcOut = threeCodeStaticQRCodeDataService
					.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

			logger.info("[接受 本行后台通知 -- 外围系统通知] 根据ewmData三码合一相关信息  结束");

			if (!StringConstans.returnCode.SUCCESS.equals(queryTcOut.getReturnCode())) {
				logger.error("[查询三码合一流水] 根据ewmStatue查询记录失败");
				return;
			}

			queryOut.putValue("userInfo", userInfo);

			NotifyMessage notifyMessage = new NotifyMessage();

			if (StringConstans.PrefixOrder.THREE_CODE.equals(prefix)) {
				this.getThreeCodeNotifyMessage(notifyMessage, queryOut, queryTcOut);
				if (!StringUtil.isEmpty(tradeMoney)) {
					notifyMessage.getThreeCodeNotifyMessage().setOrderAmount(tradeMoney);
				}
			}

			ThreadNotifyHelper.notifyThread(notifyMessage);

			logger.info("----------------- 支付宝后台通知 --通知外围系统    END ---------------");

		} catch (FrameException e) {
			logger.error("[支付宝后台通知 --通知外围系统 ] 支付宝预下单后台通知处理出现异常: " + e.getMessage(), e);
			logger.info("----------------- 支付宝后台通知 --通知外围系统    END ---------------");
		}
	}

	private void getThreeCodeNotifyMessage(NotifyMessage notifyMessage, OutputParam queryOut, OutputParam queryTcOut) {
		// 二维码流水号
		String txnSeqId = String.format("%s", queryOut.getValue("txnSeqId"));
		// 交易日期
		String txnDt = String.format("%s", queryOut.getValue("txnDt"));
		// 交易时间
		String txnTm = String.format("%s", queryOut.getValue("txnTm"));
		// 订单状态
		String txnSta = String.format("%s", queryOut.getValue("txnSta"));
		// 下单金额
		String tradeAmount = String.format("%s", queryOut.getValue("tradeMoney"));
		// 商户号
		String merId = String.format("%s", queryOut.getValue("merId"));
		// 支付接入类型
		String payAccessType = String.format("%s", queryOut.getValue("payAccessType"));
		// 扫码方式
		String payType = String.format("%s", queryOut.getValue("payType"));
		// 商户订单号
		String orderNumber = String.format("%s", queryOut.getValue("merOrderId"));
		// 商户订单日期
		String merOrDt = String.format("%s", queryOut.getValue("merOrDt"));
		// 商户订单时间
		String merOrTm = String.format("%s", queryOut.getValue("merOrTm"));
		// 支付时间
		String gmtPayment = String.format("%s", queryOut.getValue("settleDate"));
		// 账号
		String acctNo = String.format("%s", queryTcOut.getValue("acctNo"));
		// 机构号
		String orgCode = String.format("%s", queryTcOut.getValue("orgCode"));
		// 买家信息
		String userInfo = String.format("%s", queryOut.getValue("userInfo"));
		// 银行手续费率
		// String bankFeeRate = String.format("%s", queryTcOut.getValue("bankFeeRate"));
		// 手续费
		// String fee = StringUtil.getFeeByTradeAmount(bankFeeRate, tradeAmount);
		String fee = "0.00";

		NotifyMessage.ThreeCodeNotifyMessage message = new NotifyMessage.ThreeCodeNotifyMessage();
		message.setTxnSeqId(txnSeqId);
		message.setTxnTime(txnDt + txnTm);
		message.setTxnSta(txnSta);
		message.setOrderAmount(tradeAmount);
		message.setClearDate(gmtPayment);
		message.setPayAccessType(payAccessType);
		message.setPayType(payType);
		message.setOrderNumber(orderNumber);
		message.setOrderTime(merOrDt + merOrTm);
		message.setMerId(merId);
		message.setFee(fee);
		message.setAcctNo(acctNo);
		message.setBuyerInfo(userInfo);
		message.setOrgCode(orgCode);
		notifyMessage.setTxnSeqId(txnSeqId);
		notifyMessage.setThreeCodeNotifyMessage(message);
	}

	/**
	 * 丰收互联扫一码通创建订单
	 */
	@Override
	public OutputParam createMobileFrontThreeQrCode(InputParam input) {
		logger.info("---------------- 处理三码合一 丰收互联扫码  创建订单流程    START---------(input:" + input.toString() + ")");

		OutputParam outputParam = new OutputParam();

		try {

			logger.debug("[多码合一  丰收互联二维码  创建订单] 获取交易流水号  开始");

			String txnSeqId = orderService.getOrderNo(StringConstans.CHANNEL.CHANNEL_SELF);

			logger.debug("[多码合一 丰收互联二维码  创建订单] 获取交易流水号   结束");

			logger.debug("[多码合一丰收互联二维码  创建订单] 获取订单序列号完成,txnSeqId:[" + txnSeqId + "]");

			// 商户号
			String merId = String.format("%s", input.getValue("merId"));
			// 交易日期
			String txnDt = DateUtil.getDateYYYYMMDD();
			// 交易时间
			String txnTm = DateUtil.getDateHHMMSS();

			input.putParams("cardNo", ObjectUtils.toString(input.getValue("acctNo")));
			input.putParams("orderTime", txnDt);
			input.putParams("channel", StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT);

			// 新增订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("merId", merId);
			orderInput.putparamString("merOrderId", txnSeqId);
			orderInput.putparamString("merOrDt", txnDt);
			orderInput.putparamString("merOrTm", txnTm);

			// 丰收互联扫码金额暂时设定为0
			orderInput.putparamString("tradeMoney", "000000000000");
			orderInput.putparamString("txnType", StringConstans.TransType.TRANS_CONSUME);
			// 渠道新增为丰收互联扫码
			orderInput.putparamString("txnChannel", StringConstans.CHANNEL.CHANNEL_SELF);
			orderInput.putparamString("payType", StringConstans.Pay_Type.PAY_TYPE_NATIVE);
			orderInput.putparamString("payAccessType", StringConstans.PAYACCESSTYPE.ACCESS_FSHL);
			orderInput.putparamString("currencyCode", StringConstans.SETTLE_CURRENTY_TYPE);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "订单初始化");

			// 三码合一时需要该字段 与 三码合一静态二维码关联
			if (!StringUtil.isEmpty(input.getValue("ewmData"))) {
				orderInput.putparamString("ewmData", input.getValue("ewmData").toString());
			}

			// 开始查询商户限额信息
			logger.debug("[多码合一丰收互联  商户限额] 开始查询商户限额信息");
			outputParam = queryLimitAmt(input, outputParam);
			logger.debug("[多码合一丰收互联  商户限额] 查询商户限额信息结束");

			// 开始保存订单
			logger.debug("[多码合一丰收互联  创建订单] 开始保存订单信息");

			OutputParam saveOrderOutput = orderService.insertOrder(orderInput);

			logger.debug("[多码合一丰收互联  创建订单] 保存订单信息 完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				logger.error("[多码合一丰收互联  创建订单] 保存订单信息失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "保存订单信息失败");
				return outputParam;
			}

			outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.putValue("txnTime", txnDt + txnTm);
			outputParam.putValue("initOrderNumber", txnSeqId);

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "二维码前置生成订单成功");

		} catch (FrameException e) {
			logger.error("[多码合一丰收互联  创建订单] 本行创建订单异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置异常");
		}
		logger.debug(
				"-------------- [多码合一丰收互联  创建订单] 保存订单信息成功-------------(outputParam:" + outputParam.toString() + ")");
		return outputParam;
	}

	/**
	 * 商户限额查询
	 */
	@Override
	public OutputParam queryLimitAmt(InputParam input, OutputParam outputParam) {
		logger.info("[商户限额查询开始] input=" + input.getParamString().toString());
		// 渠道编号
		String channel = String.format("%s", input.getValue("channel"));
		logger.info("[商户限额查询渠道校验] 渠道  channel=" + channel);
		if (!StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
			logger.error("[商户限额查询渠道校验] 渠道校验失败");
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "渠道：[" + channel + "]错误");
			return outputParam;
		}

		Map<String, String> queryMap = new HashMap<String, String>();
		queryMap.put("limitDt", ObjectUtils.toString(input.getValue("orderTime")));
		queryMap.put("acctNo", ObjectUtils.toString(input.getValue("cardNo")));
		OutputParam limitOutPut = tblMerTradeLimitService.queryTradeLimit(queryMap);
		logger.info("[商户限额查询结果] 限额=" + limitOutPut.getReturnObj().toString());
		if (limitOutPut.getReturnCode().equals(StringConstans.returnCode.SUCCESS)) {
			outputParam.putValue("tradeAmtDay", limitOutPut.getReturnObj().get("tradeamtDay").toString());
			outputParam.putValue("tradeAmtMonth", limitOutPut.getReturnObj().get("tradeamtMonth").toString());
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "商户限额查询成功");
		} else {
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "商户限额查询异常");
			return outputParam;
		}
		outputParam.putValue("limitAmtDay", Constants.getParam("limitAmtDay"));
		outputParam.putValue("limitAmtMonth", Constants.getParam("limitAmtMonth"));
		return outputParam;
	}

	/**
	 * 接收移动前端扫一码通通知
	 */
	@Override
	public OutputParam handlerMobileFrontNotify(InputParam inputParam) {

		logger.info("[接收移动前端扫一码通通知(支付平台额度校验通知)]  handlerMobileFrontNotify 方法开始 请求信息" + inputParam.toString());
		OutputParam outputParam = new OutputParam();

		try {

			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");
			validateParamList.add("txnSta");
			validateParamList.add("orderNumber");
			validateParamList.add("orderAmount");
			validateParamList.add("orderTime");
			validateParamList.add("orderDesc");
			// validateParamList.add("codeUrl");

			String nullStr = Util.validateIsNull(validateParamList, inputParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[处理丰收互联支付通知]:请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			String codeUrl = String.format("%s", inputParam.getValue("codeUrl"));
			String orderType = String.format("%s", inputParam.getValue("orderType")); 
			if (!StringUtil.isEmpty(codeUrl) && codeUrl.startsWith(StringConstans.CupsEwmInfo.CUPS_EWM_PREFIX)) {
				// 执行附加处理结果通知
				outputParam = cupsPayService.C2BScanedAttachHandlerNotify(inputParam);
				return outputParam;		
			}else if(!StringUtil.isEmpty(codeUrl) && codeUrl.startsWith(StringConstans.CupsEwmInfo.CUPS_EWM_OWN)&&!StringUtil.isEmpty(orderType)){
				//银联二维码他行主扫付款
				return cupsPayService.C2BSPayForMainScavenging(inputParam);
			}

			String orderNumber = StringUtil.toString(inputParam.getValue(Dict.orderNumber));
			String orderAmount = StringUtil.toString(inputParam.getValue(Dict.orderAmount));
			String orderTime = StringUtil.toString(inputParam.getValue(Dict.orderTime));
			// 丰收互联订单日期
			String mobileOrDt = orderTime.substring(0, 8);
			// 丰收互联订单时间
			String mobileOrTm = orderTime.substring(8);

			// 丰收互联付款卡号
			String payerCardNo = String.format("%s", inputParam.getValue("payerCardNo"));

			// 商户清算卡号
			String acctNo = String.format("%s", inputParam.getValue("acctNo"));

			// 丰收互联付款卡属性
			String cardAttr = String.format("%s", inputParam.getValue("cardAttr"));

			logger.debug("[处理丰收互联支付通知]:订单信息orderNumber:[" + orderNumber + "],orderTime:[" + orderTime + "]");

			// 二维码前置生成订单号
			String txnSeqId = String.format("%s", inputParam.getValue("txnSeqId"));
			// 二维码前置生成订单时间
			String txnTime = String.format("%s", inputParam.getValue("txnTime"));
			// 订单日期
			String txnDt = txnTime.substring(0, 8);
			// 订单时间
			String txnTm = txnTime.substring(8);

			logger.debug("[处理丰收互联支付通知 ]:二维码前置订单信息txnSeqId:[" + txnSeqId + "],txnTime:[" + txnTime + "]");

			// 订单状态
			String txnSta = String.format("%s", inputParam.getValue("txnSta"));
			// 原订单状态描述
			String orderDesc = String.format("%s", inputParam.getValue("orderDesc"));

			logger.debug("[处理丰收互联支付通知 ]:支付结果 txnSta:[" + txnSta + "],orderDesc:[" + orderDesc + "]");

			InputParam queryOrderParam = new InputParam();
			queryOrderParam.putparamString("txnSeqId", txnSeqId);
			queryOrderParam.putparamString("txnDt", txnDt);
			queryOrderParam.putparamString("txnTm", txnTm);

			logger.debug("[接收移动前端扫一码通通知(支付平台额度校验通知)]:开始进行订单查询 请求信息" + queryOrderParam.toString());

			OutputParam queryOrderOutput = orderService.queryOrder(queryOrderParam);

			// logger.info("[处理丰收互联支付通知]:查询订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode()))) {
				logger.debug("[处理丰收互联支付通知 ：查询订单信息失败] ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", queryOrderOutput.getReturnMsg());
				return outputParam;
			}

			// logger.info("---------------[处理丰收互联支付通知 ]:查询订单信息成功-----------");
			Map<String, Object> queryOrderMap = queryOrderOutput.getReturnObj();
			if (queryOrderMap == null || queryOrderMap.isEmpty()) {
				logger.debug("[处理丰收互联支付通知] ：查询订单信息失败 ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_05);
				outputParam.putValue("respDesc", "原订单不存在");
				return outputParam;
			}

			// 更新订单参数
			InputParam updateOrderParam = new InputParam();
			updateOrderParam.putparamString("txnSeqId", txnSeqId);
			updateOrderParam.putparamString("txnDt", txnDt);
			updateOrderParam.putparamString("txnTm", txnTm);
			updateOrderParam.putparamString("mobileOrderId", orderNumber);
			updateOrderParam.putparamString("tradeMoney", orderAmount);
			updateOrderParam.putparamString("mobileOrDt", mobileOrDt);
			updateOrderParam.putparamString("mobileOrTm", mobileOrTm);
			updateOrderParam.putparamString("txnSta", txnSta);
			updateOrderParam.putparamString("payerid", payerCardNo);
			updateOrderParam.putparamString("resDesc", orderDesc);
			updateOrderParam.putparamString("settleDate", DateUtil.getCurrentDateTime());

			logger.debug("[接收移动前端扫一码通通知(支付平台额度校验通知)],开始更新订单信息 请求信息" + updateOrderParam.toString());

			// 更新订单信息
			OutputParam updateOrderOutput = this.updateOrderState(updateOrderParam);

			// logger.info("[处理丰收互联支付通知] ,更新订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateOrderOutput.getReturnCode()))) {
				logger.debug("[处理丰收互联支付通知] ：更新订单信息失败 ");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[处理丰收互联支付通知] ：更新订单信息失败");
				return outputParam;
			}

			// logger.info("-----------处理丰收互联支付通知 ,更新订单信息成功-----------");

			if (StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr)) {
				logger.debug("-----------处理丰收互联支付通知 ,开始更新限额信息-----------");
				Map<String, String> mapLimit = new HashMap<String, String>();
				mapLimit.put("acctNo", acctNo);
				mapLimit.put("limitDt", txnDt);
				mapLimit.put("tradeAmt", orderAmount);
				tblMerTradeLimitService.updateTradeLimit(mapLimit);
				// logger.info("-----------处理丰收互联支付通知 ,更新限额信息成功-----------");
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "接收成功");
		} catch (FrameException e) {
			logger.error("[处理丰收互联支付通知]:更新订单信息失败] " + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码 前置异常");
		} finally {
			if (Configs.getConfigs().getBoolean("is_push"))
				this.notfiyThreadHandler(inputParam);
		}
		logger.info("[处理丰收互联支付通知]   handlerMobileFrontNotify 方法结束  返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 查询丰收互联订单状态
	 */
	public OutputParam queryFshlOrder(Map<String, String> map) {

		OutputParam queryOut = new OutputParam();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {

			String ip = map.get("ip");
			int port = Integer.valueOf(map.get("port"));
			logger.info("[查询丰收互联订单]请求移动前端的IP:" + ip + ",port：" + port);

			String sendXmlStr = map.get("sendXmlStr");

			logger.info("[查询丰收互联订单] 发送给移动前端的xml数据:" + sendXmlStr);
			SocketClient socket = new SocketClient(ip, port);
			String reviceXmlStr = socket.sendBytes(sendXmlStr, "UTF-8");
			logger.info("[查询丰收互联订单] 接收移动前端的返回的xml数据:" + reviceXmlStr);

			resultMap = Util.getMapFromXML(reviceXmlStr);
			String stat = ObjectUtils.toString(resultMap.get("stat"));
			String respDesc = ObjectUtils.toString(resultMap.get("respDesc"));

			if (StringConstans.FshlOrderState.FAILED.equals(stat)) {
				resultMap.put("respCode", StringConstans.OrderState.STATE_03);
				respDesc = "本行二维码支付失败" + respDesc;
			} else if (StringConstans.FshlOrderState.SUCCESS.equals(stat)) {
				resultMap.put("respCode", StringConstans.OrderState.STATE_02);
				respDesc = "本行二维码支付成功";
			} else {
				resultMap.put("respCode", StringConstans.OrderState.STATE_01);
				respDesc = "本行二维码支付交易处理中";
			}
			resultMap.put("respDesc", respDesc);
			queryOut.setReturnObj(resultMap);
		} catch (Exception e) {
			logger.error("二维码 前置异常:" + e.getMessage(), e);
			queryOut.putValue("respCode", StringConstans.OrderState.STATE_03);
			queryOut.putValue("respDesc", "二维码 前置异常");
		}

		return queryOut;
	}

	/**
	 * 轮询丰收互联交易状态
	 * 
	 * @param queryInput
	 * @return
	 */
	private OutputParam loopQueryResult(InputParam queryInput) {

		OutputParam out = null;
		// 查询本地数据库订单信息参数

		// 查询本地订单map
		InputParam queryOrderParam = new InputParam();
		queryOrderParam.putparamString("txnSeqId", ObjectUtils.toString(queryInput.getValue("txnSeqId")));
		queryOrderParam.putparamString("txnDt", ObjectUtils.toString(queryInput.getValue("txnTime")).substring(0, 8));
		queryOrderParam.putparamString("txnTm", ObjectUtils.toString(queryInput.getValue("txnTime")).substring(8, 14));
		try {
			// 轮询必要参数
			int maxQueryRetry = Integer.valueOf(Constants.getParam("max_query_fshl"));
			Map<String, String> querymap = new HashMap<String, String>();
			querymap.put("ip", Constants.getParam("internet_lead_ip"));
			querymap.put("port", Constants.getParam("internet_lead_port"));

			Map<String, Object> xmlMap = new HashMap<String, Object>();
			xmlMap.put("txnSeqId", ObjectUtils.toString(queryInput.getValue("txnSeqId")));
			xmlMap.put("reqCode", StringConstans.OutSystemServiceCode.QUERY_FSHL_ORDER);

			String sendXmlStr = String.format("%06d%s", Util.mapToXml(xmlMap).getBytes("UTF-8").length,
					Util.mapToXml(xmlMap));
			querymap.put("sendXmlStr", sendXmlStr);
			/*
			 * 轮询机制与丰收家的约定如下 二维码前置收到丰收互联关于二维码解析请求起3秒后开始第一次查询 第一次查询隔8秒后开始第二次查询
			 * 第二次查询隔15秒后开始第三次查询 总共进行三次
			 */
			for (int i = 0; i < maxQueryRetry; i++) {
				Util.sleep((i + 3) * (i + 1) * 1000);
				// 轮询前查询本地数据库，如果状态为02，直接返回成功
				OutputParam queryOrderOutput = orderService.queryOrder(queryOrderParam);
				if (StringConstans.OrderState.STATE_02.equals(queryOrderOutput.getValue("txnSta"))) {
					logger.info("已经通知成功，无需开启轮询");
					out.putValue("isSuccess", StringConstans.OrderState.STATE_02);
					return out;
				}

				logger.info("轮询丰收互联交易状态: 第 " + (i + 1) + "次查询");
				out = queryFshlOrder(querymap);
				logger.info("轮询丰收互联交易状态: 第 " + (i + 1) + "次查询结果: " + out.toString());

				if (StringConstans.OrderState.STATE_02.equals(out.getValue("respCode"))) {
					return out;
				}
			}
		} catch (Exception e) {
			logger.error("二维码 前置异常:" + e.getMessage(), e);
			out.putValue("respCode", StringConstans.OrderState.STATE_03);
			out.putValue("respDesc", "二维码 前置异常");
		}
		return out;
	}

	public IOrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public IQRCodeService getQrCodeService() {
		return qrCodeService;
	}

	public void setQrCodeService(IQRCodeService qrCodeService) {
		this.qrCodeService = qrCodeService;
	}

	public WxPayService getWxPayService() {
		return wxPayService;
	}

	public void setWxPayService(WxPayService wxPayService) {
		this.wxPayService = wxPayService;
	}

	public AliPayPayService getAliPayPayService() {
		return aliPayPayService;
	}

	public void setAliPayPayService(AliPayPayService aliPayPayService) {
		this.aliPayPayService = aliPayPayService;
	}

	public IStaticQRCodeDataService getStaticQRCodeDataService() {
		return staticQRCodeDataService;
	}

	public void setStaticQRCodeDataService(IStaticQRCodeDataService staticQRCodeDataService) {
		this.staticQRCodeDataService = staticQRCodeDataService;
	}

	public IThreeCodeStaticQRCodeDataService getThreeCodeStaticQRCodeDataService() {
		return threeCodeStaticQRCodeDataService;
	}

	public void setThreeCodeStaticQRCodeDataService(
			IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService) {
		this.threeCodeStaticQRCodeDataService = threeCodeStaticQRCodeDataService;
	}

	public ICupsPayService getCupsPayService() {
		return cupsPayService;
	}

	public void setCupsPayService(ICupsPayService cupsPayService) {
		this.cupsPayService = cupsPayService;
	}

	public ITblMerTradeLimitService getTblMerTradeLimitService() {
		return tblMerTradeLimitService;
	}

	public void setTblMerTradeLimitService(ITblMerTradeLimitService tblMerTradeLimitService) {
		this.tblMerTradeLimitService = tblMerTradeLimitService;
	}

	public WxMerchantSynchService getWxMerchantSynchService() {
		return wxMerchantSynchService;
	}

	public void setWxMerchantSynchService(WxMerchantSynchService wxMerchantSynchService) {
		this.wxMerchantSynchService = wxMerchantSynchService;
	}

	public IMerchantChannelService getMerchantChannelService() {
		return merchantChannelService;
	}

	public void setMerchantChannelService(IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

}
