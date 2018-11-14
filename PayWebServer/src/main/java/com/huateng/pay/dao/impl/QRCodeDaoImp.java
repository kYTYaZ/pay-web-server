package com.huateng.pay.dao.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.dao.inter.IQRCodeDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;

public class QRCodeDaoImp extends JdbcSpringDaoFromWorkManagerUtil implements IQRCodeDao{
	
	private static Logger logger = LoggerFactory.getLogger(QRCodeDaoImp.class);
	
	/**
	 * 查询二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public List<Map<String, Object>> queryQRCodeInfo(Map<String, String> queryQRCodeParam) throws FrameException {
		try {
			
			queryQRCodeParam.put("sqlName",  "queryQRCodeInfo" );
			  
			List<Map<String, Object>> qrCodeInfoList  = queryMapS(queryQRCodeParam, SqlVmConstants.QRCode.QRCode_001);
	    
			return qrCodeInfoList;

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
	        throw new FrameException(e.getMessage());
		}
	}
	
	 /**
	 * 保存二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public boolean saveQRCodeInfo(Map<String, String> saveQRCodeParam) throws FrameException {
	    try {
	    	
	    	saveQRCodeParam.put("sqlName", "saveQRCodeInfo");
	    	
	    	return executeUpdate(saveQRCodeParam, SqlVmConstants.QRCode.QRCode_001);
	    	
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	      throw new FrameException(e.getMessage());
	    }
	}
	
	/**
	 * 更新二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public boolean updateQRCodeInfo(Map<String, String> updateQRCodeParam) throws FrameException {
		try {
			
			updateQRCodeParam.put("sqlName", "updateQRCodeStatus");
        	
            return executeUpdate(updateQRCodeParam, SqlVmConstants.QRCode.QRCode_001);
            
        } catch (Exception e) {
            throw new FrameException(e.getMessage());
        }    
	}

}
