/**
 * 
 */
package com.wldk.framework.db;

import java.sql.SQLException;

/**
 * 数据库访问接口，使用命令模式设计，实现数据库的CRUD功能。<br>
 * 返回值参数化，查询操作返回List<Object[]>对象，增删改操作返回Integer<br>
 * 
 * @author Administrator
 * 
 */
public interface DataOperationCommand<T> {
	/** 实现命令模式 */
	T execute() throws SQLException;
}
