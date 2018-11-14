/**
 * 
 */
package com.wldk.framework.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.db.parse.SQLParse;

/**
 * @author Administrator
 * 
 */
public abstract class AbstractDataOperation<T> implements DataOperationCommand<T> {
	/** 日志 */
	protected Logger log = LoggerFactory.getLogger(getClass());

	/** SQL解析器对象 */
	protected SQLParse sqlParse;

	protected String sql;

	/** 数据源类型 */
	protected DataSourceName dsName;

	/** 数据源 */
	protected DataSource ds = null;

	/** 事务处理的连接 */
	protected Connection conn = null;

	/** 是否激活事务的标志变量 */
	protected boolean transactionActive = false;

	/** dbutils */
	protected QueryRunner runner = new QueryRunner();

	/** 本地线程的connection对象 */
	private static final ThreadLocal<Connection> session = new ThreadLocal<Connection>();

	/**
	 * 构造方法
	 */
	protected AbstractDataOperation(DataSourceName dsName) {
		this.dsName = dsName;
		// 获取数据源池
		DataSourcePool pool = DataSourcePool.getInstance(this.dsName);
		if (pool != null) {
			// 获取数据源对象
			ds = pool.getDataSource(UUID.randomUUID().toString(), null);
			runner.setDataSource(ds);
		}
	}

	/**
	 * 构造方法（重载）
	 * 
	 * @param sqlParse
	 *            SQL解析器
	 * @param dsType
	 *            数据源类型(DB2或SYBASEIQ)
	 */
	public AbstractDataOperation(SQLParse sqlParse, DataSourceName dsName) {
		this(dsName);
		this.sqlParse = sqlParse;
	}

	/**
	 * 构造方法（重载）
	 * 
	 * @param sql
	 *            SQL解析器
	 * @param dsType
	 *            数据源类型(DB2或SYBASEIQ)
	 */
	public AbstractDataOperation(String sql, DataSourceName dsName) {
		this(dsName);
		this.sql = sql;
	}

	/***************************************************************************************************************************************************************************************************
	 * 定义setter和getter方法
	 * 
	 * @return
	 */
	public DataSourceName getDsName() {
		// TODO Auto-generated method stub
		return this.dsName;
	}

	/**
	 * 设置数据库连接对象
	 */
	protected void setConnection() {
		// TODO Auto-generated method stub
		if (transactionActive) { // 激活了事务处理
			this.conn = session.get();
			if (this.conn == null) { // 如果为空，则创建一个新的连接
				try {
					this.conn = this.ds.getConnection();
					session.set(this.conn);
				} catch (SQLException sqle) {
					log.error(sqle.getMessage(),sqle);
				}
			}
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public boolean isTransactionActive() {
		return transactionActive;
	}

	public void setTransactionActive(boolean transactionActive) {
		this.transactionActive = transactionActive;
		setConnection();
	}

	/**
	 * 释放事务的数据库连接
	 */
	public static void doReleaseConnection() {
		Logger log = LoggerFactory.getLogger(AbstractDataOperation.class);
		Connection conn = session.get();
		if (conn != null) {
			try {
				conn.setAutoCommit(true);
				conn.close();
				log.info("Closed Transaction JDBC Connection: " + conn);
			} catch (SQLException sqle) {
				log.error(sqle.getMessage(),sqle);
			} finally {
				session.set(null);
			}
		}
	}

	/**
	 * 开始一个事务
	 * 
	 * @throws SQLException
	 */
	public static void doBegin() throws SQLException {
		Logger log = LoggerFactory.getLogger(AbstractDataOperation.class);
		Connection conn = session.get();
		if (conn != null && conn.getAutoCommit()) {
			conn.setAutoCommit(false);
			log.info("Fetching Transaction JDBC Connection: " + conn);
		}
	}

	/**
	 * 提交一个事务
	 * 
	 * @throws SQLException
	 */
	public static void doCommit() throws SQLException {
		Logger log = LoggerFactory.getLogger(AbstractDataOperation.class);
		Connection conn = session.get();
		if (conn != null) {
			conn.commit();
			log.info("Commit Transaction JDBC Connection: " + conn);
		}
	}

	/**
	 * 回滚一个事务
	 * 
	 * @throws SQLException
	 */
	public static void doRollback() throws SQLException {
		Logger log = LoggerFactory.getLogger(AbstractDataOperation.class);
		Connection conn = session.get();
		if (conn != null) {
			conn.rollback();
			log.info("Rollback Transaction JDBC Connection: " + conn);
		}
	}
}
