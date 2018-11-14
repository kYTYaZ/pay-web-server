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

/**
 * 图形接口
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-7-31 <br>
 * Author: Administrator <br>
 * 
 */
public interface Graph {
	public final static String[] COLOURS = { "#0247fe", "#3d01a4", "#8601af", "#a7194b", "#a7194b", "#fd5308", "#fb9902", "#fabc02", "#fefe33", "#d0ea2b", "#66b032", "#0392ce", "#8B4513", "#FF0000",
			"#800000", "#696969", "#808000" };

	public final static String BGCOLOUR = "#FFFFFF";

	public final static String TITLE_STYLE = "{color: #000000; font-size: 25; text-align: center}";

	public final static String LABEL_COLOUR = "#432baf";

	/** 创建图形 */
	String createChart();
}
