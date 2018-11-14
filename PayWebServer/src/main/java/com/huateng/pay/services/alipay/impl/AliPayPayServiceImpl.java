package com.huateng.pay.services.alipay.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.domain.TradeFundBill;
import com.alipay.demo.trade.config.Configs;
import com.google.common.base.CharMatcher;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.constants.StringConstans.AlipayErrorCode;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.common.validate.YlAliValidation;
import com.huateng.pay.common.validate.vali.Validation;
import com.huateng.pay.dao.inter.IOrderDao;
import com.huateng.pay.handler.thread.ThreadNotifyHelper;
import com.huateng.pay.manager.alipay.IAliPayManager;
import com.huateng.pay.po.notify.NotifyMessage;
import com.huateng.pay.services.alipay.AliPayMerchantSynchService;
import com.huateng.pay.services.alipay.AliPayPayService;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;
import com.huateng.utils.FileUtil;
import com.huateng.utils.SftpUtil;
import com.huateng.utils.Util;
import com.wldk.framework.mapping.MappingContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 支付宝支付接口实现
 * 
 * @author zhaoyuanxiang
 * 
 */
public class AliPayPayServiceImpl implements AliPayPayService {

	private static final Logger logger = LoggerFactory.getLogger(AliPayPayServiceImpl.class);
	private static final String ALIPAY_TIME_OUT_EXPRESS = "5m";
	private static final String ALIPAY__MICRO_TIME_OUT_EXPRESS = "1m";
	private static final String ALIPAY_CURRENCY = "0156";
	private IOrderService orderService;
	private IAliPayManager aliPayManager;
	private IOrderDao orderDao;
	private IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService;
	private AliPayMerchantSynchService aliPayMerchantSynchService;
	// 支付宝支付场景默认条码支付
	public static final String ALIPAY_SENCE = "bar_code";
	private IMerchantChannelService merchantChannelService;

