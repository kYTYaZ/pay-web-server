package com.huateng.pay.services.alipay.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alipay.api.domain.AddressInfo;
import com.alipay.api.domain.BankCardInfo;
import com.alipay.api.domain.ContactInfo;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectCreateRequetBuilder;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectModifyRequetBuilder;
import com.alipay.demo.trade.model.builder.AntMerchantExpandIndirectQueryRequetBuilder;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectCreateResult;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectModifyResult;
import com.alipay.demo.trade.model.result.AntMerchantExpandIndirectQueryResult;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectCreateResponse;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectModifyResponse;
import com.alipay.demo.trade.response.AntMerchantExpandIndirectQueryResponse;
import com.alipay.demo.trade.service.AntMerchantExpandIndirectSummerchantService;
import com.google.common.collect.Lists;
import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.common.validate.YlAliValidation;
import com.huateng.pay.common.validate.vali.Validation;
import com.huateng.pay.dao.inter.ISequenceDao;
import com.huateng.pay.services.alipay.AliPayMerchantSynchService;
import com.huateng.pay.services.alipay.YLAliPayService;
import com.huateng.pay.services.db.IMerchantChannelService;
import com.huateng.utils.Util;
import com.wldk.framework.utils.MappingUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 支付宝同步商户接口实现
 * 
 */
public class AliPayMerchantSynchServiceImpl implements AliPayMerchantSynchService {

	private Logger logger = LoggerFactory.getLogger(AliPayMerchantSynchServiceImpl.class);
	private AntMerchantExpandIndirectSummerchantService antMerchantExpandIndirectSummerchantService;
	private IMerchantChannelService merchantChannelService;
	private ISequenceDao sequenceDao;
	private YLAliPayService yLAliPayService;

	public OutputParam routing(InputParam input) {
		logger.info("支付宝路由接口请求报文:"+input.toString());
		OutputParam out = new OutputParam();
		String merId = StringUtil.toString(input.getValue(Dict.merId));
		String alipayMerchantId = StringUtil.toString(input.getValue(Dict.alipayMerchantId));

		InputParam inputQuery = new InputParam();
		inputQuery.putparamString(Dict.merId, merId);
		inputQuery.putparamString(Dict.subMerchant, alipayMerchantId);
		inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
		OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
		if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
			out.setReturnCode(outQuery.getReturnCode());
			out.setReturnMsg(outQuery.getReturnMsg());
			return out;
		}

		String connectMethod = StringUtil.toString(outQuery.getValue(Dict.connectMethod));
		String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
		out.setReturnCode(StringConstans.returnCode.SUCCESS);
		out.putValue(Dict.connectMethod, connectMethod);
		out.putValue(Dict.rateChannel, rateChannel);
		
