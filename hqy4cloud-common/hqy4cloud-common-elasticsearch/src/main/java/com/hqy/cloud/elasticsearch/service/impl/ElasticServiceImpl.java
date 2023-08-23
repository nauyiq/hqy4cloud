package com.hqy.cloud.elasticsearch.service.impl;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import com.hqy.cloud.elasticsearch.exception.ElasticsearchException;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import com.hqy.cloud.elasticsearch.service.ElasticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 1. elastic支持模糊查询，它有两种思路来实现，一种是使用wildcard查询，一种是使用短语查询（match_phrase,match_phrase_prefix）。然而wildcard查询官方建议是尽量避免使用，因为有性能问题。
 * @see com.hqy.cloud.elasticsearch.service.ElasticService
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/19 11:16
 */
@Slf4j
@RequiredArgsConstructor
public abstract class ElasticServiceImpl<K, T extends ElasticDocument<K>> implements ElasticService<K, T> {

    private final ElasticMapper<K, T> elasticMapper;
    private final ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public List<T> searchByCriteria(Criteria criteria) {
        Query query = new CriteriaQuery(criteria);
        return searchByQuery(query);
    }

    @Override
    public PageResult<T> pageQueryByCriteria(int pageNumber, int pageSize, Criteria criteria) {
        Query query = new CriteriaQuery(criteria);
        return pageQueryByQuery(pageNumber, pageSize, query);
    }

    @Override
    public List<T> searchByQuery(Query query) {
        if (Objects.isNull(query)) {
            throw new ElasticsearchException("Query should not be null.");
        }
        SearchHits<T> searchHits = elasticsearchTemplate.search(query, getDocumentClass());
        return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
    }

    @Override
    public PageResult<T> pageQueryByQuery(int pageNumber, int pageSize, Query query) {
        if (Objects.isNull(query)) {
            throw new ElasticsearchException("Query should not be null.");
        }
        SearchHits<T> searchHits = elasticsearchTemplate.search(query, getDocumentClass());
        return buildPageResult(pageNumber, pageSize, searchHits);
    }

    @Override
    public PageResult<T> pageQueryByBuilder(int pageNumber, int pageSize, NativeQueryBuilder queryBuilder) {
        if (Objects.isNull(queryBuilder)) {
            throw new ElasticsearchException("Query builder should not be null.");
        }
        //设置分页参数
        queryBuilder.withPageable(PageRequest.of(--pageNumber, pageSize));
        SearchHits<T> searchHits = elasticsearchTemplate.search(queryBuilder.build(), getDocumentClass());
        return buildPageResult(pageNumber, pageSize, searchHits);
    }


    private PageResult<T> buildPageResult(int pageNumber, int pageSize, SearchHits<T> searchHits) {
        List<T> resultList = searchHits.toList().stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageResult<>(pageNumber, pageSize, searchHits.getTotalHits(), resultList);
    }


    @Override
    public ElasticMapper<K, T> getMapper() {
        return elasticMapper;
    }

    @Override
    public <S extends T> S save(S entity) {
        return elasticMapper.save(entity);
    }

    @Override
    public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
        return elasticMapper.saveAll(entities);
    }

    @Override
    public Optional<T> findById(K k) {
        return elasticMapper.findById(k);
    }

    @Override
    public boolean existsById(K k) {
        return elasticMapper.existsById(k);
    }

    @Override
    public Iterable<T> findAll() {
        return elasticMapper.findAll();
    }

    @Override
    public Iterable<T> findAllById(Iterable<K> ks) {
        return elasticMapper.findAllById(ks);
    }

    @Override
    public long count() {
        return elasticMapper.count();
    }

    @Override
    public void deleteById(K k) {
        elasticMapper.deleteById(k);
    }

    @Override
    public void delete(T entity) {
        elasticMapper.delete(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends K> ks) {
        elasticMapper.deleteAllById(ks);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        elasticMapper.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        elasticMapper.deleteAll();
    }
}
