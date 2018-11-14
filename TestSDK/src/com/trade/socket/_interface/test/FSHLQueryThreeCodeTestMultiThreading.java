package com.trade.socket._interface.test;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.socket._interface.trading.FSHLQueryThreeCode;
import com.util.CommonUtil;
import com.util.SocketUtil;
import com.util.TransUtil;

/**
 * 多线程：丰收家查询三码合一
 * @author Administrator
 *
 */
public class FSHLQueryThreeCodeTestMultiThreading {
	

	
	public static void main(String[] args) throws InterruptedException {
		
		final int threadCount = 10;
		final AtomicInteger count = new AtomicInteger(threadCount);
		final Object waitObject = new Object();
		
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < threadCount; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					Map<String,String> map = FSHLQueryThreeCode.getData();
					map.put(Dict.acctNo, "6230910199064594613");
					map.put(Dict.startDate, "20180601");
					map.put(Dict.endDate, "20180720");
					map.put(Dict.payAccessType, SDKConstant.PAYACCESSTYPE.ACCESS_ALIPAY);
					map.put(Dict.txnSta, SDKConstant.TXN_STA.STA_02);
					map.put(Dict.merId, "99900000000000000233");
					map.put(Dict.page, "1");
					map.put(Dict.txnDetailFlag, "1");
					map.put("number", Thread.currentThread().getName());
					
					String data = TransUtil.mapToXml(map);
					String reqData = CommonUtil.fillString(data.length())+data;
					SocketUtil.socketConnect(reqData);
					
					synchronized (waitObject) {
						int cnt = count.decrementAndGet();
						if(cnt == 0){
							waitObject.notifyAll();
						}
							
					}
					
				}
				
			});
			
		}
		
		synchronized (waitObject) {
			while(count.get() != 0){
				waitObject.wait();
			}
		}
		Thread.sleep(1000);
		System.exit(0);
		
	}
	
}
