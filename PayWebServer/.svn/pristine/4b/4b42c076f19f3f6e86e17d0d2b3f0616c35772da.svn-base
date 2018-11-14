/**
 * 
 */
package com.wldk.framework.mapping.xml;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.wldk.framework.db.DataSourceName;

/**
 * @author Administrator
 * 
 */
public class MappingElement {
	private String id;
	private DataSourceName dsName;
	private SqlElement sql;
	private EntityElement entity;

	public MappingElement() {
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("id", id).append("dsName", dsName).append("sql", sql)
				.append("entity", entity).toString();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the dsName
	 */
	public DataSourceName getDsName() {
		return dsName;
	}

	/**
	 * @param dsName
	 *            the dsName to set
	 */
	public void setDsName(DataSourceName dsName) {
		this.dsName = dsName;
	}

	/**
	 * @return the sql
	 */
	public SqlElement getSql() {
		return sql;
	}

	/**
	 * @param sql
	 *            the sql to set
	 */
	public void setSql(SqlElement sql) {
		this.sql = sql;
	}

	/**
	 * @return the entity
	 */
	public EntityElement getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(EntityElement entity) {
		this.entity = entity;
	}

}
