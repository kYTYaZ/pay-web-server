package com.huateng.pay.po.threecode;

import java.io.Serializable;

public class ThreeCodeTxnCount implements Serializable {

	private static final long serialVersionUID = 1L;

	// 交易笔数
	private String txnCount;
	// 总金额
	private String totalAmt;
	// 总手续费
	private String totalFee;
	// 入账总金额
	private String totalAcctAmt;
	
	
	
	public ThreeCodeTxnCount() {
		super();
	}

	public ThreeCodeTxnCount(String txnCount, String totalAmt, String totalFee,
			String totalAcctAmt) {
		super();
		this.txnCount = txnCount;
		this.totalAmt = totalAmt;
		this.totalFee = totalFee;
		this.totalAcctAmt = totalAcctAmt;
	}

	public String getTxnCount() {
		return txnCount;
	}

	public void setTxnCount(String txnCount) {
		this.txnCount = txnCount;
	}

	public String getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(String totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(String totalFee) {
		this.totalFee = totalFee;
	}

	public String getTotalAcctAmt() {
		return totalAcctAmt;
	}

	public void setTotalAcctAmt(String totalAcctAmt) {
		this.totalAcctAmt = totalAcctAmt;
	}

	@Override
	public String toString() {

		StringBuffer buf = new StringBuffer("[");
		buf.append("txnCount=").append(txnCount).append(",");
		buf.append("totalAmt=").append(totalAmt).append(",");
		buf.append("totalAcctAmt=").append(totalAcctAmt).append(",");
		buf.append("totalFee=").append(totalFee).append("]");

		return buf.toString();
	}
}
