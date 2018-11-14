package com.wldk.framework.openflashchart.chart;


import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 图表数据的PO类定义
 * 
 * Modify Information: <br>
 * Author: Administrator <br>
 * Date: 2012-7-31 <br>
 * Author: Administrator <br>
 *
 */
public class ChartData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2593795952092763472L;

	public final static String CHART_KEY = "chart_key";

	/** 图形类型 */
	private ChartType chartType;

	/** 图表的标题，格式例如：[上海分行]特约商户结构统计，从[yyyy-MM-dd]到[yyyy-MM-dd]，分类口径[行业类别] */
	private String title;

	/** Y轴的标题 */
	private String yLegend;

	/** X轴的标题 */
	private String xLegend;

	/** 分类口径 */
	private String classCaliber;

	/** 图表的标签数组，例如：new String[]{"上海分行","北京分行","长春分行",......} */
	private String[] labels;

	/** 图表的数据数组 例如：new Double[][]{129,145,11,......} */
	private BigDecimal[][] values;

	/** 分类口径数组 */
	private String[] classCaliberValues;

	/**
	 * 默认构造方法
	 * 
	 */
	public ChartData() {
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("title", title).append("classCaliber", classCaliber)
				.append("yLegend", yLegend).append("xLegend", xLegend).append(
						"labels",
						labels != null ? ArrayUtils.toString(labels) : null)
				.append("values",
						values != null ? ArrayUtils.toString(values) : null)
				.append("chartType", chartType).toString();
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public String[] getLabels() {
		return labels;
	}

	public void setLabels(String[] labels) {
		this.labels = labels;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 图形类型常量定义
	 * 
	 * @author Administrator
	 * 
	 */
	public enum ChartType {
		PIE("pie"), BAR("bar"), LINE("line"), LINE2("line2");
		private String type;

		/**
		 * 构造方法
		 * 
		 * @param type
		 */
		ChartType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			switch (this) {
			case BAR:
				/**柱状*/
				return "bar";
			case LINE:
				/**曲线*/
				return "line";
			case LINE2:
				/**曲线百分百*/
				return "line2";
			default:
				/**宾图*/
				return "pie";
			}
		}

		public String getType() {
			return type;
		}

		public static ChartType toChart(String type) {
			if (type == null || type.equals("")) {
				return ChartType.PIE;
			} else if (type.trim().equalsIgnoreCase("bar")) {
				return ChartType.BAR;
			} else if (type.trim().equalsIgnoreCase("line")) {
				return ChartType.LINE;
			} else if (type.trim().equalsIgnoreCase("line2")) {
				return ChartType.LINE2;
			}
			
			return ChartType.PIE;
		}
	}

	public String getClassCaliber() {
		return classCaliber;
	}

	public void setClassCaliber(String classCaliber) {
		this.classCaliber = classCaliber;
	}

	public BigDecimal[][] getValues() {
		return values;
	}

	public void setValues(BigDecimal[][] values) {
		this.values = values;
	}

	public String getXLegend() {
		return xLegend;
	}

	public void setXLegend(String legend) {
		xLegend = legend;
	}

	public String getYLegend() {
		return yLegend;
	}

	public void setYLegend(String legend) {
		yLegend = legend;
	}

	public String[] getClassCaliberValues() {
		return classCaliberValues;
	}

	public void setClassCaliberValues(String[] classCaliberValues) {
		this.classCaliberValues = classCaliberValues;
	}
}
