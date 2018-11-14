package com.huateng.pay.handler.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbsThread extends Thread{
	private Logger logger = LoggerFactory.getLogger(AbsThread.class);
	private static final byte[] bt =  new byte[0];
	
	@Override
	public void run() {
		synchronized (bt) {
			this.handler();
		}
	}
	
	public abstract void handler();
	
	
	public void notifyThread(){
		synchronized (bt) {
			logger.info("--------线程唤醒------");
			bt.notifyAll();
		}
	}
	
	public void waitThread(){
		try {
			logger.info("---------线程等待-----");
			bt.wait();
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
		}
	}
}
