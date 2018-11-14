package com.huateng.pay.services.alipay;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

/**
 * 支付宝商户信息同步接口
 * 
 * @author zhaoyuanxiang
 * 
 */
public interface AliPayMerchantSynchService {
	/**
	 * 查询支付宝同步商户
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam queryALiPayMer(InputParam input) throws FrameException;
	
	/**
	 * 查询支付宝断直连同步商户
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam queryALiPayMerYL(InputParam input) throws FrameException;

	/**
	 * 
	 * 新增支付宝同步商户
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam addALiPayMer(InputParam input) throws FrameException;
	
	/**
	 * 
	 * 新增支付宝同步商户
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	public OutputParam addALiPayMerYL(InputParam input) throws FrameException;
	
    /**
     * 
     * 修改支付宝同步商户
     * 
     * @param input
     * @return
	 * @throws FrameException
     */
    public OutputParam modifyALiPayMer(InputParam input) throws FrameException;
    
    /**
     * 
     * 修改支付宝间连商户
     * 
     * @param input
     * @return
	 * @throws FrameException
     */
    public OutputParam modifyALiPayMerYL(InputParam input) throws FrameException;
    
    /**
     * 
     * 删除支付宝同步商户(暂不实现)
     * 
     * @param input
     * @return
	 * @throws FrameException
     */
    public OutputParam deleteALiPayMer(InputParam input) throws FrameException;
    
    public OutputParam routing(InputParam input);

	public OutputParam transferAliMer(InputParam input);

}
