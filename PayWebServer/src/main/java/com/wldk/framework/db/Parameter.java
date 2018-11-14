
package com.wldk.framework.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 参数实体类<br>
 * 
 * @author Administrator
 * 
 */
public class Parameter {
	/** 参数名称 */
	private String field;

	/** 参数值 */
	private Object value;

	/** 操作符 */
	private Operator opertaor;

	/** 构造方法 */
	public Parameter() {
	}

	/** 构造方法 */
	public Parameter(String field, Object value, Operator opertaor) {
		super();
		this.field = field;
		this.value = value;
		this.opertaor = opertaor;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("field", field).append("value", value).append(
						"opertaor", opertaor).toString();
	}

	
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		boolean equals = false;
		if (obj != null && Parameter.class.isAssignableFrom(obj.getClass())) {
			Parameter param = (Parameter) obj;
			equals = new EqualsBuilder().append(field, param.field).append(
					value, param.value).isEquals();
		}
		return equals;
	}

	
	public int hashCode() {
		// TODO Auto-generated method stub
		return new HashCodeBuilder(17, 37).append(field).append(value).append(
				opertaor).hashCode();
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Operator getOpertaor() {
		return opertaor;
	}

	public void setOpertaor(Operator opertaor) {
		this.opertaor = opertaor;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
