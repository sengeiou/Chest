package com.stur.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stur.lib.time.Date;
import com.stur.lib.time.DateFormatter;
import com.stur.lib.time.DateLong;
import com.stur.lib.time.DateLongFormatter;
import com.stur.lib.time.DateTime;
import com.stur.lib.time.DateTimeFormatter;
import com.stur.lib.time.DateTimeZone;
import com.stur.lib.time.DateTimeZoneFormatter;

import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Created by Administrator on 2016/3/4.
 */
public abstract class GsonUtils {
    private static final String TAG = GsonUtils.class.getName();

    private static final Gson GSON = createGson(true);
    private static final Gson GSON_NO_NULLS = createGson(false);

    /**
     * Create the standard {@link com.google.gson.Gson} configuration
     *
     * @return created gson, never null
     */
    public static final Gson createGson() {
        return createGson(true);
    }

    /**
     * Create the standard {@link com.google.gson.Gson} configurationØ
     *
     * @param serializeNulls whether nulls should be serialized
     * @return created gson, never null
     */
    public static final Gson createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();

        // date formatter
        builder.registerTypeAdapter(DateLong.class, new DateLongFormatter());
        builder.registerTypeAdapter(Date.class, new DateFormatter());
        builder.registerTypeAdapter(DateTime.class, new DateTimeFormatter());
        builder.registerTypeAdapter(DateTimeZone.class, new DateTimeZoneFormatter());

        // 命名规则
        // tokenAuth -> token_auth
        // token_auth -> tokenAuth
//        builder.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES);

        // 是否序列号带空的参数到gson中
        // { token:null }
        if (serializeNulls) {
            builder.serializeNulls();
        }
        return builder.create();
    }

    /**
     * Get reusable pre-configured {@link com.google.gson.Gson} instance
     *
     * @return Gson instance
     */
    public static final Gson getGson() {
        return GSON;
    }

    /**
     * Get reusable pre-configured {@link com.google.gson.Gson} instance
     *
     * @return Gson instance
     */
    public static final Gson getGson(final boolean serializeNulls) {
        return serializeNulls ? GSON : GSON_NO_NULLS;
    }

    /**
     * Convert object to json
     *
     * @return json string
     */
    public static final String toJson(final Object object) {
        return toJson(object, true);
    }

    /**
     * Convert object to json
     *
     * @return json string
     */
    public static final String toJson(final Object object, final boolean includeNulls) {
        return includeNulls ? GSON.toJson(object) : GSON_NO_NULLS.toJson(object);
    }

    /**
     * Convert string to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(String json, Class<V> type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Convert string to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * Convert content of reader to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(Reader reader, Class<V> type) {
        return GSON.fromJson(reader, type);
    }

    /**
     * Convert content of reader to given type
     *
     * @return instance of type
     */
    public static final <V> V fromJson(Reader reader, Type type) {
        return GSON.fromJson(reader, type);
    }

}