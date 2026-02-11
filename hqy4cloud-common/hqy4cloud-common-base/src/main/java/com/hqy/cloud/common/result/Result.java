package com.hqy.cloud.common.result;

/**
 * <p>
 *     格式: T + MM + NNN
 *      │   │    │
 *      │   │    └── NNN: 具体错误序号 (001-999)
 *      │   └────── MM: 业务模块编码 (00-99)
 *      └────────── T: 错误类型 (0-9)
 *
 *     @see com.hqy.cloud.common.result.ResultCodeConstants
 * </p>
 * @author qiyuan.hong
 * @version 1.0
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
    String getCode();

    /**
     * get response data
     * @return data
     */
    default Object getData() {
        return null;
    }

}
