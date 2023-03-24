package com.hqy.cloud.elasticsearch.mapper;

import cn.easyes.core.core.BaseEsMapper;
import com.hqy.cloud.elasticsearch.document.EsDocument;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 17:19
 */
public interface EsMapper<T extends EsDocument> extends BaseEsMapper<T> {

    /**
     * 获取es rest客户端， 返回高等级客户端
     * @return {@link RestHighLevelClient}
     */
    RestHighLevelClient getClient();

}
