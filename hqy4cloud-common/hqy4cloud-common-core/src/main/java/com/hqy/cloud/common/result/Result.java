package com.hqy.cloud.common.result;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/10 9:16
 */
public interface Result {

    /**
     * get response message.
     * @return message.
     */
    String getMessage();

    /**
     * get response code
     * @return code
     */
    int getCode();

    /**
     * get response data
     * @return data
     */
    default Object getData() {
        return null;
    }



}
