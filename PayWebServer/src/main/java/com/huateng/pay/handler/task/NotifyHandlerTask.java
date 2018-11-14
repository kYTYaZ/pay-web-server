package com.huateng.pay.handler.task;

import java.util.concurrent.Callable;

import com.huateng.pay.handler.processor.NotifyProcessor;
import com.huateng.pay.po.notify.NotifyHandlerResult;
import com.huateng.pay.po.notify.NotifyMessage;

public class NotifyHandlerTask implements Callable<NotifyHandlerResult>{
	
	private NotifyMessage message;
	
	public  NotifyHandlerTask(NotifyMessage message){
		this.message = message;
	}
	
	@Override
	public NotifyHandlerResult call() throws Exception {		
		return new NotifyProcessor().notifyHandler(message);
	}

}
