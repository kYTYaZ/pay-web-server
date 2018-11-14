package com.wldk.framework.spring.db.dialect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.db.DataSourceName;

/**
 *  产生Dialect对象的工厂类<br>
 * @author Administrator
 *
 */
public class SpringDialectFactory {
	 private Logger logger = LoggerFactory.getLogger(SpringDialectFactory.class);
	
	/**单例*/
	private static SpringDialectFactory instance = new SpringDialectFactory();
	/** 获取单例 */
	public static SpringDialectFactory getInstance() {
		return instance;
	}
	/** 工厂方法 */
	public SpringDialect getDialect(DataSourceName dsType) {
		switch (dsType) {
		case DB2:
			return new SpringDB2Dialect();
		case SYBASE_IQ:
			return new SpringSybaseIQDialect();
		case ORACLE:
			return new SpringOracleDialect();
	    }
		logger.error("无数据库选择器");
		return null;
		
	}
}