package com.huateng.pay.handler.quartz;

import com.huateng.pay.services.scheduler.ISchedulerService;

public class CreatThreeCodeWxAndAlipayBills extends AbstractScheduler {

	private ISchedulerService schedulerService;
	@Override
	public void execute() {
		
		schedulerService.creatThreeCodeWxAndAipayBills();
		
	}
	public ISchedulerService getSchedulerService() {
		return schedulerService;
	}
	public void setSchedulerService(ISchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

}
