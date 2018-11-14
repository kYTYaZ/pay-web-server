package com.huateng.pay.manager.weixin.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.http.HttpRequestClient;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.IOrderDao;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.huateng.pay.handler.services.OrderQueryManager;
import com.huateng.pay.manager.weixin.IWxManager;
import com.huateng.pay.po.weixin.MicroPayReqData;
import com.huateng.pay.po.weixin.OrderQueryReqData;
import com.huateng.pay.po.weixin.RefundReqData;
import com.huateng.pay.po.weixin.UnifiedOrderData;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;
import com.huateng.pay.services.weixin.YLWXPayService;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;
import com.wldk.framework.utils.MappingUtils;

import net.sf.json.JSONObject;

/**
 * 微信支付交易业务处理模块类
 * 
 * @author guohuan
 *
 */
public class WxManagerImpl implements IWxManager {
	private IOrderService orderService;
	private IOrderDao orderDao;
	private ISequenceDao sequenceDao;
	private Logger logger = LoggerFactory.getLogger(WxManagerImpl.class);
	private OrderQueryManager orderQueryManager;
	private IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService;  //add by ghl at 20171211
	private IMerchantChannelService merchantChannelService ;
	private YLWXPayService ylwxPayService;
	

	/**
	 * 微信统一下单请求处理(第一次去下单)
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OutputParam toWxUnifiedOrder(InputParam input) throws FrameException {
		OutputParam out = new OutputParam();
		try {

			String payType = input.getValue("payType").toString();
			String txnDt = input.getValue("txnDt").toString();
			String txnTm = input.getValue("txnTm").toString();
			String txnSeqId = input.getValue("txnSeqId").toString();
			String key = input.getValue("key").toString();
			
			// 获取参数
			Map<String, Object> dataMap = (Map<String, Object>) input.getValue("dataMap");
			String subMchId = dataMap.get("subMchId").toString();
			String appid = dataMap.get("appid").toString();
			String mch_id = dataMap.get("mch_id").toString();
			String pfxPath = dataMap.get("pfxPath").toString();
			String pwd = dataMap.get("pwd").toString();
			
			UnifiedOrderData unifiedOrderData = new UnifiedOrderData(dataMap, key);
			
			Map<String,String> requestMap = new HashMap<String,String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_unifiedOrder_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pwd);
			
			// 发送统一下单请求
			logger.info("[微信支付统一下单] 开始组装下单报文："+requestMap.toString());
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap,unifiedOrderData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);

			logger.info("微信统一下单返回的响应报文：" + respString);
			String returnCode = respMap.get("return_code").toString();
			String returnMsg = respMap.get("return_msg").toString();
			if (!"SUCCESS".equals(returnCode)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信支付统一下单,微信返回失败:" + returnMsg);
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("报文签名验证不通过");
				return out;
			}
			
			/************************ 处理微信返回的报文信息 ******************************/
			String resultCode = respMap.get("result_code").toString();
			if (!"SUCCESS".equals(resultCode)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信支付统一下单失败:" + respMap.get("err_code_des").toString());
				return out;
			}
			
			String prepayId = respMap.get("prepay_id").toString();//微信预支付ID
			String urlCode = "";//微信二维码
			if (StringConstans.Pay_Type.PAY_TYPE_NATIVE.equals(payType)) {
				urlCode = respMap.get("code_url").toString();
			}

			String txnSta = StringConstans.OrderState.STATE_06;

			logger.debug("[微信支付统一下单]  获取下单返回的报文参数信息");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("payType", payType);
			map.put("codeUrl", urlCode);
			map.put("prepayId", prepayId);
//			map.put("txnDt", txnDt);
//			map.put("txnTm", txnTm);
			map.put("txnSeqId", txnSeqId);
			out.setReturnObj(map);

