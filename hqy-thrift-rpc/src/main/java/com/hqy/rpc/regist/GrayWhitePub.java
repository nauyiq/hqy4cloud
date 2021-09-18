package com.hqy.rpc.regist;

/**
 * 灰度发布还是正常发布？
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 10:44
 */
public enum GrayWhitePub {

    GRAY(50),

    WHITE(100);
    ;

    public int value;

    GrayWhitePub(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
