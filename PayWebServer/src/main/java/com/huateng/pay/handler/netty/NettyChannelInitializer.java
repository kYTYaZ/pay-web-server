package com.huateng.pay.handler.netty;

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

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class NettyChannelInitializer extends ChannelInitializer<SocketChannel> {
	private WxPayService wxPayService;
    private ITimingTaskService timingTaskService;
	private ILocalBankService localBankService; 
	private WxMerchantSynchService wxMerchantSynchService;
	private ThreadPool QRCodePool;
	private AliPayMerchantSynchService aliPayMerchantSynchService;
	private IStaticQRCodeService  staticQRCodeService;
	private AliPayPayService aliPayPayService;
	private IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService;
	private IThreeCodeService  threeCodeService;
	private ICupsPayService cupsPayService;
	
	private static final StringEncoder ENCODER = new StringEncoder(CharsetUtil.UTF_8);
	private static final StringDecoder DECODER = new StringDecoder(CharsetUtil.UTF_8);
	
	
	public NettyChannelInitializer(WxPayService wxPayService,
			ILocalBankService localBankService,
			WxMerchantSynchService wxMerchantSynchService,
			ITimingTaskService timingTaskService,
			AliPayMerchantSynchService aliPayMerchantSynchService,
			IStaticQRCodeService  staticQRCodeService,
			ThreadPool QRCodePool,
			IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService,
			AliPayPayService aliPayPayService,
			IThreeCodeService threeCodeService,
			ICupsPayService cupsPayService) {
		this.wxPayService = wxPayService;
		this.localBankService = localBankService;
		this.wxMerchantSynchService = wxMerchantSynchService;
		this.timingTaskService = timingTaskService;
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
		this.QRCodePool = QRCodePool;
		this.staticQRCodeService = staticQRCodeService;
		this.aliPayPayService = aliPayPayService;
		this.threeCodeStaticQRCodeService = threeCodeStaticQRCodeService;
		this.threeCodeService = threeCodeService;
		this.cupsPayService = cupsPayService;
	}
	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		
		socketChannel.pipeline().addLast(new NettyLengthFieldBasedFrameDecoder(1024 * 1024,0,6,0,0));
		socketChannel.pipeline().addLast(DECODER);
		socketChannel.pipeline().addLast(ENCODER);
	
		socketChannel.pipeline().addLast(new NettyServerHandler(wxPayService,localBankService,wxMerchantSynchService,timingTaskService,aliPayMerchantSynchService,staticQRCodeService,QRCodePool,threeCodeStaticQRCodeService,aliPayPayService,threeCodeService,cupsPayService));
	}
}
