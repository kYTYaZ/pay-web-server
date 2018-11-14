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


public class GraphFactory {
	/**
	 * 获取指定类型图形的工厂方法
	 * 
	 * @param data
	 * @return
	 */
	public static Graph getGraph(ChartData data) {
		switch (data.getChartType()) {
		case PIE:
			return new PieGraph(data);
		case BAR:
			return new BarGraph(data);
		case LINE:
			return new LineGraph(data);
		case LINE2:
			return new LineGraph2(data);
		default:
			return null;
		}
	}
}