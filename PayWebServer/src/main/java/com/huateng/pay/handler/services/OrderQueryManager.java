package com.huateng.pay.handler.services;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.IOrderDao;
import com.huateng.pay.manager.weixin.IWxManager;
import com.huateng.pay.services.cups.ICupsPayService;
import com.huateng.pay.services.weixin.WxPayService;

/**
 * 订单查询处理类
 * 
 * @author guohuan
 * @see 1.0
 * @since 1.0
 */
public class OrderQueryManager{
    private static Logger logger = LoggerFactory.getLogger(OrderQueryManager.class);
    private IOrderDao orderDao;
    private WxPayService wxPayService;
    private IWxManager wxManager;
    private ICupsPayService cupsPayService;
    /**
     * 微信被扫支付订单查询处理
     * @see 1.0
     * @since 1.0
     */
	public OutputParam microOrderQuery(InputParam orderInput,OutputParam orderOutput,int deep) throws FrameException {
    	
		if (deep > 2000) {
			// 具体数值由虚拟机内存大小和每次递归堆栈大小决定，一般在2000次左右
			deep = 2000;
		}
		
		if (--deep < 0) {
			return orderOutput;
		}
		
		String txnSeqId = orderInput.getValue("txnSeqId").toString();
		String txnDt = orderInput.getValue("txnDt").toString();
		String txnTm = orderInput.getValue("txnTm").toString();
		String subMchId = orderInput.getValue("subWxMerId").toString();
		String payType = orderInput.getValue("payType").toString();
		String merId = orderInput.getValue("merId").toString();
		
		String wxMerId = orderInput.getValue("wxMerId").toString();
		
		int times = Integer.parseInt(orderInput.getValue("times").toString());
		logger.info("*************开始进行微信后台订单查询处理("+txnSeqId+")****************");
    	try {
    		logger.info("[微信后台订单查询] 第  "+times+" 次查询");
    		InputParam updateInput = new InputParam();
    		updateInput.putParams("txnSeqId", txnSeqId);
    		updateInput.putParams("wxMerId", wxMerId);
    		updateInput.putParams("subMchId", subMchId);
    		updateInput.putParams("subWxMerId", subMchId);
    		updateInput.putParams("payType", payType);
    		updateInput.putParams("txnDt", txnDt);
    		updateInput.putParams("txnTm", txnTm);
    		updateInput.putParams("merId", merId);
    		OutputParam out = wxPayService.queryWxOrder(updateInput);
    		
    		String wxOrderNo = StringUtil.isNull(out.getValue("wxOrderNo"))?"":out.getValue("wxOrderNo").toString();
    		updateInput.putParams("wxOrderNo", wxOrderNo);
    		if(!StringConstans.returnCode.SUCCESS.equals(out.getReturnCode())){
    			logger.error("[微信后台订单查询] 查询处理异常");
    			updateInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
    			updateInput.putParams("resDesc", "交易异常");
    			
    			//返回响应状态
    			orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
    			orderOutput.putValue(Dict.respDesc, "交易异常");
    			
    			//更新订单状态
    			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    			if(!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())){
    				logger.error("[微信后台订单查询] 更新订单状态异常");
    			}
    		}else{
    			String tradeState = out.getValue("tradeState").toString();//支付状态
    			if("SUCCESS".equals(tradeState)){
    				logger.info("[微信后台订单查询] 支付成功");
    				
    				//微信支付完成时间
        			String payTime = out.getValue("settleDate").toString();
        			
    				updateInput.putParams("txnSta", StringConstans.OrderState.STATE_02);
        			updateInput.putParams("resDesc", StringConstans.RespDesc.RESP_DESC_02);
        			updateInput.putParams("settleDate", payTime); //tpf 2016-04-01
        			updateInput.putParams("payerid", out.getValue("openid").toString()); //tpf 2016-04-01
        			updateInput.putParams("bankType", out.getValue("bankType").toString()); //tpf 2016-04-01
        			
        			
        			//返回响应状态
        			orderOutput.putValue(Dict.resCode, StringConstans.OrderState.STATE_02);
        			orderOutput.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_02);
        			orderOutput.putValue("wxPayTime", payTime); //tpf 2016-04-01
        			orderOutput.putValue("wxOrderNo", wxOrderNo);
        			
        			//更新订单状态
        			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    			}else if("USERPAYING".equals(tradeState)){
    				logger.info("[微信后台订单查询] 正在支付中");
    				if(8 < times){  // posp要求轮询时间90秒  20180227
    					logger.error("[微信后台订单查询] 30秒内用户未支付完成,订单状态更新为04交易超时");
    					updateInput.putParams("txnSta", StringConstans.OrderState.STATE_04);
            			updateInput.putParams("resDesc", StringConstans.RespDesc.RESP_DESC_04);
            			
            			//返回响应状态
            			orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_04);
            			orderOutput.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_04);
            			
            			//更新订单状态
            			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    				}else{
    					//用户正在支付中,等待10秒再次重新查询
    					logger.info("[微信后台订单查询] 用户正在支付中,等待10秒重新查询支付结果");
    					orderInput.putParams("times", times + 1);
    					orderInput.putParams("merId", merId);
    					
    					//再过10秒重新查询
    					
    					Thread.sleep(10 * 1000);
    					microOrderQuery(orderInput,orderOutput,deep);
    					
    				}
    			}else{
    				logger.info("[微信后台订单查询] 支付未成功");
    				updateInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
    				updateInput.putParams("resDesc", "交易支付失败");
    				
    				//返回响应状态
    				orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
    				orderOutput.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_03);
    				
    				//更新订单状态
    				OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    			}
    		}
    		
			return orderOutput;
    	} catch (Exception e) {
            logger.error("[微信后台订单查询异常:" + e.getMessage() + "]",e);
            InputParam failInput = new InputParam();
            failInput.putParams("txnSta", StringConstans.OrderState.STATE_03);
            failInput.putParams("txnSeqId", txnSeqId);
            failInput.putParams("txnDt", txnDt);
            failInput.putParams("txnTm", txnTm);
            failInput.putParams("payType", payType);
            failInput.putParams("wxMerId", wxMerId);
			failInput.putParams("resDesc", "交易异常");

			//异常情况下的处理
			//更新订单状态
			OutputParam updateOut = wxManager.updateConsumeOrder(failInput);
			
			//返回响应状态
			orderOutput.putValue("resCode", StringConstans.OrderState.STATE_03);
			orderOutput.putValue("resDesc", "交易异常");
			return orderOutput;
        }
    }
	
	 /**
     * 微信被扫支付订单查询处理
     * @see 1.0
     * @since 1.0
     */
	public OutputParam microOrderQueryYL(InputParam orderInput,OutputParam orderOutput) throws FrameException {
    	
		String txnSeqId = ObjectUtils.toString(orderInput.getValue(Dict.txnSeqId));
		String txnDt = ObjectUtils.toString(orderInput.getValue(Dict.txnDt));
		String txnTm = ObjectUtils.toString(orderInput.getValue(Dict.txnTm));
		String subMchId = ObjectUtils.toString(orderInput.getValue(Dict.sub_mch_id));
		String payType = ObjectUtils.toString(orderInput.getValue(Dict.payType));
		String merId = ObjectUtils.toString(orderInput.getValue(Dict.merId));
		
		int times = Integer.parseInt(ObjectUtils.toString(orderInput.getValue(Dict.times)));
		logger.info("*************开始进行微信后台订单查询处理("+txnSeqId+")****************");
    	try {
    		logger.info("[微信后台订单查询] 第  "+times+" 次查询");
    		InputParam updateInput = new InputParam();
    		updateInput.putParams(Dict.txnSeqId, txnSeqId);
    		updateInput.putParams(Dict.subMchId, subMchId);
    		updateInput.putParams(Dict.sub_mch_id, subMchId);
    		updateInput.putParams(Dict.payType, payType);
    		updateInput.putParams(Dict.txnDt, txnDt);
    		updateInput.putParams(Dict.txnTm, txnTm);
    		updateInput.putParams(Dict.merId, merId);
    		updateInput.putParams(Dict.rate, orderInput.getValue(Dict.rate));
    		OutputParam out = wxPayService.queryWxOrderYL(updateInput);
    		logger.info("[第"+times+"次微信断直连被扫轮询结果]"+out.toString());
    		String wxOrderNo = ObjectUtils.toString(out.getValue(Dict.wxOrderNo));
    		updateInput.putParams(Dict.wxOrderNo, wxOrderNo);
    		if(!StringConstans.returnCode.SUCCESS.equals(out.getReturnCode())){
    			updateInput.putParams(Dict.txnSta, out.getValue(Dict.txnSta));
    			updateInput.putParams(Dict.resDesc, out.getValue(Dict.respDesc));
    			
    			//返回响应状态
    			orderOutput.putValue(Dict.respCode, out.getValue(Dict.respCode));
    			orderOutput.putValue(Dict.respDesc, out.getValue(Dict.respDesc));
    			
    			//更新订单状态
    			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    		}else{
    			String errCode = out.getValueString(Dict.err_code);
				if (StringConstans.WxErrorCode.SYSTEM_ERROR.equals(errCode)) {
					//查询2次都是系统异常  任务交易异常
					if (2 < times) {
						updateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_03);
		    			updateInput.putParams(Dict.resDesc, out.getValue(Dict.respDesc));
		    			
		    			//返回响应状态
		    			orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
		    			orderOutput.putValue(Dict.respDesc, out.getValue(Dict.respDesc));
		    			
		    			//更新订单状态
		    			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
		    			return orderOutput;
					} else {
						logger.info("[微信后台订单查询] 系统错误  等待10秒重新查询支付结果");
						orderInput.putParams(Dict.times, times + 1);

						// 再过10秒重新查询

						Thread.sleep(10 * 1000);
						microOrderQueryYL(orderInput, orderOutput);
					}
				}
    			String tradeState = out.getValueString(Dict.trade_state);//支付状态
    			if(Dict.SUCCESS.equals(tradeState)){
    				logger.info("[微信后台订单查询] 支付成功");
    				
    				//微信支付完成时间
        			String payTime = out.getValueString(Dict.time_end);
        			
    				updateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_02);
        			updateInput.putParams(Dict.resDesc, StringConstans.RespDesc.RESP_DESC_02);
        			updateInput.putParams(Dict.settleDate, payTime);
        			updateInput.putParams(Dict.payerid, out.getValueString(Dict.openid)); 
        			updateInput.putParams(Dict.bankType, out.getValueString(Dict.bank_type));
        			
        			//返回响应状态
        			orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_02);
        			orderOutput.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_02);
        			orderOutput.putValue(Dict.wxPayTime, payTime); //tpf 2016-04-01
        			orderOutput.putValue(Dict.wxOrderNo, out.getValueString(Dict.transaction_id));
        			
        			//更新订单状态
        			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    			}else if("USERPAYING".equals(tradeState)){
    				logger.info("[微信后台订单查询] 正在支付中");
    				if(8 < times){  // posp要求轮询时间90秒  20180227
    					logger.error("[微信后台订单查询] 30秒内用户未支付完成,订单状态更新为04交易超时");
    					updateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_04);
            			updateInput.putParams(Dict.resDesc, StringConstans.RespDesc.RESP_DESC_04);
            			
            			//返回响应状态
            			orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_04);
            			orderOutput.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_04);
            			
            			//更新订单状态
            			OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    				}else{
    					//用户正在支付中,等待10秒再次重新查询
    					logger.info("[微信后台订单查询] 用户正在支付中,等待10秒重新查询支付结果");
    					orderInput.putParams(Dict.times, times + 1);
    					orderInput.putParams(Dict.merId, merId);
    					
    					//再过10秒重新查询
    					
    					Thread.sleep(10 * 1000);
    					microOrderQueryYL(orderInput,orderOutput);
    					
    				}
    			}else{
    				updateInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_03);
    				updateInput.putParams(Dict.resDesc, "交易支付失败");
    				
    				//返回响应状态
    				orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
    				orderOutput.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_03);
    				
    				//更新订单状态
    				OutputParam updateOut = wxManager.updateConsumeOrder(updateInput);
    			}
    		}
    		
			return orderOutput;
    	} catch (Exception e) {
            logger.error("[微信后台订单查询异常:" + e.getMessage() + "]",e);
            InputParam failInput = new InputParam();
            failInput.putParams(Dict.txnSta, StringConstans.OrderState.STATE_03);
            failInput.putParams(Dict.txnSeqId, txnSeqId);
            failInput.putParams(Dict.txnDt, txnDt);
            failInput.putParams(Dict.txnTm, txnTm);
            failInput.putParams(Dict.payType, payType);
			failInput.putParams(Dict.resDesc, "交易异常");

			//异常情况下的处理
			//更新订单状态
			OutputParam updateOut = wxManager.updateConsumeOrder(failInput);
			
			//返回响应状态
			orderOutput.putValue(Dict.respCode, StringConstans.OrderState.STATE_03);
			orderOutput.putValue(Dict.respDesc, "交易异常");
			return orderOutput;
        }
    }


	
	/**
	 * 银联二维码被扫支付订单查询处理
	 * 
	 * @author Yuyk
	 * @see 1.0
	 * @since 1.0
	 */
	public OutputParam c2bMicroOrderQuery(InputParam orderInput,OutputParam orderOutput, int deep) throws FrameException {
		if (deep > 2000) {
			// 具体数值由虚拟机内存大小和每次递归堆栈大小决定，一般在2000次左右
			deep = 2000;
		}

		if (--deep < 0) {
			return orderOutput;
		}

		String qrNo = orderInput.getValue("qrNo").toString();
		String txnTm = orderInput.getValue("txnTm").toString();
		String txnDt = orderInput.getValue("txnTm").toString();

		int times = Integer.parseInt(orderInput.getValue("times").toString());
		logger.info("*************开始进行银联后台订单查询处理(" + qrNo+ ")****************");
		try {
			logger.debug("[银联后台订单查询] 第  " + times + " 次查询");
			InputParam updateInput = new InputParam();
			updateInput.putParams("orderTime", txnDt + txnTm);
			updateInput.putParams("orderNo", qrNo);
			OutputParam out = cupsPayService.C2BEWMScanedConsumeQuery(updateInput);

			if (!StringConstans.returnCode.SUCCESS.equals(out.getReturnCode())) {
				logger.error("[银联后台订单查询] 查询处理异常");
				// 返回响应状态
				orderOutput.putValue("resDesc", "交易异常");
				orderOutput.setReturnCode(StringConstans.returnCode.FAIL);
			} else {
				String txnSta = out.getValue("txnSta").toString();// 支付状态
				if (StringConstans.OrderState.STATE_02.equals(txnSta)) {
					logger.info("[银联后台订单查询] 支付成功");

					// 银联支付完成时间
					String payTime = out.getValue("settleDate").toString();

					// 返回响应状态
					orderOutput.putValue("txnSta",StringConstans.OrderState.STATE_02);
					orderOutput.putValue("settleDate", payTime);
					orderOutput.putValue("resDesc",StringConstans.RespDesc.RESP_DESC_02);
					orderOutput.setReturnCode(StringConstans.RespCode.RESP_CODE_02);
					orderOutput.setReturnMsg("银标二维码 他行二维码被扫消费交易成功");
				} else if ("06".equals(txnSta)) {
					logger.info("[银联后台订单查询] 正在支付中");
					if (8 < times) { // posp要求轮询时间90秒 20180227
						logger.error("[银联后台订单查询] 30秒内用户未支付完成,订单状态更新为04交易超时");

						// 返回响应状态
						orderOutput.putValue("txnSta",StringConstans.OrderState.STATE_04);
						orderOutput.putValue("resCode",StringConstans.OrderState.STATE_04);
						orderOutput.putValue("resDesc",StringConstans.RespDesc.RESP_DESC_04);

					} else {
						// 用户正在支付中,等待10秒再次重新查询
						logger.info("[银联后台订单查询] 用户正在支付中,等待10秒重新查询支付结果");
						orderInput.putParams("times", times + 1);
						// 再过10秒重新查询
						Thread.sleep(10 * 1000);
						c2bMicroOrderQuery(orderInput, orderOutput, deep);
					}
				} else {
					logger.info("[银联后台订单查询] 支付未成功");
					// 返回响应状态
					orderOutput.putValue("txnSta",
							StringConstans.OrderState.STATE_03);
					orderOutput.putValue("resCode",
							StringConstans.OrderState.STATE_03);
					orderOutput.putValue("resDesc",
							StringConstans.RespDesc.RESP_DESC_03);
					orderOutput.setReturnCode(StringConstans.returnCode.FAIL);
				}
			}
			return orderOutput;
		} catch (Exception e) {
			logger.error("[银联后台订单查询异常:" + e.getMessage() + "]");
			// 返回响应状态
			orderOutput.putValue("resCode", StringConstans.OrderState.STATE_03);
			orderOutput.putValue("resDesc", "交易异常");
			orderOutput.setReturnCode(StringConstans.returnCode.FAIL);
			return orderOutput;
		}
	}
	
	
	public ICupsPayService getCupsPayService() {
		return cupsPayService;
	}

	public void setCupsPayService(ICupsPayService cupsPayService) {
		this.cupsPayService = cupsPayService;
	}

	public WxPayService getWxPayService() {
		return wxPayService;
	}

	public void setWxPayService(WxPayService wxPayService) {
		this.wxPayService = wxPayService;
	}

	public IWxManager getWxManager() {
		return wxManager;
	}

	public void setWxManager(IWxManager wxManager) {
		this.wxManager = wxManager;
	}

	public IOrderDao getOrderDao() {
		return orderDao;
	}

	public void setOrderDao(IOrderDao orderDao) {
		this.orderDao = orderDao;
	}
	
}
