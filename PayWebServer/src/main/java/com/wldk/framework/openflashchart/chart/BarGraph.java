/*@(#)
 * 
 * Project: java_frame
 *
 * Modify Information:
 * =============================================================================
 *   Author         Date           Description
 *   ------------ ---------- ---------------------------------------------------
 *   Administrator        2012-7-31        first release
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
import java.math.RoundingMode;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 柱状图
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-7-31 <br>
 * Author: Administrator <br>
 *
 */
public class BarGraph implements Graph {
	/** 图形数据 */
	private ChartData chartData;

	private final static String LEGEND_STYLE = "{color: #736AFF; font-size: 12px;}";

	/**
	 * 构造方法
	 * 
	 * @param chartData
	 */
	public BarGraph(ChartData chartData) {
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
			// 设置透明度
			JSONObject elementsAttr = new JSONObject();
			elementsAttr.put("alpha", new Double(0.5));
			// 设置边框
			elementsAttr.put("border", new Double(2.0));
			// 加入颜色
			if (i == 0) {
				elementsAttr.put("colour", "#9933CC");
				elementsAttr.put("text", "基期");
			} else {
				elementsAttr.put("colour", "#44FF44");
				elementsAttr.put("text", "当期");
			}
			// 设置图形类型
//			elementsAttr.put("type", "bar_glass");
			elementsAttr.put("type", "bar_3d");
			
			
			// 设置字体大小
			elementsAttr.put("font-size", 12);
			// 设置数据
			JSONArray values = new JSONArray();
			for (int j = 0, k = row.length; j < k; j++) {
				JSONObject valueAttr = new JSONObject();
				valueAttr.put("tip", "#top#<br>#x_label#");
				valueAttr.put("top", row[j]);
				values.add(valueAttr);
			}
			elementsAttr.put("values", values);
			elements.add(elementsAttr);
		}
		// 设置X轴的相关属性
		JSONObject xAxisAttr = new JSONObject();
		xAxisAttr.put("stroke", 1);
		xAxisAttr.put("tick_height", 10);
		xAxisAttr.put("colour", "#ADB5C7");
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
		/**设置3D立体感*/
		 xAxisAttr.put("3d", 5);
		root.put("x_axis", xAxisAttr);
		// 设置Y轴的相关属性
		JSONObject yAxisAttr = new JSONObject();
		yAxisAttr.put("stroke", 1);
		yAxisAttr.put("tick_height", 3);
		yAxisAttr.put("colour", "#d000d0");
		yAxisAttr.put("grid_colour", "#00ff00");
		yAxisAttr.put("offset", 0);
		// 计算最大值，并放大1.5倍
		BigDecimal max = max(chartData.getValues());
		yAxisAttr.put("max", max = ceiling(max, BigDecimal.valueOf(1.05)));
		BigInteger steps = max.toBigIntegerExact().divide(new BigInteger("15"));
		BigDecimal s = ceiling(new BigDecimal(steps), new BigDecimal(1), 1);
		yAxisAttr.put("steps", s.intValueExact());
		root.put("y_axis", yAxisAttr);
		root.put("elements", elements);
		return root.toString();
	}

	public static BigDecimal max(BigDecimal[][] src) {
		BigDecimal[] group = new BigDecimal[src.length];
		for (int i = 0, n = src.length; i < n; i++) {
			group[i] = max(src[i]);
		}
		return max(group);
	}

	public static BigDecimal max(BigDecimal[] src) {
		BigDecimal max = new BigDecimal(0.00d);
		for (BigDecimal d : src) {
			max = d.max(max);
		}
		return max;
	}

	/**
	 * 获取指定值被放大P倍之后的大于当前值的最小整数
	 * 
	 * @param p
	 *            放大的倍数
	 * @return
	 */
	public static BigDecimal ceiling(BigDecimal value, BigDecimal p) {
		return ceiling(value, p, 2);
	}

	public static BigDecimal ceiling(BigDecimal value, BigDecimal p, int bit) {
		// 将指定的值方法P倍，并设置其标度为0
		value = value.multiply(p).setScale(0, RoundingMode.HALF_UP);
		// 获取当前值的非标度值的数字个数，并减去2位
		int length = value.precision() - bit <= 0 ? value.precision() : value
				.precision()
				- bit;
		return value.movePointLeft(length).setScale(0, RoundingMode.UP)
				.movePointRight(length);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BigDecimal max = new BigDecimal(59997);
		BigDecimal m = ceiling(max, BigDecimal.valueOf(1.05));
//		System.out.println("max: " + m);
		BigInteger step = m.toBigIntegerExact().divide(new BigInteger("15"));
		BigDecimal s = ceiling(new BigDecimal(step), new BigDecimal(1), 1);
//		System.out.println("step: " + s.intValueExact());
	}
}
