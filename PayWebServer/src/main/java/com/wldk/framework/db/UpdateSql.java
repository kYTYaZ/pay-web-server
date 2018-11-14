package com.wldk.framework.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdateSql extends AbstractDataOperation<Integer> {
	/**
	 * @param dsName
	 */
	public UpdateSql(DataSourceName dsName) {
		super(dsName);
		// TODO Auto-generated constructor stub
	}

	public UpdateSql(String sql, DataSourceName dsName) {
		super(sql, dsName);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.DataOperationCommand#execute()
	 */
	public Integer execute() throws SQLException {
		// TODO Auto-generated method stub
		int effect = 0;
		PreparedStatement ps = null;
		try {
			if (sql != null) {
				if(conn == null){
					throw new Exception("数据库连接未打开！");
				}
				if (!transactionActive) { // 无需执行事务的
					effect = update(sql);
				} else { // 需要执行事务
					log.info("SQL:[" + sql.trim() + "]");
					ps = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
					effect = ps.executeUpdate();
				}
			}
		} catch (SQLException sqle) {
			log.error(sqle.getMessage(),sqle);
			throw sqle;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new SQLException(e.toString());
		} finally {
			if(ps != null){
				ps.close();
			}
		}
		return effect;
	}

	/**
	 * 执行更新方法
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public Integer update(String sql) throws SQLException {
		if (sql == null) {
			return 0;
		}
		log.info("SQL:[" + sql.trim() + "]");
		return runner.update(sql);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
