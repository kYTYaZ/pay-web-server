package com.huateng.pay.action;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.services.alipay.AliPayPayService;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.util.Util;
import com.huateng.utils.FileUtil;
import com.huateng.utils.Signature;
import com.wldk.framework.utils.MappingUtils;

public class AlipayAction extends BaseAction {
	private static final long serialVersionUID = -3200289333344802927L;
	private Logger logger = LoggerFactory.getLogger(AlipayAction.class);
	private AliPayPayService aliPayPayService;
	private IOrderService orderService;
	private IMerchantChannelService merchantChannelService ;

	/**
	 * 直连通知
	 * @return
	 */
	public void recvAlipayNotifyReq() {
		notifyReqCommon(StringConstans.PAY_ChANNEL.ALI);
	}
	/**
	 * 间联通知
	 * @return
	 */
	public void recvAlipayNotifyReqYL() {
		notifyReqCommon(StringConstans.PAY_ChANNEL.YLALI);
		
	}
	
	public void notifyReqCommon(String payChannel) {
		logger.info("[支付宝支付接收后台通知]支付宝后台通知START,请求参数payChannel:"+payChannel);
		PrintWriter writer = null;
		try {
			String sign = request.getParameter(Dict.sign);
			String signType = request.getParameter(Dict.sign_type);

			Map<String, Object> paramMap = new HashMap<String, Object>();

			Enumeration<String> enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String paramName = enumeration.nextElement();
				String paramValue = request.getParameter(paramName);
				if (!paramName.equals(Dict.sign) && !paramName.equals(Dict.sign_type)) {
					paramMap.put(paramName, paramValue);
				}
			}

			try {
				writer = response.getWriter();
				logger.info("[支付宝支付接收后台通知] 支付宝后台处理成功,向支付宝返回sucess");
				writer.write("success");
				writer.flush();
				writer.close();
			} catch (Exception e) {
				logger.error("[支付宝支付接收后台通知] 返回响应宝物异常" + e.getMessage(),e);
			} finally {
				if (writer != null) {
					writer.write("success");
					writer.flush();
					writer.close();
				}
			}
			logger.info("[支付宝支付接收后台通知]开始进行签名验证,参数:"+paramMap.toString());

			String outTradeNo = String.format("%s", paramMap.get(Dict.out_trade_no));

			Pattern p = Pattern.compile("[a-zA-Z]*");
			Matcher m = p.matcher(outTradeNo);
			String prefix = m.find() ? m.group() : "";
			logger.debug("[支付宝预下单后台通知处理] prefix=" + prefix);
			
			outTradeNo = outTradeNo.substring(prefix.length(),outTradeNo.length());
			logger.debug("[支付宝预下单后台通知处理] 处理后的outTradeNo=" + outTradeNo);
			
			// 二维码交易流水号
			String txnSeqId = String.format("%s%s", prefix,outTradeNo.substring(0, 10));
			logger.debug("[支付宝预下单后台通知处理] 处理后的txnSeqId=" + txnSeqId);
			
			String txnDt = outTradeNo.substring(10, 18);
			String txnTm = outTradeNo.substring(18, 24);
			
			// 公钥证书
			String publicKey ="";
			
			logger.debug("[支付宝预下单后台通知处理] txnSeqId=" + txnSeqId + ",txnDt=" + txnDt + ",txnTm=" + txnTm);

			InputParam queryInput = new InputParam();
			queryInput.putparamString("txnSeqId", txnSeqId);
			queryInput.putparamString("txnDt", txnDt);
			queryInput.putparamString("txnTm", txnTm);

			logger.debug("[支付宝预下单后台通知处理]  根据外部订单号查询订单是否存在开始,请求报文:"+queryInput.toString());
			OutputParam queryOut = orderService.queryOrder(queryInput);
			logger.debug("[支付宝预下单后台通知处理]  根据外部订单号查询订单是否存在 结束,返回报文:"+queryOut.toString());
			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[支付宝预下单后台通知处理]  根据外部订单号查询订单没有找到匹配的记录");
				return ;
			}
			String merId = queryOut.getValue("merId").toString();
			String subAlipayMerId = queryOut.getValue("subAlipayMerId").toString();
			String channel = queryOut.getValue("txnChannel").toString();
			
			if(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				InputParam inputQuery = new InputParam();
				inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);
				inputQuery.putparamString(Dict.merId, merId);
				inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
				OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
				if(StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
					String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
					String cacheKey = payChannel + "_" + rateChannel;
					
					Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
					publicKey = configMap.get(Dict.ALI_PUBLIC_KEY);
				}
			} else if(StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				publicKey = Constants.getParam("alipay_public_key");
			} 
			
			
			boolean signFlag = Signature.getSign(paramMap, sign, signType, StringConstans.Charsets.UTF_8, publicKey);
			logger.info("[支付宝支付接收后台通知]完成进行签名验证  signFlag=" + signFlag);

			if (!signFlag) {
				logger.error("[支付宝支付接收后台通知]报文验签失败");
				return ;
			}


			InputParam inputParam = new InputParam();
			inputParam.putMap(paramMap);
			logger.debug("[支付宝支付接收后台通知] 调用后台通知处理接口    开始");
			OutputParam outputParam = aliPayPayService.recvALiPayNotifyReq(inputParam);
			logger.debug("[支付宝支付接收后台通知] 调用后台通知处理接口    结束");

			if (!StringConstans.returnCode.SUCCESS.equals(outputParam.getReturnCode())) {
				logger.error("[支付宝支付接收后台通知] 支付宝后台通知处理失败:" + outputParam.getReturnMsg());
				return ;
			}

		} catch (Exception e) {
			logger.error("[支付宝支付接收后台通知] 支付宝后台通知处理异常" + e.getMessage(), e);
		} finally {
			logger.info("[支付宝支付接收后台通知]支付宝后台通知END");
		}
		return ;
		
	}
	

	public AliPayPayService getAliPayPayService() {
		return aliPayPayService;
	}

	public void setAliPayPayService(AliPayPayService aliPayPayService) {
		this.aliPayPayService = aliPayPayService;
	}

	public IOrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public IMerchantChannelService getMerchantChannelService() {
		return merchantChannelService;
	}

	public void setMerchantChannelService(IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

	public static void main(String[] args) throws Exception {
		File file = new File("D:\\al_21.del");

		List<String> result = new ArrayList<String>();
		List<String> list = FileUtil.readFileToList(file);
		for (String str : list) {
			String[] strs = str.split(",");
			String amount = strs[0].replaceAll("\"", "");
			String id = strs[1].replaceAll("\"", "");
			System.out.println(amount);
			String amt = Util.fillString(Util.transTxnAt(0, amount), 12);
			System.out.println(amt);
			result.add("update tbl_order_txn set txn_sta='02',receipt_amount='" + amt + "' where txn_seq_id='" + id
					+ "';");

		}
		FileUtil.writeToTxt(new File("D:\\ali_21new20180423.sql"), result, "UTF-8");

	}

}
