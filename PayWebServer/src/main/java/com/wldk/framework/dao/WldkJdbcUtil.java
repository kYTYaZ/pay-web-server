package com.wldk.framework.dao;

import com.wldk.framework.db.TransactionManager;
import com.wldk.framework.db.TransactionProxy;

/**
 * 
 * 持久层数据库框架线程局部变量
 * 
 * @author justin
 * @see 1.0
 * @since 1.0
 */
public class WldkJdbcUtil {
    /**
     * 线程局部变量
     */
    public static final ThreadLocal<TransactionProxy> session = new ThreadLocal<TransactionProxy>();
    public static final ThreadLocal<String> transFilter = new ThreadLocal<String>();

    /**
     * 
     * 获取局部变量对象
     * 
     * @return
     * @see 1.0
     * @since 1.0
     */
    public static TransactionProxy currentProxy() {
        TransactionProxy tx = (TransactionProxy) session.get();
        if (tx == null || "null".equals(tx)) {
            tx = TransactionManager.getInstance().getProxy();
            session.set(tx);
        }
        return tx;
    }
    
    public static void setTransFilter(){
        transFilter.set("1");
    }
    public static String getTransFilter(){
        return transFilter.get();
    }
}
