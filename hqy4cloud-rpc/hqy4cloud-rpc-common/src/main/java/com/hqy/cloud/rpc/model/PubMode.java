package com.hqy.cloud.rpc.model;

/**
 * 灰度发布还是正常发布？
 * @author qy
 * @date 2021-08-13 10:44
 */
public enum PubMode {

    /**
     * 没有采用灰度模式
     */
    NONE(0),

    /**
     * 灰度发布
     */
    GRAY(50),

    /**
     * 白度发布
     */
    WHITE(100),

    /**
     * 同ip同环卡
     */
    HIGH(150),

    ;


    public final int value;

    PubMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static PubMode of(int value) {
        PubMode[] values = PubMode.values();
        for (PubMode pubMode : values) {
            if (pubMode.value == value) {
                return pubMode;
            }
        }
        throw new UnsupportedOperationException("Not support value for PubMode.");
    }

}
