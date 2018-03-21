package com.stur.lib.constant;

/**
 * Created by guanxuejin on 2018/3/20.
 */

public class StSystem {
    //CPU调频路径
    public static final String PATH_CPU_FREQ = "/sys/devices/system/cpu/cpu4/cpufreq";

    public static final String[] CPU_FREQ_SCAL_AVLB = {"interactive", "conservative",
            "ondemand", "userspace", "powersave", "performance"};

}
