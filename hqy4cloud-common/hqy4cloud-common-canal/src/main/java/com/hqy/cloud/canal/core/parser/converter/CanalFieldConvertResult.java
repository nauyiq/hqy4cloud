package com.hqy.cloud.canal.core.parser.converter;

import com.hqy.cloud.canal.core.parser.BaseCanalFieldConverter;
import lombok.Builder;
import lombok.Getter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:54
 */
@Getter
@Builder
public class CanalFieldConvertResult {
    private final BaseCanalFieldConverter<?> converter;
}
