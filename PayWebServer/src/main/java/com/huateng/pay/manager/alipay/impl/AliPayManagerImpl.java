package com.huateng.pay.manager.alipay.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.demo.trade.model.builder.AlipaySystemOauthTokenBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCancelRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCloseOrderRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeCreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePayRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryBillRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FCancelResult;
import com.alipay.demo.trade.model.result.AlipayF2FCloseOrderResult;
import com.alipay.demo.trade.model.result.AlipayF2FCreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FFastpayRefundQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FPayResult;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryBillResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.model.result.AlipaySystemOauthTokenResult;
import com.alipay.demo.trade.po.Submerchant;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.constants.StringConstans.AlipayErrorCode;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.manager.alipay.IAliPayManager;
import com.huateng.pay.services.alipay.YLAliPayService;
import com.huateng.pay.services.alipay.impl.AliPayPayServiceImpl;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.pay.services.db.IOrderService;
import com.wldk.framework.utils.MappingUtils;

import net.sf.json.JSONObject;

/**
 * 支付宝支付交易业务处理接口实现(往支付宝)
 * 
 * @author zhaoyuanxiang
 * 
 */
public class AliPayManagerImpl  implements IAliPayManager {
	private Logger logger = LoggerFactory.getLogger(AliPayManagerImpl.class);
	private AlipayTradeService alipayTradeService;
	private IOrderService  orderService;
	private IMerchantChannelService merchantChannelService ;
	private YLAliPayService yLAliPayService;
	/**
	 * 支付宝交易欲创建（生成支付宝二维码）
	 */
	@Override
	public OutputParam toALiPayPreCreate(InputParam input)throws FrameException {
		
		logger.info("----------------向支付宝请求下单流程   START-----------------");
		
		OutputParam outputParam = new OutputParam();
		
		try {
			
			String merId = String.format("%s", input.getValue("merId"));
			
			//订单号
			String outTradeNo = String.format("%s", input.getValue("outTradeNo"));
	
			//交易金额
			String totalAmount = String.format("%s",input.getValue("totalAmount"));
			totalAmount = StringUtil.str12ToAmount(totalAmount);

			//订单标题
			String subject = String.format("%s",input.getValue("subject"));		
			
			//商户门店编号
			String storeId = String.format("%s",input.getValue("storeId"));
			
			//终端号
			String termId = String.format("%s",input.getValue("termId"));
			
			//支付宝商户号
			String alipayMerchantId = String.format("%s",input.getValue("alipayMerchantId"));
			Submerchant subMerchant = new Submerchant();
			subMerchant.setMerchantId(alipayMerchantId);
			
			//交易有效时间
			String timeoutExpress = String.format("%s",input.getValue("timeoutExpress"));
				
			//可打折金额
			String discountableAmount = String.format("%s",input.getValue("discountableAmount"));
			discountableAmount = StringUtil.str12ToAmount(discountableAmount);
			
			//原不可打折金额
			String undiscountableAmount = String.format("%s",input.getValue("undiscountableAmount"));
			undiscountableAmount = StringUtil.str12ToAmount(undiscountableAmount);
			
			//对交易商品的描述
			String body = String.format("%s",input.getValue("body"));
			
			//操作员号
			String operatorId = String.format("%s",input.getValue("operatorId"));
			
			//后台通知地址
			String notifyUrl = String.format("%s",input.getValue("notifyUrl"));
			
			//商品详细信息
			//String goodsDetail = String.format("%s",input.getValue("goodsDetail"));
			
			//业务扩展参数
			//String extendParams = String.format("%s",input.getValue("extendParams"));
			
			//描述分账信息
			//String royaltyInfo = String.format("%s",input.getValue("royaltyInfo"));
			
			AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder();
			builder.setOutTradeNo(outTradeNo)
				   .setTotalAmount(totalAmount)
				   .setSubject(subject)
				   .setStoreId(storeId)
				   .setSubMerchant(subMerchant)
				   .setTimeoutExpress(timeoutExpress)
				   .setNotifyUrl(notifyUrl);
				
			if(!StringUtil.isEmpty(termId)){
				builder.setTerminalId(termId);
			}
			
			if(!StringUtil.isEmpty(discountableAmount)){
				builder.setDiscountableAmount(discountableAmount);
			}
			
			if(!StringUtil.isEmpty(undiscountableAmount)){
				builder.setUndiscountableAmount(undiscountableAmount);
			}
							
			if(!StringUtil.isEmpty(body)){
				builder.setBody(body);
			}
			
			if(!StringUtil.isEmpty(operatorId)){
				builder.setOperatorId(operatorId);
			}
				
			logger.info("组装支付宝主扫的报文,报文组装完成,请求支付宝主扫交易 开始");
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, alipayMerchantId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			
			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));
			
