package com.hqy.cloud.canal.core.adapter.support;

import com.hqy.cloud.canal.core.adapter.SourceAdapter;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:31
 */
public enum SourceAdapterFacade {


    /**
     * 单例
     */
    X;

    private static final SourceAdapter<String, String> I_S_A = RawStringSourceAdapter.of();

    @SuppressWarnings("unchecked")
    public <T> T adapt(Class<T> klass, String source) {
        if (klass.isAssignableFrom(String.class)) {
            return (T) I_S_A.adapt(source);
        }
        return JsonSourceAdapter.of(klass).adapt(source);
    }


}
