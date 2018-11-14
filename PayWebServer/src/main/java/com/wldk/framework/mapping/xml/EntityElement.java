/**
 * 
 */
package com.wldk.framework.mapping.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author Administrator
 * 
 */
public class EntityElement {
	private List<OptionElement> options;

	public EntityElement() {
		this.options = new ArrayList<OptionElement>();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("options", options).toString();
	}

	/**
	 * @return the options
	 */
	public List<OptionElement> getOptions() {
		return options;
	}

	/**
	 * @param options
	 *            the options to set
	 */
	public void setOptions(List<OptionElement> options) {
		this.options = options;
	}

}
