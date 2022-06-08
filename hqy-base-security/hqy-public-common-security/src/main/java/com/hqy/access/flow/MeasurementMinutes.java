package com.hqy.access.flow;

/**
 * 分钟计量单位
 * @author qy
 * @date  2021-08-04 14:25
 */
public enum MeasurementMinutes {

    /**
     * 1分钟
     */
    ONE_MINUTE(1),

    /**
     * 5分钟
     */
    FIVE_MINUTES(5),

    /**
     * 10分钟
     */
    TEN_MINUTES(10),

    /**
     * 30分钟
     */
    THIRTY_MINUTES(30),

    /**
     * 一小时
     */
    ONE_HOUR_MINUTES(60);

     MeasurementMinutes(int x) {
        this.minutes = x;
    }

    private int minutes = 1;

    public int getMinutes() {
        return this.minutes;
    }

    public static MeasurementMinutes find(int windowMinutes) {
        for (MeasurementMinutes measurementMinutes : values()) {
            if (measurementMinutes.minutes == windowMinutes) {
                return measurementMinutes;
            }
        }
        //兜底...
        return ONE_MINUTE;
    }
}
