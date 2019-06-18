package com.stur.lib.db;


import com.stur.lib.app.ContextBase;


public class CacheManager {
    private static ContextBase sContext = null;
    private static Cache mCache = null;

    public static void register(ContextBase context) {
        CacheManager.sContext = context;
    }

    public static Cache getInstance() {
        if(mCache == null) {
            mCache = new GCacheImpl(sContext.getCacheDir());
        }
        return mCache;
    }
}
