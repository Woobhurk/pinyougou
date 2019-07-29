package com.pinyougou.common.constant;

public class UserStatisticsConfig {
    private UserStatisticsConfig() {}

    public static final int TIME_DELTA_HOUR = 0;
    public static final int TIME_DELTA_DAY = 1;
    public static final int TIME_DELTA_MONTH = 2;

    public static final long[] TIME_DELTAS = {
        TimeConfig.MSECONDS_PER_HOUR,
        TimeConfig.MSECONDS_PER_DAY,
        TimeConfig.MSECONDS_PER_MONTH
    };
    public static final String[] TIME_FORMATS = {
        "yyyy-MM-dd HH时",
        "yyyy-MM-dd",
        "yyyy-MM"
    };
}
