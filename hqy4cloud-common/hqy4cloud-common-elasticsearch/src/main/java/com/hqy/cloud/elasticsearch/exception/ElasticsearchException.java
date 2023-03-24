package com.hqy.cloud.elasticsearch.exception;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/24 10:11
 */
public class ElasticsearchException extends RuntimeException {

    public ElasticsearchException(String message) {
        super(message);
    }

    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticsearchException(Throwable cause) {
        super(cause);
    }
}
