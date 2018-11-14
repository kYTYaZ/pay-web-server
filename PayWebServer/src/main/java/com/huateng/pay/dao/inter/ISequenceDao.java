/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: ISequenceDao.java
 * Author:   justin
 * Date:     2014-12-9 上午10:09:58
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.dao.inter;

import com.huateng.frame.exception.FrameException;

/**
 * 序列生成类
 * @author justin
 * @see 1.0
 * @since 1.0
 */
public interface ISequenceDao {
    /**
     * 查询订单表序列号
     * @return
     * @throws FrameException
     * @see 1.0
     * @since 1.0
     */
    public String getTxnSeqId() throws FrameException;
    
    /**
     * 获取二维码序列号
     * @return
     * @throws FrameException
     */
    public String getQRCodeSeqNo() throws FrameException;
    
    /**
     * 获取静态二维码序列号
     * @return
     * @throws FrameException
     */
    public String getStaticQRCodeSeqNo() throws FrameException;
    
    /**
     * 获取静态二维码流水
     * @return
     * @throws FrameException
     */
    public String getStaticQRCodeTxnNo() throws FrameException;
    
    /**
     * 获取限额表序列号
     * @return
     * @throws FrameException
     */
    public String getLimitTxnNo() throws FrameException;
    
    /**
     * 获取定时器序列号
     * @return
     * @throws FrameException
     */
    public String getTimingTask() throws FrameException;
    
    /**
     * 子商户渠道费率信息关联表序列号
     * @return
     * @throws FrameException
     */
    public String getSubmerChannelRate() throws FrameException ;
}
