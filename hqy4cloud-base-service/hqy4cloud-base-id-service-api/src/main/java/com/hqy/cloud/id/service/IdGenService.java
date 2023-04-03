package com.hqy.cloud.id.service;

import com.hqy.cloud.id.struct.ResultStruct;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 17:00
 */
public interface IdGenService {

    /**
     * get id.
     * @param key key
     * @return    ResultStruct.
     */
    ResultStruct get(String key);

}
