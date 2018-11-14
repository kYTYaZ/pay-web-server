package com.huateng.pay.handler.quartz;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.param.InputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.services.scheduler.ISchedulerService;

public class QueryIndefiniteOrderQuartzWx extends AbstractScheduler {

	private ISchedulerService schedulerService;
	@Override
	public void execute() {
		String payAccessType  = StringConstans.PAYACCESSTYPE.ACCESS_WX;
		String orderDate = DateUtil.format(DateUtil.addHour(-1),"yyyyMMdd");
		//例  微信定时器14:30:00 那么查询的时间段是13:00:00-13:59:59
		String txnTmStart = DateUtil.format(DateUtil.addHour(-1),"HH")+"0000";
		String txnTmEnd = DateUtil.format(DateUtil.addHour(-1),"HH")+"5959";
		InputParam input = new InputParam();
		input.putparamString("payAccessType", payAccessType);
		input.putparamString("orderDate", orderDate);
		input.putparamString("txnTmStart", txnTmStart);
		input.putparamString("txnTmEnd", txnTmEnd);
		
		schedulerService.queryIndefiniteOrder(input);

	}
	public ISchedulerService getSchedulerService() {
		return schedulerService;
	}
	public void setSchedulerService(ISchedulerService schedulerService) {
		this.schedulerService = schedulerService;
	}

}