			/************************ 更新订单状态为已下单 ******************************/
			logger.debug("[微信支付统一下单] 完成下单后更新订单状态为已下单(已发送)");
			InputParam updateInput = new InputParam();
			updateInput.putParams("codeUrl", urlCode);
			updateInput.putParams("wxPrepayId", prepayId);
			updateInput.putParams("randomStr", unifiedOrderData.getNonce_str());
			updateInput.putParams("txnSta", txnSta);
			updateInput.putParams("txnSeqId", txnSeqId);
			updateInput.putParams("txnDt", txnDt);
			updateInput.putParams("txnTm", txnTm);
			updateInput.putParams("payType", payType);
			updateInput.putParams("wxMerId", unifiedOrderData.getMch_id());
			if (!StringUtil.isNull(unifiedOrderData.getSub_mch_id())) {
				updateInput.putParams("subWxMerId", unifiedOrderData.getSub_mch_id());
			}
			OutputParam orderOut = orderService.updateWxOrderInfo(updateInput);
			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				return orderOut;
			}
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
		} catch (Exception e) {
			logger.error("[微信支付统一下单] 发送下单请求处理失败：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信支付统一下单] 发送下单请求处理失败");
		}
		return out;
	}

	/**
	 * 微信被扫支付(刷卡) 去微信请求处理
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public OutputParam toWxMicroPay(InputParam input) throws FrameException {

		logger.info("[微信被扫支付(刷卡) 去微信请求处理流程 ]  toWxMicroPay START:"+input.toString());

		OutputParam out = new OutputParam();

		try {
			String key = input.getValue("key").toString();
			String payType = input.getValue("payType").toString();
			String merId = input.getValue("merId").toString();
			String txnDt = input.getValue("txnDt").toString();
			String txnTm = input.getValue("txnTm").toString();
			String txnSeqId = input.getValue("txnSeqId").toString();
			String txnSta = "";// 被扫支付支付状态
			String resDesc = "";// 被扫支付支付描述
			
			// 获取参数
			Map<String, Object> dataMap = (Map<String, Object>) input.getValue("dataMap");
			String subMchId = dataMap.get("subMchId").toString();
			String appid = dataMap.get("appid").toString();
			String mch_id = dataMap.get("mch_id").toString();
			String pfxPath = dataMap.get("pfxPath").toString();
			String pwd = dataMap.get("pwd").toString();
			
			logger.debug("[微信被扫支付(刷卡) - 调用微信接口] 开始组装被扫支付(刷卡)报文");
			// 组装被扫支付报文对象
			MicroPayReqData microPayReqData = new MicroPayReqData(dataMap, key);

			Map<String,String> requestMap = new HashMap<String,String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_microPay_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pwd);
			
			// 发送被扫支付请求
			logger.info("[微信被扫支付(刷卡) - 调用微信接口] 发起支付请求:"+microPayReqData.toString());
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, microPayReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);

			logger.info("[微信被扫支付(刷卡) - 调用微信接口] 返回的响应报文：" + respString);
			String returnCode = respMap.get("return_code").toString();
			String returnMsg = respMap.get("return_msg").toString();
			if (!"SUCCESS".equals(returnCode)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[微信被扫支付(刷卡) - 调用微信接口] 微信返回失败:" + returnMsg);
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "交易异常");
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("报文签名验证不通过");
				out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				out.putValue(Dict.respDesc, "微信交易报文异常");
				return out;
			}

			String resultCode = respMap.get("result_code").toString();
			if ("SUCCESS".equals(resultCode)) {
				logger.info("[微信被扫支付(刷卡) - 调用微信接口] 支付成功");
				txnSta = StringConstans.OrderState.STATE_02;
				resDesc = StringConstans.RespDesc.RESP_DESC_02;

				// 微信支付完成时间
				String payTime = respMap.get("time_end").toString();
				// 微信支付订单号
				String wxOrderNo = String.format("%s", respMap.get("transaction_id"));
				//微信用户标识
				String payerid = respMap.get("openid").toString();
				//付款银行
				String bankType = respMap.get("bank_type").toString();
				
				// tpf 2014-04-01
				out.putValue("wxPayTime", payTime);
				out.putValue("wxOrderNo", wxOrderNo);

				/************************ 更新订单状态 ******************************/
				logger.debug("[微信被扫支付(刷卡) - 调用微信接口] 完成被扫支付请求后更新订单状态为'支付成功'");
				InputParam updateInput = new InputParam();
				updateInput.putParams("txnSta", txnSta);
				updateInput.putParams("txnSeqId", txnSeqId);
				updateInput.putParams("txnDt", txnDt);
				updateInput.putParams("txnTm", txnTm);
				updateInput.putParams("payType", payType);
				updateInput.putParams("wxMerId", mch_id);
				updateInput.putParams("wxOrderNo", wxOrderNo);
				updateInput.putParams("resDesc",resDesc );
				updateInput.putParams("settleDate", payTime);// tpf 2016-04-01
				updateInput.putParams("payerid", payerid);
				updateInput.putParams("bankType", bankType);
				if (!StringUtil.isNull(subMchId)) {
					updateInput.putParams("subWxMerId", subMchId);
				}
				OutputParam orderOut = orderService.updateWxOrderInfo(updateInput);
				if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
					out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					out.putValue(Dict.respDesc, "交易异常");
					return orderOut;
				}
			} else {
				
				String errCode = respMap.get("err_code").toString();
				String errCodeDesc = respMap.get("err_code_des").toString();

				// 默认交易失败
				txnSta = StringConstans.OrderState.STATE_03;
				resDesc = "微信被扫失败:" + errCodeDesc;
				String wxPayTime = null;
				String wxOrderNo = null;

				OutputParam orderOutput = new OutputParam();

				if (StringConstans.WxErrorCode.USER_PAYING.equals(errCode)) {
					logger.info("[微信被扫支付(刷卡) - 调用微信接口] 支付未完成,等待用户支付");
					InputParam orderInput = new InputParam();
					orderInput.putParams("merId", merId);
					orderInput.putParams("txnSeqId", txnSeqId);
					orderInput.putParams("txnDt", txnDt);
					orderInput.putParams("txnTm", txnTm);
					orderInput.putParams("payType", payType);
					orderInput.putParams("wxMerId", mch_id);
					if (!StringUtil.isNull(subMchId)) {
						orderInput.putParams("subWxMerId", subMchId);
					}
					orderInput.putParams("times", "1");

					logger.info("[微信被扫支付(刷卡) - 调用微信接口] 用户正在支付中30s的轮询  开始");

					orderQueryManager.microOrderQuery(orderInput, orderOutput, 2000);
					
					txnSta = String.format("%s", orderOutput.getValue(Dict.respCode));
					resDesc = String.format("%s", orderOutput.getValue(Dict.respDesc));
					wxPayTime = String.format("%s", orderOutput.getValue("wxPayTime"));
					wxOrderNo = String.format("%s", orderOutput.getValue("wxOrderNo"));
					
					if (!StringUtil.isEmpty(wxPayTime)) {
						out.putValue("wxPayTime", wxPayTime);
					}
					if (!StringUtil.isEmpty(wxOrderNo)) {
						out.putValue("wxOrderNo", wxOrderNo);
					}
					
					/** 调用轮询微信订单接口 */

					logger.info("[微信被扫支付(刷卡) - 调用微信接口] 用户正在支付中30s的轮询  结束");

				} else if (StringConstans.WxErrorCode.SYSTEM_ERROR.equals(errCode)
						|| StringConstans.WxErrorCode.BANK_ERROR.equals(errCode)) {

					InputParam queryInput = new InputParam();
					queryInput.putParams("outTradeNo", txnSeqId);
					queryInput.putParams("subMchId", subMchId);
					queryInput.putParams("appid", appid);
					queryInput.putParams("mchId", mch_id);
					queryInput.putParams("key", key);
					queryInput.putParams("pfxPath", pfxPath);
					queryInput.putParams("pwd", pwd);
					orderOutput = this.toQueryWxOrder(queryInput);

					// 微信支付完成时间
					if (!StringUtil.isEmpty(orderOutput.getValue("settleDate"))) {
						wxPayTime = String.format("%s", orderOutput.getValue("settleDate"));
						out.putValue("wxPayTime", wxPayTime);
					}
					if (!StringUtil.isEmpty(orderOutput.getValue("wxOrderNo"))) {
						wxOrderNo = String.format("%s", orderOutput.getValue("wxOrderNo"));
						out.putValue("wxOrderNo", wxOrderNo);
					}
					if (StringConstans.returnCode.SUCCESS.equals(orderOutput.getReturnCode())) {
						txnSta = String.format("%s", orderOutput.getValue("orderSta"));
						resDesc = String.format("%s", orderOutput.getValue("orderDesc"));
					}

					/************************ 更新订单状态 ******************************/
					InputParam updateInput = new InputParam();
					updateInput.putParams("txnSta", txnSta);
					updateInput.putParams("txnSeqId", txnSeqId);
					updateInput.putParams("txnDt", txnDt);
					updateInput.putParams("txnTm", txnTm);
					updateInput.putParams("payType", payType);
					updateInput.putParams("wxMerId", mch_id);
					updateInput.putParams("wxOrderNo", wxOrderNo);
					updateInput.putParams("settleDate", wxPayTime);
					updateInput.putParams("resDesc", resDesc);

					if (!StringUtil.isNull(subMchId)) {
						updateInput.putParams("subWxMerId", subMchId);
					}
					OutputParam orderOut = orderService.updateWxOrderInfo(updateInput);
					if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
						out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
						out.putValue(Dict.respDesc, "[微信被扫支付(刷卡)]更新订单状态失败");
						return orderOut;
					}
				}
			}
			out.putValue(Dict.respCode, txnSta);
			out.putValue(Dict.respDesc, resDesc);
			out.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (Exception e) {
			logger.error("[微信被扫支付(刷卡)] 发送支付请求处理失败：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信被扫支付(刷卡)] 发送支付请求处理失败"+e.getMessage());
			out.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			out.putValue(Dict.respDesc, "微信被扫支付交易异常");
			
		}
		logger.info("[微信被扫支付(刷卡) 去微信请求处理流程]  toWxMicroPay END:"+out.toString());
		return out;
	}
	

	/**
	 * 发起到微信申请退款
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam toWxRefundOrder(InputParam input) throws FrameException {
		OutputParam out = new OutputParam();
		try {
			// 获取参数
			@SuppressWarnings("unchecked")
			Map<String, Object> dataMap = (Map<String, Object>) input.getValue("dataMap");
//			String key = input.getValue("key").toString();
			String txnDt = input.getValue("txnDt").toString();
			String txnTm = input.getValue("txnTm").toString();
			String txnSeqId = input.getValue("txnSeqId").toString();
			String subWxMerId = input.getValue("subWxMerId").toString();
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.subMerchant, subWxMerId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.WX);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			
			String cacheKey = StringConstans.PAY_ChANNEL.WX + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String key = configMap.get(Dict.WX_KEY);
			String appid = configMap.get(Dict.WX_APPID);
			String mch_id = configMap.get(Dict.WX_MERID);
			String pfxPath = configMap.get(Dict.WX_PFX_PATH);
			String pwd = configMap.get(Dict.WX_PWD);

			// 组装退款报文对象
			RefundReqData refundReqData = new RefundReqData(dataMap, key);
			
			Map<String,String> requestMap = new HashMap<String,String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_refund_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pwd);
			
			// 发送申请退款请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap, refundReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);
			logger.info("[微信退款]申请退款返回的响应报文：" + respString);

			String returnCode = respMap.get("return_code").toString();
			String returnMsg = respMap.get("return_msg").toString();
			if (!"SUCCESS".equals(returnCode)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信退款申请失败:" + returnMsg);
				return out;
			}

			/************************ 验证报文签名信息 ******************************/
			String backSign = respMap.get("sign").toString();// 返回报文中的sign签名
			respMap.put("sign", "");
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				logger.error("[微信支付后台通知]接收的报文签名验证不通过");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("报文签名验证不通过");
				return out;
			}

			String resultCode = respMap.get("result_code").toString();
			if (!"SUCCESS".equals(resultCode)) {
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("微信退款申请失败:" + respMap.get("err_code_des").toString());
				return out;
			}

			/************************ 更新订单状态为06已发送 ******************************/
			InputParam orderInput = new InputParam();
			orderInput.putParams("txnSta", StringConstans.OrderState.STATE_06);
			orderInput.putParams("txnSeqId", txnSeqId);
			orderInput.putParams("txnDt", txnDt);
			orderInput.putParams("txnTm", txnTm);
			orderInput.putParams("wxRefundNo", respMap.get("refund_id").toString());
			orderInput.putParams("subWxMerId", subWxMerId);
			OutputParam orderOut = orderService.updateWxOrderInfo(orderInput);
			if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
				logger.error("[微信退款]更新数据库表订单状态(06已发送)失败");
				return orderOut;
			}

			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			return out;
		} catch (Exception e) {
			logger.error("[微信退款] 发送微信退款请求处理失败：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信退款] 发送微信退款请求处理失败");
			return out;
		}
	}
	
	/**
	 * 发起到微信申请退款
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam toWxRefundOrderYL(InputParam input) throws FrameException {
		logger.info("[银联微信退款] 请求参数：" + input.toString());
		OutputParam out = new OutputParam();
		try {
			HashMap<String, String> data = new HashMap<String, String>();
			String refundTxnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String refundAmount = StringUtil.toString(input.getValue(Dict.refundAmount));
			String initTradeMoney = StringUtil.toString(input.getValue(Dict.initTradeMoney));
			String refundReason = StringUtil.toString(input.getValue(Dict.remark));
			String sub_mch_id = StringUtil.toString(input.getValue(Dict.subMerchant));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			data.put("out_trade_no", initTxnSeqId); //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母，且在同一个商户号下唯一
			data.put("out_refund_no", refundTxnSeqId); //商户退款单号  商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母，同一退款单号多次请求只退一笔
			data.put("refund_fee", refundAmount); //退款总金额，单位为分，只能为整数，可部分退款
			data.put("total_fee", initTradeMoney); //订单总金额，单位为分，只能为整数
			data.put("refund_desc", refundReason); // 退款原因   若商户传入，会在下发给用户的退款消息中体现退款原因
//			data.put("notify_url", ""); //退款结果通知url  异步接收银联退款结果通知的回调地址，通知URL必须为外网可访问的url，不允许带参数。
			data.put("sub_mch_id", sub_mch_id); 
			
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_"+rateChannel;
			String url = Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.refundUrlSuffix");
			Map<String, String> resultMap = ylwxPayService.wxSend(data, cacheKey, url);
			
			String errCode = resultMap.get(Dict.err_code);
			String errCodeDes = resultMap.get(Dict.err_code_des);
			if (!StringConstans.WxTradeStatus.SUCCESS.equals(resultMap.get(Dict.result_code))) {
				if(StringConstans.WxErrorCode.SYSTEM_ERROR.equals(errCode)
						||StringConstans.WxErrorCode.BIZERR_NEED_RETRY.equals(errCode)) {
					out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
					out.putValue(Dict.msg, "微信返回:"+errCodeDes);
				}else {
					InputParam updateInput = new InputParam();
					updateInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
					updateInput.putParams("txnSeqId", refundTxnSeqId);
					updateInput.putParams("resDesc", errCode+errCodeDes);
					orderService.updateWxOrderInfo(updateInput);
					out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					out.putValue(Dict.msg,  "微信返回:"+errCodeDes);
				}
			}else {
				out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
				out.putValue(Dict.msg, "已受理退款，正在处理中");
			}
			return out;
		} catch (Exception e) {
			logger.error("[银联微信退款] 发送微信退款请求处理失败：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[银联微信退款] 发送微信退款请求处理失败");
			return out;
		} finally {
			logger.info("[银联微信退款] 返回参数：" + out.toString());
		}
	}
	
	/**
	 * 发起到银联微信退款查询
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam toWxRefundQueryYL(InputParam input) throws FrameException {
		logger.info("[银联微信退款查询] 请求参数：" + input.toString());
		OutputParam out = new OutputParam();
		try {
			String refundTxnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String sub_mch_id = StringUtil.toString(input.getValue(Dict.subMerchant));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			String refundAmount =  StringUtil.toString(input.getValue(Dict.refundAmount));
			String tradeMoney =  StringUtil.toString(input.getValue(Dict.tradeMoney));
			String initTotalRefundFee = StringUtil.toString(input.getValue(Dict.initTotalRefundFee));
			String totalRefundFee = StringUtil.amountTo12Str2((new BigDecimal(refundAmount).add(new BigDecimal(initTotalRefundFee))).toString());
			
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("out_trade_no", initTxnSeqId); //商户系统内部订单号，要求32个字符内，只能是数字、大小写字母，且在同一个商户号下唯一
			data.put("out_refund_no", refundTxnSeqId); //商户退款单号  商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母，同一退款单号多次请求只退一笔
			data.put("sub_mch_id", sub_mch_id); 
			data.put("total_fee", new BigDecimal(tradeMoney).toString());
			data.put("refund_fee", new BigDecimal(refundAmount).toString());
			
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_"+rateChannel;
			String url = Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.qrysingleUrlSuffix");
			Map<String, String> resultMap = ylwxPayService.wxSend(data, cacheKey, url);
			
			String errCode = resultMap.get(Dict.err_code);
			String errCodeDes = resultMap.get(Dict.err_code_des);
			if (!StringConstans.WxTradeStatus.SUCCESS.equals(resultMap.get(Dict.result_code))) {
				out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
				out.putValue(Dict.msg, "微信返回:"+errCodeDes);
			}else {
				/**查询处理成功*/
				String refundStatus = resultMap.get(Dict.refund_status);
				if(StringConstans.WxRefundStatus.SUCCESS.equals(refundStatus)) {
					
					/**原交易订单状态更新start*/
					
					InputParam initupdateInput = new InputParam();
					initupdateInput.putParams(Dict.txnSeqId, initTxnSeqId);
					initupdateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_02);
					initupdateInput.putParams(Dict.totalRefundFee, totalRefundFee);
					initupdateInput.putParams(Dict.initTotalRefundFee, initTotalRefundFee);
					OutputParam initupdateOut = orderService.updateWxOrderInfo(initupdateInput);
					logger.debug("退款订单状态更新"+initupdateOut.toString());
					/**原交易订单状态更新end*/
					
					if(!StringConstans.returnCode.SUCCESS.equals(initupdateOut.getReturnCode())) {
						out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
						out.putValue(Dict.msg, "退款处理中");
						throw new RuntimeException("原交易订单totalRefundFee更新失败");
					}
					
					/**退款订单状态更新start*/
					InputParam updateInput = new InputParam();
					updateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_02);
					updateInput.putParams(Dict.txnSeqId, refundTxnSeqId);
					updateInput.putParams("resDesc", "退款成功");
					OutputParam updateOut = orderService.updateWxOrderInfo(updateInput);
					logger.debug("退款订单状态更新"+updateOut.toString());
					/**退款订单状态更新end*/
					if(StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
						out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_02);
						out.putValue(Dict.msg, "退款成功");
					}else {
						out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
						out.putValue(Dict.msg, "退款处理中");
						throw new RuntimeException("退款订单更新失败");
					}
					
				}else if(StringConstans.WxRefundStatus.REFUNDCLOSE.equals(refundStatus)) {
					
				}else if(StringConstans.WxRefundStatus.PROCESSING.equals(refundStatus)) {
					out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
					out.putValue(Dict.msg, "退款处理中");
				}else if(StringConstans.WxRefundStatus.CHANGE.equals(refundStatus)) {
					/**退款订单状态更新start*/
					InputParam updateInput = new InputParam();
					updateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_03);
					updateInput.putParams(Dict.txnSeqId, refundTxnSeqId);
					updateInput.putParams("resDesc", "退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败。");
					OutputParam updateOut = orderService.updateWxOrderInfo(updateInput);
					logger.debug("退款订单状态更新"+updateOut.toString());
					/**退款订单状态更新end*/
					if(StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
						out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
						out.putValue(Dict.msg, "退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败。");
					}else {
						out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
						out.putValue(Dict.msg, "退款处理中");
					}
				}
			}
			return out;
		} catch (Exception e) {
			logger.error("[银联微信退款] 发送微信退款查询处理失败：" + e.getMessage(),e);
			out.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
			out.putValue(Dict.msg, "已受理退款，正在处理中");
			throw new RuntimeException(e);
		} finally {
			logger.info("[银联微信退款查询] 返回参数：" + out.toString());
		}
	}


	/**
	 * 处理微信后台通知订单的状态
	 */
	@Override
	public OutputParam toWxRecivBack(InputParam input) throws FrameException {
		logger.info("处理微信后台通知订单的状态开始,请求报文:"+input.toString());
		OutputParam out = new OutputParam();
		try {
			/************************ 验证微信原订单信息 ******************************/
			logger.debug("[微信支付后台通知]验证原微信支付订单信息");
			
			String txnSeqId = input.getValue("txnSeqId").toString();
			String txnDt = input.getValue("txnDt").toString();
			
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

			/************************ 处理微信订单信息 ******************************/
			Map<String, Object> wxOrder = wxOrderList.get(0);
			InputParam orderInput = new InputParam();
			String txnSta = wxOrder.get("txnSta").toString();
			// 验证原订单状态
			if (StringConstans.OrderState.STATE_02.equals(txnSta)) {
				logger.debug("[微信支付后台通知]原订单状态已为成功,无需处理");
			} else {
				String resultCode = input.getValue("result_code").toString();
				String tradeType = input.getValue("trade_type").toString();
				String payType = "";
				if (StringConstans.WeiXinTransType.APP.equals(tradeType)) {
					payType = StringConstans.Pay_Type.PAY_TYPE_WEIXIN_APP;
				} else if (StringConstans.WeiXinTransType.NATIVE.equals(tradeType)) {
					payType = StringConstans.Pay_Type.PAY_TYPE_NATIVE;
				} else if (StringConstans.WeiXinTransType.JSAPI.equals(tradeType)) {
					payType = StringConstans.Pay_Type.PAY_TYPE_WEIXIN_JSAPI;
				}

				// 验证微信订单金额跟网关订单金额是否一致
				String totalFee = input.getValue("total_fee").toString();// 微信返回金额
				String tradeMoney = wxOrder.get("tradeMoney").toString();
				BigDecimal wxFee = new BigDecimal(totalFee).divide(new BigDecimal(100));
				BigDecimal orderAmount = new BigDecimal(tradeMoney).divide(new BigDecimal(100));
				if (wxFee.compareTo(orderAmount) != 0) {
					logger.debug("[微信支付后台通知] 微信支付后台通知支付金额与网关订单金额不一致");
					// 更新订单状态失败
					orderInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
					orderInput.putParams("resCode", StringConstans.returnCode.FAIL);
					orderInput.putParams("resDesc", "支付金额与订单金额不一致");
					orderInput.putParams("settleDate", wxOrder.get("settleDate").toString());
				}

				if ("SUCCESS".equals(resultCode)) {
					logger.debug("[微信支付后台通知]微信支付后台通知支付成功");
					String payTime = input.getValue("time_end").toString();
					orderInput.putParams("payerid", input.getValue("openid").toString());
					orderInput.putParams("bankType", input.getValue("bank_type").toString());
					// 更新订单状态成功
					orderInput.putParams("txnSta", StringConstans.OrderState.STATE_02);
					orderInput.putParams("resCode", StringConstans.returnCode.SUCCESS);
					orderInput.putParams("resDesc", "交易成功");
					orderInput.putParams("settleDate", payTime);//tpf 2014-04-01
				} else {
					logger.debug("[微信支付后台通知]微信支付后台通知支付失败");
					// 更新订单状态失败
					orderInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
					orderInput.putParams("resCode", StringConstans.returnCode.FAIL);
					orderInput.putParams("resDesc", input.getValue("err_code_des").toString());
				}

				logger.debug("[微信支付后台通知]更新原微信支付订单状态");
				orderInput.putParams("txnSeqId", txnSeqId);
				orderInput.putParams("txnDt", wxOrder.get("txnDt").toString());
				orderInput.putParams("txnTm", wxOrder.get("txnTm").toString());
				orderInput.putParams("wxOrderNo", input.getValue("transaction_id").toString());
				orderInput.putParams("payType", payType);
				OutputParam orderOut = orderService.updateWxOrderInfo(orderInput);
				if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
					logger.debug("[微信支付后台通知]更新原微信支付订单表状态失败");
					return orderOut;
				}
				txnSta = orderInput.getValue("txnSta").toString();
			}
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			/*out.putValue("txnSeqId", txnSeqId);
			out.putValue("txnDt", txnDt);*/
			out.putValue("wxOrder", wxOrder);
			out.putValue("txnSta", txnSta);
			return out;
		} catch (Exception e) {
			logger.error("[微信后台通知] 微信后台通知订单状态处理失败：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信后台通知] 微信后台通知订单状态处理失败");
			return out;
		} finally {
			logger.info("处理微信后台通知订单的状态结束,返回报文:"+out.toString());
		}
	}
	
	/**
	 * 处理微信后台通知订单的状态
	 */
	@Override
	public OutputParam toWxRecivBackYL(InputParam input) throws FrameException {
		logger.info("处理微信后台通知订单的状态开始,请求报文:"+input.toString());
		OutputParam out = new OutputParam();
		try {
			/************************ 验证微信原订单信息 ******************************/
			logger.debug("[微信支付后台通知]验证原微信支付订单信息");
			
			String txnSeqId = input.getValueString("txnSeqId").toString();
			String txnDt = input.getValueString("txnDt").toString();
			
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

			/************************ 处理微信订单信息 ******************************/
			Map<String, Object> wxOrder = wxOrderList.get(0);
			InputParam orderInput = new InputParam();
			String txnSta = wxOrder.get("txnSta").toString();
			// 验证原订单状态
			if (StringConstans.OrderState.STATE_02.equals(txnSta)) {
				logger.debug("[微信支付后台通知]原订单状态已为成功,无需处理");
			} else {
				String resultCode = input.getValueString("result_code").toString();
				String tradeType = input.getValueString("trade_type").toString();
				String payType = "";
				if (StringConstans.WeiXinTransType.APP.equals(tradeType)) {
					payType = StringConstans.Pay_Type.PAY_TYPE_WEIXIN_APP;
				} else if (StringConstans.WeiXinTransType.NATIVE.equals(tradeType)) {
					payType = StringConstans.Pay_Type.PAY_TYPE_NATIVE;
				} else if (StringConstans.WeiXinTransType.JSAPI.equals(tradeType)) {
					payType = StringConstans.Pay_Type.PAY_TYPE_WEIXIN_JSAPI;
				}

				// 验证微信订单金额跟网关订单金额是否一致
				String totalFee = input.getValueString("total_fee").toString();// 微信返回金额
				String tradeMoney = wxOrder.get("tradeMoney").toString();
				BigDecimal wxFee = new BigDecimal(totalFee).divide(new BigDecimal(100));
				BigDecimal orderAmount = new BigDecimal(tradeMoney).divide(new BigDecimal(100));
				if (wxFee.compareTo(orderAmount) != 0) {
					logger.debug("[微信支付后台通知] 微信支付后台通知支付金额与网关订单金额不一致");
					// 更新订单状态失败
					orderInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
					orderInput.putParams("resCode", StringConstans.returnCode.FAIL);
					orderInput.putParams("resDesc", "支付金额与订单金额不一致");
					orderInput.putParams("settleDate", wxOrder.get("settleDate").toString());
				}

				if ("SUCCESS".equals(resultCode)) {
					logger.debug("[微信支付后台通知]微信支付后台通知支付成功");
					String payTime = input.getValueString("time_end").toString();
					orderInput.putParams("payerid", input.getValueString("openid").toString());
					orderInput.putParams("bankType", input.getValueString("bank_type").toString());
					// 更新订单状态成功
					orderInput.putParams("txnSta", StringConstans.OrderState.STATE_02);
					orderInput.putParams("resCode", StringConstans.returnCode.SUCCESS);
					orderInput.putParams("resDesc", "交易成功");
					orderInput.putParams("settleDate", payTime);//tpf 2014-04-01
				} else {
					logger.debug("[微信支付后台通知]微信支付后台通知支付失败");
					// 更新订单状态失败
					orderInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
					orderInput.putParams("resCode", StringConstans.returnCode.FAIL);
					orderInput.putParams("resDesc", input.getValueString("err_code_des").toString());
				}

				logger.debug("[微信支付后台通知]更新原微信支付订单状态");
				orderInput.putParams("txnSeqId", txnSeqId);
				orderInput.putParams("txnDt", wxOrder.get("txnDt").toString());
				orderInput.putParams("txnTm", wxOrder.get("txnTm").toString());
				orderInput.putParams("wxOrderNo", input.getValueString("transaction_id").toString());
				orderInput.putParams("payType", payType);
				OutputParam orderOut = orderService.updateWxOrderInfo(orderInput);
				if (!StringConstans.returnCode.SUCCESS.equals(orderOut.getReturnCode())) {
					logger.debug("[微信支付后台通知]更新原微信支付订单表状态失败");
					return orderOut;
				}
				txnSta = orderInput.getValue("txnSta").toString();
			}
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			/*out.putValue("txnSeqId", txnSeqId);
			out.putValue("txnDt", txnDt);*/
			out.putValue("wxOrder", wxOrder);
			out.putValue("txnSta", txnSta);
			return out;
		} catch (Exception e) {
			logger.error("[微信后台通知] 微信后台通知订单状态处理失败：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信后台通知] 微信后台通知订单状态处理失败");
			return out;
		} finally {
			logger.info("处理微信后台通知订单的状态结束,返回报文:"+out.toString());
		}
	}

	/*@Override
	public void toCoreForSettle(InputParam settleInput) throws FrameException {
		//交易流水
		String txnSeqId = settleInput.getValue("txnSeqId").toString();
		logger.info("[T+0清算] txnSeqId=" + txnSeqId);
		
		//二维码交易日期（ 网关订单日期）
		String txnDt = settleInput.getValue("txnDt").toString();
		logger.info("[T+0清算] txnDt=" + txnDt);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("txnSeqId", txnSeqId);
		params.put("txnDt", txnDt);
		
		List<Map<String, Object>> wxOrderList = orderDao.queryOrder(params);
		if (wxOrderList.size() == 0) {
			logger.error("[T+0清算]微信支付原订单不存在");
			return;
		}
		
		Map<String, Object> wxOrder = wxOrderList.get(0);
		wxOrder = wxOrderList.get(0);
		String ewmData = String.format("%s", wxOrder.get("ewmData"));
		InputParam queryThreeCodeInput = new InputParam();
		queryThreeCodeInput.putparamString("ewmData", ewmData);

		//根据流水表中的二维码明文查询三码信息表
		OutputParam queryOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);
		
		//商家账号
		String accountNo = queryOut.getValue("acctNo").toString();
		
		//商户名称
		String merName = queryOut.getValue("merName").toString();
		
		//商户编号
		String merId = queryOut.getValue("merId").toString();
		
		//机构号
		String orgCode = queryOut.getValue("orgCode").toString();
		
		//交易状态
		String txnSta = wxOrder.get("txnSta").toString();
		
		//支付方式
		String payAccessType = wxOrder.get("payAccessType").toString();
		
		//订单清算方式'0':T+0,'1':T+1
		String settleMethod = "";

		if (!StringUtil.isEmpty(wxOrder.get("settleMethod"))) {
			settleMethod = wxOrder.get("settleMethod").toString();
		}
		
		//入账状态  默认为'0', '1':入账成功  '2'：入账失败 '3'：状态未知
		String accountedFlag = "";
		if (!StringUtil.isEmpty(wxOrder.get("accountedFlag"))) {
			accountedFlag = wxOrder.get("accountedFlag").toString();
		}
		
		//String txnChannel = wxOrder.get("txnChannel").toString();
		
		//需同时满足交易成功——02；三码支付——8001；清算方式T+0；入账未成功：不为1
		if (StringConstans.OrderState.STATE_02.equals(txnSta) && "8001".equals(txnChannel)
				&& "0".equals(settleMethod) && !"1".equals(accountedFlag)
				&& ("02".equals(payAccessType) || "03".equals(payAccessType))) {
			wxOrder.put("acctNo", accountNo);
			wxOrder.put("merName", merName);
			wxOrder.put("merId", merId);
			wxOrder.put("orgCode", orgCode);
			settleInput.setParams(wxOrder);
			this.settleThreadHandler(settleInput);
		}
		
	}
	private void settleThreadHandler(InputParam settleInput) {
		NotifyMessage notifyMessage = new NotifyMessage();
		this.getSettleMessageToCore(notifyMessage, settleInput);
		ThreadNotifyHelper.notifyThread(notifyMessage);
		
	}

	*//**
	 * T+0清算报文体所需信息
	 * @param notifyMessage
	 * @param settleInput
	 *//*
	private void getSettleMessageToCore(NotifyMessage notifyMessage, InputParam settleInput) {
		//交易流水
		String txnSeqId = settleInput.getValue("txnSeqId").toString();
		
		//交易日期
		String txnDt = settleInput.getValue("txnDt").toString();
		
		//交易时间
		String txnTm = settleInput.getValue("txnTm").toString();
		
		//收款方账号
		String accountNo = String.format("%-22s", settleInput.getValue("acctNo"));
		
		//货币代码
		String currencyCode = settleInput.getValue("currencyCode").toString();
		if (currencyCode.equals("156")) {
			currencyCode = String.format("%3s", "CNY");
		}
		
		//商户名称
		String merName = settleInput.getValue("merName").toString();
		//计算字符串中汉字
		int chnLen = merName.getBytes().length - merName.length();  
		if (chnLen == 0) {
			merName = String.format("%-80s", merName);
		} else {
			//计算字符串中汉字有几部分
			int chnArrLen = EBCDICGBK.chnArrLen(merName); 
			//转码后0E表示汉字开始，0F表示汉字结束，所以有一部分汉字就需要减去两个字节
			merName = String.format("%-80s", merName).substring(0, 80 - chnLen - chnArrLen * 2);
		}
		
		//付款方账号
		String outAccountNo = String.format("%-22s","");
		
		
		//交易金额
		double money = Double.valueOf(settleInput.getValue("tradeMoney").toString());
		String tradeMoney = String.format("%17.2f", money/100);
		logger.info("[T+0清算] 交易金额" + tradeMoney);
		
		//费率
		String chargeFee = String.format("%-17s", "");
		
		//对账编号
		String checkAccountNo = String.format("%-15s", "");
		
		//商户编号(pos老系统上送商户编号是20位，新系统上送15位，因为核心只能存储15位商户编号，故将20位商户编号截取15位
		String merId = (String) settleInput.getValue("merId");
		merId = merId.getBytes().length > 15? merId.substring(0,3) + merId.substring(8) : merId;
		merId = String.format("%15s", merId);
		logger.info("[T+0清算] 商户编号" + merId);
		
		String payAccessType = settleInput.getValue("payAccessType").toString();
		if ("02".equals(payAccessType)) {
			payAccessType = "微信";
		} 
		int payAccessLen = payAccessType.getBytes().length - payAccessType.length();
		payAccessType = String.format("%-10s", payAccessType).substring(0, 10 - payAccessLen - 2);
		
		//付款方姓名
		String payerName = String.format("%-80s",payAccessType).substring(0, 80 - payAccessLen - 2);
		
		//机构号
		String orgCode = String.format("%-6s", settleInput.getValue("orgCode"));
		
		//附言
		String postscript = String.format("%-80s", "");
		
		//备注
		String remark = "";
		if (!StringUtil.isEmpty(settleInput.getValue("remark"))) {
			chnLen = remark.getBytes().length - remark.length();  
			if (chnLen == 0) {
				remark = String.format("%-80s", remark);
			} else {
				//计算字符串中汉字有几部分
				int chnArrLen = EBCDICGBK.chnArrLen(remark); 
				//转码后0E表示汉字开始，0F表示汉字结束，所以有一部分汉字就需要减去两个字节
				remark = String.format("%-80s", remark).substring(0, 80 - chnLen - chnArrLen * 2);
			}
		}else {
			remark = String.format("%-80s", "");
		}
		
		//备用
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
		
	}*/
	@Override
	public OutputParam getWxAuthCode(InputParam input) throws FrameException {
		
		logger.info("--------------------去微信后台获取 openId  START -----------------------");
		
		OutputParam outputParam = new OutputParam();
		
		try {
			
			//授权码
			String  authCode = String.format("%s", input.getValue("code"));
			//授权模式
			String  grantType = String.format("%s", input.getValue("grantType"));
			//微信分配给商户的APPID
			String appId = Constants.getParam("jg_appId");
			//秘钥信息
			String secret = Constants.getParam("wx_jsapi_secret");
			
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("appid", appId);
			paramMap.put("secret", secret);
			paramMap.put("code", authCode);
			paramMap.put("grant_type", grantType);
			
			String respResult = HttpRequestClient.httpRequest(Constants.getParam("wx_Auth_Url"),paramMap);
			JSONObject json = JSONObject.fromObject(respResult);
			
			if(json.containsKey("errcode")){
				logger.error("[去微信后台获取 openId]失败:" + json.getString("errcode"));
				outputParam.setReturnMsg( json.getString("errcode"));
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				return outputParam;
			}
			
			//用户的OpenId
			String openId = json.getString("openid");
			
			outputParam.putValue("openId", openId);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			
		} catch (Exception e) {
			logger.error("[去微信后台获取 openId]去微信后台获取 openId处理失败：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("获取openId失败");
		}
		
		return outputParam;
	}
	
	/**
	 * 线上微信统一下单请求处理
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam toWxUnifiedOrderOnLine(InputParam input) throws FrameException {

		logger.info("---------------- 调用微信接口线上微信支付统一下单   START---------------------");
		
		OutputParam  outputParam = new OutputParam();
		
		try {
			
			logger.info("[调用微信接口线上微信支付统一下单] 开始组装下单报文");
			
			//支付类型
			String payType = String.format("%s", input.getValue("payType"));
			
			String merId = String.format("%s", input.getValue("merId"));
			String subWxMerId = String.format("%s", input.getValue("subWxMerId"));
			
			String key = Constants.getParam("jg_key");
			String appid = Constants.getParam("jg_appId");
			String mch_Id = Constants.getParam("jg_merId");
			
			
			//数据集合
			Map<String, Object> dataMap = input.getParams();
			dataMap.put("appid", appid);
			dataMap.put("mch_id", mch_Id);
			
			// 组装统一下单报文对象
			UnifiedOrderData unifiedOrderData = new UnifiedOrderData(dataMap, key);
			
			Map<String,String> requestMap = new HashMap<String,String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_unifiedOrder_url"));
			requestMap.put(Dict.pfxPath, Constants.getParam("wx_jg_pfx_path"));
			requestMap.put(Dict.pfxPwd, Constants.getParam("wx_jg_pwd"));
			
			// 发送统一下单请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap,unifiedOrderData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);

			logger.info("调用微信接口线上微信支付统一下单返回的响应报文：" + respString);
			
			//响应吗
			String returnCode = String.format("%s", respMap.get("return_code"));
			//响应信息
			String returnMsg =  String.format("%s", respMap.get("return_msg"));
			if (!"SUCCESS".equals(returnCode)) {
				logger.error("调用微信接口线上微信支付统一下单下单,微信返回失败:" + returnMsg);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("调用微信接口线上微信支付统一下单,微信返回失败:" + returnMsg);
				return outputParam;
			}
			
			/************************ 验证报文签名信息 ******************************/
			
			// 返回报文中的sign签名
			String backSign = String.format("%s", respMap.get("sign"));
			
			//移除返回报文的签名信息
			respMap.remove("sign");
			
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				logger.error("[调用微信接口线上微信支付统一下单]接收的报文签名验证不通过");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("报文签名验证不通过");
				return outputParam;
			}
			
			/************************ 处理微信返回的报文信息 ******************************/
			String resultCode = String.format("%s", respMap.get("result_code"));
			if (!"SUCCESS".equals(resultCode)) {
				logger.error("调用微信接口线上微信支付统一下单失败:" + respMap.get("err_code_des").toString());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("调用微信接口线上微信支付统一下单失败:" + respMap.get("err_code_des").toString());
				return outputParam;
			}
			
			//二维码链接
			String codeUrl = String.format("%s", respMap.get("code_url"));
			//预支付交易会话标识
			String prepayId = String.format("%s", respMap.get("prepay_id"));
			//交易类型
			String tradeType = String.format("%s", respMap.get("trade_type"));
			
			outputParam.putValue("prepayId", prepayId);
			outputParam.putValue("tradeType", tradeType);
			outputParam.putValue("codeUrl", codeUrl);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			
			logger.info("---------------- 调用微信接口线上微信支付统一下单  END ----------------");
			
		} catch (Exception e) {
			logger.error("[调用微信接口线上微信支付统一下单] 调用微信接口线上微信支付统一下单出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("调用微信接口线上微信支付统一下单出现异常");
			logger.info("---------------- 调用微信接口线上微信支付统一下单出现异常  END ----------------");
		} 

		return outputParam;
	}
	
	/**
	 * 线上微信统一下单请求处理
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam toWxUnifiedOrderOnLineYL(InputParam input) throws FrameException {

		logger.info("---------------- 调用微信接口线上微信支付统一下单   START---------------------");
		
		OutputParam  outputParam = new OutputParam();
		
		try {
			
			HashMap<String, String> data = new HashMap<String, String>();
			/**
			 * 组装请求报文
			 */
			data.put("sub_appid", "wxecbccc7f8e0f752d");
			data.put("sub_mch_id", "24973215");
			data.put(Dict.sub_mch_id, StringUtil.toString(input.getValue(Dict.subMchId)));
			data.put(Dict.out_trade_no, StringUtil.toString(input.getValue(Dict.out_trade_no))); // 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母，且在同一个商户号下唯一。
