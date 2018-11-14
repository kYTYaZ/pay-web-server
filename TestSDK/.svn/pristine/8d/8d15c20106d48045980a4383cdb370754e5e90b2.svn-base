package com.trade.posp.total;

import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import com.common.dicts.Dict;
import com.trade.ali.trading.AliPreCreate;
import com.trade.posp.trading.PospRefund;
import com.util.CommonUtil;
import com.util.MatrixToImageWriter;
import com.util.PropertyUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.AliValidation;
import com.validate.PospValidation;
import com.validate.common.Validation;

public class PospTotalAliRefundTestMultiThreading {
	
	public static void main(String[] args) throws Exception {
		
		String alipayMerchantId = "2088000203474835";
		final String merId = "20181019102836102836";
		int delayTime = 10;
		String orderAmount = "000000000002";
		
		final int threadCount = 4;
		final AtomicInteger count = new AtomicInteger(threadCount);
		final Object waitObject = new Object();
		
		//主扫
		Map<String,String> mapAliPreCreate = AliPreCreate.getData();
		mapAliPreCreate.put(Dict.alipayMerchantId, alipayMerchantId);
		mapAliPreCreate.put(Dict.merId, merId);
		mapAliPreCreate.put(Dict.orderAmount, orderAmount);
		String dataAliPreCreate = TransUtil.mapToXml(mapAliPreCreate);
		String reqDataAliPreCreate = CommonUtil.fillString(dataAliPreCreate)+dataAliPreCreate;
		String respDataAliPreCreate = SocketUtil.socketConnect(reqDataAliPreCreate);
		final Map<String,String> mapRespAliPreCreate = TransUtil.xmlToMap(respDataAliPreCreate.substring(6));
		Validation.validate(mapRespAliPreCreate, AliValidation.vali_AliPreCreate ,"支付扫码交易");
		
		Assert.assertEquals("支付宝主扫生成二维码失败",mapRespAliPreCreate.get(Dict.respCode), "02");
		
		//生成二维码
		String codeUrl = mapRespAliPreCreate.get(Dict.codeUrl);
    	//删除路径下所有文件
		MatrixToImageWriter.deleteAll(new File(PropertyUtil.getProperty(Dict.ewmpath)));
		MatrixToImageWriter.createEWM(codeUrl.replace("https://qr.alipay.com/", ""), codeUrl, PropertyUtil.getProperty(Dict.ewmpath));
		
		CommonUtil.sleepTimeSysout(delayTime);
		
		ExecutorService pool = Executors.newCachedThreadPool();
		for (int i = 0; i < threadCount; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						String orderNumber = mapRespAliPreCreate.get(Dict.orderNumber);
						String orderTime = mapRespAliPreCreate.get(Dict.orderTime);
						String txnSeqId = mapRespAliPreCreate.get(Dict.txnSeqId);
						String txnTime = mapRespAliPreCreate.get(Dict.txnTime);
						mapRespAliPreCreate.put(Dict.initTxnSeqId, txnSeqId);
						mapRespAliPreCreate.put(Dict.initOrderNumber, orderNumber);
						
						//退款
						CommonUtil.sleepTime(1 * 1, false);
						Map<String,String> mapRefund = PospRefund.getData();
						mapRefund.put(Dict.merId, merId);
						String[] arr1 = {Dict.initTxnSeqId, Dict.initOrderNumber};
						String key = arr1[new Random().nextInt(arr1.length)];
						mapRefund.put(key, mapRespAliPreCreate.get(key));
						String dataRefund = TransUtil.mapToXml(mapRefund);
						String reqDataRefund = CommonUtil.fillString(dataRefund)+dataRefund;
						String respDataRefund = SocketUtil.socketConnect(reqDataRefund);
						Map<String,String> mapRespRefund = TransUtil.xmlToMap(respDataRefund.substring(6));
						Validation.validate(mapRespRefund, PospValidation.vali_PospRefund ,"退款交易");
					
						synchronized (waitObject) {
							int cnt = count.decrementAndGet();
							if(cnt == 0){
								waitObject.notifyAll();
							}
								
						}
					} catch (Exception e) {
						e.printStackTrace();
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
