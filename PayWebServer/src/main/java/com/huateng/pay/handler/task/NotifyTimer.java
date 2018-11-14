package com.huateng.pay.handler.task;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

public class NotifyTimer {
	
	public void schedule(NotifyTimeTask notifyTimeTask,long delay) throws Exception{
		this.paramVaidate(notifyTimeTask, delay);
		TimeUnit.SECONDS.sleep(delay);
		notifyTimeTask.run();
	}
	
	private void  paramVaidate(NotifyTimeTask notifyTimeTask,long delay){
		Preconditions.checkArgument(delay > 0,"delay 不能小于等于 0");
		Preconditions.checkNotNull(notifyTimeTask, "notifyTimeTask is nullpointException");
	}
}
