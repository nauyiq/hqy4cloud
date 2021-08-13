package com.hqy.rpc.regist;

/**
 * 灰度发布还是正常发布？
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-13 10:44
 */
public enum GreyWhitePub {

    GRAY(50),

    WHITE(100);
    ;

    private int value;

    GreyWhitePub(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
