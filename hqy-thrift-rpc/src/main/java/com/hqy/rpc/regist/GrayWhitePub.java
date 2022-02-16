package com.hqy.rpc.regist;

/**
 * 灰度发布还是正常发布？
 * @author qy
 * @date 2021-08-13 10:44
 */
public enum GrayWhitePub {

    /**
     * 灰度发布
     */
    GRAY(50),

    /**
     * 白度发布
     */
    WHITE(100);


    public final int value;

    GrayWhitePub(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
