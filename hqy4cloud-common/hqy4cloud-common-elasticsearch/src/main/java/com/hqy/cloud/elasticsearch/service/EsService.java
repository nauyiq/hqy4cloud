package com.hqy.cloud.elasticsearch.service;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.elasticsearch.document.EsDocument;
import com.hqy.cloud.elasticsearch.exception.ElasticsearchException;
import org.elasticsearch.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 17:26
 */
public interface EsService<T extends EsDocument> {

    /**
     * 创建索引(类似db中库的存在)
     * @param index 索引
     * @return      result.
     * @throws ElasticsearchException 异常.
     */
    boolean createIndex(String index) throws ElasticsearchException;


    /**
     * 删除索引
     * @param index 索引
     * @return      result.
     * @throws ElasticsearchException 异常.
     */
    boolean deleteIndex(String index) throws ElasticsearchException;


    /**
     * 判断索引是否存在
     * @param index 索引
     * @return      result.
     * @throws ElasticsearchException 异常.
     */
    boolean checkIndexExist(String index) throws ElasticsearchException;

    /**
     * 添加文档, 使用随机id
     * @param document 添加文档
     * @param index    索引库
     * @return         返回文档id
     * @throws ElasticsearchException 异常.
     */
    String addDocument(T document, String index) throws ElasticsearchException;

    /**
     * 添加文档，使用指定id
     * @param document 文档
     * @param index    索引库
     * @param id       id，为null时使用随机id
     * @return         id
     * @throws ElasticsearchException 异常.
     */
    String addDocument(T document, String index, String id) throws ElasticsearchException;

    /**
     * 批量添加文档
     * @param index     索引库
     * @param documents 批量添加的文档
     * @return          result
     * @throws ElasticsearchException 异常.
     */
    boolean addDocuments(String index, List<T> documents) throws ElasticsearchException;

    /**
     * 通过id删除索引
     * @param index 索引库
     * @param id    id
     * @return      result.
     * @throws ElasticsearchException 异常.
     */
    boolean deleteDocument(String index, String id) throws ElasticsearchException;


    /**
     * 更新索引
     * @param document 文档
     * @param index    索引库
     * @param id       文档id
     * @return         result.
     * @throws ElasticsearchException 异常.
     */
    boolean updateDocument(T document, String index, String id) throws ElasticsearchException;

    /**
     * 根据id获取文档
     * @param index 索引库
     * @param id    文档id
     * @return      文档
     */
    T getDocument(String index, String id);


    /**
     * 获取低水平客户端
     * @return RestClient.
     */
    RestClient getLowLevelClient();

    /**
     * 判断文档是否存在
     * @param index 索引
     * @param id    id
     * @return      result.
     */
    boolean checkExistDocument(String index, String id);

    /*  *//**
     * 返回高亮结果数据
     * @param response
     * @param highlightField
     * @return
     *//*
    List<Map<String, Object>> getHighlightResponse(SearchResponse response, String highlightField);*/

/*    *//**
     * 返回高亮结果数据
     * @param response
     * @param highlightField
     * @param tClass
     * @param <T>
     * @return
     *//*
    <T> List<T> getHighlightResponse(SearchResponse response, String highlightField, Class<T> tClass);*/



    /**
     * 分页通用查询方法，精确查询（match），模糊查询
     * @param index 索引
     * @param highlightField 高亮字段
     * @param andQueryMap   精确查询参数map  参数and连接
     * @param orQueryMap    精确查询参数map  参数or连接
     * @param andLikeMap    模糊查询参数map 参数and连接
     * @param orLikeMap     模糊查询参数map 参数or连接
     * @param pageNumber    第几页
     * @param pageSize      一页几条
     * @return
     */
    PageResult<T> search(String index,
                             String highlightField,
                             Map<String, Object> andQueryMap,
                             Map<String, Object> orQueryMap,
                             Map<String, Object> andLikeMap,
                             Map<String, Object> orLikeMap,
                             int pageNumber,
                             int pageSize);

}
