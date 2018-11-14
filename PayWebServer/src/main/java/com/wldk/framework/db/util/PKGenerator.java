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
package com.wldk.framework.db.util;


import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Éú³É¹Ø¼ü×ÖµÄ¹¤¾ßÀà<br>
 * 
 * @author Administrator
 * 
 */
public class PKGenerator {
	/** ÈÕÖ¾ */
	private Logger log = LoggerFactory.getLogger(getClass());
	/** µ¥Àý */
	private static PKGenerator instance = new PKGenerator();
	/** ÖØÈëËø */
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * ¹¹Ôì·½·¨
	 */
	protected PKGenerator() {
	}

	public static PKGenerator getInstance() {
		return instance;
	}

	/**
	 * ·µ»ØÒ»¸öLongÐÍµÄÎ¨Ò»¹Ø¼ü×Ö
	 * 
	 * @return
	 */
	public Long getLongPK() {
		lock.lock();
		try {
			return UUID.randomUUID().getMostSignificantBits();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		System.out.println(PKGenerator.getInstance().getLongPK()+"==");
	}
}