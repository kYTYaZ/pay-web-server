package com.huateng.pay.services.bill;

import java.math.BigDecimal;

/**
 * 封装对账单首行汇总
 * 
 * @author songquanle
 * 
 */
public class OrderSummary {

	// 总交易单数
	private long totalNumber = 0L;;
	// 总交易金额
	private BigDecimal totalTradeAmount = new BigDecimal("0");
	// 总实收金额
	private BigDecimal totalReceiptAmount = new BigDecimal("0");
	// 第三方手续费总额
	private BigDecimal totalChannelFee = new BigDecimal("0");
	// 总退款金额
	private BigDecimal totalRefundAmoun = new BigDecimal("0");
	// 行内手续费总额
	private BigDecimal totalBankFee = new BigDecimal("0");

	/********************* getter and setter *****************************/

	public long getTotalNumber() {
		return totalNumber;
	}

	public void setTotalNumber(long totalNumber) {
		this.totalNumber = totalNumber;
	}

	public BigDecimal getTotalTradeAmount() {
		return totalTradeAmount;
	}

	public void setTotalTradeAmount(BigDecimal totalTradeAmount) {
		this.totalTradeAmount = totalTradeAmount;
	}

	public BigDecimal getTotalReceiptAmount() {
		return totalReceiptAmount;
	}

	public void setTotalReceiptAmount(BigDecimal totalReceiptAmount) {
		this.totalReceiptAmount = totalReceiptAmount;
	}

	public BigDecimal getTotalChannelFee() {
		return totalChannelFee;
	}

	public void setTotalChannelFee(BigDecimal totalChannelFee) {
		this.totalChannelFee = totalChannelFee;
	}

	public BigDecimal getTotalRefundAmoun() {
		return totalRefundAmoun;
	}

	public void setTotalRefundAmoun(BigDecimal totalRefundAmoun) {
		this.totalRefundAmoun = totalRefundAmoun;
	}

	public BigDecimal getTotalBankFee() {
		return totalBankFee;
	}

	public void setTotalBankFee(BigDecimal totalBankFee) {
		this.totalBankFee = totalBankFee;
	}

}
