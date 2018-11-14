package com.huateng.pay.handler.quartz;

import com.huateng.pay.services.scheduler.ISchedulerService;

/**
 * 下载微信对账单定时任务处理
 * @author guohuan
 *
 */
public class DownloadBillQuartz extends AbstractScheduler{
    private ISchedulerService schedulerService;

    public void execute(){
        schedulerService.downloadWxBill();;
    }

    public ISchedulerService getSchedulerService() {
        return schedulerService;
    }

    public void setSchedulerService(ISchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
