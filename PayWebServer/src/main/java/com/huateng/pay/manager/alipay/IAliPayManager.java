package com.huateng.pay.manager.alipay;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

/**
 * 支付宝支付交易业务处理接口(往支付宝)
 * 
 * @author zhaoyuanxiang
 * 
 */
public interface IAliPayManager {
	/**
	 * 支付宝交易欲创建接口（生成支付宝二维码）
	 */
	public OutputParam toALiPayPreCreate(InputParam input) throws FrameException;
	
	
	/**
	 * 支付宝交易建接口（生成支付宝二维码）
	 */
	public OutputParam toALiPayCreate(InputParam input) throws FrameException;
	
	/**
	 * 支付宝统一收单交易支付接口 （扫描设备扫描支付宝付款码，读取二维码/条形码/声波信息后通过该接口送至支付宝发起支付）
	 */
	public OutputParam toPayALiPayOrder(InputParam input) throws FrameException;
	
	
   /**
     * 支付宝统一收单线下交易查询接口
     */
	public OutputParam toQueryALiPayOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝统一收单交易撤销接口
	 */
	public OutputParam toRevokeALiPayOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝断直连统一收单交易撤销接口
	 */
	public OutputParam toRevokeALiPayOrderYL(InputParam input) throws FrameException;
			
	/**
	 * 支付宝统一收单交易退款接口
	 */
	public OutputParam toALiPayRefundOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝统一收单交易退款查询接口
	 */
	public OutputParam toQueryALiPayRefundOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝统一收单交易关闭接口
	 */
	public OutputParam toCloseALiPayOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝断直连统一收单交易关闭接口
	 */
	public OutputParam toCloseALiPayOrderYL(InputParam input) throws FrameException;
	
	/**
	 * 支付宝统一查询账单接口
	 */
	public OutputParam toQueryBill(InputParam input) throws FrameException;
	
	/**
	 * 支付宝添加撤销订单
	 * */
	public OutputParam addRevokeOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝添加下单
	 * */
	public OutputParam addConsumeOrder(InputParam input) throws FrameException;
	
	/**
	 * 支付宝退款更新退款累计总金额
	 * */
	public OutputParam upTotalRefundFee (InputParam input)throws FrameException;
	
	/**
	 * 获取令牌
	 */
	public OutputParam getAuthToken(InputParam input) throws FrameException;


	public OutputParam toALiPayPreCreateYL(InputParam preInput);


	public OutputParam toPayALiPayOrderYL(InputParam scanedPayInput);


	public OutputParam toQueryALiPayOrderYL(InputParam queryInput);


	public OutputParam toALiPayCreateYL(InputParam preInput);
}
