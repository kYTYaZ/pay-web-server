package com.huateng.pay.handler.quartz;

import com.huateng.pay.services.scheduler.ISchedulerService;

/**
 * 每一个小时的45分查询退款订单
 * @author MyPC
 *
 */
public class QueryRefundOrderQuartz extends AbstractScheduler{
	
	private ISchedulerService schedulerService;
	
	 public ISchedulerService getSchedulerService() {
		return schedulerService;
	}


	public void setSchedulerService(ISchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}


	public void execute(){
	        schedulerService.queryRefundOrder();
	    }

}
