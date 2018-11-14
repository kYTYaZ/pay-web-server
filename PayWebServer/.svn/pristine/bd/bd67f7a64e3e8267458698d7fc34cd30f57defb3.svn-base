package com.huateng.pay.handler.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.handler.services.NotifyHandlerManager;
import com.huateng.pay.handler.thread.AbsThread;
import com.huateng.pay.po.notify.NotifyMessage;
import com.huateng.pay.po.queue.NotifyQueue;

/**
 * @author Administrator
 * 队列处理类，用于检测处理队列中是否有数据
 */
public class QueueHandlerTask  extends AbsThread{
	private  Logger logger = LoggerFactory.getLogger(QueueHandlerTask.class);
	
	private NotifyQueue<NotifyMessage> notifyQueue;
	private NotifyHandlerManager notifyHandlerManager;
	
	public QueueHandlerTask(NotifyQueue<NotifyMessage> notifyQueue,NotifyHandlerManager notifyHandlerManager){	
		this.notifyQueue = notifyQueue;
		this.notifyHandlerManager = notifyHandlerManager;
	}
	
	@Override
	public void handler() {
		while(true){
			while(notifyQueue.isEmpty()){
				logger.info("------队列为空,开始等待----------");
				this.waitThread();	
			}
			this.handlerNotify();
		}	
	}
	
	
	/**
	 * 处理通知消息
	 */
	private  void  handlerNotify(){
		for (int i = 0; i < notifyQueue.getSize(); i++) {
			notifyHandlerManager.put(notifyQueue.poll());
		}
	}
	
	public NotifyQueue<NotifyMessage> getNotifyQueue(){
		return this.notifyQueue;
	}
}
