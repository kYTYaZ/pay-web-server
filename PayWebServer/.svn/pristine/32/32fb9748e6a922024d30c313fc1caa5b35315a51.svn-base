package com.huateng.pay.services.db.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.ITimingTaskDao;
import com.huateng.pay.services.db.ITimingTaskService;
import com.huateng.utils.FileUtil;
import com.wldk.framework.db.PageVariable;

public class TimingTaskServiceImpl implements ITimingTaskService {
	private Logger logger = LoggerFactory.getLogger(TimingTaskServiceImpl.class);
	private ITimingTaskDao  timingTaskDao;
	public static final int PAST_DAYS = 7;
	
	/**
	 * 定时从二维码信息将数据拷贝到历史表
	 */
	@Override
	public void timingCopyTblEwmInfoToHis() throws FrameException {
		try {
			
			logger.debug("开始执行从二维码信息中将数据复制到历史表中");
			
			timingTaskDao.timingCopyTblEwmInfoToHis();
			
			logger.debug("执行从二维码信息中将数据复制到历史表中完成");
			
		} catch (Exception e) {
			logger.error("执行从二维码信息中将数据复制到历史表中出现异常：" + e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	
	/**
	 *  定时从订单表信息将数据拷贝到历史表
	 */
	@Override
	public void timingCopyTblOrderInfoToHis() throws FrameException {
		try {
			
			logger.debug("开始执行从订单信息表中将数据复制到历史表中");
			
			timingTaskDao.timingCopyTblOrderInfoToHis();
			
			logger.debug("执行从订单信息表中将数据复制到历史表中完成");
			
		} catch (Exception e) {
			logger.error("执行从订单信息表中将数据复制到历史表中出现异常：" + e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	/**
	 * 定时从订单表信息将数据拷贝到历史表
	 * @param inputParam
	 * @throws FrameException
	 */
	@Override
	public OutputParam timingCopyTblOrderInfoToHisByDate(InputParam inputParam)
			throws FrameException {
		
		logger.info("执行从订单信息表中将数据复制到历史表中start,请求报文:"+inputParam.toString());
		OutputParam  output = new OutputParam();
		
		try {
			String backupDate = String.format("%s", inputParam.getValue("backupDate"));
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("backupDate", backupDate);
			
			 timingTaskDao.timingCopyTblOrderInfoToHisByDate(paramMap);

			output.putValue("resDesc","备份成功");
			output.putValue("resCode",StringConstans.RespCode.RESP_CODE_02);
			output.setReturnCode(StringConstans.returnCode.SUCCESS);
			
		} catch (Exception e) {
			logger.error("执行从订单信息表中将数据复制到历史表中出现异常：" + e.getMessage(),e);
			output.putValue("resDesc","备份失败");
			output.putValue("resCode",StringConstans.RespCode.RESP_CODE_03);
			output.setReturnCode(StringConstans.returnCode.FAIL);
		}
		logger.info("执行从订单信息表中将数据复制到历史表中end,返回报文:"+output.toString());
		return  output;
	}

	/**
	 * 定时删除二维码历史表中的数据
	 */
	@Override
	public void timingDelteTblEwmInfo() throws FrameException {
		try {
			
			logger.debug("开始执行定时从二维码信息中将数据删除");
			
			timingTaskDao.timingDelteTblEwmInfo();
			
			logger.debug("执行定时从二维码信息中将数据删除完成");
			
		} catch (Exception e) {
			logger.error("执行定时从二维码信息中将数据删除出现异常：" + e.getMessage(),e);
			throw new FrameException(e);
		}

	}
	
	/**
	 * 定时删除订单表中的历史数据
	 */
	@Override
	public void timingDelteTblOrderInfo() throws FrameException {
		try {
			
			logger.debug("开始执行从订单信息表中将数据删除");
			
			timingTaskDao.timingDelteTblOrderInfo();
			
			logger.debug("开始执行从订单信息表中将数据删除");
			
		} catch (Exception e) {
			logger.error("执行从订单信息表中将数据删除出现异常：" + e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	
	/**
	 * 插入定时任务处理标志
	 * @throws FrameException
	 */
	@Override
	public boolean insertTimingTaskHandler(Map<String, String> insertMap) throws FrameException {
		
		try {
			
			logger.debug("开始执行插入定时任务处理标志到表中");
			
			boolean insertFlag = timingTaskDao.insertTimingTaskHandler(insertMap);
			
			logger.debug("执行插入定时任务处理标志到表中完成");
			
			return insertFlag;
			
		} catch (Exception e) {
			logger.error("执行插入定时任务信息到表中出现异常：" + e.getMessage());
			throw new FrameException(e);
		}
	}
	
	/**
	 * 查询状态为01，06的订单信息
	 * @param queryMap
	 * @throws FrameException
	 */
	@Override
	public OutputParam timingQueryIndefiniteOrderWxOrAli(Map<String, String> queryMap)throws FrameException {
		
		OutputParam queryOut = new OutputParam();
		try {
			
			logger.debug("开始执行查询订单状态为(01,06)的订单");
			
			List<Map<String, Object>> queryList = timingTaskDao.timingQueryOrderWxOrAli(queryMap);
			
			logger.debug("执行查询订单状态为(01,06)的订单完成");
			if (StringUtil.listIsEmpty(queryList)) {
				queryOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOut.setReturnMsg("未查询到相关订单");
				return queryOut;
			}
			
			queryOut.putReturnList(queryList);
			queryOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			return queryOut;
			
		} catch (Exception e) {
			logger.error("查询订单状态为(01，06)的订单出现异常" + e.getMessage(), e);
			throw new FrameException(e);
		}
	}
	

	/**
	 * 查询定时任务处理标志
	 * @throws FrameException
	 */
	@Override
	public List<Map<String, Object>> queryTimingTaskHandler(Map<String, String> queryMap) throws FrameException {
		
		try {
			
			logger.debug("开始执行查询定时任务处理标志");
			
			List<Map<String, Object>>  queryList = timingTaskDao.queryTimingTaskHandler(queryMap);
			
			logger.debug("执行查询定时任务处理标志完成");
			
			return queryList;
			
		} catch (Exception e) {
			logger.error("执行查询定时任务处理标志出现异常：" + e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	/**
	 * 定时下载三码合一支付宝和微信对账单
	 * @throws FrameException
	 */
	@Override
	public OutputParam timingQueryThreeCodeBills(Map<String, String> queryMap, PageVariable page) throws FrameException {
		
		OutputParam queryOut = new OutputParam();
		try {

			logger.debug("开始查询三码合一支付宝和微信订单");

			List<Map<String, Object>> queryList = timingTaskDao.timingQueryThreeCodeOrder(queryMap, page);

			logger.debug("查询三码合一支付宝和微信订单结束");
			if (StringUtil.listIsEmpty(queryList)) {
				queryOut.setReturnCode(StringConstans.returnCode.FAIL);
				queryOut.setReturnMsg("查询订单信息失败");
				return queryOut;
			}

			queryOut.putReturnList(queryList);
			queryOut.setReturnCode(StringConstans.returnCode.SUCCESS);
			return queryOut;

		} catch (Exception e) {
			logger.error("查询三码合一支付宝和微信订单出现异常：" + e.getMessage(),e);
			throw new FrameException(e);
		}
	}
	
	/**
	 * 查询三码合一微信和支付宝消费订单总数
	 */
	@Override
	public int queryThreeCodeOrderNumber(Map<String, String> queryMap) throws FrameException {

		try {
			logger.debug("查询三码合一微信和支付宝消费订单总数	开始");
			
			int totalNumber = timingTaskDao.queryThreeCodeOrderNumber(queryMap);
			
			logger.debug("查询三码合一微信和支付宝消费订单总数	结束");
			
			return totalNumber;
		} catch (Exception e) {
			logger.error("查询三码合一微信和支付宝消费订单总数时出现异常"+e.getMessage(),e);
			throw new FrameException(e);
		}
	}

	@Override
	public void timingPackLogByZip() throws FrameException {
		File [] files = null;
		File fileTar = null;
		File file = null;
		try {
			//获取七天前日期
			String pastDate = DateUtil.getPastDate(PAST_DAYS);
			//查找满足条件文件的路径
			String filePath = Constants.getParam("sys_log_path");
			//压缩文件的文件名
			File target = new File(filePath + pastDate + ".tar");
			file = new File(filePath + pastDate);
			//如果文件不存在，直接返回
			if (!file.exists()) {
				logger.error("日志文件目录不存在");
				throw new FrameException("日志文件目录不存在");
			}
			//文件过滤器
			/*FilenameFilter filter = new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(extention);
				}
			};*/
			//找到符合条件的日志文件
			files = file.listFiles();
			//文件打包
			fileTar = FileUtil.packByTar(files, target);
			//文件压缩
			FileUtil.compress(fileTar.getAbsolutePath());
		} catch (Exception e) {
			logger.error("打包压缩定时任务出现异常"+e.getMessage(),e);
			throw new FrameException(e);
		} finally {
			//删除tar包
			if (fileTar.exists()) {
				fileTar.delete();
			}
		}
		//删掉目录中文件
		for (File fileSub : files) {
			fileSub.delete();
		}
		//删除目录
		file.delete();
	}

	
	
	public ITimingTaskDao getTimingTaskDao() {
		return timingTaskDao;
	}

	public void setTimingTaskDao(ITimingTaskDao timingTaskDao) {
		this.timingTaskDao = timingTaskDao;
	}


	


}
