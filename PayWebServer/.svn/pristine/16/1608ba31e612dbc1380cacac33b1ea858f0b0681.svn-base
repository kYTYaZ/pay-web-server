/**
 * 
 */
package com.wldk.framework.db.dialect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.db.PageVariable;
import com.wldk.framework.utils.FieldToPropertyUtils;

/**
 * 适合SybaseIQ的分页处理<br>
 * 
 * @author Administrator
 * 
 */
public class SybaseIQDialect implements Dialect {
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(getClass());

	/** 构造方法 */
	public SybaseIQDialect() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.dialect.Dialect#getResult(org.apache.commons.dbutils.QueryRunner,
	 *      java.lang.String, com.mingdeng.core.db.Pagination)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getResult(QueryRunner runner, String sql,
			final PageVariable page) throws SQLException {
		// TODO Auto-generated method stub
		return (List<Object[]>) runner.query(sql, new ResultSetHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
			 */
			public Object handle(ResultSet rs) throws SQLException {
				// TODO Auto-generated method stub
				List<Object[]> result = new ArrayList<Object[]>();
				// 调整记录指针
				if (page != null && page.getCurrentPage() > 1) {
					rs.absolute((page.getCurrentPage() - 1)
							* page.getRecordPerPage());
				}
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
	}
	

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getResultMap(QueryRunner runner, String sql,final PageVariable page) throws SQLException {
		// TODO Auto-generated method stub
		return (List<Map<String, Object>>) runner.query(sql, new ResultSetHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.apache.commons.dbutils.ResultSetHandler#handle(java.sql.ResultSet)
			 */
			public Object handle(ResultSet rs) throws SQLException {
				// TODO Auto-generated method stub
				List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
				// 调整记录指针
				if (page != null && page.getCurrentPage() > 1) {
					rs.absolute((page.getCurrentPage() - 1)
							* page.getRecordPerPage());
				}
				// 获取表元数据对象
				ResultSetMetaData rsm = rs.getMetaData();
				// 获取列数
				int colNum = rsm.getColumnCount();
				while (rs.next()) {
					Map<String, Object> map=new HashMap<String, Object>();
					for (int i = 1, n = colNum; i <= n; i++) {
		              map.put(FieldToPropertyUtils.fieldToProperty(rsm.getColumnName(i)), rs.getObject(i));
					}
					result.add(map);
				}
				return result;
			}
		});
	}	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bankcomm.data.dialect.Dialect#getDialectSQL(java.lang.String,
	 *      com.bankcomm.data.Pagination)
	 */
	public String getDialectSQL(QueryRunner runner, String sql, PageVariable page) {
		// TODO Auto-generated method stub
		if (runner != null && sql != null && page != null) {
			// 查询总记录数，并计算分页对象
			StringBuffer tql = new StringBuffer("SELECT COUNT(1) FROM (");
			tql.append(sql);
			// 去掉子查询中的ORDER BY子句，SYBASE IQ不支持
			if (tql.indexOf("ORDER") >= 0) {
				tql.delete(tql.indexOf("ORDER"), tql.length());
			}
			if (tql.indexOf("order") >= 0) {
				tql.delete(tql.indexOf("order"), tql.length());
			}
			tql.append(") AS T ");
			log.debug("SQL:[" + tql + "]");
			try {
				Object[] result = (Object[]) runner.query(tql.toString(),
						new ArrayHandler());
				// 总记录数
				int count = result != null && result.length > 0 ? new Integer(
						result[0].toString()) : 0;
				log.debug("record count：" + count);
				// 设置总记录数
				page.setRecordCount(count);
				// 计算总页数
				int pages = count / page.getRecordPerPage();
				if (count % page.getRecordPerPage() != 0) {
					pages++;
				}
				page.setPageCount(pages);
			} catch (SQLException e) {
				log.error(e.getMessage(),e);
				return null;
			}
			StringBuffer s = new StringBuffer();
			int top = page.getCurrentPage() * page.getRecordPerPage();
			if (top > page.getRecordCount()) { // 如果获取的记录数大于总记录，则获取记录等于总记录数
				top = page.getRecordCount();
			}
			String key = "SELECT";
			s.append(sql.trim());
			if ((s.indexOf("DISTINCT") >= 0) || (s.indexOf("distinct") >= 0)) {
				key += " DISTINCT";
			}
			s.insert(key.length(), " TOP " + (top <= 0 ? 1 : top));
			// log.debug(s);
			return s.toString();
		}
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
