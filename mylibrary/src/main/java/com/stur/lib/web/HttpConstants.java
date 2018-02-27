package com.stur.lib.web;

/**
 * Created by Administrator on 2016/3/4.
 */
public class HttpConstants {
    public static final String TAG = "G_HTTP";

    /**
     * 请求超时时间
     */
    public static final int REQUEST_TIMEOUT_MS = 8000;

    /**
     * 请求重试次数
     */
    public static final int REQUEST_MAX_RETRIES = 5;

    /**
     * 参数默认编码
     */
    public static final String DEFAULT_PARAMS_ENCODING = "UTF-8";


    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";

    public final static String APPLICATION_JSON = "application/json";

    public enum Error {

        DEFUALT_ERROR(0, "网络不给力，请检查网络"),

        /**
         * TimeoutError
         */
        TIMEOUT_ERROR(1, "网络不给力，请检查网络"),

        /**
         * NoConnectionError
         */
        NO_CONNECTION_ERROR(2, "网络异常，未连接成功"),

        /**
         * AuthFailureError
         */
        AUTH_FAILURE_ERROR(3, "登录信息无效，请重新登录"),

        /**
         * ServerError
         */
        SERVER_ERROR(4, "网络异常，服务器错误"),

        /**
         * NetworkError
         */
        NETWORK_ERROR(5, "当前网络不可用，请检查网络设置"),

        /**
         * ParseError
         */
        PARSE_ERROR(6, "数据解析错误"),

        /**
         * 请求数据为空
         */
        RESPONSE_NULL(100, "请求数据为空"),

        /**
         * 请求数据为空
         */
        UNKNOWN_ERROR(101, "网络错误"),

        /**
         * 应用内部错误
         */
        RESPONSE_ERROR(102, "应用内部错误");

        private int code;
        private String message;

        Error(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
