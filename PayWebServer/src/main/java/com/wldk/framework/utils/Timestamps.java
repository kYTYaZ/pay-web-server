package com.wldk.framework.utils;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 获取系统时间
 * 
 * @author zhaodk
 * 
 */

public class Timestamps {

	private static Timestamps instance = new Timestamps();

	/** 作业创建时间 */
	private Timestamp timestamp = null;
	
	private String timestamps = null;

	/** 当前作业日期 */
	private Date day = null;

	private String date = null;

	/** 历史文件存放路径 */
	private String filePath = null;

	/**密码信封FLAG null为刚开始打，0为已开始打印，1为打印结束,最后一次跳转至页面，2完成打印，进行下一步*/
	private String evnFlag = null;
	
	private Timestamps simestamps;
	
	public Timestamps getSimestamps() {
		return simestamps;
	}

	public void setSimestamps(Timestamps simestamps) {
		this.simestamps = simestamps;
	}

	public String getEvnFlag() {
		return evnFlag;
	}

	public void setEvnFlag(String evnFlag) {
		this.evnFlag = evnFlag;
	}

	public Timestamps() {
		this.timestamp = DateUtils.getCurrentTimestamp();
		this.day = new Date();
		this.date = days();
		this.timestamps=getCurTimestampStrS();
	}

	/**
	 * 初始化
	 * 
	 */
	public void into() {
		timestamp = DateUtils.getCurrentTimestamp();
		day = new Date();
	}

	/**
	 * 通过作业初始化
	 * 
	 * @param jobUnit
	 */
	public void into(Timestamps simestamps) {

		this.simestamps = simestamps;
	}

	/**
	 * 获取单例对象的方法
	 * 
	 * @return
	 */
	public static Timestamps getInstance() {
		return instance;
	}

	/**
	 * 获取系统统一时间返回Timestamp
	 * 
	 * @return
	 */
	public Timestamp timestamp() {
		return timestamp;
	}

	/**
	 * 获取系统统一日前
	 * 
	 * @return
	 */
	public Date day() {
		return day;
	}

	/**
	 * 获取系统统一日前
	 * 
	 * @return
	 */
	public String days() {
		return DateUtils.Day_D(new Date());
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 获取系统统一时间返回yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public String timestamps() {
		return DateUtils.getCurTimestampStr(timestamp);
	}

	/**
	 * 获取系统统一时间返回yyyyMMddHHmmss
	 * 
	 * @return
	 */
	public String getCurTimestampStrS() {
		return DateUtils.getCurTimestampStrS(timestamp);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getTimestamps() {
		return timestamps;
	}

	public void setTimestamps(String timestamps) {
		this.timestamps = timestamps;
	}

}
