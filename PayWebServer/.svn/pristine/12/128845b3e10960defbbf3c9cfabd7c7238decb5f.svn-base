package com.huateng.pay.services.threecode;

import java.io.IOException;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;

public interface IThreeCodeService {
	
	/**
	 * 查询三码合一微信和支付宝订单信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	OutputParam queryWxAndAlipayUnknowOrder(InputParam input) throws FrameException;
	
	/**
	 * 生成三码合一微信和支付宝对账单
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	OutputParam creatWxAndAlipayBill(InputParam input) throws FrameException;

	/**
	 * 下载三码和一流水
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	OutputParam downloadThreeCodeBill(InputParam input) throws FrameException;
	
	/**
	 * 完善后的对账单生成
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	OutputParam creatWxAndAlipayBill0(InputParam input) throws FrameException;
	
	
	/**
	 * 向核心发送T+0入账交易
	 */
	OutputParam accountedTxnToCore(InputParam input) throws FrameException;
	
	
	/**
	 * 丰收家查询三码合一流水
	 * @return
	 * @throws IOException
	 */
	OutputParam queryThreeCodeStatement(InputParam input) throws IOException;

}
