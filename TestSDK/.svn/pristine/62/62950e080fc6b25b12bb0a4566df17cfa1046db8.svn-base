package com.trade.wx.test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.common.constants.SDKConstant;
import com.common.dicts.Dict;
import com.trade.wx.trading.WxClose;
import com.util.CommonUtil;
import com.util.DateUtil;
import com.util.MatrixToImageWriter;
import com.util.PropertyUtil;
import com.util.SocketUtil;
import com.util.TransUtil;
import com.validate.PospValidation;
import com.validate.common.Validation;

public class WxCreateMerTest {
	
	@Test
	public void refreshMoreConfig() {
		System.out.println("刷新多费率配置");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", SDKConstant.TXN_CODE.MORE_FEE_CONFIG);

		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);
	}

	public static void getDownLoad() {
		System.out.println("开始组装微信下载对账单");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", SDKConstant.TXN_CODE.WX_BILL_DOWN);
		map.put("protocolContent", "20180926");

		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);
	}

	@Test
	public void getSingleDownLoad() {
		System.out.println("开始组装微信下载对账单");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", SDKConstant.TXN_CODE.WX_BILL_SINGLE_DOWN);
		map.put("billDate", "20181008");
		map.put("rate", "20");

		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);
	}

	public static void sumWxBill() {
		System.out.println("开始组装微信下载对账单");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", SDKConstant.TXN_CODE.WX_BILL_SUM_DOWN);
		map.put("billDate", "20180925");

		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);
	}

	public static void wxClose() {

		Map<String, String> map = WxClose.getData();
		map.put("rateChannel", "20");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);

	}

	@Test
	public void wxCreateMer() {
		try {

			Map<String, String> map = new HashMap<String, String>();
			map.put("txnCode", SDKConstant.TXN_CODE.CODE_5001);
			map.put("mchtName", "浙江省农村信用");
			map.put("mchtShortName", "浙江省农村信用社联合社");
			map.put("servicePhone", "15900001238");
			map.put("business", "165");
			map.put("contactName", "客户810");
			map.put("contactPhone", "13524862405");
			map.put("orgCode", "801000");
			String merId = DateUtil.getDateStr("yyyyMMddHHmmssHHmmss");
			map.put("merId", merId);//249421447 249732157
			map.put("mchtRemark", merId);
			map.put("rateChannel", "20");
			String data = TransUtil.mapToXml(map);
			String reqData = CommonUtil.fillString(data) + data;
			String returndate = SocketUtil.socketConnect(reqData);
			returndate = returndate.substring(6);
			System.out.println(returndate);
			Map<String, String> reMap = TransUtil.xmlToMap(returndate);

			Map<String, String> map2 = new HashMap<String, String>();
			map2.put("txnCode", "5004");
			map2.put("merId", merId);
			map2.put("subMchId", reMap.get("subMchId"));
			String data2 = TransUtil.mapToXml(map2);
			String reqData2 = CommonUtil.fillString(data2) + data2;
			SocketUtil.socketConnect(reqData2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void wxQueryMer() {

		System.out.println("开始组装微信商户查询报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", "5004");
//		map.put("merId", "1900009211");
		map.put("subMchId", "248100834");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);

	}

	/**
	 * 被扫 被扫查询 撤销 撤销查询
	 */
	@Test
	public void wxMicroPay() {

		try {
			System.out.println("开始组装微信被扫报文");

			String orderTime = DateUtil.getDateStr("yyyyMMddHHmmss");
			String orderNumber = "1000" + orderTime;
			String merId = "20181017110238110238";
			String subWxMerId = "249732157";
			String authCode = "134657866765512445";
			String wxRevokeTime = DateUtil.getDateStr("yyyyMMddHHmmss");
			String wxRevokeNumber = "1000" + wxRevokeTime;

			boolean microPay = true;
			boolean querypay = true;
			boolean revoke = false;
			boolean revokequery = false;
			boolean refund = false;

			if (microPay) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("orderAmount", "000000000003");
				map.put("channel", "6001");
				map.put("orderNumber", orderNumber);
				map.put("orderTime", orderTime);
				map.put("merId", merId);
				map.put("payType", "15");
				map.put("transType", "01");
				map.put("merName", "1417");
				map.put("isCredit", "1");
				map.put("subWxMerId", subWxMerId);
				map.put("deviceInfo", "POS");
				map.put("currencyType", "156");
				map.put("payAccessType", "02");
				map.put("authCode", authCode);
				map.put("txnCode", "1002");
				map.put("rateChannel", "20");
				String data = TransUtil.mapToXml(map);
				String reqData = CommonUtil.fillString(data) + data;
				SocketUtil.socketConnect(reqData);
			}

			if (querypay) {
				System.out.println("开始组装微信订单查询报文");
				Thread.sleep(1000*10);
				Map<String, String> map2 = new HashMap<String, String>();
				map2.put("txnCode", "3001");
				map2.put("orderNumber", orderNumber);
				map2.put("orderTime", orderTime);
				map2.put("transType", "35");
				map2.put("channel", "6001");
				map2.put("payAccessType", "02");
				map2.put("merId", merId);
				String data2 = TransUtil.mapToXml(map2);
				String reqData2 = CommonUtil.fillString(data2) + data2;
				SocketUtil.socketConnect(reqData2);
			}
			
			if(refund) {
				System.out.println("开始组装退款报文");
				Thread.sleep(1000*10);
				for(int i=0;i<5;i++) {
					Map<String,String >map= new HashMap<String, String>();
					map.put(Dict.txnCode,"3008");
					map.put(Dict.outRefundNo, "2000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
					map.put(Dict.outRefundTime, DateUtil.getDateStr("yyyyMMddHHmmss"));
					map.put(Dict.refundReason, "七天无理由退款");
					
					map.put(Dict.merId, merId);
	//				map.put(Dict.initTxnSeqId, "1006706660");
					map.put(Dict.initOrderNumber, orderNumber);
					map.put(Dict.refundAmount, "000000000001");
					String data = TransUtil.mapToXml(map);
					String reqData = CommonUtil.fillString(data)+data;
					String respDataRefund = SocketUtil.socketConnect(reqData);
					Map<String,String> mapRespRefund = TransUtil.xmlToMap(respDataRefund.substring(6));
					Validation.validate(mapRespRefund, PospValidation.vali_PospRefund ,"退款交易");
				}
			}

			if (revoke) {
				System.out.println("开始组装微信撤销报文");

				Map<String, String> map3 = new HashMap<String, String>();
				map3.put("orderAmount", "000000000001");
				map3.put("channel", "6001");
				map3.put("orderNumber", wxRevokeNumber);
				map3.put("orderTime", wxRevokeTime);
				map3.put("merId", "20181017110238110238");
				map3.put("subWxMerId", subWxMerId);
				map3.put("transType", "31");
				map3.put("currencyType", "156");
				map3.put("payAccessType", "02");
				map3.put("txnCode", "2001");
				map3.put("initOrderNumber", orderNumber);
				map3.put("initOrderTime", orderTime);
				String data3 = TransUtil.mapToXml(map3);
				String reqData3 = CommonUtil.fillString(data3) + data3;
				SocketUtil.socketConnect(reqData3);
			}

			if (revokequery) {
				System.out.println("开始组装微信撤销订单查询报文");
				Map<String, String> map4 = new HashMap<String, String>();
				map4.put("txnCode", "3001");
				map4.put("orderNumber", wxRevokeNumber);
				map4.put("orderTime", wxRevokeTime);
				map4.put("transType", "35");
				map4.put("channel", "6001");
				map4.put("payAccessType", "02");
				map4.put("merId", merId);
				String data4 = TransUtil.mapToXml(map4);
				String reqData4 = CommonUtil.fillString(data4) + data4;
				SocketUtil.socketConnect(reqData4);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void wxPreCreate() throws Exception {

		System.out.println("开始组装微信主扫报文");
		
		String orderTime = DateUtil.getDateStr("yyyyMMddHHmmss");
		String orderNumber = "1000" + orderTime;
		String merId = "20181017110238110238";
		String subWxMerId = "249732157";

		Map<String, String> map = new HashMap<String, String>();
		map.put("txnCode", "1001");
		map.put("orderAmount", "000000000003");
		map.put("orderNumber", orderNumber);
		map.put("orderTime", orderTime);
		map.put("transType", "01");
		map.put("merId", merId);
		map.put("merName", "1107");
		map.put("channel", "6001");
		map.put("payAccessType", "02");
		map.put("currencyType", "156");
		map.put("payType", "12");
		map.put("isCredit", "1");
		map.put("subWxMerId", subWxMerId);
		map.put("deviceInfo", "POS");
		map.put("rateChannel", "20");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		String respDataAliPreCreate = SocketUtil.socketConnect(reqData);

		Map<String, String> mapRespAliPreCreate = TransUtil.xmlToMap(respDataAliPreCreate.substring(6));

		// 生成二维码
		String codeUrl = mapRespAliPreCreate.get(Dict.codeUrl);
		MatrixToImageWriter.createEWM(orderNumber, codeUrl, PropertyUtil.getProperty(Dict.ewmpath));

		System.out.println("开始组装微信订单查询报文");
		Map<String, String> map2 = new HashMap<String, String>();
		map2.put("txnCode", "3001");
		map2.put("orderNumber", orderNumber);
		map2.put("orderTime", orderTime);
		map2.put("transType", "35");
		map2.put("channel", "6001");
		map2.put("payAccessType", "02");
		map2.put("merId", merId);
		String data2 = TransUtil.mapToXml(map2);
		String reqData2 = CommonUtil.fillString(data2) + data2;
		SocketUtil.socketConnect(reqData2);
		
		
		System.out.println("开始组装微信关闭订单报文");
		
		Map<String,String >map3= new HashMap<String, String>();
		map3.put("txnCode", "2002");
		map3.put("channel", "6001");
		map3.put("initOrderNumber", orderNumber);
		map3.put("initOrderTime",orderTime);
		map3.put("payAccessType", "02");
		map3.put("merId", merId);
		String data3 = TransUtil.mapToXml(map3);
		String reqData3 = CommonUtil.fillString(data3) + data3;
		SocketUtil.socketConnect(reqData3);
	}

	@Test
	public void wxQuery() {

		System.out.println("开始组装微信订单查询报文");

		Map<String, String> map = new HashMap<String, String>();

		map.put("txnCode", "3001");
		map.put("orderNumber", "100020181105154738");
		map.put("orderTime", "20181105154738");
		map.put("transType", "35");
		map.put("channel", "6001");
		map.put("payAccessType", "02");
		map.put("merId", "20181017110238110238");
//		map.put("rateChannel", "20");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);

	}

	@Test
	public void wxRevoke() {

		System.out.println("开始组装微信撤销报文");

		Map<String, String> map = new HashMap<String, String>();
		map.put("orderAmount", "000000000899");
		map.put("channel", "6001");
		map.put("orderNumber", "1000" + DateUtil.getDateStr("yyyyMMddHHmmss"));
		map.put("orderTime", DateUtil.getDateStr("yyyyMMddHHmmss") + "");
		map.put("merId", "20181017110238110238");
		map.put("subWxMerId", "247840419");
		map.put("transType", "31");
		map.put("currencyType", "156");
		map.put("payAccessType", "02");
		map.put("txnCode", "2001");
		map.put("initOrderNumber", "100020181018181746");
		map.put("initOrderTime", "20181018181746");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data) + data;
		SocketUtil.socketConnect(reqData);

	}
	
	@Test
	public void refund() throws Exception {
		System.out.println("开始组装退款报文");
		String merId = "20181017110238110238";
		String outRefundTime = DateUtil.getDateStr("yyyyMMddHHmmss");
		String outRefundNo = "20" +(new Random().nextInt(100))+ outRefundTime; 
		
		Map<String,String >map= new HashMap<String, String>();
		map.put(Dict.txnCode,"3008");
		map.put(Dict.outRefundNo, outRefundNo);
		map.put(Dict.outRefundTime, outRefundTime);
		map.put(Dict.refundReason, "七天无理由退款");
		
		map.put(Dict.merId, merId);
		map.put(Dict.initTxnSeqId, "1006707256");
//		map.put(Dict.initOrderNumber, "");
		map.put(Dict.refundAmount, "000000000001");
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		String respDataRefund = SocketUtil.socketConnect(reqData);
		Map<String,String> mapRespRefund = TransUtil.xmlToMap(respDataRefund.substring(6));
		Validation.validate(mapRespRefund, PospValidation.vali_PospRefund ,"退款交易");
	}
	
	@Test
	public void refundQuery() throws Exception {
		System.out.println("开始组装退款查询报文");
		
		Map<String,String >map= new HashMap<String, String>();
		
		String merId = "20181017110238110238";
		String txnSeqId = "1006707262";
		
		map.put(Dict.txnCode,"3009");
		map.put(Dict.merId, merId);
		map.put(Dict.txnSeqId, txnSeqId);
		String data = TransUtil.mapToXml(map);
		String reqData = CommonUtil.fillString(data)+data;
		String respDataRefund = SocketUtil.socketConnect(reqData);
		Map<String,String> mapRespRefund = TransUtil.xmlToMap(respDataRefund.substring(6));
		Validation.validate(mapRespRefund, PospValidation.vali_PospRefund ,"退款查询交易");
	}
	
//	public static void main(String[] args) {
//		try {
//			refund();
//			wxMicroPay();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	public static void main(String[] args) throws InterruptedException {
//
//		
//		final Object waitObject = new Object();
//		ExecutorService pool = Executors.newCachedThreadPool();
//		final String[]  str= new String[] {"1006707158","1006707159","1006707160","1006707161","1006707162"}; 
//		final int threadCount = str.length;
//		final AtomicInteger count = new AtomicInteger(threadCount);
//		for (int i = 0; i < threadCount; i++) {
//			final int a = i;
//			pool.execute(new Runnable() {
//				@Override
//				public void run() {
//					try {
////						refund();
//						refundQuery(str[a]);
//						synchronized (waitObject) {
//							int cnt = count.decrementAndGet();
//							if(cnt == 0){
//								waitObject.notifyAll();
//							}
//								
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				
//			});
//			
//		}
//		
//		synchronized (waitObject) {
//			while(count.get() != 0){
//				waitObject.wait();
//			}
//		}
//	
//	}
	
	
	
}
