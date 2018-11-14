package com.huateng.pay.services.threecode.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.domain.TradeFundBill;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.po.threecode.ThreeCodeTxnCount;
import com.huateng.pay.services.alipay.AliPayPayService;
import com.huateng.pay.services.bill.Dispatcher;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;
import com.huateng.pay.services.db.ITimingTaskService;
import com.huateng.pay.services.threecode.IThreeCodeService;
import com.huateng.pay.services.weixin.WxPayService;
import com.huateng.utils.FileUtil;
import com.wldk.framework.db.PageVariable;

public class ThreeCodeServiceImpl implements IThreeCodeService {

	private static final Logger logger = LoggerFactory.getLogger(ThreeCodeServiceImpl.class);
	private static final String START_SUFFIX = "000000";
	private static final String END_SUFFIX = "235959";
	private static final String DEAFULT_BANK_RATE = "0.002";
	private static final String DEFAULT_FEE = "0.002";
	private static final String TXN_DETAIL_FLAG = "1";
	private static final String STEP = "10";
	private static final String PAGE = "1";
	private ITimingTaskService timingTaskService;
	private IOrderService orderService;
	private AliPayPayService aliPayPayService;
	private WxPayService wxPayService;
	private IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService;
	
	// 分页数
	private static int recordPerPage = Integer.valueOf(Constants.getParam("record_per_page"));

