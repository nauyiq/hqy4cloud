package com.hqy.cloud.canal.core.adapter.support;

import com.hqy.cloud.canal.core.adapter.SourceAdapter;
import com.hqy.cloud.util.JsonUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:29
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE, staticName = "of")
public class JsonSourceAdapter<T> implements SourceAdapter<String, T> {

    private final Class<T> klass;

    @Override
    public T adapt(String source) {
       if (StringUtils.isBlank(source)) {
           return null;
       }
        return JsonUtil.toBean(source, klass);
    }
}
