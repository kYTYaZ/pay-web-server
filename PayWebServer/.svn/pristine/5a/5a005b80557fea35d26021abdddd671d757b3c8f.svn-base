/**
 * 
 */
package com.wldk.framework.db.dialect;

import com.wldk.framework.db.DataSourceName;

/**
 * 产生Dialect对象的工厂类<br>
 * 
 * @author Administrator
 * 
 */
public class DialectFactory {
	/** 单例 */
	private static DialectFactory instance = new DialectFactory();

	private DialectFactory() {
	}

	/** 获取单例 */
	public static DialectFactory getInstance() {
		return instance;
	}

	/** 工厂方法 */
	public Dialect getDialect(DataSourceName dsType) {
		switch (dsType) {
		case DB2:
			return new DB2Dialect();
		case SYBASE_IQ:
			return new SybaseIQDialect();
		case ORACLE:
			return new OracleDialect();
	    }
		return null;
	}
}
