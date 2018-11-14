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
import com.huateng.pay.dao.inter.IQRCodeDao;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.huateng.pay.services.db.IQRCodeService;


public class QRCodeServiceImpl implements IQRCodeService{
	private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private IQRCodeDao qrCodeDao;
    private ISequenceDao  sequenceDao;
    
    /**
	 * 获取二维码序列号
	 * @return
	 * @throws FrameException
	 */
	@Override
	public String getQRSeqNo() throws FrameException {
		
		try {
			
			return sequenceDao.getQRCodeSeqNo();
			
		} catch (Exception e) {
			logger.error("获取二维码序列号失败"+e.getMessage(),e);
			throw new FrameException(e);
		}
	
	}
	
	
	/**
	 * 查询二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam queryQRCodeInfo(InputParam inputParam) throws FrameException {
		
		OutputParam queryQRCodeOut = new OutputParam();
		
		try {
			
			List<Map<String, Object>> qrCodeInfoList = qrCodeDao.queryQRCodeInfo(inputParam.getParamString());
			if (StringUtil.listIsEmpty(qrCodeInfoList)) {
				logger.error("银标二维码——查询二维码订单信息失败");
				queryQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryQRCodeOut.setReturnMsg("查询二维码信息失败");
				return queryQRCodeOut;
			}

			queryQRCodeOut.setReturnObj(qrCodeInfoList.get(0));
			queryQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
		
		} catch (Exception e){ 
			logger.error("银标二维码——查询二维码订单信息异常" + e.getMessage(), e);
			throw new FrameException(e);
		}
		
		return  queryQRCodeOut;
	}
	
	/**
	 * 保存二维码信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam saveQRCodeInfo(InputParam inputParam) throws FrameException {
		
		OutputParam  saveQRCodeOut = new OutputParam();
		
		try {
			
			boolean saveFlag = qrCodeDao.saveQRCodeInfo(inputParam.getParamString());
			if(saveFlag){
				saveQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				saveQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
			
		} catch (Exception e) {
			logger.error("新增二维码订单信息失败"+e.getMessage(), e);
			throw new FrameException(e);
		}
		
		return saveQRCodeOut;
		
	}

	/**
	 * 更新二维码状态信息
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam updateQRCodeStatus(InputParam inputParam)	throws FrameException {
		
		OutputParam  updateQRCodeOut = new OutputParam();
		
		try {
			
			boolean updateFlag = qrCodeDao.updateQRCodeInfo(inputParam.getParamString());
			
			if(updateFlag){
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
			
		} catch (Exception e) {
			logger.error("更新二维码订单信息失败" +e.getMessage(), e);
			throw new FrameException(e);
		}
		
		return updateQRCodeOut;
	}
	
	public IQRCodeDao getQrCodeDao() {
		return qrCodeDao;
	}

	public void setQrCodeDao(IQRCodeDao qrCodeDao) {
		this.qrCodeDao = qrCodeDao;
	}


	public ISequenceDao getSequenceDao() {
		return sequenceDao;
	}

	public void setSequenceDao(ISequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}



}
