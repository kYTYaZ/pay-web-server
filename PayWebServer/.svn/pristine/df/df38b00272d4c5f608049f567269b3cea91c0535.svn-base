/**
 * 
 */
package com.wldk.framework.web.taglib;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.wldk.framework.mapping.MappingContext;

/**
 * 通过代码映射生成下拉列表的标签<br>
 * 
 * @author Administrator
 * 
 */
public class MappingSelectTag extends SelectTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8697626273188882507L;

	private String mapping;

	/** 排序值 */
	private String exclude;

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getExclude() {
		return exclude;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public MappingSelectTag() {
	}


	protected String createOptionsStr() throws Exception {
		// TODO Auto-generated method stub
		StringBuffer sb = new StringBuffer();
		boolean alreadySelected = false;// 是否已经找到默认选中的option
		if (this.headerKey != null && this.headerValue != null) {
			sb.append("<option value=\"").append(this.headerKey).append("\">")
					.append(this.headerValue).append("</option>");
		}
		// 获取排除值
		String[] exclude = StringUtils.split(this.exclude, ",");
		List<String> excludeOptions = exclude != null && exclude.length > 0 ? Arrays
				.asList(exclude)
				: null;
		// 先从上下文中获取代码映射对象，如果没有取到，再去数据库中获取
		MappingContext context = MappingContext.getInstance();
		Map<String, Object[]> mappings = context.get(mapping);
		Set<Entry<String, Object[]>> set = mappings.entrySet();
		for (Entry<String, Object[]> entry : set) {
			String key = entry.getKey();
			Object[] value = entry.getValue() != null ? entry.getValue() : null;
			if (key != null && value != null
					&& exclideValue(excludeOptions, key.toString())) {
				sb.append("<option value=\"").append(key);
				if (this.value != null
						&& this.value.trim().equalsIgnoreCase(
								key.toString().trim()) && !alreadySelected) {
					sb.append("\" selected >");
					alreadySelected = true;
				} else {
					sb.append("\">");
				}
				/**为满足分行选择框显示需求添加*/
				if(value.length>3){
					if(value[3]!=null&&value[3].toString().equals("LINKNAMEANDBANKNO")){
						sb.append(value[1]+value[2].toString()).append("</option>");
					}else{
						sb.append(value[1]).append("</option>");
					}
				}else{
					sb.append(value[1]).append("</option>");
				}
				
			}
		}

		return sb.toString();
	}

	private boolean exclideValue(List<String> excludeOptions, String option) {
		if (excludeOptions == null || excludeOptions.size() <= 0) {
			return true;
		}
		return !excludeOptions.contains(option);
	}
}
