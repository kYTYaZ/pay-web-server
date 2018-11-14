package com.huateng.pay.action;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.services.cups.ICupsPayService;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;

public class CupsPayAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(CupsPayAction.class);
	private ICupsPayService cupsPayService;

	

	/**
	 * 接收银联返回C2B消费结果通知
	 */
	public String recvCupsNotifyReq() {
		logger.debug("收到银联返回通知");
		PrintWriter writer = null;
		Map<String, String> outMap = null;
		@SuppressWarnings("unused")
		OutputParam outputParam = null;
		try {
			writer = response.getWriter();
			
			Map<String, String> reqParam = getAllRequestParam();
        
			String reqType = StringUtil.toString(reqParam.get(Dict.reqType));
	        logger.info("接收银联后台通知的报文参数："+reqParam.toString());
			
	        outMap = new HashMap<String, String>();
			outMap.put(Dict.version, StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			outMap.put(Dict.reqType, reqType);
	        
			/************************  验证报文签名信息    ******************************/
			//logger.info("[银联二维码支付后台通知]  开始进行签名验证");
			if(!Signature.validate(reqParam, "UTF-8")){
				logger.debug("[银联二维码支付后台通知]接收的报文签名验证不通过");
				outMap.put(Dict.respCode, "11");
				outMap.put(Dict.respMsg, "FAILED");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				logger.info("[银联二维码支付后台通知]响应银联的报文参数" + rspDataStr);
				send(writer, rspDataStr);
				return null;
			}
			//logger.info("[银联二维码支付后台通知]  签名验证成功");
			
			InputParam inputParam  = new InputParam();
			inputParam.putMapString(reqParam);
			
			outputParam = new OutputParam();
			//logger.info("[银联二维码支付后台通知] 调用后台通知处理接口    开始");
			if (reqType.equals(StringConstans.CupsTradeType.C2B_ADD_HANDLER)) {
				outMap.put(Dict.respCode, "00");
				outMap.put(Dict.respMsg, "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				logger.info("[银联二维码支付后台通知]响应银联的报文参数" + rspDataStr);
				send(writer, rspDataStr);
				//C2B附加处理
				outputParam  = cupsPayService.C2BScanedAttachHandler(inputParam);
				
			} else if (reqType.equals(StringConstans.CupsTradeType.CONSUME_RESULT_NOTIFY)) {
				outMap.put(Dict.respCode, "00");
				outMap.put(Dict.respMsg, "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				logger.info("[银联二维码支付后台通知]响应银联的报文参数" + rspDataStr);
				send(writer, rspDataStr);
				//C2B消费结果通知   银联发通知到收款方（）
				outputParam = cupsPayService.C2BEWMConsumeResultNotifyHandler(inputParam);
				
			} else if (reqType.equals(StringConstans.CupsTradeType.C2B_ZS_NOTIFY)){
				outMap.put(Dict.respCode, "00");
				outMap.put(Dict.respMsg, "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				logger.info("[银联二维码支付后台通知]响应银联的报文参数" + rspDataStr);
				send(writer, rspDataStr);
				
				//C2B主扫交易结果通知
				String origReqType = StringUtil.toString(reqParam.get(Dict.origReqType));
				if ("0130000903".equals(origReqType)) {
					outputParam = cupsPayService.C2BEWMDealResultNotifyHandler(inputParam);
				}
				
			} else if ( reqType.equals(StringConstans.CupsTradeType.C2B_TXN_NOTIFY)
					||reqType.equals(StringConstans.CupsTradeType.C2B_CONSUME_REVERDE)
					|| reqType.equals(StringConstans.CupsTradeType.CONSUME_TXN_REFOUND) 
					|| reqType.equals(StringConstans.CupsTradeType.CONSUME_TXN_CANCEL)) {
				//C2B交易通知 银联发通知到付款方   (本行二维码被扫)
				outMap.put(Dict.respCode, "00");
				outMap.put(Dict.respMsg, "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				logger.info("[银联二维码支付后台通知]响应银联的报文参数" + rspDataStr);
				send(writer, rspDataStr);
				outputParam = cupsPayService.C2BEWMNotifyToLimitCenter(inputParam);
			}
			
			//logger.info("------------银联二维码支付后台通知处理流程ACTION    END-----------");
			
		} catch (Exception e) {
			logger.error("银联二维码支付后台通知处理流程理异常" + e.getMessage(), e);
		} finally {
			if (writer != null) {
				outMap.put(Dict.respCode, "00");
				outMap.put(Dict.respMsg, "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				send(writer, rspDataStr);
			}
			
		}
		return null;
	}
	
	public ICupsPayService getCupsPayService() {
		return cupsPayService;
	}

	public void setCupsPayService(ICupsPayService cupsPayService) {
		this.cupsPayService = cupsPayService;
	}
}
