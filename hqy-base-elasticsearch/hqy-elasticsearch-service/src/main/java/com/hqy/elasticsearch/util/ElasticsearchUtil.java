package com.hqy.elasticsearch.util;

import com.hqy.elasticsearch.ElasticsearchFoundation;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * es工具类
 * @author qy
 * @create 2021/9/13 22:38
 */
@Component
public class ElasticsearchUtil implements ElasticsearchFoundation {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchUtil.class);

    @Resource
    private RestHighLevelClient client;


    @Override
    public boolean createIndex(String index) throws IOException {
        if (checkIndexExist(index)) {
            log.warn("### This index existed.");
            return false;
        }
        CreateIndexRequest request = new CreateIndexRequest(index);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        log.info("### create index:{} success.", index);
        return response.isAcknowledged();
    }

    @Override
    public boolean deleteIndex(String index) throws IOException {
        return false;
    }

    @Override
    public boolean checkIndexExist(String index) throws IOException {
        GetIndexRequest request = new GetIndexRequest(index);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    @Override
    public String addDocument(String jsonData, String index) throws IOException {
        return null;
    }

    @Override
    public String addDocument(String jsonData, String index, String id) throws IOException {
        return null;
    }

    @Override
    public boolean deleteDocument(String index, String id) throws IOException {
        return false;
    }

    @Override
    public boolean updateDocument(String jsonData, String index, String id) throws IOException {
        return false;
    }

    @Override
    public Map<String, Object> getDocument(String index, String id) throws IOException {
        return null;
    }

    @Override
    public Class<?> getDocument(String index, String id, Class<?> clazz) throws IOException {
        return null;
    }

    @Override
    public RestClient getLowLevelClient() {
        return null;
    }

    @Override
    public boolean checkExistDocument(String index, String id) throws IOException {
        return false;
    }

    @Override
    public List<Map<String, Object>> getHighlightResponse(SearchResponse response, String highlightField) throws IOException {
        return null;
    }
}
