package com.huateng.pay.handler.services;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.frame.common.date.DateUtil;
import com.huateng.frame.common.json.JsonUtil;
import com.huateng.frame.exception.FrameException;
import com.huateng.frame.param.InputParam;
import com.huateng.frame.param.OutputParam;
import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.Constants;
import com.huateng.pay.common.util.StringUtil;
import com.huateng.pay.listener.QRCodeSocketServerListener;
import com.huateng.pay.po.local.SocketRequest;
import com.huateng.pay.services.alipay.AliPayMerchantSynchService;
import com.huateng.pay.services.alipay.AliPayPayService;
import com.huateng.pay.services.cups.ICupsPayService;
import com.huateng.pay.services.db.ITimingTaskService;
import com.huateng.pay.services.local.ILocalBankService;
import com.huateng.pay.services.statics.IStaticQRCodeService;
import com.huateng.pay.services.statics.IThreeCodeStaticQRCodeService;
import com.huateng.pay.services.threecode.IThreeCodeService;
import com.huateng.pay.services.weixin.WxMerchantSynchService;
import com.huateng.pay.services.weixin.WxPayService;
import com.huateng.utils.ProtocolCodeFilter;
import com.huateng.utils.Util;
import com.wldk.framework.utils.MappingUtils;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * 微信下载对账单、同步微信商户
 * 
 * @author guohuan
 *
 */
public class QRCodeSocketManager implements Runnable {
	private static final String charset = "UTF-8";
	private Logger logger = LoggerFactory.getLogger(QRCodeSocketManager.class);
	private WxPayService wxPayService;
	private ITimingTaskService timingTaskService;
	private ILocalBankService localBankService;
	private WxMerchantSynchService wxMerchantSynchService;
	private ChannelHandlerContext ctx;
	private String reviceMsg;
	private AliPayMerchantSynchService aliPayMerchantSynchService;
	private AliPayPayService aliPayPayService;
	private IStaticQRCodeService staticQRCodeService;
	private IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService;
	private IThreeCodeService threeCodeService;
	private ICupsPayService cupsPayService;

//    private BlockingQueue<String> queue = new LinkedBlockingDeque<String>(3);

	public QRCodeSocketManager(ChannelHandlerContext ctx, String reviceMsg, WxPayService wxPayService,
			ILocalBankService localBankService, WxMerchantSynchService wxMerchantSynchService,
			ITimingTaskService timingTaskService, AliPayMerchantSynchService aliPayMerchantSynchService,
			AliPayPayService aliPayPayService, IStaticQRCodeService staticQRCodeService,
			IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService, IThreeCodeService threeCodeService,
			ICupsPayService cupsPayService) {
		this.ctx = ctx;
		this.reviceMsg = reviceMsg;
		this.wxPayService = wxPayService;
		this.localBankService = localBankService;
		this.wxMerchantSynchService = wxMerchantSynchService;
		this.timingTaskService = timingTaskService;
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
		this.staticQRCodeService = staticQRCodeService;
		this.aliPayPayService = aliPayPayService;
		this.threeCodeStaticQRCodeService = threeCodeStaticQRCodeService;
		this.threeCodeService = threeCodeService;
		this.cupsPayService = cupsPayService;
	}