		logger.info("支付宝路由接口返回报文:"+out.toString());
		return out;
	}

	/**
	 * 新增支付宝同步商户
	 */
	@Override
	public OutputParam addALiPayMer(InputParam input) throws FrameException {

		logger.info("新增支付宝同步商户 START请求报文:" + input.toString());

		OutputParam outputParam = new OutputParam();

		try {

			// 请求报文非空字段验证
			List<String> list = new ArrayList<String>();
			list.add("merId");
			list.add("name");
			list.add("aliasName");
			list.add("servicePhone");
			list.add("contactName");
			list.add("contactType");
			list.add("idCardNo");
			list.add("categoryId");
			// list.add("rateChannel");
			// M2
			// list.add("provinceCode");
			// list.add("cityCode");
			// list.add("districtCode");
			// list.add("address");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[新增支付宝同步商户] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 渠道编号
			String channel = ObjectUtils.toString(input.getValue("channel"));

			// 目前渠道值支持POSP
			logger.debug("[新增支付宝同步商户] 渠道类型验证,channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[新增支付宝同步商户] 渠道类型错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("渠道类型错误");
				return outputParam;
			}

			// 二级商户号
			String externalId = ObjectUtils.toString(input.getValue("merId"));

			// 二级商户名称
			String name = ObjectUtils.toString(input.getValue("name"));

			// 二级商户简称
			String aliasName = ObjectUtils.toString(input.getValue("aliasName"));

			// 客服电话
			String servicePhone = ObjectUtils.toString(input.getValue("servicePhone"));

			// 联系人名称小二
			String contactName = ObjectUtils.toString(input.getValue("contactName"));

			// 联系人电话
			String contactPhone = ObjectUtils.toString(input.getValue("contactPhone"));

			// 联系人手机号
			String contactMobile = ObjectUtils.toString(input.getValue("contactMobile"));

			// 联系人邮箱
			String contactEmail = ObjectUtils.toString(input.getValue("contactEmail"));

			// 经营类目
			String categoryId = ObjectUtils.toString(input.getValue("categoryId"));

			// 备注
			String memo = ObjectUtils.toString(input.getValue("memo"));

			// 联系人类型
			String contactType = ObjectUtils.toString(input.getValue("contactType"));

			// 身份证号
			String idCardNo = ObjectUtils.toString(input.getValue("idCardNo"));

			// 省份编码
			String provinceCode = ObjectUtils.toString(input.getValue("provinceCode"));

			// 城市编码
			String cityCode = ObjectUtils.toString(input.getValue("cityCode"));

			// 区县编码
			String districtCode = ObjectUtils.toString(input.getValue("districtCode"));

			// 详细地址
			String address = ObjectUtils.toString(input.getValue("address"));

			// 经度
			String longitude = ObjectUtils.toString(input.getValue("longitude"));

			// 纬度
			String latitude = ObjectUtils.toString(input.getValue("latitude"));

			// 地址类型
			String addressType = ObjectUtils.toString(input.getValue("addType"));

			// 银行卡号
			String cardNo = ObjectUtils.toString(input.getValue("cardNo"));

			// 持卡人姓名
			String cardName = ObjectUtils.toString(input.getValue("cardName"));

			// 费率通道
			String rateChannel = ObjectUtils.toString(input.getValue("rateChannel"));
			if (StringUtil.isEmpty(rateChannel)) {
				rateChannel = "20";
			}

			// mcc
			String mcc = ObjectUtils.toString(input.getValue("mcc"));

			// orgPid
			String orgPid = ObjectUtils.toString(input.getValue("orgPid"));

			if (!StringUtil.isEmpty(cardName) && StringUtil.isEmpty(cardNo)) {

				logger.debug("[新增支付宝同步商户] 结算卡信息错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("结算卡信息cardNo不能为空");
				return outputParam;
			}

			if (StringUtil.isEmpty(cardName) && !StringUtil.isEmpty(cardNo)) {
				logger.debug("[新增支付宝同步商户] 结算卡信息错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("结算卡信息cardName不能为空");
				return outputParam;

			}

			// 原样返回
			outputParam.putValue("merId", externalId);
			outputParam.putValue("channel", channel);

			// 初始化新增接口并赋值(不为空的情况下)
			AntMerchantExpandIndirectCreateRequetBuilder builder = new AntMerchantExpandIndirectCreateRequetBuilder();

			// 联系信息DO
			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setIdCardNo(idCardNo);
			contactInfo.setType(contactType);
			contactInfo.setName(contactName);

			if (!StringUtil.isEmpty(contactEmail)) {
				contactInfo.setEmail(contactEmail);
			}
			if (!StringUtil.isEmpty(contactMobile)) {
				contactInfo.setEmail(contactMobile);
			}
			if (!StringUtil.isEmpty(contactPhone)) {
				contactInfo.setEmail(contactPhone);
			}

			List<String> addressInfoList = new ArrayList<String>(4);
			addressInfoList.add(address);
			addressInfoList.add(cityCode);
			addressInfoList.add(provinceCode);
			addressInfoList.add(districtCode);

			// 地址信息DO
			AddressInfo addressinfo = null;
			boolean flagM2 = Util.validateString(addressInfoList.toArray());

			if (!flagM2) {
				addressinfo = new AddressInfo();
				addressinfo.setAddress(address);
				addressinfo.setCityCode(cityCode);
				addressinfo.setProvinceCode(provinceCode);
				addressinfo.setDistrictCode(districtCode);

				if (!StringUtil.isEmpty(longitude)) {
					addressinfo.setLongitude(longitude);
				}
				if (!StringUtil.isEmpty(latitude)) {
					addressinfo.setLatitude(latitude);
				}
				if (!StringUtil.isEmpty(addressType)) {
					addressinfo.setType(addressType);
				}
			}

			List<String> bankCardInfoLisr = new ArrayList<String>(2);
			bankCardInfoLisr.add(cardName);
			bankCardInfoLisr.add(cardNo);

			boolean flagM3 = Util.validateString(bankCardInfoLisr.toArray());
			// 结算卡信息DO
			BankCardInfo bankCardInfo = null;

			if (!flagM3) {
				bankCardInfo = new BankCardInfo();
				bankCardInfo.setCardName(cardName);
				bankCardInfo.setCardNo(cardNo);
			}

			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			// 商户来源机构标识
			String source = configMap.get(Dict.ALI_MERID);

			builder.setExternalId(externalId).setName(name).setAliasName(aliasName).setServicePhone(servicePhone)
					.setCategoryId(categoryId).setSource(source).setContactInfo(Lists.newArrayList(contactInfo));

			if (addressinfo != null) {
				builder.setAddressInfo(Lists.newArrayList(addressinfo));
			}
			if (bankCardInfo != null) {
				builder.setBankcardInfo(Lists.newArrayList(bankCardInfo));
			}

			if (!StringUtil.isEmpty(memo)) {
				builder.setMemo(memo);
			}

			mcc = "5331";
			orgPid = "2088721382101609";
			if (!StringUtil.isEmpty(mcc)) {
				builder.setMcc(mcc);
			}
			if (!StringUtil.isEmpty(orgPid)) {
				builder.setOrgPid(orgPid);
			}

			logger.debug("向支付宝新增商户流程开始");

			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));

			// 发送报文并获得返回信息
			AntMerchantExpandIndirectCreateResult result = antMerchantExpandIndirectSummerchantService
					.createSubmerchant(builder);
			AntMerchantExpandIndirectCreateResponse response = result.getResponse();

			if (!result.isTradeSuccess()) {
				logger.debug("[新增支付宝商户]增加支付宝商户失败：" + "msg:" + response.getSubMsg());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				return outputParam;
			}

			String alipayMerchantId = response.getSubMerchantId();

			InputParam queryInput = new InputParam();
			queryInput.putparamString("subMerchant", alipayMerchantId);
			queryInput.putparamString("rate", rateChannel);
			queryInput.putparamString("channel", StringConstans.PAY_ChANNEL.ALI);
			OutputParam queryOut = merchantChannelService.querySubmerIsExist(queryInput);
			logger.info("验证子商户是否存在,返回报文:" + queryOut.toString());
			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				// 子商户、渠道、费率确定的唯一数据不存在
				InputParam insertInput = new InputParam();
				insertInput.putparamString("seqMerchant", sequenceDao.getSubmerChannelRate());
				insertInput.putparamString("subMerchant", alipayMerchantId);
				insertInput.putparamString("merId", externalId);
				insertInput.putparamString("channel", StringConstans.PAY_ChANNEL.ALI);
				insertInput.putparamString("rate", rateChannel);
				insertInput.putparamString("reserve1", DateUtil.getCurrentDateTimeFormat(DateUtil.defaultSimpleFormater));
				insertInput.putparamString("reserve2", externalId);
				OutputParam inertOut = merchantChannelService.insertSubmerChannelRate(insertInput);

				if (!StringConstans.returnCode.SUCCESS.equals(inertOut.getReturnCode())) {
					logger.debug("保存支付宝[子商户渠道费率信息关联表]失败");
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg("保存支付宝[子商户渠道费率信息关联表]失败");
					return outputParam;
				}
			}

			logger.info("[新增支付宝同步商户] 增加支付宝商户成功");
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", StringConstans.aliPaySyncReturnMsg.ALIPAY_ADD_SUCCESS);
			outputParam.putValue("alipayMerchantId", alipayMerchantId);

		} catch (Exception e) {
			logger.error("增加支付宝商户出现异常：" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("增加支付宝同步商户出现异常");
		} finally {
			logger.info("向支付宝增加商户流程出现结束，返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	public OutputParam addALiPayMerYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝同步商户START请求报文:" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {

			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliCreateMer, "[间联]支付宝同步商户");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String externalId = StringUtil.toString(input.getValue(Dict.merId));
			String name = StringUtil.toString(input.getValue(Dict.name));
			String aliasName = StringUtil.toString(input.getValue(Dict.aliasName));
			String servicePhone = StringUtil.toString(input.getValue(Dict.servicePhone));
			String contactName = StringUtil.toString(input.getValue(Dict.contactName));
			String contactPhone = StringUtil.toString(input.getValue(Dict.contactPhone));
			String contactMobile = StringUtil.toString(input.getValue(Dict.contactMobile));
			String contactEmail = StringUtil.toString(input.getValue(Dict.contactEmail));
			String categoryId = StringUtil.toString(input.getValue(Dict.categoryId));
			String memo = StringUtil.toString(input.getValue(Dict.memo));
			String contactType = StringUtil.toString(input.getValue(Dict.contactType));
			String idCardNo = StringUtil.toString(input.getValue(Dict.idCardNo));
			String provinceCode = StringUtil.toString(input.getValue(Dict.provinceCode));
			String cityCode = StringUtil.toString(input.getValue(Dict.cityCode));
			String districtCode = StringUtil.toString(input.getValue(Dict.districtCode));
			String address = StringUtil.toString(input.getValue(Dict.address));
			String longitude = StringUtil.toString(input.getValue(Dict.longitude));
			String latitude = StringUtil.toString(input.getValue(Dict.latitude));
			String addressType = StringUtil.toString(input.getValue(Dict.addType));
			String cardNo = StringUtil.toString(input.getValue(Dict.cardNo));
			String cardName = StringUtil.toString(input.getValue(Dict.cardName));
			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel), "20");
			String mcc = StringUtil.toString(input.getValue(Dict.mcc));
			String orgPid = StringUtil.toString(input.getValue(Dict.orgPid));
			String channel = StringUtil.toString(input.getValue(Dict.channel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;

			// 目前渠道值支持POSP
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[间联]支付宝同步商户渠道类型错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("渠道类型错误");
				return outputParam;
			}

			if (StringUtil.isEmpty(cardName) ^ StringUtil.isEmpty(cardNo)) {
				logger.debug("[间联]结算卡信息cardName和cardNo只允许同时为空或同时不为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]结算卡信息cardName和cardNo只允许同时为空或同时不为空");
				return outputParam;
			}

			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);
			String source = configMap.get(Dict.ALI_MERID);

			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("external_id", externalId); // 商户编号，由机构定义，需要保证在机构下唯一
			data.put("name", name); // 商户名称
			data.put("alias_name", aliasName); // 商户简称
			data.put("service_phone", servicePhone); // 商户客服电话
			data.put("category_id", categoryId); // 商户经营类目
			data.put("source", source); // 商户来源机构标识，填写机构在支付宝的pid
			data.put("org_pid", orgPid);
			data.put("mcc", mcc);
			// 商户联系人信息
			List<Object> contactInfo = new LinkedList<Object>();
			Map<String, Object> infos = new LinkedHashMap<String, Object>();
			infos.put("name", contactName); // 联系人姓名
			/**
			 * 商户联系人业务标识枚举，表示商户联系人的职责。 异议处理接口人:02;商户关键联系人:06;数据反馈接口人:11;服务联动接口人:08
			 */
			String[] tag = { "06" };
			infos.put("tag", JSONArray.fromObject(tag));
			infos.put("type", contactType); // 联系人类型，取值范围：LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他
			// 非必填
			infos.put("phone", contactPhone); // 电话
			infos.put("mobile", contactMobile); // 手机
			infos.put("email", contactEmail); // 电子邮箱
			infos.put("id_card_no", idCardNo); // 身份证号
			contactInfo.add(infos);
			data.put("contact_info", JSONArray.fromObject(contactInfo));
			List<Object> addressInfo = new LinkedList<Object>();
			Map<String, Object> addinfos = new LinkedHashMap<String, Object>();
			addinfos.put("city_code", cityCode); // 城市编码，城市编码是与国家统计局一致
			addinfos.put("district_code", districtCode); // 区县编码，区县编码是与国家统计局一致，
			addinfos.put("address", address); // 地址。商户详细经营地址或人员所在地点
			addinfos.put("province_code", provinceCode); // 省份编码，省份编码是与国家统计局一致
			addinfos.put("longitude", longitude); // 经度，浮点型, 小数点后最多保留6位。
			addinfos.put("latitude", latitude); // 纬度，浮点型,小数点后最多保留6位如需要录入经纬度，请以高德坐标系为准，
			addinfos.put("type", addressType); // 地址类型。取值范围：BUSINESS_ADDRESS：经营地址（默认）
			addressInfo.add(addinfos);
			data.put("address_info", JSONArray.fromObject(addressInfo));
			// data.put("business_license", "100000011234561"); //
			// 商户证件编号（企业或者个体工商户提供营业执照，事业单位提供事证号）
			/**
			 * 商户证件类型，取值范围：NATIONAL_LEGAL：营业执照；
			 * NATIONAL_LEGAL_MERGE:营业执照(多证合一)；INST_RGST_CTF：事业单位法人证书
			 */
			// data.put("business_license_type", "NATIONAL_LEGAL");
			// 商户对应银行所开立的结算卡信息
			if (!StringUtil.isEmptyMultipleStr(cardNo, cardName)) {
				List<Object> bankcardInfo = new LinkedList<Object>();
				Map<String, Object> bankinfos = new LinkedHashMap<String, Object>();
				bankinfos.put("card_no", cardNo); // 银行卡号
				bankinfos.put("card_name", cardName); // 银行卡持卡人姓名
				bankcardInfo.add(bankinfos);
				data.put("bankcard_info", JSONArray.fromObject(bankcardInfo));
			}
			// String[] codeInfo = { "http://www.domain.com" };
			// data.put("pay_code_info", JSONArray.fromObject(codeInfo)); //
			// 商户的支付二维码中信息，用于营销活动
			// String[] logonInfo = {"user@domain.com"};
			// data.put("logon_id", JSONArray.fromObject(logonInfo)); //商户的支付宝账号
			data.put("memo", memo); // 商户备注信息，可填写额外信息

			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.indirectCreate"));
			needData.put(Dict.cacheKey, cacheKey);

			String returnMsg = yLAliPayService.aliSdk(data, needData); // 商户入驻

			JSONObject json = JSONObject.fromObject(returnMsg);
			String msg = StringUtil.toString(json.get(Dict.msg));
			String code = StringUtil.toString(json.get(Dict.code));
			String sub_msg = StringUtil.toString(json.get(Dict.sub_msg));
			String sub_code = StringUtil.toString(json.get(Dict.sub_code));
			String subMerchantId = StringUtil.toString(json.get(Dict.sub_merchant_id));

			if (!"Success".equals(msg) || !"10000".equals(code)) {
				logger.debug("[间联]支付宝同步商户失败：" + sub_msg);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("新增支付宝商户返回:" + sub_msg);
				return outputParam;
			}

			InputParam queryInput = new InputParam();
			queryInput.putparamString("subMerchant", subMerchantId);
			queryInput.putparamString("rate", rateChannel);
			queryInput.putparamString("channel", StringConstans.PAY_ChANNEL.ALI);
			OutputParam queryOut = merchantChannelService.querySubmerIsExist(queryInput);
			if (StringConstans.returnCode.SUCCESS.equals(queryOut.getReturnCode())) {
				// 子商户、渠道、费率确定的唯一数据不存在
				InputParam insertInput = new InputParam();
				insertInput.putparamString("seqMerchant", sequenceDao.getSubmerChannelRate());
				insertInput.putparamString("subMerchant", subMerchantId);
				insertInput.putparamString("merId", externalId);
				insertInput.putparamString("channel", StringConstans.PAY_ChANNEL.ALI);
				insertInput.putparamString("rate", rateChannel);
				insertInput.putparamString("reserve1", DateUtil.getCurrentDateTimeFormat(DateUtil.defaultSimpleFormater));
				insertInput.putparamString("reserve2", externalId);
				insertInput.putparamString("connectMethod", StringConstans.CONNECT_METHOD.indirect);
				insertInput.putparamString("reserveType", StringConstans.RESERVE_TYPE.increase);
				OutputParam inertOut = merchantChannelService.insertSubmerChannelRate(insertInput);

				if (!(StringConstans.returnCode.SUCCESS.equals(inertOut.getReturnCode()))) {
					outputParam.setReturnCode(StringConstans.returnCode.FAIL);
					outputParam.setReturnMsg("保存支付宝[子商户渠道费率信息关联表]失败");
					return outputParam;
				}
			}

			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", StringConstans.aliPaySyncReturnMsg.ALIPAY_ADD_SUCCESS);
			outputParam.putValue("alipayMerchantId", subMerchantId);
			outputParam.putValue("merId", externalId);
			outputParam.putValue("channel", channel);

		} catch (Exception e) {
			logger.error("[间联]支付宝同步商户出现异常：" + e.getMessage(), e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("[间联]支付宝同步商户出现异常");
		} finally {
			logger.info("[间联]支付宝同步商户END返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 查询支付宝同步商户
	 */
	@Override
	public OutputParam queryALiPayMer(InputParam input) throws FrameException {

		logger.info("查询支付宝商户START请求报文:" + input.toString());

		OutputParam outputParam = new OutputParam();

		try {

			// 渠道编号
			String channel = ObjectUtils.toString(input.getValue("channel"));

			// 目前渠道只支持POSP
			logger.debug("[查询支付宝同步商户] 校验渠道类型,channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[查询支付宝同步商户] 渠道类型错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("渠道类型错误");
				return outputParam;
			}

			// 二级商户号
			String externalId = ObjectUtils.toString(input.getValue("merId"));
			if (StringUtil.isEmpty(externalId)) {
				logger.debug("[查询支付宝同步商户] 商户号不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("商户号不能为空");
				return outputParam;
			}

			// 二级商户在支付宝入驻后的识别号
			String subMerchantId = ObjectUtils.toString(input.getValue("alipayMerchantId"));
			if (StringUtil.isEmpty(subMerchantId)) {
				logger.debug("[查询支付宝同步商户]入驻标识号不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("入驻标识号不能为空");
				return outputParam;
			}

			// //判断alipayMerchantId与merId不能同时为空
			// if (StringUtil.isEmpty(subMerchantId) && StringUtil.isEmpty(externalId)) {
			// logger.error("[查询支付宝同步商户] 入驻标识号与商户号不能同时为空");
			// outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			// outputParam.setReturnMsg("入驻标识号与商户号不能同时为空");
			// return outputParam;
			// }

			outputParam.putValue("merId", externalId);
			outputParam.putValue("alipayMerchantId", subMerchantId);
			outputParam.putValue("channel", channel);

			// 初始化查询接口并赋值(不为空的情况下)
			AntMerchantExpandIndirectQueryRequetBuilder builder = new AntMerchantExpandIndirectQueryRequetBuilder();

			if (!StringUtil.isEmpty(externalId)) {
				builder.setExternalId(externalId);
			}

			if (!StringUtil.isEmpty(subMerchantId)) {
				builder.setSubMerchantId(subMerchantId);
			}

			logger.info("向支付宝查询商户流程开始");

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.subMerchant, subMerchantId);
			inputQuery.putparamString(Dict.merId, externalId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
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

			// 发送报文并获得返回信息
			AntMerchantExpandIndirectQueryResult result = antMerchantExpandIndirectSummerchantService
					.querySubmerchant(builder);
			AntMerchantExpandIndirectQueryResponse response = result.getResponse();

			if (TradeStatus.SUCCESS != result.getTradeStatus()) {
				logger.debug("[查询支付宝同步商户] 查询支付宝商户失败：" + "msg:" + response.getSubMsg());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				return outputParam;
			}

			// 处理成功
			logger.info("[查询支付宝同步商户] 查询支付宝商户成功");
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", StringConstans.aliPaySyncReturnMsg.ALIPAY_QUERY_SUCCESS);
			outputParam.putValue("name", response.getName());
			outputParam.putValue("memo", response.getMemo());
			outputParam.putValue("aliasName", response.getAliasName());
			outputParam.putValue("servicePhone", response.getServicePhone());
			outputParam.putValue("categoryId", response.getCategoryId());
			outputParam.putValue("source", response.getSource());
			outputParam.putValue("merId", response.getExternalId());
			outputParam.putValue("alipayMerchantId", response.getSubMerchantId());
			outputParam.putValue("indirectLevel", response.getIndirectLevel());
			outputParam.putValue("contactName", response.getContactInfo().get(0).getName());
			outputParam.putValue("idCardNo", response.getContactInfo().get(0).getIdCardNo());
			outputParam.putValue("contactType", response.getContactInfo().get(0).getType());
			outputParam.putValue("mcc", response.getMcc());
			outputParam.putValue("orgPid", response.getOrgPid());

		} catch (Exception e) {
			logger.error("[查询支付宝同步商户]查询支付宝商户出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("查询支付宝同步商户出现异常");
		} finally {
			logger.info("向支付宝查询商户流程出现结束,返回报文:" + outputParam.toString());

		}

		return outputParam;
	}

	/**
	 * [查询支付宝断直连商户]同步商户
	 */
	@Override
	public OutputParam queryALiPayMerYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝商户查询请求报文:" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliQueryMer, "[间联]支付宝商户查询");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String channel = ObjectUtils.toString(input.getValue(Dict.channel));
			String externalId = ObjectUtils.toString(input.getValue(Dict.merId));
			String subMerchantId = ObjectUtils.toString(input.getValue(Dict.alipayMerchantId));

			logger.debug("[查询支付宝同步商户] 校验渠道类型,channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[查询支付宝断直连同步商户]渠道类型错误:" + channel);
				return outputParam;
			}

			outputParam.putValue("merId", externalId);
			outputParam.putValue("alipayMerchantId", subMerchantId);
			outputParam.putValue("channel", channel);

			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("sub_merchant_id", subMerchantId); // 商户在支付宝入驻成功后，生成的支付宝内全局唯一的商户编号，与external_id二选一必传

			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.cacheKey, cacheKey);
			needData.put(Dict.interfaceName, Constants.getParam("asdk.indirectQuery"));
			
			String resp = yLAliPayService.aliSdk(data, needData);

			JSONObject returnMap = JSONObject.fromObject(resp);
			String msg = StringUtil.toString(returnMap.get(Dict.msg));
			String code = StringUtil.toString(returnMap.get(Dict.code));
			String sub_msg = StringUtil.toString(returnMap.get(Dict.sub_msg));

			if (!"Success".equals(msg) || !"10000".equals(code)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]支付宝商户查询失败:" + sub_msg);
				return outputParam;
			}

			logger.debug("[间联]支付宝商户查询成功");
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", StringConstans.aliPaySyncReturnMsg.ALIPAY_QUERY_SUCCESS);
			outputParam.putValue("name", StringUtil.toString(returnMap.get(Dict.name)));
			outputParam.putValue("memo", StringUtil.toString(returnMap.get(Dict.memo)));
			outputParam.putValue("aliasName", StringUtil.toString(returnMap.get(Dict.alias_name)));
			outputParam.putValue("servicePhone", StringUtil.toString(returnMap.get(Dict.service_phone)));
			outputParam.putValue("categoryId", StringUtil.toString(returnMap.get(Dict.category_id)));
			outputParam.putValue("source", StringUtil.toString(returnMap.get(Dict.source)));
			outputParam.putValue("merId", StringUtil.toString(returnMap.get(Dict.external_id)));
			outputParam.putValue("alipayMerchantId", StringUtil.toString(returnMap.get(Dict.sub_merchant_id)));
			outputParam.putValue("indirectLevel", StringUtil.toString(returnMap.get(Dict.indirect_level)));
			outputParam.putValue("mcc", StringUtil.toString(returnMap.get(Dict.mcc)));
			outputParam.putValue("orgPid", StringUtil.toString(returnMap.get(Dict.org_pid)));

			JSONObject personMap = JSONObject.fromObject(JSONArray.fromObject(returnMap.get(Dict.contact_info)).get(0));
			outputParam.putValue("contactName", StringUtil.toString(personMap.get(Dict.name)));
			outputParam.putValue("idCardNo", StringUtil.toString(personMap.get(Dict.id_card_no)));
			outputParam.putValue("contactType", StringUtil.toString(personMap.get(Dict.type)));
		} catch (Exception e) {
			logger.error("[间联]支付宝商户查询出现异常：" + e.getMessage() ,e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("[间联]支付宝商户查询出现异常:" + e.getMessage());
		} finally {
			logger.info("[间联]支付宝商户查询返回报文:" + outputParam.toString());
		}

		return outputParam;
	}

	/**
	 * 删除支付宝同步商户（暂定）
	 */
	@Override
	public OutputParam deleteALiPayMer(InputParam input) throws FrameException {
		return null;
	}

	/**
	 * 修改支付宝同步商户（暂定）
	 */
	@Override
	public OutputParam modifyALiPayMer(InputParam input) throws FrameException {
		logger.info("-修改支付宝商户START请求报文:" + input.toString());

		OutputParam outputParam = new OutputParam();

		try {

			// 请求报文非空字段验证
			List<String> list = new ArrayList<String>();
			list.add("name");
			list.add("aliasName");
			list.add("servicePhone");
			list.add("contactName");
			list.add("contactType");
			list.add("idCardNo");
			list.add("categoryId");
			list.add("merId");
			list.add("alipayMerchantId");

			String nullStr = Util.validateIsNull(list, input);
			if (!StringUtil.isEmpty(nullStr)) {
				logger.debug("[修改支付宝商户] 请求报文字段[" + nullStr + "]不能为空");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("请求报文字段[" + nullStr + "]不能为空");
				return outputParam;
			}

			// 渠道编号
			String channel = ObjectUtils.toString(input.getValue("channel"));

			// 目前渠道只支持POSP
			logger.debug("[修改支付宝商户] 校验渠道类型,channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				logger.debug("[修改支付宝商户] 渠道类型错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("渠道类型错误");
				return outputParam;
			}

			// 二级商户号
			String externalId = ObjectUtils.toString(input.getValue("merId"));

			// 二级商户在支付宝入驻后的识别号
			String subMerchantId = ObjectUtils.toString(input.getValue("alipayMerchantId"));

			// 二级商户名称
			String name = ObjectUtils.toString(input.getValue("name"));

			// 二级商户简称
			String aliasName = ObjectUtils.toString(input.getValue("aliasName"));

			// 客服电话
			String servicePhone = ObjectUtils.toString(input.getValue("servicePhone"));

			// 联系人名称
			String contactName = ObjectUtils.toString(input.getValue("contactName"));

			// 联系人类型
			String contactType = ObjectUtils.toString(input.getValue("contactType"));

			// 身份证号
			String idCardNo = ObjectUtils.toString(input.getValue("idCardNo"));

			// 商户经营类目
			String categoryId = ObjectUtils.toString(input.getValue("categoryId"));

			// 联系人电话
			String contactPhone = ObjectUtils.toString(input.getValue("contactPhone"));

			// 联系人手机
			String contactMobile = ObjectUtils.toString(input.getValue("contactMobile"));

			// 联系人邮箱
			String contactEmail = ObjectUtils.toString(input.getValue("contactEmail"));

			// 省份编码
			String provinceCode = ObjectUtils.toString(input.getValue("provinceCode"));

			// 城市编码
			String cityCode = ObjectUtils.toString(input.getValue("cityCode"));

			// 区县编码
			String districtCode = ObjectUtils.toString(input.getValue("districtCode"));

			// 详细地址
			String address = ObjectUtils.toString(input.getValue("address"));

			// 经度
			String longitude = ObjectUtils.toString(input.getValue("longitude"));

			// 纬度
			String latitude = ObjectUtils.toString(input.getValue("latitude"));

			// 地址类型
			String addressType = ObjectUtils.toString(input.getValue("addType"));

			// 银行卡号
			String cardNo = ObjectUtils.toString(input.getValue("cardNo"));

			// 持卡人姓名
			String cardName = ObjectUtils.toString(input.getValue("cardName"));

			// mcc
			String mcc = ObjectUtils.toString(input.getValue("mcc"));

			// orgPid
			String orgPid = ObjectUtils.toString(input.getValue("orgPid"));

			// //判断alipayMerchantId与merId不能同时为空
			// if (StringUtil.isEmpty(subMerchantId) && StringUtil.isEmpty(externalId)) {
			// logger.error("[修改支付宝商户] 入驻标识号与商户号不能同时为空");
			// outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			// outputParam.setReturnMsg("入驻标识号与商户号不能同时为空");
			// return outputParam;
			// }

			if (!StringUtil.isEmpty(cardName) && StringUtil.isEmpty(cardNo)) {

				logger.debug("[新增支付宝同步商户] 结算卡信息错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("结算卡信息cardNo不能为空");
				return outputParam;
			}

			if (StringUtil.isEmpty(cardName) && !StringUtil.isEmpty(cardNo)) {
				logger.debug("[新增支付宝同步商户] 结算卡信息错误");
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("结算卡信息cardName不能为空");
				return outputParam;
			}

			// 原样返回
			outputParam.putValue("merId", externalId);
			outputParam.putValue("alipayMerchantId", subMerchantId);
			outputParam.putValue("channel", channel);

			// 初始化修改接口并赋值(不为空的情况下)
			AntMerchantExpandIndirectModifyRequetBuilder builder = new AntMerchantExpandIndirectModifyRequetBuilder();

			ContactInfo contactInfo = new ContactInfo();
			contactInfo.setIdCardNo(idCardNo);
			contactInfo.setType(contactType);
			contactInfo.setName(contactName);

			if (!StringUtil.isEmpty(contactEmail)) {
				contactInfo.setEmail(contactEmail);
			}
			if (!StringUtil.isEmpty(contactMobile)) {
				contactInfo.setMobile(contactMobile);
			}
			if (!StringUtil.isEmpty(contactPhone)) {
				contactInfo.setPhone(contactPhone);
			}

			List<String> addressInfoList = new ArrayList<String>(4);
			addressInfoList.add(address);
			addressInfoList.add(cityCode);
			addressInfoList.add(provinceCode);
			addressInfoList.add(districtCode);

			boolean flagM2 = Util.validateString(addressInfoList.toArray());
			// 地址信息DO
			AddressInfo addressinfo = null;
			if (!flagM2) {
				addressinfo = new AddressInfo();
				addressinfo.setAddress(address);
				addressinfo.setCityCode(cityCode);
				addressinfo.setProvinceCode(provinceCode);
				addressinfo.setDistrictCode(districtCode);

				if (!StringUtil.isEmpty(longitude)) {
					addressinfo.setLongitude(longitude);
				}
				if (!StringUtil.isEmpty(latitude)) {
					addressinfo.setLatitude(latitude);
				}
				if (!StringUtil.isEmpty(addressType)) {
					addressinfo.setType(addressType);
				}
			}

			List<String> bankCardInfoLisr = new ArrayList<String>(2);
			bankCardInfoLisr.add(cardName);
			bankCardInfoLisr.add(cardNo);

			boolean flagM3 = Util.validateString(bankCardInfoLisr.toArray());
			// 结算卡信息DO
			BankCardInfo bankCardInfo = null;
			if (!flagM3) {
				bankCardInfo = new BankCardInfo();
				bankCardInfo.setCardName(cardName);
				bankCardInfo.setCardNo(cardNo);
			}

			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.subMerchant, subMerchantId);
			inputQuery.putparamString(Dict.merId, externalId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if (!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));

			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);

			// 商户来源机构标识
			String source = configMap.get(Dict.ALI_MERID);

			builder.setSource(source).setExternalId(externalId).setSubMerchantId(subMerchantId)
					.setCategoryId(categoryId).setServicePhone(servicePhone).setAliasName(aliasName).setName(name)
					.setContactInfo(Lists.newArrayList(contactInfo));

			if (!StringUtil.isEmpty(addressinfo)) {
				builder.setAddressInfo(Lists.newArrayList(addressinfo));
			}
			if (!StringUtil.isEmpty(bankCardInfo)) {
				builder.setBankcardInfo(Lists.newArrayList(bankCardInfo));
			}

			mcc = "5331";
			orgPid = "2088721382101609";
			if (!StringUtil.isEmpty(mcc)) {
				builder.setMcc(mcc);
			}
			if (!StringUtil.isEmpty(orgPid)) {
				builder.setOrgPid(orgPid);
			}

			logger.info("向支付宝修改商户流程开始");

			builder.setAppid(configMap.get(Dict.ALI_APPID));
			builder.setGatewayUrl(Constants.getParam(Dict.open_api_domain));
			builder.setAlipayPublicKey(configMap.get(Dict.ALI_PUBLIC_KEY));
			builder.setPrivateKey(configMap.get(Dict.ALI_PRIVATE_KEY));

			// 发送报文并获得返回信息
			AntMerchantExpandIndirectModifyResult result = antMerchantExpandIndirectSummerchantService
					.modifySubmerchant(builder);
			AntMerchantExpandIndirectModifyResponse response = result.getResponse();

			if (TradeStatus.SUCCESS != result.getTradeStatus()) {
				logger.debug("[修改支付宝商户] 修改支付宝商户失败：" + "msg:" + response.getSubMsg());
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(response.getSubMsg());
				return outputParam;
			}

			// 处理成功
			logger.info("[修改支付宝商户] 修改支付宝商户成功");
			outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
			outputParam.putValue("respCode", StringConstans.RespCode.RESP_CODE_02);
			outputParam.putValue("respDesc", StringConstans.aliPaySyncReturnMsg.ALIPAY_UPD_SUCCESS);
			outputParam.putValue("alipayMerchantId", response.getSubMerchantId());

		} catch (Exception e) {
			logger.error("[修改支付宝商户]修改支付宝商户出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("修改支付宝商户出现异常");
		} finally {
			logger.info("向支付宝修改商户流程出现结束.返回报文:" + outputParam.toString());

		}

		return outputParam;
	}

	/**
	 * 支付宝断直连同步商户修改
	 */
	@Override
	public OutputParam modifyALiPayMerYL(InputParam input) throws FrameException {
		logger.info("[间联]支付宝商户修改 modifyALiPayMerYL START:" + input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliEditMer, "[间联]支付宝商户修改");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}

			String channel = ObjectUtils.toString(input.getValue(Dict.channel));

			// 目前渠道只支持POSP
			logger.debug("[支付宝断直连同步商户修改] 校验渠道类型,channel=" + channel);
			if (!StringConstans.CHANNEL.CHANNEL_POSP.equals(channel)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]支付宝商户修改:渠道类型错误：" + channel);
				return outputParam;
			}

			String externalId = ObjectUtils.toString(input.getValue(Dict.merId));
			String subMerchantId = ObjectUtils.toString(input.getValue(Dict.alipayMerchantId));
			String name = ObjectUtils.toString(input.getValue(Dict.name));
			String aliasName = ObjectUtils.toString(input.getValue(Dict.aliasName));
			String servicePhone = ObjectUtils.toString(input.getValue(Dict.servicePhone));
			String contactName = ObjectUtils.toString(input.getValue(Dict.contactName));
			String contactType = ObjectUtils.toString(input.getValue(Dict.contactType));
			String idCardNo = ObjectUtils.toString(input.getValue(Dict.idCardNo));
			String categoryId = ObjectUtils.toString(input.getValue(Dict.categoryId));
			String contactPhone = ObjectUtils.toString(input.getValue(Dict.contactPhone));
			String contactMobile = ObjectUtils.toString(input.getValue(Dict.contactMobile));
			String contactEmail = ObjectUtils.toString(input.getValue(Dict.contactEmail));
			String provinceCode = ObjectUtils.toString(input.getValue(Dict.provinceCode));
			String cityCode = ObjectUtils.toString(input.getValue(Dict.cityCode));
			String districtCode = ObjectUtils.toString(input.getValue(Dict.districtCode));
			String address = ObjectUtils.toString(input.getValue(Dict.address));
			String longitude = ObjectUtils.toString(input.getValue(Dict.longitude));
			String latitude = ObjectUtils.toString(input.getValue(Dict.latitude));
			String addressType = ObjectUtils.toString(input.getValue(Dict.addType));
			String cardNo = ObjectUtils.toString(input.getValue(Dict.cardNo));
			String cardName = ObjectUtils.toString(input.getValue(Dict.cardName));
			String mcc = ObjectUtils.toString(input.getValue(Dict.mcc));
			String orgPid = ObjectUtils.toString(input.getValue(Dict.orgPid));

			if (StringUtil.isEmpty(cardName) ^ StringUtil.isEmpty(cardNo)) {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]支付宝商户修改:结算卡信息cardName和cardNo只允许同时为空或同时不为空");
				return outputParam;
			}

			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put(Dict.sub_merchant_id, subMerchantId); // 商户在支付宝入驻成功后，生成的支付宝内全局唯一的商户编号，与external_id二选一必传
			reqData.put(Dict.name, name); 
			reqData.put(Dict.alias_name, aliasName); 
			reqData.put(Dict.service_phone, servicePhone); 
			reqData.put(Dict.category_id, categoryId); 
			reqData.put(Dict.mcc, mcc);
			reqData.put(Dict.org_pid, orgPid);

			// 商户联系人信息 必传
			List<Object> contactInfo = new LinkedList<Object>();
			Map<String, Object> infos = new LinkedHashMap<String, Object>();
			infos.put(Dict.name, contactName);
			//商户联系人业务标识枚举，表示商户联系人的职责。 异议处理接口人:02;商户关键联系人:06;数据反馈接口人:11;服务联动接口人:08
			String[] tag = { "06" };
			infos.put(Dict.tag, JSONArray.fromObject(tag));
			infos.put(Dict.type, contactType); // 联系人类型，取值范围：LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他
			// 非必填
			infos.put(Dict.phone, contactPhone); 
			infos.put(Dict.mobile, contactMobile); 
			infos.put(Dict.email, contactEmail); 
			infos.put(Dict.id_card_no, idCardNo); 
			contactInfo.add(infos);
			reqData.put(Dict.contact_info, JSONArray.fromObject(contactInfo));

			// 地区信息 非必传
			List<String> addressInfoList = new ArrayList<String>(4);
			addressInfoList.add(address);
			addressInfoList.add(cityCode);
			addressInfoList.add(provinceCode);
			addressInfoList.add(districtCode);

			boolean flag = Util.validateString(addressInfoList.toArray());
			if (!flag) {
				List<Object> addressInfo = new LinkedList<Object>();
				Map<String, Object> addinfos = new LinkedHashMap<String, Object>();
				addinfos.put(Dict.city_code, cityCode); 
				addinfos.put(Dict.district_code, districtCode); 
				addinfos.put(Dict.address, address); 
				addinfos.put(Dict.province_code, provinceCode); 
				addinfos.put(Dict.longitude, longitude); 
				addinfos.put(Dict.latitude, latitude); 
				addinfos.put(Dict.type, addressType); 
				addressInfo.add(addinfos);
				reqData.put(Dict.address_info, JSONArray.fromObject(addressInfo));
			}

			// 商户对应银行的结算卡信息
			if (!StringUtil.isEmptyMultipleStr(cardName,cardNo)) {
				List<Object> bankcardInfo = new LinkedList<Object>();
				Map<String, Object> bankinfos = new LinkedHashMap<String, Object>();
				bankinfos.put(Dict.card_no, cardNo);
				bankinfos.put(Dict.card_name, cardName);
				bankcardInfo.add(bankinfos);
				reqData.put(Dict.bankcard_info, JSONArray.fromObject(bankcardInfo));
			}

			// 原样返回
			outputParam.putValue(Dict.merId, externalId);
			outputParam.putValue(Dict.alipayMerchantId, subMerchantId);
			outputParam.putValue(Dict.channel, channel);


			String rateChannel = StringUtil.toString(input.getValue(Dict.rateChannel));
			String cacheKey = StringConstans.PAY_ChANNEL.YLALI + "_" + rateChannel;
			Map<String, String> configMap = MappingUtils.getChannelConfig(cacheKey);

			reqData.put(Dict.source, configMap.get(Dict.ALI_MERID)); // 商户来源机构标识，填写机构在支付宝的pid

			// 发送报文并获得返回信息
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.indirectModify"));
			needData.put(Dict.cacheKey, cacheKey);

			String resp = yLAliPayService.aliSdk(reqData, needData);
			logger.info("[间联]支付宝商户修改返回报文:" + resp.toString());
			JSONObject jsonObject = JSONObject.fromObject(resp);
			String msg = StringUtil.toString(jsonObject.get(Dict.msg));
			String code = StringUtil.toString(jsonObject.get(Dict.code));
			String submsg = StringUtil.toString(jsonObject.get(Dict.sub_msg));

			int codeInt = Integer.valueOf(code);
			switch (codeInt) {
			case StringConstans.AL_CODE.CODE_10000:
				outputParam.setReturnCode(StringConstans.returnCode.SUCCESS);
				outputParam.setReturnMsg("[间联]支付宝商户修改成功");
				outputParam.putValue(Dict.respCode, StringConstans.RespCode.RESP_CODE_02);
				outputParam.putValue(Dict.respDesc, "[间联]支付宝商户修改成功");
				outputParam.putValue(Dict.alipayMerchantId, StringUtil.toString(jsonObject.get(Dict.sub_merchant_id)));
				break;
			default:
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]支付宝商户修改:" + submsg);
				break;
			}
		} catch (Exception e) {
			logger.error("[间联]支付宝商户修改出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("[间联]支付宝商户修改出现异常:" + e.getMessage());
		} finally {
			logger.info("[间联]支付宝商户修改 modifyALiPayMerYL END:" + outputParam.toString());
		}
		return outputParam;
	}
	
	
	public OutputParam transferAliMer(InputParam input) throws FrameException {
		logger.info("[间联]支付宝存量商户迁移请求报文:"+input.toString());
		OutputParam outputParam = new OutputParam();
		try {
			OutputParam valiOut = Validation.validate(input, YlAliValidation.vali_YlAliTransfer, "[间联]支付宝存量商户迁移");
			if (!StringConstans.returnCode.SUCCESS.equals(valiOut.getReturnCode())) {
				outputParam.setReturnCode(valiOut.getReturnCode());
				outputParam.setReturnMsg(valiOut.getReturnMsg());
				return outputParam;
			}
			
			String merId = ObjectUtils.toString(input.getValue(Dict.merId));
			String subMerchantId = ObjectUtils.toString(input.getValue(Dict.alipayMerchantId));
			
			InputParam inputQuery = new InputParam();
			inputQuery.putparamString(Dict.subMerchant, subMerchantId);
			inputQuery.putparamString(Dict.merId, merId);
			inputQuery.putparamString(Dict.channel, StringConstans.PAY_ChANNEL.ALI);
			OutputParam outQuery = merchantChannelService.querySubmerChannelRateInfo(inputQuery);
			if(!StringConstans.returnCode.SUCCESS.equals(outQuery.getReturnCode())){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg(outQuery.getReturnMsg());
				return outputParam;
			}
			String rateChannel = StringUtil.toString(outQuery.getValue(Dict.rate));
			String cacheKey = StringConstans.PAY_ChANNEL.ALI + "_" + rateChannel;
			String reserveType = StringUtil.toString(outQuery.getValue(Dict.reserveType));
			String seqMerchant = StringUtil.toString(outQuery.getValue(Dict.seqMerchant));
			
			if(!StringUtil.isEmpty(reserveType) && 
					!StringConstans.RESERVE_TYPE.stock.equals(reserveType)){
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("仅存量商户可进行迁移");
				return outputParam;
			}
			
			Map<String, String> needData = new HashMap<String, String>();
			needData.put(Dict.interfaceName, Constants.getParam("asdk.indirectTransfer"));
			needData.put(Dict.cacheKey, cacheKey);
			
			String transferTargetId = null;
			if(rateChannel.equals("20")){
				transferTargetId = Constants.getParam("yl_pid_20");
			} else if (rateChannel.equals("10")){
				transferTargetId = Constants.getParam("yl_pid_10");
			} else if (rateChannel.equals("00")){
				transferTargetId = Constants.getParam("yl_pid_00");
			} else if (rateChannel.equals("60")){
				transferTargetId = Constants.getParam("yl_pid_60");
			} else {
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("不存在该费率，可能子商户关联表中费率存储异常");
				return outputParam;
			}
			
			Map<String, Object> reqData = new HashMap<String, Object>();
			reqData.put(Dict.sub_merchant_id, subMerchantId);
			reqData.put(Dict.transfer_target_id, transferTargetId);
			
			String returnMsg = yLAliPayService.aliSdkTransfer(reqData, needData);
			
			JSONObject json = JSONObject.fromObject(returnMsg);
			String msg = StringUtil.toString(json.get(Dict.msg));
			String code = StringUtil.toString(json.get(Dict.code));
			String sub_msg = StringUtil.toString(json.get(Dict.sub_msg));
			String sub_code = StringUtil.toString(json.get(Dict.sub_code));

			if (!"Success".equals(msg) || !"10000".equals(code)) {
				logger.debug("[间联]支付宝存量商户迁移失败：" + sub_msg);
				outputParam.setReturnCode(StringConstans.returnCode.FAIL);
				outputParam.setReturnMsg("[间联]支付宝存量商户迁移失败:" + sub_msg);
				return outputParam;
			}
			
			InputParam updateInput = new InputParam();
			updateInput.putparamString(Dict.seqMerchant, seqMerchant);
			updateInput.putparamString(Dict.reserveType, StringConstans.RESERVE_TYPE.conversion);
			updateInput.putparamString(Dict.connectMethod, StringConstans.CONNECT_METHOD.indirect);
			merchantChannelService.updateSubmerChannelRateInfoByPrimaryKey(updateInput);
			
		} catch (Exception e){
			logger.error("[间联]支付宝存量商户迁移出现异常：" + e.getMessage(),e);
			outputParam.setReturnCode(StringConstans.returnCode.FAIL);
			outputParam.setReturnMsg("[间联]支付宝存量商户迁移出现异常:" + e.getMessage());
		} finally {
			logger.info("[间联]支付宝存量商户迁移返回报文:"+outputParam.toString());
		}
		return outputParam;
	}
	

	public AntMerchantExpandIndirectSummerchantService getAntMerchantExpandIndirectSummerchantService() {
		return antMerchantExpandIndirectSummerchantService;
	}

	public void setAntMerchantExpandIndirectSummerchantService(
			AntMerchantExpandIndirectSummerchantService antMerchantExpandIndirectSummerchantService) {
		this.antMerchantExpandIndirectSummerchantService = antMerchantExpandIndirectSummerchantService;
	}

	public IMerchantChannelService getMerchantChannelService() {
		return merchantChannelService;
	}

	public void setMerchantChannelService(IMerchantChannelService merchantChannelService) {
		this.merchantChannelService = merchantChannelService;
	}

	public ISequenceDao getSequenceDao() {
		return sequenceDao;
	}

	public void setSequenceDao(ISequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public YLAliPayService getyLAliPayService() {
		return yLAliPayService;
	}

	public void setyLAliPayService(YLAliPayService yLAliPayService) {
		this.yLAliPayService = yLAliPayService;
	}

}
