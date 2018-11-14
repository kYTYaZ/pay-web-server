/*
 * Copyright (C), 2012-2014, 涓婃捣鍗庤吘杞欢绯荤粺鏈夐檺鍏徃
 * FileName: CacheListener.java
 * Author:   justin
 * Date:     2014-8-19 涓嬪崍7:51:49
 * Description: //妯″潡鐩殑銆佸姛鑳芥弿杩�     
 * History: //淇敼璁板綍
 * <author>      <time>      <version>    <desc>
 * 淇敼浜哄鍚�            淇敼鏃堕棿            鐗堟湰鍙�                 鎻忚堪
 */
package com.huateng.pay.listener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.util.Constants;
import com.huateng.pay.handler.pool.ThreadPool;
import com.huateng.pay.handler.task.QueueHandlerTask;
import com.huateng.pay.handler.thread.SocketServerHandler;
import com.huateng.pay.handler.thread.ThreadNotifyHelper;
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

/**
 * socket监听器
 * 
 * @author guohuan
 * @see 1.0
 * @since 1.0
 */
public class QRCodeSocketServerListener extends BaseLinstener {
    private Logger logger = LoggerFactory.getLogger(QRCodeSocketServerListener.class);
    private SocketServerHandler socketServerHandler;

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    	
    }
    
    /**
     * 丰收家查询三码合一流水队列
     */
    public static BlockingQueue<SocketRequest> queue = null;

    /**
     * 
     *初始化
     * 
     * @see 1.0
     * @since 1.0
     */
    public void init() {
        logger.info("启动微信socket服务");
        WxPayService wxPayService = (WxPayService) getBean("wxPayService");
        ILocalBankService localBankService = (ILocalBankService) getBean("localBankService");
        ITimingTaskService timingTaskService = (ITimingTaskService) getBean("timingTaskService");
        WxMerchantSynchService wxMerchantSynchService = (WxMerchantSynchService) getBean("wxMerchantSynchService");
        AliPayMerchantSynchService aliPayMerchantSynchService = (AliPayMerchantSynchService) getBean("aliPayMerchantSynchService");
        AliPayPayService alipayPayService= (AliPayPayService) getBean("alipayPayService");
        IStaticQRCodeService  staticQRCodeService  = (IStaticQRCodeService)getBean("staticQRCodeService");
        ThreadPool QRCodePool = (ThreadPool)getBean("QRCodePool");
        IThreeCodeStaticQRCodeService threeCodeStaticQRCodeService = (IThreeCodeStaticQRCodeService)getBean("threeCodeStaticQRCodeService");
        ICupsPayService cupsPayService = (ICupsPayService) getBean("cupsPayService");
        IThreeCodeService threeCodeService = (IThreeCodeService)getBean("threeCodeService");
        socketServerHandler = new SocketServerHandler(wxPayService,localBankService,wxMerchantSynchService,QRCodePool,timingTaskService,aliPayMerchantSynchService,staticQRCodeService,threeCodeStaticQRCodeService,alipayPayService,threeCodeService, cupsPayService);
        socketServerHandler.start();
        QueueHandlerTask queueHandlerTask = (QueueHandlerTask)getBean("queueHandlerTask");
        ThreadNotifyHelper.setQueueHandlerTask(queueHandlerTask);
        queueHandlerTask.start();
        queue = new LinkedBlockingDeque<SocketRequest>(Integer.parseInt(Constants.getParam("socketConnNum")));
    }

}
