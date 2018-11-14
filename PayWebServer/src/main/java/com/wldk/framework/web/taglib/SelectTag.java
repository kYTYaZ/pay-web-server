/**
 * 
 */
package com.wldk.framework.web.taglib;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Administrator
 * 
 */
public class SelectTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3145902087410174390L;
	protected static Logger log = LoggerFactory.getLogger(SelectTag.class.getName());
	protected String name;
	protected String headerKey;
	protected String headerValue;
	private Integer listKeyIndex = 0;
	private Integer listValueIndex = 1;
	protected String value;
	private List<Object[]> list;
	protected String styleClass;
	protected String style;
	protected String onchange;
	protected String onclick;
	protected String onmousemove;



	public int doStartTag() throws JspException {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(createSelectBeginStr());
			sb.append(createOptionsStr());
			sb.append(this.createSelectEndStr());
			pageContext.getOut().write(sb.toString());
			return SKIP_BODY;
		} catch (Exception e) {
			throw new JspException(e.toString());
		}
	}

	protected String createSelectBeginStr() {
		StringBuffer sb = new StringBuffer();
		sb.append("<select ");
		if (this.id != null && !this.id.equals("")) {
			sb.append("id=\"").append(this.id).append("\" ");
		}
		sb.append("name=\"").append(this.name).append("\" ");
		if (this.styleClass != null && !styleClass.equals("")) {
			sb.append("class=\"").append(styleClass).append("\" ");
		}
		if (this.style != null && !this.style.equals("")) {
			sb.append("style=\"").append(this.style).append("\" ");
		}
		if (this.onchange != null && !this.onchange.equals("")) {
			sb.append("onchange=\"").append(this.onchange).append("\" ");
		}
		if (this.onclick != null && !this.onclick.equals("")) {
			sb.append("onclick=\"").append(this.onclick).append("\" ");
		}
		if (this.onmousemove != null && !this.onmousemove.equals("")) {
			sb.append("onmousemove=\"").append(this.onmousemove).append("\" ");
		}
		sb.append(">");
		return sb.toString();
	}

	protected String createOptionsStr() throws Exception {
		StringBuffer sb = new StringBuffer();
		boolean alreadySelected = false;// 是否已经找到默认选中的option
		if (this.headerKey != null && this.headerValue != null) {
			sb.append("<option value=\"").append(this.headerKey).append("\">")
					.append(this.headerValue).append("</option>");
		}
		if (list != null && list.size() > 0) {
			for (int i = 0, n = list.size(); i < n; i++) {
				Object[] row = list.get(i);
				if (row[listKeyIndex] != null && row[listValueIndex] != null) {
					sb.append("<option value=\"").append(row[listKeyIndex]);
					if (this.value != null
							&& this.value.trim().equalsIgnoreCase(
									row[listKeyIndex].toString().trim())
							&& !alreadySelected) {
						sb.append("\" selected >");
						alreadySelected = true;
					} else {
						sb.append("\">");
					}
					sb.append(row[listValueIndex]).append("</option>");
				}
			}
		}
		return sb.toString();
	}

	protected String createSelectEndStr() {
		return "</select>";
	}

	/**
	 * @return the onclick
	 */
	public String getOnclick() {
		return onclick;
	}

	/**
	 * @param onclick
	 *            the onclick to set
	 */
	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	/**
	 * @return the listKeyIndex
	 */
	public Integer getListKeyIndex() {
		return listKeyIndex;
	}

	/**
	 * @param listKeyIndex
	 *            the listKeyIndex to set
	 */
	public void setListKeyIndex(Integer listKeyIndex) {
		this.listKeyIndex = listKeyIndex;
	}

	/**
	 * @return the listValueIndex
	 */
	public Integer getListValueIndex() {
		return listValueIndex;
	}

	/**
	 * @param listValueIndex
	 *            the listValueIndex to set
	 */
	public void setListValueIndex(Integer listValueIndex) {
		this.listValueIndex = listValueIndex;
	}

	/**
	 * @return the list
	 */
	public List<Object[]> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<Object[]> list) {
		this.list = list;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHeaderKey() {
		return headerKey;
	}

	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}
	public String getOnmousemove() {
		return onmousemove;
	}

	public void setOnmousemove(String onmousemove) {
		this.onmousemove = onmousemove;
	}
	public static void main(String[] args) throws Exception {
		Object[] row = new Object[] { "1", "2", "3", "4", "5", "6" };
		System.out.println(BeanUtils.getIndexedProperty(row, "0"));
	}
}
