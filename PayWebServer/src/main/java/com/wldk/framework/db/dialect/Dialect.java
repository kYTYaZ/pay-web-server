package com.wldk.framework.db.dialect;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;

import com.wldk.framework.db.PageVariable;

/**
 * 数据库类型 选择器
 * 
 * @author Administrator
 * 
 */
public interface Dialect {
	/** 获取某种类型的数据库SQL查询字符串，主要针对需要分页的情况 */
	String getDialectSQL(QueryRunner runner, String sql, PageVariable page);

	/** 返回查询结果集合 */
	List<Object[]> getResult(QueryRunner runner, String sql, PageVariable page)
			throws SQLException;

	/** 返回查询结果集合 */
	public List<Map<String, Object>> getResultMap(QueryRunner runner,
			String sql, PageVariable page) throws SQLException;
}
