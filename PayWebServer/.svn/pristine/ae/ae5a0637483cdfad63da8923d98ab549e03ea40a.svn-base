package com.huateng.pay.services.cups.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.crypto.Base64;
import com.huateng.pay.common.socket.SocketClient;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.common.validate.C2BEWMValidation;
import com.huateng.pay.handler.services.OrderQueryManager;
import com.huateng.pay.services.cups.ICupsPayService;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IQRCodeService;
import com.huateng.pay.services.local.ILocalBankService;
import com.huateng.utils.AcpService;
import com.huateng.utils.CupsBase;
import com.huateng.utils.QRCodeTypeEnum;
import com.huateng.utils.SDKUtil;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;
import com.ibm.db2.jcc.b.in;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class CupsPayServiceImpl implements ICupsPayService {

	private Logger logger = LoggerFactory.getLogger(CupsPayServiceImpl.class);
	private IQRCodeService qrCodeService;
	private IOrderService orderService;
	private OrderQueryManager orderQueryManager;
	private ILocalBankService localBankService;

	/**
	 * 手机或者移动前端获取银联二维码(被扫申请C2B码)
	 */
	@Override
	public OutputParam unionPayApplyQrNo(InputParam intPutParam) throws FrameException {
		logger.info("[银联二维码被扫]  unionPayApplyQrNo 开始移动应用前置向银联申请c2b码   请求报文" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		String logPrefix = "[手机银行获取二维码]";
		String qrType = "";
		try {
			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("cardNo");
			validateParamList.add("customName");
			validateParamList.add("deviceNumber");
			validateParamList.add("orderNumber");
			validateParamList.add("orderTime");
			validateParamList.add("cardAttr"); // 卡属性 01- 借记卡 02-贷记卡（含准贷记卡）03- 电子账户
			validateParamList.add("mobile"); // 手机号
			validateParamList.add("deviceID"); // 设备标识 移动终端的唯一标识
			validateParamList.add("deviceType"); // 设备类型 1手机 2平板 3手表 4PC
			validateParamList.add("accountIdHash"); // 设备登陆账号ID 为登录的hash值替换
			validateParamList.add("sourceIP"); // 设备IP
			// validateParamList.add("deviceLocation"); //设备GPS位置
			// validateParamList.add("fullDeviceNumber"); //设备SIM卡号码

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug(logPrefix + " 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));

			if (StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logPrefix = "[移动前端获取二维码]";
			}

			logger.debug(logPrefix + " channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_BANK.equals(channel))
					&& !(StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel))) {
				logger.debug(logPrefix + " 渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确");
				return outputParam;
			}

			// 二维码前缀类型
			String localQrType = String.format("%s", intPutParam.getValue("localQrType"));
			if (!QRCodeTypeEnum.STATIC_QR_CODE_CUPS.getType().equals(localQrType)
					&& StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel)) {
				logger.debug(logPrefix + " 二维码前缀类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码前缀类型不正确");
				return outputParam;
			}

			// 卡属性
			String cardAttr = String.format("%s", intPutParam.getValue("cardAttr"));
			if (!StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr)
					&& !StringConstans.CupsEwmInfo.CATD_ATTR_DEBIT.equals(cardAttr)) {
				logger.debug(cardAttr + "卡属性值不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "卡属性值不正确" + cardAttr);
				return outputParam;
			}

			if (StringConstans.CupsEwmInfo.CATD_ATTR_DEBIT.equals(cardAttr)) {
				qrType = StringConstans.CupsEwmInfo.EWM_REQUEST_TYPE_DEBIT;
			} else if (StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr)) {
				qrType = StringConstans.CupsEwmInfo.EWM_REQUEST_TYPE_CREDIT;
			} else {
				qrType = StringConstans.CupsEwmInfo.EWM_REQUEST_TYPE_OTHER;
			}

			// 申请C2B码的付款方机构
			String issCode = Constants.getParam("acqCode"); // 配置文件中配置，暂时没有
			// 卡号
			String cardNo = String.format("%s", intPutParam.getValue("cardNo"));

			String[] splitCardNo = cardNo.split("\\|");
			String accNo = splitCardNo[0];
			// 姓名
			String customName = String.format("%s", intPutParam.getValue("customName"));
			// 设备号
			String deviceNumber = String.format("%s", intPutParam.getValue("deviceNumber"));
			// 订单号
			String orderNumber = String.format("%s", intPutParam.getValue("orderNumber"));
			// 手机号
			String mobile = String.format("%s", intPutParam.getValue("mobile"));
			// 设备标识
			String deviceID = String.format("%s", intPutParam.getValue("deviceID"));
			// 设备类型
			String deviceType = String.format("%s", intPutParam.getValue("deviceType"));
			// 设备登陆账号ID
			String accountIdHash = String.format("%s", intPutParam.getValue("accountIdHash"));
			// 设备IP
			String sourceIP = String.format("%s", intPutParam.getValue("sourceIP"));
			// 申码时间
			String qrOrderTime = DateUtil.getCurrentDateTime();

			logger.debug("[生成银联二维码] 组装请求报文--START");

			Map<String, String> contentData = new HashMap<String, String>();

			// 版本号
			contentData.put("version", StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			// 交易类型
			contentData.put("reqType", StringConstans.CupsTradeType.APPLY_C2B_TYPE);
			// 付款方机构代码
			contentData.put("issCode", issCode);
			// 二维码请求类型（可选）
			contentData.put("qrType", qrType); // 35借记卡 51 贷记卡

			// 付款方信息
			Map<String, String> payerInfoMap = new HashMap<String, String>();
			payerInfoMap.put("accNo", accNo);
			payerInfoMap.put("name", customName);
			payerInfoMap.put("cardAttr", cardAttr);// 01 – 借记卡 02 – 贷记卡（含准贷记卡）
			payerInfoMap.put("mobile", mobile);// 手机号，必送
			logger.info("[生成银联二维码]payerInfo" + payerInfoMap.toString());

			// 风控信息
			Map<String, String> riskInfoMap = new HashMap<String, String>();
			riskInfoMap.put("deviceID", deviceID);
			riskInfoMap.put("deviceType", deviceType);
			riskInfoMap.put("mobile", mobile);
			riskInfoMap.put("accountIdHash", accountIdHash);
			riskInfoMap.put("sourceIP", sourceIP);

			// 付款方敏感信息加密（先将原始信息整体做加密，再对加密后的数据进行Base64编码）
			// contentData.put("payerInfo", CupsBase.getPayerInfo(payerInfoMap));

			// 只需做Base64编码
			contentData.put("riskInfo", CupsBase.getPayerInfo(riskInfoMap));// 风控信息

			// 目前二维码系统要求所有接入均采用加密方式，使用正式机构号测试的时候参考如下方法上送

			contentData.put("payerInfo", CupsBase.getPayerInfoWithEncrpyt(payerInfoMap, "UTF-8"));

			// 加密证书ID
			contentData.put("encryptCertId", AcpService.getEncryptCertId());

			// 附加处理条件，若出现，则本次支付触发附加处理
			Map<String, String> addnCondMap = new HashMap<String, String>();
			addnCondMap.put("currency", StringConstans.CurrencyCode.CNY); // 金额币种

			// String pinFree = String.format("%s", intPutParam.getValue("pinFree"));
			// String maxAmont = String.format("%s", intPutParam.getValue("maxAmont"));
			String pinFree = String.format("%s", Constants.getParam("pinFree"));
			String maxAmont = String.format("%s", Constants.getParam("maxAmont"));

			addnCondMap.put("pinFree", pinFree);// 免密限额

			addnCondMap.put("maxAmont", maxAmont);// 最高交易金额
			if (!StringUtil.isEmpty(pinFree) || !StringUtil.isEmpty(maxAmont)) {
				contentData.put("addnCond", CupsBase.getPayerInfo(addnCondMap)); // 附加处理条件
				// contentData.put("addnCond", CupsBase.getPayerInfoWithEncrpyt(addnCondMap,
				// "UTF-8"));
				contentData.put("addnOpUrl", Constants.getParam("resevCupsNotifyUrl"));// 附加处理服务器地址
			}

			// contentData.put("reqReserved", "reserved"+outTxnDt);

			// 接收交易通知地址待
			contentData.put("backUrl", Constants.getParam("resevCupsNotifyUrl"));

			// 申码订单号（付款方申码交易主键）
			contentData.put("qrOrderNo", orderNumber);

			// 申码时间
			contentData.put("qrOrderTime", qrOrderTime);

			logger.info("[银联二维码被扫申请C2B码] 发送银联 请求报文" + contentData.toString());

			// 组装统一下单报文对象
			// Map<String, String> reqData = Signature.sign(contentData,"UTF-8");
			// //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
			Map<String, String> reqData = Signature.sign(contentData, "UTF-8"); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

			// logger.info("[银联二维码被扫] 组装请求报文--END");

			/// logger.info("[银联二维码被扫申请C2B码] 向银联发送C2B码申请--START");

			String requestUrl = Constants.getParam("qrcB2cIssBackTransUrl");

			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, "UTF-8"); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

			// logger.info("[银联二维码被扫申请C2B码] 向银联发送C2B码申请--END");

			InputParam saveQRParam = new InputParam();

			String qrNo = "";
			String qrValidTime = "";
			// 判断是否收到应答
			if (!rspData.isEmpty()) {
				logger.info("rspData" + rspData);
				if (Signature.validate(rspData, "UTF-8")) {
					logger.debug("[银联二维码被扫申请C2B码] 验证签名成功");
					String respCode = rspData.get("respCode");
					String respMsg = rspData.get("respMsg");
					logger.info("[银联二维码被扫申请C2B码] 银联返回响应信息: " + rspData.toString());
					if ("00".equals(respCode)) {
						// 银联返回二维码
						qrNo = rspData.get("qrNo");
						// 银联返回二维码有效期
						qrValidTime = rspData.get("qrValidTime");
						outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
						outputParam.putValue("codeUrl", qrNo);
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
						outputParam.putValue("respDesc", "二维码生成成功");
					} else {
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue("respDesc", respMsg);
						return outputParam;
					}
				} else {
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "报文签名验证不通过");
					return outputParam;
				}
			} else {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "未收到银联返回");
				return outputParam;
			}

			// logger.info("[银联二维码被扫申请C2B码] 保存二维码信息到二维码表--START");
			String validMinute = new BigDecimal(qrValidTime).divide(new BigDecimal(60)).stripTrailingZeros().toString();
			saveQRParam.putparamString("ewmData", qrNo);// c2b码
			saveQRParam.putparamString("validMinute", validMinute);// 二维码有效时间
			saveQRParam.putparamString("cardNo", cardNo);
			saveQRParam.putparamString("customName", customName);
			saveQRParam.putparamString("deviceNumber", deviceNumber);
			saveQRParam.putparamString("channel", channel);
			saveQRParam.putparamString("orderNumber", orderNumber);
			saveQRParam.putparamString("ewmType", "0"); // 暂时写死，后面再改
			saveQRParam.putparamString("status", StringConstans.QRCodeStatus.ENABLE);
			saveQRParam.putparamString("createTime", qrOrderTime);

			logger.debug("[银联二维码被扫申请C2B码] 保存二维码信息  请求信息" + saveQRParam.toString());

			OutputParam resultOutput = qrCodeService.saveQRCodeInfo(saveQRParam);
			// logger.info("[银联二维码被扫申请C2B码] 保存二维码信息到二维码表--END");

			if (!(StringConstans.returnCode.SUCCESS.equals(resultOutput.getReturnCode()))) {
				logger.debug(logPrefix + " 保存二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", logPrefix + " 保存二维码信息失败");
				return outputParam;
			}

			logger.debug("------------ " + logPrefix + " 保存二维码的信息成功-------------");

			// logger.info("[银联二维码被扫] 开始移动应用前置向银联申请c2b码 END");
		} catch (Exception e) {
			logger.error(logPrefix + "[银联二维码被扫]  银联二维码被扫申请:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "银联二维码被扫申请异常" + e.getMessage());
		}
		logger.info("[银联二维码被扫申请C2B码 ]unionPayApplyQrNo  方法结束  返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * 被扫附加请求处理
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OutputParam C2BScanedAttachHandler(InputParam intPutParam) throws FrameException {
		logger.info("[C2B附加请求处理] 方法开始  请求报文" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 校验金额
			Map<String, String> respMap = intPutParam.getParamString();
			// logger.debug("被扫附加请求处理 收到信息：" + respMap.toString());

			String txnAmt = String.format("%s", respMap.get("txnAmt"));
			logger.debug("[C2B附加请求处理] orderAmount:[" + txnAmt + "]");

			// 验证金额大小
			BigDecimal orderAmt = new BigDecimal(txnAmt);
			if (orderAmt.compareTo(new BigDecimal("0")) <= 0) {
				// logger.error("[C2B附加请求处理] 交易金额不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易金额不正确");
				return outputParam;
			}

			// 将金额转成12位
			String orderAmount = StringUtils.leftPad(txnAmt, 12, '0');
			logger.debug("[C2B附加请求处理] 转成12位长度金额:[" + orderAmount + "]");

			// 二维码
			String qrNo = String.format("%s", respMap.get("qrNo"));

			// 根据二维码信息查表
			InputParam queryQRInput = new InputParam();
			queryQRInput.putparamString("ewmData", qrNo);

			logger.info("[C2B附加请求处理]  查询  请求信息" + queryQRInput.toString());

			// 查询二维码信息
			OutputParam queryQROutput = qrCodeService.queryQRCodeInfo(queryQRInput);

			// logger.info("[C2B附加请求处理]" + " 查询二维码信息完成");
			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				// logger.error("[C2B附加请求处理]" + " 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "查询二维码信息失败");
				return outputParam;
			}

			// logger.info("------" +"[C2B附加请求处理]"+ " 查询二维码信息成功------");

			// 获取查询记录
			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();

			// 拼装发送丰收互联的报文
			Map<String, Object> map = new HashMap<String, Object>();
			// 二维码串
			map.put("codeUrl", qrNo);
			// 交易金额
			map.put("orderAmount", orderAmount);
			// 订单号
			String orderNumber = String.format("%s", queryQRMap.get("orderNumber"));
			// logger.info("C2B附加请求处理外部流水号" + orderNumber);
			map.put("orderNumber", orderNumber);

			// 二维码绑定账号
			String cardNo = String.format("%s", queryQRMap.get("cardNo"));
			map.put("cardNo", cardNo);

			// logger.info("[C2B附加请求处理] 开始获取订单流水号");
			// 获取内部流水号
			String txnSeqId = orderService.getOrderNo();
			// logger.info("[C2B附加请求处理] 获取订单流水号完成,txnSeqId:[" + txnSeqId + "]");
			map.put("txnSeqId", txnSeqId); // 暂时没有

			// 内部交易日期
			String txnDt = DateUtil.getDateYYYYMMDD();
			// 内部交易时间
			String txnTm = DateUtil.getDateHHMMSS();
			// 14位的内部交易时间
			String txnTime = String.format("%s%s", txnDt, txnTm);
			map.put("txnTime", txnTime);

			// 他行本行交易标识
			map.put("isLocalBank", StringConstans.BankFlag.NO_BANK); //// 0-他行 1-本行
			map.put("respCode", StringConstans.OrderState.STATE_01);
			map.put("respDesc", "交易正在处理");

			InputParam orderInput = new InputParam();
			if (!StringUtil.isEmpty(respMap.get("payerInfo")) && !StringUtil.isEmpty(respMap.get("encryptCertId"))) {
				String payerInfoEn = respMap.get("payerInfo");
				String payerInfo = AcpService.decryptData(payerInfoEn, "UTF-8");
				// logger.info("[C2B附加请求处理]payerInfo:"+payerInfo);
				payerInfo = payerInfo.substring(payerInfo.indexOf("{"), payerInfo.indexOf("}") + 1);
				logger.debug("[C2B附加请求处理]payerInfo去乱码后:" + payerInfo);
				String payerInfoRep = payerInfo.replaceAll("&", ",");
				Map<String, String> payerMap = getMapFromStr(payerInfoRep);
				// logger.info("[C2B附加请求处理]转换后map:"+map);

				String accNo = String.format("%s", payerMap.get("accNo"));
				// logger.info("[C2B附加请求处理]payerInfo:accNo:"+ accNo);

				String encryptCertId = respMap.get("encryptCertId");
				orderInput.putparamString("accNo", accNo);
				orderInput.putparamString("encryptCerid", encryptCertId);
			}
			if (!StringUtil.isEmpty(respMap.get("voucherNum"))) {
				String voucherNum = respMap.get("voucherNum");
				orderInput.putparamString("voucherNum", voucherNum);
			}
			if (!StringUtil.isEmpty(respMap.get("upReserved"))) {
				String upReserved = respMap.get("upReserved");
				orderInput.putparamString("voucherNum", upReserved);
			}

			// 新增订单

			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("qrNo", qrNo);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "交易正在处理");
			orderInput.putparamString("isLocalBank", StringConstans.BankFlag.NO_BANK);

			logger.info("[C2B附加请求处理] 保存订单 请求信息" + orderInput.toString());

			// 保存订单
			OutputParam saveOrderOutput = orderService.insertC2BOtherOrder(orderInput);

			// logger.info("[C2B附加请求处理] 保存订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				// logger.error("[C2B附加请求处理] 保存订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "C2B附加请求处理 二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[C2B附加请求处理] 保存订单信息成功------------");

			InputParam mobileInputParam = new InputParam();

			mobileInputParam.putMap(map);

			// logger.info("[C2B附加请求处理] 调用通知额度处理方法 开始");

			String recvStr = this.notifyMobileFontPay(mobileInputParam);

			// 判断手机银行传过来的参数值
			if (StringUtils.isBlank(recvStr)) {
				// logger.error("[C2B附加请求处理] 请求额度校验后，丰收互联返回报文为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "返回报文信息错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				return outputParam;
			}

			// 将JSON格式转换为Map
			Map<String, Object> paramMap = (Map<String, Object>) JsonUtil.json2Bean(recvStr, Map.class);
			// 订单状态
			String respCode = String.format("%s", paramMap.get("respCode"));
			// 订单状态描述
			String respDesc = String.format("%s", paramMap.get("respDesc"));

			if (StringConstans.RespCode.RESP_CODE_03.equals(respCode)) {
				logger.debug("[C2B附加请求处理] 请求额度校验后，丰收互联返回支付结果失败,原因:" + respDesc);
				// 如果因为行内处理失败，比如：密码挂失后支付等，直接发起被扫附加处理结果通知
				/*
				 * String voucherNum = String.format("%s", respMap.get("respMap")); String
				 * upReserved = String.format("%s", "upReserved");
				 *//**
					 * 组装请求报文
					 *//*
						 * Map<String, String> dataMap = new HashMap<String, String>();
						 * 
						 * dataMap.put("version", StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
						 * dataMap.put("reqType",
						 * StringConstans.CupsTradeType.C2B_SCANED_HANDLER_NOTIFY);
						 * dataMap.put("issCode", Constants.getParam("acqCode")); dataMap.put("qrNo",
						 * qrNo); if (!StringUtil.isEmpty(voucherNum)) { dataMap.put("voucherNum",
						 * voucherNum); } if (!StringUtil.isEmpty(upReserved)) {
						 * dataMap.put("upReserved", upReserved); } dataMap.put("respCode", "63");
						 * dataMap.put("respMsg", "卡状态不正确");
						 * logger.info("[被扫附加请求处理] 处理失败，发起附加处理结果通知， 开始组装下单报文"); //组装统一下单报文对象 Map<String,
						 * String> reqData = Signature.sign(dataMap,"UTF-8");
						 * //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。 String requestUrl =
						 * Constants.getParam("qrcB2cIssBackTransUrl"); Map<String, String> rspData =
						 * CupsBase.post(reqData,requestUrl,"UTF-8");
						 * //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，
						 * 调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过 //判断是否收到应答
						 * logger.info("[被扫附加请求处理]  rspData"+rspData);
						 * 
						 * return null;
						 */
			}

			// 校验字段列表
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("respDesc");
			validateParamList.add("respCode");

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, paramMap);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[C2B附加请求处理] 请求额度校验后，丰收互联返回报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "返回支付报文信息有误");
				return outputParam;
			}

			outputParam.putValue("respCode", respCode);
			outputParam.putValue("respDesc", respDesc);

			// logger.info("[C2B附加请求处理] 调用通知支付方法 结束");

			// logger.info("[附加处理结果通知] END");

		} catch (Exception e) {
			logger.error("[附加处理结果通知]出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置系统异常");
		} finally {
			logger.info("[C2B附加请求处理] 返回报文信息" + outputParam.toString());
		}
		return outputParam;
	}

	/**
	 * 被扫附加处理结果通知
	 */
	@Override
	public OutputParam C2BScanedAttachHandlerNotify(InputParam intPutParam) throws FrameException {
		logger.info("[银联二维码本行被他行扫]  C2BScanedAttachHandlerNotify 方法开始  请求信息" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");
			validateParamList.add("codeUrl");
			validateParamList.add("txnSta");
			validateParamList.add("orderNumber");
			validateParamList.add("orderTime");

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[被扫附加处理结果通知] 返回报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[被扫附加处理结果通知] 返回报文字段[\" + nullStr + \"]不能为空");
				return outputParam;
			}

			String qrNo = intPutParam.getValue("codeUrl").toString();
			String txnSta = intPutParam.getValue("txnSta").toString();
			String txnSeqId = intPutParam.getValue("txnSeqId").toString();
			String txnTime = intPutParam.getValue("txnTime").toString();
			String orderNumber = intPutParam.getValue("orderNumber").toString();
			String orderTime = intPutParam.getValue("orderTime").toString();

			if (!orderTime.matches("\\d{14}")) {
				logger.debug("[被扫附加处理结果通知] 支付平台上送订单时间长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付平台上送订单时间长度不正确" + orderTime);
				return outputParam;
			}

			String txnDt = txnTime.substring(0, 8);
			String txnTm = txnTime.substring(8);

			// 查询订单是否存在
			InputParam queryInput = new InputParam();
			queryInput.putparamString("txnSeqId", txnSeqId);
			queryInput.putparamString("txnDt", txnDt);
			queryInput.putparamString("txnTm", txnTm);
			logger.info("[被扫附加处理结果通知] 查询订单 请求信息" + queryInput.toString());
			OutputParam queryQROutput = orderService.queryC2BOtherOrder(queryInput);
			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				logger.debug("[被扫附加处理结果通知] 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[被扫附加处理结果通知] 查询二维码信息失败");
				return outputParam;
			}
			// logger.info("--------------[被扫附加处理结果通知] 查询订单信息成功------------");

			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();
			String voucherNum = String.format("%s", queryQRMap.get("voucherNum"));
			String upReserved = String.format("%s", queryQRMap.get("upReserved"));

			InputParam upateQRInput = new InputParam();
			// 查询条件
			upateQRInput.putparamString("txnSeqId", txnSeqId);
			upateQRInput.putparamString("txnDt", txnDt);
			upateQRInput.putparamString("txnTm", txnTm);

			// 要更新的信息
			upateQRInput.putparamString("orderNumber", orderNumber);
			upateQRInput.putparamString("orderTime", orderTime);
			upateQRInput.putparamString("txnSta", txnSta);

			logger.debug("[被扫附加处理结果通知] 更新银标二维码订单信息 请求信息" + upateQRInput.toString());

			// 更新二维码信息
			OutputParam updateQROutput = orderService.updateC2BOtherOrder(upateQRInput);

			if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
				logger.debug("[被扫附加处理结果通知] 更新二维码失效信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "[被扫附加处理结果通知] 更新二维码失效信息失败");
				return outputParam;
			}
			// logger.info("[被扫附加处理结果通知] 开始更新银标二维码订单信息完成");

			/**
			 * 组装请求报文
			 */
			Map<String, String> dataMap = new HashMap<String, String>();

			dataMap.put("version", StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			dataMap.put("reqType", StringConstans.CupsTradeType.C2B_SCANED_HANDLER_NOTIFY);
			dataMap.put("issCode", Constants.getParam("acqCode")); //
			dataMap.put("qrNo", qrNo);
			if (!StringUtil.isEmpty(voucherNum)) {
				dataMap.put("voucherNum", voucherNum);
			}
			if (!StringUtil.isEmpty(upReserved)) {
				dataMap.put("upReserved", upReserved);
			}
			// 确认附加信息通过，需根据额度中心返回的原订单状态（额度扣减成功或失败）来确定返回给银联的
			if (txnSta.equals("07")) {
				dataMap.put("respCode", "33");
				dataMap.put("respMsg", "交易金额超限");
			} else if (txnSta.equals("08")) {
				dataMap.put("respCode", "00");
				dataMap.put("respMsg", "SUCCESS");
			} else {
				logger.error("[银联二维码付款测确认付款通知]交易状态 不明确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "附加处理结果通知 交易状态不明确");
				return outputParam;
			}

			logger.info("[银联二维码付款测确认付款通知] 请求银联报文" + dataMap.toString());
			// 组装统一下单报文对象
			Map<String, String> reqData = Signature.sign(dataMap, "UTF-8"); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
			String requestUrl = Constants.getParam("qrcB2cIssBackTransUrl");
			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, "UTF-8"); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
			// 判断是否收到应答
			if (!rspData.isEmpty()) {
				logger.info("rspData" + rspData);
				if (Signature.validate(rspData, "UTF-8")) {
					logger.debug("[附加处理结果通知] 验证签名成功");
					String respCode = rspData.get("respCode");
					String respMsg = rspData.get("respMsg");
					logger.info("[附加处理结果通知] 收到返回信息" + rspData.toString());
					if ("00".equals(respCode)) {
						// 成功状态时的处理
						logger.debug("附加处理结果通知成功");
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
						outputParam.putValue("respDesc", "附加处理结果通知成功");
					} else {
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue("respDesc", "附加处理结果通知失败");
						return outputParam;
					}
				} else {
					logger.debug("[附加处理结果通知]接收的报文签名验证不通过");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "附加处理结果通知接收的报文签名验证不通过");
					return outputParam;
				}
			}
			// logger.info("[附加处理结果通知]被扫附加处理结果通知 END");
		} catch (Exception e) {
			logger.error("[附加处理结果通知] 被扫附加处理结果通知:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码前置系统异常");
		}
		logger.info("[附加处理结果通知]  C2BScanedAttachHandlerNotify 方法结束 返回信息" + outputParam.toString());
		return outputParam;
	}

	/**
	 * C2B交易通知，银联通知付款方，二维码前置收到通知后，通知额度中心
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OutputParam C2BEWMNotifyToLimitCenter(InputParam intPutParam) throws FrameException {
		logger.info("[C2B交易通知]  方法开始  请求信息" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			Map<String, String> rspData = intPutParam.getParamString();

			String qrNo = String.format("%s", rspData.get("qrNo"));

			String txnAmt = String.format("%s", rspData.get("txnAmt"));
			logger.debug("[C2B交易通知] 实际交易金额为:" + txnAmt);

			// 将金额转成 **.**格式
			BigDecimal TransAmtDec = new BigDecimal(txnAmt).divide(new BigDecimal(100));
			String TransAmt = String.format("%s", TransAmtDec);
			logger.debug("[C2B交易通知] 转换后交易金额为:" + TransAmt);

			// 将金额转成12位长度
			String tradeMoney = StringUtils.leftPad(txnAmt, 12, '0');

			// 根据二维码信息查表
			InputParam queryQRInput = new InputParam();
			queryQRInput.putparamString("qrNo", qrNo);

			// logger.info("[C2B交易通知]" +"开始查询二维码信息");

			// 查询二维码信息
			OutputParam queryQROutput = orderService.queryC2BOtherOrder(queryQRInput);

			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				// logger.error("[C2B交易通知]" + " 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "C2B交易通知 查询二维码信息失败");
				return outputParam;
			}

			// logger.info("------" +"[C2B交易通知]"+ " 查询二维码信息成功------");

			// 获取查询记录
			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();

			// 订单号
			String orderNumber = String.format("%s", queryQRMap.get("orderNumber"));

			// 订单时间
			String orderTime = String.format("%s", queryQRMap.get("orderTime"));

			logger.debug("[C2B交易通知] 原订单号" + orderNumber);

			String settleDate = rspData.get("settleDate"); // 清算日期
			logger.debug("[C2B交易通知] 交易日期" + settleDate);

			String origRespCode = rspData.get("origRespCode"); // 原交易应答码
			logger.debug("[C2B交易通知] 原交易应答码" + origRespCode);

			String origRespMsg = rspData.get("origRespMsg"); // 原交易应答码
			logger.debug("[C2B交易通知] 原交易应答信息" + origRespMsg);

			String txnSta = StringConstans.RespCode.RESP_CODE_01;
			String resDesc = "";
			if (origRespCode.equals("00")) {
				// logger.info("[C2B交易通知] 原交易交易成功");
				txnSta = StringConstans.RespCode.RESP_CODE_02;
				resDesc = "交易成功";
			} else if (origRespCode.equals("04") || origRespCode.equals("06")) {
				logger.debug("[C2B交易通知] 原交易正在处理中");
				resDesc = "原交易正在处理中";
			} else if (origRespCode.equals("77")) {
				logger.debug("[C2B交易通知] 银行卡未开通认证支付");
				txnSta = StringConstans.RespCode.NO_CUPS_PAY_VOCATION;
				resDesc = "银行卡未开通认证支付";
			} else {
				logger.debug("[C2B交易通知] 原交易失败" + origRespCode);
				txnSta = StringConstans.RespCode.RESP_CODE_03;
				resDesc = "交易失败";
			}

			InputParam updateOrder = new InputParam();
			updateOrder.putparamString("qrNo", qrNo);
			updateOrder.putparamString("txnSta", txnSta);
			updateOrder.putparamString("resDesc", resDesc);
			updateOrder.putparamString("tradeMoney", tradeMoney);

			logger.info("[C2B交易通知] 更新 请求信息" + updateOrder.toString());

			// 更新二维码信息
			OutputParam updateQROutput = orderService.updateC2BOtherOrder(updateOrder);

			if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
				logger.debug("[C2B交易通知] 更新二维码失效信息失败");
				txnSta = StringConstans.RespCode.RESP_CODE_03;
			}
			// logger.info("[C2B交易通知] 开始更新银标二维码订单信息完成");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("OrigPayPlatSeqNbr", orderNumber);
			map.put("OrigPayPlatDate", orderTime.substring(0, 8));
			map.put("TransStatus", txnSta);
			map.put("TransAmt", TransAmt);
			map.put("OrigMessage", origRespMsg);
			// logger.debug("[C2B交易通知] 调用通知网络支付平台 请求报文：" + map.toString());
			InputParam mobileInputParam = new InputParam();

			mobileInputParam.putMap(map);

			// logger.info("[C2B交易通知] 调用通知网络支付平台 开始");

			String recvStr = this.notifyPayPlatformResult(mobileInputParam);

			// logger.info("[C2B交易通知] 调用通知网络支付平台 结束");
			// 判断网络支付平台传来的参数值
			if (StringUtils.isBlank(recvStr)) {
				// logger.error("[C2B交易通知] 返回报文为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "返回报文信息错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				return outputParam;
			}

			// 将JSON格式转换为Map
			Map<String, Object> paramMap = (Map<String, Object>) JsonUtil.json2Bean(recvStr, Map.class);
			// 返回状态码
			String respCode = String.format("%s", paramMap.get("RespCode"));
			// 返回信息
			String respMessage = String.format("%s", paramMap.get("RespMessage"));

			if (!"00000000".equals(respCode)) {
				logger.debug("[C2B附加请求处理] 返回支付结果失败,原因:" + respMessage);
			}

		} catch (Exception e) {
			logger.error("[C2B交易通知]  C2B交易通知异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "C2B交易通知异常");
		} finally {
			logger.info("[C2B交易通知] 方法结束  返回信息" + outputParam.toString());
		}
		// logger.info("[C2B交易通知] END");

		return outputParam;
	}

	/**
	 * 二维码被扫消费交易（本行终端扫他行或本行银标二维码）
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam C2BEWMScanedConsume(InputParam intPutParam) throws FrameException {
		logger.info("[银标二维码] 他行二维码被扫消费交易  请求报文" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("areaInfo"); // 地区信息
			validateParamList.add("qrNo");
			validateParamList.add("merId");
			validateParamList.add("termId");
			validateParamList.add("merName");
			validateParamList.add("merCatCode");
			validateParamList.add("orderTime");
			validateParamList.add("orderNo");
			validateParamList.add("merAcctNo");

			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 校验串码
			String qrNo = String.format("%s", intPutParam.getValue("qrNo"));
			logger.debug("[银标二维码 他行二维码被扫消费交易] codeUrl:[" + qrNo + "]");
			if (!qrNo.startsWith(StringConstans.CupsEwmInfo.CUPS_EWM_PREFIX)) {
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "非银联二维码");
				return outputParam;
			}

			/*
			 * 根据二维码判断是本行二维码还是他行二维码 判断依据：二维码以62开头（银联二维码），并且在数据库中存在，说明是本行二维码，否则是他行二维码
			 * 如果是本行银联二维码，采用本行扣款模式，他行银联二维码，将交易上送至银联。
			 */
			InputParam queryInput = new InputParam();
			queryInput.putparamString("ewmData", qrNo);

			OutputParam queryParam = qrCodeService.queryQRCodeInfo(queryInput);

			// 如果在数据库中存在，说明是本行银联二维码，走本行支付
			if (queryParam.getReturnCode().equals(StringConstans.returnCode.SUCCESS)) {
				outputParam = this.pospMicroConsume(intPutParam);
				return outputParam;
			}

			// 校验金额
			String orderAmount = String.format("%s", intPutParam.getValue("txnAmt"));
			logger.debug("[银标二维码 他行二维码被扫消费交易] orderAmount:[" + orderAmount + "]");

			/*
			 * //验证金额长度 if(!orderAmount.matches("\\d{12}")){
			 * logger.error("[银标二维码 他行二维码被扫消费交易] 交易金额长度不正确");
			 * outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			 * outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			 * outputParam.putValue("respDesc", "交易金额长度不正确"); return outputParam; }
			 */

			// 验证金额大小
			BigDecimal orderAmt = new BigDecimal(orderAmount);
			if (orderAmt.compareTo(new BigDecimal("0")) <= 0) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 交易金额不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易金额不正确");
				return outputParam;

			}

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));
			// logger.info("[银标二维码 他行二维码被扫消费交易] channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel))) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确");
				return outputParam;
			}

			// 校验交易类型
			// String transType = String.format("%s", intPutParam.getValue("transType"));
			String transType = "01"; // 9001不上送交易类型，暂时写死
			// logger.info("[银标二维码 他行二维码被扫消费交易] transType:[" + transType + "]");
			if (!(StringConstans.TransType.TRANS_CONSUME.equals(transType))) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 交易类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易类型不正确");
				return outputParam;
			}

			// 校验接入类型
			String payAccessType = String.format("%s", intPutParam.getValue("payAccessType"));
			// logger.info("[银标二维码 他行二维码被扫消费交易] payAccessType:[" + payAccessType + "]");
			if (!(StringConstans.PAYACCESSTYPE.ACCESS_CUPS.equals(payAccessType))) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 支付接入不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "支付接入不正确");
				return outputParam;
			}

			/*
			 * //校验支付类型 String payType = String.format("%s",
			 * intPutParam.getValue("payType")); logger.info("[银标二维码 他行二维码被扫消费交易] payType:["
			 * + payType + "]");
			 * if(!(StringConstans.Pay_Type.PAY_TYPE_MICRO.equals(payType))){
			 * logger.error("[银标二维码 他行二维码被扫消费交易] 支付类型不正确");
			 * outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			 * outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			 * outputParam.putValue("respDesc", "支付类型不正确"); return outputParam; }
			 */

			// 校验币种类型
			String currencyType = String.format("%s", intPutParam.getValue("currencyCode"));
			// logger.info("[银标二维码 他行二维码被扫消费交易] currencyType:[" + currencyType + "]");
			if (!(StringConstans.SETTLE_CURRENTY_TYPE.equals(currencyType))) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 币种类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "币种型不正确");
				return outputParam;
			}

			// 商户订单时间
			String orderTime = String.format("%s", intPutParam.getValue("orderTime"));
			// logger.info("[银标二维码 他行二维码被扫消费交易] orderTime:[" + orderTime + "]");
			if (!orderTime.matches("\\d{14}")) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 商户订单时间长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "商户订单时间长度不正确");
				return outputParam;
			}

			// 商户号
			String merId = String.format("%s", intPutParam.getValue("merId"));
			// 商户名称
			String merName = String.format("%s", intPutParam.getValue("merName"));
			// 商户订单号
			String orderNumber = String.format("%s", intPutParam.getValue("orderNo"));
			// 商户类别
			String merCatCode = String.format("%s", intPutParam.getValue("merCatCode"));
			// 订单日期
			String merOrDt = orderTime.substring(0, 8);
			// 订单时间
			String merOrTm = orderTime.substring(8);
			// 二维码串
			String qrCodecipherText = String.format("%s", intPutParam.getValue("qrNo"));
			// 终端号
			String termId = String.format("%s", intPutParam.getValue("termId"));
			// 地区编号
			String areaInfo = String.format("%s", intPutParam.getValue("areaInfo"));

			outputParam.putValue("orderTime", orderTime);
			outputParam.putValue("orderNo", orderNumber);

			InputParam input = new InputParam();
			input.putparamString("merId", merId);
			input.putparamString("merOrderId", orderNumber);
			input.putparamString("merOrDt", merOrDt);
			input.putparamString("merOrTm", merOrTm);

			// logger.info("[银标二维码 他行二维码被扫消费交易] 开始查询订单信息");

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOrder(input);

			// logger.info("[银标二维码 他行二维码被扫消费交易] 查询订单信息完成");

			// 订单存在
			if (StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 该订单已存在，请勿重复提交");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该订单已存在，请勿重复提交");
				return outputParam;
			}

			// logger.info("----------[银标二维码 他行二维码被扫消费交易] 查询订单信息成功----------------------");

			// 获取内部流水号
			String txnSeqId = orderService.getOrderNo(channel);

			// 内部交易日期
			String txnDt = DateUtil.getDateYYYYMMDD();
			// 内部交易时间
			String txnTm = DateUtil.getDateHHMMSS();
			// 新增订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("qrNo", qrCodecipherText);
			orderInput.putparamString("merId", merId);
			orderInput.putparamString("merOrderId", orderNumber);
			orderInput.putparamString("merOrDt", merOrDt);
			orderInput.putparamString("merOrTm", merOrTm);
			orderInput.putparamString("merCatCode", merCatCode);// 商户类别
			orderInput.putparamString("txnType", transType);
			orderInput.putparamString("txnChannel", channel);
			orderInput.putparamString("areaInfo", areaInfo);
			orderInput.putparamString("termId", termId);
			orderInput.putparamString("payType", "01");
			orderInput.putparamString("payAccessType", payAccessType);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("currencyCode", currencyType);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "订单初始化");

			logger.info("[银标二维码 他行二维码被扫消费交易] 保存订单  请求信息" + orderInput.toString());

			// 保存订单
			OutputParam saveOrderOutput = orderService.insertC2BOrder(orderInput);

			// logger.info("[银标二维码 他行二维码被扫消费交易] 保存订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 保存订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[银标二维码 他行二维码被扫消费交易] 保存订单信息成功------------");

			// logger.info("--------------[银标二维码 他行二维码被扫消费交易] 组装消费请求报文——START------------");
			Map<String, String> contentData = new HashMap<String, String>();
			contentData.put("version", StringConstans.CupsEwmInfo.CUPS_EWM_VERSION); // 版本号
			contentData.put("reqType", StringConstans.CupsTradeType.OTEHR_EWM_SCANED_TYPE);// 交易类型
			// contentData.put("acqCode", Constants.getParam("acqCode"));//受理机构代码
			// contentData.put("acqCode", "14293310");//受理机构代码
			contentData.put("acqCode", Constants.getParam("consumeAcqCode"));// 受理机构代码
			contentData.put("merId", merId);// 商户代码
			contentData.put("merCatCode", merCatCode);// 商户类别
			contentData.put("merName", merName);// 商户名称
			contentData.put("termId", termId);// 终端号
			contentData.put("qrNo", qrCodecipherText);// C2B码
			contentData.put("currencyCode", StringConstans.CurrencyCode.CNY);// 交易币种
			contentData.put("txnAmt", orderAmount);// 交易金额
			contentData.put("orderNo", orderNumber);// 订单号
			contentData.put("orderTime", orderTime);// 订单时间
			contentData.put("backUrl", Constants.getParam("resevCupsNotifyUrl"));// 交易通知地址
			contentData.put("areaInfo", areaInfo);// 地区信息

			// logger.info("[银标二维码 他行二维码被扫消费交易] 开始组装下单报文");
			logger.info("[银标二维码 他行二维码被扫消费交易] 请求银联报文" + contentData.toString());

			// 组装统一下单报文对象
			Map<String, String> reqData = Signature.sign(contentData, "UTF-8"); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

			// logger.info("[银标二维码 他行二维码被扫消费交易] 组装请求报文--END");

			// logger.info("[银标二维码 他行二维码被扫消费交易] C2B消费请求--START");

			String requestUrl = Constants.getParam("qrcB2cMerBackTransUrl");

			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, "UTF-8"); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

			// logger.info("[银标二维码 他行二维码被扫消费交易] C2B消费请求--END");

			String respDes = rspData.get("respMsg");
			// 默认下单失败
			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("txnDt", txnDt);
			updateInput.putparamString("txnTm", txnTm);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_03);
			updateInput.putparamString("resDesc", "银联二维码被扫下单失败:" + respDes);

			outputParam.putValue("respDesc", "银联二维码被扫下单失败");
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);

			// 判断是否收到应答
			if (!rspData.isEmpty()) {
				logger.info("rspData" + rspData);
				if (Signature.validate(rspData, "UTF-8")) {
					// logger.info("[银标二维码 他行二维码被扫消费交易] 验证签名成功");
					String respCode = rspData.get("respCode");
					String respMsg = rspData.get("respMsg");
					logger.info("[银标二维码 他行二维码被扫消费交易] 银联返回响应信息: " + rspData.toString());
					if ("00".equals(respCode)) {
						outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_02);
						outputParam.setReturnMsg("银标二维码 他行二维码被扫消费交易成功");
						updateInput.putparamString("resDesc", "银联二维码被扫下单成功");
						updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_06);
						outputParam.putValue("respDesc", "银联二维码被扫下单处理中");
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_01);

						// 开始轮询该笔订单交易状态
						/*
						 * logger.info("[银标二维码 他行二维码被扫消费交易]下单成功   开始轮询该笔订单状态--"+qrCodecipherText);
						 * OutputParam orderOutput = new OutputParam();
						 * orderInput.putparamString("qrNo", qrCodecipherText);
						 * orderOutput=orderQueryManager.c2bMicroOrderQuery(orderInput, orderOutput,
						 * 2000);
						 * 
						 * String txnSta = String.format("%s", orderOutput.getValue("txnSta")); String
						 * resDesc = String.format("%s", orderOutput.getValue("resDesc"));
						 * 
						 * outputParam.putValue("resCode", txnSta); outputParam.putValue("resDesc",
						 * resDesc); logger.info("[银标二维码 他行二维码被扫消费交易]  轮询结束--"+qrCodecipherText);
						 * //轮询该笔订单交易状态结束 返回结果 return outputParam;
						 */
					} else {
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue("respDesc", respMsg);
					}
				} else {
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "报文签名验证不通过");
					return outputParam;
				}
			}
			// logger.info("[银标二维码 他行二维码被扫消费交易] 被扫下单完成更新订单 开始");
			OutputParam updateOut = orderService.updateC2BOrder(updateInput);
			// logger.info("[银标二维码 他行二维码被扫消费交易] 被扫下单接口完成更新订单 结束");
			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				// logger.error("[银标二维码 他行二维码被扫消费交易] 被扫下单下单完成更新订单 失败");
				outputParam.putValue("respDesc", "银联二维码被扫下单失败,二维码前置更新订单 失败");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				return outputParam;
			}
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("isLocalBank", StringConstans.BankFlag.NO_BANK);
		} catch (Exception e) {
			logger.error("[银标二维码]  他行二维码被扫消费交易:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "他行二维码被扫消费交易异常");
		} finally {
			logger.info("[银标二维码 他行二维码被扫消费交易] 方法结束 返回报文信息：" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 本行银联二维码被扫处理
	 */
	public OutputParam pospMicroConsume(InputParam intPutParam) {
		logger.info("[本行银联二维码POSP被扫]请求报文" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();

		try {

			// 校验金额
			String orderAmount = String.format("%s", intPutParam.getValue("txnAmt"));
			logger.debug("[本行银联二维码POSP被扫] orderAmount:[" + orderAmount + "]");

			// 验证金额长度
			/*
			 * if(!orderAmount.matches("\\d{12}")){
			 * logger.error("[本行银联二维码POSP被扫] 交易金额长度不正确");
			 * outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			 * outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			 * outputParam.putValue("respDesc", "交易金额长度不正确"); return outputParam; }
			 */

			// 验证金额大小
			BigDecimal orderAmt = new BigDecimal(orderAmount);
			if (orderAmt.compareTo(new BigDecimal("0")) <= 0) {
				// logger.error("[本行银联二维码POSP被扫] 交易金额不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "交易金额不正确");
				return outputParam;

			}

			// 校验渠道编号
			String channel = String.format("%s", intPutParam.getValue("channel"));
			// logger.info("[本行银联二维码POSP被扫] channel:[" + channel + "]");
			if (!(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel))) {
				// logger.error("[本行银联二维码POSP被扫] 渠道编号不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "渠道编号不正确");
				return outputParam;
			}

			// 校验接入类型
			String payAccessType = StringConstans.PAYACCESSTYPE.ACCESS_NATIVE;
			logger.debug("[本行银联二维码POSP被扫] payAccessType:[" + payAccessType + "]");

			// 校验币种类型
			String currencyType = String.format("%s", intPutParam.getValue("currencyCode"));
			logger.debug("[本行银联二维码POSP被扫] currencyType:[" + currencyType + "]");
			if (!(StringConstans.SETTLE_CURRENTY_TYPE.equals(currencyType))) {
				// logger.error("[本行银联二维码POSP被扫] 币种类型不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "币种型不正确");
				return outputParam;
			}

			// 商户订单时间
			String orderTime = String.format("%s", intPutParam.getValue("orderTime"));
			logger.debug("[本行银联二维码POSP被扫] orderTime:[" + orderTime + "]");
			if (!orderTime.matches("\\d{14}")) {
				// logger.error("[本行银联二维码POSP被扫] 商户订单时间长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "商户订单时间长度不正确");
				return outputParam;
			}

			// 商户号
			String merId = String.format("%s", intPutParam.getValue("merId"));
			// 商户名称
			String merName = String.format("%s", intPutParam.getValue("merName"));
			// 商户清算账号
			String merAcctNo = String.format("%s", intPutParam.getValue("merAcctNo"));
			// 商户订单号
			String orderNumber = String.format("%s", intPutParam.getValue("orderNo"));
			// 订单日期
			String merOrDt = orderTime.substring(0, 8);
			// 订单时间
			String merOrTm = orderTime.substring(8);
			// 二维码串
			String qrCodecipherText = String.format("%s", intPutParam.getValue("qrNo"));

			InputParam input = new InputParam();
			input.putparamString("merId", merId);
			input.putparamString("merOrderId", orderNumber);
			input.putparamString("merOrDt", merOrDt);
			input.putparamString("merOrTm", merOrTm);

			logger.info("[本行银联二维码POSP被扫] 查询订单  请求信息" + input.toString());

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryOrder(input);

			// logger.info("[本行银联二维码POSP被扫] 查询订单信息完成");

			// 订单存在
			if (StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// logger.error("[本行银联二维码POSP被扫] 该订单已存在，请勿重复提交");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该订单已存在，请勿重复提交");
				return outputParam;
			}

			// logger.info("----------[本行银联二维码POSP被扫] 查询订单信息成功----------------------");

			// 二维码明文信息
			String qrCodeProClaimed = String.format("%s", qrCodecipherText);

			InputParam queryQRInput = new InputParam();
			queryQRInput.putparamString("ewmData", qrCodeProClaimed);

			// logger.info("[本行银联二维码POSP被扫] 开始查询二维码信息");

			// 查询二维码信息
			OutputParam queryQROutput = qrCodeService.queryQRCodeInfo(queryQRInput);

			// logger.info("[本行银联二维码POSP被扫] 查询二维码信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				// logger.error("[本行银联二维码POSP被扫] 查询二维码信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[本行银联二维码POSP被扫] 查询二维码信息成功------------");

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
				// logger.error("[本行银联二维码POSP被扫] 二维码信息已失效");
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

				logger.error("[本行银联二维码POSP被扫] 二维码已失效");

				InputParam upateQRInput = new InputParam();
				upateQRInput.putparamString("ewmData", qrCodeProClaimed);
				upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

				logger.info("[本行银联二维码POSP被扫] 更新二维码已失效信息 请求信息" + upateQRInput.toString());

				// 更新二维码信息
				OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

				// logger.info("[本行银联二维码POSP被扫] 更新二维码已失效信息完成");

				if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
					// logger.error("[本行银联二维码POSP被扫] 更新二维码失效信息失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "二维码系统异常");
					return outputParam;
				}

				// logger.info("--------------[本行银联二维码POSP被扫] 更新二维码已失效信息成功------------");

				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该二维码已失效");
				return outputParam;
			}

			// 二维码被扫以后置为失效状态
			InputParam upateQRInput = new InputParam();
			upateQRInput.putparamString("ewmData", qrCodeProClaimed);
			upateQRInput.putparamString("status", StringConstans.QRCodeStatus.DISABLE);

			// logger.info("[本行银联二维码POSP被扫] 开始更新二维码已失效信息");

			// 更新二维码信息
			OutputParam updateQROutput = qrCodeService.updateQRCodeStatus(upateQRInput);

			logger.info("[本行银联二维码POSP被扫] 更新二维码已失效信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(updateQROutput.getReturnCode()))) {
				// logger.error("[本行银联二维码POSP被扫] 更新二维码失效信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[本行银联二维码POSP被扫] 更新二维码已失效信息成功------------");

			// 获取内部流水号
			String txnSeqId = orderService.getOrderNo(channel);

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
			orderInput.putparamString("txnChannel", channel);
			orderInput.putparamString("payAccessType", payAccessType);
			orderInput.putparamString("txnType", StringConstans.TransType.TRANS_CONSUME);
			orderInput.putparamString("payType", StringConstans.Pay_Type.PAY_TYPE_MICRO);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("currencyCode", currencyType);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("resDesc", "订单初始化");

			logger.info("[本行银联二维码POSP被扫] 保存订单 请求信息" + orderInput.toString());

			// 保存订单
			OutputParam saveOrderOutput = orderService.insertOrder(orderInput);

			// logger.info("[本行银联二维码POSP被扫] 保存订单信息完成");

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				// logger.error("[本行银联二维码POSP被扫] 保存订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "二维码系统异常");
				return outputParam;
			}

			// logger.info("--------------[本行银联二维码POSP被扫] 保存订单信息成功------------");

			InputParam mobileInputParam = new InputParam();

			// 丰收互联需要的字段
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("orderAmount", orderAmount);
			paramMap.put("merAcctNo", merAcctNo);
			paramMap.put("merName", merName);
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

			// logger.info("[本行银联二维码POSP被扫] 调用通知支付方法 开始 paramMap："+paramMap.toString());

			OutputParam mobileOutput = localBankService.notifyPay(mobileInputParam);
			// OutputParam mobileOutput = this.notifyPay(mobileInputParam);

			logger.info("[本行银联二维码POSP被扫] 调用通知支付方法  结束:返回参数" + mobileOutput.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(mobileOutput.getReturnCode())) {
				// logger.error("[本行银联二维码POSP被扫] 通知支付返回失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", mobileOutput.getValue("respCode"));
				outputParam.putValue("respDesc", mobileOutput.getValue("respDesc"));
				return outputParam;
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("isLocalBank", StringConstans.BankFlag.IS_BANK);
			// outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.putValue("orderNo", orderNumber); // posp订单号
			// outputParam.putValue("orderTime", txnTime);
			outputParam.putValue("orderTime", orderTime); // posp订单时间
			outputParam.putValue("respCode", mobileOutput.getValue("respCode"));
			outputParam.putValue("respDesc", mobileOutput.getValue("respDesc"));

		} catch (Exception e) {
			logger.error("[本行银联二维码POSP被扫] 出现异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "二维码系统异常");
		} finally {
			logger.info("[本行银联二维码POSP被扫] 支付方法  结束:返回参数" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 通知丰收互联去支付
	 * 
	 * @param inputParam
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public OutputParam notifyPay(InputParam inputParam) {

		logger.info("------------  开始通知支付    START  ----------");

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
			// 商户清算账号
			String merAcctNo = inputParam.getParamString().get("merAcctNo");

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

			String reviceStr = null;

			reviceStr = this.notifyMobileFontPay(input);

			logger.info("[开始通知支付] 接收手返回的json数据:" + reviceStr);

			// 判断手机银行传过来的参数值
			if (StringUtils.isBlank(reviceStr)) {
				logger.error("[开始通知支付] 返回支付报文为空");
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "返回支付报文信息错误");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				return outPut;
			}

			// 将JSON格式转换为Map
			Map<String, Object> paramMap = (Map<String, Object>) JsonUtil.json2Bean(reviceStr, Map.class);
			// 订单状态
			String respCode = String.format("%s", paramMap.get("respCode"));
			// 订单状态描述
			String respDesc = String.format("%s", paramMap.get("respMsg"))
					+ String.format("%s", paramMap.get("respDesc"));

			paramMap.put("txnSeqId", txnSeqId);
			paramMap.put("txnTime", txnTime);

			// 校验字段列表
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("txnSeqId");
			validateParamList.add("txnTime");

			// 校验参数是否为空
			String nullStr = Util.validateIsNull(validateParamList, paramMap);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.error("[开始通知支付] 返回报文字段[" + nullStr + "]不能为空");
				outPut.setReturnCode(StringConstans.returnCode.FAIL);
				outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outPut.putValue("respDesc", "返回支付报文信息有误");
				return outPut;
			}

			// 获取手机银行返回信息
			InputParam upateOrderParam = new InputParam();
			String txnTimereturn = ObjectUtils.toString(paramMap.get("txnTime"));
			String mobileOrTime = ObjectUtils.toString(paramMap.get("respTime"));

			upateOrderParam.putparamString("txnSta", ObjectUtils.toString(paramMap.get("respCode")));
			upateOrderParam.putparamString("resDesc", ObjectUtils.toString(paramMap.get("respDesc")));

			upateOrderParam.putparamString("txnSeqId", ObjectUtils.toString(paramMap.get("txnSeqId")));
			upateOrderParam.putparamString("mobileOrderId", ObjectUtils.toString(paramMap.get("respSeqId")));
			if (!StringUtil.isEmpty(txnTimereturn)) {
				upateOrderParam.putparamString("txnDt", txnTimereturn.substring(0, 8));
				upateOrderParam.putparamString("txnTm", txnTimereturn.substring(8, 14));
			}
			if (!StringUtil.isEmpty(mobileOrTime)) {
				upateOrderParam.putparamString("mobileOrDt", mobileOrTime.substring(0, 8));
				upateOrderParam.putparamString("mobileOrTm", mobileOrTime.substring(8, 14));
			}
			logger.info("[开始通知支付] 通知同步响应后更新订单信息  开始");

			OutputParam updateOrderStateOut = orderService.updateOrderState(upateOrderParam);

			logger.info("[开始通知支付] 通知同步响应后更新订单信息  结束");

			// 更新订单失败
			if (!StringConstans.returnCode.SUCCESS.equals(updateOrderStateOut.getReturnCode())) {
				logger.error("===================[开始通知支付] 更新订单失败======================");
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
			logger.info("================" + outPut.getReturnCode());

		} catch (Exception e) {
			logger.error("请求丰收互联信息异常:" + e.getMessage(), e);
			if (e instanceof FrameException) {
				outPut.putValue("respDesc", e.getMessage());
			} else {
				outPut.putValue("respDesc", "二维码前置异常");
			}
			outPut.setReturnMsg(e.getMessage());
			outPut.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outPut.setReturnCode(StringConstans.returnCode.FAIL);
		}

		return outPut;
	}

	/**
	 * C2B消费结果通知处理
	 */
	@Override
	public OutputParam C2BEWMConsumeResultNotifyHandler(InputParam intPutParam) throws FrameException {
		logger.info("[银标二维码  C2B消费结果通知] 方法开始  请求信息" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			Map<String, String> rspData = intPutParam.getParamString();
			// logger.debug("[银标二维码 C2B消费结果通知]收到的信息:" + rspData.toString());
			String respCode = rspData.get("respCode"); // 应答码
			String respMsg = rspData.get("respMsg"); // 响应信息
			String orderNo = rspData.get("orderNo"); // 订单号
			String orderTime = rspData.get("orderTime"); // 订单时间
			String merOrDt = orderTime.substring(0, 8); // 交易日期
			String merOrTm = orderTime.substring(8); // 交易时间

			InputParam input = new InputParam();
			input.putparamString("merOrderId", orderNo);
			input.putparamString("merOrDt", merOrDt);
			input.putparamString("merOrTm", merOrTm);

			logger.info("[银标二维码  C2B消费结果通知] 查询  请求信息" + input.toString());

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOrder(input);

			// logger.info("[银标二维码 C2B消费结果通知] 查询订单信息完成");

			// 订单不存在
			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// logger.error("[银标二维码 C2B消费结果通知] 订单不存在");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "订单不存在");
				return outputParam;
			}

			InputParam updateInput = new InputParam();
			updateInput.putparamString("merOrderId", orderNo);
			updateInput.putparamString("merOrDt", merOrDt);
			updateInput.putparamString("merOrTm", merOrTm);
			String txnSta = "";
			if ("00".equals(respCode)) {
				txnSta = StringConstans.OrderState.STATE_02;

				String settleDa = rspData.get("settleDate");
				String settleDate = DateUtil.getYear(settleDa) + settleDa;
				String settleKey = rspData.get("settleKey");
				String voucherNum = rspData.get("voucherNum");

				updateInput.putparamString("settleKey", settleKey);
				updateInput.putparamString("settleDate", settleDate);
				updateInput.putparamString("voucherNum", voucherNum);

				// 处理payerInfo
				if (!StringUtil.isEmpty(rspData.get("payerInfo"))
						&& !StringUtil.isEmpty(rspData.get("encryptCertId"))) {
					String payerInfoEn = rspData.get("payerInfo");
					String payerInfo = AcpService.decryptData(payerInfoEn, "UTF-8");
					// logger.info("[银联二维码通知]payerInfo:"+payerInfo);
					payerInfo = payerInfo.substring(payerInfo.indexOf("{"), payerInfo.indexOf("}") + 1);
					logger.debug("[银联二维码通知]payerInfo去乱码后:" + payerInfo);
					String payerInfoRep = payerInfo.replaceAll("&", ",");
					Map<String, String> map = getMapFromStr(payerInfoRep);
					// logger.info("[银联二维码被扫查询]转换后map:"+map);

					String accNo = map.get("accNo");
					String cardAttr = map.get("cardAttr");
					String issCode = map.get("issCode");

					updateInput.putparamString("accNo", accNo);
					updateInput.putparamString("cardAttr", cardAttr);
					updateInput.putparamString("issCode", issCode);

				}
				/*
				 * //付款方信息 if(!StringUtil.isEmpty(rspData.get("couponInfo"))){ //base64解密 String
				 * couponInfo = Base64.decode(rspData.get("couponInfo")); String couponStr =
				 * couponInfo.substring(1, couponInfo.length()-1); Map<String,Object> mapJson =
				 * JsonUtil.parseJSON2Map(couponStr, 2000); outputParam.putValue("chukeMoney",
				 * mapJson.get("offstAmt").toString()); }//营销优惠信息,type项目类型,spnsrId出资方。目前取值只允许为
				 * 00010000，即仅支 持银联作为出资方，未来将增加付款方 等出资方 抵
				 */

			} else {
				txnSta = StringConstans.OrderState.STATE_03;
			}
			updateInput.putparamString("txnSta", txnSta);
			updateInput.putparamString("resDesc", respMsg);
			/* 只有在明确知道交易成功或失败的情况下才更新表 */
			if (!"04".equals(respCode) && !"06".equals(respCode)) {
				// 更新表状态
				logger.info("[银标二维码  C2B消费结果通知] 更新订单   请求信息" + updateInput.toString());
				OutputParam updateOut = orderService.updateC2BOrder(updateInput);
				// logger.info("[银标二维码 C2B消费结果通知] 更新订单 结束");
				if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
					// logger.error("[银标二维码 C2B消费结果通知] 更新订单失败");
					outputParam.putValue("respDesc", "C2B消费结果通知 更新订单失败");
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					return outputParam;
				}
			}
		} catch (Exception e) {
			logger.error("[银标二维码]  C2B消费结果通知异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "C2B消费结果通知异常");
		} finally {
			logger.error("[银标二维码  C2B消费结果通知] 返回信息" + outputParam.toString());
		}
		return outputParam;
	}

	/**
	 * 银联二维码被扫交易查询
	 */
	@Override
	public OutputParam C2BEWMScanedConsumeQuery(InputParam intPutParam) throws FrameException {
		logger.info("[银联二维码被扫交易查询] C2BEWMScanedConsumeQuery START" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add("orderTime");
			validateParamList.add("orderNo");
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				// logger.error("[银标二维码 他行二维码被扫消费查询交易] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 商户订单时间
			String orderTime = String.format("%s", intPutParam.getValue("orderTime"));
			logger.debug("[银标二维码 他行二维码被扫消费查询交易] orderTime:[" + orderTime + "]");
			if (!orderTime.matches("\\d{14}")) {
				// logger.error("[银标二维码 他行二维码被扫查询消费交易] 商户订单时间长度不正确");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "商户订单时间长度不正确");
				return outputParam;
			}
			// 订单号
			String orderNo = String.format("%s", intPutParam.getValue("orderNo"));
			logger.debug("[银标二维码 他行二维码被扫消费查询交易] 订单号orderNo:[" + orderNo + "]");

			String txnDt = orderTime.substring(0, 8);
			String txnTm = orderTime.substring(8);

			InputParam input = new InputParam();
			input.putparamString("merOrderId", orderNo);
			input.putparamString("merOrDt", txnDt);
			input.putparamString("merOrTm", txnTm);

			logger.info("[银标二维码 他行二维码被扫消费查询交易] 查询 请求报文" + input.toString());

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOrder(input);

			// logger.info("[银标二维码 他行二维码被扫消费查询交易] 查询订单信息完成");

			// 订单不存在
			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// logger.error("[银标二维码 他行二维码被扫消费查询交易] 该订单不存在");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "该订单不存在");
				return outputParam;
			}

			String txnSeqId = String.format("%s", queryOrderOutput.getValue("txnSeqId"));

			String qrNo = String.format("%s", queryOrderOutput.getValue("qrNo"));

			// logger.info("[银标二维码 他行二维码被扫消费查询交易] 开始组装下单报文");

			Map<String, String> contentData = new HashMap<String, String>();

			contentData.put("version", StringConstans.CupsEwmInfo.CUPS_EWM_VERSION); // 版本号
			contentData.put("reqType", StringConstans.CupsTradeType.C2B_CONSUME_QUERY);// 交易类型
			contentData.put("acqCode", Constants.getParam("consumeAcqCode"));// 受理机构代码,后续需要写到配置文件中

			contentData.put("orderNo", orderNo); // 获取二维码的订单号
			contentData.put("orderTime", orderTime);// 获取二维码的订单时间
			contentData.put("qrNo", qrNo);

			logger.info("[银标二维码 他行二维码被扫消费查询交易] 请求银联报文" + contentData.toString());
			Map<String, String> reqData = Signature.sign(contentData, "UTF-8"); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
			String requestUrl = Constants.getParam("qrcBackTransUrl");
			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, "UTF-8"); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

			String txnSta = "";
			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("merOrDt", txnDt);
			updateInput.putparamString("merOrTm", txnTm);
			if (!rspData.isEmpty()) {
				logger.info("[银标二维码 他行二维码被扫消费查询交易] 收到银联应答" + rspData.toString());
				if (Signature.validate(rspData, "UTF-8")) {
					// logger.info("[银标二维码 他行二维码被扫消费查询交易]银联返回 验证签名成功");
					String respCode = rspData.get("respCode"); // 应答码
					String respMsg = rspData.get("respMsg");
					if ("00".equals(respCode)) {
						String origRespCode = rspData.get("origRespCode"); // 原交易应答码
						String origRespMsg = rspData.get("origRespMsg"); // 原交易响应信息
						if ("00".equals(origRespCode)) {
							txnSta = StringConstans.OrderState.STATE_02;

							String settleDa = rspData.get("settleDate");
							String settleDate = DateUtil.getYear(settleDa) + settleDa;
							String settleKey = rspData.get("settleKey");
							String voucherNum = rspData.get("voucherNum");

							outputParam.putValue("txnSta", txnSta);
							outputParam.putValue("settleKey", settleKey);
							outputParam.putValue("settleDate", settleDate);
							outputParam.putValue("voucherNum", voucherNum);

							updateInput.putparamString("settleKey", settleKey);
							updateInput.putparamString("settleDate", settleDate);
							updateInput.putparamString("voucherNum", voucherNum);

							// 处理payerInfo
							if (!StringUtil.isEmpty(rspData.get("payerInfo"))
									&& !StringUtil.isEmpty(rspData.get("encryptCertId"))) {

								String payerInfoEn = rspData.get("payerInfo");
								String payerInfo = AcpService.decryptData(payerInfoEn, "UTF-8");
								// logger.info("[银联二维码被扫查询]payerInfo:"+payerInfo);
								payerInfo = payerInfo.substring(payerInfo.indexOf("{"), payerInfo.indexOf("}") + 1);
								logger.debug("[银联二维码被扫查询]payerInfo去乱码后:" + payerInfo);
								String payerInfoRep = payerInfo.replaceAll("&", ",");
								Map<String, String> map = getMapFromStr(payerInfoRep);
								// logger.info("[银联二维码被扫查询]转换后map:"+map);

								String accNo = map.get("accNo");
								String cardAttr = map.get("cardAttr");
								String issCode = map.get("issCode");

								outputParam.putValue("accNo", accNo);
								outputParam.putValue("cardAttr", cardAttr);
								outputParam.putValue("issCode", issCode);

								updateInput.putparamString("accNo", accNo);
								updateInput.putparamString("cardAttr", cardAttr);
								updateInput.putparamString("issCode", issCode);
							}
							/*
							 * //付款方信息 if(!StringUtil.isEmpty(rspData.get("couponInfo"))){ //base64解密 String
							 * couponInfo = Base64.decode(rspData.get("couponInfo")); String couponStr =
							 * couponInfo.substring(1, couponInfo.length()-1); Map<String,Object> mapJson =
							 * JsonUtil.parseJSON2Map(couponStr, 2000); outputParam.putValue("chukeMoney",
							 * mapJson.get("offstAmt").toString()); }//营销优惠信息,type项目类型,spnsrId出资方。目前取值只允许为
							 * 00010000，即仅支 持银联作为出资方，未来将增加付款方 等出资方 抵
							 */
							updateInput.putparamString("txnSta", txnSta);
							updateInput.putparamString("resDesc", "交易成功");
							outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
							outputParam.putValue("respDesc", "支付成功");
						} else {
							// logger.error("[银联二维码被扫订单查询]"+"银联二维码支付原交易状态异常");
							outputParam.setReturnCode(StringConstans.returnCode.FAIL);
							outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
							outputParam.putValue("respDesc", "银联二维码支付原交易状态异常" + origRespMsg);
							txnSta = StringConstans.OrderState.STATE_03;
							updateInput.putparamString("txnSta", txnSta);
							updateInput.putparamString("resDesc", origRespMsg);
						}
						/* 只有在明确知道原交易（消费）成功或失败的情况下才更新表 */
						if (!"04".equals(origRespCode) && !"06".equals(origRespCode)) {
							// 更新表状态
							logger.info("[[银标二维码 他行二维码被扫消费查询交易]] 更新订单   请求信息" + updateInput.toString());
							OutputParam updateOut = orderService.updateC2BOrder(updateInput);
							// logger.info("[银联二维码被扫订单查询] 更新订单 结束");
							if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
								// logger.error("[银联二维码被扫订单查询] 更新订单失败");
								outputParam.putValue("respDesc", "银联二维码被扫订单查询 更新订单失败");
								outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
								return outputParam;
							}
						} else {
							outputParam.putValue("respDesc", "银联二维码被扫订单查询    交易正在处理中");
							outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_01);
						}
					} else {
						logger.error("[银联二维码被扫订单查询]查询失败");
						outputParam.setReturnCode(StringConstans.returnCode.FAIL);
						outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue("respDesc", respMsg);
					}

				} else {
					logger.debug("[银联二维码被扫订单查询]接收的报文签名验证不通过");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "接收的报文签名验证不通过");
				}
			}
		} catch (Exception e) {
			logger.error("[银联二维码被扫订单查询]  他行二维码被扫消费查询交易异常:" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "他行二维码被扫消费查询交易异常");
		} finally {
			logger.info("[银联二维码被扫订单查询] 方法结束 返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 银联二维码被扫消费冲正
	 */
	@Override
	public OutputParam C2BEWMScanedConsumeReverse(InputParam intPutParam) throws FrameException {
		logger.info("[银联二维码被扫消费冲正] C2BEWMScanedConsumeReverse START " + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add(Dict.orderTime);
			validateParamList.add(Dict.orderNo);
			validateParamList.add(Dict.initOrderTime);
			validateParamList.add(Dict.initOrderNo);
			validateParamList.add(Dict.merId);

			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正] 请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			/*
			 * //校验串码 String qrNo = String.format("%s", intPutParam.getValue("qrNo"));
			 * logger.info("[银标二维码 被扫消费冲正] qrNo:[" + qrNo + "]");
			 * if(!qrNo.startsWith(StringConstans.CupsEwmInfo.CUPS_EWM_PREFIX)){
			 * outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			 * outputParam.putValue("respDesc", "非银联二维码"); return outputParam; }
			 */

			String orderTime = StringUtil.toString(intPutParam.getValue(Dict.orderTime));
			if (!orderTime.matches("\\d{14}")) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]商户订单时间长度不正确" + orderTime);
				return outputParam;
			}

			String merId = StringUtil.toString(intPutParam.getValue(Dict.merId));
			String orderNumber = StringUtil.toString(intPutParam.getValue(Dict.orderNo));
			String initOrderNo = StringUtil.toString(intPutParam.getValue(Dict.initOrderNo));
			String initOrderTime = StringUtil.toString(intPutParam.getValue(Dict.initOrderTime));
			String merOrDt = orderTime.substring(0, 8);
			String merOrTm = orderTime.substring(8);

			InputParam input = new InputParam();
			input.putparamString(Dict.merOrderId, initOrderNo);
			input.putparamString(Dict.merOrDt, initOrderTime.substring(0, 8));
			input.putparamString(Dict.merOrTm, initOrderTime.substring(8));

			OutputParam queryOrderOutput = orderService.queryC2BOrder(input);
			logger.info("[银联二维码被扫消费冲正] 查询订单 " + queryOrderOutput.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]原订单不存在");
				return outputParam;
			}

			String txnSta = ObjectUtils.toString(queryOrderOutput.getReturnObj().get(Dict.txnSta));
			if (StringConstans.RespCode.RESP_CODE_03.equals(txnSta)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]该笔订单明确交易失败，无法进行冲正");
				return outputParam;
			}

			// 获取内部流水号
			String txnSeqId = orderService.getOrderNo("");
			// 内部交易日期
			String txnDt = DateUtil.getDateYYYYMMDD();
			// 内部交易时间
			String txnTm = DateUtil.getDateHHMMSS();
			String termId = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.termId));
			String currencyCode = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.currencyCode));
			String merCatCode = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.merCatCode));
			String transType = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.transType));
			String channel = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.channel));
			String areaInfo = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.areaInfo));
			String payAccessType = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.payAccessType));
			String tradeMoney = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.tradeMoney));
			String qrNo = StringUtil.toString(queryOrderOutput.getReturnObj().get(Dict.qrNo));

			// 新增并保存订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString(Dict.txnSeqId, txnSeqId);
			orderInput.putparamString(Dict.txnDt, txnDt);
			orderInput.putparamString(Dict.txnTm, txnTm);
			orderInput.putparamString(Dict.qrNo, qrNo);
			orderInput.putparamString(Dict.merId, merId);
			orderInput.putparamString(Dict.merOrderId, orderNumber);
			orderInput.putparamString(Dict.merOrDt, merOrDt);
			orderInput.putparamString(Dict.merOrTm, merOrTm);
			orderInput.putparamString(Dict.oglOrdId, initOrderNo);
			orderInput.putparamString(Dict.oglOrdDate, initOrderTime);
			orderInput.putparamString(Dict.merCatCode, merCatCode);// 商户类别
			orderInput.putparamString(Dict.txnType, transType);
			orderInput.putparamString(Dict.txnChannel, channel);
			orderInput.putparamString(Dict.areaInfo, areaInfo);
			orderInput.putparamString(Dict.termId, termId);
			orderInput.putparamString(Dict.payType, "01");
			orderInput.putparamString(Dict.payAccessType, payAccessType);
			orderInput.putparamString(Dict.tradeMoney, tradeMoney);
			orderInput.putparamString(Dict.currencyCode, currencyCode);
			orderInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_01);
			orderInput.putparamString(Dict.resDesc, "[银联二维码被扫消费冲正]订单初始化");
			OutputParam saveOrderOutput = orderService.insertC2BOrder(orderInput);

			if (!(StringConstans.returnCode.SUCCESS.equals(saveOrderOutput.getReturnCode()))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]二维码系统新增被扫冲正订单异常");
				return outputParam;
			}

			Map<String, String> contentData = new HashMap<String, String>();
			contentData.put(Dict.version, StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			contentData.put(Dict.reqType, StringConstans.CupsTradeType.C2B_CONSUME_REVERDE);
			contentData.put(Dict.qrNo, qrNo);
			contentData.put(Dict.acqCode, Constants.getParam(Dict.consumeAcqCode));
			contentData.put(Dict.orderNo, initOrderNo);
			contentData.put(Dict.orderTime, initOrderTime);
			contentData.put(Dict.merId, merId);

			logger.info("[银联二维码被扫消费冲正] 请求银联报文" + contentData.toString());

			// 组装统一下单报文对象
			Map<String, String> reqData = Signature.sign(contentData, "UTF-8"); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
			String requestUrl = Constants.getParam("qrcB2cMerBackTransUrl");
			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, "UTF-8"); // 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
			logger.info("[银联二维码被扫消费冲正] 被扫消费冲正--END" + rspData.toString());

			String respDes = rspData.get(Dict.respMsg);
			// 默认下单失败
			InputParam updateInput = new InputParam();
			updateInput.putparamString(Dict.txnSeqId, txnSeqId);
			updateInput.putparamString(Dict.txnDt, txnDt);
			updateInput.putparamString(Dict.txnTm, txnTm);
			updateInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_03);
			updateInput.putparamString(Dict.resDesc, "[银联二维码被扫消费冲正]" + respDes);

			outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]银联二维码被扫冲正失败");
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);

			// 判断是否收到应答
			if (!rspData.isEmpty()) {
				if (Signature.validate(rspData, "UTF-8")) {
					String respCode = rspData.get(Dict.respCode);
					String respMsg = rspData.get(Dict.respMsg);
					if ("00".equals(respCode)) {
						String settleDate = DateUtil.getYear(rspData.get(Dict.settleDate))
								+ rspData.get(Dict.settleDate);

						updateInput.putparamString(Dict.resDesc, "[银联二维码被扫消费冲正]被扫消费冲正成功");
						updateInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_02);
						updateInput.putparamString(Dict.settleKey, rspData.get(Dict.settleKey));
						updateInput.putparamString(Dict.settleDate, settleDate);

						outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]被扫消费冲正成功");
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
						outputParam.putValue(Dict.settleKey, rspData.get(Dict.settleKey));
						outputParam.putValue(Dict.settleDate, settleDate);

					} else {
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]" + respMsg);
					}
				} else {
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]报文签名验证不通过");
					return outputParam;
				}
			}
			OutputParam updateOut = orderService.updateC2BOrder(updateInput);

			if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码被扫消费冲正]被扫消费冲正完成更新订单失败");
				return outputParam;
			}

		} catch (Exception e) {
			logger.error("[银标二维码]  被扫消费冲正:" + e.getMessage(), e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "被扫消费冲正交易异常");
		} finally {
			logger.info("[银联二维码被扫消费冲正] C2BEWMScanedConsumeReverse END " + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 通知移动前端做额度校验
	 * 
	 * @param map
	 * @return
	 */
	public String notifyMobileFontPay(InputParam inputParam) {
		logger.info("[通知移动前端做额度校验] notifyMobileFontPay START" + inputParam.toString());
		try {
			Map<String, Object> map = inputParam.getParams();
			// 商户号
			// map.put("merId", inputParam.getValueString("merId"));
			// 商户订单号
			// map.put("orderNumber", inputParam.getValueString("orderNumber"));
			// 商户订单时间
			// map.put("orderTime", inputParam.getValueString("orderTime"));

			map.put(Dict.reqCode, StringConstans.OutSystemServiceCode.REQUEST_MOBILE_FRONT);
			map.put(Dict.channel, StringConstans.OutSystemChannel.CHANNEL_MOBILE_FRONT);

			// 请求移动前端的地址
			String ip = Constants.getParam("internet_lead_ip");
			int port = Integer.valueOf(Constants.getParam("internet_lead_port"));

			String xmlStr = Util.mapToXml(map);
			String sendXmlStr = String.format("%06d%s", xmlStr.getBytes("UTF-8").length, xmlStr);
			logger.info("[通知移动前端做额度校验] 发送给移动前端的xml数据:" + sendXmlStr);

			SocketClient socket = new SocketClient(ip, port);
			String reviceXmlStr = socket.sendBytes(sendXmlStr, "UTF-8");
			logger.info("[通知移动前端做额度校验] 接收移动前端的返回的xml数据:" + reviceXmlStr);

			Map<String, Object> converMap = Util.getMapFromXML(reviceXmlStr);
			String reviceStr = JsonUtil.bean2Json(converMap);

			return reviceStr;

		} catch (Exception e) {
			logger.error("[通知移动前端做额度校验]出现异常:" + e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 支付平台查询交易状态 3003
	 */
	@Override
	public OutputParam payCenterQuery(InputParam intPutParam) throws FrameException {
		logger.info("[支付平台查询交易状态 ] payCenterQuery START" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();

		try {
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add(Dict.orderNumber);
			validateParamList.add(Dict.orderTime);
			validateParamList.add(Dict.txnSeqId);
			validateParamList.add(Dict.txnTime);

			// 校验字段
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付平台查询交易状态 ]请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
			String txnTime = StringUtil.toString(intPutParam.getValue(Dict.txnTime));
			if (!txnTime.matches("\\d{14}")) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付平台查询交易状态 ]商户订单时间长度不正确" + "orderTime:[" + txnTime + "]");
				return outputParam;
			}
			String txnDt = txnTime.substring(0, 8);
			String txnTm = txnTime.substring(8);
			// 交易流水
			String txnSeqId = String.format("%s", intPutParam.getValue(Dict.txnSeqId));
			InputParam queryParam = new InputParam();
			queryParam.putparamString(Dict.txnSeqId, txnSeqId);
			queryParam.putparamString(Dict.txnDt, txnDt);
			queryParam.putparamString(Dict.txnTm, txnTm);

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOtherOrder(queryParam);
			logger.info("支付平台查询交易状态  查询订单  请求信息" + queryOrderOutput.toString());

			// 订单存在
			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// 订单状态为成功或失败
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付平台查询交易状态 ]" + txnSeqId + "该订单不存在");
				return outputParam;
			}
			// 获取查询记录
			Map<String, Object> queryQRMap = queryOrderOutput.getReturnObj();
			String txnSta = String.format("%s", queryQRMap.get(Dict.txnSta));
			String orderAmount = String.format("%s", queryQRMap.get(Dict.tradeMoney));

			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue(Dict.respDesc, "[支付平台查询交易状态 ]" + txnSeqId + "查询成功");
			outputParam.putValue(Dict.txnSta, txnSta);
			outputParam.putValue(Dict.orderAmount, orderAmount);

		} catch (Exception e) {
			logger.error("[支付平台查询交易状态]  查询异常:" + e.getMessage());
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "[支付平台查询交易状态 ]支付平台查询交易状态查询异常" + e.getMessage());
		} finally {
			logger.info("[支付平台查询交易状态 ] payCenterQuery END" + outputParam.toString());
		}
		return outputParam;
	}
	
	/**
	 * 支付平台查询银联主扫订单交易状态 3003
	 */
	@Override
	public OutputParam payCenterQueryZS(InputParam intPutParam) throws FrameException {
		logger.info("[支付平台查询银联主扫订单交易状态 ] payCenterQueryZS START" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();

		try {
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add(Dict.orderNumber);
			validateParamList.add(Dict.orderTime);
			validateParamList.add(Dict.txnSeqId);
			validateParamList.add(Dict.txnTime);

			// 校验字段
			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}
			String txnTime = StringUtil.toString(intPutParam.getValue(Dict.txnTime));
			if (!txnTime.matches("\\d{14}")) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]商户订单时间长度不正确" + "orderTime:[" + txnTime + "]");
				return outputParam;
			}
			String txnDt = txnTime.substring(0, 8);
			String txnTm = txnTime.substring(8);
			// 交易流水
			String txnSeqId = StringUtil.toString(intPutParam.getValue(Dict.txnSeqId));
			InputParam queryParam = new InputParam();
			queryParam.putparamString(Dict.txnSeqId, txnSeqId);
			queryParam.putparamString(Dict.txnDt, txnDt);
			queryParam.putparamString(Dict.txnTm, txnTm);

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOrder(queryParam);
			logger.info("支付平台查询交易状态  查询订单  请求信息" + queryOrderOutput.toString());

			// 订单存在
			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				// 订单状态为成功或失败
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]" + txnSeqId + "该订单不存在");
				return outputParam;
			}
			// 获取查询记录
			Map<String, Object> queryQRMap = queryOrderOutput.getReturnObj();
			String txnSta = StringUtil.toString(queryQRMap.get(Dict.txnSta));
			String orderAmount = StringUtil.toString(queryQRMap.get(Dict.tradeMoney));
			
			if (StringConstans.OrderState.STATE_02.equals(txnSta)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
				outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]" + txnSeqId + "查询成功");
				outputParam.putValue(Dict.txnSta, txnSta);
				outputParam.putValue(Dict.orderAmount, orderAmount);
			}else {
				Map<String, String> dataMap = new HashMap<String, String>();
				dataMap.put(Dict.version, StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
				dataMap.put(Dict.reqType, StringConstans.CupsTradeType.C2B_ZS_PAY_QUERY);
				dataMap.put(Dict.issCode, Constants.getParam(Dict.acqCode));
				dataMap.put(Dict.txnNo, StringUtil.toString(queryQRMap.get(Dict.txnNo)));
				
				Map<String, String> reqData = Signature.sign(dataMap, StringConstans.Charsets.UTF_8);
				String requestUrl = Constants.getParam("qrcB2cIssBackTransUrl");
				Map<String, String> rspData = CupsBase.post(reqData, requestUrl, StringConstans.Charsets.UTF_8);
				logger.info("[银标二维码 他行二维码主扫查询订单交易] 请求银联报文返回信息" + rspData.toString());
				
				if (!rspData.isEmpty()) {
					if (Signature.validate(rspData, "UTF-8")) {
						String respCode = rspData.get(Dict.respCode);
						String respMsg = rspData.get(Dict.respMsg);
						if ("00".equals(respCode)) {
							String origRespCode = rspData.get(Dict.origRespCode);
							String origRespMsg = rspData.get(Dict.origRespMsg);
							InputParam orderUpdata = new InputParam();
							if ("00".equals(origRespCode)) {
								outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_02);
								
								orderUpdata.putparamString(Dict.voucherNum, rspData.get(Dict.voucherNum));
								orderUpdata.putparamString(Dict.settleKey, rspData.get(Dict.settleKey));
								orderUpdata.putparamString(Dict.settleDate, rspData.get(Dict.settleDate));
							}else if("04".equals(origRespCode)){
								outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
							}else {
								outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
							}
							outputParam.putValue(Dict.orderAmount, StringConstans.OrderState.STATE_03);
							outputParam.putValue(Dict.respCode, StringUtil.toString(outputParam.getValue(Dict.txnSta)));
							outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]银联二维码查询成功："+origRespMsg);
							
							orderUpdata.putparamString(Dict.txnSta, StringUtil.toString(outputParam.getValue(Dict.txnSta)));
							orderUpdata.putparamString(Dict.resDesc, StringUtil.toString(outputParam.getValue(Dict.respDesc)));
							orderUpdata.putparamString(Dict.txnSeqId, txnSeqId);
							orderUpdata.putparamString(Dict.txnDt, txnDt);
							orderUpdata.putparamString(Dict.txnTm, txnTm);
							// 更新订单状态
							OutputParam updateOrderOutput = orderService.updateC2BOrder(orderUpdata);
						}else {
							outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
							outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]银联二维码查询失败："+respMsg);
						}
					}else {
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]报文验签不通过");
					}
				}
			}
		} catch (Exception e) {
			logger.error("[支付平台查询交易状态]  查询异常:" + e.getMessage());
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "[支付平台查询银联主扫订单交易状态 ]支付平台查询交易状态查询异常" + e.getMessage());
		} finally {
			logger.info("[支付平台查询银联主扫订单交易状态 ] payCenterQueryZS END" + outputParam.toString());
		}
		return outputParam;
	}

	/**
	 * 通知网络支付平台交易结果
	 * 
	 * @param map
	 * @return
	 */
	public String notifyPayPlatformResult(InputParam inputParam) {
		logger.info("[通知网络支付平台交易结果] notifyPayPlatformResult START" + inputParam.toString());
		try {

			Map<String, Object> map = inputParam.getParams();
			map.put(Dict.reqCode, StringConstans.OutSystemServiceCode.PAY_CENTER_NOTIFY);
			// map.put(Dict.channel, StringConstans.OutSystemChannel.CHANNEL_MOBILE_FRONT);

			// 请求互联网前置的地址
			String ip = Constants.getParam("internet_lead_ip");
			int port = Integer.valueOf(Constants.getParam("internet_lead_port"));

			String xmlStr = Util.mapToXml(map);
			String sendXmlStr = String.format("%06d%s", xmlStr.getBytes("UTF-8").length, xmlStr);
			logger.info("[通知网络支付平台交易结果] 发送给网络支付平台的xml数据:" + sendXmlStr);

			SocketClient socket = new SocketClient(ip, port);
			String reviceXmlStr = socket.sendBytes(sendXmlStr, "UTF-8");
			logger.info("[通知网络支付平台交易结果] 接收网络支付平台的返回的xml数据:" + reviceXmlStr);

			Map<String, Object> converMap = Util.getMapFromXML(reviceXmlStr);
			String reviceStr = JsonUtil.bean2Json(converMap);

			return reviceStr;

		} catch (Exception e) {
			logger.error("[通知网络支付平台交易结果]出现异常:" + e.getMessage(), e);
		}

		return null;
	}

	/**
	 * C2B银联二维码主扫查询营销交易
	 * 
	 * @param InputParam
	 * @author YYK
	 * @return OutputParam
	 */
	@Override
	public OutputParam C2BEWMSmarketQuery(InputParam input) throws FrameException {
		logger.info("[C2B银联二维码主扫查询营销交易] C2BEWMSmarketQuery START" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {

			String nullStr = Util.validateIsNull(C2BEWMValidation.vali_C2BEWMSmarkQuery, input);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易] 请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			String channel = StringUtil.toString(input.getValue(Dict.channel));
			String txnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String txnTime = StringUtil.toString(input.getValue(Dict.txnTime));
			String orderAmount = StringUtil.toString(input.getValue(Dict.orderAmount));
			String txnDt = txnTime.substring(0, 8);
			String txnTm = txnTime.substring(8, 14);

			if (!(StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易]渠道编号不正确");
				return outputParam;
			}

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.txnSeqId, txnSeqId);
			inputQuery.putparamString(Dict.txnDt, txnDt);
			inputQuery.putparamString(Dict.txnTm, txnTm);

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOrder(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOutput.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易]该订单不存在");
				return outputParam;
			}

			// 付款方信息
			Map<String, String> payerInfoMap = new HashMap<String, String>();
			payerInfoMap.put(Dict.accNo, StringUtil.toString(queryOrderOutput.getValue(Dict.accNo)));
			payerInfoMap.put(Dict.name, StringUtil.toString(queryOrderOutput.getValue(Dict.merName)));
			payerInfoMap.put(Dict.cardAttr, StringUtil.toString(queryOrderOutput.getValue(Dict.cardAttr)));
			payerInfoMap.put(Dict.mobile, StringUtil.toString(queryOrderOutput.getValue(Dict.mobile)));

			//收款方信息
			Map<String, String> payeeInfoMap = new HashMap<String, String>();
			payeeInfoMap.put(Dict.id, StringUtil.toString(queryOrderOutput.getValue(Dict.merId)));
			payeeInfoMap.put(Dict.name, StringUtil.toString(queryOrderOutput.getValue(Dict.merName)));
			payeeInfoMap.put(Dict.merCatCode, StringUtil.toString(queryOrderOutput.getValue(Dict.merCatCode)));

			// 风控信息
			Map<String, String> riskInfoMap = new HashMap<String, String>();
			riskInfoMap.put("deviceID", StringUtil.toString(queryOrderOutput.getValue(Dict.deviceId)));
			riskInfoMap.put(Dict.deviceType, StringUtil.toString(queryOrderOutput.getValue(Dict.deviceType)));
			riskInfoMap.put(Dict.mobile, StringUtil.toString(queryOrderOutput.getValue(Dict.mobile)));
			riskInfoMap.put(Dict.accountIdHash, StringUtil.toString(queryOrderOutput.getValue(Dict.accountIdHash)));
			riskInfoMap.put("sourceIP", StringUtil.toString(queryOrderOutput.getValue(Dict.sourceIp)));

			Map<String, String> contentData = new HashMap<String, String>();
			contentData.put(Dict.version, StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			contentData.put(Dict.reqType, StringConstans.CupsTradeType.C2B_MARKET_QUERY);
			contentData.put(Dict.issCode, StringUtil.toString(queryOrderOutput.getValue(Dict.issCode)));
			contentData.put(Dict.currencyCode, StringUtil.toString(queryOrderOutput.getValue(Dict.currencyCode)));
			contentData.put(Dict.txnAmt, StringUtil.toString(new BigDecimal(orderAmount)));
			contentData.put(Dict.txnNo, StringUtil.toString(queryOrderOutput.getValue(Dict.txnNo)));
			contentData.put(Dict.encryptCertId, AcpService.getEncryptCertId());
			contentData.put(Dict.payerInfo, CupsBase.getPayerInfoWithEncrpyt(payerInfoMap,StringConstans.Charsets.UTF_8));
			contentData.put(Dict.payeeInfo, CupsBase.getPayerInfoWithEncrpyt(payeeInfoMap,StringConstans.Charsets.UTF_8));
			contentData.put(Dict.riskInfo, CupsBase.getPayerInfo(riskInfoMap));

			String requestUrl = Constants.getParam("qrcB2cIssBackTransUrl");

			Map<String, String> rspData = CupsBase.post(Signature.sign(contentData, "UTF-8"), requestUrl, "UTF-8");
			logger.info("[C2B银联二维码主扫查询营销交易]" + rspData.toString());

			// 判断是否收到应答
			if (!rspData.isEmpty()) {
				if (Signature.validate(rspData, "UTF-8")) {
					String respCode = rspData.get(Dict.respCode);
					String respMsg = rspData.get(Dict.respMsg);
					
					InputParam inputUpdate = new InputParam();
					inputUpdate.putparamString(Dict.txnSeqId, txnSeqId);
					inputUpdate.putparamString(Dict.txnDt, txnDt);
					inputUpdate.putparamString(Dict.txnTm, txnTm);
					//本不需要更新 为了语句不出错加上去的
					inputUpdate.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_01);
					inputUpdate.putparamString(Dict.tradeMoney, orderAmount);
					if ("00".equals(respCode)) {
						String couponInfo = Base64.decode(rspData.get("couponInfo"));
						JSONArray couponInfoArray = JSONArray.fromObject(couponInfo);
						JSONObject couponInfomap = JSONObject.fromObject(couponInfoArray.get(0));
						
						inputUpdate.putparamString(Dict.discountType, couponInfomap.getString(Dict.type));
						inputUpdate.putparamString(Dict.offstAmt, StringUtil.amountTo12Str2(couponInfomap.getString(Dict.offstAmt)));
						inputUpdate.putparamString(Dict.resDesc, "[C2B银联二维码主扫查询营销交易]查询营销成功");
						
						outputParam.putValue(Dict.origTxnAmt, orderAmount);
						outputParam.putValue(Dict.offstDesc, "");
						outputParam.putValue(Dict.offstAmt, StringUtil.amountTo12Str2(couponInfomap.getString(Dict.offstAmt)));
						outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易]查询营销成功");

					} else {
						inputUpdate.putparamString(Dict.resDesc, "[C2B银联二维码主扫查询营销交易]" + respMsg);
						outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易]" + respMsg);
					}
					// 更新订单状态
					OutputParam updateOrderOutput = orderService.updateC2BOrder(inputUpdate);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
				} else {
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易]报文签名验证不通过");
				}
			}

		} catch (Exception e) {
			logger.error("[C2B银联二维码主扫查询营销交易]系统异常：" + e.getMessage(),e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易]系统异常：" + e.getMessage());
		} finally {

		}
		return outputParam;
	}

	/**
	 * C2B银联二维码主扫交易通知处理
	 * 
	 * @param InputParam
	 * @author YYK
	 * @return OutputParam
	 */
	@Override
	public OutputParam C2BEWMDealResultNotifyHandler(InputParam input) throws FrameException {
		logger.info("[C2B银联二维码主扫交易通知处理] C2BEWMDealResultNotifyHandler START" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {

			String txnNo = input.getValueString(Dict.txnNo);
			String orderNo = input.getValueString(Dict.orderNo);
			String txnAmt = input.getValueString(Dict.txnAmt);
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merOrderId, orderNo);
			inputQuery.putparamString(Dict.txnNo, txnNo);

			// 查询订单信息
			OutputParam queryOrderOutput = orderService.queryC2BOrder(inputQuery);
			if (StringConstans.returnCode.FAIL.equals(queryOrderOutput.getReturnCode())) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫交易通知处理] " + queryOrderOutput.getReturnMsg());
				return outputParam;
			}

			String txnSta = StringUtil.toString(queryOrderOutput.getValue(Dict.txnSta));

			if (StringConstans.OrderState.STATE_02.equals(txnSta)|| StringConstans.OrderState.STATE_03.equals(txnSta)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫交易通知处理] 交易已经成功，无需通知");
			} else {
				String resultCode = input.getValueString(Dict.origRespCode);
				String resultMsg = input.getValueString(Dict.origRespMsg);
				InputParam inputUpdate = new InputParam();
				inputUpdate.putparamString(Dict.txnSeqId, StringUtil.toString(queryOrderOutput.getValue(Dict.txnSeqId)));
				inputUpdate.putparamString(Dict.txnDt, StringUtil.toString(queryOrderOutput.getValue(Dict.txnDt)));
				inputUpdate.putparamString(Dict.txnTm, StringUtil.toString(queryOrderOutput.getValue(Dict.txnTm)));
				if ("00".equals(resultCode)) {
					inputUpdate.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_02);
					inputUpdate.putparamString(Dict.voucherNum, input.getValueString(Dict.voucherNum));
					inputUpdate.putparamString(Dict.settleDate, input.getValueString(Dict.settleDate));
					inputUpdate.putparamString(Dict.settleKey, input.getValueString(Dict.settleKey));
					inputUpdate.putparamString(Dict.resDesc, "[C2B银联二维码主扫交易]交易成功");
				} else {
					inputUpdate.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_03);
					inputUpdate.putparamString(Dict.resDesc, "[C2B银联二维码主扫交易]交易失败："+resultMsg);
				}
				// 更新订单状态
				OutputParam updateOrderOutput = orderService.updateC2BOrder(inputUpdate);

				Map<String, Object> map = new HashMap<String, Object>();
				map.put(Dict.OrigPayPlatSeqNbr, orderNo);
				map.put(Dict.OrigPayPlatDate, StringUtil.toString(queryOrderOutput.getValue(Dict.txnTm)));
				map.put(Dict.TransStatus, txnSta);
				map.put(Dict.TransAmt, txnAmt);
				map.put(Dict.OrigMessage, resultMsg);
				InputParam mobileInputParam = new InputParam();
				mobileInputParam.putMap(map);

				// 调用通知网络支付平台 开始
				String recvStr = this.notifyPayPlatformResult(mobileInputParam);

				if (StringUtils.isBlank(recvStr)) {
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respMsg, "返回报文信息错误");
				} else {
					Map<String, Object> paramMap = (Map<String, Object>) JsonUtil.json2Bean(recvStr, Map.class);
					String respCode = StringUtil.toString(paramMap.get("RespCode"));
					String respMessage = String.format("%s", paramMap.get("RespMessage"));
					if (!"00000000".equals(respCode)) {
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue(Dict.respMsg, "[C2B银联二维码主扫交易通知处理] 返回支付结果失败,原因:" + respMessage);
					} else {
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
						outputParam.putValue(Dict.respMsg, "[C2B银联二维码主扫交易通知处理] 通知网络支付平台成功");
					}
				}
			}

		} catch (Exception e) {
			logger.error("[C2B银联二维码主扫交易通知处理] 系统异常" + e.getMessage(), e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询营销交易] 系统异常" + e.getMessage());
		} finally {
			logger.info("[C2B银联二维码主扫交易通知处理] C2BEWMSmarketQuery END" + outputParam.toString());
		}
		return outputParam;
	}

	public IQRCodeService getQrCodeService() {
		return qrCodeService;
	}

	public void setQrCodeService(IQRCodeService qrCodeService) {
		this.qrCodeService = qrCodeService;
	}

	public IOrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public OrderQueryManager getOrderQueryManager() {
		return orderQueryManager;
	}

	public void setOrderQueryManager(OrderQueryManager orderQueryManager) {
		this.orderQueryManager = orderQueryManager;
	}

	public ILocalBankService getLocalBankService() {
		return localBankService;
	}

	public void setLocalBankService(ILocalBankService localBankService) {
		this.localBankService = localBankService;
	}

	/**
	 * string转map 例 {accNo=6216261000000002485, issCode=04100000, cardAttr=01}
	 * 
	 * @param inputStr
	 * @return
	 */
	public static Map<String, String> getMapFromStr(String inputStr) {
		String instr = inputStr.replaceAll("[{]", "").replaceAll("[}]", "");
		String[] str = instr.split(",");
		Map<String, String> map = new HashMap<String, String>();
		for (String string : str) {
			String[] strarry = string.split("=");
			map.put(strarry[0], strarry[1]);
		}
		return map;

	}
	/**
	 * C2B银联二维码主扫查询订单
	 * @author WL
	 */
	@Override
	public OutputParam C2BEWMScanedUnified(InputParam intPutParam) {
		logger.info("[C2B银联二维码主扫查询订单] C2BEWMScanedUnified START" + intPutParam.toString());
		OutputParam outputParam = new OutputParam();
		try {
			// 需要校验的字段
			List<String> validateParamList = new ArrayList<String>();
			validateParamList.add(Dict.channel);
			validateParamList.add(Dict.orderTime);
			validateParamList.add(Dict.orderNumber);
			validateParamList.add(Dict.accNo);
			validateParamList.add(Dict.cardAttr);
			validateParamList.add(Dict.mobile);
			validateParamList.add("deviceID");
			validateParamList.add(Dict.deviceType);
			validateParamList.add(Dict.accountIdHash);
			validateParamList.add(Dict.codeUrl);

			String nullStr = Util.validateIsNull(validateParamList, intPutParam);
			if (!StringUtil.isEmpty(nullStr)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] 请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			/**
			 * 如果是本行银联二维码，采用本行扣款模式，他行银联二维码，将交易上送至银联。
			 */
			// if (queryParam.getReturnCode().equals(StringConstans.returnCode.SUCCESS)) {
			// outputParam = this.pospMicroConsume(intPutParam);
			// return outputParam;
			// }

			String channel = StringUtil.toString(intPutParam.getValue(Dict.channel));
			String codeUrl = StringUtil.toString(intPutParam.getValue(Dict.codeUrl));
			String orderTime = StringUtil.toString(intPutParam.getValue(Dict.orderTime));
			String orderNumber = StringUtil.toString(intPutParam.getValue(Dict.orderNumber));
			String accNo = StringUtil.toString(intPutParam.getValue(Dict.accNo));
			String cardAttr = StringUtil.toString(intPutParam.getValue(Dict.cardAttr));
			String mobile = StringUtil.toString(intPutParam.getValue(Dict.mobile));
			String deviceId = StringUtil.toString(intPutParam.getValue("deviceID"));
			String deviceType = StringUtil.toString(intPutParam.getValue(Dict.deviceType));
			String accountIdHash = StringUtil.toString(intPutParam.getValue(Dict.accountIdHash));
			String sourceIp = StringUtil.toString(intPutParam.getValue("sourceIP"));
			String deviceLocation = StringUtil.toString(intPutParam.getValue(Dict.deviceLocation));
			String fullDeviceNumber = StringUtil.toString(intPutParam.getValue(Dict.fullDeviceNumber));

			if (!(StringConstans.CHANNEL.CHANNEL_MOBILE_FRONT.equals(channel))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] 渠道编号不正确");
				return outputParam;
			}

			if (!orderTime.matches("\\d{14}")) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] 商户订单时间长度不正确");
				return outputParam;
			}

			if (StringUtil.isEmpty(sourceIp) && StringUtil.isEmpty(deviceLocation) && StringUtil.isEmpty(fullDeviceNumber)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] 设备IP、设备GPRS、设备SIM卡号码必须出现一个");
				return outputParam;
			}

			Map<String, String> contentData = new HashMap<String, String>();
			contentData.put(Dict.version, StringConstans.CupsEwmInfo.CUPS_EWM_VERSION); 
			contentData.put(Dict.reqType, StringConstans.CupsTradeType.C2B_ZS_QUERY);
			contentData.put(Dict.issCode, Constants.getParam(Dict.acqCode)); 
			contentData.put(Dict.qrCode, codeUrl);

			// 组装统一下单报文对象
			Map<String, String> reqData = Signature.sign(contentData, StringConstans.Charsets.UTF_8);
			String requestUrl = Constants.getParam("qrcB2cIssBackTransUrl");
			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, StringConstans.Charsets.UTF_8);
			logger.info("[银标二维码 他行二维码主扫查询订单交易] 请求银联报文返回信息" + rspData.toString());

			// 判断是否收到应答
			if (!rspData.isEmpty()) {
				if (Signature.validate(rspData, "UTF-8")) {
					String respCode = rspData.get(Dict.respCode);
					String respMsg = rspData.get(Dict.respMsg);
					if ("00".equals(respCode)) {
						String merOrDt = orderTime.substring(0, 8);
						String merOrTm = orderTime.substring(8);
						String txnSeqId = orderService.getOrderNo(); 
						String txnDt = DateUtil.getDateYYYYMMDD(); 
						String txnTm = DateUtil.getDateHHMMSS(); 
						String txnAmt = StringUtil.toString(rspData.get(Dict.txnAmt));
						txnAmt = StringUtil.amountTo12Str2(txnAmt);
						String currencyCode = rspData.get(Dict.currencyCode); 
						String txnNo = rspData.get(Dict.txnNo); 
						String payeeComments = rspData.get(Dict.payeeComments); 
						String payeeInfo = rspData.get(Dict.payeeInfo); 
						String orderNo = rspData.get(Dict.orderNo); 
						String orderType = rspData.get(Dict.orderType); 
						String encryptCertId = rspData.get(Dict.encryptCertId);
						String issCode = rspData.get(Dict.issCode);
						
						if (StringUtil.isEmpty(encryptCertId) || StringUtil.isEmpty(payeeInfo)) {
							outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
							outputParam.putValue(Dict.respDesc, "收款方信息和加密验证不能为空");
							return outputParam;
						}
						
						if (StringConstans.ORDER_TYPE.ORDER_TYPE_11.equals(orderType)&&StringConstans.CupsEwmInfo.CATD_ATTR_CREDIT.equals(cardAttr)) {
							outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
							outputParam.putValue(Dict.respDesc, "[银标二维码 他行二维码主扫查询订单交易]限定非贷记帐户支付的消费不能使用贷记卡支付");
							return outputParam;
						}
						
						//付款方信息
						payeeInfo = AcpService.decryptData(payeeInfo, StringConstans.Charsets.UTF_8);
						payeeInfo = payeeInfo.substring(payeeInfo.indexOf("{"), payeeInfo.indexOf("}") + 1);
						String payeeInfoRep = payeeInfo.replaceAll("&", ",");
						Map<String, String> map = getMapFromStr(payeeInfoRep);

						String merId = map.get(Dict.id);
						String merName = map.get(Dict.name);
						String merCatCode = map.get(Dict.merCatCode);
						String termId = map.get(Dict.termId);

						InputParam orderInput = new InputParam();
						orderInput.putparamString(Dict.txnSeqId, txnSeqId);
						orderInput.putparamString(Dict.txnDt, txnDt);
						orderInput.putparamString(Dict.txnTm, txnTm);
						orderInput.putparamString(Dict.qrNo, codeUrl);
						orderInput.putparamString(Dict.txnChannel, channel);
						orderInput.putparamString(Dict.currencyCode, currencyCode);
						orderInput.putparamString(Dict.tradeMoney, txnAmt);
						orderInput.putparamString(Dict.txnNo, txnNo);
						orderInput.putparamString("encryptCerId", encryptCertId);
						orderInput.putparamString(Dict.mobile, mobile);
						orderInput.putparamString(Dict.deviceId, deviceId);
						orderInput.putparamString(Dict.deviceType, deviceType);
						orderInput.putparamString(Dict.accountIdHash, accountIdHash);
						orderInput.putparamString(Dict.sourceIp, sourceIp);
						orderInput.putparamString(Dict.deviceLocation, deviceLocation);
						orderInput.putparamString(Dict.fullDeviceNumber, fullDeviceNumber);
						orderInput.putparamString(Dict.cardAttr, cardAttr);
						orderInput.putparamString(Dict.orderNumber, orderNumber);
						orderInput.putparamString(Dict.accNo, accNo);
						orderInput.putparamString(Dict.accountIdHash, accountIdHash);
						orderInput.putparamString(Dict.merId, merId);
						orderInput.putparamString(Dict.merName, merName);
						orderInput.putparamString(Dict.merOrderId, orderNo);
						orderInput.putparamString(Dict.merOrDt, merOrDt);
						orderInput.putparamString(Dict.merOrTm, merOrTm);
						orderInput.putparamString(Dict.merCatCode, merCatCode);
						orderInput.putparamString(Dict.issCode, issCode);
						orderInput.putparamString(Dict.termId, termId);
						orderInput.putparamString(Dict.payType, StringConstans.Pay_Type.PAY_TYPE_NATIVE);
						orderInput.putparamString(Dict.payAccessType, StringConstans.PAYACCESSTYPE.ACCESS_CUPS);
						orderInput.putparamString(Dict.txnType, StringConstans.TransType.TRANS_CONSUME);
						orderInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_01);
						orderInput.putparamString(Dict.resDesc, "他行银联二维码主扫下单成功");

						// 保存订单
						OutputParam saveOrderOutput = orderService.insertC2BOrder(orderInput);

						outputParam.putValue(Dict.orderAmount, txnAmt);
						outputParam.putValue(Dict.merName, merName);
						outputParam.putValue(Dict.payeeComments, payeeComments);
						outputParam.putValue(Dict.txnSeqId, txnSeqId);
						outputParam.putValue(Dict.txnTime, txnDt + txnTm);
						outputParam.putValue(Dict.orderType, orderType);
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
						outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单]他行银联二维码主扫查询订单成功");
					} else {
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] "+respMsg);
					}
				} else {
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] 报文签名验证不通过");
				}
			} else {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单] 他行二维码主扫查询订单交易失败");
			}
			
			
		} catch (Exception e) {
			logger.error("[银标二维码]  他行银联二维码主扫查询订单交易:" + e.getMessage(), e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "[C2B银联二维码主扫查询订单]他行银联二维码主扫查询订单交易异常:"+ e.getMessage());
		} finally {
			logger.info("[C2B银联二维码主扫查询订单] C2BEWMScanedUnified END：" + outputParam.toString());
		}

		return outputParam;
	}
	
	/**
	 * 银联二维码主扫付款
	 * @author WL
	 */
	@Override
	public OutputParam C2BSPayForMainScavenging(InputParam inputParam) {
		logger.info("[银联二维码他行主扫付款] C2BSPayForMainScavenging START" + inputParam.toString());
		OutputParam outputParam = new OutputParam();
		try {

			String txnSeqId = StringUtil.toString(inputParam.getValue(Dict.txnSeqId));
			String txnTime = StringUtil.toString(inputParam.getValue(Dict.txnTime));
			String tradeMoney = StringUtil.toString(inputParam.getValue(Dict.orderAmount));
			String txnDt = txnTime.substring(0, 8);
			String txnTm = txnTime.substring(8);

			// 查询订单是否存在
			InputParam queryInput = new InputParam();
			queryInput.putparamString(Dict.txnSeqId, txnSeqId);
			queryInput.putparamString(Dict.txnDt, txnDt);
			queryInput.putparamString(Dict.txnTm, txnTm);
			OutputParam queryQROutput = orderService.queryC2BOrder(queryInput);
			if (StringUtil.isEmpty(tradeMoney)) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款] 交易金额不能为空");
				return outputParam;
			}
			if (!(StringConstans.returnCode.SUCCESS.equals(queryQROutput.getReturnCode()))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款] 查询订单信息失败");
				return outputParam;
			}
			if (StringConstans.OrderState.STATE_02.equals(queryQROutput.getValue(Dict.txnSta))||StringConstans.OrderState.STATE_06.equals(queryQROutput.getValue(Dict.txnSta))) {
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_06);
				outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款] 该笔订单已经支付成功");
				return outputParam;
			}
			
			Map<String, Object> queryQRMap = queryQROutput.getReturnObj();
			String txnNo = StringUtil.toString(queryQRMap.get(Dict.txnNo));
			String currencyCode = StringUtil.toString(queryQRMap.get(Dict.currencyCode));

			/**
			 * 组装请求报文
			 */
			Map<String, String> contentData = new HashMap<String, String>();
			contentData.put(Dict.version, StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			contentData.put(Dict.reqType, StringConstans.CupsTradeType.C2B_MAIN_SCAVENGING_PAY);
			contentData.put(Dict.issCode, StringUtil.toString(queryQRMap.get(Dict.issCode))); 
			contentData.put(Dict.txnNo, txnNo);
			contentData.put(Dict.currencyCode, currencyCode);
			contentData.put(Dict.txnAmt, new BigDecimal(tradeMoney).toString());
			// 付款方信息
			Map<String, String> payerInfoMap = new HashMap<String, String>();
			payerInfoMap.put(Dict.accNo, StringUtil.toString(queryQROutput.getValue(Dict.accNo)));
			payerInfoMap.put(Dict.name, StringUtil.toString(queryQROutput.getValue(Dict.merName)));
			payerInfoMap.put(Dict.cardAttr, StringUtil.toString(queryQROutput.getValue(Dict.cardAttr)));
			payerInfoMap.put(Dict.mobile, StringUtil.toString(queryQROutput.getValue(Dict.mobile)));

			// 风控信息
			Map<String, String> riskInfoMap = new HashMap<String, String>();
			riskInfoMap.put("deviceID", StringUtil.toString(queryQROutput.getValue(Dict.deviceId)));
			riskInfoMap.put(Dict.deviceType, StringUtil.toString(queryQROutput.getValue(Dict.deviceType)));
			riskInfoMap.put(Dict.mobile, StringUtil.toString(queryQROutput.getValue(Dict.mobile)));
			riskInfoMap.put(Dict.accountIdHash, StringUtil.toString(queryQROutput.getValue(Dict.accountIdHash)));
			riskInfoMap.put("sourceIP", StringUtil.toString(queryQROutput.getValue(Dict.sourceIp)));
			
			//判断是否有优惠信息
			String type =  StringUtil.toString(queryQRMap.get("discountType"));
			String offstAmt =  StringUtil.toString(queryQRMap.get("offstAmt"));
			if (!StringUtil.isEmpty(type)&&!StringUtil.isEmpty(offstAmt)) {
				Map<String, String> couponMap = new HashMap<String, String>();
				couponMap.put(Dict.type, StringUtil.toString(queryQROutput.getValue("discountType")));
				couponMap.put("spnsrId", Constants.getParam("spnsrId"));
				couponMap.put("offstAmt", StringUtil.toString(new BigDecimal(offstAmt)));
				couponMap.put("desc", StringUtil.toString(queryQRMap.get("discountDesc")));
				couponMap.put("id", StringUtil.toString(queryQRMap.get("discountId")));
				List<Map<String, String>> couponList= new ArrayList<Map<String, String>>();
				couponList.add(couponMap);
				String couponJson= JsonUtil.list2json(couponList);
				contentData.put(Dict.txnAmt, new BigDecimal(tradeMoney).subtract(new BigDecimal(offstAmt)).toString());
				contentData.put("origTxnAmt", new BigDecimal(tradeMoney).toString());
				contentData.put("couponInfo",Base64.encode(couponJson));
			}
			contentData.put(Dict.payerInfo, CupsBase.getPayerInfoWithEncrpyt(payerInfoMap,StringConstans.Charsets.UTF_8));
			contentData.put(Dict.riskInfo, CupsBase.getPayerInfo(riskInfoMap,StringConstans.Charsets.UTF_8));
			contentData.put(Dict.encryptCertId, AcpService.getEncryptCertId());
			contentData.put("backUrl", Constants.getParam("unionPay.backUrl"));
			logger.info("[银联二维码付款测确认付款通知] 请求银联报文" + contentData.toString());
			Map<String, String> reqData = Signature.sign(contentData, StringConstans.Charsets.UTF_8); 
			
			String requestUrl = Constants.getParam("qrcB2cIssBackTransUrl");
			
			Map<String, String> rspData = CupsBase.post(reqData, requestUrl, StringConstans.Charsets.UTF_8);
			logger.info("[银联二维码他行主扫付款] 收到返回信息" + rspData.toString());
			
			if (!rspData.isEmpty()) {
				if (Signature.validate(rspData, StringConstans.Charsets.UTF_8)) {
					String respCode = rspData.get(Dict.respCode);
					String respMsg = rspData.get(Dict.respMsg);
					if ("00".equals(respCode)) {
						outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_06);
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
						outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款]交易发送成功");
					} else if(StringConstans.RespCode.NO_CUPS_PAY_VOCATION.equals(respCode)){
						outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_77);
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.NO_CUPS_PAY_VOCATION);
						outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款]未开通银联云闪付");
					}else {
						outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
						outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
						outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款]失败,失败原因："+respMsg);
					}
				} else {
					outputParam.putValue(Dict.txnSta, StringConstans.OrderState.STATE_03);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款]接收的报文签名验证不通过");
				}
				InputParam inputUpdate = new InputParam();
				inputUpdate.putparamString(Dict.txnSeqId, txnSeqId);
				inputUpdate.putparamString(Dict.resDesc, StringUtil.toString(outputParam.getValue(Dict.respDesc)));
				inputUpdate.putparamString(Dict.txnSta, StringUtil.toString(outputParam.getValue(Dict.txnSta)));
				inputUpdate.putparamString(Dict.txnDt, txnDt);
				inputUpdate.putparamString(Dict.txnTm, txnTm);
				inputUpdate.putparamString(Dict.tradeMoney, tradeMoney);
				// 更新订单状态
				OutputParam updateOrderOutput = orderService.updateC2BOrder(inputUpdate);
			}
		} catch (Exception e) {
			logger.error("[银联二维码他行主扫付款] 银联二维码他行主扫付款:" + e.getMessage(), e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "[银联二维码他行主扫付款]二维码前置系统异常"+e.getMessage());
		}
		logger.info("[银联二维码他行主扫付款] C2BSPayForMainScavenging END" + outputParam.toString());
		return outputParam;
	}

}
