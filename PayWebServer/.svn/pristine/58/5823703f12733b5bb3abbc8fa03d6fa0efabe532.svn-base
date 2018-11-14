package com.huateng.pay.handler.services;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.param.InputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.dao.inter.IOrderDao;
import com.huateng.pay.handler.pool.ThreadPool;
import com.huateng.pay.handler.task.NotifyHandlerTask;
import com.huateng.pay.handler.task.NotifyMessageFailureAfterHandler;
import com.huateng.pay.po.notify.NotifyExceptionRety;
import com.huateng.pay.po.notify.NotifyHandlerResult;
import com.huateng.pay.po.notify.NotifyMessage;
import com.huateng.pay.po.settle.SettleMessageReviceResult;
import com.huateng.utils.Util;


public class NotifyHandlerManager {
	
	private  Logger logger = LoggerFactory.getLogger(NotifyHandlerManager.class);
	
	private  ListeningExecutorService service;
	
	private IOrderDao orderDao;
	
	public IOrderDao getOrderDao() {
		return orderDao;
	}

	public void setOrderDao(IOrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public NotifyHandlerManager(ThreadPool threadPool){
		service = MoreExecutors.listeningDecorator(threadPool.getExecutorService());
	}
	
	/**
	 * 接收通知
	 * @param notifyMessage
	 */
	public void put(NotifyMessage notifyMessage){
		this.execute(notifyMessage);
	}
	
	public void immediatelyExecute(NotifyMessage notifyMessage){
		this.execute(notifyMessage);
	}
	
	/**
	 * 处理通知
	 * @param notifyMessage
	 */
	private void execute(final NotifyMessage notifyMessage){
		
		ListenableFuture<NotifyHandlerResult> future =  service.submit(new NotifyHandlerTask(notifyMessage));		
		
		Futures.addCallback(future,new FutureCallbackImple(notifyMessage),service);
	}
	
	private class FutureCallbackImple implements FutureCallback<NotifyHandlerResult>{
		
		private NotifyMessage notifyMessage;

		public FutureCallbackImple(NotifyMessage notifyMessage){
			this.notifyMessage = notifyMessage;
		}
		
		@Override
		public void onFailure(Throwable throwable) {
			logger.info(this.handlerAfterFailureMessage(throwable));
			this.executeAfterFailureProcessing(throwable);
		}

		@Override
		public void onSuccess(NotifyHandlerResult result) {
			logger.info(this.handlerAfterSuccessMessage(result));
			this.executeAfterSuccessProcessing(result);
		}
		
		/**
		 * 执行失败后输出消息
		 * @param throwable
		 * @return
		 */
		private String handlerAfterFailureMessage(Throwable throwable){
			StringBuffer  buffer = new StringBuffer("通知处理结果:");
			buffer.append("流水号:").append(notifyMessage.getTxnSeqId()).append(",");
			buffer.append("处理时间:").append(DateUtil.format(new Date(),DateUtil.defaultSimpleFormater)).append(",");
			buffer.append("处理结果:").append("出现异常,处理失败").append(",");
			buffer.append("异常信息:").append(throwable.getMessage());
	
			return buffer.toString();
		}
		
		/**
		 * 执行成功后输出消息
		 * @param result
		 * @return
		 */
		private String handlerAfterSuccessMessage(NotifyHandlerResult result){
			StringBuffer  buffer = new StringBuffer("通知处理结果:");
			buffer.append("流水号:").append(notifyMessage.getTxnSeqId()).append(",");
			buffer.append("结果码:").append(result.getResultCode()).append(",");
			buffer.append("处理时间:").append(result.getDate()).append(",");
			buffer.append("处理结果:").append(result.getResultMsg());
			SettleMessageReviceResult settleResult = result.getSettleResult();
			if (!StringUtil.isNull(settleResult)&&!StringUtil.isEmpty(settleResult.getTxnSeqId())) {
				this.updateOrderTxn(settleResult);
				buffer.append("[T+0]");
			}
			if(!StringConstans.returnCode.SUCCESS.equals(result.getResultCode())){
				buffer.append(",").append("失败信息描述:").append(result.getResultMsg());
			}
			
			return buffer.toString();
		} 
		
		
		private void updateOrderTxn(SettleMessageReviceResult settleResult) {
			String txnSeqId = settleResult.getTxnSeqId();
			String accountedFlag = settleResult.getAccountedFlag();
			logger.info("T+0清算:" + txnSeqId + "   " + accountedFlag);
			InputParam updateInput = new InputParam();
			updateInput.putparamString("txnSeqId", txnSeqId);
			updateInput.putparamString("accountedFlag", accountedFlag);
			updateInput.putparamString("txnSta", StringConstans.OrderState.STATE_02);
			boolean updateFlag = orderDao.updateOrder(updateInput.getParamString());
			if (!updateFlag) {
				logger.info("[T+0清算]更新订单表失败");
			}
			
			
		}

		/**
		 * 执行后处理
		 * @param result
		 */
		private void  executeAfterSuccessProcessing(NotifyHandlerResult result){
			if(!StringConstans.returnCode.SUCCESS.equals(result.getResultCode())){
				this.onFailure(new Exception(result.getResultMsg()));
			}	
		}
		
		/**
		 * 执行后处理
		 * @param result
		 */
		private void  executeAfterFailureProcessing(Throwable throwable){
			try {				
				boolean flag = this.excuteAfterExceptionProcessing(throwable);
				if(flag) new NotifyMessageFailureAfterHandler().handler(NotifyHandlerManager.this, notifyMessage);				
			} catch (Exception ex) {	
				this.onFailure(ex);
			}
		}
		
		/**
		 *异常后处理
		 * @param ex
		 * @throws Exception 
		 */
		private boolean  excuteAfterExceptionProcessing(Throwable ex) throws Exception{
			
			int retryCount = notifyMessage.getRetryCount();
			
			if(ex instanceof SocketTimeoutException || ex instanceof SocketException){
				notifyMessage.setRetryCount(retryCount + 1);
			}
			
			Object obj = Util.getNotifyBeanNotNull(notifyMessage);
			
			return new NotifyExceptionRety().retryNotify(ex,retryCount,obj);
		}
	}
}
