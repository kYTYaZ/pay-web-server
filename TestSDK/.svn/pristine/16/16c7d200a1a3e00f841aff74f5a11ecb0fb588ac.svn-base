package com.trade.sycntest;

import java.util.LinkedHashMap;

public class LRUCache<K,V> extends LinkedHashMap<K,V> {
    public LRUCache(int maxSize)
    {
        super(maxSize, 1F, true);
        maxElements = maxSize;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry<K,V> eldest)
    {
        return size() > maxElements;
    }

    private static final long serialVersionUID = 1L;
    protected int maxElements;
}
