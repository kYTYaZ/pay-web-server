package com.wldk.framework.dao;

import com.wldk.framework.db.DataSourceName;

/**
 * 数据库查询公共入口（支持数据库事物代理）
 * 
 * @author Administrator
 * 
 */
public class JdbcDaoUtilsFromWorkAware extends JdbcTransactionManager2 {

	public JdbcDaoUtilsFromWorkAware(DataSourceName dsName) {
		super(dsName);
	}

	public JdbcDaoUtilsFromWorkAware() {
		super();
	}

}
