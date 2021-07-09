package com.hqy.util.codec;

/**
 * @author qy
 * @description: 序列化器
 * @project: hqy-parent
 * @create 2021-07-08 17:36
 */
public interface Serializer {

     byte[] encode(Object target);

    Object decode(byte[] bytes, Class<?> targetClass);

}
