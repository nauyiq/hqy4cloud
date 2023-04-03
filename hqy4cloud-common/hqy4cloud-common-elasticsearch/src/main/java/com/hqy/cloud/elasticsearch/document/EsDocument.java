package com.hqy.cloud.elasticsearch.document;

/**
 * EsDocument.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/23 17:23
 */
public interface EsDocument {

    /**
     * 这是document id.
     * @param id id
     */
    void setId(String id);

    /**
     * 获取document id
     * @return   id
     */
    String getId();



}
