package com.hqy.util;

import java.util.Random;

/**
 * @author qiyuan.hong
 * @date 2022-03-17 22:09
 */
public class MathUtil {

    private MathUtil() {}


    /**
     * 是否有概率发生
     * @param molecule 分子
     * @param denominator 分母
     * @return boolean
     */
    public static boolean mathIf(int molecule, int denominator) {
        int nextInt = new Random().nextInt(denominator);
        return nextInt % molecule == 0;
    }

}
