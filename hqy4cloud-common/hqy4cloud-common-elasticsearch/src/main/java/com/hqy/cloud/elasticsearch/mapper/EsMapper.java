package com.hqy.cloud.elasticsearch.mapper;

import cn.easyes.core.core.BaseEsMapper;
import com.hqy.cloud.elasticsearch.document.EsDocument;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 17:19
 */
public interface EsMapper<T extends EsDocument> extends BaseEsMapper<T> {
}
