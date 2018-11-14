package com.huateng.pay.dao.inter;

import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;
import com.wldk.framework.db.PageVariable;

/**
 * @author Administrator
 *
 */
public interface ITimingTaskDao {
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
	public void timingCopyTblOrderInfoToHisByDate(Map<String, String> insertMap) throws FrameException;
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
	 *查询微信和支付宝订单
	 * 
	 * @return
	 * @throws FrameException
	 */
	public List<Map<String, Object>> timingQueryOrderWxOrAli(Map<String, String> queryMap)throws FrameException;
	
	/**
	 * 查询三码合一订单
	 * @param queryMap
	 * @return
	 * @throws FrameException
	 */
	public List<Map<String, Object>> timingQueryThreeCodeOrder(Map<String, String> queryMap,PageVariable page) throws FrameException;
	
	/**
	 * 查询三码合一订单总数
	 * @param queryMap
	 * @return
	 * @throws FrameException
	 */
	public int queryThreeCodeOrderNumber(Map<String, String> queryMap) throws FrameException;
	
	/**
	 * 查询定时任务处理标志
	 * @throws FrameException
	 */
	public List<Map<String, Object>> queryTimingTaskHandler(Map<String, String> insertMap) throws FrameException;
}
