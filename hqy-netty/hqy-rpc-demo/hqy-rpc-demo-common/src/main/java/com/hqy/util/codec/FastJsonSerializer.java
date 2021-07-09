package com.hqy.util.codec;

import com.hqy.util.JsonUtil;

import java.util.Objects;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-08 17:38
 */
public class FastJsonSerializer implements Serializer {

    private FastJsonSerializer(){};

    private volatile FastJsonSerializer instance;

    public FastJsonSerializer getInstance() {

        if (Objects.isNull(instance)) {

            synchronized (this) {
                instance = new FastJsonSerializer();
            }
        }
        return instance;
    }


    @Override
    public byte[] encode(Object target) {
        return JsonUtil.toJsonBytes(target);
    }

    @Override
    public Object decode(byte[] bytes, Class<?> targetClass) {
        return JsonUtil.byteParseObject(bytes, targetClass);
    }
}
