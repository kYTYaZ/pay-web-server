package com.wldk.framework.spring.db.dialect;

import com.wldk.framework.db.PageVariable;
import com.wldk.framework.db.parse.SQLParse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
/**
 * 数据库渠道类型接口
 * @author Administrator
 *
 */
public abstract interface SpringDialect{
	 
  /**
   * 数据库SQL模板查询（支持分页）
   * @param jdbcTemplate 数据库连接
   * @param sqlParse SQL模板
   * @param pageVariable 分页对象
   * @return
   * @throws Exception
   */
  public abstract List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate, SQLParse sqlParse, PageVariable pageVariable)throws Exception;

	/**
	 * 数据库SQL查询（支持分页）
	 * @param jdbcTemplate 数据库连接
	 * @param SQL 查询语句
	 * @param pageVariable 查询语句
	 * @return
	 * @throws SQLException
	 */
  public abstract List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate, String SQL, PageVariable pageVariable)throws SQLException;

	/**
	  * 数据库SQL查询（不支持分页）
	  * @param jdbcTemplate 数据库连接
	  * @param SQL 查询语句
	  * @return
	  * @throws SQLException
	  */
  public abstract List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate, String SQL)throws SQLException;

  /**
   * 数据库SQL模板查询（不支持分页）
   * @param jdbcTemplate 数据库连接
   * @param sqlParse SQL模板
   * @return
   * @throws Exception
   */
  public abstract List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate, SQLParse sqlParse)throws Exception; 
  
  /**
   * 数据库SQL模板查询（支持分页）
   * @param jdbcTemplate数据库连接
   * @param sqlParse  SQL模板
   * @param page 分页对象
   * @return
   * @throws Exception
   */
  public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,SQLParse sqlParse, PageVariable page) throws Exception ;
  
  /**
   * 数据库SQL模板查询（不支持分页）
   * @param jdbcTemplate 数据库连接
   * @param sqlParse SQL模板
   * @return
   * @throws Exception
   */
  public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,SQLParse sqlParse) throws Exception;
  
  
  /**
   * 数据SQL查询（支持分页）
   * @param jdbcTemplate 数据库连接
   * @param SQL 查询语句
   * @param pageVariable 查询语句
   * @return
   * @throws SQLException
   */
  public abstract List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String SQL, PageVariable pageVariable)throws SQLException;
  
  /**
   * 数据SQL查询（不支持分页）
   * @param jdbcTemplate  数据库连接
   * @param sql  查询语句
   * @return
   * @throws SQLException
   */
  public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String sql)throws SQLException ;
  
}