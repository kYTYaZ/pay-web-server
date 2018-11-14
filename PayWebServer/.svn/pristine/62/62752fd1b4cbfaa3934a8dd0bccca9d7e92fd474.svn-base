package com.huateng.pay.common.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BandwidthLimiter {

	private static Logger logger = LoggerFactory.getLogger(BandwidthLimiter.class);
	
	private static Long KB = 1024l;
	
	private static Long CHUNK_LENGTH = 1024l;
	
	private int bytesWillBeSentOrReceive = 0;
	
	private long lastPieceSentOrReceiveTick = System.nanoTime();
	
	private int maxRate = 1024;
	
	private long timeCostPerChunk = (100000000l * CHUNK_LENGTH)/(this.maxRate * KB);
	
	public BandwidthLimiter(int maxRate){
		this.setMaxRate(maxRate);
	}
	
	public synchronized void setMaxRate(int maxRate) throws IllegalArgumentException{
		if(maxRate < 0){
			throw new IllegalArgumentException("maxRate can not less than 0");
		}
		this.maxRate = maxRate < 0 ? 0 : maxRate;
		if(maxRate == 0){
			this.timeCostPerChunk = 0;
		}else{
			this.timeCostPerChunk = (100000000l * CHUNK_LENGTH) / (this.maxRate * KB);
		}
	}

	public synchronized void limitNextBytes(){
		this.limitNextBytes(1);
	}
	
	public synchronized void limitNextBytes(int len){
		this.bytesWillBeSentOrReceive += len;
		
		while(this.bytesWillBeSentOrReceive > CHUNK_LENGTH){
			long nowTick = System.nanoTime();
			long missedTime = this.timeCostPerChunk - (nowTick - this.lastPieceSentOrReceiveTick);
			if(missedTime > 0){
				try {
					Thread.sleep(missedTime / 1000000, (int)(missedTime % 1000000));
				} catch (InterruptedException e) {
					logger.error(e.getMessage(),e);
				}
			}
			this.bytesWillBeSentOrReceive -= CHUNK_LENGTH;
			this.lastPieceSentOrReceiveTick = nowTick + (missedTime > 0 ? missedTime : 0);
		}
	}
	
	
}
