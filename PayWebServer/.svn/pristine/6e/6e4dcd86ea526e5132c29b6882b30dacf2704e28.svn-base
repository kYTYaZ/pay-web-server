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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * 适合DB2的分页处理
 * @author Administrator
 *
 */
public class SpringDB2Dialect implements SpringDialect {
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(getClass());	
	
	
	public SpringDB2Dialect(){
		log.debug("数据库类型：DB2");
	}
	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,String sql, PageVariable page) throws SQLException {
	  if(StringUtil.isNotEmpty(sql)){
			if (page != null) {
				sql = getDialectSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));		  
	  }else{
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException(" Can not acquire SQL ");
	  }

	}

	/**
	 * db2分页模板
	 * @param jdbcTemplate 数据库
	 * @param sql 原始SQL 
	 * @param page 分页对象
	 * @return
	 */
	private String getDialectSQL(JdbcTemplate jdbcTemplate, String sql,PageVariable page) {
		if ((jdbcTemplate != null) && (sql != null) && (page != null)) {
			StringBuffer tql = new StringBuffer("SELECT COUNT(1) FROM (");
			tql.append(sql).append(") AS T FETCH FIRST 1 ROWS ONLY");
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
			s.append("SELECT * FROM (");

			StringBuffer q = new StringBuffer(sql);
			q.insert(sql.indexOf("FROM"), ",ROWNUMBER() OVER ( " + this.getOrderByInfo(sql) + " ) AS RN ");
			 
			int currentPage =  page.getCurrentPage() == 0 ? 1 : page.getCurrentPage();
			page.setCurrentPage(currentPage);
				
			int	start = (currentPage - 1) * page.getRecordPerPage() + 1;
			int end = start + page.getRecordPerPage() - 1;
			
			s.append(q).append(") AS T1 WHERE T1.RN BETWEEN ").append(start).append(" AND ").append(end);
			
			return s.toString();
		}
		
		return null;
	}
	
	/**
	 * 根据提供的分页信息生成SQL语句
	 * @param jdbcTemplate
	 * @param sql
	 * @param page
	 * @return
	 */
	private String getPagingSQL(JdbcTemplate jdbcTemplate, String sql,PageVariable page){
		
		if ((jdbcTemplate != null) && (sql != null) && (page != null)) {

			StringBuffer s = new StringBuffer();
			s.append("SELECT * FROM (");
			StringBuffer q = new StringBuffer(sql);
			q.insert(sql.indexOf("FROM"), ",ROWNUMBER() OVER ("
					+ this.getOrderByInfo(sql) + ") AS RN ");

			int currentPage = page.getCurrentPage();
			int recordPerPage = page.getRecordPerPage();

			int start = (currentPage - 1) * recordPerPage + 1;
			int end = start + recordPerPage - 1;
			
			s.append(q)
			 .append(") AS T1 WHERE T1.RN BETWEEN ")
			 .append(start)
			 .append(" AND ")
			 .append(end);
			 
			return s.toString();
		}
		return null;
	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			SQLParse sqlParse, PageVariable page) throws Exception {
		String sql = sqlParse.parse();
		if(StringUtil.isNotEmpty(sql)){
			if (page != null) {
				sql = getPagingSQL(jdbcTemplate, sql.trim(), page);
			}
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		}else{
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");
		}

	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,
			String sql) throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		}else{
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL ");
		}
	}

	public List<Map<String, Object>> getResultMap(JdbcTemplate jdbcTemplate,SQLParse sqlParse) throws Exception {
		String sql = sqlParse.parse();
		if (StringUtil.isNotEmpty(sql)) {
			this.log.debug("SQL:[" + sql.trim() + "]");
			return FieldToPropertyUtils.tofupperCaseList(jdbcTemplate.queryForList(sql));
		}else{
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
			return (List<Object[]>)jdbcTemplate.query(sql, new ResultSetExtractor() {
				public Object extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					List<Object[]> result = new ArrayList<Object[]>();
					ResultSetMetaData rsm = rs.getMetaData();
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
			this.log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
			throw new SQLException("Can not acquire SQL of Pagination ");
		}
	}

	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String sql)
			throws SQLException {
		if (StringUtil.isNotEmpty(sql)) {
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
	
	public List<Object[]> getResultArrays(JdbcTemplate jdbcTemplate, String sql, PageVariable page)
            throws SQLException {
        if (StringUtil.isNotEmpty(sql)) {
            this.log.debug("SQL:[" + sql.trim() + "]");
            return (List<Object[]>)jdbcTemplate.query(sql,new ResultSetExtractor() {
                public  Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    List<Object[]> result = new ArrayList<Object[]>();
                    // 获取表元数据对象
                    ResultSetMetaData rsm = rs.getMetaData();
                    // 获取列数
                    int colNum = rsm.getColumnCount();
                     while(rs.next()){
                         Object[] row = new Object[colNum];
                            for (int i = 1, n = colNum; i <= n; i++) {
                                row[i - 1] = rs.getObject(i);
                            }
                            result.add(row);
                     }  
                     return result; 
                     }  });
        }else{
			this.log.error("数据库操作失败： [ Can not acquire SQL ]");
			throw new SQLException("Can not acquire SQL  ");
        }
    }
	
	private String getOrderByInfo(String sql){
		
		Pattern p = Pattern.compile("(?i)select\\s+.*(?i)where\\s+.*((?i)order.*((?i)desc|(?i)asc))",Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(sql);
		
		if(m.find()) return  m.group(1);
		
		return "";
	}
}