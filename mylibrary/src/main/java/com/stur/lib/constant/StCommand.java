package com.stur.lib.constant;

/**
 * Created by Sturmegezhutz on 2018/3/19.
 */

public class StCommand {
    /**
     * 使用命令adb logcat -c 清理main缓存区域的日志
     * 带-b参数指定其他类型: adb logcat -c -b events，清理系统事件信息日志；
     * 如需清理所有缓存日志的要求，可以使用如下命令
     */
    public static final String CLEAR_LOG_CACHE = "logcat -c -b main -b events -b radio -b system";
}
