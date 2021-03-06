package com.wldk.framework.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huateng.pay.common.constants.Dict;
import com.huateng.pay.common.constants.StringConstans;
import com.wldk.framework.mapping.MappingContext;

/**
 * 系统mapping.xml内存转换处理工具类
 *
 * @author Administrator
 *
 */
public class MappingUtils {

	private static Logger logger = LoggerFactory.getLogger(MappingUtils.class);

	/**
	 * 系统错误翻译
	 * 
	 * @param key
	 * @return
	 */
	public static String getException(String key) {
		return getString("M_002", key);
	}

	/**
	 * 重置指定内存块数据
	 *
	 * @param key： 模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 */
	public static void MappingContextreinit(String key) {
		// 重置内存中的构信息
		try {
			MappingContext.getInstance().reinit(key);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 重置全部内存数据(对应mapping.xml)
	 */
	public static void MappingContextreinit() {
		// 重置内存中的构信息
		try {
			MappingContext.getInstance().reinit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 初始化全部内存数据(对应mapping.xml)
	 */
	public static void MappingContextreStart() {
		// 重置内存中的构信息
		try {
			MappingContext.getInstance().init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 通过key获取mapping.xml中的参数
	 *
	 * @param key ：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @return
	 */
	public static List<Object[]> genMapToList(String key) {
		List<Object[]> list = new ArrayList<Object[]>();
		if (key != null && StringUtil.isNotEmpty(key)) {
			Map<String, Object[]> map = MappingContext.getInstance().get(key);
			Iterator<Map.Entry<String, Object[]>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object[]> em = it.next();
				list.add(em.getValue());
			}
		}
		return list;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param key： 模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> getmappingO(String key) {
		if (StringUtil.isNotEmpty(key)) {
			Map<String, Object[]> cache = MappingContext.getInstance().get(key);
			if (cache != null) {
				Map<String, Object> dataMap = new HashMap<String, Object>();
				if (!cache.isEmpty() && cache.size() > 0) {
					Iterator<Map.Entry<String, Object[]>> it = cache.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object[]> em = it.next();
						if (em.getValue() != null && em.getValue().length > 0) {
							dataMap.put(em.getKey(), em.getValue()[1]);
						} else {
							dataMap.put(em.getKey(), "");
						}
					}
				}
				return dataMap;
			}
		}
		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @return Map<String, Object>
	 */
	public static Map<String, String> getmappingS(String key) {
		if (StringUtil.isNotEmpty(key)) {
			Map<String, Object[]> cache = MappingContext.getInstance().get(key);
			if (cache != null) {
				Map<String, String> dataMap = new HashMap<String, String>();
				if (!cache.isEmpty() && cache.size() > 0) {
					Iterator<Map.Entry<String, Object[]>> it = cache.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object[]> em = it.next();
						if (em.getValue() != null && em.getValue().length > 0) {
							dataMap.put(em.getKey(), String.valueOf(em.getValue()[0]));
						} else {
							dataMap.put(em.getKey(), "");
						}
					}
				}
				return dataMap;
			}
		}
		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @return Map<String, Object[]>
	 */
	public static Map<String, Object[]> getmappingMap(String key) {
		if (StringUtil.isNotEmpty(key)) {
			Map<String, Object[]> cache = MappingContext.getInstance().get(key);
			if (cache != null) {
				Map<String, Object[]> dataMap = new HashMap<String, Object[]>();
				if (!cache.isEmpty() && cache.size() > 0) {
					Iterator<Map.Entry<String, Object[]>> it = cache.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object[]> em = it.next();
						if (em.getValue() != null && em.getValue().length > 0) {
							dataMap.put(em.getKey(), em.getValue());
						} else {
							dataMap.put(em.getKey(), new Object[0]);
						}
					}
				}
				return dataMap;
			}
		}
		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @return List<Object[]>
	 */
	public static List<Object[]> getmappingList(String key) {
		if (StringUtil.isNotEmpty(key)) {
			Map<String, Object[]> cache = MappingContext.getInstance().get(key);
			if (cache != null) {
				List<Object[]> dataList = new ArrayList<Object[]>();
				if (!cache.isEmpty() && cache.size() > 0) {
					Iterator<Map.Entry<String, Object[]>> it = cache.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object[]> em = it.next();
						if (em.getValue() != null && em.getValue().length > 0) {
							dataList.add(em.getValue());
						} else {
							dataList.add(new Object[0]);
						}
					}
				}
				return dataList;
			}
		}
		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @return Map<String, String[]>
	 */
	public static Map<String, String[]> getmappingMapS(String key) {
		if (StringUtil.isNotEmpty(key)) {
			Map<String, Object[]> cache = MappingContext.getInstance().get(key);
			if (cache != null) {
				Map<String, String[]> dataMap = new HashMap<String, String[]>();
				if (!cache.isEmpty() && cache.size() > 0) {
					Iterator<Map.Entry<String, Object[]>> it = cache.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<String, Object[]> em = it.next();
						if (em.getValue() != null && em.getValue().length > 0) {

							dataMap.put(em.getKey(), setString(em.getValue()));
						} else {
							dataMap.put(em.getKey(), new String[0]);
						}
					}
				}
				return dataMap;
			}
		}
		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param          key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @param cacheKey 过滤条件
	 * @return Object[]
	 */
	public static String[] setString(Object[] obj) {
		if (obj != null && obj.length > 0) {
			String[] val = new String[obj.length];
			for (int i = 0; i < obj.length; i++) {
				val[i] = String.valueOf(obj[i]);
			}
			return val;
		} else {
			return new String[0];
		}

	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param          key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @param cacheKey 过滤条件
	 * @return Object[]
	 */
	public static Object[] get(String key, String cacheKey) {
		Map<String, Object[]> cache = MappingContext.getInstance().get(key);
		if (cache != null) {
			if (!cache.isEmpty() && cache.size() > 0) {
				if (cache.get(cacheKey) != null && cache.get(cacheKey).length > 0) {
					return cache.get(cacheKey);
				}
			}
		}

		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param          key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @param cacheKey 过滤条件
	 * @return Object[]
	 */
	public static String[] getS(String key, String cacheKey) {
		Map<String, Object[]> cache = MappingContext.getInstance().get(key);
		if (cache != null) {
			if (!cache.isEmpty() && cache.size() > 0) {
				if (cache.get(cacheKey) != null && cache.get(cacheKey).length > 0) {
					Object[] obj = cache.get(cacheKey);
					if (obj != null && obj.length > 0) {
						String[] val = new String[obj.length];
						for (int i = 0; i < obj.length; i++) {
							val[i] = String.valueOf(obj[i]);
						}
						return val;
					} else {
						return new String[0];
					}
				}
			}
		}

		return null;
	}

	/**
	 * 字段翻译(mapping-config.xml)
	 *
	 * @param          key：模板标示(对应mapping.xml中mapping id="M_001" 如：M_007)
	 * @param cacheKey 过滤条件
	 * @return String
	 */
	public static String getString(String key, String cacheKey) {
		Map<String, Object[]> cache = MappingContext.getInstance().get(key);
		if (cache != null) {
			if (!cache.isEmpty() && cache.size() > 0) {
				if (cache.get(cacheKey) != null && cache.get(cacheKey).length > 0) {
					return cache.get(cacheKey)[1].toString();
				}
			}
		}
		return null;

	}

	/**
	 * 对key是channel_config的特殊处理
	 * 
	 * @param key
	 * @param cacheKey 联合主键格式:key1_key2
	 * @return
	 */
	public static Map<String, String> getChannelConfig(String cacheKey) {
		if (StringUtil.isEmpty(cacheKey)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		Object[] obj = MappingContext.getInstance().get(StringConstans.MappingConfig.CHANNEL_CONFIG).get(cacheKey);
		if (obj == null) {
			logger.info("配置信息缓存中未找到:"+cacheKey);
			return null;
		}
		for (int i = 0; i < obj.length; i++) {
			map.put(Dict.CHANNEL, StringUtil.toString(obj[0]));
			map.put(Dict.RATE, StringUtil.toString(obj[1]));
			map.put(Dict.ALI_PID, StringUtil.toString(obj[2]));
			map.put(Dict.ALI_APPID, StringUtil.toString(obj[3]));
			map.put(Dict.ALI_MERID, StringUtil.toString(obj[4]));
			map.put(Dict.ALI_PRIVATE_KEY, StringUtil.toString(obj[5]));
			map.put(Dict.ALI_PUBLIC_KEY, StringUtil.toString(obj[6]));
			map.put(Dict.WX_APPID, StringUtil.toString(obj[7]));
			map.put(Dict.WX_MERID, StringUtil.toString(obj[8]));
			map.put(Dict.WX_KEY, StringUtil.toString(obj[9]));
			map.put(Dict.WX_PFX_PATH, StringUtil.toString(obj[10]));
			map.put(Dict.WX_PWD, StringUtil.toString(obj[11]));
			map.put(Dict.WX_JSAPI_SECRET, StringUtil.toString(obj[12]));
			map.put(Dict.WX_CHANNEL_ID, StringUtil.toString(obj[13]));
		}
		return map;
	}

	public static Map<String, String> getChannelConfigByAppid(String appid) {
		if (StringUtil.isEmpty(appid)) {
			return null;
		}
		Map<String, String> map = new HashMap<String, String>();
		Map<String, Object[]> configMap = MappingContext.getInstance().get(StringConstans.MappingConfig.CHANNEL_CONFIG);
		if (configMap == null) {
			return null;
		}
		Set<String> set = configMap.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object[] obj = configMap.get(key);
			String wxAppid = StringUtil.toString(obj[7]);
			String aliAppid = StringUtil.toString(obj[3]);
			if (appid.equals(wxAppid) || appid.equals(aliAppid)) {
				map.put(Dict.CHANNEL, StringUtil.toString(obj[0]));
				map.put(Dict.RATE, StringUtil.toString(obj[1]));
				map.put(Dict.ALI_PID, StringUtil.toString(obj[2]));
				map.put(Dict.ALI_APPID, StringUtil.toString(obj[3]));
				map.put(Dict.ALI_MERID, StringUtil.toString(obj[4]));
				map.put(Dict.ALI_PRIVATE_KEY, StringUtil.toString(obj[5]));
				map.put(Dict.ALI_PUBLIC_KEY, StringUtil.toString(obj[6]));
				map.put(Dict.WX_APPID, StringUtil.toString(obj[7]));
				map.put(Dict.WX_MERID, StringUtil.toString(obj[8]));
				map.put(Dict.WX_KEY, StringUtil.toString(obj[9]));
				map.put(Dict.WX_PFX_PATH, StringUtil.toString(obj[10]));
				map.put(Dict.WX_PWD, StringUtil.toString(obj[11]));
				map.put(Dict.WX_JSAPI_SECRET, StringUtil.toString(obj[12]));
				map.put(Dict.WX_CHANNEL_ID, StringUtil.toString(obj[13]));
				break;
			}
		}

		return map;
	}

}
