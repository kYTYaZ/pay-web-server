package com.huateng.pay.dao.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.inter.ITblMerTradeLimitDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;

public class TblMerTradeLimitDaoImpl extends JdbcSpringDaoFromWorkManagerUtil
		implements ITblMerTradeLimitDao {

	private static Logger logger = LoggerFactory.getLogger(TblMerTradeLimitDaoImpl.class);
	
	@Override
	public Map<String, Object> queryLimitByDtAndAcctNo(
			Map<String, String> paramMap) {
		try {
			paramMap.put("sqlName", "queryLimitByDtAndAcctNo");
			Map<String, Object> limitMap = queryMapObjectMapS(paramMap, SqlVmConstants.LimitTable.Trade_Limit_001);
			return limitMap;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public boolean insertLimitTbl(Map<String, String> insertMap) {
		try {
			insertMap.put("sqlName", "insertLimitTbl");
			return executeUpdate(insertMap, SqlVmConstants.LimitTable.Trade_Limit_001) ;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> queryByDtAndAcctNo(Map<String, String> paramMap) {
		try {
			paramMap.put("sqlName", "queryByDtAndAcctNo");
			Map<String, Object> limitMap = queryMapObjectMapS(paramMap, SqlVmConstants.LimitTable.Trade_Limit_001);
			return limitMap;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}
	
	@Override
	public Map<String, Object> queryByAcctNo(Map<String, String> paramMap) {
		try {
			paramMap.put("sqlName", "queryByAcctNo");
			Map<String, Object> limitMap = queryMapObjectMapS(paramMap, SqlVmConstants.LimitTable.Trade_Limit_001);
			return limitMap;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public Map<String, Object> queryMonthAmtByDtAndAcctNo(
			Map<String, String> paramMap) {
		try {
			paramMap.put("sqlName", "queryMonthAmtByDtAndAcctNo");
			Map<String, Object> limitMap = queryMapObjectMapS(paramMap, SqlVmConstants.LimitTable.Trade_Limit_001);
			return limitMap;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public boolean updateLimitTbl(Map<String, String> updateMap) {
		try {
			updateMap.put("sqlName", "updateLimitTbl");
			return executeUpdate(updateMap, SqlVmConstants.LimitTable.Trade_Limit_001) ;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public void deleteLimitTbl(Map<String, String> deleteMap) {
		try {
			deleteMap.put("sqlName", "deleteLimitTbl");
			execute(deleteMap, SqlVmConstants.LimitTable.Trade_Limit_001);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

}
