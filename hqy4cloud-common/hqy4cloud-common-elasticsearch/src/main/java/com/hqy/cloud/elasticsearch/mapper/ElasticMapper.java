package com.hqy.cloud.elasticsearch.mapper;

import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ElasticMapper.
 * @see org.springframework.data.elasticsearch.repository.ElasticsearchRepository
 * <K> document primary key type.
 * <T> document type.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 17:19
 */
public interface ElasticMapper<K, T extends ElasticDocument<K>> extends ElasticsearchRepository<T, K> {



}
