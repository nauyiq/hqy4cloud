package com.hqy.cloud.common.result;

/**
 * @author hongqy
 * @date 2025/1/22
 */
public interface BsResult {

    /**
     * get response message.
     * @return message.
     */
    String getMessage();

    /**
     * get response code
     * @return code
     */
    String getCode();

    /**
     * get response data
     * @return data
     */
    default Object getData() {
        return null;
    }

}
