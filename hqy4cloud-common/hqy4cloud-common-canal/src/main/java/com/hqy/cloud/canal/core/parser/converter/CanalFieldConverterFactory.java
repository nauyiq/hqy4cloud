package com.hqy.cloud.canal.core.parser.converter;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:54
 */
public interface CanalFieldConverterFactory {

    /**
     * 注册转换器.
     * @param converter 转换器.
     */
    default void registerConverter(BaseCanalFieldConverter<?> converter) {
        registerConverter(converter, true);
    }

    /**
     * 注册转换器.
     * @param converter 转换器.
     * @param replace   是否替换
     */
    void registerConverter(BaseCanalFieldConverter<?> converter, boolean replace);

    /**
     * 加载Canal转换结果
     * @param input {@link CanalFieldConvertInput}
     * @return      {@link CanalFieldConvertResult}
     */
    CanalFieldConvertResult load(CanalFieldConvertInput input);

}
