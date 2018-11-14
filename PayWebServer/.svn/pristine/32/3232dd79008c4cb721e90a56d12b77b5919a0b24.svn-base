package com.huateng.pay.services.bill;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.exception.FrameException;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;

/**
 * 对账单处理类
 * @author Administrator
 *
 */
public class FileHandler {

	private static Logger logger = 	LoggerFactory.getLogger(FileHandler.class);
	
	private final String fileName;
	private final boolean sameDayClear;
	private final String payAccessType;

	private static final String DEAFULT_BANK_RATE = "0.002";

	private static final String ALIPAY_FILE_PATH = Constants.getParam("alipay_downloadBill_path");
	private static final String WX_FILE_PATH = Constants.getParam("wx_downloadBill_path");

	private BufferedWriter writer;
	private RandomAccessFile rafile;

	private OrderSummary orderSummary = new OrderSummary();

	private FileHandler(String fileName, String payAccessType, boolean sameDayClear) {
		this.fileName = fileName;
		this.sameDayClear = sameDayClear;
		this.payAccessType = payAccessType;
	}

	public void record(Map<String, Object> record) throws Exception {

		checkOption();
		if (!ifRecord(record)) {
			return;
		}

		OrderDetail orderDetail = extractDetailFromRecord(record);
		writeLine(orderDetail);
		caculateSummary(orderDetail);

	};

	private OrderDetail extractDetailFromRecord(Map<String, Object> record) throws Exception {

		OrderDetail orderDetail = new OrderDetail();

		String settleDate = ObjectUtils.toString(record.get("settleDate"));
		String tradeDate = settleDate.substring(0, 8);
		String tradeTime = settleDate.substring(8, 14);
		String channelOrderNO = ObjectUtils.toString(record.get("wxOrderNo"));
		String txnSeqId = ObjectUtils.toString(record.get("txnSeqId"));
		String txnDt = ObjectUtils.toString(record.get("txnDt"));
		String txnTm = ObjectUtils.toString(record.get("txnTm"));
		String merOrder = String.format("%s%s%s", txnSeqId, txnDt, txnTm);
		String orgCode = ObjectUtils.toString(record.get("orgCode"));
		String acctNo = ObjectUtils.toString(record.get("acctNo"));
		String transType = "1";
		String status = StringConstans.OrderState.STATE_02;
		String currencyType = StringConstans.CurrencyCode.CNY;
		String tradeMoney = ObjectUtils.toString(record.get("tradeMoney"));
		String accountedFlag = ObjectUtils.toString(record.get("accountedFlag"));
		String payAccessType = ObjectUtils.toString(record.get("payAccessType"));
		String wxFeeRate = ObjectUtils.toString(record.get("wxFeeRate"));
		String alipayFeeRate = ObjectUtils.toString(record.get("alipayFeeRate"));
		String obank = ObjectUtils.toString(record.get("obank"));
		String tbank = ObjectUtils.toString(record.get("tbank"));
		String bankFeeUpperLimit = ObjectUtils.toString(record.get("bankFeeUpperLimit"));
		String bankFeeLowerLimit = ObjectUtils.toString(record.get("bankFeeLowerLimit"));
		String receiptAmount = ObjectUtils.toString(record.get("receiptAmount"));
		String subAlipayMerId = ObjectUtils.toString(record.get("subAlipayMerId"));
		String settleMethod = ObjectUtils.toString(record.get("settleMethod"));
		
		String channelFeeRate = wxFeeRate;
		if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
			channelOrderNO = ObjectUtils.toString(record.get("alipayTradeNo"));
			channelFeeRate = alipayFeeRate;
		}
		if (StringUtil.isEmpty(obank)) {
			obank = tbank;
		}
		String bankFee = this.getBankFee(obank, channelFeeRate, tradeMoney, bankFeeUpperLimit,bankFeeLowerLimit);
		String channelFee = this.getThirdPartyFee(channelFeeRate, tradeMoney);
		
		String settlement = StringUtil.formateFeeAmt(new BigDecimal(tradeMoney).subtract(new BigDecimal(channelFee))
				.subtract(new BigDecimal(bankFee)).toString());

		orderDetail.setAccountedFlag(accountedFlag);
		orderDetail.setAcctNo(acctNo);
		orderDetail.setBankFee(bankFee);
		orderDetail.setChannelFee(channelFee);
		orderDetail.setChannelOrderNo(channelOrderNO);
		orderDetail.setCurrencyType(currencyType);
		orderDetail.setMerOrder(merOrder);
		orderDetail.setOrgCode(orgCode);
		orderDetail.setPayAccessType(payAccessType);
		orderDetail.setSettlement(settlement);
		orderDetail.setStatus(status);
		orderDetail.setTradeDate(tradeDate);
		orderDetail.setTradeMoney(tradeMoney);
		orderDetail.setTradeTime(tradeTime);
		orderDetail.setTransType(transType);
		orderDetail.setReceiptAmount(receiptAmount);
		orderDetail.setSubAlipayMerId(subAlipayMerId);
		orderDetail.setSettleMethod(settleMethod);
		
