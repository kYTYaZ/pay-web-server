/**
 * 
 */
package com.wldk.framework.web.taglib;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.mapping.MappingContext;

/**
 * 显示代码映射的JSP标签类<br>
 * 
 * @author Administrator
 * 
 */
public class MappingTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 298860983716349780L;

	private static Logger log = LoggerFactory.getLogger(MappingTag.class);

	private String key;

	private Object value;

	private int maxLength = -1;
	
	private int index=1;//表示索引
	
	public MappingTag() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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
	 * @return the maxLength
	 */
	public int getMaxLength() {
		return maxLength;
	}

	/**
	 * @param maxLength
	 *            the maxLength to set
	 */
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		// TODO Auto-generated method stub
		try {
			// 先从上下文中获取代码映射对象，如果没有取到，再去数据库中获取
			MappingContext context = MappingContext.getInstance();
			Map<String, Object[]> mappings = context.get(key);
			if (mappings != null && mappings.size() > 0 && this.value != null) {
				Object description = mappings.get(String.valueOf(this.value)) != null ? mappings
						.get(String.valueOf(this.value))[index]
						: "";
				String desc = String.valueOf(description);
				if (desc != null) {
					desc = this.maxLength <= 0
							|| this.maxLength >= desc.length() ? desc
							.substring(0, desc.length()) : desc.substring(0,
							this.maxLength)
							+ "...";
				}
				if (id == null || id.equals("")) {
					pageContext.getOut().write(desc);
				} else {
					pageContext.setAttribute(id, desc);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new JspException(e.toString());
		}
		return SKIP_BODY;
	}
}
