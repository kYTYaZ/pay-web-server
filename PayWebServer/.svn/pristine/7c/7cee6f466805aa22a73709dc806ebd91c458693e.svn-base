package com.huateng.pay.handler.quartz;

import com.huateng.pay.services.scheduler.ISchedulerService;

public class PackLogByZipQuartz extends AbstractScheduler {

	private ISchedulerService schedulerService;
	@Override
	public void execute() {
		
		schedulerService.packLogByZip();

	}
	public ISchedulerService getSchedulerService() {
		return schedulerService;
	}
	public void setSchedulerService(ISchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}
}