	/**
	 * 处理下载对账单和同步微信商户
	 * 
	 * @param socket
	 * @throws FrameException
	 */
	@Override
	public void run() {
		logger.info("/************二维码socket请求处理START,请求报文:" + reviceMsg);
		String protocol = null;
		// 待返回的响应信息
		String respString = "";
		try {
			// 处理协议
			OutputParam outputParam = ProtocolCodeFilter.handleProtocolCode(reviceMsg);
			// 协议格式
			protocol = String.format("%s", outputParam.getValue("protocol"));

			// 请求参数
			InputParam input = new InputParam();
			input.setParams(outputParam.getReturnObj());

			// 服务码
			String serviceCode = String.format("%s", outputParam.getValue("serviceCode"));

			if (StringConstans.TxnServiceCode.MORE_FEE_CONFIG.equals(serviceCode)) {
				/**
				 * 多费率配置 内存刷新
				 */
				logger.debug("多费率配置内存刷新...START");

				MappingUtils.MappingContextreinit(StringConstans.MappingConfig.CHANNEL_CONFIG);

				respString = "多费率配置内存刷新成功";
				logger.debug("多费率配置内存刷新...END");

			} else if (StringConstans.TxnServiceCode.WX_UNIFIED.equals(serviceCode)) {
				/**
				 * 微信扫码支付
				 */
				logger.info("[微信扫码支付流程处理]START:"+input.toString());
				
				OutputParam routing =wxMerchantSynchService.routing(input);
				
				OutputParam outPut = new OutputParam();
				
				if (!StringConstans.returnCode.SUCCESS.equals(routing.getReturnCode())) {
					outPut.setReturnCode(routing.getReturnCode());
					outPut.setReturnMsg(routing.getReturnMsg());
					respString = routing.getReturnMsg();
				}else {
					String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
					
					if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
						//间连
						outPut = wxPayService.wxUnifiedConsumeYL(input);/** 调用微信下单处理接口 */
					} else {
						//直连
						outPut = wxPayService.wxUnifiedConsume(input);/** 调用微信下单处理接口 */
					}
					
					logger.info("[微信扫码支付流程处理]END:"+outPut.toString());
					Map<String, Object> map = outPut.getReturnObj();
					if (StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
						// 下单成功,支付正在处理中
						map.put("respCode", StringConstans.RespCode.RESP_CODE_02);
						map.put("respDesc", StringConstans.RespDesc.RESP_DESC_02);
					} else {
						// 下单失败,交易失败
						map.put("respCode", StringConstans.RespCode.RESP_CODE_03);
						map.put("respDesc", outPut.getReturnMsg());
					}

					respString = Util.mapToXml(map);
				}
				
				logger.debug("微信扫码支付流程处理...END");

			} else if (StringConstans.TxnServiceCode.WX_MICRO.equals(serviceCode)) {
				/**
				 * 微信被扫支付
				 */
				logger.info("[微信被扫支付流程处理] START:"+input.toString());
				OutputParam routing =wxMerchantSynchService.routing(input);
				
				OutputParam outPut = new OutputParam();
				
				if (!StringConstans.returnCode.SUCCESS.equals(routing.getReturnCode())) {
					outPut=routing;
				}else {
					String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
					
					if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
						//间连
						outPut = wxPayService.wxMicroPayYL(input);/** 调用微信下单处理接口 */
					} else {
						//直连
						outPut = wxPayService.wxMicroPay(input);/** 调用微信下单处理接口 */
					}
					
					logger.info("[微信扫码支付流程处理]END:"+outPut.toString());
					respString = Util.mapToXml(outPut.getReturnObj());
				}

				logger.debug("微信被扫支付流程处理...END");

			} else if (StringConstans.TxnServiceCode.WX_SYNC_MER_ADD.equals(serviceCode)) {
				/**
				 * 微信同步商户(新增)
				 */
				logger.info("[微信同步商户(新增)流程处理] START:"+input.toString());
				
				OutputParam outPut = null;
				String switch1 = Constants.getParam("tsdk.switch");
				if(switch1.equals("true")) {
					//简连
					outPut = wxMerchantSynchService.addWxMerYL(input);
				} else {
					//直连
					outPut = wxMerchantSynchService.addWxMer(input);
				}
				
				logger.info("[微信同步商户(新增)流程处理] END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					map.put("respCode", StringConstans.RespCode.RESP_CODE_03);
					map.put("respDesc", outPut.getReturnMsg());
				}
				respString = Util.mapToXml(map);

				logger.debug("微信同步商户(新增)流程处理...END");
			} else if (StringConstans.TxnServiceCode.WX_SYNC_MER_MODIFY.equals(serviceCode)) {
				/**
				 * 微信同步商户(修改)
				 */
				logger.info("[微信同步商户(修改)流程处理] START:"+input.toString());
				OutputParam outPut = wxMerchantSynchService.modifyWxMer(input);
				logger.info("[微信同步商户(修改)流程处理] END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("微信同步商户(修改)流程处理...END");
			} else if (StringConstans.TxnServiceCode.WX_SYNC_MER_DELETE.equals(serviceCode)) {
				/**
				 * 微信同步商户(删除)
				 */
				logger.info("[微信同步商户(删除)流程处理] START:"+input.toString());
				OutputParam outPut = wxMerchantSynchService.deleteWxMer(input);
				logger.info("[微信同步商户(删除)流程处理] START:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("微信同步商户(删除)流程处理...END");
			} else if (StringConstans.TxnServiceCode.WX_SYNC_MER_QUERY.equals(serviceCode)) {
				/**
				 * 微信同步商户(查询)
				 */
				logger.info("[微信同步商户(查询)流程处理]  START:"+input.toString());
				OutputParam routing =wxMerchantSynchService.routing(input);
				
				String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
				input.putParams(Dict.rateChannel, routing.getValue(Dict.rate));
				
				OutputParam outPut = null;
				
				if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					//间连
					outPut = wxMerchantSynchService.queryWxMerYL(input);
				} else {
					//直连
					outPut = wxMerchantSynchService.queryWxMer(input);
				}
				
				
				logger.info("[微信同步商户(查询)流程处理]  END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("微信同步商户(查询)流程处理...END");
			} else if (StringConstans.TxnServiceCode.LOCAL_POSP_UNIFIED_ENCODE.equals(serviceCode)) {
				/**
				 * 本行生成二维码扫码接口[POSP][加密]
				 */
				logger.info("本行生成二维码扫码接口[POSP][加密]流程处理 START:"+input.toString());
				OutputParam outPut = localBankService.pospUnifiedConsume(input);
				logger.info("本行生成二维码扫码接口[POSP][加密]流程处理 END:"+outPut.toString());
				respString = Util.mapToXml(outPut.getReturnObj());

				logger.debug("本行生成二维码扫码接口[POSP][加密]流程处理...END");

			} else if (StringConstans.TxnServiceCode.LOCAL_POSP_MICRO_DECODE.equals(serviceCode)) {
				/**
				 * 本行二维码被扫接口[POSP][解密]
				 */
				logger.info("本行二维码被扫接口[POSP][解密]流程处理 START:"+input.toString());
				OutputParam outPut = localBankService.pospMicroConsume(input);
				logger.info("本行二维码被扫接口[POSP][解密]流程处理 END:"+outPut.toString());
				respString = Util.mapToXml(outPut.getReturnObj());

				logger.debug("本行二维码被扫接口[POSP][解密]流程处理...END");

			} else if (StringConstans.TxnServiceCode.LOCAL_MOBILE_MICRO_ENCODE.equals(serviceCode)) {
				/**
				 * 获取二维码信息[手机银行][加密]
				 * 
				 */
				logger.info("[获取二维码信息[手机银行][加密]流程处理] START:"+input.toString());
				OutputParam outPut = localBankService.getMobileQRcode(input);
				logger.info("[获取二维码信息[手机银行][加密]流程处理] END:"+outPut.toString());
				
				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(outPut.getReturnObj());

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(outPut.getReturnObj());

				}

				logger.debug("获取二维码信息[手机银行][加密]流程处理...END");

			} else if (StringConstans.TxnServiceCode.LOCAL_MOBILE_UNIFIED_DECODE.equals(serviceCode)) {
				/**
				 * 扫码支付解密接口[手机银行][解密]
				 * 
				 */

				logger.info("[扫码支付解密接口[手机银行][解密]]START:"+input.toString());
				OutputParam outPut = localBankService.mobileDecryptPospQRcode(input);
				logger.info("[扫码支付解密接口[手机银行][解密]]END:"+outPut.toString());
				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(outPut.getReturnObj());

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(outPut.getReturnObj());

				}

				logger.debug("扫码支付解密接口[手机银行][解密]流程处理...END");

			} else if (StringConstans.TxnServiceCode.FACE_TO_FACE_DECODE.equals(serviceCode)) {
				/**
				 * 面对面转账解密接口
				 */

				logger.info("[面对面转账解密接口流程处理]START:"+input.toString());
				OutputParam outPut = localBankService.mobileDecrytMobileQRcode(input);
				logger.info("[面对面转账解密接口流程处理]END:"+outPut.toString());
				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(outPut.getReturnObj());

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(outPut.getReturnObj());

				}

