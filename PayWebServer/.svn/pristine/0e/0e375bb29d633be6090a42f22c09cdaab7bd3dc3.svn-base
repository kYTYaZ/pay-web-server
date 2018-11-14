/**
 * 
 */
package com.wldk.framework.mapping;

import com.huateng.pay.common.constants.StringConstans;
import com.huateng.pay.common.util.StringUtil;
import com.wldk.framework.dao.JdbcSpringDaoFromWorkManagerUtil;
import com.wldk.framework.mapping.xml.MappingElement;
import com.wldk.framework.mapping.xml.OptionElement;
import com.wldk.framework.spring.db.dialect.SpringContextUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author zdk
 *
 */
public class MappingContext {
	/** 单例 */
	private static MappingContext instance = new MappingContext();

	/** 缓存 */
	private Map<String, Map<String, Object[]>> context;
	/** 配置元数据 */
	private Map<String, MappingElement> metadata;
	protected Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * 构造方法
	 * 
	 */
	protected MappingContext() {
		// 实例化缓存对象
		context = new TreeMap<String, Map<String, Object[]>>();
		metadata = new HashMap<String, MappingElement>();
	}

	/**
	 * 获取单例对象的方法
	 * 
	 * @return
	 */
	public static MappingContext getInstance() {
		return instance;
	}

	public void addMetadata(String key, MappingElement mapping) {
		if (key != null && mapping != null) {
			metadata.put(key, mapping);
		}
	}

	/**
	 * @return the metadata
	 */
	public Map<String, MappingElement> getMetadata() {
		return metadata;
	}

	public Object[] get(String key, String cacheKey) {
		if (context != null) {
			Map<String, Object[]> cache = context.get(key);
			return cache.get(cacheKey);
		}
		return null;
	}

	public Map<String, Object[]> get(String key) {
		if (context != null) {
			return context.get(key);
		}
		return null;
	}

	/**
	 * 在上下文中指定的键值中加入一个缓存
	 * 
	 * @param key
	 * @param cache
	 */
	public void add(String key, Map<String, Object[]> cache) {
		if (cache == null) {
			cache = new HashMap<String, Object[]>();
		}
		context.put(key, cache);
	}

	/**
	 * 在上下文中指定的键值的缓存中加入一个元素
	 * 
	 * @param key
	 * @param cacheKey
	 * @param cacheValue
	 */
	public void add(String key, String cacheKey, Object[] cacheValue) {
		Map<String, Object[]> cache = context.get(key);
		if (cache == null) {
			cache = new HashMap<String, Object[]>();
		}
		cache.put(cacheKey, cacheValue);
		add(key, cache);
	}

	public boolean set(String key, String cacheKey, Object[] cacheValue) {
		Map<String, Object[]> cache = context.get(key);
		if (cache == null) {
			return false;
		}
		add(key, cacheKey, cacheValue);
		return true;
	}

	public boolean delete(String key, String cacheKey) {
		Map<String, Object[]> cache = context.get(key);
		if (cache != null) {
			cache.remove(cacheKey);
			return true;
		} else {
			return false;
		}
	}

