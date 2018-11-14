package com.huateng.pay.handler.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class NettyServer {
	
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	private WxPayService wxPayService;
	private ILocalBankService localBankService; 
	private WxMerchantSynchService wxMerchantSynchService;
	private ITimingTaskService timingTaskService;
	private ThreadPool QRCodePool;
	private AliPayMerchantSynchService aliPayMerchantSynchService;
	private IStaticQRCodeService  staticQRCodeService;
	private AliPayPayService aliPayPayService;
	private IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService;
	private IThreeCodeService  threeCodeService;
	private ICupsPayService cupsPayService;
	
	public NettyServer(WxPayService wxPayService,
			ILocalBankService localBankService,
			WxMerchantSynchService wxMerchantSynchService,
			ITimingTaskService timingTaskService,
			AliPayMerchantSynchService aliPayMerchantSynchService,
			IStaticQRCodeService  staticQRCodeService,
			AliPayPayService aliPayPayService,
			IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService,
			ThreadPool QRCodePool,
			IThreeCodeService threeCodeService,
			ICupsPayService cupsPayService) {
		this.wxPayService = wxPayService;
		this.localBankService = localBankService;
		this.timingTaskService = timingTaskService;
		this.wxMerchantSynchService = wxMerchantSynchService;
		this.aliPayMerchantSynchService = aliPayMerchantSynchService;
		this.staticQRCodeService = staticQRCodeService;
		this.aliPayPayService = aliPayPayService;
		this.QRCodePool = QRCodePool;
		this.threeCodeStaticQRCodeService = threeCodeStaticQRCodeService;
		this.threeCodeService = threeCodeService;
		this.cupsPayService = cupsPayService;
	}
	
	public void bind(int port) {
		  EventLoopGroup bossGroup = new NioEventLoopGroup();
	      EventLoopGroup workerGroup = new NioEventLoopGroup();
	      try {
	            ServerBootstrap bootstrap = new ServerBootstrap();
	            bootstrap.group(bossGroup, workerGroup);
	            bootstrap.channel(NioServerSocketChannel.class);
	            bootstrap.option(ChannelOption.SO_BACKLOG, 6144);   
	            bootstrap.option(EpollChannelOption.SO_REUSEADDR, true);
	            bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
	            bootstrap.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(512, 4096, 15000));
	            bootstrap.childHandler(new NettyChannelInitializer(wxPayService,localBankService,wxMerchantSynchService,timingTaskService,aliPayMerchantSynchService,staticQRCodeService,QRCodePool,threeCodeStaticQRCodeService,aliPayPayService,threeCodeService, cupsPayService));
	            ChannelFuture future = bootstrap.bind(port).sync();
	            future.channel().closeFuture().sync();
	        }catch(Exception e){
	        	logger.error(e.getMessage(),e);
	        } finally {
	           bossGroup.shutdownGracefully();
	           workerGroup.shutdownGracefully();
	        }
	}
}
