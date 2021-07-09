package com.hqy.util.codec;

import com.hqy.util.JsonUtil;

import java.util.Objects;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-08 17:38
 */
public class FastJsonSerializer  {

    private FastJsonSerializer(){};

    private static volatile FastJsonSerializer instance;

    public static FastJsonSerializer getInstance() {

        if (Objects.isNull(instance)) {

            synchronized (FastJsonSerializer.class) {
                instance = new FastJsonSerializer();
            }
        }
        return instance;
    }


    public static byte[] encode(Object target) {
        return JsonUtil.toJsonBytes(target);
    }

    public static Object decode(byte[] bytes, Class<?> targetClass) {
        return JsonUtil.byteParseObject(bytes, targetClass);
    }
}