			AlipayF2FPrecreateResult result = alipayTradeService.tradePrecreate(builder);
			AlipayTradePrecreateResponse response = result.getResponse();
	
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.info("[支付宝下单] 支付宝下单成功");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);	
				outputParam.putValue("qrCode", response.getQrCode());
				break;
			case FAILED:
				logger.error("[支付宝下单] 支付宝下单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			case UNKNOWN:
				logger.info("[支付宝下单] 支付宝下单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			default:
				logger.info("[支付宝下单] 支付宝下单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			}
			
			logger.info("----------------向支付宝请求下单流程   END-----------------");
			
		} catch (Exception e) {
			logger.error("[支付宝下单] 支付宝下单出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg(" 支付宝下单出现异常");
		}
		
		return outputParam;
	}
	
	public OutputParam toALiPayPreCreateYL(InputParam input)throws FrameException {
		logger.info("支付宝断直连扫码向支付宝下单请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		
		try {
			String merId = String.format("%s", input.getValue("merId"));
			String outTradeNo = String.format("%s", input.getValue("outTradeNo"));
			String totalAmount = StringUtil.str12ToAmount(String.format("%s",input.getValue("totalAmount")));
			String subject = String.format("%s",input.getValue("subject"));		
			String storeId = String.format("%s",input.getValue("storeId"));
			String termId = String.format("%s",input.getValue("termId"));
			String alipayMerchantId = String.format("%s",input.getValue("alipayMerchantId"));
			String timeoutExpress = String.format("%s",input.getValue("timeoutExpress"));
			String discountableAmount = StringUtil.str12ToAmount(String.format("%s",input.getValue("discountableAmount")));
			String undiscountableAmount = StringUtil.str12ToAmount(String.format("%s",input.getValue("undiscountableAmount")));
			String body = String.format("%s",input.getValue("body"));
			String operatorId = String.format("%s",input.getValue("operatorId"));
			String notifyUrl = String.format("%s",input.getValue("notifyUrl"));
			//String goodsDetail = String.format("%s",input.getValue("goodsDetail"));
			//String extendParams = String.format("%s",input.getValue("extendParams"));
			//String royaltyInfo = String.format("%s",input.getValue("royaltyInfo"));
			String rateChannel = String.format("%s",input.getValue("rateChannel"));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			// 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
			data.put("out_trade_no", outTradeNo);
			/**
			 * 订单总金额 (Price).单位为元，精确到小数点后两位， 取值范围[0.01,100000000]
			 * 如果同时传入了【打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件： 【订单总金额】=【打折金额】+【不可打折金额】
			 */
			data.put("total_amount", totalAmount);
			data.put("subject", subject); // 订单标题
			Map<String, Object> SubMerchant = new HashMap<String, Object>();
			SubMerchant.put("merchant_id", alipayMerchantId);
			SubMerchant.put("merchant_type", "alipay");
			data.put("sub_merchant", SubMerchant);
//			data.put("alipay_store_id", ""); // 支付宝店铺的门店ID
//			// 非必填
//			data.put("seller_id", ""); // 卖家支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
			/**
			 * 可打折金额 (Price). 参与优惠计算的金额，单位为元，精确到小数点后两位， 取值范围[0.01,100000000]
			 * 如果该值未传入，但传入了【订单总金额】，【不可打折金额】则该值默认为【订单总金额】-【不可打折金额】
			 */
			if(!StringUtil.isEmpty(discountableAmount)){
				data.put("discountable_amount", discountableAmount);
			}
			/**
			 * 不可打折金额 (Price). 不参与优惠计算的金额，单位为元，精确到小数点后两位， 取值范围[0.01,100000000]
			 * 如果该值未传入，但传入了【订单总金额】,【打折金额】，则该值默认为【订单总金额】-【打折金额】
			 */
			if(!StringUtil.isEmpty(undiscountableAmount)){
				 data.put("undiscountable_amount", undiscountableAmount);
			}
//			data.put("buyer_logon_id", "15800923275"); // 买家支付宝账号
//			List<Object> goodsDetails = new LinkedList<Object>();
//			Map<String, Object> goodsDetail = new LinkedHashMap<String, Object>();
//			goodsDetail.put("goods_id", "apple-01"); // 商品编码
//			goodsDetail.put("goods_name", "ipad"); // 商品名称
//			goodsDetail.put("quantity", 1); // 商品数量
			// 非必填
//			goodsDetail.put("alipay_goods_id", "20010001"); // 支付宝定义的统一商品编号
//			goodsDetail.put("price", 2000); // 商品单价，单位为元
//			goodsDetail.put("goods_category", "34543238"); // 商品类目
//			goodsDetail.put("body", body); // 商品描述信息
//			goodsDetail.put("show_url", "http://www.alipay.co m/xxx.jpg"); // 商品的展示地址
//			goodsDetails.add(goodsDetail);
//			data.put("goods_detail", JSONArray.fromObject(goodsDetails)); // 订单包含的商品列表信息.Json格式.
			data.put("body", body); // 对交易或商品的描述
			data.put("operator_id", operatorId); // 商户操作员编号
			data.put("store_id", storeId); // 商户门店编号
			/**
			 * 禁用渠道，用户不可用指定渠道支付当有多个渠道时用“,”分隔注，与enable_pay_channels互斥
			 * 渠道列表：https://docs.open.alipay.c om/common/wifww7
			 */
			// data.put("disable_pay_channels", "pcredit,moneyFund,debitCardExpress");
			/**
			 * 可用渠道，用户只能在指定渠道范围内支付当有多个渠道时用“,”分隔注，与disable_pay_channels互斥
			 */
			// data.put("enable_pay_channels", "pcredit,moneyFund,debitCardExpress");
			data.put("terminal_id", termId); // 商户机具终端编号
//			Map<String, Object> extendParams = new LinkedHashMap<String, Object>();
//			extendParams.put("sys_service_provider_id", "2088511833207846");
//			// 系统商编号该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
//			extendParams.put("hb_fq_num", ""); // 使用花呗分期要进行的分期数
//			extendParams.put("hb_fq_seller_percent", ""); // 使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
//			extendParams.put("industry_reflux_info", ""); // 行业数据回流信息,
//			// 详见：地铁支付接口参数补充说明
//			extendParams.put("card_type", "S0JP0000"); // 卡类型
//			data.put("extend_params", JSONObject.fromObject(extendParams)); // 业务扩展参数
			/**
			 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
			 * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，
			 * 如1.5h，可转换为 90m。
			 */
			data.put("timeout_express", timeoutExpress);
			
			// 外部指定买家
//			Map<String, Object> extUserInfo = new LinkedHashMap<String, Object>();
//			extUserInfo.put("name", "李明"); // 姓名 注： need_check_info=T时该参数才有效
//			extUserInfo.put("mobile", "16587658765"); // 手机号注：该参数暂不校验
			/**
			 * 身份证：IDENTITY_CARD、 护照：PASSPORT、 军官证：OFFICER_CARD、 士兵证：SOLDIER_CARD、
			 * 户口本：HOKOU等。 如有其它类型需要支持，请与蚂蚁金服工作人员联系。注： need_check_info=T时该参数才有效
			 */
//			extUserInfo.put("cert_type", "IDENTITY_CARD");
//			extUserInfo.put("cert_no", "362334768769238881"); // 证件号   注：need_check_info=T时该参数才有效
			/**
			 * 允许的最小买家年龄，买家年龄必须大于等于所传数值注： 1. need_check_info=T时该参数才有效 2.
			 * min_age为整数，必须大于等于0
			 */
//			extUserInfo.put("min_age", "18");
//			extUserInfo.put("fix_buyer", "F"); // 是否强制校验付款人身份信息 T:强制校验，F：不强制
//			extUserInfo.put("need_check_info", "F"); // 是否强制校验身份信息 T:强制校验，F：不强制
//			data.put("ext_user_info", JSONObject.fromObject(extUserInfo)); // 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
			// 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
			// data.put("business_params", "{\"data\":\"123\"}"); //
			// 商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
			/**
			 * 该笔订单允许的最晚付款时间，逾期将关闭交易，从生成二维码开始计时。
			 * 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
			 * 该参数数值不接受小数点， 如1.5h，可转换为 90m。
			 */
//			data.put("qr_code_timeout_express", "90m");
			
//			 //描述分账信息，Json格式，其它说明详见分账说明
//			 Map<String, Object> royaltyInfo = new LinkedHashMap<String,Object>();
//			 royaltyInfo.put("royalty_type", "ROYALTY"); // 分账类型卖家的分账类型，目前只支持传入ROYALTY（普通分账类型）。
//			 Map<String, Object> RoyaltyDetailInfos = new LinkedHashMap<String,Object>();
//			 RoyaltyDetailInfos.put("serial_no", 1); // 分账序列号，表示分账执行的顺序，必须为正整数
//			 /**
//			 * 接受分账金额的账户类型：userId：支付宝账号对应的支付宝唯一用户号。
//			 * bankIndex：分账到银行账户的银行编号。目前暂时只支持分账到一个银行编号。 storeId：分账到门店对应的银行卡编号。
//			 * 默认值为userId。
//			 */
//			 RoyaltyDetailInfos.put("trans_in_type", "userId");
//			 RoyaltyDetailInfos.put("batch_no", "123"); // 分账批次号分账批次号。目前需要和转入账号类型为
//			 // bankIndex配合使用。
//			 RoyaltyDetailInfos.put("out_relation_id", "20131124001"); //商户分账的外部关联号，用于关联到每一笔分账信息，商户需保证其唯一性。如果为空，该值则默认为“商户网站唯一订单号+分账序列号”
//			 RoyaltyDetailInfos.put("trans_out_type", "userId"); //要分账的账户类型目前只支持userId：支付宝账号对应的支付宝唯一用户号默认值为userId。
//			 RoyaltyDetailInfos.put("trans_out", "2088101126765726"); //如果转出账号类型为userId，本参数为要分账的支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。
//			 /**
//			 * 如果转入账号类型为userId，本参数为接受分账金额的支付宝账号对应的支付宝唯一用户号。 
//			 * 以2088开头的纯16位数字。如果转入账号类型为bankIndex，
//			 * 本参数为28位的银行编号（商户和支付宝签约时确定）。 如果转入账号类型为storeId，本参数为商户的门店ID。
//			 */
//			 RoyaltyDetailInfos.put("trans_in", "2088101126708402");
//			 RoyaltyDetailInfos.put("amount", 0.1); // 分账的金额，单位为元
//			 RoyaltyDetailInfos.put("desc", "分账测试1"); // 分账描述信息
//			 RoyaltyDetailInfos.put("amount_percentage", "100"); //分账的比例，值为20代表按20%的比例分账
//			 royaltyInfo.put("royalty_detail_infos",
//			 JSONArray.fromObject(RoyaltyDetailInfos)); //分账明细的信息，可以描述多条分账指令，json数组。
//			 data.put("royalty_info", JSONObject.fromObject(royaltyInfo));
			
			
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradePrecreate"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String returnMsg = yLAliPayService.aliSdk(data, needData); // 扫码支付
			
			JSONObject json = JSONObject.fromObject(returnMsg);
			String msg = StringUtil.toString(json.get(Dict.msg));
			String code = StringUtil.toString(json.get(Dict.code));
			String qrCode = StringUtil.toString(json.get(Dict.qr_code));
			
			int codeint = Integer.parseInt(code);
			switch (codeint) {
				case StringConstans.AL_CODE.CODE_10000:
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);	
					outputParam.putValue("qrCode", qrCode);
					break;
				default:
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg(msg);
					break;
			}
			
		} catch (Exception e) {
			logger.error("[支付宝下单] 支付宝下单出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝下单出现异常");
		} finally {
			logger.info("支付宝断直连扫码向支付宝下单返回报文:"+outputParam.toString());
		}
		
		return outputParam;
	}
	
	@Override
	public OutputParam toALiPayCreate(InputParam input) throws FrameException {
		
		logger.debug("----------------向支付宝请求下单流程   START-----------------input("+input.toString()+")");
		
		OutputParam outputParam = new OutputParam();
		
		try {
			
			//订单号
			String outTradeNo = String.format("%s", input.getValue("outTradeNo"));
	
			//交易金额
			String totalAmount = String.format("%s",input.getValue("totalAmount"));
			totalAmount = StringUtil.str12ToAmount(totalAmount);

			//订单标题
			String subject = String.format("%s",input.getValue("subject"));		
			
			//商户门店编号
			String storeId = String.format("%s",input.getValue("storeId"));
			
			//支付宝商户号
			String alipayMerchantId = String.format("%s",input.getValue("alipayMerchantId"));
			Submerchant subMerchant = new Submerchant();
			subMerchant.setMerchantId(alipayMerchantId);
			
			//交易有效时间
			String timeoutExpress = String.format("%s",input.getValue("timeoutExpress"));
				
			//可打折金额
			String discountableAmount = String.format("%s",input.getValue("discountableAmount"));
			discountableAmount = StringUtil.str12ToAmount(discountableAmount);
			
			//原不可打折金额
			String undiscountableAmount = String.format("%s",input.getValue("undiscountableAmount"));
			undiscountableAmount = StringUtil.str12ToAmount(undiscountableAmount);
			
			//对交易商品的描述
			String body = String.format("%s",input.getValue("body"));
			
			//操作员号
			String operatorId = String.format("%s",input.getValue("operatorId"));
			
			String buyerId = String.format("%s", input.getValue("buyerId"));
			
			//后台通知地址
			String notifyUrl = String.format("%s",input.getValue("notifyUrl"));
			
			//商品详细信息
			//String goodsDetail = String.format("%s",input.getValue("goodsDetail"));
			
			//业务扩展参数
			//String extendParams = String.format("%s",input.getValue("extendParams"));
			
			//描述分账信息
			//String royaltyInfo = String.format("%s",input.getValue("royaltyInfo"));
			
			AlipayTradeCreateRequestBuilder builder = new AlipayTradeCreateRequestBuilder();
			builder.setOutTradeNo(outTradeNo)
				   .setTotalAmount(totalAmount)
				   .setSubject(subject)
				   .setStoreId(storeId)
				   .setSubMerchant(subMerchant)
				   .setTimeoutExpress(timeoutExpress)
				   .setBuyerId(buyerId)
				   .setNotifyUrl(notifyUrl);
				
			if(!StringUtil.isEmpty(discountableAmount)){
				builder.setDiscountableAmount(discountableAmount);
			}
			
			if(!StringUtil.isEmpty(undiscountableAmount)){
				builder.setUndiscountableAmount(undiscountableAmount);
			}
							
			if(!StringUtil.isEmpty(body)){
				builder.setBody(body);
			}
			
			if(!StringUtil.isEmpty(operatorId)){
				builder.setOperatorId(operatorId);
			}
			
			builder.setAppid(Constants.getParam("appid"));
			builder.setGatewayUrl(Constants.getParam("open_api_domain"));
			builder.setAlipayPublicKey(Constants.getParam("alipay_public_key"));
			builder.setPrivateKey(Constants.getParam("private_key"));
				
			AlipayF2FCreateResult result = alipayTradeService.tradeCreate(builder);
			AlipayTradeCreateResponse response = result.getResponse();
	
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.info("[支付宝下单] 支付宝下单成功");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);	
				outputParam.putValue("tradeNo", response.getTradeNo());
				break;
			case FAILED:
				logger.error("[支付宝下单] 支付宝下单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			case UNKNOWN:
				logger.info("[支付宝下单] 支付宝下单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			default:
				logger.info("[支付宝下单] 支付宝下单失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			}
			
			logger.info("----------------向支付宝请求下单流程   END-----------------");
			
		} catch (Exception e) {
			logger.error("[支付宝下单] 支付宝下单出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg(" 支付宝下单出现异常");
		}
		logger.debug("----------------向支付宝请求下单流程   END-----------------outputParam("+outputParam.toString()+")");
		return outputParam;
	}
	public OutputParam toALiPayCreateYL(InputParam input) throws FrameException {
		logger.debug("[间联]向支付宝下单请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String outTradeNo = String.format("%s", input.getValue("outTradeNo"));
			String totalAmount = StringUtil.str12ToAmount(String.format("%s",input.getValue("totalAmount")));
			String subject = String.format("%s",input.getValue("subject"));		
			String storeId = String.format("%s",input.getValue("storeId"));
			String alipayMerchantId = String.format("%s",input.getValue("alipayMerchantId"));
			String timeoutExpress = String.format("%s",input.getValue("timeoutExpress"));
			String discountableAmount = StringUtil.str12ToAmount(String.format("%s",input.getValue("discountableAmount")));
			String undiscountableAmount = StringUtil.str12ToAmount(String.format("%s",input.getValue("undiscountableAmount")));
			String body = String.format("%s",input.getValue("body"));
			String operatorId = String.format("%s",input.getValue("operatorId"));
			String buyerId = String.format("%s", input.getValue("buyerId"));
//			String notifyUrl = String.format("%s",input.getValue("notifyUrl"));
			//String goodsDetail = String.format("%s",input.getValue("goodsDetail"));
			//String extendParams = String.format("%s",input.getValue("extendParams"));
			//String royaltyInfo = String.format("%s",input.getValue("royaltyInfo"));
			String rateChannel = String.format("%s",input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			// 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
			data.put("out_trade_no", outTradeNo);
			Map<String, Object> SubMerchant = new HashMap<String, Object>();
			SubMerchant.put("merchant_id", alipayMerchantId);
			// SubMerchant.put("merchant_type", "alipay");
			data.put("sub_merchant", SubMerchant);
			data.put("subject", subject); // 订单标题
			/**
			 * 订单总金额 (Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
			 * 如果同时传入【可打折金额】和【不可打折金额】，该参数可以不用传入；
			 * 如果同时传入了【可打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件： 【订单总金额】=【可打折金额】+【不可打折金额】
			 */
			data.put("total_amount", totalAmount);
			data.put("body", body); // 订单描述
			data.put("buyer_logon_id", buyerId); // 买家的支付宝唯一用户号（2088开头的16位纯数字）,和buyer_logon_id不能同时为空
			// 非必填
//			data.put("seller_id", ""); // 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
			/**
			 * 参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
			 * 如果该值未传入，但传入了【订单总金额】和【不可打折金额】，则该值默认为【订单总金额】-【不可打折金额】
			 */
			if(!StringUtil.isEmpty(discountableAmount)){
				data.put("discountable_amount", discountableAmount);
			}
			/**
			 * 不参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
			 * 如果该值未传入，但传入了【订单总金额】和【可打折金额】，则该值默认为【订单总金额】-【可打折金额】
			 */
			if(!StringUtil.isEmpty(undiscountableAmount)){
				data.put("undiscountable_amount", undiscountableAmount);
			}
//			List<Object> goodsDetails = new LinkedList<Object>();
//			Map<String, Object> goodsDetail = new LinkedHashMap<String, Object>();
//			goodsDetail.put("goods_id", "apple-01"); // 商品编码由半角的大小写字母、数字、中划线、下划线中的一种或几种组成
//			goodsDetail.put("goods_name", "ipad"); // 商品名称
//			goodsDetail.put("quantity", 1); // 商品数量
//			// 非必填
//			goodsDetail.put("alipay_goods_id", "20010001"); // 支付宝定义的统一商品编号
//			goodsDetail.put("price", 0.01); // 商品单价，单位为元
//			goodsDetail.put("goods_category", "34543238"); // 商品类目
//			goodsDetail.put("body", "特价手机"); // 商品描述信息
//			goodsDetail.put("show_url", "http://www.alipay.co m/xxx.jpg"); // 商品的展示地址
//			goodsDetails.add(goodsDetail);
//			data.put("goods_detail", JSONArray.fromObject(goodsDetails)); // 订单包含的商品列表信息.Json格式.

			data.put("operator_id", operatorId); // 商户操作员编号
			/**
			 * 可用渠道，用户只能在指定渠道范围内支付当有多个渠道时用“,”分隔注，与disable_pay_channels互斥
			 */
			// data.put("enable_pay_channels", "pcredit,moneyFund,d
			// ebitCardExpress");
			data.put("store_id", storeId); // 商户门店编号
			/**
			 * 禁用渠道，用户不可用指定渠道支付当有多个渠道时用“,”分隔注，与enable_pay_channels互斥
			 * 渠道列表：https://docs.open.alipay.c om/common/wifww7
			 */
			// data.put("disable_pay_channels", "pcredit,moneyFund,d
			// ebitCardExpress");
//			data.put("terminal_id", "NJ_T_001"); // 商户机具终端编号
//			Map<String, Object> extendParams = new LinkedHashMap<String, Object>();
//			extendParams.put("sys_service_provider_id", "2088511833207846");
//			// 系统商编号该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
//			extendParams.put("hb_fq_num", ""); // 使用花呗分期要进行的分期数
//			extendParams.put("hb_fq_seller_percent", ""); // 使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
//			extendParams.put("industry_reflux_info", ""); // 行业数据回流信息,
//			// 详见：地铁支付接口参数补充说明
//			extendParams.put("card_type", "S0JP0000"); // 卡类型
//			data.put("extend_params", JSONObject.fromObject(extendParams)); // 业务扩展参数
//			/**
//			 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
//			 * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，
//			 * 如1.5h，可转换为 90m。
//			 */
			data.put("timeout_express", timeoutExpress);
//			data.put("alipay_store_id", ""); // 支付宝店铺的门店ID
//			data.put("merchant_order_no", ""); // 商户的原始订单号
//			
//			// 外部指定买家
//			Map<String, Object> extUserInfo = new LinkedHashMap<String, Object>();
//			extUserInfo.put("name", "李明"); // 姓名 注： need_check_info=T时该参数才有效
//			extUserInfo.put("mobile", "16587658765"); // 手机号注：该参数暂不校验
//			/**
//			 * 身份证：IDENTITY_CARD、 护照：PASSPORT、 军官证：OFFICER_CARD、 士兵证：SOLDIER_CARD、
//			 * 户口本：HOKOU等。 如有其它类型需要支持，请与蚂蚁金服工作人员联系。注： need_check_info=T时该参数才有效
//			 */
//			extUserInfo.put("cert_type", "IDENTITY_CARD");
//			extUserInfo.put("cert_no", "362334768769238881"); // 证件号   注：need_check_info=T时该参数才有效
//			/**
//			 * 允许的最小买家年龄，买家年龄必须大于等于所传数值注： 1. need_check_info=T时该参数才有效 2.
//			 * min_age为整数，必须大于等于0
//			 */
//			extUserInfo.put("min_age", "18");
//			extUserInfo.put("fix_buyer", "F"); // 是否强制校验付款人身份信息 T:强制校验，F：不强制
//			extUserInfo.put("need_check_info", "F"); // 是否强制校验身份信息 T:强制校验，F：不强制
//			data.put("ext_user_info", JSONObject.fromObject(extUserInfo)); // 二级商户信息,当前只对特殊银行机构特定场景下使用此字段

//			 //描述分账信息，Json格式，其它说明详见分账说明
//			 Map<String, Object> royaltyInfo = new LinkedHashMap<String,Object>();
//			 royaltyInfo.put("royalty_type", "ROYALTY"); // 分账类型卖家的分账类型，目前只支持传入ROYALTY（普通分账类型）。
//			 Map<String, Object> RoyaltyDetailInfos = new LinkedHashMap<String,Object>();
//			 RoyaltyDetailInfos.put("serial_no", 1); // 分账序列号，表示分账执行的顺序，必须为正整数
//			 /**
//			 * 接受分账金额的账户类型：userId：支付宝账号对应的支付宝唯一用户号。
//			 * bankIndex：分账到银行账户的银行编号。目前暂时只支持分账到一个银行编号。 storeId：分账到门店对应的银行卡编号。
//			 * 默认值为userId。
//			 */
//			 RoyaltyDetailInfos.put("trans_in_type", "userId");
//			 RoyaltyDetailInfos.put("batch_no", "123"); // 分账批次号分账批次号。目前需要和转入账号类型为
//			 // bankIndex配合使用。
//			 RoyaltyDetailInfos.put("out_relation_id", "20131124001"); //商户分账的外部关联号，用于关联到每一笔分账信息，商户需保证其唯一性。如果为空，该值则默认为“商户网站唯一订单号+分账序列号”
//			 RoyaltyDetailInfos.put("trans_out_type", "userId"); //要分账的账户类型目前只支持userId：支付宝账号对应的支付宝唯一用户号默认值为userId。
//			 RoyaltyDetailInfos.put("trans_out", "2088101126765726"); //如果转出账号类型为userId，本参数为要分账的支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。
//			 /**
//			 * 如果转入账号类型为userId，本参数为接受分账金额的支付宝账号对应的支付宝唯一用户号。 
//			 * 以2088开头的纯16位数字。如果转入账号类型为bankIndex，
//			 * 本参数为28位的银行编号（商户和支付宝签约时确定）。 如果转入账号类型为storeId，本参数为商户的门店ID。
//			 */
//			 RoyaltyDetailInfos.put("trans_in", "2088101126708402");
//			 RoyaltyDetailInfos.put("amount", 0.1); // 分账的金额，单位为元
//			 RoyaltyDetailInfos.put("desc", "分账测试1"); // 分账描述信息
//			 RoyaltyDetailInfos.put("amount_percentage", "100"); //分账的比例，值为20代表按20%的比例分账
//			 royaltyInfo.put("royalty_detail_infos",
//			 JSONArray.fromObject(RoyaltyDetailInfos)); //分账明细的信息，可以描述多条分账指令，json数组。
//			 data.put("royalty_info", JSONObject.fromObject(royaltyInfo));
			
			// 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
			// data.put("business_params", ""); //
			// 商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeCreate"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String returnData = yLAliPayService.aliSdk(data, needData); // 统一下单
			
			JSONObject json = JSONObject.fromObject(returnData);
			String msg = StringUtil.toString(json.get(Dict.msg));
			String code = StringUtil.toString(json.get(Dict.code));
			String subMsg = StringUtil.toString(json.get(Dict.sub_msg));
			String tradeNo = StringUtil.toString(json.get(Dict.trade_no));
			
			int codeint = Integer.parseInt(code);
			switch (codeint) {
				case StringConstans.AL_CODE.CODE_10000:
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);	
					outputParam.setReturnMsg("支付宝统一下单成功");
					outputParam.putValue("tradeNo", tradeNo);
					break;
				default:
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg(subMsg);
					break;
			}
			
		} catch (Exception e) {
			logger.error("[间联]向支付宝下单出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝下单出现异常");
		} finally {
			logger.debug("[间联]向支付宝下单返回报文:"+outputParam.toString());
		}
		return outputParam;
	}
	/**
	 * 支付宝统一收单交易支付 （扫描设备扫描支付宝付款码，读取二维码/条形码/声波信息后通过该接口送至支付宝发起支付）
	 */
	@Override
	public OutputParam toPayALiPayOrder(InputParam input) throws FrameException {
		logger.info("[支付宝被扫请求流程]   START"+input.toString());
		OutputParam outputParam = new OutputParam();
		
		try {
			/********** 组装支付宝退款交易报文并发送请求 ********/
			String merId = ObjectUtils.toString(input.getValue("merId"));
			
			//商户订单号
			String outTradeNo = ObjectUtils.toString(input.getValue("outTradeNo"));
			
			//交易场景
			String  scene = ObjectUtils.toString(input.getValue("scene"));
			
			//支付授权码（付款码）
			String authCode = ObjectUtils.toString(input.getValue("authCode"));
			
			//订单总金额(交易金额)
			String totalAmount = ObjectUtils.toString(input.getValue("orderAmount"));
			totalAmount = StringUtil.str12ToAmount(totalAmount);
			
			//可打折金额
			String discountableAmount = ObjectUtils.toString(input.getValue("discountableAmount"));
			discountableAmount = StringUtil.str12ToAmount(discountableAmount);
			
			//不可打折金额
			String undiscountableAmount = ObjectUtils.toString(input.getValue("undiscountableAmount"));
			undiscountableAmount = StringUtil.str12ToAmount(undiscountableAmount);
			
			//订单标题
			String subject = ObjectUtils.toString(input.getValue("subject"));
			
			//订单描述
			String body = ObjectUtils.toString(input.getValue("body"));
			
			//操作员编号
			String operatorId = ObjectUtils.toString(input.getValue("operatorId"));
			
			//门店编号
			String storeId = ObjectUtils.toString(input.getValue("storeId"));
			
			//终端编号
			String termId = ObjectUtils.toString(input.getValue("termId"));
			
			//支付宝店铺编号
			String alipayStoreId = ObjectUtils.toString(input.getValue("alipayStoreId"));
			
			//允许最晚付款时间
			String timeoutExpress = ObjectUtils.toString(input.getValue("timeoutExpress"));

			//二级商户信息
			String alipayMerchantId = ObjectUtils.toString(input.getValue("alipayMerchantId"));
			Submerchant subMerchant = new Submerchant();
			subMerchant.setMerchantId(alipayMerchantId);

			AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder();
			builder.setOutTradeNo(outTradeNo)
				   .setScene(scene)
				   .setAuthCode(authCode)
				   .setSubject(subject)
				   .setTotalAmount(totalAmount)
				   .setSubject(subject)
				   .setStoreId(storeId)
				   .setSubMerchant(subMerchant)
				   .setTimeoutExpress(timeoutExpress);
			
			if(!StringUtil.isEmpty(termId)){
				builder.setTerminalId(termId);
			}
			
			if(!StringUtil.isEmpty(discountableAmount)){
				builder.setDiscountableAmount(discountableAmount);
			}
			
			if(!StringUtil.isEmpty(undiscountableAmount)){
				builder.setUndiscountableAmount(undiscountableAmount);
			}
			
			if(!StringUtil.isEmpty(body)){
				builder.setBody(body);
			}
			
			if(!StringUtil.isEmpty(operatorId)){
				builder.setOperatorId(operatorId);
			}

			if(!StringUtil.isEmpty(alipayStoreId)){
				builder.setAlipayStoreId(alipayStoreId);
			}
			
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, alipayMerchantId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
				outputParam.putValue("resDesc",outQuery.getReturnMsg());
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			
			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));
			
			
			AlipayF2FPayResult result = alipayTradeService.tradePay(builder);
			AlipayTradePayResponse response = result.getResponse();
	
			String  resDesc = StringConstans.RespDesc.RESP_DESC_02;
			
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.debug("[支付宝被扫支付] 支付宝交易支付成功");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_02);
				outputParam.putValue("resDesc", resDesc);
				this.putScanedResultInfo(outputParam, response);
				break;
			case FAILED:
				logger.debug("[支付宝被扫支付] 支付宝交易支付失败");
				resDesc = String.format("%s%s%s", StringConstans.RespDesc.RESP_DESC_03,":",response.getSubMsg());
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
				outputParam.putValue("resDesc",resDesc);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				break;
			case CLOSED:
				logger.debug("[支付宝被扫支付] 支付宝交易已关闭");
				resDesc = String.format("%s%s%s", StringConstans.RespDesc.RESP_DESC_03,":","交易已关闭");
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
				outputParam.putValue("resDesc",resDesc);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				break;
			case PAYING:
				logger.debug("[支付宝被扫支付] 支付宝交易支付,等待用户输密码");
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_04);
				outputParam.putValue("resDesc", StringConstans.RespDesc.RESP_DESC_04);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				break;
			case UNKNOWN:
				logger.debug("[支付宝被扫支付] 支付宝交易支付状态未知");
				resDesc = String.format("%s%s%s", StringConstans.RespDesc.RESP_DESC_10,":",response.getSubMsg());
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_10);
				outputParam.putValue("resDesc",resDesc);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				break;
			default:
				logger.debug("支付宝被扫支付] 不支持的交易状态");
				resDesc = String.format("%s%s%s", StringConstans.RespDesc.RESP_DESC_10,":",response.getSubMsg());
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_10);
				outputParam.putValue("resDesc",resDesc);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				break;
			}
		} catch (Exception e) {
			logger.error("[支付宝被扫支付] 支付宝被扫交易出现异常：" + e.getMessage(),e);
			outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
			outputParam.putValue("resDesc","支付宝被扫出现异常");
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝被扫交易出现异常");
		}finally {
			logger.info("[支付宝被扫请求流程]   END"+outputParam.toString());
		}
		  
		return outputParam;
	}
	
	public OutputParam toPayALiPayOrderYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝被扫下单请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String merId = ObjectUtils.toString(input.getValue(Dict.merId));
			String outTradeNo = ObjectUtils.toString(input.getValue(Dict.outTradeNo));
			String scene = ObjectUtils.toString(input.getValue(Dict.scene));
			String authCode = ObjectUtils.toString(input.getValue(Dict.authCode));
			String totalAmount = StringUtil.str12ToAmount(ObjectUtils.toString(input.getValue(Dict.orderAmount)));
			String discountableAmount = StringUtil.str12ToAmount(ObjectUtils.toString(input.getValue(Dict.discountableAmount)));
			String undiscountableAmount = StringUtil.str12ToAmount(ObjectUtils.toString(input.getValue(Dict.undiscountableAmount)));
			String subject = ObjectUtils.toString(input.getValue(Dict.subject));
			String body = ObjectUtils.toString(input.getValue(Dict.body));
			String operatorId = ObjectUtils.toString(input.getValue(Dict.operatorId));
			String storeId = ObjectUtils.toString(input.getValue(Dict.storeId));
			String termId = ObjectUtils.toString(input.getValue(Dict.termId));
			String alipayStoreId = ObjectUtils.toString(input.getValue(Dict.alipayStoreId));
			String timeoutExpress = ObjectUtils.toString(input.getValue(Dict.timeoutExpress));
			String alipayMerchantId = ObjectUtils.toString(input.getValue(Dict.alipayMerchantId));
			String rateChannel = String.format("%s",input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			// 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
			data.put("out_trade_no", outTradeNo);
			data.put("scene", scene); // 支付场景 条码支付，取值：bar_code  声波支付，取值：wave_code
			data.put("auth_code", authCode); // 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
			data.put("subject", subject); // 订单标题
			Map<String, Object> SubMerchant = new HashMap<String, Object>();
			SubMerchant.put("merchant_id", alipayMerchantId);
			SubMerchant.put("merchant_type", "alipay");
			data.put("sub_merchant", SubMerchant);
			/**
			 * 订单总金额 (Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
			 * 如果同时传入【可打折金额】和【不可打折金额】，该参数可以不用传入；
			 * 如果同时传入了【可打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件： 【订单总金额】=【可打折金额】+【不可打折金额】
			 */
			data.put("total_amount", totalAmount);
			/**
			 * 标价币种, total_amount 对应的币种单位。 支持英镑：GBP、港币：HKD、美元：USD、
			 * 新加坡元：SGD、日元：JPY、加拿大元：CAD、澳元：AUD、欧元：EUR、新西兰元：NZD、
			 * 韩元：KRW、泰铢：THB、瑞士法郎：CHF、
			 * 瑞典克朗：SEK、丹麦克朗：DKK、挪威克朗：NOK、马来西亚林吉特：MYR、印尼卢比：IDR、
			 * 菲律宾比索：PHP、毛里求斯卢比：MUR、以色列新谢克尔：ILS、斯里兰卡卢比：LKR、俄罗斯卢布：RUB、阿联酋迪拉姆：AED、捷克克朗：CZK、
			 * 南非兰特：ZAR、人民币：CNY
			 */
			data.put("trans_currency", "CNY");
			/**
			 * 商户指定的结算币种 支持英镑：GBP、港币：HKD、美元：USD、
			 * 新加坡元：SGD、日元：JPY、加拿大元：CAD、澳元：AUD、欧元：EUR、新西兰元：NZD、
			 * 韩元：KRW、泰铢：THB、瑞士法郎：CHF、
			 * 瑞典克朗：SEK、丹麦克朗：DKK、挪威克朗：NOK、马来西亚林吉特：MYR、印尼卢比：IDR、
			 * 菲律宾比索：PHP、毛里求斯卢比：MUR、以色列新谢克尔：ILS、斯里兰卡卢比：LKR、俄罗斯卢布：RUB、阿联酋迪拉姆：AED、捷克克朗：CZK、
			 * 南非兰特：ZAR、人民币：CNY
			 */
			data.put("settle_currency", "CNY");
			/**
			 * 参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
			 * 如果该值未传入，但传入了【订单总金额】和【不可打折金额】，则该值默认为【订单总金额】-【不可打折金额】
			 */
			if(!StringUtil.isEmpty(discountableAmount)){
				data.put("discountable_amount", discountableAmount);
			}
			/**
			 * 不参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
			 * 如果该值未传入，但传入了【订单总金额】和【可打折金额】，则该值默认为【订单总金额】-【可打折金额】
			 */
			if(!StringUtil.isEmpty(undiscountableAmount)){
				 data.put("undiscountable_amount", undiscountableAmount);
			}
			if(!StringUtil.isEmpty(body)){
				data.put("body", body); // 订单描述
			}
			if(!StringUtil.isEmpty(operatorId)){
				data.put("operator_id", operatorId); // 商户操作员编号
			}
			if(!StringUtil.isEmpty(storeId)){
				data.put("store_id", storeId); // 商户门店编号
			}
			if(!StringUtil.isEmpty(termId)){
				data.put("terminal_id", termId); // 商户机具终端编号
			}
			if(!StringUtil.isEmpty(alipayStoreId)){
				data.put("alipay_store_id", alipayStoreId); // 支付宝店铺的门店ID
			}
			/**
			 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
			 * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，
			 * 如1.5h，可转换为 90m。
			 */
			data.put("timeout_express", timeoutExpress);
			
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradePay"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String returnData = yLAliPayService.aliSdk(data, needData); //  条码支付
			
			InputParam deal = new InputParam();
			deal.putParams(Dict.outTradeNo, outTradeNo);
			deal.putParams(Dict.subAlipayMerId, alipayMerchantId);
			deal.putParams(Dict.merId, merId);
			deal.putParams(Dict.information, returnData);
			deal.putParams(Dict.rateChannel, rateChannel);
			OutputParam outDeal = aliMicroProcessReturnInformation(deal);
			String returnCode = StringUtil.toString(outDeal.getValue(Dict.returnCode));
			String returnMsg = StringUtil.toString(outDeal.getValue(Dict.returnMsg));
			String information = StringUtil.toString(outDeal.getValue(Dict.information));
			
			if (StringConstans.AlipayTradeStatus.TRADE_SUCCESS.equals(returnCode)
					|| StringConstans.AlipayTradeStatus.TRADE_FINISH.equals(returnCode)) {
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);	
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_02);
				outputParam.putValue("resDesc", "交易支付成功");
				this.putMicroResultInfo(outputParam,information);
			} else if (StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY.equals(returnCode)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);	
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_04);
				outputParam.putValue("resDesc", "等待用户支付中");
			} else if (StringConstans.AlipayTradeStatus.TRADE_UNKNOWN.equals(returnCode)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);	
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_10);
				outputParam.putValue("resDesc", "支付宝交易支付状态未知");
			} else if (StringConstans.AlipayTradeStatus.TRADE_CLOSED.equals(returnCode)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
				outputParam.putValue("resDesc","支付宝被扫订单已关闭");
			} else {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
				outputParam.putValue("resDesc",returnMsg);
			}
			
			
		} catch (Exception e) {
			logger.error("[间联]支付宝被扫交易出现异常：" + e.getMessage(),e);
			outputParam.putValue("txnSta", StringConstans.OrderState.STATE_03);
			outputParam.putValue("resDesc","[间联]支付宝被扫出现异常");
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("[间联]支付宝被扫出现异常");
		} finally {
			logger.info("[间联]支付宝被扫下单返回报文:"+outputParam.toString());
		}
		
		return outputParam;
	}
	
	public OutputParam aliQueryProcessReturnInformation(InputParam input) throws FrameException{
		logger.info("处理支付宝断直连查询交易start,请求报文:"+input.toString());
		String information = StringUtil.toString(input.getValue(Dict.information));
		
		OutputParam out = new OutputParam();
		JSONObject json = JSONObject.fromObject(information);
		String msg = StringUtil.toString(json.get(Dict.msg));
		String code = StringUtil.toString(json.get(Dict.code));
		String subMsg = StringUtil.toString(json.get(Dict.sub_msg));
		String tradeStatus = StringUtil.toString(json.get(Dict.trade_status));
		logger.info("支付宝查询返回tradeStatus:"+tradeStatus);
		
		int codeint = Integer.parseInt(code);
		switch (codeint) {
			case StringConstans.AL_CODE.CODE_10000:
				if(StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY.equals(tradeStatus)){
					out.putValue(Dict.returnCode,  StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY);
					out.putValue(Dict.returnMsg, "等待用户支付中");
				} else if(StringConstans.AlipayTradeStatus.TRADE_CLOSED.equals(tradeStatus)){
					out.putValue(Dict.returnCode,  StringConstans.AlipayTradeStatus.TRADE_CLOSED);
					out.putValue(Dict.returnMsg, "订单已关闭");
				} else {
					out.putValue(Dict.returnCode, StringConstans.AlipayTradeStatus.TRADE_SUCCESS);
					out.putValue(Dict.returnMsg, msg);
				}
				break;
			case StringConstans.AL_CODE.CODE_10003:
				out.putValue(Dict.returnCode,  StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY);
				out.putValue(Dict.returnMsg, "等待用户支付中");
			case StringConstans.AL_CODE.CODE_20000:
				out.putValue(Dict.returnCode, StringConstans.AlipayTradeStatus.TRADE_UNKNOWN);
				out.putValue(Dict.returnMsg, "交易状态不明");
				break;
			default:
				out.putValue(Dict.returnCode, StringConstans.AlipayTradeStatus.TRADE_FAILED);
				out.putValue(Dict.returnMsg, subMsg);
				break;
		}
		logger.info("处理支付宝断直连查询交易end,返回报文:"+out.toString());
		return out;
	}
	
	/**
	 * 处理支付宝被扫交易返回信息
	 * @param information
	 * @return
	 * @throws FrameException
	 */
	public OutputParam aliMicroProcessReturnInformation(InputParam input) throws FrameException{
		logger.info("处理支付宝断直连被扫交易start,请求报文:"+input.toString());
		String information = StringUtil.toString(input.getValue(Dict.information));
		String alipayTradeNo = StringUtil.toString(input.getValue(Dict.alipayTradeNo));
		String merId = StringUtil.toString(input.getValue(Dict.merId));
		String subAlipayMerId = StringUtil.toString(input.getValue(Dict.subAlipayMerId));
		String outTradeNo = ObjectUtils.toString(input.getValue(Dict.outTradeNo));
		String rateChannel = ObjectUtils.toString(input.getValue(Dict.rateChannel));
		
		OutputParam out = new OutputParam();
		JSONObject json = JSONObject.fromObject(information);
		String msg = StringUtil.toString(json.get(Dict.msg));
		String code = StringUtil.toString(json.get(Dict.code));
		String subMsg = StringUtil.toString(json.get(Dict.sub_msg));
		int codeint = Integer.parseInt(code);
		switch (codeint) {
			case StringConstans.AL_CODE.CODE_10000:
				out.putValue(Dict.returnCode, StringConstans.AlipayTradeStatus.TRADE_SUCCESS);
				out.putValue(Dict.returnMsg, msg);
				out.putValue(Dict.information, information);
				break;
			case StringConstans.AL_CODE.CODE_10003:
			case StringConstans.AL_CODE.CODE_20000:
				InputParam queryInput = new InputParam();
				queryInput.putParams(Dict.outTradeNo, outTradeNo);
				queryInput.putParams(Dict.alipayTradeNo, alipayTradeNo);
				queryInput.putParams(Dict.subAlipayMerId, subAlipayMerId);
				queryInput.putParams(Dict.merId, merId);
				queryInput.putParams(Dict.rateChannel, rateChannel);
				OutputParam outQuery = loopQueryResult(queryInput);
				out.putValue(Dict.returnCode, outQuery.getValue(Dict.returnCode));
				out.putValue(Dict.returnMsg, outQuery.getValue(Dict.returnMsg));
				out.putValue(Dict.information, outQuery.getValue(Dict.information));
				break;
			default:
				out.putValue(Dict.returnCode, StringConstans.AlipayTradeStatus.TRADE_FAILED);
				out.putValue(Dict.returnMsg, subMsg);
				out.putValue(Dict.information, information);
				break;
		}
		logger.info("处理支付宝断直连被扫交易end,返回报文:"+out.toString());
		return out;
	}
	
	/**
	 * 支付宝轮询
	 * @param information
	 * @return
	 * @throws FrameException
	 */
	public OutputParam loopQueryResult(InputParam input) throws FrameException{
		logger.info("支付宝断直连轮询start,请求报文:"+input.toString());
		OutputParam out = new OutputParam();
		try{
			String outTradeNo = StringUtil.toString(input.getValue(Dict.outTradeNo));
			String alipayTradeNo = StringUtil.toString(input.getValue(Dict.alipayTradeNo));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI+"_"+rateChannel;
			
			int MaxQuery = Integer.parseInt(Constants.getParam("max_query_retry"));
			Long queryDuration = Long.parseLong(Constants.getParam("query_duration"));
	        for (int i = 0; i < MaxQuery; i++) {
	        	logger.info("[支付宝轮询查询交易状态]  轮询最大次数MaxQuery=" + MaxQuery + "当前轮询次数 currentQuery=" + (i + 1));
	           
				Thread.sleep(queryDuration);
	        	
	        	logger.info("[支付宝轮询查询交易状态] 睡眠："+ queryDuration + "ms完成，开始查询");
	        	
				HashMap<String, Object> data = new HashMap<String, Object>();
				if(!StringUtil.isEmpty(outTradeNo)){
					data.put("out_trade_no", outTradeNo); // 订单支付时传入的商户订单号,和支付宝交易号不能同时为空。trade_no,out_trade_no如果同时存在优先取trade_no
				}
				if(!StringUtil.isEmpty(alipayTradeNo)){
					data.put("trade_no", alipayTradeNo); // 支付宝交易号，和商户订单号不能同时为空
				}
				
				Map<String, String> needData = new HashMap<String, String>();
				needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeQuery"));
				needData.put(Dict.cacheKey, cacheKey);
				
				String returnData = yLAliPayService.aliSdk(data, needData);//  条码支付 统一收单线下交易查询
	        	JSONObject json = JSONObject.fromObject(returnData);
				
	    		String code = StringUtil.toString(json.get(Dict.code));
	    		String msg = StringUtil.toString(json.get(Dict.msg));
	    		String tradeStatus = StringUtil.toString(json.get(Dict.trade_status));
	    		logger.info("支付宝查询返回tradeStatus:"+tradeStatus);
	    		
	    		InputParam flagParam = new InputParam();
	    		flagParam.putParams(Dict.code, code);
	    		flagParam.putParams(Dict.tradeStatus, tradeStatus);
	    		if(stopQuery(flagParam)) {
	    			out.putValue(Dict.returnCode, this.transfromCode(code,tradeStatus));
	    			out.putValue(Dict.returnMsg, msg);
	    			out.putValue(Dict.information, returnData);
	    			break;
	    		} else {
	    			if(i == MaxQuery - 1 ) {
	    				out.putValue(Dict.returnCode, this.transfromCode(code,tradeStatus));
	        			out.putValue(Dict.returnMsg, msg);
	        			out.putValue(Dict.information, returnData);
	    			}
	    		}
	        }
		} catch(Exception e) {
			logger.error("支付宝断直连轮询出现异常：" + e.getMessage(),e);
			out.putValue(Dict.returnCode, StringConstans.AlipayTradeStatus.TRADE_FAILED);
			out.putValue(Dict.returnMsg, "支付宝断直连轮询出现异常");
		} finally {
			logger.info("支付宝断直连轮询end,返回报文:"+out.toString());
		}
		return out;
		
		
	}
	
    /**
      * 判断是否停止查询
      * true:停止
      * false:继续查询
     * @param response
     * @return
     */
    protected boolean stopQuery(InputParam input) {
    	String code = StringUtil.toString(input.getValue(Dict.code));
    	String tradeStatus = StringUtil.toString(input.getValue(Dict.tradeStatus));
    	
    	boolean flag ;
		switch (Integer.parseInt(code)) {
			case StringConstans.AL_CODE.CODE_10000:
				if(StringConstans.AlipayTradeStatus.TRADE_SUCCESS.equals(tradeStatus) 
						|| StringConstans.AlipayTradeStatus.TRADE_FINISH.equals(tradeStatus)
						|| StringConstans.AlipayTradeStatus.TRADE_CLOSED.equals(tradeStatus) ) {
					flag = true;
				} else {
					flag = false;
				}
				break;
			case StringConstans.AL_CODE.CODE_20000:
				flag = false;
				break;
			default:
				flag = true;
				break;
		}
    	
		return flag;
    }
    
    protected String transfromCode(String code ,String tradeStatus) {
    	logger.info("transfromCode转换输入code:"+code+",tradeStatus:"+tradeStatus);
    	String returnStr = null ;
    	switch (Integer.parseInt(code)) {
			case StringConstans.AL_CODE.CODE_10000:
				returnStr = tradeStatus;
				break;
			case StringConstans.AL_CODE.CODE_20000:
				returnStr = StringConstans.AlipayTradeStatus.TRADE_UNKNOWN;
				break;
			default:
				returnStr = StringConstans.AlipayTradeStatus.TRADE_FAILED;
				break;
		}
    	logger.info("transfromCode转换返回:"+returnStr);
    	return returnStr;
    }
    
	

	/**
	 * 支付宝统一收单线下交易查询
	 */
	@Override
	public OutputParam toQueryALiPayOrder(InputParam input) throws FrameException {

		logger.info("向支付宝请求查询流程   START,请求报文:" + input.toString());
		
		OutputParam outputParam = new OutputParam();
		
		try {
			
			/********** 组装支付宝交易查询报文并发送请求 ********/
			
			//原外部订单号
			String outTradeNo = ObjectUtils.toString(input.getValue("outTradeNo"));
			
			//支付宝订单号
			String alipayTradeNo = ObjectUtils.toString(input.getValue("alipayTradeNo"));
			
			//商户号
			String merId = ObjectUtils.toString(input.getValue("merId"));
			
			//子商户号
			String subAlipayMerId = ObjectUtils.toString(input.getValue("subAlipayMerId"));
			
			if(StringUtil.isEmpty(outTradeNo) && StringUtil.isEmpty(alipayTradeNo)){
				logger.debug("[支付宝交易查询] 原外部订单号或者支付宝订单号不能全为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(" 原外部订单号或者支付宝订单号不能全为空");
				return outputParam;
			}
			
			logger.debug("[支付宝交易查询] 组装支付宝交易查询的报文");
			AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder();
			
			if(!StringUtil.isEmpty(outTradeNo)){
				builder.setOutTradeNo(outTradeNo);
			}
			
			if(!StringUtil.isEmpty(alipayTradeNo)){
				builder.setTradeNo(alipayTradeNo);
			}
			 
			logger.debug("组装支付宝交易查询的报文,报文组装完成,请求支付宝交易查询 开始");
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			
			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));
			
			
			AlipayF2FQueryResult queryTradeResult = alipayTradeService.queryTradeResult(builder);
			AlipayTradeQueryResponse response = queryTradeResult.getResponse();
			logger.info("查询状态TradeStatus:"+queryTradeResult.getTradeStatus());
			switch (queryTradeResult.getTradeStatus()) {
				case SUCCESS:
					logger.debug("[支付宝交易查询] 支付宝交易查询成功");
					outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
					this.setOrderState(outputParam, response.getTradeStatus());
					this.putQueryResultInfo(outputParam, response);
					break;
				case FAILED:
					logger.debug("[支付宝交易查询] 支付宝交易查询失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					this.setOrderState(outputParam, response.getTradeStatus(),response.getSubMsg(),response.getSubCode());
					this.putQueryResultInfo(outputParam, response);
					break;
				case UNKNOWN:
					logger.debug("[支付宝交易查询] 支付宝交易查询未知");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					this.setOrderState(outputParam, StringConstans.AlipayTradeStatus.TRADE_UNKNOWN,response.getSubMsg());
					break;
				default:
					logger.debug("[支付宝交易查询] 不支持的交易状态");
					this.setOrderState(outputParam, StringConstans.AlipayTradeStatus.TRADE_UNKNOWN);
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg(response.getSubMsg());
					break;
			}
			
		} catch (Exception e) {
			logger.error("[支付宝交易查询] 支付宝交易查询处理异常：" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝交易查询处理异常");
		} finally {
			logger.info("向支付宝请求查询流程成功   END,返回报文:" + outputParam.toString());
		}
		
		return outputParam;
	}
	
	public OutputParam toQueryALiPayOrderYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝查询订单请求报文:" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String outTradeNo = ObjectUtils.toString(input.getValue(Dict.outTradeNo));
			String alipayTradeNo = ObjectUtils.toString(input.getValue(Dict.alipayTradeNo));
			String rateChannel = String.format("%s",input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			
			if(StringUtil.isEmpty(outTradeNo) && StringUtil.isEmpty(alipayTradeNo)){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]支付宝查询订单:原外部订单号或者支付宝订单号不能全为空");
				return outputParam;
			}
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			if(!StringUtil.isEmpty(outTradeNo)){
				data.put(Dict.out_trade_no, outTradeNo); // 订单支付时传入的商户订单号,和支付宝交易号不能同时为空。trade_no,out_trade_no如果同时存在优先取trade_no
			} else {
				data.put(Dict.trade_no, alipayTradeNo); // 支付宝交易号，和商户订单号不能同时为空
			}
			
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeQuery"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String returnData = yLAliPayService.aliSdk(data, needData); //  条码支付 统一收单线下交易查询
			
			InputParam deal = new InputParam();
			deal.putParams(Dict.information, returnData);
			OutputParam outDeal = aliQueryProcessReturnInformation(deal);
			String returnCode = StringUtil.toString(outDeal.getValue(Dict.returnCode));
			String returnMsg = StringUtil.toString(outDeal.getValue(Dict.returnMsg));
			
			this.aliQueryOrderSetStaAndDesc(outputParam,returnCode,returnMsg);
			
			if (StringConstans.AlipayTradeStatus.TRADE_SUCCESS.equals(returnCode)
					|| StringConstans.AlipayTradeStatus.TRADE_FINISH.equals(returnCode)) {
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);	
				this.putQueryResultInfo(outputParam, returnData);
			} else {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			}
			outputParam.putValue(Dict.information, returnData);
			
		} catch (Exception e) {
			logger.error("[间联]支付宝查询订单处理异常：" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("[间联]支付宝查询订单处理异常:"+e.getMessage());
		} finally {
			logger.info("[间联]支付宝查询订单返回报文:" + outputParam.toString());
		}
		
		return outputParam;
	}
	
	
	public void aliQueryOrderSetStaAndDesc(OutputParam outputParam,String tradeStatus,String msg){
		class Inner{
			public void innerMethod(OutputParam outputParam,String orderSta,String orderDesc) {
				outputParam.putValue(Dict.orderSta, orderSta);
				outputParam.putValue(Dict.orderDesc, orderDesc);
			}
		}
		
		if (StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY.equals(tradeStatus)) {
			new Inner().innerMethod(outputParam,StringConstans.OrderState.STATE_01,"交易正在处理中,等待用户输密码");
		} else if (StringConstans.AlipayTradeStatus.TRADE_CLOSED.equals(tradeStatus)) {
			new Inner().innerMethod(outputParam,StringConstans.OrderState.STATE_09,"订单已关闭");
		} else if (StringConstans.AlipayTradeStatus.TRADE_SUCCESS.equals(tradeStatus)) {
			new Inner().innerMethod(outputParam,StringConstans.OrderState.STATE_02, "交易成功");
		} else if (StringConstans.AlipayTradeStatus.TRADE_FINISH.equals(tradeStatus)) {
			new Inner().innerMethod(outputParam,StringConstans.OrderState.STATE_11, "交易结束,不可退款");
		} else if (StringConstans.AlipayTradeStatus.TRADE_UNKNOWN.equals(tradeStatus)) {
			new Inner().innerMethod(outputParam,StringConstans.OrderState.STATE_10, "交易状态未知");
		} else {
			new Inner().innerMethod(outputParam,StringConstans.OrderState.STATE_03, msg);
		}
		
	}
	
	
	
	
	public static Map<String, String> alipayStatusToOrderStatus(String alipayStatus, String... message) {

		Map<String, String> map = new HashMap<String, String>();

		if (StringConstans.AlipayTradeStatus.WAIT_BUYER_PAY.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_01);
			map.put("orderDesc", "交易正在处理中,等待用户输密码");

		} else if (StringConstans.AlipayTradeStatus.TRADE_CLOSED.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_09);
			map.put("orderDesc", "订单已关闭");

		} else if (StringConstans.AlipayTradeStatus.TRADE_SUCCESS.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_02);
			map.put("orderDesc", "交易成功");

		} else if (StringConstans.AlipayTradeStatus.TRADE_FINISH.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_11);
			map.put("orderDesc", "交易结束,不可退款");

		} else if (StringConstans.AlipayTradeStatus.TRADE_UNKNOWN.equals(alipayStatus)) {

			map.put("orderState", StringConstans.OrderState.STATE_10);
			map.put("orderDesc", "交易状态未知");

		} else if (alipayStatus == null && AlipayErrorCode.TRADE_NOT_EXIST.equals(message[1])) {

			map.put("orderState", StringConstans.OrderState.STATE_13);
			map.put("orderDesc", "交易不存在");

		} else {

			map.put("orderState", StringConstans.OrderState.STATE_03);
			map.put("orderDesc", message[0]);
		}

		return map;
	}

	/**
	 * 支付宝统一收单交易撤销
	 */
	@Override
	public OutputParam toRevokeALiPayOrder(InputParam input) throws FrameException {
		
		logger.info("[向支付宝请求撤销订单流程]   START"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			/********** 组装支付宝支付撤销报文并发送请求 ********/
			String outTradeNo = ObjectUtils.toString(input.getValue(Dict.outTradeNo));
			String subAlipayMerId = ObjectUtils.toString(input.getValue(Dict.subAlipayMerId));
			String alipayTradeNo = ObjectUtils.toString(input.getValue(Dict.alipayTradeNo));
			String merId = ObjectUtils.toString(input.getValue(Dict.merId));
			
			if(StringUtil.isEmpty(outTradeNo) && StringUtil.isEmpty(alipayTradeNo)){
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "原外部订单号或者支付宝订单号不能全为空");
				return outputParam;
			}
			
			AlipayTradeCancelRequestBuilder builder = new AlipayTradeCancelRequestBuilder();
			if(!StringUtil.isEmpty(outTradeNo)){
				builder.setOutTradeNo(outTradeNo);
			}else{
				builder.setTradeNo(alipayTradeNo);
			}
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, outQuery.getReturnMsg());
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			
			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));
			
			AlipayF2FCancelResult  result = alipayTradeService.tradeCancel(builder);
			AlipayTradeCancelResponse response = result.getResponse();
			
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.debug("[支付宝交易查询] 支付宝交易查询成功");
				//查看返回报文中RetryFlag是否需要重发				
				if("N".equals(response.getRetryFlag())){
					outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_02);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
					outputParam.putValue(Dict.respDesc, response.getMsg());
				}else {
					outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respDesc, "撤销失败:"+ response.getSubMsg() + "请重试");
				}
				outputParam.putValue("alipayTradeNo", response.getTradeNo());
				break;
			case FAILED:
				logger.debug("[支付宝交易撤销] 交易撤销失败");
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "撤销失败:"+ response.getSubMsg());
				this.covertAlipayStatus(outputParam, response.getSubCode(), response.getSubMsg());
				break;
			case UNKNOWN:
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "撤销失败:"+ response.getSubMsg());
				this.covertAlipayStatus(outputParam, response.getSubCode(), response.getSubMsg());
				break;
			default:
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_10);
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_10);
				outputParam.putValue(Dict.respDesc, response.getSubMsg());
				break;
			}
		} catch (Exception e) {
			logger.error("支付宝支付撤销处理异常：" + e.getMessage(),e);
			outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
			outputParam.putValue(Dict.respDesc, "支付宝支付撤销处理异常"+ e.getMessage());
		}finally {
			logger.info("[向支付宝请求撤销订单流程]   END"+outputParam.toString());
		}
		return outputParam;
	}
	

	/**
	 * 支付宝断直连统一收单交易撤销
	 */
	@Override
	public OutputParam toRevokeALiPayOrderYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝撤销请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			/********** 组装支付宝支付撤销报文并发送请求 ********/
			String outTradeNo = ObjectUtils.toString(input.getValue(Dict.outTradeNo));
			String alipayTradeNo = ObjectUtils.toString(input.getValue(Dict.alipayTradeNo));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			
			if(StringUtil.isEmpty(outTradeNo) && StringUtil.isEmpty(alipayTradeNo)){
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[间联]支付宝撤销:原外部订单号或者支付宝订单号不能全为空");
				return outputParam;
			}
			
			Map<String, Object> reqData = new HashMap<String, Object>();
			if(!StringUtil.isEmpty(outTradeNo)) {
				reqData.put(Dict.out_trade_no, outTradeNo);
			}else{
				reqData.put(Dict.trade_no, alipayTradeNo);
			}
			
			//组装发送报文并向银联发送撤销请求
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.cacheKey, cacheKey);
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeCancel"));
			
			String resp = yLAliPayService.aliSdk(reqData, needData);
			
			JSONObject jsonObject = JSONObject.fromObject(resp);
			String code = StringUtil.toString(jsonObject.get(Dict.code));
			String retryFlag = StringUtil.toString(jsonObject.get(Dict.retry_flag));
			String tradeNo = StringUtil.toString(jsonObject.get(Dict.trade_no));
			String subCode = StringUtil.toString(jsonObject.get(Dict.sub_code));
			String subMsg = StringUtil.toString(jsonObject.get(Dict.sub_msg));
			
			int codeInt = Integer.parseInt(code);
			
			switch (codeInt) {
			case StringConstans.AL_CODE.CODE_10000:
				//查看返回报文中RetryFlag是否需要重发				
				if(StringConstans.RETRY_FLAG.N.equals(retryFlag)){
					outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_02);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
					outputParam.putValue(Dict.respDesc, StringConstans.RespDesc.RESP_DESC_02);
				}else {
					outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
					outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					outputParam.putValue(Dict.respDesc, "[间联]支付宝撤销失败:"+ subMsg + "请重试");
				}
				outputParam.putValue(Dict.alipayTradeNo, tradeNo);
				break;
			case StringConstans.AL_CODE.CODE_20000:
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_10);
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_10);
				outputParam.putValue(Dict.respDesc, "[间联]支付宝撤销:"+subMsg);
				break;
			default:
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
				outputParam.putValue(Dict.respDesc, "[间联]支付宝撤销:"+subMsg);
				this.covertAlipayStatus(outputParam, subCode, subMsg);
				break;
			}
		} catch (Exception e) {
			logger.error("[间联]支付宝撤销处理异常：" + e.getMessage(),e);
			outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
			outputParam.putValue(Dict.respCode, "[间联]支付宝撤销处理异常"+ e.getMessage());
		}finally {
			logger.info("[间联]支付宝撤销返回报文:"+outputParam.toString());
		}
		return outputParam;
	}
	
	/**
	/**
	 * 支付宝统一收单交易退款
	 */
	@Override
	public OutputParam toALiPayRefundOrder(InputParam input) throws FrameException {
		logger.info("[支付宝退款交易]向支付宝发起退款交易请求请求报文:"+input.toString());
		
		OutputParam outputParam = new OutputParam();
		String txnSeqId = ObjectUtils.toString(input.getValue(Dict.txnSeqId));
		String initTxnSeqIdTime = ObjectUtils.toString(input.getValue(Dict.initTxnSeqIdTime));
		String rateChannel = String.format("%s",input.getValue(Dict.rateChannel));
		String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
		String refundAmount = ObjectUtils.toString(input.getValue(Dict.refundAmount));
		String refundReason = ObjectUtils.toString(input.getValue(Dict.refundReason));
		
		try {
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("out_request_no", txnSeqId); // 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
			data.put("refund_amount", refundAmount); // 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
			data.put("out_trade_no", initTxnSeqIdTime); // 原支付请求的商户订单号,和支付宝交易号不能同时为空
			data.put("refund_currency", "CNY"); // 订单退款币种信息
			if(!StringUtil.isEmpty(refundReason)){
				data.put("refund_reason", refundReason); // 退款的原因说明
			}
			
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeRefund"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String returnData = yLAliPayService.aliSdk(data, needData); //  统一收单交易退款接口
			
			JSONObject json = JSONObject.fromObject(returnData);
			String msg = StringUtil.toString(json.get(Dict.msg));
			String code = StringUtil.toString(json.get(Dict.code));
			String subMsg = StringUtil.toString(json.get(Dict.sub_msg));
			
			int codeint = Integer.parseInt(code);
			switch (codeint) {
				case StringConstans.AL_CODE.CODE_10000:
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_02);
					outputParam.putValue(Dict.msg, "支付宝退款成功");
					break;
				case StringConstans.AL_CODE.CODE_20000:
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
					outputParam.putValue(Dict.msg, "支付宝返回:"+subMsg);
					break;
				default:
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "支付宝返回:"+subMsg);
					break;
			}
			
		} catch (Exception e) {
			logger.error("[支付宝退款交易]向支付宝发起退款交易请求处理异常：" + e.getMessage(),e);
			outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
			outputParam.putValue(Dict.msg, "向支付宝发起退款交易请求处理异常");
		} finally {
			logger.info("[支付宝退款交易]向支付宝发起退款交易请求返回报文:"+outputParam.toString());
		}
		
		return outputParam;
	}

	/**
	 * 支付宝统一收单交易退款查询
	 */
	@Override
	public OutputParam toQueryALiPayRefundOrder(InputParam input) throws FrameException {
		logger.info("[调用支付宝接口查询退款交易流程]START"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String txnSeqId = StringUtil.toString(input.getValue(Dict.txnSeqId));
			String initTxnSeqId = StringUtil.toString(input.getValue(Dict.initTxnSeqId));
			String initTxnTime = StringUtil.toString(input.getValue(Dict.initTxnTime));
			String rateChannel = String.format("%s",input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("out_request_no", txnSeqId); // 必填 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
			data.put("out_trade_no", initTxnSeqId+initTxnTime); // 原支付请求的商户订单号,和支付宝交易号不能同时为空
				
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeRefundQuery"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String returnData = yLAliPayService.aliSdk(data, needData); //统一收单交易退款查询
			
			JSONObject json = JSONObject.fromObject(returnData);
			String msg = StringUtil.toString(json.get(Dict.msg));
			String code = StringUtil.toString(json.get(Dict.code));
			String subMsg = StringUtil.toString(json.get(Dict.sub_msg));
			String refundAmount = StringUtil.toString(json.get(Dict.refund_amount));
			
			//1.该接口的返回码 10000，仅代表本次查询操作成功，不代表退款成功。
			//2.如果该接口返回了查询数据(可以判断有没有返回 refund_amount)，则代表退款成功，
			//3.如果没有查询到则代表未退款成功，可以调用退款接口进行重试。
			//4.重试时请务必保证退款请求号一致
			int codeint = Integer.parseInt(code);
			switch (codeint) {
				case StringConstans.AL_CODE.CODE_10000:
					if(!StringUtil.isEmpty(refundAmount)){
						outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_02);
						outputParam.putValue(Dict.msg, "支付宝退款成功");
					} else {
						outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
						outputParam.putValue(Dict.msg, "支付宝未返回有关该笔订单信息");
					}
					break;
				case StringConstans.AL_CODE.CODE_20000:
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_01);
					outputParam.putValue(Dict.msg, "支付宝返回:"+subMsg);
					break;
				default:
					outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
					outputParam.putValue(Dict.msg, "支付宝返回:"+subMsg);
					break;
			}
		} catch (Exception e) {
			logger.error("[调用支付宝接口查询退款交易流程]支付宝交易退款查询处理异常：" + e.getMessage(),e);
			outputParam.putValue(Dict.refundStatus, StringConstans.RefundStatus.STATUS_03);
			outputParam.putValue(Dict.msg, "支付宝交易退款查询处理异常");
		} finally {
			logger.info("[调用支付宝接口查询退款交易流程]END"+outputParam.toString());
		}
		
		return outputParam;
	}

	/**
	 * 支付宝统一收单交易关闭
	 */
	@Override
	public OutputParam toCloseALiPayOrder(InputParam input)throws FrameException {
		
		OutputParam outputParam = new OutputParam();
		
		logger.info("----------------向支付宝请求关闭订单流程     START-----------------");
		
		try {

			/********** 组装支付宝交易关闭报文并发送请求 ********/
			String outTradeNo =  String.format("%s", input.getValue(Dict.outTradeNo));
			String alipayTradeNo = String.format("%s", input.getValue(Dict.alipayTradeNo)); 
			String operatorId =  String.format("%s", input.getValue(Dict.operatorId));
			String merId =  String.format("%s", input.getValue(Dict.merId));
			String subAlipayMerId =  String.format("%s", input.getValue(Dict.subAlipayMerId));
			
			if(StringUtil.isEmpty(outTradeNo) && StringUtil.isEmpty(alipayTradeNo)){
				logger.error("[支付宝交易撤销] 原外部订单号或者支付宝订单号不能全为空");
				outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
				outputParam.setReturnMsg(" 原外部订单号或者支付宝订单号不能全为空");
				return outputParam;
			}
			
			logger.info("组装支付宝交易关闭的报文");
			AlipayTradeCloseOrderRequestBuilder builder = new AlipayTradeCloseOrderRequestBuilder();
			
			if(!StringUtil.isEmpty(outTradeNo)){
				builder.setOutTradeNo(outTradeNo);
			}
			
			if(!StringUtil.isEmpty(alipayTradeNo)){
				builder.setTradeNo(alipayTradeNo);
			}
		  
			if(!StringUtil.isEmpty(operatorId)){
				builder.setOperatorId(operatorId);
			}
			logger.info("组装支付宝交易关闭的报文,报文组装完成,请求支付宝关闭订单  开始");
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			
			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));

			AlipayF2FCloseOrderResult result = alipayTradeService.closeOrder(builder);
			
			logger.info("组装支付宝交易关闭的报文,报文组装完成,请求支付宝关闭订单  完成 ，结果result：" + result.getTradeStatus());
			
			AlipayTradeCloseResponse response = result.getResponse();
			logger.debug("subCode:"+response.getSubCode()+",subMsg:"+response.getSubMsg());
			
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.info("[支付宝订单关闭] 支付宝交易订单关闭成功");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
				outputParam.setReturnMsg("支付宝交易订单关闭成功");
				break;
			case FAILED:
				logger.error("[支付宝订单关闭] 支付宝交易订单关闭失败");
				this.covertAlipayStatus(outputParam, response.getSubCode(), response.getSubMsg());
				break;
			case UNKNOWN:
				logger.error("[支付宝订单关闭] 支付宝交易订单关闭状态未知");
				this.covertAlipayStatus(outputParam, response.getSubCode(), response.getSubMsg());
				break;
			default:
				logger.error("[支付宝订单关闭] 不支持的交易状态");
				outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			}
			
			logger.info("----------------向支付宝请求关闭订单流程      END-----------------");
			
		} catch (Exception e) {
			logger.error("[支付宝订单关闭] 支付宝交易关闭处理异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
			outputParam.setReturnMsg("支付宝交易关闭处理异常");
		}
		
		return outputParam;
	}
	
	/**
	 * 支付宝断直连统一收单交易关闭
	 */
	@Override
	public OutputParam toCloseALiPayOrderYL(InputParam input)throws FrameException {
		logger.info("[支付宝断直连统一收单交易关闭]   toCloseALiPayOrderYL  START:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {

			String outTradeNo =  StringUtil.toString(input.getValue(Dict.outTradeNo));
			String alipayTradeNo = StringUtil.toString(input.getValue(Dict.alipayTradeNo)); 
			String operatorId =  StringUtil.toString(input.getValue(Dict.operatorId));
			String rateChannel = String.format("%s", input.getValue(Dict.rateChannel));
			
			if(StringUtil.isEmpty(outTradeNo) && StringUtil.isEmpty(alipayTradeNo)){
				outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
				outputParam.setReturnMsg("[支付宝断直连统一收单交易关闭] 原外部订单号或者支付宝订单号不能全为空");
				return outputParam;
			}
			
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			//组装断直连支付宝订单关闭报文并发送
			Map<String, Object> reqData = new HashMap<String, Object>();
			if(!StringUtil.isEmpty(outTradeNo)){
				reqData.put(Dict.out_trade_no, outTradeNo);
			} else {
				reqData.put(Dict.trade_no, alipayTradeNo);
			}
			reqData.put(Dict.operator_id, operatorId);
			
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.tradeClose"));
			needData.put(Dict.cacheKey, cacheKey);
			
			logger.info("[支付宝断直连统一收单交易关闭] 报文组装完成,请求支付宝关闭订单  完成 ，结果result：" + reqData.toString()+","+needData.toString());
			String resp = yLAliPayService.aliSdk(reqData, needData);
			logger.info("[支付宝断直连统一收单交易关闭] 报文组装完成,请求支付宝关闭订单  完成 ，结果result：" + resp);
			
			JSONObject jsonObject = JSONObject.fromObject(resp);
			String code = jsonObject.getString(Dict.code);
			String msg = jsonObject.getString(Dict.msg);
			String submsg = jsonObject.getString(Dict.sub_msg);
			
			int codeInter = Integer.parseInt(code);
			
			switch (codeInter) {
			case StringConstans.AL_CODE.CODE_10000:
				logger.debug("[支付宝断直连统一收单交易关闭] 支付宝交易订单关闭成功");
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_02);
				outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_02);
				outputParam.setReturnMsg("[支付宝断直连统一收单交易关闭]:"+msg);
				break;
			case StringConstans.AL_CODE.CODE_20000:
				logger.debug("[支付宝订单关闭] 支付宝交易订单关闭状态未知");
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_10);
				outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_10);
				outputParam.setReturnMsg("[支付宝断直连统一收单交易关闭]:"+submsg);
				break;	
			default:
				logger.debug("[支付宝断直连统一收单交易关闭关闭订单流程]  不支持的交易状态");
				outputParam.putValue(Dict.orderSta, StringConstans.OrderState.STATE_03);
				outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
				outputParam.setReturnMsg("[支付宝断直连统一收单交易关闭] :"+submsg);
				break;
			}
		} catch (Exception e) {
			logger.error("[支付宝断直连统一收单交易关闭] 支付宝交易关闭处理异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
			outputParam.setReturnMsg("[支付宝断直连统一收单交易关闭]支付宝交易关闭处理异常： "+e.getMessage());
		}finally {
			logger.info("[支付宝断直连统一收单交易关闭] toCloseALiPayOrderYL END:"+outputParam.toString());
		}
		
		return outputParam;
	}
	
	/**
	 * 支付宝统一查询账单接口
	 */
	@Override
	public OutputParam toQueryBill(InputParam input) throws FrameException {
		
		logger.info("-----------向支付宝发起对账单下载请求流程         START------------------");
		
		OutputParam out = new OutputParam();
		
		try {
			
			/********** 组装支付宝交易关闭报文并发送请求 ********/
			
			//账单类型
			String billType = ObjectUtils.toString(input.getValue("billType"));
			
			//账单时间
			String billDate = ObjectUtils.toString(input.getValue("billDate"));
			
			logger.info("[支付宝下载对账单]组装获取支付宝账单下载地址的报文");
			AlipayTradeQueryBillRequestBuilder builder = new AlipayTradeQueryBillRequestBuilder();
			builder.setBillDate(billDate)
				   .setBillType(billType);
			
			logger.info("[支付宝下载对账单]获取支付宝账单下载地址的报文组装完成,请求获取支付宝账单下载地址  START");
			AlipayF2FQueryBillResult result = alipayTradeService.queryBill(builder);
			AlipayDataDataserviceBillDownloadurlQueryResponse  response = result.getResponse();
			
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.info("[支付宝下载对账单] 获取支付宝账单下载地址成功");
				out.setReturnCode(StringConstans.returnCode.SUCCESS);
				//设置返回值 下载地址URL
				out.putValue("billDownloadUrl", response.getBillDownloadUrl());
				break;
			case FAILED:
				logger.error("[支付宝下载对账单] 获取支付宝账单下载地址失败");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(response.getSubMsg());
				out.putValue("subCode", response.getSubCode());
				break;
			case UNKNOWN:
				logger.error("[支付宝下载对账单] 获取支付宝账单下载地址结果未知");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(response.getSubMsg());
				out.putValue("subCode", response.getSubCode());
				break;
			default:
				logger.error("[支付宝下载对账单] 不支持的交易状态");
				out.setReturnCode(StringConstans.returnCode.FAIL);
				out.setReturnMsg(response.getSubMsg());
				out.putValue("subCode", response.getSubCode());
				break;
			}
			
			logger.info("-----------向支付宝发起对账单下载请求流程         END------------------");
			
		} catch (Exception e) {
			logger.error("[支付宝下载对账单] 下载对账单处理异常：" + e.getMessage(),e);
			out.setReturnCode(StringConstans.returnCode.FAIL);
			out.setReturnMsg("下载对账单处理异常");
		}
		
		return out;
	}
	
	@Override
	public OutputParam getAuthToken(InputParam input) throws FrameException {
		logger.info("向支付宝获取token请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			
			//授权类型
			String grantType = String.format("%s", input.getValue("grantType"));
			//授权码
			String code = String.format("%s", input.getValue("code"));
			//刷新令牌
			String refreshToken = String.format("%s", input.getValue("refreshToken"));
			
			logger.debug("[支付宝获取用户授权信息]组装获取支付宝用户授权信息的报文");
			
			AlipaySystemOauthTokenBuilder builder = new AlipaySystemOauthTokenBuilder();
			builder.setGrantType(grantType)
				   .setCode(code);
			
			if(!StringUtil.isEmpty(refreshToken)){
				builder.setRefreshToken(refreshToken);
			}
			
			logger.debug("[支付宝获取用户授权信息]获取支付宝支付宝获取用户授权信息报文组装完成    START");
			
			builder.setAppid(Constants.getParam("appid"));
			builder.setGatewayUrl(Constants.getParam("open_api_domain"));
			builder.setAlipayPublicKey(Constants.getParam("alipay_public_key"));
			builder.setPrivateKey(Constants.getParam("private_key"));
			
			
			AlipaySystemOauthTokenResult result = alipayTradeService.getAlipaySystemOauthToken(builder);
			AlipaySystemOauthTokenResponse  response = result.getResponse();
			
			logger.info("[支付宝获取用户授权信息] 返回信息:" + response.getBody());
			
			switch (result.getTradeStatus()) {
			case SUCCESS:
				logger.debug("[支付宝获取用户授权信息] 获取支付宝用户授权信息成功");
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
				outputParam.putValue("accessToken", response.getAccessToken());
				outputParam.putValue("refreshToken", response.getRefreshToken());
				outputParam.putValue("expiresIn", response.getExpiresIn());
				outputParam.putValue("userId", response.getUserId());
				break;
			case FAILED:
				logger.debug("[支付宝获取用户授权信息] 获取支付宝用户授权信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			case UNKNOWN:
				logger.debug("[支付宝获取用户授权信息] 获取支付宝用户授权信息未知");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			default:
				logger.debug("[支付宝获取用户授权信息] 获取支付宝用户授权信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				break;
			}
			
		} catch (Exception e) {
			logger.error("[支付宝获取用户授权信息] 支付宝获取用户授权信息处理异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝获取用户授权信息处理异常");
		} finally {
			logger.info("向支付宝获取token返回报文:"+outputParam.toString());
		}
		return outputParam;
	}
	
	/**
	 * 设置查询结果信息
	 * @param outputParam
	 * @param response
	 */
	private  void  putQueryResultInfo(OutputParam outputParam,AlipayTradeQueryResponse response){
		
		//交易状态描述
		String message  = response.getSubMsg();
		
		//错误码
		String subCode = response.getSubCode();

		//商户门店编号
		String storeId = response.getStoreId();
		
		//交易支付中的商户店铺名称
		String storeName =  response.getStoreName();
		
		//终端号
		String terminalId =response.getTerminalId();
		
		//外部订单号
		String outTradeNo = response.getOutTradeNo();
		
		//支付宝交易号
		String alipayTradeNo = response.getTradeNo();
				
		//买家支付宝账号
		String buyerLogonId = response.getBuyerLogonId();
		
		//买家支付宝用户ID
		String buyerUserId = response.getBuyerUserId();
		
		//支付宝店铺编号
		String alipayStoreId = response.getAlipayStoreId();
		
		//特殊行业信息
		String industrySepcDetail = response.getIndustrySepcDetail();
	
		//交易支付使用资金渠道
		List<TradeFundBill> fundBillList = response.getFundBillList();
		
		//订单详细信息
		String discountGoodsDetail = response.getDiscountGoodsDetail();
		
		//积分支付金额
		String pointAmount = StringUtil.amountTo12Str(response.getPointAmount());
		
		//交易订单金额
		String totalAmount = StringUtil.amountTo12Str(response.getTotalAmount());
		
		//实收金额
		String receiptAmount = StringUtil.amountTo12Str(response.getReceiptAmount());
		
		//买家实付金额
		String buyerPayAmount = StringUtil.amountTo12Str(response.getBuyerPayAmount());
		
		//交易中可开具发票金额
		String invoiceAmount = StringUtil.amountTo12Str(response.getInvoiceAmount());
		
		//本次打款给卖家时间
		String settleDate = DateUtil.format(response.getSendPayDate(),DateUtil.YYYYMMDDHHMMSS);	
		
		//交易状态
		String tradeStatus = AliPayPayServiceImpl.alipayStatusToOrderStatus(response.getTradeStatus(),message,subCode).get("orderState");
				
		//返回信息赋值	
		outputParam.putValue("alipayTradeNo",alipayTradeNo);
		outputParam.putValue("outTradeNo", outTradeNo);
		outputParam.putValue("buyerLogonId", buyerLogonId);
		outputParam.putValue("tradeStatus",tradeStatus);
		outputParam.putValue("totalAmount",totalAmount);
		outputParam.putValue("receiptAmount",receiptAmount);
		outputParam.putValue("settleDate", settleDate);
		outputParam.putValue("buyerUserId", buyerUserId);
		
		if(!StringUtil.isEmpty(buyerPayAmount)){
			outputParam.putValue("buyerPayAmount",buyerPayAmount);
		}
		
		if(!StringUtil.isEmpty(pointAmount)){
			outputParam.putValue("pointAmount", pointAmount);
		}
		
		if(!StringUtil.isEmpty(invoiceAmount)){
			outputParam.putValue("invoiceAmount", invoiceAmount);
		}
		
		if(!StringUtil.isEmpty(alipayStoreId)){
			outputParam.putValue("alipayStoreId", alipayStoreId);
		}
		
		if(!StringUtil.isEmpty(storeId)){
			outputParam.putValue("storeId", storeId);
		}
		
		if(!StringUtil.isEmpty(terminalId)){
			outputParam.putValue("terminalId",terminalId);
		}
		
		if(!StringUtil.listIsEmpty(fundBillList)){
			outputParam.putValue("fundBillList",fundBillList);
		}
		
		if(!StringUtil.isEmpty(storeName)){
			outputParam.putValue("storeName", storeName);
		}

		if(!StringUtil.isEmpty(industrySepcDetail)){
			outputParam.putValue("industrySepcDetail", industrySepcDetail);	
		}
		
		if(!StringUtil.isEmpty(discountGoodsDetail)){
			outputParam.putValue("discountGoodsDetail", discountGoodsDetail);	
		}
	}
	private  void  putQueryResultInfo(OutputParam outputParam,String information){
		JSONObject json = JSONObject.fromObject(information);
		String message  = StringUtil.toString(json.get(Dict.sub_msg));
		String subCode = StringUtil.toString(json.get(Dict.sub_code));
		String storeId = StringUtil.toString(json.get(Dict.store_id));
		String storeName =  StringUtil.toString(json.get(Dict.store_name));
		String terminalId =StringUtil.toString(json.get(Dict.terminal_id));
		String outTradeNo = StringUtil.toString(json.get(Dict.out_trade_no));
		String alipayTradeNo =StringUtil.toString(json.get(Dict.trade_no));
		String buyerLogonId = StringUtil.toString(json.get(Dict.buyer_logon_id));
		String buyerUserId = StringUtil.toString(json.get(Dict.buyer_user_id));
		String alipayStoreId = StringUtil.toString(json.get(Dict.aipay_store_id));
		String industrySepcDetail = StringUtil.toString(json.get(Dict.industry_sepc_detail));
		String fundBillList = StringUtil.toString(json.get(Dict.fund_bill_list));
		String discountGoodsDetail =StringUtil.toString(json.get(Dict.discount_goods_detail));
		String pointAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.point_amount)));
		String totalAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.total_amount)));
		String receiptAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.receipt_amount)));
		String buyerPayAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.buyer_pay_amount)));
		String invoiceAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.invoice_amount)));
		String settleDate = DateUtil.date2string(StringUtil.toString(json.get(Dict.send_pay_date)));	
		String tradeStatus = AliPayPayServiceImpl.alipayStatusToOrderStatus(StringUtil.toString(json.get(Dict.trade_status)),message,subCode).get("orderState");
		
		outputParam.putValue("alipayTradeNo",alipayTradeNo);
		outputParam.putValue("outTradeNo", outTradeNo);
		outputParam.putValue("buyerLogonId", buyerLogonId);
		outputParam.putValue("tradeStatus",tradeStatus);
		outputParam.putValue("totalAmount",totalAmount);
		outputParam.putValue("receiptAmount",receiptAmount);
		outputParam.putValue("settleDate", settleDate);
		outputParam.putValue("buyerUserId", buyerUserId);
		outputParam.putValueRemoveNull("buyerPayAmount",buyerPayAmount);
		outputParam.putValueRemoveNull("pointAmount", pointAmount);
		outputParam.putValueRemoveNull("invoiceAmount", invoiceAmount);
		outputParam.putValueRemoveNull("alipayStoreId", alipayStoreId);
		outputParam.putValueRemoveNull("storeId", storeId);
		outputParam.putValueRemoveNull("terminalId",terminalId);
		outputParam.putValueRemoveNull("storeName", storeName);
		outputParam.putValueRemoveNull("industrySepcDetail", industrySepcDetail);	
		outputParam.putValueRemoveNull("discountGoodsDetail", discountGoodsDetail);
		outputParam.putValueRemoveNull("fundBillList",fundBillList);
		
	}
	private  void  putMicroResultInfo(OutputParam outputParam,String information){
		
		JSONObject json = JSONObject.fromObject(information);
		String alipayTradeNo =StringUtil.toString(json.get(Dict.trade_no));
		String outTradeNo = StringUtil.toString(json.get(Dict.out_trade_no));
		String buyerLogonId = StringUtil.toString(json.get(Dict.buyer_logon_id));
		String totalAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.total_amount)));
		String receiptAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.receipt_amount)));
		String buyerPayAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.buyer_pay_amount)));
		String pointAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.point_amount)));
		String invoiceAmount = StringUtil.amountTo12Str(StringUtil.toString(json.get(Dict.invoice_amount)));
		String settleDate = DateUtil.date2string(StringUtil.toString(json.get(Dict.send_pay_date)));	
		String fundBillList = StringUtil.toString(json.get(Dict.fund_bill_list));
		String storeName =  StringUtil.toString(json.get(Dict.store_name));
		String buyerUserId = StringUtil.toString(json.get(Dict.buyer_user_id));
		String discountGoodsDetail =StringUtil.toString(json.get(Dict.discount_goods_detail));
		
		outputParam.putValue("alipayTradeNo", alipayTradeNo);
		outputParam.putValue("outTradeNo", outTradeNo);
		outputParam.putValue("buyerLogonId", buyerLogonId);
		outputParam.putValue("totalAmount", totalAmount);
		outputParam.putValue("receiptAmount", receiptAmount);
		outputParam.putValue("buyerPayAmount", buyerPayAmount);
		outputParam.putValue("pointAmount", pointAmount);
		outputParam.putValue("invoiceAmount", invoiceAmount);
		outputParam.putValue("settleDate",settleDate);
		outputParam.putValue("fundBillList", fundBillList);
		outputParam.putValue("storeName", storeName);
		outputParam.putValue("buyerUserId", buyerUserId);
		outputParam.putValue("discountGoodsDetail", discountGoodsDetail);
		
	}
	
	private  void  putRefundResultInfo(OutputParam outputParam,AlipayTradeRefundResponse response){

		//交易支付中的商户店铺名称
		String storeName = response.getStoreName();
				
		//本次退款是否发生了资金变化
		String fundChange = response.getFundChange();
				
		//外部订单号
		String outTradeNo = response.getOutTradeNo();
				
		//支付宝交易号
		String alipayTradeNo = response.getTradeNo();
						
		//买家支付宝账号
		String buyerLogonId = response.getBuyerLogonId();
				
		//买家支付宝用户ID
		String buyerUserId = response.getBuyerUserId();
		
		//退款总金额
		String refundFee = StringUtil.amountTo12Str(response.getRefundFee());
				
		//本次商户实际退回金额
		String sendBackFee = StringUtil.amountTo12Str(response.getSendBackFee());
		
		//退款支付时间
		String settleDate = DateUtil.format(response.getGmtRefundPay(),DateUtil.YYYYMMDDHHMMSS);	
				
		//返回信息赋值	
		outputParam.putValue("alipayTradeNo",alipayTradeNo);
		outputParam.putValue("outTradeNo", outTradeNo);
		outputParam.putValue("buyerLogonId", buyerLogonId);
		outputParam.putValue("fundChange",fundChange);
		outputParam.putValue("refundFee",refundFee);
		outputParam.putValue("settleDate", settleDate);
		outputParam.putValue("buyerUserId", buyerUserId);
		
		if(!StringUtil.isEmpty(storeName)){
			outputParam.putValue("storeName",storeName);
		}
		
		if(!StringUtil.isEmpty(sendBackFee)){
			outputParam.putValue("sendBackFee", sendBackFee);
		}
	}
	/**
	 *设置支付宝被扫交易返回结果信息 
	 */
	private  void  putScanedResultInfo(OutputParam outputParam,AlipayTradePayResponse response){
		//返回信息赋值
		outputParam.putValue("alipayTradeNo", response.getTradeNo());
		outputParam.putValue("outTradeNo", response.getOutTradeNo());
		//买家支付宝帐号
		outputParam.putValue("buyerLogonId", response.getBuyerLogonId());
		//交易金额
		outputParam.putValue("totalAmount", StringUtil.amountTo12Str(response.getTotalAmount()));
		outputParam.putValue("receiptAmount", StringUtil.amountTo12Str(response.getReceiptAmount()));
		outputParam.putValue("buyerPayAmount", StringUtil.amountTo12Str(response.getBuyerPayAmount()));
		outputParam.putValue("pointAmount", StringUtil.amountTo12Str(response.getPointAmount()));
		outputParam.putValue("invoiceAmount", StringUtil.amountTo12Str(response.getInvoiceAmount()));
		//支付宝支付交易时间
		String settleDate = DateUtil.format(response.getGmtPayment(),DateUtil.YYYYMMDDHHMMSS);
		outputParam.putValue("settleDate",settleDate);
		outputParam.putValue("fundBillList", response.getFundBillList());
		outputParam.putValue("cardBalance", response.getCardBalance());
		outputParam.putValue("storeName", response.getStoreName());
		//买家在支付宝的用户Id
		outputParam.putValue("buyerUserId", response.getBuyerUserId());
		outputParam.putValue("discountGoodsDetail", response.getDiscountGoodsDetail());
		
	}

	/**
	 * 支付宝支付撤销新增订单表信息
	 * @param input            发送过来的信息
	 * @param initAlipayOrder  原订单信息
	 * @return                 OutputParam
	 * @throws FrameException
	 */
	public OutputParam addRevokeOrder(InputParam input) throws FrameException {
		logger.info("[支付宝支付撤销] addRevokeOrder 新增订单信息"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String channel = ObjectUtils.toString(input.getValue(Dict.channel));	
			String txnSeqId = orderService.getOrderNo(channel);
			
			// 新增订单
			InputParam orderInput = new InputParam();
			String txnDt = DateUtil.format(new Date(), DateUtil.YYYYMMDD);
			String txnTm = DateUtil.format(new Date(), DateUtil.HHMMSS);
			String oglOrdId = ObjectUtils.toString(input.getValue(Dict.initTxnSeqId));
			String oglOrdDate = ObjectUtils.toString(input.getValue(Dict.initTxnTime));
			String orderNumber = ObjectUtils.toString(input.getValue(Dict.orderNumber));
			String orderTime = ObjectUtils.toString(input.getValue(Dict.orderTime));
			String merOrDt = orderTime.substring(0, 8);
			String merOrTm =  orderTime.substring(8, 14);
			String txnType = ObjectUtils.toString(input.getValue(Dict.txnType));
			String payAccessType = ObjectUtils.toString(input.getValue(Dict.payAccessType));	
			String orderAmount = ObjectUtils.toString(input.getValue(Dict.orderAmount));
			String merId = ObjectUtils.toString(input.getValue(Dict.merId));
			String subAlipayMerId = ObjectUtils.toString(input.getValue(Dict.subAlipayMerId));
			String payType = ObjectUtils.toString(input.getValue(Dict.payType));
			String currencyCode = ObjectUtils.toString(input.getValue(Dict.currencyCode));
			String refundAmount = ObjectUtils.toString(input.getValue(Dict.refundAmount));
			String outRequestNo = ObjectUtils.toString(input.getValue(Dict.outRequestNo));
			String resDesc = "订单初始化";
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			inputQuery.putparamString(Dict.merId, merId);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				return outputParam;
			}
			String connectMethod = StringUtil.toString(outQuery.getValue(Dict.connectMethod));
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			String cacheKey = null;
			if (StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
				 cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			}else {
				 cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			}
			
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String alipayMerId = configMap.get(Dict.ALI_MERID);
			
			orderInput.putparamString(Dict.txnSeqId, txnSeqId);
			orderInput.putparamString(Dict.txnDt, txnDt);
			orderInput.putparamString(Dict.txnTm, txnTm);
			orderInput.putparamString(Dict.oglOrdId, oglOrdId);
			orderInput.putparamString(Dict.oglOrdDate, oglOrdDate);
			orderInput.putparamString(Dict.merOrderId, orderNumber);
			orderInput.putparamString(Dict.merOrDt, merOrDt);
			orderInput.putparamString(Dict.merOrTm, merOrTm);
			orderInput.putparamString(Dict.txnType, txnType);
			orderInput.putparamString(Dict.txnChannel, channel);
			orderInput.putparamString(Dict.payAccessType, payAccessType);
			orderInput.putparamString(Dict.tradeMoney, orderAmount);
			orderInput.putparamString(Dict.merId, merId);
			orderInput.putparamString(Dict.alipayMerId,alipayMerId);
			orderInput.putparamString(Dict.subAlipayMerId, subAlipayMerId);
			orderInput.putparamString(Dict.txnSta, StringConstans.OrderState.STATE_01);
			orderInput.putparamString(Dict.payType, payType);
			orderInput.putparamString(Dict.currencyCode, currencyCode);
			orderInput.putparamString(Dict.resDesc,resDesc);
			
			if(!StringUtil.isEmpty(refundAmount)){
				orderInput.putparamString(Dict.tradeMoney,refundAmount);
				orderInput.putparamString(Dict.refundAmount,refundAmount);
			}
			
			if(!StringUtil.isEmpty(outRequestNo)){
				orderInput.putparamString(Dict.outRequestNo,outRequestNo);
			}
			
			
			OutputParam orderOutPut = orderService.insertOrder(orderInput);
			if (!StringConstans.returnCode.SUCCESS.equals(orderOutPut.getReturnCode())) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("增加订单失败");
				return outputParam;
			}
			
			outputParam.putValue(Dict.txnDt, txnDt);
			outputParam.putValue(Dict.txnTm, txnTm);
			outputParam.putValue(Dict.txnSeqId, txnSeqId);
			outputParam.putValue(Dict.connectMethod, connectMethod);
			outputParam.putValue(Dict.rateChannel, rateChannel);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			
		} catch (Exception e) {
			logger.error("支付宝支付撤销,新增订单表流水失败:" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝支付撤销,新增订单表流水失败");
		}finally {
			logger.info("[支付宝支付撤销] addRevokeOrder 返回信息"+outputParam.toString());
		}
		
		return outputParam;
	}
	
	/**
	 * 支付宝支付新增订单表信息
	 * @param input           
	 * @return OutputParam
	 * @throws FrameException
	 */
	
	public OutputParam addConsumeOrder(InputParam input) throws FrameException {
		logger.info("[支付宝下单]新增订单信息请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			String payChannel = StringUtil.toString(input.getValue(Dict.payChannel),StringConstans.PAY_ChANNEL.ALI);
			//渠道编号
			String channel = String.format("%s",input.getValue(Dict.channel));
			String txnSeqId = orderService.getOrderNo(channel);
				
			//交易日期
			String txnDt = DateUtil.format(new Date(), DateUtil.YYYYMMDD);
			
			//交易时间
			String txnTm = DateUtil.format(new Date(), DateUtil.HHMMSS);
			
			//订单号
			String orderNumber = String.format("%s", input.getValue("orderNumber"));
			
			//订单时间
			String orderTime = String.format("%s",input.getValue("orderTime"));
			
			//订单日期
			String merOrDt = orderTime.substring(0,8);
			
			//订单时间
			String merOrTm = orderTime.substring(8,14);
			
			//交易金额
			String orderAmount =String.format("%s",input.getValue("orderAmount"));
			
			//商户号
			String merId = String.format("%s",input.getValue("merId"));
			
			//商户名称
			String merName = String.format("%s",input.getValue("merName"));
			
			//交易类型
			String transType = String.format("%s",input.getValue("transType"));
			
			//支付接入类型
			String payAccessType = String.format("%s",input.getValue("payAccessType"));
			
			//币种
			String currencyType = String.format("%s",input.getValue("currencyType"));		
			
			//支付类型
			String payType = String.format("%s",input.getValue("payType"));
			
			//支付宝商户号
			String subAlipayMerId = String.format("%s",input.getValue("alipayMerchantId"));
			
			//可打折金额
			String discountableAmount = String.format("%s",input.getValue("discountableAmount"));
			
			//原不可打折金额
			String undiscountableAmount = String.format("%s",input.getValue("undiscountableAmount"));
			
			//订单备注
			String remark = String.format("%s",input.getValue("remark"));
			
			//本行费率
			String bankFeeRate =String.format("%s",input.getValue("bankFeeRate"));
			
			//清算方式
			String settleMethod =String.format("%s",input.getValue("settleMethod"));
	 
			//交易状态描述
			String resDesc = "订单初始化";
			
			String alipayMerId = "";
			if(StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				InputParam inputQuery = new InputParam();
				inputQuery.putparamString(Dict.subMerchant, subAlipayMerId);
				inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
				inputQuery.putparamString(Dict.merId, merId);
				OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
				if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg(outQuery.getReturnMsg());
					return outputParam;
				}
				String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
				
				String cacheKey = payChannel + "_" + rateChannel;
				Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
				alipayMerId = configMap.get(Dict.ALI_MERID);
			} else if(StringConstans.CHANNEL.CHANNEL_SELF.equals(channel)) {
				alipayMerId = Constants.getParam("appid");
			}
			
			// 新增订单
			InputParam orderInput = new InputParam();
			orderInput.putparamString("txnSeqId", txnSeqId);
			orderInput.putparamString("txnDt", txnDt);
			orderInput.putparamString("txnTm", txnTm);
			orderInput.putparamString("merOrderId", orderNumber);
			orderInput.putparamString("merOrDt", merOrDt);
			orderInput.putparamString("merOrTm", merOrTm);
			orderInput.putparamString("txnType", transType);
			orderInput.putparamString("txnChannel", channel);
			orderInput.putparamString("payAccessType", payAccessType);
			orderInput.putparamString("tradeMoney", orderAmount);
			orderInput.putparamString("currencyCode", currencyType);
			orderInput.putparamString("merId", merId);
			orderInput.putparamString("merName", merName);
			orderInput.putparamString("alipayMerId",alipayMerId);
			orderInput.putparamString("payType", payType);
			orderInput.putparamString("subAlipayMerId", subAlipayMerId);
			orderInput.putparamString("txnSta", StringConstans.OrderState.STATE_01);
			orderInput.putparamString("accountedFlag", StringConstans.AccountedFlag.ACCOUNTEDUNKNOWN);
			orderInput.putparamString("resDesc",resDesc);
			
			if(!StringUtil.isEmpty(discountableAmount)){
				orderInput.putparamString("discountableAmount",discountableAmount);
			}
			
			if(!StringUtil.isEmpty(undiscountableAmount)){
				orderInput.putparamString("undiscountableAmount",undiscountableAmount);
			}
			
			if(!StringUtil.isEmpty(remark)){
				orderInput.putparamString("remark",remark);
			}
			
			//三码合一时需要该字段  与 三码合一静态二维码关联
			if(!StringUtil.isEmpty(input.getValue("ewmData"))){
				orderInput.putparamString("ewmData", input.getValue("ewmData").toString());
			}
			if(!StringUtil.isEmpty(input.getValue("bankFeeRate"))){
				orderInput.putparamString("bankFeeRate", input.getValue("bankFeeRate").toString());
			}
			if(!StringUtil.isEmpty(input.getValue("settleMethod"))){
				orderInput.putparamString("settleMethod", input.getValue("settleMethod").toString());
			}
			
			logger.debug("[支付宝支付下单] 增加下单订单开始");
			
			OutputParam orderOutPut = orderService.insertOrder(orderInput);
			
			logger.debug("[支付宝支付下单] 增加下单订单结束");
			
			if (!StringConstans.returnCode.SUCCESS.equals(orderOutPut.getReturnCode())) {
				logger.debug("[支付宝支付下单] 新增订单信息失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("增加订单信息失败");
				return orderOutPut;
			}
			
			outputParam.putValue("txnDt", txnDt);
			outputParam.putValue("txnTm", txnTm);
			outputParam.putValue("txnSeqId", txnSeqId);
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			
		} catch (Exception e) {
			logger.error("[支付宝支付下单],新增订单表流水异常:" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("支付宝支付下单,新增订单表流水异常");
		} finally {
			logger.info("[支付宝下单],新增订单信息返回报文:"+outputParam.toString());
		}
		return outputParam;
	}

	/**
	 * 支付宝退款更新退款累计总金额
	 * @author  zhaoyuanxiang
	 * @return
	 */
	public OutputParam upTotalRefundFee (InputParam input)throws FrameException{
		
		OutputParam outputParam = new OutputParam();

		try {
			
			logger.info("---------- 更新原交易退款累计总金额流程  START ---------");
	
			String txnSeqId = input.getValueString("txnSeqId");
			String txnDt = input.getValueString("txnDt");
			String txnTm = input.getValueString("txnTm");
			String totalRefundFee = input.getValueString("totalRefundFee");
			
			logger.error("[更新原交易退款累计总金额] 更新原交易累计金额    开始  ");
			
			OutputParam upateOutputParam = orderService.updateRefundTotalAmount(txnSeqId, txnDt, txnTm, totalRefundFee);
			
			logger.error("[更新原交易退款累计总金额] 更新原交易累计金额    结束 ");
			
			if (!StringConstans.returnCode.SUCCESS.equals(upateOutputParam.getReturnCode())) {
				logger.error("[更新原交易退款累计总金额]  更新原交易累计金额失败");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("更新原交易累计金额");
				return outputParam;
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			
			logger.info("---------- 更新原交易退款累计总金额流程    END---------");
			
		} catch (Exception e) {
			logger.error("支付宝退款更新退款累计总金额异常:" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("更新原交易退款累计总金额出现异常");
		}
		
		return outputParam;
	}
	
	public void setOrderState(OutputParam outputParam,String tradeStatus,String ...message){
		Map<String, String> map = AliPayPayServiceImpl.alipayStatusToOrderStatus(tradeStatus,message);
		outputParam.putValue("orderSta", map.get("orderState"));
		outputParam.putValue("orderDesc",map.get("orderDesc"));
	}
	
	/**
	 * 处理订单关闭状态
	 * @param outputParam
	 * @param subCode
	 * @param subMsg
	 */
	private void covertAlipayStatus(OutputParam outputParam,String subCode,String subMsg){
		if(AlipayErrorCode.TRADE_NOT_EXIST.equals(subCode)){
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_05);
			outputParam.setReturnMsg(subMsg);
		}else if(AlipayErrorCode.TRADE_STATUS_ERROR.equals(subCode)){
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_07);
			outputParam.setReturnMsg(subMsg);
		} else if (AlipayErrorCode.SYSTEM_ERROR.equals(subCode)){
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_20);
			outputParam.setReturnMsg(subMsg);
		}else if(AlipayErrorCode.SYSTEM_ERROR_AOP.equals(subCode)){
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_20);
			outputParam.setReturnMsg(subMsg);
		}else if(AlipayErrorCode.SELLER_BALANCE_NOT_ENOUGH.equals(subCode)){
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_21);
			outputParam.setReturnMsg(subMsg);
		}else if(AlipayErrorCode.REASON_TRADE_BEEN_FREEZEN.equals(subCode)){
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_22);
			outputParam.setReturnMsg(subMsg);
		}else {
			outputParam.setReturnCode(StringConstans.RespCode.RESP_CODE_03);
			outputParam.setReturnMsg(subMsg);
		}
	}
	
	public AlipayTradeService getAlipayTradeService() {
		return alipayTradeService;
	}

	public void setAlipayTradeService(AlipayTradeService alipayTradeService) {
		this.alipayTradeService = alipayTradeService;
	}


	public IOrderService getOrderService() {
		return orderService;
	}


	public void setOrderService(IOrderService orderService) {
		this.orderService = orderService;
	}

	public IMerchantChannelService getMerchantChannelService() {
		return merchantChannelService;
	}

	public void setMerchantChannelService(
			IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

	public YLAliPayService getyLAliPayService() {
		return yLAliPayService;
	}

	public void setyLAliPayService(YLAliPayService yLAliPayService) {
		this.yLAliPayService = yLAliPayService;
	}

}
