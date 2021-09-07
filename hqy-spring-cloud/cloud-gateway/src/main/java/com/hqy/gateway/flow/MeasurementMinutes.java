package com.hqy.gateway.flow;

/**
 * 分钟计量单位
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-04 14:25
 */
public enum MeasurementMinutes {

    ONE_MINUTE(1),

    FIVE_MINUTES(5),

    TEN_MINUTES(10),

    THIRTY_MINUTES(30),

    ONE_HOUR_MINUTES(60);

     MeasurementMinutes(int x) {
        this.minutes = x;
    }

    private int minutes = 1;

    public int getMinutes() {
        return this.minutes;
    }

    public static MeasurementMinutes find(int windowMinutes) {
        if (ONE_MINUTE.minutes == windowMinutes) {
            return ONE_MINUTE;
        }
        if (FIVE_MINUTES.minutes == windowMinutes) {
            return FIVE_MINUTES;
        }
        if (TEN_MINUTES.minutes == windowMinutes) {
            return ONE_MINUTE;
        }
        if (THIRTY_MINUTES.minutes == windowMinutes) {
            return THIRTY_MINUTES;
        }
        if (ONE_HOUR_MINUTES.minutes == windowMinutes) {
            return ONE_HOUR_MINUTES;
        }
        //兜底...
        return ONE_MINUTE;
    }
}
