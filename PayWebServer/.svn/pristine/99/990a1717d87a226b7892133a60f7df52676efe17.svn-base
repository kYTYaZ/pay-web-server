package com.huateng.pay.handler.quartz;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.param.InputParam;
import com.huateng.pay.services.scheduler.ISchedulerService;

/**
 * 查询一码通入账状态未知的交易，并重新发起入账
 * @author guohuan
 *
 */
public class QuerySettleUnknowStautsQuartz extends AbstractScheduler{
    private ISchedulerService schedulerService;

    public void execute(){
		String orderDate = DateUtil.format(DateUtil.addHour(-2),"yyyyMMdd");
		//例 定时器14:00:00 那么查询的时间段是12:00:00-12:59:59
		String txnTmStart = DateUtil.format(DateUtil.addHour(-2),"HH")+"0000";
		String txnTmEnd = DateUtil.format(DateUtil.addHour(-2),"HH")+"5959";
		InputParam input = new InputParam();
		input.putparamString("orderDate", orderDate);
		input.putparamString("txnTmStart", txnTmStart);
		input.putparamString("txnTmEnd", txnTmEnd);
        schedulerService.queryUnknowStautsAndSettle(input);
    }

    public ISchedulerService getSchedulerService() {
        return schedulerService;
    }

    public void setSchedulerService(ISchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
    
}
