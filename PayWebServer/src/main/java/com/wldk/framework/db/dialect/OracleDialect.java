package com.wldk.framework.db.dialect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.db.PageVariable;
import com.wldk.framework.utils.FieldToPropertyUtils;
/**
 * Oracle数据库类型
 * @author Administrator
 *
 */
public class OracleDialect implements Dialect {
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(getClass());

	/** 构造方法 */
	public OracleDialect() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.dialect.Dialect#getResult(org.apache.commons.dbutils.QueryRunner, java.lang.String, com.mingdeng.core.db.Pagination)
	 */
	@SuppressWarnings("unchecked")
	public List<Object[]> getResult(QueryRunner runner, String sql, PageVariable page) throws SQLException {
		// TODO Auto-generated method stub
		return (List<Object[]>) runner.query(sql, new ArrayListHandler());
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getResultMap(QueryRunner runner, String sql, PageVariable page) throws SQLException {
		List<Map<String, Object>> rs= (List<Map<String, Object>>)runner.query(sql, new MapListHandler());
		List<Map<String, Object>> list= new ArrayList<Map<String,Object>>();
		for (Map<String, Object> row : rs) {
			Map<String, Object> map=new HashMap<String, Object>();
			Map<String, Object> m = row;
			Set s = m.keySet();
			Iterator i = s.iterator();
			while(i.hasNext()){
				Object key=i.next();
				Object name=m.get(key);
				map.put(FieldToPropertyUtils.fieldToProperty(key.toString()), name);
			}
			list.add(map);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bankcomm.data.dialect.Dialect#getDialectSQL(java.lang.String, com.bankcomm.data.Pagination)
	 */
	public String getDialectSQL(QueryRunner runner, String sql, PageVariable page) {
		// TODO Auto-generated method stub
		if (runner != null && sql != null && page != null) {
			// 将SQL字符串全部转换为大写
			// sql = sql.toUpperCase();
			// 查询总记录数，并计算分页对象
			StringBuffer tql = new StringBuffer("SELECT COUNT(1) FROM (");
			tql.append(sql).append(") T ");
			log.debug("SQL:[" + tql + "]");
			
			try {
				Object[] result = (Object[]) runner.query(tql.toString(), new ArrayHandler());
				// 总记录数
				int count = result != null && result.length > 0 ? new Integer(result[0].toString()) : 0;
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
			s.append("SELECT * FROM ( SELECT T1.*, ROWNUM RN FROM ( ");
			
			
			// 查找FROM关键字的位置
			StringBuffer q = new StringBuffer(sql);
//			q.insert(sql.indexOf("FROM"), ",ROWNUMBER() OVER () AS RN ");
			int start = 1;
			// 计算起始位置
			if (page.getCurrentPage() != 1) {
				start = (page.getCurrentPage() - 1) * page.getRecordPerPage() + 1;
			}
			// 计算结束位置，如果结束位置大于总记录数，则定位到最后一条记录的位置
			int end = start + page.getRecordPerPage() - 1;
			if (end > page.getRecordCount()) {
				end = page.getRecordCount();
			}
			// 如果起始位置大于总记录数，则定位到最后一页的起始位置
			if (start > page.getRecordCount()) {
				start = (page.getPageCount() - 1) * page.getRecordPerPage() + 1;
			}
			s.append(q).append(") T1 WHERE ROWNUM <= "+end+" ) WHERE RN >="+start);
			
			
	
			
			
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
