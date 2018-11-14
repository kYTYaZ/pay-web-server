/*@(#)
 * 
 * Project: bcas_pose
 *
 * Modify Information:
 * =============================================================================
 *   Author         Date           Description
 *   ------------ ---------- ---------------------------------------------------
 *   Administrator        2012-11-14        first release
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
package com.wldk.framework.enums;

/**
 * 操作系统常量定义(判断操作系统类型)
 * 
 * @author Administrator
 * 
 */
public enum OS {
	WINDOWS("Windows"), LINUX("Linux"), AIX("AIX"), UNKNOWN("unknown");

	private String name;

	/**
	 * 构造方法
	 * 
	 * @param name
	 */
	OS(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 实例化方法(判断操作系统)
	 * 
	 * @param name
	 * @return
	 */
	public static OS instance(String name) {
		if (name.toLowerCase().startsWith("windows")) {
			return WINDOWS;
		} else if (name.toLowerCase().startsWith("linux")) {
			return LINUX;
		} else if (name.toLowerCase().startsWith("aix")) {
			return AIX;
		}
		return UNKNOWN;
	}

	public static void main(String[] args) throws Exception {
//		System.out.println(OS.instance("AIX"));
	}
}