package com.huateng.pay.action;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.services.cups.ICupsPayService;
import com.huateng.utils.Signature;
import com.huateng.utils.Util;

@Controller
@RequestMapping(value="/pay/cups/")
public class CupsPayAction2/* extends BaseAction */{

//	private static final long serialVersionUID = 1L;
	private Logger logger = LoggerFactory.getLogger(CupsPayAction2.class);
	@Autowired
	private ICupsPayService cupsPayService;

	

	/**
	 * 接收银联返回C2B消费结果通知
	 */
	@RequestMapping("recvCupsNotifyReq.mvc")
	public String recvCupsNotifyReq(HttpServletRequest request,HttpServletResponse response) {
		logger.info("收到银联返回通知");
		PrintWriter writer = null;
		Map<String, String> outMap = null;
		@SuppressWarnings("unused")
		OutputParam outputParam = null;
		try {
			writer = response.getWriter();
			
			Map<String, String> reqParam = getAllRequestParam(request);
        
			//交易类型
			String reqType = String.format("%s", reqParam.get("reqType").toString());
			logger.info("[接收银联返回通知] 交易类型：" + reqType);
			
	        logger.info("接收银联后台通知的报文参数："+reqParam.toString());
			
	        outMap = new HashMap<String, String>();
			outMap.put("version", StringConstans.CupsEwmInfo.CUPS_EWM_VERSION);
			outMap.put("reqType", reqType);
	        
			/************************  验证报文签名信息    ******************************/
			logger.info("[银联二维码支付后台通知]  开始进行签名验证");
			if(!Signature.validate(reqParam, "UTF-8")){
				logger.error("[银联二维码支付后台通知]接收的报文签名验证不通过");
				outMap.put("respCode", "11");
				outMap.put("respMsg", "FAILED");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				send(writer, rspDataStr);
				return null;
			}
			logger.info("[银联二维码支付后台通知]  签名验证成功");
			
			InputParam inputParam  = new InputParam();
			inputParam.putMapString(reqParam);
			
			outputParam = new OutputParam();
			logger.info("[银联二维码支付后台通知] 调用后台通知处理接口    开始");
			if (reqType.equals(StringConstans.CupsTradeType.C2B_ADD_HANDLER)) {
				outMap.put("respCode", "00");
				outMap.put("respMsg", "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				send(writer, rspDataStr);
				//C2B附加处理
				outputParam  = cupsPayService.C2BScanedAttachHandler(inputParam);
				
			} else if (reqType.equals(StringConstans.CupsTradeType.CONSUME_RESULT_NOTIFY)) {
				outMap.put("respCode", "00");
				outMap.put("respMsg", "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				send(writer, rspDataStr);
				//C2B消费结果通知   银联发通知到收款方（）
				outputParam = cupsPayService.C2BEWMConsumeResultNotifyHandler(inputParam);
				
			} else if (reqType.equals(StringConstans.CupsTradeType.C2B_TXN_NOTIFY) 
					|| reqType.equals(StringConstans.CupsTradeType.C2B_CONSUME_REVERDE)
					|| reqType.equals(StringConstans.CupsTradeType.CONSUME_TXN_REFOUND) 
					|| reqType.equals(StringConstans.CupsTradeType.CONSUME_TXN_CANCEL)) {
				//C2B交易通知 银联发通知到付款方   (本行二维码被扫)
				outMap.put("respCode", "00");
				outMap.put("respMsg", "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				send(writer, rspDataStr);
				outputParam = cupsPayService.C2BEWMNotifyToLimitCenter(inputParam);
			}
			
			logger.info("------------银联二维码支付后台通知处理流程ACTION    END-----------");
			
		} catch (Exception e) {
			logger.error("银联二维码支付后台通知处理流程理异常" + e.getMessage(),e);
		} finally {
			if (writer != null) {
				outMap.put("respCode", "00");
				outMap.put("respMsg", "SUCCESS");
				Map<String, String> rspData = Signature.sign(outMap,"UTF-8");
				String rspDataStr = Util.getRequestParamString(rspData);
				send(writer, rspDataStr);
			}
		}
		return null;
	}
	
    /**
     * 获取request参数并转化Map
     * @param request
     * @return
     */
    public Map<String, String> getAllRequestParam(HttpServletRequest request){
    	logger.debug("开始 [ request.getParameterNames 转换 Map<String, String>]");
    	HashMap<String,String>	requestParam = new HashMap<String,String>();
        Enumeration temp = request.getParameterNames();
      if (null != temp) {
        while (temp.hasMoreElements()) {
          String en = (String)temp.nextElement();
          String value = request.getParameter(en);
          logger.info("[ ky"+en+",value:"+value+" ]");
          requestParam.put(en,value );
          if ((requestParam.get(en) == null) || (requestParam.get(en) == ""))
          {
        	  requestParam.remove(en);
          }
        }
      }
      logger.debug("完成 [ request.getParameterNames 转换 Map<String, String>]");
      return requestParam;
    }  
    
    protected void send(PrintWriter writer, String str) {
		try {
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
    }
	
	public ICupsPayService getCupsPayService() {
		return cupsPayService;
	}

	public void setCupsPayService(ICupsPayService cupsPayService) {
		this.cupsPayService = cupsPayService;
	}
}
