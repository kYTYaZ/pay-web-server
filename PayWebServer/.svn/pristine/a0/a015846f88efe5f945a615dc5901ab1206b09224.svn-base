/**
 * 
 */
package com.wldk.framework.db;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于生成动态代理类实例的管理器类
 * 
 * @author Administrator
 * 
 */
public class TransactionManager {
	/** 单例 */
	private static TransactionManager instance = new TransactionManager();

	/**
	 * 构造方法
	 */
	protected TransactionManager() {
	}

	/**
	 * 获取单例对象的方法
	 * 
	 * @return
	 */
	public static TransactionManager getInstance() {
		return instance;
	}

	/**
	 * 获取动态代理类实例的方法
	 * 
	 * @return
	 */
	public TransactionProxy getProxy() {
		return (TransactionProxy) Proxy.newProxyInstance(TransactionProxy.class
				.getClassLoader(), new Class[] { TransactionProxy.class },
				new TransactionAwareInvocationHandler());
	}

	/**
	 * 动态代理类的处理接口实现
	 * 
	 * @author Administrator
	 * 
	 */
	private class TransactionAwareInvocationHandler implements
			InvocationHandler {
		/** 日志 */
		private final Logger log = LoggerFactory.getLogger(TransactionAwareInvocationHandler.class);
		/** 命令对象数组 */
		private List<AbstractDataOperation<Integer>> targets;

		/**
		 * 构造方法
		 */
		public TransactionAwareInvocationHandler() {
			this.targets = new LinkedList<AbstractDataOperation<Integer>>();
		}

		/*
		 * 处理方法实现
		 * 
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		@SuppressWarnings("unchecked")
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			// TODO Auto-generated method stub
			if (method.getName().equals("setTarget")) {
				// log.debug(ArrayUtils.toString(args));
				AbstractDataOperation<Integer> target = (AbstractDataOperation<Integer>) args[0];
				if (target != null) {
					target.setTransactionActive(true);
					this.targets.add(target);
				}
				return (TransactionProxy) proxy;
			}else if(method.getName().equals("rollback")){
			    AbstractDataOperation.doRollback();
			} else if (method.getName().equals("commit")) {
				try {
					// 开始一个事务
					AbstractDataOperation.doBegin();
					for (int i = 0, n = targets.size(); i < n; i++) {
						AbstractDataOperation<Integer> target = targets.get(i);
						if (target != null) {
							target.execute();
						}
					}
					// 提交事务
					AbstractDataOperation.doCommit();
				} catch (SQLException sqle) {
					log.error(sqle.getMessage(),sqle);
					AbstractDataOperation.doRollback();
					throw sqle;
				} finally {
					// 清理资源，关闭连接
					AbstractDataOperation.doReleaseConnection();
					this.targets.clear();
//					this.targets = null;
				}
			}
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		TransactionProxy proxy = TransactionManager.getInstance().getProxy();
		proxy.commit();
	}
}
