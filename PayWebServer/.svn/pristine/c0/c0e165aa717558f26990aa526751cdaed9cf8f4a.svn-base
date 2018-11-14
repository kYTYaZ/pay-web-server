package com.wldk.framework.db;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据源池实现<br>
 * 主要用于多个数据库服务器的情况下，可以分配到不同主机上的连接池。Hash算法为MD5<br>
 * 
 * @author Administrator
 * 
 */
public class DataSourcePool {
	/** 日志 */
	private static Logger log = LoggerFactory.getLogger(DataSourcePool.class);

	/** 存储数据源池的容器 */
	private static Map<DataSourceName, DataSourcePool> pools = new HashMap<DataSourceName, DataSourcePool>();

	/** 桶容器 */
	private TreeMap<Long, String> consistentBuckets;

	private Map<String, DataSource> dataSources;

	private String[] dataSourceNames;

	private Integer[] weights;

	private Integer totalWeight = 0;

	// avoid recurring construction
	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {

		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				log.error("++++ no md5 algorithm found");
				throw new IllegalStateException("++++ no md5 algorythm found");
			}
		}
	};

	// empty constructor
	protected DataSourcePool() {
	}

	/**
	 * Factory to create/retrieve new pools given a unique poolName.
	 * 
	 * @param poolName
	 *            unique name of the pool
	 * @return instance of SockIOPool
	 */
	public static synchronized DataSourcePool getInstance(DataSourceName poolName) {
		if (pools.containsKey(poolName)) {
			return pools.get(poolName);
		}
		DataSourcePool pool = new DataSourcePool();
		pools.put(poolName, pool);
		return pool;
	}

	public Map<String, DataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(Map<String, DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public static Map<DataSourceName, DataSourcePool> getPools() {
		return pools;
	}

	/**
	 * Initializes the pool.
	 */
	public void initialize() {
		synchronized (this) {
			// if servers is not set, or it empty, then
			// throw a runtime exception
			if (dataSources == null || dataSources.size() <= 0) {
				throw new IllegalStateException("++++ trying to initialize with no datasource");
			}
			dataSourceNames = dataSources.keySet().toArray(new String[0]);
			// initalize our internal hashing structures
			populateConsistentBuckets();
		}
	}

	/**
	 * 销毁的方法
	 * 
	 */
	public void destory() {
		if (dataSources != null) {
			Collection<DataSource> dsCols = dataSources.values();
			for (DataSource ds : dsCols) {
				// log.debug("destory:"+ds);
				// 如果是DBCP的数据源，就转换类型，并关闭数据源
				if (ds != null && ds instanceof BasicDataSource) {
					try {
						((BasicDataSource) ds).close();
					} catch (SQLException sqle) {
						log.error(sqle.getMessage(),sqle);
					} finally {
						ds = null;
					}
				}
			}
			// 清空数据源容器，并释放
			dataSources.clear();
			dataSources = null;
		}
	}

	private void populateConsistentBuckets() {
		// store buckets in tree map
		this.consistentBuckets = new TreeMap<Long, String>();
		MessageDigest md5 = MD5.get();
		if (this.totalWeight <= 0 && this.weights != null) {
			for (int i = 0; i < this.weights.length; i++)
				this.totalWeight += (this.weights[i] == null) ? 1 : this.weights[i];
		} else if (this.weights == null) {
			this.totalWeight = this.dataSourceNames.length;
		}
		for (int i = 0; i < dataSourceNames.length; i++) {
			int thisWeight = 1;
			if (this.weights != null && this.weights[i] != null)
				thisWeight = this.weights[i];
			double factor = Math.floor(((double) (40 * this.dataSourceNames.length * thisWeight)) / (double) this.totalWeight);
			for (long j = 0; j < factor; j++) {
				byte[] d = md5.digest((dataSourceNames[i] + "-" + j).getBytes());
				for (int h = 0; h < 4; h++) {
					Long k = ((long) (d[3 + h * 4] & 0xFF) << 24) | ((long) (d[2 + h * 4] & 0xFF) << 16) | ((long) (d[1 + h * 4] & 0xFF) << 8) | ((long) (d[0 + h * 4] & 0xFF));

					consistentBuckets.put(k, dataSourceNames[i]);
				}
			}
		}
	}

	private long getBucket(String key, Integer hashCode) {
		long hc = getHash(key, hashCode);
		return findPointFor(hc);
	}

	private Long findPointFor(Long hv) {
		// this works in java 6, but still want to release support for java5
		// Long k = this.consistentBuckets.ceilingKey( hv );
		// return ( k == null ) ? this.consistentBuckets.firstKey() : k;
		SortedMap<Long, String> tmap = this.consistentBuckets.tailMap(hv);
		return (tmap.isEmpty()) ? this.consistentBuckets.firstKey() : tmap.firstKey();
	}

	/** 获取数据源 */
	public DataSource getDataSource(String key, Integer hashCode) {
		long bucket = getBucket(key, hashCode);
		// log.debug("bucket:" + bucket);
		// log.debug("size:" + consistentBuckets.size());
		// log.debug("consistentBuckets:" + consistentBuckets);
		return dataSources.get(consistentBuckets.get(bucket));
	}

	private long getHash(String key, Integer hashCode) {
		if (hashCode != null) {
			return hashCode.longValue() & 0xffffffffL;
		} else {
			return md5HashingAlg(key);
		}
	}

	private static long md5HashingAlg(String key) {
		MessageDigest md5 = MD5.get();
		md5.reset();
		md5.update(key.getBytes());
		byte[] bKey = md5.digest();
		long res = ((long) (bKey[3] & 0xFF) << 24) | ((long) (bKey[2] & 0xFF) << 16) | ((long) (bKey[1] & 0xFF) << 8) | (long) (bKey[0] & 0xFF);
		return res;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// // TODO Auto-generated method stub
		// int h = "ssssss".hashCode();
		// h ^= (h >>> 20) ^ (h >>> );
		// System.out.println(h ^ (h >>> 7) ^ (h >>> 4));
	}

}
