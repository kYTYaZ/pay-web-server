/**
 * 
 */
package com.wldk.framework.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
/**
 * 参数对象的提供器封装类，用于对查询参数和更新参数的操作<br>
 * 
 * @author Administrator
 * 
 */
public class ParameterProvider {

//	protected Logger log = LoggerFactory.getLogger(getClass());

	/** 存放查询参数的列表 */
	private List<Parameter> parameters;

	/** 构造方法 */
	public ParameterProvider() {
		this.parameters = new ArrayList<Parameter>();
	}

	public String toString() {
		// TODO Auto-generated method stub
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("parameters", parameters).toString();
	}

	/**
	 * @return the parameters
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            the parameters to set
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/** 增加一个查询参数对象 */
	public void addParameter(Parameter param) {
		if (param != null) {
			this.parameters.add(param);
		}
	}

	/** 重载方法 */
	public void addParameter(Map<String, String[]> parameter) {
		if (parameter != null) {
			Set<Entry<String, String[]>> set = parameter.entrySet();
			for (Entry<String, String[]> entry : set) {
				if (entry != null) {
					String key = entry.getKey();
					String[] value = entry.getValue();
					if (key != null && !key.equals("")) {
						if (value != null && value.length == 1) {
							addParameter(key, value[0]);
						} else if (value != null && value.length > 1) {
							addParameter(key, value);
						}
					}
				}
			}
		}
	}
	
	/** 重载方法 */
	public void addParameters(Map<String, String> parameter) {
		if (parameter != null) {
			Set<Entry<String, String>> set = parameter.entrySet();
			for (Entry<String, String> entry : set) {
				if (entry != null) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (key != null && !key.equals("")) {
						addParameter(key, value);
					}
				}
			}
		}
	}

	/** 重载方法 */
	public void addParameter(String field, Object value) {
		if (field != null) {
			Parameter parameter = new Parameter(field, value, null);
			addParameter(parameter);
		}
	}

	/** 重载方法 */
	public void addParameter(String field, Object value, Operator opt) {
		if (field != null && opt != null) {
			Parameter parameter = new Parameter(field, value, opt);
			addParameter(parameter);
		}
	}

	/** 清除所有参数 */
	public void clear() {
		if (parameters != null && parameters.size() > 0) {
			parameters.clear();
		}
	}

	/** 删除一个查询参数对象 */
	public void removeParameter(Parameter param) {
		if (param != null) {
			this.parameters.remove(param);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
