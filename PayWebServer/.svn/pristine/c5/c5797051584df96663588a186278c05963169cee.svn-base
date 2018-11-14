/**
 * 
 */
package com.wldk.framework.db;

import com.wldk.framework.db.parse.SQLParse;


/**
 * 数据库CRUD命令对象生成器。生成器模式<br>
 * 
 * @author Administrator
 * 
 */
public class DataOperationCreator {
	/** 数据源类型常量 */
	private DataSourceName dsName;

	/**
	 * 构造方法
	 * 
	 * @param dsType
	 */
	protected DataOperationCreator(DataSourceName dsName) {
		this.dsName = dsName;
	}

	/** 返回对象 */
	public static DataOperationCreator getInstance(DataSourceName dsName) {
		return new DataOperationCreator(dsName);
	}

	/** 创建查询命令对象 */
	public Query createQuery(SQLParse sqlParse, PageVariable page) {
		return new Query(sqlParse, page, dsName);
	}

	/** 创建查询命令对象 */
	public Query createQuery(PageVariable page) {
		return new Query(page, dsName);
	}

	/** 创建更新操作的命令对象 */
	public Update createUpdate(SQLParse sqlParse) {
		return new Update(sqlParse, dsName);
	}
	
	/** 创建更新操作的命令对象 */
	public UpdateSql createUpdate(String sqlParse) {
		return new UpdateSql(sqlParse, dsName);
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