	/**
	 * 生成三码合一微信和支付宝对账单（现在不用了）
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam creatWxAndAlipayBill(InputParam input) throws FrameException {

		logger.debug("---------------  生成三码合一微信和支付宝对账单	START---------------");

		OutputParam outputParam = new OutputParam();

		// 支付宝文件流
		BufferedWriter alipayWriter = null;
		FileOutputStream alipayfos = null;
		RandomAccessFile alipayRaf = null;
		// 微信文件流
		BufferedWriter wxWriter = null;
		FileOutputStream wxFos = null;
		RandomAccessFile wxRaf = null;

		try {

			String orderDate = ObjectUtils.toString(input.getValue("orderDate"));
			logger.debug("[生成三码合一对账单] 生成三码合一对账单时间orderDate = " + orderDate);

			if (StringUtil.isEmpty(orderDate)) {
				logger.debug("[生成三码合一对账单] 对账单时间orderDate不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "生成三码合一对账单时间为不能为空");
				return outputParam;
			}

			Map<String, String> queryMap = new HashMap<String, String>();
			queryMap.put("channel", StringConstans.CHANNEL.CHANNEL_SELF);
			queryMap.put("txnType", StringConstans.TransType.TRANS_CONSUME);
			queryMap.put("txnSta", StringConstans.OrderState.STATE_02);
			queryMap.put("wxAccess", StringConstans.PAYACCESSTYPE.ACCESS_WX);
			queryMap.put("alipayAccess", StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY);
			queryMap.put("startSettleDate", orderDate + START_SUFFIX);
			queryMap.put("endSettleDate", orderDate + END_SUFFIX);

			logger.debug("[生成三码合一对账单]	查询三码合一微信和支付宝总订单数		开始");

			int totalBillNumber = timingTaskService.queryThreeCodeOrderNumber(queryMap);

			logger.debug("[生成三码合一对账单]	查询三码合一微信和支付宝总订单数		结束");

			logger.debug("[生成三码合一对账单]	微信和支付宝总订单数totalBillNumber=" + totalBillNumber);

			PageVariable page = new PageVariable();
			page.setRecordPerPage(recordPerPage);
			int pages = totalBillNumber % recordPerPage == 0 ? totalBillNumber / recordPerPage : totalBillNumber
					/ recordPerPage + 1;
			logger.debug("[生成三码合一对账单]	分批查询总批次pages=" + pages);

			// 支付宝总交易笔数
			int totalAlipayOrderCount = 0;
			// 支付宝总交易金额
			BigDecimal totalAlipayTradeAmount = new BigDecimal(0);
			// 支付宝总商家实收金额
			BigDecimal totalReceiptAmount = new BigDecimal(0);
			// 支付宝手续费总额
			BigDecimal totalAlipayFeeAmount = new BigDecimal(0);
			// 支付宝对账单本行总手续费
			BigDecimal totalBankFeeAlipay = new BigDecimal(0);

			// 微信总交易单数
			int totalWxOrderCount = 0;
			// 微信总交易金额
			BigDecimal totalWxTradeAmount = new BigDecimal(0);
			// 微信总退款金额
			BigDecimal totalRefundAmount = new BigDecimal(0);
			// 微信总手续费
			BigDecimal totalWxFeeAmount = new BigDecimal(0);
			// 微信对账单本行总手续费
			BigDecimal totalBankFeeWx = new BigDecimal(0);

			String alipayFileName = orderDate + "_2PAYEWM_ZFB";
			logger.debug("[生成三码合一对账单]	支付宝对账单名称alipayFileName=" + alipayFileName);

			String wxFileName = orderDate + "_2PAYEWM_CFT";
			logger.debug("[生成三码合一对账单]	微信对账单名称=" + wxFileName);

			File alipayFile = new File(Constants.getParam("alipay_downloadBill_path") + alipayFileName);
			// logger.info("[生成三码合一对账单]	支付宝对账单路径alipayFilePath=" +
			// alipayFile.getAbsolutePath());

			File wxFile = new File(Constants.getParam("wx_downloadBill_path") + wxFileName);
			// logger.info("[生成三码合一对账单]	微信对账单账单路径wxFilePath=" +
			// wxFile.getAbsolutePath());

			if (alipayFile.exists()) {
				alipayFile.delete();
			}
			alipayFile.createNewFile();

			if (wxFile.exists()) {
				wxFile.delete();
			}
			wxFile.createNewFile();

			alipayfos = new FileOutputStream(alipayFile, true);
			BufferedOutputStream alipaybos = new BufferedOutputStream(alipayfos, 1 * 1024 * 1024);
			OutputStreamWriter alipayosw = new OutputStreamWriter(alipaybos, "GBK");
			alipayWriter = new BufferedWriter(alipayosw);
			alipayWriter.write(new char[100]);
			alipayWriter.write("\n");

			wxFos = new FileOutputStream(wxFile, true);
			BufferedOutputStream wxBos = new BufferedOutputStream(wxFos, 1 * 1024 * 1024);
			OutputStreamWriter wxosw = new OutputStreamWriter(wxBos, "GBK");
			wxWriter = new BufferedWriter(wxosw);
			wxWriter.write(new char[100]);
			wxWriter.write("\n");

			for (int i = 1; i <= pages; i++) {

				page.setCurrentPage(i);

				logger.debug("[生成三码合一对账单]	查询三码合一支付宝和微信订单信息		开始");

				OutputParam queryOrderOut = timingTaskService.timingQueryThreeCodeBills(queryMap, page);

				logger.debug("[生成三码合一对账单]	查询三码合一支付宝和微信订单信息		结束");

				if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOut.getReturnCode())) {
					logger.debug("[生成三码合一对账单] 查询三码合一支付宝和微信订单失败:" + queryOrderOut.getReturnMsg());
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "查询三码合一支付宝和微信订单失败");
					return outputParam;
				}

				List<Map<String, Object>> orderList = queryOrderOut.takeReturnList();

				if (orderList != null && !orderList.isEmpty()) {

					for (Map<String, Object> order : orderList) {

						// 支付接入类型
						String payAccessType = ObjectUtils.toString(order.get("payAccessType"));

						if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {

							// 单笔交易金额
							String tradeMoney = ObjectUtils.toString(order.get("tradeMoney"));
							// logger.info("[生成三码合一对账单] 交易金额tradeMoney=" +
							// tradeMoney);

							// 单笔商家实收金额
							String receiptAmount = ObjectUtils.toString(order.get("receiptAmount"));
							// logger.info("[生成三码合一对账单] 商家实收金额receiptAmount=" +
							// receiptAmount);

							// 支付宝手续费率
							String alipayFeeRate = ObjectUtils.toString(order.get("alipayFeeRate"));
							// logger.info("[生成三码合一对账单] 支付宝费率alipayFeeRate=" +
							// alipayFeeRate);

							// 本行手续费率
							String bankFeeRate = ObjectUtils.toString(order.get("bankFeeRate"));
							// logger.info("[生成三码合一对账单] 本行手续费率bankFeeRate=" +
							// bankFeeRate);

							// 支付宝单笔最高手续费
							String bankFeeUpperLimit = ObjectUtils.toString(order.get("bankFeeUpperLimit"));
							// logger.info("[生成三码合一对账单] 本行最高手续费bankFeeUpperLimit="
							// + bankFeeUpperLimit);

							// 支付宝单笔最低手续费
							String bankFeeLowerLimit = ObjectUtils.toString(order.get("bankFeeLowerLimit"));
							// logger.info("[生成三码合一对账单] 本行最低手续费bankFeeUpperLimit="
							// + bankFeeLowerLimit);

							// 单笔手续费
							String bankFee = this.getBankFee(bankFeeRate, alipayFeeRate, tradeMoney, bankFeeUpperLimit,
									bankFeeLowerLimit);
							// logger.info("[生成三码合一对账单] 本行单笔手续费bankFee=" +
							// bankFee);

							String alipayFee = this.getThirdPartyFee(alipayFeeRate, tradeMoney);
							// logger.info("[生成三码合一对账单] 支付宝单笔手续费alipayFee=" +
							// alipayFee);

							order.put("alipayFee", alipayFee);
							order.put("bankFee", bankFee);

							++totalAlipayOrderCount;
							totalAlipayTradeAmount = totalAlipayTradeAmount.add(new BigDecimal(tradeMoney));
							totalReceiptAmount = totalReceiptAmount.add(new BigDecimal(receiptAmount));
							totalAlipayFeeAmount = totalAlipayFeeAmount.add(new BigDecimal(alipayFee));
							totalBankFeeAlipay = totalBankFeeAlipay.add(new BigDecimal(bankFee));

							// logger.info("[生成三码合一对账单]  写入单笔支付宝交易明细  开始");

							FileUtil.writeIntoAlipayFile(order, alipayFile, alipayWriter);

							// logger.info("[生成三码合一对账单]  写入单笔支付宝交易明细  结束");
						}

						if (StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {

							// 微信单笔交易金额
							String tradeMoney = ObjectUtils.toString(order.get("tradeMoney"));
							// logger.info("[生成三码合一对账单] 交易金额tradeMoney=" +
							// tradeMoney);

							// 微信订手续费率
							String wxFeeRate = ObjectUtils.toString(order.get("wxFeeRate"));
							// logger.info("[生成三码合一对账单] 微信费率wxFeeRate=" +
							// wxFeeRate);

							// 本行手续费率
							String bankFeeRate = ObjectUtils.toString(order.get("bankFeeRate"));
							// logger.info("[生成三码合一对账单] 本行手续费率bankFeeRate=" +
							// bankFeeRate);

							// 支付宝单笔最高手续费
							String bankFeeUpperLimit = ObjectUtils.toString(order.get("bankFeeUpperLimit"));
							// logger.info("[生成三码合一对账单] 本行最高手续费bankFeeUpperLimit="
							// + bankFeeUpperLimit);

							// 支付宝单笔最低手续费
							String bankFeeLowerLimit = ObjectUtils.toString(order.get("bankFeeLowerLimit"));
							// logger.info("[生成三码合一对账单] 本行最低手续费bankFeeUpperLimit="
							// + bankFeeLowerLimit);

							// 本行单笔手续费
							String bankFee = this.getBankFee(bankFeeRate, wxFeeRate, tradeMoney, bankFeeUpperLimit,
									bankFeeLowerLimit);
							// logger.info("[生成三码合一对账单] 本行单笔手续费bankFee=" +
							// bankFee);

							// 微信单笔手续费
							String wxFee = this.getThirdPartyFee(wxFeeRate, tradeMoney);
							// logger.info("[生成三码合一对账单] 微信单笔手续费wxFee=" + wxFee);

							order.put("wxFee", wxFee);
							order.put("bankFee", bankFee);

							++totalWxOrderCount;
							totalWxTradeAmount = totalWxTradeAmount.add(new BigDecimal(tradeMoney));
							totalWxFeeAmount = totalWxFeeAmount.add(new BigDecimal(wxFee));
							totalBankFeeWx = totalBankFeeWx.add(new BigDecimal(bankFee));

							// logger.info("[生成三码合一对账单]  写入单笔微信交易明细  开始");

							FileUtil.writeIntoWxFile(order, wxFile, wxWriter);

							// logger.info("[生成三码合一对账单]  写入单笔微信交易明细  结束");
						}
					}
				}
			}

			wxRaf = new RandomAccessFile(wxFile, "rw");

			// 0000000_100000000011 总长度19，前面补了7个0;
			String localBankTotalFee = StringUtil.padding(StringUtil.formateFeeAmt(totalBankFeeWx.toString()), 1, 19,
					'0');
			// 将第0位和第7位的字符交换即可;
			String trueBankTotalFee = StringUtil.exchangeCharInString(localBankTotalFee, 0, 7);

			StringBuffer sbWx = new StringBuffer();
			sbWx.append(StringUtil.padding(totalWxOrderCount + "", 2, 10, ' ')).append(
					StringUtil.padding(totalWxTradeAmount.toString(), 1, 19, '0')).append(
					StringUtil.padding(totalRefundAmount.toString(), 1, 19, '0')).append(
					StringUtil.padding(totalWxFeeAmount.toString(), 1, 19, '0')).append(trueBankTotalFee);

			wxRaf.writeBytes(sbWx.toString());

			alipayRaf = new RandomAccessFile(alipayFile, "rw");
			StringBuffer sbAlipay = new StringBuffer();
			sbAlipay.append(StringUtil.padding(totalAlipayOrderCount + "", 2, 10, ' ')).append(
					StringUtil.padding(totalAlipayTradeAmount.toString(), 1, 12, '0')).append(
					StringUtil.padding(totalReceiptAmount.toString(), 1, 12, '0')).append(
					StringUtil.padding(totalAlipayFeeAmount.toString(), 1, 12, '0')).append(
					StringUtil.padding(StringUtil.formateFeeAmt(totalBankFeeAlipay.toString()), 1, 12, '0'));

			alipayRaf.writeBytes(sbAlipay.toString());

			logger.debug("[生成三码合一对账单] 结束时间:" + DateUtil.getCurrentDateTime());

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "生成三码合一微信和支付宝对账单成功");

		} catch (Exception e) {
			logger.error("[生成三码合一对账单] 生成账单出现异常:" + e.getMessage(),e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "生成三码合一对账单异常");
		} finally {
			try {
				if (alipayWriter != null) {
					alipayWriter.close();
				}
				if (alipayRaf != null) {
					alipayRaf.close();
				}
				if (alipayfos != null) {
					alipayfos.close();
				}
				if (wxWriter != null) {
					wxWriter.close();
				}
				if (wxRaf != null) {
					wxRaf.close();
				}
				if (wxFos != null) {
					wxFos.close();
				}
			} catch (IOException e) {
				logger.error("生成三码合一对账单] 关闭流异常:" + e.getMessage(),e);
			}
		}

		logger.debug("---------------生成三码合一对账单处理流程    END---------------");

		return outputParam;
	}

	/**
	 * 下载三码和一流水
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam downloadThreeCodeBill(InputParam input) throws FrameException {

		logger.info("生成三码合一订单流水流程START,请求报文:"+input.toString());

		OutputParam outputParam = new OutputParam();

		FileOutputStream fos = null;
		BufferedWriter buffer = null;

		try {

			String billDate = ObjectUtils.toString(input.getValue("orderDate"));
			logger.debug("[生成三码合一订单流水]	查询三码合一订单时间billDate = " + billDate);

			if (StringUtil.isEmpty(billDate)) {
				logger.debug("[生成三码合一订单流水]	生成三码合一订单流水日期不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "生成三码合一订单流水日期不能为空");
				return outputParam;
			}

			String fileName = "THREE_CODE_ORDER_" + billDate;
			logger.debug("[生成三码合一订单流水] file名称=" + fileName);

			File file = new File(Constants.getParam("alipay_downloadBill_path") + fileName);
			logger.debug("[生成三码合一订单流水] file路径=" + file.getAbsolutePath());

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			fos = new FileOutputStream(file, true);
			BufferedOutputStream bos = new BufferedOutputStream(fos, 1 * 1024 * 1024);
			OutputStreamWriter osw = new OutputStreamWriter(bos, "GBK");
			buffer = new BufferedWriter(osw);

			Map<String, String> queryMap = new HashMap<String, String>();
			queryMap.put("channel", StringConstans.CHANNEL.CHANNEL_SELF);
			queryMap.put("txnDt", billDate);

			logger.debug("[生成三码合一订单流水] 查询当日流水总数 	开始");

			int totalOrder = orderService.queryOrderNumber(queryMap);

			logger.debug("[生成三码合一订单流水] 查询当日流水总数 	结束");

			logger.debug("[生成三码合一订单流水] 当日流水totalOrder=" + totalOrder);

			PageVariable page = new PageVariable();
			page.setRecordPerPage(recordPerPage);

			int pages = totalOrder % recordPerPage == 0 ? totalOrder / recordPerPage : totalOrder / recordPerPage + 1;
			logger.debug("[生成三码合一订单流水] 查询到当日流水pages=" + pages);

			for (int i = 1; i <= pages; i++) {

				page.setCurrentPage(i);

				logger.debug("[生成三码合一订单流水] 调用查询订单信息接口 	开始");

				OutputParam queryOut = orderService.queryOrderByPage(queryMap, page);

				logger.debug("[生成三码合一订单流水] 调用查询订单信息接口 	结束");

				List<Map<String, Object>> threeCodeOrder = queryOut.takeReturnList();

				logger.debug("[生成三码合一订单流水] 调用写文件方法 	开始");

				this.writeOrderMsgIntoFile(threeCodeOrder, buffer);

				logger.debug("[生成三码合一订单流水]  调用写文件方法	 结束");
			}

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "生成三码合一订单流水成功");

		} catch (Exception e) {
			logger.error("[生成三码合一订单流水] 生成三码合一订单流水:" + e.getMessage(),e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "生成三码合一订单流水信息异常");
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("[生成三码合一订单流水] 关闭流异常:" + e.getMessage(),e);
			}
			
			logger.info("生成三码合一订单流水流程END,返回报文:"+outputParam.toString());
		}


		return outputParam;

	}

	/**
	 * 查询三码合一微信和支付宝订单状态未知信息
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam queryWxAndAlipayUnknowOrder(InputParam input) throws FrameException { 
		logger.info("查询三码合一未知订单信息处理流程 START,请求报文:"+input.toString());

		OutputParam outputParam = new OutputParam();

		try {

			String payAccessType = ObjectUtils.toString(input.getValue("payAccessType"));
			String orderDate = ObjectUtils.toString(input.getValue("orderDate"));
			String txnTmStart = ObjectUtils.toString(input.getValue("txnTmStart"));
			String txnTmEnd = ObjectUtils.toString(input.getValue("txnTmEnd"));

			if(!StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)
					&& !StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
				logger.debug("请输入正确的接入类型(02:微信,03:支付宝)");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请输入正确的接入类型(02:微信,03:支付宝)");
				return outputParam;
			}
			if(!orderDate.matches("\\d{8}")){
				logger.debug("请输入正确的查询日期");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请输入正确的查询日期");
				return outputParam;
			}
			if(!txnTmStart.matches("\\d{6}")){
				logger.debug("请输入正确的查询起点时间");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请输入正确的查询起点时间");
				return outputParam;
			}
			if(!txnTmEnd.matches("\\d{6}")){
				logger.debug("请输入正确的查询结束时间");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "请输入正确的查询结束时间");
				return outputParam;
			}
			
			Map<String, String> queryMap = new HashMap<String, String>();

			queryMap.put("wxAccess", payAccessType);
			// 订单状态01、06
			queryMap.put("txnStaOne", StringConstans.OrderState.STATE_01);
			queryMap.put("txnStaSix", StringConstans.OrderState.STATE_06);
			// 消费
			queryMap.put("txnType", StringConstans.TransType.TRANS_CONSUME);
			// 三码合一渠道
			queryMap.put("channel", StringConstans.CHANNEL.CHANNEL_SELF);
			
			queryMap.put("txnDt", orderDate);
			queryMap.put("txnTmStart", txnTmStart);
			queryMap.put("txnTmEnd", txnTmEnd);

			logger.debug("[查询三码合一未知订单信息]  调用01,06订单查询订单接口   开始");
			OutputParam queryOrderOut = timingTaskService.timingQueryIndefiniteOrderWxOrAli(queryMap);
			logger.debug("[查询三码合一未知订单信息]  调用01,06订单查询订单接口   结束,返回报文:"+queryOrderOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOut.getReturnCode())) {
				logger.debug("[查询三码合一未知订单信息]调用01,06订单查询订单接口:"+queryOrderOut.getReturnMsg());
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", queryOrderOut.getReturnMsg());
				return outputParam;
			}
			
			List<Map<String, Object>> indefiniteOrderList = queryOrderOut.takeReturnList();
			if (indefiniteOrderList != null && indefiniteOrderList.size() != 0 && indefiniteOrderList.get(0) != null) {
				InputParam queryInput = new InputParam();
				for (Map<String, Object> order : indefiniteOrderList) {
					queryInput.putMap(order);
					if(StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)){
						this.handlerQueryAlipayStatus(queryInput);
					} else if(StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)){
						this.handlerQueryWxStatus(queryInput);
					}
				}
			}

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "查询三码合一订单信息成功");

			

		} catch (Exception e) {
			logger.error("[查询三码合一未知订单信息] 查询三码合一未知订单信息出现异常:" + e.getMessage(),e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "查询三码合一未知订单信息异常");
		} finally {
			logger.info("查询三码合一未知订单信息处理流程 END,返回报文:"+outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 生成三码合一微信和支付宝对账单
	 * 
	 * @param input
	 * @return
	 * @throws FrameException
	 */
	@Override
	public OutputParam creatWxAndAlipayBill0(InputParam input) throws FrameException {

		logger.info("生成三码合一微信和支付宝对账单START,请求报文:"+input.toString());

		OutputParam outputParam = new OutputParam();

		try {
			String orderDate = ObjectUtils.toString(input.getValue("orderDate"));
			logger.debug("[生成三码合一对账单] 生成三码合一对账单时间orderDate = " + orderDate);

			if (StringUtil.isEmpty(orderDate)) {
				logger.debug("[生成三码合一对账单] 对账单时间orderDate不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "生成三码合一对账单时间为不能为空");
				return outputParam;
			}

			if (recordPerPage <= 0) {
				logger.debug("[生成三码合一对账单] 设置的分页数量非法");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "设置的分页数量非法,请检查相关配置");
				return outputParam;
			}

			Map<String, String> queryMap = new HashMap<String, String>();
			queryMap.put("channel", StringConstans.CHANNEL.CHANNEL_SELF);
			queryMap.put("txnType", StringConstans.TransType.TRANS_CONSUME);
			queryMap.put("txnSta", StringConstans.OrderState.STATE_02);
			queryMap.put("wxAccess", StringConstans.PAYACCESSTYPE.ACCESS_WX);
			queryMap.put("alipayAccess", StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY);
			queryMap.put("startSettleDate", orderDate + START_SUFFIX);
			queryMap.put("endSettleDate", orderDate + END_SUFFIX);

			logger.debug("[生成三码合一对账单]	查询三码合一微信和支付宝总订单数开始");

			int totalBillNumber = timingTaskService.queryThreeCodeOrderNumber(queryMap);

			logger.debug("[生成三码合一对账单]	查询三码合一微信和支付宝总订单数结束");

			logger.debug("[生成三码合一对账单]	微信和支付宝总订单数totalBillNumber=" + totalBillNumber);

			PageVariable page = new PageVariable();
			page.setRecordPerPage(recordPerPage);

			int pages = totalBillNumber / recordPerPage;
			pages = (totalBillNumber % recordPerPage == 0) ? pages :  (pages + 1);

			logger.debug("[生成三码合一对账单]	分批查询总批次pages=" + pages);

			Dispatcher dispatcher = new Dispatcher();
			dispatcher.addWxAndAlipay(orderDate);

			if(pages ==0){
				dispatcher.createEmptyFile();
			}
			for (int i = 1; i <= pages; i++) {

				page.setCurrentPage(i);

				logger.debug("[生成三码合一对账单]	查询三码合一支付宝和微信订单信息		开始");

				OutputParam queryOrderOut = timingTaskService.timingQueryThreeCodeBills(queryMap, page);

				logger.debug("[生成三码合一对账单]	查询三码合一支付宝和微信订单信息		结束");

				if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOut.getReturnCode())) {
					logger.debug("[生成三码合一对账单] 查询三码合一支付宝和微信订单失败:" + queryOrderOut.getReturnMsg());
					outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue("respDesc", "查询三码合一支付宝和微信订单失败");
					return outputParam;
				}

				List<Map<String, Object>> orderList = queryOrderOut.takeReturnList();

				if (orderList != null && !orderList.isEmpty()) {

					for (Map<String, Object> order : orderList) {

						dispatcher.record(order);
					}

				}
			}
			dispatcher.doneWork();
			
