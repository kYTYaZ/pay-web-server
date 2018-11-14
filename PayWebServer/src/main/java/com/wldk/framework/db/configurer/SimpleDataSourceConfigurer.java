package com.wldk.framework.db.configurer;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.wldk.framework.db.DataSourceName;
import com.wldk.framework.db.DataSourcePool;

/**
 * @author Administrator
 * 
 */
public abstract class SimpleDataSourceConfigurer<E> implements
		DataSourceConfigurer<E> {
	/** log4j */
	protected Logger log = LoggerFactory.getLogger(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.configurer.DataSourceConfigurer#initDataSource(java.lang.Object)
	 */
	public void initDataSource(E arg) throws Exception {
		// TODO Auto-generated method stub
		System.out.append("Initializing DataSource Pool...");
		DataSourceName dsName = null;
		try {
			// 获取Spring环境对象
			ApplicationContext context = getApplicationContext(arg);
			String[] beanNames = context.getBeanDefinitionNames();
			// 读取配置文件
			XMLConfiguration config = new XMLConfiguration(
					getConfigFileURL(arg));
			List children = config.getRoot().getChildren("ds");
			// 读取ds定义列表
			for (Object dsNodes : children) {
				// 获取一个ds节点
				ConfigurationNode dsNode = (ConfigurationNode) dsNodes;
				// 读取ds节点的属性节点
				ConfigurationNode dsNodeAttr = dsNode.getAttribute(0);
				if (dsNodeAttr != null) {
					dsName = DataSourceName.ds((String) dsNodeAttr.getValue());
				}
				// 读取ds节点的name子节点列表
				List childrenNodes = dsNode.getChildren("name");
				if (dsName != null && childrenNodes.size() > 0) {
					DataSourcePool pool = DataSourcePool.getInstance(dsName);
					Map<String, DataSource> container = new HashMap<String, DataSource>();
					for (Object nameNodes : childrenNodes) {
						// 获取一个name节点
						ConfigurationNode nameNode = (ConfigurationNode) nameNodes;
						// 匹配BEAN配置中的数据源，如果找到就放到哈希表中
						for (String name : beanNames) {
							if (name.startsWith((String) nameNode.getValue())) {
								DataSource ds = (DataSource) context
										.getBean(name);
								container.put(name, ds);
								break;
							}
						}
					}
					if (container.size() > 0) {
						pool.setDataSources(container);
						// 初始化
						pool.initialize();
					}
				}
			}
		} catch (ConfigurationException cx) {
			throw new IllegalArgumentException("Invalid URL parameter: "
					+ cx.getMessage());
		}
//		System.out.println("Initialized DataSource Pool is completed.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mingdeng.core.db.configurer.DataSourceConfigurer#shutdownDataSource(java.lang.Object)
	 */
	public void shutdownDataSource(E arg) throws Exception {
		// TODO Auto-generated method stub
//		System.out.append("Shutting down DataSource Pool...");
		Map<DataSourceName, DataSourcePool> pools = DataSourcePool.getPools();
		if (pools != null) {
			Collection<DataSourcePool> dataSourcePools = pools.values();
			for (DataSourcePool pool : dataSourcePools) {
				// 关闭数据源
				if (pool != null) {
					pool.destory();
				}
			}
			// 释放
			pools.clear();
			pools = null;
		}
//		System.out.println("Shutdown DataSource Pool is Completed.");
	}

	/** 获取Spring上下文环境 */
	public abstract ApplicationContext getApplicationContext(E arg);

	/** 获取配置文件的URL路径 */
	public abstract URL getConfigFileURL(E arg) throws FileNotFoundException;
}
