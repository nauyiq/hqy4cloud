package com.hqy.elasticsearch.service;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-14 18:33
 */
public enum ElasticsearchHelper {

    instance;

    /**
     * 构建分页 SearchSourceBuilder
     * @param sourceBuilder
     * @param pageNum
     * @param pageSize
     * @return
     */
    public SearchSourceBuilder limit(SearchSourceBuilder sourceBuilder, int pageNum, int pageSize) {
        if (sourceBuilder == null) {
            return null;
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
     * @param andQueryMap 精确查询参数map  参数and连接
     * @param orQueryMap  精确查询参数map  参数or连接
     * @param andLikeQueryMap  模糊查询参数map 参数and连接
     * @param orLikeQueryMap 模糊查询参数map 参数or连接
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
     * 分出处理结果集
     * @param searchResponse 结果集
     * @param highlightField 需要解析的高亮字段，空则不解析
     * @param tClass 返回的class
     * @param <T>
     * @return
     */
    public <T> List<T>  analyseResult(SearchResponse searchResponse,  String highlightField, Class<T> tClass) {
        if (Objects.isNull(searchResponse)) {
            return null;
        }



    }


}
