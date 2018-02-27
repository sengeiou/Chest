package com.stur.lib.web.base;

import android.text.TextUtils;

import com.stur.lib.StringUtils;
import com.stur.lib.db.Cache;
import com.stur.lib.db.CacheManager;
import com.stur.lib.web.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2016/3/4.
 */
public abstract class AbstractHttpService implements HttpService {
    //    private Logger logger = LoggerFactory.getLogger(HttpConstants.TAG);
    private Map<String, String> mHeaders = new HashMap<>();
    private Cache mCache;

    public AbstractHttpService() {
        mCache = CacheManager.getInstance();
    }

    /**
     * 获取请求中的头部信息包
     *
     * @return
     */
    public Map<String, String> getHeaderMap() {
//        logger.v("request mHeaders: " + mHeaders.toString());
        return mHeaders;
    }

    /**
     * 添加请求头信息
     *
     * @param key
     * @param value
     */
    @Override
    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    /**
     * 缓存响应
     * 存在 true
     *
     * @param url
     * @param httpListener
     * @return
     */
    public boolean fireCache(String url, HttpListener<?> httpListener) {
        if (httpListener == null) return false;

        String cacheContent = mCache.getString(url);
        if (!TextUtils.isEmpty(cacheContent)) {
            httpListener.handleResponse(new HttpResponse(0, cacheContent));
            httpListener.onFinish();
//            logger.v(" request: " + url + " FoundCache: " + cacheContent);
            return true;
        }
        return false;
    }

    /**
     * 放到缓存中
     *
     * @param key
     * @param value
     * @param cacheTime
     */
    public void putCache(String key, String value, int cacheTime) {
        if (!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value) && cacheTime > 0) {
            mCache.put(key, value, cacheTime);
        }
    }

}