				logger.debug("面对面转账解密接口流程处理... END");

			} else if (StringConstans.TxnServiceCode.RECV_MOBILE_NOTIFY.equals(serviceCode)) {
				/**
				 * 接收手机银行通知接口
				 */
				logger.debug("[面对面转账解密接收结果]start:"+input.toString());
				OutputParam outPut = localBankService.handleMobilePayNotify(input);
				logger.debug("[面对面转账解密接收结果]end:"+outPut.toString());
				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(outPut.getReturnObj());

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(outPut.getReturnObj());

				}

				logger.debug("面对面转账解密接口流程处理... END");

			} else if (StringConstans.TxnServiceCode.RECV_FSHL_NOTIFY.equals(serviceCode)) {
				/**
				 * 接收手机银行通知接口
				 */

				logger.info("[接收丰收互联交易结果流程处理] START:"+input.toString());
				OutputParam outPut = localBankService.handlerMobileFrontNotify(input);
				logger.info("[接收丰收互联交易结果流程处理] END:"+outPut.toString());
				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(outPut.getReturnObj());

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(outPut.getReturnObj());

				}

				logger.debug("接收丰收互联交易结果流程处理... END");

			} else if (StringConstans.TxnServiceCode.WX_MICRO_REVOKE.equals(serviceCode)) {
				/**
				 * 微信被扫撤销接口
				 */
				logger.info("[微信被扫撤销接口流程处理] START :"+input.toString());
				OutputParam outPut = wxPayService.wxMicroRevoke(input);
				logger.info("[微信被扫撤销接口流程处理] END :"+outPut.toString());

				respString = Util.mapToXml(outPut.getReturnObj());

				logger.debug("微信被扫撤销接口流程处理...END");
			} else if (StringConstans.TxnServiceCode.TRANS_QUERY.equals(serviceCode)) {
				/**
				 * 交易状态查询
				 */
				logger.debug("交易状态查询流程处理START");
				
				logger.debug("[交易状态查询处理] START:"+input.toString());
				OutputParam outPut = localBankService.pospQueryState(input);
				logger.debug("[交易状态查询处理]  END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("交易状态查询流程处理END");

			} else if (StringConstans.TxnServiceCode.REFUND.equals(serviceCode)) {
				/**
				 * 退款
				 */
				
				logger.debug("[退款]START:"+input.toString());
				OutputParam outPut = localBankService.refund(input);
				logger.debug("[退款]END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);


			} else if (StringConstans.TxnServiceCode.REFUND_QUERY.equals(serviceCode)) {
				/**
				 * 退款查询
				 */
				
				logger.debug("[退款查询]START:"+input.toString());
				OutputParam outPut = localBankService.refundQuery(input);
				logger.debug("[退款查询]END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);


			}else if (StringConstans.TxnServiceCode.WX_CLOSE_ORDER.equals(serviceCode)) {
				/**
				 * 微信关闭订单交易
				 */
				logger.info("[微信关闭订单处理] START "+input.toString());
				OutputParam outPut = wxPayService.wxCloseOrder(input);
				logger.info("[微信关闭订单处理] END "+ outPut.toString());
				
				respString = Util.mapToXml(outPut.getReturnObj());

				logger.debug("微信关闭订单处理...END");

			} else if (StringConstans.TxnServiceCode.WX_BILL_DOWN.equals(serviceCode)) {
				/**
				 * 下载微信对账单
				 */
				String billDate = String.format("%s", outputParam.getReturnObj().get("protocolContent"));
				input.putParams("billDate", billDate);
				logger.info("[下载微信对账单] START "+input.toString());
				OutputParam outPut = wxPayService.downloadWxBill(input);
				logger.info("[下载微信对账单] END "+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("下载微信对账单...END");

			} else if (StringConstans.TxnServiceCode.WX_BILL_SINGLE_DOWN.equals(serviceCode)) {
				/**
				 * 下载微信对账单单个费率
				 */
				logger.info("[下载微信对账单单个费率] START:"+input.toString());
				OutputParam outPut = wxPayService.downloadSingleWxBill(input);
				logger.info("[下载微信对账单单个费率] END:"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("下载微信对账单单个费率...END");

			} else if (StringConstans.TxnServiceCode.WX_BILL_SUM_DOWN.equals(serviceCode)) {
				/**
				 * 微信对账单合成
				 */
				logger.info("[微信对账单合成] START "+input.toString() );
				OutputParam outPut = wxPayService.sumWxBill(input);
				logger.info("[微信对账单合成] START "+outPut.toString() );
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("微信对账单合成...END");

			} else if (StringConstans.TxnServiceCode.BACK_UP_ORDER.equals(serviceCode)) {
				/**
				 * 备份订单表
				 */
				String backupDate = String.format("%s", outputParam.getReturnObj().get("protocolContent"));
				input.putParams("backupDate", backupDate);
				logger.info("[备份订单表] START "+input.toString());
				OutputParam outPut = timingTaskService.timingCopyTblOrderInfoToHisByDate(input);
				logger.info("[备份订单表] END "+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);

				logger.debug("备份订单表...END");

			} else if (StringConstans.TxnServiceCode.LOCAL_CREATE_STATIC_QR.equals(serviceCode)) {
				/**
				 * 创建静态二维码
				 */

				logger.info("[创建静态二维码]START "+input.toString());
				OutputParam outPut = staticQRCodeService.createStaticQRCode(input);
				logger.info("[创建静态二维码]END "+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(map);

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(map);

				}

				logger.debug("创建静态二维码..END");

			} else if (StringConstans.TxnServiceCode.LOCAL_QUERY_STATIC_QR.equals(serviceCode)) {
				/**
				 * 查询静态二维码
				 * 
				 */

				logger.info("[查询静态二维码]  START "+input.toString());
				OutputParam outPut = staticQRCodeService.queryStaticQRCode(input);
				logger.info("[查询静态二维码]  END "+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(map);

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(map);

				}

				logger.debug("查询静态二维码..END");

			} else if (StringConstans.TxnServiceCode.LOCAL_PARSER_STATIC_QR.equals(serviceCode)) {
				/**
				 * 解析静态二维码
				 */

				logger.info("[解析静态二维码] START"+input.toString());
				OutputParam outPut = staticQRCodeService.parseStaticQRCode(input);
				logger.info("[解析静态二维码] END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(map);

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(map);

				}

				logger.debug("解析静态二维码..END");

			} else if (StringConstans.TxnServiceCode.LOCAL_STOP_STATIC_QR.equals(serviceCode)) {
				/**
				 * 停用静态二维码
				 */

				logger.info("[停用静态二维码] START" +input.toString());
				OutputParam outPut = staticQRCodeService.stopStaticQRCode(input);
				logger.info("[停用静态二维码] END" +outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(map);

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(map);

				}

				logger.debug("停用静态二维码..END");

			} else if (StringConstans.TxnServiceCode.LOCAL_CREATE_STATIC_QR_IMAGE.equals(serviceCode)) {
				/**
				 * 生成静态二维码图片
				 */

				logger.info("[生成静态二维码图片]START "+ input.toString());
				OutputParam outPut = staticQRCodeService.createStaticQRCodeImage(input);
				logger.info("[生成静态二维码图片]END "+ outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(map);

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(map);

				}

				logger.debug("生成静态二维码图片..END");

			} else if (StringConstans.TxnServiceCode.LOCAL_BATCH_QUERY_STATIC_QR.equals(serviceCode)) {
				/**
				 * 批量查询静态二维码
				 */

				logger.info("[批量查询静态二维码] START"+input.toString());
				OutputParam outPut = staticQRCodeService.queryBatchStaticQRCode(input);
				logger.info("[批量查询静态二维码] END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (StringConstans.ProtocolType.PROTOCOL_XML.equals(protocol)) {

					respString = Util.mapToXml(map);

				} else if (StringConstans.ProtocolType.PROTOCOL_JSON.equals(protocol)) {

					respString = JsonUtil.bean2Json(map);

				}

				logger.debug("批量查询静态二维码..END");

			} else if (StringConstans.TxnServiceCode.CREATE_STATIC_QR_OF_THREE_CODE.equals(serviceCode)) {

				/**
				 * 创建三码合一静态二维码
				 */
				logger.info("[创建三码合一静态二维码] START"+input.toString());
				OutputParam outPut = threeCodeStaticQRCodeService.createThreeCodeStaticQRCode(input);
				logger.info("[创建三码合一静态二维码] START"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);
				logger.debug("创建三码合一静态二维码..END");
			} else if (StringConstans.TxnServiceCode.QUERY_STATIC_QR_OF_THREE_CODE.equals(serviceCode)) {

				/**
				 * 查询三码合一静态二维码
				 */
				logger.info("[查询三码合一静态二维码]START"+input.toString());
				OutputParam outPut = threeCodeStaticQRCodeService.queryThreeCodeStaticQRCode(input);
				logger.info("[查询三码合一静态二维码]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);
				logger.debug("查询三码合一静态二维码..END");
			} else if (StringConstans.TxnServiceCode.STOP_STATIC_QR_OF_THREE_CODE.equals(serviceCode)) {

				/**
				 * 停用三码合一静态二维码
				 */
				logger.info("[停用三码合一静态二维码]START"+input.toString());
				OutputParam outPut = threeCodeStaticQRCodeService.stopThreeCodeStaticQRCode(input);
				logger.info("[停用三码合一静态二维码]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);
				logger.debug("停用三码合一静态二维码..END");
			} else if (StringConstans.TxnServiceCode.CREATE_STATIC_QR_IMAGE_OF_THREE_CODE.equals(serviceCode)) {

				/**
				 * 生成三码合一静态二维码图片
				 */
				logger.info("[生成三码合一静态二维码图片]START"+input.toString());
				OutputParam outPut = threeCodeStaticQRCodeService.createThreeCodeStaticQRCodeImage(input);
				logger.info("[生成三码合一静态二维码图片]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);
				logger.debug("生成三码合一静态二维码图片..END");
			} else if (StringConstans.TxnServiceCode.UPDATE_SINGLE_STATIC_QR_OF_THREE_CODE.equals(serviceCode)) {

				/**
				 * 更新三码合一静态二维码
				 */
				logger.info("[更新三码合一静态二维码]START"+input.toString());
				OutputParam outPut = threeCodeStaticQRCodeService.updateThreeCodeStaticQRCode(input);
				logger.info("[更新三码合一静态二维码]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				respString = Util.mapToXml(map);
				logger.debug("更新三码合一静态二维码..END");
			} else if (StringConstans.TxnServiceCode.ALIPAY_UNIFIED.equals(serviceCode)) {
				/**
				 * 支付宝下单
				 */
				logger.info("[支付宝主扫] START" +input.toString());
				OutputParam outPut = null;
				
				
				OutputParam routing = aliPayMerchantSynchService.routing(input);
				String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
				String rateChannel = StringUtil.toString(routing.getValue(Dict.rateChannel));
				if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					//间连
					input.putParams(Dict.rateChannel, rateChannel);
					outPut = aliPayPayService.aLiPayPreCreateYL(input);
				} else {
					//直连
					outPut = aliPayPayService.aLiPayPreCreate(input);
				}
				
				logger.info("[支付宝主扫] END" +outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					map.put("respCode", StringConstans.RespCode.RESP_CODE_03);
					map.put("respDesc", outPut.getReturnMsg());
				}

				respString = Util.mapToXml(map);

				logger.debug("支付宝主扫处理..END");
			} else if (StringConstans.TxnServiceCode.ALIPAY_MICRO.equals(serviceCode)) {
				/**
				 * 支付宝被扫
				 */
				logger.info("[支付宝被扫] START"+input.toString());
				OutputParam outPut = null;
				
				OutputParam routing = aliPayMerchantSynchService.routing(input);
				String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
				String rateChannel = StringUtil.toString(routing.getValue(Dict.rateChannel));
				if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					//间连
					input.putParams(Dict.rateChannel, rateChannel);
					outPut = aliPayPayService.aLiPayMicroPayYL(input);
				} else {
					//直连
					outPut = aliPayPayService.aLiPayMicroPay(input);
				}
				
				
				logger.info("[支付宝被扫] END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					// 支付宝被扫交易失败
					map.put("respCode", outPut.getReturnCode());
					map.put("respDesc", outPut.getReturnMsg());
				}

				respString = Util.mapToXml(map);

				logger.debug("支付宝被扫处理..END");

			} else if (StringConstans.TxnServiceCode.ALIPAY_SYNC_MER_ADD.equals(serviceCode)) {
				/**
				 * 支付宝增加子商户
				 */
				logger.info("[支付宝增加子商户]START"+input.toString());
				OutputParam outPut = null;
				
				
				String switch1 = Constants.getParam("asdk.switch");
				if(switch1.equals("true")) {
					//间连
					outPut = aliPayMerchantSynchService.addALiPayMerYL(input);
				} else {
					//直连
					outPut = aliPayMerchantSynchService.addALiPayMer(input);
				}
				
				
				logger.info("[支付宝增加子商户]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					map.put("respCode", StringConstans.RespCode.RESP_CODE_03);
					map.put("respDesc", outPut.getReturnMsg());
				}
				respString = Util.mapToXml(map);

				logger.debug("[支付宝增加子商户]END");

			} else if (StringConstans.TxnServiceCode.ALIPAY_SYNC_MER_QUERY.equals(serviceCode)) {
				/**
				 * 支付宝查询子商户
				 */
				logger.info("[支付宝查询子商户]START"+input.toString());
				OutputParam outPut = null;
				
				
				OutputParam routing = aliPayMerchantSynchService.routing(input);
				String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
				input.putParams(Dict.rateChannel, routing.getValue(Dict.rate));
				if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					//间连
					outPut = aliPayMerchantSynchService.queryALiPayMerYL(input);
				} else {
					//直连
					outPut = aliPayMerchantSynchService.queryALiPayMer(input);
				}
				
				
				logger.info("[支付宝查询子商户]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					map.put("respCode", StringConstans.RespCode.RESP_CODE_03);
					map.put("respDesc", outPut.getReturnMsg());
				}

				respString = Util.mapToXml(map);

				logger.debug("支付宝查询子商户...END");

			} else if (StringConstans.TxnServiceCode.ALIPAY_SYNC_MER_MODIFY.equals(serviceCode)) {
				/**
				 * 支付宝修改子商户
				 */
				logger.info("[支付宝修改子商户]START"+input.toString());
				OutputParam outPut = null;
				
				
				OutputParam routing = aliPayMerchantSynchService.routing(input);
				String connectMethod = StringUtil.toString(routing.getValue(Dict.connectMethod));
				input.putParams(Dict.rateChannel, StringUtil.toString(routing.getValue(Dict.rate)));
				if(StringConstans.CONNECT_METHOD.indirect.equals(connectMethod)) {
					//间连
					outPut = aliPayMerchantSynchService.modifyALiPayMerYL(input);
				}else {
					//直连
					outPut = aliPayMerchantSynchService.modifyALiPayMer(input);
				}
				
				
				logger.info("[支付宝修改子商户]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					map.put(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					map.put(Dict.respDesc, outPut.getReturnMsg());
				}

				respString = Util.mapToXml(map);

				logger.debug("支付宝修改子商户...END");

			} else if (StringConstans.TxnServiceCode.ALIPAY_SYNC_MER_TRANSFER.equals(serviceCode)) {
				/**
				 * 支付宝存量商户迁移
				 */
				logger.info("[支付宝存量商户迁移]START"+input.toString());
				OutputParam outPut = aliPayMerchantSynchService.transferAliMer(input);
				logger.info("[支付宝存量商户迁移]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();
				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					map.put(Dict.respCode, StringConstans.RespCode.RESP_CODE_03);
					map.put(Dict.respDesc, outPut.getReturnMsg());
				}
				respString = Util.mapToXml(map);
			} else if (StringConstans.TxnServiceCode.ALIPAY_CLOSE_ORDER.equals(serviceCode)) {
				/**
				 * 支付宝关闭订单
				 */
				logger.info("[支付宝关闭订单处理]START"+input.toString());
				OutputParam	outPut = aliPayPayService.aLiPayCloseOrder(input);
				
				logger.info("[支付宝关闭订单处理]END"+outPut.toString());
				
				respString = Util.mapToXml(outPut.getReturnObj());
				logger.debug("支付宝关闭订单处理..END");

			} else if (StringConstans.TxnServiceCode.ALIPAY_REVOKE_ORDER.equals(serviceCode)) {
				/**
				 * 支付宝撤消
				 */
				logger.info("[支付宝撤消处理]START"+input.toString());
				
				OutputParam outPut = aliPayPayService.aLiPayRevoke(input);
				
				logger.info("[支付宝撤消处理]END"+outPut.toString());
				respString = Util.mapToXml(outPut.getReturnObj());

				logger.debug("支付宝撤消处理..END");

			} else if (StringConstans.TxnServiceCode.ALIPAY_BILL_DOWN.equals(serviceCode)) {
				/**
				 * 支付宝对账单下载
				 */
				logger.info("[支付宝对账单下载]START"+input.toString());
				OutputParam outPut = aliPayPayService.downloadALiPayBill(input);
				logger.info("[支付宝对账单下载]END"+outPut.toString());
				Map<String, Object> map = outPut.getReturnObj();

				if (!StringConstans.returnCode.SUCCESS.equals(outPut.getReturnCode())) {
					// 支付宝对账单下载失败
					map.put("respCode", StringConstans.RespCode.RESP_CODE_03);
					map.put("respDesc", outPut.getReturnMsg());
				}

				respString = Util.mapToXml(map);

				logger.debug("支付宝对账单下载..END");
			} else if (StringConstans.TxnServiceCode.TC_QUERY_ORDER.equals(serviceCode)) {
				/**
				 * 查询三码合一微信和支付宝01,06订单
				 */
				logger.info("[查询三码合一微信和支付宝01,06订单]START"+input.toString());
				OutputParam output = threeCodeService.queryWxAndAlipayUnknowOrder(input);
				logger.info("[查询三码合一微信和支付宝01,06订单]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("查询三码合一微信和支付宝01,06订单..END");

			} else if (StringConstans.TxnServiceCode.TC_WX_AND_ALIPAY_BILL.equals(serviceCode)) {
				/**
				 * 生成三码合一微信和支付宝对账单
				 */
				logger.info("[生成三码合一微信和支付宝对账单]START"+input.toString());
				OutputParam output = threeCodeService.creatWxAndAlipayBill0(input);
				logger.info("[生成三码合一微信和支付宝对账单]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("生成三码合一微信和支付宝对账单..END");

			} else if (StringConstans.TxnServiceCode.TC_ORDER_BILL.equals(serviceCode)) {
				/**
				 * 下载三码合一流水对账单
				 */
				logger.info("[下载三码合一流水对账单]START"+input.toString());
				OutputParam output = threeCodeService.downloadThreeCodeBill(input);
				logger.info("[下载三码合一流水对账单]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("下载三码合一流水对账单..END");

			} else if (StringConstans.TxnServiceCode.T0_ACCOUNTED_TXN.equals(serviceCode)) {
				/**
				 * 向核心发送T+0入账交易
				 */
				logger.info("[向核心发送T+0入账交易]START"+input.toString());
				OutputParam output = threeCodeService.accountedTxnToCore(input);
				logger.info("[向核心发送T+0入账交易]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("向核心发送T+0入账交易..END");

			} else if (StringConstans.TxnServiceCode.C2B_EWM_SCANED.equals(serviceCode)) {
				/**
				 * C2B银联二维码被扫
				 */
				logger.info("[C2B银联二维码被扫]START"+input.toString());
				OutputParam output = cupsPayService.C2BEWMScanedConsume(input);
				logger.info("[C2B银联二维码被扫]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("C2B银联二维码被扫..END");

			} else if (StringConstans.TxnServiceCode.C2B_CONSUME_REVERSE.equals(serviceCode)) {
				/**
				 * C2B银联二维码被扫消费冲正
				 */
				logger.info("[C2B银联二维码被扫消费冲正]START"+input.toString());
				OutputParam output = cupsPayService.C2BEWMScanedConsumeReverse(input);
				logger.info("[C2B银联二维码被扫消费冲正]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("C2B银联二维码被扫消费冲正..END");

			} else if (StringConstans.TxnServiceCode.C2B_CONSUME_QUERY.equals(serviceCode)) {
				/**
				 * C2B银联二维码被扫消费查询
				 */
				logger.info("[C2B银联二维码被扫消费查询]START"+input.toString());
				OutputParam output = cupsPayService.C2BEWMScanedConsumeQuery(input);
				logger.info("[C2B银联二维码被扫消费查询]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("C2B银联二维码被扫消费查询..END");

			}  else if (StringConstans.TxnServiceCode.C2B_UNIFIED_QUERY.equals(serviceCode)) {
				/**
				 * C2B银联二维码主扫
				 * 丰收互联扫二维码，调用此接口，二维码系统去银联查询订单信息，返回给丰收互联
				 */
				logger.info("[C2B银联二维码主扫]START"+input.toString());
				OutputParam output = cupsPayService.C2BEWMScanedUnified(input);
				logger.info("[C2B银联二维码主扫]END"+output.toString());
				
				respString = Util.mapToXml(output.getReturnObj());
				logger.debug("C2B银联二维码主扫..END");
			
			}else if (StringConstans.TxnServiceCode.PAY_CENTER_QUERY.equals(serviceCode)) {
				/**
				 * 支付平台查询交易状态
				 */
				logger.info("[支付平台查询交易状态]START"+input.toString());
				OutputParam output=new OutputParam();
				String qrNo = StringUtil.toString(input.getValue(Dict.qrNo));
				if (qrNo.startsWith(StringConstans.CupsEwmInfo.CUPS_EWM_OWN)) {
					output = cupsPayService.payCenterQueryZS(input);
				}else {
					output = cupsPayService.payCenterQuery(input);
				}
				logger.info("[支付平台查询交易状态]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("支付平台查询交易状态..END");

			} else if (StringConstans.TxnServiceCode.FSHL_LIMIT_QUERY.equals(serviceCode)) {
				/**
				 * 丰收互联查询商户额度
				 */
				logger.info("[丰收互联查询商户日限额和月限额]START"+input.toString());
				OutputParam output = new OutputParam();
				output = localBankService.queryLimitAmt(input, output);
				logger.info("[丰收互联查询商户日限额和月限额]END"+output.toString());
				respString = Util.mapToXml(output.getReturnObj());

				logger.debug("丰收互联查询商户日限额和月限额..END");
			} else if (StringConstans.TxnServiceCode.FSHL_QUERY_THREECODE.equals(serviceCode)) {
				/**
				 * 丰收家查询三码合一流水
				 */
				logger.debug("丰收家查询三码合一流水..START");

				SocketRequest srOffer = new SocketRequest();
				srOffer.setServiceCode(StringConstans.TxnServiceCode.FSHL_QUERY_THREECODE);
				srOffer.setTime(DateUtil.getCurrentDateTimeFormat(DateUtil.defaultSimpleFormater));
				srOffer.setThreadNum(Thread.currentThread().getName());
				// 是否加入队列 true:加入成功 false:加入失败
				boolean isOffer = QRCodeSocketServerListener.queue.offer(srOffer, 1, TimeUnit.SECONDS);
				logger.debug("插入队列是否成功:" + isOffer + ",插入对象:" + srOffer.toString() + ",当前队列深度:"
						+ QRCodeSocketServerListener.queue.size());
				if (isOffer) {
					logger.debug("[丰收家查询三码合一流水]请求报文:" + input.toString());
					OutputParam output = threeCodeService.queryThreeCodeStatement(input);
					logger.debug("[丰收家查询三码合一流水]返回报文:" + output.toString());

					if (StringConstans.returnCode.SUCCESS.equals(output.getReturnCode())) {
						respString = output.getValueString("returnMsg");
					} else {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("respCode", StringConstans.RespCode.RESP_CODE_01);
						map.put("respDesc", output.getReturnMsg());
						respString = Util.mapToXml(map);
					}

					SocketRequest srPoll = QRCodeSocketServerListener.queue.poll(1, TimeUnit.SECONDS);
					boolean isPoll = null != srPoll;
					logger.debug("取出队列是否成功:" + isPoll + ",取出对象:" + srOffer.toString() + ",当前队列深度:"
							+ QRCodeSocketServerListener.queue.size());
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("respCode", StringConstans.RespCode.RESP_CODE_01);
					map.put("respDesc", "系统繁忙，请稍后再试");
					respString = Util.mapToXml(map);
				}

				logger.debug("丰收家查询三码合一流水..END");
			} else if(StringConstans.TxnServiceCode.C2B_MARKET_QUERY.equals(serviceCode)){
				
				logger.info("[C2B银联二维码主扫查询营销交易]START"+input.toString());
				OutputParam output = cupsPayService.C2BEWMSmarketQuery(input);
				logger.info("[C2B银联二维码主扫查询营销交易]END"+output.toString());
				
				respString = Util.mapToXml(output.getReturnObj());
			}else {
				logger.info("-----------未找到服务码-----------");

				Map<String, Object> errorMap = new HashMap<String, Object>();
				errorMap.put("respCode", StringConstans.RespCode.RESP_CODE_03);
				errorMap.put("respDesc", "txnCode非法");
				errorMap.put("errorMessage", outputParam.getReturnMsg());

				respString = Util.getErrorMessage(protocol, errorMap);
			}

			byte[] resp = respString.getBytes(charset);
			respString = StringUtil.fillString(String.valueOf(resp.length), 6) + respString;

			ChannelFuture future = ctx.write(respString);
			future.addListener(ChannelFutureListener.CLOSE);
			ctx.flush();
			ctx.close();

		} catch (Exception e) {
			logger.error("二维码前置异常:" + e.getMessage(), e);
			try {
				Map<String, Object> errorMap = new HashMap<String, Object>();
				errorMap.put("respCode", StringConstans.RespCode.RESP_CODE_03);
				errorMap.put("respDesc", "二维码前置处理异常"+e.getMessage());
				respString = Util.getErrorMessage(protocol, errorMap);

				byte[] resp = respString.getBytes(charset);

				respString = StringUtil.fillString(String.valueOf(resp.length), 6) + respString;
			} catch (Exception e1) {
				logger.error("处理异常时发生异常..." + e1.getMessage(), e1);
			}
		} finally {
			ChannelFuture future = ctx.write(respString);
			future.addListener(ChannelFutureListener.CLOSE);
			ctx.flush();
			ctx.close();
			logger.info("/************二维码socket请求处理END,返回报文:" + respString);
		}
	}

	public WxPayService getWxPayService() {
		return wxPayService;
	}

	public void setWxPayService(WxPayService wxPayService) {
		this.wxPayService = wxPayService;
	}

	public ILocalBankService getLocalBankService() {
		return localBankService;
	}

	public void setLocalBankService(ILocalBankService localBankService) {
		this.localBankService = localBankService;
	}

	public WxMerchantSynchService getWxMerchantSynchService() {
		return wxMerchantSynchService;
	}

	public void setWxMerchantSynchService(WxMerchantSynchService wxMerchantSynchService) {
		this.wxMerchantSynchService = wxMerchantSynchService;
	}

	public AliPayMerchantSynchService getAliPayMerchantSynchService() {
		return aliPayMerchantSynchService;
	}

	public void setAliPayMerchantSynchService(AliPayMerchantSynchService aliPayMerchantSynchService) {
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
	}

	public AliPayPayService getAliPayPayService() {
		return aliPayPayService;
	}

	public void setAliPayPayService(AliPayPayService aliPayPayService) {
		this.aliPayPayService = aliPayPayService;
	}

	public IStaticQRCodeService getStaticQRCodeService() {
		return staticQRCodeService;
	}

	public void setStaticQRCodeService(IStaticQRCodeService staticQRCodeService) {
		this.staticQRCodeService = staticQRCodeService;
	}

	public ICupsPayService getCupsPayService() {
		return cupsPayService;
	}

	public void setCupsPayService(ICupsPayService cupsPayService) {
		this.cupsPayService = cupsPayService;
	}
}
