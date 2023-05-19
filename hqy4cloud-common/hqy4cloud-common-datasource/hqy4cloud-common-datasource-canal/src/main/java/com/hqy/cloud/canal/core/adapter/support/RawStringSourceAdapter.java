package com.hqy.cloud.canal.core.adapter.support;

import com.hqy.cloud.canal.core.adapter.SourceAdapter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/19 10:29
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE, staticName = "of")
public class RawStringSourceAdapter implements SourceAdapter<String, String> {

    @Override
    public String adapt(String source) {
        return source;
    }


}
