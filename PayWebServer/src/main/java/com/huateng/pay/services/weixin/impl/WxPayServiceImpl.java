package com.huateng.pay.services.weixin.impl;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import com.alipay.demo.trade.config.Configs;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.http.HttpRequestClient;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.common.validate.YlWXValidation;
import com.huateng.pay.dao.inter.IOrderDao;
import com.huateng.pay.handler.services.OrderQueryManager;
import com.huateng.pay.handler.thread.ThreadNotifyHelper;
import com.huateng.pay.manager.weixin.IWxManager;
import com.huateng.pay.po.notify.NotifyMessage;
import com.huateng.pay.po.weixin.CloseReqData;
import com.huateng.pay.po.weixin.DownloadBillReqData;
import com.huateng.pay.po.weixin.OrderQueryReqData;
import com.huateng.pay.po.weixin.RefundQueryReqData;
import com.huateng.pay.po.weixin.ReserveReqData;
import com.huateng.pay.po.weixin.UnifiedOrderResData;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;
import com.huateng.pay.services.weixin.WxMerchantSynchService;
import com.huateng.pay.services.weixin.WxPayService;
import com.huateng.pay.services.weixin.YLWXPayService;
import com.huateng.pay.services.weixin.wxutil.WXPayConstants;
import com.huateng.pay.services.weixin.wxutil.WXPayUtil;
import com.huateng.utils.FileUtil;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.wldk.framework.mapping.MappingContext;
import com.wldk.framework.utils.DateUtils;
import com.wldk.framework.utils.MappingUtils;

/**
 * 微信支付接口实现
 * 
 * @author guohuan
 * 
 */
public class WxPayServiceImpl implements WxPayService {
	private static final Logger logger = LoggerFactory.getLogger(WxPayServiceImpl.class);
	private IOrderDao orderDao;
	private IOrderService orderService;
	private IWxManager wxManager;
	private IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService;
	private IMerchantChannelService merchantChannelService;
	private YLWXPayService ylwxPayService;
	private OrderQueryManager orderQueryManager;
	private WxMerchantSynchService wxMerchantSynchService;

