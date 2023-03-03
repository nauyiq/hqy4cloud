package com.hqy.cloud.util;

import java.util.Arrays;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22 14:51
 */
public class ArgsUtil {

    /**
     * 动态拼接一个参数
     * @param args
     * @param appendArg
     * @return
     */
    public static Object[] addArg(Object[] args, Object appendArg) {
        if (args == null || appendArg == null) {
            throw new IllegalStateException("addArg ,args error or appendArg error!");
        }
        int len = args.length;
        Object[] result = Arrays.copyOf(args, len + 1);
        result[len] = appendArg;
        return result;
    }

    /**
     * 删除尾部数据的一个参数
     * @param args
     * @return
     */
    public static Object[] reduceTailArg(Object[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalStateException("reduceTailArg ,args error!");
        }
        int len = args.length;
        return Arrays.copyOf(args, len - 1);
    }


}
