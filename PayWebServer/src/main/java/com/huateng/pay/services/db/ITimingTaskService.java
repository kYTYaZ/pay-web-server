package com.huateng.pay.services.db;

import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.wldk.framework.db.PageVariable;

public interface ITimingTaskService {
	/**
	 * 定时从二维码信息将数据拷贝到历史表
	 */
	public void timingCopyTblEwmInfoToHis() throws FrameException;
	/**
	 * 定时删除二维码历史表中的数据
	 */
	public void timingDelteTblEwmInfo() throws FrameException;
	/**
	 *  定时从订单表信息将数据拷贝到历史表
	 */
	public void timingCopyTblOrderInfoToHis() throws FrameException;
	/**
	 * 定时从订单表信息将数据拷贝到历史表
	 * @param inputParam
	 * @throws FrameException
	 */
	public OutputParam  timingCopyTblOrderInfoToHisByDate(InputParam inputParam) throws FrameException;
	/**
	 * 定时删除订单表中的历史数据
	 */
	public void timingDelteTblOrderInfo() throws FrameException;
	
	/**
	 * 插入定时任务处理标志
	 * @throws FrameException
	 */
	public boolean insertTimingTaskHandler(Map<String, String> insertMap) throws FrameException; 
	/**
	 * 定时查询交易状态为01、06的订单
	 * @throws FrameException
	 */
	public OutputParam timingQueryIndefiniteOrderWxOrAli(Map<String, String> queryMap) throws FrameException;

	/**
	 * 定时打包日志并压缩
	 * @throws FrameException
	 */
	public void timingPackLogByZip() throws FrameException;
	
	
	/**
	 * 查询三码合一支付宝和微信消费订单
	 * @param queryMap
	 * @return
	 * @throws FrameException
	 */
	public OutputParam timingQueryThreeCodeBills(Map<String, String> queryMap, PageVariable page) throws FrameException;
	
	/**
	 * 查询三码合一支付宝和微信消费订单总数
	 * @param queryMap
	 * @return
	 * @throws FrameException
	 */
	public int queryThreeCodeOrderNumber(Map<String, String> queryMap)throws FrameException;
	
	/**
	 * 查询定时任务处理标志
	 * @throws FrameException
	 */
	public List<Map<String, Object>> queryTimingTaskHandler(Map<String, String> insertMap) throws FrameException;
	
}
