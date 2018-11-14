package com.huateng.pay.services.tradelimit.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.huateng.pay.dao.inter.ITblMerTradeLimitDao;
import com.huateng.pay.services.db.impl.TimingTaskServiceImpl;
import com.huateng.pay.services.tradelimit.ITblMerTradeLimitService;
/**
 * 商户限额service层实现类
 * @author Yuyk
 *
 */
public class TblMerTradeLimitServiceImpl implements ITblMerTradeLimitService {

	private Logger logger = LoggerFactory.getLogger(TimingTaskServiceImpl.class);
	private ITblMerTradeLimitDao tblMerTradeLimitDao;
	private ISequenceDao sequenceDao;
	

	public ITblMerTradeLimitDao getTblMerTradeLimitDao() {
		return tblMerTradeLimitDao;
	}


	public void setTblMerTradeLimitDao(ITblMerTradeLimitDao tblMerTradeLimitDao) {
		this.tblMerTradeLimitDao = tblMerTradeLimitDao;
	}


	//开始查询限额信息
	@Override
	public OutputParam queryTradeLimit(Map<String, String> queryMap) {
		OutputParam output = new OutputParam();
		try {
			logger.info("开始查询限额信息："+queryMap.toString());
			Map<String, Object> map = tblMerTradeLimitDao.queryLimitByDtAndAcctNo(queryMap);
			if(MapUtils.isEmpty(map)){
				map.put("tradeamtDay", StringUtil.amountTo12Str("0"));
				map.put("tradeamtMonth", StringUtil.amountTo12Str("0"));
			}
			logger.info("查询限额信息结束，限额结果为："+map.toString());
			output.setReturnObj(map);
			output.setReturnCode(StringConstans.returnCode.SUCCESS);
			
		} catch (Exception e) {
			logger.error("从限额表中查询当日限额与当月限额出现异常，" + e.getMessage(),e);
			output.setReturnCode(StringConstans.returnCode.FAIL);
			output.setReturnMsg("从限额表中查询当日限额与当月限额出现异常，" +e.getMessage());
		}
		return output;
	}
	
	/**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
	@Override
	public String getLimitTxnNo() throws FrameException {
		try {
			
			return sequenceDao.getLimitTxnNo();
			
		} catch (Exception e) {
			logger.error("获取二维码序列号失败"+e.getMessage(),e);
			throw new FrameException(e);
		}
	}

	/**
	 * 1首先找出该商户当天的限额信息，
	 * 2.1如果有记录，累加，更新限额信息
	 * 2.2如果没有 新增限额信息，其中的当月限额需要另行判断
	 * 3.返回结果
	 */
	@Override
	public OutputParam updateTradeLimit(Map<String, String> mapforquery) {

		logger.info("处理丰收互联支付通知更新限额  方法开始  请求信息" + mapforquery.toString());
		OutputParam output = new OutputParam();
		String limitDt=mapforquery.get("limitDt");
		String acctNo=mapforquery.get("acctNo");
		String tradeAmt=mapforquery.get("tradeAmt");
		BigDecimal dayAmt=new BigDecimal("0");
		BigDecimal monthAmt=new BigDecimal("0");
		BigDecimal tradeAmtNumber=new BigDecimal(tradeAmt).divide(new BigDecimal("100"));
		boolean flag=false;
		try {
			//logger.info("开始执行从限额表中查询该商户卡号当日限额与当月限额");
			
			Map<String, Object> map = tblMerTradeLimitDao.queryByAcctNo(mapforquery);
			
			logger.debug("从限额表中查询该商户卡号当日限额与当月限额结束，参数为：" + map.toString());
			if (MapUtils.isEmpty(map)) {
				logger.debug("不存在该商户卡号的限额信息");
				String limitId = getLimitTxnNo();
				
				logger.debug("插入该商户卡号的限额信息");
				Map<String, String> insertMap=new HashMap<String, String>();
				insertMap.put("limitId", limitId);
				insertMap.put("limitDt", limitDt);
				insertMap.put("acctNo", acctNo);
				insertMap.put("tradeamtDay", StringUtil.amountTo12Str(tradeAmtNumber.toString()));
				insertMap.put("tradeamtMonth", StringUtil.amountTo12Str(tradeAmtNumber.toString()));
				flag=tblMerTradeLimitDao.insertLimitTbl(insertMap);
			}else{
				String limitDtAgo=ObjectUtils.toString(map.get("limitDt"));
				Map<String, String> updateMap=new HashMap<String, String>();
				updateMap.put("limitDt", limitDt);
				updateMap.put("limitId", ObjectUtils.toString(map.get("limitId")));
				updateMap.put("updateTm", DateUtil.getDateYYYYMMDD()+DateUtil.getDateHHMMSS());
				//判断是否是当月
				if(limitDt.substring(0, 6).equals(limitDtAgo.substring(0, 6))){
					//是当月，月限额累加  判断是否是当日
					monthAmt=new BigDecimal(ObjectUtils.toString(map.get("tradeamtMonth"))).divide(new BigDecimal(100)).add(tradeAmtNumber);
					updateMap.put("tradeamtMonth", StringUtil.amountTo12Str(monthAmt.toString()));
					if(limitDt.equals(limitDtAgo)){
						//是当日，日限额与月限额一起累加
						dayAmt=new BigDecimal(ObjectUtils.toString(map.get("tradeamtDay"))).divide(new BigDecimal(100)).add(tradeAmtNumber);
					}else{
						//不是当日，日限额为交易金额
						dayAmt=tradeAmtNumber;
					}
					updateMap.put("tradeamtDay", StringUtil.amountTo12Str(dayAmt.toString()));
				}else{
					//不是当月 更新日期，日限额与月限额均为交易金额
					updateMap.put("tradeamtDay", StringUtil.amountTo12Str(tradeAmtNumber.toString()));
					updateMap.put("tradeamtMonth", StringUtil.amountTo12Str(tradeAmtNumber.toString()));
				}
				flag=tblMerTradeLimitDao.updateLimitTbl(updateMap);
			}
			if(flag){
				output.setReturnObj(map);
				output.setReturnCode(StringConstans.returnCode.SUCCESS);
			}
			
		} catch (Exception e) {
			logger.error("从限额表中查询当日限额与当月限额出现异常，" + e.getMessage(),e);
			output.setReturnCode(StringConstans.returnCode.FAIL);
			output.setReturnMsg(e.getMessage());
		} finally {
			logger.info("处理丰收互联支付通知更新限 方法结束  返回信息" + output.toString());
		}
		return output;
	}

	public ISequenceDao getSequenceDao() {
		return sequenceDao;
	}


	public void setSequenceDao(ISequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

}
