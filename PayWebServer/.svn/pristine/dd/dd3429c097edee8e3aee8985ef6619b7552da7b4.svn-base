package com.huateng.pay.handler.quartz;

import com.huateng.pay.services.scheduler.ISchedulerService;

/**
 * 下载支付宝对账单定时任务处理
 * @author ZhouChaoJie
 *
 */
public class DownLoadAliPayBillQuartz extends AbstractScheduler{
	private ISchedulerService schedulerService;
	
	@Override
	public void execute() {
		schedulerService.downloadAliPayBill();
	}

	public ISchedulerService getSchedulerService() {
		return schedulerService;
	}

	public void setSchedulerService(ISchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

}
