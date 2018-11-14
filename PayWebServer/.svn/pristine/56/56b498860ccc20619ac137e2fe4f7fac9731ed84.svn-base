package com.huateng.pay.handler.thread;

import com.google.common.base.Preconditions;
import com.huateng.pay.handler.task.QueueHandlerTask;
import com.huateng.pay.po.notify.NotifyMessage;

public class ThreadNotifyHelper {
	
	private static QueueHandlerTask queueHandlerTask;
	
	public static void setQueueHandlerTask(QueueHandlerTask task){
		queueHandlerTask = task;
	}
	
	public static void notifyThread(NotifyMessage message){
		Preconditions.checkNotNull(queueHandlerTask, "queueHandlerTask为空");
		queueHandlerTask.getNotifyQueue().add(message);
		queueHandlerTask.notifyThread();
	}
}
