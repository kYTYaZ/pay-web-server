package com.huateng.pay.po.notify;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

public class NotifyMessage {
	
	//重试次数
	private int retryCount;
	//失败次数
	private int times;
	
	private String txnSeqId;
	
	private OnlineNotifyMessage onlineNotifyMessage;
	
	private ThreeCodeNotifyMessage threeCodeNotifyMessage;
	
	private SettleMessageToCore settleMessageToCore;
	
	@NotifyBeanAnnotation
	public SettleMessageToCore getSettleMessageToCore() {
		return settleMessageToCore;
	}

	public void setSettleMessageToCore(SettleMessageToCore settleMessageToCore) {
		this.settleMessageToCore = settleMessageToCore;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}
    
	@NotifyBeanAnnotation
	public OnlineNotifyMessage getOnlineNotifyMessage() {
		return  onlineNotifyMessage;
	}

	public void setOnlineNotifyMessage(OnlineNotifyMessage onlineNotifyMessage) {
		this.onlineNotifyMessage = onlineNotifyMessage;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}
	
	@NotifyBeanAnnotation
	public ThreeCodeNotifyMessage getThreeCodeNotifyMessage() {
		return threeCodeNotifyMessage;
	}

	public void setThreeCodeNotifyMessage(ThreeCodeNotifyMessage threeCodeNotifyMessage) {
		this.threeCodeNotifyMessage = threeCodeNotifyMessage;
	}

	public String getTxnSeqId() {
		return txnSeqId;
	}

	public void setTxnSeqId(String txnSeqId) {
		this.txnSeqId = txnSeqId;
	}


	@XmlRootElement(name="NotifyMessage")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class OnlineNotifyMessage{
		//交易码
		private String trandCode;
		//付款人账号
		private String payerAcctNbr;
		//付款人姓名
		private String payerAcctName;
		//手续费金额
		private String feeAmount;
		//二维码前置交易流水
		private String txnSeqId;
		//二维码前置交易时间
		private String txnTime;
		//商户号
		private String merId;
		//接入类型 
		private String payAccessType;
		//支付类型
		private String payType;
		//清算日期
		private String clearDate;
		//商户订单号
		private String orderNumber;
		//商户订单时间
		private String orderTime;
		//币种
		private String currencyType;
		//订单金额
		private String orderAmount;
		//交易类型
		private String tradeType;
		//应答码
		private String respCode;
		//应答描述
		private String respMsg;
		//备注
		private String remark;
		
		public String getTxnSeqId() {
			return txnSeqId;
		}

		public void setTxnSeqId(String txnSeqId) {
			this.txnSeqId = txnSeqId;
		}

		public String getTxnTime() {
			return txnTime;
		}

		public void setTxnTime(String txnTime) {
			this.txnTime = txnTime;
		}

		public String getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}

