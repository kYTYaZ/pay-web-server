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


import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author Administrator
 * 
 */
public class LineGraph2 implements Graph {
	/** 图形数据 */
	private ChartData chartData;

	private final static String LEGEND_STYLE = "{color: #736AFF; font-size: 12px;}";

	/**
	 * 构造方法
	 * 
	 * @param chartData
	 */
	public LineGraph2(ChartData chartData) {
		this.chartData = chartData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openflashchart.chart.Graph#createChart()
	 */
	public String createChart() {
		// TODO Auto-generated method stub

		return getJsonString();
	}

	public ChartData getChartData() {
		return chartData;
	}

	public void setChartData(ChartData chartData) {
		this.chartData = chartData;
	}

	protected String getJsonString() {
		// 创建一个根json对象
		JSONObject root = new JSONObject();
		// 设置背景色
		root.put("bg_colour", BGCOLOUR);
		// 设置Title
		JSONObject titleAttr = new JSONObject();
		titleAttr.put("style", TITLE_STYLE);
		titleAttr.put("text", chartData.getTitle());
		root.put("title", titleAttr);
		// 设置Y轴的标题
		JSONObject yLegendAttr = new JSONObject();
		yLegendAttr.put("text", chartData.getYLegend());
		yLegendAttr.put("style", LEGEND_STYLE);
		root.put("y_legend", yLegendAttr);
		// 设置X轴的标题
		JSONObject xLegendAttr = new JSONObject();
		xLegendAttr.put("text", chartData.getXLegend());
		xLegendAttr.put("style", LEGEND_STYLE);
		root.put("x_legend", xLegendAttr);
		// 设置elements
		JSONArray elements = new JSONArray();
		// 循环生成elements
		for (int i = 0, n = chartData.getValues().length; i < n; i++) {
			// 获取一行数据
			BigDecimal[] row = chartData.getValues()[i];
			JSONObject elementsAttr = new JSONObject();
			// 设置图形类型
			elementsAttr.put("type", "line");
			// 设置颜色
			elementsAttr.put("colour", COLOURS[i % COLOURS.length]);
			// 设置图例说明
			elementsAttr.put("text", chartData.getClassCaliberValues()[i]);
			// 设置线的宽度
			elementsAttr.put("width", 2);
			// 设置字体大小
			elementsAttr.put("font-size", 12);
			elementsAttr.put("dot-size", 2);
			elementsAttr.put("halo-size", 0);
			// 设置数据
			JSONArray values = new JSONArray();
			for (int j = 0, k = row.length; j < k; j++) {
				JSONObject valueAttr = new JSONObject();
				valueAttr.put("tip", "#val#<br>"
						+ chartData.getClassCaliberValues()[i]);
				valueAttr.put("value", row[j]);
				valueAttr.put("colour", COLOURS[i % COLOURS.length]);
				values.add(valueAttr);
			}
			elementsAttr.put("values", values);
			elements.add(elementsAttr);
		}
		// 设置X轴的相关属性
		JSONObject xAxisAttr = new JSONObject();
		xAxisAttr.put("stroke", 2);
		xAxisAttr.put("tick_height", 10);
		xAxisAttr.put("colour", "#87421F");
		xAxisAttr.put("grid_colour", "#00ff00");
		// 设置X轴的标签
		JSONObject labelAttr = new JSONObject();
		JSONArray labels = new JSONArray();
		for (String label : chartData.getLabels()) {
			labels.add(label.length() > 10 ? label.substring(0, 10) + "..."
					: label);
		}
		labelAttr.put("labels", labels);
		xAxisAttr.put("labels", labelAttr);
		/**立体效果*/
		// xAxisAttr.put("3d", 3);
		root.put("x_axis", xAxisAttr);
		// 设置Y轴的相关属性
		JSONObject yAxisAttr = new JSONObject();
		yAxisAttr.put("stroke", 2);
		yAxisAttr.put("tick_height", 3);
		yAxisAttr.put("colour", "#87421F");
		yAxisAttr.put("grid_colour", "#00ff00");
		yAxisAttr.put("offset", 0);
		// 计算最大值
		BigDecimal max = BarGraph.max(chartData.getValues());
		yAxisAttr.put("max", max =  new BigDecimal(100));
		BigInteger steps = max.toBigIntegerExact().divide(new BigInteger("15"));
		BigDecimal s = BarGraph.ceiling(new BigDecimal(steps),
				new BigDecimal(1), 1);
		yAxisAttr.put("steps", s.intValueExact());
		root.put("y_axis", yAxisAttr);

		root.put("elements", elements);
		return root.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}