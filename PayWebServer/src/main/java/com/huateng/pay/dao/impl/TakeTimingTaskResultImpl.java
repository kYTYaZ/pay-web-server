package com.huateng.pay.dao.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.inter.ITakeTimingTaskResultDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;

public class TakeTimingTaskResultImpl  extends JdbcSpringDaoFromWorkManagerUtil implements ITakeTimingTaskResultDao {
	
	private static Logger logger = LoggerFactory.getLogger(TakeTimingTaskResultImpl.class);
	
	/**
	 * 记录定时任务执行结果
	 */
	@Override
	public boolean takeTimingTaskResult(Map<String, String> paramMap) {
		try {
	    	
			paramMap.put("sqlName", "insertTakeTimingTaskResult");
	    	
	    	return executeUpdate(paramMap, SqlVmConstants.TakeTimingTaskResult.TAKE_TIMING_TASK_RESULT_001);
	    	
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	      throw new FrameException(e.getMessage());
	    }	
	}

}
