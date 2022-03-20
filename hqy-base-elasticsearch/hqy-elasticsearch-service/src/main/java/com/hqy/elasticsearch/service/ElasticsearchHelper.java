package com.hqy.elasticsearch.service;

import com.hqy.fundation.common.result.PageResult;
import com.hqy.util.proxy.CommonBeanUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-14 18:33
 */
public enum ElasticsearchHelper {

    /**
     * 对象
     */
    instance;

    /**
     * 构建分页 SearchSourceBuilder
     *
     * @param sourceBuilder
     * @param pageNum
     * @param pageSize
     * @return
     */
    public SearchSourceBuilder limit(SearchSourceBuilder sourceBuilder, int pageNum, int pageSize) {
        if (sourceBuilder == null) {
            throw new RuntimeException("分页失败, SearchSourceBuilder is null.");
        }
        if (pageNum != 0) {
            pageNum = (pageNum - 1) * pageSize;
        }
        sourceBuilder.from(pageNum);
        sourceBuilder.size(pageSize);
        return sourceBuilder;
    }

    /**
     * 设置高亮字段
     *
     * @param sourceBuilder
     * @param highlightField
     * @return
     */
    public SearchSourceBuilder highlight(SearchSourceBuilder sourceBuilder, String highlightField) {
        if (Objects.isNull(sourceBuilder) || StringUtils.isBlank(highlightField)) {
            throw new RuntimeException("设置高亮字段失败, SearchSourceBuilder is null or highlightField is empty.");
        }
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(highlightField); //高亮字段
        highlightBuilder.requireFieldMatch(false); //多个高亮显示
        highlightBuilder.preTags("<span style='color:red'>"); //标签前缀
        highlightBuilder.postTags("</span>");//标签后缀
        sourceBuilder.highlighter(highlightBuilder);
        return sourceBuilder;
    }


    /**
     * 多条件查询
     *
     * @param andQueryMap     精确查询参数map  参数and连接
     * @param orQueryMap      精确查询参数map  参数or连接
     * @param andLikeQueryMap 模糊查询参数map 参数and连接
     * @param orLikeQueryMap  模糊查询参数map 参数or连接
     * @return
     */
    public BoolQueryBuilder conditionQuery(Map<String, Object> andQueryMap,
                                           Map<String, Object> orQueryMap,
                                           Map<String, Object> andLikeQueryMap,
                                           Map<String, Object> orLikeQueryMap) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //多条件设置  参数and连接
        //matchPhraseQuery是没有用分词器，matchQuery会使用分词器
        if (!CollectionUtils.isEmpty(andQueryMap)) {
            for (Map.Entry<String, Object> entry : andQueryMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.must(matchQueryBuilder);
            }
        }

        //精确查询参数map  参数or连接
        if (!CollectionUtils.isEmpty(orQueryMap)) {
            for (Map.Entry<String, Object> entry : orQueryMap.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.should(matchQueryBuilder);
            }
        }

        //模糊查询  参数and连接
        if (!CollectionUtils.isEmpty(andLikeQueryMap)) {
            for (Map.Entry<String, Object> entry : andLikeQueryMap.entrySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(entry.getKey(), "*" + entry.getValue() + "*");
                boolQueryBuilder.must(wildcardQueryBuilder);
            }
        }

        //模糊查询 参数or连接
        if (!CollectionUtils.isEmpty(orLikeQueryMap)) {
            for (Map.Entry<String, Object> entry : orLikeQueryMap.entrySet()) {
                WildcardQueryBuilder wildcardQueryBuilder = QueryBuilders.wildcardQuery(entry.getKey(), "*" + entry.getValue() + "*");
                boolQueryBuilder.should(wildcardQueryBuilder);
            }
        }

        return boolQueryBuilder;
    }

    /**
     * 处理结果集
     *
     * @param searchResponse 结果集
     * @param highlightField 需要解析的高亮字段，空则不解析
     * @param tClass         返回的class
     * @param <T>
     * @return
     */
    public <T> List<T> analyseResult(SearchResponse searchResponse, String highlightField, Class<T> tClass) {
        if (Objects.isNull(searchResponse)) {
            return null;
        }
        SearchHits hits = searchResponse.getHits();
        return getResultList(highlightField, tClass, hits);
    }

    /**
     * 分页处理结果集
     * @param searchResponse
     * @param highlightField
     * @param pageNumber
     * @param pageSize
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> PageResult<T> analyseResult(SearchResponse searchResponse,
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


    public <T> List<T> getResultList(String highlightField, Class<T> tClass, SearchHits hits) {
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
            return CommonBeanUtil.map2Bean(sourceAsMap, tClass);
        }).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getHighlightResponse(SearchResponse response, String highlightField) {
        SearchHit[] hits = response.getHits().getHits();
        return Arrays.stream(hits).map(e -> {
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
            return sourceAsMap;
        }).collect(Collectors.toList());
    }


}
