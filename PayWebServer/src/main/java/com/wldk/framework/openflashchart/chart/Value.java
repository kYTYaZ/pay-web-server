/*@(#)
 * 
 * Project: bcas_pose
 *
 * Modify Information:
 * =============================================================================
 *   Author         Date           Description
 *   ------------ ---------- ---------------------------------------------------
 *   Administrator        2012-11-28        first release
 *
 * 
 * Copyright Notice:
 * =============================================================================
 *       Copyright 2012 Huateng Software, Inc. All rights reserved.
 *
 *       This software is the confidential and proprietary information of
 *       Shanghai HUATENG Software Co., Ltd. ("Confidential Information").
 *       You shall not disclose such Confidential Information and shall use it
 *       only in accordance with the terms of the license agreement you entered
 *       into with Huateng.
 *
 * Warning:
 * =============================================================================
 * 
 */
package com.wldk.framework.openflashchart.chart;


import java.io.Serializable;

public class Value implements Serializable {

	/**
	 * @2012-7-31
	 */
	private static final long serialVersionUID = 3466435656455973553L;

	/** 标题 */
	private String label;

	/** 值 */
	private Double value;

	/** 字体大小 */
	private Integer fontSize;

	/** 字体颜色 */
	private String colour;

	/** 标题颜色 */
	private String labelColour;

	/** 点击事件 */
	private String onClick;

	/**
	 * 构造方法
	 */
	public Value() {
	}

	public String getColour() {
		return colour;
	}

	public void setColour(String colour) {
		this.colour = colour;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelColour() {
		return labelColour;
	}

	public void setLabelColour(String labelColour) {
		this.labelColour = labelColour;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
