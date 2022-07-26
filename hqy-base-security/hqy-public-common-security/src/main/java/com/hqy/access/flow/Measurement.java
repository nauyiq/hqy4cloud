package com.hqy.access.flow;

/**
 * 时间计量单位
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/16 17:36
 */
public class Measurement {


    public enum Seconds {

        /**
         * 一秒
         */
        ONE_SECONDS(1),

        /**
         * 一分钟
         */
        ONE_MINUTES(60),

        /**
         * 5分钟
         */
        FIVE_MINUTES(5 * 60),


        /**
         * 十分钟
         */
        TEN_MINUTES(60 * 10),


        /**
         * 半小时
         */
        HALF_HOUR(60 * 30),


        /**
         * 一小时
         */
        ONE_HOUR(60 * 60)

        ;

        public final int seconds;

        Seconds(int seconds) {
            this.seconds = seconds;
        }

    }

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



}
