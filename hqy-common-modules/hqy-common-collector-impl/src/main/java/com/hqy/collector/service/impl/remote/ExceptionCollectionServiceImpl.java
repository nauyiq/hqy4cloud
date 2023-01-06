package com.hqy.collector.service.impl.remote;

import com.hqy.coll.struct.PfExceptionStruct;
import com.hqy.collector.converter.CollectorServiceConverter;
import com.hqy.collector.entity.PfException;
import com.hqy.collector.service.PfExceptionService;
import com.hqy.foundation.common.enums.ExceptionLevel;
import com.hqy.coll.service.ExceptionCollectionService;
import com.hqy.foundation.common.enums.ExceptionType;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/7 17:39
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExceptionCollectionServiceImpl extends AbstractRPCService implements ExceptionCollectionService {

    private final PfExceptionService pfExceptionService;

    @Override
    public void collect(PfExceptionStruct struct) {
        if (Objects.isNull(struct)) {
            log.warn("PfExceptionStruct should not be null.");
            return;
        }
        PfException pfException = CollectorServiceConverter.CONVERTER.convert(struct);
        pfExceptionService.insert(pfException);
    }
}
