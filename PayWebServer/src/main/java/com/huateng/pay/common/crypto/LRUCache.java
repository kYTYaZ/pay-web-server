package com.huateng.pay.common.crypto;

import java.util.LinkedHashMap;

/**
 *  缓存 maxSize指定最多缓存量  
 * 到达量后自动删除最少使用的数据
 * 最大缓存量推荐 2的指数倍数-1
 * @author zlg
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V> {
	private static final long serialVersionUID = 1L;
	protected int maxElements;

	public LRUCache(int maxSize)
    {
        super(maxSize, 1F, true);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry<K,V> eldest)
    {
        return size() > maxElements;
    }

}
