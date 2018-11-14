/**
 * 
 */
package com.wldk.framework.web.taglib;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.struts2.util.ContainUtil;

import com.wldk.framework.mapping.MappingContext;

/**
 * 通过缓存表生成复选框列表的标签实现
 * 
 * @author Administrator
 * 
 */
public class MappedCheckboxTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6948693954184385623L;
	private static Logger log = LoggerFactory.getLogger(MappedCheckboxTag.class);
	/** 缓存表的Key值 */
	protected String mappedKey;
	/** 名称 */
	protected String name;
	protected Object value;
	protected String styleClass;
	protected String style;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	
	public int doStartTag() throws JspException {
		// TODO Auto-generated method stub
		// 先从上下文中获取代码映射对象，如果没有取到，再去数据库中获取
		try {
			StringBuilder sb = new StringBuilder();
			Map<String, Object[]> mapped = MappingContext.getInstance().get(
					mappedKey);
			Set<Entry<String, Object[]>> set = mapped.entrySet();
			int i = 0;
			for (Entry<String, Object[]> entry : set) {
				String key = entry.getKey();
				Object value = entry.getValue() != null ? entry.getValue()[1]
						: null;
				if (key != null && value != null) {
					sb.append("<input type=\"checkbox\" ").append("name=\"")
							.append(this.name).append("\" value=\"")
							.append(key).append("\" ");
					String id = name;
					if (this.id != null) {
						id = this.id + "-" + (i++);
					}
					sb.append("id=\"").append(id).append("\" ");
					if (this.styleClass != null) {
						sb.append("class=\"").append(styleClass).append("\" ");
					}
					if (this.style != null) {
						sb.append("style=\"").append(this.style).append("\" ");
					}
					if (ContainUtil.contains(this.value, key)) {
						sb.append("checked=\"checked\" ");
					}
					sb.append("/>").append("&nbsp;<label for=\"").append(id).append(
							"\">").append(value).append("</label>&nbsp;");
				}
			}
			pageContext.getOut().write(sb.toString());
			return SKIP_BODY;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new JspException(e.toString());
		}
	}

	/**
	 * @return the mappedKey
	 */
	public String getMappedKey() {
		return mappedKey;
	}

	/**
	 * @param mappedKey
	 *            the mappedKey to set
	 */
	public void setMappedKey(String mappedKey) {
		this.mappedKey = mappedKey;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @return the styleClass
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * @param styleClass
	 *            the styleClass to set
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param style
	 *            the style to set
	 */
	public void setStyle(String style) {
		this.style = style;
	}
}