		public String getOrderTime() {
			return orderTime;
		}

		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}
		
		public String getOrderAmount() {
			return orderAmount;
		}

		public void setOrderAmount(String orderAmount) {
			this.orderAmount = orderAmount;
		}

		public String getTradeType() {
			return tradeType;
		}

		public void setTradeType(String tradeType) {
			this.tradeType = tradeType;
		}

		public String getMerId() {
			return merId;
		}

		public void setMerId(String merId) {
			this.merId = merId;
		}

		public String getPayAccessType() {
			return payAccessType;
		}

		public void setPayAccessType(String payAccessType) {
			this.payAccessType = payAccessType;
		}

		public String getPayType() {
			return payType;
		}

		public void setPayType(String payType) {
			this.payType = payType;
		}

		public String getClearDate() {
			return clearDate;
		}

		public void setClearDate(String clearDate) {
			this.clearDate = clearDate;
		}

		public String getCurrencyType() {
			return currencyType;
		}

		public void setCurrencyType(String currencyType) {
			this.currencyType = currencyType;
		}

		public String getRespCode() {
			return respCode;
		}

		public void setRespCode(String respCode) {
			this.respCode = respCode;
		}

		public String getRespMsg() {
			return respMsg;
		}

		public void setRespMsg(String respMsg) {
			this.respMsg = respMsg;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}
		
		public String getTrandCode() {
			return trandCode;
		}

		public void setTrandCode(String trandCode) {
			this.trandCode = trandCode;
		}

		public String getPayerAcctNbr() {
			return payerAcctNbr;
		}

		public void setPayerAcctNbr(String payerAcctNbr) {
			this.payerAcctNbr = payerAcctNbr;
		}

		public String getPayerAcctName() {
			return payerAcctName;
		}

		public void setPayerAcctName(String payerAcctName) {
			this.payerAcctName = payerAcctName;
		}

		public String getFeeAmount() {
			return feeAmount;
		}

		public void setFeeAmount(String feeAmount) {
			this.feeAmount = feeAmount;
		}

	}
	
	@XmlRootElement(name="NotifyMessage")
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ThreeCodeNotifyMessage{
		//二维码前置交易流水
		private String txnSeqId;
		//二维码前置交易时间
		private String txnTime;
		//商户号
		private String merId;
		//订单状态
		private String txnSta;
		//接入类型 
		private String payAccessType;
		//支付类型
		private String payType;
		//清算日期
		private String clearDate;
		//商户订单号
		private String orderNumber;
		//商户订单时间
		private String orderTime;
		//订单金额
		private String orderAmount;
		//买家信息
		private String buyerInfo;
		//手续费费率
		private String fee;
		//账号
		private String acctNo;
		//交易ID
		private String transId;
		//机构号
		private String orgCode;
		//备注
		private String remark;
		
	
		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}

		{	
			transId = "receiptNotifications";
		}
		
		public String getTxnSeqId() {
			return txnSeqId;
		}

		public void setTxnSeqId(String txnSeqId) {
			this.txnSeqId = txnSeqId;
		}

		public String getTxnTime() {
			return txnTime;
		}

		public void setTxnTime(String txnTime) {
			this.txnTime = txnTime;
		}

		public String getOrderNumber() {
			return orderNumber;
		}

		public void setOrderNumber(String orderNumber) {
			this.orderNumber = orderNumber;
		}

		public String getOrderTime() {
			return orderTime;
		}

		public void setOrderTime(String orderTime) {
			this.orderTime = orderTime;
		}
		
		public String getOrderAmount() {
			return orderAmount;
		}

		public void setOrderAmount(String orderAmount) {
			this.orderAmount = orderAmount;
		}
		
		public String getMerId() {
			return merId;
		}

		public void setMerId(String merId) {
			this.merId = merId;
		}

		public String getPayAccessType() {
			return payAccessType;
		}

		public void setPayAccessType(String payAccessType) {
			this.payAccessType = payAccessType;
		}

		public String getPayType() {
			return payType;
		}

		public void setPayType(String payType) {
			this.payType = payType;
		}

		public String getClearDate() {
			return clearDate;
		}

		public void setClearDate(String clearDate) {
			this.clearDate = clearDate;
		}

		public String getBuyerInfo() {
			return buyerInfo;
		}

		public void setBuyerInfo(String buyerInfo) {
			this.buyerInfo = buyerInfo;
		}

		public String getFee() {
			return fee;
		}

		public void setFee(String fee) {
			this.fee = fee;
		}

		public String getTxnSta() {
			return txnSta;
		}

		public void setTxnSta(String txnSta) {
			this.txnSta = txnSta;
		}

		public String getTransId() {
			return transId;
		}

		public String getAcctNo() {
			return acctNo;
		}

		public void setAcctNo(String acctNo) {
			this.acctNo = acctNo;
		}

		public String getOrgCode() {
			return orgCode;
		}

		public void setOrgCode(String orgCode) {
			this.orgCode = orgCode;
		}
		
	}
	//@XmlRootElement(name="NotifyMessage")
	//@XmlAccessorType(XmlAccessType.FIELD)
	public static class SettleMessageToCore {
		private String accountNo;          //收单商户账号
		private String currencyCode;       //币种
		private String merName;            //商户名称
		private String outAccountNo;       //用户账号  ，暂时为空
		private String payerName;          //用户姓名
		private String tradeMoney;         //消费金额
		private String chargeFee;          //手续费
		private String checkAccountNo;     //对账编号
		private String merId;              //商户编号
		private String payAccessType;      //支付方式（支付宝or微信）
		private String orgId;              //机构号
		private String postscript;         //附言
		private String remark;             //备注
		private String reverse;            //备用
		private String txnDt;              //交易日期
		private String txnTm;              //交易时间
		public String getCurrencyCode() {
			return currencyCode;
		}
		public String getTxnDt() {
			return txnDt;
		}
		public void setTxnDt(String txnDt) {
			this.txnDt = txnDt;
		}
		public String getTxnTm() {
			return txnTm;
		}
		public void setTxnTm(String txnTm) {
			this.txnTm = txnTm;
		}
		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}
		public String getAccountNo() {
			return accountNo;
		}
		public void setAccountNo(String accountNo) {
			this.accountNo = accountNo;
		}
		
		public String getMerName() {
			return merName;
		}
		public void setMerName(String merName) {
			this.merName = merName;
		}
		public String getOutAccountNo() {
			return outAccountNo;
		}
		public void setOutAccountNo(String outAccountNo) {
			this.outAccountNo = outAccountNo;
		}
		public String getPayerName() {
			return payerName;
		}
		public void setPayerName(String payerName) {
			this.payerName = payerName;
		}
		public String getTradeMoney() {
			return tradeMoney;
		}
		public void setTradeMoney(String tradeMoney) {
			this.tradeMoney = tradeMoney;
		}
		public String getChargeFee() {
			return chargeFee;
		}
		public void setChargeFee(String chargeFee) {
			this.chargeFee = chargeFee;
		}
		public String getCheckAccountNo() {
			return checkAccountNo;
		}
		public void setCheckAccountNo(String checkAccountNo) {
			this.checkAccountNo = checkAccountNo;
		}
		public String getMerId() {
			return merId;
		}
		public void setMerId(String merId) {
			this.merId = merId;
		}
		public String getPayAccessType() {
			return payAccessType;
		}
		public void setPayAccessType(String payAccessType) {
			this.payAccessType = payAccessType;
		}
		public String getOrgId() {
			return orgId;
		}
		public void setOrgId(String orgId) {
			this.orgId = orgId;
		}
		public String getPostscript() {
			return postscript;
		}
		public void setPostscript(String postscript) {
			this.postscript = postscript;
		}
		public String getRemark() {
			return remark;
		}
		public void setRemark(String remark) {
			this.remark = remark;
		}
		public String getReverse() {
			return reverse;
		}
		public void setReverse(String reverse) {
			this.reverse = reverse;
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(txnDt)
			  .append(txnTm)
			  .append(accountNo)
			  .append(currencyCode)
			  .append(merName)
			  .append(outAccountNo)
			  .append(payerName)
			  .append(tradeMoney)
			  .append(chargeFee)
			  .append(checkAccountNo)
			  .append(merId)
			  .append(payAccessType)
			  .append(orgId)
			  .append(postscript)
			  .append(remark)
			  .append(reverse);
			  
			
			return sb.toString();
		}
		
		
	}
}
