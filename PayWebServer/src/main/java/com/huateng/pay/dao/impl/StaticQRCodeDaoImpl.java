package com.huateng.pay.dao.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.inter.IStaticQRCodeDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;

public class StaticQRCodeDaoImpl extends JdbcSpringDaoFromWorkManagerUtil
		implements IStaticQRCodeDao {
	
	private static Logger logger = LoggerFactory.getLogger(StaticQRCodeDaoImpl.class);
	
	/**
	 * 删除静态二维码信息
	 * 
	 * @author zyx
	 * 
	 */
	@Override
	public boolean deleteStaticQRCode(Map<String, String> deleteParam)
			throws FrameException {
		try {
			deleteParam.put("sqlName", "deleteStaticQRCodeInfo");

			return executeUpdate(deleteParam,
					SqlVmConstants.StaticQRCode.Static_QRCode_001);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}

	/**
	 * 查询静态二维码信息
	 * 
	 * @author zyx
	 * 
	 */
	@Override
	public List<Map<String, Object>> queryStaticQRCode(
			Map<String, String> queryMap) throws FrameException {
		try {
			queryMap.put("sqlName", "queryStaticQRCodeInfo");
			List<Map<String, Object>> staticQRCodeInfoList = queryMapS(
					queryMap, SqlVmConstants.StaticQRCode.Static_QRCode_001);
			return staticQRCodeInfoList;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}

	}

	/**
	 * 保存静态二维码信息
	 * 
	 * @author zyx
	 * 
	 */
	@Override
	public boolean saveStaticQRCode(Map<String, String> saveParam)
			throws FrameException {
		try {
			saveParam.put("sqlName", "saveStaticQRCodeInfo");
			return executeUpdate(saveParam,
					SqlVmConstants.StaticQRCode.Static_QRCode_001);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}

	}

	/**
	 * 更新静态二维码信息
	 * 
	 * @author zyx
	 * 
	 */
	@Override
	public boolean updateStaticQRCode(Map<String, String> updateParam)
			throws FrameException {

		try {
			updateParam.put("sqlName", "updateStaticQRCodeInfo");
			return executeUpdate(updateParam,
					SqlVmConstants.StaticQRCode.Static_QRCode_001);

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		}
	}
}
