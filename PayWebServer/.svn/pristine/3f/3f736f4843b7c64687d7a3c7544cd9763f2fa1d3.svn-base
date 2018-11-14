package com.wldk.framework.spring.db.dialect;

import com.wldk.framework.db.PageVariable;
import com.wldk.framework.db.parse.SQLParse;
import com.wldk.framework.utils.FieldToPropertyUtils;
import com.wldk.framework.utils.StringUtil;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
/**
 * 适合SybaseIQ的分页处理
 * @author Administrator
 *
 */
public class SpringSybaseIQDialect implements SpringDialect {
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(getClass());
   
	
	public SpringSybaseIQDialect(){
		log.info("数据库类型：SYBASE_IQ");
	}
	
	/**
	 * 
	 */
	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,String sql, final PageVariable page) throws SQLException {
		if(StringUtil.isNotEmpty(sql)){
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		}else{
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL");
		}
	}
	
	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			String sql) throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		}else{
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL");			
		}
	}
	
	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,SQLParse sqlParse,PageVariable page) throws Exception {
		String sql = sqlParse.parse();
		if(StringUtil.isNotEmpty(sql)){
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));			
		}else{
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");			
		}
	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			SQLParse sqlParse) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		}else{
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");					
		}
	}
	
	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String sql, PageVariable page)
            throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return (List<Object[]>)jdbcTemplate.query(sql, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					List<Object[]> result = new ArrayList<Object[]>();
					// 获取表元数据对象
					ResultSetMetaData rsm = rs.getMetaData();
					// 获取列数
					int colNum = rsm.getColumnCount();
					while (rs.next()) {
						Object[] row = new Object[colNum];
						for (int i = 1, n = colNum; i <= n; i++) {
							row[i - 1] = rs.getObject(i);
						}
						result.add(row);
					}
					return result;
				}
			});
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL ");
		}
    }

	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,
			SQLParse sqlParse, PageVariable page) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.info("SQL:[" + sql.trim() + "]");
			return (List<Object[]>)jdbcTemplate.query(sql, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					List<Object[]> result = new ArrayList<Object[]>();
					// 获取表元数据对象
					ResultSetMetaData rsm = rs.getMetaData();
					// 获取列数
					int colNum = rsm.getColumnCount();
					while (rs.next()) {
						Object[] row = new Object[colNum];
						for (int i = 1, n = colNum; i <= n; i++) {
							row[i - 1] = rs.getObject(i);
						}
						result.add(row);
					}
					return result;
				}
			});
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");
		}
	}

	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,
			SQLParse sqlParse) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			this.log.info("SQL:[" + sql.trim() + "]");
			return (List<Object[]>)jdbcTemplate.query(sql, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					List<Object[]> result = new ArrayList<Object[]>();
					// 获取表元数据对象
					ResultSetMetaData rsm = rs.getMetaData();
					// 获取列数
					int colNum = rsm.getColumnCount();
					while (rs.next()) {
						Object[] row = new Object[colNum];
						for (int i = 1, n = colNum; i <= n; i++) {
							row[i - 1] = rs.getObject(i);
						}
						result.add(row);
					}
					return result;
				}
			});
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");
		}
	}

	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String sql)
			throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			this.log.info("SQL:[" + sql.trim() + "]");
			return (List<Object[]>)jdbcTemplate.query(sql, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					List<Object[]> result = new ArrayList<Object[]>();
					// 获取表元数据对象
					ResultSetMetaData rsm = rs.getMetaData();
					// 获取列数
					int colNum = rsm.getColumnCount();
					while (rs.next()) {
						Object[] row = new Object[colNum];
						for (int i = 1, n = colNum; i <= n; i++) {
							row[i - 1] = rs.getObject(i);
						}
						result.add(row);
					}
					return result;
				}
			});
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL");
		}
	}
	
	public String getDialectSQL(JdbcTemplate jdbcTemplate, String sql,
			PageVariable page) {
		if ((jdbcTemplate != null) && (sql != null) && (page != null)) {
			StringBuffer tql = new StringBuffer("SELECT COUNT(1) FROM (");
			tql.append(sql);

			if (tql.indexOf("ORDER") >= 0) {
				tql.delete(tql.indexOf("ORDER"), tql.length());
			}
			if (tql.indexOf("order") >= 0) {
				tql.delete(tql.indexOf("order"), tql.length());
			}
			tql.append(") AS T ");
			this.log.debug("SQL:[" + tql + "]");
			try {
				int count = jdbcTemplate.queryForInt(tql.toString());
				this.log.debug("record count：" + count);

				page.setRecordCount(count);

				int pages = count / page.getRecordPerPage();
				if (count % page.getRecordPerPage() != 0) {
					pages++;
				}
				page.setPageCount(pages);
			} catch (Exception e) {
				this.log.error(e.getMessage(),e);
				return null;
			}
			StringBuffer s = new StringBuffer();
			int top = page.getCurrentPage() * page.getRecordPerPage();
			if (top > page.getRecordCount()) {
				top = page.getRecordCount();
			}
			String key = "SELECT";
			s.append(sql.trim());
			if ((s.indexOf("DISTINCT") >= 0) || (s.indexOf("distinct") >= 0)) {
				key = key + " DISTINCT";
			}
			s.insert(key.length(), " TOP " + (top <= 0 ? 1 : top));

			return s.toString();
		}
		return null;
	}
}