//			data.put(Dict.fee_type, StringUtil.toString(input.getValue(Dict.fee_type))); // 符合ISO 4217标准的三位字母代码，默认人民币：CNY
			data.put(Dict.total_fee, StringUtil.toString(input.getValue(Dict.total_fee))); // 订单总金额，只能为整数
			data.put("device_info", StringUtil.toString(input.getValue("device_info"))); // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
			data.put("trade_type", StringUtil.toString(input.getValue("trade_type"))); // 交易类型 JSAPI 公众号支付 NATIVE 扫码支付 APP APP支付

			data.put(Dict.body, StringUtil.toString(input.getValue(Dict.body))); // 商品或支付单简要描述，格式要求：门店品牌名-城市分店名-实际商品名称
			data.put(Dict.spbill_create_ip, StringUtil.toString(input.getValue(Dict.spbill_create_ip))); // 终端IP APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP
			data.put(Dict.notify_url, Constants.getParam("tsdk.notifyUrl")); // 通知地址
																			// 接收银联异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。
			data.put(Dict.sign_type, "RSA");
			data.put(Dict.attach, StringUtil.toString(input.getValue(Dict.attach))); // 附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据

			data.put(Dict.time_start, StringUtil.toString(input.getValue(Dict.time_start)));// 交易起始时间 订单生成时间，格式为yyyyMMddHHmmss
			data.put(Dict.time_expire,StringUtil.toString(input.getValue(Dict.time_expire)));
			data.put(Dict.limit_pay, StringUtil.toString(input.getValue("limitPay"))); // 指定支付方式 no_credit--指定不能使用信用卡支付
			data.put(Dict.openid,StringUtil.toString(input.getValue("openId")));
			
			String url = Constants.getParam("tsdk.frontTransUrl") + Constants.getParam("tsdk.prepayUrlSuffix");
			String cacheKey = StringConstans.PAY_ChANNEL.YLWX + "_20";
			
			Map<String, String> resultMap = ylwxPayService.wxSend(data, cacheKey, url);
			

			if (!StringConstans.WxTradeStatus.SUCCESS.equals(resultMap.get(Dict.return_code))) {
				logger.error("调用银联微信接口线上微信支付统一下单下单,微信返回失败:" + resultMap.get(Dict.return_msg));
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("调用银联微信接口线上微信支付统一下单,微信返回失败:" + resultMap.get(Dict.return_msg));
				return outputParam;
			}
			
			if (!StringConstans.WxTradeStatus.SUCCESS.equals(resultMap.get(Dict.result_code))) {
				logger.error("调用银联微信接口线上微信支付统一下单下单,微信返回失败:" + resultMap.get(Dict.err_code_des));
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("调用银联微信接口线上微信支付统一下单,微信返回失败:" + resultMap.get(Dict.err_code_des));
				return outputParam;
			}
			
			//二维码链接
			String codeUrl = String.format("%s", resultMap.get("code_url"));
			//预支付交易会话标识
			String prepayId = String.format("%s", resultMap.get("prepay_id"));
			//交易类型
			String tradeType = String.format("%s", resultMap.get("trade_type"));
			
			String wcPayData = String.format("%s", resultMap.get("wc_pay_data"));
		
			outputParam.putValue("prepayId", prepayId);
			outputParam.putValue("tradeType", tradeType);
			outputParam.putValue("codeUrl", codeUrl);
			outputParam.putValue("wcPayData", wcPayData);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
		
			logger.info("---------------- 调用银联微信接口线上微信支付统一下单  END ----------------");
			
		} catch (Exception e) {
			logger.error("[调用银联微信接口线上微信支付统一下单] 调用微信接口线上微信支付统一下单出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("调用微信接口线上微信支付统一下单出现异常");
			logger.info("---------------- 调用微信接口线上微信支付统一下单出现异常  END ----------------");
		} 

		return outputParam;
	}
	
	/**
	 * 微信支付统一下单新增订单表信息
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam addConsumeOrder(InputParam input) throws FrameException {
		logger.info("[微信支付统一下单,新增订单信息] addConsumeOrder START"+input.toString());
		OutputParam out = new OutputParam();
		try {
			// 新增订单
			String channel = input.getValue(Dict.channel).toString();
			InputParam orderInput = new InputParam();
			String txnSeqId = orderService.getOrderNo(channel);
			String txnDt = DateUtil.format(new Date(), DateUtil.YYYYMMDD);
			String txnTm = DateUtil.format(new Date(), DateUtil.HHMMSS);
			String orderTime = input.getValue(Dict.orderTime).toString();
			String wxMerId = input.getValue(Dict.wxMerId).toString();
			
			orderInput.putparamString(Dict.txnDt, txnDt);
			orderInput.putparamString(Dict.txnTm, txnTm);
			orderInput.putparamString(Dict.merOrderId, input.getValue(Dict.orderNumber).toString());
			orderInput.putparamString(Dict.merOrDt, orderTime.substring(0, 8));
			orderInput.putparamString(Dict.merOrTm, orderTime.substring(8, 14));
			orderInput.putparamString(Dict.txnType, StringConstans.TransType.TRANS_CONSUME);
			orderInput.putparamString(Dict.txnChannel, channel );
			orderInput.putparamString(Dict.payAccessType, input.getValue(Dict.payAccessType).toString());
			orderInput.putparamString(Dict.payType, input.getValue(Dict.payType).toString());
			orderInput.putparamString(Dict.tradeMoney, input.getValue(Dict.orderAmount).toString());
			orderInput.putparamString(Dict.currencyCode, input.getValue(Dict.currencyType).toString());
			orderInput.putparamString(Dict.merId, input.getValue(Dict.merId).toString());
			orderInput.putparamString(Dict.wxMerId, wxMerId);
			orderInput.putparamString(Dict.subWxMerId, input.getValue(Dict.subWxMerId).toString());
			orderInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_01);
			orderInput.putparamString(Dict.accountedFlag, StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN);
			
			if(!StringUtil.isEmpty(input.getValue(Dict.resDesc))){
				orderInput.putparamString(Dict.resDesc, input.getValue(Dict.resDesc).toString());
			}
			//三码合一时需要该字段  与 三码合一静态二维码关联
			if(!StringUtil.isEmpty(input.getValue(Dict.ewmData))){
				orderInput.putparamString(Dict.ewmData, input.getValue(Dict.ewmData).toString());
			}
			if (!StringUtil.isEmpty(input.getValue(Dict.remark))) {
				orderInput.putparamString(Dict.remark, input.getValue(Dict.remark) .toString());
			}
			if (!StringUtil.isEmpty(input.getValue(Dict.bankFeeRate))) {
				orderInput.putparamString(Dict.bankFeeRate, input.getValue(Dict.bankFeeRate) .toString());
			}
			if (!StringUtil.isEmpty(input.getValue(Dict.settleMethod))) {
				orderInput.putparamString(Dict.settleMethod, input.getValue(Dict.settleMethod) .toString());
			}
			
			
			orderInput.putparamString(Dict.txnSeqId, txnSeqId);
			
			OutputParam orderOutPut = orderService.insertOrder(orderInput);
			
			out.putValue(Dict.txnDt, txnDt);
			out.putValue(Dict.txnTm, txnTm);
			out.putValue(Dict.txnSeqId, txnSeqId);
			out.setReturnCode(orderOutPut.getReturnCode());
			out.setReturnMsg(orderOutPut.getReturnMsg());
		} catch (Exception e) {
			logger.error("微信支付统一下单新增订单表流水失败:" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("[微信支付统一下单,新增订单信息]微信支付统一下单新增订单表流水失败"+e.getMessage());
		}
		logger.info("[微信支付统一下单,新增订单信息] addConsumeOrder END"+out.toString());
		return out;
	}

	/**
	 * 微信支付被扫撤销新增订单表信息
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam addRevokeOrder(InputParam input) throws FrameException {
		OutputParam out = new OutputParam();
		try {
			logger.info("[微信支付被扫撤销,新增订单信息]");
			// 新增订单

			InputParam orderInput = new InputParam();
			String txnSeqId = sequenceDao.getTxnSeqId();
			String txnDt = DateUtil.format(new Date(), DateUtil.YYYYMMDD);
			String txnTm = DateUtil.format(new Date(), DateUtil.HHMMSS);
			String orderTime = input.getValue("orderTime").toString();
			String initTxnSeqId = input.getValue("initTxnSeqId").toString();
			String initTxnTime = input.getValue("initTxnTime").toString();
			String wxMerId = input.getValue("wxMerId").toString();
			
			orderInput.putparamString("oglOrdId", initTxnSeqId);
			orderInput.putparamString("oglOrdDate", initTxnTime);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("merOrderId", input.getValue("orderNumber").toString());
			orderInput.putparamString("merOrDt", orderTime.substring(0, 8));
			orderInput.putparamString("merOrTm", orderTime.substring(8, 14));
			orderInput.putparamString("txnType", StringConstans.TransType.TRANS_REVOKE);
			orderInput.putparamString("txnChannel", input.getValue("channel").toString());
			orderInput.putparamString("payAccessType", input.getValue("payAccessType").toString());
			orderInput.putparamString("tradeMoney", input.getValue("orderAmount").toString());
			orderInput.putparamString("currencyCode", input.getValue("currencyType").toString());
			orderInput.putparamString("merId", input.getValue("merId").toString());
			orderInput.putparamString("wxMerId", wxMerId);
			orderInput.putparamString("subWxMerId", input.getValue("subWxMerId").toString());
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("payType", StringConstans.Pay_Type.PAY_TYPE_MICRO);
			
			if(!StringUtil.isEmpty(input.getValue("resDesc"))){
				orderInput.putparamString("resDesc", input.getValue("resDesc").toString());
			}
			orderInput.putparamString("txnSeqId", txnSeqId);
			
			OutputParam orderOutPut = orderService.insertOrder(orderInput);
			if (!StringConstans.returnCode.SUCCESS.equals(orderOutPut.getReturnCode())) {
				logger.error("[微信支付被扫撤销,订单表新增失败]");
				return orderOutPut;
			}
			
			out.putValue("txnDt", txnDt);
			out.putValue("txnTm", txnTm);
			out.putValue("txnSeqId", txnSeqId);
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
		} catch (Exception e) {
			logger.error("微信支付被扫撤销新增订单表流水失败:" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信支付被扫撤销新增订单表流水失败");
			return out;
		}
		return out;
	}
	
	/**
	 * 微信支断直连撤销新增订单表信息
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam addRevokeOrderYL(InputParam input) throws FrameException {
		OutputParam out = new OutputParam();
		try {
			logger.info("[微信支断直连撤销新增订单表信息] addRevokeOrderYL START"+input.toString());
			// 新增订单

			InputParam orderInput = new InputParam();
			String txnSeqId = sequenceDao.getTxnSeqId();
			String txnDt = DateUtil.format(new Date(), DateUtil.YYYYMMDD);
			String txnTm = DateUtil.format(new Date(), DateUtil.HHMMSS);
			String orderTime = input.getValue("orderTime").toString();
			String initTxnSeqId = input.getValue("initTxnSeqId").toString();
			String initTxnTime = input.getValue("initTxnTime").toString();
			String wxMerId = input.getValue("wxMerId").toString();
			
			orderInput.putparamString("oglOrdId", initTxnSeqId);
			orderInput.putparamString("oglOrdDate", initTxnTime);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("merOrderId", input.getValue("orderNumber").toString());
			orderInput.putparamString("merOrDt", orderTime.substring(0, 8));
			orderInput.putparamString("merOrTm", orderTime.substring(8, 14));
			orderInput.putparamString("txnType", StringConstans.TransType.TRANS_REVOKE);
			orderInput.putparamString("txnChannel", input.getValue("channel").toString());
			orderInput.putparamString("payAccessType", input.getValue("payAccessType").toString());
			orderInput.putparamString("tradeMoney", input.getValue("orderAmount").toString());
			orderInput.putparamString("currencyCode", input.getValue("currencyType").toString());
			orderInput.putparamString("merId", input.getValue("merId").toString());
			orderInput.putparamString("wxMerId", wxMerId);
			orderInput.putparamString("subWxMerId", input.getValue("subWxMerId").toString());
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("payType", StringConstans.Pay_Type.PAY_TYPE_MICRO);
			
			if(!StringUtil.isEmpty(input.getValue("resDesc"))){
				orderInput.putparamString("resDesc", input.getValue("resDesc").toString());
			}
			orderInput.putparamString("txnSeqId", txnSeqId);
			
			OutputParam orderOutPut = orderService.insertOrder(orderInput);
			
			out.putValue("txnDt", txnDt);
			out.putValue("txnTm", txnTm);
			out.putValue("txnSeqId", txnSeqId);
			out.setReturnCode(orderOutPut.getReturnCode());
			out.setReturnMsg(orderOutPut.getReturnMsg());
		} catch (Exception e) {
			logger.error("微信断直连支付被扫撤销新增订单表流水失败:" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("微信断直连支付被扫撤销新增订单表流水失败");
		}
		return out;
	}
	
	/**
	 * 更新微信订单状态
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam updateConsumeOrder(InputParam input) throws FrameException {
		logger.info("开始更新微信订单表状态......");
		try {
			OutputParam orderOut = orderService.updateWxOrderInfo(input);
			return orderOut;
		} catch (Exception e) {
			logger.error("更新订单状态失败:" + e.getMessage(),e);
			OutputParam out = new OutputParam();
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("更新订单状态失败");
			return out;
		}
	}
	
	/**
	 * 查询微信订单信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam toQueryWxOrder(InputParam input) throws FrameException{

		logger.info("---------------- 调用微信API查询接口流程  START ----------------");
		
		OutputParam outputParam = new OutputParam();
		
		try {
			
			String key = StringUtil.toString(input.getValue("key"));
			String appid = StringUtil.toString(input.getValue("appid"));
			String mchId = StringUtil.toString(input.getValue("mchId"));
			String subMchId = StringUtil.toString(input.getValue("subMchId"));
			String outTradeNo = StringUtil.toString(input.getValue("outTradeNo"));
			String pfxPath = StringUtil.toString(input.getValue("pfxPath"));
			String pwd = StringUtil.toString(input.getValue("pwd"));
			
			//数据集合
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("appid", appid);
			dataMap.put("mch_id", mchId);
			dataMap.put("subMchId", subMchId);
			dataMap.put("outTradeNo", outTradeNo);
			
			// 组装统一下单报文对象
			OrderQueryReqData orderQueryReqData = new OrderQueryReqData(dataMap, key);
			
			Map<String,String> requestMap = new HashMap<String,String>();
			requestMap.put(Dict.requestUrl, Constants.getParam("wx_orderQuery_url"));
			requestMap.put(Dict.pfxPath, pfxPath);
			requestMap.put(Dict.pfxPwd, pwd);
			
			// 发送统一下单请求
			String respString = HttpRequestClient.sendWxHttpsReq(requestMap,orderQueryReqData);
			Map<String, Object> respMap = Util.getMapFromXML(respString);
			
			logger.info("[调用微信API查询接口] 调用微信API查询接口响应报文：" + respString);

			//响应吗
			String returnCode = String.format("%s", respMap.get("return_code"));
			//响应信息
			String returnMsg =  String.format("%s", respMap.get("return_msg"));
			if (!"SUCCESS".equals(returnCode)) {
				logger.error("[调用微信API查询接口],微信响应失败:" + returnMsg);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("调用微信API查询接口,微信返回失败:" + returnMsg);
				return outputParam;
			}
						
			/************************ 验证报文签名信息 ******************************/
						
			// 返回报文中的sign签名
			String backSign = String.format("%s", respMap.get("sign"));
						
			//移除返回报文的签名信息
			respMap.remove("sign");
						
			String validateSign = Signature.getSign(respMap, key);
			if (!backSign.equals(validateSign)) {
				logger.error("[调用微信API查询接口,] 接收的报文签名验证不通过");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("报文签名验证不通过");
				return outputParam;
			}
						
			/************************ 处理微信返回的报文信息 ******************************/
			String resultCode = String.format("%s", respMap.get("result_code"));
			if (!"SUCCESS".equals(resultCode)) {
				logger.error("[调用微信API查询接口] 去微信去查询失败:" + respMap.get("err_code_des").toString());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("去微信去查询失败:" + respMap.get("err_code_des").toString());
				return outputParam;
			}
			
			//将查询到的结果放到返回集合中去
			this.putQueryResult(outputParam, respMap);
			
			//交易状态
			String tradeStatus =  String.format("%s", respMap.get("trade_state"));
			//订单状态描述
			String tradeStateDesc =  String.format("%s", respMap.get("trade_state_desc"));
			
			Map<String, String>  map = this.wxStatusConvertOrderStatu(tradeStatus, tradeStateDesc);
			
			outputParam.putValue("orderSta", map.get("orderState"));
			outputParam.putValue("orderDesc",map.get("orderDesc"));
			outputParam.setReturnMsg("查询成功");
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			
			logger.info("---------------- 调用微信API查询接口  END ----------------");
			
		} catch (Exception e) {
			logger.error("[调用微信API查询接口] 调用微信API查询接口出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("调用微信API查询接口出现异常");
		}
		
		return outputParam;
		
	}
	
	private  void  putQueryResult(OutputParam outputParam,Map<String, Object> map){
		
		//交易状态
		String tradeStatus =  String.format("%s", map.get("trade_state"));
		//设备号
		String deviceInfo =  String.format("%s", map.get("device_info"));
		//用户标识
		String openid =  String.format("%s", map.get("openid"));
		//是否关注公众账号
		String isSubscribe =  String.format("%s", map.get("is_subscribe"));
		//交易类型
		String tradeType =  String.format("%s", map.get("trade_type"));
		//付款银行
		String bankType =  String.format("%s", map.get("bank_type"));
		//标价金额
		String totalFee =  String.format("%s", map.get("total_fee"));
		//应结订单金额
		String settlementTotalFee =  String.format("%s", map.get("settlement_total_fee"));
		//标价币种
		String feeType =  String.format("%s", map.get("fee_type"));
		//现金支付金额
		String cashFee =  String.format("%s", map.get("cash_fee"));
		//现金支付金额币种
		String cashFeeType =  String.format("%s", map.get("cash_fee_type"));
		//代金券使用数量
		String couponCount =  String.format("%s", map.get("coupon_count"));
		//代金券金额
		String couponFee =  String.format("%s", map.get("coupon_fee"));
		//微信支付订单号
		String wxOrderNo =  String.format("%s", map.get("transaction_id"));
		//商户订单号
		String outTradeNo =  String.format("%s", map.get("out_trade_no"));
		//附加数据
		String attach =  String.format("%s", map.get("attach"));
		//支付完成时间
		String timeEnd =  String.format("%s", map.get("time_end"));
		//订单状态描述
		String tradeStateDesc =  String.format("%s", map.get("trade_state_desc"));
		
		outputParam.putValue("tradeStatus", tradeStatus);
		outputParam.putValue("outTradeNo", outTradeNo);
	
		if(!StringUtil.isEmpty(deviceInfo)){
			outputParam.putValue("deviceInfo", deviceInfo);
		}
		
		if(!StringUtil.isEmpty(openid)){
			outputParam.putValue("openid", openid);
		}
		
		if(!StringUtil.isEmpty(isSubscribe)){
			outputParam.putValue("isSubscribe", isSubscribe);
		}
		
		if(!StringUtil.isEmpty(tradeType)){
			outputParam.putValue("tradeType", tradeType);
		}
		
		if(!StringUtil.isEmpty(bankType)){
			outputParam.putValue("bankType", bankType);
		}
		
		if(!StringUtil.isEmpty(totalFee)){
			outputParam.putValue("totalFee", totalFee);
		}
		
		if(!StringUtil.isEmpty(cashFee)){
			outputParam.putValue("cashFee", cashFee);
		}
		
		if(!StringUtil.isEmpty(settlementTotalFee)){
			outputParam.putValue("settlementTotalFee", settlementTotalFee);
		}
		
		if(!StringUtil.isEmpty(feeType)){
			outputParam.putValue("feeType", feeType);
		}
			
		if(!StringUtil.isEmpty(couponFee)){
			outputParam.putValue("couponFee", couponFee);
		}
		
		if(!StringUtil.isEmpty(cashFeeType)){
			outputParam.putValue("cashFeeType", cashFeeType);
		}
		
		if(!StringUtil.isEmpty(couponCount)){
			outputParam.putValue("couponCount", couponCount);
		}
		
		if(!StringUtil.isEmpty(wxOrderNo)){
			outputParam.putValue("wxOrderNo", wxOrderNo);
		}
		
		if(!StringUtil.isEmpty(attach)){
			outputParam.putValue("attach", attach);
		}

		if(!StringUtil.isEmpty(timeEnd)){
			outputParam.putValue("settleDate", timeEnd);
		}
		
		if(!StringUtil.isEmpty(tradeStateDesc)){
			outputParam.putValue("tradeStateDesc", tradeStateDesc);
		}
		
		//代金券类型
		Map<String,Object> couponTypeMap = StringUtil.getMapByRegx("^coupon_type_\\$[0-9]+$", map);
		//代金券ID
		Map<String,Object> couponIdMap = StringUtil.getMapByRegx("^coupon_id_\\$[0-9]+$", map);
		//单个代金券支付金额
		Map<String,Object> couponFeeMap = StringUtil.getMapByRegx("^coupon_fee_\\$[0-9]+$", map);
		
		Util.mapCopy(outputParam.getReturnObj(), couponTypeMap);
		Util.mapCopy(outputParam.getReturnObj(), couponIdMap);
		Util.mapCopy(outputParam.getReturnObj(), couponFeeMap);
		
	}

	public Map<String, String>  wxStatusConvertOrderStatu(String wxStatus,String wxStatusDesc){
	
	Map<String, String> map = new HashMap<String, String>();
	
	if(StringConstans.WxTradeStatus.SUCCESS.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_02);
		map.put("orderDesc", "交易成功");
		
	}else if(StringConstans.WxTradeStatus.CLOSED.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_09);
		map.put("orderDesc", "订单已关闭");
		
	}else if(StringConstans.WxTradeStatus.REFUND.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_02);
		map.put("orderDesc", "交易成功");
		
	}else if(StringConstans.WxTradeStatus.REVOKED.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_02);
		map.put("orderDesc", "订单已撤销");
		
	}else if(StringConstans.WxTradeStatus.USERPAYING.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_01);
		map.put("orderDesc", "用户未支付,等待支付");
		
	}else if(StringConstans.WxTradeStatus.NOTPAY.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_01);
		map.put("orderDesc", "用户未支付");
		
	}else if(StringConstans.WxTradeStatus.PAYERROR.equals(wxStatus)){
		
		map.put("orderState", StringConstans.OrderState.STATE_03);
		map.put("orderDesc", "交易失败:" + wxStatusDesc);
	}
	
	return map;
}
	

	public IOrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public IOrderDao getOrderDao() {
		return orderDao;
	}

	public void setOrderDao(IOrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public ISequenceDao getSequenceDao() {
		return sequenceDao;
	}

	public void setSequenceDao(ISequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public OrderQueryManager getOrderQueryManager() {
		return orderQueryManager;
	}

	public void setOrderQueryManager(OrderQueryManager orderQueryManager) {
		this.orderQueryManager = orderQueryManager;
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

	public void setMerchantChannelService(
			IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

	public YLWXPayService getYlwxPayService() {
		return ylwxPayService;
	}

	public void setYlwxPayService(YLWXPayService ylwxPayService) {
		this.ylwxPayService = ylwxPayService;
	}

	
}
