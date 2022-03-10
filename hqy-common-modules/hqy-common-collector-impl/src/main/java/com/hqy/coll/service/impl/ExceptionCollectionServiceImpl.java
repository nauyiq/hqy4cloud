package com.hqy.coll.service.impl;

import com.hqy.coll.service.ExceptionCollectionService;
import com.hqy.fundation.common.enums.ExceptionLevel;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 17:39
 */
@Service
public class ExceptionCollectionServiceImpl implements ExceptionCollectionService {

    @Override
    public void collect(long time, String exceptionClass, String stackTrace, int resultCode, String env, String nameEn, ExceptionLevel level, String param) {

    }
}
