/*@(#)
 * 
 * Project: bcas_pose
 *
 * Modify Information:
 * =============================================================================
 *   Author         Date           Description
 *   ------------ ---------- ---------------------------------------------------
 *   Administrator        2013-1-17        first release
 *
 * 
 * Copyright Notice:
 * =============================================================================
 *       Copyright 2013 Huateng Software, Inc. All rights reserved.
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
package com.wldk.framework.interceptor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {
	public static HashMap<String, String> httpssionmapUser = new HashMap<String, String>();// 保存sessionID和username的映射

	public void sessionCreated(HttpSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	/** 以下是实现HttpSessionListener中的方法* */
	public void sessionDestroyed(HttpSessionEvent se) {

		httpssionmapUser.remove(se.getSession().getId());

	}

	/**
	 * 判断用户是否已经登陆
	 * 
	 * @param session
	 * @param sUserName
	 * @return
	 */

	public static boolean isAlreadyEnter(HttpSession session, String sUserName) {
		return !SessionListener.httpssionmapUser.isEmpty() && httpssionmapUser.containsValue(sUserName) ? true : false;

	}

	/**
	 * 添加现在的sessionID和username
	 * 
	 * @param session
	 * @param sUserName
	 */
	public static void putHttpssionmapUser(HttpSession session, String sUserName) {
		if (httpssionmapUser.containsValue(sUserName)) {// 果该用户已经登录过，则使上次登录的用户掉线(依据使用户名是否在hUserName中)
			// 遍历原来的hUserName，删除原用户名对应的sessionID(即删除原来的sessionID和username)

			Iterator iter = httpssionmapUser.entrySet().iterator();

			while (iter.hasNext()) {

				Map.Entry entry = (Map.Entry) iter.next();

				Object key = entry.getKey();

				Object val = entry.getValue();

				if (((String) val).equals(sUserName)) {

					httpssionmapUser.remove(key);

				}

			}

			httpssionmapUser.put(session.getId(), sUserName);// 添加现在的sessionID和username

		} else {// 如果该用户没登录过，直接添加现在的sessionID和username

			httpssionmapUser.put(session.getId(), sUserName);

		}

	}

	/*
	 * 
	 * isAlreadyEnter-用于判断用户是否已经登录以及相应的处理方法
	 * 
	 * @param sUserName String-登录的用户名称
	 * 
	 * @return boolean-该用户是否已经登录过的标志
	 * 
	 */

	public static boolean putAlreadyEnter(HttpSession session, String sUserName) {

		boolean flag = false;

		if (httpssionmapUser.containsValue(sUserName)) {// 果该用户已经登录过，则使上次登录的用户掉线(依据使用户名是否在hUserName中)

			flag = true;

			// 遍历原来的hUserName，删除原用户名对应的sessionID(即删除原来的sessionID和username)

			Iterator iter = httpssionmapUser.entrySet().iterator();

			while (iter.hasNext()) {

				Map.Entry entry = (Map.Entry) iter.next();

				Object key = entry.getKey();

				Object val = entry.getValue();

				if (((String) val).equals(sUserName)) {

					httpssionmapUser.remove(key);

				}

			}

			httpssionmapUser.put(session.getId(), sUserName);// 添加现在的sessionID和username

//			System.out.println("hUserName = " + httpssionmapUser);

		} else {// 如果该用户没登录过，直接添加现在的sessionID和username

			flag = false;

			httpssionmapUser.put(session.getId(), sUserName);

		}

		return flag;

	}


	/**
	 * 用于判断用户是否在线
	 * 
	 * @param session
	 *            HttpSession-登录的用户名称
	 * @return boolean-该用户是否在线的标志
	 */
	public static boolean isOnline(HttpSession session) {
		boolean flag = true;
		if (httpssionmapUser.containsKey(session.getId())) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}

}