	/**
	 * 支付宝交易欲创建（获取支付宝二维码）
	 */
	@Override
	public OutputParam aLiPayPreCreate(InputParam input) throws FrameException {

		logger.info("-------------  调用支付宝支付主扫下单流程     START ----------------");

		OutputParam outputParam = new OutputParam();

		try {

			/**************** 1.请求报文非空字段验证 ***********************/
			List<String> list = new ArrayList<String>();
			list.add("orderNumber");
			list.add("orderTime");
			list.add("orderAmount");
			list.add("merId");
			list.add("merName");
			list.add("transType");
			list.add("payType");
			list.add("currencyType");
			list.add("channel");
			list.add("payAccessType");
			list.add("subject");
			list.add("storeId");
			list.add("termId");
			list.add("alipayMerchantId");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("[支付宝支付主扫下单] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 订单号
			String orderNumber = String.format("%s", input.getValue("orderNumber"));

			// 订单时间
			String orderTime = String.format("%s", input.getValue("orderTime"));

			// 交易金额
			String orderAmount = String.format("%s", input.getValue("orderAmount"));

			// 商户号
			String merId = String.format("%s", input.getValue("merId"));

			// 交易类型
			String transType = String.format("%s", input.getValue("transType"));

			// 渠道编号
			String channel = String.format("%s", input.getValue("channel"));

			// 支付接入类型
			String payAccessType = String.format("%s", input.getValue("payAccessType"));

			// 币种
			String currencyType = String.format("%s", input.getValue("currencyType"));

			// 支付类型
			String payType = String.format("%s", input.getValue("payType"));

			// 订单标题
			String subject = String.format("%s", input.getValue("subject"));

			// 商户门店编号
			String storeId = String.format("%s", input.getValue("storeId"));

			// 终端号
			String termId = String.format("%s", input.getValue("termId"));

			// 支付宝商户号
			String alipayMerchantId = String.format("%s", input.getValue("alipayMerchantId"));

			// 可打折金额
			String discountableAmount = String.format("%s", input.getValue("discountableAmount"));

			// 原不可打折金额
			String undiscountableAmount = String.format("%s", input.getValue("undiscountableAmount"));

			// 对交易商品的描述
			String body = String.format("%s", input.getValue("body"));

			// 商品详细信息
			// String goodsDetail = String.format("%s",input.getValue("goodsDetail"));

			// 操作员号
			String operatorId = String.format("%s", input.getValue("operatorId"));

			// 业务扩展参数
			// String extendParams = String.format("%s",input.getValue("extendParams"));

			// 交易有效时间
			String timeoutExpress = String.format("%s", input.getValue("timeoutExpress"));

			// 描述分账信息
			// String royaltyInfo = String.format("%s",input.getValue("royaltyInfo"));

			// 是否支持信用卡
			// String isCredit = String.format("%s",input.getValue("isCredit"));

			// 按输入返回
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderAmount", orderAmount);
			outputParam.putValue("transType", transType);
			outputParam.putValue("currencyType", currencyType);
			outputParam.putValue("payAccessType", payAccessType);
			outputParam.putValue("channel", channel);
			outputParam.putValue("payType", payType);
			outputParam.putValue("merId", merId);

			logger.info("[支付宝主扫下单] 时间校验 orderTime=" + orderTime);
			if (!orderTime.matches("\\d{14}")) {
				logger.error("[支付宝主扫下单] 时间校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("交易时间：[" + orderTime + "]错误");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 金额校验 orderAmount=" + orderAmount);
			if (!orderAmount.matches("\\d{12}")) {
				logger.error("[支付宝主扫下单] 金额校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("金额：[" + orderAmount + "]错误");
				return outputParam;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				logger.error("[支付宝主扫下单],订单金额[orderAmount]非法");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("订单金额[orderAmount]非法");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 交易类型   transType=" + transType);
			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				logger.error("[支付宝主扫下单] 交易类型校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("交易类型：[" + transType + "]错误");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 渠道  channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.error("[支付宝主扫下单] 渠道校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("渠道：[" + channel + "]错误");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 接入类型 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.error("[支付宝主扫下单] 接入类型校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("接入类型：[" + payAccessType + "]错误");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 支付类型  payType=" + payType);
			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				logger.error("[支付宝主扫下单] 支付类型  校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付类型  ：[" + payType + "]错误");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 交易有效期  timeoutExpress=" + timeoutExpress);
			if (StringUtil.isEmpty(timeoutExpress)) {
				timeoutExpress = ALIPAY_TIME_OUT_EXPRESS;
			}

			/************************** 校验是否有重复订单 ********************************/

			logger.info("[支付宝主扫下单]  校验是否有重复订单信息");

			// 订单日期
			String merDt = orderTime.substring(0, 8);

			// 订单时间
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);

			logger.info("[支付宝主扫下单]  查询是否有重复订单      开始");

			OutputParam queryOut = orderService.queryOrder(queryInputParam);

			logger.info("[支付宝主扫下单]   查询是否有重复订单      结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.error("[支付宝主扫下单] 重复订单");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("订单重复");
				return outputParam;
			}

			logger.info("[支付宝主扫下单]  订单重复查询成功，没有重复订单");

			/************************* 添加订单信息 **********************/

			logger.info("[支付宝主扫下单]  新增订单流水信息      开始");

			OutputParam addOut = aliPayManager.addConsumeOrder(input);

			logger.info("[支付宝主扫下单]  新增订单流水信息      结束");

			if (!StringConstans.returnCode.SUCCESS.equals(addOut.getReturnCode())) {
				logger.error("[支付宝主扫下单] 增加订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("增加订单信息失败");
				return outputParam;
			}

			logger.info("[支付宝主扫下单]  新增订单流水信息  成功");

			// 交易流水号
			String txnSeqId = String.format("%s", addOut.getValue("txnSeqId"));

			// 交易日期
			String txnDt = String.format("%s", addOut.getValue("txnDt"));

			// 交易时间
			String txnTm = String.format("%s", addOut.getValue("txnTm"));

			// 外部订单号
			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);

			/************************ 去支付宝下单 **************************/

			InputParam preInput = new InputParam();
			preInput.putParams("merId", merId);
			preInput.putParams("outTradeNo", outTradeNo);
			preInput.putParams("totalAmount", orderAmount);
			preInput.putParams("subject", subject);
			preInput.putParams("storeId", storeId);
			preInput.putParams("termId", termId);
			preInput.putParams("body", body);
			preInput.putParams("operatorId", operatorId);
			preInput.putParams("alipayMerchantId", alipayMerchantId);
			preInput.putParams("timeoutExpress", timeoutExpress);
			preInput.putParams("discountableAmount", discountableAmount);
			preInput.putParams("undiscountableAmount", undiscountableAmount);
			preInput.putParams("notifyUrl", Constants.getParam("resevAlipayNotifyUrl"));

			logger.info("[支付宝主扫下单] 支付宝下单   开始");

			OutputParam preOutput = aliPayManager.toALiPayPreCreate(preInput);

			logger.info("[支付宝主扫下单] 支付宝下单   结束");

			// 支付宝返回二维码
			String qrCode = String.format("%s", preOutput.getReturnObj().get("qrCode"));

			// 返回描述
			String resDesc = String.format("%s%s%s", "支付宝下单失败", ":", preOutput.getReturnMsg());

			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);
			updateInput.putparamString("resDesc", resDesc);

			// 默认下单失败
			outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.putValue("txnTime", txnDt + txnTm);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg(resDesc);

			// 下单成功
			if (StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.info("[支付宝主扫下单] 支付宝下单 成功");
				updateInput.putparamString("codeUrl", qrCode);
				updateInput.putparamString("resDesc", "支付宝下单成功");
				updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_06);
				outputParam.putValue("codeUrl", qrCode);
				outputParam.putValue("respDesc", "支付宝主扫下单成功");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			}

			// 失败
			if (!StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.error("[支付宝主扫下单] 支付宝下单 失败" + resDesc);
				logger.info("[支付宝主扫下单] 支付宝下单 失败" + resDesc);
			}

			logger.info("[支付宝主扫下单] 支付宝下单完成更新订单   开始");

			OutputParam updateOut = orderService.updateOrder(updateInput);

			logger.info("[支付宝主扫下单] 支付宝下单完成更新订单   结束");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.error("[支付宝主扫下单] 支付宝下单完成更新订单 失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付宝下单失败,二维码前置更新订单 失败");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 支付宝下单完成更新订单 成功");

			logger.info("-------------  调用支付宝支付主扫下单流程     END ----------------");

		} catch (Exception e) {
			logger.error("[支付宝主扫下单] 支付宝下单出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝下单出现异常");
		}

		return outputParam;
	}

	public OutputParam aLiPayPreCreateYL(InputParam input) throws FrameException {
		logger.info("支付宝断直连主扫下单START请求报文:" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliPreCreate, "支付宝断直连扫码交易");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String orderNumber = String.format("%s", input.getValue(Dict.orderNumber));
			String orderTime = String.format("%s", input.getValue(Dict.orderTime));
			String orderAmount = String.format("%s", input.getValue(Dict.orderAmount));
			String merId = String.format("%s", input.getValue(Dict.merId));
			String transType = String.format("%s", input.getValue(Dict.transType));
			String channel = String.format("%s", input.getValue(Dict.channel));
			String payAccessType = String.format("%s", input.getValue(Dict.payAccessType));
			String currencyType = String.format("%s", input.getValue(Dict.currencyType));
			String payType = String.format("%s", input.getValue(Dict.payType));
			String subject = String.format("%s", input.getValue(Dict.subject));
			String storeId = String.format("%s", input.getValue(Dict.storeId));
			String termId = String.format("%s", input.getValue(Dict.termId));
			String alipayMerchantId = String.format("%s", input.getValue(Dict.alipayMerchantId));
			String discountableAmount = String.format("%s", input.getValue(Dict.discountableAmount));
			String undiscountableAmount = String.format("%s", input.getValue(Dict.undiscountableAmount));
			String body = String.format("%s", input.getValue(Dict.body));
			// String goodsDetail = String.format("%s",input.getValue(Dict.goodsDetail));
			String operatorId = String.format("%s", input.getValue(Dict.operatorId));
			// String extendParams = String.format("%s",input.getValue(Dict.extendParams));
			String timeoutExpress = String.format("%s", input.getValue(Dict.timeoutExpress));
			// String royaltyInfo = String.format("%s",input.getValue(Dict.royaltyInfo));
			// String isCredit = String.format("%s",input.getValue(Dict.isCredit));
			String rateChannel = String.format("%s", input.getValue(Dict.rateChannel));

			// 按输入返回
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderAmount", orderAmount);
			outputParam.putValue("transType", transType);
			outputParam.putValue("currencyType", currencyType);
			outputParam.putValue("payAccessType", payAccessType);
			outputParam.putValue("channel", channel);
			outputParam.putValue("payType", payType);
			outputParam.putValue("merId", merId);

			logger.debug("[支付宝主扫下单] 时间校验 orderTime=" + orderTime);
			if (!orderTime.matches("\\d{14}")) {
				logger.debug("[支付宝主扫下单] 时间校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("交易时间：[" + orderTime + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 金额校验 orderAmount=" + orderAmount);
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug("[支付宝主扫下单] 金额校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("金额：[" + orderAmount + "]错误");
				return outputParam;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				logger.debug("[支付宝主扫下单],订单金额[orderAmount]非法");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("订单金额[orderAmount]非法");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 交易类型   transType=" + transType);
			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				logger.debug("[支付宝主扫下单] 交易类型校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("交易类型：[" + transType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 渠道  channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[支付宝主扫下单] 渠道校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("渠道：[" + channel + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 接入类型 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.debug("[支付宝主扫下单] 接入类型校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("接入类型：[" + payAccessType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 支付类型  payType=" + payType);
			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				logger.debug("[支付宝主扫下单] 支付类型  校验失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付类型  ：[" + payType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 交易有效期  timeoutExpress=" + timeoutExpress);
			if (StringUtil.isEmpty(timeoutExpress)) {
				timeoutExpress = ALIPAY_TIME_OUT_EXPRESS;
			}

			logger.info("[支付宝主扫下单]  校验是否有重复订单信息");

			// 订单日期
			String merDt = orderTime.substring(0, 8);
			// 订单时间
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);
			logger.debug("[支付宝主扫下单]  查询是否有重复订单开始");
			OutputParam queryOut = orderService.queryOrder(queryInputParam);
			logger.debug("[支付宝主扫下单]   查询是否有重复订单 结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 重复订单");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("订单重复");
				return outputParam;
			}

			/************************* 添加订单信息 **********************/
			input.putParams(Dict.payChannel, StringConstans.PAY_ChANNEL.YLALI);
			logger.debug("[支付宝主扫下单]  新增订单流水信息开始");
			OutputParam addOut = aliPayManager.addConsumeOrder(input);
			logger.debug("[支付宝主扫下单]  新增订单流水信息结束");

			if (!StringConstans.returnCode.SUCCESS.equals(addOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 增加订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("增加订单信息失败");
				return outputParam;
			}

			String txnSeqId = String.format("%s", addOut.getValue(Dict.txnSeqId));
			String txnDt = String.format("%s", addOut.getValue(Dict.txnDt));
			String txnTm = String.format("%s", addOut.getValue(Dict.txnTm));
			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);

			/************************ 去支付宝下单 **************************/

			InputParam preInput = new InputParam();
			preInput.putParams("merId", merId);
			preInput.putParams("outTradeNo", outTradeNo);
			preInput.putParams("totalAmount", orderAmount);
			preInput.putParams("subject", subject);
			preInput.putParams("storeId", storeId);
			preInput.putParams("termId", termId);
			preInput.putParams("body", body);
			preInput.putParams("operatorId", operatorId);
			preInput.putParams("alipayMerchantId", alipayMerchantId);
			preInput.putParams("timeoutExpress", timeoutExpress);
			preInput.putParams("discountableAmount", discountableAmount);
			preInput.putParams("undiscountableAmount", undiscountableAmount);
			// preInput.putParams("notifyUrl", Constants.getParam("resevAlipayNotifyUrl"));
			preInput.putParams("rateChannel", rateChannel);

			logger.debug("[支付宝主扫下单] 支付宝下单开始");
			OutputParam preOutput = aliPayManager.toALiPayPreCreateYL(preInput);
			logger.debug("[支付宝主扫下单] 支付宝下单结束");

			String qrCode = String.format("%s", preOutput.getReturnObj().get(Dict.qrCode));
			String resDesc = String.format("%s%s%s", "支付宝下单失败", ":", preOutput.getReturnMsg());

			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);
			updateInput.putparamString("resDesc", resDesc);

			// 默认下单失败
			outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.putValue("txnTime", txnDt + txnTm);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg(resDesc);

			// 下单成功
			if (StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.info("[支付宝主扫下单] 支付宝下单 成功");
				updateInput.putparamString("codeUrl", qrCode);
				updateInput.putparamString("resDesc", "支付宝下单成功");
				updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_06);
				outputParam.putValue("codeUrl", qrCode);
				outputParam.putValue("respDesc", "支付宝主扫下单成功");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			}

			// 失败
			if (!StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.error("[支付宝主扫下单] 支付宝下单 失败" + resDesc);
				logger.info("[支付宝主扫下单] 支付宝下单 失败" + resDesc);
			}

			logger.info("[支付宝主扫下单] 支付宝下单完成更新订单   开始");

			OutputParam updateOut = orderService.updateOrder(updateInput);

			logger.info("[支付宝主扫下单] 支付宝下单完成更新订单   结束");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.error("[支付宝主扫下单] 支付宝下单完成更新订单 失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付宝下单失败,二维码前置更新订单 失败");
				return outputParam;
			}

			logger.info("[支付宝主扫下单] 支付宝下单完成更新订单 成功");

			logger.info("-------------  调用支付宝支付主扫下单流程     END ----------------");

		} catch (Exception e) {
			logger.error("[支付宝主扫下单] 支付宝下单出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝下单出现异常");
		}

		return outputParam;
	}

	/**
	 * 支付宝后台通知处理
	 */
	@Override
	public OutputParam recvALiPayNotifyReq(InputParam inputParam) throws FrameException {

		logger.info("支付宝后台通知接收 处理流程[START],请求报文:" + inputParam.toString());

		OutputParam outputParam = new OutputParam();

		try {

			// 外部订单号
			String outTradeNo = String.format("%s", inputParam.getValue("out_trade_no"));

			// 开发者appId
			String appId = String.format("%s", inputParam.getValue("app_id"));

			// 退款申请流水号
			String outBizNo = String.format("%s", inputParam.getValue("out_biz_no"));

			logger.debug("[支付宝后台通知处理] 校验外部订单号 outTradeNo=" + outTradeNo);
			if (!outTradeNo.matches("[a-zA-Z]*\\d{24}")) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("外部订单号长度不正确");
				return outputParam;
			}

			// 判断是退款的后台通知还是预下单的后台通知
			if (StringUtil.isEmpty(outBizNo)) {
				logger.debug("[支付宝后台通知处理] 此通知为预下单的后台通知,请求报文:" + inputParam.toString());
				outputParam = this.handAlipayPreCreateNotify(inputParam);
				logger.debug("[支付宝后台通知处理] 此通知为预下单的后台通知,返回报文:" + outputParam.toString());
				if (!StringConstans.returnCode.SUCCESS.equals(outputParam.getReturnCode())) {
					logger.info("[支付宝后台通知处理] 支付宝后台通知处理失败");
					return outputParam;
				}
				/* add by ghl 20180104 */
				if (Configs.getConfigs().getBoolean("is_to_core_synchronous")) {
					InputParam backInput = new InputParam();
					backInput.putParams("txnSta", outputParam.getValue("txnSta"));
					backInput.putParams("alOrder", outputParam.getValue("alOrder"));
					this.toCoreForSettleHandler(backInput);
				}
				/* add end at 20180104 */
			} else {
				logger.debug("[支付宝后台通知处理] 此通知为退货的后台通知");
				outputParam = this.handAlipayRefundNotify(inputParam);
			}

		} catch (Exception e) {
			logger.error("[支付宝后台通知处理] 接收后台通知处理异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("接收后台通知处理异常");
		} finally {
			if (Configs.getConfigs().getBoolean("is_push"))
				this.notfiyThreadHandler(inputParam);
			logger.info("支付宝后台通知接收处理流程出现[END],返回报文:" + outputParam.toString());
		}
		return outputParam;
	}

	@Override
	public OutputParam aLiPayCreate(InputParam input) throws FrameException {
		logger.info("支付宝三码合一交易下单接口START请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		
		OutputParam routing = aliPayMerchantSynchService.routing(input);
		String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
		String rateChannel = StringUtil.toString(routing.getValue(Dict.rateChannel));
		if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
			//间连
			input.putParams(Dict.rateChannel, rateChannel);
			outputParam = this.aLiPayCreateYmtYL(input);
		}else {
			//直连
			outputParam = this.aLiPayCreateYmt(input);
		}
		
		logger.info("支付宝三码合一交易下单接口END返回报文:"+outputParam.toString());
		return outputParam;
		


	}
	
	
	public OutputParam aLiPayCreateYmt(InputParam input) throws FrameException{
		
		OutputParam outputParam = new OutputParam();
		try {

			/**************** 1.请求报文非空字段验证 ***********************/
			List<String> list = new ArrayList<String>();
			list.add("orderNumber");
			list.add("orderTime");
			list.add("orderAmount");
			list.add("merId");
			list.add("merName");
			list.add("transType");
			list.add("payType");
			list.add("currencyType");
			list.add("channel");
			list.add("payAccessType");
			list.add("subject");
			list.add("storeId");
			list.add("alipayMerchantId");
			list.add("buyerId");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[支付宝支付主扫下单] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 订单号
			String orderNumber = String.format("%s", input.getValue("orderNumber"));

			// 订单时间
			String orderTime = String.format("%s", input.getValue("orderTime"));

			// 交易金额
			String orderAmount = String.format("%s", input.getValue("orderAmount"));

			// 商户号
			String merId = String.format("%s", input.getValue("merId"));

			// 交易类型
			String transType = String.format("%s", input.getValue("transType"));

			// 渠道编号
			String channel = String.format("%s", input.getValue("channel"));

			// 支付接入类型
			String payAccessType = String.format("%s", input.getValue("payAccessType"));

			// 币种
			String currencyType = String.format("%s", input.getValue("currencyType"));

			// 支付类型
			String payType = String.format("%s", input.getValue("payType"));

			// 订单标题
			String subject = String.format("%s", input.getValue("subject"));

			// 商户门店编号
			String storeId = String.format("%s", input.getValue("storeId"));

			// 终端号
			String termId = String.format("%s", input.getValue("termId"));

			// 支付宝商户号
			String alipayMerchantId = String.format("%s", input.getValue("alipayMerchantId"));

			// 可打折金额
			String discountableAmount = String.format("%s", input.getValue("discountableAmount"));

			// 原不可打折金额
			String undiscountableAmount = String.format("%s", input.getValue("undiscountableAmount"));

			// 对交易商品的描述
			String body = String.format("%s", input.getValue("body"));

			// 操作员号
			String operatorId = String.format("%s", input.getValue("operatorId"));

			// 交易有效时间
			String timeoutExpress = String.format("%s", input.getValue("timeoutExpress"));

			String buyerId = String.format("%s", input.getValue("buyerId"));

			// 按输入返回
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderAmount", orderAmount);
			outputParam.putValue("transType", transType);
			outputParam.putValue("currencyType", currencyType);
			outputParam.putValue("payAccessType", payAccessType);
			outputParam.putValue("channel", channel);
			outputParam.putValue("payType", payType);
			outputParam.putValue("merId", merId);

			logger.debug("[支付宝主扫下单] 时间校验 orderTime=" + orderTime);
			if (!orderTime.matches("\\d{14}")) {
				logger.debug("[支付宝主扫下单] 时间校验失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易时间：[" + orderTime + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 金额校验 orderAmount=" + orderAmount);
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug("[支付宝主扫下单] 金额校验失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "金额：[" + orderAmount + "]错误");
				return outputParam;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				logger.debug("[支付宝主扫下单],订单金额[orderAmount]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "订单金额[orderAmount]非法");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 交易类型   transType=" + transType);
			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				logger.debug("[支付宝主扫下单] 交易类型校验失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易类型：[" + transType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 渠道  channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				logger.debug("[支付宝主扫下单] 渠道校验失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道：[" + channel + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 接入类型 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.debug("[支付宝主扫下单] 接入类型校验失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "接入类型：[" + payAccessType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 支付类型  payType=" + payType);
			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				logger.debug("[支付宝主扫下单] 支付类型  校验失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付类型  ：[" + payType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 交易有效期  timeoutExpress=" + timeoutExpress);
			if (StringUtil.isEmpty(timeoutExpress)) {
				timeoutExpress = ALIPAY_TIME_OUT_EXPRESS;
			}

			/************************** 校验是否有重复订单 ********************************/

			logger.debug("[支付宝主扫下单]  校验是否有重复订单信息");

			// 订单日期
			String merDt = orderTime.substring(0, 8);

			// 订单时间
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);

			logger.debug("[支付宝主扫下单]  查询是否有重复订单      开始");

			OutputParam queryOut = orderService.queryOrder(queryInputParam);

			logger.debug("[支付宝主扫下单]   查询是否有重复订单      结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 重复订单");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_06);
				outputParam.putValue("respDesc", "订单重复");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单]  订单重复查询成功，没有重复订单");

			/********************* 查询商户费率 **********************************/
			InputParam queryThreeCodeInput = new InputParam();
			queryThreeCodeInput.putparamString("merId", merId);
			queryThreeCodeInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);

			logger.debug("[支付宝支付统一下单 -- 商户费率查询] 根据merId查询三码合一相关信息  开始");

			OutputParam queryOutput = threeCodeStaticQRCodeDataService
					.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

			logger.debug("[支付宝支付统一下单 -- 商户费率查询] 根据merId查询三码合一相关信息  结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())) {
				if (StringUtil.isEmpty(queryOutput.getValue("bankFeeRate"))) {
					input.putParams("bankFeeRate", StringConstans.DefaultFeeRate.ALIPAY_FEE_RATE);
				} else {
					input.putParams("bankFeeRate", queryOutput.getValue("bankFeeRate"));
				}
				if (StringUtil.isEmpty(queryOutput.getValue("settleMethod"))) {
					input.putParams("settleMethod", StringConstans.SettleMethod.SETTLEMETHOD1);
				} else {
					input.putParams("settleMethod", queryOutput.getValue("settleMethod"));
				}
			} else {
				logger.debug("[支付宝支付统一下单，商户信息不存在]");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("商户信息不存在");
				return outputParam;
			}

			/************************* 添加订单信息 **********************/

			logger.debug("[支付宝主扫下单]  新增订单流水信息      开始");

			OutputParam addOut = aliPayManager.addConsumeOrder(input);

			logger.debug("[支付宝主扫下单]  新增订单流水信息      结束");

			if (!StringConstans.returnCode.SUCCESS.equals(addOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 增加订单信息失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "增加订单信息失败");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单]  新增订单流水信息  成功");

			// 交易流水号
			String txnSeqId = String.format("%s", addOut.getValue("txnSeqId"));

			// 交易日期
			String txnDt = String.format("%s", addOut.getValue("txnDt"));

			// 交易时间
			String txnTm = String.format("%s", addOut.getValue("txnTm"));

			// 外部订单号
			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);

			/************************ 去支付宝下单 **************************/

			InputParam preInput = new InputParam();
			preInput.putParams("outTradeNo", outTradeNo);
			preInput.putParams("totalAmount", orderAmount);
			preInput.putParams("subject", subject);
			preInput.putParams("storeId", storeId);
			preInput.putParams("termId", termId);
			preInput.putParams("body", body);
			preInput.putParams("operatorId", operatorId);
			preInput.putParams("alipayMerchantId", alipayMerchantId);
			preInput.putParams("timeoutExpress", timeoutExpress);
			preInput.putParams("discountableAmount", discountableAmount);
			preInput.putParams("undiscountableAmount", undiscountableAmount);
			preInput.putParams("notifyUrl", Constants.getParam("resevAlipayNotifyUrl"));
			preInput.putParams("buyerId", buyerId);

			logger.debug("[支付宝主扫下单] 支付宝下单   开始 preInput(" + preInput.toString() + ")");

			OutputParam preOutput = aliPayManager.toALiPayCreate(preInput);

			logger.debug("[支付宝主扫下单] 支付宝下单   结束 preOutput(" + preOutput.toString() + ")");

			// 支付宝返回二维码
			String tradeNo = String.format("%s", preOutput.getReturnObj().get("tradeNo"));

			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);

			// 默认下单失败
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", preOutput.getReturnMsg());

			// 下单成功
			if (StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 支付宝下单 成功");
				updateInput.putparamString("alipayPrepayId", tradeNo);
				updateInput.putparamString("resDesc", "支付宝下单成功");
				updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_06);
				outputParam.putValue("tradeNo", tradeNo);
				outputParam.putValue("txnSeqId", txnSeqId);
				outputParam.putValue("txnTime", txnDt + txnTm);
				outputParam.putValue("respDesc", "支付宝主扫下单成功");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			}

			// 失败
			if (!StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 支付宝下单 失败");
				updateInput.putparamString("resDesc", "支付宝下单失败:" + preOutput.getReturnMsg());
			}

			logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单   开始");

			OutputParam updateOut = orderService.updateOrder(updateInput);

			logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单   结束 updateOut(" + updateOut.toString() + ")");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单 失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "更新订单失败");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单 成功");

			logger.debug("-------------  调用支付宝支付主扫下单流程     END ----------------");

		} catch (Exception e) {
			logger.error("[支付宝主扫下单] 支付宝下单出现异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "支付宝下单出现异常");
		}
		logger.info("-------------  调用支付宝支付主扫下单流程     END ----------------outputParam(" + outputParam.toString() + ")");
		return outputParam;
	}
	
	
	public OutputParam aLiPayCreateYmtYL(InputParam input) throws FrameException{
		logger.info("[间联]支付宝一码通交易下单请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliYmt, "支付宝断直连一码通交易");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String orderNumber = String.format("%s", input.getValue("orderNumber"));
			String orderTime = String.format("%s", input.getValue("orderTime"));
			String orderAmount = String.format("%s", input.getValue("orderAmount"));
			String merId = String.format("%s", input.getValue("merId"));
			String transType = String.format("%s", input.getValue("transType"));
			String channel = String.format("%s", input.getValue("channel"));
			String payAccessType = String.format("%s", input.getValue("payAccessType"));
			String currencyType = String.format("%s", input.getValue("currencyType"));
			String payType = String.format("%s", input.getValue("payType"));
			String subject = String.format("%s", input.getValue("subject"));
			String storeId = String.format("%s", input.getValue("storeId"));
			String termId = String.format("%s", input.getValue("termId"));
			String alipayMerchantId = String.format("%s", input.getValue("alipayMerchantId"));
			String discountableAmount = String.format("%s", input.getValue("discountableAmount"));
			String undiscountableAmount = String.format("%s", input.getValue("undiscountableAmount"));
			String body = String.format("%s", input.getValue("body"));
			String operatorId = String.format("%s", input.getValue("operatorId"));
			String timeoutExpress = StringUtil.toString(input.getValue("timeoutExpress"),ALIPAY_TIME_OUT_EXPRESS);
			String buyerId = String.format("%s", input.getValue("buyerId"));
			String rateChannel = String.format("%s", input.getValue(Dict.rateChannel));
			
			// 按输入返回
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderAmount", orderAmount);
			outputParam.putValue("transType", transType);
			outputParam.putValue("currencyType", currencyType);
			outputParam.putValue("payAccessType", payAccessType);
			outputParam.putValue("channel", channel);
			outputParam.putValue("payType", payType);
			outputParam.putValue("merId", merId);

			logger.debug("[支付宝主扫下单] 时间校验 orderTime=" + orderTime);
			if (!orderTime.matches("\\d{14}")) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易时间：[" + orderTime + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 金额校验 orderAmount=" + orderAmount);
			if (!orderAmount.matches("\\d{12}")) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "金额：[" + orderAmount + "]错误");
				return outputParam;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "订单金额[orderAmount]非法");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 交易类型   transType=" + transType);
			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易类型：[" + transType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 渠道  channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道：[" + channel + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 接入类型 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "接入类型：[" + payAccessType + "]错误");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单] 支付类型  payType=" + payType);
			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付类型  ：[" + payType + "]错误");
				return outputParam;
			}

			/************************** 校验是否有重复订单 ********************************/

			String merDt = orderTime.substring(0, 8);
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);

			logger.debug("[支付宝主扫下单]查询是否有重复订单开始");
			OutputParam queryOut = orderService.queryOrder(queryInputParam);
			logger.debug("[支付宝主扫下单]查询是否有重复订单结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_06);
				outputParam.putValue("respDesc", "订单重复");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单]  订单重复查询成功，没有重复订单");

			/********************* 查询商户费率 **********************************/
			InputParam queryThreeCodeInput = new InputParam();
			queryThreeCodeInput.putparamString("merId", merId);
			queryThreeCodeInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);

