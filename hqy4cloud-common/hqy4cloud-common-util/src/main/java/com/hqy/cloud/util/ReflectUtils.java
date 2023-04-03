package com.hqy.cloud.util;

import com.hqy.cloud.common.base.lang.StringConstants;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射工具类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/21 15:19
 */
public class ReflectUtils {


    /**
     * 获取目标class的泛型class<t>
     * @param clazz 目标class
     * @param index 泛型下标
     * @param <T>   Class<T>
     * @return      Class<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getTargetGenericClass(Class<?> clazz, int index) {
        ParameterizedType genericSuperclass = (ParameterizedType) clazz.getGenericSuperclass();
        Type[] actualTypeArguments = genericSuperclass.getActualTypeArguments();
        return (Class<T>) actualTypeArguments[index];
    }

    /**
     * 获取某个实现的接口的泛型class对象
     * @param clazz          需要查找class类
     * @param interfaceIndex 实现的接口下标
     * @param index          泛型下标
     * @param <T>            Class<T>
     * @return               Class<T>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Class<T> getGenericInterfaceClass(Class clazz, int interfaceIndex, int index) {
        //获取到接口类型
        Type[] interfaces = clazz.getGenericInterfaces();
        Type type = interfaces[interfaceIndex];
        //如果没有实现ParameterizedType接口，即不支持泛型，直接返回Class
        if (!(type instanceof ParameterizedType)) {
            return clazz;
        } else {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (!(actualTypeArguments[index] instanceof Class)) {
                return clazz;
            }
            return (Class<T>) actualTypeArguments[index];
        }
    }


    public static String simpleClassName(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        String className = clazz.getName();
        final int lastDotIdx = className.lastIndexOf(StringConstants.Symbol.POINT);
        if (lastDotIdx > -1) {
            return className.substring(lastDotIdx + 1);
        }
        return className;
    }



    /**
     * 根据class类名生成key前缀
     * @param clazz Class
     * @return key前缀
     */
    public static String genkeyPrefix(Class<?> clazz) {
        return clazz.getSimpleName().concat(StringConstants.Symbol.COLON);
    }




}
