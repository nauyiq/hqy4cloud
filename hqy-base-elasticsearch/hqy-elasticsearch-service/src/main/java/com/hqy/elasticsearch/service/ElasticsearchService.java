package com.hqy.elasticsearch.service;

import com.hqy.elasticsearch.ElasticsearchFoundation;
import com.hqy.base.common.result.PageResult;
import com.hqy.base.common.swticher.CommonSwitcher;
import com.hqy.util.proxy.CommonBeanUtil;
import org.apache.commons.collections4.CollectionUtils;
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
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * es工具类
 * 注意：这里所有的数据使用默认的文档类型_doc 在es8之后将会废弃type 因此不建议业务继续使用type
 * @author qy
 * @create 2021/9/13 22:38
 */
@Component
public class ElasticsearchService implements ElasticsearchFoundation {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchService.class);

    @Resource
    private RestHighLevelClient client;


    @Override
    public boolean createIndex(String index) {
        try {
            if (StringUtils.isBlank(index)) {
                return false;
            }
            if (checkIndexExist(index)) {
                log.warn("### This index existed.");
                return false;
            }
            CreateIndexRequest request = new CreateIndexRequest(index);
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            log.info("### create index:{} success.", index);
            return response.isAcknowledged();
        } catch (Exception e) {
            log.error("[es] create index failure, index:{}", index);
            log.error(e.getMessage(), e);
            return false;
        }

    }

    @Override
    public boolean deleteIndex(String index) {
        if (StringUtils.isBlank(index)) {
            return false;
        }
        if (!checkIndexExist(index)) {
            log.warn("### This index is not exist.");
            return false;
        }
        try {
            DeleteIndexRequest request = new DeleteIndexRequest(index);
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            boolean result = response.isAcknowledged();
            log.info("### delete index:{}, result:{}", index, result);
            return result;
        } catch (Exception e) {
            log.error("[es] delete index failure, index:{}", index);
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public boolean checkIndexExist(String index) {
        if (StringUtils.isBlank(index)) {
            return false;
        }
        try {
            GetIndexRequest request = new GetIndexRequest(index);
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("[es] checkIndexExist failure, index:{}", index);
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public String addDocument(String jsonData, String index) {
        if (StringUtils.isAnyBlank(jsonData, index)) {
            return null;
        }
        try {
            IndexRequest request = new IndexRequest(index);
            request.timeout(new TimeValue(15, TimeUnit.SECONDS)) //设置超时时间
                    .source(jsonData, XContentType.JSON);
            IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
            String id = indexResponse.getId();
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("### add {} document! id:{} ,data:{}", index, id, jsonData);
            }
            return id;
        } catch (Exception e) {
            log.error("[es] addDocument failure, index:{} jsonData:{}", index, jsonData);
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String addDocument(String jsonData, String index, String id) {
        if (StringUtils.isAnyBlank(jsonData, index, id)) {
            return null;
        }
        try {
            IndexRequest request = new IndexRequest(index);
            request.timeout(new TimeValue(15, TimeUnit.SECONDS)) //设置超时时间
                    .id(id).source(jsonData, XContentType.JSON);
            IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
            if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
                log.info("### add {} document! id:{}, status:{}, data:{}", index, id,
                        indexResponse.status().name(), jsonData);
            }
            return id;
        } catch (Exception e) {
            log.error("[es] addDocument failure, index:{}, id:{}, jsonData:{}", index, id, jsonData);
            log.error(e.getMessage(), e);
            return null;
        }

    }

    @Override
    public boolean bulkAddDocument(String index, List<String> jsonDataList) {
        if (StringUtils.isBlank(index) || CollectionUtils.isEmpty(jsonDataList)) {
            return false;
        }
        try {
            BulkRequest request = new BulkRequest();
            jsonDataList.forEach(jsonData -> {
                request.add(new IndexRequest(index).source(jsonData, XContentType.JSON));
            });
            BulkResponse bulk = client.bulk(request, RequestOptions.DEFAULT);
            return !bulk.hasFailures();
        } catch (Exception e) {
            log.error("[es] bulkAddDocument failure, index:{}", index);
            log.error(e.getMessage(), e);
            return false;
        }

    }

    @Override
    public boolean deleteDocument(String index, String id) {
        if (StringUtils.isAnyBlank(index, id)) {
            return false;
        }
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            boolean result = response.status().getStatus() == RestStatus.OK.getStatus();
            log.info("### delete {} document, result: {}", index, result);
            return result;
        } catch (Exception e) {
            log.error("[es] deleteDocument failure, index:{} id:{}.", index, id);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateDocument(String jsonData, String index, String id) {
        if (StringUtils.isAnyBlank(jsonData, index, id)) {
            return false;
        }
        try {
            UpdateRequest request = new UpdateRequest(index, id);
            request.timeout(new TimeValue(1, TimeUnit.SECONDS))
                    .doc(jsonData, XContentType.JSON);
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            int total = response.getShardInfo().getTotal();
            return total > 0;
        } catch (Exception e) {
            log.error("[es] updateDocument failure, index:{} id:{}.", index, id);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getDocument(String index, String id) {
        if (StringUtils.isAnyBlank(index, id)) {
            return null;
        }
        try {
            GetRequest request = new GetRequest(index, id);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            Map<String, Object> map = response.getSource();
            map.put("id", response.getId()); //为返回的数据添加id
            return map;
        } catch (Exception e) {
            log.error("[es] getDocument failure, index:{} id:{}.", index, id);
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T> T getDocument(String index, String id, Class<T> tClass) {
        try {
            Map<String, Object> document = getDocument(index, id);
            if (Objects.isNull(document)) {
                return null;
            }
            return CommonBeanUtil.map2Bean(document, tClass);
        } catch (Exception e) {
            log.error("[es] getDocument failure, index:{} id:{}, class:{}", index, id, tClass.getSimpleName());
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public RestClient getLowLevelClient() {
        return client.getLowLevelClient();
    }

    @Override
    public boolean checkExistDocument(String index, String id) {
        if (StringUtils.isAnyBlank(id, index)) {
            return false;
        }
        try {
            GetRequest request = new GetRequest(index, id);
            //不获取返回的_source的上下文
            request.fetchSourceContext(new FetchSourceContext(false));
            request.storedFields("_none_");
            return client.exists(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("[es] checkExistDocument failure, index:{} id:{}", index, id);
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @Override
    public <T> List<T> getHighlightResponse(SearchResponse response, String highlightField, Class<T> tClass) {
        try {
            return ElasticsearchHelper.instance.analyseResult(response, highlightField, tClass);
        } catch (Exception e) {
            log.error("[es] getHighlightResponse failure, highlightField:{}, tClass:{}", highlightField, tClass.getSimpleName());
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> searchListData(String index,
                                                    SearchSourceBuilder query,
                                                    int size, int from, String field,
                                                    String sortField,
                                                    String highlightField) {
        try {
            SearchRequest request = new SearchRequest(index);
            if (StringUtils.isNotEmpty(field)) {
                //只查询特定字段。如果需要查询所有字段则不设置该项。
                query.fetchSource(new FetchSourceContext(true, field.split(","), Strings.EMPTY_ARRAY));
                from = from == 0 ? 0 : (from - 1) * size;
                //设置确定结果要从哪个索引开始搜索的from选项，默认为0
                query.from(from);
                query.size(size);
                if (StringUtils.isNotEmpty(sortField)) {
                    //排序字段，注意如果proposal_no是text类型会默认带有keyword性质，需要拼接.keyword
                    query.sort(sortField + ".keyword", SortOrder.ASC);
                }
                ElasticsearchHelper.instance.highlight(query, highlightField);
                //不返回源数据。只有条数之类的数据。
                //builder.fetchSource(false);
                request.source(query);
                SearchResponse response = client.search(request, RequestOptions.DEFAULT);
                if (response.status().getStatus() == 200) {
                    // 解析对象
                    return ElasticsearchHelper.instance.getHighlightResponse(response, highlightField);
                }
            }
        } catch (Exception e) {
            log.error("[es] searchListData failure");
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public <T> PageResult<T> search(String index,
                                    String highlightField,
                                    Map<String, Object> andQueryMap,
                                    Map<String, Object> orQueryMap, Map<String, Object> andLikeMap,
                                    Map<String, Object> orLikeMap, int pageNumber, int pageSize,
                                    Class<T> tClass) {
        if (StringUtils.isBlank(index)) {
            return new PageResult<>();
        }
        try {
            SearchRequest request = new SearchRequest(index);
            //构建搜索条件
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = ElasticsearchHelper.instance.conditionQuery(andQueryMap, orQueryMap, andLikeMap, orLikeMap);
            sourceBuilder.query(boolQueryBuilder);
            if (StringUtils.isNotBlank(highlightField)) {
                ElasticsearchHelper.instance.highlight(sourceBuilder, highlightField);
            }
            sourceBuilder = ElasticsearchHelper.instance.limit(sourceBuilder, pageNumber, pageSize);
            sourceBuilder.timeout(new TimeValue(30, TimeUnit.SECONDS));
            request.source(sourceBuilder);
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return ElasticsearchHelper.instance.analyseResult(response, highlightField, pageNumber, pageSize, tClass);
        } catch (Exception e) {
            log.error("[es] search failure");
            log.error(e.getMessage(), e);
            return new PageResult<>();
        }
    }
}