	/**
	 * 微信支付统一下单入口
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam wxUnifiedConsume(InputParam input) throws FrameException {

		logger.info("[微信下单流程         START]wxUnifiedConsume:" + input.toString());

		OutputParam out = new OutputParam();

		try {
			// 请求报文非空字段验证
			List<String> list = new ArrayList<String>();
			list.add("orderAmount");
			list.add("channel");
			list.add("orderNumber");
			list.add("orderTime");
			list.add("merId");
			list.add("payType");
			list.add("transType");
			list.add("merName");//
			list.add("isCredit");// 是否支持信用卡
			list.add("subWxMerId");// 微信子商户
			list.add("deviceInfo");
			list.add("currencyType");
			list.add("payAccessType");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信支付统一下单] 请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			String merId = input.getValue("merId").toString();
			String orderNumber = input.getValue("orderNumber").toString();
			String orderTime = input.getValue("orderTime").toString();
			String orderAmount = input.getValue("orderAmount").toString();
			String transType = input.getValue("transType").toString();
			String channel = input.getValue("channel").toString();
			String currencyType = input.getValue("currencyType").toString();
			String payAccessType = input.getValue("payAccessType").toString();
			String payType = input.getValue("payType").toString();
			String isCredit = input.getValue("isCredit").toString();
			String subWxMerId = input.getValue("subWxMerId").toString();

			Map<String, Object> respMap = new HashMap<String, Object>();
			respMap.put("merId", merId);
			respMap.put("orderNumber", orderNumber);
			respMap.put("orderTime", orderTime);
			respMap.put("orderAmount", orderAmount);
			respMap.put("transType", transType);
			respMap.put("channel", channel);
			respMap.put("currencyType", currencyType);
			respMap.put("payAccessType", payAccessType);
			respMap.put("payType", payType);
			out.setReturnObj(respMap);

			/******************** 1.报文参数验证 *****************************/
			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) == 0 || amount.compareTo(new BigDecimal(0)) == -1
					|| orderAmount.length() != 12) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("订单金额[orderAmount]非法:" + orderAmount);
				return out;
			}

			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易类型[transType]错误：" + transType);
				return out;
			}

			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
					&& !StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付类型[payType]非法：" + payType);
				return out;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易接入类型[payAccessType]错误：" + payAccessType);
				return out;
			}

			if (!"0".equals(isCredit) & !"1".equals(isCredit)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("是否支持信用卡值[isCredit]非法：" + isCredit);
				return out;
			}

			if (orderTime.length() != 14) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("外部订单时间[orderTime]非法：" + orderTime);
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("渠道[channel]非法：" + channel);
				return out;
			}

			/******************** 2.重复订单验证 *****************************/
			Map<String, String[]> orderParam = new HashMap<String, String[]>();
			orderParam.put("merId", new String[] { merId });
			orderParam.put("merOrderId", new String[] { orderNumber });
			orderParam.put("merOrDt", new String[] { orderTime.substring(0, 8) });
			orderParam.put("merOrTm", new String[] { orderTime.substring(8, 14) });
			orderParam.put("transType", new String[] { StringConstans.TransType.TRANS_CONSUME });

			List<Map<String, Object>> orderL = orderDao.queryRealOrderByMer(orderParam);

			// 如果订单已经存在
			if (!StringUtil.listIsEmpty(orderL)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("原订单已存在,请勿重复提交" + orderL.get(0).toString());
				return out;
			}

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subWxMerId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(outQuery.getReturnMsg());
				return out;
			}

			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String key = configMap.get(Dict.WX_KEY);
			String appid = configMap.get(Dict.WX_APPID);
			String mch_id = configMap.get(Dict.WX_MERID);
			String pfxPath = configMap.get(Dict.WX_PFX_PATH);
			String pwd = configMap.get(Dict.WX_PWD);

			/******************** 3.新增 订单表流水 *****************************/

			input.putParams("resDesc", "交易发起");
			input.putParams("wxMerId", mch_id);
			logger.debug("[微信支付统一下单] 增加订单信息    开始：" + input.toString());
			OutputParam orderOut = wxManager.addConsumeOrder(input);
			logger.debug("[微信支付统一下单] 增加订单信息   结束,返回报文:" + orderOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				return orderOut;
			}

			String txnDt = orderOut.getValue("txnDt").toString();
			String txnTm = orderOut.getValue("txnTm").toString();
			String txnSeqId = orderOut.getValue("txnSeqId").toString();

			/************************ 4.去微信发送下单请求 ********************************/
			InputParam coreInput = new InputParam();
			coreInput.putParams("txnSeqId", txnSeqId);
			coreInput.putParams("orderAmount", orderAmount);
			coreInput.putParams("merId", merId);
			coreInput.putParams("orderNumber", orderNumber);
			coreInput.putParams("txnDt", txnDt);
			coreInput.putParams("txnTm", txnTm);
			coreInput.putParams("payType", payType);
			coreInput.putParams("merName", input.getValue("merName"));
			coreInput.putParams("isCredit", input.getValue("isCredit"));
			coreInput.putParams("subWxMerId", input.getValue("subWxMerId"));//
			if (!StringUtil.isNull(input.getValue("deviceInfo"))) {
				coreInput.putParams("deviceInfo", input.getValue("deviceInfo").toString());
			}
			if (!StringUtil.isNull(input.getValue("productId"))) {
				coreInput.putParams("productId", input.getValue("productId").toString());
			}

			coreInput.putParams("appid", appid);
			coreInput.putParams("mch_id", mch_id);
			coreInput.putParams("pfxPath", pfxPath);
			coreInput.putParams("pwd", pwd);
			coreInput.putParams("key", key);

			logger.debug("[微信支付统一下单]  调用微信组报文发送请求接口    开始]:" + coreInput);
			OutputParam coreOutPut = unifiedOrder(coreInput);/** 调用微信组报文发送请求接口 */
			logger.debug("[微信支付统一下单]  调用微信组报文发送请求接口    结束,返回报文:" + coreOutPut.toString());

			coreOutPut.getReturnObj().put("orderAmount", orderAmount);
			coreOutPut.getReturnObj().put("merId", merId);
			coreOutPut.getReturnObj().put("orderTime", orderTime);
			coreOutPut.getReturnObj().put("orderNumber", orderNumber);
			coreOutPut.getReturnObj().put("payType", payType);
			coreOutPut.getReturnObj().put("currencyType", currencyType);
			coreOutPut.getReturnObj().put("payAccessType", payAccessType);
			coreOutPut.getReturnObj().put("transType", transType);
			coreOutPut.getReturnObj().put("channel", channel);
			coreOutPut.getReturnObj().put("txnTime", txnDt + txnTm);

			logger.debug("----------------- 微信下单流程         END--------------------");

			return coreOutPut;
		} catch (Exception e) {
			logger.error("微信支付统一下单处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信支付统一下单处理失败");
			return out;
		}
	}

	/**
	 * 微信支付统一下单入口
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam wxUnifiedConsumeYL(InputParam input) throws FrameException {

		logger.info("[银联微信下单流程    START]wxUnifiedConsumeYL:" + input.toString());

		OutputParam out = new OutputParam();

		try {
			// 请求报文非空字段验证
			List<String> list = new ArrayList<String>();
			list.add("orderAmount");
			list.add("channel");
			list.add("orderNumber");
			list.add("orderTime");
			list.add("merId");
			list.add("payType");
			list.add("transType");
			list.add("merName");//
			list.add("isCredit");// 是否支持信用卡
			// list.add("subWxMerId");// 微信子商户
			list.add("deviceInfo");
			list.add("currencyType");
			list.add("payAccessType");
			list.add(Dict.rate);

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[银联微信支付统一下单] 请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			String merId = input.getValue("merId").toString();
			String orderNumber = input.getValue("orderNumber").toString();
			String orderTime = input.getValue("orderTime").toString();
			String orderAmount = input.getValue("orderAmount").toString();
			String transType = input.getValue("transType").toString();
			String channel = input.getValue("channel").toString();
			String currencyType = input.getValue("currencyType").toString();
			String payAccessType = input.getValue("payAccessType").toString();
			String payType = input.getValue("payType").toString();
			String isCredit = input.getValue("isCredit").toString();
			String subWxMerId = input.getValue(Dict.sub_mch_id).toString();
			String rateChannel = StringUtil.toString(input.getValue(Dict.rate));
			String deviceInfo = StringUtil.toString(input.getValue("deviceInfo"));
			String merName = StringUtil.toString(input.getValue("merName"));
			String customerIp = StringUtil.isNull(input.getValue("customerIp")) ? "127.0.0.1"
					: input.getValue("customerIp").toString();

			/******************** 1.报文参数验证 *****************************/
			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) == 0 || amount.compareTo(new BigDecimal(0)) == -1
					|| orderAmount.length() != 12) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("订单金额[orderAmount]非法:" + orderAmount);
				return out;
			}

			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易类型[transType]错误：" + transType);
				return out;
			}

			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
					&& !StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("支付类型[payType]非法：" + payType);
				return out;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("交易接入类型[payAccessType]错误：" + payAccessType);
				return out;
			}

			if (!"0".equals(isCredit) & !"1".equals(isCredit)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("是否支持信用卡值[isCredit]非法：" + isCredit);
				return out;
			}

			if (orderTime.length() != 14) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("外部订单时间[orderTime]非法：" + orderTime);
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("渠道[channel]非法：" + channel);
				return out;
			}

			/******************** 2.重复订单验证 *****************************/
			Map<String, String[]> orderParam = new HashMap<String, String[]>();
			orderParam.put("merId", new String[] { merId });
			orderParam.put("merOrderId", new String[] { orderNumber });
			orderParam.put("merOrDt", new String[] { orderTime.substring(0, 8) });
			orderParam.put("merOrTm", new String[] { orderTime.substring(8, 14) });
			orderParam.put("transType", new String[] { StringConstans.TransType.TRANS_CONSUME });

			List<Map<String, Object>> orderL = orderDao.queryRealOrderByMer(orderParam);

			// 如果订单已经存在
			if (!StringUtil.listIsEmpty(orderL)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("原订单已存在,请勿重复提交" + orderL.get(0).toString());
				return out;
			}

			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String mch_id = configMap.get(Dict.WX_MERID);

			/******************** 3.新增 订单表流水 *****************************/

			input.putParams("resDesc", "交易发起");
			input.putParams("wxMerId", mch_id);
			OutputParam orderOut = wxManager.addConsumeOrder(input);

			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				return orderOut;
			}

			String txnDt = orderOut.getValue("txnDt").toString();
			String txnTm = orderOut.getValue("txnTm").toString();
			String txnSeqId = orderOut.getValue("txnSeqId").toString();

			/************************ 4.去微信发送下单请求 ********************************/

			System.out.println("统一下单");
			HashMap<String, String> data = new HashMap<String, String>();
			if (!"1".equals(isCredit)) {
				// 如果支持本行卡或他行卡的信用卡，则微信支付支持信用卡
				isCredit = "no_credit";
			} else {
				isCredit = "";
			}
			/**
			 * 组装请求报文
			 */
			data.put("sub_mch_id", subWxMerId);
			data.put("out_trade_no", txnSeqId); // 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母，且在同一个商户号下唯一。
			data.put("fee_type", "CNY"); // 符合ISO 4217标准的三位字母代码，默认人民币：CNY
			data.put("total_fee", orderAmount); // 订单总金额，只能为整数
			data.put("device_info", deviceInfo); // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
			data.put("trade_type", StringConstans.WeiXinTransType.NATIVE); // 交易类型 JSAPI 公众号支付 NATIVE 扫码支付 APP APP支付
			// data.put("nonce_str", WXPayUtil.generateUUID()); //随机字符串，不长于32位。推荐随机数生成算法

			// 设置商品描述信息，微信的body字段
			String productInfo = merName + "-订单编号" + orderNumber;

			data.put("body", productInfo); // 商品或支付单简要描述，格式要求：门店品牌名-城市分店名-实际商品名称
			data.put("spbill_create_ip", customerIp); // 终端IP APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
			data.put("notify_url", Constants.getParam("tsdk.notifyUrl")); // 通知地址
																			// 接收银联异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
			data.put("sign_type", "RSA");
			data.put("attach", txnDt); // 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据

			String dtTm = DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS);
			Date expireDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(dtTm);
			data.put("time_start", dtTm);// 交易起始时间 订单生成时间，格式为yyyyMMddHHmmss
			data.put("time_expire", DateUtil.format(DateUtil.addMinute(expireDate, 5), DateUtil.YYYYMMDDHHMMSS));
			data.put("limit_pay", isCredit); // 指定支付方式 no_credit--指定不能使用信用卡支付
			if (!StringUtil.isNull(input.getValue("productId"))) {
				data.put("productId", input.getValue("productId").toString());
			}

			String url = Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.prepayUrlSuffix");

			Map<String, String> resultMap = ylwxPayService.wxSend(data, cacheKey, url);

			if (StringConstans.WxTradeStatus.SUCCESS.equals(resultMap.get("result_code"))) {

				/************************ 更新订单状态为已下单 ******************************/
				logger.debug("[微信支付统一下单] 完成下单后更新订单状态为已下单(已发送)");
				InputParam updateInput = new InputParam();
				updateInput.putParams("codeUrl", resultMap.get("code_url"));
				updateInput.putParams("wxPrepayId", resultMap.get("prepay_id"));
				updateInput.putParams("randomStr", data.get("nonce_str"));
				updateInput.putParams("txnSta", StringConstans.OrderState.STATE_06);
				updateInput.putParams("txnSeqId", txnSeqId);
				updateInput.putParams("txnDt", txnDt);
				updateInput.putParams("txnTm", txnTm);
				updateInput.putParams("payType", payType);

				OutputParam orderOut2 = orderService.updateWxOrderInfo(updateInput);
				if (!StringConstans.returnCode.SUCCESS.equals(orderOut2.getReturnCode())) {
					return orderOut2;
				}
				out.putValue("codeUrl", resultMap.get("code_url"));
				out.putValue("prepayId", resultMap.get("prepay_id"));
				out.putValue("txnSeqId", txnSeqId);
				out.putValue("orderAmount", orderAmount);
				out.putValue("merId", merId);
				out.putValue("orderTime", orderTime);
				out.putValue("orderNumber", orderNumber);
				out.putValue("payType", payType);
				out.putValue("currencyType", currencyType);
				out.putValue("payAccessType", payAccessType);
				out.putValue("transType", transType);
				out.putValue("channel", channel);
				out.putValue("txnTime", txnDt + txnTm);
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				out.setReturnMsg("银联微信支付统一下单成功");
			} else {
				InputParam updateInput = new InputParam();
				updateInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
				updateInput.putParams("txnSeqId", txnSeqId);
				updateInput.putParams("txnDt", txnDt);
				updateInput.putParams("txnTm", txnTm);
				updateInput.putParams("payType", payType);
				updateInput.putParams("subWxMerId", subWxMerId);
				updateInput.putParams("resDesc", "微信下单失败");
				orderService.updateWxOrderInfo(updateInput);
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("银联微信支付统一下单,微信返回失败:" + resultMap.get("err_code_des"));

			}
			logger.debug("----------------- 微信下单流程         END--------------------");
			return out;
		} catch (Exception e) {
			logger.error("微信支付统一下单处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信支付统一下单处理失败");
			return out;
		}
	}

	/**
	 * 微信被扫支付(刷卡)入口
	 * 
	 * @param inputParam
	 * @return
	 * @throws Exception
	 */
	public OutputParam wxMicroPay(InputParam input) throws FrameException {

		logger.info("[微信被扫支付(刷卡)]  wxMicroPay START:" + input.toString());

		OutputParam out = new OutputParam();

		try {

			/**************** 1.请求报文非空字段验证 ***********************/
			List<String> list = new ArrayList<String>();
			list.add("orderAmount");
			list.add("isCredit");
			list.add("channel");
			list.add("transType");
			list.add("orderNumber");
			list.add("orderTime");
			list.add("merId");
			list.add("merName");
			list.add("payType");
			list.add("transType");
			list.add("subWxMerId");
			list.add("payAccessType");
			list.add("currencyType");
			list.add("authCode");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信被扫支付(刷卡)接口] 请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			String orderAmount = input.getValue("orderAmount").toString();// 分
			String txnAmount = new BigDecimal(orderAmount).divide(new BigDecimal(100)).toString();
			String merSupportCredit = input.getValue("isCredit").toString();// 商户支持卡种
			String merId = input.getValue("merId").toString();
			String orderNumber = input.getValue("orderNumber").toString();
			String orderTime = input.getValue("orderTime").toString();
			String payType = input.getValue("payType").toString();
			String merName = input.getValue("merName").toString();
			String subWxMerId = input.getValue("subWxMerId").toString();
			String transType = input.getValue("transType").toString();
			String payAccessType = input.getValue("payAccessType").toString();
			String merIsCredit = input.getValue("isCredit").toString();
			String channel = input.getValue("channel").toString();

			Map<String, Object> respMap = new HashMap<String, Object>();
			respMap.put("merId", merId);
			respMap.put("orderNumber", orderNumber);
			respMap.put("orderTime", orderTime);
			respMap.put("orderAmount", orderAmount);
			respMap.put("transType", input.getValue("transType"));
			respMap.put("channel", input.getValue("channel"));
			respMap.put("currencyType", input.getValue("currencyType"));
			respMap.put("payAccessType", input.getValue("payAccessType"));
			respMap.put("payType", input.getValue("payType"));
			out.setReturnObj(respMap);

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) == 0 || amount.compareTo(new BigDecimal(0)) == -1
					|| orderAmount.length() != 12) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]订单金额[orderAmount]非法：" + orderAmount);
				return out;
			}

			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]交易类型[transType]错误：" + transType);
				return out;
			}

			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
					&& !StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]支付类型[payType]非法：" + payType);
				return out;
			}

			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]交易接入类型[payAccessType]错误：" + payAccessType);
				return out;
			}

			if (!StringConstans.IsCredit.NONSUPPORT_CREDIT.equals(merIsCredit)
					& !StringConstans.IsCredit.SUPPORT_CREDIT.equals(merIsCredit)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]是否支持信用卡值[isCredit]非法：" + merIsCredit);
				return out;
			}

			if (orderTime.length() != 14) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]外部订单时间[orderTime]非法：" + orderTime);
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]渠道[channel]非法：" + channel);
				return out;
			}

			/******************** 3.重复订单验证 *****************************/
			logger.debug("[微信被扫支付(刷卡)，原交易订单校验]");
			Map<String, String[]> orderParam = new HashMap<String, String[]>();
			orderParam.put("merId", new String[] { merId });
			orderParam.put("merOrderId", new String[] { orderNumber });
			orderParam.put("merOrDt", new String[] { orderTime.substring(0, 8) });
			orderParam.put("transType", new String[] { StringConstans.TransType.TRANS_CONSUME });
			List<Map<String, Object>> orderL = orderDao.queryRealOrderByMer(orderParam);
			// 对订单表中已存在的订单进行验证
			if (!StringUtil.listIsEmpty(orderL)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]原订单已存在,请勿重复提交");
				return out;
			}

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subWxMerId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, outQuery.getReturnMsg());
				return out;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));

			String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);

			// 获取微信商户号的key
			String key = configMap.get(Dict.WX_KEY);
			String appid = configMap.get(Dict.WX_APPID);
			String mch_id = configMap.get(Dict.WX_MERID);
			String pfxPath = configMap.get(Dict.WX_PFX_PATH);
			String pwd = configMap.get(Dict.WX_PWD);

			/******************** 4.新增 订单表流水 *****************************/
			input.putParams("resDesc", "交易发起");
			input.putParams("wxMerId", mch_id);
			logger.debug("微信被扫支付(刷卡) 新增订单信息   开始:" + input.toString());
			OutputParam orderOut = wxManager.addConsumeOrder(input);
			logger.debug("微信被扫支付(刷卡) 新增订单信息   结束,返回报文:" + orderOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连被扫支付]" + orderOut.toString());
				return out;
			}

			logger.debug("微信被扫支付(刷卡) 新增订单信息 成功");

			String txnDt = orderOut.getValue("txnDt").toString();
			String txnTm = orderOut.getValue("txnTm").toString();
			String txnSeqId = orderOut.getValue("txnSeqId").toString();

			/************************ 5.组装微信被扫支付(刷卡)报文并发送请求 ******************************/
			logger.debug("[微信被扫支付(刷卡),发送微信下单请求处理    START]");
			String deviceInfo = StringUtil.toString("deviceInfo");
			String authCode = input.getValue("authCode").toString();// 用户授权码
			String productInfo = merName + "-订单编号" + orderNumber;// 商品名称

			/************************ 验证商户是否支持信用卡支付 ******************************/
			String isCredit = "";
			if (!"1".equals(merSupportCredit)) {
				// 如果支持本行卡或他行卡的信用卡，则微信支付支持信用卡
				isCredit = "no_credit";
			}

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("device_info", deviceInfo);
			dataMap.put("body", productInfo);
			dataMap.put("attach", txnDt);
			dataMap.put("out_trade_no", txnSeqId);
			dataMap.put("total_fee", com.huateng.util.Util.transTxnAt(0, txnAmount));
			dataMap.put("spbill_create_ip", "127.0.0.1");
			String dtTm = DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS);
			Date expireDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(dtTm);
			dataMap.put("time_start", dtTm);
			dataMap.put("time_expire", DateUtil.format(DateUtil.addMinute(expireDate, 5), DateUtil.YYYYMMDDHHMMSS));
			dataMap.put("goods_tag", "");
			dataMap.put("limitPay", isCredit);
			dataMap.put("appid", appid);
			dataMap.put("mch_id", mch_id);
			dataMap.put("pfxPath", pfxPath);
			dataMap.put("pwd", pwd);
			dataMap.put("authCode", authCode);
			dataMap.put("subMchId", subWxMerId);

			// 向微信发送被扫支付请求并处理返回报文
			InputParam wxInput = new InputParam();
			wxInput.putParams("dataMap", dataMap);
			wxInput.putParams("key", key);
			wxInput.putParams("payType", payType);
			wxInput.putParams("txnDt", txnDt);
			wxInput.putParams("txnTm", txnTm);
			wxInput.putParams("txnSeqId", txnSeqId);
			wxInput.putParams("merId", merId);

			logger.debug("微信被扫支付(刷卡)  调用微信被扫支付发送请求处理接口 开始:" + wxInput.toString());
			OutputParam wxOut = wxManager.toWxMicroPay(wxInput);/** 调用微信被扫支付发送请求处理接口 */
			logger.debug("微信被扫支付(刷卡)  调用微信被扫支付发送请求处理接口 结束,返回报文:" + wxOut.toString());

			// 构建返回报文参数
			respMap.put("txnSeqId", txnSeqId);
			respMap.put("txnTime", txnDt + txnTm);
			respMap.put(Dict.respCode, wxOut.getValue(Dict.respCode));
			respMap.put(Dict.respDesc, wxOut.getValue(Dict.respDesc));
			if (!StringUtil.isEmpty(wxOut.getValue("wxPayTime"))) {
				respMap.put("wxPayTime", wxOut.getValue("wxPayTime"));
			}
			if (!StringUtil.isEmpty(wxOut.getValue("wxOrderNo"))) {
				respMap.put("wxOrderNo", wxOut.getValue("wxOrderNo"));
			}
			out.setReturnObj(respMap);
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
		} catch (Exception e) {
			logger.error("微信被扫支付(刷卡)处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信被扫支付(刷卡)处理失败");
			out.putValue("txnSta", StringConstans.OrderState.STATE_03);
		}
		logger.info("[微信被扫支付(刷卡)]  wxMicroPay END:" + out.toString());
		return out;
	}
	
	/**
	 * 微信断直连被扫支付(刷卡)入口
	 * 
	 * @param inputParam
	 * @return
	 * @throws Exception
	 */
	public OutputParam wxMicroPayYL(InputParam input) throws FrameException {
	
		logger.info("[银联微信被扫支付]  wxMicroPay START:" + input.toString());
	
		OutputParam out = new OutputParam();
	
		try {
			String nullStr = Util.validateIsNull(YlWXValidation.vali_YlWXMicroPay, input);
			if (!StringUtil.isEmpty(nullStr)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[银联微信被扫支付] 请求报文字段[" + nullStr + "]不能为空");
				return out;
			}
	
			String orderAmount = StringUtil.toString(input.getValue(Dict.orderAmount));// 分
			String txnAmount = StringUtil.toString(new BigDecimal(orderAmount).divide(new BigDecimal(100)));
			String merSupportCredit = StringUtil.toString(input.getValue(Dict.isCredit));// 商户支持卡种
			String merId = StringUtil.toString(input.getValue(Dict.merId));
			String orderNumber = StringUtil.toString(input.getValue(Dict.orderNumber));
			String orderTime = StringUtil.toString(input.getValue(Dict.orderTime));
			String payType = StringUtil.toString(input.getValue(Dict.payType));
			String merName = StringUtil.toString(input.getValue(Dict.merName));
			String subWxMerId = StringUtil.toString(input.getValue(Dict.sub_mch_id));
			String transType = StringUtil.toString(input.getValue(Dict.transType));
			String payAccessType = StringUtil.toString(input.getValue(Dict.payAccessType));
			String merIsCredit = StringUtil.toString(input.getValue(Dict.isCredit));
			String channel = StringUtil.toString(input.getValue(Dict.channel));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rate));
	
			Map<String, Object> respMap = new HashMap<String, Object>();
			respMap.put(Dict.merId, merId);
			respMap.put(Dict.orderNumber, orderNumber);
			respMap.put(Dict.orderTime, orderTime);
			respMap.put(Dict.orderAmount, orderAmount);
			respMap.put(Dict.transType, input.getValue(Dict.transType));
			respMap.put(Dict.channel, input.getValue(Dict.channel));
			respMap.put(Dict.currencyType, input.getValue(Dict.currencyType));
			respMap.put(Dict.payAccessType, input.getValue(Dict.payAccessType));
			respMap.put(Dict.payType, input.getValue(payType));
			out.setReturnObj(respMap);
	
			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) == 0 || amount.compareTo(new BigDecimal(0)) == -1
					|| orderAmount.length() != 12) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]订单金额[orderAmount]非法：" + orderAmount);
				return out;
			}
	
			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]交易类型[transType]错误：" + transType);
				return out;
			}
	
			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
					&& !StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]支付类型[payType]非法：" + payType);
				return out;
			}
	
			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]交易接入类型[payAccessType]错误：" + payAccessType);
				return out;
			}
	
			if (!StringConstans.IsCredit.NONSUPPORT_CREDIT.equals(merIsCredit)
					& !StringConstans.IsCredit.SUPPORT_CREDIT.equals(merIsCredit)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]是否支持信用卡值[isCredit]非法：" + merIsCredit);
				return out;
			}
	
			if (orderTime.length() != 14) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]外部订单时间[orderTime]非法：" + orderTime);
				return out;
			}
	
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]渠道[channel]非法：" + channel);
				return out;
			}
	
			logger.debug("[银联微信被扫支付(刷卡)，原交易订单校验]");
			Map<String, String[]> orderParam = new HashMap<String, String[]>();
			orderParam.put(Dict.merId, new String[] { merId });
			orderParam.put(Dict.merOrderId, new String[] { orderNumber });
			orderParam.put(Dict.merOrDt, new String[] { orderTime.substring(0, 8) });
			orderParam.put(Dict.transType, new String[] { StringConstans.TransType.TRANS_CONSUME });
			List<Map<String, Object>> orderL = orderDao.queryRealOrderByMer(orderParam);
	
			if (!StringUtil.listIsEmpty(orderL)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]原订单已存在,请勿重复提交");
				return out;
			}
	
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
	
			String mch_id = configMap.get(Dict.WX_MERID);
	
			input.putParams(Dict.resDesc, "交易发起");
			input.putParams(Dict.wxMerId, mch_id);
			OutputParam orderOut = wxManager.addConsumeOrder(input);
	
			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付]" + orderOut.getReturnMsg());
				return out;
			}
	
			logger.debug("[银联微信被扫支付] 新增订单信息 成功");
	
			String txnDt = orderOut.getValue(Dict.txnDt).toString();
			String txnTm = orderOut.getValue(Dict.txnTm).toString();
			String txnSeqId = orderOut.getValue(Dict.txnSeqId).toString();
			out.putValue(Dict.txnSeqId, txnSeqId);
			out.putValue(Dict.txnTime, txnDt+txnTm);
			
			/************************ 5.组装微信被扫支付(刷卡)报文并发送请求 ******************************/
			logger.debug("[银联微信被扫支付,发送微信下单请求处理    START]");
	
			Map<String, String> data = new HashMap<String, String>();
			String authCode = input.getValue(Dict.authCode).toString();// 用户授权码
			data.put(Dict.auth_code, authCode);
			data.put(Dict.sub_mch_id, subWxMerId);
			data.put(Dict.fee_type, "CNY"); // 符合ISO 4217标准的三位字母代码，默认人民币：CNY
			data.put(Dict.total_fee, com.huateng.util.Util.transTxnAt(0, txnAmount)); // 订单总金额，单位为分，只能为整数
			String deviceInfo = StringUtil.toString(input.getValue(Dict.deviceInfo));
			data.put("device_info", deviceInfo);
	
			String productInfo = merName + "-订单编号" + orderNumber;// 商品名称
			data.put(Dict.body, productInfo);
			data.put(Dict.attach, txnDt);
			data.put(Dict.spbill_create_ip, "127.0.0.1");
			data.put(Dict.goods_tag, "");
			data.put(Dict.notify_url, Constants.getParam("tsdk.frontTransUrl")); // 接收银联异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
			/************************ 验证商户是否支持信用卡支付 ******************************/
			String isCredit = "";
			if (!StringConstans.IsCredit.SUPPORT_CREDIT.equals(merSupportCredit)) {
				// 如果支持本行卡或他行卡的信用卡，则微信支付支持信用卡
				isCredit = "no_credit";
			}
			data.put(Dict.limit_pay, isCredit);
			data.put(Dict.out_trade_no, txnSeqId);
			String dtTm = DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS);
			data.put(Dict.time_start, dtTm);
			Date expireDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(dtTm);
			data.put(Dict.time_expire, DateUtil.format(DateUtil.addMinute(expireDate, 5), DateUtil.YYYYMMDDHHMMSS));
			String url = Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.micropayUrlSuffix");
	
			logger.debug("[银联微信被扫支付]  调用微信被扫支付发送请求处理接口 开始:" + data.toString());
			Map<String, String> map = ylwxPayService.wxSend(data, cacheKey, url);
			if (!StringConstans.WxTradeStatus.SUCCESS.equals(map.get(Dict.return_code))) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[银联微信被扫支付 - 调用微信接口] 微信返回失败:" + map.get(Dict.return_msg));
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联微信被扫支付 - 调用微信接口] 微信返回失败:" + map.get(Dict.return_msg));
				return out;
			}
	
			if (!StringConstans.WxTradeStatus.SUCCESS.equals(map.get(Dict.result_code))) {
	
				String errCode = map.get(Dict.err_code);
				if (StringConstans.WxErrorCode.SYSTEM_ERROR.equals(errCode)
						|| StringConstans.WxErrorCode.USER_PAYING.equals(errCode)
						|| StringConstans.WxErrorCode.BANK_ERROR.equals(errCode)) {
					InputParam orderInput = new InputParam();
					orderInput.putParams(Dict.merId, merId);
					orderInput.putParams(Dict.txnSeqId, txnSeqId);
					orderInput.putParams(Dict.txnDt, txnDt);
					orderInput.putParams(Dict.txnTm, txnTm);
					orderInput.putParams(Dict.payType, payType);
					orderInput.putParams(Dict.sub_mch_id, subWxMerId);
					orderInput.putParams(Dict.times, "1");
					orderInput.putParams(Dict.rate, rateChannel);
					Thread.sleep(5*1000);
					orderQueryManager.microOrderQueryYL(orderInput, out);
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					return out;
				} else {
	
					InputParam failInput = new InputParam();
					failInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_03);
					failInput.putParams(Dict.txnSeqId, txnSeqId);
					failInput.putParams(Dict.txnDt, txnDt);
					failInput.putParams(Dict.txnTm, txnTm);
					failInput.putParams(Dict.payType, payType);
					failInput.putParams(Dict.resDesc, map.get(Dict.err_code_des));
	
					// 异常情况下的处理
					// 更新订单状态
					wxManager.updateConsumeOrder(failInput);
	
					out.setReturnCode(StringConstans.returnCode.FAIL);
					out.setReturnMsg("[银联微信被扫支付 - 调用微信接口] 微信返回失败:" + map.get(Dict.err_code_des));
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, "[银联微信被扫支付 - 调用微信接口] 微信返回失败:" + map.get(Dict.err_code_des));
					return out;
				}
	
			}
	
			String trade_state = map.get(Dict.trade_state);
	
			if (StringConstans.WxTradeStatus.SUCCESS.equals(trade_state)) {
				// 支付成功
	
				logger.info("[银联微信被扫支付 - 调用微信接口] 支付成功");
				String txnSta = StringConstans.OrderState.STATE_02;
				String resDesc = StringConstans.RespDesc.RESP_DESC_02;
	
				String payTime = StringUtil.toString(map.get(Dict.time_end));
				String wxOrderNo = String.format("%s", respMap.get(Dict.transaction_id));
				String payerid = StringUtil.toString(map.get(Dict.openid));
				String bankType = StringUtil.toString(map.get(Dict.bank_type));
	
				/************************ 更新订单状态 ******************************/
				logger.debug("[银联微信被扫支付- 调用微信接口] 完成被扫支付请求后更新订单状态为'支付成功'");
				InputParam updateInput = new InputParam();
				updateInput.putParams(Dict.txnSta, txnSta);
				updateInput.putParams(Dict.txnSeqId, txnSeqId);
				updateInput.putParams(Dict.txnDt, txnDt);
				updateInput.putParams(Dict.txnTm, txnTm);
				updateInput.putParams(Dict.payType, payType);
				updateInput.putParams(Dict.wxMerId, mch_id);
				updateInput.putParams(Dict.wxOrderNo, wxOrderNo);
				updateInput.putParams(Dict.resDesc, resDesc);
				updateInput.putParams(Dict.settleDate, payTime);
				updateInput.putParams(Dict.payerid, payerid);
				updateInput.putParams(Dict.bankType, bankType);
				updateInput.putParams(Dict.subWxMerId, subWxMerId);
	
				OutputParam updateOrderOut = orderService.updateWxOrderInfo(updateInput);
				if (!StringConstans.returnCode.SUCCESS.equals(updateOrderOut.getReturnCode())) {
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, updateOrderOut.getReturnMsg());
					return orderOut;
				}
				// 构建返回报文参数
				respMap.put(Dict.wxPayTime, payTime);
				respMap.put(Dict.wxOrderNo, wxOrderNo);
				respMap.put(Dict.txnSeqId, txnSeqId);
				respMap.put(Dict.txnTime, txnDt + txnTm);
				respMap.put(Dict.respCode, txnSta);
				respMap.put(Dict.respDesc, resDesc);
				out.setReturnObj(respMap);
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				out.setReturnMsg(resDesc);
			} else if (StringConstans.WxTradeStatus.USERPAYING.equals(trade_state)
					|| StringConstans.WxTradeStatus.NOTPAY.equals(trade_state)) {
				// 用户支付中
				InputParam orderInput = new InputParam();
				orderInput.putParams(Dict.merId, merId);
				orderInput.putParams(Dict.txnSeqId, txnSeqId);
				orderInput.putParams(Dict.txnDt, txnDt);
				orderInput.putParams(Dict.txnTm, txnTm);
				orderInput.putParams(Dict.payType, payType);
				orderInput.putParams(Dict.sub_mch_id, subWxMerId);
				orderInput.putParams(Dict.rate, rateChannel);
				orderInput.putParams(Dict.times, "1");
				Thread.sleep(5*1000);
				orderQueryManager.microOrderQueryYL(orderInput, out);
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				return out;
			} else {
				InputParam failInput = new InputParam();
				failInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_03);
				failInput.putParams(Dict.txnSeqId, txnSeqId);
				failInput.putParams(Dict.txnDt, txnDt);
				failInput.putParams(Dict.txnTm, txnTm);
				failInput.putParams(Dict.payType, payType);
				failInput.putParams(Dict.resDesc, "交易异常");
	
				// 异常情况下的处理
				// 更新订单状态
				wxManager.updateConsumeOrder(failInput);
	
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信被扫支付(刷卡) - 调用微信接口] 微信返回失败:" + trade_state + map.get(Dict.trade_state_des));
				out.putValue(Dict.resCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.resDesc,
						"[微信被扫支付(刷卡) - 调用微信接口] 微信返回失败:" + trade_state + map.get(Dict.trade_state_des));
				return out;
			}
	
		} catch (Exception e) {
			logger.error("微信被扫支付(刷卡)处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信被扫支付(刷卡)处理失败");
			out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
		}
		logger.info("[微信被扫支付(刷卡)]  wxMicroPay END:" + out.toString());
		return out;
	}

	/**
	 * 微信被扫支付撤销交易入口
	 */
	@Override
	public OutputParam wxMicroRevoke(InputParam input) throws FrameException {
		logger.info("[后台调用微信被扫撤销  wxMicroRevoke START]" + input.toString());
		OutputParam out = new OutputParam();
		try {
			/**************** 1.请求报文非空字段验证 ***********************/
			String nullStr = Util.validateIsNull(YlWXValidation.vali_YlWXRevoke, input);
			if (!StringUtil.isEmpty(nullStr)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易] 请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			// 获取参数
			String initOrderNumber = StringUtil.toString(input.getValue(Dict.initOrderNumber));
			String initOrderTime = StringUtil.toString(input.getValue(Dict.initOrderTime));
			String orderNumber = StringUtil.toString(input.getValue(Dict.orderNumber));
			String orderTime = StringUtil.toString(input.getValue(Dict.orderTime));
			String orderAmount = StringUtil.toString(input.getValue(Dict.orderAmount));
			String merId = StringUtil.toString(input.getValue(Dict.merId));
			String transType = StringUtil.toString(input.getValue(Dict.transType));
			String payAccessType = StringUtil.toString(input.getValue(Dict.payAccessType));
			String channel = StringUtil.toString(input.getValue(Dict.channel));

			/******************** 2.报文参数验证 *****************************/
			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(BigDecimal.ZERO) <= 0 || orderAmount.length() != 12) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易] 订单金额[" + orderAmount + "]非法");
				return out;
			}

			if (!StringConstans.TransType.TRANS_REVOKE.equals(transType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易] 交易类型[" + transType + "]错误");
				return out;
			}
			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易] 交易接入类型[" + payAccessType + "]错误");
				return out;
			}

			if (orderTime.length() != 14) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易]外部订单时间[" + orderTime + "]非法");
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信被扫支付撤销交易] 渠道[" + channel + "]非法");
				return out;
			}

			out.putValue(Dict.merId, merId);
			out.putValue(Dict.orderNumber, orderNumber);
			out.putValue(Dict.orderTime, orderTime);
			out.putValue(Dict.orderAmount, orderAmount);
			out.putValue(Dict.transType, input.getValue(Dict.transType));
			out.putValue(Dict.channel, input.getValue(Dict.channel));
			out.putValue(Dict.currencyType, input.getValue(Dict.currencyType));

			/************************ 3.验证微信原订单信息 ******************************/
			logger.info("[微信被扫撤销]验证原微信支付订单信息");
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put(Dict.merOrderId, new String[] { initOrderNumber });
			paramMap.put(Dict.merOrDt, new String[] { initOrderTime.substring(0, 8) });
			paramMap.put(Dict.merOrTm, new String[] { initOrderTime.substring(8, 14) });
			paramMap.put(Dict.merId, new String[] { merId });

			List<Map<String, Object>> wxOrderList = orderDao.queryRealOrderByMer(paramMap);
			if (wxOrderList.size() == 0) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易]微信支付原订单不存在");
				return out;
			}

			Map<String, Object> initWxOrder = wxOrderList.get(0);
			String subWxMerId = StringUtil.toString(initWxOrder.get(Dict.subWxMerId));
			String initTxnSeqId = StringUtil.toString(initWxOrder.get(Dict.txnSeqId));
			String initTxnDt = StringUtil.toString(initWxOrder.get(Dict.txnDt));
			String initTxnTm = StringUtil.toString(initWxOrder.get(Dict.txnTm));
			String initTxnTime = StringUtil.toString(initTxnDt, initTxnTm);

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subWxMerId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);

			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, outQuery.getReturnMsg());
				return out;
			}
			String connectMethod = StringUtil.toString(outQuery.getValue(Dict.connectMethod));
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			String cacheKey = "";
			if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_" + rateChannel;
			} else {
				cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
			}
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String mch_id = configMap.get(Dict.WX_MERID);

			/******************** 4.新增 订单表流水 *****************************/
			input.putParams(Dict.resDesc, "[微信被扫支付撤销交易]撤销发起");
			input.putParams(Dict.subWxMerId, subWxMerId);
			input.putParams(Dict.initTxnSeqId, initTxnSeqId);
			input.putParams(Dict.initTxnTime, initTxnTime);
			input.putParams(Dict.wxMerId, mch_id);

			OutputParam orderOut = wxManager.addRevokeOrder(input);

			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信被扫支付撤销交易]新增订单失败");
				return out;
			}
			String txnDt = StringUtil.toString(orderOut.getValue(Dict.txnDt));
			String txnTm = StringUtil.toString(orderOut.getValue(Dict.txnTm));
			String txnSeqId = StringUtil.toString(orderOut.getValue(Dict.txnSeqId));
			String initWxSta = StringUtil.toString(initWxOrder.get(Dict.txnSta));
			String wxOrderNo = StringUtil.toString(initWxOrder.get(Dict.wxOrderNo));// 微信订单号

			InputParam queryInput = new InputParam();
			queryInput.putParams(Dict.txnSeqId, initTxnSeqId);
			queryInput.putParams(Dict.sub_mch_id, subWxMerId);
			queryInput.putParams(Dict.subMchId, subWxMerId);
			queryInput.putParams(Dict.merId, merId);
			queryInput.putParams(Dict.rate, rateChannel);
			if (!StringConstans.OrderState.STATE_02.equals(initWxSta)) {
				OutputParam queryOut = null;
				if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					queryOut = queryWxOrderYL(queryInput);
				} else {
					queryOut = queryWxOrder(queryInput);
				}
				// 更新原支付订单状态
				if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
					InputParam updateInput = new InputParam();
					updateInput.putParams(Dict.txnSta, queryOut.getValue(Dict.txnSta));
					updateInput.putParams(Dict.txnSeqId, initTxnSeqId);
					updateInput.putParams(Dict.txnDt, initTxnDt);
					updateInput.putParams(Dict.txnTm, initTxnTm);
					updateInput.putParams(Dict.resDesc, queryOut.getValue(Dict.respDesc));
					OutputParam upOut = orderService.updateWxOrderInfo(updateInput);/** 调用接口 */
				} else {
					return queryOut;
				}
			}

			InputParam params = new InputParam();
			params.putParams(Dict.txnDt, txnDt);
			params.putParams(Dict.txnTm, txnTm);
			params.putParams(Dict.txnSeqId, txnSeqId);
			params.putParams(Dict.initTxnSeqId, initTxnSeqId);
			params.putParams(Dict.subMchId, subWxMerId);
			params.putParams(Dict.sub_mch_id, subWxMerId);
			params.putParams(Dict.merId, merId);
			params.putParams(Dict.mch_id, mch_id);
			params.putParams(Dict.cacheKey, cacheKey);
			if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				out = reverseOrderYL(params);
			} else {
				out = reverseOrder(params);
			}
			// 统一更新订单撤销订单状态
			InputParam resInput = new InputParam();
			resInput.putParams(Dict.txnSeqId, txnSeqId);
			resInput.putParams(Dict.txnDt, txnDt);
			resInput.putParams(Dict.txnTm, txnTm);
			resInput.putParams(Dict.txnSta, StringUtil.toString(out.getValue(Dict.respCode)));
			resInput.putParams(Dict.resDesc, StringUtil.toString(out.getValue(Dict.respDesc)));
			resInput.putParams(Dict.wxOrderNo, wxOrderNo);

			OutputParam resOut = orderService.updateWxOrderInfo(resInput);/** 调用接口 */
			// 返回参数信息
			out.putValue(Dict.txnSeqId, txnSeqId);
			out.putValue(Dict.txnTime, txnDt + txnTm);
			out.putValue(Dict.wxOrderNo, wxOrderNo);
		} catch (Exception e) {
			logger.error("[微信被扫支付撤销交易]订单发往微信撤销异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "[微信被扫支付撤销交易]微信撤销交易异常" + e.getMessage());
		} finally {
			logger.info("[微信被扫支付撤销交易] wxMicroRevoke END]" + out.toString());
		}
		return out;
	}

	/**
	 * 微信扫码关闭订单交易入口
	 */
	@Override
	public OutputParam wxCloseOrder(InputParam input) throws FrameException {

		logger.info("[微信关闭订单接口] wxCloseOrder START" + input.toString());
		OutputParam out = new OutputParam();
		try {

			/**************** 1.请求报文非空字段验证 ***********************/
			String nullStr = Util.validateIsNull(YlWXValidation.vali_YlWXClose, input);
			if (!StringUtil.isEmpty(nullStr)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信关闭订单接口] 请求报文字段[" + nullStr + "]不能为空");
				return out;
			}

			String merId = StringUtil.toString(input.getValue(Dict.merId));
			String txnSeqId = StringUtil.toString(input.getValue(Dict.initOrderNumber));
			String txnTime = StringUtil.toString(input.getValue(Dict.initOrderTime));
			String txnDt = txnTime.substring(0, 8);
			String txnTm = txnTime.substring(8, 14);
			String channel = StringUtil.toString(input.getValue(Dict.channel));
			String payAccessType = StringUtil.toString(input.getValue(Dict.payAccessType));

			/******************** 2.报文参数验证 *****************************/

			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信关闭订单接口]交易接入类型[payAccessType]错误" + payAccessType);
				return out;
			}

			if (txnTime.length() != 14) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信关闭订单接口]订单时间[txnTime]非法" + txnTime);
				return out;
			}

			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信关闭订单接口]渠道[channel]非法" + channel);
				return out;
			}

			/************************ 2.验证微信原订单信息 ******************************/
			Map<String, String[]> paramMap = new HashMap<String, String[]>();
			paramMap.put(Dict.txnSeqId, new String[] { txnSeqId });
			paramMap.put(Dict.txnDt, new String[] { txnDt });
			paramMap.put(Dict.txnTm, new String[] { txnTm });
			List<Map<String, Object>> wxOrderList = orderDao.queryRealOrderByMer(paramMap);
			if (wxOrderList.size() == 0) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_05);
				out.putValue(Dict.respDesc, "[微信关闭订单接口]微信支付原订单不存在");
				return out;
			}

			/**** 判断交易是否成功，如果成功，则不去关闭 start ******/
			String txnSta = String.format("%s", wxOrderList.get(0).get(Dict.txnSta));

			if (StringConstans.RespCode.RESP_CODE_02.equals(txnSta)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_07);
				out.putValue(Dict.respDesc, "[微信关闭订单接口]订单已支付，不能发起关闭订单");
				return out;
			}

			/************************ 3.调用关闭微信订单接口 ******************************/
			InputParam params = new InputParam();
			params.putParams(Dict.merId, merId);
			params.putParams(Dict.txnSeqId, txnSeqId);
			params.putParams(Dict.subMchId, wxOrderList.get(0).get(Dict.subWxMerId));

			OutputParam routing = wxMerchantSynchService.routing(params);
			String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));

			if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				// 间连
				out = closeOrderYL(params);
			} else {
				// 直连
				out = closeOrder(params);
			}

			String respCode = StringUtil.toString(out.getValue(Dict.respCode));
			String respDesc = StringUtil.toString(out.getValue(Dict.respDesc));
			String orderSta = StringUtil.toString(out.getValue(Dict.orderSta));
			/************************ 4.更新原支付订单信息 ******************************/
			if (StringConstans.OrderState.STATE_09.equals(orderSta)) {
				InputParam resInput = new InputParam();
				resInput.putParams(Dict.txnSta, out.getValue(Dict.orderSta));
				resInput.putParams(Dict.txnSeqId, txnSeqId);
				resInput.putParams(Dict.txnDt, txnDt);
				resInput.putParams(Dict.txnTm, txnTm);
				resInput.putParams(Dict.resCode, respCode);
				resInput.putParams(Dict.resDesc, respDesc);

				OutputParam resOut = orderService.updateWxOrderInfo(resInput);/** 调用接口 */

				out.putValue(Dict.respCode, respCode);
				out.putValue(Dict.respDesc, respDesc);
			}
		} catch (Exception e) {
			logger.error("订单发往微信关闭订单异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "微信关闭订单交易异常");
			logger.info("------------------调用微信关闭订单接口出现异常   END-----------------");
		} finally {
			logger.info("[微信关闭订单接口] wxCloseOrder END" + out.toString());
		}
		return out;
	}

	/**
	 * 获取微信授权信息
	 */
	@Override
	public OutputParam getWxAuthCode(InputParam input) throws FrameException {

		logger.info("------------------调用微信获取AccessToken接口 START-----------------");

		OutputParam outputParam = new OutputParam();

		try {

			/**************** 1.请求报文非空字段验证 ***********************/

			List<String> list = new ArrayList<String>();
			list.add("grantType");
			list.add("code");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("[微信获取AccessToken接口] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			logger.info("[微信获取AccessToken接口] 去微信后台获取授权信息   开始");

			OutputParam authOutput = wxManager.getWxAuthCode(input);

			logger.info("[微信获取AccessToken接口] 去微信后台获取授权信息   结束");

			if (!StringConstans.returnCode.SUCCESS.equals(authOutput.getReturnCode())) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", authOutput.getReturnMsg());
				return outputParam;
			}

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "获取openId信息成功");
			outputParam.putValue("openId", authOutput.getValue("openId"));

			logger.info("------------------调用微信获取AccessToken接口  END-----------------");

		} catch (Exception e) {
			logger.error("微信获取AccessToken接口异常：" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "微信获取AccessToken接口异常");
		}

		return outputParam;
	}

	/**
	 * 微信支付 统一下单接口处理
	 */
	@Override
	public OutputParam unifiedOrder(InputParam input) throws FrameException {

		logger.info("[组织报文调用统一下单接口]   START:" + input.toString());

		OutputParam out = new OutputParam();

		try {
			logger.debug("微信统一下单组报文发送请求   start");
			String orderAmount = input.getValue("orderAmount").toString();
			String amount = new BigDecimal(orderAmount).divide(new BigDecimal(100)).toString();
			String merId = input.getValue("merId").toString();
			String txnSeqId = input.getValue("txnSeqId").toString();
			String txnDt = input.getValue("txnDt").toString();
			String txnTm = input.getValue("txnTm").toString();
			String payType = input.getValue("payType").toString();
			String deviceInfo = StringUtil.isNull(input.getValue("deviceInfo")) ? ""
					: input.getValue("deviceInfo").toString();
			String customerIp = StringUtil.isNull(input.getValue("customerIp")) ? "127.0.0.1"
					: input.getValue("customerIp").toString();
			String productId = StringUtil.isNull(input.getValue("productId")) ? ""
					: input.getValue("productId").toString();
			String merSupportCredit = input.getValue("isCredit").toString();// 商户是否支持信用卡
			String merName = input.getValue("merName").toString();// 商户名称
			String outOrderNo = input.getValue("orderNumber").toString();// 商户订单号
			String subMchId = input.getValue("subWxMerId").toString();// 微信子商户号

			String appid = input.getValue("appid").toString();
			String mch_id = input.getValue("mch_id").toString();
			String pfxPath = input.getValue("pfxPath").toString();
			String pwd = input.getValue("pwd").toString();
			String key = input.getValue("key").toString();

			// 设置商品描述信息，微信的body字段
			String productInfo = merName + "-订单编号" + outOrderNo;

			// 验证商户是否支持信用卡支付，来设置isCredit字段
			String isCredit = "";
			if (!"1".equals(merSupportCredit)) {
				// 如果支持本行卡或他行卡的信用卡，则微信支付支持信用卡
				isCredit = "no_credit";
			}

			// 验证订单是否已支付成功
			String wxTimeStamp = String.valueOf(System.currentTimeMillis() / 1000);

			// 去微信下单
			logger.debug("[微信下单支付] 组装微信下单报文发起下单请求");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("device_info", deviceInfo);
			dataMap.put("nonce_str", Util.getRandomStringByLength(32));
			dataMap.put("body", productInfo);
			dataMap.put("attach", txnDt);
			dataMap.put("out_trade_no", txnSeqId);
			dataMap.put("total_fee", com.huateng.util.Util.transTxnAt(0, amount));
			dataMap.put("spbill_create_ip", customerIp);
			String dtTm = DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS);
			Date expireDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(dtTm);
			dataMap.put("time_start", dtTm);
			dataMap.put("time_expire", DateUtil.format(DateUtil.addMinute(expireDate, 5), DateUtil.YYYYMMDDHHMMSS));
			dataMap.put("goods_tag", "");
			dataMap.put("notify_url", Constants.getParam("resevWxNotifyUrl"));
			dataMap.put("limitPay", isCredit);

			// 不同的支付方式，组装不同的报文
			if (StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				dataMap.put("appid", appid);
				dataMap.put("mch_id", mch_id);
				dataMap.put("product_id", productId);
				dataMap.put("trade_type", StringConstans.WeiXinTransType.NATIVE);
				dataMap.put("subMchId", subMchId);
				dataMap.put("pfxPath", pfxPath);
				dataMap.put("pwd", pwd);
			}

			// 向微信发送下单请求并处理返回报文
			InputParam wxInput = new InputParam();
			wxInput.putParams("dataMap", dataMap);
			wxInput.putParams("key", key);
			wxInput.putParams("wxTimeStamp", wxTimeStamp);
			wxInput.putParams("payType", payType);
			wxInput.putParams("txnDt", txnDt);
			wxInput.putParams("txnTm", txnTm);
			wxInput.putParams("txnSeqId", txnSeqId);

			/************************ 组装微信统一下单报文并发送请求 ******************************/
			OutputParam wxOut = wxManager.toWxUnifiedOrder(wxInput);
			if (!StringConstans.returnCode.SUCCESS.equals(wxOut.getReturnCode())) {
				InputParam updateInput = new InputParam();
				updateInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
				updateInput.putParams("txnSeqId", txnSeqId);
				updateInput.putParams("txnDt", txnDt);
				updateInput.putParams("txnTm", txnTm);
				updateInput.putParams("payType", payType);
				updateInput.putParams("subWxMerId", subMchId);
				updateInput.putParams("resDesc", "微信下单失败");
				OutputParam orderOut = orderService.updateWxOrderInfo(updateInput);
				return wxOut;
			}
			out.setReturnObj(wxOut.getReturnObj());

			logger.debug(" 组织报文调用统一下单接口     下单成功");

			out.setReturnMsg("微信支付统一下单成功");
			out.setReturnCode(StringConstans.returnCode.SUCCESS);

			logger.debug("-----------  组织报文调用统一下单接口   END-----------------");

		} catch (Exception e) {
			logger.error("微信支付统一下单处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信支付统一下单处理失败");
		}
		return out;
	}

	/**
	 * 微信退款
	 */
	@Override
	public OutputParam refundOrder(InputParam input) throws FrameException {
		logger.info("[微信退款]开始后台向微信申请订单退款  START");
		OutputParam out = new OutputParam();
		try {
			String initTxnSeqId = input.getValue("initTxnSeqId").toString();
			String txnSeqId = input.getValue("txnSeqId").toString();
			String wxOrderNo = StringUtil.isNull(input.getValue("wxOrderNo")) ? ""
					: input.getValue("wxOrderNo").toString();
			String totalFee = input.getValue("orderAmount").toString();
			String refundFee = input.getValue("addMoney").toString();
			String payType = input.getValue("payType").toString();
			String txnDt = input.getValue("txnDt").toString();
			String txnTm = input.getValue("txnTm").toString();
			String deviceInfo = StringUtil.isNull(input.getValue("deviceInfo")) ? ""
					: input.getValue("deviceInfo").toString();
			String wxMerId = input.getValue("wxMerId").toString();
			String subMchId = StringUtil.isNull(input.getValue("subMchId")) ? ""
					: input.getValue("subMchId").toString();

			/************************ 组装微信申请退款报文并发送请求 ******************************/
			logger.info("组装微信退款申请的请求报文");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("transactionID", wxOrderNo);
			dataMap.put("outTradeNo", initTxnSeqId);
			dataMap.put("totalFee", totalFee);
			dataMap.put("refundFee", refundFee);
			dataMap.put("deviceInfo", deviceInfo);
			dataMap.put("outRefundNo", txnSeqId);

			/**********************
			 * 判断上送的商户是何种商户(native/app)
			 *******************************/
			boolean isApp = false;
			if (Constants.getParam("app_merId").equals(wxMerId)) {
				isApp = true;// 为app支付商户
			}

			// 获取微信商户号的key
			String keyPath = MappingUtils.getString("SYSPARAM", "netwaypfx");
			String keyPwd = MappingUtils.getString("SYSPARAM", "netwaypwd");
			// app
			String appKey = Util.getWxParam(keyPath, keyPwd, MappingUtils.getString("SYSPARAM", "appKey")).trim();
			// native
			String nativeKey = Util.getWxParam(keyPath, keyPwd, MappingUtils.getString("SYSPARAM", "nativeKey")).trim();
			String key = "";// 商户的key
			if (StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
					|| StringConstans.Pay_Type.PAY_TYPE_WEIXIN_JSAPI.equals(payType)
					|| StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType)) {
				// 扫码支付
				dataMap.put("appid", Constants.getParam("jg_appId"));
				dataMap.put("mch_id", Constants.getParam("jg_merId"));
				key = nativeKey;
				// 获取微信子商户
				dataMap.put("subMchId", subMchId);
			} else if (StringConstans.Pay_Type.PAY_TYPE_WEIXIN_APP.equals(payType)) {
				// app支付
				dataMap.put("appid", Constants.getParam("app_appId"));
				dataMap.put("mch_id", Constants.getParam("app_merId"));
				key = appKey;
			}

			// 向微信发送微信退款请求并处理返回报文
			InputParam wxInput = new InputParam();
			wxInput.putParams("dataMap", dataMap);
			wxInput.putParams("key", key);
			wxInput.putParams("txnDt", txnDt);
			wxInput.putParams("txnTm", txnTm);
			wxInput.putParams("txnSeqId", txnSeqId);
			wxInput.putParams("isApp", isApp);
			wxInput.putParams("subWxMerId", subMchId);
			OutputParam wxOut = wxManager.toWxRefundOrder(wxInput);
			if (!StringConstans.returnCode.SUCCESS.equals(wxOut.getReturnCode())) {
				logger.error("[微信退款] 发送微信退款请求处理失败");
				return wxOut;
			}

			logger.info("[微信退款]开始后台向微信申请订单退款  END");
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			return out;
		} catch (Exception e) {
			logger.error("微信退款处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信退款处理失败");
			return out;
		}
	}
	
	/**
	 * 微信退款
	 */
	@Override
	public OutputParam refundOrderYL(InputParam input) throws FrameException {
		logger.info("[银联微信退款交易]退款交易流程START" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String initTxnSta = StringUtil.toString(input.getValue(Dict.initTxnSta));
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			String subMerchant = StringUtil.toString(input.getValue(Dict.subMerchant)); 
			
			if (StringConstans.OrderState.STATE_04.equals(initTxnSta)
					|| StringConstans.OrderState.STATE_06.equals(initTxnSta)
					|| StringConstans.OrderState.STATE_10.equals(initTxnSta)
					|| StringConstans.OrderState.STATE_01.equals(initTxnSta)) {
				logger.info("原订单订单状态未明，先明确状态后再处理");
				
				InputParam queryInput = new InputParam();
				queryInput.putParams(Dict.txnSeqId, initTxnSeqId);
				queryInput.putParams(Dict.rate, rateChannel);
				queryInput.putParams(Dict.sub_mch_id, subMerchant);
				
				OutputParam queryOut =this.queryWxOrderYL(queryInput);
				
				String orderSta = StringUtil.toString(queryOut.getValue(Dict.txnSta));
				String orderDesc = StringUtil.toString(queryOut.getValue(Dict.respDesc));
				
				if(!StringConstans.OrderState.STATE_02.equals(orderSta)) {
					logger.info("原订单交易状态:"+orderDesc+",不允许退款(或稍后再重试退款)");
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "原订单交易状态:"+orderDesc+",不允许退款(或稍后再重试退款)");
					return outputParam;
				}else{
					logger.info("原订单交易成功，允许退款");
					
					/**去银联微信退款start*/
					OutputParam refundOutput = wxManager.toWxRefundOrderYL(input);
					/**去银联微信退款end*/
					
					String refundStatus = StringUtil.toString(refundOutput.getValue(Dict.refundStatus));
					String msg = StringUtil.toString(refundOutput.getValue(Dict.msg));
					
					/**更新原订单start*/
					InputParam updateInput = new InputParam();
					updateInput.putparamString(Dict.txnSeqId, initTxnSeqId);
					updateInput.putparamString(Dict.txnSta, orderSta);
					updateInput.putparamString(Dict.resDesc, orderDesc);
					orderService.updateOrderState(updateInput);
					/**更新原订单end*/
					
					outputParam.putValue(Dict.refundStatus, refundStatus);
					outputParam.putValue(Dict.msg, msg);
				} 
				
			} else if (StringConstans.OrderState.STATE_02.equals(initTxnSta)) {
				logger.info("原订单交易成功，直接进行退款");
				/**去银联微信退款start*/
				OutputParam refundOutput = wxManager.toWxRefundOrderYL(input);
				/**去银联微信退款end*/
				
				String refundStatus = StringUtil.toString(refundOutput.getValue(Dict.refundStatus));
				String msg = StringUtil.toString(refundOutput.getValue(Dict.msg));
				
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
	 * 微信退款查询
	 */
	@Override
	public OutputParam queryRefundOrder(InputParam input) throws FrameException {
		logger.info("[微信退款查询]开始后台向微信查询退款订单状态  START");
		OutputParam out = new OutputParam();
		try {
			String wxRefundNo = StringUtil.isNull(input.getValue("wxRefundNo")) ? ""
					: input.getValue("wxRefundNo").toString();
			String txnSeqId = StringUtil.isNull(input.getValue("txnSeqId")) ? ""
					: input.getValue("txnSeqId").toString();
			// String payType = input.getValue("payType").toString();
			// String wxMerId = StringUtil.isNull(input.getValue("wxMerId")) ? "" :
			// input.getValue("wxMerId").toString();
			String subMchId = StringUtil.isNull(input.getValue("subMchId")) ? ""
					: input.getValue("subMchId").toString();

			/************************ 组装微信退款查询报文并发送请求 ******************************/
			logger.info("组装微信退款查询的请求报文");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("wxRefundNo", wxRefundNo);
			dataMap.put("transactionID", "");// 微信订单号查询出来的结果，若是多次退货，则会有变量下标，故不用
			dataMap.put("outRefundNo", txnSeqId);
			dataMap.put("outTradeNo", "");// 原订单号查询出来的结果，若是多次退货，则会有变量下标，故不用
			dataMap.put("deviceInfo", "");
			dataMap.put("appid", Constants.getParam("jg_appId"));
			dataMap.put("mch_id", Constants.getParam("jg_merId"));
			dataMap.put("subMchId", subMchId);
			// 获取微信商户号的key
			String key = Constants.getParam("jg_key");

			// 组装退款查询报文对象
			RefundQueryReqData refundQueryReqData = new RefundQueryReqData(dataMap, key);

			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_refundQuery_url"));
			// requestMap.put(Dict.pfxPath, pfxPath);
			// requestMap.put(Dict.pfxPwd, pwd);

			// 发送退款查询请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, refundQueryReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);
			logger.info("[微信退款查询]查询退款请求返回的响应报文：" + respString);

			String returnCode = respMap.get("return_code").toString();
			String returnMsg = respMap.get("return_msg").toString();
			if (!"SUCCESS".equals(returnCode)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信退款查询失败:" + returnMsg);
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				logger.error("[微信退款查询]接收的报文签名验证不通过");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("报文签名验证不通过");
				return out;
			}

			String resultCode = respMap.get("result_code").toString();
			if (!"SUCCESS".equals(resultCode)) {
				if ("REFUNDNOTEXIST".equals(respMap.get("err_code").toString())) {
					// 可能是微信有延迟没有查到,状态改为06等稍后定时查询
					out.putValue("txnSta", StringConstans.OrderState.STATE_06);
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					out.setReturnMsg("微信退款可能正在处理中");
				} else {
					logger.error("微信退款查询失败:" + respMap.get("err_code_des").toString());
					out.putValue("txnSta", StringConstans.OrderState.STATE_03);
					out.setReturnCode(StringConstans.returnCode.FAIL);
					out.setReturnMsg("微信退款处理失败");
				}
			} else {
				String refundStatus = respMap.get("refund_status_0").toString();
				if ("SUCCESS".equals(refundStatus)) {
					out.putValue("txnSta", StringConstans.OrderState.STATE_02);
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					out.setReturnMsg("微信退款成功");
				} else if ("FAIL".equals(refundStatus)) {
					out.putValue("txnSta", StringConstans.OrderState.STATE_03);
					out.setReturnCode(StringConstans.returnCode.FAIL);
					out.setReturnMsg("微信退款失败");
				} else if ("PROCESSING".equals(refundStatus)) {
					out.putValue("txnSta", StringConstans.OrderState.STATE_08);
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					out.setReturnMsg("微信退款已处理");
				} else {
					out.putValue("txnSta", StringConstans.OrderState.STATE_06);
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					out.setReturnMsg("微信退款处理中");
				}
			}

			logger.info("[微信退款查询]开始后台向微信查询退款订单状态  END");
			return out;
		} catch (Exception e) {
			logger.error("微信退款查询处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信退款查询处理失败");
			return out;
		}
	}
	
	/**
	 * 银联微信退款查询
	 */
	@Override
	public OutputParam queryRefundOrderYL(InputParam input) throws FrameException {
		return wxManager.toWxRefundQueryYL(input);
	}

	/**
	 * 微信支付接收后台通知处理
	 */
	@Override
	public OutputParam recvWxNotifyReq(InputParam input) throws FrameException {
		logger.info("paywebserver 开始进行微信后台通知处理");
		OutputParam out = new OutputParam();
		try {
			/************************ 解析微信后台通知报文 ******************************/
			logger.debug("[微信支付后台通知]解析处理微信后台通知报文信息");
			// 解析后台通知报文
			String respStr = input.getValue("respStr").toString();
			Map<String, Object> respMap = Util.getMapFromXML(respStr);

			String return_code = respMap.get("return_code").toString();
			if (!"SUCCESS".equals(return_code)) {
				logger.debug("接收微信支付返回的后台通知失败");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				return out;
			}

			// 验证原订单信息
			String outTradeNo = respMap.get("out_trade_no").toString();// 网关订单号
			logger.debug("[微信支付后台通知] 微信返回outTradeNo=" + outTradeNo);

			Pattern p = Pattern.compile("[a-zA-Z]*");
			Matcher m = p.matcher(outTradeNo);
			String prefix = m.find() ? m.group() : "";
			logger.debug("[微信支付后台通知] outTradeNo前缀prefix=" + prefix);

			outTradeNo = outTradeNo.substring(prefix.length(), outTradeNo.length());
			logger.debug("[微信支付后台通知] 处理后的outTradeNo=" + outTradeNo);

			// 二维码交易流水号
			String txnSeqId = String.format("%s%s", prefix, outTradeNo.substring(0, 10));
			logger.debug("[微信支付后台通知] txnSeqId=" + txnSeqId);

			// 二维码交易日期
			String txnDt = respMap.get("attach").toString();// 网关订单日期
			logger.debug("[微信支付后台通知] txnDt=" + txnDt);

			txnDt = StringUtil.isEmpty(prefix) ? txnDt : txnDt.substring(0, 8);
			logger.debug("[微信支付后台通知] 处理后的txnDt=" + txnDt);

			Map<String, String> params = new HashMap<String, String>();
			params.put("txnSeqId", txnSeqId);
			params.put("txnDt", txnDt);
			List<Map<String, Object>> wxOrderList = orderDao.queryOrder(params);
			if (wxOrderList.size() == 0) {
				logger.debug("[微信支付后台通知]微信支付原订单不存在");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信支付原订单不存在");
				return out;
			}

			Map<String, Object> order = wxOrderList.get(0);
			String merId = order.get("merId").toString();
			String subWxMerId = order.get("subWxMerId").toString();
			String channel = order.get("txnChannel").toString();

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");

			// 获取微信商户号的key
			String key = "";

			if (StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				InputParam inputQuery = new InputParam();
				inputQuery.putparamString(Dict.merId, merId);
				inputQuery.putparamString(Dict.subMerchant, subWxMerId);
				inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
				OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
				logger.info("查询[子商户渠道费率信息关联表]返回信息:" + outQuery.toString());
				if (StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
					String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));

					String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
					Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);

					key = configMap.get(Dict.WX_KEY);
				}
			} else if (StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				key = Constants.getParam("jg_key");
			}

			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				logger.debug("[微信支付后台通知]接收的报文签名验证不通过");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("报文签名验证不通过");
				return out;
			}

			// 处理订单状态
			InputParam backInput = new InputParam();
			respMap.put("txnSeqId", txnSeqId);
			respMap.put("txnDt", txnDt);
			backInput.putMap(respMap);
			logger.debug("微信处理订单状态,请求报文:" + backInput.toString());
			OutputParam backOut = wxManager.toWxRecivBack(backInput);
			logger.debug("微信处理订单状态,返回报文:" + backOut.toString());
			if (!StringConstans.returnCode.SUCCESS.equals(backOut.getReturnCode())) {
				logger.debug("[微信后台通知] 微信后台通知订单处理失败");
				return backOut;
			}
			// add by ghl at 20171211
			// backInput.putParams("txnSeqId", backOut.getValue("txnSeqId").toString());
			// backInput.putParams("txnDt", backOut.getValue("txnDt").toString());
			if (Configs.getConfigs().getBoolean("is_to_core_synchronous")) {
				backInput.putParams("wxOrder", backOut.getValue("wxOrder"));
				backInput.putParams("txnSta", backOut.getValue("txnSta"));
				this.toCoreForSettle(backInput);
			}
			// add end at 20171211:15:07
			// 接收微信后台通知成功并处理返回成功
			UnifiedOrderResData resData = new UnifiedOrderResData();
			resData.setReturn_code("SUCCESS");
			resData.setReturn_msg("OK");
			// 解决XStream对出现双下划线的bug
			XStream xStreamForRequestPostData = new XStream(
					new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
			// 将要提交给API的数据对象转换成XML格式数据Post给API
			String postDataXML = xStreamForRequestPostData.toXML(resData);
			logger.info("返回微信报文:" + postDataXML);
			out.putValue("respData", postDataXML);
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
		} catch (Exception e) {
			logger.error("接收微信后台通知处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信后台通知处理异常");

		} finally {
			if (Configs.getConfigs().getBoolean("is_push"))
				this.notfiyThreadHandler(input);
			logger.info("开始进行微信后台通知处理,返回报文[end]:" + out.toString());
		}

		return out;
	}

	/**
	 * 微信支付接收后台通知处理
	 */
	@Override
	public OutputParam recvWxNotifyReqYL(InputParam input) throws FrameException {
		logger.info("paywebserver 开始进行银联微信后台通知处理" + input.toString());
		OutputParam out = new OutputParam();
		try {
			/************************ 解析微信后台通知报文 ******************************/
			logger.debug("[微信支付后台通知]解析处理微信后台通知报文信息");
			// 解析后台通知报文
			String respStr = input.getValue("respStr").toString();
			Map<String, String> respData = WXPayUtil.xmlToMap(respStr);
			String RETURN_CODE = "return_code";
			String return_code;
			if (respData.containsKey(RETURN_CODE)) {
				return_code = respData.get(RETURN_CODE);
			} else {
				logger.debug("接收微信支付返回的后台通知失败 no return_code");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				return out;
			}

			if (!WXPayConstants.SUCCESS.equals(return_code)) {
				logger.debug("接收微信支付返回的后台通知失败");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				return out;
			}

			String appid = respData.get("appid");
			if (StringUtil.isEmpty(appid)) {
				logger.debug("接收微信支付返回的后台通知失败 appid is null");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				return out;
			}
			Map<String, String> config = MappingUtils.getChannelConfigByAppid(appid);
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_" + config.get(Dict.RATE);

			if (!ylwxPayService.isResponseSignatureValid(respData, cacheKey)) {
				logger.debug("[微信支付后台通知]接收的报文签名验证不通过");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("报文签名验证不通过");
				return out;
			}

			// 验证原订单信息
			String outTradeNo = StringUtil.toString(respData.get("out_trade_no"));// 网关订单号
			logger.debug("[微信支付后台通知] 微信返回outTradeNo=" + outTradeNo);

			Pattern p = Pattern.compile("[a-zA-Z]*");
			Matcher m = p.matcher(outTradeNo);
			String prefix = m.find() ? m.group() : "";
			logger.debug("[微信支付后台通知] outTradeNo前缀prefix=" + prefix);

			outTradeNo = outTradeNo.substring(prefix.length(), outTradeNo.length());
			logger.debug("[微信支付后台通知] 处理后的outTradeNo=" + outTradeNo);

			// 二维码交易流水号
			String txnSeqId = String.format("%s%s", prefix, outTradeNo.substring(0, 10));
			logger.debug("[微信支付后台通知] txnSeqId=" + txnSeqId);

			// 二维码交易日期
			String txnDt = StringUtil.toString(respData.get("attach"));// 网关订单日期
			logger.debug("[微信支付后台通知] txnDt=" + txnDt);

			txnDt = StringUtil.isEmpty(prefix) ? txnDt : txnDt.substring(0, 8);
			logger.debug("[微信支付后台通知] 处理后的txnDt=" + txnDt);

			Map<String, String> params = new HashMap<String, String>();
			params.put("txnSeqId", txnSeqId);
			params.put("txnDt", txnDt);
			List<Map<String, Object>> wxOrderList = orderDao.queryOrder(params);
			if (wxOrderList.size() == 0) {
				logger.debug("[微信支付后台通知]微信支付原订单不存在");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信支付原订单不存在");
				return out;
			}

			// 处理订单状态
			InputParam backInput = new InputParam();
			respData.put("txnSeqId", txnSeqId);
			respData.put("txnDt", txnDt);
			backInput.putMapString(respData);
			logger.debug("微信处理订单状态,请求报文:" + backInput.toString());
			OutputParam backOut = wxManager.toWxRecivBackYL(backInput);
			logger.debug("微信处理订单状态,返回报文:" + backOut.toString());
			if (!StringConstans.returnCode.SUCCESS.equals(backOut.getReturnCode())) {
				logger.debug("[微信后台通知] 微信后台通知订单处理失败");
				return backOut;
			}
			// add by ghl at 20171211
			// backInput.putParams("txnSeqId", backOut.getValue("txnSeqId").toString());
			// backInput.putParams("txnDt", backOut.getValue("txnDt").toString());
			if (Configs.getConfigs().getBoolean("is_to_core_synchronous")) {
				backInput.putParams("wxOrder", backOut.getValue("wxOrder"));
				backInput.putParams("txnSta", backOut.getValue("txnSta"));
				this.toCoreForSettle(backInput);
			}
			// add end at 20171211:15:07
			// 接收微信后台通知成功并处理返回成功
			UnifiedOrderResData resData = new UnifiedOrderResData();
			resData.setReturn_code("SUCCESS");
			resData.setReturn_msg("OK");
			// 解决XStream对出现双下划线的bug
			XStream xStreamForRequestPostData = new XStream(
					new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
			// 将要提交给API的数据对象转换成XML格式数据Post给API
			String postDataXML = xStreamForRequestPostData.toXML(resData);
			logger.info("返回微信报文:" + postDataXML);
			out.putValue("respData", postDataXML);
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
		} catch (Exception e) {
			logger.error("接收微信后台通知处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信后台通知处理异常");

		} finally {
			if (Configs.getConfigs().getBoolean("is_push"))
				this.notfiyThreadHandler(input);
			logger.info("开始进行微信后台通知处理,返回报文[end]:" + out.toString());
		}

		return out;
	}

	/**
	 * 下载微信对账单
	 */
	@Override
	public OutputParam downloadWxBill(InputParam input) throws FrameException {
		logger.info("[下载对账单]开始进行下载微信对账单处理   START,请求报文:" + input.toString());
		OutputParam out = new OutputParam();
		try {
			String billDate = StringUtil.toString(input.getValue("billDate"));
			String billType = StringUtil.isEmpty(input.getValue("billType")) ? "ALL"
					: input.getValue("billType").toString();

			if (!DateUtils.isDate(billDate)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信对账单日期" + billDate + "下载失败:日期格式不正确");
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				out.putValue("respDesc", "微信对账单日期" + billDate + "下载失败:日期格式不正确");
				logger.debug("微信对账单日期" + billDate + "下载失败:日期格式不正确");
				return out;
			}

			/************************ 组装微信下载对账单报文并发送请求 ******************************/
			logger.debug("[下载对账单]组装微信下载对账单的请求报文");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("billDate", billDate);
			dataMap.put("billType", billType);
			dataMap.put("deviceInfo", "");
			// 是否汇总 默认是
			boolean isSum = true;

			Map<String, Object[]> map = MappingContext.getInstance().get(StringConstans.MappingConfig.CHANNEL_CONFIG);
			Set<String> set = map.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String key1 = it.next();
				Object[] obj = map.get(key1);

				String CHANNEL = StringUtil.toString(obj[0]);
				if (!StringConstans.PAY_ChANNEL.WX.equals(CHANNEL)) {
					continue;
				}
				String rate = StringUtil.toString(obj[1]);
				InputParam singleInput = new InputParam();
				singleInput.putParams("billDate", billDate);
				singleInput.putParams("billType", billType);
				singleInput.putParams("rate", rate);
				OutputParam singleout = downloadSingleWxBill(singleInput);
				out.setReturnMsg(out.getReturnMsg() + singleout.getReturnMsg());
				out.putValue("respDesc",
						StringUtil.toString(out.getValue("respDesc"), "") + singleout.getValue("respDesc"));
				if (isSum && StringConstans.returnCode.FAIL.equals(singleout.getReturnCode())) {
					isSum = false;
				}
			}
			if (isSum) {
				boolean flag = FileUtil.sumWxBill(billDate);
				if (!flag) {
					logger.error("微信对账单合成新的对账文件失败");
					out.setReturnCode(StringConstans.returnCode.FAIL);
					out.setReturnMsg("微信对账单合成新的对账文件失败");
					out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					out.putValue("respDesc", "微信对账单合成失败");
					return out;
				}
			} else {
				logger.debug("微信对账单日期" + billDate + "下载失败");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信对账单日期" + billDate + "下载失败");
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return out;
			}
			logger.debug("[下载对账单]开始进行下载微信对账单处理   END");
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			out.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			out.putValue("respDesc", "微信对账单下载成功");
		} catch (Exception e) {
			logger.error("下载对账单处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("下载对账单处理异常：" + e.getMessage());
		}
		logger.info("[下载对账单]开始进行下载微信对账单处理   END,请求报文:" + out.toString());
		return out;
	}

	/**
	 * 微信下载单个费率对账单
	 * 
	 * @param billDate
	 * @param rate
	 * @return
	 */
	public OutputParam downloadSingleWxBill(InputParam input) {
		logger.info("[微信下载单个费率对账单] downloadSingleWxBill START" + input.toString());
		OutputParam out = new OutputParam();
		String rate = StringUtil.toString(input.getValue("rate"));
		try {
			String billDate = StringUtil.toString(input.getValue("billDate"));
			if (!DateUtils.isDate(billDate)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信对账单日期" + billDate + "费率" + rate + "下载失败:日期格式不正确");
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				out.putValue("respDesc", "微信对账单日期" + billDate + "费率" + rate + "下载失败:日期格式不正确");
				logger.info("微信对账单日期" + billDate + "费率" + rate + "下载失败:日期格式不正确");
				return out;
			}
			String billType = StringUtil.isEmpty(input.getValue("billType")) ? "ALL"
					: input.getValue("billType").toString();
			String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rate;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			if (configMap == null) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信对账单费率" + rate + "下载失败:查无此费率");
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				out.putValue("respDesc", "微信对账单费率" + rate + "下载失败：查无此费率");
				logger.info("微信对账单费率" + rate + "下载失败:查无此费率");
				return out;
			}
			// 默认失败
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信对账单费率" + rate + "下载失败");
			out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			out.putValue("respDesc", "微信对账单费率" + rate + "下载失败 ");

			String wxFileName = billDate + "_" + rate + "_2PAYCFT";
			String downLoadPath = Constants.getParam("wx_downloadBill_path");
			// 获取微信商户号的key
			String key = configMap.get(Dict.WX_KEY);
			/************************ 扫码 ******************************/
			logger.info("[下载对账单]发送微信下载对账单(扫码)请求");
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("billDate", billDate);
			dataMap.put("billType", billType);
			dataMap.put("deviceInfo", "");
			dataMap.put("appid", configMap.get(Dict.WX_APPID));
			dataMap.put("mch_id", configMap.get(Dict.WX_MERID));
			DownloadBillReqData downloadBillReqData1 = new DownloadBillReqData(dataMap, key);

			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_downloadBill_url"));
			requestMap.put(Dict.pfxPath, configMap.get(Dict.WX_PFX_PATH));
			requestMap.put(Dict.pfxPwd, configMap.get(Dict.WX_PWD));

			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, downloadBillReqData1);
			Map<String, Object> respMap = null;
			if (respString.contains("<xml>")) {
				respMap = Util.getMapFromXML(respString);
				// logger.info("[下载对账单]下载对账单(扫码)请求返回的响应报文："+respString1);
				String returnCode = respMap.get("return_code").toString();
				String returnMsg = respMap.get("return_msg").toString();
				if (!"SUCCESS".equals(returnCode)) {
					if ("No Bill Exist".equalsIgnoreCase(returnMsg)) {
						logger.info("[" + billDate + "]_" + rate + "_未发生微信交易,对账单不存在");
						out.setReturnMsg("微信对账单费率" + rate + "不存在:" + returnMsg);
						File file = new File(downLoadPath + wxFileName);
						respString = "0,0,0,0";
						FileUtil.writeToFile(respString, file);
						out.setReturnCode(StringConstans.returnCode.SUCCESS);
						out.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
						out.putValue("respDesc", "微信对账单费率" + rate + "不存在,空文件生成成功");
						return out;
					} else {
						out.setReturnCode(StringConstans.returnCode.FAIL);
						out.setReturnMsg("微信对账单费率" + rate + "下载失败:" + returnMsg);
						out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
						out.putValue("respDesc", "微信对账单费率" + rate + "下载失败：" + returnMsg);
						logger.info("微信对账单费率" + rate + "下载失败:" + returnMsg);
						return out;
					}
				}
			} else {
				logger.info("解析微信对账单费率" + rate);
				String fileName = "wx_bill_jg_" + billDate + "_" + rate;
				File file = new File(downLoadPath + fileName);
				FileUtil.writeToFile(respString, file);
				File wxFile = new File(downLoadPath + wxFileName);
				boolean flag = FileUtil.parseWxBill(file, wxFile);
				logger.info("解析微信对账单费率:" + rate + "生成新的对账文件" + flag);
				if (flag) {
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					out.setReturnMsg("微信对账单费率：" + rate + "下载成功");
					out.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
					out.putValue("respDesc", "微信对账单费率：" + rate + "下载成功");
					return out;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("微信对账单处理费率" + rate + "异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信对账单费率" + rate + "下载失败");
			out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			out.putValue("respDesc", "微信对账单费率" + rate + "下载失败 ");
		}
		logger.info("[微信下载单个费率对账单] downloadSingleWxBill END" + out.toString());
		return out;
	}

	/**
	 * 微信对账单合成
	 */
	public OutputParam sumWxBill(InputParam input) {
		logger.info("[ 微信对账单合成]  sumWxBill START" + input.toString());
		OutputParam out = new OutputParam();
		try {
			String billDate = StringUtil.toString(input.getValue("billDate"));
			if (!DateUtils.isDate(billDate)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信对账单日期" + billDate + "合成失败:日期格式不正确");
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				out.putValue("respDesc", "微信对账单日期" + billDate + "合成失败:日期格式不正确");
				logger.info("微信对账单日期" + billDate + "合成失败:日期格式不正确");
				return out;
			}
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			out.setReturnMsg("微信对账单合成成功" + billDate);
			out.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			out.putValue("respDesc", "微信对账单合成成功" + billDate);
			boolean flag = FileUtil.sumWxBill(billDate);
			if (!flag) {
				logger.error("微信对账单合成新的对账文件失败" + billDate);
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				out.putValue("respDesc", "微信对账单合成新的对账文件失败" + billDate);
				return out;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("微信对账单合成异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信对账单合成异常");
			out.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			out.putValue("respDesc", "微信对账单合成异常 ");
		}
		logger.info("[ 微信对账单合成]  sumWxBill END" + out.toString());
		return out;
	}

	/**
	 * 查询微信订单信息
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam queryWxOrder(InputParam input) throws FrameException {
		logger.info("[微信订单查询]  queryWxOrder START:" + input.toString());
		OutputParam out = new OutputParam();
		try {

			String channel = StringUtil.toString(input.getValue(Dict.channel));
			String txnDt = StringUtil.toString(input.getValue(Dict.txnDt));
			String txnTm = StringUtil.toString(input.getValue(Dict.txnTm));
			String txnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String merId = StringUtil.toString(input.getValue(Dict.merId));
			String wxOrderNo = StringUtil.toString(input.getValue(Dict.wxOrderNo));
			String subMchId = StringUtil.toString(input.getValue(Dict.subMchId));

			if (StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				txnSeqId = String.format("%s%s%s", txnSeqId, txnDt, txnTm);
			}

			/************************ 组装微信订单查询报文并发送请求 ******************************/
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subMchId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, outQuery.getReturnMsg());
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(outQuery.getReturnMsg());
				return out;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String key = configMap.get(Dict.WX_KEY);
			String appid = configMap.get(Dict.WX_APPID);
			String mch_id = configMap.get(Dict.WX_MERID);
			String pfxPath = configMap.get(Dict.WX_PFX_PATH);
			String pwd = configMap.get(Dict.WX_PWD);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put(Dict.transactionID, wxOrderNo);
			dataMap.put(Dict.outTradeNo, txnSeqId);
			dataMap.put(Dict.appid, appid);
			dataMap.put(Dict.mch_id, mch_id);
			dataMap.put(Dict.subMchId, subMchId);

			// 组装订单查询报文对象
			OrderQueryReqData orderQueryReqData = new OrderQueryReqData(dataMap, key);
			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_orderQuery_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pwd);

			// 发送订单查询请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, orderQueryReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);
			logger.info("[微信订单查询]查询订单请求返回的响应报文：" + respString);

			String returnCode = StringUtil.toString(respMap.get(Dict.return_code));
			String returnMsg = StringUtil.toString(respMap.get(Dict.return_msg));
			if (!"SUCCESS".equals(returnCode)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信订单查询]微信订单查询失败:" + returnMsg);
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信订单查询]微信订单查询失败:" + returnMsg);
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信订单查询]报文签名验证不通过");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信订单查询]报文签名验证不通过");
				return out;
			}

			String resultCode = StringUtil.toString(respMap.get(Dict.result_code));
			if (!"SUCCESS".equals(resultCode)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
				out.putValue(Dict.resCode, StringConstans.returnCode.FAIL);
				out.putValue(Dict.resDesc, "[微信订单查询]交易异常：" + StringUtil.toString(respMap.get(Dict.err_code_des)));
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信订单查询]微信订单处理失败");
			} else {
				String tradeState = StringUtil.toString(respMap.get(Dict.trade_state));
				String transaction_id = wxOrderNo;
				out.putValue("tradeState", tradeState);
				if (!StringUtil.isEmpty(respMap.get("transaction_id"))) {
					transaction_id = respMap.get("transaction_id").toString();
				}
				out.putValue(Dict.wxOrderNo, transaction_id);
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				if ("SUCCESS".equals(tradeState)) {
					String payTime = respMap.get(Dict.time_end).toString();
					String openid = respMap.get(Dict.openid).toString();
					String bankType = respMap.get(Dict.bank_type).toString();

					out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_02);
					out.putValue(Dict.settleDate, payTime);
					out.putValue(Dict.openid, openid);
					out.putValue(Dict.bankType, bankType);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
					out.putValue(Dict.respDesc, "[微信订单查询]" + StringConstans.RespDesc.RESP_DESC_02);
					out.setReturnMsg("[微信订单查询]微信订单支付成功");
				} else if ("USERPAYING".equals(tradeState) || "NOTPAY".equals(tradeState)) {
					out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_01);
					out.putValue(Dict.respDesc, "[微信订单查询]" + StringConstans.RespDesc.RESP_DESC_01);
					out.setReturnMsg("[微信订单查询]微信订单用户正在支付中");
				} else if ("REVOKED".equals(tradeState)) {
					out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_15);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_15);
					out.putValue(Dict.respDesc, "[微信订单查询]" + StringConstans.RespDesc.RESP_DESC_15);
					out.setReturnMsg("[微信订单查询]订单已撤销");
				} else if ("CLOSED".equals(tradeState)) {
					out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_09);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_09);
					out.putValue(Dict.respDesc, "[微信订单查询]" + StringConstans.RespDesc.RESP_DESC_09);
					out.setReturnMsg("[微信订单查询]订单已关闭");
				} else {
					out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, "[微信订单查询]" + StringConstans.RespDesc.RESP_DESC_03);
					out.setReturnMsg("[微信订单查询]微信订单支付失败");
				}
			}
		} catch (Exception e) {
			logger.error("[微信订单查询]微信订单查询处理异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "[微信订单查询]" + StringConstans.RespDesc.RESP_DESC_03 + e.getMessage());
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信订单查询]微信订单查询处理失败");
		} finally {
			logger.info("[微信订单查询]  queryWxOrder END:" + out.toString());
		}
		return out;
	}

	/**
	 * 查询微信订单信息
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam queryWxOrderYL(InputParam input) throws FrameException {
		logger.info("[银联][微信订单查询]  queryWxOrder START:" + input.toString());
		OutputParam out = new OutputParam();
		try {
			String txnSeqId = input.getValue(Dict.txnSeqId).toString();
			String rateChannel = StringUtil.toString(input.getValue(Dict.rate));
			String subMchId = StringUtil.toString(input.getValue(Dict.sub_mch_id));
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_" + rateChannel;

			/************************ 组装微信订单查询报文并发送请求 ******************************/
			Map<String, String> data = new HashMap<String, String>();
			data.put(Dict.out_trade_no, txnSeqId);
			data.put(Dict.sub_mch_id, subMchId);

			String url = Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.orderqueryUrlSuffix");
			Map<String, String> respMap = ylwxPayService.wxSend(data, cacheKey, url);
			logger.info("[银联][微信订单查询] 返回报文：" + respMap.toString());
			out.setReturnStr(respMap);

			String returnCode = respMap.get(Dict.return_code);
			String returnMsg = respMap.get(Dict.return_msg);
			String resultCode = respMap.get(Dict.result_code);
			String errCode = respMap.get(Dict.err_code);
			String errCodeDes = respMap.get(Dict.err_code_des);

			if (!StringConstans.WxTradeStatus.SUCCESS.equals(returnCode)) {
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_06);
				out.setReturnMsg("[银联][微信订单查询]微信查询通讯失败"+returnMsg);
				out.putValue(Dict.respDesc, "[银联][微信订单查询]微信查询通讯失败"+returnMsg);
				return out;
			}

			if (!StringConstans.WxTradeStatus.SUCCESS.equals(resultCode)) {
				if(StringConstans.WxErrorCode.SYSTEM_ERROR.equals(errCode)) {
					out.setReturnCode(StringConstans.returnCode.SUCCESS);
					out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_06);
					out.putValue(Dict.respDesc, "[银联][微信订单查询]微信状态未知:"+errCodeDes);
					out.setReturnMsg("[银联][微信订单查询]微信状态未知:"+errCodeDes);
				}else {
					out.setReturnCode(StringConstans.returnCode.FAIL);
					out.setReturnMsg("[银联][微信订单查询]交易异常：" + errCode + "|" + errCodeDes);
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, "[银联][微信订单查询]交易异常：" + errCode + "|" + errCodeDes);
				}
				return out;
			}

			String wxStatus = respMap.get(Dict.trade_state);
			// 代表查询处理成功 不代表查询的交易成功
			out.setReturnCode(StringConstans.returnCode.SUCCESS);

			if (StringConstans.WxTradeStatus.SUCCESS.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_02);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
				out.putValue(Dict.respDesc, "[银联][微信订单查询]" + StringConstans.RespDesc.RESP_DESC_02);
				out.setReturnMsg("[银联][微信订单查询]" + StringConstans.RespDesc.RESP_DESC_02);
			} else if (StringConstans.WxTradeStatus.CLOSED.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_09);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_09);
				out.putValueStr(Dict.respDesc, "[银联][微信订单查询]订单已关闭");
				out.setReturnMsg("[银联][微信订单查询]订单已关闭");
			} else if (StringConstans.WxTradeStatus.REFUND.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_08);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_08);
				out.putValueStr(Dict.respDesc, "[银联][微信订单查询]转入退款");
				out.setReturnMsg("[银联][微信订单查询]转入退款");
			} else if (StringConstans.WxTradeStatus.REVOKED.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_15);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_15);
				out.putValue(Dict.respDesc, "[银联][微信订单查询]" + StringConstans.RespDesc.RESP_DESC_15);
				out.setReturnMsg("[银联][微信订单查询]" + StringConstans.RespDesc.RESP_DESC_15);
			} else if (StringConstans.WxTradeStatus.USERPAYING.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_06);
				out.putValue(Dict.respDesc, "[银联][微信订单查询]微信订单用户正在支付中");
				out.setReturnMsg("[银联][微信订单查询]微信订单用户正在支付中");
			} else if (StringConstans.WxTradeStatus.NOTPAY.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_06);
				out.setReturnMsg("[银联][微信订单查询]微信订单用户未支付");
				out.putValue(Dict.respDesc, "[银联][微信订单查询]微信订单用户未支付");
			} else if (StringConstans.WxTradeStatus.PAYERROR.equals(wxStatus)) {
				out.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
				out.setReturnMsg("[银联][微信订单查询]微信订单支付失败" + errCodeDes);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[银联][微信订单查询]微信订单支付失败" + errCodeDes);
			}

		} catch (Exception e) {
			logger.error("[银联][微信订单查询]微信订单查询处理异常：" + e.getMessage(), e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[银联][微信订单查询]微信订单查询处理失败" + e.getMessage());
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "[银联][微信订单查询]微信订单支付失败" + e.getMessage());
		} finally {
			logger.info("[银联][微信订单查询]开始后台向微信查询订单状态]  queryWxOrder END:" + out.toString());
		}
		return out;
	}

	/**
	 * 微信撤销
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam reverseOrder(InputParam input) throws FrameException {
		logger.info("[微信撤销] reverseOrder  START"+input.toString());
		OutputParam out = new OutputParam();
		try {
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String subMchId = StringUtil.toString(input.getValue(Dict.subMchId));

			/************************ 组装微信申请撤销报文并发送请求 ******************************/
			String appid = StringUtil.toString(input.getValue(Dict.appid));
			String mch_id = StringUtil.toString(input.getValue(Dict.mch_id));
			String pfxPath = StringUtil.toString(input.getValue(Dict.pfxPath));
			String pfxPwd = StringUtil.toString(input.getValue(Dict.pfxPwd));
			String key = StringUtil.toString(input.getValue(Dict.key));

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("transactionID", "");
			dataMap.put(Dict.outTradeNo, initTxnSeqId);
			dataMap.put(Dict.appid, appid);
			dataMap.put(Dict.mch_id, mch_id);
			dataMap.put(Dict.subMchId, subMchId);

			// 向微信发送微信撤销请求并处理返回报文
			// 组装撤销报文对象
			ReserveReqData reserveReqData = new ReserveReqData(dataMap, key);

			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_reverse_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pfxPwd);

			// 发送申请撤销请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, reserveReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);
			logger.info("[微信撤销]申请撤销返回的响应报文：" + respString);

			String returnCode = respMap.get(Dict.return_code).toString();
			String returnMsg = respMap.get(Dict.return_msg).toString();
			if (!"SUCCESS".equals(returnCode)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信撤销]微信撤销申请失败:" + returnMsg);
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信撤销]微信撤销申请失败:报文签名验证不通过");
				return out;
			}

			String resultCode = StringUtil.toString(respMap.get(Dict.result_code));
			if (!"SUCCESS".equals(resultCode)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信撤销]微信撤销申请失败:" + respMap.get("err_code_des").toString());
				return out;
			} else {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
				out.putValue(Dict.respDesc, "[微信撤销]撤销成功");
			}
		} catch (Exception e) {
			logger.error("微信撤销处理异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "[微信撤销]微信撤销处理失败" + e.getMessage());
		}finally {
			logger.info("[微信撤销] reverseOrder  END"+out.toString());
		}
		return out;
	}

	/**
	 * 微信断直连撤销
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam reverseOrderYL(InputParam input) throws FrameException {

		logger.info("[银联][微信断直连撤销] reverseOrderYL START " + input.toString());
		OutputParam out = new OutputParam();
		try {
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String subMchId = StringUtil.toString(input.getValue(Dict.sub_mch_id));
			String cacheKey = StringUtil.toString(input.getValue(Dict.cacheKey));

			// 组装撤销报文对象
			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put(Dict.out_trade_no, initTxnSeqId);
			reqData.put(Dict.sub_mch_id, subMchId);

			// 发送申请撤销请求
			Map<String, String> respMap = ylwxPayService.wxSend(reqData, cacheKey,Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.orderreverseUrlSuffix"));
			logger.info("[银联][微信断直连撤销]申请撤销返回的响应报文：" + respMap.toString());

			String returnCode = respMap.get(Dict.return_code);
			String returnMsg = respMap.get(Dict.return_msg);
			String resultCode = respMap.get(Dict.result_code);

			if (Dict.SUCCESS.equals(resultCode)) {
				out.putValue(Dict.respCode, StringConstans.OrderState.STATE_02);
				out.putValue(Dict.respDesc, "[银联][微信断直连撤销]微信撤销申请成功");
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				out.setReturnMsg("[银联][微信断直连撤销]微信撤销申请成功");
			} else if (Dict.SUCCESS.equals(returnCode)) {
				out.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
				out.putValue(Dict.respDesc, "[银联][微信断直连撤销]微信撤销申请失败:" + respMap.get(Dict.err_code_des));
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[银联][微信断直连撤销]微信撤销申请失败:" + respMap.get(Dict.err_code_des));
			} else {
				out.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
				out.putValue(Dict.respDesc, "[银联][微信断直连撤销]微信撤销申请失败:" + returnMsg);
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[银联][微信断直连撤销]微信撤销申请失败:" + returnMsg);
			}
		} catch (Exception e) {
			logger.error("[银联][微信断直连撤销]微信撤销处理异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
			out.putValue(Dict.respDesc, "[银联][微信断直连撤销]微信撤销申请失败:" + e.getMessage());
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[银联][微信断直连撤销]微信撤销处理失败" + e.getMessage());
		}finally {
			logger.info("[银联][微信断直连撤销]开始后台向微信申请订单撤销  END" + out.toString());
		}
		return out;
	}

	/**
	 * 微信关闭订单
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam closeOrder(InputParam input) throws FrameException {

		logger.info("[微信申请订单关闭] closeOrder START " + input.toString());
		OutputParam out = new OutputParam();
		try {
			String merId = StringUtil.toString(input.getValue(Dict.merId));
			String txnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String subMchId = StringUtil.toString(input.getValue(Dict.subMchId));

			/************************ 组装微信申请关闭报文并发送请求 ******************************/
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subMchId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, outQuery.getReturnMsg());
				return out;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));

			String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String key = configMap.get(Dict.WX_KEY);
			String appid = configMap.get(Dict.WX_APPID);
			String mch_id = configMap.get(Dict.WX_MERID);
			String pfxPath = configMap.get(Dict.WX_PFX_PATH);
			String pwd = configMap.get(Dict.WX_PWD);

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put(Dict.outTradeNo, txnSeqId);
			dataMap.put(Dict.appid, appid);
			dataMap.put(Dict.mch_id, mch_id);
			dataMap.put(Dict.subMchId, subMchId);

			// 向微信发送微信关闭订单请求并处理返回报文
			// 组装关闭订单报文对象
			CloseReqData closeReqData = new CloseReqData(dataMap, key);

			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_close_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pwd);

			// 发送申请关闭订单请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, closeReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);
			String returnCode = respMap.get(Dict.return_code).toString();
			String returnMsg = respMap.get(Dict.return_msg).toString();
			if (!"SUCCESS".equals(returnCode)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信申请订单关闭] " + returnMsg);
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信申请订单关闭] 报文签名验证不通过");
				return out;
			}

			String resultCode = respMap.get(Dict.result_code).toString();
			if (!Dict.SUCCESS.equals(resultCode)) {
				String errCode = respMap.get("err_code").toString();
				if ("ORDERPAID".equals(errCode)) {
					logger.error("[微信关闭订单] 订单已完成支付，关闭失败");
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_07);
					out.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_07);
				} else {
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, "[微信申请订单关闭]微信关闭订单失败" + errCode);
				}
			} else {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_09);
				out.putValue(Dict.respDesc, "[微信申请订单关闭]微信关闭订单成功");
			}
		} catch (Exception e) {
			logger.error("微信关闭订单处理异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "[微信申请订单关闭] " + e.getMessage());
		} finally {
			logger.info("[微信申请订单关闭]  closeOrder END " + out.toString());
		}
		return out;
	}

	/**
	 * 微信断直连关闭订单
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam closeOrderYL(InputParam input) throws FrameException {

		logger.info("[微信断直连关闭订单 ]  closeOrderYL  START ：" + input.toString());

		OutputParam out = new OutputParam();

		try {
			String merId = StringUtil.toString(input.getValue(Dict.merId));
			String txnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String subMchId = StringUtil.toString(input.getValue(Dict.subMchId));

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subMchId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);

			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, outQuery.getReturnMsg());
				return out;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_" + rateChannel;

			Map<String, String> reqData = new HashMap<String, String>();
			reqData.put(Dict.out_trade_no, txnSeqId);
			reqData.put(Dict.sub_mch_id, subMchId);

			// 发送申请关闭订单请求
			Map<String, String> respMap = ylwxPayService.wxSend(reqData, cacheKey,Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.ordercloseUrlSuffix"));
			logger.info("[微信断直连关闭订单 ] 申请关闭订单返回的响应报文：" + respMap.toString());

			String returnCode = respMap.get(Dict.return_code);
			String returnMsg = respMap.get(Dict.return_msg);
			String resultCode = respMap.get(Dict.result_code);

			if (Dict.SUCCESS.equals(resultCode)) {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_09);
				out.putValue(Dict.respDesc, "[微信断直连关闭订单 ] 关闭订单成功");
			} else if (Dict.SUCCESS.equals(returnCode)) {
				String errCode = respMap.get(Dict.err_code).toString();
				String errCodeDes = respMap.get(Dict.err_code).toString();
				if (Dict.ORDERPAID.equals(errCode)) {
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_07);
					out.putValue(Dict.respDesc, "[微信断直连关闭订单 ]:" + StringConstans.RespDesc.RESP_DESC_07);
				} else {
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, "[微信断直连关闭订单 ]微信关闭订单失败" + errCodeDes);
				}
			} else {
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "[微信断直连关闭订单 ] " + returnMsg);
			}

		} catch (Exception e) {
			logger.error("[微信断直连关闭订单 ]关闭订单处理异常：" + e.getMessage(), e);
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "[微信断直连关闭订单 ] " + e.getMessage());
		} finally {
			logger.info("[微信断直连关闭订单 ] closeOrderYL END:" + out.toString());
		}
		return out;
	}

	/**
	 * 微信支付线上统一下单入口
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam wxUnifiedConsumeOnLine(InputParam input) throws FrameException {

		logger.info("----------------- 微信下单流程         START--------------------");

		OutputParam outputParam = new OutputParam();

		try {
			// 请求报文非空字段验证
			List<String> list = new ArrayList<String>();
			list.add("orderAmount");
			list.add("channel");
			list.add("orderNumber");
			list.add("orderTime");
			list.add("merId");
			list.add("payType");
			list.add("body");
			list.add("transType");
			list.add("merName");//
			list.add("isCredit");// 是否支持信用卡
			list.add("subWxMerId");// 微信子商户
			list.add("currencyType");
			list.add("payAccessType");
			list.add("deviceInfo");
			list.add("customerIp");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("[微信支付线上统一下单] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			// 商户ID
			String merId = String.format("%s", input.getValue("merId"));
			// 订单号
			String orderNumber = String.format("%s", input.getValue("orderNumber"));
			// 订单时间
			String orderTime = String.format("%s", input.getValue("orderTime"));
			// 订单金额
			String orderAmount = String.format("%s", input.getValue("orderAmount"));
			// 交易类型
			String transType = String.format("%s", input.getValue("transType"));
			// 渠道
			String channel = String.format("%s", input.getValue("channel"));
			// 币种
			String currencyType = String.format("%s", input.getValue("currencyType"));
			// 接入类型
			String payAccessType = String.format("%s", input.getValue("payAccessType"));
			// 支付类型
			String payType = String.format("%s", input.getValue("payType"));
			// 是否支持信用卡
			String isCredit = String.format("%s", input.getValue("isCredit"));
			// 微信子商户号
			String subWxMerId = String.format("%s", input.getValue("subWxMerId"));
			// 订单 开始时间
			String timeStart = String.format("%s", input.getValue("timeStart"));
			// 订单结束时间
			String timeExpire = String.format("%s", input.getValue("timeExpire"));
			// 详细信息
			String productInfoDetail = String.format("%s", input.getValue("productInfoDetail"));
			// 设备信息
			String deviceInfo = String.format("%s", input.getValue("deviceInfo"));
			// 产品ID
			String productId = String.format("%s", input.getValue("productId"));
			// 商品描述
			String body = String.format("%s", input.getValue("body"));
			// 用户标识
			String openId = String.format("%s", input.getValue("openId"));
			// ip地址
			String customerIp = String.format("%s", input.getValue("customerIp"));
			// 商品标记
			String goodsTag = String.format("%s", input.getValue("goodsTag"));

			// 原样返回
			outputParam.putValue("merId", merId);
			outputParam.putValue("orderNumber", orderNumber);
			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderAmount", orderAmount);
			outputParam.putValue("transType", transType);
			outputParam.putValue("channel", channel);
			outputParam.putValue("currencyType", currencyType);
			outputParam.putValue("payAccessType", payAccessType);
			outputParam.putValue("payType", payType);
			outputParam.putValue("subWxMerId", subWxMerId);

			/******************** 1.报文参数验证 *****************************/
			logger.debug("[微信支付线上统一下单] 时间校验 orderTime=" + orderTime);
			if (!orderTime.matches("\\d{14}")) {
				logger.debug("[微信支付线上统一下单] 时间校验失败");
				outputParam.putValue("respDesc", "交易时间：[" + orderTime + "]错误");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单] 金额校验 orderAmount=" + orderAmount);
			if (!orderAmount.matches("\\d{12}")) {
				logger.debug("[微信支付线上统一下单] 金额校验失败");
				outputParam.putValue("respDesc", "金额：[" + orderAmount + "]错误");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			BigDecimal amount = new BigDecimal(orderAmount);
			if (amount.compareTo(new BigDecimal(0)) <= 0) {
				logger.debug("[微信支付线上统一下单],订单金额[orderAmount]非法");
				outputParam.putValue("respDesc", "订单金额[orderAmount]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单],交易类型验证 transType=" + transType);
			if (!StringConstans.TransType.TRANS_CONSUME.equals(transType)) {
				logger.debug("交易类型[transType]错误");
				outputParam.putValue("respDesc", "交易类型[transType]错误");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单],支付类型验证 payType=" + payType);
			if (!StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)
					&& !StringConstans.Pay_Type.PAY_TYPE_WEIXIN_APP.equals(payType)
					&& !StringConstans.Pay_Type.PAY_TYPE_WEIXIN_JSAPI.equals(payType)) {
				logger.debug("[微信支付线上统一下单] 支付类型[payType]非法");
				outputParam.putValue("respDesc", "支付类型[payType]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单],验证 openId=" + openId);
			if (StringConstans.Pay_Type.PAY_TYPE_WEIXIN_JSAPI.equals(payType) && StringUtil.isEmpty(openId)) {
				logger.debug("[微信支付线上统一下单] 用户标识openId不能为空");
				outputParam.putValue("respDesc", "openId不能为空openId不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]，交易接入类型验证 payAccessType=" + payAccessType);
			if (!StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
				logger.debug("[微信支付线上统一下单] 交易接入类型[payAccessType]错误");
				outputParam.putValue("respDesc", "交易接入类型[payAccessType]错误");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]，是否支持信用卡验证 isCredit=" + isCredit);
			if (!StringConstans.IsCredit.NONSUPPORT_CREDIT.equals(isCredit)
					&& !StringConstans.IsCredit.SUPPORT_CREDIT.equals(isCredit)) {
				logger.debug("[微信支付线上统一下单] 是否支持信用卡值[isCredit]非法");
				outputParam.putValue("respDesc", "是否支持信用卡值[isCredit]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]，渠道验证 channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_ONLINE_PAY.equals(channel)
					&& !StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				logger.debug("[微信支付线上统一下单] 渠道[channel]非法");
				outputParam.putValue("respDesc", "渠道[channel]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]，设备信息验证 deviceInfo=" + deviceInfo);
			if (!StringConstans.DeviceInfo.WEB.equals(deviceInfo)) {
				logger.debug("[微信支付线上统一下单] 设备信息[deviceInfo]非法");
				outputParam.putValue("respDesc", "设备信息[deviceInfo]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]，订单开始时间timeStart=" + timeStart);
			if (!StringUtil.isEmpty(timeStart) && !timeStart.matches("\\d{14}")) {
				logger.debug("[微信支付线上统一下单] 订单开始时间[timeStart]非法");
				outputParam.putValue("respDesc", "订单开始时间[timeStart]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]，订单到期时间timeExpire=" + timeExpire);
			if (!StringUtil.isEmpty(timeExpire) && !timeExpire.matches("\\d{14}")) {
				logger.debug("[微信支付线上统一下单] 订单到期时间[timeExpire]非法");
				outputParam.putValue("respDesc", "订单到期时间[timeExpire]非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			if (!StringUtil.isEmpty(timeStart) && StringUtil.isEmpty(timeExpire)) {
				logger.debug("[微信支付线上统一下单] 订单到期时间[timeExpire]不能为空");
				outputParam.putValue("respDesc", "订单到期时间[timeExpire]不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			if (!StringUtil.isEmpty(timeExpire) && StringUtil.isEmpty(timeStart)) {
				logger.debug("[微信支付线上统一下单] 订单开始时间[timeStart]不能为空");
				outputParam.putValue("respDesc", "订单开始时间[timeStart]不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			if (!StringUtil.isEmpty(timeStart) && !StringUtil.isEmpty(timeExpire)) {
				if (DateUtil.compareDate(timeStart, 5, DateUtil.MINUTE, timeExpire, null)) {
					logger.debug("[微信支付线上统一下单] 订单间隔时间必须超过5分钟");
					outputParam.putValue("respDesc", "订单间隔时间必须超过5分钟");
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					return outputParam;
				}
			}

			/******************** 2.重复订单验证 *****************************/

			logger.debug("[微信支付线上统一下单]  校验是否有重复订单信息");

			// 订单日期
			String merDt = orderTime.substring(0, 8);

			// 订单时间
			String merTm = orderTime.substring(8, 14);

			InputParam queryInputParam = new InputParam();
			queryInputParam.putparamString("merOrderId", orderNumber);
			queryInputParam.putparamString("merOrTm", merTm);
			queryInputParam.putparamString("merOrDt", merDt);

			logger.debug("[微信支付线上统一下单]  查询是否有重复订单      开始");

			OutputParam queryOut = orderService.queryOrder(queryInputParam);

			logger.debug("[微信支付线上统一下单]  查询是否有重复订单      结束");

			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				String codeUrl = String.format("%s", queryOut.getValue("codeUrl"));
				String txnSeqId = String.format("%s", queryOut.getValue("txnSeqId"));
				String txnDt = String.format("%s", queryOut.getValue("txnDt"));
				String txnTm = String.format("%s", queryOut.getValue("txnTm"));
				String wxPrepayId = String.format("%s", queryOut.getValue("wxPrepayId"));
				String txnTime = String.format("%s%s", txnDt, txnTm);

				outputParam.putValue("codeUrl", codeUrl);
				outputParam.putValue("txnSeqId", txnSeqId);
				outputParam.putValue("txnTime", txnTime);
				outputParam.putValue("prepayId", wxPrepayId);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
				outputParam.putValue("respDesc", "微信线上下单成功");

				return outputParam;
			}

			logger.debug("[微信支付线上统一下单]  订单重复查询成功，没有重复订单");

			/********************* 查询商户费率 **********************************/
			InputParam queryThreeCodeInput = new InputParam();
			queryThreeCodeInput.putparamString("merId", merId);
			queryThreeCodeInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);

			logger.debug("[微信支付统一下单 -- 商户费率查询] 根据merId查询三码合一相关信息  开始");

			OutputParam queryOutput = threeCodeStaticQRCodeDataService
					.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

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
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("商户信息不存在");
				return outputParam;
			}

			/******************** 3.新增 订单表流水 *****************************/
			input.putParams("wxMerId", Constants.getParam("jg_merId"));

			logger.debug("[微信支付线上统一下单] 增加订单信息    开始 input(" + input.toString() + ")");
			OutputParam orderOut = wxManager.addConsumeOrder(input);
			logger.debug("[微信支付线上统一下单] 增加订单信息   结束 orderOut(" + orderOut.toString() + ")");

			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				outputParam.putValue("respDesc", "微信支付线上下单增加订单信息失败" + orderOut.getReturnMsg());
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

			// 订单日期
			String txnDt = String.format("%s", orderOut.getValue("txnDt"));
			// 订单时间
			String txnTm = String.format("%s", orderOut.getValue("txnTm"));
			// 订单流水
			String txnSeqId = String.format("%s", orderOut.getValue("txnSeqId"));

			/************************ 4.去微信发送下单请求 ********************************/

			InputParam coreInput = new InputParam();
			coreInput.putParams("body", body);
			coreInput.putParams("merId", merId);
			coreInput.putParams("out_trade_no", txnSeqId + txnDt + txnTm);
			coreInput.putParams("subMchId", subWxMerId);
			coreInput.putParams("device_info", deviceInfo);
			coreInput.putParams("spbill_create_ip", customerIp);
			coreInput.putParams("fee_type", currencyType);
			coreInput.putParams("notify_url", Constants.getParam("resevWxNotifyUrl"));
			coreInput.putParams("total_fee", StringUtil.strToIntAmount(orderAmount));

			if (StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				coreInput.putParams("trade_type", StringConstans.WeiXinTransType.NATIVE);
			}

			if (StringConstans.Pay_Type.PAY_TYPE_WEIXIN_APP.equals(payType)) {
				coreInput.putParams("trade_type", StringConstans.WeiXinTransType.APP);
			}

			if (StringConstans.Pay_Type.PAY_TYPE_WEIXIN_JSAPI.equals(payType)) {
				coreInput.putParams("trade_type", StringConstans.WeiXinTransType.JSAPI);
			}

			if (!StringUtil.isEmpty(productInfoDetail)) {
				coreInput.putParams("detail", productInfoDetail);
			}

			if (!StringUtil.isEmpty(productId)) {
				coreInput.putParams("product_id", productId);
			}

			if (!StringUtil.isEmpty(openId)) {
				coreInput.putParams("openId", openId);
			}

			// 如果支持本行卡或他行卡的信用卡，则微信支付支持信用卡
			if (StringConstans.IsCredit.NONSUPPORT_CREDIT.equals(isCredit)) {
				coreInput.putParams("limitPay", "no_credit");
			}

			if (!StringUtil.isEmpty(goodsTag)) {
				coreInput.putParams("goods_tag", goodsTag);
			}

			// 开始时间
			String beginDateTime = DateUtil.getDateStr(DateUtil.YYYYMMDDHHMMSS);
			// 结束时间
			String endDateTime = DateUtil.getOffsetDate(beginDateTime, 5, DateUtil.MINUTE, DateUtil.YYYYMMDDHHMMSS);

			if (StringUtil.isEmpty(timeStart)) {
				timeStart = beginDateTime;
			}

			if (StringUtil.isEmpty(timeExpire)) {
				timeExpire = endDateTime;
			}

			coreInput.putParams("time_start", timeStart);
			coreInput.putParams("time_expire", timeExpire);
			coreInput.putParams("attach", txnDt + txnTm);
			
			
			InputParam routingInput = new InputParam();
			routingInput.putParams(Dict.merId, merId);
			routingInput.putParams(Dict.subWxMerId, subWxMerId);
			
			OutputParam coreOutPut = null;
			
			OutputParam routingOut =wxMerchantSynchService.routing(routingInput);
			String connectMethod = StringUtil.toString(routingOut.getValue(Dict.connectMethod));
			logger.info("[微信支付线上统一下单] 调用微信组报文发送请求接口    开始 coreInput(" + coreInput.toString() + ")");
			if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				//间连
				coreOutPut = wxManager.toWxUnifiedOrderOnLineYL(coreInput);/** 调用微信组报文发送请求接口 */
				outputParam.putValue(Dict.connectMethod, StringConstans.CONNECT_METHOD.indirect);
			} else {
				//直连
				coreOutPut = wxManager.toWxUnifiedOrderOnLine(coreInput);/** 调用微信组报文发送请求接口 */
				outputParam.putValue(Dict.connectMethod, StringConstans.CONNECT_METHOD.directly);
			}
			
			logger.info("[微信支付线上统一下单]  调用微信组报文发送请求接口    结束 coreOutPut(" + coreOutPut.toString() + ")");


			// 预支付交易会话标识
			String prepayId = String.format("%s", coreOutPut.getValue("prepayId"));
			// 二维码链接
			String codeUrl = String.format("%s", coreOutPut.getValue("codeUrl"));
			// 信息描述
			String respDes = String.format("%s", coreOutPut.getReturnMsg());

			// 默认下单失败
			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);
			updateInput.putparamString("resDesc", "微信线上下单失败:" + respDes);

			outputParam.putValue("respDesc", "微信线上下单失败:" + respDes);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);

			// 下单成功
			if (StringConstans.returnCode.SUCCESS.equals(coreOutPut.getReturnCode())) {

				if (!StringUtil.isEmpty(codeUrl)) {
					outputParam.putValue("codeUrl", codeUrl);
					updateInput.putparamString("codeUrl", codeUrl);
				}

				updateInput.putparamString("wxPrepayId", prepayId);
				updateInput.putparamString("resDesc", "微信线上下单成功");
				updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_06);

				outputParam.putValue("txnSeqId", txnSeqId);
				outputParam.putValue("txnTime", txnDt + txnTm);
				outputParam.putValue("prepayId", prepayId);
				outputParam.putValue("wcPayData",coreOutPut.getValue("wcPayData"));
				outputParam.putValue("respDesc", "微信线上下单成功");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			}

			if (!StringConstans.returnCode.SUCCESS.equals(coreOutPut.getReturnCode())) {
				logger.error("[微信支付线上统一下单]：" + respDes);
			}

			logger.debug("[微信线上下单接口] 微信线上下单完成更新订单   开始 updateInput(" + updateInput.toString() + ")");

			OutputParam updateOut = orderService.updateOrder(updateInput);

			logger.info("[微信线上下单接口] 微信线上下单接口完成更新订单   结束 updateOut(" + updateOut.toString() + ")");

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				outputParam.putValue("respDesc", "微信线上下单失败,二维码前置更新订单 失败" + updateOut.getReturnMsg());
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}

		} catch (Exception e) {
			logger.error("微信支付统一下单处理异常：" + e.getMessage(), e);
			outputParam.putValue("respDesc", "微信线上下单出现异常");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
		} finally {
			logger.info("----------------- 微信下单流程         END--------------------outputParam(" + outputParam.toString()
					+ ")");
		}

		return outputParam;
	}

	/**
	 * 通知线程处理
	 * 
	 * @param input
	 */
	private void notfiyThreadHandler(InputParam input) {
		logger.info("通知线程处理[start],请求报文:" + input.toString());
		try {

			String respStr = input.getValue("respStr").toString();
			Map<String, Object> respMap = Util.getMapFromXML(respStr);

			input.getParams().clear();
			input.putMap(respMap);

			// 二维码前置流水号
			String outTradeNo = String.format("%s", input.getValue("out_trade_no"));
			// 用户标识
			String openId = String.format("%s", input.getValue("openid"));
			// 业务结果
			String resultCode = String.format("%s", input.getValue("result_code"));
			if (!StringConstans.WxTradeStatus.SUCCESS.equals(resultCode)) {
				logger.debug("[接受 微信后台通知 --外围系统通知  ] 微信后台通知交易状态不正确不进行通知");
				return;
			}
			if (!outTradeNo.matches("ONLINE\\d*") && !outTradeNo.matches("TC\\d*")) {
				logger.debug("[接受 微信后台通知 -- 外围系统通知] 非线上扫码交易不进行通知");
				return;
			}

			Pattern p = Pattern.compile("[a-zA-Z]*");
			Matcher m = p.matcher(outTradeNo);
			String prefix = m.find() ? m.group() : "";
			logger.debug("[接受 微信后台通知 -- 外围系统通知] prefix=" + prefix);

			outTradeNo = outTradeNo.substring(prefix.length(), outTradeNo.length());
			logger.debug("[接受 微信后台通知 -- 外围系统通知] 处理后的outTradeNo=" + outTradeNo);

			// 二维码交易流水号
			String txnSeqId = String.format("%s%s", prefix, outTradeNo.substring(0, 10));
			logger.debug("[接受 微信后台通知 -- 外围系统通知] 处理后的txnSeqId=" + txnSeqId);

			// 二维码前置时间
			String txnDateTime = String.format("%s", input.getValue("attach"));
			// 二维码前置日期
			String txnDt = txnDateTime.substring(0, 8);
			// 二维码前置时间
			String txnTm = txnDateTime.substring(8, 14);

			InputParam queryInput = new InputParam();
			queryInput.putparamString("txnSeqId", txnSeqId);
			queryInput.putparamString("txnDt", txnDt);
			queryInput.putparamString("txnTm", txnTm);

			logger.debug("[接受 微信后台通知 -- 外围系统通知]  根据外部订单号查询订单是否存在,请求报文:" + queryInput.toString());
			OutputParam queryOut = orderService.queryOrder(queryInput);
			logger.debug("[接受 微信后台通知 -- 外围系统通知]  根据外部订单号查询订单是否存在,返回报文:" + queryOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[接受 微信后台通知 -- 外围系统通知]  根据外部订单号查询订单没有找到匹配的记录");
				return;
			}
			queryOut.putValue("openId", openId);

			if (StringConstans.PrefixOrder.ONLINE.equals(prefix)) {
				this.notfiyOnlineThreadHandler(queryOut);
			}

			if (StringConstans.PrefixOrder.THREE_CODE.equals(prefix)) {
				this.notfiyThreeCodeThreadHandler(queryOut);
			}

		} catch (Exception e) {
			logger.debug("[接受 微信后台通知 -- 外围系统通知] 对微信返回信息进行通知出现异常:" + e.getMessage(), e);
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

		logger.info("[接受 微信后台通知 -- 外围系统通知] 根据ewmData查询三码合一相关信息  开始");

		OutputParam queryTcOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

		logger.info("[接受 微信后台通知 -- 外围系统通知] 根据ewmData查询三码合一相关信息  结束");

		if (!StringConstans.returnCode.SUCCESS.equals(queryTcOut.getReturnCode())) {
			logger.error("[查询三码合一流水] 根据ewmStatue查询记录失败");
			return;
		}

		NotifyMessage notifyMessage = new NotifyMessage();

		this.getThreeCodeNotifyMessage(notifyMessage, queryOut, queryTcOut);

		ThreadNotifyHelper.notifyThread(notifyMessage);
	}

	/**
	 * T+0推送处理
	 * 
	 * @param settleInput
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void toCoreForSettle(InputParam settleInput) throws FrameException {

		logger.info("[T+0清算]核心推送处理  请求报文" + settleInput.toString());
		Map<String, Object> wxOrder = new HashMap<String, Object>();
		wxOrder = (Map<String, Object>) settleInput.getValue("wxOrder");
		String ewmData = String.format("%s", wxOrder.get("ewmData"));
		String txnSeqId = String.format("%s", wxOrder.get("txnSeqId"));
		if (!txnSeqId.startsWith("TC")) {
			logger.debug("当前订单[" + txnSeqId + "]非一码通交易不进行入账");
			return;
		}
		InputParam queryThreeCodeInput = new InputParam();
		queryThreeCodeInput.putparamString("ewmData", ewmData);

		// 根据流水表中的二维码明文查询三码信息表
		OutputParam queryOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);
		if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
			logger.debug("[T+0清算] 查询三码信息表失败");
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
		String wxFeeRate = String.format("%s", wxOrder.get("bankFeeRate"));

		// 交易状态
		String txnSta = String.format("%s", settleInput.getValue("txnSta"));

		// 支付方式
		String payAccessType = String.format("%s", wxOrder.get("payAccessType"));

		// 订单清算方式'0':T+0,'1':T+1
		String settleMethod = "";

		if (!StringUtil.isEmpty(wxOrder.get("settleMethod"))) {
			settleMethod = String.format("%s", wxOrder.get("settleMethod"));
		}

		// 入账状态 默认为'0', '1':入账成功 '2'：入账失败 '3'：状态未知
		String accountedFlag = "";
		if (!StringUtil.isEmpty(wxOrder.get("accountedFlag"))) {
			accountedFlag = String.format("%s", wxOrder.get("accountedFlag"));
		}

		String txnChannel = String.format("%s", wxOrder.get("txnChannel"));

		logger.debug("交易状态：" + txnSta + "对账方式:" + "|" + settleMethod + "|" + "支付方式：" + payAccessType + "入账标识：" + "|"
				+ accountedFlag + "|");

		// 需同时满足交易成功——02；三码支付——8001；清算方式T+0；入账未成功：不为01；支付方式为微信支付
		if (StringConstans.OrderState.STATE_02.equals(txnSta) && StringConstans.CHANNEL.CHANNEL_SELF.equals(txnChannel)
				&& StringConstans.SettleMethod.SETTLEMETHOD0.equals(settleMethod)
				&& !StringConstans.AccountedFlag.ACCOUNTEDSUCCESS.equals(accountedFlag)
				&& StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
			wxOrder.put("acctNo", accountNo);
			wxOrder.put("merName", merName);
			wxOrder.put("merId", merId);
			wxOrder.put("orgCode", orgCode);
			wxOrder.put("wxFeeRate", wxFeeRate);
			settleInput.setParams(wxOrder);
			this.settleThreadHandler(settleInput);
		}

	}

	/**
	 * 核心推送处理
	 * 
	 * @param settleInput
	 * @throws Exception
	 */
	private void settleThreadHandler(InputParam settleInput) throws FrameException {
		NotifyMessage notifyMessage = new NotifyMessage();
		this.getSettleMessageToCore(notifyMessage, settleInput);
		ThreadNotifyHelper.notifyThread(notifyMessage);

	}

	/**
	 * T+0清算报文体所需信息
	 * 
	 * @param notifyMessage
	 * @param settleInput
	 * @throws Exception
	 */
	private void getSettleMessageToCore(NotifyMessage notifyMessage, InputParam settleInput) throws FrameException {
		// 交易流水
		String txnSeqId = String.format("%s", settleInput.getValue("txnSeqId"));

		// 交易日期
		String txnDt = String.format("%s", settleInput.getValue("txnDt"));

		// 交易时间
		String txnTm = String.format("%s", settleInput.getValue("txnTm"));

		// 收款方账号
		String accountNo = String.format("%-22s", settleInput.getValue("acctNo"));

		// 货币代码
		String currencyCode = String.format("%s", settleInput.getValue("currencyCode"));
		if (StringConstans.SETTLE_CURRENTY_TYPE.equals(currencyCode)) {
			currencyCode = String.format("%3s", StringConstans.SettleInfo.CURRENCY_CODE);
		}

		/*
		 * //商户名称 String merName = settleInput.getValue("merName").toString();
		 * //计算字符串中汉字 int chnLen = merName.getBytes().length - merName.length(); if
		 * (chnLen == 0) { merName = String.format("%-80s", merName); } else {
		 * //计算字符串中汉字有几部分 int chnArrLen = EBCDICGBK.chnArrLen(merName);
		 * //转码后0E表示汉字开始，0F表示汉字结束，所以有一部分汉字就需要减去两个字节 //merName = String.format("%-80s",
		 * merName).substring(0, 80 - chnLen - chnArrLen * 2); merName =
		 * String.format("%-80s", merName).substring(0, 80 - chnLen); }
		 */

		// 商户名称 -可以送空值
		String merName = String.format("%-80s", "");

		// 付款方账号
		String outAccountNo = String.format("%-22s", "");

		// 手续费率
		String wxFeeRate = String.format("%s", settleInput.getValue("wxFeeRate"));

		// 交易金额
		String tradeMoneyStr = String.format("%s", settleInput.getValue("tradeMoney"));

		// 计算去除手续费后的金额
		String moneyRemoveFee = Util.getActualTradeMoney(wxFeeRate, tradeMoneyStr);
		// 交易金额
		String tradeMoney = String.format("%17s", moneyRemoveFee);
		logger.info("[T+0清算] 去除手续费后的交易金额" + tradeMoney);

		// 费率
		String chargeFee = String.format("%-17s", "");

		// 对账编号
		String checkAccountNo = String.format("%-15s", "");

		// 商户编号(pos老系统上送商户编号是20位，新系统上送15位，因为核心只能存储15位商户编号，故将20位商户编号截取15位
		String merId = String.format("%s", settleInput.getValue("merId"));
		merId = merId.getBytes().length > 15 ? merId.substring(0, 3) + merId.substring(8) : merId;
		merId = String.format("%15s", merId);
		logger.debug("[T+0清算] 商户编号" + merId);

		String payAccessType = String.format("%s", settleInput.getValue("payAccessType"));
		if (StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
			payAccessType = StringConstans.SettleInfo.PAY_ACCESS_TYPE_WX;
		}
		int payAccessLen = payAccessType.getBytes().length - payAccessType.length();
		// payAccessType = String.format("%-10s", payAccessType).substring(0, 10 -
		// payAccessLen - 2);
		payAccessType = String.format("%-10s", payAccessType).substring(0, 10 - payAccessLen);

		// 付款方姓名
		// String payerName = String.format("%-80s",payAccessType).substring(0, 80 -
		// payAccessLen - 2);
		String payerName = String.format("%-80s", payAccessType).substring(0, 80 - payAccessLen);

		// 机构号
		String orgCode = StringUtils.rightPad(settleInput.getValue("orgCode").toString().substring(0, 3), 6, '0');

		// String orgCode = String.format("%-6s",
		// settleInput.getValue("orgCode").toString().substring(0, 3));

		// 附言
		String postscript = String.format("%-80s", "");

		// 备注
		String remark = String.format("%-80s", "");
		/*
		 * if (!StringUtil.isEmpty(settleInput.getValue("remark"))) { chnLen =
		 * remark.getBytes().length - remark.length(); if (chnLen == 0) { remark =
		 * String.format("%-80s", remark); } else { //计算字符串中汉字有几部分 int chnArrLen =
		 * EBCDICGBK.chnArrLen(remark); //转码后0E表示汉字开始，0F表示汉字结束，所以有一部分汉字就需要减去两个字节
		 * //remark = String.format("%-80s", remark).substring(0, 80 - chnLen -
		 * chnArrLen * 2); remark = String.format("%-80s", remark).substring(0, 80 -
		 * chnLen); } }else { remark = String.format("%-80s", ""); }
		 */

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
		String gmtPayment = String.format("%s", queryOut.getValue("settleDate"));

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
		String gmtPayment = String.format("%s", queryOut.getValue("settleDate"));
		// 用户标识
		String openId = String.format("%s", queryOut.getValue("openId"));
		// 订单备注
		String remark = String.format("%s", queryOut.getValue("remark"));
		// 账号
		String acctNo = String.format("%s", queryTcOut.getValue("acctNo"));
		// 银行手续费率
		String bankFeeRate = String.format("%s", queryTcOut.getValue("bankFeeRate"));
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
		message.setAcctNo(acctNo);
		message.setFee(fee);
		message.setBuyerInfo(openId);
		message.setOrgCode(orgCode);
		message.setRemark(remark);
		notifyMessage.setTxnSeqId(txnSeqId);
		notifyMessage.setThreeCodeNotifyMessage(message);
	}

	public IOrderDao getOrderDao() {
		return orderDao;
	}

	public void setOrderDao(IOrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public IOrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public IWxManager getWxManager() {
		return wxManager;
	}

	public void setWxManager(IWxManager wxManager) {
		this.wxManager = wxManager;
	}

	public IThreeCodeStaticQRCodeDataService getThreeCodeStaticQRCodeDataService() {
		return threeCodeStaticQRCodeDataService;
	}

	public void setThreeCodeStaticQRCodeDataService(
			IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService) {
		this.threeCodeStaticQRCodeDataService = threeCodeStaticQRCodeDataService;
	}

	public IMerchantChannelService getMerchantChannelService() {
		return merchantChannelService;
	}

	public void setMerchantChannelService(IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

	public YLWXPayService getYlwxPayService() {
		return ylwxPayService;
	}

	public void setYlwxPayService(YLWXPayService ylwxPayService) {
		this.ylwxPayService = ylwxPayService;
	}

	public OrderQueryManager getOrderQueryManager() {
		return orderQueryManager;
	}

	public void setOrderQueryManager(OrderQueryManager orderQueryManager) {
		this.orderQueryManager = orderQueryManager;
	}

	public WxMerchantSynchService getWxMerchantSynchService() {
		return wxMerchantSynchService;
	}

	public void setWxMerchantSynchService(WxMerchantSynchService wxMerchantSynchService) {
		this.wxMerchantSynchService = wxMerchantSynchService;
	}

}
