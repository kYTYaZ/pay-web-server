package com.huateng.pay.dao.inter;

import java.util.Map;

public interface ITakeTimingTaskResultDao {
	/**
	 * 记录定时任务执行结果
	 * @param paramMap
	 */
	public boolean  takeTimingTaskResult(Map<String, String> paramMap);
}
