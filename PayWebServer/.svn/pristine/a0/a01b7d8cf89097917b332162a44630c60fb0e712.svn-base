package com.wldk.framework.dao;

import com.wldk.framework.db.DataSourceName;

/**
 * 数据库操作工厂（支持数据库事物代理、查询、分页、新增、修改、删除）
 * 
 * @author Administrator
 * 
 */
public class JdbcTransactionManagerFactory {
	/**
	 * 默认数据库查询
	 * 
	 * @return
	 */
	public static JdbcDaoUtilsFromWorkAware getFrameworkFacadeCglib() {
		return new JdbcDaoUtilsFromWorkAware();
	}

	/**
	 * 自定义数据类型
	 * 
	 * @param dsName
	 * @return
	 */
	public static JdbcDaoUtilsFromWorkAware getFrameworkFacadeCglib(
			DataSourceName dsName) {
		return new JdbcDaoUtilsFromWorkAware(dsName);
	}

	public static void main(String[] args) {
	}
}
