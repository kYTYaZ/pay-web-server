/**
 * 
 */
package com.wldk.framework.db;

/**
 * 数据源名称常量定义<br>
 * 
 * @author Administrator
 * 
 */
public enum DataSourceName {
	DB2("db2"), SYBASE_IQ("sybase_iq"), MYSQL("mysql"), INFORMIX("informix"), MANUAL("manual"), ORACLE("oracle");
	/** 数据源名称定义 */
	public String ds;

	/** 构造方法 */
	DataSourceName(String ds) {
		this.ds = ds;
	}

	public String getDs() {
		return ds;
	}

	public static DataSourceName ds(String ds) {
		if (ds == null || ds.equals("")) {
			return DataSourceName.SYBASE_IQ;
		} else if (ds.trim().equalsIgnoreCase("db2")) {
			return DataSourceName.DB2;
		} else if (ds.trim().equalsIgnoreCase("sybase_iq")) {
			return DataSourceName.SYBASE_IQ;
		} else if (ds.trim().equalsIgnoreCase("mysql")) {
			return DataSourceName.MYSQL;
		} else if (ds.trim().equalsIgnoreCase("informix")) {
			return DataSourceName.INFORMIX;
		} else if (ds.trim().equalsIgnoreCase("manual")) {
			return DataSourceName.MANUAL;
		} else if (ds.trim().equalsIgnoreCase("oracle")) {
			return DataSourceName.ORACLE;
		}
		return DataSourceName.SYBASE_IQ;
	}
}