	public boolean deleteCache(String key) {
		if (context != null) {
			context.remove(key);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 初始化方法
	 */
	public void init() throws SQLException {
		long startTime=System.currentTimeMillis();  
		if (metadata != null && metadata.size() > 0) {
			log.info("系统内存：[ 开始初始化 ] ......");
			Set<String> keyset = metadata.keySet();
			for (String key : keyset) {
				load(key);
			}
			long endTime=System.currentTimeMillis(); 
			log.info("系统内存：[ 初始化完成，共耗时："+(endTime-startTime)+" ms ] ......");
		}else{
			long endTime=System.currentTimeMillis(); 
			log.error("系统内存：[ 初始化失败 ：metadata为空，共耗时："+(endTime-startTime)+" ms ] ......");
		}
	}

	/**
	 * 重置指定内存块数据方法
	 * 
	 * @param key
	 *            重置的内存表示
	 * @throws SQLException
	 */
	public void reinit(String key) throws SQLException {
		long startTime=System.currentTimeMillis();  
		if (metadata != null && metadata.size() > 0) {
			log.info("系统内存：[ 开始初始化指定：" + key + " ] ......");
			load(key);
			long endTime=System.currentTimeMillis(); 
			log.info("系统内存：[ 初始化指定：" + key + "完成，共耗时："+(endTime-startTime)+" ms ] ......");
		}else{
			long endTime=System.currentTimeMillis(); 
			log.error("系统内存：[ 初始化失败 ：metadata为空，共耗时："+(endTime-startTime)+" ms ] ......");
		}
	}

	/**
	 * 重置内存数据方法
	 * 
	 * @throws SQLException
	 */
	public void reinit() throws SQLException {
		long startTime=System.currentTimeMillis();  
		if (metadata != null && metadata.size() > 0) {
			log.info("系统内存：[ 开始重置全部数据 ] ......");
			Set<String> keyset = metadata.keySet();
			for (String key : keyset) {
				load(key);
			}
			long endTime=System.currentTimeMillis(); 
			log.info("系统内存：[ 重置全部数据完成，共耗时："+(endTime-startTime)+" ms ] ......");
		}else{
			long endTime=System.currentTimeMillis(); 
			log.error("系统内存：[ 重置全部数据失败 ：metadata为空，共耗时："+(endTime-startTime)+" ms ] ......");
		}
	}

	/**
	 * 关闭
	 */
	public void shutdown() {
		if (context != null) {
			context.clear();
			context = null;
		}
		if (metadata != null) {
			metadata.clear();
			metadata = null;
		}
	}

	/**
	 * 检查指定的值是否存在与指定键值的缓存中，如果存在则返回符合条件的第一条缓存记录
	 * 
	 * @param key
	 * @param cacheValue
	 * @return
	 */
	public Object[] containsValue(String key, Object cacheValue) {
		if (context != null) {
			Map<String, Object[]> cache = context.get(key);
			if (cache != null) {
				Set<Entry<String, Object[]>> set = cache.entrySet();
				for (Entry<String, Object[]> entry : set) {
					Object[] values = entry.getValue();
					if (values != null
							&& ArrayUtils.contains(values, cacheValue)) {
						return values;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 重新装载指定关键字的缓存
	 * 
	 * @param key
	 */
	public void load(String key) throws SQLException {
		MappingElement mapping = metadata.get(key);
		if (mapping != null) {
			Map<String, Object[]> entity = context.get(key);
			if (entity == null) { // 如果不存在，则新建一个
				entity = new TreeMap<String, Object[]>();
			} else {
				entity.clear();// 清空重建
			}
			if (mapping.getSql() != null && mapping.getDsName() != null) { // 查询数据的
				log.info("动态数据：[" + key + "]");
				// 获取查询对象，并执行SQL语句
//				Query query = DataOperationCreator.getInstance(mapping.getDsName()).createQuery(null);

				// 查询数据库
//				List<Object[]> rows = query.query(mapping.getSql().getSql());
				
				
				List<Object[]> rows =new ArrayList<Object[]>();
				try {
					//获取Spring对象
					JdbcSpringDaoFromWorkManagerUtil query=(JdbcSpringDaoFromWorkManagerUtil)SpringContextUtil.getBean("daoAwareAdapter");
					rows =query.queryArrays(mapping.getSql().getSql());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(),e);
				}
				// log.debug(query.listToString(rows));
				// 将每条记录放入到Map中
				if(StringConstans.MappingConfig.CHANNEL_CONFIG.equals(key)){//联合主键的特殊处理
					for (Object[] row : rows) {
						if (row != null) {
							String mappingKey = StringUtil.toString(row[0])+"_"+StringUtil.toString(row[1]);
							entity.put(mappingKey.trim(), row);
						}
					}
				} else {
					for (Object[] row : rows) {
						if (row != null) {
							Object obj = ((Object[]) row)[0];
							String mappingKey = obj != null ? String.valueOf(obj): "";
							entity.put(mappingKey.trim(), row);
						}
					}
				}
			} else if (mapping.getEntity() != null) {
				log.info("静态数据：[" + key + "]");
				List<OptionElement> options = mapping.getEntity().getOptions();
				for (OptionElement option : options) {
					if (option != null && option.getName() != null
							&& option.getValue() != null) {
						entity.put(option.getValue(),new Object[] { option.getValue(),option.getName() });
					}
				}
			}
			context.put(key, entity);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	public String toString() {
		// TODO Auto-generated method stub
		StrBuilder sb = (new StrBuilder()).appendNewLine();
		if (context != null && context.size() > 0) {
			Set<Entry<String, Map<String, Object[]>>> set = context.entrySet();
			for (Entry<String, Map<String, Object[]>> entry : set) {
				String id = entry.getKey();
				Map<String, Object[]> value = entry.getValue();
				sb.appendln("[ID: " + id + "]");
				Set<Entry<String, Object[]>> cache = value.entrySet();
				for (Entry<String, Object[]> options : cache) {
					String key = options.getKey();
					Object[] values = options.getValue();
					sb.append("[KEY: " + key + "][")
							.append(ArrayUtils.toString(values)).append("]")
							.appendNewLine();
				}
				sb.appendPadding(35, '-').appendNewLine();
			}
		}
		return sb.toString();
	}

	/**
	 * 返回即时的代码映射，即直接从数据库中获取
	 * 
	 * @param mappingKey
	 * @return
	 */
	public Map<String, String> getOnlineCache(String cacheKey) {
		Map<String, String> map = new TreeMap<String, String>();
		try {
			Map<String, Object[]> cache = get(cacheKey);
			if (cache != null) {
				for (Map.Entry<String, Object[]> entry : cache.entrySet()) {
					if (entry != null) {
						String key = entry.getKey();
						Object[] values = entry.getValue();
						map.put(key, values[1].toString());
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		return map;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
