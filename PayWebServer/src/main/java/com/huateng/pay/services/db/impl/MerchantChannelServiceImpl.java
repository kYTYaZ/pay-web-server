package com.huateng.pay.services.db.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.IMerchantChannelDao;
import com.huateng.pay.services.db.IMerchantChannelService;

public class MerchantChannelServiceImpl implements IMerchantChannelService {

	private Logger logger = LoggerFactory.getLogger(MerchantChannelServiceImpl.class);
	private IMerchantChannelDao merchantChannelDao;

	/**
	 * 查询机构号对应的渠道
	 */
	@Override
	public OutputParam queryMerchantChannel(InputParam inputParam) {
		
		OutputParam queryOrderOut = new OutputParam();
		
		try {

			List<Map<String, Object>> orderList = merchantChannelDao.queryChannel(inputParam.getParamString());
			if (StringUtil.listIsEmpty(orderList)) {
				queryOrderOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOrderOut.setReturnMsg("查询渠道号失败");
				return queryOrderOut;
			}

			queryOrderOut.setReturnObj(orderList.get(0));
			queryOrderOut.setReturnCode(StringConstans.returnCode.SUCCESS);

		} catch (Exception e) {
			logger.error("查询渠道号失败"+e.getMessage(),e);
			throw new FrameException(e);
		}
		return queryOrderOut;
	}

	public IMerchantChannelDao getMerchantChannelDao() {
		return merchantChannelDao;
	}

	public void setMerchantChannelDao(IMerchantChannelDao merchantChannelDao) {
		this.merchantChannelDao = merchantChannelDao;
	}

	@Override
	public OutputParam querySubmerChannelRateInfo(InputParam inputParam) {
		logger.info("查询子商户信息请求报文:"+inputParam.toString());
		OutputParam queryOut = new OutputParam();
		try {
			Map<String,String> map = inputParam.getParamString();
			String merId = map.get(Dict.merId);
			String subMerchant = map.get(Dict.subMerchant);
			String channel = map.get(Dict.channel);
			if(StringUtil.isEmpty(merId)) {
				queryOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOut.setReturnMsg("查询商户配置时必须传入[商户号:merId]");
				queryOut.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				queryOut.putValue(Dict.respDesc, "查询商户配置时必须传入[商户号:merId]");
				return queryOut;
			}
			if(StringUtil.isEmpty(subMerchant)) {
				queryOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOut.setReturnMsg("查询商户配置时必须传入[子商户号:subMerchant]");
				queryOut.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				queryOut.putValue(Dict.respDesc, "查询商户配置时必须传入[子商户号:subMerchant]");
				return queryOut;
			}
			if(StringUtil.isEmpty(channel)) {
				queryOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOut.setReturnMsg("查询商户配置时必须传入[渠道:channel]");
				queryOut.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				queryOut.putValue(Dict.respDesc, "查询商户配置时必须传入[渠道:channel]");
				return queryOut;
			}
			
			List<Map<String, Object>> orderList = merchantChannelDao.querySubmerChannelRateInfo(map);
			if (StringUtil.listIsEmpty(orderList)) {
				queryOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOut.setReturnMsg("未查询到该子商户信息");
				queryOut.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				queryOut.putValue(Dict.respDesc, "未查询到该子商户信息");
			}else {
				queryOut.setReturnObj(orderList.get(0));
				queryOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}
		} catch (Exception e) {
			logger.error("查询子商户信息异常"+e.getMessage(),e);
			queryOut.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			queryOut.putValue(Dict.respDesc, "查询子商户信息失败"+e.getMessage());
			throw new FrameException(e);
		} 
		logger.info("查询子商户信息返回报文:"+queryOut.toString());
		return queryOut;
		
	}
	
	
	public OutputParam insertSubmerChannelRate(InputParam input) throws FrameException {
		logger.info("新增子商户费率关联表start，请求报文:"+input.toString());
		OutputParam  saveOrderOut = new OutputParam();
		try {
			
			boolean saveFlag = merchantChannelDao.insertSubmerChannelRate(input.getParamString());
			if(saveFlag){
				saveOrderOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				saveOrderOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
			
		} catch (Exception e) {
			logger.error("新增子商户费率关联表异常"+e.getMessage(),e);
			throw new FrameException(e);
		}
		logger.info("新增子商户费率关联表end，返回报文:"+saveOrderOut.toString());
		return saveOrderOut;
	}

	@Override
	public OutputParam querySubmerIsExist(InputParam queryInput) {
		logger.info("查询子商户是否存在请求报文:"+queryInput.toString());
		OutputParam queryOrderOut = new OutputParam();
		try {
			List<Map<String, Object>> list = merchantChannelDao.querySubmerIsExist(queryInput.getParamString());
			if(list.size() >= 1) {
				queryOrderOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOrderOut.setReturnMsg("子商户关联表已存在该商户信息");
			} else {
				queryOrderOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}

		} catch (Exception e) {
			logger.error("查询子商户是否存在异常"+e.getMessage(),e);
			throw new FrameException(e);
		}
		logger.info("查询子商户是否存在返回报文:"+queryOrderOut.toString());
		return queryOrderOut;
	}

	
	public OutputParam updateSubmerChannelRateInfoByPrimaryKey(InputParam inputParam) throws FrameException {
		logger.info("更新子商户关联表请求报文:"+inputParam.toString());
		OutputParam  updateOrderOut = new OutputParam();
		
		try {
			
			boolean updateFlag = merchantChannelDao.updateSubmerChannelRateInfoByPrimaryKey(inputParam.getParamString());
			
			if(updateFlag){
				updateOrderOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			}else{
				updateOrderOut.setReturnCode(StringConstans.returnCode.FAIL);
			}	
			
		} catch (Exception e) {
			logger.error("更新子商户关联表失败" + e.getMessage(), e);
			throw new FrameException(e);
		}
		logger.info("更新子商户关联表返回报文:"+updateOrderOut.toString());
		
		return updateOrderOut;
	}
	
}
