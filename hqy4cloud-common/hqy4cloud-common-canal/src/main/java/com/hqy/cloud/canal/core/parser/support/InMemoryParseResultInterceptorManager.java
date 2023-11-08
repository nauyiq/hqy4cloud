package com.hqy.cloud.canal.core.parser.support;

import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.canal.core.parser.BaseParseResultInterceptor;
import com.hqy.cloud.canal.core.parser.ModelTableMetadata;
import com.hqy.cloud.canal.core.parser.ModelTableMetadataManager;
import com.hqy.cloud.canal.core.parser.ParseResultInterceptorManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 11:27
 */
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
public class InMemoryParseResultInterceptorManager implements ParseResultInterceptorManager {
    private final Map<Class<?>, List<BaseParseResultInterceptor<?>>> cache = MapUtil.newConcurrentHashMap(16);
    private final ModelTableMetadataManager modelTableMetadataManager;

    @Override
    public <T> void registerParseResultInterceptor(BaseParseResultInterceptor<T> parseResultInterceptor) {
        synchronized (cache) {
            Class<T> klass = parseResultInterceptor.getKlass();
            ModelTableMetadata modelTableMetadata = modelTableMetadataManager.load(klass);
            Optional.ofNullable(modelTableMetadata).ifPresent(ignore -> {
                cache.putIfAbsent(parseResultInterceptor.getKlass(), new LinkedList<>());
                cache.get(klass).add(parseResultInterceptor);
            });
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> List<BaseParseResultInterceptor<T>> getParseResultInterceptors(Class<T> klass) {
        return (List<BaseParseResultInterceptor<T>>) (List<?>) cache.getOrDefault(klass, Collections.emptyList());
    }

}
