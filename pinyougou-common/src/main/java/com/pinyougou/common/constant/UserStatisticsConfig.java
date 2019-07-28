package com.pinyougou.common.constant;

public class UserStatisticsConfig {
    private UserStatisticsConfig() {}

    public static final int TIME_DELTA_HOUR = 0;
    public static final int TIME_DELTA_DAY = 1;
    public static final int TIME_DELTA_MONTH = 2;

    public static final long[] TIME_DELTAS = {
        3600 * 1000L,
        24 * 2600 * 1000L,
        30 * 24 * 3600 * 1000L
    };
    public static final String[] TIME_FORMATS = {
        "yyyy-MM-dd HH时",
        "yyyy-MM-dd",
        "yyyy-MM"
    };
}
