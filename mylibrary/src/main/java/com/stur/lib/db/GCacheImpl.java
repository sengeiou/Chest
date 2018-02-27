package com.stur.lib.db;

import com.stur.lib.constant.CacheTime;

import java.io.File;

import io.ganguo.app.gcache.CacheBuilder;
import io.ganguo.app.gcache.Config;
import io.ganguo.app.gcache.interfaces.Cache.Entry;
import io.ganguo.app.gcache.interfaces.GCache;
import io.ganguo.app.gcache.transcoder.StringTranscoder;

/**
 * Created by Administrator on 2016/3/4.
 */
public class GCacheImpl implements Cache {

    public static final int DEFAULT_CACHE_TIME = CacheTime.TEN_MINUTE;

    /**
     * GCache
     */
    private GCache mCache;

    public GCacheImpl(File cacheRootDirectory) {
        // 配置缓存
        mCache = CacheBuilder.newBuilder()
                .withTranscoder(new StringTranscoder())
                .withCacheRootDirectory(cacheRootDirectory)
                .maxDiskUsageBytes(15 * Config.BYTES_MB)
                .maxMemoryUsageBytes(3 * Config.BYTES_MB)
                .defaultCacheTime(DEFAULT_CACHE_TIME)
                .build();
    }

    /**
     * 放入数据
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, String value) {
        put(key, value, DEFAULT_CACHE_TIME);
    }

    @Override
    public void put(String key, Number value) {
        put(key, value, DEFAULT_CACHE_TIME);
    }

    /**
     * 放入数据和缓存时间
     *
     * @param key
     * @param value
     * @param cacheTime
     */
    @Override
    public void put(String key, String value, int cacheTime) {
        if (key == null || value == null) return;

        mCache.putEntry(key, new Entry(value.getBytes(), cacheTime));
    }

    @Override
    public void put(String key, Number value, int cacheTime) {
        put(key, value.toString());
    }

    /**
     * 获取缓存数据
     *
     * @param key
     * @return
     */
    @Override
    public String getString(String key) {
        return mCache.get(key);
    }

    @Override
    public Number getNumber(String key) {
        return Double.parseDouble(getString(key));
    }

    /**
     * 清除缓存
     */
    @Override
    public void clear() {
        mCache.clear();
    }
}
