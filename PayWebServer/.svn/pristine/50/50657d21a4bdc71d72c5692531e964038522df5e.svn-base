/**
 * 
 */
package com.wldk.framework.db;

import java.sql.SQLException;

/**
 * 事务处理接口
 * 
 * @author Administrator
 * 
 */
public interface TransactionProxy {
    /**
     * 设置命令目标对象
     * 
     * @param target 数据库操作命令目标对象
     * @return TransactionProxy
     */
    TransactionProxy setTarget(AbstractDataOperation<Integer> target);

    /**
     * 事务提交
     */
    void commit() throws SQLException;

    void rollback();
}
