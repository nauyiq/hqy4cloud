package com.hqy.util;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * 断言工具类 建议业务代码中少写逻辑判断等 提高代码可读性...
 * @author qiyuan.hong
 * @version 1.0
 * @create 2022/2/16 9:08
 */
public class AssertUtil {


    /**
     * 断言字符串是否为空
     * @param text 待断言字符串
     * @param message 提示消息
     */
    public static void notEmpty(String text, String message) {
        isTrue(StringUtils.isEmpty(text), message);
    }

    /**
     * 断言map是否为空
     * @param map map
     * @param message 提示消息
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        isTrue(map == null || map.isEmpty(), message);
    }


    /**
     * 断言集合是否为空
     * @param collection 集合
     * @param message 提示消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        isTrue(CollectionUtils.isEmpty(collection), message);
    }


    /**
     * 断言obj数组不为空,即array != null && array.length>0
     * @param message 提示消息
     * @param obj obj数组
     */
    public static void notEmpty(Object[] obj, String message) {
        isTrue(ObjectUtils.isEmpty(obj), message);
    }

    /**
     * 断言对象obj是不是为空对象
     * @param obj 断言对象
     * @param message 提示消息
     */
    public static void isNull(Object obj, String message) {
        isTrue(Objects.isNull(obj), message);
    }


    /**
     * 断言 expression 必须为true
     * @param expression 表达式
     * @param message 提示消息
     */
    public static void isTrue(boolean expression, String message) {
        isFalse(!expression, message);
    }

    /**
     * 断言 expression 必须为false
     * @param expression 表达式
     * @param message 提示消息
     */
    public static void isFalse(boolean expression, String message) {
        if (!expression) {
            return;
        }
        throw new RuntimeException(message);
    }



}
