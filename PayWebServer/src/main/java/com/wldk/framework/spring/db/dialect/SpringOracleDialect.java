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
 * 适合Oracle的分页处理<br>
 * 
 * @author Administrator
 * 
 */
public class SpringOracleDialect implements SpringDialect {
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(getClass());

	public SpringOracleDialect() {
		log.info("数据库类型：ORACLE");
	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			String sql, PageVariable page) throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate
					.queryForList(sql));
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL ");
		}
	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			SQLParse sqlParse, PageVariable page) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate
					.queryForList(sql));
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");
		}

	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			String sql) throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate
					.queryForList(sql));
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL");
		}
	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			SQLParse sqlParse) throws Exception {
		String sql = sqlParse.parse();
		this.log.debug("SQL:[" + sql.trim() + "]");
		if (StringUtil.isNotEmpty(sql)) {
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		} else {
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");
		}
	}

	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String sql)
			throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return (List<Object[]>) jdbcTemplate.query(sql,
					new ResultSetExtractor() {
						public Object extractData(ResultSet rs)
								throws SQLException, DataAccessException {
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

	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate,
			String sql, PageVariable page) throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return (List<Object[]>) jdbcTemplate.query(sql,
					new ResultSetExtractor() {
						public Object extractData(ResultSet rs)
								throws SQLException, DataAccessException {
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
			SQLParse sqlParse) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return (List<Object[]>) jdbcTemplate.query(sql,
					new ResultSetExtractor() {
						public Object extractData(ResultSet rs)
								throws SQLException, DataAccessException {
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
			SQLParse sqlParse, PageVariable page) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return (List<Object[]>) jdbcTemplate.query(sql,
					new ResultSetExtractor() {
						public Object extractData(ResultSet rs)
								throws SQLException, DataAccessException {
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

	private String getDialectSQL(JdbcTemplate jdbcTemplate, String sql,
			PageVariable page) {
		if ((jdbcTemplate != null) && (sql != null) && (page != null)) {
			StringBuffer tql = new StringBuffer("SELECT COUNT(1) FROM (");
			tql.append(sql).append(") T ");
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
			s.append("SELECT * FROM ( SELECT T1.*, ROWNUM RN FROM ( ");

			StringBuffer q = new StringBuffer(sql);

			int start = 1;

			if (page.getCurrentPage() != 1) {
				start = (page.getCurrentPage() - 1) * page.getRecordPerPage()
						+ 1;
			}

			int end = start + page.getRecordPerPage() - 1;
			if (end > page.getRecordCount()) {
				end = page.getRecordCount();
			}

			if (start > page.getRecordCount()) {
				start = (page.getPageCount() - 1) * page.getRecordPerPage() + 1;
			}
			s.append(q).append(
					") T1 WHERE ROWNUM <= " + end + " ) WHERE RN >=" + start);
			return s.toString();
		}
		return null;
	}

}