		return orderDetail;
	}
	
	public void usedWhenThereisNoContextToRecord() throws IOException{
		checkOption();
	}
	

	private void checkOption() throws IOException {
		if (writer == null) {
			File file = new File(fileName);
			if(file.exists()){
				file.delete();
			}
			file.createNewFile();
			
			FileOutputStream fos = new FileOutputStream(fileName, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos, 1*1024*1024);
			OutputStreamWriter osw = new OutputStreamWriter(bos, "GBK");
			writer = new BufferedWriter(osw);
			writer.write(new char[100]);
			writer.write("\n");
		}
		if (rafile == null) {
			rafile = new RandomAccessFile(fileName, "rw");
		}
	}

	private boolean ifRecord(Map<String, Object> record) {

		String payAccessType = ObjectUtils.toString(record.get("payAccessType"));
		String settleMethod = ObjectUtils.toString(record.get("settleMethod"));
		boolean clearSameDay = (StringConstans.SettleMethod.SETTLEMETHOD0.equals(settleMethod)) ? true : false;
		if (payAccessType.equals(getPayAccessType()) && clearSameDay == isSameDayClear()) {
			return true;
		}
		return false;
	}

	private void writeLine(OrderDetail orderDetail) throws IOException {

		StringBuffer sb = new StringBuffer();
		if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
			writeAlipayLine(orderDetail, sb);
		}
		if (StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
			writeWxLine(orderDetail, sb);
		}

	}

	private void writeWxLine(OrderDetail orderDetail, StringBuffer buffer) throws IOException {

	  buffer.append(orderDetail.getTradeDate())
			.append(orderDetail.getTradeTime())
			.append(StringUtil.padding(orderDetail.getMerOrder(), 2, 32, ' '))
			.append(StringUtil.padding(orderDetail.getChannelOrderNo(), 2, 32, ' '))
			.append(StringUtil.padding(orderDetail.getAcctNo(), 2, 22, ' '))
			.append(orderDetail.getTransType())
			.append(StringUtil.padding(orderDetail.getStatus(), 2, 4, ' '))
			.append(orderDetail.getCurrencyType())
			.append(orderDetail.getTradeMoney())
			.append(StringUtil.formateFeeAmt(orderDetail.getBankFee()))
			.append(StringUtil.formateFeeAmt(orderDetail.getChannelFee()))
			.append(orderDetail.getSettlement())
			.append(StringConstans.PAYACCESSTYPE.ACCESS_WX)
			.append(StringUtil.padding(orderDetail.getOrgCode(), 2, 20, ' '));
	  
		// 新增两个字段
		String settleMethod = orderDetail.getSettleMethod();
		if (StringConstans.SettleMethod.SETTLEMETHOD0.equals(settleMethod)) {
			buffer.append(StringUtil.padding(orderDetail.getAccountedFlag(), 2, 2, ' '));
		}
		
		buffer.append("\n");

		writer.write(buffer.toString());
		writer.flush();
	}

	private void writeAlipayLine(OrderDetail orderDetail, StringBuffer buffer) throws IOException {

		String tradeDate = orderDetail.getTradeDate();
		String tradeTime = orderDetail.getTradeTime();
		String outOrderNo = orderDetail.getMerOrder();
		String channelOrderNo = orderDetail.getChannelOrderNo();
		String transType = orderDetail.getTransType();
		String tradeMoney = orderDetail.getTradeMoney();
		String receiptAmount = orderDetail.getReceiptAmount();
		String bankFee = orderDetail.getBankFee();
		String channelFee = orderDetail.getChannelFee();
		String settlement = orderDetail.getSettlement();
		String subAlipayMerId = orderDetail.getSubAlipayMerId();
		String acctNo = orderDetail.getAcctNo();
		String orgCode = orderDetail.getOrgCode();

		buffer.append(tradeDate) // 交易日期
			  .append(tradeTime) // 交易时间
			  .append(StringUtil.padding(channelOrderNo, 2, 64, ' ')) // 支付宝交易号
			  .append(StringUtil.padding(outOrderNo, 2, 32, ' ')) // 商户订单号
			  .append(StringUtil.padding(transType, 2, 2, ' ')) // 业务类型
			  .append(tradeMoney) // 订单金额
			  .append(receiptAmount) // 商家实收金额
			  .append(StringUtil.padding("", 2, 64, ' ')) // 退款批次号
			  .append(StringUtil.formateFeeAmt(bankFee)) // 银行手续费
			  .append(StringUtil.formateFeeAmt(channelFee)) // 支付宝手续费
			  .append(settlement) // 实收净额为交易金额减去所有手续费
			  .append(StringUtil.padding(subAlipayMerId, 2, 30, ' ')) // 商户识别号
			  .append(StringUtil.padding(acctNo, 2, 22, ' ')) // 商户账户
			  .append(StringUtil.padding(orgCode, 2, 6, ' ')); // 机构号
		
		String settleMethod = orderDetail.getSettleMethod();
		if (StringConstans.SettleMethod.SETTLEMETHOD0.equals(settleMethod)) {
			buffer.append(StringUtil.padding(orderDetail.getAccountedFlag(), 2, 2, ' '));
		}
		buffer.append("\n");

		writer.write(buffer.toString());
		writer.flush();
	}

	private void writeHeader(OrderSummary summary, String payAccessType) throws IOException {

		StringBuffer sb = new StringBuffer();
		if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
			writeAlipayHeader(summary, sb);
		}
		if (StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
			writeWxHeader(summary, sb);
		}
	}

	private void writeAlipayHeader(OrderSummary summary, StringBuffer buffer) throws IOException {

		long totalNumber = summary.getTotalNumber();
		BigDecimal totalTradeAmount = summary.getTotalTradeAmount();
		BigDecimal totalReceiptAmount = summary.getTotalReceiptAmount();
		BigDecimal totalChannelFee = summary.getTotalChannelFee();
		BigDecimal totalBankFee = summary.getTotalBankFee();

		buffer.append(StringUtil.padding(totalNumber + "", 2, 10, ' ')).append(
				StringUtil.padding(totalTradeAmount.toString(), 1, 12, '0')).append(
				StringUtil.padding(totalReceiptAmount.toString(), 1, 12, '0')).append(
				StringUtil.padding(totalChannelFee.toString(), 1, 12, '0')).append(
				StringUtil.padding(StringUtil.formateFeeAmt(totalBankFee.toString()), 1, 12, '0'));

		rafile.writeBytes(buffer.toString());

	}

	private void writeWxHeader(OrderSummary summary, StringBuffer buffer) throws IOException {

		long totalNumber = summary.getTotalNumber();
		BigDecimal totalTradeAmount = summary.getTotalTradeAmount();
		BigDecimal totalRefundAmoun = summary.getTotalRefundAmoun();
		BigDecimal totalChannelFee = summary.getTotalChannelFee();
		BigDecimal totalBankFee = summary.getTotalBankFee();

		String localBankTotalFee = StringUtil.padding(StringUtil.formateFeeAmt(totalBankFee.toString()), 1, 19, '0');
		String trueBankTotalFee = StringUtil.exchangeCharInString(localBankTotalFee, 0, 7);

		buffer.append(StringUtil.padding(totalNumber + "", 2, 10, ' ')).append(
				StringUtil.padding(totalTradeAmount.toString(), 1, 19, '0')).append(
				StringUtil.padding(totalRefundAmoun.toString(), 1, 19, '0')).append(
				StringUtil.padding(totalChannelFee.toString(), 1, 19, '0')).append(trueBankTotalFee);

		rafile.writeBytes(buffer.toString());
	}
	
	private void caculateSummary(OrderDetail orderDetail) throws Exception {

		String tradeMoney = orderDetail.getTradeMoney();
		String receiptAmount = orderDetail.getReceiptAmount();
		String bankFee = orderDetail.getBankFee();
		String channelFee = orderDetail.getChannelFee();

		long totalNumber = orderSummary.getTotalNumber() + 1;
		BigDecimal totalTradeAmount = orderSummary.getTotalTradeAmount().add(new BigDecimal(tradeMoney));
		BigDecimal totalBankFee = orderSummary.getTotalBankFee().add(new BigDecimal(bankFee));
		BigDecimal totalChannelFee = orderSummary.getTotalChannelFee().add(new BigDecimal(channelFee));

		orderSummary.setTotalNumber(totalNumber);
		orderSummary.setTotalTradeAmount(totalTradeAmount);
		orderSummary.setTotalBankFee(totalBankFee);
		orderSummary.setTotalChannelFee(totalChannelFee);

		if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(getPayAccessType())) {
			BigDecimal totalReceiptAmount = orderSummary.getTotalReceiptAmount().add(new BigDecimal(receiptAmount));
			orderSummary.setTotalReceiptAmount(totalReceiptAmount);
		}

	}
	
	public static FileHandler createWxT0File(String billDate) {

		return new FileHandler(WX_FILE_PATH + billDate + FileNameEnum.WXPAY_T0_BILL,
				StringConstans.PAYACCESSTYPE.ACCESS_WX, true);
	}

	public static FileHandler createWxT1File(String billDate) {

		return new FileHandler(WX_FILE_PATH + billDate + FileNameEnum.WXPAY_T1_BILL,
				StringConstans.PAYACCESSTYPE.ACCESS_WX, false);
	}

	public static FileHandler createAlipayT0File(String billDate) {

		return new FileHandler(ALIPAY_FILE_PATH + billDate + FileNameEnum.ALIPAY_T0_BILL,
				StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY, true);
	}

	public static FileHandler createAlipayT1File(String billDate) {
		return new FileHandler(ALIPAY_FILE_PATH + billDate + FileNameEnum.ALIPAY_T1_BILL,
				StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY, false);
	}
	
	public String getFileName() {
		return fileName;
	}

	public boolean isSameDayClear() {
		return sameDayClear;
	}

	public OrderSummary getOrderSummary() {
		return orderSummary;
	}

	public void doneWorkWithDeepSigh() throws IOException {
		writeHeader(orderSummary, getPayAccessType());
		free();
	}

	public String getPayAccessType() {
		return payAccessType;
	}

	private void free() {
		try {
			if (writer != null) {
				writer.close();
			}
			if (rafile != null) {
				rafile.close();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}
	
	/**
	 * 通过订单金额与手续费率获得行内手续费
	 * 
	 * @param feeRate
	 *            行内手续费率
	 * @param orderAmount
	 *            订单金额 12位数字 单位为分
	 * @param feeUpperLimit
	 *            最高手续费 可能为空 12位数字 单位为分
	 * @param feeLowerLimit
	 *            最低手续费 可能为空 12位数字 单位为分
	 * @return
	 */
	private String getBankFee(String bankFeeRate, String wxOrAliapyFeeRate, String orderAmount, String feeUpperLimit,
			String feeLowerLimit) throws Exception {

		if (StringUtil.isEmpty(wxOrAliapyFeeRate)) {
			wxOrAliapyFeeRate = StringUtil.amountTo12Str(DEAFULT_BANK_RATE);
		}

		if (StringUtil.isEmpty(bankFeeRate)) {
			bankFeeRate = wxOrAliapyFeeRate;
		}

		if (StringUtil.isEmpty(orderAmount)) {
			throw new FrameException("订单金额为空");
		}

		// 两个都不为空并且最高手续费小于等于最低手续费
		if (!StringUtil.isEmpty(feeUpperLimit) && !StringUtil.isEmpty(feeLowerLimit)
				&& new BigDecimal(feeUpperLimit).compareTo(new BigDecimal(feeLowerLimit)) <= 0) {
			throw new FrameException("最高手续费小于等于最低手续费");
		}

		// 手续费=费率乘以订单金额然后四舍五入
		BigDecimal bankFee = new BigDecimal(bankFeeRate).multiply(new BigDecimal(orderAmount)).divide(
				new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		// 手续费=费率乘以订单金额然后四舍五入
		BigDecimal wxOrAliapyFee = new BigDecimal(wxOrAliapyFeeRate).multiply(new BigDecimal(orderAmount)).divide(
				new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal bankRealFee = bankFee.subtract(wxOrAliapyFee).setScale(2, BigDecimal.ROUND_HALF_UP);

		// 12位数字
		String feeStr = StringUtil.amountTo12Str(ObjectUtils.toString(bankRealFee));

		// 如果最高手续费不为空 并且 当前手续费大于等于最高手续费 手续费就是最高手续费
		if (!StringUtil.isEmpty(feeUpperLimit)
				&& new BigDecimal(feeUpperLimit).compareTo(bankRealFee.multiply(new BigDecimal(100))) <= 0) {
			return feeUpperLimit;
		}

		// 如果最低手续费不为空 并且 当前手续费小于等于最低手续费 手续费就是最低手续费
		if (!StringUtil.isEmpty(feeLowerLimit)
				&& new BigDecimal(feeLowerLimit).compareTo(bankRealFee.multiply(new BigDecimal(100))) >= 0) {
			return feeLowerLimit;
		}
		return feeStr;
	}

	/**
	 * 通过订单金额与手续费率获得第三方的手续费
	 * 
	 * @param feeRate
	 *            第三方手续费率
	 * @param orderAmount
	 *            订单金额 12位数字 单位为分
	 * @return
	 */
	private String getThirdPartyFee(String feeRate, String orderAmount) throws Exception {
		if (StringUtil.isEmpty(feeRate)) {
			throw new FrameException("手续费率为空");
		}
		if (StringUtil.isEmpty(orderAmount)) {
			throw new FrameException("订单金额为空");
		}
		// 手续费=费率乘以订单金额然后四舍五入
		BigDecimal fee = new BigDecimal(feeRate).multiply(new BigDecimal(orderAmount)).divide(new BigDecimal(100))
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		// 12位数字
		String feeStr = StringUtil.amountTo12Str(ObjectUtils.toString(fee));
		return feeStr;
	}

}
