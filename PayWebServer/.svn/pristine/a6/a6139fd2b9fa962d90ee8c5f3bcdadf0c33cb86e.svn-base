package com.huateng.pay.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.inter.ITimingTaskDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;
import com.wldk.framework.db.PageVariable;
import com.wldk.framework.db.ParameterProvider;

public class TimingTaskDaoImpl extends JdbcSpringDaoFromWorkManagerUtil  implements ITimingTaskDao {
	/**
	 * 定时从二维码信息将数据拷贝到历史表
	 */
	@Override
	public void timingCopyTblEwmInfoToHis() throws FrameException{
		try {
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("sqlName", "timingCopyTblEwmInfoToHis");
            execute(paramMap, SqlVmConstants.TimingTask.TIMING_TASK_001);
            
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        }
	}
	/**
	 * 定时删除二维码历史表中的数据
	 */
	@Override
	public void timingCopyTblOrderInfoToHis() throws FrameException{
		try {
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("sqlName", "timingCopyTblOrderInfoToHis");
            execute(paramMap, SqlVmConstants.TimingTask.TIMING_TASK_001);
            
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        }
	}
	
	/**
	 * 定时从订单表信息将数据拷贝到历史表
	 * @param inputParam
	 * @throws FrameException
	 */
	@Override
	public void timingCopyTblOrderInfoToHisByDate(Map<String, String> paramMap)
			throws FrameException {
		try {	
			
			paramMap.put("sqlName", "timingCopyTblOrderInfoToHisByDate");
			execute(paramMap, SqlVmConstants.TimingTask.TIMING_TASK_001);
            
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        }
	}
	
	/**
	 *  定时从订单表信息将数据拷贝到历史表
	 */
	@Override
	public void timingDelteTblEwmInfo() throws FrameException{
		try {
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("sqlName", "timingDelteTblEwmInfo");
            execute(paramMap, SqlVmConstants.TimingTask.TIMING_TASK_001);
            
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        }

	}
	/**
	 * 定时删除订单表中的历史数据
	 */
	@Override
	public void timingDelteTblOrderInfo() throws FrameException{
		try {
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("sqlName", "timingDelteTblOrderInfo");
            execute(paramMap, SqlVmConstants.TimingTask.TIMING_TASK_001);
            
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        }
	}
	
	/**
	 * 插入定时任务处理标志
	 * @throws FrameException
	 */
	@Override
	public boolean insertTimingTaskHandler(Map<String, String> insertMap) throws FrameException {

		try{
		
			insertMap.put("sqlName", "insertTimingTaskHandler");
			return executeUpdate(insertMap, SqlVmConstants.TakeTimingTaskResult.TAKE_TIMING_TASK_RESULT_001);
	
		}catch (Exception e) {
			throw new FrameException(e.getMessage());
		}
		
	}
	
	/**
	 * 查询微信和支付宝订单
	 * @param queryMap 
	 * @throws FrameException
	 */
	@Override
	public List<Map<String, Object>> timingQueryOrderWxOrAli(Map<String, String> queryMap) throws FrameException {
		
		try{
			
			queryMap.put("sqlName", "timingQueryOrderWxOrAli");
			
			ParameterProvider paramProvider = new ParameterProvider();
	        paramProvider.addParameters(queryMap);
			
			List<Map<String, Object>>  orderList = queryMap(paramProvider, SqlVmConstants.TimingTask.TIMING_TASK_001); 
			
			return orderList;
			
		}catch(Exception e){
			throw new FrameException(e.getMessage());
		}
	}
	
	
	/**
	 * 查询三码合一微信和支付宝订单
	 */
	@Override
	public List<Map<String, Object>> timingQueryThreeCodeOrder(Map<String, String> queryMap, PageVariable page) throws FrameException {

		try{
			
			queryMap.put("sqlName", "timingQueryThreeCodeOrder");
			
			ParameterProvider paramProvider = new ParameterProvider();
	        paramProvider.addParameters(queryMap);
			
			List<Map<String, Object>>  orderList = queryMap(paramProvider, SqlVmConstants.TimingTask.TIMING_TASK_001 ,page); 
			
			return orderList;
			
		}catch(Exception e){
			throw new FrameException(e.getMessage());
		}
	}
	/**
	 * 查询三码合一微信和支付宝订单总数
	 */
	@Override
	public int queryThreeCodeOrderNumber(Map<String, String> queryMap) throws FrameException {

		try{
			
			queryMap.put("sqlName", "queryThreeCodeOrderNumber");
			
			int totalNumber = queryTotalNumber(queryMap, SqlVmConstants.TimingTask.TIMING_TASK_001); 
			
			return totalNumber;
			
		}catch(Exception e){
			throw new FrameException(e.getMessage());
		}
	}
	
	
	/**
	 * 查询定时任务处理标志
	 * @throws FrameException
	 */
	@Override
	public List<Map<String, Object>> queryTimingTaskHandler(Map<String, String> querytMap) throws FrameException {
		try{
		
			querytMap.put("sqlName", "queryTimingTaskHandler");	
			List<Map<String,Object>> timingTaskHandlerList = queryMapS(querytMap, SqlVmConstants.TakeTimingTaskResult.TAKE_TIMING_TASK_RESULT_001);
			
			return timingTaskHandlerList;
			
		}catch (Exception e) {
			throw new FrameException(e.getMessage());
		}
	}
	
}
