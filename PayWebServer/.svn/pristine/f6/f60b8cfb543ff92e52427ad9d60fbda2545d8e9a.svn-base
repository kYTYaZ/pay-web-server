/**
 * 
 */
package com.wldk.framework.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.text.StrBuilder;

import com.wldk.framework.db.dialect.Dialect;
import com.wldk.framework.db.dialect.DialectFactory;
import com.wldk.framework.db.parse.SQLParse;

/**
 * @author Administrator
 * 
 */
public class Query extends AbstractDataOperation<List<Object[]>> {
	/** 分页 */
	private PageVariable page;

	/**
	 * @param dsName
	 */
	public Query(DataSourceName dsName) {
		super(dsName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param page
	 * @param dsName
	 */
	public Query(PageVariable page, DataSourceName dsName) {
		super(dsName);
		// TODO Auto-generated constructor stub
		this.page = page;
	}

	/**
	 * @param sqlParse
	 * @param dsType
	 */
	public Query(SQLParse sqlParse, DataSourceName dsName) {
		super(sqlParse, dsName);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sqlParse
	 * @param page
	 * @param dsType
	 */
	public Query(SQLParse sqlParse, PageVariable page, DataSourceName dsName) {
		super(sqlParse, dsName);
		this.page = page;
	}

	/**
	 * @return the page
	 */
	public PageVariable getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(PageVariable page) {
		this.page = page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.DataOperationCommand#execute()
	 */
	public List<Object[]> execute() throws SQLException {
		// TODO Auto-generated method stub
		// 获取数据库方言
		Dialect dialect = DialectFactory.getInstance().getDialect(dsName);
		try {
			// 获取由SQL解析器解析的SQL字符串
			if (sqlParse != null) {
				String sql = sqlParse.parse();
				return query(sql, dialect);
			} else {
				throw new SQLException("Can't Fetching SQLParse Object.");
			}
		} catch (SQLException se) {
			log.error("数据库操作失败： [ "+se+" ]");
			throw se;
		} catch (Exception e) {
			log.error("数据库操作失败： [ "+e+" ]");
			throw new SQLException(e);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.DataOperationCommand#execute()
	 */
	public List<Map<String,Object>> executeMap() throws SQLException {
		// TODO Auto-generated method stub
		// 获取数据库方言
		Dialect dialect = DialectFactory.getInstance().getDialect(dsName);
		try {
			// 获取由SQL解析器解析的SQL字符串
			if (sqlParse != null) {
				String sql = sqlParse.parse();
				return queryMap(sql, dialect);
			} else {
				throw new SQLException("Can't Fetching SQLParse Object.");
			}
		} catch (SQLException se) {
			log.error("数据库操作失败： [ "+se+" ]");
			throw se;
		} catch (Exception e) {
			log.error("数据库操作失败： [ "+e+" ]");
			throw new SQLException(e);
		}
	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 * @param dialect
	 * @return
	 * @throws SQLException
	 */
	protected List<Object[]> query(String sql, Dialect dialect)
			throws SQLException {
		try {
			// 如果需要分页查询，则需要对SQL查询语句做转换
			if (page != null) {
				sql = dialect.getDialectSQL(runner, sql.trim(), page);
				// 如果为null，则抛出异常
				if (sql == null) {
					log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
					throw new SQLException("Can not acquire SQL of Pagination ");
				}
			}
			log.info("SQL:[" + sql.trim() + "]");
			return dialect.getResult(runner, sql, page);
		} catch (SQLException se) {
			log.error("数据库操作失败： [ "+se+" ]");
			throw se;
		} catch (Exception e) {
			log.error("数据库操作失败： [ "+e+" ]");
			throw new SQLException(e);
		}
	}
	
	/**
	 * 查询方法
	 * 
	 * @param sql
	 * @param dialect
	 * @return
	 * @throws SQLException
	 */
	protected List<Map<String, Object>> queryMap(String sql, Dialect dialect)
			throws SQLException {
		try {
			// 如果需要分页查询，则需要对SQL查询语句做转换
			if (page != null) {
				sql = dialect.getDialectSQL(runner, sql.trim(), page);
				// 如果为null，则抛出异常
				if (sql == null) {
					log.error("数据库操作失败： [ Can not acquire SQL of Pagination ]");
					throw new SQLException("Can not acquire SQL of Pagination ");
				}
			}
			log.info("SQL:[" + sql.trim() + "]");
			return dialect.getResultMap(runner, sql, page);
		} catch (SQLException se) {
			log.error("数据库操作失败： [ "+se+" ]");
			throw se;
		} catch (Exception e) {
			log.error("数据库操作失败： [ "+e+" ]");
			throw new SQLException(e);
		}
	}

	/**
	 * 查询方法
	 * 
	 * @param sql
	 * @return List<Object[]>
	 * @throws SQLException
	 */
	public List<Object[]> query(String sql) throws SQLException {
		// 获取数据库方言
		Dialect dialect = DialectFactory.getInstance().getDialect(dsName);
		return query(sql, dialect);
	}
	
	/**
	 * 查询方法
	 * 
	 * @param sql
	 * @return List<Map<String, Object>>
	 * @throws SQLException
	 */
	public  List<Map<String, Object>> queryMap(String sql) throws SQLException {
		// 获取数据库方言
		Dialect dialect = DialectFactory.getInstance().getDialect(dsName);
		return queryMap(sql, dialect);
	}

	/**
	 * 将查询结果集合转换成字符串形式
	 * 
	 * @param result
	 *            查询结果集合
	 * @return String
	 * @throws SQLException
	 */
	public String listToString(List<Object[]> result) throws SQLException {
		// TODO Auto-generated method stub
		StrBuilder sb = new StrBuilder();
		for (Object[] cols : result) {
			sb.appendln(ArrayUtils.toString(cols));
		}
		return sb.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
