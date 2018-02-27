package com.stur.lib.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/3/4.
 */
public class URLBuilder {
    private String mEncoding = "UTF-8";
    private String mURL;

    public URLBuilder(String url) {
        mURL = url;
    }

    public URLBuilder(String url, String encoding) {
        this(url);
        mEncoding = encoding;
    }

    /**
     * http param
     * /goods?id=123&type=test
     *
     * @param name
     * @param value
     * @return
     */
    public URLBuilder append(String name, String value) {
        try {
            if (!mURL.contains("?")) {
                mURL += "?";
            } else if (lastChar() != '?' && lastChar() != '&') {
                mURL += "&";
            }
            mURL += (URLEncoder.encode(name, mEncoding));
            mURL += ("=");
            if (value != null) {
                mURL += (URLEncoder.encode(value, mEncoding));
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding not supported: " + mEncoding, e);
        }
        return this;
    }

    /**
     * RESTFUL
     * /goods/{value}?
     *
     * @param value
     * @return
     */
    public URLBuilder append(String value) {
        if (lastChar() != '/') {
            mURL += '/';
        }
        mURL += (value);
        return this;
    }

    public URLBuilder replace(String name, String value) {
        mURL.replaceAll(name, value);
        return this;
    }

    /**
     * final url
     *
     * @return
     */
    public String build() {
        return mURL.toString();
    }

    /**
     * last char
     *
     * @return
     */
    public char lastChar() {
        return mURL.charAt(mURL.length() - 1);
    }

    @Override
    public String toString() {
        return build();
    }
}
