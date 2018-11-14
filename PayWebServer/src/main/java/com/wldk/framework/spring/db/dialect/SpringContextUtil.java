package com.wldk.framework.spring.db.dialect;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.wldk.framework.utils.SpringContextUtils;

/**
 * Spring 上下文调用工具
 * 
 * @author Administrator
 *
 */
public class SpringContextUtil {
	private static ApplicationContext context;
	private static Logger log = LoggerFactory.getLogger(SpringContextUtil.class);

	public static void setContext(ApplicationContext ctx) {
		context = SpringContextUtils.getInstance().getContext();
	}
	
	

	public static ApplicationContext getContext() {
		return SpringContextUtils.getInstance().getContext();
	}



	/**
	 * 获得sping对象
	 * 
	 * @param id
	 * @return
	 */
	public static Object getBean(String id) {
		if(context==null){
			context=SpringContextUtils.getInstance().getContext();
		}
		Object obj = context.getBean(id);
		if (obj == null) {
			log.info("bean id [ " + id + " ] not found in context path");
		}
		return obj;
	}
}
