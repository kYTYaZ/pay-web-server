package com.huateng.pay.handler.task;

import com.huateng.pay.handler.services.NotifyHandlerManager;
import com.huateng.pay.po.notify.NotifyMessage;

public class NotifyMessageFailureAfterHandler {
	
	public void handler(NotifyHandlerManager notifyHandlerManager ,NotifyMessage notifyMessage) throws Exception{
		
		int times = notifyMessage.getTimes();
		
		long delay =  0;
		
		if(times <= 5){
			switch (times) {
			case 0:
				delay = 3;
				break;
			case 1:
				delay = 3;
				break;
			case 2:
				delay = 6;
				break;
			case 3:
				delay = 15;
				break;
			case 4:
				delay = 30;
				break;
			case 5:
				delay = 40;
				break;
			default:
				break;
			}
			
			new NotifyTimer().schedule(new NotifyTimeTask(notifyHandlerManager,notifyMessage), delay);
		}
	}
}
