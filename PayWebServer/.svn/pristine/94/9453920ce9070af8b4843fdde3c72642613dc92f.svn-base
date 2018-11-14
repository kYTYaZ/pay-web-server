package com.wldk.framework.db;

import java.io.File;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.HbmBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wldk.framework.utils.SpringUtil;

public class HibernateUtil {
	
	private static Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

	private static SessionFactory sessionFactory = null;

	// 通过文件方法初始化SessionFactory
	public static synchronized void init(File cfgFile) {
		if (sessionFactory == null) {
			try {
				sessionFactory = new Configuration().configure(cfgFile).buildSessionFactory();
				// String str = "Hibernate SessionFactory初始化成功！";
			} catch (HibernateException ex) {
				try {
					throw new Exception("初始化Hibernate的SessionFactory出错：", ex);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(),e);
				}

			}
		}
	}

	// 默认从Spring获取SessionFactory
	private static SessionFactory getSessionFactory() {
		if (sessionFactory == null)
			sessionFactory = SpringUtil.getBean("sessionFactory");
		return sessionFactory;
	}

	private static final ThreadLocal session = new ThreadLocal();

	/**
	 * 获取当前线程的Session对象
	 * 
	 * @return
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static Session currentSession() throws HibernateException {
		Session s = (Session) session.get();
		if (s == null || !s.isOpen()) {
			if (s != null && !s.isOpen()) {
//				String tstr = "=-=> ReOpen a new Session for thread: " + Thread.currentThread();
			}

			s = getSessionFactory().openSession();
			session.set(s);
//			String str = "===> Open a new Session for thread: " + Thread.currentThread();
		}
		return s;
	}

	/**
	 * 关闭当前线程的Session对象
	 * 
	 * @throws HibernateException
	 */
	@SuppressWarnings("unchecked")
	public static void closeSession() throws HibernateException {
		Session s = (Session) session.get();
		session.set(null);
		if (s != null) {
//			String str = "---> Close a Session for thread: " + Thread.currentThread();
			s.close();
		}
	}
}