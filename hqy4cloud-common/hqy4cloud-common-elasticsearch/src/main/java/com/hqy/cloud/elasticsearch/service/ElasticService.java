package com.hqy.cloud.elasticsearch.service;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.document.ElasticDocument;
import com.hqy.cloud.elasticsearch.mapper.ElasticMapper;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * ElasticService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/19 11:13
 */
public interface ElasticService<K, T extends ElasticDocument<K>> extends CrudRepository<T, K> {

    /**
     * get es mapper.
     * @return {@link ElasticMapper}
     */
    ElasticMapper<K, T> getMapper();

    /**
     * return document class.
     * @return document class.
     */
    Class<T> getDocumentClass();

    /**
     * 根据条件查询
     * @param criteria Criteria
     * @return         list of result.
     */
    List<T> searchByCriteria(Criteria criteria);

    /**
     * 根据条件分页查询
     * @param pageNumber 第几页
     * @param pageSize   每页数目
     * @param criteria   条件
     * @return           {@link PageResult}
     */
    PageResult<T> pageQueryByCriteria(int pageNumber, int pageSize, Criteria criteria);

    /**
     * 根据Query对象查询
     * @param query {@link Query}
     * @return       result.
     */
    List<T> searchByQuery(Query query);

    /**
     * 根据Query对象分页查询
     * @param pageNumber 第几页
     * @param pageSize   每页数目
     * @param query      {@link Query}
     * @return           {@link PageResult}
     */
    PageResult<T> pageQueryByQuery(int pageNumber, int pageSize, Query query);

    /**
     * 根据NativeSearchQueryBuilder查询分页数据
     * @param pageNumber   第几页
     * @param pageSize     每页数目
     * @param queryBuilder 查询条件构造器
     * @return             {@link PageResult}
     */
    PageResult<T> pageQueryByBuilder(int pageNumber, int pageSize, NativeSearchQueryBuilder queryBuilder);






}
