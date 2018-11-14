package com.huateng.frame.common;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对象转换器类
 * 
 * @author sunguohua
 */
public class TransFormat {
	private static Logger logger = LoggerFactory.getLogger(TransFormat.class);
    /**
     * 对象转map
     * 
     * @param bean
     * @return
     */
    public static Map<String, Object> bean2Map(Object bean,int deep) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (deep > 2000) {
			// 具体数�?由虚拟机内存大小和每次�?归堆栈大小决定，�?���?000次左�?
			deep = 2000;
		}
		if (--deep < 0) {
			return map;
		}
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("get")) {
                Object value = null;
                try {
                    value = method.invoke(bean, null);
                } catch (Exception e) {
                	logger.error("反射出错", e);
                }
                String key = methodName.substring(3, methodName.length());
                key = key.substring(0, 1).toLowerCase() + key.substring(1, key.length());
                if (value instanceof List) { // 为集合的递归将集合中的对象依次转换为map
                    List<Object> childrenList = (List<Object>) value;
                    if (childrenList == null || childrenList.size() == 0) {
                        map.put(key, null);
                    } else {
                        List<Object> transList = new ArrayList<Object>();
                        for (int i = 0; i < childrenList.size(); i++) {
                            Object children = childrenList.get(i);
                            Map transMap = bean2Map(children,2000);
                            transList.add(transMap);
                        }
                        if (transList != null && transList.size() > 0) {
                            map.put(key, transList);
                        }
                    }
                } else {
                    map.put(key, value);
                }
            }
        }
        return map;
    }

    /**
     * 
     * �?��map转对�?
     * 
     * @param map
     * @see 1.0
     * @since 1.0
     */
    public static void map2Bean(Map<String, String> map, Object bean) {
        try {
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if (methodName.startsWith("set")) {
                    String beanKey = methodName.substring(3, methodName.length());
                    beanKey = beanKey.substring(0, 1).toLowerCase() + beanKey.substring(1, beanKey.length());
                    Set keySet = map.keySet();
                    Iterator it = keySet.iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if (beanKey.equals(key)) {
                            Object value = map.get(key);
                            if (value != null
                                    && (value instanceof String || value instanceof Integer || value instanceof Double || value instanceof Float)) {
                                method.invoke(bean, value);
                            }
                        }
                    }

                }
            }

        } catch (Exception e) {
        	logger.error(e.getMessage(),e);
        }
    }
}
