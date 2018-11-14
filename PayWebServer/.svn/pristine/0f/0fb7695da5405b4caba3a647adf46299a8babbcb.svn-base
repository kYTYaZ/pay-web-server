/*
 * Copyright (C), 2012-2014, 上海华腾软件系统有限公司
 * FileName: TransactAdvice.java
 * Author:   justin
 * Date:     2014-9-28 下午1:49:21
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.huateng.pay.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.dao.WldkJdbcUtil;
import com.wldk.framework.db.TransactionProxy;

/**
 * 事物拦截器
 * @author justin
 * @see 1.0
 * @since 1.0
 */
public class TransactInterceptor implements MethodInterceptor {
    private Logger logger = LoggerFactory.getLogger(TransactInterceptor.class);

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        logger.debug("service transction start");
        Object obj;
        TransactionProxy tx = WldkJdbcUtil.currentProxy();
        WldkJdbcUtil.setTransFilter();
        try {
            obj = methodInvocation.proceed();
            // 事物提交
            tx.commit();
        } catch (Exception e) {
            // 事物回滚
            tx.rollback();
            logger.error("service transaction error:" + e.getMessage());
            throw e;
        }finally{
//            tx = null;
        }
        return obj;
    }

}