			logger.debug("[生成三码合一对账单] 结束时间:" + DateUtil.getCurrentDateTime());

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "生成三码合一微信和支付宝对账单成功");

		} catch (Exception e) {
			logger.error("[生成三码合一对账单] 生成账单出现异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "生成三码合一对账单异常");
		} finally {
			logger.info("生成三码合一对账单处理流程    END,返回报文:"+outputParam.toString());
		}

		return outputParam;

	}

	/**
	 * @param queryInput
	 *            执行支付宝状态查询
	 */
	private void handlerQueryAlipayStatus(InputParam queryInput) {

		logger.info("执行支付宝01,06状态查询流程    START,请求报文:"+queryInput.toString());

		try {

			String txnDt = String.format("%s", queryInput.getValue("txnDt"));
			String txnTm = String.format("%s", queryInput.getValue("txnTm"));
			String txnSeqId = String.format("%s", queryInput.getValue("txnSeqId"));
			String merId = String.format("%s", queryInput.getValue("merId"));

			String outTradeNo = String.format("%s%s%s", txnSeqId, txnDt, txnTm);
//			logger.info("[查询支付宝(01,06)订单状态] outTradeNo=" + outTradeNo);

			queryInput.putParams("outTradeNo", outTradeNo);
			queryInput.putParams("merId", merId);

			logger.debug("[查询支付宝(01,06)订单状态] 开始调用支付宝查询接口查询  ");
			OutputParam queryOut = aliPayPayService.queryALipayOrder(queryInput);
			logger.debug("[查询支付宝(01,06)订单状态] 完成调用支付宝查询接口查询,返回报文:"+queryOut.toString());

			// 订单状态
			String orderState = String.format("%s", queryOut.getValue("orderSta"));
//			logger.info("[查询支付宝(01,06)订单状态] orderState=" + orderState);

			// 订单状态描述
			String resDesc = String.format("%s", queryOut.getValue("orderDesc"));
//			logger.info("[查询支付宝(01,06)订单状态] resDesc=" + resDesc);

			// 支付宝订单号
			String alipayTradeNo = String.format("%s", queryOut.getValue("alipayTradeNo"));
//			logger.info("[查询支付宝(01,06)订单状态] alipayTradeNo=" + alipayTradeNo);

			// 支付宝支付时间
			String alipayPayTime = String.format("%s", queryOut.getValue("settleDate"));
//			logger.info("[查询支付宝(01,06)订单状态] alipayPayTime=" + alipayPayTime);

			// 实收金额
			String receiptAmount = String.format("%s", queryOut.getValue("receiptAmount"));
//			logger.info("[查询支付宝(01,06)订单状态] receiptAmount=" + receiptAmount);
			
			//买家支付宝Id
			String buyerLogonId = String.format("%s",queryOut.getValue("buyerLogonId"));
			
			//付款方式
			String bankType = "";
			if (!StringUtil.isEmpty(queryOut.getValue("fundBillList"))
					&& ((List)queryOut.getValue("fundBillList")).size() > 0) {
				bankType = String.format("%s",((TradeFundBill)((List)queryOut.getValue("fundBillList")).get(0)).getFundChannel());
			}

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[查询支付宝(01,06)订单状态] 查询支付宝订单状态失败:" + queryOut.getReturnMsg());
				return;
			}

			/* modified by ghl 20170815 查询支付宝返回订单状态，如果还是‘01’和‘06’，则无需更新表，提高效率 */
			if (!StringConstans.OrderState.STATE_10.equals(orderState) && !StringUtil.isEmpty(orderState)
					&& !StringConstans.OrderState.STATE_01.equals(orderState)
					&& !StringConstans.OrderState.STATE_06.equals(orderState)) {

				InputParam updateInput = new InputParam();
				updateInput.putparamString("txnSeqId", txnSeqId);
				updateInput.putparamString("txnDt", txnDt);
				updateInput.putparamString("txnTm", txnTm);
				updateInput.putparamString("payerid", buyerLogonId);
				updateInput.putparamString("bankType", bankType);
				updateInput.putparamString("txnSta", orderState);
				updateInput.putparamString("resDesc", resDesc);

				if (!StringUtil.isEmpty(alipayTradeNo)) {
					updateInput.putparamString("alipayTradeNo", alipayTradeNo);
				}

				if (!StringUtil.isEmpty(alipayPayTime)) {
					updateInput.putparamString("settleDate", alipayPayTime);
				}

				if (!StringUtil.isEmpty(receiptAmount)) {
					updateInput.putparamString("receiptAmount", receiptAmount);
				}

				logger.debug("[查询支付宝(01,06)订单状态]  更新支付宝订单状态  开始");

				OutputParam updateOut = orderService.updateOrder(updateInput);

				logger.debug("[查询支付宝(01,06)订单状态]  更新支付宝订单状态  结束");

				if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
					logger.debug("[查询支付宝(01,06)订单状态] 更新支付宝订单状态失败:" + updateOut.getReturnMsg());
				}
			}

		} catch (FrameException e) {
			logger.error("[查询支付宝(01,06)订单状态] 出现异常:" + e.getMessage(), e);
		}
		logger.info("执行支付宝01,06状态查询流程 END");
		

	}

	/**
	 * @param queryInput
	 *            执行微信状态查询
	 */
	private void handlerQueryWxStatus(InputParam queryInput) {

		logger.debug("-------------- 执行微信01,06状态查询流程    START -----------");

		try {

			// 微信子商户号
			String subMchId = String.format("%s", queryInput.getValue("subWxMerId"));
			//商户号
			String merId = String.format("%s", queryInput.getValue("merId"));

			// 送给微信的商户流水号
			String txnSeqId = String.format("%s", queryInput.getValue("txnSeqId"));
			logger.debug("[查询微信(01,06)订单状态] txnSeqId=" + txnSeqId);

			String txnDt = String.format("%s", queryInput.getValue("txnDt"));
			String txnTm = String.format("%s", queryInput.getValue("txnTm"));
			String txnChannel = String.format("%s", queryInput.getValue("txnChannel"));

			queryInput.putParams("channel", txnChannel);
			queryInput.putParams("subMchId", subMchId);
			queryInput.putParams("merId", merId);

			logger.debug("[查询微信(01,06)订单状态] 开始调用微信查询接口查询  ");

			OutputParam queryOut = wxPayService.queryWxOrder(queryInput);

			logger.debug("[查询微信(01,06)订单状态] 开始调用微信查询接口查询  ");

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[查询微信(01,06)订单状态] 查询微信订单状态失败:" + queryOut.getReturnMsg());
				return;
			}

			// 交易状态
			String tradeSta = ObjectUtils.toString(queryOut.getValue("txnSta"));
//			logger.info("[查询微信(01,06)订单状态] tradeSta=" + tradeSta);

			// 交易状态描述
			String resDesc = ObjectUtils.toString(queryOut.getValue("resDesc"));
//			logger.info("[查询微信(01,06)订单状态] resDesc=" + resDesc);

			// 微信支付时间
			String wxPayTime = ObjectUtils.toString(queryOut.getValue("settleDate"));
//			logger.info("[查询微信(01,06)订单状态] wxPayTime=" + wxPayTime);

			// 微信订单号
			String wxOrderNo = ObjectUtils.toString(queryOut.getValue("wxOrderNo"));
//			logger.info("[查询微信(01,06)订单状态] wxOrderNo=" + wxOrderNo);
			
			// 用户标识
			String payerid = ObjectUtils.toString(queryOut.getValue("openid"));
			
			// 付款银行
			String bankType = ObjectUtils.toString(queryOut.getValue("bankType"));

			/* modified by ghl 20170815 查询微信返回订单状态，如果还是‘01’和‘06’，则无需更新表，提高效率 */
			if (!StringUtil.isEmpty(tradeSta) && !StringConstans.OrderState.STATE_01.equals(tradeSta)
					&& !StringConstans.OrderState.STATE_06.equals(tradeSta)) {

				// 更新微信订单信息
				InputParam updateInput = new InputParam();
				updateInput.putParams("txnSta", tradeSta);
				updateInput.putParams("resDesc", resDesc);
				updateInput.putParams("txnSeqId", txnSeqId);
				updateInput.putParams("txnDt", txnDt);
				updateInput.putParams("txnTm", txnTm);

				if (!StringUtil.isEmpty(wxPayTime)) {
					updateInput.putParams("settleDate", wxPayTime);
				}

				if (!StringUtil.isEmpty(wxOrderNo)) {
					updateInput.putParams("wxOrderNo", wxOrderNo);
				}

				if (!StringUtil.isEmpty(payerid)) {
					updateInput.putParams("payerid", payerid);
				}
				
				if (!StringUtil.isEmpty(bankType)) {
					updateInput.putParams("bankType", bankType);
				}
				
				logger.debug("[查询微信(01,06)订单状态]  更新微信订单状态  开始");

				OutputParam updateOut = orderService.updateWxOrderInfo(updateInput);

				logger.debug("[查询微信(01,06)订单状态]  更新微信订单状态  结束");

				if (!StringConstans.returnCode.SUCCESS.equals(updateOut.getReturnCode())) {
					logger.debug("[查询微信(01,06)订单状态] 更新微信订单状态失败:" + updateOut.getReturnMsg());
				}
			}

		} catch (FrameException e) {
			logger.error("[查询微信(01,06)订单状态] 出现异常:" + e.getMessage(),e);
		}

		logger.debug("----------------- 执行微信01,06状态查询流程    END ---------------");
	}

	/**
	 * 向核心发送T+0入账交易
	 */
	@Override
	public OutputParam accountedTxnToCore(InputParam input) throws FrameException {
		logger.info("[T+0核心入账交易] 方法开始 请求信息" + input.toString());
		OutputParam outputParam = new OutputParam();
		
		try {
			String orderDate = ObjectUtils.toString(input.getValue("orderDate"));
			String txnTmStart = ObjectUtils.toString(input.getValue("txnTmStart"));
			String txnTmEnd = ObjectUtils.toString(input.getValue("txnTmEnd"));
			
			logger.debug("[T+0核心入账交易] 查询T+0入账交易时间orderDate = " + orderDate);

			if (StringUtil.isEmpty(orderDate)) {
				logger.debug("[T+0核心入账交易] T+0入账交易时间orderDate不能为空");
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", "T+0入账交易时间不能为空");
				return outputParam;
			}

			Map<String, String> queryMap = new HashMap<String, String>();
			
			queryMap.put("txnDt", orderDate);
			queryMap.put("channel", StringConstans.CHANNEL.CHANNEL_SELF);
			queryMap.put("txnType", StringConstans.TransType.TRANS_CONSUME);
			queryMap.put("txnSta", StringConstans.OrderState.STATE_02);
			queryMap.put("wxAccess", StringConstans.PAYACCESSTYPE.ACCESS_WX);
			queryMap.put("alipayAccess", StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY);
			queryMap.put("accountedFlag", StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN);
			queryMap.put("settleMethod", StringConstans.SettleMethod.SETTLEMETHOD0);
			queryMap.put("startSettleDate", orderDate + START_SUFFIX);
			queryMap.put("endSettleDate", orderDate + END_SUFFIX);
			queryMap.put("txnTmStart", txnTmStart);
			queryMap.put("txnTmEnd", txnTmEnd);

			logger.debug("[T+0核心入账交易]查询三码合一交易成功但入账标识为未知交易明细,请求信息" + queryMap.toString());
			OutputParam queryOrderOut = orderService.queryUnknowSettleOrders(queryMap);
			logger.debug("[T+0核心入账交易]查询三码合一交易成功但入账标识为未知交易明细,结束,返回信息:"+queryOrderOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOrderOut.getReturnCode())) {
				logger.debug("T+0核心入账交易] 查询三码合一交易成功但入账标识为未知交易明细:" + queryOrderOut.getReturnMsg());
				outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue("respDesc", queryOrderOut.getReturnMsg());
				return outputParam;
			}

			List<Map<String, Object>> orderList = queryOrderOut.takeReturnList();

			if (orderList != null && !orderList.isEmpty() && orderList.get(0) != null) {
				InputParam coreInput = new InputParam();
				for (Map<String, Object> order : orderList) {
					String payAccessType = order.get("payAccessType").toString();
					String txnSta = order.get("txnSta").toString();
					coreInput.putParams("txnSta", txnSta);
					if (StringConstans.PAYACCESSTYPE.ACCESS_WX.equals(payAccessType)) {
						coreInput.putParams("wxOrder", order);
						wxPayService.toCoreForSettle(coreInput);
					} else if (StringConstans.PAYACCESSTYPE.ACCESS_ALIPAY.equals(payAccessType)) {
						coreInput.putParams("alOrder", order);
						aliPayPayService.toCoreForSettleHandler(coreInput);
					}
				}
			}
			

			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", "向核心发送入账交易成功");

		} catch (Exception e) {
			logger.error("[T+0核心入账交易] 入账出现异常:" + e.getMessage(), e);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue("respDesc", "入账异常");
			
		} finally {
			logger.info("[T+0核心入账交易] 方法结束 返回信息" + outputParam.toString());
		}
		
		
		return outputParam;
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

	/**
	 * 订单流水写入文件
	 * 
	 * @param orderList
	 * @param buffer
	 */
	private void writeOrderMsgIntoFile(List<Map<String, Object>> orderList, BufferedWriter buffer) {

		logger.info("创建三码合一订单文件写入流程  START");

		StringBuffer sb = new StringBuffer();

		List<String> orderMessage = new ArrayList<String>();

		try {

			for (Map<String, Object> order : orderList) {

				String txnSeqId = String.format("%s", order.get("txnSeqId"));
				String txnDt = String.format("%s", order.get("txnDt"));
				String txnTm = String.format("%s", order.get("txnTm"));
				String ewmData = String.format("%s", order.get("ewmData"));
				String merOrderId = String.format("%s", order.get("merOrderId"));
				String merOrDt = String.format("%s", order.get("merOrDt"));
				String merOrTm = String.format("%s", order.get("merOrTm"));
				String oglOrdId = String.format("%s", order.get("oglOrdId"));
				String oglOrdDate = String.format("%s", order.get("oglOrdDate"));
				String mobileOrderId = String.format("%s", order.get("mobileOrderId"));
				String mobileOrDt = String.format("%s", order.get("mobileOrDt"));
				String mobileOrTm = String.format("%s", order.get("mobileOrTm"));
				String txnType = String.format("%s", order.get("txnType"));
				String txnChannel = String.format("%s", order.get("txnChannel"));
				String payAccessType = String.format("%s", order.get("payAccessType"));
				String payType = String.format("%s", order.get("payType"));
				String tradeMoney = String.format("%s", order.get("tradeMoney"));
				String currencyCode = String.format("%s", order.get("currencyCode"));
				String merId = String.format("%s", order.get("merId"));
				String wxOrderNo = String.format("%s", order.get("wxOrderNo"));
				String codeUrl = String.format("%s", order.get("codeUrl"));
				String wxPrepayId = String.format("%s", order.get("wxPrepayId"));
				String randomStr = String.format("%s", order.get("randomStr"));
				String wxMerId = String.format("%s", order.get("wxMerId"));
				String subWxMerId = String.format("%s", order.get("subWxMerId"));
				String txnSta = String.format("%s", order.get("txnSta"));
				String resDesc = String.format("%s", order.get("resDesc"));
				String settleDate = String.format("%s", order.get("settleDate"));
				String discountableAmount = String.format("%s", order.get("discountableAmount"));
				String unDiscountableAmount = String.format("%s", order.get("unDiscountableAmount"));
				String alipayTradeNo = String.format("%s", order.get("alipayTradeNo"));
				String receiptAmount = String.format("%s", order.get("receiptAmount"));
				String outRequestNo = String.format("%s", order.get("outRequestNo"));
				String refundAmount = String.format("%s", order.get("refundAmount"));
				String alipayRefundFee = String.format("%s", order.get("alipayRefundFee"));
				String totalRefundFee = String.format("%s", order.get("totalRefundFee"));
				String sendBackFee = String.format("%s", order.get("sendBackFee"));
				String subAlipayMerId = String.format("%s", order.get("subAlipayMerId"));
				String alipayMerId = String.format("%s", order.get("alipayMerId"));
				String alipayPrepayId = String.format("%s", order.get("alipayPrepayId"));

				orderMessage.add(txnSeqId);
				orderMessage.add(txnDt);
				orderMessage.add(txnTm);
				orderMessage.add(ewmData);
				orderMessage.add(merOrderId);
				orderMessage.add(merOrDt);
				orderMessage.add(merOrTm);
				orderMessage.add(oglOrdId);
				orderMessage.add(oglOrdDate);
				orderMessage.add(mobileOrderId);
				orderMessage.add(mobileOrDt);
				orderMessage.add(mobileOrTm);
				orderMessage.add(txnType);
				orderMessage.add(txnChannel);
				orderMessage.add(payAccessType);
				orderMessage.add(payType);
				orderMessage.add(tradeMoney);
				orderMessage.add(currencyCode);
				orderMessage.add(merId);
				orderMessage.add(wxOrderNo);
				orderMessage.add(codeUrl);
				orderMessage.add(wxPrepayId);
				orderMessage.add(randomStr);
				orderMessage.add(wxMerId);
				orderMessage.add(subWxMerId);
				orderMessage.add(txnSta);
				orderMessage.add(resDesc);
				orderMessage.add(settleDate);
				orderMessage.add(discountableAmount);
				orderMessage.add(unDiscountableAmount);
				orderMessage.add(alipayTradeNo);
				orderMessage.add(receiptAmount);
				orderMessage.add(outRequestNo);
				orderMessage.add(refundAmount);
				orderMessage.add(alipayRefundFee);
				orderMessage.add(totalRefundFee);
				orderMessage.add(sendBackFee);
				orderMessage.add(subAlipayMerId);
				orderMessage.add(alipayMerId);
				orderMessage.add(alipayPrepayId);

				for (int i = 0; i < orderMessage.size(); i++) {

					if (StringUtil.isEmpty(orderMessage.get(i))) {
						sb.append(",");
						continue;
					}

					sb.append("\"").append(orderMessage.get(i)).append("\"").append(",");
				}

				sb.deleteCharAt(sb.length() - 1);
				sb.append("\n");

				buffer.write(sb.toString());
				buffer.flush();

				orderMessage.clear();
				sb.delete(0, sb.length());

			}

		} catch (IOException e) {
			logger.error("[创建三码合一订单文件写入] 三码合一流水文件写出现异常:" + e.getMessage(), e);
		}
		logger.debug("创建三码合一订单文件写入流程  END");
	}
	
	
	public OutputParam queryThreeCodeStatement(InputParam input)
			throws IOException {

		logger.info("查询三码合一流水流程[START],请求报文:"+input.toString());

		String startDate = ObjectUtils.toString(input.getValue("startDate"));
		String endDate = ObjectUtils.toString(input.getValue("endDate"));
		String merId = ObjectUtils.toString(input.getValue("merId"));
		String payAccessType = ObjectUtils.toString(input.getValue("payAccessType"));
		String step = ObjectUtils.toString(input.getValue("step"));
		String page = ObjectUtils.toString(input.getValue("page"));
		String acctNo = ObjectUtils.toString(input.getValue("acctNo"));
		String txnSta = ObjectUtils.toString(input.getValue("txnSta"));
		String txnDetailFlag = ObjectUtils.toString(input.getValue("txnDetailFlag"));
		String txnChannel = ObjectUtils.toString(input.getValue("txnChannel"));

		OutputParam out = new OutputParam();
		try {

			logger.debug("[查询三码合一流水] startDate=" + startDate);
			if (StringUtil.isEmpty(startDate)) {
				logger.debug("[查询三码合一流水] [startDate]元素不能为空");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[startDate]元素不能为空");
				return out;
			}

			logger.debug("[查询三码合一流水] endDate=" + endDate);
			if (StringUtil.isEmpty(endDate)) {
				logger.debug("[查询三码合一流水] [endDate]元素不能为空");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[endDate]元素不能为空");
				return out;
			}

			logger.debug("[查询三码合一流水] acctNo=" + acctNo);
			if (StringUtil.isEmpty(acctNo)) {
				logger.debug("[查询三码合一流水] [acctNo]元素不能为空");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[acctNo]元素不能为空");
				return out;
			}

			if (!startDate.matches("\\d{8}")) {
				logger.debug("[查询三码合一流水] [startDate]格式不正确");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[startDate]格式不正确");
				return out;
			}

			if (!endDate.matches("\\d{8}")) {
				logger.debug("[查询三码合一流水] [endDate]格式不正确");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[endDate]格式不正确");
				return out;
			}
			
			if (!StringConstans.CHANNEL.CHANNEL_SELF.equals(txnChannel)) {
				logger.debug("[查询三码合一流水] [txnChannel]不正确");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[txnChannel]不正确");
				return out;
			}
			
			if (!"0".equals(txnDetailFlag) && !"1".equals(txnDetailFlag)) {
				logger.debug("[查询三码合一流水] [txnDetailFlag]不正确");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg("[txnDetailFlag]不正确");
				return out;
			}

			InputParam queryThreeCodeInput = new InputParam();
			queryThreeCodeInput.putparamString("acctNo", acctNo);
			queryThreeCodeInput.putparamString("ewmStatue",StringConstans.QRCodeStatus.ENABLE);

			logger.debug("[查询三码合一流水] 根据acctNo三码合一相关信息  开始,请求报文:"+queryThreeCodeInput.toString());
			OutputParam queryOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);
			logger.debug("[查询三码合一流水] 根据acctNo三码合一相关信息  结束,返回报文:"+queryOut.toString());

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.debug("[查询三码合一流水]:"+queryOut.getReturnMsg());
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(queryOut.getReturnMsg());
				return out;
			}

			String ewmData = String.format("%s", queryOut.getValue("ewmData"));
			logger.debug("[查询三码合一流水] ewmData=" + ewmData);

			// 银行手续费率
			String feeRate = String.format("%s", queryOut.getValue("bankFeeRate"));
			if (StringUtil.isEmpty(feeRate)) {
				feeRate = DEFAULT_FEE;
			}

			InputParam queryInput = new InputParam();
			queryInput.putparamString("startDate", startDate);
			queryInput.putparamString("endDate", endDate);
			queryInput.putparamString("ewmData", ewmData);
			queryInput.putparamString("txnSta", txnSta);
			queryInput.putparamString("feeRate", feeRate);

			if (!StringUtil.isEmpty(payAccessType)) {
				queryInput.putparamString("payAccessType", payAccessType);
			}

			if (!StringUtil.isEmpty(merId)) {
				queryInput.putparamString("merId", merId);
			}

			if (!StringUtil.isEmpty(txnChannel)) {
				queryInput.putparamString("channel", txnChannel);
			}

			logger.debug("[查询三码合一流水] 查询流水汇总  开始,请求报文:"+queryInput.toString());
			List<Map<String, Object>> summary = orderService.queryThreeCodeBillDetail(queryInput, null);
			logger.debug("[查询三码合一流水] 查询流水汇总  结束");

			// 汇总信息
			ThreeCodeTxnCount threeCodeTxnCount = new ThreeCodeTxnCount("0","0", "0", "0");
			// 明细信息
			List<Map<String, Object>> threeCodeInfoList = new ArrayList<Map<String, Object>>();

			if (!StringUtil.listIsEmpty(summary)) {
				int txnCount = 0;
				BigDecimal totalMoney = new BigDecimal("0.00");
				BigDecimal totalFee = new BigDecimal("0.00");

				for (Map<String, Object> order : summary) {

					String payAccessType1 = String.format("%s", order.get("payAccessType"));
					String tradeMoney = String.format("%s", order.get("tradeMoney"));
					String bankFee = String.format("%s", order.get("bankFeeRate"));
					bankFee = StringUtil.isEmpty(bankFee) ? feeRate : bankFee;

					BigDecimal fee = new BigDecimal(bankFee).multiply(new BigDecimal(tradeMoney)).
						divide(new BigDecimal(100)).setScale(2,BigDecimal.ROUND_HALF_UP);

					if (StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType1)
							|| StringConstans.PAYACCESSTYPE.ACCESS_FSHL.equals(payAccessType1)) {
						fee = new BigDecimal("0");
					}
					totalFee = totalFee.add(fee);
					txnCount++;
					totalMoney = totalMoney.add(new BigDecimal(StringUtil.str12ToAmount(tradeMoney)));
				}

				// 总手续费
				String totalFeeStr = StringUtil.str12ToAmount(totalFee.toString());
				// 总入账金额
				String totalAcctAmt = totalMoney.subtract(new BigDecimal(totalFeeStr)).toString();

				threeCodeTxnCount.setTotalAmt(totalMoney.toString());
				threeCodeTxnCount.setTxnCount(String.valueOf(txnCount));
				threeCodeTxnCount.setTotalAcctAmt(totalAcctAmt);
				threeCodeTxnCount.setTotalFee(totalFeeStr);

				if (TXN_DETAIL_FLAG.equals(txnDetailFlag)) {

					page = StringUtil.toString(page, PAGE);
					logger.debug("[查询三码合一流水] 分页page=" + page);

					step = StringUtil.toString(step, STEP);
					logger.debug("[查询三码合一流水] 分页step=" + step);

					PageVariable pageVariable = new PageVariable();
					pageVariable.setCurrentPage(Integer.valueOf(page));
					pageVariable.setRecordPerPage(Integer.valueOf(step));

					logger.debug("[查询三码合一流水] 查询流水  开始");
					List<Map<String, Object>> queryList = orderService.queryThreeCodeBillDetail(queryInput, pageVariable);
					logger.debug("[查询三码合一流水] 查询流水  结束");

					for (Map<String, Object> m : queryList) {
						Map<String, Object> mapOrder = new HashMap<String, Object>();

						String payAccessType1 = String.format("%s", m.get("payAccessType"));
						String bankFeeRate = String.format("%s", m.get("bankFeeRate"));
						bankFeeRate = StringUtil.isEmpty(bankFeeRate) ? feeRate : bankFeeRate;
						String aBillFee = null;
						if (StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType1)
								|| StringConstans.PAYACCESSTYPE.ACCESS_FSHL.equals(payAccessType1)) {
							aBillFee = "0.00";
						} else {
							aBillFee = StringUtil.getFeeByTradeAmount(feeRate,String.format("%s", m.get("tradeMoney")));
						}

						mapOrder.put("txnSeqId", StringUtil.toString(m.get("txnSeqId")));
						mapOrder.put("txnDt", StringUtil.toString(m.get("txnDt")));
						mapOrder.put("txnTm", StringUtil.toString(m.get("txnTm")));
						mapOrder.put("merId", StringUtil.toString(m.get("merId")));
						mapOrder.put("merOrderId", StringUtil.toString(m.get("merOrderId")));
						mapOrder.put("merOrDt", StringUtil.toString(m.get("merOrDt")));
						mapOrder.put("merOrTm", StringUtil.toString(m.get("merOrTm")));
						mapOrder.put("txnSta", StringUtil.toString(m.get("txnSta")));
						mapOrder.put("resDesc", StringUtil.toString(m.get("resDesc")));
						mapOrder.put("tradeMoney", StringUtil.toString(m.get("tradeMoney")));
						mapOrder.put("settleDate", StringUtil.toString(m.get("settleDate")));
						mapOrder.put("payAccessType", StringUtil.toString(m.get("payAccessType")));
						mapOrder.put("payType", StringUtil.toString(m.get("payType")));
						mapOrder.put("remark", StringUtil.toString(m.get("remark")));
						mapOrder.put("fee", aBillFee);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("threeCodeInfo", mapOrder);
						threeCodeInfoList.add(map);
					}

				}
			}

			Map<String, Object> mapp = new HashMap<String, Object>();
			mapp.put("respCode", StringConstans.RespCode.RESP_CODE_02);
			mapp.put("respMsg", "查询成功");
			mapp.put("txnCount", threeCodeTxnCount.getTxnCount());
			mapp.put("totalAmt", threeCodeTxnCount.getTotalAmt());
			mapp.put("totalFee", threeCodeTxnCount.getTotalFee());
			mapp.put("totalAcctAmt", threeCodeTxnCount.getTotalAcctAmt());
			mapp.put("ThreeCodeInfoList", threeCodeInfoList);

			Map<String, Object> mapXml = new HashMap<String, Object>();
			mapXml.put("xml", mapp);
			StringBuffer sb = new StringBuffer();
			String returnMsg = com.huateng.utils.Util.mapToXML(mapXml, sb);
			
			out.setReturnCode(StringConstans.returnCode.SUCCESS);
			out.setReturnMsg("查询成功");
			out.putValueStr("returnMsg", returnMsg);

		} catch (Exception e) {
			logger.error("[查询三码合一流水] 查询三码合一流水流程出现异常:" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("查询三码合一流水流程出现异常");
		} finally {
			logger.info("[查询三码合一流水流程end]返回报文:"+out.toString());
		}
		return out;
	}

	public void setTimingTaskService(ITimingTaskService timingTaskService) {
		this.timingTaskService = timingTaskService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public void setAliPayPayService(AliPayPayService aliPayPayService) {
		this.aliPayPayService = aliPayPayService;
	}

	public void setWxPayService(WxPayService wxPayService) {
		this.wxPayService = wxPayService;
	}

	public IThreeCodeStaticQRCodeDataService getThreeCodeStaticQRCodeDataService() {
		return threeCodeStaticQRCodeDataService;
	}

	public void setThreeCodeStaticQRCodeDataService(
			IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService) {
		this.threeCodeStaticQRCodeDataService = threeCodeStaticQRCodeDataService;
	}

}
