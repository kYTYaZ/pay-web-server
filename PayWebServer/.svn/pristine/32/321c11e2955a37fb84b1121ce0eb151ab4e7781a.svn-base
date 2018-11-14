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
import com.huateng.pay.dao.IThreeCodeStaticQRCodeDao;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;

public class ThreeCodeStaticQRCodeDataService implements IThreeCodeStaticQRCodeDataService{
	private Logger logger = LoggerFactory.getLogger(ThreeCodeStaticQRCodeDataService.class);
    private IThreeCodeStaticQRCodeDao threeCodeStaticQRCodeDao;
    private ISequenceDao  sequenceDao;
    
    @Override
	public String getThreeCodeStaticQRSeqNo() throws FrameException {
		try {
			return sequenceDao.getStaticQRCodeSeqNo();
		} catch (Exception e) {
			logger.error("获取二维码序列号失败"+ e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	/**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
	@Override
	public String getThreeCodeStaticQRCodeTxnNo() throws FrameException {
		try {
			return sequenceDao.getStaticQRCodeTxnNo();
		} catch (Exception e) {
			logger.error("获取二维码序列号失败"+ e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	@Override
	public OutputParam queryThreeCodeStaticQRCodeInfo(InputParam inputParam)
			throws FrameException {
		logger.info("查询三码合一二维码信息请求报文:"+inputParam.toString());
		OutputParam queryQRCodeOut = new OutputParam();
		try {
			List<Map<String, Object>> qrCodeInfoList = threeCodeStaticQRCodeDao.queryThreeCodeStaticQRCode(inputParam.getParamString());
			if (StringUtil.listIsEmpty(qrCodeInfoList)) {
				queryQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryQRCodeOut.setReturnMsg("该卡号未绑定丰收一码通");
				return queryQRCodeOut;
			}
			queryQRCodeOut.setReturnObj(qrCodeInfoList.get(0));
			queryQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
		} catch (Exception e){ 
			logger.error("查询二维码订单信息失败"+ e.getMessage(),e);
			throw new FrameException(e);
		}
		logger.info("查询三码合一二维码信息返回报文:"+queryQRCodeOut.toString());
		return  queryQRCodeOut;
	}

	@Override
	public OutputParam saveThreeCodeStaticQRCodeInfo(InputParam inputParam)
			throws FrameException {
		OutputParam  saveQRCodeOut = new OutputParam();
		try {
			boolean saveFlag = threeCodeStaticQRCodeDao.saveThreeCodeStaticQRCode(inputParam.getParamString());
			if(saveFlag){
				saveQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				saveQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
		} catch (Exception e) {
			logger.error("新增二维码订单信息失败"+ e.getMessage(),e);
			throw new FrameException(e);
		}
		return saveQRCodeOut;
	}

	@Override
	public OutputParam updateThreeCodeStaticQRCodeStatus(InputParam inputParam)
			throws FrameException {
		OutputParam  updateQRCodeOut = new OutputParam();
		try {
			boolean updateFlag = threeCodeStaticQRCodeDao.updateThreeCodeStaticQRCode(inputParam.getParamString());
			if(updateFlag){
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
		} catch (Exception e) {
			logger.error("更新二维码订单信息失败"+ e.getMessage(),e);
			throw new FrameException(e);
		}
		
		return updateQRCodeOut;
	}
	
	
	@Override
	public OutputParam updateBatchThreeCodeStaticQRCode(InputParam inputParam)
			throws FrameException {
		OutputParam  updateQRCodeOut = new OutputParam();
		try {
			boolean updateFlag = threeCodeStaticQRCodeDao.updateBatchThreeCodeStaticQRCode(inputParam.getParamStrings());
			if(updateFlag){
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				updateQRCodeOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
		} catch (Exception e) {
			logger.error("更新二维码订单信息失败"+ e.getMessage(),e);
			throw new FrameException(e);
		}
		
		return updateQRCodeOut;
	}

	public IThreeCodeStaticQRCodeDao getThreeCodeStaticQRCodeDao() {
		return threeCodeStaticQRCodeDao;
	}

	public void setThreeCodeStaticQRCodeDao(
			IThreeCodeStaticQRCodeDao threeCodeStaticQRCodeDao) {
		this.threeCodeStaticQRCodeDao = threeCodeStaticQRCodeDao;
	}

	public ISequenceDao getSequenceDao() {
		return sequenceDao;
	}

	public void setSequenceDao(ISequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}


	
}
