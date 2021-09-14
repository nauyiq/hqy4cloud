package com.hqy.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author qy
 * @create 2021/9/13 23:31
 */
public interface ElasticsearchFoundation {


    /**
     * 创建索引(类似db中库的存在)
     * @param index
     * @return
     */
    boolean createIndex(String index);


    /**
     * 删除索引
     * @param index
     * @return
     */
    boolean deleteIndex(String index);


    /**
     * 判断索引是否存在
     * @param index
     * @return
     */
    boolean checkIndexExist(String index);

    /**
     * 添加文档, 使用随机id
     * @param jsonData 文档json数据
     * @param index 索引库
     * @return
     */
    String addDocument(String jsonData, String index);

    /**
     * 添加文档，使用指定id
     * @param jsonData 文档json数据
     * @param index 索引库
     * @param id id，为null时使用随机id
     * @return
     */
    String addDocument(String jsonData, String index, String id);

    /**
     * 添加文档
     * @param index
     * @param jsonDataList
     * @return
     */
    boolean bulkAddDocument(String index, List<String> jsonDataList);

    /**
     * 通过id删除索引
     * @param index
     * @param id
     * @return
     */
    boolean deleteDocument(String index, String id);


    /**
     * 更新索引
     * @param jsonData
     * @param index
     * @param id
     * @return
     */
    boolean updateDocument(String jsonData, String index, String id);

    /**
     * 根据id获取文档
     * @param index
     * @param id
     * @return
     */
    Map<String, Object> getDocument(String index, String id);

    /**
     * 根据id获取文档
     * @param index
     * @param id
     * @return
     */
    <T> T getDocument(String index, String id, Class<T> tClass);

    /**
     * 获取低水平客户端
     * @return
     */
    RestClient getLowLevelClient();

    /**
     * 判断文档是否存在
     * @param index
     * @param id
     * @return
     */
    boolean checkExistDocument(String index, String id);

    /**
     * 返回高亮结果数据
     * @param response
     * @param highlightField
     * @return
     */
    List<Map<String, Object>> getHighlightResponse(SearchResponse response, String highlightField);

    /**
     * 返回高亮结果数据
     * @param response
     * @param highlightField
     * @param tClass
     * @param <T>
     * @return
     */
    <T> List<T> getHighlightResponse(SearchResponse response, String highlightField, Class<T> tClass);


    /**
     * 查询并分页
     * @param index  索引名称
     * @param query  查询条件
     * @param size   文档大小限制
     * @param from   从第几页开始
     * @param field  需要显示的字段，逗号分隔（缺省为全部字段）
     * @param sortField 排序字段
     * @param highlightField 高亮字段
     * @return
     */
    List<Map<String, Object>> searchListData(String index,
                                             SearchSourceBuilder query,
                                             int size,
                                             int from,
                                             String field,
                                             String sortField,
                                             String highlightField);

}
