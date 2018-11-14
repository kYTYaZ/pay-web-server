package com.huateng.pay.services.bill;

/**
 * 封装对账单每条明细
 * 
 * @author songquanle
 * 
 */
public class OrderDetail {

	// 交易日期
	private String tradeDate;
	// 交易时间
	private String tradeTime;
	// 商户订单号
	private String merOrder;
	// 渠道订单号
	private String channelOrderNo;
	// 商户帐号
	private String acctNo;
	// 交易类型
	private String transType;
	// 交易状态
	private String status;
	// 货币类型
	private String currencyType;
	// 交易金额
	private String tradeMoney;
	// 银行手续费
	private String bankFee;
	// 渠道手续费
	private String channelFee;
	// 结算金额
	private String settlement;
	// 支付接入类型
	private String payAccessType;
	// 机构号
	private String orgCode;
	// 清算方式
	private String settleMethod;
	// 入账状态
	private String accountedFlag;
	// 实收金额
	private String receiptAmount;
	// 支付宝子商户号
	private String subAlipayMerId;

	public String getTradeDate() {
		return tradeDate;
	}

	public void setTradeDate(String tradeDate) {
		this.tradeDate = tradeDate;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getMerOrder() {
		return merOrder;
	}

	public void setMerOrder(String merOrder) {
		this.merOrder = merOrder;
	}

	public String getChannelOrderNo() {
		return channelOrderNo;
	}

	public void setChannelOrderNo(String channelOrderNo) {
		this.channelOrderNo = channelOrderNo;
	}

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

	public String getTransType() {
		return transType;
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public String getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(String tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public String getBankFee() {
		return bankFee;
	}

	public void setBankFee(String bankFee) {
		this.bankFee = bankFee;
	}

	public String getChannelFee() {
		return channelFee;
	}

	public void setChannelFee(String channelFee) {
		this.channelFee = channelFee;
	}

	public String getSettlement() {
		return settlement;
	}

	public void setSettlement(String settlement) {
		this.settlement = settlement;
	}

	public String getPayAccessType() {
		return payAccessType;
	}

	public void setPayAccessType(String payAccessType) {
		this.payAccessType = payAccessType;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}
	
	public String getSettleMethod() {
		return settleMethod;
	}

	public void setSettleMethod(String settleMethod) {
		this.settleMethod = settleMethod;
	}

	public String getAccountedFlag() {
		return accountedFlag;
	}

	public void setAccountedFlag(String accountedFlag) {
		this.accountedFlag = accountedFlag;
	}

	public String getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(String receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public String getSubAlipayMerId() {
		return subAlipayMerId;
	}

	public void setSubAlipayMerId(String subAlipayMerId) {
		this.subAlipayMerId = subAlipayMerId;
	}

}
