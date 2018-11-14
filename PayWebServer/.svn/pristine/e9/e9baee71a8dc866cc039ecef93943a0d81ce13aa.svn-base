package com.huateng.pay.handler.thread;

import com.huateng.pay.handler.netty.NettyServer;
import com.huateng.pay.handler.pool.ThreadPool;
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

public class SocketServerHandler extends Thread{
	private WxPayService wxPayService;
	private ILocalBankService localBankService; 
	private WxMerchantSynchService wxMerchantSynchService;
	private ITimingTaskService timingTaskService;
	private ThreadPool QRCodePool;
	private NettyServer nettyServer;
	private AliPayMerchantSynchService aliPayMerchantSynchService;
	private AliPayPayService aliPayPayService;
	private IStaticQRCodeService  staticQRCodeService;
	private IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService;
	private IThreeCodeService threeCodeService;
	private ICupsPayService cupsPayService;
	
	public ICupsPayService getCupsPayService() {
		return cupsPayService;
	}

	public void setCupsPayService(ICupsPayService cupsPayService) {
		this.cupsPayService = cupsPayService;
	}

	public SocketServerHandler(WxPayService wxPayService,
			ILocalBankService localBankService,
			WxMerchantSynchService wxMerchantSynchService,
			ThreadPool QRCodePool, 
			ITimingTaskService timingTaskService,
			AliPayMerchantSynchService aliPayMerchantSynchService,
			IStaticQRCodeService staticQRCodeService,
			IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService,
			 AliPayPayService aliPayPayService,
			 IThreeCodeService threeCodeService,
			 ICupsPayService cupsPayService) {		
		this.wxPayService = wxPayService;
		this.localBankService = localBankService;
		this.wxMerchantSynchService = wxMerchantSynchService;
		this.QRCodePool = QRCodePool;
		this.timingTaskService = timingTaskService;
		this.staticQRCodeService = staticQRCodeService;
		this.aliPayPayService = aliPayPayService;
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
		this.threeCodeStaticQRCodeService = threeCodeStaticQRCodeService;
		this.threeCodeService = threeCodeService;
		this.cupsPayService = cupsPayService;
	}

	public void run() {
		System.out.println("二维码SOCKET服务启动...  ");
		nettyServer = new NettyServer(wxPayService,localBankService,wxMerchantSynchService,timingTaskService,aliPayMerchantSynchService,staticQRCodeService,aliPayPayService,threeCodeStaticQRCodeService,QRCodePool,threeCodeService, cupsPayService);
		nettyServer.bind(8888);
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

	public void setWxMerchantSynchService(
			WxMerchantSynchService wxMerchantSynchService) {
		this.wxMerchantSynchService = wxMerchantSynchService;
	}

	public ThreadPool getQRCodePool() {
		return QRCodePool;
	}

	public void setQRCodePool(ThreadPool qRCodePool) {
		QRCodePool = qRCodePool;
	}

	public ITimingTaskService getTimingTaskService() {
		return timingTaskService;
	}

	public void setTimingTaskService(ITimingTaskService timingTaskService) {
		this.timingTaskService = timingTaskService;
	}

	public AliPayPayService getAliPayPayService() {
		return aliPayPayService;
	}

	public void setAliPayPayService(AliPayPayService aliPayPayService) {
		this.aliPayPayService = aliPayPayService;
	}

	public IThreeCodeStaticQRCodeService getThreeCodeStaticQRCodeService() {
		return threeCodeStaticQRCodeService;
	}

	public void setThreeCodeStaticQRCodeService(
			IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService) {
		this.threeCodeStaticQRCodeService = threeCodeStaticQRCodeService;
	}

	public AliPayMerchantSynchService getAliPayMerchantSynchService() {
		return aliPayMerchantSynchService;
	}

	public void setAliPayMerchantSynchService(
			AliPayMerchantSynchService aliPayMerchantSynchService) {
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
	}

	public IStaticQRCodeService getStaticQRCodeService() {
		return staticQRCodeService;
	}

	public void setStaticQRCodeService(IStaticQRCodeService staticQRCodeService) {
		this.staticQRCodeService = staticQRCodeService;
	}

	public IThreeCodeService getThreeCodeService() {
		return threeCodeService;
	}

	public void setThreeCodeService(IThreeCodeService threeCodeService) {
		this.threeCodeService = threeCodeService;
	}
	
	
}
