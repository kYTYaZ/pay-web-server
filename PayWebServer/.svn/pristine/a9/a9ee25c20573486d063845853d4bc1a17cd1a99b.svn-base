package com.unionpay.apay.test;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.unionpay.apay.AliPay;
import com.unionpay.apay.AliPayConstants;

public class AliPayTest {

	private AliPay alipay;
	private String out_trade_no = "tradeprecreate" + System.currentTimeMillis() + (long) (Math.random() * 10000000L);
	
	private String rstNotify = "gmt_create=2018-04-20+15%3A25%3A41&charset=UTF-8&seller_email=zfbtest25%40service.aliyun.com&subject=Iphone6+16G&sign=UzAU4ChotPtzi1fubz4Llw4Cfks1nFrHLGUwwFyLLXdJYzUeawSflWYJCSELkZb9sGaJT%2BXwedbaZ16bFvLGCE5F1ij270B3idjELbvJszTSEDMtmcigtIb9fC5o0G2K%2BuKT0xn9K%2FqSj1j9gTvRoID5s%2FFynsSJ5CSlJt8g%2FnNU68MAMBOrYi3EZuhd75eehZSMe7HQVx08Kx66eQP%2F7tUUbWS0Gs49BY2cvb17xiZ6vDMhY8nvKjuyWVoyhlvjmoIhP4CUw4zwrNqZnQJYa9Un%2FtqdVzxb1MCRV6XQShJsMQzSJl3lXdbA4k5TvX1SKEhyWZ5GKYKnV%2FXKzN%2F6%2Fw%3D%3D&buyer_id=2088012369756614&body=Iphone6+16G&invoice_amount=0.01&notify_id=fa37a3f7a30e982292c81f89a227e72kpi&fund_bill_list=%5B%7B%22amount%22%3A%220.01%22%2C%22fundChannel%22%3A%22ALIPAYACCOUNT%22%7D%5D&notify_type=trade_status_sync&trade_status=TRADE_SUCCESS&receipt_amount=0.01&app_id=2016080700188024&buyer_pay_amount=0.01&sign_type=RSA2&seller_id=2088911212416201&gmt_payment=2018-04-20+15%3A25%3A41&notify_time=2018-04-20+15%3A29%3A24&version=1.0&out_trade_no=tradeprecreate15242084564057922493&total_amount=0.01&trade_no=962018042021001004610236969008&auth_app_id=2015051100069170&buyer_logon_id=119***%40qq.com&point_amount=0.00";
	
	@Before
	public void init() {
		alipay = new AliPay();
	}
	
	public static void main(String[] args) {
		AliPayTest aliPayTest = new AliPayTest();
		aliPayTest.init();
		aliPayTest.doIndirectCreate();
	}
	
