package com.hqy.cloud.elasticsearch.document;

/**
 * Elasticsearch document.
 * <K> primary key type.
 * 注解：@Document用来声明Java对象与ElasticSearch索引的关系
 *              indexName 索引名称(是字母的话必须是小写字母)
 *              type 索引类型
 *              shards 主分区数量，默认5
 *              replicas 副本分区数量，默认1
 *              createIndex 索引不存在时，是否自动创建索引，默认true
 *                         不建议自动创建索引(自动创建的索引 是按着默认类型和默认分词器)
 * 注解：@Id 表示索引的主键
 * 注解：@Field 用来描述字段的ES数据类型，是否分词等配置，等于Mapping描述
 *              index 设置字段是否索引，默认是true，如果是false则该字段不能被查询
 *              store 标记原始字段值是否应该存储在 Elasticsearch 中，默认值为false，以便于快速检索。虽然store占用磁盘空间，但是减少了计算。
 *              type 数据类型(text、keyword、date、object、geo等)
 *              analyzer 对字段使用分词器，注意一般如果要使用分词器，字段的type一般是text。
 *              format 定义日期时间格式
 * 注解：@CompletionField 定义关键词索引 要完成补全搜索
 *              analyzer 对字段使用分词器，注意一般如果要使用分词器，字段的type一般是text。
 *              searchAnalyzer 显示指定搜索时分词器，默认是和索引是同一个，保证分词的一致性。
 *              maxInputLength:设置单个输入的长度，默认为50 UTF-16 代码点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 17:23
 */
public interface ElasticDocument<K> {

}
