package com.huateng.pay.dao.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.IThreeCodeStaticQRCodeDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;

public class ThreeCodeStaticQRCodeDaoImpl extends JdbcSpringDaoFromWorkManagerUtil
		implements IThreeCodeStaticQRCodeDao {
	 
	private static Logger logger = LoggerFactory.getLogger(ThreeCodeStaticQRCodeDaoImpl.class);
	
	@Override
	public List<Map<String, Object>> queryThreeCodeStaticQRCode(
			Map<String, String> queryMap) throws FrameException {
		try {
			queryMap.put("sqlName", "queryThreeCodeStaticQRCodeInfo");
			List<Map<String, Object>> ThreeCodeStaticQRCodeInfoList = queryMapS(
					queryMap, SqlVmConstants.StaticQRCode.Static_QRCode_002);
			return ThreeCodeStaticQRCodeInfoList;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}

	}

	@Override
	public boolean saveThreeCodeStaticQRCode(Map<String, String> saveParam)
			throws FrameException {
		try {
			saveParam.put("sqlName", "saveThreeCodeStaticQRCodeInfo");
			return executeUpdate(saveParam,
					SqlVmConstants.StaticQRCode.Static_QRCode_002);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}

	}

	@Override
	public boolean updateThreeCodeStaticQRCode(Map<String, String> updateParam)
			throws FrameException {

		try {
			updateParam.put("sqlName", "updateThreeCodeStaticQRCodeInfo");
			return executeUpdate(updateParam,
					SqlVmConstants.StaticQRCode.Static_QRCode_002);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	@Override
	public boolean updateBatchThreeCodeStaticQRCode(List<Map<String, String>> updateParam)
			throws FrameException {

		try {
			for (int i = 0; i < updateParam.size(); i++) {
				updateParam.get(i).put("sqlName", "updateBatchThreeCodeStaticQRCodeInfo");
			}
			
			return execute(updateParam,
					SqlVmConstants.StaticQRCode.Static_QRCode_002);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
		
	}
}
