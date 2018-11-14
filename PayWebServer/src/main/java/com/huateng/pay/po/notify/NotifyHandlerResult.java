package com.huateng.pay.po.notify;

import com.huateng.pay.po.settle.SettleMessageReviceResult;


public class NotifyHandlerResult {
	private String resultCode;
	private String resultMsg;
	private int  count;
	private String  date;
	private SettleMessageReviceResult settleResult;
	public SettleMessageReviceResult getSettleResult() {
		return settleResult;
	}

	public void setSettleResult(SettleMessageReviceResult settleResult) {
		this.settleResult = settleResult;
	}
	private String failureReason;
	

	public String getResultCode() {
		return resultCode;
	}
	
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	
	public String getResultMsg() {
		return resultMsg;
	}
	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFailureReason() {
		return failureReason;
	}
	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}
	
	
}