			logger.debug("[支付宝支付统一下单 -- 商户费率查询] 根据merId查询三码合一相关信息  开始");
			OutputParam queryOutput = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);
			logger.debug("[支付宝支付统一下单 -- 商户费率查询] 根据merId查询三码合一相关信息  结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOutput.getReturnCode())) {
				if (StringUtil.isEmpty(queryOutput.getValue("bankFeeRate"))) {
					input.putParams("bankFeeRate", StringConstans.DefaultFeeRate.ALIPAY_FEE_RATE);
				} else {
					input.putParams("bankFeeRate", queryOutput.getValue("bankFeeRate"));
				}
				if (StringUtil.isEmpty(queryOutput.getValue("settleMethod"))) {
					input.putParams("settleMethod", StringConstans.SettleMethod.SETTLEMETHOD1);
				} else {
					input.putParams("settleMethod", queryOutput.getValue("settleMethod"));
				}
			} else {
				logger.debug("[支付宝支付统一下单，商户信息不存在]");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("商户信息不存在");
				return outputParam;
			}

			/************************* 添加订单信息 **********************/
			input.putParams(Dict.payChannel, StringConstans.PAY_ChANNEL.YLALI);
			logger.debug("[支付宝主扫下单]  新增订单流水信息      开始");
			OutputParam addOut = aliPayManager.addConsumeOrder(input);
			logger.debug("[支付宝主扫下单]  新增订单流水信息      结束");

			if (!StringConstans.returnCode.SUCCESS.equals(addOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 增加订单信息失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "增加订单信息失败");
				return outputParam;
			}

			logger.debug("[支付宝主扫下单]  新增订单流水信息  成功");

			String txnSeqId = String.format("%s", addOut.getValue("txnSeqId"));
			String txnDt = String.format("%s", addOut.getValue("txnDt"));
			String txnTm = String.format("%s", addOut.getValue("txnTm"));
			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);

			/************************ 去支付宝下单 **************************/

			InputParam preInput = new InputParam();
			preInput.putParams("outTradeNo", outTradeNo);
			preInput.putParams("totalAmount", orderAmount);
			preInput.putParams("subject", subject);
			preInput.putParams("storeId", storeId);
			preInput.putParams("termId", termId);
			preInput.putParams("body", body);
			preInput.putParams("operatorId", operatorId);
			preInput.putParams("alipayMerchantId", alipayMerchantId);
			preInput.putParams("timeoutExpress", timeoutExpress);
			preInput.putParams("discountableAmount", discountableAmount);
			preInput.putParams("undiscountableAmount", undiscountableAmount);
			preInput.putParams("notifyUrl", Constants.getParam("resevAlipayNotifyUrl"));
			preInput.putParams("buyerId", buyerId);
			preInput.putParams(Dict.rateChannel, rateChannel);

			logger.debug("[支付宝主扫下单] 支付宝下单   开始");
			OutputParam preOutput = aliPayManager.toALiPayCreateYL(preInput);
			logger.debug("[支付宝主扫下单] 支付宝下单   结束");

			String tradeNo = String.format("%s", preOutput.getReturnObj().get("tradeNo"));
			String returnMsg = preOutput.getReturnMsg();

			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);
			updateInput.putparamString("resDesc", returnMsg);

			// 默认下单失败
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", returnMsg);
			
			if (StringConstans.returnCode.SUCCESS.equals(preOutput.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 支付宝下单 成功");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
				outputParam.putValue("tradeNo", tradeNo);
				outputParam.putValue("txnSeqId", txnSeqId);
				outputParam.putValue("txnTime", txnDt + txnTm);
				outputParam.putValue("respDesc", "支付宝主扫下单成功");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				
				updateInput.putparamString("alipayPrepayId", tradeNo);
				updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_06);
			}

			logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单   开始");
			OutputParam updateOut = orderService.updateOrder(updateInput);
			logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单   结束 updateOut(" + updateOut.toString() + ")");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.debug("[支付宝主扫下单] 支付宝下单完成更新订单 失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "更新订单失败");
				return outputParam;
			}

		} catch (Exception e) {
			logger.error("[间联]支付宝一码通交易下单出现异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "[间联]支付宝一码通交易下单出现异常");
		} finally {
			logger.info("[间联]支付宝一码通交易下单返回报文:"+outputParam.toString());
		}
		return outputParam;
	
	}

	/**
	 * 支付宝被扫支付入口
	 */
	@Override
	public OutputParam aLiPayMicroPay(InputParam input) throws FrameException {

		logger.info("[调用支付宝被扫支付流程处理 ]    START" + input.toString());

		OutputParam out = new OutputParam();

		try {

			/********************* 请求报文非空验证 ************************/
			List<String> list = new ArrayList<String>();
			list.add("orderNumber");
			list.add("orderTime");
			list.add("merId");
			list.add("merName");
			list.add("scene");
			list.add("authCode");
			list.add("subject");
			list.add("storeId");
			list.add("alipayMerchantId");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[支付宝被扫交易] 请求报文字段[" + nullStr + "]不能为空");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			// 交易金额
			String orderAmount = String.format("%s", input.getValue("orderAmount"));
			// 交易时间
			String orderTime = String.format("%s", input.getValue("orderTime"));
			// 订单号
			String orderNumber = String.format("%s", input.getValue("orderNumber"));
			// 交易类型
			String transType = String.format("%s", input.getValue("transType"));
			// 商户编号
			String merId = String.format("%s", input.getValue("merId"));
			// 交易渠道
			String channel = String.format("%s", input.getValue("channel"));
			// 支付接入类型
			String payAccessType = String.format("%s", input.getValue("payAccessType"));
			// 币种
			String currencyType = String.format("%s", input.getValue("currencyType"));
			// 支付类型
			String payType = String.format("%s", input.getValue("payType"));
			// 支付场景
			String scene = String.format("%s", input.getValue("scene"));
			// 授权码
			String authCode = String.format("%s", input.getValue("authCode"));
			// 可打折金额
			String discountableAmount = String.format("%s", input.getValue("discountableAmount"));
			// 不可打折金额
			String undiscountableAmount = String.format("%s", input.getValue("undiscountableAmount"));
			// 订单标题
			String subject = String.format("%s", input.getValue("subject"));
			// 商品描述
			String body = String.format("%s", input.getValue("body"));
			// 商品信息列表
			// String goodsDetail = String.format("%s", input.getValue("goodsDetail"));
			// 操作员编号
			String operatorId = String.format("%s", input.getValue("operatorId"));
			// 门店编号
			String storeId = String.format("%s", input.getValue("storeId"));
			// 终端编号
			String termId = String.format("%s", input.getValue("termId"));
			// 业务扩展参数
			// String extendParams = String.format("%s", input.getValue("extendParams"));
			// 允许最晚付款时间
			String timeoutExpress = String.format("%s", input.getValue("timeoutExpress"));
			// 分账信息
			// String royaltyInfo = String.format("%s", input.getValue("royaltyInfo"));
			// 支付宝二级商户信息
			String alipayMerchantId = String.format("%s", input.getValue("alipayMerchantId"));
			// 支付宝门店编号
			String alipayStoreId = String.format("%s", input.getValue("alipayStoreId"));
			// 是否使用信用卡
			// String isCredit = String.format("%s", input.getValue("isCredit"));

			// 组织应答报文必返参数
			out.putValue("channel", channel);
			out.putValue("payAccessType", payAccessType);
			out.putValue("payType", payType);
			out.putValue("merId", merId);
			out.putValue("currencyType", currencyType);
			out.putValue("orderAmount", orderAmount);
			out.putValue("orderTime", orderTime);
			out.putValue("orderNumber", orderNumber);
			out.putValue("transType", transType);

			/********************* 校验参数合法性 ************************/
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug("[支付宝支付被扫] 订单金额校验失败");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("金额：[" + orderAmount + "]错误");
				return out;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				logger.debug("[支付宝支付被扫],订单金额[" + orderAmount + "]非法");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("订单金额[" + orderAmount + "]非法");
				return out;
			}

			if (!orderTime.matches("\\d{14}")) {
				logger.debug("外部订单时间 [" + orderTime + "] 非法");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("外部订单时间 [" + orderTime + "] 非法");
				return out;
			}

			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				logger.debug("[支付宝支付被扫] 交易类型  [" + transType + "] 错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易类型  [" + transType + "] 错误");
				return out;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.debug("[支付宝支付被扫],交易接入类型：[" + payAccessType + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易接入类型[" + payAccessType + "]错误");
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[支付宝支付被扫],渠道编号：[" + channel + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("渠道编号[" + channel + "]错误");
				return out;
			}

			if (!StringConstans.CurrencyCode.CNY.equals(currencyType)) {
				logger.debug("[支付宝支付被扫],币种：[" + currencyType + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("币种 [" + currencyType + "]错误");
				return out;
			}

			if (!StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				logger.debug("[支付宝支付被扫],支付类型：[" + payType + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付类型 [" + payType + "]错误");
				return out;
			}

			if (!ALIPAY_SENCE.equals(scene)) {
				logger.debug("[支付宝支付被扫],支付场景：[" + scene + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付场景 [" + scene + "]错误");
				return out;
			}

			if (StringUtil.isEmpty(timeoutExpress)) {
				logger.debug("[支付宝支付被扫] 交易有效期为空，设置默认有效期1m");
				timeoutExpress = ALIPAY__MICRO_TIME_OUT_EXPRESS;
			}

			/********************* 重复订单验证 ************************/
			logger.debug("[支付宝被扫支付] 原订单信息校验---------");

			// 订单日期
			String merDt = orderTime.substring(0, 8);
			// 订单时间
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);

			logger.debug("[支付宝被扫支付] 查询是否有重复订单              开始");

			OutputParam queryOut = orderService.queryOrder(queryInputParam);

			logger.debug("[支付宝被扫支付] 查询是否有重复订单              结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[支付宝被扫支付] 被扫支付交易订单重复");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付交易订单重复，请核对后重试!");
				return out;
			}

			logger.debug("[支付宝被扫支付]  新增订单流水信息      开始");

			OutputParam orderInsertOut = aliPayManager.addConsumeOrder(input);

			logger.debug("[支付宝被扫支付]  新增订单流水信息      完成");

			if (!StringConstans.returnCode.SUCCESS.equals(orderInsertOut.getReturnCode())) {
				logger.debug("[支付宝被扫支付] 新增订单流水信息失败!");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("新增订单流水信息失败!");
				return out;
			}

			// 交易流水号
			String txnSeqId = String.format("%s", orderInsertOut.getValue("txnSeqId"));

			// 交易日期
			String txnDt = String.format("%s", orderInsertOut.getValue("txnDt"));

			// 交易时间
			String txnTm = String.format("%s", orderInsertOut.getValue("txnTm"));

			// 外部订单号
			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);

			/********************* 调用支付宝交易支付接口 ************************/
			InputParam scanedPayInput = new InputParam();
			scanedPayInput.putParams("merId", merId);
			scanedPayInput.putParams("outTradeNo", outTradeNo);
			scanedPayInput.putParams("scene", scene);
			scanedPayInput.putParams("authCode", authCode);
			scanedPayInput.putParams("orderAmount", orderAmount);
			scanedPayInput.putParams("discountableAmount", discountableAmount);
			scanedPayInput.putParams("undiscountableAmount", undiscountableAmount);
			scanedPayInput.putParams("subject", subject);
			scanedPayInput.putParams("body", body);
			scanedPayInput.putParams("operatorId", operatorId);
			scanedPayInput.putParams("storeId", storeId);
			scanedPayInput.putParams("termId", termId);
			scanedPayInput.putParams("alipayStoreId", alipayStoreId);
			scanedPayInput.putParams("timeoutExpress", timeoutExpress);
			scanedPayInput.putParams("alipayMerchantId", alipayMerchantId);

			logger.debug("[支付宝被扫支付] 开始调用支付宝支付交易接口-------");
			OutputParam scanedPayOut = aliPayManager.toPayALiPayOrder(scanedPayInput);
			logger.debug("[支付宝被扫支付] 调用支付宝支付交易接口完成-------,返回报文:" + scanedPayOut.toString());

			/********************* 更新订单信息 ************************/
			// 交易状态
			String txnSta = String.format("%s", scanedPayOut.getValue("txnSta"));
			// logger.info("[支付宝被扫支付] 支付宝被扫完成交易状态 txnSta=" + txnSta);

			// 交易状态描述
			String resDesc = String.format("%s", scanedPayOut.getValue("resDesc"));
			// logger.info("[支付宝被扫支付] 支付宝被扫完成交易状态描述 resDesc=" + resDesc);

			// 支付宝订单号
			String alipayTradeNo = String.format("%s", scanedPayOut.getValue("alipayTradeNo"));
			// logger.info("[支付宝被扫支付] 支付宝被扫完成支付宝订单号 alipayTradeNo=" + alipayTradeNo);

			// 实收金额
			String receiptAmount = String.format("%s", scanedPayOut.getValue("receiptAmount"));
			// logger.info("[支付宝被扫支付] 支付宝被扫完成实收金额 receiptAmount=" + receiptAmount);

			// 清算日期
			String settleDate = String.format("%s", scanedPayOut.getValue("settleDate"));
			// logger.info("[支付宝被扫支付] 支付宝被扫完成清算日期 settleDate=" + settleDate);

			// 买家支付宝Id
			String buyerLogonId = String.format("%s", scanedPayOut.getValue("buyerLogonId"));

			// 付款方式
			String bankType = "";
			if (!StringUtil.isEmpty(scanedPayOut.getValue("fundBillList"))
					&& ((List) scanedPayOut.getValue("fundBillList")).size() > 0) {
				bankType = String.format("%s",
						((TradeFundBill) ((List) scanedPayOut.getValue("fundBillList")).get(0)).getFundChannel());
			}

			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", txnSta);
			updateInput.putparamString("resDesc", "交易成功");

			if (!StringUtil.isEmpty(alipayTradeNo)) {
				updateInput.putparamString("alipayTradeNo", alipayTradeNo);
			}

			if (!StringUtil.isEmpty(receiptAmount)) {
				updateInput.putparamString("receiptAmount", receiptAmount);
			}

			if (!StringUtil.isEmpty(buyerLogonId)) {
				updateInput.putparamString("payerid", buyerLogonId);
			}

			if (!StringUtil.isEmpty(bankType)) {
				updateInput.putparamString("bankType", bankType);
			}

			out.putValue("txnSeqId", txnSeqId);
			out.putValue("txnTime", txnDt + txnTm);

			// 交易失败 将交易码返回
			if (!StringConstans.returnCode.SUCCESS.equals(scanedPayOut.getReturnCode())) {
				logger.debug("[支付宝被扫支付] 支付宝被扫支付失败" + resDesc);
				out.setReturnCode(txnSta);
				out.setReturnMsg(resDesc);
				updateInput.putparamString("resDesc", resDesc);
			}

			// 交易成功
			if (StringConstans.returnCode.SUCCESS.equals(scanedPayOut.getReturnCode())) {
				logger.debug("[支付宝被扫支付] 支付宝被扫支付成功");
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				out.setReturnMsg("支付宝被扫支付成功");
				out.putValue("alipayTradeNo", alipayTradeNo);
				out.putValue("alipayPayDate", settleDate);
				out.putValue("receiptAmount", receiptAmount);
				out.putValue("buyerLogonId", buyerLogonId);
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				out.putValue("respDesc", resDesc);

				updateInput.putparamString("settleDate", settleDate);
			}

			logger.debug("[支付宝被扫支付] 更新支付订单信息   开始-------");

			OutputParam updateOut = orderService.updateOrder(updateInput);

			logger.debug("[支付宝被扫支付] 更新支付订单信息   完成-------");
		} catch (Exception e) {
			logger.error("[支付宝被扫支付] 支付宝被扫交易支付处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("支付宝被扫交易支付处理出现异常");
		} finally {
			logger.info("[调用支付宝被扫支付流程处理 ]    END" + out.toString());
		}
		return out;
	}

	public OutputParam aLiPayMicroPayYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝被扫请求报文:" + input.toString());
		OutputParam out = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliMicroPay, "[间联]支付宝被扫");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				out.setReturnCode(valiOut.getReturnCode());
				out.setReturnMsg(valiOut.getReturnMsg());
				return out;
			}

			String orderAmount = String.format("%s", input.getValue(Dict.orderAmount));
			String orderTime = String.format("%s", input.getValue(Dict.orderTime));
			String orderNumber = String.format("%s", input.getValue(Dict.orderNumber));
			String transType = String.format("%s", input.getValue(Dict.transType));
			String merId = String.format("%s", input.getValue(Dict.merId));
			String channel = String.format("%s", input.getValue(Dict.channel));
			String payAccessType = String.format("%s", input.getValue(Dict.payAccessType));
			String currencyType = String.format("%s", input.getValue(Dict.currencyType));
			String payType = String.format("%s", input.getValue(Dict.payType));
			String scene = String.format("%s", input.getValue(Dict.scene));
			String authCode = String.format("%s", input.getValue(Dict.authCode));
			String discountableAmount = String.format("%s", input.getValue(Dict.discountableAmount));
			String undiscountableAmount = String.format("%s", input.getValue(Dict.undiscountableAmount));
			String subject = String.format("%s", input.getValue(Dict.subject));
			String body = String.format("%s", input.getValue(Dict.body));
			// String goodsDetail = String.format("%s", input.getValue(Dict.goodsDetail));
			String operatorId = String.format("%s", input.getValue(Dict.operatorId));
			String storeId = String.format("%s", input.getValue(Dict.storeId));
			String termId = String.format("%s", input.getValue(Dict.termId));
			// String extendParams = String.format("%s", input.getValue(Dict.extendParams));
			String timeoutExpress = String.format("%s", input.getValue(Dict.timeoutExpress));
			// String royaltyInfo = String.format("%s", input.getValue(Dict.royaltyInfo));
			String alipayMerchantId = String.format("%s", input.getValue(Dict.alipayMerchantId));
			String alipayStoreId = String.format("%s", input.getValue(Dict.alipayStoreId));
			// String isCredit = String.format("%s", input.getValue(Dict.isCredit));
			String rateChannel = String.format("%s", input.getValue(Dict.rateChannel));

			// 组织应答报文必返参数
			out.putValue("channel", channel);
			out.putValue("payAccessType", payAccessType);
			out.putValue("payType", payType);
			out.putValue("merId", merId);
			out.putValue("currencyType", currencyType);
			out.putValue("orderAmount", orderAmount);
			out.putValue("orderTime", orderTime);
			out.putValue("orderNumber", orderNumber);
			out.putValue("transType", transType);

			/********************* 校验参数合法性 ************************/
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug("[支付宝支付被扫] 订单金额校验失败");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("金额：[" + orderAmount + "]错误");
				return out;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				logger.debug("[支付宝支付被扫],订单金额[" + orderAmount + "]非法");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("订单金额[" + orderAmount + "]非法");
				return out;
			}

			if (!orderTime.matches("\\d{14}")) {
				logger.debug("外部订单时间 [" + orderTime + "] 非法");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("外部订单时间 [" + orderTime + "] 非法");
				return out;
			}

			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				logger.debug("[支付宝支付被扫] 交易类型  [" + transType + "] 错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易类型  [" + transType + "] 错误");
				return out;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				logger.debug("[支付宝支付被扫],交易接入类型：[" + payAccessType + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易接入类型[" + payAccessType + "]错误");
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[支付宝支付被扫],渠道编号：[" + channel + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("渠道编号[" + channel + "]错误");
				return out;
			}

			if (!StringConstans.CurrencyCode.CNY.equals(currencyType)) {
				logger.debug("[支付宝支付被扫],币种：[" + currencyType + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("币种 [" + currencyType + "]错误");
				return out;
			}

			if (!StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				logger.debug("[支付宝支付被扫],支付类型：[" + payType + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付类型 [" + payType + "]错误");
				return out;
			}

			if (!ALIPAY_SENCE.equals(scene)) {
				logger.debug("[支付宝支付被扫],支付场景：[" + scene + "]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付场景 [" + scene + "]错误");
				return out;
			}

			if (StringUtil.isEmpty(timeoutExpress)) {
				logger.debug("[支付宝支付被扫] 交易有效期为空，设置默认有效期1m");
				timeoutExpress = ALIPAY__MICRO_TIME_OUT_EXPRESS;
			}

			/********************* 重复订单验证 ************************/

			// 订单日期
			String merDt = orderTime.substring(0, 8);
			// 订单时间
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);

			logger.debug("[间联]支付宝被扫查询是否有重复订单开始");
			OutputParam queryOut = orderService.queryOrder(queryInputParam);
			logger.debug("[间联]支付宝被扫查询是否有重复订单结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[间联]支付宝被扫交易订单重复");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付交易订单重复，请核对后重试!");
				return out;
			}

			input.putParams(Dict.payChannel, StringConstans.PAY_ChANNEL.YLALI);
			logger.debug("[间联]支付宝被扫新增订单流水信息开始");
			OutputParam orderInsertOut = aliPayManager.addConsumeOrder(input);
			logger.debug("[间联]支付宝被扫新增订单流水信息完成");

			if (!StringConstans.returnCode.SUCCESS.equals(orderInsertOut.getReturnCode())) {
				logger.debug("[间联]支付宝被扫新增订单流水信息失败!");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[间联]支付宝被扫新增订单流水信息失败!");
				return out;
			}

			String txnSeqId = String.format("%s", orderInsertOut.getValue(Dict.txnSeqId));
			String txnDt = String.format("%s", orderInsertOut.getValue(Dict.txnDt));
			String txnTm = String.format("%s", orderInsertOut.getValue(Dict.txnTm));
			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);

			/********************* 调用支付宝交易支付接口 ************************/
			InputParam scanedPayInput = new InputParam();
			scanedPayInput.putParams("merId", merId);
			scanedPayInput.putParams("outTradeNo", outTradeNo);
			scanedPayInput.putParams("scene", scene);
			scanedPayInput.putParams("authCode", authCode);
			scanedPayInput.putParams("orderAmount", orderAmount);
			scanedPayInput.putParams("discountableAmount", discountableAmount);
			scanedPayInput.putParams("undiscountableAmount", undiscountableAmount);
			scanedPayInput.putParams("subject", subject);
			scanedPayInput.putParams("body", body);
			scanedPayInput.putParams("operatorId", operatorId);
			scanedPayInput.putParams("storeId", storeId);
			scanedPayInput.putParams("termId", termId);
			scanedPayInput.putParams("alipayStoreId", alipayStoreId);
			scanedPayInput.putParams("timeoutExpress", timeoutExpress);
			scanedPayInput.putParams("alipayMerchantId", alipayMerchantId);
			scanedPayInput.putParams(Dict.rateChannel, rateChannel);

			logger.debug("[间联]支付宝被扫开始调用支付宝支付交易接口start");
			OutputParam scanedPayOut = aliPayManager.toPayALiPayOrderYL(scanedPayInput);
			logger.debug("[间联]支付宝被扫开始调用支付宝支付交易接口end");

			/********************* 更新订单信息 ************************/

			String txnSta = String.format("%s", scanedPayOut.getValue(Dict.txnSta));
			String resDesc = String.format("%s", scanedPayOut.getValue(Dict.resDesc));
			String alipayTradeNo = String.format("%s", scanedPayOut.getValue(Dict.alipayTradeNo));
			String receiptAmount = String.format("%s", scanedPayOut.getValue(Dict.receiptAmount));
			String settleDate = String.format("%s", scanedPayOut.getValue(Dict.settleDate));
			String buyerLogonId = String.format("%s", scanedPayOut.getValue(Dict.buyerLogonId));

			// 付款方式
			String bankType = "";
			String fundBillList = StringUtil.toString(scanedPayOut.getValue("fundBillList"));
			if (Util.isJsonArray(fundBillList)) {
				bankType = StringUtil.toString(
						JSONObject.fromObject(JSONArray.fromObject(fundBillList).get(0)).get(Dict.fund_channel));
			}

			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", txnSta);
			updateInput.putparamString("resDesc", resDesc);
			updateInput.putparamStringRemoveNull("alipayTradeNo", alipayTradeNo);
			updateInput.putparamStringRemoveNull("receiptAmount", receiptAmount);
			updateInput.putparamStringRemoveNull("payerid", buyerLogonId);
			updateInput.putparamStringRemoveNull("bankType", bankType);

			out.putValue("txnSeqId", txnSeqId);
			out.putValue("txnTime", txnDt + txnTm);
			out.putValue("respCode", txnSta);
			out.putValue("respDesc", resDesc);

			if (StringConstans.returnCode.SUCCESS.equals(scanedPayOut.getReturnCode())) {
				logger.debug("[间联]支付宝被扫支付成功");
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				out.setReturnMsg(resDesc);
				out.putValue("alipayTradeNo", alipayTradeNo);
				out.putValue("alipayPayDate", settleDate);
				out.putValue("receiptAmount", receiptAmount);
				out.putValue("buyerLogonId", buyerLogonId);
				updateInput.putparamString("settleDate", settleDate);
			} else {
				out.setReturnCode(txnSta);
				out.setReturnMsg(resDesc);
			}

			logger.debug("[间联]支付宝被扫更新支付订单信息开始");
			OutputParam updateOut = orderService.updateOrder(updateInput);
			logger.debug("[间联]支付宝被扫更新支付订单信息完成");

		} catch (Exception e) {
			logger.error("[间联]支付宝被扫交易支付处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[间联]支付宝被扫交易支付处理异常");
		} finally {
			logger.info("[间联]支付宝被扫返回报文:" + out.toString());
		}
		return out;
	}

	/**
	 * 支付宝关闭订单
	 */
	@Override
	public OutputParam aLiPayCloseOrder(InputParam input) throws FrameException {

		logger.info("[支付宝关闭订单] aLiPayCloseOrder START:" + input.toString());
		OutputParam outputParam = new OutputParam();

		try {
			/**************** 请求报文非空字段验证 ***********************/
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliClose, "[支付宝订单关闭]");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String initTxnSeqId = String.format("%s", input.getValue(Dict.initTxnSeqId));
			String initTxnTime = String.format("%s", input.getValue(Dict.initTxnTime));
			String txnDt = initTxnTime.substring(0, 8);
			String txnTm = initTxnTime.substring(8, 14);
			String merId = String.format("%s", input.getValue(Dict.merId));
			String channel = String.format("%s", input.getValue(Dict.channel));
			String transType = String.format("%s", input.getValue(Dict.transType));
			String payAccessType = String.format("%s", input.getValue(Dict.payAccessType));
			String operatorId = String.format("%s", input.getValue(Dict.operatorId));

			/****************** 校验报文参数 ***********/
			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]交易接入类型：[" + payAccessType + "]错误");
				return outputParam;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]渠道编号： [" + channel + "]错误");
				return outputParam;
			}

			if (!StringConstans.TransType.TRANS_CLOSE_ORDER.equals(transType)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]交易类型： [" + transType + "]错误");
				return outputParam;
			}

			/****************** 验证支付宝原订单信息 ***********/

			InputParam queryInput = new InputParam();
			queryInput.putparamString(Dict.txnSeqId, initTxnSeqId);
			queryInput.putparamString(Dict.txnDt, txnDt);
			queryInput.putparamString(Dict.txnTm, txnTm);

			OutputParam queryOrderOutput = orderService.queryOrder(queryInput);
			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_05);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]原订单信息不存在");
				return outputParam;
			}

			// 判断商户是否与原商户匹配
			if (!merId.equals(queryOrderOutput.getReturnObj().get(Dict.merId))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]与原订单商户不匹配");
				return outputParam;
			}

			if (StringConstans.OrderState.STATE_02.equals(queryOrderOutput.getValue(Dict.txnSta))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_07);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]原订单已交易成功 ，不可关闭");
				return outputParam;
			}

			if (StringConstans.OrderState.STATE_09.equals(queryOrderOutput.getValue(Dict.txnSta))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_09);
				outputParam.putValue(Dict.respDesc, "[支付宝关闭订单]原订单已关闭成功 ");
				return outputParam;
			}

			// 支付宝外部订单号
			String outTradeNo = initTxnSeqId + initTxnTime;
			String alipayTradeNo = String.format("%s", queryOrderOutput.getValue(Dict.alipayTradeNo));
			String subAlipayMerId = String.format("%s", queryOrderOutput.getValue(Dict.subAlipayMerId));
			String payType = String.format("%s", queryOrderOutput.getValue(Dict.payType));

			if(!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "只允许支付宝扫码支付关闭订单");
				return outputParam;
			}
			
			InputParam aliPayInput = new InputParam();
			aliPayInput.putParams(Dict.merId, merId);
			aliPayInput.putParams(Dict.outTradeNo, outTradeNo);
			if (!StringUtil.isEmpty(alipayTradeNo)) {
				aliPayInput.putParams(Dict.alipayTradeNo, alipayTradeNo);
			}
			if (!StringUtil.isEmpty(operatorId)) {
				aliPayInput.putParams(Dict.operatorId, operatorId);
			}
			aliPayInput.putParams(Dict.subAlipayMerId, subAlipayMerId);

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);

			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			OutputParam alipayout = null;
			if (StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				if (StringConstans.CONNECT_METHOD.indirect.equals(outQuery.getValue(Dict.connectMethod))) {
					aliPayInput.putParams(Dict.rateChannel, outQuery.getValue(Dict.rate));
					alipayout = aliPayManager.toCloseALiPayOrderYL(aliPayInput);
				} else {
					alipayout = aliPayManager.toCloseALiPayOrder(aliPayInput);
				}
			} else {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[支付宝断直连统一收单交易关闭] 未查询到该商户信息");
				return outputParam;
			}

			String respCode = alipayout.getReturnCode();
			String respDesc = alipayout.getReturnMsg();

			/************* 更新支付宝原支付订单信息 ************************/

			InputParam udInputParam = new InputParam();
			udInputParam.putparamString("txnSta", StringConstans.OrderState.STATE_09);
			udInputParam.putparamString("resDesc", "关闭订单成功");
			udInputParam.putparamString("txnSeqId", initTxnSeqId);
			udInputParam.putparamString("txnDt", txnDt);
			udInputParam.putparamString("txnTm", txnTm);

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_09);
			outputParam.putValue("respDesc", "订单关闭成功");

			if (!StringConstans.returnCode.SUCCESS.equals(respCode)) {

				logger.error("[支付宝关闭订单交易] 发送支付宝关闭订单交易请求处理失败，失败原因:" + respDesc);

				udInputParam.putparamString("txnSta", StringConstans.OrderState.STATE_12);
				udInputParam.putparamString("resDesc", "用户未支付,订单关闭失败");
				outputParam.putValue("respCode", respCode);
				outputParam.putValue("respDesc", "用户未支付,订单关闭失败");
			}

			logger.info("[支付宝交易关闭订单] 开始更新支付宝原订单信息   ");

			OutputParam updateOut = orderService.updateOrder(udInputParam);

			logger.info("[支付宝交易关闭订单] 更新支付宝原订单信息完成   ");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.info("[支付宝关闭订单交易] 更新支付宝原支付订单信息处理失败" + respDesc);
				logger.error("[支付宝关闭订单交易] 更新支付宝原支付订单信息处理失败" + respDesc);
			}

			logger.info("----------- 支付宝订单关闭流程     END -----------------------");

		} catch (Exception e) {
			logger.error("支付宝关闭订单处理异常：" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "订单关闭成功出现异常");
		}

		return outputParam;
	}

	/**
	 * 支付宝支付撤销交易入口
	 */
	@Override
	public OutputParam aLiPayRevoke(InputParam input) throws FrameException {
		logger.info("[支付宝支付撤销流程]请求报文:" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 参数校验
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliRevoke, "支付宝撤销交易");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String orderNumber = ObjectUtils.toString(input.getValue(Dict.orderNumber));
			String orderTime = ObjectUtils.toString(input.getValue(Dict.orderTime));
			String orderAmount = ObjectUtils.toString(input.getValue(Dict.orderAmount));
			String merId = ObjectUtils.toString(input.getValue(Dict.merId));
			String transType = ObjectUtils.toString(input.getValue(Dict.transType));
			String channel = ObjectUtils.toString(input.getValue(Dict.channel));
			String payAccessType = ObjectUtils.toString(input.getValue(Dict.payAccessType));
			String initOrderNumber = ObjectUtils.toString(input.getValue(Dict.initOrderNumber));
			String initOrderTime = ObjectUtils.toString(input.getValue(Dict.initOrderTime));

			// 按输入返回
			outputParam.putValue(Dict.orderNumber, orderNumber);
			outputParam.putValue(Dict.orderTime, orderTime);
			outputParam.putValue(Dict.orderAmount, orderAmount);
			outputParam.putValue(Dict.merId, merId);

			/******************** 2.报文参数验证 *****************************/
			if (!orderAmount.matches("\\d{12}")) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "订单金额[" + orderAmount + "]非法");
				return outputParam;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "订单金额[" + orderAmount + "]非法");
				return outputParam;
			}

			if (!StringConstans.TransType.TRANS_REVOKE.equals(transType)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "交易类型[" + transType + "]错误");
				return outputParam;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "交易接入类型[payAccessType]错误");
				return outputParam;
			}

			if (!orderTime.matches("\\d{14}")) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "外部订单时间[" + orderTime + "]非法");
				return outputParam;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "渠道[" + channel + "]非法");
				return outputParam;
			}

			/************************ 3.验证支付宝原订单信息 ******************************/
			String initMerOrDt = initOrderTime.substring(0, 8);
			String initMerOrTm = initOrderTime.substring(8, 14);

			InputParam queryParam = new InputParam();
			queryParam.putparamString(Dict.merOrderId, initOrderNumber);
			queryParam.putparamString(Dict.merOrDt, initMerOrDt);
			queryParam.putparamString(Dict.merOrTm, initMerOrTm);

			OutputParam alipayOrderListOut = orderService.queryOrder(queryParam);
			if (!StringConstans.returnCode.SUCCESS.equals(alipayOrderListOut.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_05);
				outputParam.putValue(Dict.respDesc, "原订单不存在");
				return outputParam;
			}

			Map<String, Object> initAlipayOrderMap = alipayOrderListOut.getReturnObj();
			String initTxnSeqId = StringUtil.toString(initAlipayOrderMap.get(Dict.txnSeqId));
			String initTxnDt = StringUtil.toString(initAlipayOrderMap.get(Dict.txnDt));
			String initTxnTm = StringUtil.toString(initAlipayOrderMap.get(Dict.txnTm));
			String initTxnTime = String.format("%s%s", initTxnDt, initTxnTm);
			String subAlipayMerId = StringUtil.toString(initAlipayOrderMap.get(Dict.subAlipayMerId));
			String initAlipaySta = StringUtil.toString(initAlipayOrderMap.get(Dict.txnSta));
			String initTradeMoney = StringUtil.toString(initAlipayOrderMap.get(Dict.tradeMoney));
			String initAlipayTradeNo = StringUtil.toString(initAlipayOrderMap.get(Dict.alipayTradeNo));
			String initMerId = StringUtil.toString(initAlipayOrderMap.get(Dict.merId));
			String initPayType = StringUtil.toString(initAlipayOrderMap.get(Dict.payType));
			String currencyCode = StringUtil.toString(initAlipayOrderMap.get(Dict.currencyCode));

			if (!merId.equals(initMerId)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "撤销商户号与原订单商户号不一致");
				return outputParam;
			}

			if (!orderAmount.equals(initTradeMoney)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "撤销金额与原订单金额不一致");
				return outputParam;
			}

			// 原订单交易状态为失败
			if (StringConstans.OrderState.STATE_03.equals(initAlipaySta)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "原订单交易状态为失败,不能撤销");
				return outputParam;
			}

			input.putParams(Dict.initTxnSeqId, initTxnSeqId);
			input.putParams(Dict.initTxnTime, initTxnTime);
			input.putParams(Dict.subAlipayMerId, subAlipayMerId);
			input.putParams(Dict.currencyCode, currencyCode);
			input.putParams(Dict.payType, initPayType);
			input.putParams(Dict.txnType, transType);
			// 新增撤销订单
			OutputParam orderOut = aliPayManager.addRevokeOrder(input);

			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, orderOut.getReturnMsg());
				return outputParam;
			}
			String txnSeqId = StringUtil.toString(orderOut.getValue(Dict.txnSeqId));
			String txnDt = StringUtil.toString(orderOut.getValue(Dict.txnDt));
			String txnTm = StringUtil.toString(orderOut.getValue(Dict.txnTm));
			String connectMethod = StringUtil.toString(orderOut.getValue(Dict.connectMethod));
			String rateChannel = StringUtil.toString(orderOut.getValue(Dict.rateChannel));
			String initAlipayDesc = "";

			// 原订单交易状态为超时或者未知
			if (StringConstans.OrderState.STATE_04.equals(initAlipaySta)
					|| StringConstans.OrderState.STATE_06.equals(initAlipaySta)
					|| StringConstans.OrderState.STATE_10.equals(initAlipaySta)
					|| StringConstans.OrderState.STATE_01.equals(initAlipaySta)) {
				// 查询支付宝订单是否支付成功
				InputParam queryInput = new InputParam();
				queryInput.putParams(Dict.outTradeNo, initTxnSeqId + initTxnTime);
				queryInput.putParams(Dict.alipayTradeNo, initAlipayTradeNo);
				queryInput.putParams(Dict.merId, merId);
				queryInput.putParams(Dict.subAlipayMerId, subAlipayMerId);

				OutputParam queryOut = null;
				if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					queryOut = aliPayManager.toQueryALiPayOrderYL(queryInput);
				} else {
					queryOut = aliPayManager.toQueryALiPayOrder(queryInput);
				}

				logger.debug("[支付宝支付撤销交易]  原交易状态查询 结束");

				initAlipaySta = StringUtil.toString(queryOut.getValue(Dict.orderSta));
				initAlipayDesc = String.format("%s", queryOut.getValue(Dict.orderDesc));

				if (!StringConstans.OrderState.STATE_10.equals(initAlipaySta)) {

					InputParam updateInput = new InputParam();
					updateInput.putparamString(Dict.txnSta, initAlipaySta);
					updateInput.putparamString(Dict.txnSeqId, initTxnSeqId);
					updateInput.putparamString(Dict.txnDt, initTxnDt);
					updateInput.putparamString(Dict.txnTm, initTxnTm);
					updateInput.putparamString(Dict.resDesc, initAlipayDesc);

					OutputParam updateOutput = orderService.updateOrderState(updateInput);
				}
			}

			/********************* 开始进行支付宝撤销 ******************************/
			InputParam revokeInput = new InputParam();
			revokeInput.putParams(Dict.outTradeNo, initTxnSeqId + initTxnTime);
			revokeInput.putParams(Dict.alipayTradeNo, initAlipayTradeNo);
			revokeInput.putParams(Dict.merId, merId);
			revokeInput.putParams(Dict.subAlipayMerId, subAlipayMerId);
			revokeInput.putParams(Dict.rateChannel, rateChannel);

			OutputParam revokeOutput = null;
			if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				// 间连
				revokeOutput = aliPayManager.toRevokeALiPayOrderYL(revokeInput);
			} else {
				// 直连
				revokeOutput = aliPayManager.toRevokeALiPayOrder(revokeInput);
			}

			String txnSta = StringUtil.toString(revokeOutput.getValue(Dict.orderSta));
			String respCode = StringUtil.toString(revokeOutput.getValue(Dict.respCode));
			String resDesc = StringUtil.toString(revokeOutput.getValue(Dict.respDesc));
			String alipayTradeNo = StringUtil.toString(revokeOutput.getValue(Dict.alipayTradeNo));
			// 2、统一更新订单撤销订单状态
			InputParam updateInput = new InputParam();
			updateInput.putparamString(Dict.txnSta, txnSta);
			updateInput.putparamString(Dict.txnSeqId, txnSeqId);
			updateInput.putparamString(Dict.txnDt, txnDt);
			updateInput.putparamString(Dict.txnTm, txnTm);
			updateInput.putparamString(Dict.resDesc, resDesc);
			updateInput.putparamStringRemoveNull(Dict.alipayTradeNo, alipayTradeNo);
			OutputParam updateOutput = orderService.updateOrder(updateInput);

			outputParam.putValue(Dict.txnSeqId, txnSeqId);
			outputParam.putValue(Dict.txnTime, txnDt + txnTm);
			outputParam.putValue(Dict.respCode, respCode);
			outputParam.putValue(Dict.respDesc, resDesc);
		} catch (Exception e) {
			logger.error("订单发往支付宝撤销异常：" + e.getMessage(), e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "调用支付撤销出现异常" + e.getMessage());
		} finally {
			logger.info("[支付宝支付撤销流程]返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 支付宝退款
	 */
	@Override
	public OutputParam refundOrder(InputParam inputParam) throws FrameException {
		logger.info("[支付宝退款交易]支付宝退款交易流程START" + inputParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String merId = StringUtil.toString(inputParam.getValue(Dict.merId));
			String initTxnSeqId = StringUtil.toString(inputParam.getValue(Dict.initTxnSeqId));
			String initTxnTime = StringUtil.toString(inputParam.getValue(Dict.initTxnTime));
			String refundAmount = StringUtil.toString(inputParam.getValue(Dict.refundAmount));
			String refundReason = StringUtil.toString(inputParam.getValue(Dict.refundReason));
			String initTxnSta = StringUtil.toString(inputParam.getValue(Dict.initTxnSta));
			String subMerchant = StringUtil.toString(inputParam.getValue(Dict.subMerchant));
			String rateChannel = StringUtil.toString(inputParam.getValue(Dict.rateChannel));
			String initTotalRefundFee = StringUtil.toString(inputParam.getValue(Dict.initTotalRefundFee),"0");
			String updTotalRefundFee = StringUtil.amountTo12Str2(new BigDecimal(refundAmount).add(new BigDecimal(initTotalRefundFee))+"");
			String txnSeqId = StringUtil.toString(inputParam.getValue(Dict.txnSeqId));
			String initTxnSeqIdTime = initTxnSeqId+initTxnTime;
			
			if (StringConstans.OrderState.STATE_04.equals(initTxnSta)
					|| StringConstans.OrderState.STATE_06.equals(initTxnSta)
					|| StringConstans.OrderState.STATE_10.equals(initTxnSta)
					|| StringConstans.OrderState.STATE_01.equals(initTxnSta)) {
				logger.info("原订单订单状态未明，先明确状态后再处理");
				
				InputParam queryInput = new InputParam();
				queryInput.putParams(Dict.outTradeNo, initTxnSeqIdTime);
				queryInput.putParams(Dict.subAlipayMerId, subMerchant);
				queryInput.putParams(Dict.merId, merId);
				queryInput.putParams(Dict.rateChannel, rateChannel);
				OutputParam queryOut = aliPayManager.toQueryALiPayOrderYL(queryInput);
				
				String orderSta = StringUtil.toString(queryOut.getValue("orderSta"));
				String orderDesc = StringUtil.toString(queryOut.getValue("orderDesc"));
				if(StringConstans.OrderState.STATE_02.equals(orderSta)){
					logger.info("原订单交易成功，允许退款");
					
					/**去支付宝退款start*/
					InputParam refundInput = new InputParam();
					refundInput.putParams(Dict.txnSeqId, txnSeqId);
					refundInput.putParams(Dict.initTxnSeqIdTime, initTxnSeqIdTime);
					refundInput.putParams(Dict.rateChannel, rateChannel);
					refundInput.putParams(Dict.refundAmount, StringUtil.str12ToAmount(refundAmount));
					refundInput.putParams(Dict.refundReason, refundReason);
					OutputParam refundOutput = aliPayManager.toALiPayRefundOrder(refundInput);
					/**去支付宝退款end*/
					
					String refundStatus = StringUtil.toString(refundOutput.getValue(Dict.refundStatus));
					String msg = StringUtil.toString(refundOutput.getValue(Dict.msg));
					
					//如果处理中则直接返回
					if(StringConstans.RefundStatus.STATUS_01.equals(refundStatus)){
						outputParam.putValue(Dict.refundStatus, refundStatus);
						outputParam.putValue(Dict.msg, msg);
						return outputParam;
					}
					
					/**更新退款订单start*/
					InputParam updateRefund = new InputParam();
					updateRefund.putparamString(Dict.txnSeqId, txnSeqId);
					updateRefund.putparamString(Dict.txnSta, refundStatus);
					updateRefund.putparamString(Dict.resDesc, msg);
					orderService.updateOrderState(updateRefund);
					/**更新退款订单end*/
					
					/**更新原订单start*/
					if(StringConstans.RefundStatus.STATUS_02.equals(refundStatus)){
						InputParam updateInput = new InputParam();
						updateInput.putparamString(Dict.txnSeqId, initTxnSeqId);
						updateInput.putparamString(Dict.txnSta, orderSta);
						updateInput.putparamString(Dict.resDesc, orderDesc);
						updateInput.putparamString(Dict.totalRefundFee, updTotalRefundFee);
						updateInput.putparamString(Dict.initTotalRefundFee, initTotalRefundFee);
						orderService.updateOrderState(updateInput);
					}
					/**更新原订单end*/
					
					outputParam.putValue(Dict.refundStatus, refundStatus);
					outputParam.putValue(Dict.msg, msg);
				} else if(StringConstans.OrderState.STATE_09.equals(orderSta)
						|| StringConstans.OrderState.STATE_11.equals(orderSta)
						|| StringConstans.OrderState.STATE_03.equals(orderSta)){
					
					/**更新原订单start*/
					InputParam updateInput = new InputParam();
					updateInput.putparamString(Dict.txnSeqId, initTxnSeqId);
					updateInput.putparamString(Dict.txnSta, orderSta);
					updateInput.putparamString(Dict.resDesc, orderDesc);
					orderService.updateOrderState(updateInput);
					/**更新原订单end*/
					
					logger.info("原订单交易状态:"+orderDesc+",不允许退款");
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "原订单交易状态:"+orderDesc+",不允许退款");
					return outputParam;
				} else {
					logger.info("原订单交易状态:"+orderDesc+",请稍后再重试退款");
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "原订单交易状态:"+orderDesc+",请稍后再重试退款");
					return outputParam;
				}
				
			} else if (StringConstans.OrderState.STATE_02.equals(initTxnSta)) {
				logger.info("原订单交易成功，直接进行退款");
				/**去支付宝退款start*/
				InputParam refundInput = new InputParam();
				refundInput.putParams(Dict.txnSeqId, txnSeqId);
				refundInput.putParams(Dict.initTxnSeqIdTime, initTxnSeqIdTime);
				refundInput.putParams(Dict.rateChannel, rateChannel);
				refundInput.putParams(Dict.refundAmount, StringUtil.str12ToAmount(refundAmount));
				refundInput.putParams(Dict.refundReason, refundReason);
				OutputParam refundOutput = aliPayManager.toALiPayRefundOrder(refundInput);
				/**去支付宝退款end*/
				
				String refundStatus = StringUtil.toString(refundOutput.getValue(Dict.refundStatus));
				String msg = StringUtil.toString(refundOutput.getValue(Dict.msg));
				
				//如果处理中则直接返回
				if(StringConstans.RefundStatus.STATUS_01.equals(refundStatus)){
					outputParam.putValue(Dict.refundStatus, refundStatus);
					outputParam.putValue(Dict.msg, msg);
					return outputParam;
				}
				
				/**更新退款订单start*/
				InputParam updateRefund = new InputParam();
				updateRefund.putparamString(Dict.txnSeqId, txnSeqId);
				updateRefund.putparamString(Dict.txnSta, refundStatus);
				updateRefund.putparamString(Dict.resDesc, msg);
				orderService.updateOrderState(updateRefund);
				/**更新退款订单end*/
				
				/**更新原订单start*/
				if(StringConstans.RefundStatus.STATUS_02.equals(refundStatus)){
					InputParam updateInput = new InputParam();
					updateInput.putparamString(Dict.txnSeqId, initTxnSeqId);
					updateInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_02);
					updateInput.putparamString(Dict.totalRefundFee, updTotalRefundFee);
					updateInput.putparamString(Dict.initTotalRefundFee, initTotalRefundFee);
					orderService.updateOrderState(updateInput);
				}
				/**更新原订单end*/
				
				outputParam.putValue(Dict.refundStatus, refundStatus);
				outputParam.putValue(Dict.msg, msg);
			}
		} catch (Exception e) {
			logger.error("支付宝退款处理异常：" + e.getMessage(), e);
			outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
			outputParam.putValue(Dict.msg, "支付宝退款处理异常");
		} finally {
			logger.info("[支付宝退款交易]支付宝退款交易流程END" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 支付宝退款状态查询
	 */
	@Override
	public OutputParam queryRefundOrder(InputParam input) throws FrameException {
		logger.info("[支付宝退款状态查询]开始支付宝退款状态查询交易 START" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String txnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String refundAmount = StringUtil.toString(input.getValue(Dict.refundAmount));
			String initTotalRefundFee = StringUtil.toString(input.getValue(Dict.initTotalRefundFee),"0");
			String updTotalRefundFee = StringUtil.amountTo12Str2(new BigDecimal(refundAmount).add(new BigDecimal(initTotalRefundFee))+"");
			
			OutputParam refundOutput = aliPayManager.toQueryALiPayRefundOrder(input);
			
			String refundStatus = StringUtil.toString(refundOutput.getValue(Dict.refundStatus));
			String msg = StringUtil.toString(refundOutput.getValue(Dict.msg));
			
			outputParam.putValue(Dict.refundStatus, refundStatus);
			outputParam.putValue(Dict.msg, msg);
			
			//如果处理中则直接返回
			if(StringConstans.RefundStatus.STATUS_01.equals(refundStatus)){
				return outputParam;
			}
			
			if(StringConstans.RefundStatus.STATUS_03.equals(refundStatus)){
				/**更新退款订单start*/
				InputParam updateRefund = new InputParam();
				updateRefund.putparamString(Dict.txnSeqId, txnSeqId);
				updateRefund.putparamString(Dict.txnSta, refundStatus);
				updateRefund.putparamString(Dict.resDesc, msg);
				orderService.updateOrderState(updateRefund);
				/**更新退款订单end*/
				
				return outputParam;
			}
			
			if(StringConstans.RefundStatus.STATUS_02.equals(refundStatus)){
				/**更新退款订单start*/
				InputParam updateRefund = new InputParam();
				updateRefund.putparamString(Dict.txnSeqId, txnSeqId);
				updateRefund.putparamString(Dict.txnSta, refundStatus);
				updateRefund.putparamString(Dict.resDesc, msg);
				orderService.updateOrderState(updateRefund);
				/**更新退款订单end*/
				
				/**更新原订单start*/
				InputParam updateInput = new InputParam();
				updateInput.putparamString(Dict.txnSeqId, initTxnSeqId);
				updateInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_02);
				updateInput.putparamString(Dict.totalRefundFee, updTotalRefundFee);
				updateInput.putparamString(Dict.initTotalRefundFee, initTotalRefundFee);
				orderService.updateOrderState(updateInput);
				/**更新原订单end*/
			}
			
			
		} catch (Exception e) {
			logger.error("[支付宝退款状态查询]支付宝退款查询处理异常：" + e.getMessage(), e);
			outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
			outputParam.putValue(Dict.msg, "支付宝退款查询处理异常");
		} finally {
			logger.info("[支付宝退款状态查询]开始支付宝退款状态查询交易 END" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 下载支付宝对账单
	 */
	@Override
	public OutputParam downloadALiPayBill(InputParam input) throws FrameException {

		logger.info("下载支付宝对账单流程  START,请求报文:" + input.toString());

		OutputParam out = new OutputParam();

		try {

			/**************** 请求报文非空字段验证 ***********************/
			List<String> list = new ArrayList<String>();
			list.add("transType");
			list.add("billType");
			list.add("alipayBillDate");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[支付宝下载对账单] 请求报文字段[" + nullStr + "]不能为空");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			// 交易类型
			String transType = ObjectUtils.toString(input.getValue("transType"));

			// 账单类型
			String billType = ObjectUtils.toString(input.getValue("billType"));

			// 支付宝账单类型
			String alipayBillType = ObjectUtils.toString(input.getValue("alipayBillType"));

			// 账单日期
			String alipayBillDate = ObjectUtils.toString(input.getValue("alipayBillDate"));

			logger.debug("[支付宝下载对账单],交易类型验证 transType=" + transType);
			if (!StringConstans.TransType.TRANS_DOWN_FILE.equals(transType)) {
				logger.debug("[支付宝下载对账单] 交易类型[transType]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易类型[transType]错误");
				return out;
			}

			logger.debug("[支付宝下载对账单],账单日期验证  alipayBillDate=" + alipayBillDate);
			if (!alipayBillDate.matches("^[1-9]{1}\\d{7}$")) {
				logger.debug("[支付宝下载对账单],账单日期[alipayBillDate]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("账单日期[alipayBillDate]错误");
				return out;
			}

			logger.debug("[支付宝下载对账单],账单类型验证 billType=" + billType);
			if (!StringConstans.BillType.BILLTYPE_ALIPAY.equals(billType)) {
				logger.debug("[支付宝下载对账单],账单类型[billType]错误");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("账单类型[billType]错误");
				return out;
			}

			if (StringUtil.isEmpty(alipayBillType)) {
				alipayBillType = StringConstans.AlipayBillType.ALIPAYBILLTYPE_TRADE;
			}

			OutputParam initAlipayBillFileOut = this.downAlipayBillFileBySftp(alipayBillDate);

			if (!StringConstans.returnCode.SUCCESS.equals(initAlipayBillFileOut.getReturnCode())) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(initAlipayBillFileOut.getReturnMsg());
				return out;
			}

			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			out.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			out.putValue("respDesc", "支付宝对账单下载成功");

		} catch (Exception e) {
			logger.error("下载支付宝账单出现异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("下载支付宝账单出现异常");
		} finally {
			logger.info("下载支付宝对账单流程出现   END,返回报文:" + out.toString());
		}

		return out;
	}

	/**
	 * 下载对账单
	 * 
	 * @param input
	 * @return OutputParam
	 * @throws Exception
	 */
	private OutputParam downAlipayBillFileBySftp(String alipayBillDate) throws Exception {

		logger.info("根据sftp链接 获取支付宝账单流程 START,请求参数alipayBillDate:" + alipayBillDate);

		OutputParam outputParam = new OutputParam();

		try {

			// 支付宝对账单下载目录
			String alipayInitDir = Constants.getParam("alipay_sftp_down_path");
			logger.debug("[sftp对账单下载] 对账单下载初始目录" + alipayInitDir);

			Map<String, Object[]> map = MappingContext.getInstance().get(StringConstans.MappingConfig.CHANNEL_CONFIG);
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String key1 = it.next();
				Object[] obj = map.get(key1);

				String CHANNEL = StringUtil.toString(obj[0]);
				if (!StringConstans.PAY_ChANNEL.ALI.equals(CHANNEL)) {
					continue;
				}
				String rate = StringUtil.toString(obj[1]);
				// 支付宝pid
				String alipayPid = StringUtil.toString(obj[2]);// Constants.getParam("pid");
				// 支付宝对账单目录
				String alipayBillDir = String.format("%s%s", alipayInitDir, alipayBillDate);
				logger.info("[sftp对账单下载] 对账单目录" + alipayBillDir);
				// 对账单名称
				String billFileName = String.format("%s%s%s%s%s", alipayPid, ALIPAY_CURRENCY, "_", alipayBillDate,
						".zip");
				logger.info("[sftp对账单下载] 对账单名称" + billFileName);

				// 保存下载对账单文件目录
				File file = new File(Constants.getParam("alipay_originalBill_path") + billFileName);
				logger.info("[sftp对账单下载] 保存对账单文件 file=" + file);

				outputParam = this.sftpDownFile(billFileName, alipayBillDir, file, alipayBillDate, rate);

				// outputParam = this.handleAlipayBillDownFile(alipayBillDate, file,rate);
				if (StringConstans.returnCode.FAIL.equals(outputParam.getReturnCode())) {
					return outputParam;
				}
			}

			boolean flag = FileUtil.sumAliBill(alipayBillDate);
			if (!flag) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付宝账单合成失败");
			}

			logger.info("-------------   支付宝对账单处理流程        END-------------");

		} catch (Exception e) {
			logger.error("支付宝账单流程处理出现异常：" + e.getMessage(), e);
			// this.handlerException(e, alipayBillDate);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			logger.info("-----------  支付宝账单流程处理出现异常        END-----------");
		}

		return outputParam;
	}

	/**
	 * sftp 下载对账单文件
	 * 
	 * @param billFileName
	 * @param alipayBillDir
	 * @param file
	 * @throws Exception
	 */
	private OutputParam sftpDownFile(String billFileName, String alipayBillDir, File file, String alipayBillDate,
			String rate) throws Exception {

		logger.info("----------------连接sftp 进行对账文件下载流程  START -----------------");
		OutputParam outputParam = new OutputParam();
		try {

			// sftp用户名
			String username = Constants.getParam("sftp_username");
			// sftp密码
			String password = Constants.getParam("sftp_password");
			// sftp主机名
			String host = Constants.getParam("sftp_host");
			// sftp端口
			String port = Constants.getParam("sftp_port");

			BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(file));

			SftpUtil sftpUtil = new SftpUtil(username, password, host, Integer.valueOf(port));
			sftpUtil.connect();
			sftpUtil.downFile(billFileName, alipayBillDir, buffer);
			outputParam = this.handleAlipayBillDownFile(alipayBillDate, file, rate);
		} catch (Exception e) {

			logger.error("[连接sftp 进行对账文件下载] 出现异常:" + e.getMessage(), e);
			logger.info("[连接sftp 进行对账文件下载] 出现异常进行对账单文件删除");
			boolean flag = file.delete();
			logger.info("[连接sftp 进行对账文件下载] 出现异常进行对账单文件删除成功标识flag=" + flag);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝对账单处理异常");
			writeDefualtBillInfo(alipayBillDate + "_" + rate);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("支付宝对账单处理成功");
			// throw e;
		}

		logger.info("----------------连接sftp 进行对账文件下载流程  END -----------------");
		return outputParam;
	}

	/***
	 * 处理对账单下载
	 */
	// private void handlerNotExistBillFile(String alipayBillDate) throws Exception
	// {
	//
	// writeDefualtBillInfo(alipayBillDate);
	// }

	/**
	 * 写入默认对账单信息
	 * 
	 * @param outputParam
	 * @param alipayBillDate
	 * @throws Exception
	 */
	// private void writeDefualtBillInfo(String alipayBillDate) throws Exception {
	//
	// logger.info("-------------- 支付宝对账单不存在,默认支付宝对账单写入流程 START
	// ------------------");
	//
	// String fileName = alipayBillDate + "_2PAYZFB";
	//
	// File file = new File(Constants.getParam("alipay_downloadBill_path") +
	// fileName);
	// logger.info("[默认支付宝对账单写入] fileName=" + file.getPath());
	//
	// String billContent = "0,0,0,0";
	// FileUtil.writeToFile(billContent, file);
	//
	// logger.info("-------------- 支付宝对账单不存在,默认支付宝对账单写入流程 END ------------------");
	// }

	private void writeDefualtBillInfo(String date_rate) throws Exception {

		logger.info("-------------- 支付宝对账单不存在,默认支付宝对账单写入流程  START ------------------");

		String fileName = date_rate + "_2PAYZFB";

		File file = new File(Constants.getParam("alipay_downloadBill_path") + fileName);
		logger.info("[默认支付宝对账单写入] fileName=" + file.getPath());

		String billContent = "0,0,0,0";
		FileUtil.writeToFile(billContent, file);

		logger.info("-------------- 支付宝对账单不存在,默认支付宝对账单写入流程  END ------------------");
	}

	/**
	 * 处理支付宝对账单下载文件
	 * 
	 * @param alipayBillDate
	 * @param file
	 * @return
	 */
	private OutputParam handleAlipayBillDownFile(String alipayBillDate, File file, String rate) {

		logger.info("对账单文件解压处理   START");

		OutputParam outputParam = new OutputParam();

		try {

			logger.debug("[对账单文件解压处理] 开始对对账单压缩文件的个数进行提取");

			List<String> fileList = FileUtil.unZip(file);

			logger.debug("[对账单文件解压处理] 完成对对账单压缩文件的个数进行提取 fileList=" + fileList);

			if (fileList.isEmpty()) {
				logger.debug("[支付宝对账单处理] 解压文件失败，没有合适的文件");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("解压文件失败，没有合适的文件");
				return outputParam;
			}

			logger.debug("[对账单文件解压处理] 对账单压缩里面文件个数不为空 ");

			String regex = String.format("%s%s%s", ".+", alipayBillDate, "_DETAILS.csv$");
			Pattern p = Pattern.compile(regex);

			logger.debug("[对账单文件解压处理] 用正则表达式匹配是否有合适的文件 regex=" + regex);

			String billName = null;
			for (String name : fileList) {
				Matcher m = p.matcher(name);
				billName = m.find() ? m.group() : billName;
			}

			logger.debug("[对账单文件解压处理] 用正则表达式匹配到的文件名称 billName=" + billName);

			if (StringUtil.isEmpty(billName)) {
				logger.debug("[支付宝对账单处理] 解压文件失败，没有合适的文件");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("解压文件失败，没有合适的文件");
				return outputParam;
			}

			File billFile = new File(Constants.getParam("alipay_downloadBill_path") + billName);
			logger.debug("[对账单文件解压处理] 需要处理的对账单名称 billFile=" + billName);

			logger.debug("[对账单文件解压处理] 开始对对账单的内容进行处理");

			String fileName = alipayBillDate + "_" + rate + "_2PAYZFB";
			File aliPayFile = new File(Constants.getParam("alipay_downloadBill_path") + fileName);

			boolean flag = FileUtil.parseAlipayBill(billFile, aliPayFile);

			logger.debug("[对账单文件解压处理] 完成对对账单的内容进行处理  处理标识 flag=" + flag);

			if (!flag) {
				logger.debug("[支付宝对账单处理] 处理对账单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("处理对账单失败");
				return outputParam;
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("支付宝对账单处理成功");

		} catch (Exception e) {
			logger.error("支付宝账单流程处理出现异常：" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝账单流程处理出现异常");
		} finally {
			logger.info("支付宝账单流程处理 END,返回报文:" + outputParam.toString());

		}

		return outputParam;
	}

	/**
	 * 处理异常信息
	 * 
	 * @param e
	 * @throws Exception
	 */
	// private void handlerException(Exception e, String... args) throws Exception {
	//
	// logger.info("----------- 异常处理流程 START --------");
	//
	// logger.info("[异常处理] 异常信息：" + e.getMessage());
	//
	// if (e.getMessage().contains("No such file")) {
	// logger.info("[异常处理] 开始进行默认文件写入流程");
	// this.handlerNotExistBillFile(args[0]);
	// logger.info("[异常处理] 完成进行默认文件写入流程");
	// }
	//
	// logger.info("----------- 异常处理流程 END --------");
	// }

	/**
	 * 查询支付宝交易
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam queryALipayOrder(InputParam input) throws FrameException {

		logger.info("支付宝订单查询流程     START请求报文:" + input.toString());
		OutputParam outputParam = new OutputParam();

		try {
			String outTradeNo = String.format("%s", input.getValue("outTradeNo"));
			String alipayTradeNo = String.format("%s", input.getValue("alipayTradeNo"));
			String subAlipayMerId = String.format("%s", input.getValue("subAlipayMerId"));
			String merId = String.format("%s", input.getValue("merId"));

			// 查询支付宝订单是否支付成功
			InputParam queryInput = new InputParam();
			queryInput.putParams("outTradeNo", outTradeNo);
			queryInput.putParams("alipayTradeNo", alipayTradeNo);
			queryInput.putParams("subAlipayMerId", subAlipayMerId);
			queryInput.putParams("merId", merId);

			logger.debug("[支付宝查询交易]  交易状态查询 开始");
			OutputParam queryOut = null;
			InputParam routingInput = new InputParam();
			routingInput.putParams(Dict.merId, merId);
			routingInput.putParams(Dict.alipayMerchantId, subAlipayMerId);
			OutputParam routing = aliPayMerchantSynchService.routing(routingInput);
			String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
			String rateChannel = StringUtil.toString(routing.getValue(Dict.rateChannel));
			if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				// 间连
				queryInput.putParams(Dict.rateChannel, rateChannel);
				queryOut = aliPayManager.toQueryALiPayOrderYL(queryInput);
			} else {
				// 直连
				queryOut = aliPayManager.toQueryALiPayOrder(queryInput);
			}
			logger.debug("[支付宝查询交易]  交易状态查询 结束");

			String orderSta = ObjectUtils.toString(queryOut.getValue("orderSta"));
			String orderDesc = ObjectUtils.toString(queryOut.getValue("orderDesc"));

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode()) && StringUtil.isEmpty(orderSta)) {
				logger.debug("[支付宝交易查询],支付宝交易查询失败:" + queryOut.getReturnMsg());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付宝交易查询失败:" + queryOut.getReturnMsg());
				return outputParam;
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnObj(queryOut.getReturnObj());
			outputParam.setReturnMsg("支付宝交易查询成功");
			outputParam.putValue("orderSta", orderSta);
			outputParam.putValue("orderDesc", orderDesc);

		} catch (Exception e) {
			logger.error("[支付宝交易查询],支付宝交易查询异常" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝交易查询异常");
		} finally {
			logger.info("支付宝订单查询流程END,返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 通知线程处理
	 * 
	 * @param input
	 */
	private void notfiyThreadHandler(InputParam input) {

		logger.info("支付宝后台通知 --通知外围系统[start],请求报文:" + input.toString());

		try {

			// 退款申请流水号
			String outBizNo = String.format("%s", input.getValue("out_biz_no"));

			// 外部订单号
			String outTradeNo = String.format("%s", input.getValue("out_trade_no"));

			// 交易状态
			String tradeStatus = String.format("%s", input.getValue("trade_status"));

			// 交易状态
			String orderState = AliPayPayServiceImpl.alipayStatusToOrderStatus(tradeStatus).get("orderState");
			logger.debug("[支付宝预下单后台通知处理] 支部宝返回校验状态tradeStatus=" + tradeStatus + ",orderState=" + orderState);

			if (!StringConstans.OrderState.STATE_02.equals(orderState)) {
				logger.debug("[ 支付宝后台通知 --通知外围系统  ] 支付宝后台通知交易状态不正确不进行通知");
				return;
			}

			if (StringUtil.isEmpty(outBizNo) && !outTradeNo.matches("ONLINE\\d*") && !outTradeNo.matches("TC\\d*")) {
				logger.debug("[  支付宝后台通知 --通知外围系统  ] 支付宝后台通知非线上扫码支付,不进行通知");
				return;
			}

			// 该笔交易买家付款时间
			String gmtPayment = String.format("%s", input.getValue("gmt_payment")).replaceAll("[-|:|\\s]", "");

			// 买家支付宝账号
			String buyerLogonId = String.format("%s", input.getValue("buyer_logon_id"));

			// 获取数字部分
			String txnNo = CharMatcher.javaDigit().retainFrom(outTradeNo);

			// 获取字母部分
			String prefix = CharMatcher.javaUpperCase().retainFrom(outTradeNo);

			// 二维码交易流水号
			String txnSeqId = String.format("%s%s", prefix, txnNo.substring(0, 10));

			// 交易日期
			String txnDt = txnNo.substring(10, 18);

			// 交易时间
			String txnTm = txnNo.substring(18, 24);

			logger.debug("[支付宝后台通知 --通知外围系统] txnSeqId=" + txnSeqId + ",txnDt=" + txnDt + ",txnTm=" + txnTm);

			InputParam queryInput = new InputParam();
			queryInput.putparamString("txnSeqId", txnSeqId);
			queryInput.putparamString("txnDt", txnDt);
			queryInput.putparamString("txnTm", txnTm);

			logger.debug("[支付宝后台通知 --通知外围系统]  根据外部订单号查询订单是否存在 开始,请求报文:" + queryInput.toString());
			OutputParam queryOut = orderService.queryOrder(queryInput);
			logger.debug("[支付宝后台通知 --通知外围系统]  根据外部订单号查询订单是否存在 结束,返回报文:" + queryOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[支付宝后台通知 --通知外围系统理]  根据外部订单号查询订单没有找到匹配的记录");
				return;
			}

			queryOut.putValue("gmtPayment", gmtPayment);
			queryOut.putValue("buyerLogonId", buyerLogonId);

			if (StringConstans.PrefixOrder.ONLINE.equals(prefix)) {
				this.notfiyOnlineThreadHandler(queryOut);
			}

			if (StringConstans.PrefixOrder.THREE_CODE.equals(prefix)) {
				this.notfiyThreeCodeThreadHandler(queryOut);
			}

		} catch (FrameException e) {
			logger.error("[支付宝后台通知 --通知外围系统 ] 支付宝预下单后台通知处理出现异常: " + e.getMessage(), e);
		} finally {
			logger.debug("支付宝后台通知 --通知外围系统[END]");
		}
	}

	/**
	 * 线上推送处理
	 * 
	 * @param queryOut
	 */
	private void notfiyOnlineThreadHandler(OutputParam queryOut) {

		NotifyMessage notifyMessage = new NotifyMessage();

		this.getOnlineNotifyMessage(notifyMessage, queryOut);

		ThreadNotifyHelper.notifyThread(notifyMessage);
	}

	/**
	 * 三码推送处理
	 * 
	 * @param queryOut
	 */
	private void notfiyThreeCodeThreadHandler(OutputParam queryOut) {

		// 串码明文
		String ewmData = String.format("%s", queryOut.getValue("ewmData"));

		InputParam queryThreeCodeInput = new InputParam();
		queryThreeCodeInput.putparamString("ewmData", ewmData);
		queryThreeCodeInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);

		logger.info("[接受 微信后台通知 -- 外围系统通知] 根据ewmData三码合一相关信息  开始,请求报文:" + queryThreeCodeInput.toString());

		OutputParam queryTcOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

		logger.info("[接受 微信后台通知 -- 外围系统通知] 根据ewmData三码合一相关信息  结束,返回报文:" + queryTcOut.toString());

		if (!StringConstans.returnCode.SUCCESS.equals(queryTcOut.getReturnCode())) {
			logger.debug("[查询三码合一流水] 根据ewmStatue查询记录失败");
			return;
		}

		NotifyMessage notifyMessage = new NotifyMessage();

		this.getThreeCodeNotifyMessage(notifyMessage, queryOut, queryTcOut);

		ThreadNotifyHelper.notifyThread(notifyMessage);
	}

	/**
	 * 支付宝交易类型转订单状态
	 * 
	 * @param alipayStatus
	 * @return
	 */
	public static Map<String, String> alipayStatusToOrderStatus(String alipayStatus, String... message) {

		Map<String, String> map = new HashMap<String, String>();

		if (StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_01);
			map.put("orderDesc", "交易正在处理中,等待用户输密码");

		} else if (StringConstans.AlipayTradeStatus.TRADE_CLOSED.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_09);
			map.put("orderDesc", "订单已关闭");

		} else if (StringConstans.AlipayTradeStatus.TRADE_SUCCESS.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_02);
			map.put("orderDesc", "交易成功");

		} else if (StringConstans.AlipayTradeStatus.TRADE_FINISH.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_11);
			map.put("orderDesc", "交易结束,不可退款");

		} else if (StringConstans.AlipayTradeStatus.TRADE_UNKNOWN.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_10);
			map.put("orderDesc", "交易状态未知");

		} else if (alipayStatus == null && AlipayErrorCode.TRADE_NOT_EXIST.equals(message[1])) {

			map.put("orderState", StringConstans.OrderState.STATE_13);
			map.put("orderDesc", "交易不存在");

		} else {

			map.put("orderState", StringConstans.OrderState.STATE_03);
			map.put("orderDesc", message[0]);
		}

		return map;
	}

	/**
	 * 处理预下单的后台通知
	 * 
	 * @param input
	 * @return
	 */
	private OutputParam handAlipayPreCreateNotify(InputParam input) {

		logger.info("支付宝预下单后台通知处理流程[START],请求报文:" + input.toString());

		OutputParam outputParam = new OutputParam();

		try {

			// 外部订单号
			String outTradeNo = String.format("%s", input.getValue("out_trade_no"));

			// 支付宝订单号
			String alipayTradeNo = String.format("%s", input.getValue("trade_no"));

			// 交易状态
			String tradeStatus = String.format("%s", input.getValue("trade_status"));

			// 订单金额
			String orderAmount = String.format("%s", input.getValue("total_amount"));
			orderAmount = com.huateng.pay.common.util.StringUtil.amountTo12Str(orderAmount);

			// 商家实收金额
			String receiptAmount = String.format("%s", input.getValue("receipt_amount"));
			receiptAmount = com.huateng.pay.common.util.StringUtil.amountTo12Str(receiptAmount);

			// 该笔交易买家付款时间
			String gmtPayment = String.format("%s", input.getValue("gmt_payment"));

			// 该笔交易买家支付宝账号
			String payerid = String.format("%s", input.getValue("buyer_id"));

			Pattern p = Pattern.compile("[a-zA-Z]*");
			Matcher m = p.matcher(outTradeNo);
			String prefix = m.find() ? m.group() : "";
			logger.debug("[支付宝预下单后台通知处理] prefix=" + prefix);

			outTradeNo = outTradeNo.substring(prefix.length(), outTradeNo.length());
			logger.debug("[支付宝预下单后台通知处理] 处理后的outTradeNo=" + outTradeNo);

			// 二维码交易流水号
			String txnSeqId = String.format("%s%s", prefix, outTradeNo.substring(0, 10));
			logger.debug("[支付宝预下单后台通知处理] 处理后的txnSeqId=" + txnSeqId);

			// 交易日期
			String txnDt = outTradeNo.substring(10, 18);

			// 交易时间
			String txnTm = outTradeNo.substring(18, 24);

			logger.debug("[支付宝预下单后台通知处理] txnSeqId=" + txnSeqId + ",txnDt=" + txnDt + ",txnTm=" + txnTm);

			InputParam queryInput = new InputParam();
			queryInput.putparamString("txnSeqId", txnSeqId);
			queryInput.putparamString("txnDt", txnDt);
			queryInput.putparamString("txnTm", txnTm);

			logger.debug("[支付宝预下单后台通知处理]  根据外部订单号查询订单是否存在开始,请求报文:" + queryInput.toString());
			OutputParam queryOut = orderService.queryOrder(queryInput);
			logger.debug("[支付宝预下单后台通知处理]  根据外部订单号查询订单是否存在 结束,返回报文:" + queryOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[支付宝预下单后台通知处理]  根据外部订单号查询订单没有找到匹配的记录");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("根据外部订单号查询订单没有找到匹配的记录");
				return outputParam;
			}

			// 下单金额
			String tradeAmount = String.format("%s", queryOut.getValue("tradeMoney"));
			logger.debug("[支付宝预下单后台通知处理] 支付宝返回订单金额tradeAmount=" + tradeAmount + ",POSP下单金额orderAmount=" + orderAmount);

			// 订单状态
			String txnSta = String.format("%s", queryOut.getValue("txnSta"));
			logger.debug("[支付宝预下单后台通知处理] 原订单状态 txnSta=" + txnSta);

			if (StringConstans.OrderState.STATE_02.equals(txnSta)) {
				logger.debug("[支付宝预下单后台通知处理] 原订单状态为成功不进行更新处理");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("原订单状态为成功不进行更新处理");
				return outputParam;
			}

			if (!tradeAmount.equals(orderAmount)) {
				logger.debug("[支付宝预下单后台通知处理] 支付宝返回金额与POS下单金额不匹配");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付宝返回金额与POS下单金额不匹配");
				return outputParam;
			}

			// 交易状态
			String orderState = AliPayPayServiceImpl.alipayStatusToOrderStatus(tradeStatus).get("orderState");
			logger.debug("[支付宝预下单后台通知处理] 支部宝返回校验状态tradeStatus=" + tradeStatus + ",orderState=" + orderState);

			String resDesc = AliPayPayServiceImpl.alipayStatusToOrderStatus(tradeStatus).get("orderDesc");
			logger.debug("[支付宝预下单后台通知处理] 支部宝返回校验状态描述orderDesc=" + resDesc);

			InputParam updateInput = new InputParam();
			updateInput.setParamString(queryInput.getParamString());
			updateInput.putparamString("alipayTradeNo", alipayTradeNo);
			updateInput.putparamString("settleDate", gmtPayment.replaceAll("[-|:|\\s]", ""));
			updateInput.putparamString("receiptAmount", receiptAmount);
			updateInput.putparamString("payerid", payerid);
			updateInput.putparamString("txnSta", orderState);
			updateInput.putparamString("resDesc", resDesc);

			logger.debug("[支付宝预下单后台通知处理]   更新订单信息开始,请求报文:" + updateInput.toString());
			OutputParam updateOut = orderService.updateOrder(updateInput);
			logger.debug("[支付宝预下单后台通知处理]   更新订单信息 结束.返回报文:" + updateOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.debug("[支付宝预下单后台通知处理] 更新订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("更新订单信息失败");
				return outputParam;
			}

			outputParam.putValue("txnSta", orderState);
			outputParam.putValue("alOrder", queryOut.getReturnObj());
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);

			logger.debug("支付宝预下单后台通知处理流程[END]");

		} catch (FrameException e) {
			logger.error("[支付宝预下单后台通知处理] 支付宝预下单后台通知处理出现异常: " + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝预下单后台通知处理出现异常");
		} finally {
			logger.info("支付宝预下单后台通知处理流程[END],返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 处理支付宝退货后台通知
	 * 
	 * @param input
	 * @return
	 */
	private OutputParam handAlipayRefundNotify(InputParam input) {

		logger.info("----------------- 支付宝退货后台通知处理流程  START ---------------");

		OutputParam outputParam = new OutputParam();

		try {

			// 外部订单号
			String outTradeNo = String.format("%s", input.getValue("out_trade_no"));

			// 支付宝订单号
			String alipayTradeNo = String.format("%s", input.getValue("trade_no"));

			// 退款申请流水号
			String outBizNo = String.format("%s", input.getValue("out_biz_no"));

			// 交易状态
			String tradeStatus = String.format("%s", input.getValue("trade_status"));

			// 订单金额
			String orderAmount = String.format("%s", input.getValue("total_amount"));
			orderAmount = com.huateng.pay.common.util.StringUtil.amountTo12Str(orderAmount);

			// 支付宝总退款金额
			String alipayFefundFee = String.format("%s", input.getValue("refund_fee"));

			// 该笔交易买家付款时间
			String gmtPayment = String.format("%s", input.getValue("gmt_payment"));

			// 交易状态
			String orderState = AliPayPayServiceImpl.alipayStatusToOrderStatus(tradeStatus).get("orderState");
			logger.info("[支付宝预下单后台通知处理] 支部宝返回校验状态tradeStatus=" + tradeStatus + ",orderState=" + orderState);

			// 交易状态描述
			String resDesc = "退货成功";

			if (!StringConstans.OrderState.STATE_02.equals(orderState)) {
				resDesc = "退货失败";
			}

			InputParam queryInput = new InputParam();
			queryInput.putparamString("outRequestNo", outBizNo);

			logger.info("[支付宝退货后台通知处理]  根据退货标识请求号查询订单是否存在            开始");

			OutputParam queryOut = orderService.queryOrder(queryInput);

			logger.info("[支付宝退货后台通知处理]  根据退货标识请求号查询订单是否存在           结束");

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.error("[支付宝退货后台通知处理]  根据退货标识请求号查询订单没有找到匹配的记录");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("根据退货标识请求号查询订单没有找到匹配的记录");
				return outputParam;
			}

			logger.info("-----[支付宝退货后台通知处理]  根据退货标识请求号查询订单存在  ----- ");

			// 交易流水号
			String txnSeqId = String.format("%s", queryOut.getValue("txnSeqId"));

			// 交易日期
			String txnDt = String.format("%s", queryOut.getValue("txnDt"));

			// 交易时间
			String txnTm = String.format("%s", queryOut.getValue("txnTm"));

			logger.info("[支付宝退货后台通知处理] 交易 txnSeqId=" + txnSeqId + ",txnDt=" + txnDt + ",txnTm=" + txnTm);

			InputParam updateInput = new InputParam();
			updateInput.setParamString(queryInput.getParamString());
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("alipayFefundFee", alipayFefundFee);
			updateInput.putparamString("alipayTradeNo", alipayTradeNo);
			updateInput.putparamString("settleDate", gmtPayment.replaceAll("[-|:|\\s]", ""));
			updateInput.putparamString("txnSta", orderState);
			updateInput.putparamString("resDesc", resDesc);

			logger.info("[支付宝退货后台通知处理]   更新订单信息            开始");

			OutputParam updateOut = orderService.updateOrder(updateInput);

			logger.info("[支付宝退货后台通知处理]   更新订单信息            结束");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				logger.error("[支付宝退货后台通知处理] 更新订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			}

			// 原交易二维码交易流水号
			String initTxnSeqId = outTradeNo.substring(0, 10);

			// 原交易交易日期
			String initTxnDt = outTradeNo.substring(10, 18);

			// 原交易交易时间
			String initTxnTm = outTradeNo.substring(18, 24);

			logger.info("[支付宝退货后台通知处理] 原交易 initTxnSeqId=" + initTxnDt + ",initTxnDt=" + initTxnDt + ",initTxnTm="
					+ initTxnTm);

			queryInput.getParamString().clear();
			queryInput.putparamString("txnSeqId", initTxnSeqId);
			queryInput.putparamString("txnDt", initTxnDt);
			queryInput.putparamString("txnTm", initTxnTm);

			logger.info("[支付宝退货后台通知处理]  查询原交易是否存在            开始");

			OutputParam queryInitOut = orderService.queryOrder(queryInput);

			logger.info("[支付宝退货后台通知处理]  查询原交易是否存在           结束");

			if (!StringConstans.returnCode.SUCCESS.equals(queryInitOut.getReturnCode())) {
				logger.error("[支付宝退货后台通知处理] 查询原交易信息不存在");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("支付宝退货后台通知处理,查询原交易信息不存在");
				return outputParam;
			}

			// 原交易累计退款总额
			String totalRefundFee = String.format("%s", queryInitOut.getValue("totalRefundFee"));

			alipayFefundFee = StringUtil.amountTo12Str(alipayFefundFee);

			if (!alipayFefundFee.equals(totalRefundFee)) {

				logger.info("[支付宝退货后台通知处理]  原交易累计退货总金额和支付宝退货总金额不相等,更新原交易累计退货总金额");

				logger.info("[支付宝退货后台通知处理]   更新原订单累计退货总金额信息            开始");

				OutputParam updateInitOut = orderService.updateRefundTotalAmount(initTxnSeqId, initTxnDt, initTxnTm,
						alipayFefundFee);

				logger.info("[支付宝退货后台通知处理]   更新原订单累计退货总金额信息            结束");

				if (!StringConstans.returnCode.SUCCESS.equals(updateInitOut.getReturnCode())) {
					logger.error("[支付宝退货后台通知处理] 更新原订单累计退货总金额信息失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				}
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);

			logger.info("----------------- 支付宝退货后台通知处理流程  END ---------------");

		} catch (FrameException e) {
			logger.error("[支付宝退货后台通知处理] 支付宝退货后台通知处理流程出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.setReturnMsg("支付宝退货后台通知处理流程出现异常");
		}

		return outputParam;
	}

	/**
	 * 获取用户授权信息
	 * 
	 * @param input
	 * @return
	 */
	@Override
	public OutputParam getAuthToken(InputParam input) throws FrameException {

		logger.info("-----------------  获取用户授权信息处理流程  START ---------------input(" + input.toString() + ")");

		OutputParam outputParam = new OutputParam();

		try {

			/**************** 1.请求报文非空字段验证 ***********************/
			List<String> list = new ArrayList<String>();
			list.add("grantType");
			list.add("code");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			OutputParam authTokenOut = aliPayManager.getAuthToken(input);

			if (!StringConstans.returnCode.SUCCESS.equals(authTokenOut.getReturnCode())) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", authTokenOut.getReturnMsg());
				return outputParam;
			}

			Util.mapCopy(outputParam.getReturnObj(), authTokenOut.getReturnObj());
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "支付宝 获取用户授权信息成功");
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (Exception e) {
			logger.error("[支付宝 获取用户授权信息]支付宝 获取用户授权信息处理流程出现异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "支付宝 获取用户授权信息程异常");
		}
		logger.info(
				"----------------- 支付宝 获取用户授权信息处理流程 END ---------------outputParam(" + outputParam.toString() + ")");
		return outputParam;
	}

	/**
	 * 核心推送处理
	 * 
	 * @param settleInput
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void toCoreForSettleHandler(InputParam settleInput) throws FrameException {
		logger.info("核心推送处理start,请求报文:" + settleInput.toString());
		Map<String, Object> alipayOrder = (Map<String, Object>) settleInput.getValue("alOrder");
		String ewmData = String.format("%s", alipayOrder.get("ewmData"));
		String txnSeqId = String.format("%s", alipayOrder.get("txnSeqId"));
		if (!txnSeqId.startsWith("TC")) {
			logger.debug("当前订单[" + txnSeqId + "]非一码通交易不进行入账");
			return;
		}
		InputParam queryThreeCodeInput = new InputParam();
		queryThreeCodeInput.putparamString("ewmData", ewmData);

		// 根据流水表中的二维码明文查询三码信息表
		OutputParam queryOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

		if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
			logger.debug("[T+0入账] 查询三码信息表失败");
			return;
		}
		// 商家账号
		String accountNo = String.format("%s", queryOut.getValue("acctNo"));

		// 商户名称
		String merName = String.format("%s", queryOut.getValue("merName"));

		// 商户编号
		String merId = String.format("%s", queryOut.getValue("merId"));

		// 机构号
		String orgCode = String.format("%s", queryOut.getValue("orgCode"));

		// 手续费率
		String alipayFeeRate = String.format("%s", alipayOrder.get("bankFeeRate"));

		// 交易状态
		String txnSta = String.format("%s", settleInput.getValue("txnSta"));

		// 支付方式
		String payAccessType = String.format("%s", alipayOrder.get("payAccessType"));

		// 订单清算方式'0':T+0,'1':T+1
		String settleMethod = "";
		if (!StringUtil.isEmpty(alipayOrder.get("settleMethod"))) {
			settleMethod = String.format("%s", alipayOrder.get("settleMethod"));
		}

		// 入账状态 默认为'0', '1':入账成功 '2'：入账失败 '3'：状态未知
		String accountedFlag = "";
		if (!StringUtil.isEmpty(alipayOrder.get("accountedFlag"))) {
			accountedFlag = String.format("%s", alipayOrder.get("accountedFlag"));
		}

		String txnChannel = String.format("%s", alipayOrder.get("txnChannel"));

		// 需同时满足交易成功——02；三码支付——8001；清算方式T+0；入账未成功：不为01；支付方式为支付宝支付
		if (StringConstans.OrderState.STATE_02.equals(txnSta) && StringConstans.CHANNEL.CHANNEL_SELF.equals(txnChannel)
				&& StringConstans.SettleMethod.SETTLEMETHOD0.equals(settleMethod)
				&& !StringConstans.AccountedFlag.ACCOUNTEDSUCCESS.equals(accountedFlag)
				&& StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
			alipayOrder.put("acctNo", accountNo);
			alipayOrder.put("merName", merName);
			alipayOrder.put("merId", merId);
			alipayOrder.put("orgCode", orgCode);
			alipayOrder.put("alipayFeeRate", alipayFeeRate);
			settleInput.setParams(alipayOrder);
			this.settleThreadHandler(settleInput);
		}

	}

	private void settleThreadHandler(InputParam settleInput) throws FrameException {
		NotifyMessage notifyMessage = new NotifyMessage();
		this.getSettleMessageToCore(notifyMessage, settleInput);
		ThreadNotifyHelper.notifyThread(notifyMessage);

	}

	/**
	 * T+0清算报文体内容
	 * 
	 * @param notifyMessage
	 * @param settleInput
	 * @throws Exception
	 */
	private void getSettleMessageToCore(NotifyMessage notifyMessage, InputParam settleInput) throws FrameException {
		// 二维码流水号
		String txnSeqId = String.format("%s", settleInput.getValue("txnSeqId"));

		// 交易日期
		String txnDt = String.format("%s", settleInput.getValue("txnDt"));

		// 交易时间
		String txnTm = String.format("%s", settleInput.getValue("txnTm"));

		// 收款方账号
		String accountNo = String.format("%-22s", settleInput.getValue("acctNo"));

		// 币种代码
		String currencyCode = settleInput.getValue("currencyCode").toString();
		if (StringConstans.SETTLE_CURRENTY_TYPE.equals(currencyCode)) {
			currencyCode = String.format("%3s", StringConstans.SettleInfo.CURRENCY_CODE);
		}

		/*
		 * //商户名称 String merName = settleInput.getValue("merName").toString();
		 * //计算字符串中汉字个数 int chnLen = merName.getBytes().length - merName.length(); if
		 * (chnLen == 0) { merName = String.format("%-80s", merName); } else { int
		 * chnArrLen = EBCDICGBK.chnArrLen(merName);
		 * //计算字符串中汉字有几部分,转码后0E表示汉字开始，0F表示汉字结束，所以需要减去两个字节 merName =
		 * String.format("%-80s", merName).substring(0, 80 - chnLen); }
		 */

		String merName = String.format("%-80s", "");

		// 付款方账号
		String outAccountNo = String.format("%-22s", "");

		// 手续费率
		String alipayFeeRate = String.format("%s", settleInput.getValue("alipayFeeRate"));

		// 交易金额
		String tradeMoneyStr = String.format("%s", settleInput.getValue("tradeMoney"));

		// 计算去除手续费后的金额
		String moneyRemoveFee = Util.getActualTradeMoney(alipayFeeRate, tradeMoneyStr);

		String tradeMoney = String.format("%17s", moneyRemoveFee);
		logger.debug("[T+0清算]去除手续费后的交易金额" + tradeMoney);

		// 手续费
		String chargeFee = String.format("%-17s", "");

		// 对账分类编号
		String checkAccountNo = String.format("%-15s", "");

		// 商户编号
		String merId = String.format("%s", settleInput.getValue("merId"));
		merId = merId.getBytes().length > 15 ? merId.substring(0, 3) + merId.substring(8) : merId;
		merId = String.format("%15s", merId);

		// 支付方式
		String payAccessType = String.format("%s", settleInput.getValue("payAccessType"));
		if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
			payAccessType = StringConstans.SettleInfo.PAY_ACCESS_TYPE_ALIPAY;
		}
		int payAccessLen = payAccessType.getBytes().length - payAccessType.length();
		payAccessType = String.format("%-10s", payAccessType).substring(0, 10 - payAccessLen + 1);

		// 付款方姓名
		String payerName = String.format("%-80s", payAccessType).substring(0, 80 - payAccessLen + 1);

		// 机构号
		// String orgCode = String.format("%-6s", settleInput.getValue("orgCode"));
		String orgCode = StringUtils.rightPad(settleInput.getValue("orgCode").toString().substring(0, 3), 6, '0');

		// 附言
		String postscript = String.format("%-80s", "");

		/*
		 * //备注 String remark = ""; if
		 * (!StringUtil.isEmpty(settleInput.getValue("remark"))) { //remark =
		 * String.format("%-80s", settleInput.getValue("remark")); chnLen =
		 * remark.getBytes().length - remark.length(); if (chnLen == 0) { remark =
		 * String.format("%-80s", remark); } else { //计算字符串中汉字有几部分 int chnArrLen =
		 * EBCDICGBK.chnArrLen(remark); //转码后0E表示汉字开始，0F表示汉字结束，所以有一部分汉字就需要减去两个字节 remark
		 * = String.format("%-80s", remark).substring(0, 80 - chnLen); } }else { remark
		 * = String.format("%-80s", ""); }
		 */

		String remark = String.format("%-80s", "");

		// 备用
		String reverse = String.format("%-80s", "");

		NotifyMessage.SettleMessageToCore settleMessageToCore = new NotifyMessage.SettleMessageToCore();
		settleMessageToCore.setTxnDt(txnDt);
		settleMessageToCore.setTxnTm(txnTm);
		settleMessageToCore.setAccountNo(accountNo);
		settleMessageToCore.setCurrencyCode(currencyCode);
		settleMessageToCore.setMerName(merName);
		settleMessageToCore.setOutAccountNo(outAccountNo);
		settleMessageToCore.setPayerName(payerName);
		settleMessageToCore.setTradeMoney(tradeMoney);
		settleMessageToCore.setChargeFee(chargeFee);
		settleMessageToCore.setCheckAccountNo(checkAccountNo);
		settleMessageToCore.setMerId(merId);
		settleMessageToCore.setPayAccessType(payAccessType);
		settleMessageToCore.setOrgId(orgCode);
		settleMessageToCore.setPostscript(postscript);
		settleMessageToCore.setRemark(remark);
		settleMessageToCore.setReverse(reverse);

		notifyMessage.setTxnSeqId(txnSeqId);
		notifyMessage.setSettleMessageToCore(settleMessageToCore);

	}

	private void getOnlineNotifyMessage(NotifyMessage notifyMessage, OutputParam queryOut) {

		// 二维码流水号
		String txnSeqId = String.format("%s", queryOut.getValue("txnSeqId"));
		// 交易日期
		String txnDt = String.format("%s", queryOut.getValue("txnDt"));
		// 交易时间
		String txnTm = String.format("%s", queryOut.getValue("txnTm"));
		// 下单金额
		String tradeAmount = String.format("%s", queryOut.getValue("tradeMoney"));
		// 交易类型
		String tradeType = String.format("%s", queryOut.getValue("txnType"));
		// 币种
		String currencyCode = String.format("%s", queryOut.getValue("currencyCode"));
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
		String gmtPayment = String.format("%s", queryOut.getValue("gmtPayment"));
		// 买家信息
		String buyerLogonId = String.format("%s", queryOut.getValue("buyerLogonId"));

		NotifyMessage.OnlineNotifyMessage message = new NotifyMessage.OnlineNotifyMessage();
		message.setTxnSeqId(txnSeqId);
		message.setTxnTime(txnDt + txnTm);
		message.setOrderAmount(tradeAmount);
		message.setTradeType(tradeType);
		message.setClearDate(gmtPayment);
		message.setPayAccessType(payAccessType);
		message.setPayType(payType);
		message.setOrderNumber(orderNumber);
		message.setOrderTime(merOrDt + merOrTm);
		message.setMerId(merId);
		message.setCurrencyType(currencyCode);
		message.setPayerAcctNbr(buyerLogonId);

		notifyMessage.setTxnSeqId(txnSeqId);
		notifyMessage.setOnlineNotifyMessage(message);
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
		String gmtPayment = String.format("%s", queryOut.getValue("gmtPayment"));
		// 买家信息
		String buyerLogonId = String.format("%s", queryOut.getValue("buyerLogonId"));
		// 订单备注
		String remark = String.format("%s", queryOut.getValue("remark"));
		// 银行手续费率
		String bankFeeRate = String.format("%s", queryTcOut.getValue("bankFeeRate"));
		// 账号
		String acctNo = String.format("%s", queryTcOut.getValue("acctNo"));
		// 机构号
		String orgCode = String.format("%s", queryTcOut.getValue("orgCode"));
		// 手续费
		String fee = StringUtil.getFeeByTradeAmount(bankFeeRate, tradeAmount);

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
		message.setBuyerInfo(buyerLogonId);
		message.setRemark(remark);
		message.setFee(fee);
		message.setAcctNo(acctNo);
		message.setOrgCode(orgCode);
		notifyMessage.setTxnSeqId(txnSeqId);
		notifyMessage.setThreeCodeNotifyMessage(message);
	}

	public IOrderService getOrderService() {

		return orderService;
	}

	public void setOrderService(IOrderService orderService) {

		this.orderService = orderService;
	}

	public IAliPayManager getAliPayManager() {

		return aliPayManager;
	}

	public void setAliPayManager(IAliPayManager aliPayManager) {

		this.aliPayManager = aliPayManager;
	}

	public IOrderDao getOrderDao() {

		return orderDao;
	}

	public void setOrderDao(IOrderDao orderDao) {

		this.orderDao = orderDao;
	}

	public IThreeCodeStaticQRCodeDataService getThreeCodeStaticQRCodeDataService() {
		return threeCodeStaticQRCodeDataService;
	}

	public void setThreeCodeStaticQRCodeDataService(
			IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService) {
		this.threeCodeStaticQRCodeDataService = threeCodeStaticQRCodeDataService;
	}

	public AliPayMerchantSynchService getAliPayMerchantSynchService() {
		return aliPayMerchantSynchService;
	}

	public void setAliPayMerchantSynchService(AliPayMerchantSynchService aliPayMerchantSynchService) {
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
	}

	public IMerchantChannelService getMerchantChannelService() {
		return merchantChannelService;
	}

	public void setMerchantChannelService(IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

}
