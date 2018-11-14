package com.huateng.pay.dao.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.inter.IMerchantChannelDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;

public class MerchantChannelDaoImpl extends JdbcSpringDaoFromWorkManagerUtil
		implements IMerchantChannelDao {

	private static Logger logger = LoggerFactory.getLogger(MerchantChannelDaoImpl.class);
	
	@Override
	public List<Map<String, Object>> queryChannel(Map<String, String> queryParams) {

		try {
			queryParams.put("sqlName", "queryMerchantChannel");
			List<Map<String, Object>> orderList = queryMapS(queryParams,SqlVmConstants.MerChantChannel.MERCHANT_CHANNEL_001);
			return orderList;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public List<Map<String, Object>> querySubmerChannelRateInfo(Map<String, String> queryParams) {
		try {
			queryParams.put("sqlName", "querySubmerChannelRateInfo");
			List<Map<String, Object>> orderList = queryMapS(queryParams,SqlVmConstants.MerChantChannel.MERCHANT_CHANNEL_001);
			return orderList;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public boolean insertSubmerChannelRate(Map<String, String> insertMap) {
		try {
	    	
			insertMap.put("sqlName", "insertSubmerChannelRate");
	    	
	    	return executeUpdate(insertMap, SqlVmConstants.MerChantChannel.MERCHANT_CHANNEL_001);
	    	
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	      throw new FrameException(e.getMessage());
	    }
	}

	@Override
	public List<Map<String, Object>> querySubmerIsExist(Map<String, String> paramString) {

		try {
			paramString.put("sqlName", "querySubmerIsExist");
			List<Map<String, Object>> orderList = queryMapS(paramString,SqlVmConstants.MerChantChannel.MERCHANT_CHANNEL_001);
			return orderList;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public boolean updateSubmerChannelRateInfoByPrimaryKey(Map<String, String> updateMap) {
		try {
			updateMap.put("sqlName", "updateSubmerChannelRateInfoByPrimaryKey");
            return executeUpdate(updateMap, SqlVmConstants.MerChantChannel.MERCHANT_CHANNEL_001);
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        } 
	}

}
