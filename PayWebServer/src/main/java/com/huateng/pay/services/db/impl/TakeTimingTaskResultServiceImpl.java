package com.huateng.pay.services.db.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.dao.inter.ITakeTimingTaskResultDao;
import com.huateng.pay.services.db.ITakeTimingTaskResultService;

public class TakeTimingTaskResultServiceImpl implements ITakeTimingTaskResultService{
	private Logger logger = LoggerFactory.getLogger(TimingTaskServiceImpl.class);
	private ITakeTimingTaskResultDao  takeTimingTaskResultDao;
	
	/**
	 * 记录定时任务执行结果
	 * @param paramMap
	 */
	public void takeTimingTaskResult(Map<String, String> paramMap) throws FrameException {
		
		logger.info("--------记录定时任务执行结果开始---------");
		
		try {
			
			takeTimingTaskResultDao.takeTimingTaskResult(paramMap);
		
		} catch (Exception e){ 
			logger.error("记录定时任务执行结果失败"+e.getMessage(),e);
			throw new FrameException(e);
		}
		
		logger.info("--------记录定时任务执行结果结束---------");
	}
	
	
	public ITakeTimingTaskResultDao getTakeTimingTaskResultDao() {
		return takeTimingTaskResultDao;
	}
	public void setTakeTimingTaskResultDao(
			ITakeTimingTaskResultDao takeTimingTaskResultDao) {
		this.takeTimingTaskResultDao = takeTimingTaskResultDao;
	}
}
