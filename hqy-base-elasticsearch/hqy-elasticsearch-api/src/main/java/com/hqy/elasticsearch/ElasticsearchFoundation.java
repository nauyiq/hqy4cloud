package com.hqy.elasticsearch;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;

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
     * @throws IOException
     */
    boolean createIndex(String index) throws IOException;


    /**
     * 删除索引
     * @param index
     * @return
     * @throws IOException
     */
    boolean deleteIndex(String index) throws IOException;


    /**
     * 判断索引是否存在
     * @param index
     * @return
     * @throws IOException
     */
    boolean checkIndexExist(String index) throws IOException;

    /**
     * 添加文档, 使用随机id
     * @param jsonData 文档json数据
     * @param index 索引库
     * @return
     * @throws IOException
     */
    String addDocument(String jsonData, String index) throws IOException;

    /**
     * 添加文档，使用指定id
     * @param jsonData 文档json数据
     * @param index 索引库
     * @param id id，为null时使用随机id
     * @return
     * @throws IOException
     */
    String addDocument(String jsonData, String index, String id) throws IOException;

    /**
     * 通过id删除索引
     * @param index
     * @param id
     * @return
     * @throws IOException
     */
    boolean deleteDocument(String index, String id) throws IOException;


    /**
     * 更新索引
     * @param jsonData
     * @param index
     * @param id
     * @return
     * @throws IOException
     */
    boolean updateDocument(String jsonData, String index, String id) throws IOException;

    /**
     * 根据id获取文档
     * @param index
     * @param id
     * @return
     * @throws IOException
     */
    Map<String, Object> getDocument(String index, String id) throws IOException;

    /**
     * 根据id获取文档
     * @param index
     * @param id
     * @return
     * @throws IOException
     */
    Class<?> getDocument(String index, String id, Class<?> clazz) throws IOException;

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
     * @throws IOException
     */
    boolean checkExistDocument(String index, String id) throws IOException;

    /**
     * 返回高亮结果数据
     * @param response
     * @param highlightField
     * @return
     * @throws IOException
     */
    List<Map<String, Object>> getHighlightResponse(SearchResponse response, String highlightField) throws IOException;


//    public List<Class<?>>

}
