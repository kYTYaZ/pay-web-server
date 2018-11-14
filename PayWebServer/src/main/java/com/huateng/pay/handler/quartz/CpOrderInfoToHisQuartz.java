package com.huateng.pay.handler.quartz;

import com.huateng.pay.services.scheduler.ISchedulerService;

/**
 * 针对06状态的订单定时任务处理
 * @author guohuan
 *
 */
public class CpOrderInfoToHisQuartz extends AbstractScheduler{
    private ISchedulerService schedulerService;

    public void execute(){
        schedulerService.timingCopyTblOrderInfoToHis();
    }

    public ISchedulerService getSchedulerService() {
        return schedulerService;
    }

    public void setSchedulerService(ISchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