	// 商户入驻
	@Test
	public void doIndirectCreate() {
		System.out.println("商户入驻");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文 
		 */
		data.put("external_id", "10529005999019"); // 商户编号，由机构定义，需要保证在机构下唯一
		data.put("name", "TEST665"); // 商户名称
		data.put("alias_name", "xx公司"); // 商户简称
		data.put("service_phone", "95188"); // 商户客服电话
		data.put("category_id", "2015050700000000"); // 商户经营类目
		data.put("source", "2088102170346152"); // 商户来源机构标识，填写机构在支付宝的pid
		// 商户联系人信息
		List<Object> contactInfo = new LinkedList<Object>();
		Map<String, Object> infos = new LinkedHashMap<String, Object>();
		infos.put("name", "张三"); // 联系人姓名
		/**
		 * 商户联系人业务标识枚举，表示商户联系人的职责。 异议处理接口人:02;商户关键联系人:06;数据反馈接口人:11;服务联动接口人:08
		 */
		String[] tag = {"06"};
		infos.put("tag", JSONArray.fromObject(tag));
		infos.put("type", "LEGAL_PERSON"); // 联系人类型，取值范围：LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他
		// 非必填
		infos.put("phone", "0571-85022088"); // 电话
		infos.put("mobile", "13888888888"); // 手机
		infos.put("email", "user@domain.com"); // 电子邮箱
		infos.put("id_card_no", "110000199001011234"); // 身份证号
		contactInfo.add(infos);
		data.put("contact_info", JSONArray.fromObject(contactInfo));
		List<Object> addressInfo = new LinkedList<Object>();
		Map<String, Object> addinfos = new LinkedHashMap<String, Object>();
		addinfos.put("city_code", "371000"); // 城市编码，城市编码是与国家统计局一致
		addinfos.put("district_code", "371002"); // 区县编码，区县编码是与国家统计局一致，
		addinfos.put("address", "万塘路18号黄龙时代广场B座"); // 地址。商户详细经营地址或人员所在地点
		addinfos.put("province_code", "370000"); // 省份编码，省份编码是与国家统计局一致
		addinfos.put("longitude", "120.760001"); // 经度，浮点型, 小数点后最多保留6位。
		addinfos.put("latitude", "60.270001"); // 纬度，浮点型,小数点后最多保留6位如需要录入经纬度，请以高德坐标系为准，
		addinfos.put("type", "BUSINESS_ADDRESS"); // 地址类型。取值范围：BUSINESS_ADDRESS：经营地址（默认）
		addressInfo.add(addinfos);
		data.put("address_info", JSONArray.fromObject(addressInfo));
		data.put("business_license", "100000011234561"); // 商户证件编号（企业或者个体工商户提供营业执照，事业单位提供事证号）
		/**
		 * 商户证件类型，取值范围：NATIONAL_LEGAL：营业执照；
		 * NATIONAL_LEGAL_MERGE:营业执照(多证合一)；INST_RGST_CTF：事业单位法人证书
		 */
		//data.put("business_license_type", "NATIONAL_LEGAL");
		// 商户对应银行所开立的结算卡信息
		List<Object> bankcardInfo = new LinkedList<Object>();
		Map<String, Object> bankinfos = new LinkedHashMap<String, Object>();
		bankinfos.put("card_no", "6228480402637874213"); // 银行卡号
		bankinfos.put("card_name", "张三"); // 银行卡持卡人姓名
		bankcardInfo.add(bankinfos);
		data.put("bankcard_info", JSONArray.fromObject(bankcardInfo));
		String[] codeInfo = { "http://www.domain.com" };
		data.put("pay_code_info", JSONArray.fromObject(codeInfo)); // 商户的支付二维码中信息，用于营销活动
		String[] logonInfo = {"user@domain.com"};
		data.put("logon_id", JSONArray.fromObject(logonInfo)); //商户的支付宝账号
		data.put("memo", "备注信息"); //商户备注信息，可填写额外信息
		try {
			alipay.indirectCreate(data); // 商户入驻
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//商户信息查询
	@Test
	public void doIndirectQuery() {
		System.out.println("商户信息查询");
		Map<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		data.put("sub_merchant_id", "2088031855065735"); //商户在支付宝入驻成功后，生成的支付宝内全局唯一的商户编号，与external_id二选一必传
//		data.put("external_id", "105290059990194"); //商户编号，由机构定义，需要保证在机构下唯一，与sub_merchant_id二选一
		try {
			alipay.indirectQuery(data); // 商户信息查询
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//商户信息修改
	@Test
	public void doIndirectModify() {
		System.out.println("商户信息修改");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		data.put("sub_merchant_id", "2088031855065735"); // 商户在支付宝入驻成功后，生成的支付宝内全局唯一的商户编号，与external_id二选一必传
		//data.put("external_id", "xxx"); // 商户编号，由机构定义，需要保证在机构下唯一，与sub_merchant_id二选一
		data.put("name","测试"); //商户名称
		data.put("alias_name","test"); //商户简称
		data.put("source","2088911212416201"); //商户来源机构标识，填写机构在支付宝的pid
		data.put("service_phone","95188"); //商户客服电话
		data.put("category_id","2015050700000000"); //商户经营类目
		//非必填
		data.put("business_license","100000011234561"); //商户证件编号（企业或者个体工商户提供营业执照，事业单位提供事证号）
		/**
		 * 商户证件类型，取值范围：NATIONAL_LEGAL：营业执照；
		 * NATIONAL_LEGAL_MERGE:营业执照(多证合一)；INST_RGST_CTF：事业单位法人证书
		 */
		data.put("business_license_type","NATIONAL_LEGAL");
		List<Object> addressInfo = new LinkedList<Object>();
		Map<String, Object> addinfos = new LinkedHashMap<String, Object>();
		addinfos.put("city_code", "371000"); // 城市编码，城市编码是与国家统计局一致
		addinfos.put("district_code", "371002"); // 区县编码，区县编码是与国家统计局一致，
		addinfos.put("address", "万塘路18号黄龙时代广场B座"); // 地址。商户详细经营地址或人员所在地点
		addinfos.put("province_code", "370000"); // 省份编码，省份编码是与国家统计局一致
		addinfos.put("longitude", "120.760001"); // 经度，浮点型, 小数点后最多保留6位。
		addinfos.put("latitude", "60.270001"); // 纬度，浮点型,小数点后最多保留6位如需要录入经纬度，请以高德坐标系为准，
		addinfos.put("type", "BUSINESS_ADDRESS"); // 地址类型。取值范围：BUSINESS_ADDRESS：经营地址（默认）
		addressInfo.add(addinfos);
		data.put("address_info", JSONArray.fromObject(addressInfo));
		// 商户联系人信息
		List<Object> contactInfo = new LinkedList<Object>();
		Map<String, Object> infos = new LinkedHashMap<String, Object>();
		infos.put("name", "张三"); // 联系人姓名
		/**
		 * 商户联系人业务标识枚举，表示商户联系人的职责。 异议处理接口人:02;商户关键联系人:06;数据反馈接口人:11;服务联动接口人:08
		 */
		String[] tag = {"06"};
		infos.put("tag", JSONArray.fromObject(tag));
		infos.put("type", "LEGAL_PERSON"); // 联系人类型，取值范围：LEGAL_PERSON：法人；CONTROLLER：实际控制人；AGENT：代理人；OTHER：其他
		// 非必填
		infos.put("phone", "0571-85022088"); // 电话
		infos.put("mobile", "13888888888"); // 手机
		infos.put("email", "user@domain.com"); // 电子邮箱
		infos.put("id_card_no", "110000199001011234"); // 身份证号
		contactInfo.add(infos);
		data.put("contact_info", JSONArray.fromObject(contactInfo));
		List<Object> bankcardInfo = new LinkedList<Object>();
		Map<String, Object> bankinfos = new LinkedHashMap<String, Object>();
		bankinfos.put("card_no", "6228480402637874213"); //银行卡号
		bankinfos.put("card_name", "张三"); //银行卡持卡人姓名
		bankcardInfo.add(bankinfos);
		data.put("bankcard_info", JSONArray.fromObject(bankcardInfo));
		data.put("pay_code_info", ""); //商户的支付二维码中信息，用于营销活动
		data.put("logon_id", ""); //商户的支付宝账号
		data.put("memo", "备注信息"); //商户备注信息，可填写额外信息
		try {
			alipay.indirectModify(data); // 商户信息修改
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 统一下单
	@Test
	public void doTradeCreate() {
		System.out.println("统一下单");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		// 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
		data.put("out_trade_no", out_trade_no);
		System.out.println(out_trade_no);
		Map<String, Object> SubMerchant = new HashMap<String, Object>();
		SubMerchant.put("merchant_id", "2088031855065735");
		// SubMerchant.put("merchant_type", "alipay");
		data.put("sub_merchant", SubMerchant);
		data.put("subject", "Iphone6 16G"); // 订单标题
		/**
		 * 订单总金额 (Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		 * 如果同时传入【可打折金额】和【不可打折金额】，该参数可以不用传入；
		 * 如果同时传入了【可打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件： 【订单总金额】=【可打折金额】+【不可打折金额】
		 */
		data.put("total_amount", 1);
		data.put("body", "Iphone6 16G"); // 订单描述
		data.put("buyer_logon_id", "15800923275"); // 买家的支付宝唯一用户号（2088开头的16位纯数字）,和buyer_logon_id不能同时为空
		// 非必填
		data.put("seller_id", ""); // 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
		/**
		 * 参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
		 * 如果该值未传入，但传入了【订单总金额】和【不可打折金额】，则该值默认为【订单总金额】-【不可打折金额】
		 */
		// data.put("discountable_amount", 8.88);
		/**
		 * 不参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
		 * 如果该值未传入，但传入了【订单总金额】和【可打折金额】，则该值默认为【订单总金额】-【可打折金额】
		 */
		// data.put("undiscountable_amount", 80.00);
		List<Object> goodsDetails = new LinkedList<Object>();
		Map<String, Object> goodsDetail = new LinkedHashMap<String, Object>();
		goodsDetail.put("goods_id", "apple-01"); // 商品编码由半角的大小写字母、数字、中划线、下划线中的一种或几种组成
		goodsDetail.put("goods_name", "ipad"); // 商品名称
		goodsDetail.put("quantity", 1); // 商品数量
		// 非必填
		goodsDetail.put("alipay_goods_id", "20010001"); // 支付宝定义的统一商品编号
		goodsDetail.put("price", 0.01); // 商品单价，单位为元
		goodsDetail.put("goods_category", "34543238"); // 商品类目
		goodsDetail.put("body", "特价手机"); // 商品描述信息
		goodsDetail.put("show_url", "http://www.alipay.co m/xxx.jpg"); // 商品的展示地址
		goodsDetails.add(goodsDetail);
		data.put("goods_detail", JSONArray.fromObject(goodsDetails)); // 订单包含的商品列表信息.Json格式.

		data.put("operator_id", "yx_001"); // 商户操作员编号
		/**
		 * 可用渠道，用户只能在指定渠道范围内支付当有多个渠道时用“,”分隔注，与disable_pay_channels互斥
		 */
		// data.put("enable_pay_channels", "pcredit,moneyFund,d
		// ebitCardExpress");
		data.put("store_id", "NJ_001"); // 商户门店编号
		/**
		 * 禁用渠道，用户不可用指定渠道支付当有多个渠道时用“,”分隔注，与enable_pay_channels互斥
		 * 渠道列表：https://docs.open.alipay.c om/common/wifww7
		 */
		// data.put("disable_pay_channels", "pcredit,moneyFund,d
		// ebitCardExpress");
		data.put("terminal_id", "NJ_T_001"); // 商户机具终端编号
		Map<String, Object> extendParams = new LinkedHashMap<String, Object>();
		extendParams.put("sys_service_provider_id", "2088511833207846");
		// 系统商编号该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
		extendParams.put("hb_fq_num", ""); // 使用花呗分期要进行的分期数
		extendParams.put("hb_fq_seller_percent", ""); // 使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
		extendParams.put("industry_reflux_info", ""); // 行业数据回流信息,
		// 详见：地铁支付接口参数补充说明
		extendParams.put("card_type", "S0JP0000"); // 卡类型
		data.put("extend_params", JSONObject.fromObject(extendParams)); // 业务扩展参数
		/**
		 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
		 * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，
		 * 如1.5h，可转换为 90m。
		 */
		data.put("timeout_express", "90m");
		data.put("alipay_store_id", ""); // 支付宝店铺的门店ID
		data.put("merchant_order_no", ""); // 商户的原始订单号
		
		// 外部指定买家
		Map<String, Object> extUserInfo = new LinkedHashMap<String, Object>();
		extUserInfo.put("name", "李明"); // 姓名 注： need_check_info=T时该参数才有效
		extUserInfo.put("mobile", "16587658765"); // 手机号注：该参数暂不校验
		/**
		 * 身份证：IDENTITY_CARD、 护照：PASSPORT、 军官证：OFFICER_CARD、 士兵证：SOLDIER_CARD、
		 * 户口本：HOKOU等。 如有其它类型需要支持，请与蚂蚁金服工作人员联系。注： need_check_info=T时该参数才有效
		 */
		extUserInfo.put("cert_type", "IDENTITY_CARD");
		extUserInfo.put("cert_no", "362334768769238881"); // 证件号   注：need_check_info=T时该参数才有效
		/**
		 * 允许的最小买家年龄，买家年龄必须大于等于所传数值注： 1. need_check_info=T时该参数才有效 2.
		 * min_age为整数，必须大于等于0
		 */
		extUserInfo.put("min_age", "18");
		extUserInfo.put("fix_buyer", "F"); // 是否强制校验付款人身份信息 T:强制校验，F：不强制
		extUserInfo.put("need_check_info", "F"); // 是否强制校验身份信息 T:强制校验，F：不强制
		data.put("ext_user_info", JSONObject.fromObject(extUserInfo)); // 二级商户信息,当前只对特殊银行机构特定场景下使用此字段

//		 //描述分账信息，Json格式，其它说明详见分账说明
//		 Map<String, Object> royaltyInfo = new LinkedHashMap<String,Object>();
//		 royaltyInfo.put("royalty_type", "ROYALTY"); // 分账类型卖家的分账类型，目前只支持传入ROYALTY（普通分账类型）。
//		 Map<String, Object> RoyaltyDetailInfos = new LinkedHashMap<String,Object>();
//		 RoyaltyDetailInfos.put("serial_no", 1); // 分账序列号，表示分账执行的顺序，必须为正整数
//		 /**
//		 * 接受分账金额的账户类型：userId：支付宝账号对应的支付宝唯一用户号。
//		 * bankIndex：分账到银行账户的银行编号。目前暂时只支持分账到一个银行编号。 storeId：分账到门店对应的银行卡编号。
//		 * 默认值为userId。
//		 */
//		 RoyaltyDetailInfos.put("trans_in_type", "userId");
//		 RoyaltyDetailInfos.put("batch_no", "123"); // 分账批次号分账批次号。目前需要和转入账号类型为
//		 // bankIndex配合使用。
//		 RoyaltyDetailInfos.put("out_relation_id", "20131124001"); //商户分账的外部关联号，用于关联到每一笔分账信息，商户需保证其唯一性。如果为空，该值则默认为“商户网站唯一订单号+分账序列号”
//		 RoyaltyDetailInfos.put("trans_out_type", "userId"); //要分账的账户类型目前只支持userId：支付宝账号对应的支付宝唯一用户号默认值为userId。
//		 RoyaltyDetailInfos.put("trans_out", "2088101126765726"); //如果转出账号类型为userId，本参数为要分账的支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。
//		 /**
//		 * 如果转入账号类型为userId，本参数为接受分账金额的支付宝账号对应的支付宝唯一用户号。 
//		 * 以2088开头的纯16位数字。如果转入账号类型为bankIndex，
//		 * 本参数为28位的银行编号（商户和支付宝签约时确定）。 如果转入账号类型为storeId，本参数为商户的门店ID。
//		 */
//		 RoyaltyDetailInfos.put("trans_in", "2088101126708402");
//		 RoyaltyDetailInfos.put("amount", 0.1); // 分账的金额，单位为元
//		 RoyaltyDetailInfos.put("desc", "分账测试1"); // 分账描述信息
//		 RoyaltyDetailInfos.put("amount_percentage", "100"); //分账的比例，值为20代表按20%的比例分账
//		 royaltyInfo.put("royalty_detail_infos",
//		 JSONArray.fromObject(RoyaltyDetailInfos)); //分账明细的信息，可以描述多条分账指令，json数组。
//		 data.put("royalty_info", JSONObject.fromObject(royaltyInfo));
		
		// 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
		// data.put("business_params", ""); //
		// 商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
		try {
			alipay.tradeCreate(data); // 统一下单
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 扫码支付
	@Test
	public void doTradePrecreate() {
		System.out.println("扫码支付");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		// 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
		data.put("out_trade_no", out_trade_no);
		/**
		 * 订单总金额 (Price).单位为元，精确到小数点后两位， 取值范围[0.01,100000000]
		 * 如果同时传入了【打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件： 【订单总金额】=【打折金额】+【不可打折金额】
		 */
		data.put("total_amount", 0.01);
		data.put("subject", "Iphone6 16G"); // 订单标题
		Map<String, Object> SubMerchant = new HashMap<String, Object>();
		SubMerchant.put("merchant_id", "2088031855065735");
		SubMerchant.put("merchant_type", "alipay");
		data.put("sub_merchant", SubMerchant);
		data.put("alipay_store_id", ""); // 支付宝店铺的门店ID
		// 非必填
		data.put("seller_id", ""); // 卖家支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
		/**
		 * 可打折金额 (Price). 参与优惠计算的金额，单位为元，精确到小数点后两位， 取值范围[0.01,100000000]
		 * 如果该值未传入，但传入了【订单总金额】，【不可打折金额】则该值默认为【订单总金额】-【不可打折金额】
		 */
		// data.put("discountable_amount", "8.88");
		/**
		 * 不可打折金额 (Price). 不参与优惠计算的金额，单位为元，精确到小数点后两位， 取值范围[0.01,100000000]
		 * 如果该值未传入，但传入了【订单总金额】,【打折金额】，则该值默认为【订单总金额】-【打折金额】
		 */
		// data.put("undiscountable_amount", "80");
		data.put("buyer_logon_id", "15800923275"); // 买家支付宝账号
		List<Object> goodsDetails = new LinkedList<Object>();
		Map<String, Object> goodsDetail = new LinkedHashMap<String, Object>();
		goodsDetail.put("goods_id", "apple-01"); // 商品编码
		goodsDetail.put("goods_name", "ipad"); // 商品名称
		goodsDetail.put("quantity", 1); // 商品数量
		// 非必填
		goodsDetail.put("alipay_goods_id", "20010001"); // 支付宝定义的统一商品编号
		goodsDetail.put("price", 2000); // 商品单价，单位为元
		goodsDetail.put("goods_category", "34543238"); // 商品类目
		goodsDetail.put("body", "特价手机"); // 商品描述信息
		goodsDetail.put("show_url", "http://www.alipay.co m/xxx.jpg"); // 商品的展示地址
		goodsDetails.add(goodsDetail);
		data.put("goods_detail", JSONArray.fromObject(goodsDetails)); // 订单包含的商品列表信息.Json格式.
		data.put("body", "Iphone6 16G"); // 对交易或商品的描述
		data.put("operator_id", "yx_001"); // 商户操作员编号
		data.put("store_id", "NJ_001"); // 商户门店编号
		/**
		 * 禁用渠道，用户不可用指定渠道支付当有多个渠道时用“,”分隔注，与enable_pay_channels互斥
		 * 渠道列表：https://docs.open.alipay.c om/common/wifww7
		 */
		// data.put("disable_pay_channels", "pcredit,moneyFund,d
		// ebitCardExpress");
		/**
		 * 可用渠道，用户只能在指定渠道范围内支付当有多个渠道时用“,”分隔注，与disable_pay_channels互斥
		 */
		// data.put("enable_pay_channels", "pcredit,moneyFund,d
		// ebitCardExpress");
		data.put("terminal_id", "NJ_T_001"); // 商户机具终端编号
		Map<String, Object> extendParams = new LinkedHashMap<String, Object>();
		extendParams.put("sys_service_provider_id", "2088511833207846");
		// 系统商编号该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
		extendParams.put("hb_fq_num", ""); // 使用花呗分期要进行的分期数
		extendParams.put("hb_fq_seller_percent", ""); // 使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
		extendParams.put("industry_reflux_info", ""); // 行业数据回流信息,
		// 详见：地铁支付接口参数补充说明
		extendParams.put("card_type", "S0JP0000"); // 卡类型
		data.put("extend_params", JSONObject.fromObject(extendParams)); // 业务扩展参数
		/**
		 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
		 * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，
		 * 如1.5h，可转换为 90m。
		 */
		data.put("timeout_express", "90m");
		
		// 外部指定买家
		Map<String, Object> extUserInfo = new LinkedHashMap<String, Object>();
		extUserInfo.put("name", "李明"); // 姓名 注： need_check_info=T时该参数才有效
		extUserInfo.put("mobile", "16587658765"); // 手机号注：该参数暂不校验
		/**
		 * 身份证：IDENTITY_CARD、 护照：PASSPORT、 军官证：OFFICER_CARD、 士兵证：SOLDIER_CARD、
		 * 户口本：HOKOU等。 如有其它类型需要支持，请与蚂蚁金服工作人员联系。注： need_check_info=T时该参数才有效
		 */
		extUserInfo.put("cert_type", "IDENTITY_CARD");
		extUserInfo.put("cert_no", "362334768769238881"); // 证件号   注：need_check_info=T时该参数才有效
		/**
		 * 允许的最小买家年龄，买家年龄必须大于等于所传数值注： 1. need_check_info=T时该参数才有效 2.
		 * min_age为整数，必须大于等于0
		 */
		extUserInfo.put("min_age", "18");
		extUserInfo.put("fix_buyer", "F"); // 是否强制校验付款人身份信息 T:强制校验，F：不强制
		extUserInfo.put("need_check_info", "F"); // 是否强制校验身份信息 T:强制校验，F：不强制
		data.put("ext_user_info", JSONObject.fromObject(extUserInfo)); // 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
		// 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
		// data.put("business_params", "{\"data\":\"123\"}"); //
		// 商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
		/**
		 * 该笔订单允许的最晚付款时间，逾期将关闭交易，从生成二维码开始计时。
		 * 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
		 * 该参数数值不接受小数点， 如1.5h，可转换为 90m。
		 */
		data.put("qr_code_timeout_express", "90m");
		
//		 //描述分账信息，Json格式，其它说明详见分账说明
//		 Map<String, Object> royaltyInfo = new LinkedHashMap<String,Object>();
//		 royaltyInfo.put("royalty_type", "ROYALTY"); // 分账类型卖家的分账类型，目前只支持传入ROYALTY（普通分账类型）。
//		 Map<String, Object> RoyaltyDetailInfos = new LinkedHashMap<String,Object>();
//		 RoyaltyDetailInfos.put("serial_no", 1); // 分账序列号，表示分账执行的顺序，必须为正整数
//		 /**
//		 * 接受分账金额的账户类型：userId：支付宝账号对应的支付宝唯一用户号。
//		 * bankIndex：分账到银行账户的银行编号。目前暂时只支持分账到一个银行编号。 storeId：分账到门店对应的银行卡编号。
//		 * 默认值为userId。
//		 */
//		 RoyaltyDetailInfos.put("trans_in_type", "userId");
//		 RoyaltyDetailInfos.put("batch_no", "123"); // 分账批次号分账批次号。目前需要和转入账号类型为
//		 // bankIndex配合使用。
//		 RoyaltyDetailInfos.put("out_relation_id", "20131124001"); //商户分账的外部关联号，用于关联到每一笔分账信息，商户需保证其唯一性。如果为空，该值则默认为“商户网站唯一订单号+分账序列号”
//		 RoyaltyDetailInfos.put("trans_out_type", "userId"); //要分账的账户类型目前只支持userId：支付宝账号对应的支付宝唯一用户号默认值为userId。
//		 RoyaltyDetailInfos.put("trans_out", "2088101126765726"); //如果转出账号类型为userId，本参数为要分账的支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。
//		 /**
//		 * 如果转入账号类型为userId，本参数为接受分账金额的支付宝账号对应的支付宝唯一用户号。 
//		 * 以2088开头的纯16位数字。如果转入账号类型为bankIndex，
//		 * 本参数为28位的银行编号（商户和支付宝签约时确定）。 如果转入账号类型为storeId，本参数为商户的门店ID。
//		 */
//		 RoyaltyDetailInfos.put("trans_in", "2088101126708402");
//		 RoyaltyDetailInfos.put("amount", 0.1); // 分账的金额，单位为元
//		 RoyaltyDetailInfos.put("desc", "分账测试1"); // 分账描述信息
//		 RoyaltyDetailInfos.put("amount_percentage", "100"); //分账的比例，值为20代表按20%的比例分账
//		 royaltyInfo.put("royalty_detail_infos",
//		 JSONArray.fromObject(RoyaltyDetailInfos)); //分账明细的信息，可以描述多条分账指令，json数组。
//		 data.put("royalty_info", JSONObject.fromObject(royaltyInfo));
		try {
			alipay.tradePrecreate(data); // 扫码支付
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 条码支付
	@Test
	public void doTradePay() {
		System.out.println("条码支付");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		// 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
		data.put("out_trade_no", out_trade_no);
		data.put("scene", "bar_code"); // 支付场景 条码支付，取值：bar_code  声波支付，取值：wave_code
		data.put("auth_code", "288186539097317031"); // 支付授权码，25~30开头的长度为16~24位的数字，实际字符串长度以开发者获取的付款码长度为准
		data.put("subject", "Iphone6 16G"); // 订单标题
		Map<String, Object> SubMerchant = new HashMap<String, Object>();
		SubMerchant.put("merchant_id", "2088031855065735");
		SubMerchant.put("merchant_type", "alipay");
		data.put("sub_merchant", SubMerchant);
		// 非必填
		// data.put("product_code", "FACE_TO_FACE_PAYM ENT"); //销售产品码
		data.put("buyer_id", "");// 买家的支付宝用户id，如果为空，会从传入了码值信息中获取买家ID
		data.put("seller_id", ""); // 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
		/**
		 * 订单总金额 (Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
		 * 如果同时传入【可打折金额】和【不可打折金额】，该参数可以不用传入；
		 * 如果同时传入了【可打折金额】，【不可打折金额】，【订单总金额】三者，则必须满足如下条件： 【订单总金额】=【可打折金额】+【不可打折金额】
		 */
		data.put("total_amount", 0.01);
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
		// data.put("discountable_amount", "8.88");
		/**
		 * 不参与优惠计算的金额(Price)，单位为元，精确到小数点后两位，取值范围[0.01,100000000]。
		 * 如果该值未传入，但传入了【订单总金额】和【可打折金额】，则该值默认为【订单总金额】-【可打折金额】
		 */
		// data.put("undiscountable_amount", "80.00");
		data.put("body", "Iphone6 16G"); // 订单描述
		List<Object> goodsDetails = new LinkedList<Object>();
		Map<String, Object> goodsDetail = new LinkedHashMap<String, Object>();
		goodsDetail.put("goods_id", "apple-01"); // 商品编码
		goodsDetail.put("goods_name", "ipad"); // 商品名称
		goodsDetail.put("quantity", 1); // 商品数量
		// 非必填
		goodsDetail.put("alipay_goods_id", "20010001"); // 支付宝定义的统一商品编号
		goodsDetail.put("price", 0.01); // 商品单价，单位为元
		goodsDetail.put("goods_category", "34543238"); // 商品类目
		goodsDetail.put("body", "特价手机"); // 商品描述信息
		goodsDetail.put("show_url", "http://www.alipay.co m/xxx.jpg"); // 商品的展示地址
		goodsDetails.add(goodsDetail);
		data.put("goods_detail", JSONArray.fromObject(goodsDetails)); // 订单包含的商品列表信息.Json格式.
		data.put("operator_id", "yx_001"); // 商户操作员编号
		data.put("store_id", "NJ_001"); // 商户门店编号
		data.put("terminal_id", "NJ_T_001"); // 商户机具终端编号
		data.put("alipay_store_id", ""); // 支付宝店铺的门店ID
		/**
		 * 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：
		 * 1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，
		 * 如1.5h，可转换为 90m。
		 */
		data.put("timeout_express", "90m");

		Map<String, Object> extendParams = new LinkedHashMap<String, Object>();
		extendParams.put("sys_service_provider_id", ""); // 系统商编号该参数作为系统商返佣数据提取的依据，请填写系统商签约协议的PID
		extendParams.put("hb_fq_num", ""); // 使用花呗分期要进行的分期数
		extendParams.put("hb_fq_seller_percent", ""); // 使用花呗分期需要卖家承担的手续费比例的百分值，传入100代表100%
		extendParams.put("industry_reflux_info", ""); // 行业数据回流信息,
		extendParams.put("card_type", ""); // 卡类型
		data.put("extend_params", JSONObject.fromObject(extendParams)); // 业务扩展参数

		// 代扣业务需要传入协议相关信息
		Map<String, Object> agreementParams = new LinkedHashMap<String, Object>();
		agreementParams.put("agreement_no", ""); // 支付宝系统中用以唯一标识用户签约记录的编号（用户签约成功后的协议号
													// ）
		agreementParams.put("auth_confirm_no", ""); // 鉴权确认码，在需要做支付鉴权校验时，该参数不能为空
		agreementParams.put("apply_token", ""); // 鉴权申请token，其格式和内容，由支付宝定义。在需要做支付鉴权校验时，该参数不能为空。
		data.put("agreement_params", JSONObject.fromObject(agreementParams)); // 代扣业务需要传入协议相关信息

		/**
		 * 禁用支付渠道,多个渠道以逗号分割，如同时禁用信用支付类型和积分， 则disable_pay_channels="cre
		 * dit_group,point"
		 */
		// data.put("disable_pay_channels", "credit_group");
		// data.put("merchant_order_no", "201008123456789"); //商户的原始订单号
		/**
		 * 预授权号，预授权转交易请求中传入，适用于预授权转交易业务使用， 目前只支持FUND_TRADE_FAST_PAY（资金订单即时到帐交易）、
		 * 境外预授权产品（OVERSEAS_AUTH_PAY）两个产品。
		 */
		// data.put("auth_no", "2016110310002001760201905725");
		// 外部指定买家
		Map<String, Object> extUserInfo = new LinkedHashMap<String, Object>();
		extUserInfo.put("name", "李明"); // 姓名 注： need_check_info=T时该参数才有效
		extUserInfo.put("mobile", "16587658765"); // 手机号注：该参数暂不校验
		/**
		 * 身份证：IDENTITY_CARD、 护照：PASSPORT、 军官证：OFFICER_CARD、 士兵证：SOLDIER_CARD、
		 * 户口本：HOKOU等。 如有其它类型需要支持，请与蚂蚁金服工作人员联系。注： need_check_info=T时该参数才有效
		 */
		extUserInfo.put("cert_type", "IDENTITY_CARD");
		extUserInfo.put("cert_no", "362334768769238881"); // 证件号
															// 注：need_check_info=T时该参数才有效
		/**
		 * 允许的最小买家年龄，买家年龄必须大于等于所传数值注： 1. need_check_info=T时该参数才有效 2.
		 * min_age为整数，必须大于等于0
		 */
		extUserInfo.put("min_age", "18");
		extUserInfo.put("fix_buyer", "F"); // 是否强制校验付款人身份信息 T:强制校验，F：不强制
		extUserInfo.put("need_check_info", "F"); // 是否强制校验身份信息 T:强制校验，F：不强制
		data.put("ext_user_info", JSONObject.fromObject(extUserInfo)); // 二级商户信息,当前只对特殊银行机构特定场景下使用此字段
//		 //描述分账信息，Json格式，其它说明详见分账说明
//		 Map<String, Object> royaltyInfo = new LinkedHashMap<String,Object>();
//		 royaltyInfo.put("royalty_type", "ROYALTY"); // 分账类型卖家的分账类型，目前只支持传入ROYALTY（普通分账类型）。
//		 Map<String, Object> RoyaltyDetailInfos = new LinkedHashMap<String,Object>();
//		 RoyaltyDetailInfos.put("serial_no", 1); // 分账序列号，表示分账执行的顺序，必须为正整数
//		 /**
//		 * 接受分账金额的账户类型：userId：支付宝账号对应的支付宝唯一用户号。
//		 * bankIndex：分账到银行账户的银行编号。目前暂时只支持分账到一个银行编号。 storeId：分账到门店对应的银行卡编号。
//		 * 默认值为userId。
//		 */
//		 RoyaltyDetailInfos.put("trans_in_type", "userId");
//		 RoyaltyDetailInfos.put("batch_no", "123"); // 分账批次号分账批次号。目前需要和转入账号类型为
//		 // bankIndex配合使用。
//		 RoyaltyDetailInfos.put("out_relation_id", "20131124001"); //商户分账的外部关联号，用于关联到每一笔分账信息，商户需保证其唯一性。如果为空，该值则默认为“商户网站唯一订单号+分账序列号”
//		 RoyaltyDetailInfos.put("trans_out_type", "userId"); //要分账的账户类型目前只支持userId：支付宝账号对应的支付宝唯一用户号默认值为userId。
//		 RoyaltyDetailInfos.put("trans_out", "2088101126765726"); //如果转出账号类型为userId，本参数为要分账的支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字。
//		 /**
//		 * 如果转入账号类型为userId，本参数为接受分账金额的支付宝账号对应的支付宝唯一用户号。 
//		 * 以2088开头的纯16位数字。如果转入账号类型为bankIndex，
//		 * 本参数为28位的银行编号（商户和支付宝签约时确定）。 如果转入账号类型为storeId，本参数为商户的门店ID。
//		 */
//		 RoyaltyDetailInfos.put("trans_in", "2088101126708402");
//		 RoyaltyDetailInfos.put("amount", 0.1); // 分账的金额，单位为元
//		 RoyaltyDetailInfos.put("desc", "分账测试1"); // 分账描述信息
//		 RoyaltyDetailInfos.put("amount_percentage", "100"); //分账的比例，值为20代表按20%的比例分账
//		 royaltyInfo.put("royalty_detail_infos",
//		 JSONArray.fromObject(RoyaltyDetailInfos)); //分账明细的信息，可以描述多条分账指令，json数组。
//		 data.put("royalty_info", JSONObject.fromObject(royaltyInfo));
		/**
		 * 预授权确认模式，授权转交易请求中传入，适用于预授权转交易业务使用， 目前只支持
		 * PRE_AUTH(预授权产品码)COMPLETE：转交易支付完成结束预授权，解冻剩余金额;
		 * NOT_COMPLETE：转交易支付完成不结束预授权，不解冻剩余金额
		 */
		// data.put("auth_confirm_mode", "COMPLETE");
		// json格式；商户传入终端设备相关信息，具体值要和支付宝约定
		// data.put("terminal_params",
		// "{\"credential\":\"28763443825664394:20180207192030954:abcdefGH
		// IJKLMN\",\"signature\":\" xxxxxxx\",\"terminalType\":\"IOT\"}");
		// data.put("business_params", "{\"data\":\"123\"}"); //
		// 商户传入业务信息，具体值要和支付宝约定，应用于安全，营销等参数直传场景，格式为json格式
		try {
			alipay.tradePay(data); // 条码支付
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 统一收单交易撤销
	@Test
	public void doTradeCancel() {
		System.out.println("统一收单交易撤销");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		data.put("out_trade_no", "tradeprecreate15242084564057922493"); // 原支付请求的商户订单号,和支付宝交易号不能同时为空
		// data.put("trade_no", "2014112611001004680073956707"); //
		// 支付宝交易号，和商户订单号不能同时为空
		try {
			alipay.tradeCancle(data); // 统一收单交易撤销
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 统一收单交易退款查询
	@Test
	public void doTradeRefundQuery() {
		System.out.println("统一收单交易退款查询");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		data.put("out_request_no", "2014112611001004680073956707"); // 必填 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
		// 非必填
		data.put("out_trade_no", "tradeprecreate15235821242387941050"); // 原支付请求的商户订单号,和支付宝交易号不能同时为空
		//data.put("trade_no", "2014112611001004680073956707"); // 支付宝交易号，和商户订单号不能同时为空
		try {
			alipay.tradeRefundQuery(data); // 统一收单交易退款查询
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 统一收单交易退款接口
	@Test
	public void doTradeRefund() {
		System.out.println("统一收单交易退款接口");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		data.put("out_request_no", "2014112611001004680073956707"); // 请求退款接口时，传入的退款请求号，如果在退款请求时未传入，则该值为创建交易时的外部交易号
		data.put("refund_amount", 0.01); // 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
		// 非必填
		data.put("out_trade_no", "tradeprecreate15235821242387941050"); // 原支付请求的商户订单号,和支付宝交易号不能同时为空
		//data.put("trade_no", "2014112611001004680073956707"); // 支付宝交易号，和商户订单号不能同时为空

		data.put("refund_currency", "CNY"); // 订单退款币种信息
		data.put("refund_reason", "正常退款"); // 退款的原因说明
		data.put("out_request_no", "HZ01RF001"); // 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
		data.put("operator_id", "yx_001"); // 商户操作员编号
		data.put("store_id", "NJ_001"); // 商户门店编号
		data.put("terminal_id", "NJ_T_001"); // 商户机具终端编号
		
		List<Object> goodsDetails = new LinkedList<Object>();
		Map<String, Object> goodsDetail = new LinkedHashMap<String, Object>();
		goodsDetail.put("goods_id", "apple-01"); // 商品编码
		goodsDetail.put("goods_name", "ipad"); // 商品名称
		goodsDetail.put("quantity", 1); // 商品数量
		// 非必填
		goodsDetail.put("alipay_goods_id", "20010001"); // 支付宝定义的统一商品编号
		goodsDetail.put("price", 2000); // 商品单价，单位为元
		goodsDetail.put("goods_category", "34543238"); // 商品类目
		goodsDetail.put("body", "特价手机"); // 商品描述信息
		goodsDetail.put("show_url", "http://www.alipay.co m/xxx.jpg"); // 商品的展示地址
		goodsDetails.add(goodsDetail);
		data.put("goods_detail", JSONArray.fromObject(goodsDetails)); // 订单包含的商品列表信息.Json格式.
		try {
			alipay.tradeRefund(data); // 统一收单交易退款接口
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 统一收单线下交易查询
	@Test
	public void doTradeQuery() {
		System.out.println("统一收单线下交易查询");
		HashMap<String, Object> data = new HashMap<String, Object>();
		/**
		 * 组装请求报文
		 */
		data.put("out_trade_no", "tradeprecreate15235821242387941050"); // 订单支付时传入的商户订单号,和支付宝交易号不能同时为空。trade_no,out_trade_no如果同时存在优先取trade_no
		//data.put("trade_no", "2014112611001004680073956707"); // 支付宝交易号，和商户订单号不能同时为空
		try {
			alipay.tradeQuery(data); // 统一收单线下交易查询
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//支付结果通知
	@Test
	public void doParseNotifyInfo() throws Exception{
		System.out.println("扫码支付结果通知");
		String str = URLDecoder.decode(rstNotify,AliPayConstants.CHARSET_UTF_8);
		alipay.processNotifyResponse(str);
	}
	
}
