package com.huateng.pay.action;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.po.threecode.QueryResult;
import com.huateng.pay.po.threecode.ThreeCodeInfo;
import com.huateng.pay.po.threecode.ThreeCodeTxnCount;
import com.huateng.pay.services.db.IOrderService;
import com.huateng.pay.services.db.IThreeCodeStaticQRCodeDataService;
import com.huateng.utils.FileUtil;
import com.wldk.framework.db.PageVariable;

@Controller
@RequestMapping(value="/query/tc/")
public class ThreeCodeAction2 /*extends BaseAction*/ {

//	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(ThreeCodeAction2.class);
	private static final String STEP = "10";
	private static final String PAGE = "1";
	private static final String TXN_DETAIL_FLAG = "1";
	private static final String DEFAULT_FEE = "0.002";

	@Autowired
	private IOrderService orderService;
	@Autowired
	private IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService;

//	private String startDate;
//	private String endDate;
//	private String merId;
//	private String payAccessType;
//	private String step;
//	private String page;
//	private String acctNo;
//	private String txnSta;
//	private String txnDetailFlag;
//	private String txnChannel;

	@RequestMapping("recvAlipayNotifyReq.mvc")
	public String queryThreeCodeStatement(HttpServletRequest request,HttpServletResponse response) throws IOException {

		logger.info("--------------------  查询三码合一流水流程   START -----------");

		QueryResult queryResult = new QueryResult();

		OutputStream out = response.getOutputStream();

		try {
			
			String startDate = request.getParameter("startDate");
			String endDate = request.getParameter("endDate");
			String acctNo = request.getParameter("acctNo");
			String txnSta = request.getParameter("txnSta");
			String payAccessType = request.getParameter("payAccessType");
			String merId = request.getParameter("merId");
			String txnChannel = request.getParameter("txnChannel");
			String txnDetailFlag = request.getParameter("txnDetailFlag");
			String page = request.getParameter("page");
			String step = request.getParameter("step");

			logger.info("[查询三码合一流水] startDate=" + startDate);
			if (StringUtil.isEmpty(startDate)) {
				logger.error("[查询三码合一流水] [startDate]元素不能为空");
				queryResult.setReturnCode(StringConstans.returnCode.FAIL);
				queryResult.setReturnMsg("[startDate]元素不能为空");
				FileUtil.writeResponse(queryResult, out);
				return null;
			}

			logger.info("[查询三码合一流水] endDate=" + endDate);
			if (StringUtil.isEmpty(endDate)) {
				logger.error("[查询三码合一流水] [endDate]元素不能为空");
				queryResult.setReturnCode(StringConstans.returnCode.FAIL);
				queryResult.setReturnMsg("[endDate]元素不能为空");
				FileUtil.writeResponse(queryResult, out);
				return null;
			}

			logger.info("[查询三码合一流水] acctNo=" + acctNo);
			if (StringUtil.isEmpty(acctNo)) {
				logger.error("[查询三码合一流水] [acctNo]元素不能为空");
				queryResult.setReturnCode(StringConstans.returnCode.FAIL);
				queryResult.setReturnMsg("[acctNo]元素不能为空");
				FileUtil.writeResponse(queryResult, out);
				return null;
			}

			if (!startDate.matches("\\d{8}")) {
				logger.error("[查询三码合一流水] [startDate]格式不正确");
				queryResult.setReturnCode(StringConstans.returnCode.FAIL);
				queryResult.setReturnMsg("[startDate]格式不正确");
				FileUtil.writeResponse(queryResult, out);
				return null;
			}

			if (!endDate.matches("\\d{8}")) {
				logger.error("[查询三码合一流水] [endDate]格式不正确");
				queryResult.setReturnCode(StringConstans.returnCode.FAIL);
				queryResult.setReturnMsg("[endDate]格式不正确");
				FileUtil.writeResponse(queryResult, out);
				return null;
			}

			InputParam queryThreeCodeInput = new InputParam();
			queryThreeCodeInput.putparamString("acctNo", acctNo);
			queryThreeCodeInput.putparamString("ewmStatue", StringConstans.QRCodeStatus.ENABLE);

			logger.info("[查询三码合一流水] 根据acctNo三码合一相关信息  开始");

			OutputParam queryOut = threeCodeStaticQRCodeDataService.queryThreeCodeStaticQRCodeInfo(queryThreeCodeInput);

			logger.info("[查询三码合一流水] 根据acctNo三码合一相关信息  结束");

			if (!StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				logger.error("[查询三码合一流水] 根据acctNo查询记录失败");
				queryResult.setReturnCode(StringConstans.returnCode.FAIL);
				queryResult.setReturnMsg("查询失败");
				FileUtil.writeResponse(queryResult, out);
				return null;
			}

			String ewmData = String.format("%s", queryOut.getValue("ewmData"));
			logger.info("[查询三码合一流水] ewmData=" + ewmData);

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

			logger.info("[查询三码合一流水] 查询流水汇总  开始");

			List<Map<String, Object>> summary = orderService.queryThreeCodeBillDetail(queryInput,null);

			logger.info("[查询三码合一流水] 查询流水汇总  结束");

			//汇总信息
			ThreeCodeTxnCount threeCodeTxnCount = new ThreeCodeTxnCount();
			//明细信息
			ArrayList<ThreeCodeInfo> resultList = new ArrayList<ThreeCodeInfo>();

			if (!StringUtil.listIsEmpty(summary)) {
				int txnCount = 0;
				BigDecimal totalMoney = new BigDecimal("0.00");
				BigDecimal totalFee = new BigDecimal("0.00");
					
				for (Map<String, Object> order : summary) {

					String payAccessType2 = String.format("%s", order.get("payAccessType"));
					String tradeMoney = String.format("%s", order.get("tradeMoney"));
					String bankFee = String.format("%s", order.get("bankFeeRate"));
					bankFee = StringUtil.isEmpty(bankFee) ? feeRate : bankFee;
						
					BigDecimal fee = new BigDecimal(bankFee).multiply(new BigDecimal(tradeMoney)).divide(
								new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);

						if (StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType2)
								|| StringConstans.PAYACCESSTYPE.ACCESS_FSHL.equals(payAccessType2)) {
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

						page = ObjectUtils.toString(page, PAGE);
						logger.info("[查询三码合一流水] 分页page=" + page);

						step = ObjectUtils.toString(step, STEP);
						logger.info("[查询三码合一流水] 分页step=" + step);

						PageVariable pageVariable = new PageVariable();
						pageVariable.setCurrentPage(Integer.valueOf(page));
						pageVariable.setRecordPerPage(Integer.valueOf(step));

						logger.info("[查询三码合一流水] 查询流水  开始");
						List<Map<String, Object>> queryList = orderService.queryThreeCodeBillDetail(queryInput,pageVariable);
						logger.info("[查询三码合一流水] 查询流水  结束");

						for (Map<String, Object> m : queryList) {

							String payAccessType3 = String.format("%s", m.get("payAccessType"));
							String bankFeeRate = String.format("%s", m.get("bankFeeRate"));
							bankFeeRate = StringUtil.isEmpty(bankFeeRate) ? feeRate : bankFeeRate;
							String aBillFee = StringUtil.getFeeByTradeAmount(feeRate, String.format("%s", m.get("tradeMoney")));

							ThreeCodeInfo codeInfo = new ThreeCodeInfo();
							codeInfo.setMerId(String.format("%s", m.get("merId")));
							codeInfo.setMerOrderId(String.format("%s", m.get("merOrderId")));
							codeInfo.setMerOrDt(String.format("%s", m.get("merOrDt")));
							codeInfo.setMerOrTm(String.format("%s", m.get("merOrTm")));
							codeInfo.setTxnSta(String.format("%s", m.get("txnSta")));
							codeInfo.setResDesc(String.format("%s", m.get("resDesc")));
							codeInfo.setPayType(String.format("%s", m.get("payType")));
							codeInfo.setTxnSeqId(String.format("%s", m.get("txnSeqId")));
							codeInfo.setTxnDt(String.format("%s", m.get("txnDt")));
							codeInfo.setTxnTm(String.format("%s", m.get("txnTm")));
							codeInfo.setTradeAmount(String.format("%s", m.get("tradeMoney")));
							codeInfo.setSettleDate(String.format("%s", m.get("settleDate")));
							codeInfo.setPayAccessType(String.format("%s", m.get("payAccessType")));
							codeInfo.setTxnChannel(String.format("%s", m.get("txnChannel")));
							codeInfo.setRemark(String.format("%s", m.get("remark")));
							codeInfo.setFee(aBillFee);
							
							if (StringConstans.PAYACCESSTYPE.ACCESS_NATIVE.equals(payAccessType3)
									||StringConstans.PAYACCESSTYPE.ACCESS_FSHL.equals(payAccessType3)) {
								codeInfo.setFee("0.00");
							}
							resultList.add(codeInfo);
						}

					}
				}

			queryResult.setReturnCode(StringConstans.returnCode.SUCCESS);
			queryResult.setReturnMsg("查询成功");
			queryResult.setThreeCodeTxnCount(threeCodeTxnCount);
			queryResult.setResultList(resultList);
			FileUtil.writeResponse(queryResult, out);

			logger.info("--------------------  查询三码合一流水流程   END -----------");

		} catch (Exception e) {
			logger.error("[查询三码合一流水] 查询三码合一流水流程出现异常:" + e.getMessage(),e);
			queryResult.setReturnCode(StringConstans.returnCode.FAIL);
			queryResult.setReturnMsg("查询三码合一流水流程出现异常");
			FileUtil.writeResponse(queryResult, out);
			logger.info("--------------------  查询三码合一流水流程 出现异常  END -----------");
		}

		return null;
	}

	public IOrderService getOrderService() {
		return orderService;
	}

	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public IThreeCodeStaticQRCodeDataService getThreeCodeStaticQRCodeDataService() {
		return threeCodeStaticQRCodeDataService;
	}

	public void setThreeCodeStaticQRCodeDataService(IThreeCodeStaticQRCodeDataService threeCodeStaticQRCodeDataService) {
		this.threeCodeStaticQRCodeDataService = threeCodeStaticQRCodeDataService;
	}

}
