package com.stur.lib.db;

/**
 * Created by Administrator on 2016/3/4.
 */
/**
 * 缓存接口
 * <p/>
 * Created by zhihui_chen on 14-8-14.
 */
public interface Cache {

    /**
     * 放入数据
     *
     * @param key
     * @param value
     */
    public void put(String key, String value);

    public void put(String key, Number value);

    /**
     * 放入数据和缓存时间
     *
     * @param key
     * @param value
     * @param cacheTime
     */
    public void put(String key, String value, int cacheTime);

    public void put(String key, Number value, int cacheTime);

    /**
     * 获取缓存数据
     *
     * @param key
     * @return
     */
    public String getString(String key);

    public Number getNumber(String key);

    /**
     * 清除缓存
     */
    public void clear();
}

