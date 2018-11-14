/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: SequenceDaoImpl.java
 * Author:   justin
 * Date:     2014-12-9 上午10:11:23
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.SqlVmConstants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;


/**
 * 序列生成
 * 
 * @author justin
 * @see 1.0
 * @since 1.0
 */
public class SequenceDaoImpl extends JdbcSpringDaoFromWorkManagerUtil implements ISequenceDao {
	
	private static Logger logger = LoggerFactory.getLogger(SequenceDaoImpl.class);
	
	/**
     * 查询订单表序列号
     * @return
     * @throws FrameException
     * @see 1.0
     * @since 1.0
     */
    @Override
    public String getTxnSeqId() throws FrameException {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put("sqlName", new String[] { "queryTxnSeqId" });
        String seqId = null;
        try {
            List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
            seqId = seqIds.get(0).get("1").toString();
        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
            throw new FrameException(e.getMessage());
        }
        return StringUtil.isEmpty(seqId) ? "1000000001" : seqId;
    }

    /**
     * 获取二维码序列号
     * @return
     * @throws FrameException
     */
	@Override
	public String getQRCodeSeqNo() throws FrameException {
		Map<String, String[]> params = new HashMap<String, String[]>();
	    params.put("sqlName", new String[] { "queryEwmSeq" });
	    try {
	            
	        List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
	        String seqId = seqIds.get(0).get("1").toString();
	            
	       return StringUtil.isEmpty(seqId) ? "100001" : seqId;
	       
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	       throw new FrameException(e.getMessage());
	   }     
	}
	  /**
     * 获取静态二维码序列号
     * @return
     * @throws FrameException
     */
	@Override
	public String getStaticQRCodeSeqNo() throws FrameException {
		Map<String, String[]> params = new HashMap<String, String[]>();
	    params.put("sqlName", new String[] { "queryStaticEwmSeq" });
	    try {
	            
	        List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
	        String seqId = seqIds.get(0).get("1").toString();
	            
	       return StringUtil.isEmpty(seqId) ? "100001" : seqId;
	       
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	       throw new FrameException(e.getMessage());
	   } 
	}
	
	/**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
	@Override
	public String getStaticQRCodeTxnNo() throws FrameException {
		Map<String, String[]> params = new HashMap<String, String[]>();
	    params.put("sqlName", new String[] { "queryStaticEwmtxn" });
	    try {
	            
	        List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
	        String seqId = seqIds.get(0).get("1").toString();
	            
	       return StringUtil.isEmpty(seqId) ? "100001" : seqId;
	       
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	       throw new FrameException(e.getMessage());
	   } 
	}
	
	/**
     * 获取限额表流水
     * @return
     * @throws FrameException
     */
	@Override
	public String getLimitTxnNo() throws FrameException {
		Map<String, String[]> params = new HashMap<String, String[]>();
	    params.put("sqlName", new String[] { "queryLimitEwmtxn" });
	    try {
	            
	        List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
	        String seqId = seqIds.get(0).get("1").toString();
	            
	       return StringUtil.isEmpty(seqId) ? "100001" : seqId;
	       
	    } catch (Exception e) {
	    	logger.error(e.getMessage(),e);
	       throw new FrameException(e.getMessage());
	   } 
	}
	
	/**
	 * 获取定时器序列号
	 * @return
	 * @throws FrameException
	 */
	@Override
	public String getTimingTask() throws FrameException {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("sqlName", new String[] { "queryTimingTask" });
		try {
			
			List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
			String seqId = seqIds.get(0).get("1").toString();
			
			return StringUtil.isEmpty(seqId) ? "10000001" : seqId;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		} 
	}
	
	@Override
	public String getSubmerChannelRate() throws FrameException {
		Map<String, String[]> params = new HashMap<String, String[]>();
		params.put("sqlName", new String[] { "querySubmerChannelRate" });
		try {
			
			List<Map<String, Object>> seqIds = queryMap(params, SqlVmConstants.Sequence.SEQ_001);
			String seqId = seqIds.get(0).get("1").toString();
			
			return StringUtil.isEmpty(seqId) ? "10000001" : seqId;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new FrameException(e.getMessage());
		} 
	}
}
