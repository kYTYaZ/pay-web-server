/**
 * 
 */
package com.wldk.framework.mapping.xml;

/**
 * @author Administrator
 * 
 */
public enum Attribute {
	ID("id"), DSNAME("dsName"), NAME("name"), VALUE("value");
	private String attribute;

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	Attribute(String attribute) {
		this.attribute = attribute;
	}
}
