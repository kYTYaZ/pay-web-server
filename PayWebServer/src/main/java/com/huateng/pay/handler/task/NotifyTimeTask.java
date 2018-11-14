package com.huateng.pay.handler.task;

import com.huateng.pay.handler.services.NotifyHandlerManager;
import com.huateng.pay.po.notify.NotifyMessage;


public class NotifyTimeTask {
	
	private NotifyHandlerManager notifyHandlerManager;
	private NotifyMessage notifyMessage;
	
	public NotifyTimeTask(NotifyHandlerManager notifyHandlerManager,NotifyMessage notifyMessage){
		this.notifyHandlerManager = notifyHandlerManager;
		this.notifyMessage = notifyMessage;
	}
	
	public void run() {
		notifyHandlerManager.immediatelyExecute(notifyMessage);
	}
}
