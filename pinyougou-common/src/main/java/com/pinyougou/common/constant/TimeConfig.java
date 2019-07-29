package com.pinyougou.common.constant;

public class TimeConfig {
    private TimeConfig() {}

    public static final int UNIT_MINUTE = 0;
    public static final int UNIT_HOUR = 1;
    public static final int UNIT_DAY = 2;
    public static final int UNIT_MONTH = 3;
    public static final int UNIT_YEAR = 4;

    public static final long SECONDS_PER_MINUTE = 60;
    public static final long SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
    public static final long SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
    public static final long SECONDS_PER_WEEK = SECONDS_PER_DAY * 7;
    public static final long SECONDS_PER_MONTH = SECONDS_PER_DAY * 30;
    public static final long SECONDS_PER_YEAR = SECONDS_PER_MONTH * 12;

    public static final long MSECONDS_PER_MINUTE = SECONDS_PER_MINUTE * 1000;
    public static final long MSECONDS_PER_HOUR = SECONDS_PER_HOUR * 1000;
    public static final long MSECONDS_PER_DAY = SECONDS_PER_DAY * 1000;
    public static final long MSECONDS_PER_WEEK = SECONDS_PER_WEEK * 1000;
    public static final long MSECONDS_PER_MONTH = SECONDS_PER_MONTH * 1000;
    public static final long MSECONDS_PER_YEAR = SECONDS_PER_YEAR * 1000;

    public static final long[] VALUES = {
        MSECONDS_PER_MINUTE,
        MSECONDS_PER_HOUR,
        MSECONDS_PER_DAY,
        MSECONDS_PER_MONTH,
        MSECONDS_PER_YEAR
    };
    public static final String[] FORMATS = {
        "yyyy-MM-dd HH:mm",
        "yyyy-MM-dd HH时",
        "yyyy-MM-dd",
        "yyyy-MM",
        "yyyy"
    };
}
