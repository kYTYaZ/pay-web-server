package com.huateng.pay.services.db.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.huateng.pay.dao.inter.IStaticQRCodeDao;
import com.huateng.pay.services.db.IStaticQRCodeDataService;

public class StaticQRCodeDataService implements IStaticQRCodeDataService{
	private Logger logger = LoggerFactory.getLogger(StaticQRCodeDataService.class);
    private IStaticQRCodeDao staticQRCodeDao;
    private ISequenceDao  sequenceDao;
    
	@Override
	public String getStaticQRSeqNo() throws FrameException {

		try {
			
			return sequenceDao.getStaticQRCodeSeqNo();
			
		} catch (Exception e) {
			logger.error("获取二维码序列号失败" + e.getMessage(), e);
			throw new FrameException(e);
		}
	}
	
	/**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
	@Override
	public String getStaticQRCodeTxnNo() throws FrameException {
		try {
			
			return sequenceDao.getStaticQRCodeTxnNo();
			
		} catch (Exception e) {
			logger.error("获取二维码序列号失败" + e.getMessage(), e);
			throw new FrameException(e);
		}
	}
	
	/**
	 * 查询静态二维码
	 * @author zyx
	 *
	 */
	@Override
	public OutputParam queryStaticQRCodeInfo(InputParam inputParam) throws FrameException {
		
		OutputParam queryQRCodeOut = new OutputParam();
		
		try {
			
			List<Map<String, Object>> qrCodeInfoList = staticQRCodeDao.queryStaticQRCode(inputParam.getParamString());
			if (StringUtil.listIsEmpty(qrCodeInfoList)) {
				queryQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryQRCodeOut.setReturnMsg("查询二维码信息失败");
				return queryQRCodeOut;
			}

			queryQRCodeOut.setReturnObj(qrCodeInfoList.get(0));
			queryQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
		
		} catch (Exception e){ 
			logger.error("查询二维码订单信息失败" + e.getMessage(), e);
			throw new FrameException(e);
		}
		
		return  queryQRCodeOut;
	}

	/**
	 * 保存静态二维码
	 * @author zyx
	 *
	 */
	@Override
	public OutputParam saveStaticQRCodeInfo(InputParam inputParam) throws FrameException {
		
		OutputParam  saveQRCodeOut = new OutputParam();
		
		try {
			
			boolean saveFlag = staticQRCodeDao.saveStaticQRCode(inputParam.getParamString());
			if(saveFlag){
				saveQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				saveQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
			
		} catch (Exception e) {
			logger.error("新增二维码订单信息失败"+ e.getMessage(), e);
			throw new FrameException(e);
		}
		
		return saveQRCodeOut;
	}
	
	/**
	 * 更新静态二维码
	 * @author zyx
	 *
	 */
	@Override
	public OutputParam updateStaticQRCodeStatus(InputParam inputParam) throws FrameException {
		
		OutputParam  updateQRCodeOut = new OutputParam();
		
		try {
			
			boolean updateFlag = staticQRCodeDao.updateStaticQRCode(inputParam.getParamString());
			
			if(updateFlag){
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
			
		} catch (Exception e) {
			logger.error("更新二维码订单信息失败"+ e.getMessage(), e);
			throw new FrameException(e);
		}
		
		return updateQRCodeOut;
	}
	/**
     * 查询静态二维码信息（返回list）
     * @param inputParam
     * @return
     * @throws FrameException
     */
	@Override
	public List<Map<String, Object>> queryStaticQRCodeList(InputParam inputParam)throws FrameException {
	
		try {
			
			return staticQRCodeDao.queryStaticQRCode(inputParam.getParamString());
			
		} catch (Exception e){ 
			logger.error("批量查询二维码订单信息失败" + e.getMessage(), e);
			throw new FrameException(e);
		}

	}
	
	public IStaticQRCodeDao getStaticQRCodeDao() {
		return staticQRCodeDao;
	}

	public void setStaticQRCodeDao(IStaticQRCodeDao staticQRCodeDao) {
		this.staticQRCodeDao = staticQRCodeDao;
	}

	public ISequenceDao getSequenceDao() {
		return sequenceDao;
	}

	public void setSequenceDao(ISequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	
}
