package com.hqy.cloud.elasticsearch.service.impl;

import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.elasticsearch.document.EsDocument;
import com.hqy.cloud.elasticsearch.exception.ElasticsearchException;
import com.hqy.cloud.elasticsearch.mapper.EsMapper;
import com.hqy.cloud.elasticsearch.service.EsService;
import com.hqy.cloud.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 18:00
 */
@Slf4j
public abstract class EsServiceImpl<T extends EsDocument> implements EsService<T> {

    protected final boolean useEyEs;
    protected final EsMapper<T> mapper;

    public EsServiceImpl(boolean useEyEs, EsMapper<T> mapper) {
        this.useEyEs = useEyEs;
        if (Objects.isNull(mapper) || Objects.isNull(mapper.getClient())) {
            throw new UnsupportedOperationException("Error bean for esMapper.");
        }
        this.mapper = mapper;
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
                Boolean result = mapper.createIndex(index);
                return Boolean.TRUE.equals(result);
            } else {
                CreateIndexRequest request = new CreateIndexRequest(index);
                CreateIndexResponse response = mapper.getClient().indices().create(request, RequestOptions.DEFAULT);
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
                Boolean result = mapper.deleteIndex(index);
                return Boolean.TRUE.equals(result);
            } else {
                DeleteIndexRequest request = new DeleteIndexRequest(index);
                AcknowledgedResponse response = mapper.getClient().indices().delete(request, RequestOptions.DEFAULT);
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
                Boolean result = mapper.existsIndex(index);
                return Boolean.TRUE.equals(result);
            } else {
                GetIndexRequest request = new GetIndexRequest(index);
                return mapper.getClient().indices().exists(request, RequestOptions.DEFAULT);
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
                Integer insert = mapper.insert(document, index);
                return document.getId();
            } else {
                IndexRequest request = new IndexRequest(index);
                request.source(JsonUtil.toJson(document), XContentType.JSON);
                IndexResponse response = mapper.getClient().index(request, RequestOptions.DEFAULT);
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
                Integer insert = mapper.insert(document, index);
                return id;
            } else {
                IndexRequest request = new IndexRequest(index);
                request.id(id).source(JsonUtil.toJson(document), XContentType.JSON);
                IndexResponse response = mapper.getClient().index(request, RequestOptions.DEFAULT);
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
                Integer insertBatch = mapper.insertBatch(documents);
                return insertBatch != null && insertBatch > 0;
            } else {
                BulkRequest request = new BulkRequest();
                for (T document : documents) {
                    request.add(new IndexRequest(index).source(JsonUtil.toJson(document), XContentType.JSON));
                }
                BulkResponse bulk = mapper.getClient().bulk(request, RequestOptions.DEFAULT);
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

        } catch (Throwable cause) {

        }



        return false;
    }

    @Override
    public boolean updateDocument(T document, String index, String id) {
        return false;
    }

    @Override
    public T getDocument(String index, String id) {
        return null;
    }

    @Override
    public RestClient getLowLevelClient() {
        return null;
    }

    @Override
    public boolean checkExistDocument(String index, String id) {
        return false;
    }

    @Override
    public PageResult<T> search(String index, String highlightField, Map<String, Object> andQueryMap, Map<String, Object> orQueryMap, Map<String, Object> andLikeMap, Map<String, Object> orLikeMap, int pageNumber, int pageSize) {
        return null;
    }
}

