package com.hqy.cloud.elasticsearch.service.impl;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.core.EsWrappers;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.elasticsearch.document.EsDocument;
import com.hqy.cloud.elasticsearch.exception.ElasticsearchException;
import com.hqy.cloud.elasticsearch.service.EsService;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 18:00
 */
@Slf4j
public abstract class EsServiceImpl<T extends EsDocument> implements EsService<T> {

    protected boolean useEyEs;
    protected final RestHighLevelClient client;

    public EsServiceImpl(RestHighLevelClient client) {
        this(true, client);
    }

    public EsServiceImpl(boolean useEyEs,  RestHighLevelClient client) {
        this.useEyEs = useEyEs;
        this.client = client;
    }


    @Override
    public boolean createIndex(String index) throws ElasticsearchException {
        if (StringUtils.isBlank(index)) {
            return false;
        }
        if (checkIndexExist(index)) {
            log.info("Index:{} already exist.", index);
            return true;
        }
        try {
            if (useEyEs) {
                Boolean result = getMapper().createIndex(index);
                return Boolean.TRUE.equals(result);
            } else {
                CreateIndexRequest request = new CreateIndexRequest(index);
                CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
                return response.isAcknowledged();
            }
        } catch (Throwable cause) {
            log.error("Failed execute to created es index = {}, cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }

    }

    @Override
    public boolean deleteIndex(String index) throws ElasticsearchException {
        if (StringUtils.isBlank(index)) {
            return false;
        }
        try {
            if (useEyEs) {
                Boolean result = getMapper().deleteIndex(index);
                return Boolean.TRUE.equals(result);
            } else {
                DeleteIndexRequest request = new DeleteIndexRequest(index);
                AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
                return response.isAcknowledged();
            }
        } catch (Throwable cause) {
            log.error("Failed execute to delete es index = {}, cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }

    }

    @Override
    public boolean checkIndexExist(String index) throws ElasticsearchException {
        if (StringUtils.isBlank(index)) {
            return false;
        }
        try {
            if (useEyEs) {
                Boolean result = getMapper().existsIndex(index);
                return Boolean.TRUE.equals(result);
            } else {
                GetIndexRequest request = new GetIndexRequest(index);
                return client.indices().exists(request, RequestOptions.DEFAULT);
            }
        } catch (Throwable cause) {
            log.error("Failed execute to check es index = {}, cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }

    @Override
    public String addDocument(T document, String index) throws ElasticsearchException {
        if (Objects.isNull(document) || StringUtils.isBlank(index)) {
            throw new UnsupportedOperationException();
        }
        try {
            if (useEyEs) {
                Integer insert = getMapper().insert(document, index);
                return document.getId();
            } else {
                IndexRequest request = new IndexRequest(index);
                request.source(JsonUtil.toJson(document), XContentType.JSON);
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                return response.getId();
            }
        } catch (Throwable cause) {
            log.error("Failed execute to addDocument, index = {}, cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }

    @Override
    public String addDocument(T document, String index, String id) throws ElasticsearchException {
        if (StringUtils.isAnyBlank(index, id) || Objects.isNull(document)) {
            throw new UnsupportedOperationException(ResultCode.ERROR_PARAM_UNDEFINED.message);
        }

        try {
            if (useEyEs) {
                document.setId(id);
                Integer insert = getMapper().insert(document, index);
                return id;
            } else {
                IndexRequest request = new IndexRequest(index);
                request.id(id).source(JsonUtil.toJson(document), XContentType.JSON);
                IndexResponse response = client.index(request, RequestOptions.DEFAULT);
                return response.getId();
            }
        } catch (Throwable cause) {
            log.error("Failed execute to addDocument, index = {}, cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }

    @Override
    public boolean addDocuments(String index, List<T> documents) throws ElasticsearchException {
        if (CollectionUtils.isEmpty(documents) || StringUtils.isBlank(index)) {
            return false;
        }
        try {
            if (useEyEs) {
                Integer insertBatch = getMapper().insertBatch(documents);
                return insertBatch != null && insertBatch > 0;
            } else {
                BulkRequest request = new BulkRequest();
                for (T document : documents) {
                    request.add(new IndexRequest(index).source(JsonUtil.toJson(document), XContentType.JSON));
                }
                BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
                return !bulk.hasFailures();
            }
        } catch (Throwable cause) {
            log.error("Failed execute to addDocuments, index = {}, cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }

    }

    @Override
    public boolean deleteDocument(String index, String id) throws ElasticsearchException {
        if (StringUtils.isAnyBlank(id, index)) {
            return false;
        }
        try {
            if (useEyEs) {
                Integer deleted = getMapper().deleteById(id, index);
                return  deleted > 0;
            } else {
                DeleteRequest request = new DeleteRequest(index, id);
                DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
                return response.status().getStatus() == RestStatus.OK.getStatus();
            }
        } catch (Throwable cause) {
            log.error("Failed execute to delete document, index = {}, id = {}, cause: {}", index, id, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }

    @Override
    public boolean updateDocument(T document, String index, String id) {
        if (StringUtils.isAnyBlank(id, index) || Objects.isNull(document)) {
            return false;
        }

        try {
            if (useEyEs) {
                document.setId(id);
                Integer update = getMapper().updateById(document, index);
                return update > 0;
            } else {
                UpdateRequest request = new UpdateRequest(index, id);
                request.doc(JsonUtil.toJson(document), XContentType.JSON);
                UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
                return response.getShardInfo().getTotal() > 0;
            }

        } catch (Throwable cause) {
            log.error("Failed execute to update document, index = {}, id = {}, cause: {}", index, id, cause.getMessage());
            throw new ElasticsearchException(cause);
        }

    }

    @Override
    public T getDocument(String index, String id) {
        if (StringUtils.isAnyBlank(id, index)) {
            return null;
        }
        try {
            if (useEyEs) {
                 return getMapper().selectById(id);
            } else {
                GetRequest request = new GetRequest(index, id);
                GetResponse response = client.get(request, RequestOptions.DEFAULT);
                Map<String, Object> source = response.getSource();
                return JsonUtil.toBean(JsonUtil.toJson(source), getDocumentClass());
            }
        } catch (Throwable cause) {
            log.error("Failed execute to get document, index = {}, id = {}, cause: {}", index, id, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }

    @Override
    public RestHighLevelClient getClient() {
        return client;
    }

    @Override
    public boolean checkExistDocument(String index, String id) {
        if (StringUtils.isAnyBlank(id, index)) {
            return false;
        }
        try {
            if (useEyEs) {
                return getMapper().selectById(id, index) != null;
            } else {
                GetRequest request = new GetRequest(index, id);
                request.fetchSourceContext(new FetchSourceContext(false));
                request.storedFields("_none_");
                return client.exists(request, RequestOptions.DEFAULT);
            }
        } catch (Throwable cause) {
            log.error("Failed execute to checkExistDocument, index = {}, id = {}, cause: {}", index, id, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }

    @Override
    public PageResult<T> search(String index, String highlightField, Map<String, Object> andQueryMap, Map<String, Object> orQueryMap, Map<String, Object> andLikeMap, Map<String, Object> orLikeMap, int pageNumber, int pageSize) {
        if (StringUtils.isBlank(index)) {
            return new PageResult<>();
        }
        PageResult<T> pageResult;
        try {
            if (useEyEs) {
                pageResult = eyesQueryPage(index, andQueryMap, orQueryMap, andLikeMap, pageNumber, pageSize);
            } else {
                pageResult = clientQueryPage(index, highlightField, andQueryMap, orQueryMap, andLikeMap, pageNumber, pageSize);
            }
            return pageResult;
        } catch (Throwable cause) {
            log.error("Failed execute to search, index = {},  cause: {}", index, cause.getMessage());
            throw new ElasticsearchException(cause);
        }
    }


    @Override
    public void setUsingEs(boolean isUsing) {
        this.useEyEs = isUsing;
    }

    private PageResult<T> eyesQueryPage(String index, Map<String, Object> andQueryMap, Map<String, Object> orQueryMap, Map<String, Object> andLikeMap, int pageNumber, int pageSize) {
        LambdaEsQueryWrapper<T> queryWrapper = EsWrappers.lambdaQuery(getDocumentClass());
        if (MapUtils.isNotEmpty(andQueryMap)) {
            queryWrapper.allEq(andQueryMap);
        }
        if (MapUtils.isNotEmpty(orQueryMap)) {
           queryWrapper.or().allEq(orQueryMap);
        }
        if (MapUtils.isNotEmpty(andLikeMap)) {
            for (Map.Entry<String, Object> entry : andLikeMap.entrySet()) {
                queryWrapper =  queryWrapper.like(entry.getKey(), entry.getValue());
            }
        }

        EsPageInfo<T> pageQuery = getMapper().pageQuery(queryWrapper, pageNumber, pageSize);
        if (Objects.isNull(pageQuery)) {
            throw new ElasticsearchException("Eyes page query error.");
        }
        return new PageResult<>(pageQuery.getPageNum(), pageQuery.getTotal(), pageQuery.getPages(), pageQuery.getList());
    }

    private PageResult<T> clientQueryPage(String index, String highlightField, Map<String, Object> andQueryMap, Map<String, Object> orQueryMap, Map<String, Object> andLikeMap, int pageNumber, int pageSize) throws IOException {
        SearchRequest request = new SearchRequest(index);
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        if (StringUtils.isNotBlank(highlightField)) {
            //构建高亮环境
            sourceBuilder = highlight(sourceBuilder, highlightField);
        }
        //构建条件查询
        BoolQueryBuilder boolQueryBuilder = condition(andQueryMap, orQueryMap, andLikeMap);
        sourceBuilder.query(boolQueryBuilder);
        pageNumber = (pageNumber - 1) * pageSize;
        sourceBuilder.from(pageNumber);
        sourceBuilder.size(pageSize);
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        return analyseResult(response, highlightField, pageNumber, pageSize, getDocumentClass());
    }

    public PageResult<T> analyseResult(SearchResponse searchResponse,
                                           String highlightField,
                                           int pageNumber,
                                           int pageSize,
                                           Class<T> tClass) {
        SearchHits hits = searchResponse.getHits();
        List<T> resultList = getResultList(highlightField, tClass, hits);
        long total = hits.getTotalHits().value;
        int pages = (int) ((total + pageSize - 1) / pageSize);
        return new PageResult<>(pageNumber, total, pages, resultList);
    }

    public  List<T> getResultList(String highlightField, Class<T> tClass, SearchHits hits) {
        return Arrays.stream(hits.getHits()).map(e -> {
            Map<String, HighlightField> highlightFields = e.getHighlightFields();
            HighlightField highlight = highlightFields.get(highlightField);
            Map<String, Object> sourceAsMap = e.getSourceAsMap();
            if (Objects.nonNull(highlight)) {
                Text[] fragments = highlight.fragments();
                StringBuilder newField = new StringBuilder();
                for (Text fragment : fragments) {
                    newField.append(fragment);
                }
                sourceAsMap.put(highlightField, newField.toString());
            }
            return BeanUtil.mapToBean(sourceAsMap, getDocumentClass(),true, CopyOptions.create());
        }).collect(Collectors.toList());
    }

    private BoolQueryBuilder condition(Map<String, Object> andQueryMap, Map<String, Object> orQueryMap, Map<String, Object> andLikeMap) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //matchPhraseQuery是没有用分词器，matchQuery会使用分词器
        if (MapUtil.isNotEmpty(andQueryMap)) {
            for (Map.Entry<String, Object> entry : andQueryMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.must(matchQueryBuilder);
            }
        }
        //精确查询参数map 参数or连接
        if (MapUtil.isNotEmpty(orQueryMap)) {
            for (Map.Entry<String, Object> entry : orQueryMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.should(matchQueryBuilder);
            }
        }
        //模糊查询  参数and连接
        if (MapUtil.isNotEmpty(andLikeMap)) {
            for (Map.Entry<String, Object> entry : andLikeMap.entrySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(entry.getKey(), "*" + entry.getValue() + "*");
                boolQueryBuilder.must(wildcardQueryBuilder);
            }
        }

        return boolQueryBuilder;
    }


    public SearchSourceBuilder highlight(SearchSourceBuilder sourceBuilder, String highlightField) {
        if (Objects.isNull(sourceBuilder) || StringUtils.isBlank(highlightField)) {
            throw new RuntimeException("设置高亮字段失败, SearchSourceBuilder is null or highlightField is empty.");
        }
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //高亮字段
        highlightBuilder.field(highlightField);
        //多个高亮显示
        highlightBuilder.requireFieldMatch(false);
        //标签前缀
        highlightBuilder.preTags("<span style='color:red'>");
        //标签后缀
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        return sourceBuilder;
    }


}

