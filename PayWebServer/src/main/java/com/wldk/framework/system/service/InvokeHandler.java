/**
 * 
 */
package com.wldk.framework.system.service;


/**
 * 业务逻辑调用的处理类接口，Service类需要实现该接口来实现每个具体的业务逻辑处理。
 * 
 * @author Administrator
 * 
 */
public interface InvokeHandler<E> {
	/** 调用处理方法 */
	E invoke() throws Exception;
}
