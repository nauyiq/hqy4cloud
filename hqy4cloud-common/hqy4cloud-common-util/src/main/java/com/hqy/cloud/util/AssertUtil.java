package com.hqy.cloud.util;


import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.Result;
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
        isTrue(StringUtils.isNotBlank(text), message);
    }

    /**
     * 断言map是否不为空
     * @param map map
     * @param message 提示消息
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        isTrue(map != null && !map.isEmpty(), message);
    }


    /**
     * 断言集合是否为空
     * @param collection 集合
     * @param message 提示消息
     */
    public static void notEmpty(Collection<?> collection, String message) {
        isTrue(CollectionUtils.isNotEmpty(collection), message);
    }


    /**
     * 断言obj数组不为空,即array != null && array.length>0
     * @param message 提示消息
     * @param obj obj数组
     */
    public static void notEmpty(Object[] obj, String message) {
        isFalse(ObjectUtils.isEmpty(obj), message);
    }

    /**
     * 断言对象obj不为空
     * @param obj 断言对象
     * @param message 提示消息
     */
    public static void notNull(Object obj, String message) {
        isTrue(Objects.nonNull(obj), message);
    }


    /**
     * 断言 expression 是否为true
     * @param expression 表达式
     * @param message 提示消息
     */
    public static void isTrue(boolean expression, String message) {
        isFalse(!expression, message);
    }

    public static void isTrue(boolean expression, Result result) {
        isFalse(!expression, result);
    }


    /**
     * 断言 expression 是否为false
     * @param expression 表达式
     * @param message 提示消息
     */
    public static void isFalse(boolean expression, String message) {
        if (!expression) {
            return;
        }
        throw new RuntimeException(message);
    }

    /**
     * 断言 expression 是否为false
     * @param expression 表达式
     * @param result
     */
    public static void isFalse(boolean expression, Result result) {
        if (!expression) {
            return;
        }
        throw new BizException(result);
    }

    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message);
        }
    }



}
