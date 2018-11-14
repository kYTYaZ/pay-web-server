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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 饼状图实现
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-7-31 <br>
 * Author: Administrator <br>
 * 
 */
public class PieGraph implements Graph {
	/** 图形数据 */
	private ChartData chartData;

	private final static String TIPS = "#val#<br>#total#<br>#percent#<br>#label#";

	/**
	 * 构造方法
	 * 
	 * @param chartData
	 */
	public PieGraph(ChartData chartData) {
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
		// 设置elements
		JSONArray elements = new JSONArray();
		// 循环生成elements
		for (int i = 0, n = chartData.getValues().length; i < n; i++) {
			// 获取一行数据
			BigDecimal[] row = chartData.getValues()[i];
			// 设置透明度
			JSONObject elementsAttr = new JSONObject();
			elementsAttr.put("alpha", new Double(0.6));
			// 设置边框
			elementsAttr.put("border", new Double(2.0));
			// 加入颜色
			JSONArray colours = new JSONArray();
			for (String colour : Graph.COLOURS) {
				colours.add(colour);
			}
			elementsAttr.put("colours", colours);
			// 设置标签的颜色
			elementsAttr.put("label-colour", LABEL_COLOUR);
			// 设置提示信息
			elementsAttr.put("tip", TIPS);
			// 设置图形类型
			elementsAttr.put("type", chartData.getChartType().toString());
			// 设置数据
			JSONArray values = new JSONArray();
			for (int j = 0, k = row.length; j < k; j++) {
				JSONObject valueAttr = new JSONObject();
				valueAttr.put("label", chartData.getLabels()[j]);
				valueAttr.put("value", row[j]);
				valueAttr.put("font-size", 12);
				values.add(valueAttr);
			}
			elementsAttr.put("values", values);
			elements.add(elementsAttr);
		}
		root.put("elements", elements);
		return root.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 创建一个根json对象
		JSONObject root = new JSONObject();
		// 设置背景色
		root.put("bg_colour", BGCOLOUR);
		// 设置Title
		JSONObject titleAttr = new JSONObject();
		titleAttr.put("style", TITLE_STYLE);
		titleAttr.put("text", "Title");
		root.put("title", titleAttr);
		// 设置elements
		JSONArray elements = new JSONArray();
		// 设置透明度
		JSONObject elementsAttr = new JSONObject();
		elementsAttr.put("alpha", new Double(0.6));
		// 设置边框
		elementsAttr.put("border", new Double(6.0));
		// 加入颜色
		JSONArray colours = new JSONArray();
		for (String colour : Graph.COLOURS) {
			colours.add(colour);
		}
		elementsAttr.put("colours", colours);
		// 设置标签的颜色
		elementsAttr.put("label-colour", LABEL_COLOUR);
		// 设置提示信息
		elementsAttr.put("tip", TIPS);
		// 设置图形类型
		elementsAttr.put("type", "pie");
		elements.add(elementsAttr);
		root.put("elements", elements);
//		System.out.println(root);
	}
}