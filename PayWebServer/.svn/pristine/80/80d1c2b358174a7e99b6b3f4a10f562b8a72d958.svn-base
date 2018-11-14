package com.wldk.framework.proxy;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
/**
 * 系统框架系统日志记录代理器
 * @author Administrator
 *
 */
public class FrameworkFacadeCglib implements MethodInterceptor{
	private Object target;
	/**
	 * 创建代理对象
	 * 
	 * @param target
	 * @return
	 */
	public Object getInstance(Object target) {
		this.target = target;
//		  this.conn = (ConnectDatabase) target; 
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target.getClass());
		// 回调方法
		enhancer.setCallback(this);
		// 创建代理对象
		return enhancer.create();
	}
	public Object intercept(Object obj, Method method, Object[] args,MethodProxy proxy) throws Throwable {
//		System.out.println("事物开始");
		proxy.invokeSuper(obj, args);
//		System.out.println("事物结束");
		return null;
	}
}
