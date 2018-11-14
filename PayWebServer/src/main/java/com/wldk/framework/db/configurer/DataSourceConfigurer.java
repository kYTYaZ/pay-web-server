/**
 * 
 */
package com.wldk.framework.db.configurer;

/**
 * 读取配置文件的接口
 * 
 * @author Administrator
 * 
 */
public interface DataSourceConfigurer<E> {
	/**
	 * 初始化数据源
	 * 
	 * @param arg
	 *            泛化参数
	 * @throws Exception
	 */
	void initDataSource(E arg) throws Exception;

	/**
	 * 关闭数据源
	 * 
	 * @param arg
	 *            泛化参数
	 * @throws Exception
	 */
	void shutdownDataSource(E arg) throws Exception;
}
